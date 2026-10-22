// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.squircle

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.skiaShader
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Surface
import java.io.File
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.jetbrains.skia.Color as SkColor

class SoftCapsuleTest {

    private fun pathSamples(width: Float, height: Float, radius: Float, weight: Float): List<Pair<Double, Double>> {
        val path = Path().apply { addSquircleRect(width, height, radius, weight = weight) }
        val measure = PathMeasure().apply { setPath(path, false) }
        val samples = ArrayList<Pair<Double, Double>>()
        var distance = 0f
        while (distance <= measure.length) {
            val p = measure.getPosition(distance)
            samples += p.x.toDouble() to p.y.toDouble()
            distance += 0.05f
        }
        return samples
    }

    // Native contour point on the ray from the rect center at angle theta, by bisection.
    private fun nativeContour(width: Double, height: Double, weight: Double, theta: Double): Pair<Double, Double> {
        val dx = cos(theta)
        val dy = sin(theta)
        var lo = 0.0
        var hi = hypot(width, height)
        repeat(80) {
            val mid = (lo + hi) / 2
            if (NativeG2.distance(mid * dx, mid * dy, width, height, max(width, height), weight) < 0) lo = mid else hi = mid
        }
        val rho = (lo + hi) / 2
        return width / 2 + rho * dx to height / 2 + rho * dy
    }

    /** Symmetric Hausdorff distance between the path and the native zero contour, in pixels. */
    private fun deviation(width: Float, height: Float, weight: Float): Double {
        val samples = pathSamples(width, height, max(width, height), weight)
        assertTrue(samples.size > 200, "too few samples")
        val w = width.toDouble()
        val h = height.toDouble()
        // Path -> native: the native field is a Euclidean distance near its zero set.
        var worst = samples.maxOf { (x, y) -> abs(NativeG2.distance(x - w / 2, y - h / 2, w, h, max(w, h), weight.toDouble())) }
        println("  path -> native |d| max = $worst px")
        // Native -> path.
        for (i in 0 until 720) {
            val (cx, cy) = nativeContour(w, h, weight.toDouble(), 2 * PI * i / 720)
            val d = samples.minOf { (x, y) -> hypot(x - cx, y - cy) }
            worst = max(worst, d)
        }
        return worst
    }

    @Test
    fun softCapsulePathFollowsTheNativeContour() {
        for ((size, weight) in listOf(
            Size(300f, 80f) to 1f,
            Size(300f, 80f) to 1.3f,
            Size(300f, 80f) to 1.7f,
            Size(80f, 300f) to 1f,
            Size(140f, 126f) to 1f,
            Size(1080f, 154f) to 1f,
        )) {
            val deviation = deviation(size.width, size.height, weight)
            println("soft capsule ${size.width}x${size.height} n=$weight path deviation = $deviation px")
            // Half the 0.05px sampling step is the floor here.
            assertTrue(deviation < 0.04, "${size.width}x${size.height} n=$weight deviation $deviation")
        }
    }

    @Test
    fun softJunctionPullsInsideTheCircularCapsule() {
        // The soft capsule sits up to about 0.02 r inside the circular capsule around the side/cap joint.
        val width = 300.0
        val height = 80.0
        val r = height / 2
        var worst = 0.0
        for (i in 0..2000) {
            val theta = PI * i / 4000
            val (x, y) = nativeContour(width, height, 1.0, theta)
            val circular = NativeG2.distance(x - width / 2, y - height / 2, width, height, r, 2.0)
            worst = max(worst, -circular)
        }
        println("soft capsule (n=1) inset from the circular capsule = ${worst / r} r")
        assertTrue(worst / r in 0.015..0.025, "inset ${worst / r} r")
    }

    @Test
    fun weightBelowTwoOnlyAppliesWhenFullyRounded() {
        val soft = Path().apply { addSquircleRect(300f, 80f, 20f, weight = 1f) }
        val circular = Path().apply { addSquircleRect(300f, 80f, 20f, weight = 2f) }
        assertEquals(circular.getBounds(), soft.getBounds())
        val a = PathMeasure().apply { setPath(soft, false) }
        val b = PathMeasure().apply { setPath(circular, false) }
        assertEquals(b.length, a.length, 1e-3f)
    }

    private fun render(width: Int, height: Int, radius: Float, weight: Float, name: String): Bitmap {
        val brush = SquircleShaderBrush(Color.White, floatArrayOf(radius, radius, radius, radius), weight)
        val surface = Surface.makeRasterN32Premul(width, height)
        surface.canvas.clear(SkColor.TRANSPARENT)
        surface.canvas.drawPaint(Paint().apply { shader = brush.createShader(Size(width.toFloat(), height.toFloat())).skiaShader })
        File("build/squircle").mkdirs()
        surface.makeImageSnapshot().encodeToData(EncodedImageFormat.PNG)?.bytes?.let {
            File("build/squircle/$name.png").writeBytes(it)
        }
        return Bitmap().apply {
            allocN32Pixels(width, height)
            surface.readPixels(this, 0, 0)
        }
    }

    @Test
    fun shaderMatchesTheNativeFormula() {
        for ((size, weight) in listOf(Size(300f, 80f) to 1f, Size(300f, 80f) to 1.3f, Size(64f, 200f) to 1f)) {
            val w = size.width.toInt()
            val h = size.height.toInt()
            val bitmap = render(w, h, 1000f, weight, "capsule-${w}x$h-n$weight")
            var worst = 0.0
            for (y in 0 until h) {
                for (x in 0 until w) {
                    val d = NativeG2.distance(x + 0.5 - w / 2.0, y + 0.5 - h / 2.0, w.toDouble(), h.toDouble(), 1000.0, weight.toDouble())
                    val t = (0.5 - d).coerceIn(0.0, 1.0)
                    val expected = t * t * (3 - 2 * t)
                    val actual = SkColor.getA(bitmap.getColor(x, y)) / 255.0
                    worst = max(worst, abs(actual - expected))
                }
            }
            println("soft capsule ${w}x$h n=$weight shader alpha error = $worst")
            assertTrue(worst < 3.0 / 255, "${w}x$h n=$weight alpha error $worst")
        }
    }

    @Test
    fun shaderKeepsCircularCornersWhenNotFullyRounded() {
        val soft = render(200, 80, 20f, 1.3f, "rect-r20-n1.3")
        val circular = render(200, 80, 20f, 2f, "rect-r20-n2")
        for (y in 0 until 80) {
            for (x in 0 until 200) {
                assertEquals(circular.getColor(x, y), soft.getColor(x, y), "pixel ($x, $y)")
            }
        }
    }

    @Test
    fun capsuleMaskCoversTheSoftJunction() {
        val brush = SquircleShaderBrush(Color.White, floatArrayOf(40f, 40f, 40f, 40f), 1f)
        val size = Size(300f, 80f)
        // The junction softening reaches 0.875 r past the cap; the mask must cover it.
        assertTrue(brush.cornerWidthPx(0, size) >= 40f * 1.875f)
        assertEquals(40f, brush.cornerHeightPx(0, size))
        val vertical = Size(80f, 300f)
        assertTrue(brush.cornerHeightPx(0, vertical) >= 40f * 1.875f)
        assertEquals(40f, brush.cornerWidthPx(0, vertical))
    }
}
