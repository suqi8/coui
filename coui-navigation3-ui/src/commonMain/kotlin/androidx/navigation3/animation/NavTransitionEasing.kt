// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package androidx.navigation3.animation

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Immutable
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt

@Immutable
internal class NavTransitionEasing(
    response: Float,
    damping: Float,
) : Easing {
    private val r: Float
    private val w: Float
    private val c2: Float

    init {
        val omega = 2.0 * PI / response
        val k = omega * omega
        val c = damping * 4.0 * PI / response

        w = (sqrt(4.0 * k - c * c) / 2.0).toFloat()
        r = (-c / 2.0).toFloat()
        c2 = r / w
    }

    override fun transform(fraction: Float): Float {
        val t = fraction.toDouble()
        val decay = exp(r * t)
        return (decay * (-cos(w * t) + c2 * sin(w * t)) + 1.0).toFloat()
    }

    fun inverseTransform(fraction: Float, tolerance: Float = 1e-6f): Float {
        if (fraction <= 0f) return 0f
        if (fraction >= 1f) return 1f

        var low = 0f
        var high = 1f
        var mid = 0f

        repeat(16) {
            mid = (low + high) / 2f
            val value = transform(mid)
            if (abs(value - fraction) < tolerance) return mid

            if (value < fraction) {
                low = mid
            } else {
                high = mid
            }
        }
        return mid
    }
}

/**
 * A cubic-bezier [Easing] (an Android `pathInterpolator`) that can also be inverted, so a
 * predictive-back seek that interrupts an entering transition can map a physical offset back onto
 * the transition fraction, as [NavTransitionEasing.inverseTransform] does for the spring curve.
 */
@Immutable
internal class NavBezierEasing(
    x1: Float,
    y1: Float,
    x2: Float,
    y2: Float,
) : Easing {
    private val curve = CubicBezierEasing(x1, y1, x2, y2)

    override fun transform(fraction: Float): Float = curve.transform(fraction)

    fun inverseTransform(fraction: Float, tolerance: Float = 1e-6f): Float {
        if (fraction <= 0f) return 0f
        if (fraction >= 1f) return 1f

        var low = 0f
        var high = 1f
        var mid = 0f

        repeat(24) {
            mid = (low + high) / 2f
            val value = transform(mid)
            if (abs(value - fraction) < tolerance) return mid

            if (value < fraction) {
                low = mid
            } else {
                high = mid
            }
        }
        return mid
    }
}

/**
 * The COUI 17 page switch. `Theme.COUI` sets `Animation.COUI.Activity` as the window animation
 * (`coui_open_slide_enter` / `_exit`, `coui_close_slide_enter` / `_exit` in Settings 17
 * `res/anim`), and the ColorOS shell plays the same curves for system apps
 * (`oplus_open_slide_*` / `oplus_close_slide_*` in SystemUI `res/anim`): every part runs 350ms,
 * the entering page slides the full width, the covered page shifts 30% of the width and fades to
 * 0.5 over a black backdrop. The ColorOS predictive back (`CrossBackAnimationExt`) settles a
 * released page on critically damped springs.
 */
internal object COUIPageTransition {
    /** Duration of every part of the page switch. */
    const val DURATION_MILLIS: Int = 350

    /** `coui_open_slide_exit` `toXDelta -29.999996%`: the covered page's shift, as a share of the width. */
    const val COVERED_SHIFT_FRACTION: Float = 0.3f

    /** `CrossBackAnimationExt.DROP_STIFFNESS_VALUE`: the commit spring of a released back gesture (damping 1). */
    const val GESTURE_COMMIT_STIFFNESS: Float = 400f

    /** `CrossBackAnimationExt.DRAG_CANCEL_STIFFNESS_VALUE`: the cancel spring of a released back gesture (damping 1). */
    const val GESTURE_CANCEL_STIFFNESS: Float = 450f

    /** `coui_open_slide_enter_translate_interpolator`: the page sliding in on a push. */
    val OpenEnterEasing: NavBezierEasing = NavBezierEasing(0.3f, 0.1f, 0.3f, 1f)

    /** `coui_open_slide_exit_translate_interpolator`: the covered page on a push. */
    val OpenExitEasing: NavBezierEasing = NavBezierEasing(0.3f, 0.15f, 0.3f, 1f)

    /** `coui_close_slide_enter_translate_interpolator`: the covered page coming back on a pop. */
    val CloseEnterEasing: NavBezierEasing = NavBezierEasing(0.3f, 0.26f, 0.4f, 1f)

    /** `coui_close_slide_exit_translate_interpolator`: the top page leaving on a pop. */
    val CloseExitEasing: NavBezierEasing = NavBezierEasing(0.25f, 0.1f, 0.3f, 1f)

    /** `coui_ease` (`coui_open_slide_alpha_interpolator`): the covered page's 1 -> 0.5 fade. */
    val DimEasing: Easing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)
}
