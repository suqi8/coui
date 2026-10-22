// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.squircle

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.skiaShader
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Surface
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.jetbrains.skia.Color as SkColor

class PerCornerSquircleTest {

    private fun samples(path: Path, step: Float = 0.02f): List<Pair<Double, Double>> {
        val measure = PathMeasure().apply { setPath(path, false) }
        val out = ArrayList<Pair<Double, Double>>()
        var distance = 0f
        while (distance <= measure.length) {
            val p = measure.getPosition(distance)
            out += p.x.toDouble() to p.y.toDouble()
            distance += step
        }
        return out
    }

    /**
     * Symmetric Hausdorff distance between the path samples inside one corner square and the
     * analytic superellipse quarter. [toCorner] maps a canvas point into the top-left frame of
     * [AnalyticCorner] (the corner vertex at the origin, the shape towards +x / +y).
     */
    private fun cornerDeviation(
        points: List<Pair<Double, Double>>,
        r: Double,
        n: Double,
        toCorner: (Double, Double) -> Pair<Double, Double>,
        fromCorner: (Double, Double) -> Pair<Double, Double>,
    ): Double {
        val corner = AnalyticCorner(r, n)
        val local = points.map { (x, y) -> toCorner(x, y) }.filter { (x, y) -> x <= r && y <= r && x >= -1e-3 && y >= -1e-3 }
        assertTrue(local.size > 50, "too few corner samples: ${local.size}")
        var worst = local.maxOf { (x, y) -> corner.distance(x, y) }
        for (i in corner.xs.indices step 400) {
            val (cx, cy) = fromCorner(corner.xs[i], corner.ys[i])
            val d = points.minOf { (x, y) -> hypot(x - cx, y - cy) }
            if (d > worst) worst = d
        }
        return worst
    }

    @Test
    fun eachCornerFollowsItsOwnSuperellipse() {
        val rect = Rect(10f, 20f, 310f, 220f)
        val n = 2.5
        // Top-left 40, top-right 12, bottom-right 0 (square), bottom-left 500 (clamped to 100).
        val path = Path().apply { addSquircleRect(rect, 40f, 12f, 0f, 500f, weight = n.toFloat()) }
        val points = samples(path)
        val l = rect.left.toDouble()
        val t = rect.top.toDouble()
        val r = rect.right.toDouble()
        val b = rect.bottom.toDouble()

        val tl = cornerDeviation(points, 40.0, n, { x, y -> x - l to y - t }, { x, y -> l + x to t + y })
        val tr = cornerDeviation(points, 12.0, n, { x, y -> r - x to y - t }, { x, y -> r - x to t + y })
        val bl = cornerDeviation(points, 100.0, n, { x, y -> x - l to b - y }, { x, y -> l + x to b - y })
        println("per-corner deviation: tl=$tl tr=$tr bl=$bl px")
        assertTrue(tl < 0.05, "top-left $tl")
        assertTrue(tr < 0.05, "top-right $tr")
        assertTrue(bl < 0.05, "bottom-left $bl")

        // The square corner keeps its vertex.
        val square = points.minOf { (x, y) -> hypot(x - r, y - b) }
        assertTrue(square < 0.05, "bottom-right vertex $square")
        // Nothing leaves the rect.
        assertTrue(points.all { (x, y) -> x >= l - 1e-3 && x <= r + 1e-3 && y >= t - 1e-3 && y <= b + 1e-3 })
    }

    @Test
    fun equalRadiiMatchTheUniformBuilder() {
        val rect = Rect(5f, 7f, 205f, 107f)
        val perCorner = Path().apply { addSquircleRect(rect, 24f, 24f, 24f, 24f, weight = 3f) }
        val uniform = Path().apply { addSquircleRect(200f, 100f, 24f, weight = 3f) }
        val a = samples(perCorner, 0.5f)
        val b = samples(uniform, 0.5f)
        assertEquals(a.size, b.size)
        for (i in a.indices) {
            assertEquals(b[i].first + 5.0, a[i].first, 1e-3)
            assertEquals(b[i].second + 7.0, a[i].second, 1e-3)
        }
    }

    @Test
    fun offsetSoftCapsuleIsTheTranslatedCapsule() {
        val moved = Path().apply { addSquircleRect(Rect(30f, 40f, 330f, 100f), 72f, weight = SquircleDefaults.CapsuleWeight) }
        val origin = Path().apply { addSquircleRect(300f, 60f, 72f, weight = SquircleDefaults.CapsuleWeight) }
        val a = samples(moved, 0.5f)
        val b = samples(origin, 0.5f)
        assertEquals(a.size, b.size)
        for (i in a.indices) {
            assertEquals(b[i].first + 30.0, a[i].first, 1e-3)
            assertEquals(b[i].second + 40.0, a[i].second, 1e-3)
        }
    }

