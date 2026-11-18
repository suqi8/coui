// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos

/**
 * The self-timer countdown of the camera, the port of the number `TimerSnapShotManager` puts over
 * the preview while the timer shutter counts down.
 *
 * The number is the remaining time rounded up to whole seconds. It is a 100dp white number centered
 * in a 175dp box, or, when [compact], a 26dp number in a 46dp disc of 50% black (the small indicator
 * the camera moves to the top of the preview). Every new number pulses like the camera's: it fades in
 * over 200ms, holds, and from 800ms after it appeared fades out over 200ms, so each second blinks
 * once (`TimerSnapShotManager$b.d` driven by `ValueAnimator`'s default accelerate-decelerate curve).
 * Above 80% opacity the number grows a faint 2px black glow, up to 20% black, as the camera's
 * `setShadowLayer` does. Switching [compact] scales the number from its old size to the new one in
 * 450ms on (0.3, 0, 0.1, 1). Nothing is drawn once the remaining time reaches zero.
 *
 * @param remainingMillis The time left before the shot, in milliseconds.
 * @param modifier The modifier to be applied to the countdown.
 * @param compact Whether to show the small 46dp disc instead of the large number.
 * @param colors The colors of the countdown, see [CameraCountdownDefaults.cameraCountdownColors].
 */
