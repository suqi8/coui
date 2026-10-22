// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.squircle.internal

import androidx.compose.ui.graphics.Path
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * The Oplus G2 shape for weights below 2 (libhwui `g2RoundRectCommonFill`, `superN < 2`): the
 * signed distance blends from the circular capsule towards a soft capsule,
 * `mix(sdRoundRect(n = 2), sdCapsule, smoothstep(2, 1, n))`, and is the soft capsule alone for
 * `n <= 1.005`. The soft capsule takes its radius from half the shorter side (the corner radius is
 * ignored) and rounds off the joint between a straight side and an end cap with a smooth maximum
 * whose width scales with that radius (`kMax` 26.6, junction bands 70 / 60, all times
 * `r * 0.0125`); `kAbs` softens both coordinates by one pixel.
 *
 * Like `OplusPathSmooth.addSmoothRoundRect`, a weight below 2 only applies to a fully rounded rect
 * (four equal radii of at least half the shorter side); any other rect is drawn with weight 2.
 *
 * All lengths are pixels, the unit the native constants are expressed in.
 */
internal object SoftCapsule {

    /** At or below this weight the native shader draws the soft capsule alone. */
    const val PURE_CAPSULE_WEIGHT = 1.005f

    /** Below this radius (px) the one pixel `kAbs` softening swallows the shape; draw a plain capsule. */
    const val MIN_RADIUS_PX = 2f

    private const val K_ABS = 1.0
    private const val K_MAX = 26.6
    private const val JUNCTION_X_BAND = 70.0
    private const val JUNCTION_Y_BAND = 60.0
    private const val SCALE_PER_RADIUS = 0.0125
    private const val EPSILON = 0.01f

    /** Bisection steps per contour point: 2^-36 of the search span, far below a thousandth of a pixel. */
    private const val ROOT_ITERATIONS = 36

    /** Step (px) of the central differences that give the contour tangents. */
    private const val GRADIENT_STEP = 1e-4

    /** Cubic segments that follow the softened straight side up to the end cap, per quarter. */
    private const val SIDE_SEGMENTS = 8

    /** Cubic segments along the end cap, per quarter. */
    private const val CAP_SEGMENTS = 16

    /**
     * Whether the four corner [radii] round the `width x height` rect fully
     * (`OplusSmoothRoundedManager.isFullyRoundedToCircle`): all equal, and at least half the
     * shorter side.
     */
    fun isFullyRounded(radii: FloatArray, width: Float, height: Float): Boolean {
        val first = radii[0]
        for (radius in radii) {
            if (abs(radius - first) > EPSILON) return false
        }
        return isFullyRounded(first, width, height)
    }

    /** Four radius variant of [isFullyRounded] that does not allocate. */
    fun isFullyRounded(topLeft: Float, topRight: Float, bottomRight: Float, bottomLeft: Float, width: Float, height: Float): Boolean = abs(topRight - topLeft) <= EPSILON &&
        abs(bottomRight - topLeft) <= EPSILON &&
        abs(bottomLeft - topLeft) <= EPSILON &&
        isFullyRounded(topLeft, width, height)

    /** Single radius variant of [isFullyRounded]. */
    fun isFullyRounded(radius: Float, width: Float, height: Float): Boolean = radius >= min(width, height) * 0.5f

    /**
     * The blend factor towards the soft capsule for an already clamped [weight] on a fully rounded
     * rect: `smoothstep(2, 1, weight)`, 1 at or below [PURE_CAPSULE_WEIGHT], and -1 when the
     * weight is 2 or more (no capsule blend; draw the superellipse).
     */
    fun capsuleMix(weight: Float): Float {
        if (weight >= 2f) return -1f
        if (weight <= PURE_CAPSULE_WEIGHT) return 1f
        val x = ((weight - 2f) / (1f - 2f)).coerceIn(0f, 1f)
        return x * x * (3f - 2f * x)
    }

