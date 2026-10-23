// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.couiPressScale
import io.github.suqi8.coui.kmp.squircle.squircleSurface
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalContentColor
import kotlin.coroutines.cancellation.CancellationException
import androidx.compose.animation.Animatable as ColorAnimatable

/**
 * A [Chip] component with COUI style.
 *
 * A capsule shaped, checkable filter tag mirroring ColorOS's COUIChip in its selection style
 * (Widget.COUI.Chip.Choice): the unselected chip is a translucent gray capsule with a primary
 * label; selecting it fills the capsule with the accent color and flips the label to white.
 * The selected/unselected colors cross-fade with the same critically damped spring
 * COUIChipDrawable.TintAnimation uses, while enabling or disabling the chip switches its colors
 * at once. Pressing scales the chip like COUI 17's COUIPressFeedbackHelper (the size-dependent
 * COUIPressAnimatorUnit press, about 0.89 for a 52 x 32dp chip, released on an under-damped
 * spring) while COUIMaskEffectDrawable tints the fill with couiColorPress; hovering tints it with
 * couiColorHover.
 *
 * @param selected Whether the [Chip] is selected.
 * @param onClick The callback when the [Chip] is clicked.
 * @param label The text label of the [Chip]. Single line, ellipsized beyond
 *   [ChipDefaults.TextMaxWidth].
 * @param modifier The modifier to be applied to the [Chip].
 * @param enabled Whether the [Chip] is enabled.
 * @param icon The optional leading icon of the [Chip], laid out in a [ChipDefaults.IconSize] box.
 *   [LocalContentColor] is provided with the current label color so a default [Icon] is tinted
 *   like COUIChip with chipIconApplyTint enabled.
 * @param cornerRadius The corner radius of the [Chip]. COUIChip uses a capsule (half of the
 *   default 32dp height) drawn on the smooth-corner path without a weight
 *   (`COUIChipDrawable.clipSmoothRoundRect`): a fully rounded chip is the weight-1.0 Oplus soft
 *   capsule, a smaller radius takes `default_g2_weight` 2.45 ([COUIRoundCorner.UnspecifiedWeight]).
 * @param minWidth The minimum width of the [Chip].
 * @param minHeight The minimum height of the [Chip].
 * @param maxWidth The maximum width of the [Chip].
 * @param colors The [ChipColors] of the [Chip].
 * @param insideMargin The horizontal padding inside the [Chip].
 * @param interactionSource The [MutableInteractionSource] to be used for the [Chip].
 * @param indication The [Indication] to be used for the [Chip]. Defaults to `null` because the
 *   COUI press feedback (scale + press tint) is built into the chip itself.
 */
