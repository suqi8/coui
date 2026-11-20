// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** The result a [CompleteState] reports, selecting its animation (COUICompleteStateView.completeType). */
enum class CompleteStateType {
    /** No animation; the 100dp slot stays empty (COMPLETE_TYPE_DEFAULT, completeType 0, COUI's default). */
    None,

    /** A check mark drawn inside a green ring (complete_page_success_anim.json, completeType 1). */
    Success,

    /** An exclamation mark that shakes in inside a red ring (complete_page_fail_anim.json, completeType 2). */
    Failed,

    /** A clock whose hands sweep around a ring (complete_page_wait_anim.json, completeType 3). */
    Waiting,
}

/**
 * A [CompleteState] component with COUI style, mirroring ColorOS's COUICompleteStateView
 * (coui_component_complete_state.xml): a 100dp result animation centered above a title and an
 * optional subtitle, used on "operation complete" pages.
 *
 * The three animations are redrawn from the Lottie files COUI ships (complete_page_success_anim,
 * complete_page_fail_anim and complete_page_wait_anim, 300x300 compositions at 60fps), keeping
 * their key frames, easings and geometry. The animation plays once when the component enters the
 * composition and again whenever [type] or [replayKey] changes, like COUICompleteStateView with
 * anim_autoPlay on its EffectiveAnimationView.
 *
 * @param title The title below the animation (COUI couiTextHeadlineM, couiColorLabelPrimary).
 * @param modifier The modifier to be applied to the [CompleteState].
 * @param subtitle The optional subtitle below the title (COUI couiTextBodyXS, couiColorLabelSecondary).
 * @param type The [CompleteStateType] selecting the animation. Defaults to
 *   [CompleteStateType.None], like COUICompleteStateView's completeType 0.
 * @param animate Whether to play the animation. When false the last frame is shown statically.
 * @param replayKey Changing this value replays the animation.
 * @param animationSize The size of the animation (COUI complete_anim 100dp).
 * @param colors The [CompleteStateColors] of the [CompleteState].
 * @param titleStyle The [TextStyle] of the title.
 * @param subtitleStyle The [TextStyle] of the subtitle.
 */