@Composable
fun CameraCountdown(
    remainingMillis: Long,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    colors: CameraCountdownColors = CameraCountdownDefaults.cameraCountdownColors(),
) {
    val seconds = ((remainingMillis.coerceAtLeast(0L) + 999L) / 1000L).toInt()
    val pulse = remember { Animatable(0f) }
    LaunchedEffect(seconds) {
        if (seconds <= 0) return@LaunchedEffect
        pulse.snapTo(0f)
        pulse.animateTo(1f, tween(TIMER_FADE_MILLIS, easing = ACCELERATE_DECELERATE_EASING))
        delay((TIMER_FADE_OUT_START_MILLIS - TIMER_FADE_MILLIS).toLong())
        pulse.animateTo(0f, tween(TIMER_FADE_MILLIS, easing = ACCELERATE_DECELERATE_EASING))
    }
    val scale = remember { Animatable(1f) }
    val sizeRatio = CameraCountdownDefaults.LargeSize / CameraCountdownDefaults.CompactSize
    var lastCompact by remember { mutableStateOf(compact) }
    LaunchedEffect(compact) {
        if (compact == lastCompact) return@LaunchedEffect
        lastCompact = compact
        // `TimerSnapShotManager$b.c`: the view takes its new size at once and scales from the old one.
        scale.snapTo(if (compact) sizeRatio else 1f / sizeRatio)
        scale.animateTo(1f, tween(TIMER_RESIZE_MILLIS, easing = MOVE_EASE_EASING))
    }
    if (seconds <= 0) return

    val density = LocalDensity.current
    val boxSize = if (compact) CameraCountdownDefaults.CompactSize else CameraCountdownDefaults.LargeSize
    val textSize = if (compact) CameraCountdownDefaults.CompactTextSize else CameraCountdownDefaults.LargeTextSize
    val fraction = pulse.value
    val glow = ((fraction - TIMER_GLOW_START) / (1f - TIMER_GLOW_START)).coerceIn(0f, 1f) * TIMER_GLOW_MAX_ALPHA
    val style = TextStyle(
        color = colors.contentColor.copy(alpha = colors.contentColor.alpha * fraction),
        fontSize = cameraFixedTextSize(textSize, density),
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
        shadow = if (glow > 0f) {
            Shadow(color = colors.shadowColor.copy(alpha = glow), offset = Offset.Zero, blurRadius = TIMER_GLOW_RADIUS_PX)
        } else {
            null
        },
    )
    Box(
        modifier = modifier
            .size(boxSize)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .clearAndSetSemantics { contentDescription = seconds.toString() }
            .then(if (compact) Modifier.background(colors.compactContainerColor, CircleShape) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(text = seconds.toString(), style = style, softWrap = false, maxLines = 1)
    }
}

/**
 * The long-exposure countdown of the camera (night, long exposure and similar modes), the port of
 * `NightTimeView` inflated from `night_mode_count_down_time_ui.xml` and fed by
 * `NightCountDownManager`.
 *
 * The remaining time reads in tenths of a second with a unit suffix, for example "12.3s", in 18dp
 * white digits of weight 750 with a soft 30% black shadow. The hundreds and tens sit in fixed 11dp
 * cells so the number does not shift while it counts; the hundreds only appear from 100s and the tens
 * from 10s. The row is centered in a 94 x 22dp box. Showing fades it in over 250ms after a 150ms
 * delay and hiding fades it out over 250ms, on the COUI standard curve (0.33, 0, 0.67, 1).
 *
 * @param remainingMillis The exposure time left, in milliseconds.
 * @param modifier The modifier to be applied to the countdown.
 * @param visible Whether the countdown is shown; changes fade it in or out.
 * @param suffix The unit after the number (`camera_night_count_down_time_ui_suffix`).
 * @param colors The colors of the countdown, see
 *   [CameraCountdownDefaults.cameraExposureCountdownColors].
 */
@Composable
fun CameraExposureCountdown(
    remainingMillis: Long,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    suffix: String = CameraCountdownDefaults.ExposureSuffix,
    colors: CameraExposureCountdownColors = CameraCountdownDefaults.cameraExposureCountdownColors(),
) {
    val countdownAlpha = remember { Animatable(0f) }
    LaunchedEffect(visible) {
        if (visible) {
            countdownAlpha.animateTo(1f, tween(EXPOSURE_FADE_MILLIS, delayMillis = EXPOSURE_SHOW_DELAY_MILLIS, easing = STANDARD_EASING))
        } else {
            countdownAlpha.animateTo(0f, tween(EXPOSURE_FADE_MILLIS, easing = STANDARD_EASING))
        }
    }
    val faded by remember { derivedStateOf { countdownAlpha.value == 0f } }
    if (!visible && faded) return

    // `NightCountDownManager.h`: hundreds, tens, ones and tenths of the remaining time.
    val millis = remainingMillis.coerceAtLeast(0L)
    val hundreds = millis / 100_000L
    val tens = (millis % 100_000L) / 10_000L
    val ones = (millis % 10_000L) / 1_000L
    val tenths = (millis % 1_000L) / 100L
    val density = LocalDensity.current
    val style = remember(density, colors) {
        TextStyle(
            color = colors.contentColor,
            fontSize = cameraFixedTextSize(CameraCountdownDefaults.ExposureTextSize, density),
            fontWeight = CameraCountdownDefaults.ExposureFontWeight,
            textAlign = TextAlign.Center,
            shadow = Shadow(
                color = colors.shadowColor,
                offset = Offset.Zero,
                blurRadius = with(density) { CameraCountdownDefaults.ExposureShadowRadius.toPx() },
            ),
        )
    }
    val description = buildString {
        if (hundreds > 0) append(hundreds)
        if (hundreds > 0 || tens > 0) append(tens)
        append(ones).append('.').append(tenths).append(suffix)
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(
            modifier = modifier
                .size(CameraCountdownDefaults.ExposureWidth, CameraCountdownDefaults.ExposureHeight)
                .graphicsLayer { alpha = countdownAlpha.value }
                .clearAndSetSemantics { contentDescription = description },
            contentAlignment = Alignment.BottomCenter,
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                if (hundreds > 0) {
                    ExposureCell(hundreds.toString(), style, Modifier.width(CameraCountdownDefaults.ExposureDigitWidth))
                }
                if (hundreds > 0 || tens > 0) {
                    ExposureCell(tens.toString(), style, Modifier.width(CameraCountdownDefaults.ExposureDigitWidth))
                }
                ExposureCell(ones.toString(), style)
                ExposureCell(".", style, Modifier.width(CameraCountdownDefaults.ExposurePointWidth))
                ExposureCell(tenths.toString(), style)
                ExposureCell(
                    text = suffix,
                    style = style,
                    modifier = Modifier.padding(horizontal = CameraCountdownDefaults.ExposureSuffixMargin),
                )
            }
        }
    }
}

