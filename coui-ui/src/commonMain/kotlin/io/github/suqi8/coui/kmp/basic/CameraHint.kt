// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.effect.MaterialBlurBlendMode
import io.github.suqi8.coui.kmp.effect.MaterialBlurProvider
import io.github.suqi8.coui.kmp.effect.MaterialBlurSpec

/**
 * The background of a [CameraHint], the `bgDrawable` of the camera's `CameraScreenHintInfo`.
 */
enum class CameraHintBackground {
    /** A 50% black rounded rect (`screen_hint_textview_bg`), the default. */
    Dark,

    /** A 20% white rounded rect (`screen_hint_textview_white_bg`). */
    Light,

    /** No background; the text casts a soft 70% black shadow instead (`bgDrawable` 0). */
    None,
}

/**
 * The floating hint of the camera, the port of `CameraScreenHintView` with its `HintTextView`:
 * a short white line such as "Hold the shutter for burst" over the viewfinder, on a small
 * rounded panel (4dp corners) 24dp high, 6dp of horizontal padding and 12dp text. Text that wraps
 * (wider than [maxWidth] or holding a line break) grows the panel with 4dp of vertical padding
 * and 4dp of extra line spacing, centered.
 *
 * Showing (and changing [text] while shown) fades the hint in over 250ms on (0.33, 0, 0.67, 1)
 * after [showDelayMillis]; hiding fades it out in 100ms on the same curve. A fully hidden hint
 * takes no space. The camera hides most hints [CameraHintDefaults.AutoHideMillis] after they
 * appear: drive [visible] from a timer to do the same.
 *
 * With [materialBlur], [CameraHintBackground.Dark] and [CameraHintBackground.Light] both become
 * the camera's material blur (80dp blur, glow overlay of a 15% grey over a 60% mix of 30% grey)
 * under a 20% light grey fill, as on devices that blur their hints.
 *
 * @param text The hint.
 * @param modifier The modifier to be applied to the hint.
 * @param visible Whether the hint is shown.
 * @param background The panel behind the text.
 * @param showDelayMillis The delay before the hint fades in (`startOffset`).
 * @param materialBlur Paints the camera material blur behind the panel. Pass a provider recording
 *   the content under the hint (for example the camera preview); null keeps the flat fill. Ignored
 *   for [CameraHintBackground.None].
 * @param maxWidth The width past which the text wraps (`camera_up_hint_text_default_max_width`).
 * @param textSize The size of the text, in dp like the camera's dimension (not scaled with the
 *   font scale).
 * @param colors The colors of the hint, see [CameraHintDefaults.cameraHintColors].
 */
@Composable
fun CameraHint(
    text: String,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    background: CameraHintBackground = CameraHintBackground.Dark,
    showDelayMillis: Int = 0,
    materialBlur: MaterialBlurProvider? = null,
    maxWidth: Dp = CameraHintDefaults.MaxWidth,
    textSize: Dp = CameraHintDefaults.TextSize,
    colors: CameraHintColors = CameraHintDefaults.cameraHintColors(),
) {
    val alpha = remember { Animatable(if (visible) 1f else 0f) }
    var shownText by remember { mutableStateOf(text) }
    LaunchedEffect(visible, text) {
        if (visible) {
            if (text != shownText) {
                // CameraScreenHintView.showCameraScreenHintText: a new text starts again from 0.
                alpha.snapTo(0f)
                shownText = text
            }
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = CameraHintDefaults.ShowDuration,
                    delayMillis = showDelayMillis,
                    easing = HintStandardEase,
                ),
            )
        } else {
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = CameraHintDefaults.HideDuration, easing = HintStandardEase),
            )
        }
    }
    val gone by remember { derivedStateOf { alpha.value == 0f } }
    if (!visible && gone) return

    val density = LocalDensity.current
    val shape = remember { RoundedCornerShape(CameraHintDefaults.CornerRadius) }
    val blurSpec = remember(density, colors) {
        MaterialBlurSpec(
            blurRadiusPx = with(density) { CameraHintDefaults.BlurRadius.toPx() },
            mixColor = colors.blurMixColor,
            blendColor = colors.blurBlendColor,
            blendMode = MaterialBlurBlendMode.GlowOverlay,
        )
    }
    val blurModifier = if (background != CameraHintBackground.None) materialBlur?.materialBlur(blurSpec, shape) else null
    val panel = when {
        background == CameraHintBackground.None -> Modifier

        // HintTextView.onDraw: the blur drawable under a 20% hint_blur_mix_light_gray round rect.
        blurModifier != null -> blurModifier.background(colors.blurFillColor, shape)

        background == CameraHintBackground.Dark -> Modifier.background(colors.darkContainerColor, shape)

        else -> Modifier.background(colors.lightContainerColor, shape)
    }
    val style = remember(density, textSize, colors, background) {
        cameraTextStyle(density, textSize, FontWeight.Normal).merge(
            TextStyle(
                color = colors.textColor,
                textAlign = TextAlign.Center,
                lineHeight = with(density) {
                    (textSize * NATURAL_LINE_HEIGHT_RATIO + CameraHintDefaults.LineSpacingExtra).toSp()
                },
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both,
                ),
                shadow = if (background == CameraHintBackground.None) {
                    Shadow(color = colors.textShadowColor, offset = Offset.Zero, blurRadius = TEXT_SHADOW_RADIUS)
                } else {
                    null
                },
            ),
        )
    }
    Box(
        modifier = modifier
            .graphicsLayer { this.alpha = alpha.value }
            .widthIn(max = maxWidth)
            .heightIn(min = CameraHintDefaults.MinHeight)
            .then(panel)
            .padding(
                horizontal = CameraHintDefaults.HorizontalPadding,
                vertical = CameraHintDefaults.VerticalPadding,
            ),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(text = shownText, style = style)
    }
}

