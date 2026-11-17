// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect.materialPanel
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.interfaces.HoldDownObserver
import io.github.suqi8.coui.kmp.squircle.squircleSurface
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A [IconButton] component with COUI style.
 *
 * Icon buttons help people take supplementary actions with a single tap. They’re used when a
 * compact button is required, such as in a toolbar or image list.
 *
 * @param onClick The callback when the [IconButton] is clicked.
 * @param modifier The modifier to be applied to the [IconButton]
 * @param enabled Whether the [IconButton] is enabled.
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param material Whether to draw the COUI 17 top-bar button material (filtered backdrop, shadow,
 *   edge lighting, touch spotlight, press and hover masks, and the `COUIViewTouchAnimator` press
 *   scale, drag-follow rubber band and drag deformation; see [COUIMaterialEffect.materialPanel]).
 *   When `false` the button is flat and uses the theme's default press indication. Defaults to
 *   [LocalCOUIMaterialEffectEnabled].
 * @param backgroundColor A solid fill drawn under the button, [Color.Unspecified] for none.
 * @param cornerRadius The corner radius of the [IconButton].
 * @param minHeight The minimum height of of the [IconButton].
 * @param minWidth The minimum width of the [IconButton].
 * @param redDot The toolbar menu red dot (`COUIToolbar.setRedDot` -> `COUIActionMenuView.drawRedDot`):
 *   `null` for none, `0` for a plain dot, a positive number for a count [Badge]. It hangs off the
 *   top end corner of the centered [IconButtonDefaults.RedDotAnchorSize] icon area by the COUI
 *   `coui_toolbar_menu_red_dot_*_offset` dimensions, without changing the button's size.
 * @param content The content of this icon button, typically an [Icon].
 */
