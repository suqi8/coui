// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.pickers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.NumberPicker
import io.github.suqi8.coui.kmp.basic.NumberPickerDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_value_format
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_number_picker_ignorable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_number_picker_ignorable_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_number_picker_ignored
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_number_picker_leading_zero
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_number_picker_range
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_number_picker_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_number_picker_show_unit
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_number_picker_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_number_picker_unit_minute
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_number_picker_variant_time
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_number_picker_variant_unit
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_number_picker_visible_items
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_number_picker_wrap_around
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_number_picker_wrap_around_summary
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource

/** The demo page of `NumberPicker`, listed in its category and found by the search. */
internal val NumberPickerDemo = Demo(
    id = "number_picker",
    apiName = "NumberPicker",
    title = Res.string.demo_number_picker_title,
    category = DemoCategory.Pickers,
    page = DemoPage.Variants { numberPickerPage() },
    searchTerms = Res.string.demo_number_picker_search_terms,
)

/** The ranges offered by the playground; the short one shows that wrapping needs enough rows. */
private val PickerRanges = listOf(0..59, 1..12, 1..5)

/** The odd visible row counts offered by the playground. */
private val VisibleItemCounts = listOf(3, 5, 7)

private fun LazyListScope.numberPickerPage() {
    playground {
        var value by rememberSaveable { mutableIntStateOf(30) }
        var rangeIndex by rememberSaveable { mutableIntStateOf(0) }
        var visibleIndex by rememberSaveable { mutableIntStateOf(0) }
        var wrapAround by rememberSaveable { mutableStateOf(true) }
        var leadingZero by rememberSaveable { mutableStateOf(true) }
        var showUnit by rememberSaveable { mutableStateOf(false) }
        var ignorable by rememberSaveable { mutableStateOf(false) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        val range = PickerRanges[rangeIndex]
        val ignored = value == NumberPickerDefaults.IgnoredValue

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                NumberPicker(
                    value = value,
                    onValueChange = { value = it },
                    enabled = enabled,
                    range = range,
                    label = { if (leadingZero) it.twoDigits() else it.toString() },
                    unit = if (showUnit) stringResource(Res.string.demo_number_picker_unit_minute) else "",
                    visibleItemCount = VisibleItemCounts[visibleIndex],
                    wrapAround = wrapAround,
                    ignorable = ignorable,
                )
            }
            PickerCaption(
                text = stringResource(
                    Res.string.common_value_format,
                    if (ignored) stringResource(Res.string.demo_number_picker_ignored) else value.toString(),
                ),
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = PickerRanges.map { "${it.first} - ${it.last}" },
                    selectedIndex = rangeIndex,
                    title = stringResource(Res.string.demo_number_picker_range),
                    onSelectedIndexChange = {
                        rangeIndex = it
                        if (!ignored) value = value.coerceIn(PickerRanges[it])
                    },
                )
            }
            item {
                OverlayDropdownPreference(
                    items = VisibleItemCounts.map { it.toString() },
                    selectedIndex = visibleIndex,
                    title = stringResource(Res.string.demo_number_picker_visible_items),
                    onSelectedIndexChange = { visibleIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = wrapAround,
                    onCheckedChange = { wrapAround = it },
                    title = stringResource(Res.string.demo_number_picker_wrap_around),
                    summary = stringResource(Res.string.demo_number_picker_wrap_around_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = leadingZero,
                    onCheckedChange = { leadingZero = it },
                    title = stringResource(Res.string.demo_number_picker_leading_zero),
                )
            }
            item {
                SwitchPreference(
                    checked = showUnit,
                    onCheckedChange = { showUnit = it },
                    title = stringResource(Res.string.demo_number_picker_show_unit),
                )
            }
            item {
                SwitchPreference(
                    checked = ignorable,
                    onCheckedChange = {
                        ignorable = it
                        if (!it && ignored) value = range.first
                    },
                    title = stringResource(Res.string.demo_number_picker_ignorable),
                    summary = stringResource(Res.string.demo_number_picker_ignorable_summary),
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
    variant(key = "time", title = Res.string.demo_number_picker_variant_time) {
        TimeNumberPickers()
    }
    variant(key = "unit", title = Res.string.demo_number_picker_variant_unit) {
        var weight by remember { mutableIntStateOf(60) }
        NumberPicker(
            value = weight,
            onValueChange = { weight = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            range = 30..150,
            unit = "kg",
        )
    }
}

@Composable
private fun TimeNumberPickers() {
    var hour by remember { mutableIntStateOf(16) }
    var minute by remember { mutableIntStateOf(30) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NumberPicker(
            value = hour,
            onValueChange = { hour = it },
            modifier = Modifier.weight(1f),
            range = 0..23,
            label = { it.twoDigits() },
            wrapAround = true,
        )
        Text(
            text = ":",
            style = COUITheme.textStyles.headlineM,
            color = COUITheme.colorScheme.labelPrimary,
        )
        NumberPicker(
            value = minute,
            onValueChange = { minute = it },
            modifier = Modifier.weight(1f),
            range = 0..59,
            label = { it.twoDigits() },
            wrapAround = true,
        )
    }
}
