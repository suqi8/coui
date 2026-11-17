// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.preference

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.InstallLoadState
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Ok
import io.github.suqi8.coui.kmp.preference.ActivityDialogPreference
import io.github.suqi8.coui.kmp.preference.ListPreferenceEntry
import io.github.suqi8.coui.kmp.preference.LoadInstallProgressPreference
import io.github.suqi8.coui.kmp.preference.StatusSwitchingPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_off
import io.github.suqi8.coui.kmp.shared.generated.resources.common_on
import io.github.suqi8.coui.kmp.shared.generated.resources.common_progress
import io.github.suqi8.coui.kmp.shared.generated.resources.common_retry
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_dark
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_follow_system
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_light
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_preference_show_value
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_component
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_appearance
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_appearance_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_backup
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_calculator
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_dark_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_download_failed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_hotspot
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_install
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_resume
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_scheduled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_show_status
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_state
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_status_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_switching
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_tap_to_switch
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_unavailable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_up_to_date
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_status_preference_weather
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_option
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private val StatusTypes = listOf("StatusSwitchingPreference", "LoadInstallProgressPreference", "ActivityDialogPreference")
private val InstallStates = InstallLoadState.entries.map { it.name }
private val ProgressSteps = listOf("0%", "25%", "50%", "75%", "100%")

/** The demo page of `StatusSwitchingPreference`, listed in its category and found by the search. */
internal val StatusPreferenceDemo = Demo(
    id = "status_preference",
    apiName = "StatusSwitchingPreference",
    title = Res.string.demo_status_preference_title,
    category = DemoCategory.Preference,
    page = DemoPage.Variants {
        playground {
            val state = rememberPreferenceRowState(showSummary = false)
            var type by rememberSaveable { mutableIntStateOf(0) }
            var switching by rememberSaveable { mutableStateOf(false) }
            var showStatus by rememberSaveable { mutableStateOf(true) }
            var statusIcon by rememberSaveable { mutableStateOf(false) }
            var installState by rememberSaveable { mutableIntStateOf(1) }
            var progressIndex by rememberSaveable { mutableIntStateOf(2) }
            var showValue by rememberSaveable { mutableStateOf(true) }
            var selected by rememberSaveable { mutableIntStateOf(0) }
            val title = state.title()
            val summary = state.summary()
            val startAction: (@Composable () -> Unit)? = if (state.showIcon) {
                { PreferenceStartIcon() }
            } else {
                null
            }
            val loadState = InstallLoadState.entries[installState]
            val progress = progressIndex / 4f
            val entries = List(4) { index -> ListPreferenceEntry(stringResource(Res.string.sample_option, index + 1)) }
            PreferencePreview {
                item {
                    when (type) {
                        0 -> StatusSwitchingPreference(
                            title = title,
                            status = if (showStatus) stringResource(Res.string.common_on) else null,
                            switching = switching,
                            statusIcon = if (statusIcon) {
                                { StatusOkIcon() }
                            } else {
                                null
                            },
                            summary = summary,
                            startAction = startAction,
                            onClick = { switching = !switching },
                            enabled = state.enabled,
                        )

                        1 -> LoadInstallProgressPreference(
                            title = title,
                            progressText = installLabel(loadState, progress),
                            onProgressClick = {
                                installState = when (loadState) {
                                    InstallLoadState.Loading -> InstallLoadState.Paused.ordinal
                                    else -> InstallLoadState.Loading.ordinal
                                }
                            },
                            state = loadState,
                            progress = progress,
                            summary = summary,
                            startAction = startAction,
                            enabled = state.enabled,
                        )

                        else -> ActivityDialogPreference(
                            entries = entries,
                            selectedIndex = selected,
                            onSelectedIndexChange = { selected = it },
                            title = title,
                            summary = summary,
                            startAction = startAction,
                            enabled = state.enabled,
                            showValue = showValue,
                        )
                    }
                }
            }
            PlaygroundControls {
                literalChoiceControl(Res.string.demo_pref_component, StatusTypes, type) { type = it }
                when (type) {
                    0 -> {
                        switchControl(Res.string.demo_status_preference_switching, switching) { switching = it }
                        switchControl(Res.string.demo_status_preference_show_status, showStatus) { showStatus = it }
                        switchControl(Res.string.demo_status_preference_status_icon, statusIcon) { statusIcon = it }
                    }

                    1 -> {
                        literalChoiceControl(Res.string.demo_status_preference_state, InstallStates, installState) {
                            installState = it
                        }
                        literalChoiceControl(Res.string.common_progress, ProgressSteps, progressIndex) { progressIndex = it }
                    }

                    else -> {
                        switchControl(Res.string.demo_list_preference_show_value, showValue) { showValue = it }
                    }
                }
                preferenceRowControls(state, endText = false)
            }
        }
        variant(key = "statusSwitching", title = "StatusSwitchingPreference") {
            StatusSwitchingRows()
        }
        variant(key = "loadInstall", title = "LoadInstallProgressPreference") {
            LoadInstallRows()
        }
        variant(key = "activityDialog", title = "ActivityDialogPreference") {
            ActivityDialogRows()
        }
    },
    searchTerms = Res.string.demo_status_preference_search_terms,
)

