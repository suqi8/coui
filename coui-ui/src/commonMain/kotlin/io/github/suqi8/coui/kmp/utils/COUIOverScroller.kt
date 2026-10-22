// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt

/**
 * The overscroll distance helpers of `COUIPhysicalAnimationUtil`.
 */
internal object COUIPhysicalAnimation {

    /**
     * The scroll a drag of [delta] px applies while the view is already [overScroll] px past its
     * edge: 40% of the drag, falling linearly to nothing at [maxOverScroll] (the screen width).
     */
    fun calcRealOverScrollDist(delta: Int, overScroll: Int, maxOverScroll: Int): Int = ((delta * (1f - min(abs(overScroll) * 1f / maxOverScroll, 1f))) / 5f * 2f).toInt()

    /**
     * The displayed position of a fling that ran [over] px past [edge]: the overshoot saturates at
     * 37.31% of [screenSize].
     */
    fun calcOverFlingDecelerateDist(edge: Int, over: Int, screenSize: Int): Int {
        val limit = screenSize * 0.3731f
        val overShoot = over * limit
        return edge + (overShoot.toDouble() / sqrt(over.toDouble() * over + (limit * limit).toDouble())).toInt()
    }
}

/**
 * A port of `COUISpringInterpolator`, the normalized spring curve that shapes COUI flings.
 *
 * The fling flavour keeps the original's `sinh(ζ·t)` / `cosh(ζ·t)` terms as they ship.
 */
internal class COUISpringInterpolator(
    stiffness: Double,
    dampingRatio: Double,
    initialVelocity: Double,
    cutRatio: Float,
    velocityUnit: Float,
    private val isFling: Boolean,
) {
    private val unDampedAngularFreq = sqrt(if (stiffness <= 0.0) DEFAULT_STIFFNESS else stiffness)
    private val dampingRatio = if (dampingRatio <= 0.0) DEFAULT_DAMPING_RATIO else dampingRatio
    private val initialVel = min(abs(initialVelocity), VELOCITY_MAX) / (if (velocityUnit <= 0f) DEFAULT_VELOCITY_UNIT else velocityUnit)
    private val cutRatio = if (cutRatio <= 0f) 1f else cutRatio
    private val angularFreq: Double
    private val impulse: Double
    private var finalValue = -1f

    init {
        val d = this.dampingRatio
        val w = unDampedAngularFreq
        if (d < 1.0) {
            angularFreq = sqrt(1.0 - d * d) * w
            impulse = (d * w - initialVel) / angularFreq
        } else if (d == 1.0) {
            angularFreq = 0.0
            impulse = -initialVel + w
        } else {
            angularFreq = 0.0
            impulse = -initialVel + d * w
        }
    }

    private fun originInterpolation(fraction: Float): Float {
        val f = (if (fraction >= 0f) fraction else 0f) * cutRatio
        val t = f.toDouble()
        var envelope = exp(-dampingRatio * unDampedAngularFreq * t)
        val a: Double
        val b: Double
        val c: Double
        if (dampingRatio < 1.0) {
            a = cos(angularFreq * t)
            b = impulse
            c = sin(angularFreq * t)
        } else {
            if (dampingRatio == 1.0) {
                envelope = impulse * t + 1.0
                return (1.0 - envelope * exp(-f * unDampedAngularFreq)).toFloat()
            }
            b = unDampedAngularFreq * sqrt(dampingRatio * dampingRatio - 1.0)
            if (!isFling) {
                val x = fraction.toDouble() * b
                return (
                    1.0 - (envelope / b) *
                        ((-initialVel + dampingRatio * unDampedAngularFreq) * sinh(x) + b * cosh(x))
                    ).toFloat()
            }
            envelope /= b
            a = (-initialVel + unDampedAngularFreq * dampingRatio) * sinh(dampingRatio * t)
            c = cosh(dampingRatio * t)
        }
        return (1.0 - envelope * (a + b * c)).toFloat()
    }

    fun getInterpolation(fraction: Float): Float {
        if (finalValue == -1f) {
            val atEnd = originInterpolation(1f)
            finalValue = if (atEnd != 0f && !atEnd.isNaN()) atEnd else 1f
        }
        return originInterpolation(fraction) / finalValue
    }

    fun getSpeed(fraction: Float): Float {
        val t = (if (fraction >= 0f) fraction else 0f).toDouble()
        val envelope = exp(-cutRatio * dampingRatio * unDampedAngularFreq * t)
        val d = dampingRatio
        val w = unDampedAngularFreq
        val r = cutRatio.toDouble()
        val speed = if (d < 1.0) {
            abs(
                envelope * (
                    -r * (impulse * d * w + angularFreq) * sin(r * angularFreq * t) +
                        r * (impulse * angularFreq - d * w) * cos(r * angularFreq * t)
                    ),
            )
        } else if (d == 1.0) {
            abs(r * ((impulse - w) - impulse * r * w * t) * exp(-r * w * t))
        } else {
            val b = w * sqrt(d * d - 1.0)
            val k = r * ((b * b + initialVel * d * w) - d * d * w * w)
            if (isFling) {
                abs((envelope / b) * (k * sinh(r * d * t) + r * d * ((d * w - initialVel) - w * b) * cosh(r * d * t)))
            } else {
                val ft = fraction.toDouble()
                abs((envelope / b) * (k * sinh(r * b * ft) + (-r * initialVel * b) * cosh(r * b * ft)))
            }
        }
        return speed.toFloat()
    }

    private companion object {
        const val DEFAULT_DAMPING_RATIO = 1.15
        const val DEFAULT_STIFFNESS = 40.0
        const val DEFAULT_VELOCITY_UNIT = 15000f
        const val VELOCITY_MAX = 20000.0
    }
}

