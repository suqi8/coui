// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.squircle

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.max
import kotlin.math.min

/**
 * Appends the COUI Java fallback smooth rounded rectangle, a port of
 * `com.coui.appcompat.roundRect.COUIShapePath.getRoundRectPath(path, rect, radius, ...)`.
 *
 * ColorOS 17 still draws a few components with this path instead of the Oplus superellipse
 * (`COUIRoundRectUtil` users such as the hint red dot and the recommended preference card, and
 * `COUICodeInputView`). Each corner is three cubic Béziers whose control points are fixed
 * percentages of the radius: the curve leaves the straight side at 128.19 % of the radius, so it
 * reaches further along the sides than a circular arc of the same radius. Once the radius passes
 * half of the shorter half-side, the extension shrinks (to 110.4 % at 90 %) and the first control
 * point grows (up to 87.17 %), and the side lines stop at the middle of each side.
 *
 * The geometry is reproduced literally, including the cases where it degenerates: the radius is
 * not clamped (only the end points of each corner are), so a radius larger than the half-side
 * gives the same overlapping outline as COUI.
 *
 * @param width The width of the rectangle in pixels; nothing is appended when not positive.
 * @param height The height of the rectangle in pixels; nothing is appended when not positive.
 * @param cornerRadius The corner radius in pixels; negative values count as 0.
 * @param left The left edge of the rectangle in pixels.
 * @param top The top edge of the rectangle in pixels.
 * @param topLeft Whether the top-left corner is rounded.
 * @param topRight Whether the top-right corner is rounded.
 * @param bottomRight Whether the bottom-right corner is rounded.
 * @param bottomLeft Whether the bottom-left corner is rounded.
 */
fun Path.addLegacySquircleRect(
    width: Float,
    height: Float,
    cornerRadius: Float,
    left: Float = 0f,
    top: Float = 0f,
    topLeft: Boolean = true,
    topRight: Boolean = true,
    bottomRight: Boolean = true,
    bottomLeft: Boolean = true,
) {
    if (width <= 0f || height <= 0f) return
    val radius = if (cornerRadius < 0f || cornerRadius.isNaN()) 0f else cornerRadius
    val halfWidth = width / 2f
    val halfHeight = height / 2f
    val ratio = radius / min(halfWidth, halfHeight)
    val extensionScale = if (ratio > 0.5f) 1f - min(1f, (ratio - 0.5f) / 0.4f) * EXTENSION_SHRINK else 1f
    val controlScale = if (ratio > 0.6f) 1f + min(1f, (ratio - 0.6f) / 0.3f) * CONTROL_GROWTH else 1f
    val k = radius / 100f
    val extension = k * 128.19f * extensionScale
    val c = k * 83.62f * controlScale
    val a1 = k * 67.45f
    val b1 = k * 4.64f
    val a2 = k * 51.16f
    val b2 = k * 13.36f
    val a3 = k * 34.86f
    val b3 = k * 22.07f
    val right = left + width
    val bottom = top + height

    moveTo(left + halfWidth, top)
    if (topRight) {
        lineTo(max(halfWidth, width - extension) + left, top)
        cubicTo(right - c, top, right - a1, top + b1, right - a2, top + b2)
        cubicTo(right - a3, top + b3, right - b3, top + a3, right - b2, top + a2)
        cubicTo(right - b1, top + a1, right, top + c, right, top + min(halfHeight, extension))
    } else {
        lineTo(right, top)
    }
    if (bottomRight) {
        lineTo(right, top + max(halfHeight, height - extension))
        cubicTo(right, bottom - c, right - b1, bottom - a1, right - b2, bottom - a2)
        cubicTo(right - b3, bottom - a3, right - a3, bottom - b3, right - a2, bottom - b2)
        cubicTo(right - a1, bottom - b1, right - c, bottom, left + max(halfWidth, width - extension), bottom)
    } else {
        lineTo(right, bottom)
    }
    if (bottomLeft) {
        lineTo(min(halfWidth, extension) + left, bottom)
        cubicTo(left + c, bottom, left + a1, bottom - b1, left + a2, bottom - b2)
        cubicTo(left + a3, bottom - b3, left + b3, bottom - a3, left + b2, bottom - a2)
        cubicTo(left + b1, bottom - a1, left, bottom - c, left, top + max(halfHeight, height - extension))
    } else {
        lineTo(left, bottom)
    }
    if (topLeft) {
        lineTo(left, top + min(halfHeight, extension))
        cubicTo(left, top + c, left + b1, top + a1, left + b2, top + a2)
        cubicTo(left + b3, top + a3, left + a3, top + b3, left + a2, top + b2)
        cubicTo(left + a1, top + b1, left + c, top, left + min(halfWidth, extension), top)
    } else {
        lineTo(left, top)
    }
    close()
}

/**
 * A [Shape] whose outline is [addLegacySquircleRect] (COUI `COUIShapePath.getRoundRectPath`) with
 * [cornerRadius] on all four corners. Use it with `Modifier.background`, `Modifier.clip` or
 * `Modifier.border` for the components ColorOS 17 still draws with the Java fallback path; every
 * other squircle in this module follows the Oplus superellipse.
 *
 * @param cornerRadius The corner radius; like COUI it is not clamped to the size.
 */
@Immutable
class LegacySquircleShape(private val cornerRadius: Dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path()
        path.addLegacySquircleRect(size.width, size.height, with(density) { cornerRadius.toPx() })
        return Outline.Generic(path)
    }

    override fun equals(other: Any?): Boolean = other is LegacySquircleShape && other.cornerRadius == cornerRadius

    override fun hashCode(): Int = cornerRadius.hashCode()

    override fun toString(): String = "LegacySquircleShape(cornerRadius=$cornerRadius)"
}

// 1 - 0.86122155: the extension at a radius of 90 % or more of the shorter half-side.
private const val EXTENSION_SHRINK = 0.13877845f

// 1.042454: the first control point at a radius of 90 % or more of the shorter half-side.
private const val CONTROL_GROWTH = 0.042454004f
