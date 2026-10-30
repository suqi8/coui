// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.ChipTab
import io.github.suqi8.coui.kmp.basic.ChipTabRow
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Search
import io.github.suqi8.coui.kmp.icon.basic.Sidebar

@Composable
fun ChipTabDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(vertical = 16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val tabs = listOf("All", "Photos", "Videos", "Documents", "Music", "Downloads", "Others")
            var selectedIndex1 by remember { mutableIntStateOf(0) }
            ChipTabRow(
                tabs = tabs,
                selectedTabIndex = selectedIndex1,
                onTabSelected = { selectedIndex1 = it },
            )

            var selectedIndex2 by remember { mutableIntStateOf(1) }
            ChipTabRow(
                tabs = tabs,
                selectedTabIndex = selectedIndex2,
                onTabSelected = { selectedIndex2 = it },
                onLeadingIconClick = { selectedIndex2 = 0 },
                leadingIcon = {
                    Icon(
                        imageVector = COUIIcons.Basic.Sidebar,
                        contentDescription = "Categories",
                        modifier = Modifier.size(20.dp),
                    )
                },
            )

            var searchSelected by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ChipTab(
                    selected = searchSelected,
                    onClick = { searchSelected = !searchSelected },
                    icon = {
                        Icon(
                            imageVector = COUIIcons.Basic.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(16.dp),
                        )
                    },
                )
                ChipTab(
                    selected = true,
                    onClick = {},
                    label = "Disabled",
                    enabled = false,
                )
            }
        }
    }
}
