// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.suqi8.coui.kmp.basic

import kotlin.js.ExperimentalWasmJsInterop

// Intl has no pattern API; the order of the year / month / day parts of a long date is the
// same information, returned as a pattern-like string ("MMMM dd, yyyy" -> "Mdy").
@JsFun(
    """
        function couiDateFieldOrder() {
            try {
                const parts = new Intl.DateTimeFormat(undefined, { year: 'numeric', month: 'long', day: '2-digit' })
                    .formatToParts(new Date(2001, 0, 2));
                let order = '';
                for (const part of parts) {
                    if (part.type === 'year') order += 'y';
                    else if (part.type === 'month') order += 'M';
                    else if (part.type === 'day') order += 'd';
                }
                return order;
            } catch (e) {
                return '';
            }
        }
    """,
)
private external fun couiDateFieldOrder(): String

@JsFun(
    """
        function couiTodayKey() {
            const now = new Date();
            return now.getFullYear() * 10000 + (now.getMonth() + 1) * 100 + now.getDate();
        }
    """,
)
private external fun couiTodayKey(): Int

internal actual fun platformDatePattern(): String = couiDateFieldOrder()

internal actual fun platformToday(): DateValue {
    val key = couiTodayKey()
    return DateValue(year = key / 10000, month = key / 100 % 100, day = key % 100)
}
