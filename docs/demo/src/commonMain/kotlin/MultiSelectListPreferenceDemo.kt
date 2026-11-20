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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.preference.ListPreferenceEntry
import io.github.suqi8.coui.kmp.preference.MultiSelectListPreference

@Composable
fun MultiSelectListPreferenceDemo() {
    // The selection dialog is an OverlayDialog, which needs a hosting Scaffold.
    Scaffold {
        MultiSelectListPreferenceDemoContent()
    }
}

@Composable
private fun MultiSelectListPreferenceDemoContent() {
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
                    ListPreferenceEntry("Photos"),
                    ListPreferenceEntry("Documents"),
                    ListPreferenceEntry("Contacts", summary = "Contacts and call logs"),
                    ListPreferenceEntry("System settings", enabled = false),
                )
            }
            var selectedIndices by remember { mutableStateOf(setOf(0, 1)) }

            Card {
                CardListGroup {
                    item {
                        MultiSelectListPreference(
                            entries = entries,
                            selectedIndices = selectedIndices,
                            onSelectedIndicesChange = { selectedIndices = it },
                            title = "Sync items",
                            confirmButtonText = "Confirm",
                            cancelButtonText = "Cancel",
                            summary = "Changes apply on Confirm",
                        )
                    }
                    item {
                        MultiSelectListPreference(
                            entries = entries,
                            selectedIndices = setOf(0),
                            onSelectedIndicesChange = {},
                            title = "Disabled",
                            confirmButtonText = "Confirm",
                            cancelButtonText = "Cancel",
                            enabled = false,
                        )
                    }
                }
            }
        }
    }
}
