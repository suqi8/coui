// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangedIgnoreConsumed
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

/**
 * The pure maths of COUI 17 `COUITouchMotionAnimator`, the touch animator that both `COUIButton`
 * and the top-bar buttons (`ToolbarMaterialEffectDelegate`, through `COUIViewTouchAnimator` with
 * deformation, rubber band and press scale all enabled) drive.
 *
 * All values are the `OlivelinkDefault` provider defaults: `CommonParamsExtKt.commonParamsDefault`
 * and `TouchMotionTierParamsExtKt.touchMotionTierParamsDefault`. Distances are in pixels unless a
 * name says otherwise; `SpringData` is `(bounce, response)` and `COUISpringForce` maps it to a
 * damping ratio of `1 - bounce` and a stiffness of `(2π / response)²`.
 */
internal object TouchMotion {

    /** `CommonParams.deadZone`: the raw drag, in pixels, below which a move is ignored. */
    val DeadZonePx = 5f

    /** `CommonParams.maxScale`: the deformation scale of a view up to [KMinArea] pixels. */
    val DeformMaxScale = 1.2f

    /** `CommonParams.k1`, the area term of `calculateScaleWithK`. */
    private val K1 = 350_000

    /** `CommonParams.k2`, the aspect-ratio term of `calculateScaleWithK`. */
    private val K2 = 15

    /** `TouchAnimatorParamProvider.K_MIN_AREA`, in square pixels. */
    private val KMinArea = 38_416f

    /** `TouchAnimatorParamProvider.K_MIN_SCALE`. */
    private val KMinScale = 1.03f

    /** `TouchMotionTierParams.maxStretchDistance`: the rubber-band translation ceiling. */
    val MaxStretchDistance = 28.dp

    /** `TouchMotionTierParams.rubberCurveRatio`. */
    val RubberCurveRatio = 0.05f

    /** `TouchMotionTierParams.maxDeformRubberDistance`: the drag that saturates the deformation. */
    val MaxDeformRubberDistance = 150.dp

    /** `TouchMotionTierParams.deformRubberCurveRatio`. */
    val DeformRubberCurveRatio = 0.55f

    /** `TouchMotionTierParams.dragSpring` response. */
    val DragSpringResponse = 0.15f

    /** `TouchMotionTierParams.dragSpring` bounce. */
    val DragSpringBounce = 0.15f

    /** `TouchMotionTierParams.handUpSpring` response (small views). */
    val HandUpSpringResponse = 0.42f

    /** `TouchMotionTierParams.handUpSpring` bounce (small views). */
    val HandUpSpringBounce = 0.65f

    /** `TouchMotionTierParams.handUpSpringLargeArea` response. */
    val HandUpLargeAreaSpringResponse = 0.45f

    /** `TouchMotionTierParams.handUpSpringLargeArea` bounce. */
    val HandUpLargeAreaSpringBounce = 0.65f

    /** `TouchMotionTierParams.handUpMaxInitialVelocity`. */
    val HandUpMaxInitialVelocity = 1000f

    /** `TouchMotionTierParams.handUpSmallAreaRefWidthDp`: below (this dp)² the small spring applies. */
    val HandUpSmallAreaRefWidthDp = 56f

    /** `TouchMotionTierParams.handUpLargeAreaRefWidthDp`: above (this dp)² the large spring applies. */
    val HandUpLargeAreaRefWidthDp = 100f

    /** `TouchMotionTierParams.handUpAreaBezier`. */
    private val HandUpAreaEasing = CubicBezierEasing(0.55026454f, 0f, 0.8915344f, 0.18783069f)

    /**
     * `COUIVelocityTrackerHelper`: velocities are read in pixels per millisecond
     * (`computeCurrentVelocity(1)`) and scaled by `TouchMotionDebugFlags.velocityTrackerScale`.
     */
    val VelocityScale = 0.3f

    /** `TouchMotionMinVisibleChange.DEFORM_REST_EPSILON`: a deformation axis this close to 1 snaps. */
    val DeformRestEpsilon = 0.002f

