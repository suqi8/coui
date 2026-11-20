// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.selection

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.Chip
import io.github.suqi8.coui.kmp.basic.ChipGroup
import io.github.suqi8.coui.kmp.basic.ChipGroupState
import io.github.suqi8.coui.kmp.basic.rememberChipGroupState
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_selected_value
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_about_phone
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_accessibility
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_all
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_apps
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_collapsable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_collapsable_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_collapsed_rows
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_documents
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_family_space
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_location
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_lock_screen
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_none
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_security
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_selection_required
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_selection_required_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_single_line
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_single_line_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_single_selection
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_variant_collapsable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_variant_single_line
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_group_variant_single_select
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_page_indicator_animated
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_battery
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_bluetooth
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_display
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_mobile_network
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_notifications
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_photos
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_privacy
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_sound
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_storage
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_videos
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_wallpaper
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_wifi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/** The demo page of `ChipGroup`, listed in its category and found by the search. */
internal val ChipGroupDemo = Demo(
    id = "chip_group",
    apiName = "ChipGroup",
    title = Res.string.demo_chip_group_title,
    category = DemoCategory.Selection,
    page = DemoPage.Variants { chipGroupPage() },
    searchTerms = Res.string.demo_chip_group_search_terms,
    isNew = true,
)

/** The settings entries used as chip labels. */
private val SettingsLabels: List<StringResource> = listOf(
    Res.string.sample_wifi,
    Res.string.sample_bluetooth,
    Res.string.sample_mobile_network,
    Res.string.sample_battery,
    Res.string.sample_display,
    Res.string.sample_sound,
    Res.string.sample_notifications,
    Res.string.sample_privacy,
    Res.string.demo_chip_group_security,
    Res.string.demo_chip_group_location,
    Res.string.demo_chip_group_apps,
    Res.string.sample_storage,
    Res.string.demo_chip_group_accessibility,
    Res.string.sample_wallpaper,
    Res.string.demo_chip_group_lock_screen,
    Res.string.demo_chip_group_family_space,
    Res.string.demo_chip_group_about_phone,
)

/** The media filters of the single-select variant. */
private val FilterLabels: List<StringResource> = listOf(
    Res.string.demo_chip_group_all,
    Res.string.sample_photos,
    Res.string.sample_videos,
    Res.string.demo_chip_group_documents,
)

private fun LazyListScope.chipGroupPage() {
    playground {
        var count by rememberSaveable { mutableIntStateOf(12) }
        var singleSelection by rememberSaveable { mutableStateOf(false) }
        var selectionRequired by rememberSaveable { mutableStateOf(false) }
        var singleLine by rememberSaveable { mutableStateOf(false) }
        var collapsable by rememberSaveable { mutableStateOf(true) }
        var collapsedRows by rememberSaveable { mutableIntStateOf(2) }
        var animationsEnabled by rememberSaveable { mutableStateOf(true) }
        // A new state whenever the selection rules change, as rememberChipGroupState keys on them.
        val state = rememberChipGroupState(
            singleSelection = singleSelection,
            selectionRequired = selectionRequired,
            initialSelection = setOf(0),
            initialCollapsed = true,
        )

        PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
            LabeledChipGroup(
                labels = SettingsLabels.take(count),
                state = state,
                modifier = Modifier
                    .then(if (singleLine) Modifier.horizontalScroll(rememberScrollState()) else Modifier)
                    .padding(16.dp),
                singleLine = singleLine,
                collapsable = collapsable,
                collapsedMaxRows = collapsedRows,
                animationsEnabled = animationsEnabled,
            )
            val selected = state.selectedIndices.filter { it < count }.sorted()
            ValueCaption(
                text = stringResource(
                    Res.string.common_selected_value,
                    if (selected.isEmpty()) {
                        stringResource(Res.string.demo_chip_group_none)
                    } else {
                        selected.map { stringResource(SettingsLabels[it]) }.joinToString()
                    },
                ),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            )
        }
        PlaygroundControls {
            item {
                SliderPreference(
                    value = count.toFloat(),
                    onValueChange = { count = it.roundToInt() },
                    title = stringResource(Res.string.common_count),
                    valueText = count.toString(),
                    valueRange = 2f..SettingsLabels.size.toFloat(),
                    steps = SettingsLabels.size - 3,
                )
            }
            item {
                SwitchPreference(
                    checked = singleSelection,
                    onCheckedChange = { singleSelection = it },
                    title = stringResource(Res.string.demo_chip_group_single_selection),
                )
            }
            item {
                SwitchPreference(
                    checked = selectionRequired,
                    onCheckedChange = { selectionRequired = it },
                    title = stringResource(Res.string.demo_chip_group_selection_required),
                    summary = stringResource(Res.string.demo_chip_group_selection_required_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = singleLine,
                    onCheckedChange = { singleLine = it },
                    title = stringResource(Res.string.demo_chip_group_single_line),
                    summary = stringResource(Res.string.demo_chip_group_single_line_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = collapsable,
                    onCheckedChange = { collapsable = it },
                    title = stringResource(Res.string.demo_chip_group_collapsable),
                    summary = stringResource(Res.string.demo_chip_group_collapsable_summary),
                    enabled = !singleLine,
                )
            }
            item {
                SliderPreference(
                    value = collapsedRows.toFloat(),
                    onValueChange = { collapsedRows = it.roundToInt() },
                    title = stringResource(Res.string.demo_chip_group_collapsed_rows),
                    valueText = collapsedRows.toString(),
                    valueRange = 1f..4f,
                    steps = 2,
                    enabled = collapsable && !singleLine,
                )
            }
            item {
                SwitchPreference(
                    checked = animationsEnabled,
                    onCheckedChange = { animationsEnabled = it },
                    title = stringResource(Res.string.demo_page_indicator_animated),
                )
            }
        }
    }
    variant(key = "singleSelect", title = Res.string.demo_chip_group_variant_single_select) {
        val state = rememberChipGroupState(singleSelection = true, selectionRequired = true, initialSelection = setOf(0))
        LabeledChipGroup(labels = FilterLabels, state = state, modifier = Modifier.padding(16.dp))
    }
    variant(key = "collapsable", title = Res.string.demo_chip_group_variant_collapsable) {
        val state = rememberChipGroupState(initialCollapsed = true)
        LabeledChipGroup(
            labels = SettingsLabels,
            state = state,
            modifier = Modifier.padding(16.dp),
            collapsable = true,
            collapsedMaxRows = 2,
        )
    }
    variant(key = "singleLine", title = Res.string.demo_chip_group_variant_single_line) {
        val state = rememberChipGroupState()
        LabeledChipGroup(
            labels = SettingsLabels,
            state = state,
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(16.dp),
            singleLine = true,
        )
    }
}

/**
 * A [ChipGroup] of one [Chip] per label, selected through [state].
 */
@Composable
private fun LabeledChipGroup(
    labels: List<StringResource>,
    state: ChipGroupState,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    collapsable: Boolean = false,
    collapsedMaxRows: Int = 3,
    animationsEnabled: Boolean = true,
) {
    ChipGroup(
        modifier = modifier,
        state = state,
        singleLine = singleLine,
        collapsable = collapsable,
        collapsedMaxRows = collapsedMaxRows,
        animationsEnabled = animationsEnabled,
    ) {
        labels.forEachIndexed { index, label ->
            Chip(
                selected = state.isSelected(index),
                onClick = { state.toggle(index) },
                label = stringResource(label),
            )
        }
    }
}
