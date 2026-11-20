// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class COUIOverScrollerTest {

    @Test
    fun dragPastTheEdgeKeepsFortyPercentFallingWithTheOverscroll() {
        assertEquals(40, COUIPhysicalAnimation.calcRealOverScrollDist(100, 0, 1000))
        assertEquals(20, COUIPhysicalAnimation.calcRealOverScrollDist(100, -500, 1000))
        assertEquals(0, COUIPhysicalAnimation.calcRealOverScrollDist(100, 1200, 1000))
    }

    @Test
    fun flingOvershootSaturatesAtAThirdOfTheScreen() {
        // 100 * 373.1 / sqrt(100^2 + 373.1^2) = 96.6
        assertEquals(96, COUIPhysicalAnimation.calcOverFlingDecelerateDist(0, 100, 1000))
        assertEquals(1000 + 373, COUIPhysicalAnimation.calcOverFlingDecelerateDist(1000, 1_000_000, 1000))
        assertEquals(-96, COUIPhysicalAnimation.calcOverFlingDecelerateDist(0, -100, 1000))
    }

    @Test
    fun springBackReturnsToTheEdgeWithoutCrossingIt() {
        val scroller = COUIReboundOverScroller(springBackTensionMultiple = 3.2f)
        assertFalse(scroller.springBack(50, 0, 100), "an in-range position needs no spring-back")
        assertTrue(scroller.springBack(-200, 0, 100))
        var frames = 0
        var time = 0L
        while (scroller.update(time, 1f / 60f)) {
            assertTrue(scroller.position <= 0.5, "overshot to ${scroller.position}")
            time += 16
            frames++
            assertTrue(frames < 600, "spring-back never settled")
        }
        assertTrue(abs(scroller.position) < 0.5, "settled at ${scroller.position}")
    }

    @Test
    fun flingMovesForwardAndStops() {
        val scroller = COUIReboundOverScroller(springBackTensionMultiple = 3.2f)
        scroller.fling(0, 5000, 0L, 1f / 120f)
        val end = scroller.endValue
        assertTrue(end > 500.0, "a 5000px/s fling should travel, ended at $end")
        var last = 0.0
        var time = 0L
        var frames = 0
        while (scroller.update(time, 1f / 120f)) {
            assertTrue(scroller.position >= last - 0.5, "went backwards at $time ms")
            last = scroller.position
            time += 8
            frames++
            assertTrue(frames < 2000, "fling never stopped")
        }
        assertTrue(abs(scroller.position - end) < end * 0.05, "stopped at ${scroller.position}, expected near $end")
    }

    @Test
    fun onlyAFastFlingBackFlingsTheContentFromAnOverscroll() {
        // Pulled down at the top (positive offset): an upward fling is negative.
        assertTrue(COUIOverScrollRelease.isReverseFling(offset = 120f, velocity = -6500f))
        assertFalse(COUIOverScrollRelease.isReverseFling(offset = 120f, velocity = -5000f), "slow flings only spring back")
        assertFalse(COUIOverScrollRelease.isReverseFling(offset = 120f, velocity = 9000f), "a fling further out is not reversed")
        assertTrue(COUIOverScrollRelease.isReverseFling(offset = -80f, velocity = 7000f))
        assertFalse(COUIOverScrollRelease.isReverseFling(offset = 0f, velocity = -9000f))
    }

    @Test
    fun onlyAFastFlingFurtherOutCarriesItsSpeedIntoTheSpringBack() {
        assertEquals(9000f, COUIOverScrollRelease.springBackVelocity(offset = 120f, velocity = 9000f))
        assertEquals(0f, COUIOverScrollRelease.springBackVelocity(offset = 120f, velocity = 3000f))
        assertEquals(0f, COUIOverScrollRelease.springBackVelocity(offset = 120f, velocity = -9000f))
        assertEquals(-7000f, COUIOverScrollRelease.springBackVelocity(offset = -40f, velocity = -7000f))
    }

    @Test
    fun aSpringBackCarryingAFlingRunsOnBeforeReturning() {
        val scroller = COUIReboundOverScroller(springBackTensionMultiple = 2.15f)
        scroller.notifyEdgeReached(start = 100, edge = 0, min = 0, max = 0)
        scroller.velocity = 8000.0
        var peak = scroller.position
        var time = 0L
        var frames = 0
        while (scroller.update(time, 1f / 120f)) {
            peak = maxOf(peak, scroller.position)
            time += 8
            frames++
            assertTrue(frames < 2000, "spring-back never settled")
        }
        assertTrue(peak > 150.0, "the carried speed should push the overscroll further first, peaked at $peak")
        assertTrue(abs(scroller.position) < 0.5, "settled at ${scroller.position}")
    }

    @Test
    fun aFlingBackFromAnOverscrollCrossesTheEdgeAtSpeed() {
        // The curve flingBack follows: from 200px out, 8000px/s back towards the content.
        val scroller = COUIReboundOverScroller(springBackTensionMultiple = 2.15f)
        scroller.fling(200, -8000, 0L, 1f / 120f)
        var time = 0L
        var frames = 0
        while (scroller.update(time, 1f / 120f) && scroller.position > 0.0) {
            time += 8
            frames++
            assertTrue(frames < 100, "never reached the edge")
        }
        assertTrue(scroller.position <= 0.0, "stopped at ${scroller.position} before the edge")
        assertTrue(scroller.velocity < -3000.0, "should hand on most of its speed, had ${scroller.velocity}")
    }

    @Test
    fun springInterpolatorIsNormalized() {
        val curve = COUISpringInterpolator(40.0, 1.15, 5000.0, 1f, 15000f, true)
        assertEquals(0f, curve.getInterpolation(0f), 1e-4f)
        assertEquals(1f, curve.getInterpolation(1f), 1e-4f)
        assertTrue(curve.getInterpolation(0.5f) in 0f..1f)
    }
}
