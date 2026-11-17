// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.squircle

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import io.github.suqi8.coui.kmp.squircle.internal.SoftCapsule
import io.github.suqi8.coui.kmp.squircle.internal.SuperellipseCorner
import kotlin.math.min

/**
 * Shared default constants for every squircle API in this module.
 *
 * A squircle corner here is the ColorOS smooth round rect corner (Oplus
 * `Path.addSmoothRoundRect(rect, radii, dir, weight)`): a Lamé superellipse quarter
 * `|x|^n + |y|^n = r^n` with exponent `n = weight`, tangent to both straight sides at the corner
 * radius `r`. `weight = 2` is a plain circular arc; larger weights square the corner off.
 *
 * Weights below 2 only shape a fully rounded rect (four equal radii of at least half the shorter
 * side): the outline then blends from the circular capsule towards the Oplus soft capsule, whose
 * ends join the straight sides through a smooth maximum instead of a tangent arc, reaching the soft
 * capsule alone at [CapsuleWeight]. Any other rect draws a weight below 2 as 2, as
 * `OplusPathSmooth.addSmoothRoundRect` does.
 */
object SquircleDefaults {

    /**
     * The default corner weight (superellipse exponent): 2.5, ColorOS 17 `weight_16_1_normal`, the
     * weight of card lists and input cards.
     */
    val Weight = 2.5f

    /**
     * Inclusive lower bound for the weight: 1, the soft capsule. Weights below 2 apply to fully
     * rounded rects only (see [SquircleDefaults]).
     */
    val WeightMin = 1f

    /**
     * The weight of a ColorOS full-round capsule: 1.0, the Oplus soft capsule
     * (`OplusSmoothRoundedManager.FULL_RRECT_G2_WEIGHT`, `RoundCornerUtil.OS_16_1_FULL_ROUND_SMOOTH_WEIGHT`),
     * the weight every fully rounded `addSmoothRoundRect` call without an explicit weight receives.
     */
    val CapsuleWeight = 1f

    /**
     * The weight of a smooth round rect that is not fully rounded and is given no weight: 2.45, the
     * fallback of the `persist.sys.oplus.default_g2_weight` system property
     * (`OplusSmoothRoundedManager.DEFAULT_G2_WEIGHT`, 245 / 100).
     */
    val DefaultG2Weight = 2.45f

    /**
     * Marks a weight as unspecified, the way ColorOS calls `addSmoothRoundRect` / `setSmoothRoundRect`
     * without a weight argument: it resolves per rect to [CapsuleWeight] when the rect is fully
     * rounded (four equal radii of at least half the shorter side) and to [DefaultG2Weight] otherwise.
     * Pass it wherever COUI leaves the weight out, e.g. a capsule whose radius may be customised.
     */
    val UnspecifiedWeight = 0f

    /** Inclusive upper bound for the weight. */
    val WeightMax = 8f
}

/**
 * Appends a squircle-shaped rounded rectangle path. Use this for path-based effects that can't
 * ride the shader pipeline (e.g. `clipPath` reveals rebuilt per frame); for static fills/clips
 * prefer the modifier APIs.
 *
 * Each corner is a superellipse quarter with exponent [weight] (see [SquircleDefaults]), built from
 * a handful of cubic Bézier segments that stay within a hundredth of a percent of the radius. A
 * weight below 2 on a fully rounded rect ([cornerRadius] at least half the smaller side) appends
 * the Oplus soft capsule contour instead, fitted to the native distance function.
 *
 * Pass `squircleEnabled = false` (typically forwarded from [isSquircleEnabled]) to append a
 * plain rounded rectangle of the same dimensions instead — useful when the surrounding visuals
 * use the shader-backed modifiers' fallback path.
 *
 * @param width The width of the rectangle in pixels; nothing is appended when not positive.
 * @param height The height of the rectangle in pixels; nothing is appended when not positive.
 * @param cornerRadius The corner radius in pixels, clamped to half the smaller side.
 * @param weight The superellipse exponent of the corners, clamped to
 *   [SquircleDefaults.WeightMin]..[SquircleDefaults.WeightMax]; below 2 it selects the soft capsule
 *   blend on a fully rounded rect and is raised to 2 otherwise. [SquircleDefaults.UnspecifiedWeight]
 *   resolves to the soft capsule or [SquircleDefaults.DefaultG2Weight].
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
    appendSquircleRect(0f, 0f, width, height, cornerRadius, cornerRadius, cornerRadius, cornerRadius, weight, squircleEnabled)
}

/**
 * Variant of [addSquircleRect] that appends the rectangle at [rect] instead of at the origin, for
 * shapes drawn inside a larger canvas (tracks, indicators, inset capsules).
 *
 * @param rect The bounds of the rectangle in pixels; nothing is appended when empty.
 * @param cornerRadius The corner radius in pixels, clamped to half the smaller side.
 * @param weight The superellipse exponent of the corners; see [addSquircleRect].
 * @param squircleEnabled When `false`, appends a plain rounded rectangle instead of the squircle
 *   silhouette; typically forwarded from [isSquircleEnabled].
 */
