// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import java.time.LocalDate
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.Locale

// The JDK has no "best pattern" API; its long localized date pattern has the same field order.
internal actual fun platformDatePattern(): String = runCatching {
    DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.LONG, null, IsoChronology.INSTANCE, Locale.getDefault())
}.getOrDefault("")

internal actual fun platformToday(): DateValue {
    val today = LocalDate.now()
    return DateValue(year = today.year, month = today.monthValue, day = today.dayOfMonth)
}
