// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The horizontal geometry a percent-width container resolves on the COUI layout grid.
 *
 * @property contentWidth The width of the content area.
 * @property startPadding The inset of the content area from the start edge.
 * @property endPadding The inset of the content area from the end edge.
 */
@Immutable
data class PercentWidthResult(
    val contentWidth: Dp,
    val startPadding: Dp,
    val endPadding: Dp,
)

/**
 * The content type of a percent-width container (`percentWidthType` / `typeFlag` of the COUI
 * percent-width layouts). List and card-list content widen the grid by a special padding on
 * each side so their rows' own insets line up with the grid columns.
 */
enum class PercentWidthType {
    /** Plain content (`DEFAULT_FLAG`). */
    Default,

    /** A list (`LIST_FLAG`): widened by `grid_list_special_padding` (24dp) per side. */
    List,

    /** A card list (`CARD_LIST_FLAG`): widened by `grid_card_special_padding` (16dp) per side. */
    CardList,
}

/** Which grid margin a percent-width container uses (`paddingSize`). */
enum class GridMarginType {
    /** `MarginType.MARGIN_SMALL`: 16dp / 24dp / 40dp on compact / medium / expanded grids. */
    Small,

    /** `MarginType.MARGIN_LARGE` (the default): 24dp / 24dp / 40dp. */
    Large,
}

/** How a percent-width container applies the grid width (`percentMode`). */
enum class PercentWidthMode {
    /** `PADDING_MODE`: the container keeps the incoming width and insets its content. */
    Padding,

    /** `REMEASURE_MODE`: the container itself is measured at the grid width. */
    Remeasure,
}

/** Default values of the COUI percent-width layouts. */
object PercentWidthDefaults {

    /**
     * Asks for the automatic grid number (`AUTO_GRID_NUMBER`, -1):
     * `COUIResponsiveUtils.getDefaultGridNumbers` spans 4 columns on compact grids, 6 on medium
     * grids and 8 on expanded grids, which is also what the `grid_guide_column_preference`
     * resource the percent-width layouts default to resolves to.
     */
    const val AUTO_GRID_NUMBER: Int = -1

    /** `grid_list_special_padding`. */
    val ListSpecialPadding: Dp = 24.dp

    /** `grid_card_special_padding`. */
    val CardSpecialPadding: Dp = 16.dp

    /**
     * Resolves the content area of a percent-width container `parentWidth` wide, as
     * `COUIResponsiveUtils.measureLayout` does for `COUIPercentWidthLinearLayout`,
     * `COUIPercentWidthFrameLayout` and the other percent-width containers.
     *
     * The grid is built over [parentWidth] and [windowHeight]. The content spans [gridNumber]
     * centred columns (or the automatic count), widened by the special padding of [type], and
     * never exceeds the width minus both margins. A [parentChildHierarchy] container (the child
     * pane of a list-detail split) spans every column on a large window and, for
     * [PercentWidthType.Default] content, simply insets by 24dp (40dp on large windows).
     *
     * @param parentWidth The width the container is measured at.
     * @param windowWidth The window width, used for the large-window check of [parentChildHierarchy].
     * @param windowHeight The window height.
     */
    @Stable
    fun resolve(
        parentWidth: Dp,
        windowWidth: Dp,
        windowHeight: Dp,
        gridNumber: Int = AUTO_GRID_NUMBER,
        type: PercentWidthType = PercentWidthType.Default,
        marginType: GridMarginType = GridMarginType.Large,
        parentChildHierarchy: Boolean = false,
    ): PercentWidthResult {
        val gridMarginType = if (marginType == GridMarginType.Small) {
            COUILayoutGrid.MarginType.Small
        } else {
            COUILayoutGrid.MarginType.Large
        }
        val columnCount = COUILayoutGrid.columnCount(parentWidth, windowHeight)
        val largeWindow = COUILayoutGrid.screenTier(windowWidth, windowHeight) == COUILayoutGrid.ScreenTier.Large
        val requested = if (parentChildHierarchy && largeWindow) columnCount else gridNumber
        val gridMargin = COUILayoutGrid.margin(parentWidth, windowHeight, gridMarginType)
        // getChildLayerDefaultTypeMargin: 40dp on a large grid, 24dp otherwise.
        val margin = if (parentChildHierarchy && type == PercentWidthType.Default) {
            if (COUILayoutGrid.screenTier(parentWidth, windowHeight) == COUILayoutGrid.ScreenTier.Large) 40.dp else 24.dp
        } else {
            gridMargin
        }
        val grid = if (requested == AUTO_GRID_NUMBER) {
            COUILayoutGrid.defaultGridNumbers(parentWidth, windowHeight)
        } else {
            requested.coerceAtMost(columnCount)
        }
        if (grid <= 0) return PercentWidthResult(parentWidth, 0.dp, 0.dp)
        val special = when (type) {
            PercentWidthType.Default -> 0.dp
            PercentWidthType.List -> ListSpecialPadding
            PercentWidthType.CardList -> CardSpecialPadding
        }
        val span = COUILayoutGrid.spanWidth(grid, parentWidth, windowHeight, gridMarginType)
        // calculateWidth: the centred span plus the special padding on both sides.
        var contentWidth = span + special * 2
        // calculatePadding: the grid margin plus the columns (and gutters) left of the span.
        val offsetColumns = (columnCount - grid) / 2
        val columnWidth = COUILayoutGrid.columnWidth(parentWidth, windowHeight, gridMarginType)
        val gutter = COUILayoutGrid.gutter(parentWidth, windowHeight)
        var padding = gridMargin - special + (columnWidth + gutter) * offsetColumns
        val available = parentWidth - margin * 2
        if (contentWidth > available || (parentChildHierarchy && type == PercentWidthType.Default)) {
            contentWidth = available
        }
        if (parentChildHierarchy && type == PercentWidthType.CardList) {
            padding = margin - special
        } else if (parentChildHierarchy && type == PercentWidthType.Default) {
            padding = margin
        } else if (padding * 2 + span > parentWidth) {
            padding = margin - special
        }
        return PercentWidthResult(
            contentWidth = contentWidth.coerceAtLeast(0.dp),
            startPadding = padding.coerceAtLeast(0.dp),
            endPadding = padding.coerceAtLeast(0.dp),
        )
    }
}

