// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.animation.core.withInfiniteAnimationFrameNanos
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.LongState
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import io.github.suqi8.coui.kmp.anim.COUIInEaseEasing
import io.github.suqi8.coui.kmp.anim.COUIMoveEaseEasing
import io.github.suqi8.coui.kmp.anim.COUIOutEaseEasing
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.sqrt

/**
 * A [LinearProgressIndicator] with COUI style.
 *
 * The determinate form ports COUIHorizontalProgressBar: a capsule track with a square-edged fill
 * clipped to it, redrawn directly on every progress change (no progress animation), filling from
 * the end edge in right-to-left layouts.
 *
 * The indeterminate form (`progress = null`) is a library extension with no COUI counterpart:
 * COUIProgressHorizontal is `indeterminateOnly = false` and draws no indeterminate animation, so the
 * three-segment loop drawn here is not sourced from ColorOS.
 *
 * @param modifier The modifier to be applied to the indicator.
 * @param progress The current progress value between 0.0f and 1.0f, or null for indeterminate state.
 * @param enabled Whether the indicator is enabled. A disabled indicator switches to the disabled
 *   foreground and track colors, as the `state_enabled="false"` items of
 *   coui_progressbar_progress_selector / coui_progressbar_background_selector do.
 * @param colors The colors used for the indicator.
 * @param height The height of the indicator.
 */
@Composable
fun LinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    enabled: Boolean = true,
    colors: ProgressIndicatorColors = ProgressIndicatorDefaults.progressIndicatorColors(),
    height: Dp = ProgressIndicatorDefaults.DefaultLinearProgressIndicatorHeight,
) {
    val currentBackgroundColor = colors.backgroundColor(enabled)
    val currentForegroundColor = colors.foregroundColor(enabled)
    // COUIHorizontalProgressBar.onDraw fills from the right edge when the layout is RTL.
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val directionScaleX = if (isRtl) -1f else 1f

    if (progress == null) {
        val transition = rememberInfiniteTransition()
        val animatedValue by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1250, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        )

        Canvas(
            modifier = modifier
                .semantics { progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate }
                .fillMaxWidth()
                .height(height),
        ) {
            drawRoundRect(
                color = currentBackgroundColor,
                size = size,
                cornerRadius = CornerRadius(size.height / 2),
            )

            scale(scaleX = directionScaleX, scaleY = 1f) {
                for (i in 0 until 3) {
                    drawIndeterminateSegment(animatedValue, i, currentForegroundColor)
                }
            }
        }
    } else {
        val progressValue = progress.coerceIn(0f, 1f)

        Canvas(
            modifier = modifier
                .semantics { progressBarRangeInfo = ProgressBarRangeInfo(progressValue, 0f..1f) }
                .fillMaxWidth()
                .height(height),
        ) {
            // COUIHorizontalProgressBar.onDraw: the fill is a full-height rectangle with a
            // square leading edge, intersected with the capsule track path (Path.Op.INTERSECT),
            // so only the track's own corners round the fill.
            val cornerRadius = size.height / 2
            val trackPath = Path().apply {
                addRoundRect(RoundRect(0f, 0f, size.width, size.height, CornerRadius(cornerRadius)))
            }

            clipPath(trackPath) {
                drawRect(
                    color = currentBackgroundColor,
                    size = size,
                )
                scale(scaleX = directionScaleX, scaleY = 1f) {
                    drawRect(
                        color = currentForegroundColor,
                        size = Size(size.width * progressValue, size.height),
                    )
                }
            }
        }
    }
}

/**
 * A [CircularProgressIndicator] with COUI style.
 *
 * With a [progress] value it ports COUICircularProgressBar / COUICircularProgressDrawable: a
 * neutral arc over a divider-colored track, starting at 12 o'clock, which follows progress changes
 * on a critically damped spring (dampingRatio 1, stiffness 50). Like `onMeasure` /
 * `configDrawable`, the indicator lays out at [size] plus 4dp of padding on each side and draws a
 * ring whose outer diameter is [size] less 4dp on each side: the default large tier (40dp) is a
 * 48dp box around a 32dp ring. It can be [CircularProgressState.Paused] (the ring fades out and
 * two bars fade in, [CircularProgressType.OnImage] only) or show a
 * [CircularProgressState.Error] (the ring shrinks to a 19dp outline around an exclamation mark).
 *
 * Without a [progress] it ports COUILoadingView, a ring across the whole [size] whose arc and track
 * use the theme accent (couiColorPrimary on couiColorSecondary), so the default [colors], [size]
 * and [strokeWidth] depend on [progress]. Its arc turns once a second and, like COUILoadingView,
 * which reads `uptimeMillis` in `onDraw`, takes its phase from the frame clock, so every loading
 * view on screen spins in step. [state] and [type] only apply with a [progress].
 *
 * @param modifier The modifier to be applied to the indicator.
 * @param progress The current progress value between 0.0f and 1.0f, or null for indeterminate state.
 * @param state The [CircularProgressState]: `pause()` / `resume()` / `error()` / `recover()`.
 * @param type The [CircularProgressType] (`couiCircularProgressBarType`).
 * @param colors The colors used for the indicator. Defaults to
 *   [ProgressIndicatorDefaults.circularProgressIndicatorColors] with a [progress] value and to
 *   [ProgressIndicatorDefaults.loadingViewColors] without one.
 * @param strokeWidth The width of the circular stroke.
 * @param size The length of the indicator: the `coui_circular_progress_*_length` tier with a
 *   [progress], or the diameter of the loading view without one.
 */
