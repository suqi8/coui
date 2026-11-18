// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.effect.MaterialBlurBlendMode
import io.github.suqi8.coui.kmp.effect.MaterialBlurProvider
import io.github.suqi8.coui.kmp.effect.MaterialBlurSpec
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.min

/**
 * The round menu button of the camera's bottom effect menu, the port of `BottomMenuIcon`.
 *
 * A [size] disc (54dp, `bottom_menu_item_width`, clipped to a 30dp `bottom_menu_icon_corner_radius`
 * smooth round rect, which is a circle at this size) holding a 24dp icon (the 20dp camera vector
 * scaled 1.2x and centered, `cq.b`). Unselected, the disc is a 10% white fill
 * (`color_menu_grey_bg`) and the icon is white; selected, the disc is the camera accent, painted as
 * the vertical #FF6B00 -> #FF932D gradient of the Hasselblad style, and the icon turns black
 * (`setColorFilter(0xFF000000)`). The camera's edge light (`InnerShadowEffect` type 2 unselected,
 * type 3 selected) draws a light rim inside the disc. The state change is not animated, as in the
 * camera.
 *
 * With [label], the button becomes a bottom menu item (`CameraMenuContainerBottom`): the label sits
 * 4dp below the disc in white 10dp medium text, one line, scrolling when it does not fit.
 *
 * [colorfulBorder] adds the 1dp sweep-gradient ring the camera shows on its AI preset entry
 * (`setColorfulBorderEnable`). The button always uses its fixed light-on-dark palette, since it sits
 * over the camera preview.
 *
 * @param onClick Called when the button is tapped.
 * @param icon The icon, drawn in [CameraMenuButtonColors.iconColor] (black when selected).
 * @param contentDescription The accessibility description of the button.
 * @param modifier The modifier to be applied to the button.
 * @param selected Whether the button is selected (accent disc, black icon).
 * @param enabled Whether the button accepts taps.
 * @param label An optional label below the disc.
 * @param colorfulBorder Whether to draw the colorful sweep-gradient ring.
 * @param edgeLight Whether to draw the camera's edge light (`setShadowVisibility`).
 * @param size The disc size.
 * @param colors The colors of the button, see [CameraMenuButtonDefaults.cameraMenuButtonColors].
 */
