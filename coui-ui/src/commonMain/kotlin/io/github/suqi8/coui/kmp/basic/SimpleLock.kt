// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

/** The number of dots of a [SimpleLock] (`couiCircleNum` / `setSimpleLockType`). */
enum class SimpleLockType(val count: Int) {
    /** Four dots (`DEFAULTTYPE`). */
    Four(4),

    /** Six dots (`SIXCIRCLETYPE`). */
    Six(6),
}

/**
 * The animation state of a [SimpleLock]. Create it with [rememberSimpleLockState].
 */
@Stable
class SimpleLockState internal constructor() {
    internal var shownCount by mutableIntStateOf(0)
    internal var failing by mutableStateOf(false)
    internal var failCount = 0
    internal var shakeX by mutableFloatStateOf(0f)
    internal var fallY by mutableFloatStateOf(0f)
    internal var hapticRequest by mutableIntStateOf(0)
    internal val dots = Array(6) { SimpleLockDotState() }

    /**
     * Plays the failure (`createFailedAnimator`, 800ms, linear): the dots shake sideways through
     * 30, -28, 14, -8, 4, -3, 0px while the filled ones fall 250px one after another, fading out
     * over their first 150px, with a reject haptic. Returns when it ends with every dot empty
     * (COUI `fail_lastDraw`); reset `filledCount` to 0, and the next digit fills the first dot.
     */
    suspend fun playFailure() {
        failCount = shownCount
        failing = true
        hapticRequest++
        val progress = Animatable(0f)
        try {
            progress.animateTo(1f, tween(FAIL_DURATION_MS, easing = LinearEasing)) {
                shakeX = shakeAt(value)
                fallY = FALL_SPEED_PX * value
            }
        } finally {
            shakeX = 0f
            fallY = 0f
            failing = false
            dots.forEach { it.snap(filled = false) }
            // The row is empty now: count from zero so the next change of filledCount animates
            // from what is shown instead of from the count before the failure.
            shownCount = 0
        }
    }
}

/** The per-dot values (`mOpacitys`, `mCircleScales`, `mGlowScales`, `mGlowOpacitys`). */
internal class SimpleLockDotState {
    var fill by mutableFloatStateOf(0f)
    var scale by mutableFloatStateOf(1f)
    var glowScale by mutableFloatStateOf(0f)
    var glowAlpha by mutableFloatStateOf(0f)
    var job: Job? = null

    fun snap(filled: Boolean) {
        job?.cancel()
        fill = if (filled) 1f else 0f
        scale = 1f
        glowScale = 0f
        glowAlpha = 0f
    }
}

/** Creates and remembers a [SimpleLockState]. */
@Composable
fun rememberSimpleLockState(): SimpleLockState = remember { SimpleLockState() }

/**
 * The COUI PIN dot row (`COUISimpleLock`): four or six 10dp dots, 10dp apart, centered in the
 * available width. Dots up to [filledCount] are filled, the rest outlined.
 *
 * Changes of [filledCount] animate like COUI: a new digit fills its dot at once (`setOneCode`;
 * with [glowEffect], the dot springs in with a glow instead), deleting one fades the last dot
 * out over 230ms (`setDeleteLast`, `COUIEaseInterpolator`) and clearing several fades them all
 * (`setClearAll`). Call [SimpleLockState.playFailure] to shake the row and drop the dots.
 *
 * The row is `10dp + 150px` tall (`onMeasure`: the drawable height plus room for the fall).
 *
 * @param filledCount The number of entered digits, 0 to [SimpleLockType.count].
 * @param modifier The modifier applied to the row.
 * @param type The number of dots.
 * @param state The [SimpleLockState].
 * @param glowEffect Whether a new dot springs in with a glow (the transparent style with
 *   `couiGlowEffectDrawable`, response 0.35 / bounce 0.5).
 * @param hapticFeedbackEnabled Whether the failure vibrates.
 * @param colors The [SimpleLockColors].
 * @param contentDescription The accessibility description of the row
 *   (`coui_simple_lock_access_description`).
 */
