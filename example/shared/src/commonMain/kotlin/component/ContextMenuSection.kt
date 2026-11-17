// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.menu.OverlayContextMenu
import io.github.suqi8.coui.kmp.menu.WindowContextMenu

fun LazyListScope.contextMenuSection() {
    item(key = "contextMenu") {
        SmallTitle(text = "Context Menu")
        var lastAction by remember { mutableStateOf("None") }
        val entries = remember {
            listOf(
                DropdownEntry(
                    items = listOf("Copy", "Share", "Rename").map { text ->
                        DropdownItem(text = text, onClick = { lastAction = text })
                    },
                ),
                DropdownEntry(
                    items = listOf(DropdownItem(text = "Delete", alert = true, onClick = { lastAction = "Delete" })),
                ),
            )
        }
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            CardListGroup {
                item {
                    OverlayContextMenu(entries = entries) {
                        BasicComponent(
                            title = "Long press or right click (overlay)",
                            summary = "Last action: $lastAction",
                            onClick = {},
                        )
                    }
                }
                item {
                    WindowContextMenu(entries = entries) {
                        BasicComponent(
                            title = "Long press or right click (window)",
                            summary = "Opens at the pointer in a platform window",
                            onClick = {},
                        )
                    }
                }
                item {
                    OverlayContextMenu(entries = entries, enabled = false) {
                        BasicComponent(
                            title = "Disabled",
                            summary = "No menu on long press",
                            enabled = false,
                        )
                    }
                }
            }
        }
    }
}
