// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.runtime.Immutable

/**
 * A Chinese lunar calendar date, as produced by [LunarCalendar.fromSolar].
 *
 * @param year The lunar year (1900..2100 are covered by the conversion tables).
 * @param month The lunar month, 1-based (1 = 正月 .. 12 = 腊月).
 * @param day The lunar day of the month, 1-based (1..30).
 * @param isLeapMonth Whether [month] is the leap (闰) month that follows the regular month of
 *   the same number.
 */
@Immutable
data class LunarDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val isLeapMonth: Boolean = false,
)

/**
 * Chinese lunar calendar conversions, a pure Kotlin port of COUILunarUtil
 * (`com.coui.appcompat.lunarutil`). The tables and the day-walking algorithms are kept as in
 * COUI so the results match ColorOS's lunar date picker, festival and solar term labels exactly.
 *
 * Each [LunarInfo] entry packs one lunar year (COUILunarUtil.LunarInfo, 1900..2100): bits 0..3
 * hold the leap month (0 = none), bits 4..15 flag the 30-day months (bit `16 - month`), and bit 16
 * flags a 30-day leap month.
 */
object LunarCalendar {

    /** The lunar years covered by the conversion tables (COUILunarUtil MIN_YEAR / MAX_YEAR). */
    val SupportedYears: IntRange = 1900..2100

    /**
     * The leap month of a lunar [year], or 0 when the year has none (COUILunarUtil.leapMonth).
     * Years outside [SupportedYears] report 0.
     */
    fun leapMonth(year: Int): Int {
        if (year !in SupportedYears) return 0
        return (LunarInfo[year - 1900] and 0xF).toInt()
    }

    /** The number of days (29 or 30) of the regular [month] of a lunar [year] (COUILunarUtil.daysOfALunarMonth). */
    fun daysInMonth(year: Int, month: Int): Int {
        if (year >= 1900 && year - 1900 < LunarInfo.size) {
            if (((0x10000L shr month) and LunarInfo[year - 1900]) == 0L) return 29
        }
        return 30
    }

    /** The number of days of the leap month of a lunar [year], or 0 without one (COUILunarUtil.daysOfLeapMonthInLunarYear). */
    fun daysInLeapMonth(year: Int): Int {
        if (leapMonth(year) == 0) return 0
        return if ((LunarInfo[year - 1900] and 0x10000L) != 0L) 30 else 29
    }

    /** The number of days of a lunar [year], leap month included (COUILunarUtil.daysOfLunarYear). */
    fun daysInYear(year: Int): Int {
        var days = 348
        var bit = 0x8000
        while (bit > 8) {
            val index = year - 1900
            if (index >= 0 && index < LunarInfo.size && (LunarInfo[index] and bit.toLong()) != 0L) days++
            bit = bit shr 1
        }
        return days + daysInLeapMonth(year)
    }

    /**
     * The number of days of the given lunar month, the leap month when [isLeapMonth] is true
     * (the month length COUILunarUtil.clampDay clamps against).
     */
    fun daysInMonth(year: Int, month: Int, isLeapMonth: Boolean): Int = if (isLeapMonth) daysInLeapMonth(year) else daysInMonth(year, month)

    /**
     * Converts a Gregorian [date] to its lunar date (COUILunarUtil.calculateLunarByGregorian),
     * walking whole lunar years and then months from the 1900-01-31 epoch (正月初一 1900).
     */
    fun fromSolar(date: DateValue): LunarDate {
        var offset = (civilToEpochDay(date.year, date.month, date.day) - EpochDayOf19000131).toInt()
        var year = 1900
        var yearDays = 0
        while (year < 10000 && offset > 0) {
            yearDays = daysInYear(year)
            offset -= yearDays
            year++
        }
        if (offset < 0) {
            offset += yearDays
            year--
        }
        val leap = leapMonth(year)
        var inLeap = false
        var monthDays = 0
        var month = 1
        while (month < 13 && offset > 0) {
            if (leap > 0 && month == leap + 1 && !inLeap) {
                month--
                monthDays = daysInLeapMonth(year)
                inLeap = true
            } else {
                monthDays = daysInMonth(year, month)
            }
            offset -= monthDays
            if (inLeap && month == leap + 1) inLeap = false
            month++
        }
        if (offset == 0 && leap > 0 && month == leap + 1) {
            if (inLeap) {
                inLeap = false
            } else {
                month--
                inLeap = true
            }
        }
        if (offset < 0) {
            offset += monthDays
            month--
        }
        return LunarDate(year = year, month = month, day = offset + 1, isLeapMonth = inLeap)
    }

