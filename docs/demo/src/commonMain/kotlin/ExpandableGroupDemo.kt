// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.expandableGroup

@Composable
fun ExpandableGroupDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.TopCenter,
    ) {
        var networkExpanded by remember { mutableStateOf(true) }
        var moreExpanded by remember { mutableStateOf(false) }
        LazyColumn(
            Modifier
                .padding(vertical = 16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
        ) {
            expandableGroup(
                key = "network",
                title = "Network",
                summary = "Wi-Fi, mobile network, hotspot",
                expanded = networkExpanded,
                onExpandedChange = { networkExpanded = it },
            ) {
                listOf("Wi-Fi", "Mobile network", "Personal hotspot").forEach {
                    BasicComponent(title = it, onClick = {})
                }
            }
            expandableGroup(
                key = "more",
                title = "More settings",
                expanded = moreExpanded,
                onExpandedChange = { moreExpanded = it },
            ) {
                listOf("Accessibility", "Date and time", "Language and region", "Backup and reset").forEach {
                    BasicComponent(title = it, onClick = {})
                }
            }
        }
    }
}
