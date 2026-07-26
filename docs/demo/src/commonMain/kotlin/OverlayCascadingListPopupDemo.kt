// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.DropdownEntry
import com.suqi8.coui.kmp.basic.DropdownItem
import com.suqi8.coui.kmp.basic.PopupPositionProvider
import com.suqi8.coui.kmp.basic.Scaffold
import com.suqi8.coui.kmp.basic.TextButton
import com.suqi8.coui.kmp.overlay.OverlayCascadingListPopup

@Composable
fun OverlayCascadingListPopupDemo() {
    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(demoBackground()),
            contentAlignment = Alignment.Center,
        ) {
            var showPopup by remember { mutableStateOf(false) }
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

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                Box {
                    TextButton(
                        text = "Click to show menu",
                        onClick = { showPopup = true },
                        modifier = Modifier.padding(top = 16.dp),
                    )
                    OverlayCascadingListPopup(
                        show = showPopup,
                        entries = entries,
                        onDismissRequest = { showPopup = false },
                        alignment = PopupPositionProvider.Align.TopStart,
                    )
                }
            }
        }
    }
}
