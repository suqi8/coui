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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference

@Composable
fun CardListGroupDemo() {
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
            val options = remember { listOf("Standard", "Large", "Extra large") }
            var wlan by remember { mutableStateOf(true) }
            var selectedIndex by remember { mutableIntStateOf(0) }

            Card {
                CardListGroup {
                    item(key = "wlan") {
                        SwitchPreference(
                            title = "WLAN",
                            checked = wlan,
                            onCheckedChange = { wlan = it },
                        )
                    }
                    if (wlan) {
                        item(key = "network") {
                            ArrowPreference(
                                title = "Connected network",
                                summary = "Home-5G",
                                onClick = {},
                            )
                        }
                    }
                    item(key = "bluetooth") {
                        ArrowPreference(
                            title = "Bluetooth",
                            onClick = {},
                        )
                    }
                }
            }
            Card {
                CardListGroup {
                    options.forEachIndexed { index, option ->
                        item(key = option) {
                            BasicComponent(
                                title = option,
                                selected = selectedIndex == index,
                                onClick = { selectedIndex = index },
                            )
                        }
                    }
                }
            }
        }
    }
}
