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
import io.github.suqi8.coui.kmp.basic.FloatingAccessoryNavigationBar
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBarAccessory
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBarItem
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Add
import io.github.suqi8.coui.kmp.icon.extended.Close
import io.github.suqi8.coui.kmp.icon.extended.Contacts
import io.github.suqi8.coui.kmp.icon.extended.Music
import io.github.suqi8.coui.kmp.icon.extended.Photos
import io.github.suqi8.coui.kmp.icon.extended.Search

fun LazyListScope.floatingAccessoryNavigationBarSection() {
    item(key = "floatingAccessoryNavigationBar") {
        SmallTitle(text = "FloatingAccessoryNavigationBar")
        val icons = remember { listOf(COUIIcons.Photos, COUIIcons.Music, COUIIcons.Contacts) }
        val labels = remember { listOf("Photos", "Music", "Me") }
        var selected by remember { mutableIntStateOf(0) }
        var startShown by remember { mutableStateOf(true) }
        var endShown by remember { mutableStateOf(true) }
        var searching by remember { mutableStateOf(false) }
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            FloatingAccessoryNavigationBar(
                startAccessoryVisible = startShown,
                endAccessoryVisible = endShown,
                defaultWindowInsetsPadding = false,
                startAccessory = {
                    FloatingNavigationBarAccessory(
                        onClick = { selected = 0 },
                        icon = COUIIcons.Add,
                        contentDescription = "Create",
                    )
                },
                endAccessory = {
                    // Clicking swaps the icon with the COUI 17 icon-change animation.
                    FloatingNavigationBarAccessory(
                        onClick = { searching = !searching },
                        icon = if (searching) COUIIcons.Close else COUIIcons.Search,
                        contentDescription = if (searching) "Close" else "Search",
                    )
                },
            ) {
                icons.forEachIndexed { index, icon ->
                    FloatingNavigationBarItem(
                        selected = selected == index,
                        onClick = { selected = index },
                        icon = icon,
                        label = labels[index],
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextButton(
                    text = if (startShown) "Hide start" else "Show start",
                    onClick = { startShown = !startShown },
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    text = if (endShown) "Hide end" else "Show end",
                    onClick = { endShown = !endShown },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