@Composable
fun SimpleLock(
    filledCount: Int,
    modifier: Modifier = Modifier,
    type: SimpleLockType = SimpleLockType.Four,
    state: SimpleLockState = rememberSimpleLockState(),
    glowEffect: Boolean = false,
    hapticFeedbackEnabled: Boolean = true,
    colors: SimpleLockColors = SimpleLockDefaults.colors(),
    contentDescription: String? = null,
) {
    val count = type.count
    val target = filledCount.coerceIn(0, count)
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val currentHapticEnabled by rememberUpdatedState(hapticFeedbackEnabled)

    val currentHaptic by rememberUpdatedState(haptic)
    LaunchedEffect(state) {
        // Only requests made while composed: re-entering the composition must not vibrate again.
        snapshotFlow { state.hapticRequest }.drop(1).collect {
            if (currentHapticEnabled) currentHaptic.performHapticFeedback(HapticFeedbackType.Reject)
        }
    }

    LaunchedEffect(state, target, glowEffect) {
        val previous = state.shownCount
        state.shownCount = target
        if (state.failing || previous == target) return@LaunchedEffect
        when {
            target > previous -> for (index in previous until target) {
                val dot = state.dots[index]
                if (glowEffect) springIn(dot, scope) else dot.snap(filled = true)
            }

            target == previous - 1 -> fadeOut(state.dots[target], scope, glowEffect)

            else -> for (index in target until previous) fadeOut(state.dots[index], scope, glowEffect = false)
        }
    }

    val density = LocalDensity.current
    val fallRoom = with(density) { FALL_HEIGHT_PX.toDp() }
    val glowBrush = remember { Brush.radialGradient(colorStops = GlowStops, center = Offset.Zero, radius = 1f) }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(SimpleLockDefaults.DotSize + fallRoom)
            .then(if (contentDescription != null) Modifier.semantics { this.contentDescription = contentDescription } else Modifier),
    ) {
        val dotSize = SimpleLockDefaults.DotSize.toPx()
        val padding = SimpleLockDefaults.DotSpacing.toPx()
        val radius = dotSize / 2f
        val rowWidth = dotSize * count + padding * (count - 1)
        val start = (size.width - rowWidth) / 2f
        val delays = if (count == 4) FallDelayFour else FallDelaySix
        val glowRadius = radius + SimpleLockDefaults.GlowOffset.toPx()
        for (index in 0 until count) {
            val cx = start + index * (dotSize + padding) + radius + state.shakeX
            val center = Offset(cx, radius)
            drawCircle(colors.outlinedColor, radius, center)
            if (state.failing) {
                if (index < state.failCount) {
                    val fall = max(0f, state.fallY - delays[index])
                    val alpha = (1f - fall / FALL_HEIGHT_PX).coerceIn(0f, 1f)
                    if (alpha > 0f) drawCircle(colors.filledColor, radius, Offset(cx, radius + fall), alpha = alpha)
                }
                continue
            }
            val dot = state.dots[index]
            val fill = dot.fill.coerceIn(0f, 1f)
            if (fill > 0f) {
                val scale = dot.scale.coerceIn(0f, MAX_DOT_SCALE)
                if (scale > 0f) drawCircle(colors.filledColor, radius * scale, center, alpha = fill)
            }
            if (glowEffect && dot.glowAlpha > 0f && dot.glowScale > 0f) {
                val r = glowRadius * dot.glowScale
                translate(center.x, center.y) {
                    scale(r, r, pivot = Offset.Zero) {
                        drawCircle(brush = glowBrush, radius = 1f, center = Offset.Zero, alpha = dot.glowAlpha.coerceIn(0f, 1f))
                    }
                }
            }
        }
    }
}

/** `createPreAnimationOutLinedToFilled`: the dot springs to 1 while its glow flares and fades. */
private fun springIn(dot: SimpleLockDotState, scope: CoroutineScope) {
    dot.job?.cancel()
    dot.fill = 0f
    dot.scale = 0f
    dot.glowScale = 0f
    dot.glowAlpha = 0f
    dot.job = scope.launch {
        val glowProgress = Animatable(0f)
        val glowJob = launch { glowProgress.animateTo(1f, tween(MORPH_DURATION_MS, easing = LinearEasing)) }
        var last = 0f
        Animatable(0f).animateTo(1f, AddSpring) {
            dot.scale = max(0f, value)
            dot.glowScale = max(0f, value)
            dot.fill = value.coerceIn(0f, 1f)
            val progress = glowProgress.value
            dot.glowAlpha = when {
                progress <= GLOW_FADE_IN_END -> value.coerceIn(0f, 1f)
                progress < 1f -> 1f
                else -> (dot.glowAlpha - max(abs(value - last) * 2f, GLOW_FADE_OUT_STEP)).coerceIn(0f, 1f)
            }
            last = value
        }
        glowJob.join()
        // After the spring settles COUI keeps lowering the glow by at least 15/255 a frame.
        if (dot.glowAlpha > 0f) {
            val duration = ((dot.glowAlpha / GLOW_FADE_OUT_STEP) * FRAME_MS).toInt()
            Animatable(dot.glowAlpha).animateTo(0f, tween(duration, easing = LinearEasing)) { dot.glowAlpha = value }
        }
    }
}

/** `createMorphingAnimationFilledToOutLined` (230ms, COUIEase), or the glow style's spring out. */
private fun fadeOut(dot: SimpleLockDotState, scope: CoroutineScope, glowEffect: Boolean) {
    dot.job?.cancel()
    val from = dot.fill
    dot.glowAlpha = 0f
    dot.glowScale = 0f
    dot.job = scope.launch {
        if (glowEffect) {
            val startScale = dot.scale
            Animatable(1f).animateTo(0f, RemoveSpring) {
                dot.scale = max(0f, startScale * value)
                dot.fill = (from * value).coerceIn(0f, 1f)
            }
        } else {
            dot.scale = 1f
            Animatable(from).animateTo(0f, tween(MORPH_DURATION_MS, easing = SimpleLockEase)) { dot.fill = value }
        }
    }
}

