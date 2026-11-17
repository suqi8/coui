// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Ok
import io.github.suqi8.coui.kmp.preference.StatusSwitchingPreference
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.delay

@Composable
fun StatusSwitchingPreferenceDemo() {
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
            var enabled by remember { mutableStateOf(false) }
            var switching by remember { mutableStateOf(false) }
            LaunchedEffect(switching) {
                if (switching) {
                    delay(1500)
                    enabled = !enabled
                    switching = false
                }
            }
            Card {
                CardListGroup {
                    item {
                        StatusSwitchingPreference(
                            title = "Hotspot",
                            summary = "Tap to switch",
                            status = if (enabled) "On" else "Off",
                            switching = switching,
                            onClick = { if (!switching) switching = true },
                        )
                    }
                    item {
                        StatusSwitchingPreference(
                            title = "Backup",
                            statusIcon = {
                                Icon(
                                    imageVector = COUIIcons.Ok,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = COUITheme.colorScheme.primary,
                                )
                            },
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
    }
}
