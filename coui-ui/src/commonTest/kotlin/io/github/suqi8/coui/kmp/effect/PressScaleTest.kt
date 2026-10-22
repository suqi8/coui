// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PressScaleTest {

    @Test
    fun wideButtonsBlendTheAspectRatioChannel() {
        // PressCalc.calculateUniformScaleBreakdown reference values (dp).
        assertClose(0.9655f, COUIMaterialEffect.pressTargetScale(296f, 44f))
        assertClose(0.9202f, COUIMaterialEffect.pressTargetScale(132f, 44f))
        assertClose(0.9124f, COUIMaterialEffect.pressTargetScale(96f, 28f))
    }

    @Test
    fun compactButtonsUseTheAreaChannelOnly() {
        // 52 x 28dp has an aspect ratio below 2, so only the area channel applies.
        assertClose(0.8859f, COUIMaterialEffect.pressTargetScale(52f, 28f))
    }

    @Test
    fun squareAndToolbarButtonsAreUnchanged() {
        // The top-bar buttons only ever hit the area channel: a 40dp circle and a 48 x 40dp capsule.
        assertClose(areaOnly(40f, 40f), COUIMaterialEffect.pressTargetScale(40f, 40f))
        assertClose(0.889f, COUIMaterialEffect.pressTargetScale(40f, 40f))
        assertClose(areaOnly(48f, 40f), COUIMaterialEffect.pressTargetScale(48f, 40f))
    }

    @Test
    fun springFamilyFollowsTheAspectRatio() {
        // Candy springs (ar > 2): press k 438.65 / zeta 1, release k 157.91 / zeta 0.5.
        assertEquals(438.65f, pressSpring(132f, 44f, pressed = true).stiffness)
        assertEquals(1f, pressSpring(132f, 44f, pressed = true).dampingRatio)
        assertEquals(157.91f, pressSpring(132f, 44f, pressed = false).stiffness)
        assertEquals(0.5f, pressSpring(132f, 44f, pressed = false).dampingRatio)
        // Jelly springs (ar <= 2): press k 631.65 / zeta 1, release k 157.91 / zeta 0.4.
        assertEquals(631.65f, pressSpring(40f, 40f, pressed = true).stiffness)
        assertEquals(0.4f, pressSpring(52f, 28f, pressed = false).dampingRatio)
    }

    /** The area channel alone, as the toolbar implementation computed it before. */
    private fun areaOnly(width: Float, height: Float): Float {
        val t = ((width * height).coerceIn(400f, 22500f) - 400f) / 22100f
        // cubic-bezier(0, 0, 0, 1): x = u^3, y = 3u^2 - 2u^3.
        var lo = 0f
        var hi = 1f
        repeat(30) {
            val mid = (lo + hi) / 2f
            if (mid * mid * mid < t) lo = mid else hi = mid
        }
        val u = (lo + hi) / 2f
        return 0.85f + (3f * u * u - 2f * u * u * u) * 0.12f
    }

    private fun assertClose(expected: Float, actual: Float, tolerance: Float = 0.001f) {
        assertTrue(abs(expected - actual) <= tolerance, "expected $expected but was $actual")
    }
}
