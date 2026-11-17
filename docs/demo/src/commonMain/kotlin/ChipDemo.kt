// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.Chip
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconChip
import io.github.suqi8.coui.kmp.basic.SuggestionChip
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Check
import io.github.suqi8.coui.kmp.icon.basic.Search

private val defaultRecords = listOf("Wallpaper", "Dark mode", "Battery")

@Composable
fun ChipDemo() {
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
            var recommended by remember { mutableStateOf(true) }
            var favorites by remember { mutableStateOf(false) }
            var downloads by remember { mutableStateOf(false) }
            Card(modifier = Modifier.fillMaxWidth()) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Chip(
                        selected = recommended,
                        onClick = { recommended = !recommended },
                        label = "Recommended",
                    )
                    Chip(
                        selected = favorites,
                        onClick = { favorites = !favorites },
                        label = "Favorites",
                        icon = {
                            Icon(
                                imageVector = COUIIcons.Basic.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                        },
                    )
                    Chip(
                        selected = downloads,
                        onClick = { downloads = !downloads },
                        label = "Downloads",
                        count = "12",
                        showRedDot = true,
                    )
                }
            }

            val records = remember { mutableStateListOf(*defaultRecords.toTypedArray()) }
            Card(modifier = Modifier.fillMaxWidth()) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    IconChip(
                        onClick = {
                            records.clear()
                            records.addAll(defaultRecords)
                        },
                    ) {
                        Icon(
                            imageVector = COUIIcons.Basic.Search,
                            contentDescription = "Restore records",
                        )
                    }
                    records.forEach { record ->
                        key(record) {
                            SuggestionChip(
                                onClick = {},
                                label = record,
                                onClose = { records.remove(record) },
                            )
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Chip(
                    selected = true,
                    onClick = {},
                    label = "Disabled",
                    enabled = false,
                )
                Chip(
                    selected = false,
                    onClick = {},
                    label = "Disabled",
                    enabled = false,
                )
            }
        }
    }
}
