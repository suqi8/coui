// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.preference.ListPreference
import io.github.suqi8.coui.kmp.preference.ListPreferenceEntry

@Composable
fun ListPreferenceDemo() {
    // The selection panel is an OverlayBottomSheet, which needs a hosting Scaffold.
    Scaffold {
        ListPreferenceDemoContent()
    }
}

@Composable
private fun ListPreferenceDemoContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val themeEntries = remember {
                listOf(
                    ListPreferenceEntry("Light"),
                    ListPreferenceEntry("Dark"),
                    ListPreferenceEntry("Follow system", summary = "Switch with the system dark mode"),
                )
            }
            val qualityEntries = remember {
                listOf(
                    ListPreferenceEntry("Standard"),
                    ListPreferenceEntry("High"),
                    ListPreferenceEntry("Ultra", summary = "Requires a premium plan", enabled = false),
                )
            }
            var themeIndex by remember { mutableIntStateOf(2) }
            var qualityIndex by remember { mutableIntStateOf(1) }

            Card {
                CardListGroup {
                    item {
                        ListPreference(
                            entries = themeEntries,
                            selectedIndex = themeIndex,
                            onSelectedIndexChange = { themeIndex = it },
                            title = "Theme",
                            cancelButtonText = "Cancel",
                        )
                    }
                    item {
                        ListPreference(
                            entries = qualityEntries,
                            selectedIndex = qualityIndex,
                            onSelectedIndexChange = { qualityIndex = it },
                            title = "Video quality",
                            cancelButtonText = "Cancel",
                            summary = "One entry in the panel is disabled",
                        )
                    }
                    item {
                        ListPreference(
                            entries = themeEntries,
                            selectedIndex = 0,
                            onSelectedIndexChange = {},
                            title = "Disabled",
                            cancelButtonText = "Cancel",
                            enabled = false,
                        )
                    }
                }
            }
        }
    }
}
