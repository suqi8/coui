// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A date plus a time of day, used by [DateTimePicker].
 *
 * @param date The calendar date.
 * @param time The time of day; the hour is always in 24-hour form.
 */
@Immutable
data class DateTimeValue(
    val date: DateValue,
    val time: TimeValue,
)

/**
 * The ISO day of the week of this date: 1 = Monday .. 7 = Sunday.
 */
val DateValue.dayOfWeek: Int
    get() = (epochDayOf(this) + 3).mod(7) + 1

/**
 * A [DateTimePicker] component with COUI style, mirroring COUITimePicker: one date wheel
 * (weekday, month and day, with "Today" for the current date) followed by hour and minute wheels,
 * plus an AM/PM wheel in 12-hour mode.
 *
 * As in COUITimePicker the date wheel does not wrap, the hour and minute wheels do, the wheels are
 * 150dp (date, 174dp outside Chinese and English) and 62dp wide and shrink proportionally when the
 * picker is narrower than their sum. In 12-hour mode scrolling the hour wheel onto 12 flips
 * AM/PM, and the two hairlines run edge to edge (they are inset 16dp in 24-hour mode).
 *
 * @param value The currently selected date and time.
 * @param onValueChange The callback invoked when the selected date or time changes.
 * @param modifier The modifier to be applied to the [DateTimePicker].
 * @param enabled Whether the [DateTimePicker] is enabled for user interaction.
 * @param is24Hour Whether the hour wheel uses the 24-hour format. When false, hours display as
 *   1..12 with an AM/PM wheel.
 * @param amPmFirst Whether the AM/PM wheel sits before the hour wheel (the COUITimePicker layout
 *   order) rather than after the minute wheel. Only used when [is24Hour] is false.
 * @param minuteFiveStep Whether the minute wheel steps by five minutes (COUITimePicker
 *   setMinuteStepToFive).
 * @param yearRange The years the date wheel covers; null keeps COUI's window of 50 years before
 *   and 150 years after the first [value].
 * @param dateLabel Converts a date to its display string on the date wheel.
 * @param todayLabel The display string of the date wheel row for today.
 * @param amLabel The display string for AM.
 * @param pmLabel The display string for PM.
 * @param dateColumnWidth The width of the date column.
 * @param timeColumnWidth The width of the hour, minute and AM/PM columns.
 * @param colors The [NumberPickerColors] for the wheels.
 * @param dividerColor The color of the two hairlines around the selected row.
 * @param textStyle The [TextStyle] for the wheel items.
 * @param itemHeight The height of each wheel item.
 * @param insideMargin The padding around the wheel columns.
 */