fun Path.addSquircleRect(
    rect: Rect,
    cornerRadius: Float,
    weight: Float = SquircleDefaults.Weight,
    squircleEnabled: Boolean = true,
) {
    appendSquircleRect(
        rect.left,
        rect.top,
        rect.width,
        rect.height,
        cornerRadius,
        cornerRadius,
        cornerRadius,
        cornerRadius,
        weight,
        squircleEnabled,
    )
}

/**
 * Per-corner variant of [addSquircleRect], in physical corner order (clockwise from the top left,
 * never flipped by the layout direction). Like the Oplus G2 shader (`getNormalizedRadius`), each
 * radius is clamped to half the smaller side on its own and every corner shares the exponent
 * [weight]; a corner with a zero radius stays square. A weight below 2 only selects the soft
 * capsule when all four radii are equal and fully round the rect.
 *
 * @param rect The bounds of the rectangle in pixels; nothing is appended when empty.
 * @param topLeft The radius of the top-left corner in pixels.
 * @param topRight The radius of the top-right corner in pixels.
 * @param bottomRight The radius of the bottom-right corner in pixels.
 * @param bottomLeft The radius of the bottom-left corner in pixels.
 * @param weight The superellipse exponent of the corners; see [addSquircleRect].
 * @param squircleEnabled When `false`, appends a plain rounded rectangle with the same radii.
 */
fun Path.addSquircleRect(
    rect: Rect,
    topLeft: Float,
    topRight: Float,
    bottomRight: Float,
    bottomLeft: Float,
    weight: Float = SquircleDefaults.Weight,
    squircleEnabled: Boolean = true,
) {
    appendSquircleRect(rect.left, rect.top, rect.width, rect.height, topLeft, topRight, bottomRight, bottomLeft, weight, squircleEnabled)
}

private fun Path.appendSquircleRect(
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    topLeft: Float,
    topRight: Float,
    bottomRight: Float,
    bottomLeft: Float,
    weight: Float,
    squircleEnabled: Boolean,
) {
    if (!(width > 0f) || !(height > 0f)) return
    val right = left + width
    val bottom = top + height
    val rTL = SuperellipseCorner.effectiveRadius(topLeft, width, height)
    val rTR = SuperellipseCorner.effectiveRadius(topRight, width, height)
    val rBR = SuperellipseCorner.effectiveRadius(bottomRight, width, height)
    val rBL = SuperellipseCorner.effectiveRadius(bottomLeft, width, height)
    if (rTL <= 0f && rTR <= 0f && rBR <= 0f && rBL <= 0f) {
        addRect(Rect(left, top, right, bottom))
        return
    }
    if (!squircleEnabled) {
        addRoundRect(
            RoundRect(
                left = left,
                top = top,
                right = right,
                bottom = bottom,
                topLeftCornerRadius = CornerRadius(rTL, rTL),
                topRightCornerRadius = CornerRadius(rTR, rTR),
                bottomRightCornerRadius = CornerRadius(rBR, rBR),
                bottomLeftCornerRadius = CornerRadius(rBL, rBL),
            ),
        )
        return
    }
    val fullyRounded = SoftCapsule.isFullyRounded(topLeft, topRight, bottomRight, bottomLeft, width, height)
    val resolvedWeight = SuperellipseCorner.resolveWeight(weight, fullyRounded)
    if (resolvedWeight < 2f && fullyRounded && min(width, height) * 0.5f >= SoftCapsule.MIN_RADIUS_PX) {
        SoftCapsule.appendContour(this, left, top, width, height, SoftCapsule.capsuleMix(resolvedWeight))
        return
    }
    val quarter = SuperellipseCorner.unitQuarter(resolvedWeight)
    // Clockwise from the top edge. Each corner maps the unit quarter's (a, b) onto
    // center + r * (a * v + b * u), running from its first tangent point (b = 1) to its second (a = 1).
    moveTo(left + rTL, top)
    lineTo(right - rTR, top)
    if (rTR > 0f) appendCorner(quarter, cx = right - rTR, cy = top + rTR, ux = 0f, uy = -1f, vx = 1f, vy = 0f, r = rTR)
    lineTo(right, bottom - rBR)
    if (rBR > 0f) appendCorner(quarter, cx = right - rBR, cy = bottom - rBR, ux = 1f, uy = 0f, vx = 0f, vy = 1f, r = rBR)
    lineTo(left + rBL, bottom)
    if (rBL > 0f) appendCorner(quarter, cx = left + rBL, cy = bottom - rBL, ux = 0f, uy = 1f, vx = -1f, vy = 0f, r = rBL)
    lineTo(left, top + rTL)
    if (rTL > 0f) appendCorner(quarter, cx = left + rTL, cy = top + rTL, ux = -1f, uy = 0f, vx = 0f, vy = -1f, r = rTL)
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
