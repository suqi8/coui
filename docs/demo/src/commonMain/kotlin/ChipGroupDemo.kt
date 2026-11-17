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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.Chip
import io.github.suqi8.coui.kmp.basic.ChipGroup
import io.github.suqi8.coui.kmp.basic.rememberChipGroupState

@Composable
fun ChipGroupDemo() {
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
            val single = rememberChipGroupState(singleSelection = true, selectionRequired = true, initialSelection = setOf(0))
            val singleLabels = remember { listOf("All", "Photos", "Videos", "Documents") }
            Card(modifier = Modifier.fillMaxWidth()) {
                ChipGroup(
                    modifier = Modifier.padding(16.dp),
                    state = single,
                ) {
                    singleLabels.forEachIndexed { index, label ->
                        Chip(
                            selected = single.isSelected(index),
                            onClick = { single.toggle(index) },
                            label = label,
                        )
                    }
                }
            }
            val multi = rememberChipGroupState(initialCollapsed = true)
            val multiLabels = remember {
                listOf(
                    "Wi-Fi", "Bluetooth", "Mobile network", "Battery", "Display", "Sound",
                    "Notifications", "Privacy", "Security", "Location", "Apps", "Storage",
                    "Accessibility", "Wallpaper", "Lock screen", "About phone",
                )
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                ChipGroup(
                    modifier = Modifier.padding(16.dp),
                    state = multi,
                    collapsable = true,
                    collapsedMaxRows = 2,
                ) {
                    multiLabels.forEachIndexed { index, label ->
                        Chip(
                            selected = multi.isSelected(index),
                            onClick = { multi.toggle(index) },
                            label = label,
                        )
                    }
                }
            }
        }
    }
}
