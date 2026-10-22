// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.squircle

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

/**
 * A [Shape] whose outline is [addSquircleRect]: the Oplus smooth round rect with [cornerRadius] on
 * all four corners at [weight]. Use it where a component takes a `Shape` (surfaces, shadows, clips)
 * instead of one of the shader-backed modifiers. The outline is a path, so it is rebuilt only when
 * the size changes; branch on [isSquircleEnabled] to fall back to a `RoundedCornerShape` together
 * with the modifier APIs.
 *
 * @param cornerRadius The corner radius, clamped to half the smaller side.
 * @param weight The superellipse exponent of the corners; see [addSquircleRect].
 */
@Immutable
class SquircleShape(
    private val cornerRadius: Dp,
    private val weight: Float = SquircleDefaults.Weight,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path()
        path.addSquircleRect(size.width, size.height, with(density) { cornerRadius.toPx() }, weight)
        return Outline.Generic(path)
    }

    override fun equals(other: Any?): Boolean = other is SquircleShape && other.cornerRadius == cornerRadius && other.weight == weight

    override fun hashCode(): Int = 31 * cornerRadius.hashCode() + weight.hashCode()

    override fun toString(): String = "SquircleShape(cornerRadius=$cornerRadius, weight=$weight)"
}

/**
 * Fills a squircle rectangle at [topLeft] with [size] in [color], the [addSquircleRect] counterpart
 * of `DrawScope.drawRoundRect` for canvas-drawn components (tracks, indicators, progress fills).
 * The path is built on every call; cache it with `drawWithCache` when the geometry is static.
 *
 * @param color The fill color.
 * @param cornerRadius The corner radius in pixels, clamped to half the smaller side.
 * @param topLeft The top-left corner of the rectangle.
 * @param size The size of the rectangle; defaults to the rest of the draw scope.
 * @param weight The superellipse exponent of the corners; see [addSquircleRect].
 * @param alpha The opacity applied to [color].
 * @param squircleEnabled When `false`, draws a plain rounded rectangle; forward [isSquircleEnabled].
 */
fun DrawScope.drawSquircleRect(
    color: Color,
    cornerRadius: Float,
    topLeft: Offset = Offset.Zero,
    size: Size = Size(this.size.width - topLeft.x, this.size.height - topLeft.y),
    weight: Float = SquircleDefaults.Weight,
    alpha: Float = 1f,
    squircleEnabled: Boolean = true,
) {
    val path = Path()
    path.addSquircleRect(Rect(topLeft, size), cornerRadius, weight, squircleEnabled)
    drawPath(path = path, color = color, alpha = alpha)
}

/**
 * [Brush] variant of [drawSquircleRect].
 *
 * @param brush The fill brush.
 * @param cornerRadius The corner radius in pixels, clamped to half the smaller side.
 * @param topLeft The top-left corner of the rectangle.
 * @param size The size of the rectangle; defaults to the rest of the draw scope.
 * @param weight The superellipse exponent of the corners; see [addSquircleRect].
 * @param alpha The opacity applied to [brush].
 * @param squircleEnabled When `false`, draws a plain rounded rectangle; forward [isSquircleEnabled].
 */
fun DrawScope.drawSquircleRect(
    brush: Brush,
    cornerRadius: Float,
    topLeft: Offset = Offset.Zero,
    size: Size = Size(this.size.width - topLeft.x, this.size.height - topLeft.y),
    weight: Float = SquircleDefaults.Weight,
    alpha: Float = 1f,
    squircleEnabled: Boolean = true,
) {
    val path = Path()
    path.addSquircleRect(Rect(topLeft, size), cornerRadius, weight, squircleEnabled)
    drawPath(path = path, brush = brush, alpha = alpha)
}
