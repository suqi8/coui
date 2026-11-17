// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import io.github.suqi8.coui.kmp.effect.MaterialStrokePresets
import io.github.suqi8.coui.kmp.effect.SpotLightShader
import io.github.suqi8.coui.kmp.effect.SpotLightState
import io.github.suqi8.coui.kmp.effect.SpotLightType
import io.github.suqi8.coui.kmp.effect.materialStroke
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asBrush
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * One `COUISpringAnimation` value (`COUISpringForce`: damping ratio `1 - bounce`, natural frequency
 * `2π / response`), stepped with the exact damped-oscillator solution so a moving target can be
 * retargeted every event without restarting anything (`animateToFinalPosition`).
 */
private class PopupSpring(private val epsilon: Float) {
    var value = 0f
    var velocity = 0f
    var target = 0f
    private var omega = 0f
    private var zeta = 1f

    fun configure(response: Float, bounce: Float) {
        omega = 2f * PI.toFloat() / response
        zeta = (1f - bounce).coerceIn(0.01f, 1f)
    }

    fun snap(to: Float) {
        value = to
        target = to
        velocity = 0f
    }

    val isSettled: Boolean
        get() = abs(value - target) < epsilon && abs(velocity) < epsilon * 62.5f

    /** Advances by [dt] seconds; settles exactly on [target] once within [epsilon]. */
    fun step(dt: Float) {
        if (dt <= 0f) return
        val d0 = value - target
        val v0 = velocity
        if (zeta >= 1f) {
            val b = v0 + omega * d0
            val e = exp(-omega * dt)
            value = target + (d0 + b * dt) * e
            velocity = (b - omega * (d0 + b * dt)) * e
        } else {
            val wd = omega * sqrt(1f - zeta * zeta)
            val b = (v0 + zeta * omega * d0) / wd
            val e = exp(-zeta * omega * dt)
            val c = cos(wd * dt)
            val s = sin(wd * dt)
            value = target + e * (d0 * c + b * s)
            velocity = e * ((b * wd - zeta * omega * d0) * c - (d0 * wd + zeta * omega * b) * s)
        }
        if (isSettled) snap(target)
    }
}

/**
 * The touch spotlight of a COUI 17 popup panel, `COUISpotLightEffectDrawable` +
 * `COUISpotLightEffect` with `TYPE_TRANSLUCENT_LARGE_1`:
 * - **Glow** (DOWN): the progress springs to 1 on bounce 0 / response 0.15.
 * - **Follow** (MOVE): while lit, the light chases the finger on two bounce 0.15 / response 0.35
 *   springs (`mFollowHandX/YAnimation`, retargeted per event); while dark it jumps there.
 * - **Extinguish** (UP / CANCEL / `clearSpotLightState`): the progress springs to 0 on bounce 0 /
 *   response 0.4 while the radius spreads by `2 · endRadius · (1 - progress)`; reaching 0 snaps the
 *   follow springs to their target.
 *
 * The springs are stepped by one frame loop ([run]) that sleeps while everything is at rest, so an
 * event costs a few field writes and no coroutine or allocation. Positions are in the panel's own
 * pixels, as the drawable's bounds are the whole panel (`RoundFrameLayout.onSizeChanged`).
 */
@Stable
internal class PopupSpotLight {
    /** Glow progress, 0 (dark) → 1 (lit). */
    var progress: Float by mutableFloatStateOf(0f)
        private set
    var x: Float by mutableFloatStateOf(0f)
        private set
    var y: Float by mutableFloatStateOf(0f)
        private set

    /** Whether the light is going out (`mIsExtinguishAnimation`): the radius then spreads. */
    var extinguishing: Boolean = false
        private set

    private val glow = PopupSpring(epsilon = 1e-4f)
    private val followX = PopupSpring(epsilon = 0.05f).apply { configure(FOLLOW_RESPONSE, FOLLOW_BOUNCE) }
    private val followY = PopupSpring(epsilon = 0.05f).apply { configure(FOLLOW_RESPONSE, FOLLOW_BOUNCE) }
    private val wake = Channel<Unit>(Channel.CONFLATED)

    private val isAnimating: Boolean
        get() = !glow.isSettled || !followX.isSettled || !followY.isSettled

    /** `onTouchEvent(ACTION_DOWN)`: jump (or follow) to [position], then glow. */
    fun onDown(position: Offset) {
        setPosition(position)
        extinguishing = false
        glow.configure(GLOW_RESPONSE, 0f)
        glow.target = 1f
        wake.trySend(Unit)
    }

    /** `onTouchEvent(ACTION_MOVE)`. */
    fun onMove(position: Offset) = setPosition(position)

    /** `onTouchEvent(ACTION_UP)`: the light moves to the lift point, then goes out. */
    fun onUp(position: Offset) {
        setPosition(position)
        extinguish()
    }

