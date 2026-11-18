// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import io.github.suqi8.coui.kmp.effect.MaterialBlurBlendMode
import io.github.suqi8.coui.kmp.effect.MaterialBlurProvider
import io.github.suqi8.coui.kmp.effect.MaterialBlurSpec
import io.github.suqi8.coui.kmp.shader.isRenderEffectSupported
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

/**
 * What a [CameraShutterButton] captures, which picks the color of its inner disc.
 */
enum class CameraShutterMode {
    /** Photo capture: the white (or accent) disc. */
    Photo,

    /** Video capture: the red disc, which turns into a rounded square while recording. */
    Video,
}

/**
 * The look of a [CameraShutterButton].
 */
enum class CameraShutterStyle {
    /**
     * The camera's shutter on devices with render effects: a white disc on a blurred glass circle
     * edged by a hairline ring that fades in towards the bottom.
     */
    Glass,

    /**
     * [Glass] with the camera's accent disc: an orange gradient disc with a soft orange halo
     * below it and the shutter edge light inside its rim.
     */
    Accent,

    /**
     * The camera's shutter without render effects: a white disc inside a 4dp white ring with a
     * faint dark outline and no glass.
     */
    Ring,
}

/**
 * The main shutter of the camera, the port of `MainShutterButton` (a `ShutterButton`).
 *
 * A [CameraShutterDefaults.Size] circle holds a 27dp-radius disc: white (or the accent gradient)
 * in [CameraShutterMode.Photo] and red in [CameraShutterMode.Video]; switching the mode cross-fades
 * the disc. While [recording] in video mode, or while [captureProgress] is set, the disc morphs
 * into a 30dp rounded square in 300ms; stopping a recording morphs it back. A [captureProgress]
 * (a long exposure in the camera) dims the ring to 30% and draws a 4dp progress arc over it from
 * the top. [loading] hides the disc and spins a fading arc around its edge, once every 700ms; a
 * [timerLabel] (the camera shows the self-timer, "3" or "10") replaces the disc by the label.
 *
 * Pressing shrinks the disc (or square, or label) to 80% in 350ms and dims the ring to 50%;
 * releasing grows it back in 300ms. In video mode the press feedback waits 120ms and the release
 * 100ms, so a quick tap only starts or stops the recording. Touches within
 * [CameraShutterDefaults.TouchInset] of the edge are ignored. A disabled shutter is drawn at 40%.
 *
 * The glass circle of [CameraShutterStyle.Glass] and [CameraShutterStyle.Accent] is the camera
 * material blur when [materialBlur] is given (30dp blur, glow overlay of a 15% grey over a 60% mix
 * of 30% grey) and otherwise a 50% black circle. The shutter always uses its fixed light-on-dark
 * palette, since it sits over the camera preview.
 *
 * @param onClick Called when the shutter is tapped.
 * @param modifier The modifier to be applied to the shutter.
 * @param onLongClick Called once the shutter has been held for the long-press timeout (the camera
 *   starts a burst or a quick recording); a tap then no longer calls [onClick].
 * @param enabled Whether the shutter accepts input.
 * @param mode The capture mode, which picks the disc color.
 * @param recording Whether a video is being recorded: the disc becomes the red rounded square.
 * @param captureProgress The progress of a capture in progress, 0..1, drawn as an arc around the
 *   square; null when no such capture runs.
 * @param loading Whether the camera is busy (saving, processing): the disc gives way to a spinning
 *   arc.
 * @param timerLabel A short label shown in place of the disc, such as the self-timer "3" or "10";
 *   null shows the disc.
 * @param style The look of the shutter, see [CameraShutterStyle].
 * @param materialBlur Paints the camera material blur behind the glass circle. Pass a provider
 *   recording the content under the shutter (for example the camera preview); null keeps the 50%
 *   black fill. Not used by [CameraShutterStyle.Ring].
 * @param colors The colors of the shutter, see [CameraShutterDefaults.cameraShutterColors].
 */
