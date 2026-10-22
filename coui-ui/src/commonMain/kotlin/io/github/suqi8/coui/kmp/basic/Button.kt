// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect.contentButtonMaterial
import io.github.suqi8.coui.kmp.effect.ContentButtonMaterialType
import io.github.suqi8.coui.kmp.effect.couiTouchMotion
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.squircle.squircleSurface
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalContentColor
import kotlin.coroutines.cancellation.CancellationException

/**
 * A [Button] component with COUI style.
 *
 * The touch feedback follows COUI 17 `COUIButton` and its `COUITouchMotionAnimator`: the press
 * scale (a target derived from the button's area and aspect ratio, with under-damped release
 * springs), the rubber-band drag-follow translation and area-conserving drag deformation, the
 * `COUIMaskEffectDrawable` press and hover tints, and the [material] layer that ColorOS 17 draws
 * with its system material switches on (the default on real devices).
 *
 * @param onClick The callback when the [Button] is clicked.
 * @param modifier The modifier to be applied to the [Button].
 * @param enabled Whether the [Button] is enabled.
 * @param pressScaleEnabled Whether the [Button] shrinks while pressed (COUIButton `scaleEnable`).
 *   Set to `false` for buttons that fill a container cell. The drag deformation and rubber band
 *   run either way.
 * @param cornerRadius The corner radius of the [Button]. [Dp.Unspecified] (the default) rounds
 *   the button at half its measured height, the COUI capsule (`drawableRadius = -1`); an explicit
 *   radius draws a weight-3.0 smooth round rect (COUI 17 `setSmoothRoundRect(..., 3.0f)`).
 * @param minWidth The minimum width of the [Button].
 * @param minHeight The minimum height of the [Button].
 * @param colors The [ButtonColors] of the [Button].
 * @param insideMargin The margin inside the [Button].
 * @param material The COUI 17 material layer (edge lighting, inner shadow and touch spotlight)
 *   drawn over the fill. [ButtonMaterial.Auto] (the default) picks the preset matching [colors],
 *   as every `ButtonNew` style follows the system material switch; [ButtonMaterial.None] opts
 *   out. While the layer is drawn the press tint is off (`COUIButton.applyBlurEffect` disables the
 *   mask's pressed state).
 * @param interactionSource The [MutableInteractionSource] to be used for the [Button].
 * @param indication The [Indication] to be used for the [Button]. Defaults to `null` because
 *   the COUI press feedback (scale + press tint) is built into the button itself.
 * @param content The [Composable] content of the [Button].
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    pressScaleEnabled: Boolean = true,
    cornerRadius: Dp = ButtonDefaults.CornerRadius,
    minWidth: Dp = ButtonDefaults.MinWidth,
    minHeight: Dp = ButtonDefaults.MinHeight,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    insideMargin: PaddingValues = ButtonDefaults.InsideMargin,
    material: ButtonMaterial = ButtonMaterial.Auto,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    content: @Composable RowScope.() -> Unit,
) {
    CouiButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        pressScaleEnabled = pressScaleEnabled,
        cornerRadius = cornerRadius,
        minWidth = minWidth,
        minHeight = minHeight,
        colors = colors,
        insideMargin = insideMargin,
        materialType = material.toContentType(colors),
        interactionSource = interactionSource,
        indication = indication,
        content = content,
    )
}

/**
 * The [Button] implementation with the material preset already resolved, so that components such
 * as the dialog button bar can pick the `drawableMaterialEffect` presets.
 */