    /** `CommonParams.rubberMinimumVisibleChange`, in pixels. */
    val RubberMinimumVisibleChange = 0.5f

    /** `CommonParams.deformMinimumVisibleChange`. */
    val DeformMinimumVisibleChange = 5.0e-4f

    /**
     * `TouchMotionRubberBand.apply`: `(1 - 1 / (c·|x| / max + 1)) · max · sign(x)`. The result
     * approaches [maxDistance] asymptotically; 0 when [maxDistance] is not positive.
     */
    fun rubberBand(offset: Float, curveRatio: Float, maxDistance: Float): Float {
        if (maxDistance <= 0f) return 0f
        return (1f - 1f / (curveRatio * abs(offset) / maxDistance + 1f)) * maxDistance * sign(offset)
    }

    /**
     * `TouchMotionRubberBand.inverse`: the raw offset that [rubberBand] maps to [result], used to
     * resume a drag from a view that is still springing back.
     */
    fun inverseRubberBand(result: Float, curveRatio: Float, maxDistance: Float): Float {
        if (maxDistance <= 0f) return result
        val magnitude = abs(result)
        if (magnitude >= maxDistance) return maxDistance * sign(result)
        val remaining = maxDistance - magnitude
        if (curveRatio <= 0f || remaining <= 0f) return result
        return maxDistance * magnitude / (curveRatio * remaining) * sign(result)
    }

    /**
     * `TouchAnimatorParamProvider.calculateScaleWithK` with the default `maxScale`, `k1` and `k2`:
     * the deformation ceiling of a [width] x [height] pixel view. Views up to [KMinArea] square
     * pixels stretch to [DeformMaxScale]; larger and longer views stretch less, never below 1.03.
     */
    fun deformMaxScale(width: Float, height: Float): Float {
        val area = (width * height).toInt()
        val longSide = max(width, height)
        val shortSide = min(width, height)
        if (area <= KMinArea || shortSide == 0f) return DeformMaxScale
        val decay = min(
            K1 / 1_000_000.0 * ln(area / KMinArea.toDouble()) + K2 / 1000.0 * (longSide / shortSide - 1f),
            1.0,
        )
        return max((DeformMaxScale - KMinScale) * (1.0 - decay).toFloat() + KMinScale, KMinScale)
    }

    /**
     * `COUIDeformAnimatorUnit.calculateScaleFactors`: the area-conserving stretch of a drag of
     * ([dx], [dy]) pixels. Along each axis the rubber-banded drag lengthens that axis up to
     * [maxScale] and shortens the other by the inverse; a diagonal drag blends both stretches
     * geometrically, weighted by the drag components.
     *
     * @return the scale pair packed as `(scaleX, scaleY)`.
     */
    fun deformScale(dx: Float, dy: Float, maxDeformDistance: Float, maxScale: Float): Pair<Float, Float> {
        val ax = abs(dx)
        val ay = abs(dy)
        val horizontal = directionScale(ax, maxDeformDistance, maxScale, alongX = true)
        val vertical = directionScale(ay, maxDeformDistance, maxScale, alongX = false)
        val total = ax + ay
        return when {
            total <= 0f -> 1f to 1f

            ay <= 0f -> horizontal

            ax <= 0f -> vertical

            else -> {
                val wx = ax / total
                val wy = ay / total
                exp(ln(horizontal.first) * wx + ln(vertical.first) * wy) to
                    exp(ln(horizontal.second) * wx + ln(vertical.second) * wy)
            }
        }
    }

    private fun directionScale(distance: Float, maxDistance: Float, maxScale: Float, alongX: Boolean): Pair<Float, Float> {
        if (distance <= 0f || maxDistance <= 0f) return 1f to 1f
        val stretch = rubberBand(distance, DeformRubberCurveRatio, maxDistance) / maxDistance * (maxScale - 1f) + 1f
        val squeeze = if (stretch > 0f) 1f / stretch else 1f
        return if (alongX) stretch to squeeze else squeeze to stretch
    }