@Composable
fun CameraShutterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    mode: CameraShutterMode = CameraShutterMode.Photo,
    recording: Boolean = false,
    captureProgress: Float? = null,
    loading: Boolean = false,
    timerLabel: String? = null,
    style: CameraShutterStyle = CameraShutterStyle.Glass,
    materialBlur: MaterialBlurProvider? = null,
    colors: CameraShutterColors = CameraShutterDefaults.cameraShutterColors(style),
) {
    val density = LocalDensity.current
    val video = mode == CameraShutterMode.Video
    val capturing = captureProgress != null
    val square = (video && recording) || capturing
    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnLongClick by rememberUpdatedState(onLongClick)

    // Press feedback (`MainShutterButton.setPressed`): the disc scales to 0.8 in 350ms and back in
    // 300ms, both on (0, 0, 0.1, 1); video shutters hold the press 120ms and the release 100ms.
    var pressed by remember { mutableStateOf(false) }
    val pressScale = remember { Animatable(1f) }
    val delayFeedback by rememberUpdatedState(video)
    LaunchedEffect(pressed) {
        if (pressed) {
            if (delayFeedback) delay(VIDEO_PRESS_DELAY_MILLIS)
            pressScale.animateTo(CameraShutterDefaults.PressedScale, tween(350, easing = ShutterPressEasing))
        } else {
            if (delayFeedback && pressScale.value < 1f) delay(VIDEO_RELEASE_DELAY_MILLIS)
            pressScale.animateTo(1f, tween(300, easing = ShutterPressEasing))
        }
    }

    // Photo <-> video: a 200ms color animation on (0.3, 0, 0.1, 1); the accent disc instead fades
    // against the red one in 300ms on (0.33, 0, 0.67, 1) (`startInsideColorAnimator`).
    val videoFraction = remember { Animatable(if (video) 1f else 0f) }
    LaunchedEffect(video, style) {
        val spec = if (style == CameraShutterStyle.Accent) {
            tween<Float>(300, easing = ShutterStandardEasing)
        } else {
            tween(200, easing = ShutterMoveEasing)
        }
        videoFraction.animateTo(if (video) 1f else 0f, spec)
    }

    // Disc <-> square: 300ms on (0.3, 0, 0.1, 1) (button types 5 and 6). After a capture with
    // progress the camera shows the disc again at once (type 9).
    val morph = remember { Animatable(if (square) 1f else 0f) }
    val squareCause = remember { BooleanArray(1) }
    if (square) squareCause[0] = capturing
    LaunchedEffect(square) {
        when {
            square -> morph.animateTo(1f, tween(300, easing = ShutterMoveEasing))
            squareCause[0] -> morph.snapTo(0f)
            else -> morph.animateTo(0f, tween(300, easing = ShutterMoveEasing))
        }
    }

    // The disc fades out for the spinner and the label and shrinks away for the label; the label
    // grows from 0.8 and fades in; all 300ms on (0.3, 0, 0.1, 1).
    val discAlpha = animateFloatAsState(
        targetValue = if (loading || timerLabel != null) 0f else 1f,
        animationSpec = tween(300, easing = ShutterMoveEasing),
    )
    val discScale = animateFloatAsState(
        targetValue = if (timerLabel != null) 0f else 1f,
        animationSpec = tween(300, easing = ShutterMoveEasing),
    )
    val labelProgress = animateFloatAsState(
        targetValue = if (timerLabel != null) 1f else 0f,
        animationSpec = tween(300, easing = ShutterMoveEasing),
    )

    // The spinner fades in over 300ms and turns once every 700ms.
    val loadingAlpha = remember { Animatable(0f) }
    val loadingAngle = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(loading) {
        if (!loading) {
            loadingAlpha.snapTo(0f)
            return@LaunchedEffect
        }
        launch { loadingAlpha.animateTo(1f, tween(300, easing = ShutterMoveEasing)) }
        val start = withFrameMillis { it }
        while (true) {
            withFrameMillis { now ->
                loadingAngle.floatValue = ((now - start) % LOADING_TURN_MILLIS) * 360f / LOADING_TURN_MILLIS
            }
        }
    }

    // A capture with progress dims the ring from 255 to 77 alpha in 180ms (`kb`).
    val ringDim = remember { Animatable(1f) }
    LaunchedEffect(capturing) {
        if (capturing) {
            ringDim.animateTo(PROGRESS_RING_ALPHA, tween(180, easing = ShutterStandardEasing))
        } else {
            ringDim.snapTo(1f)
        }
    }
    val progress = rememberUpdatedState(captureProgress ?: 0f)

    val textMeasurer = rememberTextMeasurer()
    val labelHolder = remember { arrayOfNulls<String>(1) }
    if (timerLabel != null) labelHolder[0] = timerLabel
    val shownLabel = labelHolder[0]
    val labelStyle = remember(density, colors.timerLabelColor, colors.timerLabelShadowColor) {
        TextStyle(
            color = colors.timerLabelColor,
            fontSize = (CameraShutterDefaults.TimerTextSize.value / density.fontScale).sp,
            fontWeight = FontWeight(TIMER_FONT_WEIGHT),
            shadow = Shadow(
                color = colors.timerLabelShadowColor,
                blurRadius = with(density) { CameraShutterDefaults.TimerShadowRadius.toPx() },
            ),
        )
    }
    val labelLayout = remember(shownLabel, labelStyle) {
        shownLabel?.let { textMeasurer.measure(it, labelStyle) }
    }

    // The halo and the edge light belong to the idle accent photo disc only.
    val glow = style == CameraShutterStyle.Accent && !video && !square && !loading && timerLabel == null

    val blurSpec = remember(density, colors) {
        MaterialBlurSpec(
            blurRadiusPx = with(density) { CameraShutterDefaults.BlurRadius.toPx() },
            mixColor = colors.blurMixColor,
            blendColor = colors.blurBlendColor,
            blendMode = MaterialBlurBlendMode.GlowOverlay,
        )
    }
    // The camera without render effects (the Ring look) draws no glass behind the shutter.
    val glass = if (style == CameraShutterStyle.Ring) {
        Modifier
    } else {
        materialBlur?.materialBlur(blurSpec, CircleShape) ?: Modifier.background(colors.containerColor, CircleShape)
    }

    Box(
        modifier = modifier
            .size(CameraShutterDefaults.Size)
            .graphicsLayer { alpha = if (enabled) 1f else CameraShutterDefaults.DisabledAlpha }
            .then(glass)
            .semantics {
                role = Role.Button
                if (enabled) {
                    // `this.` picks the semantics actions over the callback parameters of the same name.
                    this.onClick(label = null) {
                        currentOnClick()
                        true
                    }
                    if (onLongClick != null) {
                        this.onLongClick(label = null) {
                            currentOnLongClick?.invoke()
                            true
                        }
                    }
                } else {
                    disabled()
                }
            }
            .pointerInput(enabled, onLongClick != null) {
                if (!enabled) return@pointerInput
                val inset = CameraShutterDefaults.TouchInset.toPx()
                awaitEachGesture {
                    val down = awaitFirstDown()
                    val p = down.position
                    // ShutterButton ignores downs outside its drawing rect inset by the touch offset.
                    if (p.x < inset || p.y < inset || p.x > size.width - inset || p.y > size.height - inset) {
                        return@awaitEachGesture
                    }
                    down.consume()
                    pressed = true
                    try {
                        val longClick = currentOnLongClick
                        val up: PointerInputChange?
                        if (longClick != null) {
                            var timedOut = true
                            up = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                                waitForUpOrCancellation().also { timedOut = false }
                            }
                            if (timedOut) {
                                longClick()
                                waitForUpOrCancellation()?.consume()
                                return@awaitEachGesture
                            }
                        } else {
                            up = waitForUpOrCancellation()
                        }
                        if (up != null) {
                            up.consume()
                            currentOnClick()
                        }
                    } finally {
                        pressed = false
                    }
                }
            }
            .drawWithCache {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val center = Offset(cx, cy)
                val outer = CameraShutterDefaults.OuterRadius.toPx()
                val hairline = CameraShutterDefaults.RingWidth.toPx()
                val dial = CameraShutterDefaults.DialWidth.toPx()
                val ringWidth = if (style == CameraShutterStyle.Ring) dial else hairline
                val ringRadius = outer - ringWidth / 2f
                val ringBrush = Brush.verticalGradient(
                    colors = listOf(colors.ringTopColor, colors.ringBottomColor),
                    startY = 0f,
                    endY = size.height * RING_GRADIENT_END,
                )
                val outlineWidth = ringWidth + CameraShutterDefaults.RingOutlineExtraWidth.toPx()
                val arcRadius = outer - dial / 2f
                val inner = CameraShutterDefaults.InnerRadius.toPx()
                // The disc gradient and the spinner run on the inner circle less half a hairline.
                val innerTrack = inner - hairline / 2f
                val photoBrush = Brush.verticalGradient(
                    0f to colors.photoTopColor,
                    PHOTO_GRADIENT_STOP to colors.photoTopColor,
                    1f to colors.photoBottomColor,
                    startY = cy - innerTrack,
                    endY = cy + innerTrack,
                )
                val loadingBrush = Brush.sweepGradient(
                    colors = listOf(Color.Transparent, colors.loadingColor),
                    center = center,
                )
                val squareHalf = CameraShutterDefaults.InnerSquareSize.toPx() / 2f
                val squareCorner = CameraShutterDefaults.InnerSquareCornerRadius.toPx()
                val haloOffset = CameraShutterDefaults.HaloOffsetY.toPx()
                val halo = if (style == CameraShutterStyle.Accent && isRenderEffectSupported()) {
                    val radius = CameraShutterDefaults.HaloRadius.toPx()
                    val blur = CameraShutterDefaults.HaloBlurRadius.toPx()
                    val pad = ceil(radius + blur * 2f)
                    obtainGraphicsLayer().apply {
                        renderEffect = BlurEffect(blur, blur, TileMode.Decal)
                        record(size = IntSize((pad * 2f).toInt(), (pad * 2f).toInt())) {
                            drawCircle(color = colors.haloColor, radius = radius, center = Offset(pad, pad))
                        }
                    } to pad
                } else {
                    null
                }
                onDrawBehind {
                    val ringAlpha = ringDim.value * if (pressed && !capturing) PRESSED_RING_ALPHA else 1f
                    if (glow && halo != null) {
                        val (layer, pad) = halo
                        translate(cx - pad, cy + haloOffset - pad) { drawLayer(layer) }
                    }

                    val m = morph.value
                    val s = pressScale.value * discScale.value
                    val half = lerp(inner, squareHalf, m) * s
                    val corner = lerp(inner, squareCorner, m) * s
                    val a = discAlpha.value
                    if (a > 0f && half > 0f) {
                        val f = videoFraction.value
                        val topLeft = Offset(cx - half, cy - half)
                        val discSize = Size(half * 2f, half * 2f)
                        val radius = CornerRadius(corner)
                        val photoAlpha = when {
                            style == CameraShutterStyle.Accent -> 1f - f
                            f >= 1f -> 0f
                            else -> 1f
                        }
                        if (photoAlpha > 0f) {
                            drawRoundRect(photoBrush, topLeft, discSize, radius, alpha = a * photoAlpha)
                        }
                        if (f > 0f) {
                            drawRoundRect(colors.videoColor, topLeft, discSize, radius, alpha = a * f)
                        }
                    }

                    if (style == CameraShutterStyle.Ring && ringAlpha >= 1f) {
                        drawCircle(colors.ringOutlineColor, ringRadius, center, style = Stroke(outlineWidth))
                    }
                    drawCircle(ringBrush, ringRadius, center, alpha = ringAlpha, style = Stroke(ringWidth))

                    val la = loadingAlpha.value
                    if (la > 0f) {
                        rotate(loadingAngle.floatValue, center) {
                            drawCircle(loadingBrush, innerTrack, center, alpha = la, style = Stroke(dial))
                        }
                    }

                    if (capturing) {
                        val sweep = progress.value.coerceIn(0f, 1f) * 360f
                        if (sweep > 0f) {
                            drawArc(
                                color = colors.progressColor,
                                startAngle = -90f,
                                sweepAngle = sweep,
                                useCenter = false,
                                topLeft = Offset(cx - arcRadius, cy - arcRadius),
                                size = Size(arcRadius * 2f, arcRadius * 2f),
                                style = Stroke(dial),
                            )
                        }
                    }

                    val lp = labelProgress.value
                    if (lp > 0f && labelLayout != null) {
                        scale((LABEL_START_SCALE + (1f - LABEL_START_SCALE) * lp) * pressScale.value, center) {
                            drawText(
                                textLayoutResult = labelLayout,
                                topLeft = Offset(
                                    cx - labelLayout.size.width / 2f,
                                    cy - labelLayout.size.height / 2f,
                                ),
                                alpha = lp,
                            )
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        if (style == CameraShutterStyle.Accent) {
            // The shutter edge light (`ap.d.a(4)`), clipped to the disc and scaled with it.
            Box(
                Modifier
                    .size(CameraShutterDefaults.InnerRadius * 2)
                    .graphicsLayer {
                        val s = pressScale.value * discScale.value
                        scaleX = s
                        scaleY = s
                    }
                    .cameraInnerShadow(CameraInnerShadows.Shutter, CameraShutterDefaults.InnerRadius, enabled = glow),
            )
        }
    }
}

/** How long a video shutter holds the press feedback (`MainShutterButton` posts it after 120ms). */
private const val VIDEO_PRESS_DELAY_MILLIS = 120L

/** How long a video shutter holds the release feedback (`postDelayed(..., 100)`). */
private const val VIDEO_RELEASE_DELAY_MILLIS = 100L

/** One turn of the spinner (`main_btn_default_angleAnimationDurationMillis`). */
private const val LOADING_TURN_MILLIS = 700L

/** The ring alpha during a capture with progress (77 / 255). */
private const val PROGRESS_RING_ALPHA = 77f / 255f

/** The ring alpha while pressed (128 / 255). */
private const val PRESSED_RING_ALPHA = 128f / 255f

/** The hairline ring gradient ends at 80% of the height (`getOutsideCircleGradient`). */
private const val RING_GRADIENT_END = 0.8f

/** The accent disc keeps its top color down to 25% of its height. */
private const val PHOTO_GRADIENT_STOP = 0.25f

/** The scale the timer label grows from. */
private const val LABEL_START_SCALE = 0.8f

/** The variable weight of the camera's countdown typeface (`SysSans-En-Regular`, wght 533). */
private const val TIMER_FONT_WEIGHT = 533

/** `c9.a.e` / `COUIMoveEaseInterpolator` (0.3, 0, 0.1, 1). */
private val ShutterMoveEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** `c9.a.p`, the press interpolator (0, 0, 0.1, 1). */
private val ShutterPressEasing = CubicBezierEasing(0f, 0f, 0.1f, 1f)

/** `COUIEaseInterpolator` (0.33, 0, 0.67, 1). */
private val ShutterStandardEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/**
 * Default values of [CameraShutterButton], from the camera's `shutter_button_*` dimensions.
 */
object CameraShutterDefaults {
    /** The shutter size (`shutter_button_length`, `shutter_button_size`). */
    val Size = 66.dp

    /** The radius of the ring (`shutter_button_big_circle_radius`). */
    val OuterRadius = 33.dp

    /** The radius of the disc (`shutter_button_small_circle_radius`). */
    val InnerRadius = 27.dp

    /** The side of the recording square (`shutter_button_inside_rect_length`). */
    val InnerSquareSize = 30.dp

    /** The corner radius of the recording square (`shutter_button_inside_rect_corner_radius`). */
    val InnerSquareCornerRadius = 8.dp

    /** The hairline ring of the glass styles (`shutter_button_big_circle_thickness`). */
    val RingWidth = 0.9.dp

    /**
     * The ring of [CameraShutterStyle.Ring], the progress arc and the spinner
     * (`shutter_button_big_circle_dial_thickness`).
     */
    val DialWidth = 4.dp

    /** How much wider the dark outline of [CameraShutterStyle.Ring] is (`shutter_button_circle_stroke`). */
    val RingOutlineExtraWidth = 0.33.dp

    /** The edge band in which touches are ignored (`main_shutter_button_touch_offset`). */
    val TouchInset = 3.dp

    /** The material blur radius of the glass circle (`shutter_button_blur_radius`). */
    val BlurRadius = 30.dp

    /** The radius of the accent halo (`shutter_button_shadow_circle_radius`). */
    val HaloRadius = 19.dp

    /** How far the accent halo sits below the center (`shutter_button_shadow_circle_center_offset`). */
    val HaloOffsetY = 10.dp

    /** The blur of the accent halo (`shutter_button_shadow_blur_radius`). */
    val HaloBlurRadius = 28.dp

    /** The size of the timer label, in dp like the camera (`camera_button_count_down_text_size`). */
    val TimerTextSize = 24.dp

    /** The shadow blur of the timer label (`num_seekbar_text_shadow_blur`). */
    val TimerShadowRadius = 2.dp

    /** The disc scale while pressed (`press_tag_scale`). */
    val PressedScale = 0.8f

    /** The alpha of a disabled shutter (`TwoStateImageView.setEnabled`). */
    val DisabledAlpha = 0.4f

    /**
     * The fixed camera palette of [style]: the non-blur glass `color_black_with_50_percent_transparency`,
     * the material blur colors of `BlurBackgroundImpl` (light material, progress 1), the hairline
     * ring #00D9D9D9 to #33D9D9D9 (white for [CameraShutterStyle.Ring], with a #4D000000 outline),
     * the white photo disc or the accent gradient #F99018 to #FF6B00 with the
     * `color_shutter_button_bg_halo_color_hasselblad` halo, the #E32127 video disc, a white progress
     * arc, the #66FFFFFF spinner and the white timer label with a #33000000 shadow.
     */
    @Composable
    fun cameraShutterColors(
        style: CameraShutterStyle = CameraShutterStyle.Glass,
        containerColor: Color = Color(0x7F000000),
        blurMixColor: Color = Color(red = 0.302f, green = 0.302f, blue = 0.302f, alpha = 0.6f),
        blurBlendColor: Color = Color(red = 0.15f, green = 0.15f, blue = 0.15f, alpha = 0.15f),
        ringTopColor: Color = if (style == CameraShutterStyle.Ring) Color.White else Color(0x00D9D9D9),
        ringBottomColor: Color = if (style == CameraShutterStyle.Ring) Color.White else Color(0x33D9D9D9),
        ringOutlineColor: Color = Color(0x4D000000),
        photoTopColor: Color = if (style == CameraShutterStyle.Accent) Color(0xFFF99018) else Color.White,
        photoBottomColor: Color = if (style == CameraShutterStyle.Accent) Color(0xFFFF6B00) else Color.White,
        videoColor: Color = Color(0xFFE32127),
        haloColor: Color = Color(0xB3FF5900),
        progressColor: Color = Color.White,
        loadingColor: Color = Color(0x66FFFFFF),
        timerLabelColor: Color = Color.White,
        timerLabelShadowColor: Color = Color(0x33000000),
    ): CameraShutterColors = remember(
        containerColor,
        blurMixColor,
        blurBlendColor,
        ringTopColor,
        ringBottomColor,
        ringOutlineColor,
        photoTopColor,
        photoBottomColor,
        videoColor,
        haloColor,
        progressColor,
        loadingColor,
        timerLabelColor,
        timerLabelShadowColor,
    ) {
        CameraShutterColors(
            containerColor = containerColor,
            blurMixColor = blurMixColor,
            blurBlendColor = blurBlendColor,
            ringTopColor = ringTopColor,
            ringBottomColor = ringBottomColor,
            ringOutlineColor = ringOutlineColor,
            photoTopColor = photoTopColor,
            photoBottomColor = photoBottomColor,
            videoColor = videoColor,
            haloColor = haloColor,
            progressColor = progressColor,
            loadingColor = loadingColor,
            timerLabelColor = timerLabelColor,
            timerLabelShadowColor = timerLabelShadowColor,
        )
    }
}

/**
 * The colors of a [CameraShutterButton].
 *
 * @param containerColor The glass circle without material blur.
 * @param blurMixColor The material blur mix color.
 * @param blurBlendColor The material blur glow-overlay color.
 * @param ringTopColor The ring at the top of the shutter.
 * @param ringBottomColor The ring at 80% of the height and below.
 * @param ringOutlineColor The dark outline of [CameraShutterStyle.Ring].
 * @param photoTopColor The photo disc down to 25% of its height.
 * @param photoBottomColor The photo disc at its bottom.
 * @param videoColor The video disc and the recording square.
 * @param haloColor The halo below the accent disc.
 * @param progressColor The progress arc.
 * @param loadingColor The bright end of the spinner.
 * @param timerLabelColor The timer label.
 * @param timerLabelShadowColor The shadow of the timer label.
 */
@Immutable
data class CameraShutterColors(
    val containerColor: Color,
    val blurMixColor: Color,
    val blurBlendColor: Color,
    val ringTopColor: Color,
    val ringBottomColor: Color,
    val ringOutlineColor: Color,
    val photoTopColor: Color,
    val photoBottomColor: Color,
    val videoColor: Color,
    val haloColor: Color,
    val progressColor: Color,
    val loadingColor: Color,
    val timerLabelColor: Color,
    val timerLabelShadowColor: Color,
)