    /**
     * The native signed distance at ([u], [v]) from the center of a capsule whose long axis is `u`,
     * with half sizes [halfLong] >= [halfShort], blended by [mix] (see [capsuleMix]).
     */
    fun distance(u: Double, v: Double, halfLong: Double, halfShort: Double, mix: Double): Double {
        val r = halfShort
        val a = max(halfLong - r, 0.0)
        // sdCapsule.
        val qx = sqrt(u * u + K_ABS * K_ABS)
        val qy = sqrt(v * v + K_ABS * K_ABS)
        val scale = r * SCALE_PER_RADIUS
        val kMax = max(K_MAX * scale, 1e-4)
        val xBand = max(JUNCTION_X_BAND * scale, 1e-3)
        val yBand = max(JUNCTION_Y_BAND * scale, 1e-3)
        val s = qx - a
        val xHard = max(s, 0.0)
        val xSoft = 0.5 * (s + sqrt(s * s + kMax * kMax))
        val wx = 1.0 - smoothstep(0.0, xBand, abs(s))
        val wy = smoothstep(r - yBand, r, qy)
        val w = (wx * wy).coerceIn(0.0, 1.0)
        val x = xHard + (xSoft - xHard) * w
        val capsule = sqrt(x * x + qy * qy) - r
        if (mix >= 1.0) return capsule
        // sdRoundRect with n = 2 and the radius clamped to half the shorter side: a circular capsule.
        val cx = abs(u) - a
        val cy = abs(v)
        val outside = sqrt(max(cx, 0.0) * max(cx, 0.0) + max(cy, 0.0) * max(cy, 0.0))
        val round = outside + min(max(cx, cy), 0.0) - r
        return round + (capsule - round) * mix
    }

    /**
     * How far past the end cap along the long axis the soft junction reaches (px), measured from
     * the rect edge: the region a corner mask has to cover.
     */
    fun junctionReach(halfShort: Float): Float = halfShort * (1f + (JUNCTION_X_BAND * SCALE_PER_RADIUS).toFloat()) + 2f

    /**
     * Appends the zero contour of [distance] for a `width x height` rect as one closed path of
     * cubic segments: the straight sides and the softened junctions are sampled along the side
     * normal, the end caps along rays from the cap center, and consecutive samples are joined by
     * cubic Hermite segments whose tangents are differentiated numerically. The rect's top-left
     * corner sits at ([left], [top]).
     */
    fun appendContour(path: Path, left: Float, top: Float, width: Float, height: Float, mix: Float) {
        val vertical = height > width
        val halfLong = (if (vertical) height else width) * 0.5
        val halfShort = (if (vertical) width else height) * 0.5
        val quarter = quarterSegments(halfLong, halfShort, mix.toDouble())
        val cx = left + width * 0.5
        val cy = top + height * 0.5

        // Maps (u along the long axis, v along the short axis) of quadrant (su, sv) to the canvas.
        fun x(u: Double, v: Double, su: Int, sv: Int): Float = (cx + if (vertical) sv * v else su * u).toFloat()
        fun y(u: Double, v: Double, su: Int, sv: Int): Float = (cy + if (vertical) su * u else -sv * v).toFloat()

        val start = quarter.start
        path.moveTo(x(start[0], start[1], 1, 1), y(start[0], start[1], 1, 1))
        // Quadrants in drawing order; odd ones run the quarter backwards.
        val quadrants = arrayOf(intArrayOf(1, 1), intArrayOf(1, -1), intArrayOf(-1, -1), intArrayOf(-1, 1))
        for ((index, quadrant) in quadrants.withIndex()) {
            val su = quadrant[0]
            val sv = quadrant[1]
            val segments = quarter.segments
            if (index % 2 == 0) {
                for (seg in segments) {
                    path.cubicTo(
                        x(seg[0], seg[1], su, sv),
                        y(seg[0], seg[1], su, sv),
                        x(seg[2], seg[3], su, sv),
                        y(seg[2], seg[3], su, sv),
                        x(seg[4], seg[5], su, sv),
                        y(seg[4], seg[5], su, sv),
                    )
                }
            } else {
                for (i in segments.indices.reversed()) {
                    val seg = segments[i]
                    val end = if (i == 0) start else segments[i - 1].let { doubleArrayOf(it[4], it[5]) }
                    path.cubicTo(
                        x(seg[2], seg[3], su, sv),
                        y(seg[2], seg[3], su, sv),
                        x(seg[0], seg[1], su, sv),
                        y(seg[0], seg[1], su, sv),
                        x(end[0], end[1], su, sv),
                        y(end[0], end[1], su, sv),
                    )
                }
            }
        }
        path.close()
    }

    /** One quarter of the contour, from the middle of a long side to the tip of an end cap. */
    private class Quarter(val start: DoubleArray, val segments: List<DoubleArray>)

