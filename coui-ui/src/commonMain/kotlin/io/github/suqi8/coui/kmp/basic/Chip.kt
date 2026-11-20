// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.couiPressScale
import io.github.suqi8.coui.kmp.squircle.squircleClip
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalContentColor
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * A [Chip] component with COUI style.
 *
 * A capsule shaped, checkable filter tag mirroring ColorOS's COUIChip in its selection style
 * (Widget.COUI.Chip.Choice): the unselected chip is a translucent gray capsule with a primary
 * label; selecting it fills the capsule with the accent color and flips the label to white.
 * The selected/unselected colors cross-fade with the same critically damped spring
 * COUIChipDrawable.TintAnimation uses, blended like its `ArgbEvaluator` (in linear light), while
 * enabling or disabling the chip switches its colors at once. Pressing scales the chip like
 * COUI 17's COUIPressFeedbackHelper (the size-dependent
 * COUIPressAnimatorUnit press, about 0.89 for a 52 x 32dp chip, released on an under-damped
 * spring) while COUIMaskEffectDrawable tints the fill with couiColorPress; hovering tints it with
 * couiColorHover. Keyboard focus draws COUIStrokeDrawable's 3dp couiColorFocusOutline ring just
 * outside the capsule.
 *
 * @param selected Whether the [Chip] is selected.
 * @param onClick The callback when the [Chip] is clicked.
 * @param label The text label of the [Chip]. Single line, ellipsized beyond
 *   [ChipDefaults.TextMaxWidth].
 * @param modifier The modifier to be applied to the [Chip].
 * @param enabled Whether the [Chip] is enabled.
 * @param showRedDot Whether to draw the COUI hint red dot (`COUIChip.setShowRedDot`): a 6dp dot
 *   2dp after the label (or the [count]) and 6dp below the chip's top edge, inside the end padding.
 * @param icon The optional leading icon of the [Chip], laid out in a [ChipDefaults.IconSize] box.
 *   [LocalContentColor] is provided with the current label color so a default [Icon] is tinted
 *   like COUIChip with chipIconApplyTint enabled.
 * @param onClose When not `null`, the chip shows the COUI close icon (`ic_chip_close`) at its end
 *   and calls this when it is tapped (`COUIChip.setCloseIconVisible` +
 *   `setOnCloseIconClickListener`). The close icon replaces the end padding with its own
 *   8dp + 12dp + 8dp touch area, and pressing it lays the press tint over a 24dp circle behind it.
 *   Switching between `null` and a callback plays COUIChipDrawable's CloseIconAnimation: the chip
 *   grows or shrinks by the slot while the icon fades and scales between 0.3 and 1.
 * @param count An optional count drawn 2dp after the label in [ChipColors.countColor]
 *   (`COUIChip.setCountText`, couiColorLabelSecondary at the label size).
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
    showRedDot: Boolean = false,
    icon: (@Composable () -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    count: String? = null,
    cornerRadius: Dp = ChipDefaults.CornerRadius,
    minWidth: Dp = ChipDefaults.MinWidth,
    minHeight: Dp = ChipDefaults.MinHeight,
    maxWidth: Dp = ChipDefaults.MaxWidth,
    colors: ChipColors = ChipDefaults.chipColors(),
    insideMargin: PaddingValues = ChipDefaults.InsideMargin,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
) {
    ChipImpl(
        selectable = true,
        selected = selected,
        onClick = onClick,
        label = label,
        modifier = modifier,
        enabled = enabled,
        showRedDot = showRedDot,
        icon = icon,
        onClose = onClose,
        count = count,
        cornerRadius = cornerRadius,
        minWidth = minWidth,
        minHeight = minHeight,
        maxWidth = maxWidth,
        colors = colors,
        insideMargin = insideMargin,
        metrics = SelectionChipMetrics,
        textStyle = ChipDefaults.textStyle(),
        interactionSource = interactionSource,
        indication = indication,
    )
}

