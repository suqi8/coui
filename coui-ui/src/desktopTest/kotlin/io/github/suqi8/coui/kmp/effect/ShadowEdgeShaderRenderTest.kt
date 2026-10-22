// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asSkikoRuntimeShader
import org.jetbrains.skia.Color
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Surface
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ShadowEdgeShaderRenderTest {

    @Test
    fun circleRendersEdgeInsideTheShapeOnly() {
        val size = 168
        val density = 3.5f
        val shader = RuntimeShader(ShadowEdgeShader.Source)
        shader.setFloatUniform("u_commonArray", ShadowEdgeShader.commonArray(168f, 168f, 84f, 2f))
        shader.setFloatUniform("u_shadow", 1f)
        shader.setFloatUniform(
            "u_shadowArray",
            ShadowEdgeShader.shadowArray(ShadowEdgePresets.CircleShadow, 168f, 168f, 1f, 0.15f, 1f, density),
        )
        shader.setFloatUniform("u_edge", 1f)
        shader.setFloatUniform(
            "u_edgeArray",
            ShadowEdgeShader.edgeArray(ShadowEdgePresets.CircleEdge, 168f, 168f, 1.9f * density, 0.2f, 0f, density),
        )

        val surface = Surface.makeRasterN32Premul(size, size)
        surface.canvas.clear(Color.TRANSPARENT)
        surface.canvas.drawPaint(Paint().apply { this.shader = shader.asSkikoRuntimeShader().makeShader() })
        val image = surface.makeImageSnapshot()
        File("build/shadow-edge").mkdirs()
        image.encodeToData(EncodedImageFormat.PNG)?.bytes?.let { File("build/shadow-edge/circle-48dp.png").writeBytes(it) }

        val bitmap = org.jetbrains.skia.Bitmap().apply { allocN32Pixels(size, size) }
        surface.readPixels(bitmap, 0, 0)
        fun alpha(x: Int, y: Int) = Color.getA(bitmap.getColor(x, y))

        // Outside the circle: fully transparent.
        assertEquals(0, alpha(2, 2))
        // The rim carries the edge lighting somewhere along the perimeter.
        val rim = listOf(alpha(84, 2), alpha(84, 165), alpha(2, 84), alpha(165, 84))
        assertTrue(rim.max() > 0, "rim alphas $rim")
    }

    @Test
    fun spotlightLightsAroundTheTouchOnly() {
        // Wider than the 500px light (whose 200px height keeps every point at least 200px away).
        val size = 700
        val shader = RuntimeShader(SpotLightShader.Source)
        SpotLightShader.setUniforms(shader, lightX = 30f, lightY = 30f, radius = SpotLightShader.EndRadius, intensity = 1f)

        val surface = Surface.makeRasterN32Premul(size, size)
        surface.canvas.clear(Color.TRANSPARENT)
        surface.canvas.drawPaint(Paint().apply { this.shader = shader.asSkikoRuntimeShader().makeShader() })
        val bitmap = org.jetbrains.skia.Bitmap().apply { allocN32Pixels(size, size) }
        surface.readPixels(bitmap, 0, 0)

        assertTrue(Color.getA(bitmap.getColor(30, 30)) > 0, "lit under the touch point")
        assertEquals(0, Color.getA(bitmap.getColor(690, 690)), "dark beyond the light radius")
    }
}