@Composable
fun CompleteState(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    type: CompleteStateType = CompleteStateType.None,
    animate: Boolean = true,
    replayKey: Any? = null,
    animationSize: Dp = CompleteStateDefaults.AnimationSize,
    colors: CompleteStateColors = CompleteStateDefaults.completeStateColors(),
    titleStyle: TextStyle = COUITheme.textStyles.headlineM,
    subtitleStyle: TextStyle = COUITheme.textStyles.bodyXS,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CompleteStateAnimation(
            type = type,
            animate = animate,
            replayKey = replayKey,
            colors = colors,
            modifier = Modifier
                .padding(horizontal = CompleteStateDefaults.TextPaddingHorizontal)
                .size(animationSize),
        )
        if (title.isNotEmpty()) {
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = CompleteStateDefaults.TitleMarginTop)
                    .padding(horizontal = CompleteStateDefaults.TextPaddingHorizontal),
                color = colors.titleColor,
                style = titleStyle,
                textAlign = TextAlign.Center,
            )
        }
        if (!subtitle.isNullOrEmpty()) {
            Text(
                text = subtitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = CompleteStateDefaults.SubtitleMarginTop)
                    .padding(horizontal = CompleteStateDefaults.TextPaddingHorizontal),
                color = colors.subtitleColor,
                style = subtitleStyle,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** The result animation of a [CompleteState], drawn in the 300x300 Lottie composition space. */
@Composable
private fun CompleteStateAnimation(
    type: CompleteStateType,
    animate: Boolean,
    replayKey: Any?,
    colors: CompleteStateColors,
    modifier: Modifier = Modifier,
) {
    val lastFrame = when (type) {
        CompleteStateType.None -> 0f
        CompleteStateType.Success -> SUCCESS_LAST_FRAME
        CompleteStateType.Failed -> FAIL_LAST_FRAME
        CompleteStateType.Waiting -> WAIT_LAST_FRAME
    }
    val frame = remember(type, replayKey) { Animatable(if (animate) 0f else lastFrame) }
    LaunchedEffect(frame, animate) {
        if (animate) {
            frame.snapTo(0f)
            frame.animateTo(
                targetValue = lastFrame,
                animationSpec = tween(durationMillis = (lastFrame * 1000f / LOTTIE_FRAME_RATE).toInt(), easing = LinearEasing),
            )
        } else {
            frame.snapTo(lastFrame)
        }
    }
    Spacer(
        modifier = modifier.drawWithCache {
            // The check mark measure and its trimmed segment are reused by every frame.
            val checkMeasure = PathMeasure().apply { setPath(SuccessCheckPath, false) }
            val checkSegment = Path()
            onDrawBehind {
                val f = frame.value
                scale(size.width / COMPOSITION_SIZE, size.height / COMPOSITION_SIZE, pivot = Offset.Zero) {
                    when (type) {
                        CompleteStateType.None -> Unit
                        CompleteStateType.Success -> drawSuccess(f, colors.successColor, checkMeasure, checkSegment)
                        CompleteStateType.Failed -> drawFailed(f, colors.failedColor)
                        CompleteStateType.Waiting -> drawWaiting(f, colors.waitingColor)
                    }
                }
            }
        },
    )
}

/** complete_page_success_anim.json: ring fade-in (frames 0..25) and a trimmed check mark sweep (9..39). */
private fun DrawScope.drawSuccess(frame: Float, color: Color, checkMeasure: PathMeasure, checkSegment: Path) {
    val ringAlpha = keyframe(frame, 0f, 25f, 0f, 1f, LottieEaseOut)
    drawCircle(
        color = color.copy(alpha = color.alpha * ringAlpha),
        radius = RING_RADIUS,
        center = CompositionCenter,
        style = RoundStroke,
    )
    // Trim path: start 100 -> 20 over frames 9..39, end 100 -> 80 over frames 9..27.96.
    val trimStart = keyframe(frame, 9f, 39f, 1f, 0.2f, LottieEaseOut)
    val trimEnd = keyframe(frame, 9f, 27.958984f, 1f, 0.8f, LottieEaseOut)
    val from = minOf(trimStart, trimEnd)
    val to = maxOf(trimStart, trimEnd)
    if (to - from <= 0f) return
    checkSegment.reset()
    checkMeasure.getSegment(from * checkMeasure.length, to * checkMeasure.length, checkSegment, true)
    drawPath(path = checkSegment, color = color, style = CheckStroke)
}

/** complete_page_fail_anim.json: ring and exclamation mark fade in (0..25) while the mark shakes (-3..27). */
private fun DrawScope.drawFailed(frame: Float, color: Color) {
    val alpha = keyframe(frame, 0f, 25f, 0f, 1f, LottieEaseOut)
    drawCircle(
        color = color.copy(alpha = color.alpha * alpha),
        radius = RING_RADIUS,
        center = CompositionCenter,
        style = ButtStroke,
    )
    val shake = keyframes(frame, FailShakeFrames, FailShakeValues, LottieStandardEasing)
    val x = CompositionCenter.x + shake
    drawLine(
        color = color.copy(alpha = color.alpha * alpha),
        start = Offset(x, CompositionCenter.y - 55f),
        end = Offset(x, CompositionCenter.y + 11f),
        strokeWidth = STROKE_WIDTH,
        cap = StrokeCap.Round,
    )
    val dotScale = keyframe(frame, 0f, 7f, 0f, 1f, LottieEaseOut)
    if (dotScale > 0f) {
        drawCircle(
            color = color.copy(alpha = color.alpha * alpha),
            radius = 9f * dotScale,
            center = Offset(x, CompositionCenter.y + 39f),
        )
    }
}

/**
 * complete_page_wait_anim.json: a 40% track ring and a ring that unwinds (12..92) and winds back
 * (92..179), an hour hand growing out of the pivot (0..12) and a minute hand that appears whole at
 * frame 2 and turns twice around the pivot (12..179).
 *
 * The minute hand layer carries an animated mask, but in mode `"n"` (None), which the animation
 * renderer skips (BaseLayer.applyMasks draws the layer unmasked when every mask is None, and
 * intersectBoundsWithMask ignores it), so the hand is never clipped or revealed.
 */
private fun DrawScope.drawWaiting(frame: Float, color: Color) {
    val ringStroke = RoundStroke
    val ringTopLeft = Offset(CompositionCenter.x - RING_RADIUS, CompositionCenter.y - RING_RADIUS)
    val ringSize = Size(RING_RADIUS * 2f, RING_RADIUS * 2f)
    val fadeIn = keyframe(frame, 0f, 12f, 0f, 1f, LottieStandardEasing)
    drawCircle(
        color = color.copy(alpha = color.alpha * 0.4f * fadeIn),
        radius = RING_RADIUS,
        center = CompositionCenter,
        style = ringStroke,
    )
    if (frame < 92f) {
        val trimStart = keyframe(frame, 12f, 92f, 0f, 1f, LottieLinearishEasing)
        if (trimStart < 1f) {
            drawArc(
                color = color.copy(alpha = color.alpha * fadeIn),
                startAngle = -90f + 360f * trimStart,
                sweepAngle = 360f * (1f - trimStart),
                useCenter = false,
                topLeft = ringTopLeft,
                size = ringSize,
                style = ringStroke,
            )
        }
    } else {
        val trimEnd = keyframe(frame, 92f, 179f, 0f, 1f, LottieLinearishEasing)
        if (trimEnd > 0f) {
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * trimEnd,
                useCenter = false,
                topLeft = ringTopLeft,
                size = ringSize,
                style = ringStroke,
            )
        }
    }
    // Hour hand: (204, 156) -> (150, 156) trimmed from the tip, start 100 -> 0 over frames 0..12.
    val hourTrim = keyframe(frame, 0f, 12f, 1f, 0f, LottieRevealEasing)
    if (hourTrim < 1f) {
        val tipX = HandPivot.x + 54f
        drawLine(
            color = color,
            start = Offset(tipX + (HandPivot.x - tipX) * hourTrim, HandPivot.y - 0.031f),
            end = Offset(HandPivot.x, HandPivot.y - 0.031f),
            strokeWidth = STROKE_WIDTH,
            cap = StrokeCap.Round,
        )
    }
    // Minute hand (layer in point 2): a 15x76.5 capsule, 69.3 above the pivot and 7.2 below it,
    // rotating about the pivot with its null parent.
    if (frame >= 2f) {
        val rotation = when {
            frame <= 12f -> 0f
            frame <= 92f -> keyframe(frame, 12f, 92f, 0f, 360f, LottieLinearishEasing)
            else -> keyframe(frame, 92f, 179f, 360f, 720f, LottieLinearishEasing)
        }
        rotate(degrees = rotation, pivot = HandPivot) {
            translate(left = HandPivot.x, top = HandPivot.y - 6.281f) {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(-7.674f, -63f),
                    size = Size(15f, 76.499f),
                    cornerRadius = CornerRadius(7.5f, 7.5f),
                )
            }
        }
    }
}

