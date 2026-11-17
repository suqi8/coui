// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.InstallLoadState
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Backup
import io.github.suqi8.coui.kmp.icon.extended.Contacts
import io.github.suqi8.coui.kmp.icon.extended.Lock
import io.github.suqi8.coui.kmp.icon.extended.Music
import io.github.suqi8.coui.kmp.icon.extended.Ok
import io.github.suqi8.coui.kmp.icon.extended.Photos
import io.github.suqi8.coui.kmp.icon.extended.Scan
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.preference.ActivityDialogPreference
import io.github.suqi8.coui.kmp.preference.CardButtonItem
import io.github.suqi8.coui.kmp.preference.CardButtonPreferenceGroup
import io.github.suqi8.coui.kmp.preference.CheckboxPreference
import io.github.suqi8.coui.kmp.preference.CheckboxPreferenceCategory
import io.github.suqi8.coui.kmp.preference.ListPreferenceEntry
import io.github.suqi8.coui.kmp.preference.LoadInstallProgressPreference
import io.github.suqi8.coui.kmp.preference.LoadingPreferenceCategory
import io.github.suqi8.coui.kmp.preference.StatusSwitchingPreference
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.delay

fun LazyListScope.statusPreferenceSection() {
    item(key = "statusSwitchingPreference") {
        // Tapping the row shows the spinner for a moment, then flips the status text.
        val enabled = remember { mutableStateOf(false) }
        val switching = remember { mutableStateOf(false) }
        LaunchedEffect(switching.value) {
            if (switching.value) {
                delay(1500)
                enabled.value = !enabled.value
                switching.value = false
            }
        }

        SmallTitle(text = "StatusSwitchingPreference")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            CardListGroup {
                item {
                    StatusSwitchingPreference(
                        title = "Hotspot",
                        summary = "Tap to switch",
                        status = if (enabled.value) "On" else "Off",
                        switching = switching.value,
                        onClick = { if (!switching.value) switching.value = true },
                    )
                }
                item {
                    StatusSwitchingPreference(
                        title = "Backup",
                        status = "Up to date",
                        statusIcon = {
                            Icon(
                                imageVector = COUIIcons.Ok,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = COUITheme.colorScheme.primary,
                            )
                        },
                        onClick = {},
                    )
                }
                item {
                    StatusSwitchingPreference(
                        title = "Disabled",
                        status = "Unavailable",
                        enabled = false,
                    )
                }
            }
        }
    }

    item(key = "loadInstallProgressPreference") {
        // Each tap advances Idle -> Loading -> Paused -> Loading ...; loading fills the bar.
        val state = remember { mutableStateOf(InstallLoadState.Idle) }
        val progress = remember { mutableFloatStateOf(0f) }
        LaunchedEffect(state.value) {
            while (state.value == InstallLoadState.Loading && progress.floatValue < 1f) {
                delay(120)
                progress.floatValue = (progress.floatValue + 0.04f).coerceAtMost(1f)
            }
            if (progress.floatValue >= 1f) {
                state.value = InstallLoadState.Idle
                progress.floatValue = 0f
            }
        }
        val label = when (state.value) {
            InstallLoadState.Idle -> "Install"
            InstallLoadState.Loading -> "${(progress.floatValue * 100).toInt()}%"
            InstallLoadState.Paused -> "Resume"
            InstallLoadState.Failed -> "Retry"
        }

        SmallTitle(text = "LoadInstallProgressPreference")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            CardListGroup {
                item {
                    LoadInstallProgressPreference(
                        title = "Weather",
                        summary = "18.6 MB",
                        progressText = label,
                        state = state.value,
                        progress = progress.floatValue,
                        onProgressClick = {
                            state.value = when (state.value) {
                                InstallLoadState.Loading -> InstallLoadState.Paused
                                else -> InstallLoadState.Loading
                            }
                        },
                    )
                }
                item {
                    LoadInstallProgressPreference(
                        title = "Calculator",
                        summary = "Download failed",
                        progressText = "Retry",
                        state = InstallLoadState.Failed,
                        onProgressClick = {},
                    )
                }
                item {
                    LoadInstallProgressPreference(
                        title = "Disabled",
                        progressText = "Install",
                        onProgressClick = {},
                        enabled = false,
                    )
                }
            }
        }
    }

    item(key = "loadingPreferenceCategory") {
        // The category spins while "scanning", then offers a refresh action in place of the spinner.
        val scanning = remember { mutableStateOf(true) }
        LaunchedEffect(scanning.value) {
            if (scanning.value) {
                delay(3000)
                scanning.value = false
            }
        }

        LoadingPreferenceCategory(
            title = "Available devices",
            loading = scanning.value,
            loadingText = "Searching",
            endContent = {
                Text(
                    text = "Refresh",
                    modifier = Modifier.clickable { scanning.value = true },
                    style = COUITheme.textStyles.bodyXS.copy(fontWeight = FontWeight.Medium),
                    color = COUITheme.colorScheme.primary,
                )
            },
        )
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            CardListGroup {
                item {
                    StatusSwitchingPreference(title = "Living room speaker", status = "Paired")
                }
                item {
                    StatusSwitchingPreference(title = "Earbuds", status = "Not connected")
                }
            }
        }
    }

    item(key = "checkboxPreferenceCategory") {
        val options = remember { listOf("Photos", "Contacts", "Messages") }
        val checked = remember { mutableStateListOf(true, false, false) }
        val categoryState = when (checked.count { it }) {
            0 -> ToggleableState.Off
            checked.size -> ToggleableState.On
            else -> ToggleableState.Indeterminate
        }

        CheckboxPreferenceCategory(
            title = "Items to migrate",
            state = categoryState,
            onStateChange = { newState ->
                val value = newState == ToggleableState.On
                for (i in checked.indices) checked[i] = value
            },
        )
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            CardListGroup {
                options.forEachIndexed { index, option ->
                    item(key = option) {
                        CheckboxPreference(
                            title = option,
                            checked = checked[index],
                            onCheckedChange = { checked[index] = it },
                        )
                    }
                }
            }
        }
    }

    item(key = "activityDialogPreference") {
        val entries = remember {
            listOf(
                ListPreferenceEntry("Follow system"),
                ListPreferenceEntry("Light"),
                ListPreferenceEntry("Dark", summary = "Easier on the eyes at night"),
                ListPreferenceEntry("Scheduled", enabled = false),
            )
        }
        val selected = remember { mutableIntStateOf(0) }

        SmallTitle(text = "ActivityDialogPreference")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            CardListGroup {
                item {
                    ActivityDialogPreference(
                        entries = entries,
                        selectedIndex = selected.intValue,
                        onSelectedIndexChange = { selected.intValue = it },
                        title = "Appearance",
                        summary = "Opens the choices on a full page",
                    )
                }
                item {
                    ActivityDialogPreference(
                        entries = entries,
                        selectedIndex = selected.intValue,
                        onSelectedIndexChange = {},
                        title = "Disabled",
                        enabled = false,
                    )
                }
            }
        }
    }

    item(key = "cardButtonPreference") {
        val clicked = remember { mutableStateOf("Tap a card button") }
        val items = remember {
            listOf(
                CardButtonItem("Scan", icon = { CardButtonIcon(COUIIcons.Scan) }, onClick = { clicked.value = "Scan" }),
                CardButtonItem("Photos", icon = { CardButtonIcon(COUIIcons.Photos) }, onClick = { clicked.value = "Photos" }),
                CardButtonItem("Music", icon = { CardButtonIcon(COUIIcons.Music) }, onClick = { clicked.value = "Music" }),
                CardButtonItem(
                    "Contacts and accounts",
                    icon = { CardButtonIcon(COUIIcons.Contacts) },
                    onClick = { clicked.value = "Contacts" },
                ),
                CardButtonItem("Backup", icon = { CardButtonIcon(COUIIcons.Backup) }, onClick = { clicked.value = "Backup" }),
                CardButtonItem("Privacy", icon = { CardButtonIcon(COUIIcons.Lock) }, onClick = { clicked.value = "Privacy" }),
                CardButtonItem("Disabled", icon = { CardButtonIcon(COUIIcons.Settings) }, enabled = false, onClick = {}),
            )
        }

        SmallTitle(text = "CardButtonPreference: ${clicked.value}")
        CardButtonPreferenceGroup(
            items = items,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        )
    }
}

@Composable
private fun CardButtonIcon(imageVector: ImageVector) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        modifier = Modifier.size(28.dp),
        tint = COUITheme.colorScheme.labelPrimary,
    )
}
