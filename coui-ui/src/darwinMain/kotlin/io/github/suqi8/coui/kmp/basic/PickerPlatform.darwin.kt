// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
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