@Composable
fun CameraMenuButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    label: String? = null,
    colorfulBorder: Boolean = false,
    edgeLight: Boolean = true,
    size: Dp = CameraMenuButtonDefaults.Size,
    colors: CameraMenuButtonColors = CameraMenuButtonDefaults.cameraMenuButtonColors(),
) {
    val density = LocalDensity.current
    val clickModifier = Modifier
        .then(if (enabled) Modifier else Modifier.graphicsLayer { alpha = CameraMenuButtonDefaults.DisabledAlpha })
        .semantics {
            this.selected = selected
            if (label != null && contentDescription != null) this.contentDescription = contentDescription
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            enabled = enabled,
            role = Role.Button,
            onClick = onClick,
        )
    if (label == null) {
        CameraMenuDisc(
            icon = icon,
            contentDescription = contentDescription,
            selected = selected,
            colorfulBorder = colorfulBorder,
            edgeLight = edgeLight,
            size = size,
            colors = colors,
            modifier = modifier.then(clickModifier),
        )
    } else {
        Column(
            modifier = modifier.then(clickModifier),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CameraMenuDisc(
                icon = icon,
                contentDescription = null,
                selected = selected,
                colorfulBorder = colorfulBorder,
                edgeLight = edgeLight,
                size = size,
                colors = colors,
            )
            val labelStyle = remember(density, colors.labelColor) {
                TextStyle(
                    color = colors.labelColor,
                    fontSize = (CameraMenuButtonDefaults.LabelTextSize.value / density.fontScale).sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
            MarqueeText(
                text = label,
                modifier = Modifier
                    .padding(top = CameraMenuButtonDefaults.LabelSpacing)
                    .width(size),
                color = colors.labelColor,
                style = labelStyle,
            )
        }
    }
}

/**
 * The disc of [CameraMenuButton] (`BottomMenuIcon.onDraw`): the fill (`s`), the edge light and the
 * colorful ring (`w`), with the icon in between.
 */
@Composable
private fun CameraMenuDisc(
    icon: ImageVector,
    contentDescription: String?,
    selected: Boolean,
    colorfulBorder: Boolean,
    edgeLight: Boolean,
    size: Dp,
    colors: CameraMenuButtonColors,
    modifier: Modifier = Modifier,
) {
    val shape = remember(size) { RoundedCornerShape(min(CameraMenuButtonDefaults.CornerRadius.value, size.value / 2f).dp) }
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .drawWithCache {
                val selectedBrush = if (colors.selectedGradientTopColor.isSpecified && colors.selectedGradientBottomColor.isSpecified) {
                    Brush.verticalGradient(
                        colors = listOf(colors.selectedGradientTopColor, colors.selectedGradientBottomColor),
                        startY = 0f,
                        endY = CameraMenuButtonDefaults.GradientHeight.toPx(),
                    )
                } else {
                    null
                }
                onDrawBehind {
                    when {
                        !selected -> drawRect(colors.containerColor)
                        selectedBrush != null -> drawRect(selectedBrush)
                        else -> drawRect(colors.selectedContainerColor)
                    }
                }
            }
            .cameraInnerShadow(
                spec = if (selected) CameraInnerShadows.MenuIconSelected else CameraInnerShadows.MenuIcon,
                cornerRadius = CameraMenuButtonDefaults.CornerRadius,
                enabled = edgeLight,
            )
            .then(if (colorfulBorder) Modifier.cameraColorfulBorder() else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(CameraMenuButtonDefaults.IconSize),
            tint = if (selected) colors.selectedIconColor else colors.iconColor,
        )
    }
}

/**
 * The colorful ring of `BottomMenuIcon.setColorfulBorderEnable`: a 1dp stroke
 * (`bottom_menu_border_stroke_width`) along the disc outline, inset by half the stroke, painted
 * with a sweep gradient around the center.
 */
private fun Modifier.cameraColorfulBorder(): Modifier = drawWithCache {
    val stroke = CameraMenuButtonDefaults.ColorfulBorderWidth.toPx()
    val inset = stroke / 2f
    val radius = min(CameraMenuButtonDefaults.CornerRadius.toPx(), min(size.width, size.height) / 2f) - inset
    val brush = Brush.sweepGradient(
        *COLORFUL_BORDER_STOPS,
        center = Offset(size.width / 2f, size.height / 2f),
    )
    onDrawWithContent {
        drawContent()
        drawRoundRect(
            brush = brush,
            topLeft = Offset(inset, inset),
            size = Size(size.width - stroke, size.height - stroke),
            cornerRadius = CornerRadius(radius.coerceAtLeast(0f)),
            style = Stroke(width = stroke),
        )
    }
}

/** `BottomMenuIcon.w` / `x`: the colorful ring colors and positions. */
private val COLORFUL_BORDER_STOPS = arrayOf(
    0.1f to Color(0xFF0FA7FF),
    0.24f to Color(0xFF3D8BFF),
    0.36f to Color(0xFFBE5DFF),
    0.54f to Color(0xFFFF5447),
    0.72f to Color(0xFFFFBA19),
    0.9f to Color(0xFF45E555),
    1f to Color(0xFF0FA7FF),
)

/**
 * The small round entry button the camera floats above its shutter row to open an effect panel
 * (beauty, filter, bokeh), the port of `MenuLeftButton` / `MenuRightButton`.
 *
 * A [CameraEffectButtonDefaults.Size] (32dp, `face_beauty_enter_button_width`) disc holding a 24dp
 * white icon. Its background is the camera material blur when [materialBlur] is given (80dp
 * `button_blur_radius`) and otherwise a 50% black fill (`face_beauty_enter_button_bg_selector`). When
 * [selected], the icon fades to the camera accent in 320ms (`RotateAnimationView`).
 *
 * Changing [visible] plays the camera's show and hide animations: the button fades in over 200ms
 * while growing from 80% (0.3, 0, 0.1, 1), and fades out over 200ms (0.33, 0, 0.67, 1) while
 * shrinking back to 80%. It stays in the layout while hidden.
 *
 * @param onClick Called when the button is tapped.
 * @param icon The icon.
 * @param contentDescription The accessibility description of the button.
 * @param modifier The modifier to be applied to the button.
 * @param visible Whether the button is shown; changes animate.
 * @param selected Whether the effect is on (accent icon).
 * @param enabled Whether the button accepts taps.
 * @param materialBlur Paints the camera material blur behind the disc. Pass a provider recording the
 *   content under the button; null keeps the 50% black fill.
 * @param colors The colors of the button, see [CameraEffectButtonDefaults.cameraEffectButtonColors].
 */
@Composable
fun CameraEffectButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    selected: Boolean = false,
    enabled: Boolean = true,
    materialBlur: MaterialBlurProvider? = null,
    colors: CameraEffectButtonColors = CameraEffectButtonDefaults.cameraEffectButtonColors(),
) {
    val density = LocalDensity.current
    val shape = remember { RoundedCornerShape(CameraEffectButtonDefaults.Size / 2) }
    val blurSpec = remember(density, colors) {
        MaterialBlurSpec(
            blurRadiusPx = with(density) { CameraEffectButtonDefaults.BlurRadius.toPx() },
            mixColor = colors.blurMixColor,
            blendColor = colors.blurBlendColor,
            blendMode = MaterialBlurBlendMode.GlowOverlay,
        )
    }
    val background = materialBlur?.materialBlur(blurSpec, shape) ?: Modifier.background(colors.containerColor, shape)
    val iconColor by animateColorAsState(
        targetValue = if (selected) colors.selectedIconColor else colors.iconColor,
        animationSpec = tween(durationMillis = 320, easing = CameraButtonMoveEaseEasing),
    )
    val alpha = remember { Animatable(if (visible) 1f else 0f) }
    val scale = remember { Animatable(if (visible) 1f else CameraEffectButtonDefaults.HiddenScale) }
    var shown by remember { mutableStateOf(visible) }
    LaunchedEffect(visible) {
        if (visible) shown = true
        coroutineScope {
            launch {
                alpha.animateTo(
                    targetValue = if (visible) 1f else 0f,
                    animationSpec = tween(200, easing = if (visible) CameraButtonMoveEaseEasing else CameraButtonStandardEasing),
                )
            }
            launch {
                scale.animateTo(
                    targetValue = if (visible) 1f else CameraEffectButtonDefaults.HiddenScale,
                    animationSpec = tween(200, easing = CameraButtonMoveEaseEasing),
                )
            }
        }
        if (!visible) shown = false
    }
    Box(
        modifier = modifier
            .size(CameraEffectButtonDefaults.Size)
            .graphicsLayer {
                this.alpha = alpha.value
                scaleX = scale.value
                scaleY = scale.value
            }
            .then(background)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled && shown,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics { this.selected = selected },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(CameraEffectButtonDefaults.IconSize),
            tint = iconColor,
        )
    }
}