/**
 * Text drawn straight over the viewfinder, the port of the camera's `StrokeTextView`: the text is
 * drawn twice, first as a thin dark outline ([strokeWidth] 0.333dp of 40% black, round caps) and
 * then filled on top, so it stays readable over bright scenes.
 *
 * [brush] fills the text with a gradient instead of [color], as `GradientColorGuideTextView` does
 * with a horizontal gradient over the text width (see [CameraHintDefaults.GuideGradientColors]).
 *
 * @param text The text.
 * @param modifier The modifier to be applied to the text.
 * @param color The fill color.
 * @param brush A fill brush overriding [color], or null.
 * @param textSize The size of the text, in dp like the camera's dimensions (not scaled with the
 *   font scale).
 * @param fontWeight The weight of the text.
 * @param textAlign The alignment of the lines.
 * @param maxLines The maximum number of lines; the last one ends with an ellipsis.
 * @param strokeWidth The width of the outline; 0dp draws none.
 * @param strokeColor The color of the outline.
 */
@Composable
fun CameraOutlinedText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    brush: Brush? = null,
    textSize: Dp = CameraHintDefaults.OutlinedTextSize,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Center,
    maxLines: Int = Int.MAX_VALUE,
    strokeWidth: Dp = CameraHintDefaults.TextStrokeWidth,
    strokeColor: Color = CameraHintDefaults.TextStrokeColor,
) {
    val density = LocalDensity.current
    val base = remember(density, textSize, fontWeight, textAlign) {
        cameraTextStyle(density, textSize, fontWeight).merge(TextStyle(textAlign = textAlign))
    }
    val fill = remember(base, color, brush) {
        if (brush != null) base.merge(TextStyle(brush = brush)) else base.merge(TextStyle(color = color))
    }
    val outline = remember(base, density, strokeWidth, strokeColor) {
        base.merge(
            TextStyle(
                color = strokeColor,
                drawStyle = Stroke(width = with(density) { strokeWidth.toPx() }, cap = StrokeCap.Round),
            ),
        )
    }
    Box(modifier = modifier) {
        if (strokeWidth > 0.dp) {
            BasicText(
                text = text,
                modifier = Modifier.clearAndSetSemantics {},
                style = outline,
                overflow = TextOverflow.Ellipsis,
                maxLines = maxLines,
            )
        }
        BasicText(
            text = text,
            style = fill,
            overflow = TextOverflow.Ellipsis,
            maxLines = maxLines,
        )
    }
}

/**
 * A camera text style: [textSize] taken as dp (the camera's `TextView`s get pixel sizes from dp
 * dimensions) and the system sans serif at [fontWeight] (`TypeFaceUtil`).
 */
private fun cameraTextStyle(density: Density, textSize: Dp, fontWeight: FontWeight): TextStyle = TextStyle(
    fontSize = (textSize.value / density.fontScale).sp,
    fontWeight = fontWeight,
)

/** The natural line height of the system font relative to its size, without font padding. */
private const val NATURAL_LINE_HEIGHT_RATIO = 1.1719f

/** The text shadow radius of a hint without background (`setShadowLayer(4f, …)`, in px). */
private const val TEXT_SHADOW_RADIUS = 4f