@Composable
fun CircularProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    state: CircularProgressState = CircularProgressState.Normal,
    type: CircularProgressType = CircularProgressType.Default,
    colors: ProgressIndicatorColors = if (progress == null) {
        ProgressIndicatorDefaults.loadingViewColors()
    } else {
        ProgressIndicatorDefaults.circularProgressIndicatorColors()
    },
    strokeWidth: Dp = if (progress == null) {
        ProgressIndicatorDefaults.DefaultLoadingViewStrokeWidth
    } else {
        ProgressIndicatorDefaults.LargeCircularProgressIndicatorStrokeWidth
    },
    size: Dp = if (progress == null) {
        ProgressIndicatorDefaults.DefaultLoadingViewSize
    } else {
        ProgressIndicatorDefaults.LargeCircularProgressIndicatorSize
    },
) {
    val currentBackgroundColor = colors.backgroundColor()
    val currentForegroundColor = colors.foregroundColor(true)

    if (progress == null) {
        // COUILoadingView.onDraw derives the arc from the clock, not from its animator (which only
        // invalidates): (uptimeMillis % 1000) * 360 / 1000, one linear turn per second. Reading the
        // frame clock the same way keeps every loading view on screen in the same phase.
        val frameTimeMillis = rememberLoadingViewFrameTime()

        Canvas(
            modifier = modifier
                .semantics { progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate }
                .size(size),
        ) {
            val strokeWidthPx = strokeWidth.toPx()
            val radius = (size.toPx() - strokeWidthPx) / 2
            val center = Offset(size.toPx() / 2, size.toPx() / 2)
            // COUILoadingView.drawBackgroundCircle and the arc go straight onto the canvas with the
            // default blending (no layer, unlike COUICircularProgressDrawable): the arc covers the track.
            drawCircularBackground(currentBackgroundColor, radius, center, strokeWidthPx)
            val time = frameTimeMillis.longValue
            // The first frame is drawn before the clock has ticked; show the track alone rather than
            // an arc at a phase it would jump away from.
            if (time < 0L) return@Canvas
            val cycle = (time % LoadingViewTurnMillis) * 360f / LoadingViewTurnMillis
            // COUILoadingView: drawArc(rect, cycle - 30, (2 - |180 - cycle| / 180) * 60, ...)
            // on a canvas pre-rotated by -90 degrees; the sweep pulses 60 -> 120 -> 60
            // within a single rotation.
            val sweepAngle = (2f - abs(180f - cycle) / 180f) * 60f
            val startAngle = cycle - 30f - 90f
            drawArc(
                color = currentForegroundColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(2 * radius, 2 * radius),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
            )
        }
    } else {
        val progressValue = progress.coerceIn(0f, 1f)
        // COUICircularProgressDrawable.setProgress(p, animate = true) drives the visual progress
        // with a SpringAnimation of SpringForce(dampingRatio = 1.0, stiffness = 50).
        val visualProgress by animateFloatAsState(
            targetValue = progressValue,
            animationSpec = CircularProgressSpring,
        )
        val stateAnimation = rememberCircularProgressStateAnimation(
            paused = state == CircularProgressState.Paused && type == CircularProgressType.OnImage,
            error = state == CircularProgressState.Error,
        )
        // configDrawable: TYPE_FOLLOW_THEME draws the track at alpha 89.
        val trackColor = if (type == CircularProgressType.FollowTheme) {
            currentBackgroundColor.copy(alpha = FollowThemeTrackAlpha)
        } else {
            currentBackgroundColor
        }
        val errorColor = colors.errorColor()
        val pauseIconColor = colors.pauseIconColor()
        val shadowColor = colors.shadowColor()
        val drawShadow = type == CircularProgressType.OnImage

        Canvas(
            modifier = modifier
                .semantics { progressBarRangeInfo = ProgressBarRangeInfo(progressValue, 0f..1f) }
                .size(size + CircularProgressPadding * 2),
        ) {
            val center = this.center
            val error = stateAnimation.error.value
            // The error animators move the ring from its own geometry to the 19dp / 1.5dp outline.
            val outerDiameter = lerp((size - CircularProgressPadding * 2).toPx(), CircularErrorDiameter.toPx(), error)
            val strokeWidthPx = lerp(strokeWidth.toPx(), CircularErrorStrokeWidth.toPx(), error)
            val radius = ((outerDiameter - strokeWidthPx) / 2f).coerceAtLeast(0f)
            val ringTrackColor = lerp(trackColor, errorColor, error)
            val ringProgressColor = lerp(currentForegroundColor, errorColor, error)

            // COUICircularProgressDrawable.drawProgress: sweep = max(1e-4, progress * 360 / max).
            val sweepAngle = maxOf(1.0E-4f, visualProgress * 360f)

            val ringAlpha = stateAnimation.ringAlpha.value
            if (ringAlpha > 0f) {
                if (drawShadow) drawRingShadow(shadowColor, radius, center, strokeWidthPx, ringAlpha)
                withSrcCompositingLayer(alpha = ringAlpha) {
                    drawCircularBackground(ringTrackColor, radius, center, strokeWidthPx)

                    drawArc(
                        color = ringProgressColor,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(2 * radius, 2 * radius),
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
                        blendMode = BlendMode.Src,
                    )
                }
            }

            // drawPauseIcon: two 4 x 16dp bars 4dp apart with 1dp corners.
            val pauseAlpha = stateAnimation.pauseIconAlpha.value
            if (pauseAlpha > 0f) {
                scale(stateAnimation.pauseIconScale.value, pivot = center) {
                    val barWidth = CircularPauseBarWidth.toPx()
                    val barHeight = CircularPauseBarHeight.toPx()
                    val gap = CircularPauseBarGap.toPx()
                    val corner = CornerRadius(CircularPauseBarRadius.toPx())
                    for (left in floatArrayOf(center.x - gap / 2f - barWidth, center.x + gap / 2f)) {
                        drawRoundRect(
                            color = pauseIconColor,
                            topLeft = Offset(left, center.y - barHeight / 2f),
                            size = Size(barWidth, barHeight),
                            cornerRadius = corner,
                            alpha = pauseAlpha,
                        )
                    }
                }
            }

            // drawErrorIcon: an exclamation mark, a 1.6 x 5.25dp bar over a 1.1dp-radius dot.
            if (error > 0f) {
                scale(ErrorIconMinScale + (1f - ErrorIconMinScale) * error, pivot = center) {
                    val barWidth = CircularErrorBarWidth.toPx()
                    val barTop = center.y - CircularErrorBarBias.toPx()
                    drawRect(
                        color = errorColor,
                        topLeft = Offset(center.x - barWidth / 2f, barTop),
                        size = Size(barWidth, CircularErrorBarHeight.toPx()),
                        alpha = error,
                    )
                    drawCircle(
                        color = errorColor,
                        radius = CircularErrorDotRadius.toPx(),
                        center = Offset(center.x, center.y + CircularErrorDotBias.toPx()),
                        alpha = error,
                    )
                }
            }
        }
    }
}