@Composable
fun Chip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: (@Composable () -> Unit)? = null,
    cornerRadius: Dp = ChipDefaults.CornerRadius,
    minWidth: Dp = ChipDefaults.MinWidth,
    minHeight: Dp = ChipDefaults.MinHeight,
    maxWidth: Dp = ChipDefaults.MaxWidth,
    colors: ChipColors = ChipDefaults.chipColors(),
    insideMargin: PaddingValues = ChipDefaults.InsideMargin,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    // COUIChipDrawable.TintAnimation: the checked/unchecked background, label and icon colors are
    // evaluated by an ArgbEvaluator driven by COUISpringForce(response 0.3, bounce 0). Enabling or
    // disabling cancels it and assigns the new colors directly (COUIChipDrawable.onStateChange).
    val containerColor by animateChipColor(colors.containerColor(enabled, selected), enabled)
    val contentColor by animateChipColor(colors.contentColor(enabled, selected), enabled)

    // COUI 17 press feedback: COUIPressFeedbackHelper is a COUIPressFeedbackAnimator with only the
    // press scale on (no rubber, no deformation), and COUIMaskEffectDrawable tints the fill with
    // couiColorPress on a critically damped response-0.3 spring.
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val hoverProgress = remember { Animatable(0f) }
    LaunchedEffect(enabled, isHovered) {
        if (!enabled) hoverProgress.snapTo(0f) else hoverProgress.animateTo(if (isHovered) 1f else 0f, ChipPressFeedbackSpring)
    }
    val tintProgress = remember { Animatable(0f) }
    LaunchedEffect(enabled, isPressed) {
        if (enabled && isPressed) {
            tintProgress.animateTo(1f, ChipPressFeedbackSpring)
        } else if (!enabled) {
            // COUIMaskEffectDrawable resets the mask without animation when disabled.
            tintProgress.snapTo(0f)
        } else if (tintProgress.value > 0f) {
            if (tintProgress.value < ChipPressedTintMinVisibleProgress) {
                // COUIMaskEffectDrawable.animateToProgressUntil: keep the press-in animation
                // running until the tint is clearly visible, then reverse from there.
                try {
                    tintProgress.animateTo(1f, ChipPressFeedbackSpring) {
                        if (value >= ChipPressedTintMinVisibleProgress) throw ChipPressTintThresholdReached()
                    }
                } catch (_: ChipPressTintThresholdReached) {
                    // Minimum visible progress reached; fall through to the fade-out.
                }
            }
            tintProgress.animateTo(0f, ChipPressFeedbackSpring)
        }
    }
    // couiColorPress #1F000000 / #33FFFFFF over couiColorHover #14000000 / #26FFFFFF.
    val pressTint = COUITheme.colorScheme.press
    val hoverTint = COUITheme.colorScheme.hover
    var fillColor = containerColor
    if (hoverProgress.value > 0f) fillColor = hoverTint.copy(alpha = hoverTint.alpha * hoverProgress.value).compositeOver(fillColor)
    if (tintProgress.value > 0f) fillColor = pressTint.copy(alpha = pressTint.alpha * tintProgress.value).compositeOver(fillColor)

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(
            modifier = modifier
                .couiPressScale(pressed = isPressed, enabled = enabled)
                .widthIn(max = maxWidth)
                .squircleSurface(color = fillColor, cornerRadius = cornerRadius, weight = COUIRoundCorner.UnspecifiedWeight)
                .selectable(
                    selected = selected,
                    interactionSource = interactionSource,
                    indication = indication,
                    enabled = enabled,
                    role = Role.Checkbox,
                    onClick = onClick,
                ),
            propagateMinConstraints = true,
        ) {
            Row(
                modifier = Modifier
                    .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
                    .padding(insideMargin),
                horizontalArrangement = Arrangement.spacedBy(ChipDefaults.IconSpacing, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier.size(ChipDefaults.IconSize),
                        contentAlignment = Alignment.Center,
                    ) {
                        icon()
                    }
                }
                Text(
                    text = label,
                    color = contentColor,
                    style = ChipDefaults.textStyle(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = ChipDefaults.TextMaxWidth),
                )
            }
        }
    }
}

/** Contains default values used by [Chip]. */
object ChipDefaults {
    /** The minimum width of a chip (COUI coui_chip_default_min_width). */
    val MinWidth = 52.dp

    /** The minimum height of a chip (COUI coui_chip_selection_style_height). */
    val MinHeight = 32.dp

    /** The maximum width of a chip (COUI coui_chip_default_max_width). */
    val MaxWidth = 300.dp

    /** The maximum width of the label (COUI coui_chip_default_max_text_width / chipTextMaxWidth). */
    val TextMaxWidth = 200.dp

    /**
     * The corner radius of a chip. COUIChipDrawable resolves the default chipCornerRadius (-1) to
     * half of the background height, i.e. a capsule for the default 32dp height.
     */
    val CornerRadius = 16.dp

    /** The size of the leading icon (COUI coui_chip_selection_style_chip_icon_size). */
    val IconSize = 16.dp

    /** The spacing between the icon and the label (COUI coui_chip_selection_style_chip_icon_end_padding). */
    val IconSpacing = 4.dp

    /**
     * The horizontal padding inside a chip (COUI coui_chip_selection_style_chip_horizontal_padding;
     * the icon start padding shares the same 12dp value).
     */
    val InsideMargin = PaddingValues(horizontal = 12.dp)

    /** The default text style of the label (COUI Widget.COUI.Chip.Selection textAppearance couiTextBodyM: 14sp). */
    @Composable
    fun textStyle(): TextStyle = COUITheme.textStyles.body2

