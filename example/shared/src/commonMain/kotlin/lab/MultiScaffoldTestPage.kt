// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package lab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import app.LocalAppSettings
import app.LocalNavigator
import io.github.suqi8.coui.kmp.basic.COUIScrollBehavior
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.scrollEndHaptic
import ui.AppPageScaffold

@Composable
fun MultiScaffoldTestPage(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.current
    val haptic = LocalAppSettings.current.enableScrollEndHaptic
    AppPageScaffold(
        title = "Multi-Scaffold Test",
        modifier = modifier,
        onBack = { navigator.pop() },
    ) { scope ->
        val dropdownOptions = remember { listOf("A", "B", "C") }
        val topLeftSelected = remember { mutableIntStateOf(0) }
        val topRightSelected = remember { mutableIntStateOf(0) }
        val bottomLeftSelected = remember { mutableIntStateOf(0) }
        val bottomRightSelected = remember { mutableIntStateOf(0) }

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (haptic) Modifier.scrollEndHaptic() else Modifier)
                .nestedScroll(scope.scrollBehavior.nestedScrollConnection)
                .verticalScroll(scrollState)
                .padding(scope.contentPadding),
        ) {
            Row(
                modifier = Modifier
                    .weight(1f),
            ) {
                Scaffold(
                    modifier = Modifier
                        .weight(1f)
                        .background(COUITheme.colorScheme.card),
                ) {
                    Column {
                        SmallTitle(text = "Top Left")
                        Card(
                            modifier = Modifier.padding(horizontal = 16.dp),
                        ) {
                            CardListGroup {
                                item {
                                    OverlayDropdownPreference(
                                        title = "Dropdown",
                                        items = dropdownOptions,
                                        selectedIndex = topLeftSelected.intValue,
                                        onSelectedIndexChange = { index -> topLeftSelected.intValue = index },
                                        renderInRootScaffold = false,
                                    )
                                }
                            }
                        }
                    }
                }
                Scaffold(
                    modifier = Modifier
                        .weight(1f)
                        .background(COUITheme.colorScheme.card),
                ) {
                    Column {
                        SmallTitle(text = "Top Right")
                        Card(
                            modifier = Modifier.padding(horizontal = 16.dp),
                        ) {
                            CardListGroup {
                                item {
                                    OverlayDropdownPreference(
                                        title = "Dropdown",
                                        items = dropdownOptions,
                                        selectedIndex = topRightSelected.intValue,
                                        onSelectedIndexChange = { index -> topRightSelected.intValue = index },
                                        renderInRootScaffold = false,
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .weight(1f),
            ) {
                Scaffold(
                    modifier = Modifier
                        .weight(1f)
                        .background(COUITheme.colorScheme.card),
                ) {
                    Column {
                        SmallTitle(text = "Bottom Left")
                        Card(
                            modifier = Modifier.padding(horizontal = 16.dp),
                        ) {
                            CardListGroup {
                                item {
                                    OverlayDropdownPreference(
                                        title = "Dropdown",
                                        items = dropdownOptions,
                                        selectedIndex = bottomLeftSelected.intValue,
                                        onSelectedIndexChange = { index -> bottomLeftSelected.intValue = index },
                                        renderInRootScaffold = false,
                                    )
                                }
                            }
                        }
                    }
                }
                Scaffold(
                    modifier = Modifier
                        .weight(1f)
                        .background(COUITheme.colorScheme.card),
                ) {
                    Column {
                        SmallTitle(text = "Bottom Right")
                        Card(
                            modifier = Modifier.padding(horizontal = 16.dp),
                        ) {
                            CardListGroup {
                                item {
                                    OverlayDropdownPreference(
                                        title = "Dropdown",
                                        items = dropdownOptions,
                                        selectedIndex = bottomRightSelected.intValue,
                                        onSelectedIndexChange = { index -> bottomRightSelected.intValue = index },
                                        renderInRootScaffold = false,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
