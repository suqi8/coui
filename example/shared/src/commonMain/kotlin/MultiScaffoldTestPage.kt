// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
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
import component.BackNavigationIcon
import io.github.suqi8.coui.kmp.basic.COUIScrollBehavior
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.blur.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.scrollEndHaptic
import utils.AdaptiveTopAppBar
import utils.BlurredBar

@Composable
fun MultiScaffoldTestPage(
    padding: PaddingValues,
) {
    val appState = LocalAppState.current
    val isWideScreen = LocalIsWideScreen.current
    val blurSupported = isRuntimeShaderSupported()
    val surfaceColor = COUITheme.colorScheme.surface
    val backdrop = if (blurSupported) {
        rememberLayerBackdrop {
            drawRect(surfaceColor)
            drawContent()
        }
    } else {
        null
    }
    val blurActive = appState.enableBlur && blurSupported
    val barColor = if (blurActive) Color.Transparent else COUITheme.colorScheme.surface
    val topAppBarScrollBehavior = COUIScrollBehavior()
    val navigator = LocalNavigator.current

    Scaffold(
        topBar = {
            BlurredBar(backdrop, blurActive) {
                AdaptiveTopAppBar(
                    title = "Multi-Scaffold Test",
                    showTopAppBar = appState.showTopAppBar,
                    isWideScreen = isWideScreen,
                    scrollBehavior = topAppBarScrollBehavior,
                    color = barColor,
                    navigationIcon = {
                        BackNavigationIcon(
                            onClick = { navigator.pop() },
                        )
                    },
                )
            }
        },
    ) { innerPadding ->
        val dropdownOptions = remember { listOf("A", "B", "C") }
        val topLeftSelected = remember { mutableIntStateOf(0) }
        val topRightSelected = remember { mutableIntStateOf(0) }
        val bottomLeftSelected = remember { mutableIntStateOf(0) }
        val bottomRightSelected = remember { mutableIntStateOf(0) }

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier)
                .then(if (appState.enableScrollEndHaptic) Modifier.scrollEndHaptic() else Modifier)
                .then(if (appState.showTopAppBar) Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection) else Modifier)
                .verticalScroll(scrollState)
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = WindowInsets.displayCutout.asPaddingValues().calculateLeftPadding(LayoutDirection.Ltr),
                    end = WindowInsets.displayCutout.asPaddingValues().calculateRightPadding(LayoutDirection.Ltr),
                    bottom = if (isWideScreen) {
                        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + padding.calculateBottomPadding() + 12.dp
                    } else {
                        padding.calculateBottomPadding() + 12.dp
                    },
                ),
        ) {
            Row(
                modifier = Modifier
                    .weight(1f),
            ) {
                Scaffold(
                    modifier = Modifier
                        .weight(1f)
                        .background(COUITheme.colorScheme.surfaceVariant),
                ) {
                    Column {
                        SmallTitle(text = "Top Left")
                        Card(
                            modifier = Modifier.padding(horizontal = 16.dp),
                        ) {
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
                Scaffold(
                    modifier = Modifier
                        .weight(1f)
                        .background(COUITheme.colorScheme.surfaceVariant),
                ) {
                    Column {
                        SmallTitle(text = "Top Right")
                        Card(
                            modifier = Modifier.padding(horizontal = 16.dp),
                        ) {
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
            Row(
                modifier = Modifier
                    .weight(1f),
            ) {
                Scaffold(
                    modifier = Modifier
                        .weight(1f)
                        .background(COUITheme.colorScheme.surfaceVariant),
                ) {
                    Column {
                        SmallTitle(text = "Bottom Left")
                        Card(
                            modifier = Modifier.padding(horizontal = 16.dp),
                        ) {
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
                Scaffold(
                    modifier = Modifier
                        .weight(1f)
                        .background(COUITheme.colorScheme.surfaceVariant),
                ) {
                    Column {
                        SmallTitle(text = "Bottom Right")
                        Card(
                            modifier = Modifier.padding(horizontal = 16.dp),
                        ) {
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
