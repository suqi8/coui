// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The COUI responsive layout grid (`com.coui.component.responsiveui.layoutgrid.LayoutGridSystem`)
 * with the small margin type (`MarginType.MARGIN_SMALL`).
 *
 * Window size classes follow `WindowTotalSizeClass.fromWidthAndHeight`:
 * - width < 600dp: compact, 4 columns, 16dp margin, 8dp gutter;
 * - 600dp <= width < 840dp: medium, 8 columns, 24dp margin, 8dp gutter;
 * - width >= 840dp: expanded, 12 columns, 40dp margin, 12dp gutter, except a height under 480dp
 *   (`ExpandedLandPortrait`) or a portrait window narrower than 960dp with a height of at least
 *   900dp (`ExpandedPortrait`), which keep the medium 8-column grid.
 */
internal object COUILayoutGrid {

    /**
     * The width spanned by [columns] adjacent columns centred in the grid, like
     * `ResponsiveUIModel.calculateGridWidth`: requests larger than the column count span the
     * whole grid (the window minus both margins).
     */
    fun gridWidth(columns: Int, windowWidth: Dp, windowHeight: Dp): Dp {
        val spec = spec(windowWidth, windowHeight)
        val span = columns.coerceIn(1, spec.columnCount)
        val gutters = spec.gutter * (spec.columnCount - 1)
        val available = windowWidth - spec.margin * 2 - gutters
        if (available < 0.dp) return 0.dp
        val columnWidth = available / spec.columnCount
        return columnWidth * span + spec.gutter * (span - 1)
    }

    /**
     * The COUI screen tier of a window, as `COUIResponsiveUtils.isSmallScreen` /
     * `isMediumScreen` / `isLargeScreen(context, width, height)` classify it from
     * `WindowTotalSizeClass.fromWidthAndHeight`: `MediumPortrait`, `MediumLandScape`,
     * `MediumSquare`, `ExpandedPortrait` and `ExpandedLandPortrait` all count as medium.
     */
    fun screenTier(windowWidth: Dp, windowHeight: Dp): ScreenTier = when {
        windowWidth < 600.dp -> ScreenTier.Small
        windowWidth < 840.dp -> ScreenTier.Medium
        windowHeight < 480.dp -> ScreenTier.Medium
        windowHeight < 900.dp -> ScreenTier.Large
        windowHeight > windowWidth && windowWidth < 960.dp -> ScreenTier.Medium
        else -> ScreenTier.Large
    }

    /**
     * The grid margin (`MarginType.MARGIN_SMALL`) of a window: 16dp, 24dp or 40dp by
     * [screenTier], as `LayoutGridSystem.calculateLayoutGrid` picks `layout_grid_margin_*_small`.
     */
    fun margin(windowWidth: Dp, windowHeight: Dp): Dp = spec(windowWidth, windowHeight).margin

    enum class ScreenTier { Small, Medium, Large }

    private fun spec(windowWidth: Dp, windowHeight: Dp): GridSpec = when (screenTier(windowWidth, windowHeight)) {
        ScreenTier.Small -> Compact
        ScreenTier.Medium -> Medium
        ScreenTier.Large -> Expanded
    }

    private class GridSpec(val columnCount: Int, val margin: Dp, val gutter: Dp)

    // layout_grid_margin_compat_small / medium_small / expanded_small, layout_grid_gutter(_expanded).
    private val Compact = GridSpec(columnCount = 4, margin = 16.dp, gutter = 8.dp)
    private val Medium = GridSpec(columnCount = 8, margin = 24.dp, gutter = 8.dp)
    private val Expanded = GridSpec(columnCount = 12, margin = 40.dp, gutter = 12.dp)
}
