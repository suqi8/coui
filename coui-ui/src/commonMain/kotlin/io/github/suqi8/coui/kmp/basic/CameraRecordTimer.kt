// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * The video recording timer of the camera, the port of `CameraTimeView` inflated from
 * `record_time.xml`.
 *
 * The time is laid out digit by digit, each digit centered in its own fixed cell
 * ([CameraRecordTimerDefaults.DigitWidth] x [CameraRecordTimerDefaults.DigitHeight], 16dp text), so
 * the timer never changes width while the seconds tick. Minutes and seconds are always shown
 * (`mm:ss`); the hours and their colon only appear once the time reaches an hour (`hh:mm:ss`), as
 * in `CameraTimeView.f`. The colons are the camera's two-dot glyph (`ic_time_colon`) and every
 * digit carries the camera's thin dark outline (`InverseTextView.setStrokeEnable`, 0.33dp of 40%
 * black) so the white digits stay legible over a bright preview.
 *
 * By default the timer sits on the camera's red recording plate (`record_time_red_bg`: 4dp corners,
 * 6dp horizontal padding). Without the plate it can lead with the blinking red recording dot of the
 * camera's black-screen and time-lapse layouts (`blink_record_icon`), which toggles every
 * [CameraRecordTimerDefaults.BlinkIntervalMillis] like the video mode's recording tick
 * (`VideoMode`: every 6th 81ms update).
 *
 * Showing fades the timer in over 400ms after a 150ms delay and hiding fades it out in 100ms, both on
 * the COUI standard curve (0.33, 0, 0.67, 1), the default `CameraTimeView.c` / `a` animators. The
 * timer always lays its digits out left to right and always uses its fixed light-on-dark palette,
 * since it sits over the camera preview.
 *
 * @param timeMillis The recorded time in milliseconds, or the remaining time when [countDown] is
 *   true. Negative values show 00:00.
 * @param modifier The modifier to be applied to the timer.
 * @param visible Whether the timer is shown; changes fade it in or out.
 * @param countDown Whether [timeMillis] is a remaining time (the video count-down of
 *   `support_video_count_down`), in which case partial seconds round up instead of down.
 * @param showBackground Whether to draw the red recording plate behind the digits.
 * @param showRecordingDot Whether to lead with the blinking red recording dot.
 * @param fontWeight The digit weight: the camera's regular `SysSans-En` weight 400 by default, or
 *   [CameraRecordTimerDefaults.LightFontWeight] (weight 266) for the light variant.
 * @param colors The colors of the timer, see [CameraRecordTimerDefaults.cameraRecordTimerColors].
 */