/**
 * The state of a determinate [CircularProgressIndicator] (`COUICircularProgressBar.pause()` /
 * `resume()` / `error()` / `recover()`).
 */
enum class CircularProgressState {
    /** The ring shows the progress. */
    Normal,

    /**
     * The ring fades out in 200ms and a pause icon fades in after it, 300ms on the COUI in-ease
     * curve, growing from 70%. Only a [CircularProgressType.OnImage] indicator pauses, as
     * `pause()` refuses the other types.
     */
    Paused,

    /**
     * In 350ms on the COUI move-ease curve the ring shrinks to a 19dp, 1.5dp outline in the error
     * color around an exclamation mark that fades in, growing from 70%.
     */
    Error,
}

/** The look of a determinate [CircularProgressIndicator] (`couiCircularProgressBarType`). */
enum class CircularProgressType {
    /** `TYPE_DEFAULT`. */
    Default,

    /**
     * `TYPE_ON_IMAGE`: the track and the icons cast a soft shadow (radius 4dp, 2dp down) so the
     * indicator reads over pictures; the only type that can pause.
     */
    OnImage,

    /** `TYPE_FOLLOW_THEME`: the track is drawn at alpha 89 / 255. */
    FollowTheme,
}

/** The animated state values of a determinate [CircularProgressIndicator]. */
@Stable
private class CircularProgressStateAnimation {
    /** `mProgressBarAlpha`. */
    val ringAlpha = Animatable(1f)

    /** `mCurrentPauseIconAlpha`. */
    val pauseIconAlpha = Animatable(0f)

    /** `mIconPauseScale`. */
    val pauseIconScale = Animatable(1f)

    /** The progress of the error animators: 0 normal, 1 error (`mCurrentErrorIconAlpha`). */
    val error = Animatable(0f)
}

@Composable
private fun rememberCircularProgressStateAnimation(paused: Boolean, error: Boolean): CircularProgressStateAnimation {
    val animation = remember { CircularProgressStateAnimation() }
    LaunchedEffect(paused) {
        val target = if (paused) 1f else 0f
        if (animation.pauseIconAlpha.value == target && animation.ringAlpha.value == 1f - target) return@LaunchedEffect
        if (paused) {
            // initPauseAnimator: the ring fades out (200ms, linear) while the icon fades in after a
            // 200ms delay (300ms, in-ease) from 70% scale.
            launch { animation.ringAlpha.animateTo(0f, tween(200, easing = LinearEasing)) }
            launch {
                animation.pauseIconScale.snapTo(PauseIconMinScale)
                animation.pauseIconAlpha.snapTo(0f)
                delay(200)
                launch { animation.pauseIconScale.animateTo(1f, tween(300, easing = COUIInEaseEasing)) }
                animation.pauseIconAlpha.animateTo(1f, tween(300, easing = COUIInEaseEasing))
            }
        } else {
            // initResumeAnimator: the icon fades out (200ms, linear) at full scale while the ring
            // fades in after a 200ms delay (200ms, out-ease).
            animation.pauseIconScale.snapTo(1f)
            launch { animation.pauseIconAlpha.animateTo(0f, tween(200, easing = LinearEasing)) }
            launch { animation.ringAlpha.animateTo(1f, tween(200, delayMillis = 200, easing = COUIOutEaseEasing)) }
        }
    }
    LaunchedEffect(error) {
        // initErrorAnimator / initRecoverAnimator: 350ms on the move-ease curve.
        animation.error.animateTo(if (error) 1f else 0f, tween(350, easing = COUIMoveEaseEasing))
    }
    return animation
}

/**
 * The `TYPE_ON_IMAGE` shadow of the track (`setShadowLayer(4dp, 0, 2dp,
 * coui_circular_progress_shadow_color)`): the stroke blurred with the Android sigma of a 4dp shadow
 * radius, drawn 2dp below the ring as a radial gradient.
 */