/**
 * Sizes this layout on the COUI responsive grid, like the COUI percent-width containers
 * (`COUIPercentWidthLinearLayout`, `COUIPercentWidthFrameLayout`, `COUIPercentWidthRecyclerView`, …)
 * do in `onMeasure` through `COUIResponsiveUtils.measureLayout`.
 *
 * In [PercentWidthMode.Padding] the layout keeps the incoming width and its content is measured
 * at the grid width and placed after the grid start padding; in [PercentWidthMode.Remeasure] the
 * layout itself is the grid width. Without a bounded incoming width it is a no-op.
 *
 * @param gridNumber The number of grid columns the content spans, or
 *   [PercentWidthDefaults.AUTO_GRID_NUMBER] for 4 / 6 / 8 by the grid size.
 * @param type The content type, which adds the list / card-list special padding.
 * @param marginType The grid margin type.
 * @param mode Whether the grid width insets the content or sizes the layout itself.
 * @param parentChildHierarchy Whether the layout is the child pane of a list-detail split.
 * @param enabled `percentIndentEnabled`: whether the grid applies at all.
 */
@Stable
fun Modifier.percentWidth(
    gridNumber: Int = PercentWidthDefaults.AUTO_GRID_NUMBER,
    type: PercentWidthType = PercentWidthType.Default,
    marginType: GridMarginType = GridMarginType.Large,
    mode: PercentWidthMode = PercentWidthMode.Padding,
    parentChildHierarchy: Boolean = false,
    enabled: Boolean = true,
): Modifier = if (enabled) {
    this then PercentWidthElement(gridNumber, type, marginType, mode, parentChildHierarchy)
} else {
    this
}

private data class PercentWidthElement(
    val gridNumber: Int,
    val type: PercentWidthType,
    val marginType: GridMarginType,
    val mode: PercentWidthMode,
    val parentChildHierarchy: Boolean,
) : ModifierNodeElement<PercentWidthNode>() {
    override fun create() = PercentWidthNode(gridNumber, type, marginType, mode, parentChildHierarchy)

    override fun update(node: PercentWidthNode) {
        node.gridNumber = gridNumber
        node.type = type
        node.marginType = marginType
        node.mode = mode
        node.parentChildHierarchy = parentChildHierarchy
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "percentWidth"
        properties["gridNumber"] = gridNumber
        properties["type"] = type
        properties["marginType"] = marginType
        properties["mode"] = mode
        properties["parentChildHierarchy"] = parentChildHierarchy
    }
}

private class PercentWidthNode(
    var gridNumber: Int,
    var type: PercentWidthType,
    var marginType: GridMarginType,
    var mode: PercentWidthMode,
    var parentChildHierarchy: Boolean,
) : Modifier.Node(),
    LayoutModifierNode,
    CompositionLocalConsumerModifierNode {

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        if (!constraints.hasBoundedWidth) {
            val placeable = measurable.measure(constraints)
            return layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
        }
        val windowSize = currentValueOf(LocalWindowInfo).containerSize
        val parentWidth = constraints.maxWidth.toDp()
        val windowWidth = if (windowSize.width > 0) windowSize.width.toDp() else parentWidth
        val windowHeight = if (windowSize.height > 0) windowSize.height.toDp() else 0.dp
        val result = PercentWidthDefaults.resolve(
            parentWidth = parentWidth,
            windowWidth = windowWidth,
            windowHeight = windowHeight,
            gridNumber = gridNumber,
            type = type,
            marginType = marginType,
            parentChildHierarchy = parentChildHierarchy,
        )
        val contentWidth = result.contentWidth.roundToPx().coerceIn(0, constraints.maxWidth)
        return when (mode) {
            PercentWidthMode.Remeasure -> {
                val placeable = measurable.measure(constraints.copy(minWidth = contentWidth, maxWidth = contentWidth))
                layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
            }

            PercentWidthMode.Padding -> {
                val start = result.startPadding.roundToPx().coerceIn(0, constraints.maxWidth - contentWidth)
                val placeable = measurable.measure(
                    constraints.copy(minWidth = constraints.minWidth.coerceAtMost(contentWidth), maxWidth = contentWidth),
                )
                val width = constraints.maxWidth
                layout(width, placeable.height) {
                    placeable.placeRelative(start, 0)
                }
            }
        }
    }
}
