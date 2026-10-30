// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.ExpandableGroupContent
import io.github.suqi8.coui.kmp.basic.ExpandableGroupHeader
import io.github.suqi8.coui.kmp.basic.SmallTitle

fun LazyListScope.expandableGroupSection() {
    item(key = "expandableGroup") {
        SmallTitle(text = "ExpandableGroup")
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            ExpandableGroupDemo(
                title = "Network",
                summary = "Wi-Fi, mobile network, hotspot",
                children = listOf("Wi-Fi", "Mobile network", "Personal hotspot"),
                initiallyExpanded = true,
            )
            ExpandableGroupDemo(
                title = "More settings",
                summary = null,
                children = listOf("Accessibility", "Date & time", "Language & region", "Backup & reset"),
                initiallyExpanded = false,
            )
            ExpandableGroupHeader(
                title = "Disabled group",
                expanded = false,
                onExpandedChange = {},
                enabled = false,
            )
        }
    }
}

@Composable
private fun ExpandableGroupDemo(
    title: String,
    summary: String?,
    children: List<String>,
    initiallyExpanded: Boolean,
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    ExpandableGroupHeader(
        title = title,
        expanded = expanded,
        onExpandedChange = { expanded = it },
        summary = summary,
    )
    ExpandableGroupContent(expanded = expanded) {
        children.forEach { child ->
            BasicComponent(
                title = child,
                onClick = {},
            )
        }
    }
}