    /**
     * `COUIDeformAnimatorUnit.calculateEffectiveDistanceFromScale`: the (unsigned) drag that
     * produces the current [scale] on the dominant axis, used to resume a drag mid-spring.
     */
    fun deformEffectiveDistance(scale: Float, maxDeformDistance: Float, maxScale: Float): Float {
        if (scale == 1f) return 0f
        val range = maxScale - 1f
        if (range <= 0f) return 1f
        if (maxDeformDistance <= 0f) return 0f
        return inverseRubberBand(min((scale - 1f) / range, 1f) * maxDeformDistance, DeformRubberCurveRatio, maxDeformDistance)
    }

    /**
     * `TouchMotionHandUpSpringResolver.resolve`: the release spring `(response, bounce)` of a
     * [width] x [height] pixel view, eased from the small to the large spring between
     * ([HandUpSmallAreaRefWidthDp])² and ([HandUpLargeAreaRefWidthDp])².
     */
    fun handUpSpring(width: Float, height: Float, density: Float): Pair<Float, Float> {
        val area = width * height
        if (area <= 0f) return HandUpSpringResponse to HandUpSpringBounce
        val small = HandUpSmallAreaRefWidthDp * density
        val large = HandUpLargeAreaRefWidthDp * density
        val t = HandUpAreaEasing.transform(((area - small * small) / (large * large - small * small)).coerceIn(0f, 1f))
        return HandUpSpringResponse + (HandUpLargeAreaSpringResponse - HandUpSpringResponse) * t to
            HandUpSpringBounce + (HandUpLargeAreaSpringBounce - HandUpSpringBounce) * t
    }

    /**
     * `TouchMotionHandUpHelper.computeInitialVelocity`: the release velocity of an axis displaced
     * by [rest] (in the axis' own unit) for a finger moving at [velocity] (scaled px/ms) over a
     * [totalDistance] pixel span, clamped to ±[HandUpMaxInitialVelocity].
     */
    fun handUpInitialVelocity(velocity: Float, totalDistance: Float, rest: Float): Float {
        if (totalDistance <= 0f) return 0f
        return (velocity / totalDistance * rest).coerceIn(-HandUpMaxInitialVelocity, HandUpMaxInitialVelocity)
    }

    /**
     * `COUIITouchMotionUnit.computeHandUpBounce` (normalized hand-up velocity, gain 1): the larger
     * of the velocity-derived bounce and the area-resolved [springBounce], at most 1.
     */
    fun handUpBounce(initialVelocity: Float, springBounce: Float): Float {
        val fromVelocity = min(abs(initialVelocity) / HandUpMaxInitialVelocity, 1f)
        return min(max(fromVelocity, abs(springBounce.coerceIn(-1f, 1f))), 1f)
    }

    /** A `COUISpringForce` of [response] / [bounce] as a Compose spring. */
    fun spring(response: Float, bounce: Float, visibilityThreshold: Float): SpringSpec<Float> {
        val omega = 2f * PI.toFloat() / response
        return spring(
            dampingRatio = (1f - bounce).coerceAtLeast(0f),
            stiffness = omega * omega,
            visibilityThreshold = visibilityThreshold,
        )
    }
}

/**
 * The state of one `COUITouchMotionAnimator`: the press scale (`COUIPressAnimatorUnit`), the
 * area-conserving drag deformation (`COUIDeformAnimatorUnit`) and the rubber-band drag
 * translation (`COUIRubberAnimatorUnit`).
 */
@Stable
internal class TouchMotionState {
    val press = Animatable(1f, visibilityThreshold = PressMinimumVisibleChange)
    val scaleX = Animatable(1f, visibilityThreshold = TouchMotion.DeformMinimumVisibleChange)
    val scaleY = Animatable(1f, visibilityThreshold = TouchMotion.DeformMinimumVisibleChange)
    val translationX = Animatable(0f, visibilityThreshold = TouchMotion.RubberMinimumVisibleChange)
    val translationY = Animatable(0f, visibilityThreshold = TouchMotion.RubberMinimumVisibleChange)

    /**
     * The velocity of the running [press] spring, sampled every frame. A cancelled `Animatable`
     * forgets its velocity, so the release that interrupts a press-in reads it from here to keep the
     * momentum, as `COUISpringAnimation.animateToFinalPosition` does on a running spring.
     */
    var pressVelocity = 0f

