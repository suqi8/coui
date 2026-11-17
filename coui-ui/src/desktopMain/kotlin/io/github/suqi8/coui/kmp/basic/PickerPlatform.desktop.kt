// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import java.text.DateFormatSymbols
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

// The JDK has no "best pattern" API; its long localized date pattern has the same field order.
internal actual fun platformDatePattern(): String = runCatching {
    DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.LONG, null, IsoChronology.INSTANCE, Locale.getDefault())
}.getOrDefault("")

internal actual fun platformToday(): DateValue {
    val today = LocalDate.now()
    return DateValue(year = today.year, month = today.monthValue, day = today.dayOfMonth)
}

// JDK 19+ resolves CLDR skeletons like Android's getBestDateTimePattern.
internal actual fun platformTimePattern(): String = runCatching {
    DateTimeFormatterBuilder.getLocalizedDateTimePattern("hm", IsoChronology.INSTANCE, Locale.getDefault())
}.getOrDefault("")

internal actual fun platformPickerSymbols(): PickerSymbols {
    val locale = Locale.getDefault()
    val monthFormat = localizedFormatter("MMM", locale) ?: DateTimeFormatter.ofPattern("MMM", locale)
    val months = List(12) { monthFormat.format(LocalDate.of(2001, it + 1, 1)) }
    // DayOfWeek starts on Monday; the pickers want Sunday first.
    val sundayFirst = List(7) { DayOfWeek.SUNDAY.plus(it.toLong()) }
    val amPm = DateFormatSymbols.getInstance(locale).amPmStrings
    return PickerSymbols(
        shortMonths = months,
        shortWeekdays = sundayFirst.map { it.getDisplayName(TextStyle.SHORT, locale) },
        shortestWeekdays = sundayFirst.map { it.getDisplayName(TextStyle.NARROW_STANDALONE, locale) },
        am = amPm[0],
        pm = amPm[1],
        today = fallbackTodayLabel(locale.language),
    )
}

internal actual fun platformDateFormatter(format: PickerDateFormat): (DateValue) -> String {
    val locale = Locale.getDefault()
    val formatter = localizedFormatter(format.skeleton, locale)
        ?: DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale)
    return { date -> formatter.format(LocalDate.of(date.year, date.month, date.day)) }
}

private fun localizedFormatter(skeleton: String, locale: Locale): DateTimeFormatter? = runCatching {
    DateTimeFormatter.ofLocalizedPattern(skeleton).withLocale(locale)
}.getOrNull()
