// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.nav.transition

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

/**
 * Pins [NavTransitions.COUIDefault] to the COUI 17 page switch: `Animation.COUI.Activity`
 * (`coui_open_slide_*` / `coui_close_slide_*`, 350ms, covered page -30%) for programmatic
 * push/pop, and the ColorOS predictive back (`CrossBackAnimationExt`: revealed page at
 * `-(width - x) / 4`, critically damped 400 / 450 release springs) while a gesture drives.
 */
class NavCOUIPageTransitionTest {

    @Test
    fun pageMotion_matchesTheOfficialCurves() {
        val programmatic = assertIs<NavSettleSpec.Tween>(NavTransitions.COUIPageMotion.programmatic)
        assertEquals(350, programmatic.durationMillis)
        assertSame(NavTransitions.COUIPageEasing, programmatic.easing)

        val commit = assertIs<NavSettleSpec.Spring>(NavTransitions.COUIPageMotion.commit)
        assertEquals(1f, commit.dampingRatio)
        assertEquals(400f, commit.stiffness)

        val cancel = assertIs<NavSettleSpec.Spring>(NavTransitions.COUIPageMotion.cancel)
        assertEquals(1f, cancel.dampingRatio)
        assertEquals(450f, cancel.stiffness)
    }

    @Test
    fun couiDefault_declaresThePageMotionAndAnOpaqueSlide() {
        val motion = NavTransitions.COUIDefault.motion
        assertSame(NavTransitions.COUIPageMotion.commit, motion.commit)
        assertSame(NavTransitions.COUIPageMotion.cancel, motion.cancel)
        assertSame(NavTransitions.COUIPageMotion.programmatic, motion.programmatic)
        assertEquals(1f, NavTransitions.COUIDefault.opaqueDepth)
        assertEquals(NavSwipeDirection.None, NavTransitions.COUIDefault.dismissDirection)
    }

    @Test
    fun pageEasing_isTheOpenSlideEnterCurve() {
        // coui_open_slide_enter_translate_interpolator = cubic-bezier(0.3, 0.1, 0.3, 1).
        assertEquals(0f, NavTransitions.COUIPageEasing.transform(0f), absoluteTolerance = 1e-4f)
        assertEquals(0.228f, NavTransitions.COUIPageEasing.transform(0.2f), absoluteTolerance = 3e-3f)
        assertEquals(0.767f, NavTransitions.COUIPageEasing.transform(0.5f), absoluteTolerance = 3e-3f)
        assertEquals(1f, NavTransitions.COUIPageEasing.transform(1f), absoluteTolerance = 1e-4f)
    }

    @Test
    fun geometry_enteringPageSlidesTheFullWidth() {
        assertEquals(540f, couiPageTranslationX(relativeDepth = -0.5f, width = 1080f, rtl = false, coveredShift = 0.3f))
        assertEquals(-540f, couiPageTranslationX(relativeDepth = -0.5f, width = 1080f, rtl = true, coveredShift = 0.3f))
        assertEquals(0f, couiPageTranslationX(relativeDepth = 0f, width = 1080f, rtl = false, coveredShift = 0.3f))
    }

    @Test
    fun geometry_coveredPageShiftsThirtyPercent_andAQuarterUnderTheGesture() {
        val programmatic = NavTransitions.COUI_COVERED_SHIFT_FRACTION
        val gesture = NavTransitions.COUI_GESTURE_COVERED_SHIFT_FRACTION
        assertEquals(-324f, couiPageTranslationX(relativeDepth = 1f, width = 1080f, rtl = false, coveredShift = programmatic), absoluteTolerance = 1e-3f)
        assertEquals(324f, couiPageTranslationX(relativeDepth = 1f, width = 1080f, rtl = true, coveredShift = programmatic), absoluteTolerance = 1e-3f)
        // ColorOS predictive back: a closing page at x = 0.4 * width puts the revealed page at
        // -(width - x) / 4; the covered depth is then 1 - 0.4 = 0.6.
        assertEquals(-(1080f - 432f) / 4f, couiPageTranslationX(relativeDepth = 0.6f, width = 1080f, rtl = false, coveredShift = gesture), absoluteTolerance = 1e-3f)
    }
}
