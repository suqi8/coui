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
import io.github.suqi8.coui.kmp.basic.CalendarPicker
import io.github.suqi8.coui.kmp.basic.CalendarPickerDefaults
import io.github.suqi8.coui.kmp.basic.DateValue
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_value_format
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_calendar_picker_first_day
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_calendar_picker_limited_range
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_calendar_picker_limited_range_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_calendar_picker_monday
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_calendar_picker_saturday
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_calendar_picker_show_today
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_calendar_picker_show_today_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_calendar_picker_sunday
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_calendar_picker_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_calendar_picker_variant_limited
import org.jetbrains.compose.resources.stringResource

internal val CalendarPickerDemo = Demo(
    id = "calendar_picker",
    apiName = "CalendarPicker",
    title = Res.string.demo_calendar_picker_title,
    category = DemoCategory.Pickers,
    page = DemoPage.Variants { calendarPickerPage() },
    keywords = listOf("日历", "calendar", "date"),
)

/** The first days of the week offered by the playground (1 = Sunday .. 7 = Saturday). */
private val FirstDaysOfWeek = listOf(1, 2, 7)

/** The limits of the limited-range examples. */
private val RangeMinDate = DateValue(year = 2026, month = 9, day = 10)
private val RangeMaxDate = DateValue(year = 2026, month = 12, day = 20)

private fun LazyListScope.calendarPickerPage() {
    playground {
        var year by rememberSaveable { mutableIntStateOf(2026) }
        var month by rememberSaveable { mutableIntStateOf(10) }
        var day by rememberSaveable { mutableIntStateOf(30) }
        var firstDayIndex by rememberSaveable { mutableIntStateOf(0) }
        var showToday by rememberSaveable { mutableStateOf(true) }
        var limitedRange by rememberSaveable { mutableStateOf(false) }
        val date = DateValue(year = year, month = month, day = day)

        PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
            CalendarPicker(
                value = date,
                onValueChange = {
                    year = it.year
                    month = it.month
                    day = it.day
                },
                minDate = if (limitedRange) RangeMinDate else CalendarPickerDefaults.MinDate,
                maxDate = if (limitedRange) RangeMaxDate else CalendarPickerDefaults.MaxDate,
                today = if (showToday) CalendarPickerDefaults.today() else null,
                firstDayOfWeek = FirstDaysOfWeek[firstDayIndex],
            )
            PickerCaption(text = stringResource(Res.string.common_value_format, date.isoText()))
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = listOf(
                        stringResource(Res.string.demo_calendar_picker_sunday),
                        stringResource(Res.string.demo_calendar_picker_monday),
                        stringResource(Res.string.demo_calendar_picker_saturday),
                    ),
                    selectedIndex = firstDayIndex,
                    title = stringResource(Res.string.demo_calendar_picker_first_day),
                    onSelectedIndexChange = { firstDayIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = showToday,
                    onCheckedChange = { showToday = it },
                    title = stringResource(Res.string.demo_calendar_picker_show_today),
                    summary = stringResource(Res.string.demo_calendar_picker_show_today_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = limitedRange,
                    onCheckedChange = { limitedRange = it },
                    title = stringResource(Res.string.demo_calendar_picker_limited_range),
                    summary = stringResource(
                        Res.string.demo_calendar_picker_limited_range_summary,
                        RangeMinDate.isoText(),
                        RangeMaxDate.isoText(),
                    ),
                )
            }
        }
    }
    variant(key = "limited", title = Res.string.demo_calendar_picker_variant_limited) {
        var date by remember { mutableStateOf(DateValue(year = 2026, month = 10, day = 12)) }
        CalendarPicker(
            value = date,
            onValueChange = { date = it },
            minDate = RangeMinDate,
            maxDate = RangeMaxDate,
            firstDayOfWeek = 2,
        )
        PickerCaption(
            text = stringResource(
                Res.string.demo_calendar_picker_limited_range_summary,
                RangeMinDate.isoText(),
                RangeMaxDate.isoText(),
            ),
        )
    }
}
