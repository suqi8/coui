// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.Icon
import com.suqi8.coui.kmp.basic.IconButton
import com.suqi8.coui.kmp.basic.LargeTopAppBar
import com.suqi8.coui.kmp.basic.LargeTopAppBarWithSearch
import com.suqi8.coui.kmp.basic.Scaffold
import com.suqi8.coui.kmp.basic.topAppBarScrollBehavior
import com.suqi8.coui.kmp.extra.SuperArrow
import com.suqi8.coui.kmp.icon.MiuixIcons
import com.suqi8.coui.kmp.icon.icons.useful.Back
import com.suqi8.coui.kmp.icon.icons.useful.More

@Composable
fun TopAppBarDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xfff77062), Color(0xfffe5196)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .widthIn(max = 900.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(0.5f)
                ) {
                    val scrollBehavior = topAppBarScrollBehavior()
                    Scaffold(
                        topBar = {
                            LargeTopAppBar(
                                title = "Title",
                                subtitle = "Pinned subtitle",
                                largeTitle = "Large Title",
                                scrollBehavior = scrollBehavior,
                                navigationIcon = {
                                    DemoBackButton()
                                },
                                actions = {
                                    DemoMoreButton()
                                }
                            )
                        }
                    ) { paddingValues ->
                        DemoTopAppBarList(
                            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                            contentPadding = PaddingValues(top = paddingValues.calculateTopPadding())
                        )
                    }
                }
                Card(
                    modifier = Modifier.weight(0.5f)
                ) {
                    val scrollBehavior = topAppBarScrollBehavior()
                    var query by remember { mutableStateOf("") }
                    var active by remember { mutableStateOf(false) }
                    Scaffold(
                        topBar = {
                            LargeTopAppBarWithSearch(
                                title = "Cities",
                                subtitle = "Recent places",
                                largeTitle = "Add city",
                                query = query,
                                onQueryChange = { query = it },
                                onSearch = { active = false },
                                onCancel = {
                                    active = false
                                    query = ""
                                },
                                active = active,
                                onActiveChange = { active = it },
                                scrollBehavior = scrollBehavior,
                                hintTexts = listOf("City, country, or region"),
                                navigationIcon = {
                                    DemoBackButton()
                                },
                                actions = {
                                    DemoMoreButton()
                                }
                            )
                        }
                    ) { paddingValues ->
                        DemoTopAppBarList(
                            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                            contentPadding = PaddingValues(top = paddingValues.calculateTopPadding())
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoTopAppBarList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        item {
            Spacer(Modifier.height(8.dp))
        }
        items(100) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
            ) {
                SuperArrow(
                    title = "Something"
                )
            }
        }
    }
}

@Composable
private fun DemoBackButton() {
    IconButton(
        onClick = { },
        modifier = Modifier.padding(start = 16.dp)
    ) {
        Icon(
            MiuixIcons.Useful.Back,
            contentDescription = "Back"
        )
    }
}

@Composable
private fun DemoMoreButton() {
    IconButton(
        onClick = { },
        modifier = Modifier.padding(end = 16.dp)
    ) {
        Icon(
            MiuixIcons.Useful.More,
            contentDescription = "More"
        )
    }
}
