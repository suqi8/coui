// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.squircle.internal

import io.github.suqi8.coui.kmp.squircle.SquircleDefaults
import kotlin.math.pow

/**
 * Geometry of one smooth corner, after Oplus `Path.addSmoothRoundRect(rect, radii, dir, weight)`:
 * a quarter of the Lamé superellipse `|a|^n + |b|^n = 1` (scaled by the corner radius `r`) with
 * `n = weight`, tangent to both straight sides at distance `r` from the corner vertex. `n = 2` is a
 * circular arc; larger `n` keeps the curve on the straight sides longer and squares the corner.
 *
 * The quarter is approximated by [SEGMENTS_PER_HALF] cubic Hermite segments per half. Each half is
 * parametrized by the coordinate that stays smooth there (`a` next to the `b = 1` tangent point,
 * `b` next to the `a = 1` one), so the derivatives stay bounded where `cos^(2/n)` would blow up.
 */
internal object SuperellipseCorner {

    /** Cubic segments per half quarter; 6 keeps the path within 0.1 % of `r` up to n = 8. */
    const val SEGMENTS_PER_HALF = 6

    /** Number of cubic segments in one quarter. */
    const val SEGMENTS = SEGMENTS_PER_HALF * 2

    /** Clamps [weight] into the supported range; NaN falls back to [SquircleDefaults.Weight]. */
    fun clampWeight(weight: Float): Float = if (weight.isNaN()) {
        SquircleDefaults.Weight
    } else {
        weight.coerceIn(SquircleDefaults.WeightMin, SquircleDefaults.WeightMax)
    }

    /**
     * The superellipse exponent drawn for [weight]: the clamped weight, raised to 2 (a circular arc)
     * when below it. Weights below 2 only shape fully rounded rects, through [SoftCapsule].
     */
    fun exponent(weight: Float): Float = maxOf(clampWeight(weight), 2f)

    /**
     * The unit quarter from `(a, b) = (0, 1)` to `(1, 0)` as [SEGMENTS] cubic Béziers. Returns
     * `SEGMENTS * 6` floats: for each segment the two control points and the end point
     * (`c1a, c1b, c2a, c2b, ea, eb`); the start point of segment `i` is the end of segment `i - 1`
     * (`(0, 1)` for the first).
     */
    fun unitQuarter(weight: Float): FloatArray {
        val n = exponent(weight).toDouble()
        val out = FloatArray(SEGMENTS * 6)
        // Where the two halves meet: a == b == 2^(-1/n).
        val mid = 2.0.pow(-1.0 / n)
        val step = mid / SEGMENTS_PER_HALF
        var index = 0
        // First half: parameter a in [0, mid], b = f(a).
        for (i in 0 until SEGMENTS_PER_HALF) {
            val a0 = step * i
            val a1 = step * (i + 1)
            val b0 = lpComplement(a0, n)
            val b1 = lpComplement(a1, n)
            val d0 = lpComplementSlope(a0, n)
            val d1 = lpComplementSlope(a1, n)
            val h = step / 3.0
            out[index++] = (a0 + h).toFloat()
            out[index++] = (b0 + h * d0).toFloat()
            out[index++] = (a1 - h).toFloat()
            out[index++] = (b1 - h * d1).toFloat()
            out[index++] = a1.toFloat()
            out[index++] = b1.toFloat()
        }
        // Second half: parameter b from mid down to 0, a = f(b).
        for (i in 0 until SEGMENTS_PER_HALF) {
            val b0 = step * (SEGMENTS_PER_HALF - i)
            val b1 = step * (SEGMENTS_PER_HALF - i - 1)
            val a0 = lpComplement(b0, n)
            val a1 = lpComplement(b1, n)
            val d0 = lpComplementSlope(b0, n)
            val d1 = lpComplementSlope(b1, n)
            val h = -step / 3.0
            out[index++] = (a0 + h * d0).toFloat()
            out[index++] = (b0 + h).toFloat()
            out[index++] = (a1 - h * d1).toFloat()
            out[index++] = (b1 - h).toFloat()
            out[index++] = a1.toFloat()
            out[index++] = b1.toFloat()
        }
        // Pin the end point exactly onto the tangent point.
        out[out.size - 2] = 1f
        out[out.size - 1] = 0f
        return out
    }

    /**
     * The corner radii the Oplus G2 shader draws: each radius clamped to `[0, min(width, height) / 2]`.
     * The shader's per-edge `k = min(1, edge / (r1 + r2))` scale is a no-op after that clamp (two
     * corners sharing an edge can never exceed the shorter side), so it is not repeated here.
     */
    fun effectiveRadius(radius: Float, width: Float, height: Float): Float {
        val halfMin = minOf(width, height) * 0.5f
        return if (radius.isNaN()) 0f else radius.coerceIn(0f, maxOf(halfMin, 0f))
    }

    // (1 - x^n)^(1/n)
    private fun lpComplement(x: Double, n: Double): Double = (1.0 - x.coerceAtLeast(0.0).pow(n)).coerceAtLeast(0.0).pow(1.0 / n)

    // d/dx (1 - x^n)^(1/n) = -x^(n-1) * (1 - x^n)^(1/n - 1); bounded for x <= 2^(-1/n).
    private fun lpComplementSlope(x: Double, n: Double): Double {
        if (x <= 0.0) return 0.0
        val rest = (1.0 - x.pow(n)).coerceAtLeast(1e-12)
        return -x.pow(n - 1.0) * rest.pow(1.0 / n - 1.0)
    }
}