@Composable
fun DateTimePicker(
    value: DateTimeValue,
    onValueChange: (DateTimeValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    is24Hour: Boolean = true,
    amPmFirst: Boolean = true,
    minuteFiveStep: Boolean = false,
    yearRange: IntRange? = null,
    dateLabel: (DateValue) -> String = DateTimePickerDefaults.DateLabel,
    todayLabel: String = DateTimePickerDefaults.TodayLabel,
    amLabel: String = TimePickerDefaults.AmLabel,
    pmLabel: String = TimePickerDefaults.PmLabel,
    dateColumnWidth: Dp = DateTimePickerDefaults.dateColumnWidth(),
    timeColumnWidth: Dp = DateTimePickerDefaults.TimeColumnWidth,
    colors: NumberPickerColors = NumberPickerDefaults.colors(),
    dividerColor: Color = COUITheme.colorScheme.dividerLine,
    textStyle: TextStyle = COUITheme.textStyles.title3,
    itemHeight: Dp = NumberPickerDefaults.ItemHeight,
    insideMargin: PaddingValues = DateTimePickerDefaults.InsideMargin,
) {
    // COUITimePicker.getTimePicker: the date wheel starts 50 years before the initial date.
    val initialYear = remember { value.date.year }
    val defaultYears = remember(initialYear) { (initialYear - 50)..(initialYear + 149) }
    val years = yearRange ?: defaultYears
    require(years.first <= years.last) { "yearRange must not be empty" }

    val firstDay = epochDayOf(DateValue(years.first, 1, 1))
    val lastDay = epochDayOf(DateValue(years.last, 12, 31))
    val date = value.date.let {
        val month = it.month.coerceIn(1, 12)
        DateValue(it.year, month, it.day.coerceIn(1, daysInMonth(it.year, month)))
    }
    val selectedDay = epochDayOf(date).coerceIn(firstDay, lastDay)
    val hour = value.time.hour.coerceIn(0, 23)
    val minute = value.time.minute.coerceIn(0, 59)

    val currentValue by rememberUpdatedState(DateTimeValue(dateOfEpochDay(selectedDay), TimeValue(hour, minute)))
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentIs24Hour by rememberUpdatedState(is24Hour)
    val currentFiveStep by rememberUpdatedState(minuteFiveStep)

    val onDateChange = remember {
        { epochDay: Int -> currentOnValueChange(currentValue.copy(date = dateOfEpochDay(epochDay))) }
    }
    val onHourChange = remember {
        { newHour: Int ->
            val time = currentValue.time
            val hour24 = when {
                currentIs24Hour -> newHour

                // COUITimePicker flips AM/PM whenever the hour wheel lands on 12.
                newHour == 12 -> if (time.hour >= 12) 0 else 12

                time.hour >= 12 -> newHour + 12

                else -> newHour
            }
            currentOnValueChange(currentValue.copy(time = time.copy(hour = hour24)))
        }
    }
    val onMinuteChange = remember {
        { newMinute: Int ->
            val minuteValue = if (currentFiveStep) newMinute * 5 else newMinute
            currentOnValueChange(currentValue.copy(time = currentValue.time.copy(minute = minuteValue)))
        }
    }
    val onAmPmChange = remember {
        { amPm: Int ->
            val time = currentValue.time
            val hour24 = time.hour % 12 + if (amPm == 1) 12 else 0
            if (hour24 != time.hour) currentOnValueChange(currentValue.copy(time = time.copy(hour = hour24)))
        }
    }

    val today = remember { platformToday() }
    val todayEpochDay = remember(today) { epochDayOf(today) }
    val currentDateLabel by rememberUpdatedState(dateLabel)
    val currentTodayLabel by rememberUpdatedState(todayLabel)
    val dayLabel = remember(todayEpochDay) {
        { epochDay: Int -> if (epochDay == todayEpochDay) currentTodayLabel else currentDateLabel(dateOfEpochDay(epochDay)) }
    }
    val amPmLabel = remember(amLabel, pmLabel) { { amPm: Int -> if (amPm == 0) amLabel else pmLabel } }
    val minuteLabel = if (minuteFiveStep) FiveStepMinuteLabel else TwoDigitMinuteLabel

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .numberPickerDividers(dividerColor, inset = if (is24Hour) NumberPickerDividerInset else 0.dp)
            .padding(insideMargin),
        contentAlignment = Alignment.Center,
    ) {
        // COUITimePicker.measureChildConstrained: shrink every column when they do not fit.
        val total = dateColumnWidth + timeColumnWidth * if (is24Hour) 2 else 3
        val scale = if (maxWidth < total && total > 0.dp) maxWidth / total else 1f
        val timeWidth = timeColumnWidth * scale

        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            NumberPicker(
                value = selectedDay,
                onValueChange = onDateChange,
                modifier = Modifier.width(dateColumnWidth * scale),
                enabled = enabled,
                range = firstDay..lastDay,
                label = dayLabel,
                wrapAround = false,
                colors = colors,
                textStyle = textStyle,
                itemHeight = itemHeight,
            )
            val amPmWheel: @Composable () -> Unit = {
                NumberPicker(
                    value = if (hour < 12) 0 else 1,
                    onValueChange = onAmPmChange,
                    modifier = Modifier.width(timeWidth),
                    enabled = enabled,
                    range = 0..1,
                    label = amPmLabel,
                    wrapAround = false,
                    colors = colors,
                    textStyle = textStyle,
                    itemHeight = itemHeight,
                )
            }
            if (!is24Hour && amPmFirst) amPmWheel()
            NumberPicker(
                value = when {
                    is24Hour -> hour
                    hour % 12 == 0 -> 12
                    else -> hour % 12
                },
                onValueChange = onHourChange,
                modifier = Modifier.width(timeWidth),
                enabled = enabled,
                range = if (is24Hour) 0..23 else 1..12,
                label = if (is24Hour) TwoDigitMinuteLabel else PlainHourLabel,
                wrapAround = true,
                colors = colors,
                textStyle = textStyle,
                itemHeight = itemHeight,
            )
            NumberPicker(
                value = if (minuteFiveStep) minute / 5 else minute,
                onValueChange = onMinuteChange,
                modifier = Modifier.width(timeWidth),
                enabled = enabled,
                range = if (minuteFiveStep) 0..11 else 0..59,
                label = minuteLabel,
                wrapAround = true,
                colors = colors,
                textStyle = textStyle,
                itemHeight = itemHeight,
            )
            if (!is24Hour && !amPmFirst) amPmWheel()
        }
    }
}

