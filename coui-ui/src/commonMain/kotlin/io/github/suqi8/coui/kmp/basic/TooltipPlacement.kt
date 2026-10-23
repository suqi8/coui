// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.toPath
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** The side of the anchor a COUI tooltip ended up on. */
internal enum class TooltipSide { Above, Below, Left, Right }

/**
 * The twelve `COUIToolTips` arrow drawables (`ARROW_*`). Each one is the middle arrow
 * (`coui_*_sharp_middle_arrow_up`) or the edge arrow (`coui_*_sharp_edges_arrow_up_right`), mirrored
 * horizontally first when [mirror] is set and then rotated clockwise by [rotation] degrees, exactly as
 * `DrawableUtils.rotateDrawable` builds them.
 */
internal enum class TooltipArrowDirection(val edge: Boolean, val rotation: Int, val mirror: Boolean) {
    UpLeft(edge = true, rotation = 0, mirror = true),
    Up(edge = false, rotation = 0, mirror = false),
    UpRight(edge = true, rotation = 0, mirror = false),
    RightUp(edge = true, rotation = 90, mirror = true),
    Right(edge = false, rotation = 90, mirror = false),
    RightDown(edge = true, rotation = 90, mirror = false),
    DownRight(edge = true, rotation = 180, mirror = true),
    Down(edge = false, rotation = 180, mirror = false),
    DownLeft(edge = true, rotation = 180, mirror = false),
    LeftDown(edge = true, rotation = 270, mirror = true),
    Left(edge = false, rotation = 270, mirror = false),
    LeftUp(edge = true, rotation = 270, mirror = false),
    ;

    /** The size of the up-pointing source vector in dp. */
    private val sourceSize: Size
        get() = if (edge) TooltipArrowGeometry.EdgeSize else TooltipArrowGeometry.MiddleSize

    /** The size of the rotated arrow in pixels. */
    fun size(density: Float): Size {
        val source = sourceSize
        return if (rotation == 90 || rotation == 270) {
            Size(source.height * density, source.width * density)
        } else {
            Size(source.width * density, source.height * density)
        }
    }

    /** The arrow outline in pixels, with its bounding box at [topLeft]. */
    fun path(density: Float, topLeft: Offset): Path {
        val path = addPathNodes(if (edge) TooltipArrowGeometry.EdgePathData else TooltipArrowGeometry.MiddlePathData).toPath()
        val w = sourceSize.width
        val h = sourceSize.height
        // Mirror: x1 = mx * x + mt. Rotation (clockwise, then translated back into the bounding box):
        //   0: (x1, y)  90: (h - y, x1)  180: (w - x1, h - y)  270: (y, w - x1)
        val mx = if (mirror) -1f else 1f
        val mt = if (mirror) w else 0f
        val a: Float
        val b: Float
        val c: Float
        val d: Float
        val tx: Float
        val ty: Float
        when (rotation) {
            90 -> {
                a = 0f
                c = -1f
                tx = h
                b = mx
                d = 0f
                ty = mt
            }

            180 -> {
                a = -mx
                c = 0f
                tx = w - mt
                b = 0f
                d = -1f
                ty = h
            }

            270 -> {
                a = 0f
                c = 1f
                tx = 0f
                b = -mx
                d = 0f
                ty = w - mt
            }

            else -> {
                a = mx
                c = 0f
                tx = mt
                b = 0f
                d = 1f
                ty = 0f
            }
        }
        val matrix = Matrix()
        matrix.values[Matrix.ScaleX] = a * density
        matrix.values[Matrix.SkewX] = c * density
        matrix.values[Matrix.TranslateX] = tx * density + topLeft.x
        matrix.values[Matrix.SkewY] = b * density
        matrix.values[Matrix.ScaleY] = d * density
        matrix.values[Matrix.TranslateY] = ty * density + topLeft.y
        path.transform(matrix)
        return path
    }
}

/** The `COUIToolTips` arrow vectors, in dp. */
internal object TooltipArrowGeometry {
    /** `coui_*_sharp_middle_arrow_up`: 20 x 10.1dp, a 2dp-rounded tip and concave flared base corners. */
    val MiddleSize = Size(20f, 10.1f)
    val MiddlePathData: String =
        "M0,10.1A5.19,5.19 0,0 0,4.5 7.5L8.27,1a2,2 0,0 1,3.46 0L15.5,7.5A5.19,5.19 0,0 0,20 10.1Z"

