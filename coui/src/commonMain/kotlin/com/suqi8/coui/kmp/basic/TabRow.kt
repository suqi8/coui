// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.theme.COUITheme

/**
 * A COUI-style [TabRow].
 *
 * @param tabs The text to be displayed in the [TabRow].
 * @param selectedTabIndex The selected tab index of the [TabRow]
 * @param modifier The modifier to be applied to the [TabRow].
 * @param colors The colors of the [TabRow].
 * @param minWidth The minimum width of the tab in [TabRow].
 * @param maxWidth The maximum width of the tab in [TabRow].
 * @param height The height of the [TabRow].
 * @param cornerRadius The round corner radius of the tab in [TabRow].
 * @param onTabSelected The callback when a tab is selected.
 */
@Composable
fun TabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    colors: TabRowColors = TabRowDefaults.tabRowColors(),
    minWidth: Dp = TabRowDefaults.TabRowMinWidth,
    maxWidth: Dp? = TabRowDefaults.TabRowMaxWidth,
    height: Dp = TabRowDefaults.TabRowHeight,
    cornerRadius: Dp = TabRowDefaults.TabRowCornerRadius,
    onTabSelected: ((Int) -> Unit)? = null,
) {
    CouiSegmentedRow(
        items = tabs,
        selectedIndex = selectedTabIndex,
        onSelectionChange = onTabSelected,
        modifier = modifier,
        enabled = onTabSelected != null,
        colors = colors.toSegmentedControlColors(),
        metrics = SegmentedControlMetrics(
            height = height,
            outerPadding = 2.dp,
            horizontalItemPadding = 14.dp,
            minSegmentWidth = minWidth,
            maxSegmentWidth = maxWidth,
            indicatorShadowElevation = 0.dp,
            textStyle = COUITheme.textStyles.headline,
            fillMaxWidth = false,
            sizeToContent = true,
            cornerRadius = cornerRadius,
            indicatorCornerRadius = 1.dp,
            itemCornerRadius = cornerRadius - 2.dp,
            indicatorHeight = 2.dp,
            indicatorAtBottom = true,
            indicatorBackgroundHeight = 2.dp,
            indicatorBackgroundAtBottom = true,
            indicatorBackgroundHorizontalPadding = 24.dp,
            itemHorizontalInset = 2.dp,
        ),
    )
}

/**
 * A COUI-style [TabRowWithContour].
 *
 * @param tabs The text to be displayed in the [TabRow].
 * @param selectedTabIndex The selected tab index of the [TabRow]
 * @param modifier The modifier to be applied to the [TabRow].
 * @param colors The colors of the [TabRow].
 * @param minWidth The minimum width of the tab in [TabRow].
 * @param maxWidth The maximum width of the tab in [TabRow].
 * @param height The height of the [TabRow].
 * @param cornerRadius The round corner radius of the tab in [TabRow].
 * @param onTabSelected The callback when a tab is selected.
 */
@Composable
fun TabRowWithContour(
    tabs: List<String>,
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    colors: TabRowColors = TabRowDefaults.tabRowColors(),
    minWidth: Dp = TabRowDefaults.TabRowWithContourMinWidth,
    maxWidth: Dp? = TabRowDefaults.TabRowWithContourMaxWidth,
    height: Dp = TabRowDefaults.TabRowWithContourHeight,
    cornerRadius: Dp = TabRowDefaults.TabRowWithContourCornerRadius,
    onTabSelected: ((Int) -> Unit)? = null,
) {
    val contourBorder = BorderStroke(1.dp, COUITheme.colorScheme.outline.copy(alpha = 0.16f))
    CouiSegmentedRow(
        items = tabs,
        selectedIndex = selectedTabIndex,
        onSelectionChange = onTabSelected,
        modifier = modifier,
        enabled = onTabSelected != null,
        colors = colors.toSegmentedControlColors(),
        metrics = SegmentedControlMetrics(
            height = height,
            outerPadding = 2.dp,
            horizontalItemPadding = 10.dp,
            minSegmentWidth = minWidth,
            maxSegmentWidth = maxWidth,
            indicatorShadowElevation = 0.dp,
            textStyle = COUITheme.textStyles.body,
            border = contourBorder,
            fillMaxWidth = false,
            sizeToContent = true,
            cornerRadius = cornerRadius,
            indicatorCornerRadius = 1.dp,
            itemCornerRadius = cornerRadius - 2.dp,
            indicatorHeight = 2.dp,
            indicatorAtBottom = true,
            indicatorBackgroundHeight = 2.dp,
            indicatorBackgroundAtBottom = true,
            indicatorBackgroundHorizontalPadding = 24.dp,
            itemHorizontalInset = 2.dp,
        ),
    )
}

object TabRowDefaults {

    /**
     * The default height of the [TabRow].
     */
    val TabRowHeight = 40.dp

    /**
     * The default height of the [TabRowWithContour].
     */
    val TabRowWithContourHeight = 36.dp

    /**
     * The default corner radius of the [TabRow].
     */
    val TabRowCornerRadius = 20.dp

