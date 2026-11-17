// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.preference

import androidx.compose.runtime.Composable
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
import io.github.suqi8.coui.kmp.preference.ListPreference
import io.github.suqi8.coui.kmp.preference.ListPreferenceEntry
import io.github.suqi8.coui.kmp.preference.MultiSelectListPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_cancel
import io.github.suqi8.coui.kmp.shared.generated.resources.common_confirm
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_contacts_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_dark
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_documents
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_entry_summaries
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_follow_system
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_follow_system_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_light
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_multi_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_show_value
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_single_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_sync_items
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_system_settings
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_theme
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_unavailable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_variant_states
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_component
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_contacts
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_option
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_photos
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_summary
import org.jetbrains.compose.resources.stringResource

private val ListTypes = listOf("ListPreference", "MultiSelectListPreference")
private val ListCounts = listOf(3, 5, 8)

internal val ListPreferenceDemo = Demo(
    id = "list_preference",
    apiName = "ListPreference",
    title = Res.string.demo_list_preference_title,
    category = DemoCategory.Preference,
    page = DemoPage.Variants {
        playground {
            val state = rememberPreferenceRowState()
            var type by rememberSaveable { mutableIntStateOf(0) }
            var countIndex by rememberSaveable { mutableIntStateOf(0) }
            var showValue by rememberSaveable { mutableStateOf(true) }
            var entrySummaries by rememberSaveable { mutableStateOf(false) }
            var selected by rememberSaveable { mutableIntStateOf(0) }
            var selectedSet by remember { mutableStateOf(setOf(0)) }
            val count = ListCounts[countIndex]
            val entrySummary = stringResource(Res.string.sample_summary)
            val entries = List(count) { index ->
                ListPreferenceEntry(
                    text = stringResource(Res.string.sample_option, index + 1),
                    summary = if (entrySummaries) entrySummary else null,
                )
            }
            val title = state.title()
            val summary = state.summary()
            val cancel = stringResource(Res.string.common_cancel)
            val startAction: (@Composable () -> Unit)? = if (state.showIcon) {
                { PreferenceStartIcon() }
            } else {
                null
            }
            PreferencePreview {
                item {
                    if (type == 0) {
                        ListPreference(
                            entries = entries,
                            selectedIndex = selected.coerceAtMost(count - 1),
                            onSelectedIndexChange = { selected = it },
                            title = title,
                            cancelButtonText = cancel,
                            summary = summary,
                            startAction = startAction,
                            enabled = state.enabled,
                            showValue = showValue,
                        )
                    } else {
                        MultiSelectListPreference(
                            entries = entries,
                            selectedIndices = selectedSet.filter { it < count }.toSet(),
                            onSelectedIndicesChange = { selectedSet = it },
                            title = title,
                            confirmButtonText = stringResource(Res.string.common_confirm),
                            cancelButtonText = cancel,
                            summary = summary,
                            startAction = startAction,
                            enabled = state.enabled,
                            showValue = showValue,
                        )
                    }
                }
            }
            PlaygroundControls {
                literalChoiceControl(Res.string.demo_pref_component, ListTypes, type) { type = it }
                literalChoiceControl(Res.string.common_count, ListCounts.map { it.toString() }, countIndex) { countIndex = it }
                switchControl(Res.string.demo_list_preference_entry_summaries, entrySummaries) { entrySummaries = it }
                switchControl(Res.string.demo_list_preference_show_value, showValue) { showValue = it }
                preferenceRowControls(state, endText = false)
            }
        }
        variant(key = "single", title = "ListPreference") {
            CardListGroup {
                item { ThemeListPreference() }
            }
        }
        variant(key = "multi", title = "MultiSelectListPreference") {
            CardListGroup {
                item { SyncItemsPreference() }
            }
        }
        variant(key = "states", title = Res.string.demo_list_preference_variant_states) {
            val entries = listOf(ListPreferenceEntry(stringResource(Res.string.demo_list_preference_unavailable)))
            CardListGroup {
                item {
                    ListPreference(
                        entries = entries,
                        selectedIndex = 0,
                        onSelectedIndexChange = {},
                        title = stringResource(Res.string.common_disabled),
                        cancelButtonText = stringResource(Res.string.common_cancel),
                        enabled = false,
                    )
                }
            }
        }
    },
    keywords = listOf("设置", "多选", "list", "列表", "单选"),
    isNew = true,
)

@Composable
private fun ThemeListPreference() {
    val entries = listOf(
        ListPreferenceEntry(stringResource(Res.string.demo_list_preference_light)),
        ListPreferenceEntry(stringResource(Res.string.demo_list_preference_dark)),
        ListPreferenceEntry(
            stringResource(Res.string.demo_list_preference_follow_system),
            summary = stringResource(Res.string.demo_list_preference_follow_system_summary),
        ),
    )
    var selectedIndex by rememberSaveable { mutableIntStateOf(2) }
    ListPreference(
        entries = entries,
        selectedIndex = selectedIndex,
        onSelectedIndexChange = { selectedIndex = it },
        title = stringResource(Res.string.demo_list_preference_theme),
        cancelButtonText = stringResource(Res.string.common_cancel),
        summary = stringResource(Res.string.demo_list_preference_single_summary),
    )
}

@Composable
private fun SyncItemsPreference() {
    val entries = listOf(
        ListPreferenceEntry(stringResource(Res.string.sample_photos)),
        ListPreferenceEntry(stringResource(Res.string.demo_list_preference_documents)),
        ListPreferenceEntry(
            stringResource(Res.string.sample_contacts),
            summary = stringResource(Res.string.demo_list_preference_contacts_summary),
        ),
        ListPreferenceEntry(stringResource(Res.string.demo_list_preference_system_settings), enabled = false),
    )
    var selectedIndices by remember { mutableStateOf(setOf(0, 1)) }
    MultiSelectListPreference(
        entries = entries,
        selectedIndices = selectedIndices,
        onSelectedIndicesChange = { selectedIndices = it },
        title = stringResource(Res.string.demo_list_preference_sync_items),
        confirmButtonText = stringResource(Res.string.common_confirm),
        cancelButtonText = stringResource(Res.string.common_cancel),
        summary = stringResource(Res.string.demo_list_preference_multi_summary),
    )
}