private fun DrawScope.drawRingShadow(
    color: Color,
    radius: Float,
    center: Offset,
    strokeWidthPx: Float,
    alpha: Float,
) {
    val sigma = 0.57735f * CircularShadowRadius.toPx() + 0.5f
    val inner = radius - strokeWidthPx / 2f
    val outer = radius + strokeWidthPx / 2f
    val reach = outer + 3f * sigma
    if (reach <= 0f) return
    val samples = 16
    val stops = Array(samples + 1) { i ->
        val distance = reach * i / samples
        val coverage = progressGaussianCdf((distance - inner) / sigma) - progressGaussianCdf((distance - outer) / sigma)
        distance / reach to color.copy(alpha = color.alpha * coverage.coerceIn(0f, 1f) * alpha)
    }
    val shadowCenter = Offset(center.x, center.y + CircularShadowOffsetY.toPx())
    drawCircle(
        brush = Brush.radialGradient(*stops, center = shadowCenter, radius = reach),
        radius = reach,
        center = shadowCenter,
    )
}

/** The standard normal cumulative distribution, after Abramowitz and Stegun 7.1.26. */
private fun progressGaussianCdf(x: Float): Float {
    val z = abs(x) / sqrt(2f)
    val t = 1f / (1f + 0.3275911f * z)
    val poly = t * (0.25482959f + t * (-0.28449672f + t * (1.4214138f + t * (-1.4531521f + t * 1.0614054f))))
    val erf = 1f - poly * exp(-z * z)
    return if (x >= 0f) 0.5f * (1f + erf) else 0.5f * (1f - erf)
}

/**
 * The frame time in milliseconds, updated on every frame while composed, or -1 before the first
 * frame. Compose frame times come from the platform's monotonic frame clock (the Choreographer
 * frame time on Android, the time base of `SystemClock.uptimeMillis`), so a phase derived from it
 * is shared by every indicator reading it, like COUILoadingView's `uptimeMillis % 1000`.
 */
@Composable
private fun rememberLoadingViewFrameTime(): LongState {
    val frameTime = remember { mutableLongStateOf(-1L) }
    LaunchedEffect(frameTime) {
        while (true) {
            withInfiniteAnimationFrameMillis { frameTime.longValue = it }
        }
    }
    return frameTime
}

/** COUILoadingView ONE_THOUSAND_INT: the duration of one turn of the loading view. */
private val LoadingViewTurnMillis = 1000L

/** COUICircularProgressDrawable.initSpring: SpringForce(dampingRatio = 1.0, stiffness = 50). */
private val CircularProgressSpring = spring(dampingRatio = 1f, stiffness = 50f, visibilityThreshold = 0.001f)

/** coui_circular_progress_default_padding: the inset between the box and the length, and between the length and the ring. */
private val CircularProgressPadding = 4.dp

/** COUICircularProgressBar FOLLOW_THEME_PROGRESS_BAR_TRACK_ALPHA (89 / 255). */
private val FollowThemeTrackAlpha = 89f / 255f

/** coui_circular_progress_error_diameter. */
private val CircularErrorDiameter = 19.dp

/** coui_circular_progress_error_stroke_width. */
private val CircularErrorStrokeWidth = 1.5.dp

/** coui_circular_progress_error_icon_rect_width / _height / _bias. */
private val CircularErrorBarWidth = 1.6.dp
private val CircularErrorBarHeight = 5.25.dp
private val CircularErrorBarBias = 4.25.dp

/** coui_circular_progress_error_icon_circle_radius / _bias. */
private val CircularErrorDotRadius = 1.1.dp
private val CircularErrorDotBias = 3.1.dp

/** coui_circular_progress_pause_icon_rect_width / _height / _gap / _radius. */
private val CircularPauseBarWidth = 4.dp
private val CircularPauseBarHeight = 16.dp
private val CircularPauseBarGap = 4.dp
private val CircularPauseBarRadius = 1.dp

/** coui_circular_progress_shadow_radius / _y_bias. */
private val CircularShadowRadius = 4.dp
private val CircularShadowOffsetY = 2.dp

/** The icons grow from 70% (`0.3 * fraction + 0.7`). */
private val PauseIconMinScale = 0.7f
private val ErrorIconMinScale = 0.7f

/**
 * A [InfiniteProgressIndicator] with COUI style.
 *
 * A thin wrapper around [RotatingProgressIndicator] that keeps the accent tint and the
 * `coui_loading_view_*` size tier, for indeterminate waits inside dialogs and inline content.
 *
 * @param modifier The modifier to be applied to the indicator.
 * @param color The color of the arc.
 * @param size The size (the square view box) of the indicator.
 * @param strokeWidth The width of the arc stroke.
 */
@Composable
@NonRestartableComposable
fun InfiniteProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = COUITheme.colorScheme.primary,
    size: Dp = ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorSize,
    strokeWidth: Dp = ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorStrokeWidth,
) {
    RotatingProgressIndicator(
        modifier = modifier,
        color = color,
        size = size,
        ringDiameter = size - strokeWidth,
        strokeWidth = strokeWidth,
    )
}