/**
 * The label on the install button for [state].
 */
@Composable
private fun installLabel(state: InstallLoadState, progress: Float): String = when (state) {
    InstallLoadState.Idle -> stringResource(Res.string.demo_status_preference_install)
    InstallLoadState.Loading -> "${(progress * 100).toInt()}%"
    InstallLoadState.Paused -> stringResource(Res.string.demo_status_preference_resume)
    InstallLoadState.Failed -> stringResource(Res.string.common_retry)
}

@Composable
private fun StatusOkIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = COUIIcons.Ok,
        contentDescription = null,
        modifier = modifier.size(20.dp),
        tint = COUITheme.colorScheme.primary,
    )
}

/**
 * Tapping the first row shows the spinner for a moment, then flips the status text.
 */
@Composable
private fun StatusSwitchingRows() {
    var on by rememberSaveable { mutableStateOf(false) }
    var switching by remember { mutableStateOf(false) }
    LaunchedEffect(switching) {
        if (switching) {
            delay(1500)
            on = !on
            switching = false
        }
    }
    CardListGroup {
        item {
            StatusSwitchingPreference(
                title = stringResource(Res.string.demo_status_preference_hotspot),
                summary = stringResource(Res.string.demo_status_preference_tap_to_switch),
                status = stringResource(if (on) Res.string.common_on else Res.string.common_off),
                switching = switching,
                onClick = { if (!switching) switching = true },
            )
        }
        item {
            StatusSwitchingPreference(
                title = stringResource(Res.string.demo_status_preference_backup),
                status = stringResource(Res.string.demo_status_preference_up_to_date),
                statusIcon = { StatusOkIcon() },
                onClick = {},
            )
        }
        item {
            StatusSwitchingPreference(
                title = stringResource(Res.string.common_disabled),
                status = stringResource(Res.string.demo_status_preference_unavailable),
                enabled = false,
            )
        }
    }
}

/**
 * Each tap advances Idle, Loading, Paused, Loading, …; loading fills the bar.
 */
@Composable
private fun LoadInstallRows() {
    var state by remember { mutableStateOf(InstallLoadState.Idle) }
    var progress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(state) {
        while (state == InstallLoadState.Loading && progress < 1f) {
            delay(120)
            progress = (progress + 0.04f).coerceAtMost(1f)
        }
        if (progress >= 1f) {
            state = InstallLoadState.Idle
            progress = 0f
        }
    }
    CardListGroup {
        item {
            LoadInstallProgressPreference(
                title = stringResource(Res.string.demo_status_preference_weather),
                progressText = installLabel(state, progress),
                onProgressClick = {
                    state = when (state) {
                        InstallLoadState.Loading -> InstallLoadState.Paused
                        else -> InstallLoadState.Loading
                    }
                },
                state = state,
                progress = progress,
                summary = "18.6 MB",
            )
        }
        item {
            LoadInstallProgressPreference(
                title = stringResource(Res.string.demo_status_preference_calculator),
                progressText = stringResource(Res.string.common_retry),
                onProgressClick = {},
                state = InstallLoadState.Failed,
                summary = stringResource(Res.string.demo_status_preference_download_failed),
            )
        }
        item {
            LoadInstallProgressPreference(
                title = stringResource(Res.string.common_disabled),
                progressText = stringResource(Res.string.demo_status_preference_install),
                onProgressClick = {},
                enabled = false,
            )
        }
    }
}

/**
 * Rows that open their choices on a full page.
 */
@Composable
private fun ActivityDialogRows() {
    val entries = listOf(
        ListPreferenceEntry(stringResource(Res.string.demo_list_preference_follow_system)),
        ListPreferenceEntry(stringResource(Res.string.demo_list_preference_light)),
        ListPreferenceEntry(
            stringResource(Res.string.demo_list_preference_dark),
            summary = stringResource(Res.string.demo_status_preference_dark_summary),
        ),
        ListPreferenceEntry(stringResource(Res.string.demo_status_preference_scheduled), enabled = false),
    )
    var selected by rememberSaveable { mutableIntStateOf(0) }
    CardListGroup {
        item {
            ActivityDialogPreference(
                entries = entries,
                selectedIndex = selected,
                onSelectedIndexChange = { selected = it },
                title = stringResource(Res.string.demo_status_preference_appearance),
                summary = stringResource(Res.string.demo_status_preference_appearance_summary),
            )
        }
        item {
            ActivityDialogPreference(
                entries = entries,
                selectedIndex = selected,
                onSelectedIndexChange = {},
                title = stringResource(Res.string.common_disabled),
                enabled = false,
            )
        }
    }
}
