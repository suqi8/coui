// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.SearchHistory
import io.github.suqi8.coui.kmp.basic.SmallTitle

private val SearchHistoryDemoItems = listOf(
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
    "Hotspot",
    "Location",
    "Accessibility",
)

fun LazyListScope.searchHistorySection() {
    item(key = "searchHistory") {
        SmallTitle(text = "SearchHistory")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            SearchHistoryDemo()
        }
    }
}

@Composable
private fun SearchHistoryDemo() {
    val items = remember { mutableStateListOf(*SearchHistoryDemoItems.toTypedArray()) }
    var lastClicked by remember { mutableStateOf<String?>(null) }
    SearchHistory(
        title = "Search history",
        items = items,
        onItemClick = { lastClicked = it },
        onClear = { items.clear() },
        maxLinesFolded = 2,
        clearContentDescription = "Clear search history",
    )
    BasicComponent(
        title = if (items.isEmpty()) "History cleared" else "Last clicked",
        summary = if (items.isEmpty()) "Click to restore the records" else lastClicked ?: "None",
        onClick = if (items.isEmpty()) {
            { items.addAll(SearchHistoryDemoItems) }
        } else {
            null
        },
    )
}