/**
 * A suggestion [Chip] with COUI style (`Widget.COUI.Chip.Suggestion`, also used as
 * `Widget.COUI.Chip.Record` for history records).
 *
 * Unlike [Chip] it is not checkable (the base `Widget.COUI.Chip` sets `checkable = false`): a tap
 * only calls [onClick]. It is smaller than the selection chip: 28dp high with a 12sp
 * (couiTextBodyXS) label, a 14dp leading icon 12dp after the start padding
 * (`chipStartPadding` + `chipIconStartPadding`) and a 12dp close icon. The colors, press scale,
 * press / hover tints and focus ring are those of [Chip].
 *
 * @param onClick The callback when the chip is clicked.
 * @param label The text label of the chip.
 * @param modifier The modifier to be applied to the chip.
 * @param enabled Whether the chip is enabled.
 * @param showRedDot Whether to draw the COUI hint red dot after the label, see [Chip].
 * @param icon The optional leading icon, laid out in a [ChipDefaults.SuggestionIconSize] box.
 * @param onClose When not `null`, the chip shows the close icon and calls this when it is tapped,
 *   see [Chip].
 * @param count An optional count drawn after the label, see [Chip].
 * @param cornerRadius The corner radius of the chip: half of the 28dp height by default.
 * @param minWidth The minimum width of the chip.
 * @param minHeight The minimum height of the chip.
 * @param maxWidth The maximum width of the chip.
 * @param colors The [ChipColors] of the chip; only the unselected colors are used.
 * @param insideMargin The horizontal padding inside the chip.
 * @param interactionSource The [MutableInteractionSource] to be used for the chip.
 * @param indication The [Indication] to be used for the chip.
 */
@Composable
fun SuggestionChip(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showRedDot: Boolean = false,
    icon: (@Composable () -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    count: String? = null,
    cornerRadius: Dp = ChipDefaults.SuggestionCornerRadius,
    minWidth: Dp = ChipDefaults.MinWidth,
    minHeight: Dp = ChipDefaults.SuggestionMinHeight,
    maxWidth: Dp = ChipDefaults.MaxWidth,
    colors: ChipColors = ChipDefaults.chipColors(),
    insideMargin: PaddingValues = ChipDefaults.InsideMargin,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
) {
    ChipImpl(
        selectable = false,
        selected = false,
        onClick = onClick,
        label = label,
        modifier = modifier,
        enabled = enabled,
        showRedDot = showRedDot,
        icon = icon,
        onClose = onClose,
        count = count,
        cornerRadius = cornerRadius,
        minWidth = minWidth,
        minHeight = minHeight,
        maxWidth = maxWidth,
        colors = colors,
        insideMargin = insideMargin,
        metrics = SuggestionChipMetrics,
        textStyle = ChipDefaults.suggestionTextStyle(),
        interactionSource = interactionSource,
        indication = indication,
    )
}

/**
 * An icon-only round [Chip] with COUI style (`Widget.COUI.Chip.Suggestion.Collapsable`): a 28dp
 * circle with a 16dp icon and 6dp on either side. [ChipGroup] uses it for its expand / collapse
 * button.
 *
 * @param onClick The callback when the chip is clicked.
 * @param modifier The modifier to be applied to the chip.
 * @param enabled Whether the chip is enabled.
 * @param colors The [ChipColors] of the chip; only the unselected colors are used.
 * @param interactionSource The [MutableInteractionSource] to be used for the chip.
 * @param icon The icon, laid out in a [ChipDefaults.CollapsableIconSize] box.
 */
@Composable
fun IconChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ChipColors = ChipDefaults.chipColors(),
    interactionSource: MutableInteractionSource? = null,
    icon: @Composable () -> Unit,
) {
    ChipImpl(
        selectable = false,
        selected = false,
        onClick = onClick,
        label = "",
        modifier = modifier,
        enabled = enabled,
        showRedDot = false,
        icon = icon,
        onClose = null,
        count = null,
        cornerRadius = ChipDefaults.CollapsableSize / 2,
        minWidth = ChipDefaults.CollapsableSize,
        minHeight = ChipDefaults.CollapsableSize,
        maxWidth = ChipDefaults.CollapsableSize,
        colors = colors,
        insideMargin = PaddingValues(),
        metrics = CollapsableChipMetrics,
        textStyle = ChipDefaults.suggestionTextStyle(),
        interactionSource = interactionSource,
        indication = null,
    )
}

/** The COUIChipDrawable paddings and icon sizes of one chip style. */
@Immutable
private class ChipMetrics(
    val iconSize: Dp,
    val iconStartPadding: Dp,
    val iconEndPadding: Dp,
)

