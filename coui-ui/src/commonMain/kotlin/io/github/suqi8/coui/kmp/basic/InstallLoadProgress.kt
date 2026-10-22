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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.anim.folmeSpring
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
 * @param text The label of the [InstallLoadProgress].
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
    textStyle: TextStyle = COUITheme.textStyles.subtitle,
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
        Canvas(modifier = Modifier.matchParentSize()) {
            val fraction = pressFraction.value
            val inset = Offset(size.width * PressInsetFraction * fraction, size.height * PressInsetFraction * fraction)
            val brightness = 1f - (1f - InstallLoadProgressDefaults.PressedBrightness) * fraction
            val progressFill = colors.fillColor(colors.progressColor, enabled, brightness)
            val trackFill = colors.fillColor(colors.trackColor, enabled, brightness)
            drawCapsule(inset, if (filled) progressFill else trackFill)
            if (showsProgress) {
                val (left, right) = progressClip(inset.x, visualProgress, isRtl)
                clipRect(left = left, right = right) {
                    drawCapsule(inset, progressFill)
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

/** Draws the button capsule inset by [inset] on each side; COUI uses a smooth capsule of radius h / 2. */
private fun DrawScope.drawCapsule(inset: Offset, color: Color) {
    val capsule = Size(size.width - inset.x * 2, size.height - inset.y * 2)
    drawRoundRect(
        color = color,
        topLeft = inset,
        size = capsule,
        cornerRadius = CornerRadius(capsule.height / 2),
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

    /** The horizontal padding of the label (Widget.COUI.COUILoadProgress couiInstallPadding). */
    val TextPadding = 6.dp

    /** The lightness factor of the fills while pressed (Widget.COUI.COUILoadProgress.InstallDownload brightness). */
    val PressedBrightness = 0.8f

    /** The width of [style]; [isChineseLocale] = `false` widens the small style like the original. */
    fun width(style: InstallLoadStyle, isChineseLocale: Boolean): Dp = when (style) {
        InstallLoadStyle.Small -> if (isChineseLocale) SmallWidth else SmallWidth + SmallForeignLanguageExtraWidth
        InstallLoadStyle.Large -> LargeWidth
    }

    /** The height of [style]. */
    fun height(style: InstallLoadStyle): Dp = when (style) {
        InstallLoadStyle.Small -> SmallHeight
        InstallLoadStyle.Large -> LargeHeight
    }

    /**
     * The default [InstallLoadProgressColors]: progress = couiColorPrimary, track = couiColorSecondary,
     * disabled = couiBtnDrawableColorDisabled (#14000000 / #26FFFFFF), label = couiColorPrimary on
     * the track and coui_install_load_progress_text_color_in_progress (white) on the fill.
     */
    @Composable
    fun installLoadProgressColors(
        progressColor: Color = COUITheme.colorScheme.primary,
        trackColor: Color = COUITheme.colorScheme.tertiaryContainer,
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