    /**
     * Converts a lunar date back to its Gregorian date (COUILunarUtil.lunarToSolar), or null when
     * the lunar date does not exist (year outside [SupportedYears], a leap flag on a month that is
     * not the leap month, or a day past the end of the month).
     */
    fun toSolar(lunar: LunarDate): DateValue? {
        val (year, month, day, isLeap) = lunar
        if (year !in SupportedYears || month !in 1..12 || day !in 1..30) return null
        val leap = leapMonth(year)
        if (isLeap && month != leap) return null
        var offset = 0
        for (y in 1900 until year) offset += daysInYear(y)
        for (m in 1 until month) offset += daysInMonth(year, m)
        if (leap == 0 || month < leap || (month == leap && !isLeap)) {
            if (day > daysInMonth(year, month)) return null
        } else if (month > leap) {
            offset += daysInLeapMonth(year)
            if (day > daysInMonth(year, month)) return null
        } else {
            offset += daysInMonth(year, month)
            if (day > daysInLeapMonth(year)) return null
        }
        return epochDayToCivil(EpochDayOf19000130 + offset + day)
    }

    /**
     * Moves a lunar date to [newYear], keeping the month and day (COUILunarUtil.changeALunarYear):
     * the leap flag survives only when [newYear] has the same leap month, and the day is clamped
     * to the length of the target month.
     */
    fun changeYear(lunar: LunarDate, newYear: Int): DateValue? {
        val keepLeap = lunar.isLeapMonth && leapMonth(newYear) == lunar.month
        val day = lunar.day.coerceAtMost(daysInMonth(newYear, lunar.month, keepLeap))
        return toSolar(LunarDate(newYear, lunar.month, day, keepLeap))
    }

    /** The Chinese name of a lunar [day] (初一 .. 三十, COUILunarUtil.chineseStringOfALunarDay). */
    fun dayName(day: Int): String {
        if (day > 30 || day < 1) return ""
        return when (day) {
            10 -> "初十"

            20 -> "二十"

            30 -> "三十"

            else -> {
                val unit = day % 10
                DayPrefixes[day / 10] + ChineseNumbers[if (unit == 0) 9 else unit - 1]
            }
        }
    }

    /**
     * The lunar date of a Gregorian [date] as a Chinese string such as `2026年九月初十` or
     * `2025年闰六月初一` (COUILunarUtil.getLunarDateString).
     */
    fun format(date: DateValue): String = format(fromSolar(date))

    /** A lunar date as a Chinese string such as `2026年九月初十` (COUILunarUtil.getLunarDateString). */
    fun format(lunar: LunarDate): String = buildString {
        append(lunar.year)
        append('年')
        if (lunar.isLeapMonth) append('闰')
        append(ChineseNumbers[lunar.month - 1])
        append('月')
        append(dayName(lunar.day))
    }

    /**
     * The solar term falling on a Gregorian [date], or null (COUILunarUtil.getSolarTerm). The
     * table covers 1970..2036 only, and COUI returns the traditional-Chinese names.
     */
    fun solarTerm(date: DateValue): String? {
        if (date.year !in 1970..2036 || date.month !in 1..12) return null
        val row = SolarTermDays[date.year - 1970]
        val index = (date.month - 1) * 2
        return when (date.day) {
            row[index] -> SolarTermNames[index]
            row[index + 1] -> SolarTermNames[index + 1]
            else -> null
        }
    }