@Composable
private fun ExposureCell(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.height(CameraCountdownDefaults.ExposureHeight), contentAlignment = Alignment.Center) {
        BasicText(text = text, style = style, softWrap = false, maxLines = 1)
    }
}

/**
 * The camera's progress ring, in its two forms.
 *
 * By default it is `RoundProgressBar`: a thin 20% white track with an accent arc growing clockwise
 * from 12 o'clock to [progress], squared off at its ends, gliding to each new progress in 320ms on
 * (0.3, 0, 0.1, 1). With [draining] it is `ProgressRingView`, the ring of the camera's
 * recording-stop countdown: a 2dp 55% white track and an 85% white arc for the part still left,
 * which shrinks clockwise from 12 o'clock as [progress] runs from 0 to 1 (the camera drives it over
 * [CameraCountdownDefaults.RecordingStopCountdownMillis] after a 500ms delay, on
 * [CameraCountdownDefaults.DrainingRingEasing]). The ring is inset by half its stroke so it fits
 * the bounds; [content], typically a number, is centered inside.
 *
 * @param progress The progress in 0..1; for [draining], the elapsed fraction of the countdown.
 * @param modifier The modifier to be applied to the ring. Give it a size.
 * @param draining Whether to draw the remaining part (`ProgressRingView`) instead of the elapsed
 *   part (`RoundProgressBar`).
 * @param animated Whether progress changes glide (`RoundProgressBar.a`); off by default for
 *   [draining] rings, whose caller animates the progress itself.
 * @param strokeWidth The width of the track and the arc.
 * @param colors The colors of the ring, see [CameraCountdownDefaults.ringColors] and
 *   [CameraCountdownDefaults.drainingRingColors].
 * @param content The content centered in the ring.
 */
@Composable
fun CameraCountdownRing(
    progress: Float,
    modifier: Modifier = Modifier,
    draining: Boolean = false,
    animated: Boolean = !draining,
    strokeWidth: Dp = if (draining) CameraCountdownDefaults.DrainingRingStrokeWidth else CameraCountdownDefaults.RingStrokeWidth,
    colors: CameraCountdownRingColors = if (draining) {
        CameraCountdownDefaults.drainingRingColors()
    } else {
        CameraCountdownDefaults.ringColors()
    },
    content: @Composable BoxScope.() -> Unit = {},
) {
    val target = progress.coerceIn(0f, 1f)
    val shown by animateFloatAsState(
        targetValue = target,
        animationSpec = if (animated) tween(RING_ANIMATION_MILLIS, easing = MOVE_EASE_EASING) else snap(),
    )
    Box(
        modifier = modifier.semantics { progressBarRangeInfo = ProgressBarRangeInfo(target, 0f..1f) },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = strokeWidth.toPx()
            val diameter = size.minDimension - stroke
            if (diameter <= 0f) return@Canvas
            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            val arcSize = Size(diameter, diameter)
            val style = Stroke(width = stroke, cap = StrokeCap.Butt)
            drawCircle(
                color = colors.trackColor,
                radius = diameter / 2f,
                center = Offset(topLeft.x + diameter / 2f, topLeft.y + diameter / 2f),
                style = Stroke(width = stroke),
            )
            if (draining) {
                // `ProgressRingView.onDraw`: the arc from the elapsed angle round to 12 o'clock.
                val sweep = FULL_TURN * (1f - shown)
                if (sweep > 0f) {
                    drawArc(colors.progressColor, START_ANGLE + FULL_TURN * shown, sweep, false, topLeft, arcSize, style = style)
                }
            } else if (shown > 0f) {
                drawArc(colors.progressColor, START_ANGLE, FULL_TURN * shown, false, topLeft, arcSize, style = style)
            }
        }
        content()
    }
}

/**
 * The camera's fixed text sizing: its countdown sizes are in dp and do not follow the font scale.
 */
private fun cameraFixedTextSize(size: Dp, density: Density): TextUnit = (size.value / density.fontScale).sp

/** The fade of each self-timer number (`TimerSnapShotManager$b.d`). */
private const val TIMER_FADE_MILLIS = 200

/** When the self-timer number starts fading out, from its appearance (`ThreadManager.n(..., 800)`). */
private const val TIMER_FADE_OUT_START_MILLIS = 800

