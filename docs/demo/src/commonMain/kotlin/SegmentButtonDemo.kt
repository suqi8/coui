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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.SegmentButton
import io.github.suqi8.coui.kmp.basic.SegmentButtonDefaults

@Composable
fun SegmentButtonDemo() {
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
            var wrapIndex by remember { mutableIntStateOf(0) }
            SegmentButton(
                items = listOf("Day", "Week", "Month"),
                selectedIndex = wrapIndex,
                onSelectedIndexChange = { wrapIndex = it },
            )
            var fillIndex by remember { mutableIntStateOf(1) }
            SegmentButton(
                items = listOf("All", "Allowed", "Not allowed"),
                selectedIndex = fillIndex,
                onSelectedIndexChange = { fillIndex = it },
                modifier = Modifier.fillMaxWidth(),
                materialEffect = true,
            )
            var smallIndex by remember { mutableIntStateOf(0) }
            SegmentButton(
                items = listOf("List", "Grid"),
                selectedIndex = smallIndex,
                onSelectedIndexChange = { smallIndex = it },
                height = SegmentButtonDefaults.HeightSmall,
                padding = SegmentButtonDefaults.PaddingSmall,
            )
            SegmentButton(
                items = listOf("On", "Off"),
                selectedIndex = 0,
                onSelectedIndexChange = {},
                enabled = false,
            )
        }
    }
}