    /** `extinguish()` / `clearSpotLightState()`. */
    fun extinguish() {
        if (glow.target == 0f && glow.value == 0f) return
        extinguishing = true
        glow.configure(EXTINGUISH_RESPONSE, 0f)
        glow.target = 0f
        wake.trySend(Unit)
    }

    /** `cancel()`: drops the light at once, e.g. when the panel leaves. */
    fun cancel() {
        glow.snap(0f)
        followX.snap(followX.target)
        followY.snap(followY.target)
        extinguishing = false
        progress = 0f
        x = followX.value
        y = followY.value
    }

    /** `COUISpotLightEffect.setLightPosition`: animated while lit, immediate while dark. */
    private fun setPosition(position: Offset) {
        if (glow.value != 0f) {
            followX.target = position.x
            followY.target = position.y
            wake.trySend(Unit)
        } else {
            followX.snap(position.x)
            followY.snap(position.y)
            x = position.x
            y = position.y
        }
    }

    /** The frame loop; runs for the lifetime of the panel. */
    suspend fun run() {
        while (true) {
            wake.receive()
            var last = -1L
            while (isAnimating) {
                withFrameNanos { now ->
                    val dt = if (last < 0L) 0f else (now - last) / 1_000_000_000f
                    last = now
                    glow.step(dt)
                    followX.step(dt)
                    followY.step(dt)
                    if (glow.value <= 0f && glow.target == 0f) {
                        // setProgress(0): the follow springs skip to their end.
                        glow.snap(0f)
                        followX.snap(followX.target)
                        followY.snap(followY.target)
                        extinguishing = false
                    }
                    progress = glow.value.coerceIn(0f, 1f)
                    x = followX.value
                    y = followY.value
                }
            }
        }
    }

    private companion object {
        const val GLOW_RESPONSE = 0.15f
        const val EXTINGUISH_RESPONSE = 0.4f
        const val FOLLOW_RESPONSE = 0.35f
        const val FOLLOW_BOUNCE = 0.15f
    }
}

/**
 * Creates a [SpotLightState] for surfaces that borrow the popup's touch light (the floating action
 * button and the floating toolbar). Popup panels use [rememberPopupPanelLight].
 */
@Composable
internal fun rememberPopupSpotLight(): SpotLightState = remember { SpotLightState() }

/**
 * Feeds touches on this node to a [SpotLightState] (glow at the DOWN point, follow, extinguish),
 * for the surfaces that borrow the popup's touch light. Observes in the initial pass and consumes
 * nothing.
 *
 * @param spotLight The spotlight to drive.
 * @param enabled Whether touches drive it.
 * @param suppressed Read on each touch; while `true` the spotlight stays dark.
 */
@Composable
internal fun Modifier.popupSpotLightInput(
    spotLight: SpotLightState,
    enabled: Boolean,
    suppressed: () -> Boolean = { false },
): Modifier {
    if (!enabled || !isRuntimeShaderSupported()) return this
    val scope = rememberCoroutineScope()
    val currentSuppressed by rememberUpdatedState(suppressed)
    return pointerInput(spotLight) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            scope.launch { if (!currentSuppressed()) spotLight.glow(down.position) }
            while (true) {
                val change = awaitPointerEvent(PointerEventPass.Initial).changes
                    .firstOrNull { it.id == down.id } ?: break
                if (change.changedToUp() || !change.pressed) break
                scope.launch { if (!currentSuppressed()) spotLight.follow(change.position) }
            }
            scope.launch { spotLight.extinguish() }
        }
    }
}

/** Creates the spotlight of a popup panel and runs its frame loop for as long as it is composed. */
@Composable
internal fun rememberPopupPanelLight(): PopupSpotLight {
    val light = remember { PopupSpotLight() }
    LaunchedEffect(light) {
        try {
            light.run()
        } finally {
            light.cancel()
        }
    }
    return light
}

/**
 * Feeds the touches this node dispatches to [spotLight], as `RoundFrameLayout.dispatchTouchEvent`
 * hands every event to `COUISpotLightEffectDrawable.onTouchEvent`: DOWN glows, MOVE follows, UP moves
 * and extinguishes, a gesture that ends without an UP extinguishes, and a second pointer takes over
 * when the tracked one lifts. Observes in the initial pass and consumes nothing, so the rows still
 * see the whole gesture (the light follows drag-to-select).
 *
 * @param spotLight The spotlight to drive.
 * @param enabled Whether touches drive it.
 * @param suppressed Read per event; while `true` the event does not reach the light
 *   (`setSpotLightTouchEnabled(false)`).
 */