/** The self-timer resize between the large and compact forms (`TimerSnapShotManager$b.c`). */
private const val TIMER_RESIZE_MILLIS = 450

/** The opacity from which the self-timer number glows (`ra.s0`: `if (0.8 <= value)`). */
private const val TIMER_GLOW_START = 0.8f

/** The glow opacity at full opacity: `(value - 0.8) * 5 * 51` of 255. */
private const val TIMER_GLOW_MAX_ALPHA = 0.2f

/** The glow radius, in raw pixels like the camera's `setShadowLayer(2.0f, ...)`. */
private const val TIMER_GLOW_RADIUS_PX = 2f

/** The long-exposure countdown fades (`NightTimeView.a`). */
private const val EXPOSURE_FADE_MILLIS = 250

/** The long-exposure countdown show delay (`NightTimeView.a`). */
private const val EXPOSURE_SHOW_DELAY_MILLIS = 150

/** `RoundProgressBar`'s `animationDuration` default. */
private const val RING_ANIMATION_MILLIS = 320

private const val START_ANGLE = -90f
private const val FULL_TURN = 360f

/** `c9.a.a`, the COUI standard curve (`com.coui.appcompat.animation.b`). */
private val STANDARD_EASING = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** `com.coui.appcompat.animation.e`, the COUI move-ease curve. */
private val MOVE_EASE_EASING = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** `AccelerateDecelerateInterpolator`, the default of an Android `ValueAnimator`. */
private val ACCELERATE_DECELERATE_EASING = Easing { fraction -> (cos((fraction + 1f) * PI) / 2.0 + 0.5).toFloat() }

/**
 * Default values of [CameraCountdown], [CameraExposureCountdown] and [CameraCountdownRing], from the
 * camera's `time_snapshot_*`, `night_countdown_time_*`, `progress_bar_*` and
 * `recording_stop_count_down_ring_*` dimensions.
 */
object CameraCountdownDefaults {
    /** The large self-timer box (`time_snapshot_indicator_textview_size`). */
    val LargeSize: Dp = 175.dp

    /** The large self-timer number size (`time_snapshot_indicator_text_size_big`). */
    val LargeTextSize: Dp = 100.dp

    /** The compact self-timer disc (`time_snapshot_inidcator_tv_size_small`, `timer_text_background`). */
    val CompactSize: Dp = 46.dp

    /** The compact self-timer number size (`time_snapshot_indicator_text_size_small`). */
    val CompactTextSize: Dp = 26.dp

    /** The long-exposure countdown box width (`night_countdown_time_layout_width`). */
    val ExposureWidth: Dp = 94.dp

    /** The long-exposure countdown box height (`night_countdown_time_layout_height`). */
    val ExposureHeight: Dp = 22.dp

    /** The hundreds and tens cell width (`night_countdown_time_text_width`). */
    val ExposureDigitWidth: Dp = 11.dp

    /** The decimal point cell width (`night_countdown_time_layout_point_width`). */
    val ExposurePointWidth: Dp = 4.dp

    /** The margins around the unit (`night_countdown_time_suffix_margin_start`). */
    val ExposureSuffixMargin: Dp = 3.dp

    /** The long-exposure digit size (`night_countdown_time_text_size`). */
    val ExposureTextSize: Dp = 18.dp

    /** The long-exposure digit weight (`NightTimeView.getFontTypeface`: `sys-sans-en` at `'wght' 750`). */
    val ExposureFontWeight: FontWeight = FontWeight(750)

    /** The long-exposure shadow radius (`night_countdown_time_text_shadow_radius`, 4px at xxhdpi). */
    val ExposureShadowRadius: Dp = 1.33.dp

    /** The long-exposure unit (`camera_night_count_down_time_ui_suffix`). */
    val ExposureSuffix: String = "s"

    /** The `RoundProgressBar` stroke (`progress_bar_stroke_width`). */
    val RingStrokeWidth: Dp = 1.6.dp

    /** The `RoundProgressBar` size in the beauty menu (`face_beauty_item_width`). */
    val RingSize: Dp = 46.dp

