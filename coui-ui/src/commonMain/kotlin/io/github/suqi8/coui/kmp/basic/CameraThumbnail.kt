// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

/**
 * How [CameraThumbnail] brings in a new image, the `animType` of the camera's
 * `ThumbImageView.setBitmap`.
 */
enum class CameraThumbnailTransition {
    /** The new image replaces the old one at once (`animType` 0). */
    None,

    /**
     * The new image grows out of the center over the old one, from 6% to its full size (`animType`
     * 1 and 2, after a capture). During a burst, a new image arriving before the growing one has
     * reached 80% replaces it in place instead of restarting the animation.
     */
    ScaleIn,

    /**
     * The new image is shown at once and the old one shrinks into the center over it (`animType`
     * 3, for example after the latest photo was deleted).
     */
    ScaleOut,
}

/**
 * The gallery thumbnail button of the camera, the port of `ThumbImageView`: a 40dp rounded square
 * (6dp corners) showing the latest photo, center-cropped, with a hairline 10% white frame. Without
 * an image it shows the camera's dark placeholder (`thumbnail_bg_normal`, #262626).
 *
 * Each time [image] changes, the new image comes in with [transition]: by default it grows out of
 * the center over the previous one in 350ms on (0.3, 0, 0.1, 1). Pass a new [Painter] instance for
 * each new photo and keep it stable between recompositions (`remember` it), since the thumbnail
 * tells photos apart by identity.
 *
 * Pressing dims the thumbnail to 50% (200ms, released in 340ms, on (0.33, 0, 0.67, 1), the camera's
 * `COUIPressFeedbackHelper` alpha feedback); a disabled thumbnail stays at 40%. Toggling [visible]
 * fades and scales the thumbnail between 80% and full size in 200ms on (0.3, 0, 0.1, 1), showing
 * after a 200ms delay; a hidden thumbnail keeps its space. [rotation] turns the image with the
 * device orientation along the shorter way, 1000ms per 270 degrees.
 *
 * The camera draws no edge light on the thumbnail. It always uses its fixed light-on-dark palette.
 *
 * @param image The latest photo, or null for the placeholder.
 * @param onClick Called when the thumbnail is tapped, typically to open the gallery.
 * @param modifier The modifier to be applied to the thumbnail.
 * @param enabled Whether the thumbnail accepts taps.
 * @param visible Whether the thumbnail is shown.
 * @param transition How a new [image] comes in.
 * @param rotation The angle the image is turned to, in clockwise degrees (a multiple of 90 to keep
 *   the photo upright while the device rotates).
 * @param contentDescription The accessibility description of the thumbnail.
 * @param size The side of the square thumbnail.
 * @param cornerRadius The corner radius of the thumbnail.
 * @param strokeWidth The width of the frame drawn inside the edge; 0dp draws none.
 * @param colors The colors of the thumbnail, see [CameraThumbnailDefaults.cameraThumbnailColors].
 */
