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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.SearchHistory

private val SearchHistoryDemoRecords = listOf(
    "Wallpaper",
    "Dark mode",
    "Bluetooth",
    "Battery health",
    "Always-on display",
    "Notification history",
    "Screen time",
    "Fonts",
    "Wi-Fi",
    "Do not disturb",
    "Storage",
    "App lock",
    "Developer options",
)

@Composable
fun SearchHistoryDemo() {
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
            val items = remember { mutableStateListOf(*SearchHistoryDemoRecords.toTypedArray()) }
            Card(Modifier.fillMaxWidth()) {
                SearchHistory(
                    title = "Search history",
                    items = items,
                    onItemClick = {},
                    onClear = { items.clear() },
                    maxLinesFolded = 2,
                    clearContentDescription = "Clear search history",
                )
                if (items.isEmpty()) {
                    BasicComponent(
                        title = "Restore the records",
                        onClick = { items.addAll(SearchHistoryDemoRecords) },
                    )
                }
            }
        }
    }
}