/**
 * A [RotatingProgressIndicator] with COUI style.
 *
 * ColorOS's indeterminate spinner: a single bare stroked arc with flat caps and no background ring,
 * whose sweep pulses while the whole arc spins. Ported from the Lottie asset
 * `coui_rotating_loading.json`, which `Theme.COUI` binds to `couiRotatingSpinnerJsonName` and
 * `COUILottieLoadingView` renders (the same animation also ships as the `fb_loading.xml`
 * AnimatedVectorDrawable). The asset is one ellipse driven by one trim path, so it is reproduced
 * here with a single [drawArc] and needs no Lottie runtime.
 *
 * @param modifier The modifier to be applied to the indicator.
 * @param paused Whether the spinner holds its current frame (`EffectiveAnimationView.pauseAnimation`,
 *   as COUILoadingPreferenceCategory does in its PAUSE state). It continues from that frame when
 *   unpaused (`resumeAnimation`).
 * @param color The color of the arc. Defaults to
 *   [ProgressIndicatorDefaults.rotatingProgressIndicatorColor], the asset's 85% stroke opacity; the
 *   small tier's asset is dimmer, [ProgressIndicatorDefaults.smallRotatingProgressIndicatorColor].
 * @param size The size (the square view box) of the indicator.
 * @param ringDiameter The diameter of the arc's stroke centerline, centered inside [size].
 * @param strokeWidth The width of the arc stroke.
 */
@Composable
fun RotatingProgressIndicator(
    modifier: Modifier = Modifier,
    paused: Boolean = false,
    color: Color = ProgressIndicatorDefaults.rotatingProgressIndicatorColor(),
    size: Dp = ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorSize,
    ringDiameter: Dp = ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorRingDiameter,
    strokeWidth: Dp = ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorStrokeWidth,
) {
    val frame = rememberRotatingSpinnerFrame(paused)

    Canvas(
        modifier = modifier
            .semantics { progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate }
            .size(size),
    ) {
        val strokeWidthPx = strokeWidth.toPx()
        val diameterPx = ringDiameter.toPx()
        val inset = (this.size.minDimension - diameterPx) / 2
        val cycle = min(frame.floatValue, RotatingSpinnerLastKeyframe) / RotatingSpinnerLastKeyframe

        // `Trim Paths 1` pins `end` at 100% and animates `start`, so the arc's trailing edge is
        // fixed while its leading edge swings; both the sweep and the start angle derive from it.
        val trimStart = rotatingSpinnerTrimStart(cycle)

        drawArc(
            color = color,
            startAngle = rotatingSpinnerStartAngle(cycle, trimStart),
            sweepAngle = 360f - trimStart,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = Size(diameterPx, diameterPx),
            // Stroke 1 declares lc = 1, i.e. a butt/flat cap (not the round cap the other COUI
            // progress indicators use), and lj = 1 (miter, irrelevant for a single open arc).
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt),
        )
    }
}

/**
 * The current frame of coui_rotating_loading.json: `fr = 60`, `ip = 0`, `op = 76`, so the 76 frames
 * play in 1266.7 ms and repeat forever (the keyframes end at frame 75, which holds for the last
 * frame). While [paused] the frame stays where it is (`pauseAnimation`); unpausing continues from it
 * (`resumeAnimation`). Like InfiniteTransition, the playback follows the [MotionDurationScale]; with
 * animations switched off the spinner rests on its last frame, as a Lottie view does.
 */
@Composable
private fun rememberRotatingSpinnerFrame(paused: Boolean): FloatState {
    val frame = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(paused) {
        if (paused) return@LaunchedEffect
        var originFrame = frame.floatValue
        var originNanos = Long.MIN_VALUE
        var scale = 1f
        while (true) {
            withInfiniteAnimationFrameNanos { frameTimeNanos ->
                val currentScale = coroutineContext[MotionDurationScale]?.scaleFactor ?: 1f
                if (originNanos == Long.MIN_VALUE || currentScale != scale) {
                    // Start, or restart the timing from the current frame when the scale changes.
                    originFrame = frame.floatValue
                    originNanos = frameTimeNanos
                    scale = currentScale
                }
                frame.floatValue = if (scale == 0f) {
                    RotatingSpinnerLastKeyframe
                } else {
                    val elapsedFrames = (frameTimeNanos - originNanos) / (RotatingSpinnerFrameNanos * scale)
                    ((originFrame + elapsedFrames) % RotatingSpinnerFrameCount).toFloat()
                }
            }
            if (scale == 0f) {
                snapshotFlow { coroutineContext[MotionDurationScale]?.scaleFactor ?: 1f }.first { it > 0f }
            }
        }
    }
    return frame
}

/** coui_rotating_loading.json `fr`: the duration of one frame at 60 frames per second. */
private val RotatingSpinnerFrameNanos = 1_000_000_000.0 / 60.0

/** coui_rotating_loading.json `op`: the frames of one cycle. */
private val RotatingSpinnerFrameCount = 76f

/** The last keyframe of coui_rotating_loading.json; the cycle's last frame holds it. */
private val RotatingSpinnerLastKeyframe = 75f

/** The normalized time at which the arc is shortest (coui_rotating_loading.json keyframe t = 37 of 75). */
private val RotatingSpinnerBreakpoint = 37f / 75f

/** Lottie's standard keyframe easing, used by every keyframe pair except the trim-start ones. */
private val RotatingSpinnerStandardEasing = CubicBezierEasing(0.167f, 0.167f, 0.833f, 0.833f)

/** The trim-start easing while the arc shortens (keyframe 0 -> 37). */
private val RotatingSpinnerTrimGrowEasing = CubicBezierEasing(0.167f, 0.167f, 0.833f, 1f)

/** The trim-start easing while the arc lengthens again (keyframe 37 -> 75). */
private val RotatingSpinnerTrimShrinkEasing = CubicBezierEasing(0.167f, 0f, 0.833f, 0.833f)