/**
 * Contains default values used by [DateTimePicker].
 */
object DateTimePickerDefaults {

    /** Width of the date column (COUI coui_time_picker_date_width). */
    val DateColumnWidth = 150.dp

    /** Width of the date column outside Chinese and English (COUI coui_number_picker_width_biggest). */
    val WideDateColumnWidth = 174.dp

    /** Width of the hour, minute and AM/PM columns (COUI coui_number_picker_width_small). */
    val TimeColumnWidth = 62.dp

    /** Padding around the wheel columns (COUI coui_number_picker_view_padding_vertical). */
    val InsideMargin = PaddingValues(vertical = 12.dp)

    /** Default label of today's row (COUI coui_time_picker_today in English). */
    val TodayLabel = "Today"

    /**
     * Default date label, as COUI's `DateUtils.formatDateTime` with abbreviated weekday and month
     * and no year shows it in English: "Thu, Oct 23".
     */
    val DateLabel: (DateValue) -> String = { date ->
        "${WeekdayAbbreviations[date.dayOfWeek - 1]}, ${MonthAbbreviations[date.month - 1]} ${date.day}"
    }

    /**
     * The date column width for the current locale: COUITimePicker widens it to 174dp unless the
     * language is Chinese or English.
     */
    @Composable
    fun dateColumnWidth(): Dp {
        val language = Locale.current.language
        return if (language == "zh" || language == "en") DateColumnWidth else WideDateColumnWidth
    }

    private val WeekdayAbbreviations = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private val MonthAbbreviations = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
    )
}

private val TwoDigitMinuteLabel: (Int) -> String = { it.toString().padStart(2, '0') }

private val PlainHourLabel: (Int) -> String = { it.toString() }

private val FiveStepMinuteLabel: (Int) -> String = { (it * 5).toString().padStart(2, '0') }

/** Days since 1970-01-01 (proleptic Gregorian), H. Hinnant's days_from_civil. */
internal fun epochDayOf(date: DateValue): Int {
    val y = if (date.month <= 2) date.year - 1 else date.year
    val era = (if (y >= 0) y else y - 399) / 400
    val yearOfEra = y - era * 400
    val shiftedMonth = (date.month + 9) % 12
    val dayOfYear = (153 * shiftedMonth + 2) / 5 + date.day - 1
    val dayOfEra = yearOfEra * 365 + yearOfEra / 4 - yearOfEra / 100 + dayOfYear
    return era * 146097 + dayOfEra - 719468
}

/** Inverse of [epochDayOf], H. Hinnant's civil_from_days. */
internal fun dateOfEpochDay(epochDay: Int): DateValue {
    val z = epochDay + 719468
    val era = (if (z >= 0) z else z - 146096) / 146097
    val dayOfEra = z - era * 146097
    val yearOfEra = (dayOfEra - dayOfEra / 1460 + dayOfEra / 36524 - dayOfEra / 146096) / 365
    val dayOfYear = dayOfEra - (365 * yearOfEra + yearOfEra / 4 - yearOfEra / 100)
    val shiftedMonth = (5 * dayOfYear + 2) / 153
    val day = dayOfYear - (153 * shiftedMonth + 2) / 5 + 1
    val month = if (shiftedMonth < 10) shiftedMonth + 3 else shiftedMonth - 9
    val year = yearOfEra + era * 400 + if (month <= 2) 1 else 0
    return DateValue(year, month, day)
}