/** Widget.COUI.Chip.Selection: 16dp icon, no icon start padding, 4dp icon end padding. */
private val SelectionChipMetrics = ChipMetrics(
    iconSize = ChipDefaults.IconSize,
    iconStartPadding = 0.dp,
    iconEndPadding = ChipDefaults.IconSpacing,
)

/**
 * Widget.COUI.Chip.Suggestion: 14dp icon, 12dp icon start padding on top of the 12dp chip start
 * padding (COUIChipDrawable.calculateChipIconBounds adds both), 4dp icon end padding.
 */
private val SuggestionChipMetrics = ChipMetrics(
    iconSize = ChipDefaults.SuggestionIconSize,
    iconStartPadding = 12.dp,
    iconEndPadding = ChipDefaults.IconSpacing,
)

/** Widget.COUI.Chip.Suggestion.Collapsable: 16dp icon with 6dp on either side, no chip padding. */
private val CollapsableChipMetrics = ChipMetrics(
    iconSize = ChipDefaults.CollapsableIconSize,
    iconStartPadding = 6.dp,
    iconEndPadding = 6.dp,
)

@Composable
private fun ChipImpl(
    selectable: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    enabled: Boolean,
    showRedDot: Boolean,
    icon: (@Composable () -> Unit)?,
    onClose: (() -> Unit)?,
    count: String?,
    cornerRadius: Dp,
    minWidth: Dp,
    minHeight: Dp,
    maxWidth: Dp,
    colors: ChipColors,
    insideMargin: PaddingValues,
    metrics: ChipMetrics,
    textStyle: TextStyle,
    interactionSource: MutableInteractionSource?,
    indication: Indication?,
    modifier: Modifier = Modifier,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    // COUIChipDrawable.TintAnimation: one checked fraction drives the background, label and icon
    // colors through an ArgbEvaluator on COUISpringForce(response 0.3, bounce 0). Enabling or
    // disabling cancels it and assigns the new colors directly (COUIChipDrawable.onStateChange).
    // The colors are resolved while drawing, so the cross-fade does not recompose the chip.
    val checkProgress = remember { Animatable(if (selected) 1f else 0f) }
    // The enabled flag the last effect saw; only written from the effect.
    val lastEnabled = remember { booleanArrayOf(enabled) }
    LaunchedEffect(selected, enabled) {
        val target = if (selected) 1f else 0f
        val wasEnabled = lastEnabled[0]
        lastEnabled[0] = enabled
        if (enabled && wasEnabled) checkProgress.animateTo(target, ChipTintSpring) else checkProgress.snapTo(target)
    }
    val contentTint = remember(colors, enabled, selected, checkProgress) {
        ColorProducer {
            chipTint(
                unchecked = colors.contentColor,
                checked = colors.selectedContentColor,
                disabledUnchecked = colors.disabledContentColor,
                disabledChecked = colors.selectedDisabledContentColor,
                enabled = enabled,
                selected = selected,
                fraction = checkProgress.value,
            )
        }
    }
    val countTint = remember(colors, contentTint) { ColorProducer { colors.countColor.takeOrElse { contentTint() } } }
    val closeIconTint = remember(colors, contentTint) { ColorProducer { colors.closeIconColor.takeOrElse { contentTint() } } }

    // COUI 17 press feedback: COUIPressFeedbackHelper is a COUIPressFeedbackAnimator with only the
    // press scale on (no rubber, no deformation), and COUIMaskEffectDrawable tints the fill with
    // couiColorPress and couiColorHover on critically damped response-0.3 springs.
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val pressProgress = rememberChipMaskPress(pressed = isPressed, enabled = enabled)
    val hoverProgress = rememberChipMaskLayer(active = isHovered, enabled = enabled)
    // COUIStrokeDrawable: the focus outline fades on the same spring and is cleared at once when the
    // chip is disabled.
    val focusProgress = rememberChipMaskLayer(active = isFocused, enabled = enabled)
    val focusOutline = COUITheme.colorScheme.focusOutline
    // couiColorPress #1F000000 / #33FFFFFF over couiColorHover #14000000 / #26FFFFFF.
    val pressTint = COUITheme.colorScheme.press
    val hoverTint = COUITheme.colorScheme.hover

    // COUIChipDrawable.CloseIconAnimation (setCloseIconVisible animates): the close icon fades and
    // scales in or out while the chip grows or shrinks by its slot, and stays drawn until it is gone.
    val closeVisible = onClose != null
    val closeProgress = remember { Animatable(if (closeVisible) 1f else 0f) }
    LaunchedEffect(closeVisible) {
        closeProgress.animateTo(if (closeVisible) 1f else 0f, ChipCloseIconSpring)
    }
    val closeIconDrawn by remember { derivedStateOf { closeProgress.value > 0f } }
    val showCloseSlot = closeVisible || closeIconDrawn

    Box(
        modifier = modifier
            .couiPressScale(pressed = isPressed, enabled = enabled)
            .widthIn(max = maxWidth)
            .drawWithContent {
                drawContent()
                val focus = focusProgress.value
                if (focus > 0f) {
                    // COUIStrokeDrawable TYPE_OUTER: a 2 x default_focus_stroke_radius stroke on the
                    // plain round rect, clipped to its outside, i.e. a 3dp ring around the chip.
                    val ring = ChipFocusStrokeWidth.toPx()
                    val radius = minOf(cornerRadius.toPx(), size.minDimension / 2f)
                    drawRoundRect(
                        color = focusOutline,
                        topLeft = Offset(-ring / 2f, -ring / 2f),
                        size = Size(size.width + ring, size.height + ring),
                        cornerRadius = CornerRadius(radius + ring / 2f),
                        style = Stroke(width = ring),
                        alpha = focus.coerceIn(0f, 1f),
                    )
                }
            }
            .squircleClip(cornerRadius = cornerRadius, weight = COUIRoundCorner.UnspecifiedWeight)
            .drawBehind {
                // COUIChipDrawable.drawChipBackground, then the hover and press layers of drawStateEffects.
                var fill = chipTint(
                    unchecked = colors.containerColor,
                    checked = colors.selectedContainerColor,
                    disabledUnchecked = colors.disabledContainerColor,
                    disabledChecked = colors.selectedDisabledContainerColor,
                    enabled = enabled,
                    selected = selected,
                    fraction = checkProgress.value,
                )
                val hover = hoverProgress.value.coerceAtMost(1f)
                if (hover > 0f) fill = hoverTint.copy(alpha = hoverTint.alpha * hover).compositeOver(fill)
                val press = pressProgress.value.coerceAtMost(1f)
                if (press > 0f) fill = pressTint.copy(alpha = pressTint.alpha * press).compositeOver(fill)
                drawRect(fill)
            }
            .then(
                if (selectable) {
                    Modifier.selectable(
                        selected = selected,
                        interactionSource = interactionSource,
                        indication = indication,
                        enabled = enabled,
                        role = Role.Checkbox,
                        onClick = onClick,
                    )
                } else {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = indication,
                        enabled = enabled,
                        role = Role.Button,
                        onClick = onClick,
                    )
                },
            ),
        propagateMinConstraints = true,
    ) {
        val layoutDirection = LocalLayoutDirection.current
        val endPadding = insideMargin.calculateEndPadding(layoutDirection)
        // COUIChipDrawable: a visible (or animating) close icon replaces chipEndPadding with its own
        // closeIconStartPadding + closeIconSize + closeIconEndPadding.
        val rowPadding = if (showCloseSlot) {
            PaddingValues(
                start = insideMargin.calculateStartPadding(layoutDirection),
                top = insideMargin.calculateTopPadding(),
                bottom = insideMargin.calculateBottomPadding(),
            )
        } else {
            insideMargin
        }
        Row(
            modifier = Modifier
                .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
                .padding(rowPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .padding(start = metrics.iconStartPadding, end = if (label.isEmpty()) metrics.iconEndPadding else 0.dp)
                        .size(metrics.iconSize),
                    contentAlignment = Alignment.Center,
                ) {
                    ChipIconTint(tint = contentTint, icon = icon)
                }
                if (label.isNotEmpty()) Spacer(Modifier.width(metrics.iconEndPadding))
            }
            if (label.isNotEmpty() || count != null) {
                val redDotColor = BadgeDefaults.badgeColors().containerColor
                Row(
                    modifier = if (showRedDot) {
                        Modifier.drawWithContent {
                            drawContent()
                            // COUIChipDrawable.calculateRedDotBounds: 2dp after the label / count,
                            // 6dp below the chip's top edge (the chip is minHeight tall).
                            val dot = ChipRedDotDiameter.toPx()
                            val gap = ChipRedDotOffsetHorizontal.toPx()
                            val top = ChipRedDotOffsetVertical.toPx() - (minHeight.toPx() - size.height) / 2f
                            val left = if (layoutDirection == LayoutDirection.Ltr) size.width + gap else -gap - dot
                            drawCircle(
                                color = redDotColor,
                                radius = dot / 2f,
                                center = Offset(left + dot / 2f, top + dot / 2f),
                            )
                        }
                    } else {
                        Modifier
                    },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (label.isNotEmpty()) {
                        Text(
                            text = label,
                            color = contentTint,
                            modifier = Modifier.widthIn(max = ChipDefaults.TextMaxWidth),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = textStyle,
                        )
                    }
                    if (count != null) {
                        Text(
                            text = count,
                            color = countTint,
                            modifier = Modifier.padding(start = ChipDefaults.CountSpacing),
                            maxLines = 1,
                            style = textStyle,
                        )
                    }
                }
            }
            if (showCloseSlot) {
                ChipCloseIconSlot(
                    progress = { closeProgress.value },
                    onClose = onClose,
                    enabled = enabled,
                    restWidth = endPadding,
                    minHeight = minHeight,
                    tint = closeIconTint,
                )
            }
        }
    }
}