    /**
     * Whether the drag deforms the element. Hosts that only apply the translation (the COUI 17
     * seek bar, `TouchMotionEffectRenderer`) turn it off to skip the unused scale springs.
     */
    var deformEnabled = true

    /**
     * Whether a touch gesture holds the element, from the touch down to the last pointer's up or
     * the gesture's cancellation. The press scale stays in even when the finger slides out of
     * bounds, and hosts drive their touch-entered state from it (`COUIButton.onTouchEvent` enters
     * its press mask on `ACTION_DOWN` and exits it on `ACTION_UP` / `ACTION_CANCEL`).
     */
    var touching by mutableStateOf(false)

    /** The latest measured size in pixels and dp, written from the layer block. */
    var width = 0f
    var height = 0f
    var widthDp = 0f
    var heightDp = 0f

    /** The density of the pointer input scope, kept for releases started outside a gesture. */
    var densityOrNull: Density? = null

    // Per-unit drag origins: a gesture that lands on a still-springing element resumes from the
    // drag that would produce its current displacement (`COUIITouchMotionUnit.handleTouchDown`).
    private var rubberStart = Offset.Zero
    private var deformStart = Offset.Zero
    private var lastPosition = Offset.Zero

    // The velocities of the springs a touch down stopped (translationX, translationY, scaleX,
    // scaleY), NaN for none. COUIDynamicAnimation.cancel() keeps mVelocity, so the next drag restarts
    // each spring with the momentum it was caught with.
    private val caughtVelocity = floatArrayOf(Float.NaN, Float.NaN, Float.NaN, Float.NaN)

    private fun Density.maxStretch() = TouchMotion.MaxStretchDistance.toPx()
    private fun Density.maxDeform() = TouchMotion.MaxDeformRubberDistance.toPx()

    /** Springs [axis] to [target] with the drag spring, resuming a caught velocity once. */
    private fun CoroutineScope.dragTo(axis: Animatable<Float, *>, index: Int, target: Float, spec: SpringSpec<Float>) {
        val caught = caughtVelocity[index]
        caughtVelocity[index] = Float.NaN
        launch {
            if (caught.isNaN()) axis.animateTo(target, spec) else axis.animateTo(target, spec, initialVelocity = caught)
        }
    }

    /**
     * The touch down of the rubber and deformation units (`COUIITouchMotionUnit.handleTouchDown`).
     * A gesture that lands on a still-springing element resumes from the drag that would produce
     * its current displacement. With a [scope], the running springs are also stopped where they are
     * (`cancel()`), so the finger catches the element mid-bounce; the next drag restarts them with
     * the velocity they were caught with, or the release springs them home.
     */
    fun Density.down(position: Offset, scope: CoroutineScope? = null) {
        val maxStretch = maxStretch()
        val rubberMoving = translationX.isRunning || translationY.isRunning
        rubberStart = if (rubberMoving) {
            position - Offset(
                TouchMotion.inverseRubberBand(translationX.value, TouchMotion.RubberCurveRatio, maxStretch),
                TouchMotion.inverseRubberBand(translationY.value, TouchMotion.RubberCurveRatio, maxStretch),
            )
        } else {
            position
        }
        val deformMoving = scaleX.isRunning || scaleY.isRunning
        deformStart = if (deformMoving) {
            val maxScale = TouchMotion.deformMaxScale(width, height)
            val sx = scaleX.value
            val sy = scaleY.value
            val effective = TouchMotion.deformEffectiveDistance(if (sx >= sy) sx else sy, maxDeform(), maxScale)
            position - if (sx >= sy) Offset(effective, 0f) else Offset(0f, effective)
        } else {
            position
        }
        lastPosition = position
        caughtVelocity.fill(Float.NaN)
        if (scope != null) {
            if (rubberMoving) {
                caughtVelocity[0] = translationX.velocity
                caughtVelocity[1] = translationY.velocity
                scope.launch { translationX.stop() }
                scope.launch { translationY.stop() }
            }
            if (deformMoving) {
                caughtVelocity[2] = scaleX.velocity
                caughtVelocity[3] = scaleY.velocity
                scope.launch { scaleX.stop() }
                scope.launch { scaleY.stop() }
            }
        }
    }

