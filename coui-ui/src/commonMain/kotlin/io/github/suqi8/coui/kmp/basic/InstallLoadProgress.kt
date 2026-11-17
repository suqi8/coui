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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.anim.folmeSpring
import io.github.suqi8.coui.kmp.squircle.drawSquircleRect
import io.github.suqi8.coui.kmp.squircle.isSquircleEnabled
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * The state of an [InstallLoadProgress] (COUILoadProgress `couiState`).
 */
enum class InstallLoadState {
    /** Not started (DEFAULT_UP_OR_DOWN). */
    Idle,

    /** Downloading / installing, showing [InstallLoadProgress]'s progress fill (UPING_OR_DOWNING). */
    Loading,

    /** Paused, drawn exactly like [Loading] (UP_OR_DOWN_WAIT). */
    Paused,

    /** Failed, drawn as a filled accent capsule (UP_OR_DOWN_FAIL). */
    Failed,
}

/**
 * The size style of an [InstallLoadProgress] (COUIInstallLoadProgress `couiStyle`).
 */
enum class InstallLoadStyle {
    /** `defaultSmallRoundButton`: 52 x 28dp (+30dp wide outside Chinese locales), tinted when idle. */
    Small,

    /** `bigRoundButton`: 220 x 44dp, accent-filled when idle. */
    Large,

    /**
     * `LOAD_STYLE_CIRCLE`: a 33dp icon button without a label or a progress fill. A 30dp rounded
     * square (7dp corners) holds a download arrow: accent on the tinted fill when idle, a white
     * pause icon on the accent fill while loading, and a white arrow on the accent fill when
     * paused or failed. The new icon cross-fades in over 340ms, and a press shrinks the square to
     * 90% in 200ms.
     */
    Circle,
}

/**
 * An [InstallLoadProgress] with COUI style.
 *
 * The download / install capsule button of app stores (COUIInstallLoadProgress). While
 * [InstallLoadState.Loading] or [InstallLoadState.Paused], the capsule shows the accent-tinted track
 * with an accent fill that grows from the start edge with [progress]; the part of the label covered
 * by the fill turns white. Progress changes follow a critically damped spring (COUISpringForce
 * bounce 0, response 1.0).
 *
 * Pressing dims the fills (HSL lightness x 0.8), insets the capsule by 5% of its width and height on
 * every side and shrinks the label to 92%, over 200ms on press and 340ms on release with the move
 * ease curve. This feedback is specific to this control: it uses neither the COUI 17 material nor
 * the generic press-scale springs.
 *
 * @param text The label of the [InstallLoadProgress]; the content description of the
 *   [InstallLoadStyle.Circle] style, which draws no label.
 * @param onClick The callback when the [InstallLoadProgress] is clicked.
 * @param modifier The modifier to be applied to the [InstallLoadProgress]. A size set here overrides
 *   the [style] size.
 * @param state The [InstallLoadState] of the [InstallLoadProgress].
 * @param progress The progress between 0.0f and 1.0f, shown in the [InstallLoadState.Loading] and
 *   [InstallLoadState.Paused] states.
 * @param enabled Whether the [InstallLoadProgress] is enabled. A disabled button draws its fills in
 *   [InstallLoadProgressColors.disabledColor] and keeps its label colors, like the original.
 * @param style The [InstallLoadStyle] of the [InstallLoadProgress].
 * @param colors The [InstallLoadProgressColors] of the [InstallLoadProgress].
 * @param textStyle The [TextStyle] of the label.
 * @param interactionSource The [MutableInteractionSource] to be used for the [InstallLoadProgress].
 */
