// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * The fling of the COUI 17 lists and scroll views (`COUIRecyclerView`, `COUINestedScrollView`,
 * `COUIScrollView`, `COUIHorizontalScrollView`), which all fling through `SpringOverScroller`
 * (`ReboundOverScroller.fling` with an unbounded range):
 *
 * - the distance and duration come from a friction simulation of the release speed (friction 2.5
 *   below 10000px/s, `adjustFrictionByStartVelocity`, Origami 0.2 above), stepped at the display
 *   frame interval until a frame moves less than 0.2 to 0.35px;
 * - the distance is scaled by 1 at up to 2000px/s, down to 0.8 at 6000px/s and 0.4 at 20000px/s,
 *   the duration by 1.3, down to 0.8 and 0.5 (`getDistanceScale`, `getDurationScale`);
 * - the content then follows `COUISpringInterpolator` (stiffness 40, damping ratio 1.15, the
 *   release speed over 15000) over that duration: a long, soft glide that slows down smoothly
 *   instead of the Android spline's quick stop.
 *
 * A fling that reaches the end of the content stops there and returns its current speed, which
 * the COUI overscroll ([COUIOverscrollEffect], the theme default) carries on past the edge.
 *
 * Compose has no theme-wide fling, so pass it to each scrollable:
 * `LazyColumn(flingBehavior = rememberCOUIFlingBehavior())`.
 */
@Stable
class COUIFlingBehavior : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        // DefaultFlingBehavior leaves a speed this low untouched.
        if (abs(initialVelocity) <= MIN_FLING_VELOCITY) return initialVelocity
        var velocityLeft = 0f
        var lastPosition = 0.0
        COUIReboundOverScroller().runFling(start = 0, velocity = initialVelocity.roundToInt()) { position, velocity ->
            val delta = (position - lastPosition).toFloat()
            lastPosition = position
            val consumed = scrollBy(delta)
            if (abs(delta - consumed) > EDGE_TOLERANCE) {
                // The content ran out: hand the current speed to the overscroll.
                velocityLeft = velocity.toFloat()
                false
            } else {
                true
            }
        }
        return velocityLeft
    }

    override fun equals(other: Any?): Boolean = other is COUIFlingBehavior

    override fun hashCode(): Int = COUIFlingBehavior::class.hashCode()

    private companion object {
        /** `DefaultFlingBehavior`: a release this slow (px/s) does not fling. */
        const val MIN_FLING_VELOCITY = 1f

        /** `DefaultFlingBehavior`: a step that loses more than this (px) has reached the edge. */
        const val EDGE_TOLERANCE = 0.5f
    }
}

/**
 * Creates and remembers a [COUIFlingBehavior], the fling of the COUI 17 lists, for a scrollable's
 * `flingBehavior` (for example `LazyColumn`, `LazyRow`, `Modifier.verticalScroll`).
 */
@Composable
fun rememberCOUIFlingBehavior(): FlingBehavior = remember { COUIFlingBehavior() }
