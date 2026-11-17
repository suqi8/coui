// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The COUI responsive layout grid (`com.coui.component.responsiveui.layoutgrid.LayoutGridSystem`).
 *
 * Window size classes follow `WindowTotalSizeClass.fromWidthAndHeight`:
 * - width < 600dp: compact, 4 columns, 16dp (small) / 24dp (large) margin, 8dp gutter;
 * - 600dp <= width < 840dp: medium, 8 columns, 24dp margin, 8dp gutter;
 * - width >= 840dp: expanded, 12 columns, 40dp margin, 12dp gutter, except a height under 480dp
 *   (`ExpandedLandPortrait`) or a portrait window narrower than 960dp with a height of at least
 *   900dp (`ExpandedPortrait`), which keep the medium 8-column grid.
 *
 * Every function takes the width the grid is laid over (`ResponsiveUIModel` is built from the
 * measured width of the view that uses it, not necessarily the window) and the window height.
 * The margin type defaults to [MarginType.Small], the type the existing callers use.
 *
 * It also carries the fixed page spacings COUI layouts share ([CardMarginHorizontal],
 * [DefaultMarginHorizontal], [ColumnGap], ...).
 */
object COUILayoutGrid {

    /**
     * The horizontal margin of card lists and grouped preferences: 16dp (COUI
     * coui_preference_card_margin_horizontal, grid_guide_column_card_margin_start / _end).
     */
    val CardMarginHorizontal = 16.dp

    /** The default horizontal page margin: 24dp (COUI grid_guide_column_default_margin_start / _end). */
    val DefaultMarginHorizontal = 24.dp

    /** The gap between grid guide columns: 8dp (COUI grid_guide_column_gap). */
    val ColumnGap = 8.dp

    /** The extra padding of card grids: 16dp (COUI grid_card_special_padding). */
    val CardSpecialPadding = 16.dp

    /** The extra padding of list grids: 24dp (COUI grid_list_special_padding). */
    val ListSpecialPadding = 24.dp

    /** The small responsive margin: 16dp (COUI layout_grid_margin_small, responsive_ui_margin_small). */
    val MarginSmall = 16.dp

    /** The large responsive margin: 24dp (COUI layout_grid_margin_large, responsive_ui_margin_large). */
    val MarginLarge = 24.dp

    /** The compact / medium gutter: 8dp (COUI layout_grid_gutter). */
    val Gutter = 8.dp

    /** The expanded gutter: 12dp (COUI layout_grid_gutter_expanded). */
    val GutterExpanded = 12.dp

    /**
     * The width spanned by [columns] adjacent columns centred in the grid, like
     * `ResponsiveUIModel.calculateGridWidth`: requests larger than the column count span the
     * whole grid (the window minus both margins).
     */
    fun gridWidth(
        columns: Int,
        windowWidth: Dp,
        windowHeight: Dp,
        marginType: MarginType = MarginType.Small,
    ): Dp {
        val spec = spec(windowWidth, windowHeight)
        return spanWidth(columns.coerceIn(1, spec.columnCount), windowWidth, windowHeight, marginType)
    }

    /**
     * The width of [span] adjacent columns, `LayoutGridSystem.width(from, from + span - 1)`:
     * `span` column widths plus `span - 1` gutters. [span] is clamped to the column count.
     */
    fun spanWidth(
        span: Int,
        windowWidth: Dp,
        windowHeight: Dp,
        marginType: MarginType = MarginType.Small,
    ): Dp {
        val spec = spec(windowWidth, windowHeight)
        val columns = span.coerceIn(0, spec.columnCount)
        if (columns == 0) return 0.dp
        return columnWidth(windowWidth, windowHeight, marginType) * columns + spec.gutter * (columns - 1)
    }

    /**
     * The width of one column, as `AccumulationCalculator` splits the window minus both margins
     * and the gutters evenly over the columns.
     */
    fun columnWidth(windowWidth: Dp, windowHeight: Dp, marginType: MarginType = MarginType.Small): Dp {
        val spec = spec(windowWidth, windowHeight)
        val gutters = spec.gutter * (spec.columnCount - 1)
        val available = windowWidth - spec.margin(marginType) * 2 - gutters
        if (available < 0.dp) return 0.dp
        return available / spec.columnCount
    }

    /** The column count of the grid: 4, 8 or 12 (`LayoutGridSystem.calculateLayoutGrid`). */
    fun columnCount(windowWidth: Dp, windowHeight: Dp): Int = spec(windowWidth, windowHeight).columnCount

    /** The gutter: `layout_grid_gutter_expanded` (12dp) for expanded grids, else `layout_grid_gutter` (8dp). */
    fun gutter(windowWidth: Dp, windowHeight: Dp): Dp = spec(windowWidth, windowHeight).gutter

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
     * The grid margin of a window: 16dp, 24dp or 40dp by [screenTier] for [MarginType.Small]
     * (`layout_grid_margin_*_small`), 24dp, 24dp or 40dp for [MarginType.Large]
     * (`layout_grid_margin_*_large`), as `LayoutGridSystem.rebuild` picks them.
     */
    fun margin(windowWidth: Dp, windowHeight: Dp, marginType: MarginType = MarginType.Small): Dp = spec(windowWidth, windowHeight).margin(marginType)

    /**
     * `COUIResponsiveUtils.getDefaultGridNumbers`: the number of columns a percent-width layout
     * spans when it asks for the automatic grid number (-1): 4 on compact, 6 on the medium
     * classes (including `ExpandedPortrait` / `ExpandedLandPortrait`), 8 on expanded.
     */
    fun defaultGridNumbers(windowWidth: Dp, windowHeight: Dp): Int = when (screenTier(windowWidth, windowHeight)) {
        ScreenTier.Small -> 4
        ScreenTier.Medium -> 6
        ScreenTier.Large -> 8
    }

    /** The COUI screen tiers (`COUIResponsiveUtils.isSmallScreen` / `isMediumScreen` / `isLargeScreen`). */
    enum class ScreenTier { Small, Medium, Large }

    /** `com.coui.component.responsiveui.layoutgrid.MarginType`. */
    enum class MarginType { Small, Large }

    private fun spec(windowWidth: Dp, windowHeight: Dp): GridSpec = when (screenTier(windowWidth, windowHeight)) {
        ScreenTier.Small -> Compact
        ScreenTier.Medium -> Medium
        ScreenTier.Large -> Expanded
    }

    private class GridSpec(val columnCount: Int, val smallMargin: Dp, val largeMargin: Dp, val gutter: Dp) {
        fun margin(type: MarginType): Dp = if (type == MarginType.Small) smallMargin else largeMargin
    }

    // layout_grid_margin_compat_* / medium_* / expanded_* (small, large), layout_grid_gutter(_expanded).
    private val Compact = GridSpec(columnCount = 4, smallMargin = 16.dp, largeMargin = 24.dp, gutter = 8.dp)
    private val Medium = GridSpec(columnCount = 8, smallMargin = 24.dp, largeMargin = 24.dp, gutter = 8.dp)
    private val Expanded = GridSpec(columnCount = 12, smallMargin = 40.dp, largeMargin = 40.dp, gutter = 12.dp)
}