    /** The recording-stop ring stroke (`recording_stop_count_down_ring_stroke_width`). */
    val DrainingRingStrokeWidth: Dp = 2.dp

    /** The recording-stop ring size (`recording_stop_count_down_ring_width`). */
    val DrainingRingSize: Dp = 270.dp

    /** The recording-stop ring stroke on small screens (`tiny_recording_stop_count_down_ring_stroke_width`). */
    val TinyDrainingRingStrokeWidth: Dp = 1.5.dp

    /** The recording-stop ring size on small screens (`tiny_recording_stop_count_down_ring_width`). */
    val TinyDrainingRingSize: Dp = 156.dp

    /** The recording-stop countdown length (`ae.c.e`, 10000ms). */
    val RecordingStopCountdownMillis: Int = 10_000

    /** The delay before the recording-stop ring starts draining. */
    val RecordingStopCountdownDelayMillis: Int = 500

    /** The curve of the recording-stop ring: the default accelerate-decelerate of `ValueAnimator`. */
    val DrainingRingEasing: Easing = ACCELERATE_DECELERATE_EASING

    /**
     * The fixed camera palette of the self-timer: a white number with a black glow, and the compact
     * disc `color_black_with_50_percent_transparency` (#7F000000).
     */
    @Composable
    fun cameraCountdownColors(
        contentColor: Color = Color.White,
        compactContainerColor: Color = Color(0x7F000000),
        shadowColor: Color = Color.Black,
    ): CameraCountdownColors = remember(contentColor, compactContainerColor, shadowColor) {
        CameraCountdownColors(
            contentColor = contentColor,
            compactContainerColor = compactContainerColor,
            shadowColor = shadowColor,
        )
    }

    /**
     * The fixed camera palette of the long-exposure countdown: white digits (`white_inverse_selector`)
     * with a `color_black_with_30_percent_transparency` shadow (`NightCountDownTimeTextStyle`).
     */
    @Composable
    fun cameraExposureCountdownColors(
        contentColor: Color = Color.White,
        shadowColor: Color = Color(0x4D000000),
    ): CameraExposureCountdownColors = remember(contentColor, shadowColor) {
        CameraExposureCountdownColors(contentColor = contentColor, shadowColor = shadowColor)
    }

    /**
     * The `RoundProgressBar` palette: a `color_white_with_20_percent_transparency` track and the
     * camera accent arc (`couiColorPrimary`, `color_primary_light_hasselblad` #FB822A).
     */
    @Composable
    fun ringColors(
        trackColor: Color = Color(0x33FFFFFF),
        progressColor: Color = Color(0xFFFB822A),
    ): CameraCountdownRingColors = remember(trackColor, progressColor) {
        CameraCountdownRingColors(trackColor = trackColor, progressColor = progressColor)
    }

    /**
     * The `ProgressRingView` palette of the recording-stop countdown: a
     * `color_white_with_55_percent_transparency` track and a `color_white_with_85_percent_transparency`
     * arc.
     */
    @Composable
    fun drainingRingColors(
        trackColor: Color = Color(0x8CFFFFFF),
        progressColor: Color = Color(0xD9FFFFFF),
    ): CameraCountdownRingColors = remember(trackColor, progressColor) {
        CameraCountdownRingColors(trackColor = trackColor, progressColor = progressColor)
    }
}

/**
 * The colors of a [CameraCountdown].
 *
 * @param contentColor The number.
 * @param compactContainerColor The disc behind the compact number.
 * @param shadowColor The glow around the number; its alpha follows the number's opacity.
 */
@Immutable
data class CameraCountdownColors(
    val contentColor: Color,
    val compactContainerColor: Color,
    val shadowColor: Color,
)

/**
 * The colors of a [CameraExposureCountdown].
 *
 * @param contentColor The digits, the point and the unit.
 * @param shadowColor The shadow behind them.
 */
@Immutable
data class CameraExposureCountdownColors(
    val contentColor: Color,
    val shadowColor: Color,
)

/**
 * The colors of a [CameraCountdownRing].
 *
 * @param trackColor The full ring behind the arc.
 * @param progressColor The arc.
 */
@Immutable
data class CameraCountdownRingColors(
    val trackColor: Color,
    val progressColor: Color,
)
