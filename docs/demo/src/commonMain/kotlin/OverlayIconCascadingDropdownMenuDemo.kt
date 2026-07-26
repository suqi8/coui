// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.DropdownEntry
import com.suqi8.coui.kmp.basic.DropdownItem
import com.suqi8.coui.kmp.basic.Icon
import com.suqi8.coui.kmp.basic.Scaffold
import com.suqi8.coui.kmp.basic.SmallTopAppBar
import com.suqi8.coui.kmp.basic.Text
import com.suqi8.coui.kmp.icon.COUIIcons
import com.suqi8.coui.kmp.icon.extended.MoreCircle
import com.suqi8.coui.kmp.icon.extended.Tune
import com.suqi8.coui.kmp.menu.OverlayIconCascadingDropdownMenu
import com.suqi8.coui.kmp.theme.COUITheme

@Composable
fun OverlayIconCascadingDropdownMenuDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        var sortIndex by remember { mutableIntStateOf(0) }
        var viewIndex by remember { mutableIntStateOf(0) }
        var filterIndex by remember { mutableIntStateOf(0) }
        val sortLabels = listOf("Sort by capture date", "Sort by date added")
        val viewLabels = listOf("Group by date", "Compact")
        val filterLabels = listOf("All items", "Camera album")

        val entries = remember(sortIndex, viewIndex, filterIndex) {
            listOf(
                DropdownEntry(
                    items = sortLabels.mapIndexed { idx, label ->
                        DropdownItem(
                            text = label,
                            selected = sortIndex == idx,
                            onClick = { sortIndex = idx },
                        )
                    },
                ),
                DropdownEntry(
                    items = listOf(
                        DropdownItem(
                            text = "View mode",
                            children = viewLabels.mapIndexed { idx, label ->
                                DropdownItem(
                                    text = label,
                                    selected = viewIndex == idx,
                                    onClick = { viewIndex = idx },
                                )
                            },
                        ),
                        DropdownItem(
                            text = "Filter",
                            children = filterLabels.mapIndexed { idx, label ->
                                DropdownItem(
                                    text = label,
                                    selected = filterIndex == idx,
                                    onClick = { filterIndex = idx },
                                )
                            },
                        ),
                    ),
                ),
            )
        }
        val emptyEntry = DropdownEntry(items = listOf(DropdownItem(text = "Option 1")))

        Card(modifier = Modifier.padding(16.dp).widthIn(max = 600.dp).fillMaxWidth()) {
            Scaffold(
                topBar = {
                    SmallTopAppBar(
                        title = "Library",
                        actions = {
                            OverlayIconCascadingDropdownMenu(entries = entries) {
                                Icon(
                                    imageVector = COUIIcons.Tune,
                                    tint = COUITheme.colorScheme.onBackground,
                                    contentDescription = "Adjust",
                                )
                            }
                            OverlayIconCascadingDropdownMenu(
                                entry = emptyEntry,
                                enabled = false,
                            ) {
                                Icon(
                                    imageVector = COUIIcons.MoreCircle,
                                    tint = COUITheme.colorScheme.disabledOnSecondaryVariant,
                                    contentDescription = "More",
                                )
                            }
                        },
                    )
                },
            ) { padding ->
                LazyColumn(
                    contentPadding = PaddingValues(top = padding.calculateTopPadding()),
                ) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(text = "Sort by: ${sortLabels[sortIndex]}")
                            Text(text = "View mode: ${viewLabels[viewIndex]}")
                            Text(text = "Filter: ${filterLabels[filterIndex]}")
                        }
                    }
                }
            }
        }
    }
}
