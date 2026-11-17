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
import io.github.suqi8.coui.kmp.preference.CheckboxWithDividerPreference

@Composable
fun CheckboxWithDividerPreferenceDemo() {
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
            var photos by remember { mutableStateOf(true) }
            var contacts by remember { mutableStateOf(false) }
            var opened by remember { mutableStateOf("") }

            Card {
                CheckboxWithDividerPreference(
                    checked = photos,
                    onCheckedChange = { photos = it },
                    title = "Photos",
                    summary = if (opened == "Photos") "Opened the Photos page" else "Tap the row to choose albums",
                    onClick = { opened = "Photos" },
                )
                CheckboxWithDividerPreference(
                    checked = contacts,
                    onCheckedChange = { contacts = it },
                    title = "Contacts",
                    summary = if (opened == "Contacts") "Opened the Contacts page" else null,
                    onClick = { opened = "Contacts" },
                )
                CheckboxWithDividerPreference(
                    checked = true,
                    onCheckedChange = {},
                    title = "Disabled",
                    onClick = {},
                    enabled = false,
                )
            }
        }
    }
}
