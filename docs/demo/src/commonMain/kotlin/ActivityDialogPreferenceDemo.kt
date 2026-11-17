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
import io.github.suqi8.coui.kmp.preference.ActivityDialogPreference
import io.github.suqi8.coui.kmp.preference.ListPreferenceEntry

@Composable
fun ActivityDialogPreferenceDemo() {
    Scaffold {
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
                val entries = remember {
                    listOf(
                        ListPreferenceEntry("Follow system"),
                        ListPreferenceEntry("Light"),
                        ListPreferenceEntry("Dark", summary = "Easier on the eyes at night"),
                        ListPreferenceEntry("Scheduled", enabled = false),
                    )
                }
                var selected by remember { mutableIntStateOf(0) }
                Card {
                    CardListGroup {
                        item {
                            ActivityDialogPreference(
                                entries = entries,
                                selectedIndex = selected,
                                onSelectedIndexChange = { selected = it },
                                title = "Appearance",
                                summary = "Opens the choices on a full page",
                            )
                        }
                        item {
                            ActivityDialogPreference(
                                entries = entries,
                                selectedIndex = selected,
                                onSelectedIndexChange = {},
                                title = "Disabled",
                                enabled = false,
                            )
                        }
                    }
                }
            }
        }
    }
}
