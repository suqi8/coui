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
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.NavigationBar
import io.github.suqi8.coui.kmp.basic.NavigationBarItem
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Home
import io.github.suqi8.coui.kmp.icon.extended.More
import io.github.suqi8.coui.kmp.icon.extended.Music
import io.github.suqi8.coui.kmp.icon.extended.Photos
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.overlay.OverlayNavigationPopupMenu

@Composable
fun NavigationPopupMenuDemo() {
    var selected by remember { mutableIntStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }
    val destinations = remember { listOf("Home", "Photos", "Music", "Settings") }
    val icons = remember { listOf(COUIIcons.Home, COUIIcons.Photos, COUIIcons.Music, COUIIcons.Settings) }
    val menuItems = destinations.indices.drop(2).map { index ->
        DropdownItem(
            text = destinations[index],
            selected = selected == index,
            onClick = { selected = index },
            icon = { iconModifier -> Icon(imageVector = icons[index], contentDescription = null, modifier = iconModifier) },
        )
    }
    Scaffold(
        bottomBar = {
            // The Box around the navigation bar is the menu's anchor.
            Box {
                NavigationBar {
                    NavigationBarItem(
                        selected = selected == 0,
                        onClick = { selected = 0 },
                        icon = icons[0],
                        label = destinations[0],
                    )
                    NavigationBarItem(
                        selected = selected == 1,
                        onClick = { selected = 1 },
                        icon = icons[1],
                        label = destinations[1],
                    )
                    NavigationBarItem(
                        selected = selected >= 2,
                        onClick = { showMenu = true },
                        icon = COUIIcons.More,
                        label = "More",
                    )
                }
                OverlayNavigationPopupMenu(
                    show = showMenu,
                    items = menuItems,
                    onDismissRequest = { showMenu = false },
                )
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(demoBackground())
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "Current: ${destinations[selected]}. Tap More for the other destinations.")
        }
    }
}
