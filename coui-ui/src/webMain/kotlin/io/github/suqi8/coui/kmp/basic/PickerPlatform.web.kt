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

// Intl has no pattern API either: report "ah" when the day period precedes the hour, else "ha".
@JsFun(
    """
        function couiTimeFieldOrder() {
            try {
                const parts = new Intl.DateTimeFormat(undefined, { hour: 'numeric', minute: '2-digit', hour12: true })
                    .formatToParts(new Date(2001, 0, 1, 9, 30));
                let order = '';
                for (const part of parts) {
                    if (part.type === 'dayPeriod') order += 'a';
                    else if (part.type === 'hour') order += 'h';
                }
                return order;
            } catch (e) {
                return '';
            }
        }
    """,
)
private external fun couiTimeFieldOrder(): String

// The picker symbols joined by '\n': 12 short months, 7 short and 7 narrow weekdays (Sunday
// first), AM, PM and "today".
@JsFun(
    """
        function couiPickerSymbols() {
            const out = [];
            try {
                const month = new Intl.DateTimeFormat(undefined, { month: 'short' });
                for (let m = 0; m < 12; m++) out.push(month.format(new Date(2001, m, 1)));
                const weekday = new Intl.DateTimeFormat(undefined, { weekday: 'short' });
                for (let d = 0; d < 7; d++) out.push(weekday.format(new Date(2001, 0, 7 + d)));
                const narrow = new Intl.DateTimeFormat(undefined, { weekday: 'narrow' });
                for (let d = 0; d < 7; d++) out.push(narrow.format(new Date(2001, 0, 7 + d)));
                const period = new Intl.DateTimeFormat(undefined, { hour: 'numeric', hour12: true });
                for (const hour of [9, 21]) {
                    const part = period.formatToParts(new Date(2001, 0, 1, hour)).find(p => p.type === 'dayPeriod');
                    out.push(part ? part.value : (hour < 12 ? 'AM' : 'PM'));
                }
                let today = new Intl.RelativeTimeFormat(undefined, { numeric: 'auto' }).format(0, 'day');
                out.push(today.charAt(0).toLocaleUpperCase() + today.slice(1));
            } catch (e) {
                return '';
            }
            return out.join('\n');
        }
    """,
)
private external fun couiPickerSymbols(): String

// kind 0 = MonthYear, 1 = WeekdayMonthDay, 2 = FullDate (see PickerDateFormat).
@JsFun(
    """
        function couiFormatDate(kind, year, month, day) {
            try {
                const options = kind === 0 ? { year: 'numeric', month: 'long' }
                    : kind === 1 ? { weekday: 'short', month: 'short', day: 'numeric' }
                    : { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
                const date = new Date(2001, 0, 1, 12);
                date.setFullYear(year, month - 1, day);
                return new Intl.DateTimeFormat(undefined, options).format(date);
            } catch (e) {
                return year + '-' + month + '-' + day;
            }
        }
    """,
)
private external fun couiFormatDate(kind: Int, year: Int, month: Int, day: Int): String

internal actual fun platformDatePattern(): String = couiDateFieldOrder()

internal actual fun platformTimePattern(): String = couiTimeFieldOrder()

internal actual fun platformPickerSymbols(): PickerSymbols {
    val parts = couiPickerSymbols().split('\n')
    if (parts.size != 12 + 7 + 7 + 3) {
        return PickerSymbols(
            shortMonths = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
            shortWeekdays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"),
            shortestWeekdays = listOf("S", "M", "T", "W", "T", "F", "S"),
            am = "AM",
            pm = "PM",
            today = "Today",
        )
    }
    return PickerSymbols(
        shortMonths = parts.subList(0, 12),
        shortWeekdays = parts.subList(12, 19),
        shortestWeekdays = parts.subList(19, 26),
        am = parts[26],
        pm = parts[27],
        today = parts[28],
    )
}

internal actual fun platformDateFormatter(format: PickerDateFormat): (DateValue) -> String {
    val kind = format.ordinal
    return { date -> couiFormatDate(kind, date.year, date.month, date.day) }
}

internal actual fun platformToday(): DateValue {
    val key = couiTodayKey()
    return DateValue(year = key / 10000, month = key / 100 % 100, day = key % 100)
}
