// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.FloatingToolbar
import io.github.suqi8.coui.kmp.basic.FloatingToolbarGroupArrangement
import io.github.suqi8.coui.kmp.basic.FloatingToolbarItem
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Delete
import io.github.suqi8.coui.kmp.icon.extended.Edit
import io.github.suqi8.coui.kmp.icon.extended.Link
import io.github.suqi8.coui.kmp.icon.extended.Search
import io.github.suqi8.coui.kmp.theme.COUITheme

fun LazyListScope.floatingToolbarSection() {
    item(key = "floatingToolbar") {
        SmallTitle(text = "FloatingToolbar")
        var lastAction by remember { mutableStateOf("-") }
        var itemCount by remember { mutableIntStateOf(3) }
        var packed by remember { mutableStateOf(false) }
        var visible by remember { mutableStateOf(true) }
        val icons = remember { listOf(COUIIcons.Edit, COUIIcons.Delete, COUIIcons.Link, COUIIcons.Search) }
        val names = remember { listOf("Edit", "Delete", "Share", "Search") }
        // The first group grows past what fits so the overflow item appears; the second group is a
        // single title item.
        val items = List(itemCount) { index ->
            FloatingToolbarItem(
                onClick = { lastAction = names[index % names.size] + " ${index + 1}" },
                icon = icons[index % icons.size],
                title = names[index % names.size] + " ${index + 1}",
                showRedDot = index == 1,
                enabled = index != 2,
            )
        } + FloatingToolbarItem(onClick = { lastAction = "Done" }, title = "Done", group = 1)
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            Text(
                text = "Last action: $lastAction",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                color = COUITheme.colorScheme.labelSecondary,
            )
            FloatingToolbar(
                items = items,
                visible = visible,
                defaultWindowInsetsPadding = false,
                groupArrangement = if (packed) FloatingToolbarGroupArrangement.Packed else FloatingToolbarGroupArrangement.Spread,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextButton(
                    text = "Items: $itemCount",
                    onClick = { itemCount = if (itemCount >= 9) 1 else itemCount + 2 },
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    text = if (packed) "Packed" else "Spread",
                    onClick = { packed = !packed },
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    text = if (visible) "Hide" else "Show",
                    onClick = { visible = !visible },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
