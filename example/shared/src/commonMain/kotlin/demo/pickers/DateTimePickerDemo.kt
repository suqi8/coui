// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.pickers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.DateFieldOrder
import io.github.suqi8.coui.kmp.basic.DatePicker
import io.github.suqi8.coui.kmp.basic.DatePickerDefaults
import io.github.suqi8.coui.kmp.basic.DateTimePicker
import io.github.suqi8.coui.kmp.basic.DateTimeValue
import io.github.suqi8.coui.kmp.basic.DateValue
import io.github.suqi8.coui.kmp.basic.TimePicker
import io.github.suqi8.coui.kmp.basic.TimeValue
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_default
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_value_format
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_24_hour
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_component
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_field_order
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_limited_range
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_limited_range_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_minute_five_step
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_no_year
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_show_units
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_unit_hour
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_unit_minute
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_variant_date
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_variant_date_bounded
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_variant_date_time
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_variant_time_12h
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_variant_time_24h
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_variant_year_optional
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_year_ignorable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_date_time_picker_year_ignorable_summary
import org.jetbrains.compose.resources.stringResource

/** The demo page of `DateTimePicker`, listed in its category and found by the search. */
internal val DateTimePickerDemo = Demo(
    id = "date_time_picker",
    apiName = "DateTimePicker",
    title = Res.string.demo_date_time_picker_title,
    category = DemoCategory.Pickers,
    page = DemoPage.Variants { dateTimePickerPage() },
    keywords = listOf("DatePicker", "TimePicker"),
    searchTerms = Res.string.demo_date_time_picker_search_terms,
)

/** The pickers of the playground, by API name. */
private val PickerTypes = listOf("DatePicker", "TimePicker", "DateTimePicker")

/** The limits of the limited-range examples. */
private val BoundedMinDate = DateValue(year = 2026, month = 3, day = 15)
private val BoundedMaxDate = DateValue(year = 2027, month = 8, day = 20)

/** The year a date falls back to when an ignored year has to be shown by a picker without that row. */
private const val FALLBACK_YEAR = 2026

