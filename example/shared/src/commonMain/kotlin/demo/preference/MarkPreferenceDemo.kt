// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.LocalDemoSnackbarHostState
import demo.framework.PlaygroundControls
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.preference.MarkLocation
import io.github.suqi8.coui.kmp.preference.MarkPreference
import io.github.suqi8.coui.kmp.preference.MarkWithDividerPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_end
import io.github.suqi8.coui.kmp.shared.generated.resources.common_position
import io.github.suqi8.coui.kmp.shared.generated.resources.common_start
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_mark_preference_every_day
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_mark_preference_never
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_mark_preference_only_option
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_mark_preference_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_mark_preference_tap_for_details
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_mark_preference_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_mark_preference_variant_group
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_mark_preference_variant_standalone
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_mark_preference_workdays
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_both_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_component
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_opened_page
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_title
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_option
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

private val MarkTypes = listOf("MarkPreference", "MarkWithDividerPreference")
private val MarkCounts = listOf("2", "3", "4", "5")
private val WifiNetworks = listOf("Home-5G", "Office-2.4G", "Guest-WiFi")

/** The demo page of `MarkPreference`, listed in its category and found by the search. */
internal val MarkPreferenceDemo = Demo(
    id = "mark_preference",
    apiName = "MarkPreference",
    title = Res.string.demo_mark_preference_title,
    category = DemoCategory.Preference,
    page = DemoPage.Variants {
        playground {
            val state = rememberPreferenceRowState(showSummary = false)
            var type by rememberSaveable { mutableIntStateOf(0) }
            var location by rememberSaveable { mutableIntStateOf(1) }
            var countIndex by rememberSaveable { mutableIntStateOf(1) }
            var selected by rememberSaveable { mutableIntStateOf(0) }
            val snackbar = LocalDemoSnackbarHostState.current
            val scope = rememberCoroutineScope()
            val count = countIndex + 2
            val summary = state.summary()
            val longTitle = stringResource(Res.string.sample_long_title)
            val titles = List(count) { index ->
                if (state.longTitle && index == 0) longTitle else stringResource(Res.string.sample_option, index + 1)
            }
            val startAction: (@Composable () -> Unit)? = if (state.showIcon) {
                { PreferenceStartIcon() }
            } else {
                null
            }
            PreferencePreview {
                titles.forEachIndexed { index, title ->
                    item(key = index) {
                        if (type == 0) {
                            MarkPreference(
                                title = title,
                                checked = selected == index,
                                onClick = { selected = index },
                                summary = summary,
                                startAction = startAction,
                                endActions = if (state.showEndText) {
                                    { PreferenceEndText(enabled = state.enabled) }
                                } else {
                                    null
                                },
                                markLocation = if (location == 0) MarkLocation.Start else MarkLocation.End,
                                enabled = state.enabled,
                            )
                        } else {
                            MarkWithDividerPreference(
                                checked = selected == index,
                                onCheckedChange = { if (it) selected = index },
                                title = title,
                                onClick = {
                                    scope.launch { snackbar.showSnackbar(getString(Res.string.demo_pref_opened_page, title)) }
                                },
                                summary = summary,
                                startAction = startAction,
                                endActions = if (state.showEndText) {
                                    { PreferenceEndText(enabled = state.enabled) }
                                } else {
                                    null
                                },
                                enabled = state.enabled,
                            )
                        }
                    }
                }
            }
            PlaygroundControls {
                literalChoiceControl(Res.string.demo_pref_component, MarkTypes, type) { type = it }
                if (type == 0) {
                    choiceControl(
                        Res.string.common_position,
                        listOf(Res.string.common_start, Res.string.common_end),
                        location,
                    ) { location = it }
                }
                literalChoiceControl(Res.string.common_count, MarkCounts, countIndex) {
                    countIndex = it
                    if (selected >= it + 2) selected = 0
                }
                preferenceRowControls(state)
            }
        }
        variant(key = "group", title = Res.string.demo_mark_preference_variant_group) {
            RepeatGroup()
        }
        variant(key = "standalone", title = Res.string.demo_mark_preference_variant_standalone) {
            // A standalone row is CardListPosition.Full: both rounded edges get the padding, so this
            // card is 4dp taller than a middle row of a group. Outside a CardListGroup a row
            // defaults to CardListPosition.None, so the position is passed explicitly here.
            MarkPreference(
                title = stringResource(Res.string.demo_mark_preference_only_option),
                checked = true,
                onClick = {},
                cardListPosition = CardListPosition.Full,
            )
        }
        variant(key = "divider", title = "MarkWithDividerPreference") {
            NetworkRows()
        }
    },
    searchTerms = Res.string.demo_mark_preference_search_terms,
    isNew = true,
)

/**
 * A single-select group where the mark follows the checked row.
 */
@Composable
private fun RepeatGroup() {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val options = listOf(
        stringResource(Res.string.demo_mark_preference_every_day),
        stringResource(Res.string.demo_mark_preference_workdays),
        stringResource(Res.string.demo_mark_preference_never),
    )
    // The group gives the first row Head (2dp on top), the last Tail (2dp on the bottom) and the
    // rows between Middle (none).
    CardListGroup {
        options.forEachIndexed { index, option ->
            item(key = index) {
                MarkPreference(
                    title = option,
                    checked = selected == index,
                    onClick = { selected = index },
                )
            }
        }
    }
}

/**
 * Networks whose mark selects one of them and whose main area opens its details.
 */
@Composable
private fun NetworkRows() {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val snackbar = LocalDemoSnackbarHostState.current
    val scope = rememberCoroutineScope()
    val tapForDetails = stringResource(Res.string.demo_mark_preference_tap_for_details)
    CardListGroup {
        WifiNetworks.forEachIndexed { index, name ->
            item(key = name) {
                MarkWithDividerPreference(
                    checked = selected == index,
                    onCheckedChange = { if (it) selected = index },
                    title = name,
                    summary = tapForDetails,
                    onClick = {
                        scope.launch { snackbar.showSnackbar(getString(Res.string.demo_pref_opened_page, name)) }
                    },
                )
            }
        }
        item(key = "disabled") {
            MarkWithDividerPreference(
                checked = true,
                onCheckedChange = {},
                title = stringResource(Res.string.common_disabled),
                summary = stringResource(Res.string.demo_pref_both_disabled),
                onClick = {},
                enabled = false,
            )
        }
    }
}