@Composable
internal fun CouiButton(
    onClick: () -> Unit,
    colors: ButtonColors,
    materialType: ContentButtonMaterialType?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    pressScaleEnabled: Boolean = true,
    cornerRadius: Dp = ButtonDefaults.CornerRadius,
    minWidth: Dp = ButtonDefaults.MinWidth,
    minHeight: Dp = ButtonDefaults.MinHeight,
    insideMargin: PaddingValues = ButtonDefaults.InsideMargin,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    content: @Composable RowScope.() -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val rowModifier = remember(minWidth, minHeight, insideMargin) {
        Modifier
            .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
            .padding(insideMargin)
    }
    val containerColor = if (enabled) colors.color else colors.disabledColor
    val contentColor = if (enabled) colors.contentColor else colors.disabledContentColor
    // The material layer needs runtime shaders; without them COUI keeps the plain mask.
    val shadersSupported = remember { isRuntimeShaderSupported() }
    val materialActive = materialType != null && shadersSupported

    // COUIMaskEffectDrawable press tint. COUIButton.applyBlurEffect turns the pressed state off
    // while the material is on.
    val isPressed by interactionSource.collectIsPressedAsState()
    val tintProgress = remember { Animatable(0f) }
    val pressTintEnabled = enabled && !materialActive
    LaunchedEffect(pressTintEnabled, isPressed) {
        if (pressTintEnabled && isPressed) {
            tintProgress.animateTo(1f, PressTintSpring)
        } else if (!pressTintEnabled) {
            tintProgress.snapTo(0f)
        } else if (tintProgress.value > 0f) {
            if (tintProgress.value < PressedTintMinVisibleProgress) {
                // Keep the press-in animation running until the tint is clearly visible.
                try {
                    tintProgress.animateTo(1f, PressTintSpring) {
                        if (value >= PressedTintMinVisibleProgress) throw PressTintThresholdReached()
                    }
                } catch (_: PressTintThresholdReached) {
                    // Minimum visible progress reached; fall through to the fade-out.
                }
            }
            tintProgress.animateTo(0f, PressTintSpring)
        }
    }
    // The hover layer of the same drawable (COUIButton.dispatchHoverEvent), on the same spring.
    val isHovered by interactionSource.collectIsHoveredAsState()
    val hoverProgress = remember { Animatable(0f) }
    LaunchedEffect(enabled, isHovered) {
        hoverProgress.animateTo(if (enabled && isHovered) 1f else 0f, PressTintSpring)
    }
    val isDark = COUITheme.colorScheme.background.luminance() < 0.5f
    val pressTint = if (isDark) PressedTintDark else PressedTintLight
    val hoverTint = if (isDark) HoveredTintDark else HoveredTintLight
    var fillColor = containerColor
    if (hoverProgress.value > 0f) {
        fillColor = hoverTint.copy(alpha = hoverTint.alpha * hoverProgress.value).compositeOver(fillColor)
    }
    if (tintProgress.value > 0f) {
        fillColor = pressTint.copy(alpha = pressTint.alpha * tintProgress.value).compositeOver(fillColor)
    }
    val surfaceModifier = if (cornerRadius == Dp.Unspecified) {
        Modifier.clip(CircleShape).background(fillColor)
    } else {
        Modifier.squircleSurface(color = fillColor, cornerRadius = cornerRadius, weight = COUIRoundCorner.Large.Weight)
    }

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(
            modifier = modifier
                .semantics { role = Role.Button }
                .couiTouchMotion(
                    interactionSource = interactionSource,
                    enabled = enabled,
                    pressScaleEnabled = pressScaleEnabled,
                )
                .then(surfaceModifier)
                .then(
                    if (materialType != null) {
                        Modifier.contentButtonMaterial(materialType, enabled, cornerRadius)
                    } else {
                        Modifier
                    },
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = indication,
                    enabled = enabled,
                    onClick = onClick,
                ),
            propagateMinConstraints = true,
        ) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
}

/**
 * A [TextButton] component with COUI style.
 *
 * @param text The text of the [TextButton].
 * @param onClick The callback when the [TextButton] is clicked.
 * @param modifier The modifier to be applied to the [TextButton].
 * @param enabled Whether the [TextButton] is enabled.
 * @param pressScaleEnabled Whether the [TextButton] shrinks while pressed (COUIButton
 *   `scaleEnable`). Set to `false` for buttons that fill a container cell, leaving the press tint
 *   as the only feedback.
 * @param cornerRadius The corner radius of the [TextButton]. [Dp.Unspecified] (the default)
 *   rounds the button at half its measured height (COUI capsule).
 * @param minWidth The minimum width of the [TextButton].
 * @param minHeight The minimum height of the [TextButton].
 * @param colors The [TextButtonColors] of the [TextButton].
 * @param insideMargin The margin inside the [TextButton].
 * @param textStyle The [TextStyle] of the label. Pass [ButtonDefaults.textStyleSmall] together with
 *   the `Small` metrics from [ButtonDefaults] to get the COUI small size tier.
 * @param maxLines The maximum number of label lines. COUIButton is single-line with an end
 *   ellipsis (`singleLine = true`, `ellipsize = end`).
 * @param material The COUI 17 material layer drawn over the fill, see [Button].
 * @param interactionSource The [MutableInteractionSource] to be used for the [TextButton].
 * @param indication The [Indication] to be used for the [TextButton]. Defaults to `null` because
 *   the COUI press feedback (scale + press tint) is built into the button itself.
 */
