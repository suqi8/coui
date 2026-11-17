// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.WindowDropdownPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_mode
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_preference_collapse_on_selection
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_preference_collapsed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_preference_expanded
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_preference_group_option
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_preference_grouped
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_preference_long_option
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_preference_overlay
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_preference_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_preference_show_value
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_preference_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_preference_variant_grouped
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_preference_variant_options
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_preference_variant_states
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_preference_window
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_option
import org.jetbrains.compose.resources.stringResource

private val DropdownCounts = listOf(2, 4, 8, 12)

/** The demo page of `OverlayDropdownPreference`, listed in its category and found by the search. */
internal val DropdownPreferenceDemo = Demo(
    id = "dropdown_preference",
    apiName = "OverlayDropdownPreference",
    title = Res.string.demo_dropdown_preference_title,
    category = DemoCategory.Preference,
    page = DemoPage.Variants {
        playground {
            val state = rememberPreferenceRowState()
            var window by rememberSaveable { mutableIntStateOf(0) }
            var countIndex by rememberSaveable { mutableIntStateOf(1) }
            var longOptions by rememberSaveable { mutableStateOf(false) }
            var grouped by rememberSaveable { mutableStateOf(false) }
            var collapseOnSelection by rememberSaveable { mutableStateOf(false) }
            var showValue by rememberSaveable { mutableStateOf(true) }
            var selected by rememberSaveable { mutableIntStateOf(0) }
            var expanded by remember { mutableStateOf(false) }
            val groupSelection = rememberGroupSelection()
            val count = DropdownCounts[countIndex]
            val items = dropdownOptions(count, longOptions)
            val entries = groupedEntries(groupSelection)
            val title = state.title()
            val summary = if (state.showSummary) expandedSummary(expanded) else null
            val startAction: (@Composable () -> Unit)? = if (state.showIcon) {
                { PreferenceStartIcon() }
            } else {
                null
            }
            PreferencePreview {
                item {
                    when {
                        window == 0 && !grouped -> OverlayDropdownPreference(
                            items = items,
                            selectedIndex = selected.coerceAtMost(count - 1),
                            title = title,
                            summary = summary,
                            startAction = startAction,
                            enabled = state.enabled,
                            showValue = showValue,
                            onExpandedChange = { expanded = it },
                            onSelectedIndexChange = { selected = it },
                        )

                        window == 0 -> OverlayDropdownPreference(
                            entries = entries,
                            title = title,
                            summary = summary,
                            startAction = startAction,
                            enabled = state.enabled,
                            showValue = showValue,
                            collapseOnSelection = collapseOnSelection,
                            onExpandedChange = { expanded = it },
                        )

                        !grouped -> WindowDropdownPreference(
                            items = items,
                            selectedIndex = selected.coerceAtMost(count - 1),
                            title = title,
                            summary = summary,
                            startAction = startAction,
                            enabled = state.enabled,
                            showValue = showValue,
                            onExpandedChange = { expanded = it },
                            onSelectedIndexChange = { selected = it },
                        )

                        else -> WindowDropdownPreference(
                            entries = entries,
                            title = title,
                            summary = summary,
                            startAction = startAction,
                            enabled = state.enabled,
                            showValue = showValue,
                            collapseOnSelection = collapseOnSelection,
                            onExpandedChange = { expanded = it },
                        )
                    }
                }
            }
            PlaygroundControls {
                choiceControl(
                    Res.string.common_mode,
                    listOf(Res.string.demo_dropdown_preference_overlay, Res.string.demo_dropdown_preference_window),
                    window,
                ) { window = it }
                switchControl(Res.string.demo_dropdown_preference_grouped, grouped) { grouped = it }
                if (grouped) {
                    switchControl(Res.string.demo_dropdown_preference_collapse_on_selection, collapseOnSelection) {
                        collapseOnSelection = it
                    }
                } else {
                    literalChoiceControl(Res.string.common_count, DropdownCounts.map { it.toString() }, countIndex) {
                        countIndex = it
                    }
                    switchControl(Res.string.common_long_text, longOptions) { longOptions = it }
                }
                switchControl(Res.string.demo_dropdown_preference_show_value, showValue) { showValue = it }
                preferenceRowControls(state, endText = false)
            }
        }
        variant(key = "options", title = Res.string.demo_dropdown_preference_variant_options) {
            OptionRows()
        }
        variant(key = "grouped", title = Res.string.demo_dropdown_preference_variant_grouped) {
            GroupedRows()
        }
        variant(key = "states", title = Res.string.demo_dropdown_preference_variant_states) {
            val single = listOf(stringResource(Res.string.sample_option, 1))
            CardListGroup {
                item {
                    OverlayDropdownPreference(
                        items = single,
                        selectedIndex = 0,
                        title = "${stringResource(Res.string.common_disabled)} (Overlay)",
                        enabled = false,
                        onSelectedIndexChange = {},
                    )
                }
                item {
                    WindowDropdownPreference(
                        items = single,
                        selectedIndex = 0,
                        title = "${stringResource(Res.string.common_disabled)} (Window)",
                        enabled = false,
                        onSelectedIndexChange = {},
                    )
                }
            }
        }
    },
    searchTerms = Res.string.demo_dropdown_preference_search_terms,
)

