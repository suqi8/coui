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
import io.github.suqi8.coui.kmp.preference.SwitchLoadingPreference
import kotlinx.coroutines.delay

@Composable
fun SwitchLoadingPreferenceDemo() {
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
            var checked by remember { mutableStateOf(false) }
            var target by remember { mutableStateOf(false) }
            var isLoading by remember { mutableStateOf(false) }

            LaunchedEffect(isLoading) {
                if (isLoading) {
                    delay(1500)
                    checked = target
                    isLoading = false
                }
            }

            Card {
                CardListGroup {
                    item {
                        SwitchLoadingPreference(
                            checked = checked,
                            onCheckedChange = {
                                target = it
                                isLoading = true
                            },
                            title = "Async switch",
                            summary = if (isLoading) "Applying..." else "Applies the change after a short delay",
                            isLoading = isLoading,
                        )
                    }
                    item {
                        SwitchLoadingPreference(
                            checked = true,
                            onCheckedChange = {},
                            title = "Always loading",
                            isLoading = true,
                        )
                    }
                    item {
                        SwitchLoadingPreference(
                            checked = true,
                            onCheckedChange = {},
                            title = "Disabled",
                            summary = "This switch is currently unavailable",
                            enabled = false,
                        )
                    }
                }
            }
        }
    }
}
