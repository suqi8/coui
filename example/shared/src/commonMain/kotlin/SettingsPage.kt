// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

@file:OptIn(ExperimentalScrollBarApi::class)

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.COUIScrollBehavior
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.HorizontalDivider
import com.suqi8.coui.kmp.basic.Scaffold
import com.suqi8.coui.kmp.basic.ScrollBehavior
import com.suqi8.coui.kmp.basic.SmallTitle
import com.suqi8.coui.kmp.basic.VerticalScrollBar
import com.suqi8.coui.kmp.basic.rememberScrollBarAdapter
import com.suqi8.coui.kmp.blur.LayerBackdrop
import com.suqi8.coui.kmp.blur.isRuntimeShaderSupported
import com.suqi8.coui.kmp.blur.layerBackdrop
import com.suqi8.coui.kmp.interfaces.ExperimentalScrollBarApi
import com.suqi8.coui.kmp.preference.ArrowPreference
import com.suqi8.coui.kmp.preference.OverlayDropdownPreference
import com.suqi8.coui.kmp.preference.SwitchPreference
import com.suqi8.coui.kmp.theme.COUITheme
import com.suqi8.coui.kmp.theme.ThemeColorSpec
import com.suqi8.coui.kmp.theme.ThemePaletteStyle
import misc.VersionInfo
import navigation3.Route
import utils.AdaptiveTopAppBar
import utils.BlurredBar
import utils.pageContentPadding
import utils.pageScrollModifiers
import utils.rememberBlurBackdrop

private val NavigationBarDisplayModeOptions = listOf("IconAndText", "IconOnly", "TextOnly", "IconWithSelectedLabel")
private val NavigationRailDisplayModeOptions = listOf("IconAndText", "IconOnly", "TextOnly", "IconWithSelectedLabel")
private val FloatingNavigationBarStyleOptions = listOf("Default", "iOS-like")
private val FloatingNavigationBarPositionOptions = listOf("Center", "Start", "End")
private val FloatingToolbarPositionOptions =
    listOf("TopStart", "CenterStart", "BottomStart", "TopEnd", "CenterEnd", "BottomEnd", "TopCenter", "BottomCenter")
private val FloatingToolbarOrientationOptions = listOf("Horizontal", "Vertical")
private val FabPositionOptions = listOf("Start", "Center", "End", "EndOverlay")
private val ColorModeOptions = listOf("System", "Light", "Dark", "MonetSystem", "MonetLight", "MonetDark")
private val PaletteStyleOptions = ThemePaletteStyle.entries.map { it.name }
private val ColorSpecOptions = ThemeColorSpec.entries.map { it.name }
private val KeyColorOptions = listOf("Default") + ui.KeyColors.map { it.first }

@Composable
fun SettingsPage(
    padding: PaddingValues,
) {
    val appState = LocalAppState.current
    val isWideScreen = LocalIsWideScreen.current
    val backdrop = rememberBlurBackdrop()
    val blurActive = backdrop != null
    val barColor = if (blurActive) Color.Transparent else COUITheme.colorScheme.surface
    val topAppBarScrollBehavior = COUIScrollBehavior()

    Scaffold(
        topBar = {
            BlurredBar(backdrop, blurActive) {
                AdaptiveTopAppBar(
                    title = "Settings",
                    showTopAppBar = appState.showTopAppBar,
                    isWideScreen = isWideScreen,
                    scrollBehavior = topAppBarScrollBehavior,
                    color = barColor,
                    subtitle = "v${VersionInfo.VERSION_NAME} (${VersionInfo.VERSION_CODE})",
                )
            }
        },
    ) { innerPadding ->
        SettingsContent(
            padding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding(),
            ),
            topAppBarScrollBehavior = topAppBarScrollBehavior,
            backdrop = backdrop,
        )
    }
}