/**
 * The flat options of a dropdown; long options grow one step longer each row.
 */
@Composable
private fun dropdownOptions(count: Int, long: Boolean): List<String> {
    val longPrefix = stringResource(Res.string.demo_dropdown_preference_long_option)
    return List(count) { index ->
        val option = stringResource(Res.string.sample_option, index + 1)
        if (long) (List(index + 1) { longPrefix } + option).joinToString(" ") else option
    }
}

/**
 * The selected index of each of the three option groups.
 */
@Composable
private fun rememberGroupSelection(): GroupSelection = remember { GroupSelection() }

/**
 * Three mutable selected indices, one per option group.
 */
@Stable
private class GroupSelection {
    var first by mutableIntStateOf(0)
    var second by mutableIntStateOf(0)
    var third by mutableIntStateOf(0)
}

/**
 * Three option groups; every other option of the last group is disabled.
 */
@Composable
private fun groupedEntries(selection: GroupSelection): List<DropdownEntry> {
    val groups = listOf("A" to 2, "B" to 3, "C" to 4)
    val labels = groups.map { (group, size) ->
        List(size) { index -> stringResource(Res.string.demo_dropdown_preference_group_option, "$group-${index + 1}") }
    }
    return listOf(
        DropdownEntry(
            items = labels[0].mapIndexed { index, text ->
                DropdownItem(text = text, selected = selection.first == index, onClick = { selection.first = index })
            },
        ),
        DropdownEntry(
            items = labels[1].mapIndexed { index, text ->
                DropdownItem(text = text, selected = selection.second == index, onClick = { selection.second = index })
            },
        ),
        DropdownEntry(
            items = labels[2].mapIndexed { index, text ->
                DropdownItem(
                    text = text,
                    enabled = index % 2 == 0,
                    selected = selection.third == index,
                    onClick = { selection.third = index },
                )
            },
        ),
    )
}

/**
 * A short dropdown in an overlay popup and a long one in a window popup.
 */
@Composable
private fun OptionRows() {
    var overlaySelected by rememberSaveable { mutableIntStateOf(0) }
    var windowSelected by rememberSaveable { mutableIntStateOf(0) }
    var overlayExpanded by remember { mutableStateOf(false) }
    var windowExpanded by remember { mutableStateOf(false) }
    val shortOptions = dropdownOptions(4, long = false)
    val longOptions = dropdownOptions(12, long = true)
    CardListGroup {
        item {
            OverlayDropdownPreference(
                items = shortOptions,
                selectedIndex = overlaySelected,
                title = "OverlayDropdownPreference",
                summary = expandedSummary(overlayExpanded),
                onExpandedChange = { overlayExpanded = it },
                onSelectedIndexChange = { overlaySelected = it },
            )
        }
        item {
            WindowDropdownPreference(
                items = longOptions,
                selectedIndex = windowSelected,
                title = "WindowDropdownPreference",
                summary = expandedSummary(windowExpanded),
                onExpandedChange = { windowExpanded = it },
                onSelectedIndexChange = { windowSelected = it },
            )
        }
    }
}

/**
 * Grouped dropdowns that stay open while options are picked.
 */
@Composable
private fun GroupedRows() {
    val overlaySelection = rememberGroupSelection()
    val windowSelection = rememberGroupSelection()
    var overlayExpanded by remember { mutableStateOf(false) }
    var windowExpanded by remember { mutableStateOf(false) }
    CardListGroup {
        item {
            OverlayDropdownPreference(
                entries = groupedEntries(overlaySelection),
                title = "OverlayDropdownPreference",
                summary = expandedSummary(overlayExpanded),
                collapseOnSelection = false,
                onExpandedChange = { overlayExpanded = it },
            )
        }
        item {
            WindowDropdownPreference(
                entries = groupedEntries(windowSelection),
                title = "WindowDropdownPreference",
                summary = expandedSummary(windowExpanded),
                collapseOnSelection = false,
                onExpandedChange = { windowExpanded = it },
            )
        }
    }
}

@Composable
private fun expandedSummary(expanded: Boolean): String = stringResource(if (expanded) Res.string.demo_dropdown_preference_expanded else Res.string.demo_dropdown_preference_collapsed)
