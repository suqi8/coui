// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.ExpandableFloatingActionButton
import io.github.suqi8.coui.kmp.basic.FabPosition
import io.github.suqi8.coui.kmp.basic.FloatingActionButton
import io.github.suqi8.coui.kmp.basic.FloatingActionButtonItem
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Add
import io.github.suqi8.coui.kmp.icon.extended.Edit
import io.github.suqi8.coui.kmp.icon.extended.SelectAll
import io.github.suqi8.coui.kmp.icon.extended.Share
import io.github.suqi8.coui.kmp.preference.ArrowPreference

@Composable
fun FloatingActionButtonDemo() {
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Card(
                    modifier = Modifier.weight(0.5f),
                ) {
                    Scaffold(
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {
                                    // Handle FAB click
                                },
                            ) {
                                Icon(
                                    imageVector = COUIIcons.SelectAll,
                                    contentDescription = "SelectAll",
                                    tint = Color.White,
                                )
                            }
                        },
                    ) { paddingValues ->
                        LazyColumn(
                            contentPadding = PaddingValues(top = paddingValues.calculateTopPadding()),
                        ) {
                            items(100) {
                                ArrowPreference(
                                    title = "Something",
                                )
                            }
                        }
                    }
                }
                Card(
                    modifier = Modifier.weight(0.5f),
                ) {
                    Scaffold(
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {
                                    // Handle FAB click
                                },
                            ) {
                                Icon(
                                    imageVector = COUIIcons.SelectAll,
                                    contentDescription = "SelectAll",
                                    tint = Color.White,
                                )
                            }
                        },
                        floatingActionButtonPosition = FabPosition.Center,
                    ) { paddingValues ->
                        LazyColumn(
                            contentPadding = PaddingValues(top = paddingValues.calculateTopPadding()),
                        ) {
                            items(100) {
                                ArrowPreference(
                                    title = "Something",
                                )
                            }
                        }
                    }
                }
                Card(
                    modifier = Modifier.weight(0.5f),
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    val items = remember {
                        listOf(
                            FloatingActionButtonItem(
                                icon = { Icon(imageVector = COUIIcons.Edit, contentDescription = "Edit") },
                                onClick = { expanded = false },
                                label = "Edit",
                            ),
                            FloatingActionButtonItem(
                                icon = { Icon(imageVector = COUIIcons.Share, contentDescription = "Share") },
                                onClick = { expanded = false },
                                label = "Share",
                            ),
                        )
                    }
                    Scaffold(
                        floatingActionButton = {
                            ExpandableFloatingActionButton(
                                expanded = expanded,
                                onExpandedChange = { expanded = it },
                                items = items,
                            ) {
                                Icon(imageVector = COUIIcons.Add, contentDescription = "Add")
                            }
                        },
                    ) { paddingValues ->
                        LazyColumn(
                            contentPadding = PaddingValues(top = paddingValues.calculateTopPadding()),
                        ) {
                            items(100) {
                                ArrowPreference(
                                    title = "Something",
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
