// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.utils.COUILayoutGrid
import kotlin.math.max
import kotlin.math.min

/**
 * A responsive container for a tab bar and a search bar (`COUIPercentTabWithSearchView`), laid
 * out on the COUI percent grid of its own width:
 *
 * - **Compact** (narrower than 600dp): the search bar spans the width on top, inset 16dp at the
 *   start and 7dp at the end, and the tab bar spans the width below it.
 * - **Medium**: the tab bar takes the first 4 grid columns plus the grid margin at the start,
 *   vertically centred, and the search bar the same width after a gutter, inset 15dp at the end.
 * - **Expanded**: the tab bar takes 8 grid columns plus the margin, minus a 16dp inset at the start,
 *   and the search bar 4 columns plus the margin after a gutter, inset 31dp at the end.
 *
 * Column widths come from the grid with the large margin type
 * (`COUIResponsiveUtils.calculateWidth(width, n, 0, 0)`), the added margin and gutter from the
 * small one (`ResponsiveUIModel.chooseMargin(MARGIN_SMALL)`); the medium / expanded split also
 * follows the window height, like `COUIResponsiveUtils.isMediumScreen(context, width, height)`.
 * Right to left, the whole arrangement is mirrored.
 *
 * Both slots are measured at the width they are given: pass a [TabRow] (or another tab bar) and a
 * [SearchBar]. COUI drops the search bar's own responsive padding here
 * (`setUseResponsivePadding(false)`) and applies the insets above instead, so give the search bar no
 * horizontal inside margin, e.g. [TabWithSearchLayoutDefaults.SearchBarInsideMargin].
 *
 * @param tabs The tab bar.
 * @param modifier The modifier applied to the layout.
 * @param searchBar The search bar.
 */
@Composable
fun TabWithSearchLayout(
    tabs: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    searchBar: @Composable () -> Unit,
) {
    val windowHeight = LocalWindowInfo.current.containerDpSize.height
    Layout(
        contents = listOf(tabs, searchBar),
        modifier = modifier.fillMaxWidth(),
    ) { (tabMeasurables, searchMeasurables), constraints ->
        val widthPx = constraints.maxWidth
        val width = widthPx.toDp()
        val tier = COUILayoutGrid.screenTier(width, windowHeight)
        val margin = COUILayoutGrid.margin(width, windowHeight, COUILayoutGrid.MarginType.Small).roundToPx()
        val gutter = COUILayoutGrid.gutter(width, windowHeight).roundToPx()
        val columns = { count: Int -> COUILayoutGrid.gridWidth(count, width, windowHeight, COUILayoutGrid.MarginType.Large).roundToPx() }
        // mSearchBarPaddingStart / mSearchBarPaddingEnd by mResponsiveWidthSize.
        val (searchStart, searchEnd) = when (tier) {
            COUILayoutGrid.ScreenTier.Small -> TabWithSearchLayoutDefaults.SearchBarPaddingStartCompact to TabWithSearchLayoutDefaults.SearchBarPaddingEndCompact
            COUILayoutGrid.ScreenTier.Medium -> TabWithSearchLayoutDefaults.SearchBarPaddingStartMedium to TabWithSearchLayoutDefaults.SearchBarPaddingEndMedium
            COUILayoutGrid.ScreenTier.Large -> TabWithSearchLayoutDefaults.SearchBarPaddingStartExpanded to TabWithSearchLayoutDefaults.SearchBarPaddingEndExpanded
        }
        val searchPaddingStart = searchStart.roundToPx()
        val searchPaddingEnd = searchEnd.roundToPx()

        if (tier == COUILayoutGrid.ScreenTier.Small) {
            // measureTabLayout: calculateWidth(width, 4, list padding) spans the compact grid.
            val tabWidth = min(widthPx, columns(4) + TabWithSearchLayoutDefaults.ListSpecialPadding.roundToPx() * 2)
            val searchContentWidth = max(0, widthPx - searchPaddingStart - searchPaddingEnd)
            val search = searchMeasurables.firstOrNull()?.measure(
                Constraints(minWidth = searchContentWidth, maxWidth = searchContentWidth, maxHeight = constraints.maxHeight),
            )
            val tab = tabMeasurables.firstOrNull()?.measure(
                Constraints(minWidth = tabWidth, maxWidth = tabWidth, maxHeight = constraints.maxHeight),
            )
            val searchHeight = search?.height ?: 0
            // layoutSearchBarAndTabLayout: the search bar on top, the tab bar below it.
            layout(widthPx, searchHeight + (tab?.height ?: 0)) {
                search?.placeRelative(searchPaddingStart, 0)
                tab?.placeRelative(0, searchHeight)
            }
        } else {
            val expanded = tier == COUILayoutGrid.ScreenTier.Large
            val tabColumns = if (expanded) 8 else 4
            val largeInset = if (expanded) TabWithSearchLayoutDefaults.ExpandedHorizontalPadding.roundToPx() else 0
            val tabWidth = max(0, min(widthPx, columns(tabColumns) + margin - largeInset))
            val searchWidth = min(widthPx, columns(4) + margin)
            val searchLeft = columns(tabColumns) + margin + gutter
            val searchContentWidth = max(0, searchWidth - searchPaddingStart - searchPaddingEnd)
            val tab = tabMeasurables.firstOrNull()?.measure(
                Constraints(minWidth = tabWidth, maxWidth = tabWidth, maxHeight = constraints.maxHeight),
            )
            val search = searchMeasurables.firstOrNull()?.measure(
                Constraints(minWidth = searchContentWidth, maxWidth = searchContentWidth, maxHeight = constraints.maxHeight),
            )
            val height = max(tab?.height ?: 0, search?.height ?: 0)
            layout(widthPx, height) {
                // The tab bar at the start (16dp in on expanded windows), vertically centred; the
                // search bar after the tab columns, at the top.
                tab?.placeRelative(largeInset, (height - tab.height) / 2)
                search?.placeRelative(searchLeft + searchPaddingStart, 0)
            }
        }
    }
}

/** Contains default values used by [TabWithSearchLayout]. */
object TabWithSearchLayoutDefaults {
    /**
     * The inside margin to give a [SearchBar] in a [TabWithSearchLayout]: no horizontal padding
     * (the layout applies COUI's insets) and the default vertical one.
     */
    val SearchBarInsideMargin = SearchBarDefaults.InsideMargin.copy(width = 0.dp)

    /** The tab bar's start inset on expanded windows (coui_tab_search_horizontal_padding). */
    val ExpandedHorizontalPadding = 16.dp

    /** The extra width the compact tab bar takes on both sides (grid_list_special_padding). */
    val ListSpecialPadding = 24.dp

    /** The search bar's start inset on compact windows (coui_tab_search_bar_padding_start_compat). */
    val SearchBarPaddingStartCompact = 16.dp

    /** The search bar's end inset on compact windows (coui_tab_search_bar_padding_end_compat). */
    val SearchBarPaddingEndCompact = 7.dp

    /** The search bar's start inset on medium windows (coui_tab_search_bar_padding_start_medium). */
    val SearchBarPaddingStartMedium = 0.dp

    /** The search bar's end inset on medium windows (coui_tab_search_bar_padding_end_medium). */
    val SearchBarPaddingEndMedium = 15.dp

    /** The search bar's start inset on expanded windows (coui_tab_search_bar_padding_start_expanded). */
    val SearchBarPaddingStartExpanded = 0.dp

    /** The search bar's end inset on expanded windows (coui_tab_search_bar_padding_end_expanded). */
    val SearchBarPaddingEndExpanded = 31.dp
}