/** `Trim Paths 1` start at keyframes t = 0 and t = 75, in degrees of arc. */
private val TrimStartMin = 24f / 100f * 360f

/** `Trim Paths 1` start at keyframe t = 37, where the arc is shortest, in degrees of arc. */
private val TrimStartMax = 86f / 100f * 360f

/** `Trim Paths 1` offset at keyframes t = 0 / 37 / 75. */
private val TrimOffsetStart = -90f
private val TrimOffsetMid = -41f
private val TrimOffsetEnd = 270f

/**
 * The eased `Trim Paths 1` start value at normalized time [cycle], in degrees.
 *
 * `start` animates 24% -> 86% -> 24% while `end` stays pinned at 100%, so the visible arc runs
 * 76% -> 14% -> 76% of the circle, i.e. 273.6 -> 50.4 -> 273.6 degrees.
 */
private fun rotatingSpinnerTrimStart(cycle: Float): Float = if (cycle < RotatingSpinnerBreakpoint) {
    val fraction = RotatingSpinnerTrimGrowEasing.transform(cycle / RotatingSpinnerBreakpoint)
    lerp(TrimStartMin, TrimStartMax, fraction)
} else {
    val fraction = RotatingSpinnerTrimShrinkEasing.transform(
        (cycle - RotatingSpinnerBreakpoint) / (1f - RotatingSpinnerBreakpoint),
    )
    lerp(TrimStartMax, TrimStartMin, fraction)
}

/**
 * The start angle of [RotatingProgressIndicator]'s arc at normalized time [cycle], given the already
 * eased [trimStart].
 *
 * Three rotations compose here. The layer's own rotation runs 90 -> 450 degrees over the cycle while
 * the shape group carries a constant -90 degree transform, which together reduce to `360 * cycle`.
 * The trim path's offset independently runs -90 -> -41 -> 270 degrees, so the arc advances 720
 * degrees per cycle. Finally the arc is drawn from its leading edge, which sits [trimStart] into the
 * path, and Compose measures angles from 3 o'clock while the ellipse path starts at 12 o'clock.
 *
 * The layer's `s: [-100, -100, 100]` scale is a 180 degree point reflection about the center; on a
 * centered circular arc that is a constant phase shift of an already continuously spinning
 * animation, so it is not applied.
 */
private fun rotatingSpinnerStartAngle(cycle: Float, trimStart: Float): Float {
    val trimOffset = if (cycle < RotatingSpinnerBreakpoint) {
        val fraction = RotatingSpinnerStandardEasing.transform(cycle / RotatingSpinnerBreakpoint)
        lerp(TrimOffsetStart, TrimOffsetMid, fraction)
    } else {
        val fraction = RotatingSpinnerStandardEasing.transform(
            (cycle - RotatingSpinnerBreakpoint) / (1f - RotatingSpinnerBreakpoint),
        )
        lerp(TrimOffsetMid, TrimOffsetEnd, fraction)
    }
    return -90f + 360f * cycle + trimOffset + trimStart
}

/**
 * Draws a single animated segment for the indeterminate linear progress indicator.
 */
private fun DrawScope.drawIndeterminateSegment(
    animatedValue: Float,
    segmentIndex: Int,
    color: Color,
) {
    val position = animatedValue - segmentIndex * (0.45f + 0.55f)
    val adjustedPos = (position % 1f + 1f) % 1f
    val cornerRadius = CornerRadius(size.height / 2)

    if (adjustedPos < 1f - 0.45f) {
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * adjustedPos, 0f),
            size = Size(size.width * 0.45f, size.height),
            cornerRadius = cornerRadius,
        )
    } else {
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * adjustedPos, 0f),
            size = Size(size.width * (1f - adjustedPos), size.height),
            cornerRadius = cornerRadius,
        )
        val remainingWidth = adjustedPos + 0.45f - 1f
        if (remainingWidth > 0) {
            drawRoundRect(
                color = color,
                size = Size(size.width * remainingWidth, size.height),
                cornerRadius = cornerRadius,
            )
        }
    }
}

/**
 * Draws a circular background ring used by [CircularProgressIndicator].
 */
private fun DrawScope.drawCircularBackground(
    color: Color,
    radius: Float,
    center: Offset,
    strokeWidthPx: Float,
) {
    drawCircle(
        color = color,
        radius = radius,
        center = center,
        style = Stroke(width = strokeWidthPx),
    )
}

/**
 * Runs [block] inside an offscreen layer so the progress arc drawn with [BlendMode.Src]
 * replaces the track pixels underneath it instead of stacking on top of them.
 * COUICircularProgressDrawable does the same with saveLayerAlpha + PorterDuff.Mode.SRC,
 * which matters because both track and progress colors are translucent. [alpha] fades the whole
 * layer (`mProgressBarAlpha`).
 */
private inline fun DrawScope.withSrcCompositingLayer(alpha: Float = 1f, block: DrawScope.() -> Unit) {
    val canvas = drawContext.canvas
    canvas.saveLayer(Rect(Offset.Zero, size), Paint().apply { this.alpha = alpha })
    block()
    canvas.restore()
}

object ProgressIndicatorDefaults {
    /** The default height of [LinearProgressIndicator] (COUI coui_loading_dialog_progress_height). */
    val DefaultLinearProgressIndicatorHeight = 4.dp

    /** The stroke width of the medium [CircularProgressIndicator] tier (COUI coui_circular_progress_medium_stroke_width). */
    val MediumCircularProgressIndicatorStrokeWidth = 3.dp