    @Test
    fun unspecifiedWeightResolvesLikeTheNativeDefault() {
        // A full-round rect without a weight is the soft capsule ...
        val capsule = SquircleShaderBrush(Color.White, floatArrayOf(72f, 72f, 72f, 72f), SquircleDefaults.UnspecifiedWeight)
        assertEquals(SquircleDefaults.CapsuleWeight, capsule.resolvedWeight(Size(300f, 60f)))
        assertEquals(1f, capsule.capsuleMix(Size(300f, 60f)))
        // ... anything else takes default_g2_weight.
        val card = SquircleShaderBrush(Color.White, floatArrayOf(16f, 16f, 16f, 16f), SquircleDefaults.UnspecifiedWeight)
        assertEquals(SquircleDefaults.DefaultG2Weight, card.resolvedWeight(Size(300f, 60f)))
        assertEquals(-1f, card.capsuleMix(Size(300f, 60f)))

        fun lengthOf(build: Path.() -> Unit) = PathMeasure().apply { setPath(Path().apply(build), false) }.length
        assertEquals(
            lengthOf { addSquircleRect(300f, 60f, 72f, weight = SquircleDefaults.CapsuleWeight) },
            lengthOf { addSquircleRect(300f, 60f, 72f, weight = SquircleDefaults.UnspecifiedWeight) },
        )
        assertEquals(
            lengthOf { addSquircleRect(300f, 60f, 16f, weight = SquircleDefaults.DefaultG2Weight) },
            lengthOf { addSquircleRect(300f, 60f, 16f, weight = SquircleDefaults.UnspecifiedWeight) },
        )
    }

    @Test
    fun shaderDrawsPerCornerRadii() {
        // Radii are topStart, topEnd, bottomEnd, bottomStart; the bottom-end corner is the large one.
        val width = 200
        val height = 160
        val small = 12f
        val large = 60f
        val n = 2.5
        val brush = SquircleShaderBrush(Color.White, floatArrayOf(small, small, large, small), n.toFloat())
        val surface = Surface.makeRasterN32Premul(width, height)
        surface.canvas.clear(SkColor.TRANSPARENT)
        surface.canvas.drawPaint(Paint().apply { shader = brush.createShader(Size(width.toFloat(), height.toFloat())).skiaShader })
        val bitmap = Bitmap().apply {
            allocN32Pixels(width, height)
            surface.readPixels(this, 0, 0)
        }
        fun alpha(x: Int, y: Int) = SkColor.getA(bitmap.getColor(x, y)) / 255.0
        val corner = AnalyticCorner(large.toDouble(), n, samples = 4000)
        var worst = 0.0
        for (y in height - large.toInt() until height) {
            for (x in width - large.toInt() until width) {
                // Mirror the bottom-right corner into the analytic top-left frame.
                val d = corner.signedDistance(width - (x + 0.5), height - (y + 0.5))
                if (abs(d) > 1.0) {
                    assertEquals(if (d < 0) 1.0 else 0.0, alpha(x, y), 1.0 / 255, "pixel ($x, $y) d=$d")
                } else {
                    worst = maxOf(worst, abs(alpha(x, y) - (0.5 - d).coerceIn(0.0, 1.0)))
                }
            }
        }
        println("per-corner shader coverage error = $worst")
        assertTrue(worst < 0.15, "coverage error $worst")
        // The small top-left corner is clear at its vertex and opaque one radius in.
        assertEquals(0.0, alpha(0, 0), 1e-9)
        assertEquals(1.0, alpha(small.toInt(), small.toInt()), 1e-9)
    }

    @Test
    fun softCapsulePathBuildCost() {
        // Informational: canvas-drawn tracks rebuild the soft capsule path per frame.
        repeat(20) { Path().addSquircleRect(Rect(0f, 0f, 900f, 70f), 72f, SquircleDefaults.CapsuleWeight) }
        val runs = 200
        val start = System.nanoTime()
        repeat(runs) { i -> Path().addSquircleRect(Rect(0f, 0f, 600f + i, 70f), 72f, SquircleDefaults.CapsuleWeight) }
        val micros = (System.nanoTime() - start) / 1000.0 / runs
        println("soft capsule path build = $micros us")
    }
}