    /** `coui_*_sharp_edges_arrow_up_right`: 16.27 x 10.29dp, one straight flank with the tip 2dp from it. */
    val EdgeSize = Size(16.27f, 10.29f)
    val EdgePathData: String = "M0,10.29A7.92,7.92 0,0 0,3.35 8.61L13,0.48A2,2 0,0 1,16.27 2v8.29Z"
}

/**
 * The `COUIToolTips` placement metrics in pixels: the transparent window inset around the bubble
 * (`couiToolTipsPadding*`, it holds the arrow and the shadow), the screen margin, the anchor gaps and
 * the arrow placement constants.
 */
@Immutable
internal data class TooltipMetrics(
    val density: Float,
    val insetLeft: Int,
    val insetTop: Int,
    val insetRight: Int,
    val insetBottom: Int,
    /** `coui_tooltips_bubble_horizontal_padding`: 16dp. */
    val screenMargin: Int = (16f * density).roundToInt(),
    /** `detail_floating_arrow_overflow` 5.7dp + `coui_tooltips_vertical_and_horizontal_gap_top` -0.29dp. */
    val overflowAbove: Int = (5.7f * density).roundToInt() + (-0.29f * density).roundToInt(),
    /** `detail_floating_arrow_overflow` 5.7dp + `coui_tooltips_vertical_and_horizontal_gap_bottom` -0.4dp. */
    val overflowBelow: Int = (5.7f * density).roundToInt() + (-0.4f * density).roundToInt(),
    /** `detail_floating_arrow_overflow`: 5.7dp. */
    val overflowSide: Int = (5.7f * density).roundToInt(),
    /** `coui_tooltips_indicator_embed_distance`: 0.33dp of the arrow base overlaps the bubble. */
    val embed: Float = 0.33f * density,
    /** `coui_tooltips_edge_extra_distance`: 2dp. */
    val edgeExtra: Float = 2f * density,
    /** `couiRoundCornerMRadius` 9dp, the corner allowance before the edge arrow takes over. */
    val arrowCornerThreshold: Float = 9f * density,
) {
    companion object {
        /** Builds the metrics for a bubble whose window insets are given in dp. */
        fun of(density: Float, insetLeft: Float, insetTop: Float, insetRight: Float, insetBottom: Float): TooltipMetrics = TooltipMetrics(
            density = density,
            insetLeft = (insetLeft * density).roundToInt(),
            insetTop = (insetTop * density).roundToInt(),
            insetRight = (insetRight * density).roundToInt(),
            insetBottom = (insetBottom * density).roundToInt(),
        )
    }
}

/** The resolved position of a COUI tooltip window, its arrow and its scale pivot. */
@Immutable
internal data class TooltipPlacement(
    val position: IntOffset,
    val side: TooltipSide,
    val arrow: TooltipArrowDirection?,
    val arrowTopLeft: Offset,
    val pivot: TransformOrigin,
)

/** Resolves [TooltipAnchorPosition.Start] / [TooltipAnchorPosition.End] to a physical side. */
internal fun TooltipAnchorPosition.toTooltipSide(layoutDirection: LayoutDirection): TooltipSide = when (this) {
    TooltipAnchorPosition.Below -> TooltipSide.Below
    TooltipAnchorPosition.Left -> TooltipSide.Left
    TooltipAnchorPosition.Right -> TooltipSide.Right
    TooltipAnchorPosition.Start -> if (layoutDirection == LayoutDirection.Ltr) TooltipSide.Left else TooltipSide.Right
    TooltipAnchorPosition.End -> if (layoutDirection == LayoutDirection.Ltr) TooltipSide.Right else TooltipSide.Left
    else -> TooltipSide.Above
}

/**
 * `COUIToolTips.sizePopupWindow`: the widest the tooltip window (bubble plus insets) may be. Above /
 * below it is the style max width plus the insets, capped at the viewport; to the left / right it is
 * further capped by the room on that side, so the bubble keeps the 16dp screen margin.
 */
internal fun tooltipMaxWindowWidth(
    side: TooltipSide,
    anchor: IntRect,
    viewport: IntSize,
    metrics: TooltipMetrics,
    styleMaxWidth: Int,
): Int {
    var maxWidth = styleMaxWidth + metrics.insetLeft + metrics.insetRight
    when (side) {
        TooltipSide.Right -> maxWidth = min(
            viewport.width - anchor.right - metrics.overflowSide + metrics.insetRight - metrics.screenMargin,
            maxWidth,
        )

        TooltipSide.Left -> maxWidth = min(
            anchor.left - metrics.overflowSide - (metrics.screenMargin - metrics.insetLeft),
            maxWidth,
        )

        else -> Unit
    }
    return max(min(viewport.width, maxWidth), 0)
}

