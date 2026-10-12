// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect.materialPanel
import io.github.suqi8.coui.kmp.interfaces.HoldDownObserver

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
 * @param backgroundColor The material fill of the [IconButton]. Defaults to the COUI 17 top-bar
 *   material wash (a translucent light/dark layer), matching `ToolbarMaterialEffectDelegate`, whose
 *   button view has a transparent background and takes its look from the backdrop filter plus its
 *   colour layers. Pass [Color.Unspecified] for a genuinely flat (no-fill) icon button.
 * @param cornerRadius The corner radius of the [IconButton]. COUI 17 top-bar buttons are circular
 *   (`coui_toolbar_menu_bg_radius` = half the button size, e.g. 24dp for a 48dp circle).
 * @param minHeight The minimum height of of the [IconButton].
 * @param minWidth The minimum width of the [IconButton].
 * @param content The content of this icon button, typically an [Icon].
 */
@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    holdDownState: Boolean = false,
    backgroundColor: Color = COUIMaterialEffect.materialColor(),
    cornerRadius: Dp = IconButtonDefaults.CornerRadius,
    minHeight: Dp = IconButtonDefaults.MinHeight,
    minWidth: Dp = IconButtonDefaults.MinWidth,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    HoldDownObserver(holdDownState, interactionSource)

    val currentOnClick by androidx.compose.runtime.rememberUpdatedState(onClick)
    val clickableModifier = remember(enabled, interactionSource) {
        if (enabled) {
            Modifier.clickable(
                role = Role.Button,
                interactionSource = interactionSource,
                onClick = { currentOnClick() },
            )
        } else {
            Modifier
        }
    }

    // COUI 17 top-bar buttons are circular (width == height); the corner radius is half the size.
    val buttonSize = maxOf(minWidth, minHeight)
    val resolvedCornerRadius = cornerRadius.coerceAtMost(buttonSize / 2f)

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = buttonSize, minHeight = buttonSize)
            .then(
                // COUI 17 unified material panel: fill + stroke + shadow + spotlight + caustic +
                // press scale, from the shared COUIMaterialEffect pipeline.
                if (backgroundColor != Color.Unspecified) {
                    Modifier.materialPanel(
                        interactionSource = interactionSource,
                        panelColor = backgroundColor,
                        size = buttonSize,
                        cornerRadius = resolvedCornerRadius,
                    )
                } else {
                    Modifier
                },
            )
            .then(clickableModifier),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

object IconButtonDefaults {

    /**
     * The default minimum width of the [IconButton].
     * COUI `coui_action_menu_item_min_width` (48dp); the button is circular, so width == height.
     */
    val MinWidth = 48.dp

    /**
     * The default minimum height of the [IconButton].
     * COUI `coui_action_menu_item_min_height` (40dp); the button is circular, so width == height
     * (48dp) when used as a top-bar icon button.
     */
    val MinHeight = 48.dp

    /**
     * The default corner radius of the [IconButton]. COUI 17 top-bar buttons are circular, so the
     * radius is half the button size (48dp / 2 = 24dp, `coui_toolbar_menu_bg_radius`).
     */
    val CornerRadius = 24.dp
}