/** Contains the default values used by [SimpleLock]. */
object SimpleLockDefaults {
    /** The dot diameter (`coui_simple_lock_*_rectangle_icon` size). */
    val DotSize: Dp = 10.dp

    /** The gap between dots (`couiRectanglePadding`). */
    val DotSpacing: Dp = 10.dp

    /** How far the glow reaches past the dot (`coui_glow_ball_offset`). */
    val GlowOffset: Dp = 25.dp

    /**
     * The default colors (`Widget.COUI.COUISimpleLock` / `.Dark`): filled
     * `coui_simple_lock_filled_rectangle_icon_color` #000000 / #E6FFFFFF, outlined
     * `coui_simple_lock_outlined_rectangle_icon_color` #19000000 / #66FFFFFF.
     */
    @Composable
    fun colors(
        filledColor: Color = if (isDark()) Color(0xE6FFFFFF) else Color.Black,
        outlinedColor: Color = if (isDark()) Color(0x66FFFFFF) else Color(0x19000000),
    ): SimpleLockColors = remember(filledColor, outlinedColor) { SimpleLockColors(filledColor, outlinedColor) }

    /**
     * The colors over a wallpaper (`coui_simple_lock_transparent_*_rectangle_icon_color`):
     * filled #FFFFFF, outlined #26FFFFFF.
     */
    @Composable
    fun transparentColors(
        filledColor: Color = Color.White,
        outlinedColor: Color = Color(0x26FFFFFF),
    ): SimpleLockColors = remember(filledColor, outlinedColor) { SimpleLockColors(filledColor, outlinedColor) }

    @Composable
    private fun isDark(): Boolean = COUITheme.colorScheme.background.luminance() < 0.5f
}

/**
 * Colors used by a [SimpleLock].
 *
 * @param filledColor The entered dots (`couiFilledRectangleIconDrawable`).
 * @param outlinedColor The empty dots (`couiOutLinedRectangleIconDrawable`, a filled disc despite
 *   the name).
 */
@Immutable
data class SimpleLockColors(
    val filledColor: Color,
    val outlinedColor: Color,
)

/** `SHAKE_X_POINT`, evenly spaced over the failure. */
private val ShakePoints = floatArrayOf(0f, 30f, -28f, 14f, -8f, 4f, -3f, 0f)

private fun shakeAt(fraction: Float): Float {
    val position = fraction.coerceIn(0f, 1f) * (ShakePoints.size - 1)
    val index = position.toInt().coerceAtMost(ShakePoints.size - 2)
    val local = position - index
    return ShakePoints[index] + (ShakePoints[index + 1] - ShakePoints[index]) * local
}

/** `DELAY_FOUR` / `DELAY_SIX`: how far (px) the fall runs before each dot starts dropping. */
private val FallDelayFour = floatArrayOf(0f, 38.5f, 91f, 63f)
private val FallDelaySix = floatArrayOf(0f, 38.5f, 91f, 63f, 38.5f, 70f)

/**
 * `coui_simple_lock_glow_effect_drawable.png` against the fraction of its radius: the opaque dot
 * to 17%, then a halo of 43% at 20%, 25% at 27%, 15% at 33%, 5% at 43% gone by 55%.
 */
private val GlowStops = arrayOf(
    0f to Color.White,
    0.167f to Color.White,
    0.2f to Color.White.copy(alpha = 0.435f),
    0.267f to Color.White.copy(alpha = 0.247f),
    0.333f to Color.White.copy(alpha = 0.149f),
    0.433f to Color.White.copy(alpha = 0.055f),
    0.5f to Color.White.copy(alpha = 0.031f),
    0.55f to Color.Transparent,
    1f to Color.Transparent,
)

private const val FAIL_DURATION_MS = 800
private const val FALL_SPEED_PX = 250f
private const val FALL_HEIGHT_PX = 150f
private const val MORPH_DURATION_MS = 230
private const val MAX_DOT_SCALE = 1.2f
private const val GLOW_FADE_IN_END = 0.4f
private const val GLOW_FADE_OUT_STEP = 15f / 255f
private const val FRAME_MS = 16f

/** `COUIEaseInterpolator`: cubic-bezier(0.33, 0, 0.67, 1). */
private val SimpleLockEase = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** `SPRING_RESPONSE` 0.35 / `SPRING_BOUNCE` 0.5. */
private val AddSpring = spring<Float>(dampingRatio = 0.5f, stiffness = 322.27f)

/** The delete spring: response 0.35, bounce 0. */
private val RemoveSpring = spring<Float>(dampingRatio = 1f, stiffness = 322.27f)