@Composable
fun InstallLoadProgress(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    state: InstallLoadState = InstallLoadState.Idle,
    progress: Float = 0f,
    enabled: Boolean = true,
    style: InstallLoadStyle = InstallLoadStyle.Small,
    colors: InstallLoadProgressColors = InstallLoadProgressDefaults.installLoadProgressColors(),
    textStyle: TextStyle = COUITheme.textStyles.buttonM,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    val pressFraction = remember { Animatable(0f) }
    LaunchedEffect(isPressed, enabled) {
        if (!enabled) {
            pressFraction.snapTo(0f)
        } else if (isPressed) {
            pressFraction.animateTo(1f, tween(PressDurationMillis, easing = MoveEase))
        } else if (pressFraction.value != 0f) {
            pressFraction.animateTo(0f, tween(ReleaseDurationMillis, easing = MoveEase))
        }
    }

    val progressValue = progress.coerceIn(0f, 1f)
    val visualProgress by animateFloatAsState(targetValue = progressValue, animationSpec = InstallProgressSpring)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val showsProgress = state == InstallLoadState.Loading || state == InstallLoadState.Paused
    // The capsule is fully accent-filled when failed, and when idle in the large style.
    val filled = state == InstallLoadState.Failed || (state == InstallLoadState.Idle && style == InstallLoadStyle.Large)
    val isZh = Locale.current.language.equals("zh", ignoreCase = true)
    val width = InstallLoadProgressDefaults.width(style, isZh)
    val height = InstallLoadProgressDefaults.height(style)

    if (style == InstallLoadStyle.Circle) {
        InstallLoadCircle(
            text = text,
            onClick = onClick,
            modifier = modifier,
            state = state,
            enabled = enabled,
            colors = colors,
            source = source,
            pressFraction = { pressFraction.value },
        )
        return
    }

    Box(
        modifier = modifier
            .size(width = width, height = height)
            .semantics {
                if (showsProgress) progressBarRangeInfo = ProgressBarRangeInfo(progressValue, 0f..1f)
            }
            .clickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        val squircleEnabled = isSquircleEnabled()
        Canvas(modifier = Modifier.matchParentSize()) {
            val fraction = pressFraction.value
            val inset = Offset(size.width * PressInsetFraction * fraction, size.height * PressInsetFraction * fraction)
            val brightness = 1f - (1f - InstallLoadProgressDefaults.PressedBrightness) * fraction
            val progressFill = colors.fillColor(colors.progressColor, enabled, brightness)
            val trackFill = colors.fillColor(colors.trackColor, enabled, brightness)
            drawCapsule(inset, if (filled) progressFill else trackFill, squircleEnabled)
            if (showsProgress) {
                val (left, right) = progressClip(inset.x, visualProgress, isRtl)
                clipRect(left = left, right = right) {
                    drawCapsule(inset, progressFill, squircleEnabled)
                }
            }
        }
        val labelModifier = Modifier
            .padding(horizontal = InstallLoadProgressDefaults.TextPadding)
            .graphicsLayer {
                val scale = 1f - (1f - PressedTextScale) * pressFraction.value
                scaleX = scale
                scaleY = scale
            }
        Text(
            text = text,
            modifier = labelModifier,
            color = if (filled) colors.onProgressContentColor else colors.contentColor,
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (showsProgress) {
            // The label is drawn again in white, clipped to the progress fill (onDrawText with
            // mIsChangeTextColor), so the covered part of the text reads on the accent fill.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .drawWithContent {
                        val inset = size.width * PressInsetFraction * pressFraction.value
                        val (left, right) = progressClip(inset, visualProgress, isRtl)
                        clipRect(left = left, right = right) {
                            this@drawWithContent.drawContent()
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = text,
                    modifier = labelModifier,
                    color = colors.onProgressContentColor,
                    style = textStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/**
 * The [InstallLoadStyle.Circle] button (`onDrawCircle`): the rounded square clipped from
 * `mRoundRectPath` filled with the state color, and the state icon cross-faded in
 * (`CIRCLE_IN_ALPHA_HOLDER` / `CIRCLE_OUT_ALPHA_HOLDER`).
 */
@Composable
private fun InstallLoadCircle(
    text: String,
    onClick: () -> Unit,
    state: InstallLoadState,
    enabled: Boolean,
    colors: InstallLoadProgressColors,
    source: MutableInteractionSource,
    pressFraction: () -> Float,
    modifier: Modifier = Modifier,
) {
    val icon = if (state == InstallLoadState.Loading) CircleIcon.Pause else CircleIcon.Arrow
    var previousIcon by remember { mutableStateOf(icon) }
    val iconFade = remember { Animatable(1f) }
    LaunchedEffect(icon) {
        if (icon == previousIcon) return@LaunchedEffect
        iconFade.snapTo(0f)
        iconFade.animateTo(1f, tween(ReleaseDurationMillis, easing = MoveEase))
        previousIcon = icon
    }
    // onDrawCircle: the accent fill except when idle, which uses the tinted fill.
    val accentFill = state != InstallLoadState.Idle
    val arrowPath = remember { circleIconPath(CircleArrowPathData) }
    val pausePath = remember { circleIconPath(CirclePausePathData) }
    val squircleEnabled = isSquircleEnabled()

    Canvas(
        modifier = modifier
            .size(InstallLoadProgressDefaults.CircleSize)
            .semantics { contentDescription = text }
            .clickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        val fraction = pressFraction()
        val brightness = 1f - (1f - InstallLoadProgressDefaults.PressedBrightness) * fraction
        // getDefaultSize(16.5dp, 1.5dp): a 15dp half side, pressed to 90%.
        val halfSide = InstallLoadProgressDefaults.CircleRadius.toPx() * (1f - (1f - CirclePressedScale) * fraction)
        val fill = colors.fillColor(if (accentFill) colors.progressColor else colors.trackColor, enabled, brightness)
        drawSquircleRect(
            color = fill,
            cornerRadius = InstallLoadProgressDefaults.CircleCornerRadius.toPx(),
            topLeft = Offset(center.x - halfSide, center.y - halfSide),
            size = Size(halfSide * 2f, halfSide * 2f),
            weight = COUIRoundCorner.UnspecifiedWeight,
            squircleEnabled = squircleEnabled,
        )
        val iconColor = if (accentFill) colors.onProgressContentColor else colors.contentColor
        val fade = iconFade.value
        if (fade < 1f) {
            drawCircleIcon(if (previousIcon == CircleIcon.Pause) pausePath else arrowPath, iconColor, 1f - fade)
        }
        drawCircleIcon(if (icon == CircleIcon.Pause) pausePath else arrowPath, iconColor, fade)
    }
}

private enum class CircleIcon { Arrow, Pause }

/** Draws a 36 x 42 viewport icon as a 12 x 14dp glyph centered in the button. */
private fun DrawScope.drawCircleIcon(path: Path, color: Color, alpha: Float) {
    if (alpha <= 0f) return
    val width = CircleIconWidth.toPx()
    val height = CircleIconHeight.toPx()
    translate(left = center.x - width / 2f, top = center.y - height / 2f) {
        scale(scaleX = width / CircleIconViewportWidth, scaleY = height / CircleIconViewportHeight, pivot = Offset.Zero) {
            drawPath(path = path, color = color, alpha = alpha)
        }
    }
}

private fun circleIconPath(pathData: String): Path = PathParser().parsePathString(pathData).toPath().apply {
    fillType = PathFillType.EvenOdd
}

/** coui_install_load_progress_circle_load / _reload: the download arrow. */
private val CircleArrowPathData =
    "M 20 34.2 L 31.6 22.5 C 32.4 21.7 33.6 21.7 34.4 22.5 C 35.2 23.3 35.2 24.5 34.4 25.3 L 20.8 39.1 " +
        "C 19.2 40.7 16.7 40.7 15.1 39.1 L 1.6 25.5 C 0.8 24.7 0.8 23.5 1.6 22.7 C 2.4 21.9 3.6 21.9 4.4 22.7 " +
        "L 16 34.2 L 16 4 C 16 2.9 16.9 2 18 2 C 19.1 2 20 2.9 20 4 L 20 34.2 Z"

/** coui_install_load_progress_circle_pause: two bars. */
private val CirclePausePathData =
    "M 12 8 L 12 34 C 12 35.1 11.1 36 10 36 C 8.9 36 8 35.1 8 34 L 8 8 C 8 6.9 8.9 6 10 6 C 11.1 6 12 6.9 12 8 Z " +
        "M 28 8 L 28 34 C 28 35.1 27.1 36 26 36 C 24.9 36 24 35.1 24 34 L 24 8 C 24 6.9 24.9 6 26 6 C 27.1 6 28 6.9 28 8 Z"

/** The circle icons' vector size (12 x 14dp) and viewport (36 x 42). */
private val CircleIconWidth = 12.dp
private val CircleIconHeight = 14.dp
private val CircleIconViewportWidth = 36f
private val CircleIconViewportHeight = 42f

/** COUIInstallLoadProgress: the pressed circle radius is `mDefaultCircleRadius * 0.9`. */
private val CirclePressedScale = 0.9f

/**
 * Draws the button capsule inset by [inset] on each side. COUIInstallLoadProgress adds a smooth round
 * rect of radius h / 2 without a weight, which ColorOS 17 draws as the soft capsule.
 */
private fun DrawScope.drawCapsule(inset: Offset, color: Color, squircleEnabled: Boolean) {
    val capsule = Size(size.width - inset.x * 2, size.height - inset.y * 2)
    drawSquircleRect(
        color = color,
        cornerRadius = capsule.height / 2,
        topLeft = inset,
        size = capsule,
        weight = COUIRoundCorner.UnspecifiedWeight,
        squircleEnabled = squircleEnabled,
    )
}

/**
 * The horizontal clip of the progress fill: `space = progress * (width - 2 * inset) + inset`,
 * measured from the start edge, or mirrored from the end edge in RTL (COUIInstallLoadProgress.onDraw).
 */
private fun DrawScope.progressClip(inset: Float, progress: Float, isRtl: Boolean): Pair<Float, Float> {
    val space = progress * (size.width - inset * 2) + inset
    return if (isRtl) (size.width - space) to size.width else 0f to space
}

/** COUIMoveEaseInterpolator. */
private val MoveEase = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** COUIInstallLoadProgress PRESS_ANIMATOR_DURATION. */
private val PressDurationMillis = 200

/** COUIInstallLoadProgress NORMAL_ANIMATOR_DURATION. */
private val ReleaseDurationMillis = 340

/** COUIInstallLoadProgress DEFAULT_SCALE_PARAMETER: the press inset per side, as a fraction of the size. */
private val PressInsetFraction = 0.05f

/** COUIInstallLoadProgress DEFAULT_NARROW_FINAL_VALUE: the pressed label scale. */
private val PressedTextScale = 0.92f

/** COUILoadProgress visual progress spring: COUISpringForce(bounce = 0, response = 1.0). */
private val InstallProgressSpring = folmeSpring(damping = 1f, response = 1f, visibilityThreshold = 0.001f)

/** Contains default values used by [InstallLoadProgress]. */
object InstallLoadProgressDefaults {

    /** The width of the [InstallLoadStyle.Small] style (COUI coui_install_download_progress_width). */
    val SmallWidth = 52.dp

    /**
     * The extra width of the [InstallLoadStyle.Small] style outside Chinese locales
     * (COUI coui_install_download_progress_width_in_foreign_language).
     */
    val SmallForeignLanguageExtraWidth = 30.dp

    /** The height of the [InstallLoadStyle.Small] style (COUI coui_install_download_progress_height). */
    val SmallHeight = 28.dp

    /** The width of the [InstallLoadStyle.Large] style (COUI coui_install_download_progress_width_large). */
    val LargeWidth = 220.dp

    /** The height of the [InstallLoadStyle.Large] style (COUI coui_install_download_progress_height_large). */
    val LargeHeight = 44.dp

    /** The size of the [InstallLoadStyle.Circle] style (COUI coui_install_download_progress_circle_width / _height). */
    val CircleSize = 33.dp

    /**
     * The half side of the [InstallLoadStyle.Circle] square: coui_install_download_progress_default_circle_radius
     * (16.5dp) less 1.5dp (`getDefaultSize`).
     */
    val CircleRadius = 15.dp

    /** The corner radius of the [InstallLoadStyle.Circle] square (COUI coui_install_download_progress_circle_round_border_radius). */
    val CircleCornerRadius = 7.dp

    /** The horizontal padding of the label (Widget.COUI.COUILoadProgress couiInstallPadding). */
    val TextPadding = 6.dp

    /** The lightness factor of the fills while pressed (Widget.COUI.COUILoadProgress.InstallDownload brightness). */
    val PressedBrightness = 0.8f

    /** The width of [style]; [isChineseLocale] = `false` widens the small style like the original. */
    fun width(style: InstallLoadStyle, isChineseLocale: Boolean): Dp = when (style) {
        InstallLoadStyle.Small -> if (isChineseLocale) SmallWidth else SmallWidth + SmallForeignLanguageExtraWidth
        InstallLoadStyle.Large -> LargeWidth
        InstallLoadStyle.Circle -> CircleSize
    }

    /** The height of [style]. */
    fun height(style: InstallLoadStyle): Dp = when (style) {
        InstallLoadStyle.Small -> SmallHeight
        InstallLoadStyle.Large -> LargeHeight
        InstallLoadStyle.Circle -> CircleSize
    }

    /**
     * The default [InstallLoadProgressColors]: progress = couiColorPrimary, track = couiColorSecondary,
     * disabled = couiBtnDrawableColorDisabled (#14000000 / #26FFFFFF), label = couiColorPrimary on
     * the track and coui_install_load_progress_text_color_in_progress (white) on the fill.
     */
    @Composable
    fun installLoadProgressColors(
        progressColor: Color = COUITheme.colorScheme.primary,
        trackColor: Color = COUITheme.colorScheme.secondary,
        disabledColor: Color = COUITheme.colorScheme.secondaryVariant,
        contentColor: Color = COUITheme.colorScheme.primary,
        onProgressContentColor: Color = COUITheme.colorScheme.onPrimary,
    ): InstallLoadProgressColors = remember(progressColor, trackColor, disabledColor, contentColor, onProgressContentColor) {
        InstallLoadProgressColors(
            progressColor = progressColor,
            trackColor = trackColor,
            disabledColor = disabledColor,
            contentColor = contentColor,
            onProgressContentColor = onProgressContentColor,
        )
    }
}

/**
 * The colors of an [InstallLoadProgress].
 *
 * @param progressColor The accent fill: the progress fill, and the whole capsule when failed or
 *   idle in the large style.
 * @param trackColor The tinted capsule behind the progress, and the idle small style.
 * @param disabledColor The fill of every capsule layer while disabled.
 * @param contentColor The label color over [trackColor].
 * @param onProgressContentColor The label color over [progressColor].
 */
@Immutable
data class InstallLoadProgressColors(
    val progressColor: Color,
    val trackColor: Color,
    val disabledColor: Color,
    val contentColor: Color,
    val onProgressContentColor: Color,
) {
    /**
     * COUIInstallLoadProgress.getCurrentColor: [disabledColor] when disabled, otherwise [color] with
     * its HSL lightness multiplied by [brightness] and its alpha kept.
     */
    @Stable
    internal fun fillColor(color: Color, enabled: Boolean, brightness: Float): Color = when {
        !enabled -> disabledColor
        brightness >= 1f -> color
        else -> color.scaleLightness(brightness)
    }
}

/** Multiplies the HSL lightness of this color by [factor], keeping hue, saturation and alpha. */
private fun Color.scaleLightness(factor: Float): Color {
    val maxChannel = max(red, max(green, blue))
    val minChannel = min(red, min(green, blue))
    val lightness = (maxChannel + minChannel) / 2f
    val delta = maxChannel - minChannel
    if (delta == 0f) {
        return Color.hsl(0f, 0f, (lightness * factor).coerceIn(0f, 1f), alpha)
    }
    val saturation = delta / (1f - abs(2f * lightness - 1f))
    val hue = when (maxChannel) {
        red -> 60f * (((green - blue) / delta).mod(6f))
        green -> 60f * ((blue - red) / delta + 2f)
        else -> 60f * ((red - green) / delta + 4f)
    }
    return Color.hsl(hue, saturation.coerceIn(0f, 1f), (lightness * factor).coerceIn(0f, 1f), alpha)
}
