// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import androidx.compose.ui.graphics.Color
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asSkikoRuntimeShader
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Surface
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConicLightShaderRenderTest {

    @Test
    fun playingLightTrailsBehindTheThumbOnly() {
        // A 1000px track at 3.5x with the thumb in the middle and the playing parameters.
        val trackLength = 1000f
        val width = (trackLength + 2f * ConicLightShader.HORIZONTAL_PADDING).toInt()
        val height = ConicLightShader.LIGHT_HEIGHT.toInt()
        val shader = RuntimeShader(ConicLightShader.Source)
        ConicLightShader.setUniforms(
            shader = shader,
            width = width.toFloat(),
            process = 0.5f,
            trackLength = trackLength,
            height = 0.6f,
            heightFalloff = 0.6f,
            alpha1 = 1f,
            alpha2 = 0.6f,
            w0 = 0.008f,
            coneK = 0.2f,
            secondConeK = 0.2f,
            isRtl = false,
            color = Color(0xFFF87796),
        )

        val surface = Surface.makeRasterN32Premul(width, height)
        surface.canvas.clear(org.jetbrains.skia.Color.TRANSPARENT)
        surface.canvas.drawPaint(Paint().apply { this.shader = shader.asSkikoRuntimeShader().makeShader() })
        val bitmap = org.jetbrains.skia.Bitmap().apply { allocN32Pixels(width, height) }
        surface.readPixels(bitmap, 0, 0)
        fun alpha(x: Int, y: Int) = org.jetbrains.skia.Color.getA(bitmap.getColor(x, y))

        val thumbX = (ConicLightShader.HORIZONTAL_PADDING + 0.5f * trackLength).toInt()
        val centerY = height / 2
        assertTrue(alpha(thumbX - 60, centerY) > 0, "lit behind the thumb")
        assertEquals(0, alpha(thumbX + 60, centerY), "dark ahead of the thumb")
    }

    @Test
    fun rtlMirrorsTheCone() {
        val trackLength = 1000f
        val width = (trackLength + 2f * ConicLightShader.HORIZONTAL_PADDING).toInt()
        val height = ConicLightShader.LIGHT_HEIGHT.toInt()
        val shader = RuntimeShader(ConicLightShader.Source)
        ConicLightShader.setUniforms(
            shader = shader,
            width = width.toFloat(),
            process = 0.5f,
            trackLength = trackLength,
            height = 0.2f,
            heightFalloff = 2f,
            alpha1 = 0.3f,
            alpha2 = 0.3f,
            w0 = 0.014f,
            coneK = 0f,
            secondConeK = 0f,
            isRtl = true,
            color = Color(0xFFF87796),
        )

        val surface = Surface.makeRasterN32Premul(width, height)
        surface.canvas.clear(org.jetbrains.skia.Color.TRANSPARENT)
        surface.canvas.drawPaint(Paint().apply { this.shader = shader.asSkikoRuntimeShader().makeShader() })
        val bitmap = org.jetbrains.skia.Bitmap().apply { allocN32Pixels(width, height) }
        surface.readPixels(bitmap, 0, 0)
        fun alpha(x: Int, y: Int) = org.jetbrains.skia.Color.getA(bitmap.getColor(x, y))

        val thumbX = (ConicLightShader.HORIZONTAL_PADDING + 0.5f * trackLength).toInt()
        val centerY = height / 2
        assertTrue(alpha(thumbX + 40, centerY) > 0, "lit behind the thumb (to its right in RTL)")
        assertEquals(0, alpha(thumbX - 40, centerY), "dark ahead of the thumb")
    }
}
