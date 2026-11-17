// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterMediumStyle
import platform.Foundation.NSDateFormatterNoStyle
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

internal actual fun platformDatePattern(): String = NSDateFormatter.dateFormatFromTemplate("yyyyMMMMdd", 0u, NSLocale.currentLocale) ?: ""

internal actual fun platformToday(): DateValue {
    val calendar = NSCalendar.currentCalendar
    val now = NSDate()
    return DateValue(
        year = calendar.component(NSCalendarUnitYear, fromDate = now).toInt(),
        month = calendar.component(NSCalendarUnitMonth, fromDate = now).toInt(),
        day = calendar.component(NSCalendarUnitDay, fromDate = now).toInt(),
    )
}

internal actual fun platformTimePattern(): String = NSDateFormatter.dateFormatFromTemplate("hm", 0u, NSLocale.currentLocale) ?: ""

internal actual fun platformPickerSymbols(): PickerSymbols {
    val formatter = NSDateFormatter().apply { locale = NSLocale.currentLocale }
    val months = formatter.shortStandaloneMonthSymbols.toStrings()
    val weekdays = formatter.shortWeekdaySymbols.toStrings()
    val shortest = formatter.veryShortStandaloneWeekdaySymbols.toStrings()
    val relative = NSDateFormatter().apply {
        locale = NSLocale.currentLocale
        dateStyle = NSDateFormatterMediumStyle
        timeStyle = NSDateFormatterNoStyle
        doesRelativeDateFormatting = true
    }
    return PickerSymbols(
        shortMonths = if (months.size == 12) months else EnglishShortMonths,
        shortWeekdays = if (weekdays.size == 7) weekdays else EnglishShortWeekdays,
        shortestWeekdays = if (shortest.size == 7) shortest else EnglishShortWeekdays.map { it.take(1) },
        am = formatter.AMSymbol,
        pm = formatter.PMSymbol,
        today = relative.stringFromDate(NSDate()),
    )
}

internal actual fun platformDateFormatter(format: PickerDateFormat): (DateValue) -> String {
    val formatter = NSDateFormatter().apply {
        locale = NSLocale.currentLocale
        dateFormat = NSDateFormatter.dateFormatFromTemplate(format.skeleton, 0u, NSLocale.currentLocale) ?: "yyyy-MM-dd"
    }
    val calendar = NSCalendar.currentCalendar
    return { date ->
        val components = NSDateComponents().apply {
            year = date.year.toLong()
            month = date.month.toLong()
            day = date.day.toLong()
            hour = 12
        }
        calendar.dateFromComponents(components)?.let { formatter.stringFromDate(it) } ?: ""
    }
}

private fun List<*>?.toStrings(): List<String> = this?.map { it.toString() } ?: emptyList()

private val EnglishShortMonths = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

private val EnglishShortWeekdays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