    /** `handlePointerSwitch`: keeps the accumulated drag when another pointer takes over. */
    fun switchPointer(newPosition: Offset) {
        rubberStart = newPosition - (lastPosition - rubberStart)
        deformStart = newPosition - (lastPosition - deformStart)
        lastPosition = newPosition
    }

    fun Density.move(position: Offset, scope: CoroutineScope) {
        lastPosition = position
        val rubber = position - rubberStart
        val deform = position - deformStart
        // checkDeadZone runs per unit on its own drag.
        val spec = TouchMotion.spring(TouchMotion.DragSpringResponse, TouchMotion.DragSpringBounce, TouchMotion.RubberMinimumVisibleChange)
        if (abs(rubber.x) >= TouchMotion.DeadZonePx || abs(rubber.y) >= TouchMotion.DeadZonePx) {
            val maxStretch = maxStretch()
            val tx = TouchMotion.rubberBand(rubber.x, TouchMotion.RubberCurveRatio, maxStretch)
            val ty = TouchMotion.rubberBand(rubber.y, TouchMotion.RubberCurveRatio, maxStretch)
            scope.dragTo(translationX, 0, tx, spec)
            scope.dragTo(translationY, 1, ty, spec)
        }
        if (deformEnabled && (abs(deform.x) >= TouchMotion.DeadZonePx || abs(deform.y) >= TouchMotion.DeadZonePx)) {
            val deformSpec = TouchMotion.spring(TouchMotion.DragSpringResponse, TouchMotion.DragSpringBounce, TouchMotion.DeformMinimumVisibleChange)
            val (sx, sy) = TouchMotion.deformScale(deform.x, deform.y, maxDeform(), TouchMotion.deformMaxScale(width, height))
            scope.dragTo(scaleX, 2, sx, deformSpec)
            scope.dragTo(scaleY, 3, sy, deformSpec)
        }
    }

    /**
     * The hand-up of the deformation and rubber units: every displaced axis springs home on the
     * area-resolved release spring, seeded with the normalized finger velocity
     * ([velocityX] / [velocityY] in px/s).
     */
    fun Density.release(velocityX: Float, velocityY: Float, scope: CoroutineScope) {
        // applyHandUpSpring sets every start velocity explicitly.
        caughtVelocity.fill(Float.NaN)
        val vx = velocityX / 1000f * TouchMotion.VelocityScale
        val vy = velocityY / 1000f * TouchMotion.VelocityScale
        // resolveTotalDistancePx(stretch = true) for both units.
        val total = max(maxStretch(), 1f)
        val (response, springBounce) = TouchMotion.handUpSpring(width, height, density)
        fun handUp(animatable: Animatable<Float, *>, target: Float, velocity: Float, threshold: Float) {
            val init = TouchMotion.handUpInitialVelocity(velocity, total, abs(animatable.value - target))
            val spec = TouchMotion.spring(response, TouchMotion.handUpBounce(init, springBounce), threshold)
            scope.launch { animatable.animateTo(target, spec, initialVelocity = init) }
        }
        handUp(translationX, 0f, vx, TouchMotion.RubberMinimumVisibleChange)
        handUp(translationY, 0f, vy, TouchMotion.RubberMinimumVisibleChange)
        listOf(scaleX to vx, scaleY to vy).forEach { (axis, velocity) ->
            if (abs(axis.value - 1f) < TouchMotion.DeformRestEpsilon) {
                scope.launch { axis.snapTo(1f) }
            } else {
                handUp(axis, 1f, velocity, TouchMotion.DeformMinimumVisibleChange)
            }
        }
    }
}

