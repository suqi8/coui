// Copyright 2025, compose-coui-ui contributors
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
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.Icon
import com.suqi8.coui.kmp.basic.IconButton
import com.suqi8.coui.kmp.basic.Scaffold
import com.suqi8.coui.kmp.basic.TextButton
import com.suqi8.coui.kmp.icon.COUIIcons
import com.suqi8.coui.kmp.icon.extended.Close
import com.suqi8.coui.kmp.icon.extended.Ok
import com.suqi8.coui.kmp.overlay.OverlayBottomSheet
import com.suqi8.coui.kmp.preference.SwitchPreference
import com.suqi8.coui.kmp.theme.COUITheme

@Composable
fun OverlayBottomSheetDemo() {
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
                var showBottomSheet by remember { mutableStateOf(false) }
                var notificationsEnabled by remember { mutableStateOf(true) }
                var soundEnabled by remember { mutableStateOf(false) }

                Card {
                    TextButton(
                        text = "Show a BottomSheet",
                        onClick = { showBottomSheet = true },
                    )
                    OverlayBottomSheet(
                        title = "BottomSheet Title",
                        show = showBottomSheet,
                        startAction = {
                            IconButton(onClick = { showBottomSheet = false }) {
                                Icon(
                                    imageVector = COUIIcons.Close,
                                    contentDescription = "Cancel",
                                    tint = COUITheme.colorScheme.onBackground,
                                )
                            }
                        },
                        endAction = {
                            IconButton(onClick = { showBottomSheet = false }) {
                                Icon(
                                    imageVector = COUIIcons.Ok,
                                    contentDescription = "Confirm",
                                    tint = COUITheme.colorScheme.onBackground,
                                )
                            }
                        },
                        onDismissRequest = { showBottomSheet = false },
                    ) {
                        Card(modifier = Modifier.padding(bottom = 16.dp)) {
                            SwitchPreference(
                                title = "Notifications",
                                summary = "Receive push notifications",
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it },
                            )
                            SwitchPreference(
                                title = "Sound",
                                summary = "Play sound on notification",
                                checked = soundEnabled,
                                onCheckedChange = { soundEnabled = it },
                            )
                        }
                    }
                }
            }
        }
    }
}
