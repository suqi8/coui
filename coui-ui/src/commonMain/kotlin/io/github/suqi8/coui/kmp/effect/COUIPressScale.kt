// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.max

/**
 * The aspect ratio above which `PressCalc` blends in the aspect-ratio channel and
 * `COUIPressAnimatorUnit` switches from the jelly to the candy springs.
 */
internal val PressAspectRatioThreshold = 2f

/** `max(w / h, h / w)`, or 1 for an empty size. */
internal fun pressAspectRatio(width: Float, height: Float): Float = if (width <= 0f || height <= 0f) 1f else max(width / height, height / width)

/**
 * `TouchMotionMinVisibleChange.clampPressBaseline(CommonParams.pressMinimumVisibleChange)`: the
 * press-in spring of `COUIPressAnimatorUnit` settles once it is within 0.001 of its target scale.
 */
internal val PressMinimumVisibleChange = 0.001f

/**
 * `CommonParams.pressHandUpMinimumVisibleChange` (`TouchMotionMinVisibleChange.PRESS_HAND_UP_DEFAULT`):
 * the release spring of `COUIPressAnimatorUnit.backAnimation` runs on until it is within 0.0001 of
 * the rest scale, so its last bounces fade out instead of being cut short.
 */
internal val PressHandUpMinimumVisibleChange = 1.0e-4f

/**
 * The `COUIPressAnimatorUnit` spring for a [width] x [height] view (any unit) moving towards the
 * pressed state ([pressed] = `true`) or back to rest.
 *
 * `PressParamsExtKt.pressParamsDefault`, as `SpringData(bounce, response)` with
 * damping ratio = 1 - bounce and stiffness = (2π / response)²:
 * - jelly (aspect ratio <= 2, squares, circles and small buttons): press (0, 0.25) =
 *   k 631.65 / ζ 1, release (0.6, 0.5) = k 157.91 / ζ 0.4;
 * - candy (aspect ratio > 2, most text buttons): press (0, 0.3) = k 438.65 / ζ 1, release
 *   (0.5, 0.5) = k 157.91 / ζ 0.5.
 *
 * The spec carries no visibility threshold, so it suits a 0..1 press fraction. Animate a scale with
 * [pressScaleSpring], which settles at the official minimum visible changes.
 */
internal fun pressSpring(width: Float, height: Float, pressed: Boolean): SpringSpec<Float> {
    val candy = pressAspectRatio(width, height) > PressAspectRatioThreshold
    return when {
        candy && pressed -> CandyPressSpring
        candy -> CandyReleaseSpring
        pressed -> JellyPressSpring
        else -> JellyReleaseSpring
    }
}

/**
 * [pressSpring] for an animation of the scale itself: it settles at the `COUIPressAnimatorUnit`
 * minimum visible changes, [PressMinimumVisibleChange] while pressing and
 * [PressHandUpMinimumVisibleChange] on release. A spring without a threshold stops 0.01 short of
 * its target and jumps the rest, a visible 1% hop at the end of the press and of the release bounce.
 */
internal fun pressScaleSpring(width: Float, height: Float, pressed: Boolean): SpringSpec<Float> {
    val candy = pressAspectRatio(width, height) > PressAspectRatioThreshold
    return when {
        candy && pressed -> CandyPressScaleSpring
        candy -> CandyReleaseScaleSpring
        pressed -> JellyPressScaleSpring
        else -> JellyReleaseScaleSpring
    }
}

/**
 * [pressSpring] for a 0..1 press fraction of a [width] x [height] dp view: the official minimum
 * visible changes, which are in scale units, divided by the view's press amount.
 */
private fun pressFractionSpring(width: Float, height: Float, pressed: Boolean): SpringSpec<Float> {
    val base = pressSpring(width, height, pressed)
    val amount = (1f - COUIMaterialEffect.pressTargetScale(width, height)).coerceAtLeast(MinPressAmount)
    val change = if (pressed) PressMinimumVisibleChange else PressHandUpMinimumVisibleChange
    return spring(dampingRatio = base.dampingRatio, stiffness = base.stiffness, visibilityThreshold = change / amount)
}

/**
 * The `COUITouchMotionAnimator` press scale: while [pressed], the element springs down to
 * [COUIMaterialEffect.pressTargetScale] of its own dp size on the [pressSpring] matching its
 * shape, and springs back with the under-damped release when let go.
 *
 * As in `COUIPressAnimatorUnit`, a press always starts its spring from rest (the touch down cancels
 * the running spring and restarts it with the impact velocity, 0 by default), while a release
 * re-targets the running spring and keeps its momentum, so a quick tap sinks a little further before
 * it bounces back.
 *
 * The graphics layer is always attached, so toggling [pressed] never swaps modifier nodes.
 *
 * @param pressed Whether the element is pressed.
 * @param enabled Whether the scale runs. When `false` the element springs back to rest, as
 *   `COUIStateEffectDrawable` releases its press feedback and `COUIButton.setEnabled(false)` resets
 *   every touch unit with animation (`COUITouchMotionAnimator.reset(true)`).
 */
@Composable
internal fun Modifier.couiPressScale(
    pressed: Boolean,
    enabled: Boolean = true,
): Modifier {
    val fraction = remember { Animatable(0f) }
    // [0] and [1]: the latest measured dp size, written from the layer block and read when the press
    // starts. [2]: the velocity of the running spring, sampled every frame, so the next spring can
    // take over its momentum after this one is cancelled (a cancelled Animatable forgets it).
    val state = remember { FloatArray(3) }
    LaunchedEffect(pressed, enabled) {
        val down = enabled && pressed
        val target = if (down) 1f else 0f
        val start = if (down) 0f else state[2]
        state[2] = start
        if (fraction.value != target || start != 0f) {
            fraction.animateTo(target, pressFractionSpring(state[0], state[1], down), start) {
                state[2] = velocity
            }
        }
        state[2] = 0f
    }
    return this.graphicsLayer {
        val widthDp = this.size.width / density
        val heightDp = this.size.height / density
        state[0] = widthDp
        state[1] = heightDp
        val progress = fraction.value
        if (progress != 0f) {
            val scale = 1f + (COUIMaterialEffect.pressTargetScale(widthDp, heightDp) - 1f) * progress
            scaleX = scale
            scaleY = scale
        } else {
            scaleX = 1f
            scaleY = 1f
        }
    }
}

private val JellyPressSpring = spring<Float>(dampingRatio = 1f, stiffness = 631.65f)
private val JellyReleaseSpring = spring<Float>(dampingRatio = 0.4f, stiffness = 157.91f)
private val CandyPressSpring = spring<Float>(dampingRatio = 1f, stiffness = 438.65f)
private val CandyReleaseSpring = spring<Float>(dampingRatio = 0.5f, stiffness = 157.91f)

private val JellyPressScaleSpring = spring(dampingRatio = 1f, stiffness = 631.65f, visibilityThreshold = PressMinimumVisibleChange)
private val JellyReleaseScaleSpring = spring(dampingRatio = 0.4f, stiffness = 157.91f, visibilityThreshold = PressHandUpMinimumVisibleChange)
private val CandyPressScaleSpring = spring(dampingRatio = 1f, stiffness = 438.65f, visibilityThreshold = PressMinimumVisibleChange)
private val CandyReleaseScaleSpring = spring(dampingRatio = 0.5f, stiffness = 157.91f, visibilityThreshold = PressHandUpMinimumVisibleChange)

/** The smallest press amount (1 - target scale) a fraction threshold is derived from. */
private val MinPressAmount = 0.001f