    /**
     * The length of the medium [CircularProgressIndicator] tier (COUI coui_circular_progress_medium_length):
     * a 38dp box around a 22dp ring.
     */
    val MediumCircularProgressIndicatorSize = 30.dp

    /**
     * The stroke width of the large [CircularProgressIndicator] tier, the default with a progress
     * (COUI coui_circular_progress_large_stroke_width).
     */
    val LargeCircularProgressIndicatorStrokeWidth = 5.dp

    /**
     * The length of the large [CircularProgressIndicator] tier, the default with a progress
     * (COUI coui_circular_progress_large_length, `couiCircularProgressBarSize` large_size): a 48dp
     * box around a 32dp ring.
     */
    val LargeCircularProgressIndicatorSize = 40.dp

    /**
     * The stroke width of the indeterminate [CircularProgressIndicator], the default loading view
     * (COUI coui_circle_loading_medium_strokewidth).
     */
    val DefaultLoadingViewStrokeWidth = 2.67.dp

    /** The size of the indeterminate [CircularProgressIndicator] (COUI coui_loading_view_default_length). */
    val DefaultLoadingViewSize = 18.dp

    /** The stroke width of the large loading view (COUI coui_circle_loading_large_strokewidth). */
    val LargeLoadingViewStrokeWidth = 3.33.dp

    /** The size of the large loading view (COUI coui_loading_view_large_width / _height). */
    val LargeLoadingViewSize = 26.dp

    /** The default stroke width of [InfiniteProgressIndicator] (COUI coui_circle_loading_medium_strokewidth). */
    val DefaultInfiniteProgressIndicatorStrokeWidth = 2.67.dp

    /** The default size of [InfiniteProgressIndicator] (COUI coui_loading_view_default_length). */
    val DefaultInfiniteProgressIndicatorSize = 18.dp

    /** The stroke width of the large [InfiniteProgressIndicator] tier (COUI coui_circle_loading_large_strokewidth). */
    val LargeInfiniteProgressIndicatorStrokeWidth = 3.33.dp

    /** The size of the large [InfiniteProgressIndicator] tier (COUI coui_loading_view_large_width/height). */
    val LargeInfiniteProgressIndicatorSize = 26.dp

    /**
     * The default size of [RotatingProgressIndicator]
     * (COUI coui_lottie_loading_view_large_width/height, the tier COUILottieLoadingView defaults to).
     */
    val DefaultRotatingProgressIndicatorSize = 26.dp

    /**
     * The default ring diameter of [RotatingProgressIndicator].
     * coui_rotating_loading.json: a 26-unit ellipse at 300% group scale is 78 units across on an
     * 84-unit comp, so 78 / 84 * 26.dp = 24.14.dp.
     */
    val DefaultRotatingProgressIndicatorRingDiameter = 24.14.dp

    /**
     * The default stroke width of [RotatingProgressIndicator].
     * coui_rotating_loading.json: a 2-unit stroke at 300% group scale is 6 units on an 84-unit comp,
     * so 6 / 84 * 26.dp = 1.857.dp.
     */
    val DefaultRotatingProgressIndicatorStrokeWidth = 1.857.dp

    /**
     * The size of the small [RotatingProgressIndicator] tier
     * (COUI coui_lottie_loading_view_small_width/height): the `Widget.COUI.COUILottieLoadingView`
     * spinner, which plays coui_lottie_loading_small.json. Draw it with
     * [smallRotatingProgressIndicatorColor], as that asset dims its whole layer.
     */
    val SmallRotatingProgressIndicatorSize = 16.dp

    /**
     * The ring diameter of the small [RotatingProgressIndicator] tier.
     * coui_lottie_loading_small.json: a 14-unit ellipse at 300% group scale is 42 units across on a
     * 53-unit comp, so 42 / 53 * 16.dp = 12.68.dp.
     */
    val SmallRotatingProgressIndicatorRingDiameter = 12.68.dp

    /**
     * The stroke width of the small [RotatingProgressIndicator] tier.
     * coui_lottie_loading_small.json: a 2-unit stroke at 300% group scale is 6 units on a 53-unit
     * comp, so 6 / 53 * 16.dp = 1.811.dp.
     */
    val SmallRotatingProgressIndicatorStrokeWidth = 1.811.dp

    /**
     * The largest supported size of [RotatingProgressIndicator]
     * (COUI coui_loading_max_large_width/height; COUICompProgressIndicator warns above it).
     */
    val MaxRotatingProgressIndicatorSize = 40.dp

    /**
     * The default arc color of [RotatingProgressIndicator].
     *
     * coui_rotating_loading.json (light) and coui_rotating_loading_night.json (dark) stroke the arc
     * in black / white at opacity 85. The animation renderer (BaseStrokeContent.draw) replaces the
     * paint alpha with the stroke opacity after StrokeContent sets the color, so the color's own
     * alpha component is discarded and the arc renders as #D9000000 / #D9FFFFFF.
     */
    @Composable
    fun rotatingProgressIndicatorColor(): Color = COUITheme.colorScheme.labelPrimary.copy(alpha = RotatingSpinnerOpacity)

