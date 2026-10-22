// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.squircle

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import io.github.suqi8.coui.kmp.squircle.internal.SuperellipseCorner
import kotlin.math.max
import kotlin.math.min

/**
 * Shared default constants for every squircle API in this module.
 *
 * A squircle corner here is the ColorOS smooth round rect corner (Oplus
 * `Path.addSmoothRoundRect(rect, radii, dir, weight)`): a Lamé superellipse quarter
 * `|x|^n + |y|^n = r^n` with exponent `n = weight`, tangent to both straight sides at the corner
 * radius `r`. `weight = 2` is a plain circular arc; larger weights square the corner off.
 */
object SquircleDefaults {

    /**
     * The default corner weight (superellipse exponent): 2.5, ColorOS 17 `weight_16_1_normal`, the
     * weight of card lists and input cards.
     */
    val Weight = 2.5f

    /** Inclusive lower bound for the weight: 2, a circular arc. */
    val WeightMin = 2f

    /** Inclusive upper bound for the weight. */
    val WeightMax = 8f

    /** Former corner-tile multiplier of the cubic squircle model; no longer used by any API. */
    @Deprecated("Squircle corners are superellipses tangent at the corner radius; use Weight.")
    val Extension = 1.2819f

    /** Former lower bound for [Extension]; no longer used by any API. */
    @Deprecated("Squircle corners are superellipses tangent at the corner radius; use WeightMin.")
    val ExtensionMin = 1f

    /** Former upper bound for [Extension]; no longer used by any API. */
    @Deprecated("Squircle corners are superellipses tangent at the corner radius; use WeightMax.")
    val ExtensionMax = 2f
}

/**
 * Appends a squircle-shaped rounded rectangle path. Use this for path-based effects that can't
 * ride the shader pipeline (e.g. `clipPath` reveals rebuilt per frame); for static fills/clips
 * prefer the modifier APIs.
 *
 * Each corner is a superellipse quarter with exponent [weight] (see [SquircleDefaults]), built from
 * a handful of cubic Bézier segments that stay within a hundredth of a percent of the radius.
 *
 * Pass `squircleEnabled = false` (typically forwarded from [isSquircleEnabled]) to append a
 * plain rounded rectangle of the same dimensions instead — useful when the surrounding visuals
 * use the shader-backed modifiers' fallback path.
 *
 * @param width The width of the rectangle in pixels; nothing is appended when not positive.
 * @param height The height of the rectangle in pixels; nothing is appended when not positive.
 * @param cornerRadius The corner radius in pixels, clamped to half the smaller side.
 * @param weight The superellipse exponent of the corners, clamped to
 *   [SquircleDefaults.WeightMin]..[SquircleDefaults.WeightMax].
 * @param squircleEnabled When `false`, appends a plain rounded rectangle instead of the squircle
 *   silhouette; typically forwarded from [isSquircleEnabled].
 */
fun Path.addSquircleRect(
    width: Float,
    height: Float,
    cornerRadius: Float,
    weight: Float = SquircleDefaults.Weight,
    squircleEnabled: Boolean = true,
) {
    if (width <= 0f || height <= 0f) return
    if (!squircleEnabled) {
        val radius = max(0f, cornerRadius).coerceAtMost(min(width, height) * 0.5f)
        if (radius <= 0f) {
            addRect(Rect(0f, 0f, width, height))
        } else {
            addRoundRect(
                RoundRect(
                    left = 0f,
                    top = 0f,
                    right = width,
                    bottom = height,
                    cornerRadius = CornerRadius(radius, radius),
                ),
            )
        }
        return
    }
    val r = SuperellipseCorner.effectiveRadius(cornerRadius, width, height)
    if (r <= 0f) {
        addRect(Rect(0f, 0f, width, height))
        return
    }
    val quarter = SuperellipseCorner.unitQuarter(weight)
    // Clockwise from the top edge. Each corner maps the unit quarter's (a, b) onto
    // center + r * (a * v + b * u), running from its first tangent point (b = 1) to its second (a = 1).
    moveTo(r, 0f)
    lineTo(width - r, 0f)
    appendCorner(quarter, cx = width - r, cy = r, ux = 0f, uy = -1f, vx = 1f, vy = 0f, r = r)
    lineTo(width, height - r)
    appendCorner(quarter, cx = width - r, cy = height - r, ux = 1f, uy = 0f, vx = 0f, vy = 1f, r = r)
    lineTo(r, height)
    appendCorner(quarter, cx = r, cy = height - r, ux = 0f, uy = 1f, vx = -1f, vy = 0f, r = r)
    lineTo(0f, r)
    appendCorner(quarter, cx = r, cy = r, ux = -1f, uy = 0f, vx = 0f, vy = -1f, r = r)
    close()
}

private fun Path.appendCorner(
    quarter: FloatArray,
    cx: Float,
    cy: Float,
    ux: Float,
    uy: Float,
    vx: Float,
    vy: Float,
    r: Float,
) {
    fun x(a: Float, b: Float) = cx + r * (a * vx + b * ux)
    fun y(a: Float, b: Float) = cy + r * (a * vy + b * uy)
    var i = 0
    while (i < quarter.size) {
        cubicTo(
            x(quarter[i], quarter[i + 1]),
            y(quarter[i], quarter[i + 1]),
            x(quarter[i + 2], quarter[i + 3]),
            y(quarter[i + 2], quarter[i + 3]),
            x(quarter[i + 4], quarter[i + 5]),
            y(quarter[i + 4], quarter[i + 5]),
        )
        i += 6
    }
}
