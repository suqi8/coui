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
import io.github.suqi8.coui.kmp.preference.SwitchWithDividerPreference

@Composable
fun SwitchWithDividerPreferenceDemo() {
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
            var wlan by remember { mutableStateOf(true) }
            var bluetooth by remember { mutableStateOf(false) }
            var opened by remember { mutableStateOf("") }

            Card {
                SwitchWithDividerPreference(
                    checked = wlan,
                    onCheckedChange = { wlan = it },
                    title = "WLAN",
                    summary = when {
                        opened == "WLAN" -> "Opened the WLAN page"
                        wlan -> "Connected"
                        else -> "Off"
                    },
                    onClick = { opened = "WLAN" },
                )
                SwitchWithDividerPreference(
                    checked = bluetooth,
                    onCheckedChange = { bluetooth = it },
                    title = "Bluetooth",
                    summary = if (opened == "Bluetooth") "Opened the Bluetooth page" else null,
                    onClick = { opened = "Bluetooth" },
                )
                SwitchWithDividerPreference(
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
