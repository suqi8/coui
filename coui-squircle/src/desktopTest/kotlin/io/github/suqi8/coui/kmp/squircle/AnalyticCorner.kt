// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.squircle

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sin

/**
 * The analytic top-left corner of an Oplus smooth round rect with radius [r] and exponent [n]:
 * `x = r - r * cos^(2/n)(t)`, `y = r - r * sin^(2/n)(t)`, densely sampled.
 */
internal class AnalyticCorner(val r: Double, val n: Double, samples: Int = 40_000) {
    val xs = DoubleArray(samples + 1)
    val ys = DoubleArray(samples + 1)

    init {
        for (i in 0..samples) {
            val t = PI / 2 * i / samples
            xs[i] = r - r * abs(cos(t)).pow(2.0 / n)
            ys[i] = r - r * abs(sin(t)).pow(2.0 / n)
        }
    }

    /** Distance from (x, y) to the sampled curve, measured point-to-segment. */
    fun distance(x: Double, y: Double): Double {
        var best = Double.MAX_VALUE
        for (i in 0 until xs.size - 1) {
            val ax = xs[i]
            val ay = ys[i]
            val sx = xs[i + 1] - ax
            val sy = ys[i + 1] - ay
            val len2 = sx * sx + sy * sy
            val u = if (len2 == 0.0) 0.0 else (((x - ax) * sx + (y - ay) * sy) / len2).coerceIn(0.0, 1.0)
            val d = hypot(x - (ax + u * sx), y - (ay + u * sy))
            if (d < best) best = d
        }
        return best
    }

    /** Whether (x, y), inside the corner square, lies inside the shape. */
    fun inside(x: Double, y: Double): Boolean {
        val qx = r - x
        val qy = r - y
        if (qx <= 0.0 || qy <= 0.0) return true
        return qx.pow(n) + qy.pow(n) <= r.pow(n)
    }

    /** Signed distance to the corner boundary, negative inside the shape. */
    fun signedDistance(x: Double, y: Double): Double = distance(x, y).let { if (inside(x, y)) -it else it }
}