@Composable
fun CameraRecordTimer(
    timeMillis: Long,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    countDown: Boolean = false,
    showBackground: Boolean = true,
    showRecordingDot: Boolean = false,
    fontWeight: FontWeight = CameraRecordTimerDefaults.RegularFontWeight,
    colors: CameraRecordTimerColors = CameraRecordTimerDefaults.cameraRecordTimerColors(),
) {
    val timerAlpha = remember { Animatable(0f) }
    LaunchedEffect(visible) {
        if (visible) {
            timerAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(SHOW_DURATION_MILLIS, delayMillis = SHOW_DELAY_MILLIS, easing = STANDARD_EASING),
            )
        } else {
            timerAlpha.animateTo(targetValue = 0f, animationSpec = tween(HIDE_DURATION_MILLIS, easing = STANDARD_EASING))
        }
    }
    val faded by remember { derivedStateOf { timerAlpha.value == 0f } }
    if (!visible && faded) return

    val time = remember(timeMillis, countDown) { CameraRecordTimeParts.of(timeMillis, countDown) }
    val density = LocalDensity.current
    val digitStyle = remember(density, fontWeight, colors.contentColor) {
        TextStyle(
            color = colors.contentColor,
            // The camera sizes its timer digits in dp: they do not follow the font scale.
            fontSize = (CameraRecordTimerDefaults.TextSize.value / density.fontScale).sp,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
        )
    }
    val outlineStyle = remember(digitStyle, density, colors.strokeColor) {
        digitStyle.copy(
            color = colors.strokeColor,
            drawStyle = Stroke(width = with(density) { CameraRecordTimerDefaults.StrokeWidth.toPx() }),
        )
    }
    val plateShape = remember { RoundedCornerShape(CameraRecordTimerDefaults.CornerRadius) }
    val plate = if (showBackground) Modifier.background(colors.backgroundColor, plateShape) else Modifier

    // `record_time.xml` pins the timer to left-to-right (`android:layoutDirection="ltr"`).
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = modifier
                .graphicsLayer { alpha = timerAlpha.value }
                .clearAndSetSemantics { contentDescription = time.toString() }
                .then(plate)
                .padding(horizontal = CameraRecordTimerDefaults.HorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showRecordingDot) {
                RecordingDot(color = colors.dotColor)
                Spacer(Modifier.width(CameraRecordTimerDefaults.RecordingDotGap))
            }
            if (time.hours > 0) {
                TimerDigit(time.hours / 10, digitStyle, outlineStyle, Modifier.padding(start = CameraRecordTimerDefaults.HourMarginStart))
                TimerDigit(time.hours % 10, digitStyle, outlineStyle)
                TimerColon(colors.contentColor)
            }
            TimerDigit(time.minutes / 10, digitStyle, outlineStyle)
            TimerDigit(time.minutes % 10, digitStyle, outlineStyle)
            TimerColon(colors.contentColor)
            TimerDigit(time.seconds / 10, digitStyle, outlineStyle)
            TimerDigit(time.seconds % 10, digitStyle, outlineStyle)
        }
    }
}

/** One digit cell (`RecordTimeTextStyle`): the dark outline drawn first, the white digit over it. */
@Composable
private fun TimerDigit(
    digit: Int,
    style: TextStyle,
    outlineStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    val text = digit.toString()
    Box(
        modifier = modifier.size(CameraRecordTimerDefaults.DigitWidth, CameraRecordTimerDefaults.DigitHeight),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(text = text, style = outlineStyle, softWrap = false)
        BasicText(text = text, style = style, softWrap = false)
    }
}

/**
 * The colon glyph (`ic_time_colon`, 21 x 66px at xxhdpi): two 2.67dp dots, 7dp apart, centered 1dp
 * below the middle of a 7 x 22dp box.
 */
@Composable
private fun TimerColon(color: Color) {
    Canvas(modifier = Modifier.size(COLON_WIDTH, COLON_HEIGHT)) {
        val radius = (COLON_DOT_SIZE / 2).toPx()
        val x = size.width / 2
        drawCircle(color = color, radius = radius, center = Offset(x, COLON_TOP_DOT_Y.toPx()))
        drawCircle(color = color, radius = radius, center = Offset(x, COLON_BOTTOM_DOT_Y.toPx()))
    }
}

/** The blinking recording dot (`blink_record_icon` with `ic_recording_indicator`). */
@Composable
private fun RecordingDot(color: Color) {
    var on by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(CameraRecordTimerDefaults.BlinkIntervalMillis.toLong())
            on = !on
        }
    }
    Box(
        modifier = Modifier
            .size(CameraRecordTimerDefaults.RecordingDotSize)
            // `setVisibility(INVISIBLE)`: the dot keeps its space while it is off.
            .graphicsLayer { alpha = if (on) 1f else 0f }
            .background(color, CircleShape),
    )
}

/** A time split the way `CameraTimeView.f` splits it. */
@Immutable
private data class CameraRecordTimeParts(val hours: Int, val minutes: Int, val seconds: Int) {
    override fun toString(): String {
        val mmss = "${pad(minutes)}:${pad(seconds)}"
        return if (hours > 0) "${pad(hours)}:$mmss" else mmss
    }