/**
 * Provides the chip's current icon tint as [LocalContentColor] (COUIChip `chipIconApplyTint`), in
 * its own scope so the check cross-fade only recomposes the icon.
 */
@Composable
private fun ChipIconTint(
    tint: ColorProducer,
    icon: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalContentColor provides tint(), content = icon)
}

/**
 * The close icon slot of a chip (`COUIChipDrawable.drawCloseIcon`).
 *
 * At [progress] 1 the slot is `closeIconStartPadding + closeIconSize + closeIconEndPadding` wide,
 * at 0 it shrinks to [restWidth] (the chip's end padding), so the chip grows and shrinks with the
 * `CloseIconAnimation`. The icon fades with the progress and scales from 0.3 about a centre that
 * travels with the chip's end edge at half its speed, as `drawCloseIcon` places it. Only a shown
 * icon ([onClose] not `null`) is clickable: its 24dp circle (`closeIconTouchBoundsSize`, scaled with
 * the icon) takes the press, hover and focus layers of `mCloseIconEffectDrawable`.
 */
@Composable
private fun ChipCloseIconSlot(
    progress: () -> Float,
    onClose: (() -> Unit)?,
    enabled: Boolean,
    restWidth: Dp,
    minHeight: Dp,
    tint: ColorProducer,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val interactive = onClose != null && enabled
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val pressProgress = rememberChipMaskPress(pressed = isPressed, enabled = interactive)
    val hoverProgress = rememberChipMaskLayer(active = isHovered, enabled = interactive)
    val focusProgress = rememberChipMaskLayer(active = isFocused, enabled = interactive)
    val pressTint = COUITheme.colorScheme.press
    val hoverTint = COUITheme.colorScheme.hover
    val focusTint = COUITheme.colorScheme.focus
    val painter = rememberVectorPainter(ChipCloseIcon)
    Box(
        modifier = modifier
            .layout { measurable, constraints ->
                val fullWidth = ChipCloseIconSlotWidth.roundToPx()
                val shrunkWidth = restWidth.roundToPx()
                val fraction = progress().coerceIn(0f, 1f)
                val width = (shrunkWidth + (fullWidth - shrunkWidth) * fraction).roundToInt()
                val height = minHeight.roundToPx().coerceIn(constraints.minHeight, constraints.maxHeight)
                val placeable = measurable.measure(Constraints.fixed(width, height))
                layout(width, height) { placeable.place(0, 0) }
            }
            .then(
                if (onClose != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = enabled,
                        role = Role.Button,
                        onClick = onClose,
                    )
                } else {
                    Modifier
                },
            )
            .drawBehind {
                val fraction = progress().coerceIn(0f, 1f)
                if (fraction <= 0f) return@drawBehind
                val scale = ChipCloseIconMinScale + (1f - ChipCloseIconMinScale) * fraction
                val iconSize = ChipDefaults.CloseIconSize.toPx()
                val fullWidth = ChipCloseIconSlotWidth.toPx()
                val shrunkWidth = restWidth.toPx()
                // drawCloseIcon: the icon centre follows the background's current width at half
                // speed; measured from the label side of the slot it is 6dp at rest and 14dp shown.
                val fromLabel = shrunkWidth - ChipDefaults.CloseIconEndPadding.toPx() - iconSize / 2f +
                    (fullWidth - shrunkWidth) * (1f + fraction) / 2f
                val center = Offset(
                    x = if (layoutDirection == LayoutDirection.Ltr) fromLabel else size.width - fromLabel,
                    y = size.height / 2f,
                )
                val maskRadius = ChipCloseIconTouchSize.toPx() / 2f * scale
                val hover = hoverProgress.value.coerceAtMost(1f)
                if (hover > 0f) drawCircle(color = hoverTint, radius = maskRadius, center = center, alpha = hover)
                val focus = focusProgress.value.coerceAtMost(1f)
                if (focus > 0f) drawCircle(color = focusTint, radius = maskRadius, center = center, alpha = focus)
                val press = pressProgress.value.coerceAtMost(1f)
                if (press > 0f) drawCircle(color = pressTint, radius = maskRadius, center = center, alpha = press)
                val drawSize = iconSize * scale
                translate(left = center.x - drawSize / 2f, top = center.y - drawSize / 2f) {
                    with(painter) {
                        draw(size = Size(drawSize, drawSize), alpha = fraction, colorFilter = ColorFilter.tint(tint()))
                    }
                }
            },
    )
}

