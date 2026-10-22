// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.squircle

import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * A line-by-line transliteration of the libhwui / Oplus material G2 shape shader (`sdCapsule`,
 * `lpNorm`, `sdRoundRect`, `getNormalizedRadius` and the `superN < 2` branch of
 * `g2RoundRectCommonFill`), kept independent from the library code so tests compare against the
 * native formula rather than against themselves. Coordinates are pixels relative to the rect center.
 */
internal object NativeG2 {
    private const val EPS = 1e-6
    private const val K_ABS = 1.0
    private const val K_MAX = 26.6
    private const val JUNCTION_X_BAND = 70.0
    private const val JUNCTION_Y_BAND = 60.0

    private fun smoothAbsF(x: Double, k: Double) = sqrt(x * x + k * k)

    private fun smoothMaxSqrt(a: Double, b: Double, k: Double): Double {
        val d = a - b
        return 0.5 * (a + b + sqrt(d * d + k * k))
    }

    // GLSL smoothstep, also for edge0 > edge1 as the shader calls it.
    private fun smoothstep(edge0: Double, edge1: Double, x: Double): Double {
        val t = ((x - edge0) / (edge1 - edge0)).coerceIn(0.0, 1.0)
        return t * t * (3.0 - 2.0 * t)
    }

    fun sdCapsule(pLocalX: Double, pLocalY: Double, halfX: Double, halfY: Double): Double {
        val isVertical = halfX < halfY
        val hsX = if (isVertical) halfY else halfX
        val hsY = if (isVertical) halfX else halfY
        val plX = if (isVertical) pLocalY else pLocalX
        val plY = if (isVertical) pLocalX else pLocalY
        val r = hsY
        val a = max(hsX - r, 0.0)
        val qX = smoothAbsF(plX, K_ABS)
        val qY = smoothAbsF(plY, K_ABS)
        val scale = r * 0.0125
        val kMaxEff = max(K_MAX * scale, 1e-4)
        val xBandEff = max(JUNCTION_X_BAND * scale, 1e-3)
        val yBandEff = max(JUNCTION_Y_BAND * scale, 1e-3)
        val s = qX - a
        val xHard = max(s, 0.0)
        val xSoft = smoothMaxSqrt(s, 0.0, kMaxEff)
        val wx = 1.0 - smoothstep(0.0, xBandEff, abs(s))
        val wy = smoothstep(r - yBandEff, r, qY)
        val w = (wx * wy).coerceIn(0.0, 1.0)
        val x = xHard + (xSoft - xHard) * w
        return sqrt(x * x + qY * qY) - r
    }

    private fun powFast(x: Double, n: Double): Double = exp(ln(max(x, 1e-8)) * n)

    private fun lpNorm(vx: Double, vy: Double, n: Double): Double {
        val ax = abs(vx)
        val ay = abs(vy)
        val m = max(ax, ay)
        if (m < 1e-8) return 0.0
        val s = powFast(ax / m, n) + powFast(ay / m, n)
        return m * exp(ln(max(s, EPS)) / n)
    }

    fun sdRoundRect(qX: Double, qY: Double, r: Double, n: Double): Double {
        val nC = max(n, 2.0)
        val px = max(qX, 0.0)
        val py = max(qY, 0.0)
        val dCorner = if (abs(nC - 2.0) < 1e-3) sqrt(px * px + py * py) else lpNorm(px, py, nC)
        return dCorner + min(max(qX, qY), 0.0) - r
    }

    /** `g2RoundRectCommonFill` distance for a `width x height` rect with uniform [radius]. */
    fun distance(px: Double, py: Double, width: Double, height: Double, radius: Double, superN: Double): Double {
        val halfX = width / 2
        val halfY = height / 2
        val r = radius.coerceIn(0.0, min(halfX, halfY))
        val qX = abs(px) - halfX + r
        val qY = abs(py) - halfY + r
        return if (superN < 2.0) {
            if (superN > 1.005) {
                val t = smoothstep(2.0, 1.0, superN)
                val dG2 = sdRoundRect(qX, qY, r, 2.0)
                val dCapsule = sdCapsule(px, py, halfX, halfY)
                dG2 + (dCapsule - dG2) * t
            } else {
                sdCapsule(px, py, halfX, halfY)
            }
        } else {
            sdRoundRect(qX, qY, r, superN)
        }
    }
}
