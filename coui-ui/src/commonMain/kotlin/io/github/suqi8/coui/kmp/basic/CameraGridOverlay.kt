// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * The composition guides of the camera viewfinder ("Grid and guides"), the port of the camera's
 * `GuideLineView`.
 *
 * The lines are hairlines of 50% white (0.66dp) edged on both sides by 0.33dp of 30% black so they
 * read on any preview; where lines cross, the outlines never cover a line. The overlay fades in and
 * out over 310ms when [visible] changes; switching [type] redraws at once.
 *
 * Given the [levelState] of a [CameraLevelIndicator] over the same area, the lines leave a 4.33dp
 * gap where the level line crosses them: always for [CameraGridType.Diagonal] and
 * [CameraGridType.Symmetry], only once level for [CameraGridType.Grid] (where the grown level line
 * reaches the vertical lines), never for [CameraGridType.GoldenSpiral].
 *
 * The overlay draws over its own bounds; give it the viewfinder area, for example with
 * `Modifier.matchParentSize()`.
 *
 * @param type Which guides to draw, see [CameraGridType].
 * @param modifier The modifier to be applied to the overlay; it must give it a size.
 * @param visible Whether the guides are shown; changes fade over 310ms.
 * @param levelState The state of a [CameraLevelIndicator] over the same area, or null.
 * @param colors The colors of the guides, see [CameraGridOverlayDefaults.cameraGridOverlayColors].
 */