/**
 * `COUIToolTips.sizePopupWindow`: a left / right tooltip whose window, centred on the anchor, would
 * reach the viewport bottom is shown above / below the anchor instead (it never flips sideways).
 */
internal fun tooltipSideFallsBackToVertical(
    anchor: IntRect,
    viewport: IntSize,
    metrics: TooltipMetrics,
    windowHeight: Int,
): Boolean {
    val top = anchor.center.y - (windowHeight + metrics.insetTop - metrics.insetBottom) / 2
    return top + windowHeight - metrics.insetBottom >= viewport.height
}

/**
 * `COUIToolTips.refreshCoordinated` for above / below: the preferred side when the window fits, else
 * the other side when it fits, else the side with more room. Returns the side and, when neither side
 * fits, the height the window shrinks to (its content then scrolls).
 */
internal fun tooltipVerticalSide(
    preferred: TooltipSide,
    anchor: IntRect,
    viewport: IntSize,
    windowHeight: Int,
): Pair<TooltipSide, Int?> {
    val spaceAbove = anchor.top
    val spaceBelow = viewport.height - anchor.bottom
    return if (preferred == TooltipSide.Below) {
        when {
            spaceBelow >= windowHeight -> TooltipSide.Below to null
            spaceAbove >= windowHeight -> TooltipSide.Above to null
            spaceAbove > spaceBelow -> TooltipSide.Above to spaceAbove
            else -> TooltipSide.Below to spaceBelow
        }
    } else {
        when {
            spaceAbove >= windowHeight -> TooltipSide.Above to null
            spaceBelow >= windowHeight -> TooltipSide.Below to null
            spaceAbove > spaceBelow -> TooltipSide.Above to spaceAbove
            else -> TooltipSide.Below to spaceBelow
        }
    }
}

/**
 * `COUIToolTips.refreshCoordinated` + `calculatePivot` + `addIndicator`: positions the tooltip window
 * of [windowSize] next to [anchor] inside [viewport] (all in window pixels), places its arrow and its
 * scale pivot.
 *
 * @param requested the requested side; a left / right request has already been turned into a vertical
 *   one by the caller when [tooltipSideFallsBackToVertical] holds.
 * @param offset the extra window offset (`showWithDirection` offsetX / offsetY); x is mirrored in RTL.
 * @param arrowFollowsOffset when false (the COUI default) the arrow is shifted back by [offset] so it
 *   keeps pointing at the anchor.
 */