/** Interpolates one Lottie key frame segment from [startFrame] to [endFrame]. */
private fun keyframe(
    frame: Float,
    startFrame: Float,
    endFrame: Float,
    from: Float,
    to: Float,
    easing: Easing,
): Float {
    if (frame <= startFrame) return from
    if (frame >= endFrame) return to
    val fraction = easing.transform((frame - startFrame) / (endFrame - startFrame))
    return from + (to - from) * fraction
}

/** Interpolates a run of Lottie key frames that all share [easing]. */
private fun keyframes(frame: Float, frames: FloatArray, values: FloatArray, easing: Easing): Float {
    if (frame <= frames.first()) return values.first()
    for (i in 0 until frames.size - 1) {
        if (frame < frames[i + 1]) return keyframe(frame, frames[i], frames[i + 1], values[i], values[i + 1], easing)
    }
    return values.last()
}

/** Contains default values used by [CompleteState]. */
object CompleteStateDefaults {

    /** The size of the result animation (coui_component_complete_state.xml complete_anim 100dp). */
    val AnimationSize = 100.dp

    /** Horizontal margin of the animation and the texts (COUI complete_page_text_padding_hor). */
    val TextPaddingHorizontal = 8.dp

    /** Top margin of the title (COUI complete_page_text_title_margin_top). */
    val TitleMarginTop = 8.dp

    /** Top margin of the subtitle (COUI complete_page_text_subtitle_margin_top). */
    val SubtitleMarginTop = 8.dp

    /**
     * The green baked into complete_page_success_anim.json and complete_page_wait_anim.json
     * (rgb 0.2, 0.69, 0.329). The Lottie view disables force-dark, so it is the same in both
     * themes and no theme color slot carries it.
     */
    val SuccessColor = Color(0xFF33B054)

    /** The red baked into complete_page_fail_anim.json (rgb 0.929, 0.251, 0.251), the same in both themes. */
    val FailedColor = Color(0xFFED4040)

