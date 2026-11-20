// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TouchMotionTest {

    @Test
    fun rubberBandFollowsTheCouiCurve() {
        // (1 - 1 / (c·|x| / max + 1)) · max · sign(x)
        assertEquals(0f, TouchMotion.rubberBand(0f, 0.05f, 98f))
        assertClose(4.6667f, TouchMotion.rubberBand(98f, 0.05f, 98f))
        assertClose(-4.6667f, TouchMotion.rubberBand(-98f, 0.05f, 98f))
        // Saturates at the ceiling but never exceeds it.
        val far = TouchMotion.rubberBand(1_000_000f, 0.05f, 98f)
        assertTrue(far < 98f && far > 97f, "far drag: $far")
        assertEquals(0f, TouchMotion.rubberBand(50f, 0.05f, 0f))
    }

    @Test
    fun inverseRubberBandUndoesTheCurve() {
        listOf(-300f, -12f, 3f, 40f, 250f).forEach { x ->
            val y = TouchMotion.rubberBand(x, 0.05f, 98f)
            assertClose(x, TouchMotion.inverseRubberBand(y, 0.05f, 98f), tolerance = 0.01f)
        }
        // Beyond the ceiling the inverse clamps to it.
        assertEquals(98f, TouchMotion.inverseRubberBand(120f, 0.05f, 98f))
    }

    @Test
    fun deformationCeilingShrinksWithArea() {
        // A 40dp button at 3.5x (140px) stays under K_MIN_AREA.
        assertEquals(1.2f, TouchMotion.deformMaxScale(140f, 140f))
        // A 132 x 44dp button at 3.5x: log-area and aspect-ratio decay.
        assertClose(1.1582f, TouchMotion.deformMaxScale(462f, 154f))
        // Never below K_MIN_SCALE.
        assertEquals(1.03f, TouchMotion.deformMaxScale(4000f, 100f))
    }

    @Test
    fun deformationConservesArea() {
        val (sx, sy) = TouchMotion.deformScale(120f, 0f, 525f, 1.2f)
        assertTrue(sx > 1f && sy < 1f)
        assertClose(1f, sx * sy)

        val (vx, vy) = TouchMotion.deformScale(0f, -80f, 525f, 1.2f)
        assertTrue(vy > 1f && vx < 1f)
        assertClose(1f, vx * vy)

        val (dx, dy) = TouchMotion.deformScale(60f, 60f, 525f, 1.2f)
        assertClose(1f, dx * dy)
        assertClose(1f, dx)

        assertEquals(1f to 1f, TouchMotion.deformScale(0f, 0f, 525f, 1.2f))
    }

    @Test
    fun deformationStaysBelowItsCeiling() {
        val (sx, _) = TouchMotion.deformScale(100_000f, 0f, 525f, 1.2f)
        assertTrue(sx < 1.2f && sx > 1.19f, "saturated stretch: $sx")
        // The effective distance of a scale inverts the stretch.
        val (s, _) = TouchMotion.deformScale(200f, 0f, 525f, 1.2f)
        assertClose(200f, TouchMotion.deformEffectiveDistance(s, 525f, 1.2f), tolerance = 0.1f)
    }

    @Test
    fun handUpSpringEasesWithArea() {
        assertEquals(0.42f to 0.65f, TouchMotion.handUpSpring(140f, 140f, 3.5f))
        val (response, bounce) = TouchMotion.handUpSpring(1000f, 1000f, 3.5f)
        assertClose(0.45f, response)
        assertClose(0.65f, bounce)
        val (mid, _) = TouchMotion.handUpSpring(78f * 3.5f, 78f * 3.5f, 3.5f)
        assertTrue(mid > 0.42f && mid < 0.45f, "mid response: $mid")
    }

    @Test
    fun handUpVelocityAndBounce() {
        assertEquals(0f, TouchMotion.handUpInitialVelocity(2f, 0f, 10f))
        assertClose(0.2f, TouchMotion.handUpInitialVelocity(2f, 100f, 10f))
        assertEquals(1000f, TouchMotion.handUpInitialVelocity(1e9f, 1f, 10f))
        assertEquals(0.65f, TouchMotion.handUpBounce(0.2f, 0.65f))
        assertEquals(1f, TouchMotion.handUpBounce(-5000f, 0.65f))
    }

    @Test
    fun pressScaleSpringsSettleAtTheOfficialMinimumVisibleChanges() {
        // TouchMotionMinVisibleChange.clampPressBaseline(0.001) pressing, PRESS_HAND_UP_DEFAULT
        // (1e-4) releasing; a spring without a threshold would stop 0.01 short of its target.
        assertEquals(0.001f, pressScaleSpring(132f, 44f, pressed = true).visibilityThreshold)
        assertEquals(1.0e-4f, pressScaleSpring(132f, 44f, pressed = false).visibilityThreshold)
        assertEquals(0.001f, pressScaleSpring(40f, 40f, pressed = true).visibilityThreshold)
        assertEquals(1.0e-4f, pressScaleSpring(40f, 40f, pressed = false).visibilityThreshold)
        // Same jelly / candy springs as the fraction variant.
        listOf(132f to 44f, 40f to 40f, 52f to 28f).forEach { (w, h) ->
            listOf(true, false).forEach { pressed ->
                assertEquals(pressSpring(w, h, pressed).stiffness, pressScaleSpring(w, h, pressed).stiffness)
                assertEquals(pressSpring(w, h, pressed).dampingRatio, pressScaleSpring(w, h, pressed).dampingRatio)
            }
        }
    }

    private fun assertClose(expected: Float, actual: Float, tolerance: Float = 0.001f) {
        assertTrue(abs(expected - actual) <= tolerance, "expected $expected but was $actual")
    }
}