internal fun calculateTooltipPlacement(
    anchor: IntRect,
    viewport: IntSize,
    windowSize: IntSize,
    requested: TooltipSide,
    metrics: TooltipMetrics,
    showArrow: Boolean,
    offset: IntOffset = IntOffset.Zero,
    arrowFollowsOffset: Boolean = false,
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
): TooltipPlacement {
    val w = windowSize.width
    val h = windowSize.height
    val anchorCenterX = anchor.center.x
    val anchorCenterY = anchor.center.y

    var x: Int
    var y: Int
    val side: TooltipSide
    when (requested) {
        TooltipSide.Above, TooltipSide.Below -> {
            x = min(anchorCenterX - w / 2, viewport.width - ((w - metrics.insetRight) + metrics.screenMargin))
            side = tooltipVerticalSide(requested, anchor, viewport, h).first
            y = if (side == TooltipSide.Above) {
                anchor.top - h - metrics.overflowAbove
            } else {
                anchor.bottom + metrics.overflowBelow
            }
        }

        TooltipSide.Left, TooltipSide.Right -> {
            side = requested
            x = if (side == TooltipSide.Left) anchor.left - w - metrics.overflowSide else anchor.right + metrics.overflowSide
            y = max(anchorCenterY - (h + metrics.insetTop - metrics.insetBottom) / 2, -metrics.insetTop)
        }
    }
    x = max(metrics.screenMargin - metrics.insetLeft, x)
    y = max(0, y)

    // calculatePivot runs before the offset is applied.
    val pivot = when (side) {
        TooltipSide.Above, TooltipSide.Below -> {
            val pivotX = when {
                anchorCenterX - x >= w -> 1f
                w != 0 -> abs(anchorCenterX - x).toFloat() / w
                else -> 0.5f
            }
            TransformOrigin(pivotX, if (side == TooltipSide.Below) 0f else 1f)
        }

        TooltipSide.Left -> TransformOrigin(1f, if (h != 0) (anchorCenterY - y).toFloat() / h else 0.5f)

        TooltipSide.Right -> TransformOrigin(0f, if (h != 0) (anchorCenterY - y).toFloat() / h else 0.5f)
    }

    val rtl = layoutDirection == LayoutDirection.Rtl
    val shiftX = if (rtl) -offset.x else offset.x
    val compensateX = if (arrowFollowsOffset) 0 else -shiftX
    val compensateY = if (arrowFollowsOffset) 0 else -offset.y

    var arrow: TooltipArrowDirection? = null
    var arrowTopLeft = Offset.Zero
    if (showArrow) {
        val density = metrics.density
        val radius = metrics.arrowCornerThreshold
        when (side) {
            TooltipSide.Above, TooltipSide.Below -> {
                val below = side == TooltipSide.Below
                val middle = TooltipArrowDirection.Up.size(density)
                val edge = TooltipArrowDirection.UpRight.size(density)
                val left = (anchorCenterX - x) - (middle.width / 2f).toInt() + compensateX
                val right = w - left - middle.width
                val edgeLeftLimit = metrics.insetLeft + radius + metrics.edgeExtra
                val edgeRightLimit = radius + metrics.insetRight + if (below) metrics.edgeExtra else 0f
                val arrowLeft: Float
                val arrowHeight: Float
                if (left < edgeLeftLimit) {
                    arrow = if (below) TooltipArrowDirection.UpLeft else TooltipArrowDirection.DownLeft
                    arrowLeft = max((radius + metrics.insetRight + metrics.edgeExtra).toInt(), anchorCenterX).toFloat()
                    arrowHeight = edge.height
                } else if (right < edgeRightLimit) {
                    arrow = if (below) TooltipArrowDirection.UpRight else TooltipArrowDirection.DownRight
                    val rightMargin = max((radius + metrics.insetRight + metrics.edgeExtra).toInt(), viewport.width - anchorCenterX)
                    arrowLeft = w - rightMargin - edge.width
                    arrowHeight = edge.height
                } else {
                    arrow = if (below) TooltipArrowDirection.Up else TooltipArrowDirection.Down
                    arrowLeft = left.toFloat()
                    arrowHeight = middle.height
                }
                val arrowTop = if (below) {
                    metrics.insetTop - arrowHeight + metrics.embed
                } else {
                    h - (metrics.insetBottom - arrowHeight + metrics.embed) - arrowHeight
                }
                arrowTopLeft = Offset(arrowLeft, arrowTop)
            }

            TooltipSide.Left, TooltipSide.Right -> {
                val toLeft = side == TooltipSide.Left
                val middle = TooltipArrowDirection.Right.size(density)
                val edge = TooltipArrowDirection.RightUp.size(density)
                val arrowLeft = if (toLeft) {
                    val rightMargin = metrics.insetRight - middle.width + metrics.embed
                    w - rightMargin - middle.width - metrics.embed
                } else {
                    metrics.insetLeft - middle.width + metrics.embed
                }
                var top = (anchorCenterY - y - middle.height / 2f).toInt().toFloat()
                val bottom = h - top - middle.height
                val topLimit = metrics.insetTop + radius
                val bottomLimit = radius + metrics.insetBottom
                if (top < topLimit) {
                    arrow = if (toLeft) TooltipArrowDirection.RightUp else TooltipArrowDirection.LeftUp
                    top = topLimit.toInt().toFloat()
                } else if (bottom < bottomLimit) {
                    arrow = if (toLeft) TooltipArrowDirection.RightDown else TooltipArrowDirection.LeftDown
                    top = h - bottomLimit.toInt() - edge.height
                } else {
                    arrow = if (toLeft) TooltipArrowDirection.Right else TooltipArrowDirection.Left
                }
                arrowTopLeft = Offset(arrowLeft, top + compensateY)
            }
        }
    }

    return TooltipPlacement(
        position = IntOffset(x + shiftX, y + offset.y),
        side = side,
        arrow = arrow,
        arrowTopLeft = arrowTopLeft,
        pivot = pivot,
    )
}
