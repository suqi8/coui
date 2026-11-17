// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.pickers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import io.github.suqi8.coui.kmp.basic.CalendarPickerDefaults
import io.github.suqi8.coui.kmp.basic.DateFieldOrder
import io.github.suqi8.coui.kmp.basic.DatePickerDefaults
import io.github.suqi8.coui.kmp.basic.DateValue
import io.github.suqi8.coui.kmp.basic.LunarCalendar
import io.github.suqi8.coui.kmp.basic.LunarDatePicker
import io.github.suqi8.coui.kmp.basic.LunarDatePickerDefaults
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_default
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lunar_date_picker_field_order
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lunar_date_picker_jump_leap_month
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lunar_date_picker_jump_leap_month_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lunar_date_picker_jump_today
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lunar_date_picker_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lunar_date_picker_solar_format
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lunar_date_picker_title
import org.jetbrains.compose.resources.stringResource

/** The demo page of `LunarDatePicker`, listed in its category and found by the search. */
internal val LunarDatePickerDemo = Demo(
    id = "lunar_date_picker",
    apiName = "LunarDatePicker",
    title = Res.string.demo_lunar_date_picker_title,
    category = DemoCategory.Pickers,
    page = DemoPage.Variants { lunarDatePickerPage() },
    searchTerms = Res.string.demo_lunar_date_picker_search_terms,
)

/** 2025-07-25 is the first day of the leap sixth lunar month. */
private val LeapMonthStart = DateValue(year = 2025, month = 7, day = 25)

private fun LazyListScope.lunarDatePickerPage() {
    playground {
        var year by rememberSaveable { mutableIntStateOf(LeapMonthStart.year) }
        var month by rememberSaveable { mutableIntStateOf(LeapMonthStart.month) }
        var day by rememberSaveable { mutableIntStateOf(LeapMonthStart.day) }
        // 0 follows the locale; i > 0 is DateFieldOrder.entries[i - 1].
        var orderIndex by rememberSaveable { mutableIntStateOf(DateFieldOrder.YearMonthDay.ordinal + 1) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        val date = DateValue(year = year, month = month, day = day)
        val setDate: (DateValue) -> Unit = {
            year = it.year
            month = it.month
            day = it.day
        }

        PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
            LunarDatePicker(
                value = date,
                onValueChange = setDate,
                enabled = enabled,
                minDate = LunarDatePickerDefaults.MinDate,
                maxDate = LunarDatePickerDefaults.MaxDate,
                fieldOrder = if (orderIndex == 0) DatePickerDefaults.localeFieldOrder() else DateFieldOrder.entries[orderIndex - 1],
            )
            PickerCaption(
                text = LunarCalendar.format(date) + "\n" +
                    stringResource(Res.string.demo_lunar_date_picker_solar_format, date.isoText()),
            )
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = listOf(stringResource(Res.string.common_default)) + DateFieldOrder.entries.map { it.name },
                    selectedIndex = orderIndex,
                    title = stringResource(Res.string.demo_lunar_date_picker_field_order),
                    onSelectedIndexChange = { orderIndex = it },
                )
            }
            item {
                ArrowPreference(
                    title = stringResource(Res.string.demo_lunar_date_picker_jump_leap_month),
                    summary = stringResource(Res.string.demo_lunar_date_picker_jump_leap_month_summary),
                    onClick = { setDate(LeapMonthStart) },
                )
            }
            item {
                val today = CalendarPickerDefaults.today()
                ArrowPreference(
                    title = stringResource(Res.string.demo_lunar_date_picker_jump_today),
                    summary = today.isoText(),
                    onClick = { setDate(today) },
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
    variant(key = "disabled", title = Res.string.common_disabled) {
        LunarDatePicker(
            value = DateValue(year = 2026, month = 2, day = 17),
            onValueChange = {},
            enabled = false,
            fieldOrder = DateFieldOrder.YearMonthDay,
        )
    }
}