    /**
     * The short calendar-cell label of a Gregorian [date] (COUILunarUtil.getLunarFestivalChineseString):
     * a lunar festival (春節 / 端午 / 中秋), else a solar term, else the lunar month name on the first
     * day of a lunar month (`九月`, `闰六`) or the lunar day name otherwise.
     */
    fun cellLabel(date: DateValue): String {
        val lunar = fromSolar(date)
        val festival = when {
            lunar.month == 1 && lunar.day == 1 -> "春節"
            lunar.month == 5 && lunar.day == 5 -> "端午"
            lunar.month == 8 && lunar.day == 15 -> "中秋"
            else -> null
        }
        if (festival != null) return festival
        solarTerm(date)?.let { return it }
        if (lunar.day != 1) return dayName(lunar.day)
        return if (lunar.isLeapMonth) "闰" + ChineseNumbers[lunar.month - 1] else ChineseNumbers[lunar.month - 1] + "月"
    }

    private val ChineseNumbers = arrayOf("一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二")

    private val DayPrefixes = arrayOf("初", "十", "廿", "卅")

    /** COUILunarUtil.ALL_TC_SOLAR_TERM_NAMES, the names getAMonthSolarTermNames returns. */
    private val SolarTermNames = arrayOf(
        "小寒", "大寒", "立春", "雨水", "驚蟄", "春分", "清明", "穀雨", "立夏", "小滿", "芒種", "夏至",
        "小暑", "大暑", "立秋", "處暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至",
    )

    /** Epoch day of 1900-01-31, the lunar epoch calculateLunarByGregorian counts from. */
    private val EpochDayOf19000131: Long = civilToEpochDay(1900, 1, 31)

    /** Epoch day of 1900-01-30, the START_DATE lunarToSolar adds its day offset to. */
    private val EpochDayOf19000130: Long = civilToEpochDay(1900, 1, 30)

    /** COUILunarUtil.LunarInfo, one entry per lunar year from 1900 to 2100. */
    private val LunarInfo = longArrayOf(
        0x04BD8, 0x04AE0, 0x0A570, 0x054D5, 0x0D260, 0x0D950, 0x16554, 0x056A0, 0x09AD0, 0x055D2,
        0x04AE0, 0x0A5B6, 0x0A4D0, 0x0D250, 0x1D255, 0x0B540, 0x0D6A0, 0x0ADA2, 0x095B0, 0x14977,
        0x04970, 0x0A4B0, 0x0B4B5, 0x06A50, 0x06D40, 0x1AB54, 0x02B60, 0x09570, 0x052F2, 0x04970,
        0x06566, 0x0D4A0, 0x0EA50, 0x16A95, 0x05AD0, 0x02B60, 0x186E3, 0x092E0, 0x1C8D7, 0x0C950,
        0x0D4A0, 0x1D8A6, 0x0B550, 0x056A0, 0x1A5B4, 0x025D0, 0x092D0, 0x0D2B2, 0x0A950, 0x0B557,
        0x06CA0, 0x0B550, 0x15355, 0x04DA0, 0x0A5B0, 0x14573, 0x052B0, 0x0A9A8, 0x0E950, 0x06AA0,
        0x0AEA6, 0x0AB50, 0x04B60, 0x0AAE4, 0x0A570, 0x05260, 0x0F263, 0x0D950, 0x05B57, 0x056A0,
        0x096D0, 0x04DD5, 0x04AD0, 0x0A4D0, 0x0D4D4, 0x0D250, 0x0D558, 0x0B540, 0x0B6A0, 0x195A6,
        0x095B0, 0x049B0, 0x0A974, 0x0A4B0, 0x0B27A, 0x06A50, 0x06D40, 0x0AF46, 0x0AB60, 0x09570,
        0x04AF5, 0x04970, 0x064B0, 0x074A3, 0x0EA50, 0x06B58, 0x05AC0, 0x0AB60, 0x096D5, 0x092E0,
        0x0C960, 0x0D954, 0x0D4A0, 0x0DA50, 0x07552, 0x056A0, 0x0ABB7, 0x025D0, 0x092D0, 0x0CAB5,
        0x0A950, 0x0B4A0, 0x0BAA4, 0x0AD50, 0x055D9, 0x04BA0, 0x0A5B0, 0x15176, 0x052B0, 0x0A930,
        0x07954, 0x06AA0, 0x0AD50, 0x05B52, 0x04B60, 0x0A6E6, 0x0A4E0, 0x0D260, 0x0EA65, 0x0D530,
        0x05AA0, 0x076A3, 0x096D0, 0x04AFB, 0x04AD0, 0x0A4D0, 0x1D0B6, 0x0D250, 0x0D520, 0x0DD45,
        0x0B5A0, 0x056D0, 0x055B2, 0x049B0, 0x0A577, 0x0A4B0, 0x0AA50, 0x1B255, 0x06D20, 0x0ADA0,
        0x14B63, 0x09370, 0x049F8, 0x04970, 0x064B0, 0x168A6, 0x0EA50, 0x06B20, 0x1A6C4, 0x0AAE0,
        0x092E0, 0x0D2E3, 0x0C960, 0x0D557, 0x0D4A0, 0x0DA50, 0x05D55, 0x056A0, 0x0A6D0, 0x055D4,
        0x052D0, 0x0A9B8, 0x0A950, 0x0B4A0, 0x0B6A6, 0x0AD50, 0x055A0, 0x0ABA4, 0x0A5B0, 0x052B0,
        0x0B273, 0x06930, 0x07337, 0x06AA0, 0x0AD50, 0x14B55, 0x04B60, 0x0A570, 0x054E4, 0x0D160,
        0x0E968, 0x0D520, 0x0DAA0, 0x16AA6, 0x056D0, 0x04AE0, 0x0A9D4, 0x0A2D0, 0x0D150, 0x0F252,
        0x0D520,
    )