@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    holdDownState: Boolean = false,
    material: Boolean = LocalCOUIMaterialEffectEnabled.current,
    backgroundColor: Color = Color.Unspecified,
    cornerRadius: Dp = IconButtonDefaults.CornerRadius,
    minHeight: Dp = IconButtonDefaults.MinHeight,
    minWidth: Dp = IconButtonDefaults.MinWidth,
    redDot: Int? = null,
    content: @Composable () -> Unit,
) {
    if (redDot == null) {
        IconButtonContainer(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            holdDownState = holdDownState,
            material = material,
            backgroundColor = backgroundColor,
            cornerRadius = cornerRadius,
            minHeight = minHeight,
            minWidth = minWidth,
            content = content,
        )
        return
    }
    Layout(
        content = {
            IconButtonContainer(
                onClick = onClick,
                enabled = enabled,
                holdDownState = holdDownState,
                material = material,
                backgroundColor = backgroundColor,
                cornerRadius = cornerRadius,
                minHeight = minHeight,
                minWidth = minWidth,
                content = content,
            )
            Badge(count = redDot)
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val button = measurables[0].measure(constraints)
        val badge = measurables.getOrNull(1)?.measure(Constraints())
        // COUIActionMenuView.drawRedDot: the badge's end edge sits the horizontal offset past the
        // icon area's end edge, its top the vertical offset above the icon area's top.
        val (horizontalOffset, verticalOffset) = when {
            redDot <= 0 -> IconButtonDefaults.RedDotHorizontalOffset to IconButtonDefaults.RedDotVerticalOffset
            redDot < 10 -> IconButtonDefaults.RedDotWithSmallNumberHorizontalOffset to IconButtonDefaults.RedDotWithNumberVerticalOffset
            redDot < 100 -> IconButtonDefaults.RedDotWithNumberHorizontalOffset to IconButtonDefaults.RedDotWithNumberVerticalOffset
            else -> IconButtonDefaults.RedDotWithBigNumberHorizontalOffset to IconButtonDefaults.RedDotWithNumberVerticalOffset
        }
        val anchorHalf = IconButtonDefaults.RedDotAnchorSize.roundToPx() / 2
        layout(button.width, button.height) {
            button.placeRelative(0, 0)
            if (badge != null) {
                badge.placeRelative(
                    x = button.width / 2 + anchorHalf + horizontalOffset.roundToPx() - badge.width,
                    y = button.height / 2 - anchorHalf - verticalOffset.roundToPx(),
                )
            }
        }
    }
}

/**
 * A text action for the top app bar's `actions`, mirroring a COUI 17 text menu item
 * (`COUIActionMenuItemView` without an icon, `ToolbarMaterialHelper.applyTextButtonSize`): a
 * [minWidth]-[maxWidth] wide, [minHeight] tall capsule on the top-bar button material, with a
 * single 14dp `sans-serif-medium` label in the theme color that ellipsizes past [maxWidth].
 *
 * @param text The label of the button.
 * @param onClick The callback when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 * @param enabled Whether the button is enabled.
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param material Whether to draw the COUI 17 top-bar button material (see [IconButton]).
 * @param backgroundColor A solid fill drawn under the button, [Color.Unspecified] for none.
 * @param minWidth The minimum width. COUI `coui_action_text_menu_item_min_width` (60dp).
 * @param maxWidth The maximum width. COUI `coui_action_bar_text_menu_item_max_width` (104dp).
 * @param minHeight The minimum height. COUI `coui_action_menu_item_min_height` (40dp).
 * @param insideMargin The padding around the label. COUI
 *   `coui_toolbar_text_menu_bg_padding_horizontal` (12dp, no vertical padding).
 * @param colors The [ToolbarTextButtonColors] of the label.
 */
@Composable
fun ToolbarTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    holdDownState: Boolean = false,
    material: Boolean = LocalCOUIMaterialEffectEnabled.current,
    backgroundColor: Color = Color.Unspecified,
    minWidth: Dp = IconButtonDefaults.TextButtonMinWidth,
    maxWidth: Dp = IconButtonDefaults.TextButtonMaxWidth,
    minHeight: Dp = IconButtonDefaults.MinHeight,
    insideMargin: PaddingValues = IconButtonDefaults.TextButtonInsideMargin,
    colors: ToolbarTextButtonColors = IconButtonDefaults.toolbarTextButtonColors(),
) {
    val textSize = with(LocalDensity.current) { IconButtonDefaults.TextButtonTextSize.toSp() }
    IconButtonContainer(
        onClick = onClick,
        modifier = modifier.widthIn(max = maxWidth),
        enabled = enabled,
        holdDownState = holdDownState,
        material = material,
        backgroundColor = backgroundColor,
        cornerRadius = minHeight / 2,
        minHeight = minHeight,
        minWidth = minWidth,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(insideMargin),
            color = colors.contentColor(enabled),
            fontSize = textSize,
            fontWeight = FontWeight.Medium,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            maxLines = 1,
        )
    }
}

