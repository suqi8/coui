// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.VectorConverter
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Checks the COUI 17 functional (cancel) button frames from the `mFunBtnSpringAnimation` update
 * listener in `COUISearchBar`: `scale = 0.5 + 0.5p`, `translationX = offset * (1 - p)`,
 * `alpha = remapAlphaProgress(p, 0.2)` with the label at `p`, and `blur = 30 * (1 - p)` for a
 * blurred button. Expanding and collapsing share the function and the spring.
 */
class SearchBarMotionTest {

    private val spring = StateChangeSpring.vectorize(Float.VectorConverter)

    /** The edit-state progress [millis] into a change from [from] to [to], at rest initially. */
    private fun progressAt(millis: Long, from: Float, to: Float): Float = spring.getValueFromNanos(
        playTimeNanos = millis * 1_000_000L,
        initialValue = AnimationVector1D(from),
        targetValue = AnimationVector1D(to),
        initialVelocity = AnimationVector1D(0f),
    ).value

    /** 60 fps frames over the first second of a collapse. */
    private val collapseCurve = (0L..1000L step 16L).map { progressAt(it, from = 1f, to = 0f) }

    @Test
    fun frame_atRestBothWays() {
        val expanded = searchBarActionFrame(1f, blurred = true)
        assertEquals(1f, expanded.scale, 1e-6f)
        assertEquals(0f, expanded.translationPx, 1e-6f)
        assertEquals(1f, expanded.alpha, 1e-6f)
        assertEquals(1f, expanded.textAlpha, 1e-6f)
        assertEquals(0f, expanded.blurPx, 1e-6f)
        val collapsed = searchBarActionFrame(0f, blurred = true)
        assertEquals(0.5f, collapsed.scale, 1e-6f)
        assertEquals(20f, collapsed.translationPx, 1e-6f)
        assertEquals(0f, collapsed.alpha, 1e-6f)
        assertEquals(30f, collapsed.blurPx, 1e-6f)
    }

    @Test
    fun collapseCurve_settlesAtZeroWithASmallUndershoot() {
        assertEquals(1f, collapseCurve.first(), 1e-4f)
        assertEquals(0f, collapseCurve.last(), 2e-3f)
        val lowest = collapseCurve.min()
        // Bounce 0.25 (damping 0.75) undershoots by e^(-pi * 0.75 / sqrt(1 - 0.75^2)), about 2.8%.
        assertTrue(lowest < 0f && lowest > -0.04f, "undershoot $lowest")
    }

    @Test
    fun collapse_mirrorsExpandOnTheSameSpring() {
        for (millis in 0L..1000L step 16L) {
            val expand = progressAt(millis, from = 0f, to = 1f)
            val collapse = progressAt(millis, from = 1f, to = 0f)
            assertEquals(1f, expand + collapse, 1e-4f, "at $millis ms")
        }
    }

    @Test
    fun collapseFrames_followTheFunctionButtonListener() {
        for (p in collapseCurve) {
            val frame = searchBarActionFrame(p, blurred = true)
            assertEquals(0.5f + 0.5f * p, frame.scale, 1e-5f)
            assertEquals(20f * (1f - p), frame.translationPx, 1e-4f)
            assertEquals(30f * maxOf(1f - p, 0f), frame.blurPx, 1e-4f)
            val clamped = p.coerceIn(0f, 1f)
            val expectedAlpha = if (clamped < 0.2f) clamped / 0.2f else 1f
            assertEquals(expectedAlpha, frame.alpha, 1e-5f)
            // The label colour carries p inside a view at the remapped alpha.
            assertEquals(expectedAlpha * clamped, frame.alpha * frame.textAlpha, 1e-5f)
        }
    }

    @Test
    fun collapse_dissolvesIntoBlurBeforeFading() {
        // The capsule stays opaque while it blurs; once it starts to fade it is already blurred by
        // at least 30 * (1 - 0.2) = 24px, so it never pops out as a sharp shape.
        val firstFading = collapseCurve.first { searchBarActionFrame(it, blurred = true).alpha < 1f }
        assertTrue(searchBarActionFrame(firstFading, blurred = true).blurPx >= 24f)
        val labelAtHalf = searchBarActionFrame(0.5f, blurred = true)
        assertEquals(0.5f, labelAtHalf.alpha * labelAtHalf.textAlpha, 1e-6f)
    }