/** `com.coui.appcompat.animation.e`, the COUI move-ease curve (0.3, 0, 0.1, 1). */
private val CameraButtonMoveEaseEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** `com.coui.appcompat.animation.b`, the COUI standard curve (0.33, 0, 0.67, 1). */
private val CameraButtonStandardEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** Default values of [CameraMenuButton], from the camera's `bottom_menu_*` dimensions. */
object CameraMenuButtonDefaults {
    /** The disc size (`bottom_menu_item_width`). */
    val Size = 54.dp

    /** The outline corner radius (`bottom_menu_icon_corner_radius`); a circle at [Size]. */
    val CornerRadius = 30.dp

    /** The icon size: the camera's 20dp vectors scaled 1.2x (`cq.b` image matrix). */
    val IconSize = 24.dp

    /** The height the selected gradient spans (`bottom_menu_item_width`). */
    val GradientHeight = 54.dp

    /** The colorful ring width (`bottom_menu_border_stroke_width`). */
    val ColorfulBorderWidth = 1.dp

    /** The label size (`bottom_menu_item_text_size`), in dp like the camera. */
    val LabelTextSize = 10.dp

    /** The gap between the disc and the label (`bottom_menu_item_text_margin_top`). */
    val LabelSpacing = 4.dp

    /** The alpha of a disabled button (the camera's `ResetImageView` disabled alpha). */
    val DisabledAlpha = 0.3f