/**
 * The touch layer of a `COUIMaskEffectDrawable` (`StateEffectAnimator` "press", response 0.3 /
 * bounce 0): it follows the press, but a release before it is clearly visible keeps it rising to
 * [ChipPressedTintMinVisibleProgress] first (`animateToProgressUntil`), so a quick tap still
 * flashes. A disabled control resets it without animation.
 */
@Composable
private fun rememberChipMaskPress(pressed: Boolean, enabled: Boolean): Animatable<Float, AnimationVector1D> {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(enabled, pressed) {
        if (enabled && pressed) {
            progress.animateTo(1f, ChipPressFeedbackSpring)
        } else if (!enabled) {
            // COUIMaskEffectDrawable resets the mask without animation when disabled.
            progress.snapTo(0f)
        } else if (progress.value > 0f) {
            if (progress.value < ChipPressedTintMinVisibleProgress) {
                // COUIMaskEffectDrawable.animateToProgressUntil: keep the press-in animation
                // running until the tint is clearly visible, then reverse from there.
                try {
                    progress.animateTo(1f, ChipPressFeedbackSpring) {
                        if (value >= ChipPressedTintMinVisibleProgress) throw ChipPressTintThresholdReached()
                    }
                } catch (_: ChipPressTintThresholdReached) {
                    // Minimum visible progress reached; fall through to the fade-out.
                }
            }
            progress.animateTo(0f, ChipPressFeedbackSpring)
        }
    }
    return progress
}