@Composable
fun CameraThumbnail(
    image: Painter?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    visible: Boolean = true,
    transition: CameraThumbnailTransition = CameraThumbnailTransition.ScaleIn,
    rotation: Int = 0,
    contentDescription: String? = null,
    size: Dp = CameraThumbnailDefaults.Size,
    cornerRadius: Dp = CameraThumbnailDefaults.CornerRadius,
    strokeWidth: Dp = CameraThumbnailDefaults.StrokeWidth,
    colors: CameraThumbnailColors = CameraThumbnailDefaults.cameraThumbnailColors(),
) {
    val scope = rememberCoroutineScope()
    val state = remember { CameraThumbnailState(image) }

    LaunchedEffect(image) {
        state.update(image, transition, scope)
    }

    // ThumbImageView.setOrientation: the shorter way, 1000ms per 270 degrees (c0.b on COUI e).
    val angle = remember { Animatable(rotation.toFloat()) }
    LaunchedEffect(rotation) {
        val from = angle.value
        var delta = ((rotation - from) % 360f + 360f) % 360f
        if (delta > 180f) delta -= 360f
        if (delta == 0f) return@LaunchedEffect
        val duration = (abs(delta) * CameraThumbnailDefaults.RotationMillisPer270 / 270f).toInt()
        angle.animateTo(from + delta, tween(durationMillis = duration, easing = ThumbnailMoveEase))
    }

    // ThumbImageView.setVisibilityWithAnimation (b3): alpha and scale 0.8..1 in 200ms on COUI e,
    // showing after a 200ms start offset.
    val shown = remember { Animatable(if (visible) 1f else 0f) }
    LaunchedEffect(visible) {
        shown.animateTo(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(
                durationMillis = CameraThumbnailDefaults.VisibilityDuration,
                delayMillis = if (visible) CameraThumbnailDefaults.VisibilityDuration else 0,
                easing = ThumbnailMoveEase,
            ),
        )
    }

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressAlpha by animateFloatAsState(
        targetValue = if (pressed) CameraThumbnailDefaults.PressedAlpha else 1f,
        animationSpec = tween(durationMillis = if (pressed) 200 else 340, easing = ThumbnailStandardEase),
    )
    val enabledAlpha = if (enabled) 1f else CameraThumbnailDefaults.DisabledAlpha

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                val fraction = shown.value
                alpha = fraction * pressAlpha * enabledAlpha
                val s = CameraThumbnailDefaults.HiddenScale + (1f - CameraThumbnailDefaults.HiddenScale) * fraction
                scaleX = s
                scaleY = s
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && visible,
                role = Role.Button,
                onClick = onClick,
            )
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else {
                    Modifier
                },
            ),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = angle.value },
        ) {
            val radius = cornerRadius.toPx().coerceAtMost(this.size.minDimension / 2f)
            val stroke = strokeWidth.toPx()
            val clip = Path().apply {
                addRoundRect(RoundRect(0f, 0f, this@Canvas.size.width, this@Canvas.size.height, CornerRadius(radius)))
            }
            val fraction = state.progress.value
            val animating = state.animating && fraction < 1f
            if (!animating) {
                drawThumb(state.current, clip, 1f, colors)
                drawFrame(stroke, radius, colors)
            } else if (state.reverse) {
                // animType 3: the new image is the base, the old one shrinks from 94% to nothing.
                drawThumb(state.current, clip, 1f, colors)
                drawFrame(stroke, radius, colors)
                drawThumb(state.previous, clip, NEW_THUMB_MAX_SCALE * (1f - fraction), colors)
            } else {
                // animType 1/2: the old image stays, the new one grows from 6% (ratio 0.03..0.5).
                drawThumb(state.previous, clip, 1f, colors)
                drawThumb(state.current, clip, NEW_THUMB_MIN_SCALE + (1f - NEW_THUMB_MIN_SCALE) * fraction, colors)
                drawFrame(stroke, radius, colors)
            }
        }
    }
}

/**
 * [CameraThumbnail] for an [ImageBitmap]: each new bitmap instance counts as a new photo.
 *
 * @param bitmap The latest photo, or null for the placeholder.
 * @param onClick Called when the thumbnail is tapped.
 * @param modifier The modifier to be applied to the thumbnail.
 * @param enabled Whether the thumbnail accepts taps.
 * @param visible Whether the thumbnail is shown.
 * @param transition How a new [bitmap] comes in.
 * @param rotation The angle the image is turned to, in clockwise degrees.
 * @param contentDescription The accessibility description of the thumbnail.
 * @param size The side of the square thumbnail.
 * @param cornerRadius The corner radius of the thumbnail.
 * @param strokeWidth The width of the frame drawn inside the edge.
 * @param colors The colors of the thumbnail.
 */
@Composable
fun CameraThumbnail(
    bitmap: ImageBitmap?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    visible: Boolean = true,
    transition: CameraThumbnailTransition = CameraThumbnailTransition.ScaleIn,
    rotation: Int = 0,
    contentDescription: String? = null,
    size: Dp = CameraThumbnailDefaults.Size,
    cornerRadius: Dp = CameraThumbnailDefaults.CornerRadius,
    strokeWidth: Dp = CameraThumbnailDefaults.StrokeWidth,
    colors: CameraThumbnailColors = CameraThumbnailDefaults.cameraThumbnailColors(),
) {
    val painter = remember(bitmap) { bitmap?.let { BitmapPainter(it) } }
    CameraThumbnail(
        image = painter,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        visible = visible,
        transition = transition,
        rotation = rotation,
        contentDescription = contentDescription,
        size = size,
        cornerRadius = cornerRadius,
        strokeWidth = strokeWidth,
        colors = colors,
    )
}

/** The smallest scale of a growing new image: `ThumbImageView.J0` starts the ratio at 0.03. */
private const val NEW_THUMB_MIN_SCALE = 0.06f

/** The largest scale of a shrinking old image: 0.5 - 0.03 in `ThumbImageView.Y`. */
private const val NEW_THUMB_MAX_SCALE = 0.94f

/**
 * The progress past which a new burst image restarts the scale-in: `ThumbImageView.setBitmap`
 * restarts once the ratio passed 0.4, that is (0.4 - 0.03) / 0.47 of the eased progress.
 */
private const val BURST_RESTART_PROGRESS = 0.787f

/** COUI `e`, the camera's `c9.a.e` (0.3, 0, 0.1, 1). */
private val ThumbnailMoveEase = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** COUI `b`, the camera's `c9.a.a` (0.33, 0, 0.67, 1). */
private val ThumbnailStandardEase = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/**
 * The image model of [CameraThumbnail]: [current] is the latest image (`B`), [previous] the one
 * it replaces (`C`) while [animating], and [progress] the eased animation progress.
 */
