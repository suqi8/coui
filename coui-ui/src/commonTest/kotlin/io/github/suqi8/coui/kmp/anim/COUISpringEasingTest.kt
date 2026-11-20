// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.anim

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class COUISpringEasingTest {

    @Test
    fun theCurveStartsAtZeroAndEndsAtOne() {
        listOf(0.5f to 0f, 0.85f to 0.6f, 0.6f to 0f, 0.85f to 0.4f).forEach { (response, bounce) ->
            val easing = COUISpringEasing(response, bounce)
            assertEquals(0f, easing.transform(0f))
            assertClose(1f, easing.transform(1f))
        }
    }

    @Test
    fun theCurveFollowsCouiSpringInterpolator() {
        // COUISpringInterpolator(response, bounce).getInterpolation reference values.
        assertClose(0.821064f, COUISpringEasing(0.5f, 0f).transform(0.25f))
        assertClose(0.357756f, COUISpringEasing(0.5f, 0f).transform(0.1f))
        assertClose(0.967129f, COUISpringEasing(0.6f, 0f).transform(0.5f))
        assertClose(1.319996f, COUISpringEasing(0.85f, 0.6f).transform(0.5f))
        assertClose(0.728860f, COUISpringEasing(0.85f, 0.4f).transform(0.25f))
    }

    @Test
    fun aCriticallyDampedCurveNeverOvershoots() {
        val easing = COUISpringEasing(0.5f, 0f)
        var previous = 0f
        for (i in 1..100) {
            val value = easing.transform(i / 100f)
            assertTrue(value >= previous && value <= 1.0001f, "not monotonic at ${i / 100f}: $previous -> $value")
            previous = value
        }
    }

    @Test
    fun couiSpringMapsResponseAndBounce() {
        // COUISpringForce: stiffness (2π / response)², damping ratio 1 - bounce.
        val spec = couiSpring<Float>(response = 0.3f, bounce = 0f, visibilityThreshold = 0.001f)
        assertClose(438.65f, spec.stiffness, tolerance = 0.05f)
        assertEquals(1f, spec.dampingRatio)
        assertEquals(0.001f, spec.visibilityThreshold)
        val bouncy = couiSpring<Float>(response = 0.5f, bounce = 0.6f)
        assertClose(157.91f, bouncy.stiffness, tolerance = 0.05f)
        assertClose(0.4f, bouncy.dampingRatio)
    }

    private fun assertClose(expected: Float, actual: Float, tolerance: Float = 0.0005f) {
        assertTrue(abs(expected - actual) <= tolerance, "expected $expected but was $actual")
    }
}