/**
 * A hover or focus layer of a `COUIMaskEffectDrawable` / `COUIStrokeDrawable`: it fades with
 * [active] on the response 0.3 / bounce 0 spring and is cleared at once while disabled.
 */
@Composable
private fun rememberChipMaskLayer(active: Boolean, enabled: Boolean): Animatable<Float, AnimationVector1D> {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(enabled, active) {
        if (!enabled) progress.snapTo(0f) else progress.animateTo(if (active) 1f else 0f, ChipPressFeedbackSpring)
    }
    return progress
}

/** COUI `ic_chip_close` (14 x 14 viewport), drawn at `coui_chip_suggest_style_close_icon_size`. */
private val ChipCloseIcon: ImageVector by lazy {
    ImageVector.Builder("ChipClose", 14.dp, 14.dp, 14f, 14f).apply {
        addPath(
            pathData = addPathNodes(
                "M12.286,2.54C12.4,2.426 12.4,2.242 12.286,2.128L11.874,1.715C11.76,1.601 11.575,1.601 11.461,1.715" +
                    "L7.001,6.176L2.54,1.715C2.426,1.601 2.242,1.601 2.128,1.715L1.715,2.128C1.601,2.242 1.601,2.426 1.715,2.54" +
                    "L6.176,7.001L1.715,11.461C1.601,11.575 1.601,11.76 1.715,11.874L2.128,12.286C2.242,12.4 2.426,12.4 2.54,12.286" +
                    "L7.001,7.826L11.461,12.286C11.575,12.4 11.76,12.4 11.874,12.286L12.286,11.874C12.4,11.76 12.4,11.575 12.286,11.461" +
                    "L7.826,7.001L12.286,2.54Z",
            ),
            fill = SolidColor(Color.Black),
        )
    }.build()
}