@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    pressScaleEnabled: Boolean = true,
    cornerRadius: Dp = ButtonDefaults.CornerRadius,
    minWidth: Dp = ButtonDefaults.MinWidth,
    minHeight: Dp = ButtonDefaults.MinHeight,
    colors: TextButtonColors = ButtonDefaults.textButtonColors(),
    insideMargin: PaddingValues = ButtonDefaults.InsideMargin,
    textStyle: TextStyle = COUITheme.textStyles.button,
    maxLines: Int = 1,
    material: ButtonMaterial = ButtonMaterial.Auto,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
) {
    val mappedColors = remember(colors) {
        ButtonColors(
            color = colors.color,
            disabledColor = colors.disabledColor,
            contentColor = colors.textColor,
            disabledContentColor = colors.disabledTextColor,
        )
    }
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        pressScaleEnabled = pressScaleEnabled,
        cornerRadius = cornerRadius,
        minWidth = minWidth,
        minHeight = minHeight,
        colors = mappedColors,
        insideMargin = insideMargin,
        material = material,
        interactionSource = interactionSource,
        indication = indication,
    ) {
        Text(
            text = text,
            style = textStyle,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * The COUI 17 material layer of a [Button], matching the `COUIButton` `buttonType` it is drawn
 * for. Only the edge lighting + inner shadow (`COUIMaterialStrokeEffect` `CONTENT_CAPSULE_*`) and
 * the touch spotlight (`COUISpotLightEffect`) are drawn; they need runtime shaders (Android 13+
 * and every Skia target) and are skipped elsewhere.
 */
enum class ButtonMaterial {
    /**
     * The preset matching the button's fill, the COUI 17 default (`useMaterialEffect` is
     * `followSystem` for every `ButtonNew` style): [Primary] for a [COUITheme] primary fill,
     * [Transparent] for the transparent-button fill, none for a fully transparent (borderless)
     * fill, and [Secondary] for everything else.
     */
    Auto,

    /** No material layer: the solid fill, press tint and touch motion only (`forceDisable`). */
    None,

    /** The primary (theme-filled) button presets: `CONTENT_CAPSULE_1`, `TYPE_OPAQUE_MEDIUM_1`. */
    Primary,

    /** The secondary (grey) button presets: `CONTENT_CAPSULE_2`, `TYPE_OPAQUE_MEDIUM_2`. */
    Secondary,

    /** The transparent button presets: `CONTENT_CAPSULE_3`, `TYPE_TRANSPARENT_MEDIUM_1`. */
    Transparent,
}

@Composable
private fun ButtonMaterial.toContentType(colors: ButtonColors): ContentButtonMaterialType? = when (this) {
    ButtonMaterial.None -> null

    ButtonMaterial.Primary -> ContentButtonMaterialType.Primary

    ButtonMaterial.Secondary -> ContentButtonMaterialType.Secondary

    ButtonMaterial.Transparent -> ContentButtonMaterialType.Transparent

    ButtonMaterial.Auto -> {
        val scheme = COUITheme.colorScheme
        when {
            // Borderless buttons have no buttonType (UNKNOWN) and never draw the material.
            colors.color.alpha == 0f -> null

            colors.color == scheme.primary -> ContentButtonMaterialType.Primary

            colors.color == scheme.transparentButton -> ContentButtonMaterialType.Transparent

            else -> ContentButtonMaterialType.Secondary
        }
    }
}

object ButtonDefaults {

    /**
     * The legacy COUI 16 press end scale. Retained for source compatibility only and no longer
     * used by [Button], whose COUI 17 target scale is derived from its size (see
     * [io.github.suqi8.coui.kmp.effect.COUIMaterialEffect.pressTargetScale]).
     */
    val PressedScale = 0.92f

    /**
     * The legacy COUIButton `brightness` attribute value. Retained for source compatibility only
     * and no longer used by [Button].
     */
    val PressedBrightness = 0.8f

    /**
     * The default min width applied for all buttons (COUI `coui_btn_large_width_min`, 132dp). Note
     * that you can override it by applying Modifier.widthIn directly on the button composable.
     */
    val MinWidth = 132.dp

    /**
     * The default min height applied for all buttons (COUI `coui_btn_large_height_min`, 44dp).
     * Note that you can override it by applying Modifier.heightIn directly on the button
     * composable.
     */
    val MinHeight = 44.dp

    /**
     * The default corner radius applied for all buttons: [Dp.Unspecified], which rounds the button
     * at half its measured height (COUI `drawableRadius = -1` capsule).
     */
    val CornerRadius = Dp.Unspecified

    /**
     * The default inside margin applied for all buttons (COUI `Widget.COUI.Button.Large` padding:
     * 12dp horizontal, 8dp vertical; the vertical padding only shows once the label outgrows the
     * 44dp min height).
     */
    val InsideMargin = PaddingValues(horizontal = 12.dp, vertical = 8.dp)

    /**
     * The min width of the small size tier (COUI coui_btn_small_width_min).
     */
    val MinWidthSmall = 52.dp

    /**
     * The min height of the small size tier (COUI coui_btn_small_height_min).
     */
    val MinHeightSmall = 28.dp

    /**
     * The corner radius of the small size tier: [Dp.Unspecified], the height-derived capsule.
     */
    val CornerRadiusSmall = Dp.Unspecified

    /**
     * The inside margin of the small size tier (COUI Widget.COUI.Button.Small padding 12dp/4dp).
     */
    val InsideMarginSmall = PaddingValues(horizontal = 12.dp, vertical = 4.dp)

    /**
     * The label style of the small size tier (COUI `Widget.COUI.Button.Small`: 14sp,
     * sans-serif-medium).
     */
    @Composable
    fun textStyleSmall(): TextStyle = COUITheme.textStyles.button.copy(fontSize = 14.sp)

    /**
     * The default [ButtonColors] for all buttons: the COUI 17 secondary button
     * (`Widget.COUI.Button.Large.ButtonNew.Secondary`).
     */
    @Composable
    fun buttonColors(
        color: Color = COUITheme.colorScheme.secondaryButton,
        disabledColor: Color = COUITheme.colorScheme.disabledSecondaryButton,
        contentColor: Color = COUITheme.colorScheme.onSecondaryVariant,
        disabledContentColor: Color = COUITheme.colorScheme.disabledOnSecondaryButton,
    ): ButtonColors = remember(color, disabledColor, contentColor, disabledContentColor) {
        ButtonColors(
            color = color,
            disabledColor = disabledColor,
            contentColor = contentColor,
            disabledContentColor = disabledContentColor,
        )
    }

    /**
     * The [ButtonColors] for primary buttons.
     */
    @Composable
    fun buttonColorsPrimary(
        color: Color = COUITheme.colorScheme.primary,
        disabledColor: Color = COUITheme.colorScheme.disabledPrimaryButton,
        contentColor: Color = COUITheme.colorScheme.onPrimary,
        disabledContentColor: Color = COUITheme.colorScheme.disabledOnPrimaryButton,
    ): ButtonColors = remember(color, disabledColor, contentColor, disabledContentColor) {
        ButtonColors(
            color = color,
            disabledColor = disabledColor,
            contentColor = contentColor,
            disabledContentColor = disabledContentColor,
        )
    }

    /**
     * The [ButtonColors] for transparent buttons laid over images or colored backdrops (COUI
     * `Widget.COUI.Button.Small.ButtonNew.Translate`): a translucent white fill and white label in
     * both schemes.
     */
    @Composable
    fun buttonColorsTransparent(
        color: Color = COUITheme.colorScheme.transparentButton,
        disabledColor: Color = COUITheme.colorScheme.transparentButton,
        contentColor: Color = COUITheme.colorScheme.onTransparentButton,
        disabledContentColor: Color = COUITheme.colorScheme.disabledOnTransparentButton,
    ): ButtonColors = remember(color, disabledColor, contentColor, disabledContentColor) {
        ButtonColors(
            color = color,
            disabledColor = disabledColor,
            contentColor = contentColor,
            disabledContentColor = disabledContentColor,
        )
    }

    /**
     * The default [TextButtonColors] for all text buttons: the COUI 17 secondary button.
     */
    @Composable
    fun textButtonColors(
        color: Color = COUITheme.colorScheme.secondaryButton,
        disabledColor: Color = COUITheme.colorScheme.disabledSecondaryButton,
        textColor: Color = COUITheme.colorScheme.onSecondaryVariant,
        disabledTextColor: Color = COUITheme.colorScheme.disabledOnSecondaryButton,
    ): TextButtonColors = remember(color, disabledColor, textColor, disabledTextColor) {
        TextButtonColors(
            color = color,
            disabledColor = disabledColor,
            textColor = textColor,
            disabledTextColor = disabledTextColor,
        )
    }

    /**
     * The [TextButtonColors] for primary text buttons.
     */
    @Composable
    fun textButtonColorsPrimary(
        color: Color = COUITheme.colorScheme.primary,
        disabledColor: Color = COUITheme.colorScheme.disabledPrimaryButton,
        textColor: Color = COUITheme.colorScheme.onPrimary,
        disabledTextColor: Color = COUITheme.colorScheme.disabledOnPrimaryButton,
    ): TextButtonColors = remember(color, disabledColor, textColor, disabledTextColor) {
        TextButtonColors(
            color = color,
            disabledColor = disabledColor,
            textColor = textColor,
            disabledTextColor = disabledTextColor,
        )
    }

    /**
     * The [TextButtonColors] for transparent text buttons, see [buttonColorsTransparent].
     */
    @Composable
    fun textButtonColorsTransparent(
        color: Color = COUITheme.colorScheme.transparentButton,
        disabledColor: Color = COUITheme.colorScheme.transparentButton,
        textColor: Color = COUITheme.colorScheme.onTransparentButton,
        disabledTextColor: Color = COUITheme.colorScheme.disabledOnTransparentButton,
    ): TextButtonColors = remember(color, disabledColor, textColor, disabledTextColor) {
        TextButtonColors(
            color = color,
            disabledColor = disabledColor,
            textColor = textColor,
            disabledTextColor = disabledTextColor,
        )
    }

    /**
     * The [TextButtonColors] for borderless text buttons: no fill, primary-tinted label.
     */
    @Composable
    fun textButtonColorsBorderless(
        color: Color = Color.Transparent,
        disabledColor: Color = Color.Transparent,
        textColor: Color = COUITheme.colorScheme.primary,
        disabledTextColor: Color = COUITheme.colorScheme.disabledPrimary,
    ): TextButtonColors = remember(color, disabledColor, textColor, disabledTextColor) {
        TextButtonColors(
            color = color,
            disabledColor = disabledColor,
            textColor = textColor,
            disabledTextColor = disabledTextColor,
        )
    }
}

@Immutable
data class ButtonColors(
    val color: Color,
    val disabledColor: Color,
    val contentColor: Color,
    val disabledContentColor: Color,
)

@Immutable
data class TextButtonColors(
    val color: Color,
    val disabledColor: Color,
    val textColor: Color,
    val disabledTextColor: Color,
)

/** The COUIMaskEffectDrawable press tint spring (COUISpringForce response 0.3f / bounce 0f). */
private val PressTintSpring = spring<Float>(dampingRatio = 1f, stiffness = 438.65f)

/** couiColorPress in light themes (coui_color_press #1F000000). */
private val PressedTintLight = Color(0x1F000000)

/** couiColorPress in dark themes (coui_color_press_dark #33FFFFFF). */
private val PressedTintDark = Color(0x33FFFFFF)

/** couiColorHover in light themes (coui_color_hover #14000000). */
private val HoveredTintLight = Color(0x14000000)

/** couiColorHover in dark themes (coui_color_hover_dark #26FFFFFF). */
private val HoveredTintDark = Color(0x26FFFFFF)

/** The minimum press tint progress before a release may fade out (COUIMaskEffectDrawable). */
private val PressedTintMinVisibleProgress = 0.7f

/** Thrown to stop the deferred press-in tint animation once [PressedTintMinVisibleProgress] is reached. */
private class PressTintThresholdReached : CancellationException("Press tint reached the minimum visible progress")