@Composable
internal fun Modifier.popupPanelLightInput(
    spotLight: PopupSpotLight,
    enabled: Boolean,
    suppressed: () -> Boolean = { false },
): Modifier {
    if (!enabled || !isRuntimeShaderSupported()) return this
    val currentSuppressed by rememberUpdatedState(suppressed)
    return pointerInput(spotLight) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            if (!currentSuppressed()) spotLight.onDown(down.position)
            var active: PointerId = down.id
            var lifted = false
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                val change = event.changes.firstOrNull { it.id == active } ?: break
                if (!change.pressed) {
                    // ACTION_POINTER_UP of the tracked pointer hands over to another one.
                    val next = event.changes.firstOrNull { it.pressed && it.id != active }
                    if (next != null) {
                        active = next.id
                        if (!currentSuppressed()) spotLight.onMove(next.position)
                        continue
                    }
                    if (!currentSuppressed()) spotLight.onUp(change.position)
                    lifted = true
                    break
                }
                if (!currentSuppressed()) spotLight.onMove(change.position)
            }
            // ACTION_CANCEL.
            if (!lifted && !currentSuppressed()) spotLight.extinguish()
        }
    }
}

/**
 * Draws [light] the way a ColorOS 17 popup shows it. `RoundFrameLayout.onDraw` paints the
 * `TYPE_TRANSLUCENT_LARGE_1` shader with OVERLAY onto the panel's own canvas, whose background is
 * the window background-blur drawable (`COUIBackgroundBlurBuilder.applyBlurBackground`): the canvas
 * itself stays transparent and the blur, mix and blend colours are composed by the window
 * compositor. OVERLAY onto a transparent destination returns the source unchanged, so what reaches
 * the screen is the light composited source-over onto the glass; drawing it source-over here, on top
 * of this surface's fill or blur, gives the same pixels.
 *
 * @param offset The panel origin in this draw scope (non-zero when the scope is the compact sub
 *   menu's band rather than the whole panel).
 */
internal fun DrawScope.drawPopupSpotLight(
    shader: RuntimeShader,
    light: PopupSpotLight,
    isDark: Boolean,
    offset: Offset = Offset.Zero,
) {
    val glow = light.progress
    if (glow <= 0f) return
    val type = SpotLightType.TranslucentLarge1
    SpotLightShader.setUniforms(
        shader = shader,
        lightX = light.x - offset.x,
        lightY = light.y - offset.y,
        radius = SpotLightShader.radius(glow, light.extinguishing, type.endRadius),
        intensity = type.endIntensity(isDark) * glow,
    )
    drawRect(brush = shader.asBrush(), blendMode = BlendMode.SrcOver)
}

/**
 * The material effects of a COUI 17 popup panel (`RoundFrameLayout.initMaterialEffects`, drawn in
 * `RoundFrameLayout.onDraw` above the background and below the rows): the `COUIShadowEdgeDrawable`
 * stroke (`EDGE_STYLE_2` / `SHADOW_STYLE_2`, [MaterialStrokePresets.PopupLight] /
 * [MaterialStrokePresets.PopupDark], `setPaintBlendMode(SRC_OVER)`, popup corner weight) and the
 * touch spotlight (see [drawPopupSpotLight]). Apply it inside the surface's clip so both stay inside
 * its corners. Needs runtime shaders; elsewhere nothing is drawn, like COUI on a device without the
 * effects.
 *
 * @param enabled Whether the effects are drawn.
 * @param spotLight The spotlight to draw.
 * @param trackTouches Whether this node feeds its own touches to [spotLight]; turn it off when an
 *   ancestor does it with [popupPanelLightInput].
 * @param spotLightSuppressed Read per event; while it returns `true` touches here do not reach the
 *   spotlight (COUI turns the main menu's off while a sub menu is open).
 * @param spotLightOffset The panel origin in this node, read at draw time.
 */
@Composable
internal fun Modifier.popupMaterialEffect(
    enabled: Boolean,
    spotLight: PopupSpotLight,
    trackTouches: Boolean = true,
    spotLightSuppressed: () -> Boolean = { false },
    spotLightOffset: (() -> Offset)? = null,
): Modifier {
    if (!enabled) return this
    val shader = remember { if (isRuntimeShaderSupported()) RuntimeShader(SpotLightShader.Source) else null }
    val isDark = COUITheme.colorScheme.isDark
    return this
        .materialStroke(
            light = MaterialStrokePresets.PopupLight,
            dark = MaterialStrokePresets.PopupDark,
            topCornerRadius = PopupCornerRadius,
            bottomCornerRadius = PopupCornerRadius,
            weight = PopupCornerWeight,
        )
        .then(if (trackTouches) Modifier.popupPanelLightInput(spotLight, enabled = true, suppressed = spotLightSuppressed) else Modifier)
        .then(
            if (shader == null) {
                Modifier
            } else {
                Modifier.drawBehind {
                    drawPopupSpotLight(shader, spotLight, isDark, spotLightOffset?.invoke() ?: Offset.Zero)
                }
            },
        )
}