/** The diameter of the chip red dot (Widget.COUI.COUIHintRedDot.Small, coui_dot_diameter_small). */
private val ChipRedDotDiameter = 6.dp

/** The gap between the label / count and the red dot (coui_chip_red_dot_offset_horizontal). */
private val ChipRedDotOffsetHorizontal = 2.dp

/** The red dot's distance from the chip's top edge (coui_chip_red_dot_offset_vertical). */
private val ChipRedDotOffsetVertical = 6.dp

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

    /** The minimum height of a suggestion chip (COUI coui_chip_suggest_style_height). */
    val SuggestionMinHeight = 28.dp

    /** The corner radius of a suggestion chip: half of [SuggestionMinHeight], a capsule. */
    val SuggestionCornerRadius = 14.dp

    /** The size of the suggestion chip's leading icon (COUI coui_chip_suggest_style_chip_icon_size). */
    val SuggestionIconSize = 14.dp

    /** The size of the close icon (COUI coui_chip_suggest_style_close_icon_size). */
    val CloseIconSize = 12.dp

    /** The padding before the close icon (COUI coui_chip_suggest_style_close_icon_start_padding). */
    val CloseIconStartPadding = 8.dp

    /** The padding after the close icon (COUI coui_chip_suggest_style_close_icon_end_padding). */
    val CloseIconEndPadding = 8.dp

    /** The gap between the label and the count (COUIChipDrawable mCountMarginStart, 2dp). */
    val CountSpacing = 2.dp

    /**
     * The size of the icon-only [IconChip] and of the [ChipGroup] expand / collapse button (COUI
     * coui_chip_group_collapsable_button_size, Widget.COUI.Chip.Suggestion.Collapsable).
     */
    val CollapsableSize = 28.dp

    /** The icon size of the icon-only [IconChip] (Widget.COUI.Chip.Suggestion.Collapsable chipIconSize). */
    val CollapsableIconSize = 16.dp

    /** The default text style of the label (COUI Widget.COUI.Chip.Selection textAppearance couiTextBodyM: 14sp). */
    @Composable
    fun textStyle(): TextStyle = COUITheme.textStyles.bodyM

    /** The text style of the suggestion chip label (COUI Widget.COUI.Chip textAppearance couiTextBodyXS: 12sp). */
    @Composable
    fun suggestionTextStyle(): TextStyle = COUITheme.textStyles.bodyXS

    /**
     * The default [ChipColors], mapping the Widget.COUI.Chip color attributes:
     * - uncheckedBackgroundColor / uncheckedDisabledBackgroundColor = couiColorContainer8
     *   #14000000 / #1AFFFFFF;
     * - checkedBackgroundColor = couiColorPrimary, checkedDisabledBackgroundColor = couiColorDisable
     *   #4D0080FF / #4D1A8CFF;
     * - uncheckedTextColor = couiColorLabelPrimary #E6000000 / #E6FFFFFF, checkedTextColor =
     *   couiColorLabelOnColor #FFFFFF;
     * - chip_unchecked_text_disable_color #42000000 / #29FFFFFF and chip_checked_text_disable_color
     *   #8AFFFFFF / #29FFFFFF;
     * - the count text is couiColorLabelSecondary #8A000000 / #8AFFFFFF in every state
     *   (COUIChipDrawable mCountTextColor);
     * - the close icon is `ic_chip_close`'s couiColorSecondNeutral #8A000000 / #8AFFFFFF.
     */
    @Composable
    fun chipColors(
        containerColor: Color = COUITheme.colorScheme.container8,
        selectedContainerColor: Color = COUITheme.colorScheme.primary,
        disabledContainerColor: Color = COUITheme.colorScheme.container8,
        selectedDisabledContainerColor: Color = COUITheme.colorScheme.disable,
        contentColor: Color = COUITheme.colorScheme.labelPrimary,
        selectedContentColor: Color = COUITheme.colorScheme.onPrimary,
        disabledContentColor: Color = COUITheme.colorScheme.disabledChipText,
        selectedDisabledContentColor: Color = COUITheme.colorScheme.disabledOnPrimaryButton,
        countColor: Color = COUITheme.colorScheme.labelSecondary,
        closeIconColor: Color = COUITheme.colorScheme.labelSecondary,
    ): ChipColors = remember(
        containerColor,
        selectedContainerColor,
        disabledContainerColor,
        selectedDisabledContainerColor,
        contentColor,
        selectedContentColor,
        disabledContentColor,
        selectedDisabledContentColor,
        countColor,
        closeIconColor,
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
            countColor = countColor,
            closeIconColor = closeIconColor,
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
 * @param countColor The color of the count text; [Color.Unspecified] follows the label color.
 * @param closeIconColor The color of the close icon; [Color.Unspecified] follows the label color.
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
    val countColor: Color = Color.Unspecified,
    val closeIconColor: Color = Color.Unspecified,
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
 * COUIChipDrawable's current tint: while enabled, the `ArgbEvaluator` blend of the unchecked and
 * checked colors at the TintAnimation [fraction]; while disabled, the disabled color of the state,
 * assigned without animation.
 */
private fun chipTint(
    unchecked: Color,
    checked: Color,
    disabledUnchecked: Color,
    disabledChecked: Color,
    enabled: Boolean,
    selected: Boolean,
    fraction: Float,
): Color = if (enabled) {
    chipArgbEvaluate(unchecked, checked, fraction)
} else if (selected) {
    disabledChecked
} else {
    disabledUnchecked
}

/**
 * `android.animation.ArgbEvaluator`, which COUIChipDrawable.TintAnimation uses: alpha blends
 * linearly while the color channels blend in linear light (sRGB decoded with a 2.2 power, then
 * encoded back), so a dark label turning white brightens early instead of passing through the
 * mid grey of an Oklab blend.
 */
private fun chipArgbEvaluate(start: Color, end: Color, fraction: Float): Color = when {
    fraction <= 0f -> start

    fraction >= 1f -> end

    else -> Color(
        red = chipLinearLightBlend(start.red, end.red, fraction),
        green = chipLinearLightBlend(start.green, end.green, fraction),
        blue = chipLinearLightBlend(start.blue, end.blue, fraction),
        alpha = start.alpha + (end.alpha - start.alpha) * fraction,
    )
}

private fun chipLinearLightBlend(start: Float, end: Float, fraction: Float): Float {
    val from = start.pow(CHIP_ARGB_EVALUATOR_GAMMA)
    val to = end.pow(CHIP_ARGB_EVALUATOR_GAMMA)
    return (from + (to - from) * fraction).coerceIn(0f, 1f).pow(1f / CHIP_ARGB_EVALUATOR_GAMMA)
}

/** The transfer exponent `ArgbEvaluator` decodes and encodes the color channels with. */
private const val CHIP_ARGB_EVALUATOR_GAMMA = 2.2f

/** The visible width of the focus outline: COUI default_focus_stroke_radius. */
private val ChipFocusStrokeWidth = 3.dp

/**
 * The COUI check tint spring: COUIChipDrawable.TintAnimation uses
 * COUISpringForce(response = 0.3f, bounce = 0f), i.e. a critically damped spring with stiffness
 * (2 * PI / 0.3)^2 ~= 438.65.
 */
private val ChipTintSpring = spring(dampingRatio = 1f, stiffness = 438.65f, visibilityThreshold = 0.001f)

/** COUIChipDrawable.CloseIconAnimation: COUISpringForce(response = 0.3f, bounce = 0f). */
private val ChipCloseIconSpring = spring(dampingRatio = 1f, stiffness = 438.65f, visibilityThreshold = 0.001f)

/** CloseIconAnimation.CLOSE_ICON_SCALE_MIN_VALUE: the close icon scales from 0.3 as it appears. */
private val ChipCloseIconMinScale = 0.3f

/** The width of a shown close icon slot: closeIconStartPadding + closeIconSize + closeIconEndPadding. */
private val ChipCloseIconSlotWidth =
    ChipDefaults.CloseIconStartPadding + ChipDefaults.CloseIconSize + ChipDefaults.CloseIconEndPadding

/** The close icon's press circle (COUI coui_chip_suggest_style_close_icon_touch_bounds_size). */
private val ChipCloseIconTouchSize = 24.dp

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