/**
 * A single-axis port of `SpringOverScroller.ReboundOverScroller` as `COUIHorizontalScrollView`
 * configures it (`setIsScrollView(true)`): flings follow a [COUISpringInterpolator] curve scaled
 * from a friction simulation, and spring-backs integrate an origami spring with RK4.
 *
 * Positions are in px, velocities in px/s and times in ms. [update] advances one frame and
 * returns `false` once the motion has finished.
 */
internal class COUIReboundOverScroller(
    private val springBackTensionMultiple: Float = 0.83f,
    private val isScrollView: Boolean = true,
) {
    private class Config(friction: Double, tension: Double) {
        var friction: Double = frictionFromOrigami(friction)
        var tension: Double = tensionFromOrigami(tension)

        fun applyFriction(value: Double) {
            friction = frictionFromOrigami(value)
        }

        fun applyTension(value: Double) {
            tension = tensionFromOrigami(value)
        }

        private fun frictionFromOrigami(value: Double): Double = if (value.toFloat() == 0f) 0.0 else ((value.toFloat() - 8f) * 3f + 25f).toDouble()

        private fun tensionFromOrigami(value: Double): Double = if (value.toFloat() == 0f) 0.0 else ((value.toFloat() - 30f) * 3.62f + 194f).toDouble()
    }

    private val flingFriction = FLING_FRICTION_NORMAL
    private val flingConfig = Config(flingFriction.toDouble(), 0.0)
    private val springBackConfig = Config(SPRING_BACK_FRICTION.toDouble(), SPRING_BACK_TENSION.toDouble())
    private var config = flingConfig

    /** The current position, px. */
    var position = 0.0
        private set

    /** The current velocity, px/s. */
    var velocity = 0.0

    private var tempPosition = 0.0
    private var tempVelocity = 0.0
    private var startValue = 0.0

    /** Where the current motion ends, px. */
    var endValue = 0.0
        private set

    private var count = 1
    private var finished = true
    private var isSpringBack = false
    private var tensionAdjusted = false
    private var withSpring = false
    private var startTime = 0L
    private var splineMinDelta = 0.0
    private var simulateSplineDistance = 0
    private var simulateSplineDuration = 0
    private var interpolator: COUISpringInterpolator? = null

    /** The frame interval the integrations run at, seconds. */
    private var refreshTime = SOLVER_TIMESTEP_SEC

    val isAtRest: Boolean
        get() = abs(velocity) <= REST_SPEED_THRESHOLD_SPRING &&
            (abs(endValue - position) <= DISPLACEMENT_FROM_REST_THRESHOLD || config.tension == 0.0)

    fun setAtRest() {
        endValue = position
        tempPosition = position
        velocity = 0.0
        isSpringBack = false
    }

    private fun setCurrentValue(value: Double, atRest: Boolean) {
        startValue = value
        if (!isScrollView) tempPosition = 0.0
        position = value
        if (atRest) setAtRest()
    }

    private fun setEndValue(value: Double) {
        if (endValue == value) return
        startValue = position
        endValue = value
        finished = false
    }

    /** `ReboundOverScroller.springBack`: returns `false` when [start] is already within range. */
    fun springBack(start: Int, min: Int, max: Int): Boolean {
        setCurrentValue(start.toDouble(), false)
        if (start in min..max) {
            config = Config(flingFriction.toDouble(), 0.0)
            return false
        }
        if (start > max) setEndValue(max.toDouble()) else setEndValue(min.toDouble())
        isSpringBack = true
        springBackConfig.applyFriction(SPRING_BACK_FRICTION.toDouble())
        springBackConfig.applyTension((springBackTensionMultiple * SPRING_BACK_TENSION).toDouble())
        config = springBackConfig
        return true
    }

    /** `SpringOverScroller.notifyHorizontalEdgeReached`: hand a fling over to a spring-back. */
    fun notifyEdgeReached(start: Int, edge: Int, min: Int, max: Int) {
        position = start.toDouble()
        tempPosition = edge.toDouble()
        tempVelocity = velocity
        springBack(start, min, max)
    }

    /**
     * `ReboundOverScroller.fling` for an unbounded scroll view. [frameIntervalSec] is the display
     * frame interval the original reads from the Choreographer for its simulation.
     */
    fun fling(start: Int, velocity: Int, nowMs: Long, frameIntervalSec: Float, min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE) {
        splineMinDelta = splineMinDelta(abs(velocity).toFloat())
        finished = false
        initFling(start, velocity, nowMs)
        interpolator = COUISpringInterpolator(DEFAULT_STIFFNESS, DEFAULT_DAMPING_RATIO, velocity.toDouble(), 1f, DEFAULT_VELOCITY_UNIT, true)
        refreshTime = frameIntervalSec
        val durationScale = durationScale(abs(velocity).toFloat())
        val distanceScale = distanceScale(abs(velocity).toFloat())
        val edge = if (velocity >= 0) max.toDouble() else min.toDouble()
        val simulated = calculateFinalPosition(edge, false)
        val simulatedDistance = simulated[0] - start
        simulateSplineDistance = (simulatedDistance * distanceScale).toInt()
        simulateSplineDuration = (simulated[1] * durationScale).toInt()
        val curve = calculateFinalPosition(edge, true)
        // An unbounded fling never reaches its edge, so the spline curve always drives it.
        withSpring = true
        endValue = curve[0].coerceIn(min, max).toDouble()
    }

    private fun initFling(start: Int, velocity: Int, nowMs: Long) {
        startTime = nowMs
        count = 1
        flingConfig.applyFriction(flingFriction.toDouble())
        flingConfig.applyTension(0.0)
        config = flingConfig
        setCurrentValue(start.toDouble(), true)
        this.velocity = velocity.toDouble()
    }

    private fun calculateFinalPosition(edge: Double, withInterpolator: Boolean): IntArray {
        val savedPosition = position.toInt()
        val savedVelocity = velocity.toInt()
        val savedCount = count
        val savedFinished = finished
        tempPosition = 0.0
        tempVelocity = 0.0
        count = 1
        var reachedEdge = false
        while (!finished) {
            val before = position
            if (withInterpolator) {
                curveStep(count * refreshTime * 1000f / simulateSplineDuration)
            } else {
                reboundStep()
            }
            val after = position
            if (abs(velocity) < SPLINE_MIN_VELOCITY) finished = true
            if (abs(after - before) < splineMinDelta) finished = true
            if (edge != -1.0 && !reachedEdge && (before - edge) * (after - edge) <= 0.0) {
                position = edge
                reachedEdge = true
            }
        }
        val finalPosition = position.toInt()
        val duration = count * refreshTime
        position = savedPosition.toDouble()
        velocity = savedVelocity.toDouble()
        count = savedCount
        tempPosition = 0.0
        tempVelocity = 0.0
        finished = savedFinished
        return intArrayOf(finalPosition, (duration * 1000f).toInt())
    }

    private fun curveStep(fraction: Float) {
        val curve = interpolator ?: return
        position = simulateSplineDistance * curve.getInterpolation(fraction) + startValue
        velocity = (curve.getSpeed(fraction) * simulateSplineDistance / simulateSplineDuration * 1000f).toDouble()
        count++
    }

    private fun reboundStep() {
        // adjustFrictionByStartVelocity: slower flings get a heavier base friction on their first step.
        if (!isSpringBack && count == 1) {
            if (abs(velocity) > MID_VELOCITY_ADJUST_FRICTION && abs(velocity) < MAX_VELOCITY_ADJUST_FRICTION) {
                config.friction = MID_FLING_BASE_FRICTION
            } else if (abs(velocity) <= MID_VELOCITY_ADJUST_FRICTION) {
                config.friction = SLOW_FLING_BASE_FRICTION
            }
        }
        rk4(refreshTime)
        count++
    }

    /** `calculateWithRk4`, including its use of the intermediate position for the first slope. */
    private fun rk4(dt: Float) {
        val x = position
        val v = velocity
        val tension = config.tension
        val friction = config.friction
        val end = endValue
        val h = dt.toDouble()
        val a1 = (end - tempPosition) * tension
        val x2 = v * h / 2.0 + x
        val v2 = v + a1 * h / 2.0
        val a2 = (end - x2) * tension - friction * v2
        val x3 = x + v2 * h / 2.0
        val v3 = v + a2 * h / 2.0
        val a3 = (end - x3) * tension - friction * v3
        val x4 = x + v3 * h
        val v4 = v + a3 * h
        val dxdt = (v + (v2 + v3) * 2.0 + v4) * RK4_WEIGHT
        val dvdt = (a1 + (a2 + a3) * 2.0 + ((end - x4) * tension - friction * v4)) * RK4_WEIGHT
        position = x + dxdt * h
        velocity = v + h * dvdt
        tempPosition = x4
        tempVelocity = v4
    }

    /**
     * Advance one frame. [frameDeltaSec] is the time since the previous frame and [nowMs] the frame
     * time. Returns `false` once the motion is over.
     */
    fun update(nowMs: Long, frameDeltaSec: Float): Boolean {
        // calculateRefreshTime.
        refreshTime = max(MIN_FRAME_INTERVAL, frameDeltaSec)
        if (refreshTime > ERROR_THRESHOLD) refreshTime = MIN_FRAME_INTERVAL
        if (isSpringBack) {
            if (isAtRest) return false
            val before = position
            val displacement = abs(endValue - position)
            if (!tensionAdjusted && displacement < SPRING_BACK_ADJUST_THRESHOLD) {
                tensionAdjusted = true
            } else if (displacement < SPRING_BACK_STOP_THRESHOLD) {
                position = endValue
                tensionAdjusted = false
                isSpringBack = false
                return false
            }
            while (true) {
                rk4(refreshTime)
                if (abs(before - position) > MIN_UPDATE_ONE_STEP || !isSpringBack || isAtRest) break
            }
            count++
            return true
        }
        if (isAtRest) return false
        val before = position
        if (!withSpring) {
            reboundStep()
        } else {
            if (simulateSplineDuration <= 0) return false
            curveStep(max(nowMs - startTime, 0L).toFloat() / simulateSplineDuration)
        }
        val after = position
        if (!withSpring && abs(after - before) < splineMinDelta) return false
        if (withSpring && abs(velocity) < SPLINE_MIN_VELOCITY) return false
        if ((before - endValue) * (after - endValue) <= 0.0) {
            position = endValue
            return false
        }
        if (velocity.isNaN() || position.isNaN()) return false
        return true
    }

    private fun splineMinDelta(velocity: Float): Double = when {
        velocity <= 5000f -> 0.2
        velocity <= 8000f -> 0.3
        else -> 0.35
    }

    private fun distanceScale(velocity: Float): Float = when {
        velocity <= 2000f -> 1f
        velocity <= 6000f -> 1f - (velocity - 2000f) / 4000f * 0.19999999f
        velocity <= 10000f -> 0.8f
        velocity > 20000f -> 0.4f
        else -> 0.8f - (velocity - 10000f) / 10000f * 0.4f
    }

    private fun durationScale(velocity: Float): Float = when {
        velocity <= 2000f -> 1.3f
        velocity <= 6000f -> 1.3f - (velocity - 2000f) / 4000f * 0.49999994f
        velocity <= 10000f -> 0.8f
        velocity > 20000f -> 0.5f
        else -> 0.8f - (velocity - 10000f) / 10000f * 0.3f
    }

    private companion object {
        const val FLING_FRICTION_NORMAL = 0.2f
        const val MID_FLING_BASE_FRICTION = 2.5
        const val SLOW_FLING_BASE_FRICTION = 2.5
        const val MID_VELOCITY_ADJUST_FRICTION = 4000.0
        const val MAX_VELOCITY_ADJUST_FRICTION = 10000.0
        const val SPRING_BACK_FRICTION = 12.19f
        const val SPRING_BACK_TENSION = 16f
        const val SPRING_BACK_ADJUST_THRESHOLD = 180.0
        const val SPRING_BACK_STOP_THRESHOLD = 0.25
        const val REST_SPEED_THRESHOLD_SPRING = 20.0
        const val DISPLACEMENT_FROM_REST_THRESHOLD = 0.05
        const val SPLINE_MIN_VELOCITY = 5.0
        const val MIN_UPDATE_ONE_STEP = 0.5
        const val RK4_WEIGHT = 0.16699999570846558
        const val DEFAULT_STIFFNESS = 40.0
        const val DEFAULT_DAMPING_RATIO = 1.15
        const val DEFAULT_VELOCITY_UNIT = 15000f
        const val MIN_FRAME_INTERVAL = 0.008f
        const val ERROR_THRESHOLD = 0.025f
        const val SOLVER_TIMESTEP_SEC = 0.016f
    }
}