@Composable
private fun IconButtonContainer(
    onClick: () -> Unit,
    enabled: Boolean,
    holdDownState: Boolean,
    material: Boolean,
    backgroundColor: Color,
    cornerRadius: Dp,
    minHeight: Dp,
    minWidth: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    HoldDownObserver(holdDownState, interactionSource)

    val currentOnClick by rememberUpdatedState(onClick)
    // The material draws its own press feedback, so the default indication would stack a square
    // highlight over the circle.
    val indication = if (material) null else LocalIndication.current
    val clickableModifier = remember(enabled, interactionSource, indication) {
        if (enabled) {
            Modifier.clickable(
                role = Role.Button,
                interactionSource = interactionSource,
                indication = indication,
                onClick = { currentOnClick() },
            )
        } else {
            Modifier
        }
    }

    // ToolbarMaterialEffectDelegate: a wider-than-tall button is a smooth round rect without a weight
    // (the soft capsule when fully rounded, default_g2_weight 2.45 otherwise), a square one a plain
    // circle (weight 2.0).
    var isCapsule by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
            .onSizeChanged { isCapsule = it.width != it.height }
            .materialPanel(
                interactionSource = interactionSource,
                cornerRadius = cornerRadius,
                enabled = material,
                motionEnabled = enabled,
            )
            .squircleSurface(
                color = backgroundColor,
                cornerRadius = cornerRadius,
                weight = if (isCapsule) COUIRoundCorner.UnspecifiedWeight else COUIRoundCorner.CircularWeight,
            )
            .then(clickableModifier),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

object IconButtonDefaults {

    /**
     * The default minimum width of the [IconButton]. COUI 17 `coui_action_menu_item_min_width`:
     * 40dp in `values-v37` (48dp before), applied to both the navigation and the menu buttons by
     * `ToolbarMaterialHelper.applyIconButtonSize`. 48dp is only the tiny-screen back button.
     */
    val MinWidth = 40.dp

    /**
     * The default minimum height of the [IconButton]. COUI `coui_action_menu_item_min_height` (40dp);
     * with the 40dp width the button is a circle (`isCapsule()` = width != height).
     */
    val MinHeight = 40.dp

    /** The default corner radius of the [IconButton]: half the 40dp circle. */
    val CornerRadius = 20.dp

    /** The minimum width of a [ToolbarTextButton]. COUI `coui_action_text_menu_item_min_width` (60dp). */
    val TextButtonMinWidth = 60.dp

    /** The maximum width of a [ToolbarTextButton]. COUI `coui_action_bar_text_menu_item_max_width` (104dp). */
    val TextButtonMaxWidth = 104.dp

    /**
     * The padding around a [ToolbarTextButton] label. COUI `coui_toolbar_text_menu_bg_padding_horizontal`
     * (12dp), applied horizontally only by `ToolbarMaterialHelper.applyTextButtonSize`.
     */
    val TextButtonInsideMargin = PaddingValues(horizontal = 12.dp)

    /**
     * The label size of a [ToolbarTextButton]: COUI `coui_action_text_menu_size` (14dp, a dp size
     * that ignores font scale; `COUIMenuItemStyle` is `sans-serif-medium`).
     */
    val TextButtonTextSize = 14.dp

    /**
     * The icon area a red dot hangs off: COUI `coui_toolbar_menu_bg_radius` (24dp), centered in
     * the button by `COUIActionMenuView.drawRedDot`.
     */
    val RedDotAnchorSize = 24.dp

    /** COUI `coui_toolbar_menu_red_dot_horizontal_offset` (1dp): the plain dot's end overhang. */
    val RedDotHorizontalOffset = 1.dp

    /** COUI `coui_toolbar_menu_red_dot_vertical_offset` (1dp): the plain dot's top overhang. */
    val RedDotVerticalOffset = 1.dp

    /** COUI `coui_toolbar_menu_red_dot_with_small_number_horizontal_offset` (6dp): counts under 10. */
    val RedDotWithSmallNumberHorizontalOffset = 6.dp

    /** COUI `coui_toolbar_menu_red_dot_with_number_horizontal_offset` (8dp): counts under 100. */
    val RedDotWithNumberHorizontalOffset = 8.dp

    /** COUI `coui_toolbar_menu_red_dot_with_big_number_horizontal_offset` (9dp): counts of 100 and more. */
    val RedDotWithBigNumberHorizontalOffset = 9.dp

    /** COUI `coui_toolbar_menu_red_dot_with_number_vertical_offset` (5dp): the count badge's top overhang. */
    val RedDotWithNumberVerticalOffset = 5.dp

    /**
     * The default [ToolbarTextButtonColors]. COUI `coui_menu_btn_text_color_selector`:
     * `?couiColorLabelTheme` enabled, `?couiColorLabelTertiary` disabled.
     */
    @Composable
    fun toolbarTextButtonColors(
        contentColor: Color = COUITheme.colorScheme.primary,
        disabledContentColor: Color = COUITheme.colorScheme.disabledNeutral,
    ): ToolbarTextButtonColors = remember(contentColor, disabledContentColor) {
        ToolbarTextButtonColors(
            contentColor = contentColor,
            disabledContentColor = disabledContentColor,
        )
    }
}

/**
 * The label colors of a [ToolbarTextButton].
 *
 * @param contentColor The label color when enabled.
 * @param disabledContentColor The label color when disabled.
 */
@Immutable
data class ToolbarTextButtonColors(
    val contentColor: Color,
    val disabledContentColor: Color,
) {
    @Stable
    internal fun contentColor(enabled: Boolean): Color = if (enabled) contentColor else disabledContentColor
}
