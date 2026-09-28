// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalScrollBarApi::class)

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.COUIScrollBehavior
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.InputField
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.SearchBar
import io.github.suqi8.coui.kmp.basic.TopAppBar
import io.github.suqi8.coui.kmp.basic.TopAppBarDefaults
import io.github.suqi8.coui.kmp.basic.TopAppBarSearchSlot
import io.github.suqi8.coui.kmp.basic.VerticalScrollBar
import io.github.suqi8.coui.kmp.basic.rememberScrollBarAdapter
import io.github.suqi8.coui.kmp.blur.TopAppBarGlow
import io.github.suqi8.coui.kmp.blur.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Back
import io.github.suqi8.coui.kmp.interfaces.ExperimentalScrollBarApi
import io.github.suqi8.coui.kmp.theme.COUITheme
import utils.pageContentPadding
import utils.pageScrollModifiers

/**
 * The COUI 17 settings home page top bar, assembled from the migrated library pieces:
 * a 116dp collapsing large title at 40sp, a capsule search slot that folds away with the title,
 * and the condensed-light glow revealed as the bar collapses.
 */
@Composable
fun SettingsHomeTopBarPage(
    padding: PaddingValues,
) {
    val appState = LocalAppState.current
    val isWideScreen = LocalIsWideScreen.current
    val topAppBarScrollBehavior = COUIScrollBehavior()
    val lazyListState = rememberLazyListState()
    var query by rememberSaveable { mutableStateOf("") }

    val navigator = LocalNavigator.current
    val surfaceColor = COUITheme.colorScheme.surface
    val backdrop = rememberLayerBackdrop {
        drawRect(surfaceColor)
        drawContent()
    }
    val isDark = COUITheme.colorScheme.background.let { it.red * 0.299f + it.green * 0.587f + it.blue * 0.114f < 0.5f }
    val glowEnabled = isRuntimeShaderSupported() && appState.enableBlur
    // COUI 17 material button panel: light #FFF0F0F0, dark #FF333333.
    val buttonPanelColor = TopAppBarDefaults.buttonPanelColor()

    Scaffold(
        topBar = {
            Box {
                // Condensed-light glow sits BEHIND the bar as its frosted backing (it must not
                // cover the title / search / buttons). The bar itself is transparent so the glow
                // shows through where the bar has no content.
                TopAppBarGlow(
                    backdrop = backdrop,
                    collapseFraction = topAppBarScrollBehavior.state.collapsedFraction,
                    isDark = isDark,
                    baseColor = surfaceColor,
                    enabled = glowEnabled,
                )
                TopAppBar(
                    title = "Settings",
                    color = if (glowEnabled) Color.Transparent else surfaceColor,
                    scrollBehavior = topAppBarScrollBehavior,
                    expandedHeight = 116.dp,
                    largeTitleFontSize = 40.sp,
                    navigationIcon = {
                        IconButton(
                            onClick = { navigator.pop() },
                            backgroundColor = buttonPanelColor,
                        ) {
                            Icon(
                                imageVector = COUIIcons.Back,
                                contentDescription = null,
                                tint = COUITheme.colorScheme.onSurface,
                            )
                        }
                    },
                    bottomContent = {
                        TopAppBarSearchSlot(
                            inputField = {
                                SearchBar(
                                    inputField = {
                                        InputField(
                                            query = query,
                                            onQueryChange = { query = it },
                                            onSearch = {},
                                            expanded = false,
                                            onExpandedChange = {},
                                            label = "Search settings",
                                        )
                                    },
                                    expanded = false,
                                    onExpandedChange = {},
                                ) {}
                            },
                        )
                    },
                )
            }
        },
    ) { innerPadding ->
        val contentPadding = pageContentPadding(innerPadding, padding, isWideScreen)
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .layerBackdrop(backdrop)
                    .pageScrollModifiers(
                        appState.enableScrollEndHaptic,
                        appState.showTopAppBar,
                        topAppBarScrollBehavior,
                    ),
                contentPadding = contentPadding,
            ) {
                items(40, key = { it }) { index ->
                    BasicComponent(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        title = "Settings entry ${index + 1}",
                    )
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
            VerticalScrollBar(
                adapter = rememberScrollBarAdapter(lazyListState),
                modifier = Modifier.align(Alignment.CenterEnd),
                trackPadding = contentPadding,
            )
        }
    }
}