@Composable
private fun SettingsContent(
    padding: PaddingValues,
    topAppBarScrollBehavior: ScrollBehavior,
    backdrop: LayerBackdrop?,
) {
    val appState = LocalAppState.current
    val isWideScreen = LocalIsWideScreen.current
    val updateAppState = LocalUpdateAppState.current
    val navigator = LocalNavigator.current
    val lazyListState = rememberLazyListState()

    val contentPadding = pageContentPadding(padding, padding, isWideScreen, extraTop = 12.dp)
    Box(modifier = if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.pageScrollModifiers(
                appState.enableScrollEndHaptic,
                appState.showTopAppBar,
                topAppBarScrollBehavior,
            ),
            contentPadding = contentPadding,
        ) {
            item(key = "settingsUi") {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                ) {
                    SwitchPreference(
                        title = "Show FPS Monitor",
                        checked = appState.showFPSMonitor,
                        onCheckedChange = { updateAppState { state -> state.copy(showFPSMonitor = it) } },
                    )
                    AnimatedVisibility(visible = isRuntimeShaderSupported()) {
                        Column {
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            SwitchPreference(
                                title = "Enable Squircle Shapes",
                                checked = appState.enableSquircle,
                                onCheckedChange = { updateAppState { state -> state.copy(enableSquircle = it) } },
                            )
                        }
                    }
                    AnimatedVisibility(visible = isRuntimeShaderSupported()) {
                        Column {
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            SwitchPreference(
                                title = "Enable Blur Effect",
                                checked = appState.enableBlur,
                                onCheckedChange = { updateAppState { state -> state.copy(enableBlur = it) } },
                            )
                        }
                    }
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    SwitchPreference(
                        title = "Show TopAppBar",
                        checked = appState.showTopAppBar,
                        onCheckedChange = { updateAppState { state -> state.copy(showTopAppBar = it) } },
                    )
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    SwitchPreference(
                        title = if (isWideScreen) "Show NavigationRail" else "Show NavigationBar",
                        checked = appState.showNavigationBar,
                        onCheckedChange = { updateAppState { state -> state.copy(showNavigationBar = it) } },
                    )
                    AnimatedVisibility(visible = appState.showNavigationBar && !isWideScreen && !appState.useFloatingNavigationBar) {
                        Column {
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            OverlayDropdownPreference(
                                title = "NavigationBar Mode",
                                items = NavigationBarDisplayModeOptions,
                                selectedIndex = appState.navigationBarMode,
                                onSelectedIndexChange = { updateAppState { state -> state.copy(navigationBarMode = it) } },
                            )
                        }
                    }
                    AnimatedVisibility(visible = appState.showNavigationBar && isWideScreen) {
                        Column {
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            OverlayDropdownPreference(
                                title = "NavigationRail Mode",
                                items = NavigationRailDisplayModeOptions,
                                selectedIndex = appState.navigationRailMode,
                                onSelectedIndexChange = { updateAppState { state -> state.copy(navigationRailMode = it) } },
                            )
                        }
                    }
                    AnimatedVisibility(visible = appState.showNavigationBar && !isWideScreen) {
                        Column {
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            SwitchPreference(
                                title = "Use FloatingNavigationBar",
                                checked = appState.useFloatingNavigationBar,
                                onCheckedChange = { updateAppState { state -> state.copy(useFloatingNavigationBar = it) } },
                            )
                            AnimatedVisibility(visible = appState.useFloatingNavigationBar) {
                                Column {
                                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                                    OverlayDropdownPreference(
                                        title = "FloatingNavigationBar Style",
                                        items = FloatingNavigationBarStyleOptions,
                                        selectedIndex = appState.floatingNavigationBarStyle,
                                        onSelectedIndexChange = { updateAppState { state -> state.copy(floatingNavigationBarStyle = it) } },
                                    )
                                    AnimatedVisibility(visible = appState.floatingNavigationBarStyle == 0) {
                                        Column {
                                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                                            OverlayDropdownPreference(
                                                title = "FloatingNavigationBar Position",
                                                items = FloatingNavigationBarPositionOptions,
                                                selectedIndex = appState.floatingNavigationBarPosition,
                                                onSelectedIndexChange = { updateAppState { state -> state.copy(floatingNavigationBarPosition = it) } },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    SwitchPreference(
                        title = "Show FloatingToolbar",
                        checked = appState.showFloatingToolbar,
                        onCheckedChange = { updateAppState { state -> state.copy(showFloatingToolbar = it) } },
                    )
                    AnimatedVisibility(visible = appState.showFloatingToolbar) {
                        Column {
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            OverlayDropdownPreference(
                                title = "FloatingToolbar Position",
                                items = FloatingToolbarPositionOptions,
                                selectedIndex = appState.floatingToolbarPosition,
                                onSelectedIndexChange = { updateAppState { state -> state.copy(floatingToolbarPosition = it) } },
                            )
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            OverlayDropdownPreference(
                                title = "FloatingToolbar Orientation",
                                items = FloatingToolbarOrientationOptions,
                                selectedIndex = appState.floatingToolbarOrientation,
                                onSelectedIndexChange = { updateAppState { state -> state.copy(floatingToolbarOrientation = it) } },
                            )
                        }
                    }
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    SwitchPreference(
                        title = "Show FloatingActionButton",
                        checked = appState.showFloatingActionButton,
                        onCheckedChange = { updateAppState { state -> state.copy(showFloatingActionButton = it) } },
                    )
                    AnimatedVisibility(visible = appState.showFloatingActionButton) {
                        Column {
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            OverlayDropdownPreference(
                                title = "FloatingActionButton Position",
                                items = FabPositionOptions,
                                selectedIndex = appState.floatingActionButtonPosition,
                                onSelectedIndexChange = { updateAppState { state -> state.copy(floatingActionButtonPosition = it) } },
                            )
                        }
                    }
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    SwitchPreference(
                        title = "Enable Scroll End Haptic",
                        checked = appState.enableScrollEndHaptic,
                        onCheckedChange = { updateAppState { state -> state.copy(enableScrollEndHaptic = it) } },
                    )
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    SwitchPreference(
                        title = "Enable Page User Scroll",
                        checked = appState.enablePageUserScroll,
                        onCheckedChange = { updateAppState { state -> state.copy(enablePageUserScroll = it) } },
                    )
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    OverlayDropdownPreference(
                        title = "Color Mode",
                        items = ColorModeOptions,
                        selectedIndex = appState.colorMode,
                        onSelectedIndexChange = { updateAppState { state -> state.copy(colorMode = it) } },
                    )
                    AnimatedVisibility(visible = appState.colorMode in 3..5) {
                        Column {
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            OverlayDropdownPreference(
                                title = "Key Color",
                                items = KeyColorOptions,
                                selectedIndex = appState.seedIndex,
                                onSelectedIndexChange = { updateAppState { state -> state.copy(seedIndex = it) } },
                            )
                        }
                    }
                    AnimatedVisibility(visible = appState.colorMode in 3..5 && appState.seedIndex > 0) {
                        Column {
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            OverlayDropdownPreference(
                                title = "Palette Style",
                                items = PaletteStyleOptions,
                                selectedIndex = appState.paletteStyle,
                                onSelectedIndexChange = { updateAppState { state -> state.copy(paletteStyle = it) } },
                            )
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            OverlayDropdownPreference(
                                title = "Color Spec",
                                items = ColorSpecOptions,
                                selectedIndex = appState.colorSpec,
                                onSelectedIndexChange = { updateAppState { state -> state.copy(colorSpec = it) } },
                            )
                        }
                    }
                }
            }
            item(key = "settingsTransition") {
                SmallTitle("Navigation3")
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                ) {
                    SwitchPreference(
                        title = "Enable Corner Clip",
                        summary = "Clip the top scene with rounded corners during transitions",
                        checked = appState.enableCornerClip,
                        onCheckedChange = { updateAppState { state -> state.copy(enableCornerClip = it) } },
                    )
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    SwitchPreference(
                        title = "Enable Dim",
                        summary = "Dim the scene behind during transitions",
                        checked = appState.enableDim,
                        onCheckedChange = { updateAppState { state -> state.copy(enableDim = it) } },
                    )
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    SwitchPreference(
                        title = "Block Input During Transition",
                        summary = "Block touch input on the non-target scene",
                        checked = appState.blockInputDuringTransition,
                        onCheckedChange = { updateAppState { state -> state.copy(blockInputDuringTransition = it) } },
                    )
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    SwitchPreference(
                        title = "Pop Follows Swipe Edge",
                        summary = "Pop animation direction follows the finger swipe edge",
                        checked = appState.popDirectionFollowsSwipeEdge,
                        onCheckedChange = { updateAppState { state -> state.copy(popDirectionFollowsSwipeEdge = it) } },
                    )
                }
            }
            item(key = "settingsAbout") {
                SmallTitle("Other")
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    ArrowPreference(
                        title = "About",
                        summary = "About this example App",
                        onClick = { navigator.push(Route.About) },
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
        VerticalScrollBar(
            adapter = rememberScrollBarAdapter(lazyListState),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            trackPadding = contentPadding,
        )
    }
}
