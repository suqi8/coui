// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.squircle

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import io.github.suqi8.coui.kmp.squircle.internal.SuperellipseCorner
import kotlin.math.hypot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SquirclePathTest {

    /** Symmetric Hausdorff distance between the path's top-left corner and the analytic corner. */
    private fun cornerDeviation(width: Float, height: Float, r: Float, n: Float): Double {
        val path = Path().apply { addSquircleRect(width, height, r, weight = n) }
        val measure = PathMeasure().apply { setPath(path, false) }
        val corner = AnalyticCorner(r.toDouble(), n.toDouble())
        val pathPoints = ArrayList<Pair<Double, Double>>()
        var distance = 0f
        while (distance <= measure.length) {
            val p = measure.getPosition(distance)
            if (p.x <= r && p.y <= r) pathPoints += p.x.toDouble() to p.y.toDouble()
            distance += 0.02f
        }
        assertTrue(pathPoints.size > 100, "too few corner samples: ${pathPoints.size}")
        var worst = pathPoints.maxOf { (x, y) -> corner.distance(x, y) }
        // Reverse direction: every analytic point must have a path sample nearby.
        for (i in corner.xs.indices step 200) {
            val d = pathPoints.minOf { (x, y) -> hypot(x - corner.xs[i], y - corner.ys[i]) }
            if (d > worst) worst = d
        }
        return worst
    }

    @Test
    fun dialogCornerMatchesSuperellipse() {
        // COUI 17 alert dialog: r = 45dp, weight_16_1_large = 3.0 (1px stands for 1dp here).
        val deviation = cornerDeviation(328f, 400f, 45f, 3f)
        println("r=45 n=3 path deviation = $deviation dp")
        assertTrue(deviation < 0.25, "deviation $deviation")
    }

    @Test
    fun cardCornerMatchesSuperellipse() {
        // COUI 17 card list: r = 24dp, weight_16_1_normal = 2.5.
        val deviation = cornerDeviation(360f, 120f, 24f, 2.5f)
        println("r=24 n=2.5 path deviation = $deviation dp")
        assertTrue(deviation < 0.25, "deviation $deviation")
    }

    @Test
    fun weightTwoIsACircularArc() {
        val deviation = cornerDeviation(200f, 200f, 45f, 2f)
        assertTrue(deviation < 0.05, "deviation $deviation")
    }

    @Test
    fun everySupportedWeightStaysOnTheCurve() {
        for (n in listOf(2.25f, 3.5f, 4f, 5f, 8f)) {
            val deviation = cornerDeviation(300f, 300f, 60f, n)
            println("r=60 n=$n path deviation = $deviation dp")
            assertTrue(deviation < 0.25, "n=$n deviation $deviation")
        }
    }

    @Test
    fun unitQuarterIsTangentToBothSides() {
        val q = SuperellipseCorner.unitQuarter(3f)
        // The first control point leaves (0, 1) horizontally; the last enters (1, 0) vertically.
        assertEquals(1f, q[1], 1e-6f)
        assertEquals(1f, q[q.size - 4], 1e-6f)
        assertEquals(1f, q[q.size - 2], 0f)
        assertEquals(0f, q[q.size - 1], 0f)
    }

    @Test
    fun radiusAndWeightAreClamped() {
        assertEquals(20f, SuperellipseCorner.effectiveRadius(45f, 100f, 40f))
        assertEquals(0f, SuperellipseCorner.effectiveRadius(-3f, 100f, 40f))
        assertEquals(SquircleDefaults.WeightMin, SuperellipseCorner.clampWeight(1f))
        assertEquals(SquircleDefaults.Weight, SuperellipseCorner.clampWeight(Float.NaN))
    }
}