    /**
     * The default [CompleteStateColors]: the Lottie-baked result colors, the title in
     * couiColorLabelPrimary and the subtitle in couiColorLabelSecondary.
     */
    @Composable
    fun completeStateColors(
        successColor: Color = SuccessColor,
        failedColor: Color = FailedColor,
        waitingColor: Color = SuccessColor,
        titleColor: Color = COUITheme.colorScheme.labelPrimary,
        subtitleColor: Color = COUITheme.colorScheme.labelSecondary,
    ): CompleteStateColors = remember(successColor, failedColor, waitingColor, titleColor, subtitleColor) {
        CompleteStateColors(
            successColor = successColor,
            failedColor = failedColor,
            waitingColor = waitingColor,
            titleColor = titleColor,
            subtitleColor = subtitleColor,
        )
    }
}

/**
 * Colors used by a [CompleteState].
 *
 * @param successColor The color of the success ring and check mark.
 * @param failedColor The color of the failure ring and exclamation mark.
 * @param waitingColor The color of the waiting clock.
 * @param titleColor The color of the title.
 * @param subtitleColor The color of the subtitle.
 */
@Immutable
data class CompleteStateColors(
    val successColor: Color,
    val failedColor: Color,
    val waitingColor: Color,
    val titleColor: Color,
    val subtitleColor: Color,
)

/** The Lottie composition size, 300x300. */
private const val COMPOSITION_SIZE = 300f

/** The Lottie frame rate, 60fps. */
private const val LOTTIE_FRAME_RATE = 60f

/** complete_page_success_anim.json op 89. */
private const val SUCCESS_LAST_FRAME = 89f

/** complete_page_fail_anim.json op 61. */
private const val FAIL_LAST_FRAME = 61f

/** complete_page_wait_anim.json op 180. */
private const val WAIT_LAST_FRAME = 180f

/** The ring center, layer position (150, 150). */
private val CompositionCenter = Offset(150f, 150f)

/** The ring radius: a 68x68 ellipse scaled by 300%. */
private const val RING_RADIUS = 102f

/** Every stroke is 5 wide scaled by 300%. */
private const val STROKE_WIDTH = 15f

/** The round-capped stroke of the rings and the clock. */
private val RoundStroke = Stroke(width = STROKE_WIDTH, cap = StrokeCap.Round)

/** The fail ring: its Lottie stroke has butt caps. */
private val ButtStroke = Stroke(width = STROKE_WIDTH, cap = StrokeCap.Butt)

/** The check mark: round caps and joins (lc 2, lj 2). */
private val CheckStroke = Stroke(width = STROKE_WIDTH, cap = StrokeCap.Round, join = StrokeJoin.Round)

/** The clock pivot: the hand layers' anchor at (150, 156.031). */
private val HandPivot = Offset(150f, 156.031f)

/** The fade / trim easing of the success and fail files: out (0.001, 0), in (0.1, 1). */
private val LottieEaseOut = CubicBezierEasing(0.001f, 0f, 0.1f, 1f)

/** The shake and wait fade-in easing: out (0.33, 0), in (0.67, 1). */
private val LottieStandardEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** The wait hour hand's trim easing: out (0.3, 0), in (0.1, 1). */
private val LottieRevealEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** The near-linear After Effects default easing: out (0.167, 0.167), in (0.833, 0.833). */
private val LottieLinearishEasing = CubicBezierEasing(0.167f, 0.167f, 0.833f, 0.833f)

/** The fail mark's x shake key frames (-3, 3, 9, 15, 21, 27). */
private val FailShakeFrames = floatArrayOf(-3f, 3f, 9f, 15f, 21f, 27f)

/** The fail mark's x shake values at [FailShakeFrames]. */
private val FailShakeValues = floatArrayOf(0f, 15.934f, -17.862f, 3f, -5f, 0f)

/**
 * The success check mark in composition space. The Lottie shape is the polyline
 * (28.61, 6.997) -> (-14, 7) -> (-13.975, -21.437), scaled by 300% and rotated by -45 degrees in
 * its group, placed at (149.548, 135.151): the layer position (14.849, -0.452) turned by the
 * parent ring layer's -90 degree rotation around (150, 150).
 */
private val SuccessCheckPath: Path = run {
    val origin = Offset(150f - 0.452f, 150f - 14.849f)
    val angle = -45.0 * PI / 180.0
    val c = cos(angle).toFloat()
    val s = sin(angle).toFloat()
    fun point(x: Float, y: Float): Offset {
        val sx = x * 3f
        val sy = y * 3f
        return Offset(origin.x + sx * c - sy * s, origin.y + sx * s + sy * c)
    }
    val p0 = point(28.61f, 6.997f)
    val p1 = point(-14f, 7f)
    val p2 = point(-13.975f, -21.437f)
    Path().apply {
        moveTo(p0.x, p0.y)
        lineTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
    }
}