/**
 * The COUI 17 `COUITouchMotionAnimator` touch motion shared by `COUIButton` and the top-bar
 * buttons:
 * - **Press scale** (`COUIPressAnimatorUnit`): the element springs down to
 *   [COUIMaterialEffect.pressTargetScale] while touched (it stays down when the finger slides
 *   out, as the unit only reacts to up / cancel) or while [interactionSource] reports a press.
 *   Every press starts its spring from rest (impact velocity 0) and the release takes over the
 *   momentum of a press-in still under way, so a quick tap sinks a little further before the
 *   jelly / candy release spring bounces it back. Both settle at the official minimum visible
 *   changes (0.001, release 0.0001) rather than jumping the last 1%.
 * - **Rubber band** (`COUIRubberAnimatorUnit`): past a 5px dead zone the element follows the
 *   finger on `TouchMotionRubberBand` up to 28dp, on a response 0.15 / bounce 0.15 spring.
 * - **Deformation** (`COUIDeformAnimatorUnit`): the drag stretches the element along the drag
 *   and squeezes it across, area-conserving, up to `calculateScaleWithK` (1.2x for small views).
 * - **Release**: translation and deformation spring home on the area-resolved hand-up spring
 *   (response 0.42 → 0.45, bounce 0.65), seeded with the finger velocity; the press scale uses
 *   its own jelly / candy release spring.
 * - **Re-touch**: a touch that lands while the element is still springing back stops the rubber
 *   and deformation springs where they are (`COUIITouchMotionUnit.handleTouchDown` cancels every
 *   unit) and resumes the drag from that displacement.
 *
 * A gesture another node consumes (a parent scroll stealing it) is handled like `ACTION_CANCEL`:
 * a release with zero velocity. Pointer events are only observed, never consumed. When
 * [enabled] turns `false` everything springs back (`COUITouchMotionAnimator.reset(true)`) and
 * touches are ignored.
 *
 * Scales compose additively as in `TouchMotionCollector` (`press + deform - 1`) around the
 * centre.
 *
 * @param interactionSource An extra press source (keyboard, hold-down); may be `null`.
 * @param enabled Whether the element reacts to touches.
 * @param pressScaleEnabled Whether the press scale unit runs (`COUIButton.scaleEnable`); the
 *   deformation and rubber band run either way.
 * @param touchSuppressed When non-null, the touch down is taken in the Final pass, after every
 *   handler in the Main pass has seen it, and a gesture for which this returns `true` never reaches
 *   the animator (no press scale, rubber band or deformation, not even for a frame). `null` keeps
 *   the Main-pass behaviour.
 * @param state The animator state. Hoist it when another element follows this one's motion (a
 *   cascading sub menu following its main menu, `ParentInfluenceUnit`).
 */