    /**
     * The default corner radius of the [TabRowWithContour].
     */
    val TabRowWithContourCornerRadius = 12.dp

    /**
     * The default minimum width of the [TabRow].
     */
    val TabRowMinWidth = 76.dp

    /**
     * The default minimum width of the [TabRowWithContour].
     */
    val TabRowWithContourMinWidth = 52.dp

    /**
     * The default maximum width of the tab in [TabRow].
     */
    val TabRowMaxWidth: Dp? = null

    /**
     * The default minimum width of the tab in [TabRowWithContour].
     */
    val TabRowWithContourMaxWidth: Dp? = null

    /**
     * The default colors for the [TabRow].
     */
    @Composable
    fun tabRowColors(
        backgroundColor: Color = if (COUITheme.colorScheme.isDark) {
            Color.White.copy(alpha = 0.10f)
        } else {
            Color.Black.copy(alpha = 0.06f)
        },
        contentColor: Color = if (COUITheme.colorScheme.isDark) {
            Color.White.copy(alpha = 0.54f)
        } else {
            Color.Black.copy(alpha = 0.40f)
        },
        selectedBackgroundColor: Color = if (COUITheme.colorScheme.isDark) {
            Color.White.copy(alpha = 0.85f)
        } else {
            Color.Black.copy(alpha = 0.85f)
        },
        indicatorBackgroundColor: Color = if (COUITheme.colorScheme.isDark) {
            Color.White.copy(alpha = 0.10f)
        } else {
            Color.Black.copy(alpha = 0.06f)
        },
        selectedContentColor: Color = if (COUITheme.colorScheme.isDark) {
            Color.White.copy(alpha = 0.85f)
        } else {
            Color.Black.copy(alpha = 0.85f)
        },
        pressedContentColor: Color = if (COUITheme.colorScheme.isDark) {
            Color.White.copy(alpha = 0.70f)
        } else {
            Color.Black.copy(alpha = 0.55f)
        },
        pressedSelectedContentColor: Color = if (COUITheme.colorScheme.isDark) {
            Color.White.copy(alpha = 0.85f)
        } else {
            Color.Black.copy(alpha = 0.85f)
        },
        disabledBackgroundColor: Color = if (COUITheme.colorScheme.isDark) {
            Color.White.copy(alpha = 0.10f)
        } else {
            Color.Black.copy(alpha = 0.06f)
        },
        disabledSelectedBackgroundColor: Color = if (COUITheme.colorScheme.isDark) {
            Color.White.copy(alpha = 0.29f)
        } else {
            Color.Black.copy(alpha = 0.16f)
        },
        disabledIndicatorBackgroundColor: Color = if (COUITheme.colorScheme.isDark) {
            Color.White.copy(alpha = 0.10f)
        } else {
            Color.Black.copy(alpha = 0.06f)
        },
        disabledContentColor: Color = COUITheme.colorScheme.disabledOnSurface,
        disabledSelectedContentColor: Color = COUITheme.colorScheme.disabledOnSurface,
    ): TabRowColors = TabRowColors(
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        selectedBackgroundColor = selectedBackgroundColor,
        indicatorBackgroundColor = indicatorBackgroundColor,
        selectedContentColor = selectedContentColor,
        pressedContentColor = pressedContentColor,
        pressedSelectedContentColor = pressedSelectedContentColor,
        disabledBackgroundColor = disabledBackgroundColor,
        disabledSelectedBackgroundColor = disabledSelectedBackgroundColor,
        disabledIndicatorBackgroundColor = disabledIndicatorBackgroundColor,
        disabledContentColor = disabledContentColor,
        disabledSelectedContentColor = disabledSelectedContentColor,
    )
}

@Immutable
class TabRowColors(
    private val backgroundColor: Color,
    private val contentColor: Color,
    private val selectedBackgroundColor: Color,
    private val indicatorBackgroundColor: Color,
    private val selectedContentColor: Color,
    private val pressedContentColor: Color,
    private val pressedSelectedContentColor: Color,
    private val disabledBackgroundColor: Color,
    private val disabledSelectedBackgroundColor: Color,
    private val disabledIndicatorBackgroundColor: Color,
    private val disabledContentColor: Color,
    private val disabledSelectedContentColor: Color,
) {
    @Stable
    internal fun toSegmentedControlColors(): SegmentedControlColors = SegmentedControlColors(
        containerColor = backgroundColor,
        indicatorColor = selectedBackgroundColor,
        indicatorBackgroundColor = indicatorBackgroundColor,
        contentColor = contentColor,
        selectedContentColor = selectedContentColor,
        pressedContentColor = pressedContentColor,
        pressedSelectedContentColor = pressedSelectedContentColor,
        disabledContainerColor = disabledBackgroundColor,
        disabledIndicatorColor = disabledSelectedBackgroundColor,
        disabledIndicatorBackgroundColor = disabledIndicatorBackgroundColor,
        disabledContentColor = disabledContentColor,
        disabledSelectedContentColor = disabledSelectedContentColor,
    )
}