@Stable
private class CameraThumbnailState(initial: Painter?) {
    var current by mutableStateOf(initial)
    var previous by mutableStateOf<Painter?>(null)
    var reverse by mutableStateOf(false)
    var animating by mutableStateOf(false)
    val progress = Animatable(1f)
    private var job: Job? = null

    fun update(image: Painter?, transition: CameraThumbnailTransition, scope: CoroutineScope) {
        if (image === current) return
        if (image == null || transition == CameraThumbnailTransition.None) {
            job?.cancel()
            animating = false
            previous = null
            current = image
            return
        }
        val reverse = transition == CameraThumbnailTransition.ScaleOut
        val restart = reverse || this.reverse || !animating || progress.value >= BURST_RESTART_PROGRESS
        previous = current
        current = image
        this.reverse = reverse
        if (!restart) return
        job?.cancel()
        animating = true
        job = scope.launch {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = CameraThumbnailDefaults.TransitionDuration, easing = ThumbnailMoveEase),
            )
            animating = false
            previous = null
        }
    }
}

/** Draws [painter] (or the placeholder) center-cropped into [clip], scaled around the center. */
private fun DrawScope.drawThumb(
    painter: Painter?,
    clip: Path,
    factor: Float,
    colors: CameraThumbnailColors,
) {
    if (factor <= 0f) return
    scale(factor) {
        clipPath(clip) {
            if (painter == null) {
                drawRect(colors.placeholderColor)
            } else {
                drawCenterCropped(painter)
            }
        }
    }
}

/** `ThumbImageView.T`: the center of the image, cropped to the thumbnail's aspect ratio. */
private fun DrawScope.drawCenterCropped(painter: Painter) {
    val intrinsic = painter.intrinsicSize
    if (intrinsic.isUnspecified || intrinsic.width <= 0f || intrinsic.height <= 0f) {
        with(painter) { draw(size) }
        return
    }
    val s = max(size.width / intrinsic.width, size.height / intrinsic.height)
    val w = intrinsic.width * s
    val h = intrinsic.height * s
    translate((size.width - w) / 2f, (size.height - h) / 2f) {
        with(painter) { draw(Size(w, h)) }
    }
}

/** `ThumbImageView.drawStroke`: the frame, its stroke centered half a width inside the edge. */
private fun DrawScope.drawFrame(stroke: Float, radius: Float, colors: CameraThumbnailColors) {
    if (stroke <= 0f) return
    val half = stroke / 2f
    drawRoundRect(
        color = colors.strokeColor,
        topLeft = Offset(half, half),
        size = Size(size.width - stroke, size.height - stroke),
        cornerRadius = CornerRadius((radius - half).coerceAtLeast(0f)),
        style = Stroke(width = stroke),
    )
}

/**
 * Default values of [CameraThumbnail], from the camera's `thumbnail_*` dimensions and
 * `ThumbImageView`.
 */
object CameraThumbnailDefaults {
    /** The thumbnail side (`thumbnail_view_width`). */
    val Size = 40.dp

    /** The corner radius (`thumbnail_round_corner_radius`). */
    val CornerRadius = 6.dp

    /** The corner radius of the retro film style (`thumbnail_retro_style_radius`). */
    val RetroCornerRadius = 4.dp

    /** The frame width (`thumbnail_round_stroke_width`). */
    val StrokeWidth = 0.67.dp

    /** The duration of the new-image animation, in ms (`ThumbImageView.J0`). */
    val TransitionDuration = 350

    /** The duration of the show and hide animations, in ms (`ThumbImageView.b3`). */
    val VisibilityDuration = 200

    /** The rotation speed: ms per 270 degrees (`ThumbImageView.b`). */
    val RotationMillisPer270 = 1000

    /** The scale of a hidden thumbnail (`ThumbImageView.b3`). */
    val HiddenScale = 0.8f

    /** The alpha while pressed (`COUIPressFeedbackHelper`, type 0). */
    val PressedAlpha = 0.5f

    /** The alpha of a disabled thumbnail (`TwoStateImageView.setEnabled`). */
    val DisabledAlpha = 0.4f

    /**
     * The fixed camera palette: the placeholder `thumbnail_bg_normal` (#262626, also
     * `color_thumbnail_background`) and the frame `color_white_with_10_percent_transparency`.
     */
    @Composable
    fun cameraThumbnailColors(
        placeholderColor: Color = Color(0xFF262626),
        strokeColor: Color = Color(0x1AFFFFFF),
    ): CameraThumbnailColors = remember(placeholderColor, strokeColor) {
        CameraThumbnailColors(placeholderColor = placeholderColor, strokeColor = strokeColor)
    }
}

/**
 * The colors of a [CameraThumbnail].
 *
 * @param placeholderColor The fill shown without an image.
 * @param strokeColor The frame.
 */
@Immutable
data class CameraThumbnailColors(
    val placeholderColor: Color,
    val strokeColor: Color,
)
