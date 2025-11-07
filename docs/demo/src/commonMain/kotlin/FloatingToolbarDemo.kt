// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.FloatingToolbar
import com.suqi8.coui.kmp.basic.Icon
import com.suqi8.coui.kmp.basic.IconButton
import com.suqi8.coui.kmp.basic.Scaffold
import com.suqi8.coui.kmp.basic.ToolbarPosition
import com.suqi8.coui.kmp.extra.SuperArrow
import com.suqi8.coui.kmp.icon.MiuixIcons
import com.suqi8.coui.kmp.icon.icons.useful.Delete
import com.suqi8.coui.kmp.icon.icons.useful.Edit
import com.suqi8.coui.kmp.theme.COUITheme

@Composable
fun FloatingToolbarDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xfff77062), Color(0xfffe5196)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(0.5f)
                ) {
                    Scaffold(
                        floatingToolbar = {
                            FloatingToolbar {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) { // or Column
                                    IconButton(onClick = { /* Action 1 */ }) {
                                        Icon(
                                            imageVector = MiuixIcons.Useful.Edit,
                                            contentDescription = "Edit",
                                            tint = COUITheme.colorScheme.onBackground
                                        )
                                    }
                                    IconButton(onClick = { /* Action 2 */ }) {
                                        Icon(
                                            imageVector = MiuixIcons.Useful.Delete,
                                            contentDescription = "Delete",
                                            tint = COUITheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }
                    ) { paddingValues ->
                        LazyColumn(
                            contentPadding = PaddingValues(top = paddingValues.calculateTopPadding())
                        ) {
                            items(100) {
                                SuperArrow(
                                    title = "Something"
                                )
                            }
                        }
                    }
                }
                Card(
                    modifier = Modifier.weight(0.5f)
                ) {
                    Scaffold(
                        floatingToolbar = {
                            FloatingToolbar {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) { // or Column
                                    IconButton(onClick = { /* Action 1 */ }) {
                                        Icon(
                                            imageVector =  MiuixIcons.Useful.Edit,
                                            contentDescription = "Edit",
                                            tint = COUITheme.colorScheme.onBackground
                                        )
                                    }
                                    IconButton(onClick = { /* Action 2 */ }) {
                                        Icon(
                                            imageVector = MiuixIcons.Useful.Delete,
                                            contentDescription = "Delete",
                                            tint = COUITheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        },
                        floatingToolbarPosition = ToolbarPosition.BottomEnd
                    ) { paddingValues ->
                        LazyColumn(
                            contentPadding = PaddingValues(top = paddingValues.calculateTopPadding())
                        ) {
                            items(100) {
                                SuperArrow(
                                    title = "Something"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
