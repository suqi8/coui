// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.NonRestartableComposable
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
 * A plain time-of-day holder used by [TimePicker].
 *
 * The hour is always stored in 24-hour form (0..23) regardless of the
 * display mode of the picker; out-of-range values are coerced.
 *
 * @param hour The hour of the day in 24-hour form (0..23).
 * @param minute The minute of the hour (0..59).
 */
@Immutable
data class TimeValue(
    val hour: Int,
    val minute: Int,
)

/**
 * A [TimePicker] component with COUI style.
 *
 * Hour and minute scroll wheels arranged like the COUI time picker
 * (COUITimeLimitPicker). In 12-hour mode an AM/PM wheel is added; COUI places
 * it before the hour wheel for locales whose time pattern starts with the
 * day period (e.g. Chinese) and after the minute wheel otherwise, which
 * [amPmFirst] follows by default. Optional unit labels (e.g. "h" / "min")
 * are drawn after the selected value on its baseline, matching the COUI unit
 * text. Two hairlines frame the selected row at the center ± 24dp, inset 16dp
 * from both sides, the AM/PM wheel uses an 18dp unselected text size, and the
 * picker is at most [NumberPickerDefaults.PickersMaxWidth] wide.
 *
 * @param value The currently selected time. The hour is always in 24-hour form.
 * @param onValueChange The callback invoked when the selected time changes.
 * @param modifier The modifier to be applied to the [TimePicker].
 * @param enabled Whether the [TimePicker] is enabled for user interaction.
 * @param is24Hour Whether the hour wheel uses the 24-hour format. When false,
 *   hours display as 1..12 with an AM/PM wheel.
 * @param amPmFirst Whether the AM/PM wheel is placed before the hour wheel
 *   (only used when [is24Hour] is false). Defaults to the locale's order.
 * @param amLabel The display string for AM on the AM/PM wheel.
 * @param pmLabel The display string for PM on the AM/PM wheel.
 * @param hourUnit The unit label drawn beside the selected hour; empty to hide.
 * @param minuteUnit The unit label drawn beside the selected minute; empty to hide.
 * @param visibleItemCount The number of visible rows of every wheel (COUITimeLimitPicker
 *   setRowNumber). Must be odd and at least 3.
 * @param hourColumnWidth The width of the hour column.
 * @param minuteColumnWidth The width of the minute column.
 * @param amPmColumnWidth The width of the AM/PM column.
 * @param colors The [NumberPickerColors] for the wheels.
 * @param dividerColor The color of the two hairlines around the selected row.
 * @param textStyle The [TextStyle] for the wheel items.
 * @param itemHeight The height of each wheel item.
 * @param insideMargin The padding around the wheel columns.
 */
@Composable
fun TimePicker(
    value: TimeValue,
    onValueChange: (TimeValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    is24Hour: Boolean = true,
    amPmFirst: Boolean = TimePickerDefaults.localeAmPmFirst(),
    amLabel: String = TimePickerDefaults.amLabel(),
    pmLabel: String = TimePickerDefaults.pmLabel(),
    hourUnit: String = TimePickerDefaults.HourUnit,
    minuteUnit: String = TimePickerDefaults.MinuteUnit,
    visibleItemCount: Int = 3,
    hourColumnWidth: Dp = TimePickerDefaults.HourColumnWidth,
    minuteColumnWidth: Dp = TimePickerDefaults.MinuteColumnWidth,
    amPmColumnWidth: Dp = TimePickerDefaults.AmPmColumnWidth,
    colors: NumberPickerColors = NumberPickerDefaults.colors(),
    dividerColor: Color = COUITheme.colorScheme.divider,
    textStyle: TextStyle = COUITheme.textStyles.headlineM,
    itemHeight: Dp = NumberPickerDefaults.ItemHeight,
    insideMargin: PaddingValues = TimePickerDefaults.InsideMargin,
) {
    val coercedHour = value.hour.coerceIn(0, 23)
    val coercedMinute = value.minute.coerceIn(0, 59)

    // Latest state readable from the remember-cached per-wheel callbacks below.
    val currentValue by rememberUpdatedState(TimeValue(coercedHour, coercedMinute))
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentIs24Hour by rememberUpdatedState(is24Hour)

    val onHourChange = remember {
        { newHour: Int ->
            val time = currentValue
            // Wheel shows 1..12; map back to 24-hour form keeping the current half of the day.
            val hour = when {
                currentIs24Hour -> newHour
                time.hour < 12 -> newHour % 12
                else -> newHour % 12 + 12
            }
            currentOnValueChange(TimeValue(hour, time.minute))
        }
    }
    val onMinuteChange = remember {
        { newMinute: Int ->
            currentOnValueChange(currentValue.copy(minute = newMinute))
        }
    }
    val onAmPmChange = remember {
        { newAmPm: Int ->
            val time = currentValue
            val hour = if (newAmPm == 0) time.hour % 12 else time.hour % 12 + 12
            if (hour != time.hour) {
                currentOnValueChange(TimeValue(hour, time.minute))
            }
        }
    }

    val amPmLabel = remember(amLabel, pmLabel) {
        { amPm: Int -> if (amPm == 0) amLabel else pmLabel }
    }
    // COUI zero-pads hours only in 24-hour mode; minutes are always two digits.
    val hourLabel = if (is24Hour) TwoDigitLabel else PlainLabel
    val hourRange = if (is24Hour) 0..23 else 1..12
    val displayHour = when {
        is24Hour -> coercedHour
        coercedHour % 12 == 0 -> 12
        else -> coercedHour % 12
    }
    val showAmPm = !is24Hour

    Row(
        modifier = modifier
            .widthIn(max = NumberPickerDefaults.PickersMaxWidth)
            .fillMaxWidth()
            .numberPickerDividers(dividerColor)
            .padding(insideMargin),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showAmPm && amPmFirst) {
            AmPmWheel(
                value = if (coercedHour < 12) 0 else 1,
                onValueChange = onAmPmChange,
                enabled = enabled,
                label = amPmLabel,
                visibleItemCount = visibleItemCount,
                columnWidth = amPmColumnWidth,
                colors = colors,
                textStyle = textStyle,
                itemHeight = itemHeight,
            )
        }
        NumberPicker(
            value = displayHour,
            onValueChange = onHourChange,
            modifier = Modifier.width(hourColumnWidth),
            enabled = enabled,
            range = hourRange,
            label = hourLabel,
            unit = hourUnit,
            visibleItemCount = visibleItemCount,
            wrapAround = true,
            colors = colors,
            textStyle = textStyle,
            itemHeight = itemHeight,
        )
        NumberPicker(
            value = coercedMinute,
            onValueChange = onMinuteChange,
            modifier = Modifier.width(minuteColumnWidth),
            enabled = enabled,
            range = 0..59,
            label = TwoDigitLabel,
            unit = minuteUnit,
            visibleItemCount = visibleItemCount,
            wrapAround = true,
            longPressStepInterval = TimePickerDefaults.MinuteStepInterval,
            colors = colors,
            textStyle = textStyle,
            itemHeight = itemHeight,
        )
        if (showAmPm && !amPmFirst) {
            AmPmWheel(
                value = if (coercedHour < 12) 0 else 1,
                onValueChange = onAmPmChange,
                enabled = enabled,
                label = amPmLabel,
                visibleItemCount = visibleItemCount,
                columnWidth = amPmColumnWidth,
                colors = colors,
                textStyle = textStyle,
                itemHeight = itemHeight,
            )
        }
    }
}