    /** COUILunarUtil.SOLAR_TERM_DAYS: the two solar term days of every month, one row per year 1970..2036. */
    private val SolarTermDays = arrayOf(
        intArrayOf(6, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 9, 24, 8, 23, 7, 22),
        intArrayOf(6, 21, 4, 19, 6, 21, 5, 21, 6, 22, 6, 22, 8, 23, 8, 24, 8, 24, 9, 24, 8, 23, 8, 22),
        intArrayOf(6, 21, 5, 19, 5, 20, 5, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 21, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(6, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 9, 24, 8, 22, 7, 22),
        intArrayOf(6, 20, 4, 19, 6, 21, 5, 21, 6, 22, 6, 22, 8, 23, 8, 24, 8, 24, 9, 24, 8, 23, 8, 22),
        intArrayOf(6, 21, 4, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(5, 20, 4, 19, 5, 21, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 8, 24, 8, 23, 7, 22),
        intArrayOf(6, 20, 4, 19, 6, 21, 5, 21, 6, 22, 6, 22, 8, 23, 8, 24, 8, 23, 9, 24, 8, 23, 8, 22),
        intArrayOf(6, 21, 4, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21),
        intArrayOf(5, 20, 4, 19, 5, 21, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 8, 24, 8, 22, 7, 22),
        intArrayOf(6, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 8, 23, 8, 24, 8, 23, 9, 24, 8, 23, 8, 22),
        intArrayOf(6, 21, 4, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21),
        intArrayOf(5, 20, 4, 19, 5, 20, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 8, 24, 8, 22, 7, 22),
        intArrayOf(6, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 8, 23, 8, 24, 8, 23, 9, 24, 8, 23, 7, 22),
        intArrayOf(6, 21, 4, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21),
        intArrayOf(5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 21, 7, 23, 8, 23, 8, 23, 8, 24, 8, 22, 7, 22),
        intArrayOf(6, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 9, 24, 8, 23, 7, 22),
        intArrayOf(6, 21, 4, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21),
        intArrayOf(5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(6, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 9, 24, 8, 23, 7, 22),
        intArrayOf(6, 21, 4, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21),
        intArrayOf(5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 21, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(6, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 9, 24, 8, 23, 7, 22),
        intArrayOf(6, 21, 4, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21),
        intArrayOf(5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 21, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(6, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 9, 24, 8, 23, 7, 22),
        intArrayOf(6, 21, 4, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21),
        intArrayOf(5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 21, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(6, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 9, 24, 8, 22, 7, 22),
        intArrayOf(6, 20, 4, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21),
        intArrayOf(5, 20, 3, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(5, 20, 4, 19, 6, 21, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(6, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 8, 24, 8, 23, 7, 22),
        intArrayOf(6, 21, 4, 19, 5, 20, 4, 20, 5, 20, 5, 21, 7, 22, 7, 23, 7, 22, 8, 23, 7, 22, 7, 21),
        intArrayOf(5, 20, 4, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(5, 20, 4, 19, 5, 21, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 8, 24, 8, 22, 7, 22),
        intArrayOf(6, 20, 4, 19, 5, 20, 4, 19, 5, 20, 5, 21, 7, 22, 7, 23, 7, 22, 8, 23, 7, 22, 6, 21),
        intArrayOf(5, 20, 3, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21),
        intArrayOf(5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 21, 7, 23, 8, 23, 8, 23, 8, 24, 8, 22, 7, 22),
        intArrayOf(6, 20, 4, 19, 5, 20, 4, 19, 5, 20, 5, 21, 7, 22, 7, 23, 7, 22, 8, 23, 7, 22, 6, 21),
        intArrayOf(5, 20, 3, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21),
        intArrayOf(5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(6, 20, 4, 19, 5, 20, 4, 19, 5, 20, 5, 21, 6, 22, 7, 23, 7, 22, 8, 23, 7, 22, 6, 21),
        intArrayOf(5, 20, 3, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21),
        intArrayOf(5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 21, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(6, 20, 4, 19, 5, 20, 4, 19, 5, 20, 5, 21, 6, 22, 7, 22, 7, 22, 8, 23, 7, 22, 6, 21),
        intArrayOf(5, 20, 3, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21),
        intArrayOf(5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 21, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(6, 20, 4, 19, 5, 20, 4, 19, 5, 20, 5, 21, 6, 22, 7, 22, 7, 22, 8, 23, 7, 22, 6, 21),
        intArrayOf(5, 20, 3, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21),
        intArrayOf(5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(5, 20, 4, 19, 6, 21, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22),
        intArrayOf(6, 20, 4, 19, 5, 20, 4, 19, 5, 20, 5, 21, 6, 22, 7, 22, 7, 22, 8, 23, 7, 22, 6, 21),
    )
}

/** Days since 1970-01-01 of a proleptic Gregorian date (month and day 1-based). */
internal fun civilToEpochDay(year: Int, month: Int, day: Int): Long {
    val y = if (month <= 2) year - 1L else year.toLong()
    val era = (if (y >= 0) y else y - 399) / 400
    val yearOfEra = y - era * 400
    val monthIndex = (month + 9) % 12
    val dayOfYear = (153 * monthIndex + 2) / 5 + day - 1
    val dayOfEra = yearOfEra * 365 + yearOfEra / 4 - yearOfEra / 100 + dayOfYear
    return era * 146097 + dayOfEra - 719468
}

/** The proleptic Gregorian date of a day count since 1970-01-01. */
internal fun epochDayToCivil(epochDay: Long): DateValue {
    val z = epochDay + 719468
    val era = (if (z >= 0) z else z - 146096) / 146097
    val dayOfEra = z - era * 146097
    val yearOfEra = (dayOfEra - dayOfEra / 1460 + dayOfEra / 36524 - dayOfEra / 146096) / 365
    val dayOfYear = dayOfEra - (365 * yearOfEra + yearOfEra / 4 - yearOfEra / 100)
    val monthIndex = (5 * dayOfYear + 2) / 153
    val day = (dayOfYear - (153 * monthIndex + 2) / 5 + 1).toInt()
    val month = (if (monthIndex < 10) monthIndex + 3 else monthIndex - 9).toInt()
    val year = (yearOfEra + era * 400 + if (month <= 2) 1 else 0).toInt()
    return DateValue(year, month, day)
}

/** Day of week of a Gregorian date, 1 = Sunday .. 7 = Saturday (java.util.Calendar.DAY_OF_WEEK). */
internal fun dayOfWeek(year: Int, month: Int, day: Int): Int {
    // 1970-01-01 was a Thursday (5).
    val shifted = (civilToEpochDay(year, month, day) + 4).mod(7L)
    return shifted.toInt() + 1
}
