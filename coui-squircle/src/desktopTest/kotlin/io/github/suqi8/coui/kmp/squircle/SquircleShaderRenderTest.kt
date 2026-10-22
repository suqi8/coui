// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.squircle

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.skiaShader
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Surface
import java.io.File
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.jetbrains.skia.Color as SkColor

class SquircleShaderRenderTest {

    private fun render(width: Int, height: Int, r: Float, n: Float, name: String): Bitmap {
        val brush = SquircleShaderBrush(Color.White, floatArrayOf(r, r, r, r), n)
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

    private fun alpha(bitmap: Bitmap, x: Int, y: Int): Double = SkColor.getA(bitmap.getColor(x, y)) / 255.0

    @Test
    fun dialogCornerCoverageMatchesSuperellipse() {
        val r = 45f
        val n = 3f
        val bitmap = render(328, 400, r, n, "dialog-r45-n3")
        val corner = AnalyticCorner(r.toDouble(), n.toDouble(), samples = 4000)
        var worstCoverage = 0.0
        for (y in 0 until r.toInt()) {
            for (x in 0 until r.toInt()) {
                val d = corner.signedDistance(x + 0.5, y + 0.5)
                if (abs(d) > 1.0) {
                    // Away from the boundary the shader must be fully opaque or fully clear.
                    assertEquals(if (d < 0) 1.0 else 0.0, alpha(bitmap, x, y), 1.0 / 255, "pixel ($x, $y) d=$d")
                } else {
                    val expected = (0.5 - d).coerceIn(0.0, 1.0)
                    worstCoverage = maxOf(worstCoverage, abs(alpha(bitmap, x, y) - expected))
                }
            }
        }
        println("r=45 n=3 shader coverage error = $worstCoverage")
        // A smoothstep ramp differs from linear coverage by <= 0.094 even for an exact boundary.
        assertTrue(worstCoverage < 0.15, "coverage error $worstCoverage")
    }

    @Test
    fun boundaryOnTheDiagonalMatchesSuperellipse() {
        val r = 45f
        val n = 3f
        val bitmap = render(328, 400, r, n, "dialog-r45-n3-diagonal")
        // The shader ramps alpha = smoothstep(0.5, -0.5, d) over the signed distance d, so a partially
        // covered pixel on the diagonal gives its distance back; by symmetry the boundary normal there
        // runs along the diagonal, so the boundary sits at center + d / sqrt(2) on each axis.
        var deviation = Double.NaN
        for (i in 0 until r.toInt()) {
            val a = alpha(bitmap, i, i)
            if (a <= 0.02 || a >= 0.98) continue
            var lo = 0.0
            var hi = 1.0
            repeat(60) {
                val t = (lo + hi) / 2
                if (t * t * (3 - 2 * t) < a) lo = t else hi = t
            }
            val d = 0.5 - lo
            val boundary = i + 0.5 + d / sqrt(2.0)
            // Analytic: q.x == q.y == r * 2^(-1/n) from the corner center.
            val expected = r - r * 2.0.pow(-1.0 / n)
            deviation = abs(boundary - expected) * sqrt(2.0)
            break
        }
        println("r=45 n=3 shader diagonal deviation = $deviation px")
        assertTrue(deviation < 0.25, "deviation $deviation")
    }

    @Test
    fun straightSidesAndCenterStayOpaque() {
        val bitmap = render(200, 120, 24f, 2.5f, "card-r24-n2.5")
        assertEquals(1.0, alpha(bitmap, 100, 0), 1e-9)
        assertEquals(1.0, alpha(bitmap, 0, 60), 1e-9)
        assertEquals(1.0, alpha(bitmap, 100, 60), 1e-9)
        assertEquals(0.0, alpha(bitmap, 0, 0), 1e-9)
    }
}