@Composable
fun CameraGridOverlay(
    type: CameraGridType,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    levelState: CameraLevelState? = null,
    colors: CameraGridOverlayColors = CameraGridOverlayDefaults.cameraGridOverlayColors(),
) {
    val fade = remember { Animatable(0f) }
    LaunchedEffect(visible) {
        fade.animateTo(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(CameraGridOverlayDefaults.FadeDurationMillis, easing = GridFadeEasing),
        )
    }
    Spacer(
        modifier = modifier
            .graphicsLayer {
                alpha = fade.value
                // The camera draws into its own bitmap: the clear pass must only punch this layer.
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithCache {
                val lineWidth = CameraGridOverlayDefaults.LineWidth.toPx() - GRID_SHAPE_THICKNESS
                val outline = CameraGridOverlayDefaults.OutlineWidth.toPx()
                val lines = cameraGridPath(type, size.width, size.height, inset = lineWidth + outline)
                val outerStroke = Stroke(width = lineWidth + 2f * outline)
                val innerStroke = Stroke(width = lineWidth)
                onDrawBehind {
                    val cut = if (type == CameraGridType.GoldenSpiral) {
                        null
                    } else {
                        levelState?.gridCutout(size, this, cutWhileTilted = type != CameraGridType.Grid)
                    }
                    val path = if (cut != null) Path.combine(PathOperation.Difference, lines, cut) else lines
                    drawPath(path, colors.outlineColor, style = outerStroke)
                    drawPath(path, Color.Black, style = innerStroke, blendMode = BlendMode.Clear)
                    drawPath(path, colors.lineColor, style = innerStroke)
                }
            },
    )
}

/**
 * The guide types of [CameraGridOverlay], the line types of `GuideLineView`.
 */
enum class CameraGridType {
    /** Thirds: two horizontal and two vertical lines (`grid`, "Grid"). */
    Grid,

    /**
     * The diagonal from the top-right to the bottom-left corner with a perpendicular from each
     * side through its thirds (`triangle`, "Diagonal lines").
     */
    Diagonal,

    /**
     * The golden spiral: nine quarter-circle arcs in a 34:55 golden rectangle centered in the frame,
     * with the sides of the four inner squares (`spiral`, "Golden spiral").
     */
    GoldenSpiral,

    /** A centered cross through the whole frame (`symmetry`). */
    Symmetry,
}

/**
 * Default values of [CameraGridOverlay], from the camera's guide line resources.
 */
object CameraGridOverlayDefaults {
    /** The line width (`guideline_line_width`). */
    val LineWidth = 0.66.dp

    /** The dark outline on each side of a line (`outer_stroke_width`). */
    val OutlineWidth = 0.33.dp

    /** The fade when the guides are shown or hidden (`GuideLineView.a`, 310ms). */
    val FadeDurationMillis = 310

    /**
     * The fixed light-on-dark palette of the guides: 50% white lines (`guide_line_view_line_paint_color`
     * #80FFFFFF) with a 30% black outline (`outer_stroke_color` #4D000000).
     */
    @Composable
    fun cameraGridOverlayColors(
        lineColor: Color = Color(0x80FFFFFF),
        outlineColor: Color = Color(0x4D000000),
    ): CameraGridOverlayColors = remember(lineColor, outlineColor) {
        CameraGridOverlayColors(lineColor = lineColor, outlineColor = outlineColor)
    }
}

/**
 * The colors of a [CameraGridOverlay].
 *
 * @param lineColor The guide lines.
 * @param outlineColor The thin outline on both sides of the lines.
 */
@Immutable
data class CameraGridOverlayColors(
    val lineColor: Color,
    val outlineColor: Color,
)

/**
 * The guide geometry of `GuideLineView`: every line is a 0.2px-thin closed shape (so the level gap
 * can be cut out of it with a path operation) that the strokes then widen.
 */
private fun cameraGridPath(type: CameraGridType, width: Float, height: Float, inset: Float): Path {
    val path = Path()
    if (width <= 0f || height <= 0f) return path
    when (type) {
        CameraGridType.Grid -> {
            val x1 = (width / 3f).roundToInt().toFloat()
            val x2 = (width * 2f / 3f).roundToInt().toFloat()
            val y1 = (height / 3f).roundToInt().toFloat()
            val y2 = (height * 2f / 3f).roundToInt().toFloat()
            path.addGridSegment(0f, y1, width, y1)
            path.addGridSegment(0f, y2, width, y2)
            path.addGridSegment(x1, 0f, x1, height)
            path.addGridSegment(x2, 0f, x2, height)
        }

        CameraGridType.Symmetry -> {
            val x = (width / 2f).roundToInt().toFloat()
            val y = (height / 2f).roundToInt().toFloat()
            path.addGridSegment(0f, y, width, y)
            path.addGridSegment(x, 0f, x, height)
        }

        CameraGridType.Diagonal -> addGridDiagonal(path, width, height)

        CameraGridType.GoldenSpiral -> addGridSpiral(path, width, height, inset)
    }
    return path
}

/** `GuideLineView.f`: the anti-diagonal and the perpendiculars through its thirds. */
private fun addGridDiagonal(path: Path, width: Float, height: Float) {
    path.moveTo(width, 0f)
    path.lineTo(0f, height)
    path.lineTo(GRID_SHAPE_THICKNESS, height)
    path.lineTo(width + GRID_SHAPE_THICKNESS, 0f)
    path.close()
    // From the left side to the point at a third of the width on the diagonal.
    val x1 = width / 3f
    val y1 = height * 2f / 3f
    val left = x1.pow(2) / (y1 - height) + y1
    path.moveTo(0f, left)
    path.lineTo(x1, y1)
    path.lineTo(x1 + GRID_SHAPE_THICKNESS, y1)
    path.lineTo(GRID_SHAPE_THICKNESS, left)
    path.close()
    // From the right side to the point at two thirds of the width.
    val x2 = width * 2f / 3f
    val y2 = height / 3f
    val right = (width - x2).pow(2) / y2 + y2
    path.moveTo(width, right)
    path.lineTo(x2, y2)
    path.lineTo(x2 + GRID_SHAPE_THICKNESS, y2)
    path.lineTo(width + GRID_SHAPE_THICKNESS, right)
    path.close()
}

/**
 * `GuideLineView.i`: nine quarter arcs with Fibonacci radii (34, 21, 13, … of 34 parts of the
 * rectangle's short side), each ending where the previous one starts, turning a quarter each time.
 * The four inner squares (arcs 1..4) draw their two radii, and the innermost one its bottom side.
 */
private fun addGridSpiral(path: Path, width: Float, height: Float, inset: Float) {
    val origin = inset.toInt().toFloat()
    val w = width - 2f * inset
    val h = height - 2f * inset
    if (w <= 0f || h <= 0f) return
    val side: Float
    val offsetX: Float
    val offsetY: Float
    if (w / h >= GRID_SPIRAL_RATIO) {
        side = h * GRID_SPIRAL_RATIO
        offsetX = (w - side) * 0.5f
        offsetY = 0f
    } else {
        side = w
        offsetX = 0f
        offsetY = (h - w / GRID_SPIRAL_RATIO) * 0.5f
    }
    var angle = 0
    var x = origin + offsetX
    var y = origin + h - offsetY
    for (i in 0 until GRID_SPIRAL_ARCS) {
        val radius = GRID_FIBONACCI[GRID_SPIRAL_ARCS - 1 - i] * side / GRID_SPIRAL_UNITS
        // The arc ends at (x, y); its start and oval follow from the quarter it covers.
        val startX: Float
        val startY: Float
        val oval: Rect
        when (angle) {
            0 -> {
                startX = x + radius
                startY = y - radius
                oval = Rect(x - radius, y - 2f * radius, x + radius, y)
            }

            90 -> {
                startX = x + radius
                startY = y + radius
                oval = Rect(x, y - radius, x + 2f * radius, y + radius)
            }

            180 -> {
                startX = x - radius
                startY = y + radius
                oval = Rect(x - radius, y, x + radius, y + 2f * radius)
            }

            else -> {
                startX = x - radius
                startY = y - radius
                oval = Rect(x - 2f * radius, y - radius, x, y + radius)
            }
        }
        path.addArc(oval, angle.toFloat(), GRID_QUARTER)
        when (angle) {
            0 -> path.relativeLineTo(0f, GRID_SHAPE_THICKNESS)
            90 -> path.relativeLineTo(-GRID_SHAPE_THICKNESS, 0f)
            180 -> path.relativeLineTo(0f, -GRID_SHAPE_THICKNESS)
            else -> path.relativeLineTo(GRID_SHAPE_THICKNESS, 0f)
        }
        path.arcTo(oval.inflate(GRID_SHAPE_THICKNESS), angle + GRID_QUARTER, -GRID_QUARTER, false)
        path.close()
        if (i in 1..4) {
            val center = oval.center
            path.addGridSegment(center.x, center.y, x, y)
            path.addGridSegment(center.x, center.y, startX, startY)
        }
        if (i == 4) path.addGridSegment(x, y, startX, y)
        x = startX
        y = startY
        angle = (angle + 270) % 360
    }
}

/** An axis-aligned line from (x0, y0) to (x1, y1) as a 0.2px-thin rectangle. */
private fun Path.addGridSegment(x0: Float, y0: Float, x1: Float, y1: Float) {
    val left = min(x0, x1)
    val top = min(y0, y1)
    val right = max(x0, x1)
    val bottom = max(y0, y1)
    if (top == bottom) {
        addRect(Rect(left, top, right, top + GRID_SHAPE_THICKNESS))
    } else {
        addRect(Rect(left, top, left + GRID_SHAPE_THICKNESS, bottom))
    }
}

/** The default interpolator of an Android `Animation` (AccelerateDecelerate). */
private val GridFadeEasing = Easing { (cos((it + 1f) * PI.toFloat()) / 2f) + 0.5f }

/** The thickness of the line shapes the strokes widen (`+ 0.2f` throughout `GuideLineView`). */
private const val GRID_SHAPE_THICKNESS = 0.2f

private const val GRID_QUARTER = 90f

/** The spiral's arcs: `GuideLineView.c.a`, the first nine Fibonacci numbers. */
private const val GRID_SPIRAL_ARCS = 9
private val GRID_FIBONACCI = floatArrayOf(1f, 1f, 2f, 3f, 5f, 8f, 13f, 21f, 34f)

/** The short side of the golden rectangle in Fibonacci parts (1 + 1 + 3 + 8 + 21). */
private const val GRID_SPIRAL_UNITS = 34f

/** The golden rectangle's width over its height: 34 / 55 (1 + 1 + 3 + 8 + 21 over 1 + 2 + 5 + 13 + 34). */
private const val GRID_SPIRAL_RATIO = 34f / 55f