    /**
     * The arc color of the small [RotatingProgressIndicator] tier.
     *
     * coui_lottie_loading_small.json (light) and coui_lottie_loading_small_night.json (dark) are the
     * same animation as the default spinner, but their layer opacity is 30 on top of the 85 stroke
     * opacity. The animation renderer multiplies the two in integer steps (BaseLayer.draw, then
     * BaseStrokeContent.draw): (int) (0.30 * 255) = 76, then (int) (76 * 0.85) = 64, so the arc
     * renders as #40000000 / #40FFFFFF.
     */
    @Composable
    fun smallRotatingProgressIndicatorColor(): Color = COUITheme.colorScheme.labelPrimary.copy(alpha = SmallRotatingSpinnerOpacity)

    /**
     * The default [ProgressIndicatorColors] used by [LinearProgressIndicator]
     * (COUIProgressHorizontal style: progress = couiColorPrimary, track = couiColorDivider; the
     * disabled pair comes from the `state_enabled="false"` items of the same selectors).
     */
    @Composable
    fun progressIndicatorColors(
        foregroundColor: Color = COUITheme.colorScheme.primary,
        disabledForegroundColor: Color = COUITheme.colorScheme.disabledProgress,
        backgroundColor: Color = COUITheme.colorScheme.divider,
        disabledBackgroundColor: Color = COUITheme.colorScheme.disabledProgressBackground,
    ): ProgressIndicatorColors = remember(foregroundColor, disabledForegroundColor, backgroundColor, disabledBackgroundColor) {
        ProgressIndicatorColors(
            foregroundColor = foregroundColor,
            disabledForegroundColor = disabledForegroundColor,
            backgroundColor = backgroundColor,
            disabledBackgroundColor = disabledBackgroundColor,
        )
    }

    /**
     * The default [ProgressIndicatorColors] used by the indeterminate [CircularProgressIndicator]
     * (Widget.COUI.COUILoadingView style: arc = couiColorPrimary, track = couiColorSecondary,
     * i.e. #0080FF on #260080FF / #1A8CFF on #401A8CFF).
     */
    @Composable
    fun loadingViewColors(
        foregroundColor: Color = COUITheme.colorScheme.primary,
        disabledForegroundColor: Color = COUITheme.colorScheme.disable,
        backgroundColor: Color = COUITheme.colorScheme.secondary,
    ): ProgressIndicatorColors = remember(foregroundColor, disabledForegroundColor, backgroundColor) {
        ProgressIndicatorColors(
            foregroundColor = foregroundColor,
            disabledForegroundColor = disabledForegroundColor,
            backgroundColor = backgroundColor,
        )
    }

    /**
     * The default [ProgressIndicatorColors] used by [CircularProgressIndicator]
     * (Widget.COUI.COUICircularProgressBar style: progress = couiColorHintNeutral,
     * track = couiColorDivider, couiCircularErrorDrawableTint and couiCircularPauseDrawableTint =
     * couiColorHintNeutral). The [CircularProgressType.OnImage] shadow is
     * coui_circular_progress_shadow_color (#19000000); the closest theme slot is used.
     */
    @Composable
    fun circularProgressIndicatorColors(
        foregroundColor: Color = COUITheme.colorScheme.labelTertiary,
        disabledForegroundColor: Color = COUITheme.colorScheme.disable,
        backgroundColor: Color = COUITheme.colorScheme.divider,
        errorColor: Color = COUITheme.colorScheme.labelTertiary,
        pauseIconColor: Color = COUITheme.colorScheme.labelTertiary,
        shadowColor: Color = COUITheme.colorScheme.sliderThumbShadow,
    ): ProgressIndicatorColors = remember(foregroundColor, disabledForegroundColor, backgroundColor, errorColor, pauseIconColor, shadowColor) {
        ProgressIndicatorColors(
            foregroundColor = foregroundColor,
            disabledForegroundColor = disabledForegroundColor,
            backgroundColor = backgroundColor,
            errorColor = errorColor,
            pauseIconColor = pauseIconColor,
            shadowColor = shadowColor,
        )
    }
}

/** The stroke opacity of coui_rotating_loading.json (`"o": 85`). */
private val RotatingSpinnerOpacity = 0.85f

/** The rendered arc alpha of coui_lottie_loading_small.json: layer opacity 30 times stroke opacity 85. */
private val SmallRotatingSpinnerOpacity = 64f / 255f

/**
 * The colors of the progress indicators.
 *
 * @param foregroundColor The progress or arc color.
 * @param disabledForegroundColor The progress color when disabled.
 * @param backgroundColor The track color.
 * @param disabledBackgroundColor The track color when disabled.
 * @param errorColor The ring and icon color of a [CircularProgressState.Error] circular indicator.
 * @param pauseIconColor The icon color of a [CircularProgressState.Paused] circular indicator.
 * @param shadowColor The shadow of a [CircularProgressType.OnImage] circular indicator.
 */
@Immutable
data class ProgressIndicatorColors(
    private val foregroundColor: Color,
    private val disabledForegroundColor: Color,
    private val backgroundColor: Color,
    private val disabledBackgroundColor: Color = backgroundColor,
    private val errorColor: Color = foregroundColor,
    private val pauseIconColor: Color = foregroundColor,
    private val shadowColor: Color = Color.Transparent,
) {
    @Stable
    internal fun errorColor(): Color = errorColor

    @Stable
    internal fun pauseIconColor(): Color = pauseIconColor

    @Stable
    internal fun shadowColor(): Color = shadowColor

    @Stable
    internal fun foregroundColor(enabled: Boolean): Color = if (enabled) foregroundColor else disabledForegroundColor

    @Stable
    internal fun backgroundColor(enabled: Boolean = true): Color = if (enabled) backgroundColor else disabledBackgroundColor
}