private fun LazyListScope.dateTimePickerPage() {
    playground {
        var typeIndex by rememberSaveable { mutableIntStateOf(0) }
        var year by rememberSaveable { mutableIntStateOf(2026) }
        var month by rememberSaveable { mutableIntStateOf(7) }
        var day by rememberSaveable { mutableIntStateOf(26) }
        var hour by rememberSaveable { mutableIntStateOf(16) }
        var minute by rememberSaveable { mutableIntStateOf(30) }
        var is24Hour by rememberSaveable { mutableStateOf(true) }
        var showUnits by rememberSaveable { mutableStateOf(false) }
        var minuteFiveStep by rememberSaveable { mutableStateOf(false) }
        var yearIgnorable by rememberSaveable { mutableStateOf(false) }
        var limitedRange by rememberSaveable { mutableStateOf(false) }
        // 0 follows the locale; i > 0 is DateFieldOrder.entries[i - 1].
        var orderIndex by rememberSaveable { mutableIntStateOf(0) }
        var enabled by rememberSaveable { mutableStateOf(true) }

        val yearIgnored = year == DatePickerDefaults.IgnoredYear
        val date = DateValue(year = year, month = month, day = day)
        val time = TimeValue(hour = hour, minute = minute)
        val onDateChange: (DateValue) -> Unit = {
            year = it.year
            month = it.month
            day = it.day
        }
        val onTimeChange: (TimeValue) -> Unit = {
            hour = it.hour
            minute = it.minute
        }
        val hourUnit = if (showUnits) stringResource(Res.string.demo_date_time_picker_unit_hour) else ""
        val minuteUnit = if (showUnits) stringResource(Res.string.demo_date_time_picker_unit_minute) else ""

        PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
            when (typeIndex) {
                0 -> DatePicker(
                    value = date,
                    onValueChange = onDateChange,
                    enabled = enabled,
                    yearIgnorable = yearIgnorable,
                    minDate = if (limitedRange) BoundedMinDate else DatePickerDefaults.MinDate,
                    maxDate = if (limitedRange) BoundedMaxDate else DatePickerDefaults.MaxDate,
                    fieldOrder = if (orderIndex == 0) DatePickerDefaults.localeFieldOrder() else DateFieldOrder.entries[orderIndex - 1],
                )

                1 -> TimePicker(
                    value = time,
                    onValueChange = onTimeChange,
                    enabled = enabled,
                    is24Hour = is24Hour,
                    hourUnit = hourUnit,
                    minuteUnit = minuteUnit,
                )

                else -> DateTimePicker(
                    value = DateTimeValue(date = if (yearIgnored) date.copy(year = FALLBACK_YEAR) else date, time = time),
                    onValueChange = {
                        onDateChange(it.date)
                        onTimeChange(it.time)
                    },
                    enabled = enabled,
                    is24Hour = is24Hour,
                    minuteFiveStep = minuteFiveStep,
                    hourUnit = hourUnit,
                    minuteUnit = minuteUnit,
                )
            }
            val dateText = if (yearIgnored) "--${month.twoDigits()}-${day.twoDigits()}" else date.isoText()
            val timeText = "${hour.twoDigits()}:${minute.twoDigits()}"
            PickerCaption(
                text = stringResource(
                    Res.string.common_value_format,
                    when (typeIndex) {
                        0 -> dateText
                        1 -> timeText
                        else -> "$dateText $timeText"
                    },
                ),
            )
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = PickerTypes,
                    selectedIndex = typeIndex,
                    title = stringResource(Res.string.demo_date_time_picker_component),
                    onSelectedIndexChange = { typeIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = is24Hour,
                    onCheckedChange = { is24Hour = it },
                    title = stringResource(Res.string.demo_date_time_picker_24_hour),
                    enabled = typeIndex != 0,
                )
            }
            item {
                SwitchPreference(
                    checked = showUnits,
                    onCheckedChange = { showUnits = it },
                    title = stringResource(Res.string.demo_date_time_picker_show_units),
                    enabled = typeIndex != 0,
                )
            }
            item {
                SwitchPreference(
                    checked = minuteFiveStep,
                    onCheckedChange = { minuteFiveStep = it },
                    title = stringResource(Res.string.demo_date_time_picker_minute_five_step),
                    enabled = typeIndex == 2,
                )
            }
            item {
                OverlayDropdownPreference(
                    items = listOf(stringResource(Res.string.common_default)) + DateFieldOrder.entries.map { it.name },
                    selectedIndex = orderIndex,
                    title = stringResource(Res.string.demo_date_time_picker_field_order),
                    enabled = typeIndex == 0,
                    onSelectedIndexChange = { orderIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = yearIgnorable,
                    onCheckedChange = {
                        yearIgnorable = it
                        if (!it && yearIgnored) year = FALLBACK_YEAR
                    },
                    title = stringResource(Res.string.demo_date_time_picker_year_ignorable),
                    summary = stringResource(Res.string.demo_date_time_picker_year_ignorable_summary),
                    enabled = typeIndex == 0,
                )
            }
            item {
                SwitchPreference(
                    checked = limitedRange,
                    onCheckedChange = { limitedRange = it },
                    title = stringResource(Res.string.demo_date_time_picker_limited_range),
                    summary = stringResource(
                        Res.string.demo_date_time_picker_limited_range_summary,
                        BoundedMinDate.isoText(),
                        BoundedMaxDate.isoText(),
                    ),
                    enabled = typeIndex == 0,
                )
            }
            item {
                SwitchPreference(
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                    title = stringResource(Res.string.common_enabled),
                )
            }
        }
    }
    variant(key = "date", title = Res.string.demo_date_time_picker_variant_date) {
        var date by remember { mutableStateOf(DateValue(year = 2026, month = 7, day = 26)) }
        DatePicker(value = date, onValueChange = { date = it })
    }
    variant(key = "dateBounded", title = Res.string.demo_date_time_picker_variant_date_bounded) {
        var date by remember { mutableStateOf(DateValue(year = 2026, month = 3, day = 20)) }
        DatePicker(
            value = date,
            onValueChange = { date = it },
            minDate = BoundedMinDate,
            maxDate = BoundedMaxDate,
        )
        PickerCaption(
            text = stringResource(
                Res.string.demo_date_time_picker_limited_range_summary,
                BoundedMinDate.isoText(),
                BoundedMaxDate.isoText(),
            ),
        )
    }
    variant(key = "yearOptional", title = Res.string.demo_date_time_picker_variant_year_optional) {
        var date by remember { mutableStateOf(DateValue(year = DatePickerDefaults.IgnoredYear, month = 2, day = 29)) }
        DatePicker(value = date, onValueChange = { date = it }, yearIgnorable = true)
        PickerCaption(
            text = stringResource(
                Res.string.common_value_format,
                if (date.year == DatePickerDefaults.IgnoredYear) {
                    "${stringResource(Res.string.demo_date_time_picker_no_year)} ${date.month.twoDigits()}-${date.day.twoDigits()}"
                } else {
                    date.isoText()
                },
            ),
        )
    }
    variant(key = "time24h", title = Res.string.demo_date_time_picker_variant_time_24h) {
        var time by remember { mutableStateOf(TimeValue(hour = 16, minute = 30)) }
        TimePicker(
            value = time,
            onValueChange = { time = it },
            hourUnit = stringResource(Res.string.demo_date_time_picker_unit_hour),
            minuteUnit = stringResource(Res.string.demo_date_time_picker_unit_minute),
        )
    }
    variant(key = "time12h", title = Res.string.demo_date_time_picker_variant_time_12h) {
        var time by remember { mutableStateOf(TimeValue(hour = 16, minute = 30)) }
        TimePicker(value = time, onValueChange = { time = it }, is24Hour = false)
    }
    variant(key = "dateTime", title = Res.string.demo_date_time_picker_variant_date_time) {
        var value by remember {
            mutableStateOf(DateTimeValue(DateValue(year = 2026, month = 10, day = 23), TimeValue(hour = 11, minute = 30)))
        }
        DateTimePicker(value = value, onValueChange = { value = it }, is24Hour = false)
    }
}
