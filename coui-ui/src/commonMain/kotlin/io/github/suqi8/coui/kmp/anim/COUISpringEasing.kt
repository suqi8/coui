// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.anim

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Immutable
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt

/**
 * A [SpringSpec] with the parameters of a COUI `COUISpringForce`: `setResponse(response)` gives a
 * stiffness of `(2π / response)²` (a response of 0 counts as 1) and `setBounce(bounce)` a damping
 * ratio of `1 - bounce`. A bounce of 0 is critically damped; a positive bounce overshoots.
 *
 * @param response The time constant of the spring in seconds.
 * @param bounce The bounciness, 0..1.
 * @param visibilityThreshold The distance from the target below which the spring settles (the
 *   COUI `minimumVisibleChange`); `null` keeps the Compose default of 0.01.
 */
fun <T> couiSpring(
    response: Float,
    bounce: Float = 0f,
    visibilityThreshold: T? = null,
): SpringSpec<T> {
    val omega = 2f * PI.toFloat() / (if (response == 0f) 1f else response)
    return spring(
        dampingRatio = (1f - bounce).coerceAtLeast(0f),
        stiffness = omega * omega,
        visibilityThreshold = visibilityThreshold,
    )
}

/**
 * `COUISpringInterpolator(response, bounce)`: the unit step response of a COUI spring over one
 * second of spring time, normalized to end exactly at 1, used as the easing of a fixed-duration
 * animation (`ValueAnimator.setInterpolator(new COUISpringInterpolator(0.5, 0))`). The duration of
 * the animation compresses or stretches that second.
 *
 * The spring has a stiffness of `(2π / response)²` and a damping ratio of `1 - bounce` (a damping
 * ratio of 0 falls back to the interpolator's default 1.15). A positive bounce overshoots before
 * settling, a bounce of 0 rises monotonically. The spring starts at rest, as every
 * `COUISpringInterpolator(response, bounce)` does.
 *
 * @param response The time constant of the spring in seconds.
 * @param bounce The bounciness, 0..1.
 */
@Immutable
class COUISpringEasing(
    private val response: Float,
    private val bounce: Float = 0f,
) : Easing {
    private val omega: Double = 2.0 * PI / (if (response == 0f) 1.0 else response.toDouble())
    private val zeta: Double = (1.0 - bounce.toDouble()).let { if (it <= 0.0) DEFAULT_DAMPING_RATIO else it }
    private val dampedOmega: Double = if (zeta < 1.0) sqrt(1.0 - zeta * zeta) * omega else 0.0
    private val impulse: Double = when {
        zeta < 1.0 -> zeta * omega / dampedOmega
        zeta == 1.0 -> omega
        else -> zeta * omega
    }
    private val end: Double = raw(1.0).let { if (it == 0.0 || it.isNaN()) 1.0 else it }

    /** `getOriginInterpolation`: the spring's position at [t] seconds, from 0 towards 1. */
    private fun raw(t: Double): Double {
        val decay = exp(-zeta * omega * t)
        return when {
            zeta < 1.0 -> 1.0 - decay * (cos(dampedOmega * t) + impulse * sin(dampedOmega * t))

            zeta == 1.0 -> 1.0 - (impulse * t + 1.0) * exp(-omega * t)

            else -> {
                val s = omega * sqrt(zeta * zeta - 1.0)
                1.0 - decay / s * (zeta * omega * sinh(s * t) + s * cosh(s * t))
            }
        }
    }

    override fun transform(fraction: Float): Float = (raw(fraction.coerceAtLeast(0f).toDouble()) / end).toFloat()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is COUISpringEasing) return false
        return response == other.response && bounce == other.bounce
    }

    override fun hashCode(): Int = 31 * response.hashCode() + bounce.hashCode()

    override fun toString(): String = "COUISpringEasing(response=$response, bounce=$bounce)"

    private companion object {
        /** `COUISpringInterpolator.DEFAULT_DAMPINGRATIO`, used when the damping ratio is not positive. */
        const val DEFAULT_DAMPING_RATIO = 1.15
    }
}