    companion object {
        fun of(timeMillis: Long, countDown: Boolean): CameraRecordTimeParts {
            val millis = timeMillis.coerceAtLeast(0L)
            // Count-downs round the remaining time up to whole seconds before splitting it, so a
            // minute boundary never reads as ":60".
            val totalSeconds = if (countDown) (millis + 999L) / 1000L else millis / 1000L
            return CameraRecordTimeParts(
                hours = (totalSeconds / 3600L).toInt(),
                minutes = ((totalSeconds % 3600L) / 60L).toInt(),
                seconds = (totalSeconds % 60L).toInt(),
            )
        }

        private fun pad(value: Int): String = if (value < 10) "0$value" else value.toString()
    }
}

/** The fade-in of `CameraTimeView.c`: 400ms ... */
private const val SHOW_DURATION_MILLIS = 400

/** ... after a 150ms delay. */
private const val SHOW_DELAY_MILLIS = 150

/** The fade-out of `CameraTimeView.a`. */
private const val HIDE_DURATION_MILLIS = 100

/** `c9.a.a`, the COUI standard curve (`com.coui.appcompat.animation.b`). */
private val STANDARD_EASING = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

private val COLON_WIDTH = 7.dp
private val COLON_HEIGHT = 22.dp
private val COLON_DOT_SIZE = 2.67.dp
private val COLON_TOP_DOT_Y = 8.5.dp
private val COLON_BOTTOM_DOT_Y = 15.5.dp

/**
 * Default values of [CameraRecordTimer], from the camera's `record_time_*` dimensions and
 * `RecordTimeTextStyle`.
 */
object CameraRecordTimerDefaults {
    /** The digit cell width (`record_time_number_text_width`). */
    val DigitWidth: Dp = 11.dp

    /** The digit cell height (`record_time_number_text_height`). */
    val DigitHeight: Dp = 24.dp

    /** The digit size (`record_time_number_text_size`), in dp like the camera. */
    val TextSize: Dp = 16.dp

    /** The plate corner radius (`record_time_bg_corner_radius`). */
    val CornerRadius: Dp = 4.dp

    /** The plate padding at both sides (`record_time_bg_padding_h`). */
    val HorizontalPadding: Dp = 6.dp

    /** The gap before the hours (`record_time_margin_start` on `video_hour_ten`). */
    val HourMarginStart: Dp = 4.dp

    /** The digit outline width (`text_view_stroke_width`). */
    val StrokeWidth: Dp = 0.333.dp

    /** The recording dot size (`video_recording_time_blink_icon_width`). */
    val RecordingDotSize: Dp = 8.dp

    /** The gap after the recording dot (`record_icon_margin_end`). */
    val RecordingDotGap: Dp = 4.dp

    /** The recording dot toggle interval: every 6th 81ms recording tick (`VideoMode.ul`). */
    val BlinkIntervalMillis: Int = 486

    /** The regular digit weight (`TypeFaceUtil` `SysSans-En-Regular` at `'wght' 400`). */
    val RegularFontWeight: FontWeight = FontWeight.Normal

    /** The light digit weight (`TypeFaceUtil` `SysSans-En-Regular` at `'wght' 266`). */
    val LightFontWeight: FontWeight = FontWeight(266)

    /**
     * The fixed camera palette: the plate `record_time_bg_red` #EB3B2F, white digits and colons
     * (`coui_color_white`), the digit outline `text_view_stroke_color` (40% black) and the red of
     * `ic_recording_indicator` (#E32E27).
     */
    @Composable
    fun cameraRecordTimerColors(
        backgroundColor: Color = Color(0xFFEB3B2F),
        contentColor: Color = Color.White,
        strokeColor: Color = Color(0x66000000),
        dotColor: Color = Color(0xFFE32E27),
    ): CameraRecordTimerColors = remember(backgroundColor, contentColor, strokeColor, dotColor) {
        CameraRecordTimerColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            strokeColor = strokeColor,
            dotColor = dotColor,
        )
    }
}

/**
 * The colors of a [CameraRecordTimer].
 *
 * @param backgroundColor The recording plate.
 * @param contentColor The digits and colons.
 * @param strokeColor The outline around the digits.
 * @param dotColor The blinking recording dot.
 */
@Immutable
data class CameraRecordTimerColors(
    val backgroundColor: Color,
    val contentColor: Color,
    val strokeColor: Color,
    val dotColor: Color,
)
