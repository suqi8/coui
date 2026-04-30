// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.theme.COUITheme
import com.suqi8.coui.kmp.utils.LocalDialogStates
import com.suqi8.coui.kmp.utils.LocalPopupStates
import com.suqi8.coui.kmp.utils.MiuixPopupUtils
import com.suqi8.coui.kmp.utils.MiuixPopupUtils.Companion.MiuixPopupHost

@Composable
fun AdaptiveNavigationScaffold(
    isWideScreen: Boolean,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    navigationBar: @Composable () -> Unit = {},
    navigationRail: @Composable () -> Unit = {},
    actionBar: @Composable () -> Unit = {},
    popupHost: @Composable () -> Unit = { MiuixPopupHost() },
    contentWindowInsets: WindowInsets = WindowInsets.systemBars.union(WindowInsets.displayCutout),
    content: @Composable (PaddingValues) -> Unit,
) {
    val safeInsets = remember(contentWindowInsets) { MutableWindowInsets(contentWindowInsets) }
    val dialogStates = remember { mutableStateListOf<MiuixPopupUtils.DialogState>() }
    val popupStates = remember { mutableStateListOf<MiuixPopupUtils.PopupState>() }

    CompositionLocalProvider(
        LocalDialogStates provides dialogStates,
        LocalPopupStates provides popupStates
    ) {
        Surface(
            modifier = modifier.onConsumedWindowInsetsChanged { consumedWindowInsets ->
                safeInsets.insets = contentWindowInsets.exclude(consumedWindowInsets)
            },
            color = COUITheme.colorScheme.background
        ) {
            AdaptiveNavigationScaffoldLayout(
                isWideScreen = isWideScreen,
                topBar = topBar,
                navigationBar = navigationBar,
                navigationRail = navigationRail,
                actionBar = actionBar,
                popup = popupHost,
                contentWindowInsets = safeInsets,
                content = content
            )
        }
    }
}

@Composable
private fun AdaptiveNavigationScaffoldLayout(
    isWideScreen: Boolean,
    topBar: @Composable () -> Unit,
    navigationBar: @Composable () -> Unit,
    navigationRail: @Composable () -> Unit,
    actionBar: @Composable () -> Unit,
    popup: @Composable () -> Unit,
    contentWindowInsets: WindowInsets,
    content: @Composable (PaddingValues) -> Unit,
) {
    val contentPadding = remember {
        object : PaddingValues {
            var paddingHolder by mutableStateOf(PaddingValues(0.dp))

            override fun calculateLeftPadding(layoutDirection: LayoutDirection) =
                paddingHolder.calculateLeftPadding(layoutDirection)

            override fun calculateTopPadding() = paddingHolder.calculateTopPadding()

            override fun calculateRightPadding(layoutDirection: LayoutDirection) =
                paddingHolder.calculateRightPadding(layoutDirection)

            override fun calculateBottomPadding() = paddingHolder.calculateBottomPadding()
        }
    }

    val popupContent: @Composable () -> Unit = remember(popup) { { Box { popup() } } }
    val topBarContent: @Composable () -> Unit = remember(topBar) { { Box { topBar() } } }
    val bottomBarContent: @Composable () -> Unit = remember(navigationBar) { { Box { navigationBar() } } }
    val railContent: @Composable () -> Unit = remember(navigationRail) { { Box { navigationRail() } } }
    val actionBarContent: @Composable () -> Unit = remember(actionBar) { { Box { actionBar() } } }
    val bodyContent: @Composable () -> Unit = remember(content, contentPadding) {
        { Box { content(contentPadding) } }
    }

    SubcomposeLayout { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val insets = contentWindowInsets.asPaddingValues(this)

        val popupPlaceable = subcompose(AdaptiveScaffoldSlot.Popup, popupContent)
            .first()
            .measure(looseConstraints)

        val railPlaceable = subcompose(AdaptiveScaffoldSlot.Rail, railContent)
            .first()
            .measure(looseConstraints)
        val railWidth = if (isWideScreen) railPlaceable.width else 0
        val contentWidth = (layoutWidth - railWidth).coerceAtLeast(0)
        val contentConstraints = looseConstraints.copy(maxWidth = contentWidth)

        val topBarPlaceable = subcompose(AdaptiveScaffoldSlot.TopBar, topBarContent)
            .first()
            .measure(contentConstraints)
        val bottomBarPlaceable = subcompose(AdaptiveScaffoldSlot.BottomBar, bottomBarContent)
            .first()
            .measure(contentConstraints)
        val actionBarPlaceable = subcompose(AdaptiveScaffoldSlot.ActionBar, actionBarContent)
            .first()
            .measure(contentConstraints)

        val topBarHeight = topBarPlaceable.height
        val bottomBarHeight = if (isWideScreen) 0 else bottomBarPlaceable.height
        val actionBarHeight = actionBarPlaceable.height

        contentPadding.paddingHolder = PaddingValues(
            start = insets.calculateStartPadding(layoutDirection),
            top = if (topBarHeight == 0) insets.calculateTopPadding() else topBarHeight.toDp(),
            end = insets.calculateEndPadding(layoutDirection),
            bottom = bottomBarHeight.toDp() + actionBarHeight.toDp()
        )

        val bodyPlaceable = subcompose(AdaptiveScaffoldSlot.Content, bodyContent)
            .first()
            .measure(contentConstraints.copy(maxHeight = layoutHeight))

        layout(layoutWidth, layoutHeight) {
            bodyPlaceable.place(railWidth, 0)
            topBarPlaceable.place(railWidth, 0)

            if (isWideScreen && railPlaceable.width > 0) {
                railPlaceable.place(0, 0)
            }

            if (!isWideScreen && bottomBarHeight > 0) {
                bottomBarPlaceable.place(0, layoutHeight - bottomBarHeight - actionBarHeight)
            }

            if (actionBarHeight > 0) {
                actionBarPlaceable.place(railWidth, layoutHeight - actionBarHeight)
            }

            popupPlaceable.place(0, 0)
        }
    }
}

private enum class AdaptiveScaffoldSlot {
    Popup,
    TopBar,
    Rail,
    BottomBar,
    ActionBar,
    Content,
}
