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
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.preference.MarkWithDividerPreference

@Composable
fun MarkWithDividerPreferenceDemo() {
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
            val networks = listOf("Home-5G", "Office")
            var selected by remember { mutableIntStateOf(0) }
            var opened by remember { mutableStateOf("") }

            Card {
                CardListGroup {
                    networks.forEachIndexed { index, name ->
                        item {
                            MarkWithDividerPreference(
                                checked = selected == index,
                                onCheckedChange = { if (it) selected = index },
                                title = name,
                                summary = if (opened == name) "Opened the $name page" else "Tap the row for details",
                                onClick = { opened = name },
                            )
                        }
                    }
                    item {
                        MarkWithDividerPreference(
                            checked = false,
                            onCheckedChange = {},
                            title = "Disabled",
                            onClick = {},
                            enabled = false,
                        )
                    }
                }
            }
        }
    }
}
