// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.preference

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import io.github.suqi8.coui.kmp.preference.SwitchLoadingPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.preference.SwitchWithDividerPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_loading
import io.github.suqi8.coui.kmp.shared.generated.resources.common_off
import io.github.suqi8.coui.kmp.shared.generated.resources.common_on
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_both_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_component
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_opened_page
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_switch_preference_always_loading
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_switch_preference_always_loading_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_switch_preference_async
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_switch_preference_async_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_switch_preference_expand
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_switch_preference_expand_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_switch_preference_expanded_row
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_switch_preference_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_switch_preference_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_switch_preference_wifi_connected
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_bluetooth
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_wifi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

private val SwitchTypes = listOf("SwitchPreference", "SwitchLoadingPreference", "SwitchWithDividerPreference")

/** The demo page of `SwitchPreference`, listed in its category and found by the search. */
internal val SwitchPreferenceDemo = Demo(
    id = "switch_preference",
    apiName = "SwitchPreference",
    title = Res.string.demo_switch_preference_title,
    category = DemoCategory.Preference,
    page = DemoPage.Variants {
        playground {
            val state = rememberPreferenceRowState()
            var type by rememberSaveable { mutableIntStateOf(0) }
            var checked by rememberSaveable { mutableStateOf(true) }
            var loading by rememberSaveable { mutableStateOf(false) }
            val snackbar = LocalDemoSnackbarHostState.current
            val scope = rememberCoroutineScope()
            val title = state.title()
            val summary = state.summary()
            val openedMessage = stringResource(Res.string.demo_pref_opened_page, title)
            val startAction: (@Composable () -> Unit)? = if (state.showIcon) {
                { PreferenceStartIcon() }
            } else {
                null
            }
            PreferencePreview {
                item {
                    when (type) {
                        0 -> SwitchPreference(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            title = title,
                            summary = summary,
                            startAction = startAction,
                            endActions = { if (state.showEndText) PreferenceEndText(enabled = state.enabled) },
                            enabled = state.enabled,
                        )

                        1 -> SwitchLoadingPreference(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            title = title,
                            isLoading = loading,
                            summary = summary,
                            startAction = startAction,
                            endActions = { if (state.showEndText) PreferenceEndText(enabled = state.enabled) },
                            enabled = state.enabled,
                        )

                        else -> SwitchWithDividerPreference(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            title = title,
                            onClick = { scope.launch { snackbar.showSnackbar(openedMessage) } },
                            summary = summary,
                            startAction = startAction,
                            endActions = { if (state.showEndText) PreferenceEndText(enabled = state.enabled) },
                            enabled = state.enabled,
                        )
                    }
                }
            }
            PlaygroundControls {
                literalChoiceControl(Res.string.demo_pref_component, SwitchTypes, type) { type = it }
                if (type == 1) switchControl(Res.string.common_loading, loading) { loading = it }
                preferenceRowControls(state)
            }
        }
        variant(key = "switch", title = "SwitchPreference") {
            ExpandingSwitchRows()
        }
        variant(key = "loading", title = "SwitchLoadingPreference") {
            AsyncSwitchRows()
        }
        variant(key = "divider", title = "SwitchWithDividerPreference") {
            SwitchWithDividerRows()
        }
    },
    searchTerms = Res.string.demo_switch_preference_search_terms,
)

/**
 * A switch that reveals another row when it is turned on, plus a disabled row.
 */
@Composable
private fun ExpandingSwitchRows() {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var innerChecked by rememberSaveable { mutableStateOf(false) }
    CardListGroup {
        item {
            SwitchPreference(
                checked = expanded,
                onCheckedChange = { expanded = it },
                title = stringResource(Res.string.demo_switch_preference_expand),
                summary = stringResource(Res.string.demo_switch_preference_expand_summary),
            )
        }
        item {
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Column {
                    SwitchPreference(
                        checked = innerChecked,
                        onCheckedChange = { innerChecked = it },
                        title = stringResource(Res.string.demo_switch_preference_expanded_row),
                        endActions = {
                            PreferenceEndText(text = stringResource(if (innerChecked) Res.string.common_on else Res.string.common_off))
                        },
                    )
                }
            }
        }
        item {
            SwitchPreference(
                checked = true,
                onCheckedChange = {},
                title = stringResource(Res.string.common_disabled),
                enabled = false,
            )
        }
    }
}

/**
 * A switch that applies its change after a delay, and one stuck in the loading state.
 */
@Composable
private fun AsyncSwitchRows() {
    var checked by remember { mutableStateOf(false) }
    var target by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    LaunchedEffect(loading) {
        if (loading) {
            delay(1500)
            checked = target
            loading = false
        }
    }
    CardListGroup {
        item {
            SwitchLoadingPreference(
                checked = checked,
                onCheckedChange = {
                    target = it
                    loading = true
                },
                title = stringResource(Res.string.demo_switch_preference_async),
                summary = stringResource(Res.string.demo_switch_preference_async_summary),
                isLoading = loading,
            )
        }
        item {
            SwitchLoadingPreference(
                checked = true,
                onCheckedChange = {},
                title = stringResource(Res.string.demo_switch_preference_always_loading),
                summary = stringResource(Res.string.demo_switch_preference_always_loading_summary),
                isLoading = true,
            )
        }
        item {
            SwitchLoadingPreference(
                checked = false,
                onCheckedChange = {},
                title = stringResource(Res.string.common_disabled),
                isLoading = true,
                enabled = false,
            )
        }
    }
}

/**
 * Rows whose main area opens a page and whose switch toggles the setting.
 */
@Composable
private fun SwitchWithDividerRows() {
    var wifi by rememberSaveable { mutableStateOf(true) }
    var bluetooth by rememberSaveable { mutableStateOf(false) }
    val snackbar = LocalDemoSnackbarHostState.current
    val scope = rememberCoroutineScope()
    val wifiTitle = stringResource(Res.string.sample_wifi)
    val bluetoothTitle = stringResource(Res.string.sample_bluetooth)
    CardListGroup {
        item {
            SwitchWithDividerPreference(
                checked = wifi,
                onCheckedChange = { wifi = it },
                title = wifiTitle,
                summary = if (wifi) {
                    stringResource(Res.string.demo_switch_preference_wifi_connected, "Home-5G")
                } else {
                    stringResource(Res.string.common_off)
                },
                onClick = {
                    scope.launch { snackbar.showSnackbar(getString(Res.string.demo_pref_opened_page, wifiTitle)) }
                },
            )
        }
        item {
            SwitchWithDividerPreference(
                checked = bluetooth,
                onCheckedChange = { bluetooth = it },
                title = bluetoothTitle,
                onClick = {
                    scope.launch { snackbar.showSnackbar(getString(Res.string.demo_pref_opened_page, bluetoothTitle)) }
                },
            )
        }
        item {
            SwitchWithDividerPreference(
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