    /**
     * The fixed camera palette: the `color_menu_grey_bg` #1AFFFFFF disc, the Hasselblad selected
     * gradient #FF6B00 -> #FF932D (`BottomMenuIcon` constructor), the solid accent
     * `color_primary_light_hasselblad` #FB822A used when either gradient color is unspecified, a
     * white icon turning black when selected, and a white label.
     */
    @Composable
    fun cameraMenuButtonColors(
        containerColor: Color = Color(0x1AFFFFFF),
        selectedContainerColor: Color = Color(0xFFFB822A),
        selectedGradientTopColor: Color = Color(0xFFFF6B00),
        selectedGradientBottomColor: Color = Color(0xFFFF932D),
        iconColor: Color = Color.White,
        selectedIconColor: Color = Color.Black,
        labelColor: Color = Color.White,
    ): CameraMenuButtonColors = remember(
        containerColor,
        selectedContainerColor,
        selectedGradientTopColor,
        selectedGradientBottomColor,
        iconColor,
        selectedIconColor,
        labelColor,
    ) {
        CameraMenuButtonColors(
            containerColor = containerColor,
            selectedContainerColor = selectedContainerColor,
            selectedGradientTopColor = selectedGradientTopColor,
            selectedGradientBottomColor = selectedGradientBottomColor,
            iconColor = iconColor,
            selectedIconColor = selectedIconColor,
            labelColor = labelColor,
        )
    }
}

/**
 * The colors of a [CameraMenuButton].
 *
 * @param containerColor The disc when not selected.
 * @param selectedContainerColor The selected disc when no gradient is given.
 * @param selectedGradientTopColor The top of the selected gradient; unspecified for a solid disc.
 * @param selectedGradientBottomColor The bottom of the selected gradient; unspecified for a solid disc.
 * @param iconColor The icon when not selected.
 * @param selectedIconColor The icon when selected.
 * @param labelColor The label.
 */
@Immutable
data class CameraMenuButtonColors(
    val containerColor: Color,
    val selectedContainerColor: Color,
    val selectedGradientTopColor: Color,
    val selectedGradientBottomColor: Color,
    val iconColor: Color,
    val selectedIconColor: Color,
    val labelColor: Color,
)

/** Default values of [CameraEffectButton], from the camera's enter button resources. */
object CameraEffectButtonDefaults {
    /** The button size (`face_beauty_enter_button_width`). */
    val Size = 32.dp

    /** The icon size (`face_beauty_menu_icon_size`). */
    val IconSize = 24.dp

    /** The material blur radius (`button_blur_radius`). */
    val BlurRadius = 80.dp

    /** The scale the button grows from and shrinks to (`MenuRightButton.Q0` / `w0`). */
    val HiddenScale = 0.8f

    /**
     * The fixed camera palette: the `color_black_with_50_percent_transparency` fill, the material
     * blur colors of `BlurBackgroundImpl` (dark parameters, progress 1: a 15% grey mixed at 60%, a
     * 65% grey glow overlay at 40%), a white icon and the accent `color_primary_light_hasselblad`
     * #FB822A when selected.
     */
    @Composable
    fun cameraEffectButtonColors(
        containerColor: Color = Color(0x7F000000),
        blurMixColor: Color = Color(red = 0.15f, green = 0.15f, blue = 0.15f, alpha = 0.6f),
        blurBlendColor: Color = Color(red = 0.65f, green = 0.65f, blue = 0.65f, alpha = 0.4f),
        iconColor: Color = Color.White,
        selectedIconColor: Color = Color(0xFFFB822A),
    ): CameraEffectButtonColors = remember(containerColor, blurMixColor, blurBlendColor, iconColor, selectedIconColor) {
        CameraEffectButtonColors(
            containerColor = containerColor,
            blurMixColor = blurMixColor,
            blurBlendColor = blurBlendColor,
            iconColor = iconColor,
            selectedIconColor = selectedIconColor,
        )
    }
}

/**
 * The colors of a [CameraEffectButton].
 *
 * @param containerColor The disc without material blur.
 * @param blurMixColor The material blur mix color.
 * @param blurBlendColor The material blur glow-overlay color.
 * @param iconColor The icon when not selected.
 * @param selectedIconColor The icon when selected.
 */
@Immutable
data class CameraEffectButtonColors(
    val containerColor: Color,
    val blurMixColor: Color,
    val blurBlendColor: Color,
    val iconColor: Color,
    val selectedIconColor: Color,
)