    private fun quarterSegments(halfLong: Double, halfShort: Double, mix: Double): Quarter {
        val r = halfShort
        val a = max(halfLong - r, 0.0)
        val xBand = max(JUNCTION_X_BAND * r * SCALE_PER_RADIUS, 1e-3)

        // Contour point on the vertical through (u, 0), with its derivative d/du from the implicit
        // function theorem: dv/du = -(dD/du) / (dD/dv).
        fun side(u: Double): DoubleArray {
            val v = root(u, 0.0, 0.0, 1.0, halfLong, halfShort, mix)
            val g = gradient(u, v, halfLong, halfShort, mix)
            return doubleArrayOf(u, v, 1.0, -g[0] / g[1])
        }

        // Contour point on the ray from the cap center (a, 0) at angle theta from the long axis, with
        // its derivative d/dtheta: rho' = -rho * (grad . perp) / (grad . dir).
        fun cap(theta: Double): DoubleArray {
            val dx = cos(theta)
            val dy = sin(theta)
            val rho = root(a, 0.0, dx, dy, halfLong, halfShort, mix)
            val x = a + rho * dx
            val y = rho * dy
            val g = gradient(x, y, halfLong, halfShort, mix)
            val rhoPrime = -rho * (-g[0] * dy + g[1] * dx) / (g[0] * dx + g[1] * dy)
            return doubleArrayOf(x, y, rhoPrime * dx - rho * dy, rhoPrime * dy + rho * dx)
        }

        val segments = ArrayList<DoubleArray>()
        // Where the soft junction stops touching the side: |s| >= xBand for sqrt(u^2 + kAbs^2) <= a - xBand.
        val flatEnd = max(a - xBand - K_ABS, 0.0)
        val start = side(0.0)
        if (flatEnd > 0.0) {
            // Straight run: the distance does not depend on u here, so the side is a line.
            val end = doubleArrayOf(flatEnd, start[1])
            segments += doubleArrayOf(
                start[0] + (end[0] - start[0]) / 3,
                start[1],
                start[0] + (end[0] - start[0]) * 2 / 3,
                start[1],
                end[0],
                end[1],
            )
        }
        if (a > flatEnd) {
            hermite(segments, flatEnd, a, SIDE_SEGMENTS, ::side)
        }
        hermite(segments, PI / 2, 0.0, CAP_SEGMENTS, ::cap)
        return Quarter(start, segments)
    }

    // Joins `count` samples of `curve` over [from, to] with cubic Hermite segments. `curve` returns
    // the point and its derivative d/dt: (x, y, dx/dt, dy/dt).
    private fun hermite(out: MutableList<DoubleArray>, from: Double, to: Double, count: Int, curve: (Double) -> DoubleArray) {
        val step = (to - from) / count
        var p0 = curve(from)
        for (i in 1..count) {
            val p1 = curve(if (i == count) to else from + step * i)
            // Tangents are d/dt; the parameter runs by `step` per segment.
            val k = step / 3
            out += doubleArrayOf(
                p0[0] + p0[2] * k,
                p0[1] + p0[3] * k,
                p1[0] - p1[2] * k,
                p1[1] - p1[3] * k,
                p1[0],
                p1[1],
            )
            p0 = p1
        }
    }

    // Central-difference gradient of the distance at (u, v); the field is smooth at this scale.
    private fun gradient(u: Double, v: Double, halfLong: Double, halfShort: Double, mix: Double): DoubleArray {
        val h = GRADIENT_STEP
        val du = distance(u + h, v, halfLong, halfShort, mix) - distance(u - h, v, halfLong, halfShort, mix)
        val dv = distance(u, v + h, halfLong, halfShort, mix) - distance(u, v - h, halfLong, halfShort, mix)
        return doubleArrayOf(du / (2 * h), dv / (2 * h))
    }

    // Distance along the ray (ox, oy) + rho * (dx, dy) to the zero contour, by bisection.
    private fun root(ox: Double, oy: Double, dx: Double, dy: Double, halfLong: Double, halfShort: Double, mix: Double): Double {
        var lo = 0.0
        var hi = halfShort * 1.5 + 2 * K_ABS
        repeat(ROOT_ITERATIONS) {
            val mid = (lo + hi) * 0.5
            if (distance(ox + mid * dx, oy + mid * dy, halfLong, halfShort, mix) < 0.0) lo = mid else hi = mid
        }
        return (lo + hi) * 0.5
    }

    private fun smoothstep(edge0: Double, edge1: Double, x: Double): Double {
        val t = ((x - edge0) / (edge1 - edge0)).coerceIn(0.0, 1.0)
        return t * t * (3.0 - 2.0 * t)
    }
}