/** COUI `b`, the camera's `c9.a.a` (0.33, 0, 0.67, 1). */
private val HintStandardEase = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/**
 * Default values of [CameraHint] and [CameraOutlinedText], from the camera's `camera_hint_*`
 * dimensions and `CameraScreenHintView`.
 */
object CameraHintDefaults {
    /** The text size (`camera_hint_text_size`, xxhdpi). */
    val TextSize = 12.dp

    /** The panel height of a single line (`hint_right_hint_view_height`). */
    val MinHeight = 24.dp

    /** The panel corner radius (`camera_hint_text_radius`). */
    val CornerRadius = 4.dp

    /** The horizontal padding (`camera_hint_text_padding_left` / `_right`). */
    val HorizontalPadding = 6.dp

    /** The vertical padding (`camera_hint_text_padding_top` / `_bottom`). */
    val VerticalPadding = 4.dp

    /** The extra spacing between wrapped lines (`camera_hint_extra_line_spacing`). */
    val LineSpacingExtra = 4.dp

    /** The width past which the text wraps (`camera_up_hint_text_default_max_width`, xxhdpi). */
    val MaxWidth = 322.dp

    /** The material blur radius (`button_blur_radius`). */
    val BlurRadius = 80.dp

    /** The fade-in duration, in ms (`CameraScreenHintView.c`, state 0). */
    val ShowDuration = 250

    /** The fade-out duration, in ms (`CameraScreenHintView.c`, state 2). */
    val HideDuration = 100

    /** How long the camera shows a hint before hiding it, in ms (`CameraScreenHintInfo`). */
    val AutoHideMillis = 3000L

    /** The outline width of [CameraOutlinedText] (`text_view_stroke_width`). */
    val TextStrokeWidth = 0.333.dp

    /** The outline color of [CameraOutlinedText] (`text_view_stroke_color`, 40% black). */
    val TextStrokeColor = Color(0x66000000)

    /** The text size of [CameraOutlinedText] (`effect_style_slide_hint_font_size`). */
    val OutlinedTextSize = 12.dp

    /** The gradient of the guide titles (`lumo_guide_gradient_colors`). */
    val GuideGradientColors: List<Color> = listOf(Color(0xFFECECEC), Color(0xFFE987AB), Color(0xFF6687F9))

    /**
     * The fixed camera palette: white text (`screen_hint_text_color`), the panels
     * `screen_hint_bg_color` #7F000000 and `screen_hint_bg_white_color` #33FFFFFF, the text shadow
     * `color_black_with_70_percent_transparency`, the blur fill `hint_blur_mix_light_gray` #A6A6A6
     * at alpha 51 and the material blur colors of `BlurBackgroundImpl` (dark, progress 0).
     */
    @Composable
    fun cameraHintColors(
        textColor: Color = Color.White,
        darkContainerColor: Color = Color(0x7F000000),
        lightContainerColor: Color = Color(0x33FFFFFF),
        textShadowColor: Color = Color(0xB3000000),
        blurFillColor: Color = Color(0x33A6A6A6),
        blurMixColor: Color = Color(red = 0.302f, green = 0.302f, blue = 0.302f, alpha = 0.6f),
        blurBlendColor: Color = Color(red = 0.15f, green = 0.15f, blue = 0.15f, alpha = 0.15f),
    ): CameraHintColors = remember(
        textColor,
        darkContainerColor,
        lightContainerColor,
        textShadowColor,
        blurFillColor,
        blurMixColor,
        blurBlendColor,
    ) {
        CameraHintColors(
            textColor = textColor,
            darkContainerColor = darkContainerColor,
            lightContainerColor = lightContainerColor,
            textShadowColor = textShadowColor,
            blurFillColor = blurFillColor,
            blurMixColor = blurMixColor,
            blurBlendColor = blurBlendColor,
        )
    }
}

/**
 * The colors of a [CameraHint].
 *
 * @param textColor The text.
 * @param darkContainerColor The [CameraHintBackground.Dark] panel.
 * @param lightContainerColor The [CameraHintBackground.Light] panel.
 * @param textShadowColor The text shadow of [CameraHintBackground.None].
 * @param blurFillColor The fill over the material blur.
 * @param blurMixColor The material blur mix color.
 * @param blurBlendColor The material blur glow-overlay color.
 */
@Immutable
data class CameraHintColors(
    val textColor: Color,
    val darkContainerColor: Color,
    val lightContainerColor: Color,
    val textShadowColor: Color,
    val blurFillColor: Color,
    val blurMixColor: Color,
    val blurBlendColor: Color,
)
