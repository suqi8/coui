// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A [LunarDatePicker] component with COUI style, mirroring ColorOS's COUILunarDatePicker
 * (coui_lunar_date_picker.xml).
 *
 * Like COUILunarDatePicker, the picker holds a Gregorian date ([value] / [onValueChange]) and
 * shows it as three lunar wheels: the lunar year, the lunar month (13 entries in a year with a
 * leap month, the leap month labelled `闰` + its regular month) and the lunar day (初一 .. 廿九 / 三十).
 * Conversions use [LunarCalendar], a port of COUILunarUtil. Moving the year keeps the lunar month
 * and day (the leap flag only survives when the new year has the same leap month); moving the
 * month or the year clamps the day to the new month's length.
 *
 * The date is clamped to [minDate] .. [maxDate]. As in COUILunarDatePicker.updateSpinners, the
 * month and day wheels stop wrapping and only offer the values on the reachable side while the
 * date sits exactly on [minDate] or [maxDate]. Two hairlines frame the selected row and the wheels
 * follow the locale's date field order, like [DatePicker]. The picker is at most
 * [NumberPickerDefaults.PickersMaxWidth] wide, and its first and last wheel also cover the space
 * left at either side, so a drag anywhere across the picker moves them (COUILunarDatePicker.onMeasure).
 *
 * @param value The selected Gregorian date. It is clamped to [minDate] .. [maxDate].
 * @param onValueChange The callback invoked with the new Gregorian date when a wheel moves.
 * @param modifier The modifier to be applied to the [LunarDatePicker].
 * @param enabled Whether the [LunarDatePicker] is enabled for user interaction.
 * @param minDate The earliest selectable Gregorian date (COUI 1910-03-10). Kept at or after
 *   [LunarCalendar.MinSolarDate].
 * @param maxDate The latest selectable Gregorian date (COUI 2099-12-31). Kept at or before
 *   [LunarCalendar.MaxSolarDate].
 * @param yearLabel Converts a lunar year to its display string.
 * @param monthLabel Converts a lunar month (1..12) and its leap flag to its display string.
 * @param dayLabel Converts a lunar day (1..30) to its display string.
 * @param fieldOrder The left-to-right order of the year, month and day wheels (mirrored in RTL).
 * @param columnWidth The width of each of the three wheels.
 * @param colors The [NumberPickerColors] for the wheels.
 * @param dividerColor The color of the two hairlines around the selected row.
 * @param textStyle The [TextStyle] for the wheel items.
 * @param itemHeight The height of each wheel item.
 * @param insideMargin The padding around the wheel columns.
 */
