// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class ShadowEdgeShaderTest {

    // A 48dp circle on the 3.5x reference device: 168px, CIRCLE_1 light, 1.9dp line.
    private val sizePx = 168f
    private val density = 3.5f

    @Test
    fun edgeArrayMatchesGradientStrokeStyleMapper() {
        val edge = ShadowEdgeShader.edgeArray(
            spec = ShadowEdgePresets.CircleEdge,
            width = sizePx,
            height = sizePx,
            lineWidth = 1.9f * density,
            lineAlpha = ShadowEdgePresets.CircleLight.edgeAlpha,
            lineAngle = 0f,
            density = density,
        )
        // Near band (2, 6) scaled by 6.65 / 8, floored at 2px, stored as (start, start + width).
        assertClose(2f, edge[3])
        assertClose(6.9875f, edge[4])
        assertClose(4.49375f, edge[6])
        assertClose(0.14f, edge[7])
        assertClose(0.2f, edge[8])
        // Fade (0.06, 0.4) re-fitted to a 48dp perimeter by rectLineFadeForSize.
        assertClose(0.034667f, edge[9])
        assertClose(0.488f, edge[10])
    }

    @Test
    fun shadowArrayMatchesInnerShadowStyleMapper() {
        val shadow = ShadowEdgeShader.shadowArray(
            spec = ShadowEdgePresets.CircleShadow,
            width = sizePx,
            height = sizePx,
            fadeWidthScale = 1f,
            fadeAlphaInScale = ShadowEdgePresets.CircleLight.shadowFadeIn,
            fadeAlphaOutScale = ShadowEdgePresets.CircleLight.shadowFadeOut,
            density = density,
        )
        assertClose(0.06f, shadow[3])
        assertClose(0f, shadow[5])
        assertClose(166.6f, shadow[6])
        assertClose(23.8f, shadow[8])
        assertClose(1.453333f, shadow[9])
    }

    private fun assertClose(expected: Float, actual: Float) {
        assertTrue(abs(expected - actual) < 1e-3f, "expected $expected but was $actual")
    }
}
