// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import io.github.suqi8.coui.kmp.utils.COUILayoutGrid
import io.github.suqi8.coui.kmp.utils.GridMarginType
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * How a [ResponsiveGrid] picks its column count and child width (`COUIGridLayout.specificType`).
 */
@Immutable
sealed interface ResponsiveGridSpec {

    /**
     * `GRID_MODE`: every child spans [childGridNumber] columns of the COUI layout grid built over
     * the grid's width. The row starts after the grid margin and children are a gutter apart, so
     * the column count is `columnCount / childGridNumber` (4, 8 or 12 grid columns).
     *
     * @property childGridNumber The number of grid columns one child spans, at least 1.
     * @property marginType The grid margin type (`gridMarginType`, small by default).
     */
    @Immutable
    data class Grid(
        val childGridNumber: Int,
        val marginType: GridMarginType = GridMarginType.Small,
    ) : ResponsiveGridSpec

    /**
     * `SPECIFIC_GAP_MODE`: as many columns as fit children at least [childMinWidth] wide with
     * a fixed [horizontalGap]; the children stretch to fill the row. With [childMinHeight] the
     * child height keeps the `childMinHeight / childMinWidth` ratio unless a height is given.
     */
    @Immutable
    data class SpecificGap(
        val childMinWidth: Dp,
        val horizontalGap: Dp,
        val childMinHeight: Dp = Dp.Unspecified,
    ) : ResponsiveGridSpec

    /**
     * `SPECIFIC_SIZE_MODE`: children are exactly [childWidth] wide, as many per row as fit with
     * at least [minHorizontalGap] between them; the leftover width is spread between the columns.
     */
    @Immutable
    data class SpecificSize(
        val childWidth: Dp,
        val minHorizontalGap: Dp,
    ) : ResponsiveGridSpec
}

/**
 * A responsive grid ported from `COUIGridLayout`: its children are placed row by row in a column
 * count derived from its width, either on the COUI layout grid ([ResponsiveGridSpec.Grid]) or
 * from a child size ([ResponsiveGridSpec.SpecificGap], [ResponsiveGridSpec.SpecificSize]).
 *
 * `COUIGridLayout` gives every child the same fixed height; here a row is as tall as its tallest
 * child unless [childHeight] fixes it.
 *
 * @param spec How the column count and child width are derived.
 * @param modifier The modifier to be applied to the grid.
 * @param verticalGap The space between rows (`couiVerticalGap`).
 * @param childHeight A fixed child height (`childHeight`), or [Dp.Unspecified] to wrap.
 * @param content The grid children.
 */
@Composable
fun ResponsiveGrid(
    spec: ResponsiveGridSpec,
    modifier: Modifier = Modifier,
    verticalGap: Dp = ResponsiveGridDefaults.VerticalGap,
    childHeight: Dp = Dp.Unspecified,
    content: @Composable () -> Unit,
) {
    val windowInfo = LocalWindowInfo.current
    val measurePolicy = remember(spec, verticalGap, childHeight, windowInfo) {
        responsiveGridMeasurePolicy(spec, verticalGap, childHeight) { windowInfo.containerSize }
    }
    Layout(content = content, modifier = modifier, measurePolicy = measurePolicy)
}

/** Default values of [ResponsiveGrid]. */
object ResponsiveGridDefaults {
    /** `couiVerticalGap` defaults to 0. */
    val VerticalGap: Dp = 0.dp
}

private fun responsiveGridMeasurePolicy(
    spec: ResponsiveGridSpec,
    verticalGap: Dp,
    childHeight: Dp,
    windowSize: () -> IntSize,
): MeasurePolicy = MeasurePolicy { measurables, constraints ->
    val width = if (constraints.hasBoundedWidth) constraints.maxWidth else constraints.minWidth
    val widthDp = width.toDp()
    var startInset = 0f
    val columns: Int
    val childWidths: FloatArray
    val gap: Float
    var fixedHeight = if (childHeight.isSpecified) childHeight.toPx() else Float.NaN
    when (spec) {
        is ResponsiveGridSpec.Grid -> {
            // calculateInGridMode: the grid is rebuilt over the measured width and the window height.
            val window = windowSize()
            val windowHeight = if (window.height > 0) window.height.toDp() else 0.dp
            val marginType = if (spec.marginType == GridMarginType.Small) {
                COUILayoutGrid.MarginType.Small
            } else {
                COUILayoutGrid.MarginType.Large
            }
            val perChild = spec.childGridNumber.coerceAtLeast(1)
            val columnCount = COUILayoutGrid.columnCount(widthDp, windowHeight)
            columns = max(1, columnCount / perChild)
            startInset = COUILayoutGrid.margin(widthDp, windowHeight, marginType).toPx()
            gap = COUILayoutGrid.gutter(widthDp, windowHeight).toPx()
            val childWidth = COUILayoutGrid.spanWidth(perChild, widthDp, windowHeight, marginType).toPx()
            childWidths = FloatArray(columns) { childWidth }
        }

        is ResponsiveGridSpec.SpecificGap -> {
            // calculateInSpecificGapMode.
            val minWidth = spec.childMinWidth.toPx()
            gap = spec.horizontalGap.toPx()
            columns = max(1, ((width + gap) / (gap + minWidth).coerceAtLeast(1f)).toInt())
            val childWidth = ((width - gap * (columns - 1)) / columns).coerceAtLeast(0f)
            childWidths = FloatArray(columns) { childWidth }
            if (fixedHeight.isNaN() && spec.childMinHeight.isSpecified && minWidth > 0f) {
                // calculateChildHeight: keep the minimum size's aspect ratio.
                fixedHeight = spec.childMinHeight.toPx() / minWidth * childWidth
            }
        }

        is ResponsiveGridSpec.SpecificSize -> {
            // calculateInSpecificSizeMode.
            val childWidth = spec.childWidth.toPx()
            val minGap = spec.minHorizontalGap.toPx()
            columns = max(1, ((width + minGap) / (minGap + childWidth).coerceAtLeast(1f)).toInt())
            gap = if (columns > 1) ((width - childWidth * columns) / (columns - 1)).coerceAtLeast(0f) else 0f
            childWidths = FloatArray(columns) { childWidth }
        }
    }

    val placeables = measurables.mapIndexed { index, measurable ->
        val childWidth = childWidths[index % columns].roundToInt().coerceAtLeast(0)
        val childConstraints = if (fixedHeight.isNaN()) {
            Constraints.fixedWidth(childWidth)
        } else {
            Constraints.fixed(childWidth, fixedHeight.roundToInt().coerceAtLeast(0))
        }
        measurable.measure(childConstraints)
    }
    val rowCount = (placeables.size + columns - 1) / columns
    val rowGap = verticalGap.toPx()
    val rowHeights = IntArray(rowCount) { row ->
        var tallest = 0
        for (i in row * columns until minOf(placeables.size, (row + 1) * columns)) {
            tallest = max(tallest, placeables[i].height)
        }
        tallest
    }
    val contentHeight = rowHeights.sum() + (rowGap * (rowCount - 1).coerceAtLeast(0)).roundToInt()
    val height = contentHeight.coerceIn(constraints.minHeight, constraints.maxHeight)

    layout(width, height) {
        var y = 0f
        for (row in 0 until rowCount) {
            var x = startInset
            for (column in 0 until columns) {
                val index = row * columns + column
                if (index >= placeables.size) break
                placeables[index].placeRelative(x.roundToInt(), y.roundToInt())
                x += childWidths[column] + gap
            }
            y += rowHeights[row] + rowGap
        }
    }
}
