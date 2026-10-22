// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBar
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBarItem
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.NavigationBar
import io.github.suqi8.coui.kmp.basic.NavigationBarItem
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Add
import io.github.suqi8.coui.kmp.icon.extended.Contacts
import io.github.suqi8.coui.kmp.icon.extended.Image
import io.github.suqi8.coui.kmp.icon.extended.Music
import io.github.suqi8.coui.kmp.icon.extended.Photos
import io.github.suqi8.coui.kmp.icon.extended.Search
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.theme.COUITheme

fun LazyListScope.navigationBarSection() {
    item(key = "navigationBar") {
        SmallTitle(text = "NavigationBar")
        val icons = remember { listOf(COUIIcons.Photos, COUIIcons.Music, COUIIcons.Add, COUIIcons.Image, COUIIcons.Contacts) }
        val labels = remember { listOf("Photos", "Music", "Create", "Albums", "Me") }

        // COUI 17 floating bar: slide across it to select, add or remove items to see the capsule
        // spring, show the search expansion view, the large "Create" icon and the red dots.
        var floatingSelected by remember { mutableIntStateOf(0) }
        var itemCount by remember { mutableIntStateOf(4) }
        var expansionShown by remember { mutableStateOf(false) }
        var floatingVisible by remember { mutableStateOf(true) }
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            FloatingNavigationBar(
                visible = floatingVisible,
                expansionVisible = expansionShown,
                defaultWindowInsetsPadding = false,
                expansion = {
                    Box(
                        modifier = Modifier.size(56.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        IconButton(
                            onClick = { expansionShown = false },
                            backgroundColor = COUITheme.colorScheme.secondaryContainer,
                            cornerRadius = 28.dp,
                            minWidth = 56.dp,
                            minHeight = 56.dp,
                        ) {
                            Icon(imageVector = COUIIcons.Search, contentDescription = "Search")
                        }
                    }
                },
            ) {
                repeat(itemCount) { index ->
                    FloatingNavigationBarItem(
                        selected = floatingSelected == index,
                        onClick = { floatingSelected = index },
                        icon = icons[index],
                        label = labels[index],
                        largeIcon = index == 2,
                        badge = when (index) {
                            1 -> 0
                            3 -> 8
                            else -> null
                        },
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
                    text = "-",
                    onClick = { if (itemCount > 2) itemCount-- },
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    text = "+",
                    onClick = { if (itemCount < icons.size) itemCount++ },
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    text = "Search",
                    onClick = { expansionShown = !expansionShown },
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    text = if (floatingVisible) "Hide" else "Show",
                    onClick = { floatingVisible = !floatingVisible },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // The classic tab bar: red dots and the COUINavigationView show / hide translation.
        var classicSelected by remember { mutableIntStateOf(0) }
        var classicVisible by remember { mutableStateOf(true) }
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            TextButton(
                text = if (classicVisible) "Hide the tab bar" else "Show the tab bar",
                onClick = { classicVisible = !classicVisible },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            )
            NavigationBar(
                visible = classicVisible,
                defaultWindowInsetsPadding = false,
            ) {
                listOf(COUIIcons.Photos, COUIIcons.Music, COUIIcons.Settings).forEachIndexed { index, icon ->
                    NavigationBarItem(
                        selected = classicSelected == index,
                        onClick = { classicSelected = index },
                        icon = icon,
                        label = listOf("Photos", "Music", "Settings")[index],
                        badge = when (index) {
                            1 -> 0
                            2 -> 3
                            else -> null
                        },
                    )
                }
            }
        }
    }
}
