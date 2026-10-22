// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect.materialPanel
import io.github.suqi8.coui.kmp.interfaces.HoldDownObserver
import io.github.suqi8.coui.kmp.squircle.squircleSurface

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
 *   edge lighting, touch spotlight and press feedback, see [COUIMaterialEffect.materialPanel]).
 *   When `false` the button is flat and uses the theme's default press indication.
 * @param backgroundColor A solid fill drawn under the button, [Color.Unspecified] for none.
 * @param cornerRadius The corner radius of the [IconButton].
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
    material: Boolean = true,
    backgroundColor: Color = Color.Unspecified,
    cornerRadius: Dp = IconButtonDefaults.CornerRadius,
    minHeight: Dp = IconButtonDefaults.MinHeight,
    minWidth: Dp = IconButtonDefaults.MinWidth,
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

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
            .materialPanel(
                interactionSource = interactionSource,
                cornerRadius = cornerRadius,
                enabled = material,
            )
            .squircleSurface(color = backgroundColor, cornerRadius = cornerRadius)
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
}
