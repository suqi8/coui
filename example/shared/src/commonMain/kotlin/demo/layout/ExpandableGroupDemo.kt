// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
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
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.ExpandableGroupArrow
import io.github.suqi8.coui.kmp.basic.ExpandableGroupContent
import io.github.suqi8.coui.kmp.basic.ExpandableGroupDefaults
import io.github.suqi8.coui.kmp.basic.ExpandableGroupHeader
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_color
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_default
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_expandable_group_accessibility
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_expandable_group_arrow_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_expandable_group_arrow_theme
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_expandable_group_backup
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_expandable_group_date_time
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_expandable_group_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_expandable_group_expanded
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_expandable_group_hotspot
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_expandable_group_language
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_expandable_group_more_settings
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_expandable_group_network
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_expandable_group_network_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_expandable_group_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_expandable_group_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_expandable_group_variant_arrow
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_expandable_group_variant_groups
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_item
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_title
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_mobile_network
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_title
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_wifi
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/** The demo page of `ExpandableGroup`, listed in its category and found by the search. */
internal val ExpandableGroupDemo = Demo(
    id = "expandable_group",
    apiName = "ExpandableGroup",
    title = Res.string.demo_expandable_group_title,
    category = DemoCategory.Layout,
    page = DemoPage.Variants {
        expandableGroupPlayground()
        variant(key = "groups", title = Res.string.demo_expandable_group_variant_groups) {
            ExpandableGroupSample(
                title = stringResource(Res.string.demo_expandable_group_network),
                summary = stringResource(Res.string.demo_expandable_group_network_summary),
                children = listOf(
                    stringResource(Res.string.sample_wifi),
                    stringResource(Res.string.sample_mobile_network),
                    stringResource(Res.string.demo_expandable_group_hotspot),
                ),
                initiallyExpanded = true,
            )
            ExpandableGroupSample(
                title = stringResource(Res.string.demo_expandable_group_more_settings),
                summary = null,
                children = listOf(
                    stringResource(Res.string.demo_expandable_group_accessibility),
                    stringResource(Res.string.demo_expandable_group_date_time),
                    stringResource(Res.string.demo_expandable_group_language),
                    stringResource(Res.string.demo_expandable_group_backup),
                ),
                initiallyExpanded = false,
            )
            ExpandableGroupHeader(
                title = stringResource(Res.string.demo_expandable_group_disabled),
                expanded = false,
                onExpandedChange = {},
                enabled = false,
            )
        }
        variant(key = "arrow", title = Res.string.demo_expandable_group_variant_arrow) {
            var expanded by rememberSaveable { mutableStateOf(false) }
            BasicComponent(
                title = "ExpandableGroupArrow",
                summary = stringResource(Res.string.demo_expandable_group_arrow_hint),
                endActions = { ExpandableGroupArrow(expanded = expanded) },
                onClick = { expanded = !expanded },
            )
        }
    },
    searchTerms = Res.string.demo_expandable_group_search_terms,
)

private fun LazyListScope.expandableGroupPlayground() {
    playground {
        var expanded by rememberSaveable { mutableStateOf(true) }
        var showSummary by rememberSaveable { mutableStateOf(true) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        var longTitle by rememberSaveable { mutableStateOf(false) }
        var childCount by rememberSaveable { mutableIntStateOf(3) }
        var arrowColorIndex by rememberSaveable { mutableIntStateOf(0) }

        val arrowColor = if (arrowColorIndex == 0) {
            ExpandableGroupDefaults.arrowColor()
        } else {
            COUITheme.colorScheme.primary
        }
        PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
            ExpandableGroupHeader(
                title = stringResource(if (longTitle) Res.string.sample_long_title else Res.string.sample_title),
                expanded = expanded,
                onExpandedChange = { expanded = it },
                summary = if (showSummary) stringResource(Res.string.sample_summary) else null,
                enabled = enabled,
                arrowColor = arrowColor,
            )
            ExpandableGroupContent(expanded = expanded) {
                repeat(childCount) { index ->
                    BasicComponent(
                        title = stringResource(Res.string.sample_item, index + 1),
                        onClick = {},
                    )
                }
            }
        }

        val expandedTitle = stringResource(Res.string.demo_expandable_group_expanded)
        val summaryTitle = stringResource(Res.string.common_show_summary)
        val enabledTitle = stringResource(Res.string.common_enabled)
        val longTitleTitle = stringResource(Res.string.common_long_text)
        val countTitle = stringResource(Res.string.common_count)
        val colorTitle = stringResource(Res.string.common_color)
        val colorLabels = listOf(
            stringResource(Res.string.common_default),
            stringResource(Res.string.demo_expandable_group_arrow_theme),
        )
        PlaygroundControls {
            item {
                SwitchPreference(
                    checked = expanded,
                    onCheckedChange = { expanded = it },
                    title = expandedTitle,
                )
            }
            item {
                SliderPreference(
                    value = childCount.toFloat(),
                    onValueChange = { childCount = it.roundToInt() },
                    title = countTitle,
                    valueText = "$childCount",
                    valueRange = 1f..6f,
                    steps = 4,
                )
            }
            item {
                SwitchPreference(
                    checked = showSummary,
                    onCheckedChange = { showSummary = it },
                    title = summaryTitle,
                )
            }
            item {
                SwitchPreference(
                    checked = longTitle,
                    onCheckedChange = { longTitle = it },
                    title = longTitleTitle,
                )
            }
            item {
                SwitchPreference(
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                    title = enabledTitle,
                )
            }
            item {
                OverlayDropdownPreference(
                    items = colorLabels,
                    selectedIndex = arrowColorIndex,
                    title = colorTitle,
                    onSelectedIndexChange = { arrowColorIndex = it },
                )
            }
        }
    }
}

@Composable
private fun ExpandableGroupSample(
    title: String,
    summary: String?,
    children: List<String>,
    initiallyExpanded: Boolean,
) {
    var expanded by rememberSaveable { mutableStateOf(initiallyExpanded) }
    Column {
        ExpandableGroupHeader(
            title = title,
            expanded = expanded,
            onExpandedChange = { expanded = it },
            summary = summary,
        )
        ExpandableGroupContent(expanded = expanded) {
            children.forEach { child ->
                BasicComponent(
                    title = child,
                    onClick = {},
                )
            }
        }
    }
}
