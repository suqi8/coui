// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.fail

class LunarCalendarTest {

    private fun lunar(year: Int, month: Int, day: Int) = LunarCalendar.fromSolar(DateValue(year, month, day))

    @Test
    fun lunarNewYears() {
        assertEquals(LunarDate(1900, 1, 1), lunar(1900, 1, 31))
        assertEquals(LunarDate(1984, 1, 1), lunar(1984, 2, 2))
        assertEquals(LunarDate(2000, 1, 1), lunar(2000, 2, 5))
        assertEquals(LunarDate(2020, 1, 1), lunar(2020, 1, 25))
        assertEquals(LunarDate(2025, 1, 1), lunar(2025, 1, 29))
        assertEquals(LunarDate(2026, 1, 1), lunar(2026, 2, 17))
    }

    @Test
    fun yearBoundaries() {
        // New Year's Eve 2020 was 腊月三十, New Year's Eve 2025 was 腊月廿九.
        assertEquals(LunarDate(2019, 12, 30), lunar(2020, 1, 24))
        assertEquals(LunarDate(2024, 12, 29), lunar(2025, 1, 28))
        assertEquals(DateValue(2020, 1, 24), LunarCalendar.toSolar(LunarDate(2019, 12, 30)))
        assertEquals(DateValue(2025, 1, 29), LunarCalendar.toSolar(LunarDate(2025, 1, 1)))
    }

    @Test
    fun leapMonths() {
        assertEquals(6, LunarCalendar.leapMonth(2017))
        assertEquals(4, LunarCalendar.leapMonth(2020))
        assertEquals(2, LunarCalendar.leapMonth(2023))
        assertEquals(0, LunarCalendar.leapMonth(2024))
        assertEquals(6, LunarCalendar.leapMonth(2025))

        assertEquals(LunarDate(2017, 6, 1, isLeapMonth = true), lunar(2017, 7, 23))
        assertEquals(LunarDate(2020, 4, 1, isLeapMonth = true), lunar(2020, 5, 23))
        assertEquals(LunarDate(2020, 5, 1), lunar(2020, 6, 21))
        assertEquals(LunarDate(2023, 2, 1, isLeapMonth = true), lunar(2023, 3, 22))
        assertEquals(LunarDate(2023, 3, 1), lunar(2023, 4, 20))
        assertEquals(LunarDate(2025, 6, 1, isLeapMonth = true), lunar(2025, 7, 25))
        assertEquals(LunarDate(2025, 7, 1), lunar(2025, 8, 23))

        assertEquals(DateValue(2023, 3, 22), LunarCalendar.toSolar(LunarDate(2023, 2, 1, isLeapMonth = true)))
        // A leap flag on a month that is not the leap month does not exist.
        assertNull(LunarCalendar.toSolar(LunarDate(2024, 2, 1, isLeapMonth = true)))
    }

    @Test
    fun festivalsAndLabels() {
        assertEquals(LunarDate(2024, 8, 15), lunar(2024, 9, 17))
        assertEquals("中秋", LunarCalendar.cellLabel(DateValue(2024, 9, 17)))
        assertEquals("春節", LunarCalendar.cellLabel(DateValue(2025, 1, 29)))
        assertEquals("2025年闰六月初一", LunarCalendar.format(DateValue(2025, 7, 25)))
        assertEquals("初一", LunarCalendar.dayName(1))
        assertEquals("十一", LunarCalendar.dayName(11))
        assertEquals("二十", LunarCalendar.dayName(20))
        assertEquals("廿一", LunarCalendar.dayName(21))
        assertEquals("三十", LunarCalendar.dayName(30))
    }

    @Test
    fun changeYearDropsLeapFlagAndKeepsMonthAndDay() {
        // 2024 has no leap second month: 闰二月十五 2023 moves to 二月十五 2024.
        assertEquals(DateValue(2024, 3, 24), LunarCalendar.changeYear(LunarDate(2023, 2, 15, isLeapMonth = true), 2024))
    }

    @Test
    fun everySupportedDayRoundTrips() {
        val first = civilToEpochDay(1900, 1, 31)
        val last = civilToEpochDay(2100, 12, 31)
        var previous: LunarDate? = null
        for (epochDay in first..last) {
            val solar = epochDayToCivil(epochDay)
            val lunar = LunarCalendar.fromSolar(solar)
            // Messages are built only on a failure: two per day for ~73k days made this test slow.
            val back = LunarCalendar.toSolar(lunar)
            if (back != solar) fail("round trip of $solar ($lunar) gave $back")
            // Consecutive days advance the lunar day by one or start a new month on day 1.
            if (previous != null && lunar.day != 1 && lunar.day != previous.day + 1) fail("$lunar follows $previous")
            previous = lunar
        }
    }

    @Test
    fun datesOutsideTheTablesAreRejected() {
        assertFailsWith<IllegalArgumentException> { lunar(1900, 1, 30) }
        assertFailsWith<IllegalArgumentException> { lunar(2101, 1, 1) }
    }

    @Test
    fun gregorianHelpers() {
        assertEquals(29, daysInMonth(2024, 2))
        assertEquals(28, daysInMonth(2100, 2))
        assertEquals(29, daysInMonth(2000, 2))
        // 1970-01-01 was a Thursday, 2026-10-30 is a Friday (1 = Sunday).
        assertEquals(5, dayOfWeek(1970, 1, 1))
        assertEquals(6, dayOfWeek(2026, 10, 30))
        assertEquals(DateValue(2024, 2, 29), epochDayToCivil(civilToEpochDay(2024, 2, 29)))
        assertEquals(DateValue(2024, 3, 1), epochDayToCivil(civilToEpochDay(2024, 2, 29) + 1))
    }
}