@Composable
@NonRestartableComposable
private fun AmPmWheel(
    value: Int,
    onValueChange: (Int) -> Unit,
    enabled: Boolean,
    label: (Int) -> String,
    visibleItemCount: Int,
    columnWidth: Dp,
    colors: NumberPickerColors,
    textStyle: TextStyle,
    itemHeight: Dp,
) {
    NumberPicker(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.width(columnWidth),
        enabled = enabled,
        range = 0..1,
        label = label,
        visibleItemCount = visibleItemCount,
        wrapAround = false,
        colors = colors,
        textStyle = textStyle,
        unselectedTextSize = TimePickerDefaults.AmPmUnselectedTextSize,
        itemHeight = itemHeight,
    )
}

/**
 * Contains default values used by [TimePicker].
 */
object TimePickerDefaults {

    /** Width of the hour column (COUI coui_number_picker_width_middle). */
    val HourColumnWidth = 76.dp

    /** Width of the minute column (COUI coui_number_picker_width_middle). */
    val MinuteColumnWidth = 76.dp

    /** Width of the AM/PM column (COUI coui_number_picker_width_small). */
    val AmPmColumnWidth = 62.dp

    /** Padding around the wheel columns (COUI coui_number_picker_view_padding_vertical). */
    val InsideMargin = PaddingValues(vertical = 12.dp)

    /** Unselected text size of the AM/PM wheel (COUI coui_time_limit_picker startTextSize). */
    val AmPmUnselectedTextSize = 18.dp

    /** Auto-repeat interval of the minute wheel while a side row is held (COUITimeLimitPicker). */
    val MinuteStepInterval: Long = 100L

    /** The AM label of the current locale (COUI coui_time_picker_ampm: "AM", "上午" in Chinese). */
    @Composable
    fun amLabel(): String = rememberPickerSymbols().am

    /** The PM label of the current locale (COUI coui_time_picker_ampm: "PM", "下午" in Chinese). */
    @Composable
    fun pmLabel(): String = rememberPickerSymbols().pm

    /**
     * Whether the current locale puts AM/PM before the hour, read from its best "hm" time pattern
     * as COUITimeLimitPicker.reorderSpinners does (true for Chinese, false for English).
     */
    @Composable
    fun localeAmPmFirst(): Boolean {
        val locale = Locale.current
        return remember(locale) { amPmBeforeHour(platformTimePattern()) }
    }

    /** Default hour unit label (hidden). */
    val HourUnit = ""

    /** Default minute unit label (hidden). */
    val MinuteUnit = ""
}

private val TwoDigitLabel: (Int) -> String = { it.toString().padStart(2, '0') }

private val PlainLabel: (Int) -> String = { it.toString() }