    @Test
    fun unblurredButton_fadesLinearlyWithoutBlur() {
        for (p in collapseCurve) {
            val frame = searchBarActionFrame(p, blurred = false)
            assertEquals(p.coerceIn(0f, 1f), frame.alpha, 1e-6f)
            assertEquals(1f, frame.textAlpha, 1e-6f)
            assertEquals(0f, frame.blurPx, 1e-6f)
        }
    }

    @Test
    fun collapseUndershoot_carriesTheCapsulePastItsNormalWidth() {
        // doWidthChangedAnimate: right = left + max + (min - max) * p, so below 0 the capsule is wider
        // than at rest by (gap + button) * -p. For a 12dp gap and a 60dp cancel button at 3x density
        // (216px) the bounce-0.25 undershoot opens a few pixels, well inside the 20px COUI lays the
        // background out wider by.
        val reachPx = 216f
        val overshootPx = reachPx * -collapseCurve.min()
        assertTrue(overshootPx > 3f && overshootPx < 20f, "overshoot $overshootPx")
    }

    // --- edit frame: SearchBarBackgroundView.doBeforeAnimate / updateEditFrameClip ---

    /** The edit frame width at [progress] for a 900px normal frame that shrinks by 216px. */
    private fun frameWidthAt(progress: Float): Int = 900 - (216 * progress).roundToInt()

    @Test
    fun editFrame_keepsItsNormalWidthThroughMostOfTheChange() {
        for (p in listOf(0.05f, 0.25f, 0.5f, 0.8f)) {
            assertEquals(900, searchBarEditFrameMeasureWidth(frameWidthAt(p), shrinkPx = 216, progress = p), "at $p")
        }
    }

    @Test
    fun editFrame_snapsToItsEditWidthWithinThirtyPixels() {
        // 216 * 0.9 = 194px in: 22px from the 684px edit width.
        assertEquals(684, searchBarEditFrameMeasureWidth(frameWidthAt(0.9f), shrinkPx = 216, progress = 0.9f))
        // The overshoot past the edit width stays measured at the edit width, clipped.
        assertEquals(684, searchBarEditFrameMeasureWidth(frameWidthAt(1.02f), shrinkPx = 216, progress = 1.02f))
        // The undershoot of a collapse is measured at the normal width, inside the wider capsule.
        assertEquals(900, searchBarEditFrameMeasureWidth(frameWidthAt(-0.03f), shrinkPx = 216, progress = -0.03f))
    }

    @Test
    fun editFrame_measuresAsItIsWhenItDoesNotShrink() {
        assertEquals(700, searchBarEditFrameMeasureWidth(700, shrinkPx = 0, progress = 0.5f))
        assertEquals(700, searchBarEditFrameMeasureWidth(700, shrinkPx = -40, progress = 0.5f))
    }

    // --- search history expand / fold button: COUIPressFeedbackImageView ---

    @Test
    fun historyTogglePress_runsToNinetyPercentOnMoveEase() {
        assertEquals(1f, searchHistoryTogglePressScale(from = 1f, elapsedMillis = 0f), 1e-6f)
        assertEquals(0.9f, searchHistoryTogglePressScale(from = 1f, elapsedMillis = 200f), 1e-6f)
        assertEquals(0.9f, searchHistoryTogglePressScale(from = 1f, elapsedMillis = 500f), 1e-6f)
        // COUIMoveEaseInterpolator (0.3, 0, 0.1, 1) has covered most of the way by the 160ms hold
        // point a quick tap waits for.
        val atHold = searchHistoryTogglePressScale(from = 1f, elapsedMillis = 160f)
        assertTrue(atHold < 0.91f && atHold > 0.9f, "scale at the hold point $atHold")
    }

    @Test
    fun historyTogglePress_continuesFromAPartlyReleasedScale() {
        assertEquals(0.95f, searchHistoryTogglePressScale(from = 0.95f, elapsedMillis = 0f), 1e-6f)
        assertEquals(0.9f, searchHistoryTogglePressScale(from = 0.95f, elapsedMillis = 200f), 1e-6f)
        // setScale clamps to [0.9, 1].
        for (millis in 0..200 step 10) {
            val scale = searchHistoryTogglePressScale(from = 1f, elapsedMillis = millis.toFloat())
            assertTrue(scale in 0.9f..1f, "scale $scale at $millis ms")
        }
    }
}
