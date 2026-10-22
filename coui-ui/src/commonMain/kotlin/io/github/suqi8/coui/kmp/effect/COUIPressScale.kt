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
 * The `COUIPressAnimatorUnit` spring for a [width] x [height] view (any unit) moving towards the
 * pressed state ([pressed] = `true`) or back to rest.
 *
 * `PressParamsExtKt.pressParamsDefault`, as `SpringData(bounce, response)` with
 * damping ratio = 1 - bounce and stiffness = (2π / response)²:
 * - jelly (aspect ratio <= 2, squares, circles and small buttons): press (0, 0.25) =
 *   k 631.65 / ζ 1, release (0.6, 0.5) = k 157.91 / ζ 0.4;
 * - candy (aspect ratio > 2, most text buttons): press (0, 0.3) = k 438.65 / ζ 1, release
 *   (0.5, 0.5) = k 157.91 / ζ 0.5.
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
 * The `COUITouchMotionAnimator` press scale: while [pressed], the element springs down to
 * [COUIMaterialEffect.pressTargetScale] of its own dp size on the [pressSpring] matching its
 * shape, and springs back with the under-damped release when let go.
 *
 * The graphics layer is always attached, so toggling [pressed] never swaps modifier nodes.
 *
 * @param pressed Whether the element is pressed.
 * @param enabled Whether the scale runs. When `false` the element snaps back to rest, as
 *   `COUIButton.setEnabled(false)` resets every touch unit.
 */
@Composable
internal fun Modifier.couiPressScale(
    pressed: Boolean,
    enabled: Boolean = true,
): Modifier {
    val fraction = remember { Animatable(0f) }
    // The latest measured dp size, written from the layer block and read when the press starts.
    val size = remember { FloatArray(2) }
    LaunchedEffect(pressed, enabled) {
        if (!enabled) {
            fraction.snapTo(0f)
        } else {
            val target = if (pressed) 1f else 0f
            if (fraction.value != target) {
                fraction.animateTo(target, pressSpring(size[0], size[1], pressed))
            }
        }
    }
    return this.graphicsLayer {
        val widthDp = this.size.width / density
        val heightDp = this.size.height / density
        size[0] = widthDp
        size[1] = heightDp
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
