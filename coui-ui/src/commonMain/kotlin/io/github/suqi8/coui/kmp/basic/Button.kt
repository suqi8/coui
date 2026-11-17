// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalWindowInfo
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
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.couiFocusStroke
import io.github.suqi8.coui.kmp.effect.couiTouchMotion
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.squircle.SquircleShape
import io.github.suqi8.coui.kmp.squircle.squircleSurface
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalContentColor
import io.github.suqi8.coui.kmp.utils.COUILayoutGrid
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
 *   the button at half its measured height, the COUI capsule (`drawableRadius = -1`) drawn as the
 *   weight-1.0 Oplus soft capsule; an explicit radius draws a weight-3.0 smooth round rect (COUI 17
 *   `setSmoothRoundRect(..., 3.0f)`).
 * @param minWidth The minimum width of the [Button].
 * @param minHeight The minimum height of the [Button].
 * @param colors The [ButtonColors] of the [Button].
 * @param insideMargin The margin inside the [Button].
 * @param material The COUI 17 material layer (edge lighting, inner shadow and touch spotlight)
 *   drawn over the fill. [ButtonMaterial.Auto] (the default) picks the preset matching [colors],
 *   as every `ButtonNew` style follows the system material switch, and draws none while
 *   [LocalCOUIMaterialEffectEnabled] is `false`; [ButtonMaterial.None] opts out. While the layer is
 *   drawn the press tint is off (`COUIButton.applyBlurEffect` disables the mask's pressed state).
 * @param materialBackdrop Whether the [material] layer may replace the fill with the COUI 17
 *   backdrop blur (`COUIButton` `isBlurEffectEnabled`). It is only drawn when a
 *   [LocalMaterialBackdrop] provider is present and runtime shaders are available: the solid fill
 *   is then skipped and the content behind the button is blurred and tinted with the preset of
 *   its type (primary 10px with the fill at alpha 229/230, secondary 25px with white at
 *   80% / 20%, transparent 25px overlay + luminosity). Without a provider the button keeps its
 *   opaque fill, which is exactly COUI's non-blur fallback. Defaults to
 *   [LocalCOUIMaterialEffectEnabled]; pass `false` to keep the fill
 *   (`useMaterialEffect = forceDisable`).
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
    materialBackdrop: Boolean = LocalCOUIMaterialEffectEnabled.current,
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
        backdropEnabled = materialBackdrop,
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
    backdropEnabled: Boolean = true,
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
    val isDark = COUITheme.colorScheme.isDark
    val pressTint = if (isDark) PressedTintDark else PressedTintLight
    val hoverTint = if (isDark) HoveredTintDark else HoveredTintLight
    // The background outline, shared by the backdrop blur and the focus ring.
    val shape = remember(cornerRadius) {
        if (cornerRadius == Dp.Unspecified) {
            SquircleShape(COUIRoundCorner.Full, COUIRoundCorner.CapsuleWeight)
        } else {
            SquircleShape(cornerRadius, COUIRoundCorner.Large.Weight)
        }
    }
    // COUIButton.onDraw skips the solid fill while the view blur is on: the filtered backdrop is
    // the fill. The drawable variants never blur, and without a provider COUI's non-blur path
    // keeps the opaque fill.
    val backdropProvider = LocalMaterialBackdrop.current
    val backdropSpec = if (materialActive && backdropEnabled && backdropProvider != null) {
        materialType?.backdropSpec(isDark, enabled, colors.color)
    } else {
        null
    }
    val backdropModifier = if (backdropProvider != null && backdropSpec != null) {
        remember(backdropProvider, backdropSpec, shape) { backdropProvider.materialBackdrop(backdropSpec, shape) }
    } else {
        Modifier
    }
    var fillColor = if (backdropSpec != null) Color.Transparent else containerColor
    if (hoverProgress.value > 0f) {
        fillColor = hoverTint.copy(alpha = hoverTint.alpha * hoverProgress.value).compositeOver(fillColor)
    }
    if (tintProgress.value > 0f) {
        fillColor = pressTint.copy(alpha = pressTint.alpha * tintProgress.value).compositeOver(fillColor)
    }
    // With the backdrop the fill is only the hover tint, drawn over the backdrop below.
    val surfaceColor = if (backdropSpec != null) Color.Transparent else fillColor
    val surfaceModifier = if (cornerRadius == Dp.Unspecified) {
        // COUIButton.updateRoundRectPath: a height-derived capsule takes the 1.0 soft capsule weight.
        Modifier.squircleSurface(color = surfaceColor, cornerRadius = COUIRoundCorner.Full, weight = COUIRoundCorner.CapsuleWeight)
    } else {
        Modifier.squircleSurface(color = surfaceColor, cornerRadius = cornerRadius, weight = COUIRoundCorner.Large.Weight)
    }
    val backdropTint = if (backdropSpec != null && fillColor.alpha > 0f) fillColor else null

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(
            modifier = modifier
                .semantics { role = Role.Button }
                .couiTouchMotion(
                    interactionSource = interactionSource,
                    enabled = enabled,
                    pressScaleEnabled = pressScaleEnabled,
                )
                // COUIButton layers a TYPE_OUTER COUIStrokeDrawable around its background outline.
                .couiFocusStroke(interactionSource = interactionSource, shape = shape, enabled = enabled)
                // Inside the touch-motion layer so the blurred backdrop scales and deforms with
                // the button, and inside the surface's offscreen layer: the framework edge light
                // is overlaid on what lies beneath it in this layer, so it needs the backdrop there.
                .then(surfaceModifier)
                .then(backdropModifier)
                .then(if (backdropTint != null) Modifier.drawBehind { drawRect(backdropTint) } else Modifier)
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
 * @param materialBackdrop Whether the material may replace the fill with the backdrop blur, see
 *   [Button].
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
    textStyle: TextStyle = COUITheme.textStyles.buttonL,
    maxLines: Int = 1,
    material: ButtonMaterial = ButtonMaterial.Auto,
    materialBackdrop: Boolean = LocalCOUIMaterialEffectEnabled.current,
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
        materialBackdrop = materialBackdrop,
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
 * A text button with a description line, the COUI desc-type button (`COUIButton.setDescType` /
 * `isDescType` + `descText`, drawn by `DescButtonTextSpan`).
 *
 * The label (16sp, `coui_btn_desc_text_size`) sits above the description (10sp,
 * `coui_btn_desc_sub_text_size`) with no gap (`coui_btn_desc_top_margin` 0dp); both use the label
 * color and font, are centred on their own and ellipsized to the button width. The button keeps
 * the 44dp min height (`coui_btn_desc_height_min`) with 12dp / 4dp padding
 * (`coui_btn_desc_padding_horizontal` / `_vertical`) and no min width (`setIsDescTypeStyle`).
 * Give it a width from [ButtonDefaults] (for instance [ButtonDefaults.MediumWidth], the
 * `SingleButtonWrap` DescMedium tier) or let it wrap its text.
 *
 * @param text The label of the button.
 * @param descText The description drawn under the label.
 * @param onClick The callback when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 * @param enabled Whether the button is enabled.
 * @param pressScaleEnabled Whether the button shrinks while pressed, see [Button].
 * @param cornerRadius The corner radius of the button; the height-derived capsule by default.
 * @param minWidth The minimum width of the button.
 * @param minHeight The minimum height of the button.
 * @param colors The [TextButtonColors] of the button.
 * @param insideMargin The margin inside the button.
 * @param material The COUI 17 material layer drawn over the fill, see [Button].
 * @param materialBackdrop Whether the material may replace the fill with the backdrop blur, see
 *   [Button].
 * @param interactionSource The [MutableInteractionSource] to be used for the button.
 */
@Composable
fun DescTextButton(
    text: String,
    descText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    pressScaleEnabled: Boolean = true,
    cornerRadius: Dp = ButtonDefaults.CornerRadius,
    minWidth: Dp = ButtonDefaults.MinWidthDesc,
    minHeight: Dp = ButtonDefaults.MinHeight,
    colors: TextButtonColors = ButtonDefaults.textButtonColors(),
    insideMargin: PaddingValues = ButtonDefaults.InsideMarginDesc,
    material: ButtonMaterial = ButtonMaterial.Auto,
    materialBackdrop: Boolean = LocalCOUIMaterialEffectEnabled.current,
    interactionSource: MutableInteractionSource? = null,
) {
    val mappedColors = remember(colors) {
        ButtonColors(
            color = colors.color,
            disabledColor = colors.disabledColor,
            contentColor = colors.textColor,
            disabledContentColor = colors.disabledTextColor,
        )
    }
    val labelStyle = ButtonDefaults.textStyleDesc()
    val descStyle = ButtonDefaults.textStyleDescSub()
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
        materialBackdrop = materialBackdrop,
        interactionSource = interactionSource,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text,
                style = labelStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = descText,
                style = descStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
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
     * fill, and [Secondary] for everything else. None either while
     * [LocalCOUIMaterialEffectEnabled] is `false`.
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
            // followSystem: the app-wide material switch is off.
            !LocalCOUIMaterialEffectEnabled.current -> null

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
    fun textStyleSmall(): TextStyle = COUITheme.textStyles.buttonL.copy(fontSize = 14.sp)

    /**
     * The fixed width of the medium single-button tier (`SingleButtonWrap` Medium and
     * DescMedium, COUI `coui_medium_btn_width`).
     */
    val MediumWidth = 152.dp

    /**
     * The fixed width of the large single-button tier on medium and large screens
     * (`SingleButtonWrap` Large / TextLarge / SingleCentralLarge / DescLarge, COUI
     * `coui_larger_btn_width`). See [largeWidth] for the small-screen widths.
     */
    val LargeWidth = 174.dp

    /**
     * The width of the large single-button tier on small screens at least
     * [SingleLargeWindowWidth] wide (COUI `coui_single_larger_btn_width`).
     */
    val SingleLargeWidth = 296.dp

    /** The narrowest small-screen window that gets [SingleLargeWidth] (COUI `coui_single_larger_window_screen`). */
    val SingleLargeWindowWidth = 360.dp

    /** The min width of the desc-type button: none (`COUIButton.setIsDescTypeStyle` sets `minWidth` 0). */
    val MinWidthDesc = 0.dp

    /**
     * The inside margin of the desc-type button (COUI `coui_btn_desc_padding_horizontal` 12dp /
     * `coui_btn_desc_padding_vertical` 4dp).
     */
    val InsideMarginDesc = PaddingValues(horizontal = 12.dp, vertical = 4.dp)

    /**
     * The width of a large single button (`SingleButtonWrap.getLargeButtonWidth`):
     * [LargeWidth] unless the window is a COUI small screen (narrower than 600dp); there
     * [SingleLargeWidth] for windows at least [SingleLargeWindowWidth] wide, and
     * [Dp.Unspecified] below, meaning the button should fill the available width
     * (`Modifier.fillMaxWidth()`).
     */
    @Composable
    fun largeWidth(): Dp {
        val window = LocalWindowInfo.current.containerDpSize
        val small = COUILayoutGrid.screenTier(window.width, window.height) == COUILayoutGrid.ScreenTier.Small
        return when {
            !small -> LargeWidth
            window.width >= SingleLargeWindowWidth -> SingleLargeWidth
            else -> Dp.Unspecified
        }
    }

    /** The label style of the desc-type button (COUI `coui_btn_desc_text_size`, 16sp, medium). */
    @Composable
    fun textStyleDesc(): TextStyle = COUITheme.textStyles.buttonL

    /** The description style of the desc-type button (COUI `coui_btn_desc_sub_text_size`, 10sp, medium). */
    @Composable
    fun textStyleDescSub(): TextStyle = COUITheme.textStyles.buttonL.copy(fontSize = 10.sp)

    /**
     * The default [ButtonColors] for all buttons: the COUI 17 secondary button
     * (`Widget.COUI.Button.Large.ButtonNew.Secondary`).
     */
    @Composable
    fun buttonColors(
        color: Color = COUITheme.colorScheme.secondaryButton,
        disabledColor: Color = COUITheme.colorScheme.disabledSecondaryButton,
        contentColor: Color = COUITheme.colorScheme.labelPrimary,
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
        disabledColor: Color = COUITheme.colorScheme.disable,
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
        textColor: Color = COUITheme.colorScheme.labelPrimary,
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
        disabledColor: Color = COUITheme.colorScheme.disable,
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
        disabledTextColor: Color = COUITheme.colorScheme.disable,
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