@Composable
fun LunarDatePicker(
    value: DateValue,
    onValueChange: (DateValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minDate: DateValue = LunarDatePickerDefaults.MinDate,
    maxDate: DateValue = LunarDatePickerDefaults.MaxDate,
    yearLabel: (Int) -> String = LunarDatePickerDefaults.YearLabel,
    monthLabel: (month: Int, isLeapMonth: Boolean) -> String = LunarDatePickerDefaults.MonthLabel,
    dayLabel: (Int) -> String = LunarDatePickerDefaults.DayLabel,
    fieldOrder: DateFieldOrder = DatePickerDefaults.localeFieldOrder(),
    columnWidth: Dp = LunarDatePickerDefaults.ColumnWidth,
    colors: NumberPickerColors = NumberPickerDefaults.colors(),
    dividerColor: Color = COUITheme.colorScheme.divider,
    textStyle: TextStyle = COUITheme.textStyles.headlineM,
    itemHeight: Dp = NumberPickerDefaults.ItemHeight,
    insideMargin: PaddingValues = LunarDatePickerDefaults.InsideMargin,
) {
    // The bounds are also kept inside the dates the lunar tables cover.
    val supportedMin = civilToEpochDay(LunarCalendar.MinSolarDate.year, LunarCalendar.MinSolarDate.month, LunarCalendar.MinSolarDate.day)
    val supportedMax = civilToEpochDay(LunarCalendar.MaxSolarDate.year, LunarCalendar.MaxSolarDate.month, LunarCalendar.MaxSolarDate.day)
    val minEpoch = civilToEpochDay(minDate.year, minDate.month, minDate.day).coerceIn(supportedMin, supportedMax)
    val maxEpoch = civilToEpochDay(maxDate.year, maxDate.month, maxDate.day).coerceIn(supportedMin, supportedMax)
    require(minEpoch <= maxEpoch) { "minDate must not be after maxDate" }

    val safeMonth = value.month.coerceIn(1, 12)
    val safeDay = value.day.coerceIn(1, daysInMonth(value.year, safeMonth))
    val epoch = civilToEpochDay(value.year, safeMonth, safeDay).coerceIn(minEpoch, maxEpoch)
    val solar = remember(epoch) { epochDayToCivil(epoch) }
    val lunar = remember(solar) { LunarCalendar.fromSolar(solar) }
    val minLunar = remember(minEpoch) { LunarCalendar.fromSolar(epochDayToCivil(minEpoch)) }
    val maxLunar = remember(maxEpoch) { LunarCalendar.fromSolar(epochDayToCivil(maxEpoch)) }
    val atMin = epoch == minEpoch
    val atMax = epoch == maxEpoch

    // COUILunarDatePicker.updateSpinners: 13 month entries in a year with a leap month; the leap
    // month sits right after its regular month.
    val leapMonth = LunarCalendar.leapMonth(lunar.year)
    val monthCount = if (leapMonth != 0) 13 else 12
    val monthIndex = lunarMonthIndex(lunar.month, lunar.isLeapMonth, leapMonth)
    val monthDays = LunarCalendar.daysInMonth(lunar.year, lunar.month, lunar.isLeapMonth)

    val monthRange = when {
        atMin -> monthIndex until monthCount
        atMax -> 0..monthIndex
        else -> 0 until monthCount
    }
    val dayRange = when {
        atMin -> lunar.day..monthDays
        atMax -> 1..lunar.day
        else -> 1..monthDays
    }
    val wrapMonthAndDay = !atMin && !atMax

    val currentLunar by rememberUpdatedState(lunar)
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentMinEpoch by rememberUpdatedState(minEpoch)
    val currentMaxEpoch by rememberUpdatedState(maxEpoch)

    val emit = remember {
        { target: DateValue? ->
            if (target != null) {
                val clamped = civilToEpochDay(target.year, target.month, target.day).coerceIn(currentMinEpoch, currentMaxEpoch)
                currentOnValueChange(epochDayToCivil(clamped))
            }
        }
    }
    val onYearChange = remember {
        { newYear: Int -> emit(LunarCalendar.changeYear(currentLunar, newYear)) }
    }
    val onMonthChange = remember {
        { newIndex: Int ->
            // COUILunarDatePicker.IncompleteDate.change(MONTH): index -> (month, leap).
            val date = currentLunar
            val leap = LunarCalendar.leapMonth(date.year)
            val number = newIndex + 1
            val (month, isLeap) = when {
                leap != 0 && number == leap + 1 -> leap to true
                leap != 0 && number > leap + 1 -> newIndex to false
                else -> number to false
            }
            val day = date.day.coerceAtMost(LunarCalendar.daysInMonth(date.year, month, isLeap))
            emit(LunarCalendar.toSolar(LunarDate(date.year, month, day, isLeap)))
        }
    }
    val onDayChange = remember {
        { newDay: Int -> emit(LunarCalendar.toSolar(currentLunar.copy(day = newDay))) }
    }
    val currentMonthLabel by rememberUpdatedState(monthLabel)
    val monthIndexLabel = remember(leapMonth) {
        { index: Int ->
            val (month, isLeap) = lunarMonthOfIndex(index, leapMonth)
            currentMonthLabel(month, isLeap)
        }
    }

    // COUILunarDatePicker.onMeasure: the first and last wheel take the space left at the sides.
    NumberPickerRow(
        modifier = modifier
            .widthIn(max = NumberPickerDefaults.PickersMaxWidth)
            .fillMaxWidth()
            .numberPickerDividers(dividerColor)
            .padding(insideMargin),
    ) {
        val fields = fieldOrder.fields
        fields.forEachIndexed { index, field ->
            val alignment = numberPickerColumnAlignment(index, fields.size)
            key(field) {
                when (field) {
                    DateField.Year -> NumberPickerWheel(
                        value = lunar.year,
                        onValueChange = onYearChange,
                        modifier = Modifier.width(columnWidth),
                        enabled = enabled,
                        range = minLunar.year..maxLunar.year,
                        label = yearLabel,
                        wrapAround = true,
                        longPressStepInterval = LunarDatePickerDefaults.YearStepInterval,
                        colors = colors,
                        textStyle = textStyle,
                        itemHeight = itemHeight,
                        contentWidth = columnWidth,
                        contentAlignment = alignment,
                    )

                    DateField.Month -> NumberPickerWheel(
                        value = monthIndex,
                        onValueChange = onMonthChange,
                        modifier = Modifier.width(columnWidth),
                        enabled = enabled,
                        range = monthRange,
                        label = monthIndexLabel,
                        wrapAround = wrapMonthAndDay,
                        longPressStepInterval = LunarDatePickerDefaults.MonthStepInterval,
                        colors = colors,
                        textStyle = textStyle,
                        itemHeight = itemHeight,
                        contentWidth = columnWidth,
                        contentAlignment = alignment,
                    )

                    DateField.Day -> NumberPickerWheel(
                        value = lunar.day,
                        onValueChange = onDayChange,
                        modifier = Modifier.width(columnWidth),
                        enabled = enabled,
                        range = dayRange,
                        label = dayLabel,
                        wrapAround = wrapMonthAndDay,
                        longPressStepInterval = LunarDatePickerDefaults.DayStepInterval,
                        colors = colors,
                        textStyle = textStyle,
                        itemHeight = itemHeight,
                        contentWidth = columnWidth,
                        contentAlignment = alignment,
                    )
                }
            }
        }
    }
}

/** Contains default values used by [LunarDatePicker]. */
object LunarDatePickerDefaults {

    /**
     * The earliest selectable date: COUILunarDatePicker's static sMinDate, 1910-03-10. Its
     * constructor also calls setMinDate(1910-01-01), but setMinDate only accepts a date in a
     * different year or on the same day of the year, so that call is rejected ("setMinDate
     * failed!") and 1910-03-10 stays in effect.
     */
    val MinDate: DateValue = DateValue(1910, 3, 10)

    /** The latest selectable date (COUILunarDatePicker constructor: setMaxDate(2099-12-31)). */
    val MaxDate: DateValue = DateValue(2099, 12, 31)

    /** Width of every wheel (coui_lunar_date_picker.xml: coui_number_picker_width_middle). */
    val ColumnWidth = 76.dp

    /** Padding around the wheel columns (coui_lunar_date_picker.xml: coui_number_picker_view_padding_vertical). */
    val InsideMargin = PaddingValues(vertical = 12.dp)

    /** Auto-repeat interval of the year wheel while a side row is held (COUILunarDatePicker). */
    val YearStepInterval: Long = 100L

    /** Auto-repeat interval of the month wheel while a side row is held (COUILunarDatePicker). */
    val MonthStepInterval: Long = 200L

    /** Auto-repeat interval of the day wheel while a side row is held (COUILunarDatePicker). */
    val DayStepInterval: Long = 100L

    /** Default year label: the plain lunar year number, as COUINumberPicker formats it. */
    val YearLabel: (Int) -> String = { it.toString() }

    /**
     * Default month label: the coui_lunar_month names (正月 .. 冬月, 腊月), prefixed with
     * coui_lunar_leap_string (闰) for the leap month.
     */
    val MonthLabel: (Int, Boolean) -> String = { month, isLeap ->
        if (isLeap) "闰" + LunarMonthNames[month - 1] else LunarMonthNames[month - 1]
    }

    /** Default day label: COUILunarUtil.chineseStringOfALunarDay (初一 .. 三十). */
    val DayLabel: (Int) -> String = { LunarCalendar.dayName(it) }

    private val LunarMonthNames = listOf(
        "正月", "二月", "三月", "四月", "五月", "六月",
        "七月", "八月", "九月", "十月", "冬月", "腊月",
    )
}

/** The wheel index of a lunar month: months after the leap month shift down by one row. */
private fun lunarMonthIndex(month: Int, isLeapMonth: Boolean, leapMonth: Int): Int = if (leapMonth == 0 || month < leapMonth || (month == leapMonth && !isLeapMonth)) month - 1 else month

/** The lunar month and its leap flag shown at a wheel [index]. */
private fun lunarMonthOfIndex(index: Int, leapMonth: Int): Pair<Int, Boolean> = when {
    leapMonth == 0 || index < leapMonth -> (index + 1) to false
    index == leapMonth -> leapMonth to true
    else -> index to false
}