@Composable
internal fun Modifier.couiTouchMotion(
    interactionSource: InteractionSource?,
    enabled: Boolean = true,
    pressScaleEnabled: Boolean = true,
    touchSuppressed: (() -> Boolean)? = null,
    state: TouchMotionState = remember { TouchMotionState() },
): Modifier {
    val currentTouchSuppressed by rememberUpdatedState(touchSuppressed)
    val scope = rememberCoroutineScope()
    val sourcePressed = interactionSource?.collectIsPressedAsState()?.value ?: false
    val pressed = enabled && pressScaleEnabled && (state.touching || sourcePressed)

    LaunchedEffect(pressed) {
        val target = if (pressed) COUIMaterialEffect.pressTargetScale(state.widthDp, state.heightDp) else 1f
        // COUIPressAnimatorUnit.handleTouchDown cancels the running spring and restarts it with the
        // impact velocity (PressPhysicsEngine.calculateImpactVelocity, 0 at the default impact
        // factor); backAnimation re-targets the running spring, which keeps its momentum.
        val start = if (pressed) 0f else state.pressVelocity
        state.pressVelocity = start
        if (state.press.value != target || start != 0f) {
            state.press.animateTo(target, pressScaleSpring(state.widthDp, state.heightDp, pressed), start) {
                state.pressVelocity = velocity
            }
        }
        state.pressVelocity = 0f
    }
    LaunchedEffect(enabled) {
        if (!enabled) {
            state.touching = false
            with(state) { densityOrNull?.release(0f, 0f, scope) }
        }
    }

    return this
        .pointerInput(enabled) {
            if (!enabled) return@pointerInput
            state.densityOrNull = this
            awaitEachGesture {
                val suppressed = currentTouchSuppressed
                val down = awaitFirstDown(
                    requireUnconsumed = false,
                    pass = if (suppressed == null) PointerEventPass.Main else PointerEventPass.Final,
                )
                if (suppressed?.invoke() == true) {
                    do {
                        val event = awaitPointerEvent(PointerEventPass.Final)
                    } while (event.changes.any { it.pressed })
                    return@awaitEachGesture
                }
                val tracker = VelocityTracker()
                tracker.addPosition(down.uptimeMillis, down.position)
                var activeId: PointerId = down.id
                state.touching = true
                with(state) { down(down.position, scope) }
                var cancelled = false
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Final)
                    // ACTION_POINTER_DOWN: the newest pointer becomes the active one.
                    val newcomer = event.changes.firstOrNull { it.pressed && !it.previousPressed && it.id != activeId }
                    if (newcomer != null) {
                        activeId = newcomer.id
                        state.switchPointer(newcomer.position)
                        tracker.resetTracking()
                        tracker.addPosition(newcomer.uptimeMillis, newcomer.position)
                        continue
                    }
                    val active = event.changes.firstOrNull { it.id == activeId }
                    if (active == null || !active.pressed) {
                        // ACTION_POINTER_UP of the active pointer hands over to another one.
                        val next = event.changes.firstOrNull { it.pressed && it.id != activeId }
                        if (next != null) {
                            activeId = next.id
                            state.switchPointer(next.position)
                            tracker.resetTracking()
                            tracker.addPosition(next.uptimeMillis, next.position)
                            continue
                        }
                        active?.let { tracker.addPosition(it.uptimeMillis, it.position) }
                        break
                    }
                    if (active.positionChangedIgnoreConsumed() && active.isConsumed) {
                        // Someone else (a scrolling parent) took the gesture: ACTION_CANCEL.
                        cancelled = true
                        break
                    }
                    tracker.addPosition(active.uptimeMillis, active.position)
                    with(state) { move(active.position, scope) }
                }
                state.touching = false
                val velocity = if (cancelled) null else tracker.calculateVelocity()
                with(state) { release(velocity?.x ?: 0f, velocity?.y ?: 0f, scope) }
            }
        }
        .graphicsLayer {
            state.width = size.width
            state.height = size.height
            state.widthDp = size.width / density
            state.heightDp = size.height / density
            val press = state.press.value
            scaleX = press + state.scaleX.value - 1f
            scaleY = press + state.scaleY.value - 1f
            translationX = state.translationX.value
            translationY = state.translationY.value
        }
}

/**
 * Lets a touch target keep its gesture from a dragging ancestor, as a COUI 17 top-bar button does
 * with `requestDisallowInterceptTouchEvent(true)` on `ACTION_DOWN`
 * (`ToolbarMaterialEffectDelegate.handleTouch`): the button then keeps every move, so its touch
 * motion stretches it along a vertical drag too instead of the app bar taking the drag.
 *
 * The ancestor provides one through [LocalGestureHold] and leaves its drag disabled while [held].
 */
@Stable
internal class GestureHold {
    private var holders by mutableIntStateOf(0)

    /** Whether a touch target below is holding the current gesture. */
    val held: Boolean get() = holders > 0

    internal fun acquire() {
        holders++
    }

    internal fun release() {
        holders = max(holders - 1, 0)
    }
}

/** The [GestureHold] of the dragging ancestor, or `null` when none takes drags. */
internal val LocalGestureHold = staticCompositionLocalOf<GestureHold?> { null }

/**
 * Holds [hold] from a touch down on this element until every pointer is up. The down is observed
 * in [PointerEventPass.Initial], before any ancestor sees it; nothing is consumed, so the element's
 * own click and press handling are unaffected.
 */
internal fun Modifier.holdGesture(hold: GestureHold?): Modifier = if (hold == null) {
    this
} else {
    pointerInput(hold) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            hold.acquire()
            try {
                do {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                } while (event.changes.any { it.pressed })
            } finally {
                hold.release()
            }
        }
    }
}
