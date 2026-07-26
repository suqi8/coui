// Copyright 2025, compose-coui-ui contributors
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.FloatingActionButton
import com.suqi8.coui.kmp.basic.Icon
import com.suqi8.coui.kmp.basic.NavigationBar
import com.suqi8.coui.kmp.basic.NavigationBarItem
import com.suqi8.coui.kmp.basic.NavigationItem
import com.suqi8.coui.kmp.basic.Scaffold
import com.suqi8.coui.kmp.basic.SmallTopAppBar
import com.suqi8.coui.kmp.basic.Text
import com.suqi8.coui.kmp.icon.COUIIcons
import com.suqi8.coui.kmp.icon.extended.Contacts
import com.suqi8.coui.kmp.icon.extended.Settings
import com.suqi8.coui.kmp.icon.extended.VerticalSplit
import com.suqi8.coui.kmp.theme.COUITheme

@Composable
fun ScaffoldDemo() {
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
            val pages = listOf("Home", "Profile", "Settings")
            val items = listOf(
                NavigationItem("Home", COUIIcons.VerticalSplit),
                NavigationItem("Profile", COUIIcons.Contacts),
                NavigationItem("Settings", COUIIcons.Settings),
            )
            var selectedIndex by remember { mutableIntStateOf(0) }
            Card {
                Scaffold(
                    topBar = {
                        SmallTopAppBar(
                            title = "SmallTopAppBar",
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            items.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    selected = selectedIndex == index,
                                    onClick = { selectedIndex = index },
                                    icon = item.icon,
                                    label = item.label,
                                )
                            }
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                // Handle FAB click
                            },
                        ) {
                            Icon(
                                imageVector = COUIIcons.Contacts,
                                contentDescription = "Personal",
                                tint = Color.White,
                            )
                        }
                    },
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Current: ${pages[selectedIndex]}",
                            style = COUITheme.textStyles.title1,
                        )
                    }
                }
            }
        }
    }
}
