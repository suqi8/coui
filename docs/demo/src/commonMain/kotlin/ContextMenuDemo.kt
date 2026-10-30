// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.SmallTopAppBar
import io.github.suqi8.coui.kmp.menu.OverlayContextMenu
import io.github.suqi8.coui.kmp.menu.WindowContextMenu

@Composable
fun ContextMenuDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        var lastAction by remember { mutableStateOf("None") }
        var menuOpen by remember { mutableStateOf(false) }
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

        Card(modifier = Modifier.padding(16.dp).widthIn(max = 600.dp).fillMaxWidth()) {
            Scaffold(
                topBar = { SmallTopAppBar(title = "Files") },
            ) { padding ->
                LazyColumn(
                    contentPadding = PaddingValues(top = padding.calculateTopPadding()),
                ) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                            CardListGroup {
                                item {
                                    OverlayContextMenu(
                                        entries = entries,
                                        onExpandedChange = { menuOpen = it },
                                    ) {
                                        BasicComponent(
                                            title = "Report.pdf",
                                            summary = "Long press or right click · last action: $lastAction",
                                            onClick = {},
                                        )
                                    }
                                }
                                item {
                                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                                    WindowContextMenu(entries = entries) {
                                        BasicComponent(
                                            title = "Photo.jpg",
                                            summary = "Window-layer menu at the pointer",
                                            onClick = {},
                                        )
                                    }
                                }
                                item {
                                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                                    OverlayContextMenu(entries = entries, enabled = false) {
                                        BasicComponent(
                                            title = "Locked.txt",
                                            summary = if (menuOpen) "Menu open" else "Menu disabled",
                                            enabled = false,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