    /**
     * The default [ChipColors], mapping the Widget.COUI.Chip color attributes:
     * - uncheckedBackgroundColor / uncheckedDisabledBackgroundColor = couiColorContainer8
     *   #14000000 / #1AFFFFFF (the `dialogButton` slot);
     * - checkedBackgroundColor = couiColorPrimary, checkedDisabledBackgroundColor = couiColorDisable
     *   #4D0080FF / #4D1A8CFF;
     * - uncheckedTextColor = couiColorLabelPrimary #E6000000 / #E6FFFFFF, checkedTextColor =
     *   couiColorLabelOnColor #FFFFFF;
     * - chip_unchecked_text_disable_color #42000000 / #29FFFFFF (the same values as the
     *   `disabledSliderKeyPoint` slot) and chip_checked_text_disable_color #8AFFFFFF / #29FFFFFF.
     */
    @Composable
    fun chipColors(
        containerColor: Color = COUITheme.colorScheme.dialogButton,
        selectedContainerColor: Color = COUITheme.colorScheme.primary,
        disabledContainerColor: Color = COUITheme.colorScheme.dialogButton,
        selectedDisabledContainerColor: Color = COUITheme.colorScheme.disabledPrimaryButton,
        contentColor: Color = COUITheme.colorScheme.onSurface,
        selectedContentColor: Color = COUITheme.colorScheme.onPrimary,
        disabledContentColor: Color = COUITheme.colorScheme.disabledSliderKeyPoint,
        selectedDisabledContentColor: Color = COUITheme.colorScheme.disabledOnPrimaryButton,
    ): ChipColors = remember(
        containerColor,
        selectedContainerColor,
        disabledContainerColor,
        selectedDisabledContainerColor,
        contentColor,
        selectedContentColor,
        disabledContentColor,
        selectedDisabledContentColor,
    ) {
        ChipColors(
            containerColor = containerColor,
            selectedContainerColor = selectedContainerColor,
            disabledContainerColor = disabledContainerColor,
            selectedDisabledContainerColor = selectedDisabledContainerColor,
            contentColor = contentColor,
            selectedContentColor = selectedContentColor,
            disabledContentColor = disabledContentColor,
            selectedDisabledContentColor = selectedDisabledContentColor,
        )
    }
}

/**
 * Colors used by a [Chip].
 *
 * @param containerColor The capsule color when unselected.
 * @param selectedContainerColor The capsule color when selected.
 * @param disabledContainerColor The capsule color when unselected and disabled.
 * @param selectedDisabledContainerColor The capsule color when selected and disabled.
 * @param contentColor The label and icon color when unselected.
 * @param selectedContentColor The label and icon color when selected.
 * @param disabledContentColor The label and icon color when unselected and disabled.
 * @param selectedDisabledContentColor The label and icon color when selected and disabled.
 */
@Immutable
data class ChipColors(
    val containerColor: Color,
    val selectedContainerColor: Color,
    val disabledContainerColor: Color,
    val selectedDisabledContainerColor: Color,
    val contentColor: Color,
    val selectedContentColor: Color,
    val disabledContentColor: Color,
    val selectedDisabledContentColor: Color,
) {
    @Stable
    internal fun containerColor(enabled: Boolean, selected: Boolean): Color = when {
        enabled && selected -> selectedContainerColor
        enabled -> containerColor
        selected -> selectedDisabledContainerColor
        else -> disabledContainerColor
    }

    @Stable
    internal fun contentColor(enabled: Boolean, selected: Boolean): Color = when {
        enabled && selected -> selectedContentColor
        enabled -> contentColor
        selected -> selectedDisabledContentColor
        else -> disabledContentColor
    }
}

/**
 * A chip color that follows [target] on the check tint spring while the chip stays enabled, and
 * jumps to it when the chip is disabled or re-enabled (COUIChipDrawable cancels the TintAnimation
 * and assigns the enabled or disabled colors directly).
 */
@Composable
private fun animateChipColor(target: Color, enabled: Boolean): State<Color> {
    val color = remember { ColorAnimatable(target) }
    // The enabled flag the last effect saw; only written from the effect.
    val lastEnabled = remember { booleanArrayOf(enabled) }
    LaunchedEffect(target, enabled) {
        if (enabled && lastEnabled[0]) {
            color.animateTo(target, CheckTintColorSpring)
        } else {
            color.snapTo(target)
        }
        lastEnabled[0] = enabled
    }
    return color.asState()
}

/**
 * The COUI check tint spring for colors: COUIChipDrawable.TintAnimation uses
 * COUISpringForce(response = 0.3f, bounce = 0f), i.e. a critically damped spring with stiffness
 * (2 * PI / 0.3)^2 ~= 438.65.
 */
private val CheckTintColorSpring = spring<Color>(dampingRatio = 1f, stiffness = 438.65f)

/**
 * The COUI mask spring: COUISpringForce(response = 0.3f, bounce = 0f), shared by the press and
 * hover tints (COUIMaskEffectDrawable).
 */
private val ChipPressFeedbackSpring = spring<Float>(dampingRatio = 1f, stiffness = 438.65f)

/**
 * A release before the press tint reaches this progress defers the fade-out until it does, so
 * quick taps still flash visibly (COUIMaskEffectDrawable DEFAULT_MIN_PROGRESS_FOR_TOUCH_ENTER_ANIMATION).
 */
private val ChipPressedTintMinVisibleProgress = 0.7f

/** Thrown to stop the deferred press-in tint animation once [ChipPressedTintMinVisibleProgress] is reached. */
private class ChipPressTintThresholdReached : CancellationException("Press tint reached the minimum visible progress")
