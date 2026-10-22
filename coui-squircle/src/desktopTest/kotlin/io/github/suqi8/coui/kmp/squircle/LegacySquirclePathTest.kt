// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.squircle

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.PathSegment
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LegacySquirclePathTest {

    /** Records the cubic segments of a path as flat (x1, y1, x2, y2, x3, y3) arrays. */
    private class Recorder {
        val cubics = ArrayList<FloatArray>()
        val lines = ArrayList<FloatArray>()

        fun lineTo(x: Float, y: Float) {
            lines += floatArrayOf(x, y)
        }

        fun cubicTo(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
            cubics += floatArrayOf(x1, y1, x2, y2, x3, y3)
        }
    }

    /**
     * `COUIShapePath.getRoundRectPath(Path, RectF, float, boolean z2, z3, z4, z5)` from the COUI 17
     * Settings dex, transliterated with the jadx variable names (z2 = top-left, z3 = top-right,
     * z4 = bottom-left, z5 = bottom-right).
     */
    @Suppress("LocalVariableName")
    private fun javaRoundRectPath(rec: Recorder, left: Float, top: Float, right: Float, bottom: Float, f3: Float, z2: Boolean, z3: Boolean, z4: Boolean, z5: Boolean) {
        val f4: Float
        val f5: Float
        val f6: Float
        val f7: Float
        val f8 = if (f3 < 0.0f) 0.0f else f3
        val f9 = left
        val f10 = right
        val f11 = bottom
        val f12 = top
        val f13 = f10 - f9
        val f14 = f11 - f12
        val f15 = f13 / 2.0f
        val f16 = f14 / 2.0f
        val fMin = if ((f8 / min(f15, f16)).toDouble() > 0.5) 1.0f - (min(1.0f, ((f8 / min(f15, f16)) - 0.5f) / 0.4f) * 0.13877845f) else 1.0f
        val fMin2 = if (f8 / min(f15, f16) > 0.6f) 1.0f + (min(1.0f, ((f8 / min(f15, f16)) - 0.6f) / 0.3f) * 0.042454004f) else 1.0f
        if (z3) {
            val f17 = f8 / 100.0f
            val f18 = f17 * 128.19f * fMin
            rec.lineTo(max(f15, f13 - f18) + f9, f12)
            val f19 = f9 + f13
            val f20 = f17 * 83.62f * fMin2
            val f21 = f17 * 67.45f
            val f22 = f17 * 4.64f
            val f23 = f17 * 51.16f
            val f24 = f17 * 13.36f
            f4 = 22.07f
            rec.cubicTo(f19 - f20, f12, f19 - f21, f12 + f22, f19 - f23, f12 + f24)
            val f25 = f17 * 34.86f
            val f26 = f17 * 22.07f
            f5 = f8
            f6 = f12
            rec.cubicTo(f19 - f25, f12 + f26, f19 - f26, f25 + f12, f19 - f24, f12 + f23)
            rec.cubicTo(f19 - f22, f6 + f21, f19, f6 + f20, f19, f6 + min(f16, f18))
        } else {
            rec.lineTo(f9 + f13, f12)
            f5 = f8
            f4 = 22.07f
            f6 = f12
        }
        if (z5) {
            val f27 = f9 + f13
            val f28 = f5 / 100.0f
            val f29 = f28 * 128.19f * fMin
            rec.lineTo(f27, f6 + max(f16, f14 - f29))
            val f30 = f6 + f14
            val f31 = f28 * 83.62f * fMin2
            val f32 = f28 * 4.64f
            val f33 = f28 * 67.45f
            val f34 = f28 * 13.36f
            val f35 = f28 * 51.16f
            rec.cubicTo(f27, f30 - f31, f27 - f32, f30 - f33, f27 - f34, f30 - f35)
            val f36 = f28 * f4
            val f37 = f28 * 34.86f
            rec.cubicTo(f27 - f36, f30 - f37, f27 - f37, f30 - f36, f27 - f35, f30 - f34)
            rec.cubicTo(f27 - f33, f30 - f32, f27 - f31, f30, f9 + max(f15, f13 - f29), f30)
        } else {
            rec.lineTo(f13 + f9, f6 + f14)
        }
        if (z4) {
            val f38 = f5 / 100.0f
            val f39 = f38 * 128.19f * fMin
            val f40 = f6 + f14
            rec.lineTo(min(f15, f39) + f9, f40)
            val f41 = f38 * 83.62f * fMin2
            val f42 = f38 * 67.45f
            val f43 = f38 * 4.64f
            val f44 = f38 * 51.16f
            val f45 = f38 * 13.36f
            rec.cubicTo(f9 + f41, f40, f9 + f42, f40 - f43, f9 + f44, f40 - f45)
            val f46 = f38 * 34.86f
            val f47 = f38 * f4
            rec.cubicTo(f9 + f46, f40 - f47, f9 + f47, f40 - f46, f9 + f45, f40 - f44)
            rec.cubicTo(f9 + f43, f40 - f42, f9, f40 - f41, f9, f6 + max(f16, f14 - f39))
            f7 = f9
        } else {
            rec.lineTo(f9, f6 + f14)
            f7 = f9
        }
        if (z2) {
            val f48 = f5 / 100.0f
            val f49 = 128.19f * f48 * fMin
            rec.lineTo(f7, f6 + min(f16, f49))
            val f50 = 83.62f * f48 * fMin2
            val f51 = 4.64f * f48
            val f52 = 67.45f * f48
            val f53 = 13.36f * f48
            val f54 = 51.16f * f48
            rec.cubicTo(f7, f6 + f50, f7 + f51, f6 + f52, f7 + f53, f6 + f54)
            val f55 = f7
            val f56 = f48 * f4
            val f57 = f48 * 34.86f
            rec.cubicTo(f55 + f56, f6 + f57, f55 + f57, f6 + f56, f55 + f54, f6 + f53)
            rec.cubicTo(f55 + f52, f6 + f51, f55 + f50, f6, f55 + min(f15, f49), f6)
        } else {
            rec.lineTo(f7, f6)
        }
    }

    private fun cubicsOf(path: Path): List<FloatArray> = path.iterator().asSequence()
        .filter { it.type == PathSegment.Type.Cubic }
        .map { s -> s.points.copyOfRange(2, 8) }
        .toList()

    private fun assertMatchesJava(left: Float, top: Float, width: Float, height: Float, radius: Float, tl: Boolean = true, tr: Boolean = true, br: Boolean = true, bl: Boolean = true) {
        val path = Path().apply { addLegacySquircleRect(width, height, radius, left, top, tl, tr, br, bl) }
        val rec = Recorder()
        javaRoundRectPath(rec, left, top, left + width, top + height, radius, tl, tr, bl, br)
        val actual = cubicsOf(path)
        assertEquals(rec.cubics.size, actual.size, "cubic count for $width x $height r=$radius")
        for (i in actual.indices) {
            for (j in 0 until 6) {
                assertEquals(rec.cubics[i][j], actual[i][j], 1e-4f, "cubic $i coordinate $j for $width x $height r=$radius")
            }
        }
    }

    @Test
    fun matchesCouiShapePath() {
        // CodeTextField cell (8dp at 3.5x on a 44dp x 52dp cell), red dot capsule, recommended card.
        assertMatchesJava(0f, 0f, 154f, 182f, 28f)
        assertMatchesJava(0f, 0f, 70f, 56f, 28f)
        assertMatchesJava(0f, 0f, 56f, 56f, 28f)
        assertMatchesJava(0f, 0f, 1008f, 600f, 84f)
        assertMatchesJava(3f, 5f, 120f, 80f, 30f)
        // Ratios inside both scale ramps, and partial corner sets.
        assertMatchesJava(0f, 0f, 200f, 100f, 27.5f)
        assertMatchesJava(0f, 0f, 200f, 100f, 40f, tl = false, br = false)
        assertMatchesJava(0f, 0f, 200f, 100f, 40f, tr = false, bl = false)
    }

    @Test
    fun cornerExtendsPastTheRadius() {
        // The curve leaves the straight side at 128.19 % of the radius (ratio <= 0.5).
        val r = 20f
        val path = Path().apply { addLegacySquircleRect(400f, 200f, r) }
        val bounds = path.getBounds()
        assertEquals(0f, bounds.left, 1e-4f)
        assertEquals(400f, bounds.right, 1e-4f)
        // The top-right corner's first cubic starts where the top side ends.
        val firstCubic = path.iterator().asSequence().first { it.type == PathSegment.Type.Cubic }
        val leaveX = firstCubic.points[0]
        assertEquals(0f, firstCubic.points[1], 1e-4f)
        assertEquals(400f - 1.2819f * r, leaveX, 1e-3f)
    }

    @Test
    fun deviationFromTheCircularArc() {
        // How far the legacy corner sits from the circular arc of the same radius (reported only).
        for (r in listOf(8f, 24f, 28f)) {
            val path = Path().apply { addLegacySquircleRect(400f, 400f, r) }
            val measure = PathMeasure().apply { setPath(path, false) }
            var worst = 0.0
            var d = 0f
            while (d < measure.length) {
                val p = measure.getPosition(d)
                if (p.x < r && p.y < r) worst = max(worst, abs(hypot((r - p.x).toDouble(), (r - p.y).toDouble()) - r))
                d += 0.01f
            }
            println("legacy r=$r distance from the circular arc = ${worst / r} r")
            assertTrue(worst < 0.2 * r)
        }
    }
}
