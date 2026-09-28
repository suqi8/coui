// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.interfaces.HoldDownObserver
import io.github.suqi8.coui.kmp.squircle.squircleSurface
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
 * @param backgroundColor The background color of the [IconButton]. COUI 17 top-bar buttons have
 *   no panel fill (`Color.Unspecified`, default); the pressed feedback is a circular ripple, not a
 *   background tint. Pass a color only for a genuinely filled button.
 * @param cornerRadius The corner radius of of the [IconButton].
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
    backgroundColor: Color = Color.Unspecified,
    cornerRadius: Dp = IconButtonDefaults.CornerRadius,
    minHeight: Dp = IconButtonDefaults.MinHeight,
    minWidth: Dp = IconButtonDefaults.MinWidth,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    HoldDownObserver(holdDownState, interactionSource)

    // COUI 17 press feedback: a circular ripple (COUIMaskRippleDrawable, couiColorPress
    // #1F000000 / #33FFFFFF) plus the COUIViewTouchAnimator scale-down to 0.92.
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 1f, stiffness = 438.65f),
        label = "iconButtonPressScale",
    )
    val rippleColor = COUITheme.colorScheme.onSurface.copy(alpha = if (pressed) 0.12f else 0f)

    val currentOnClick by rememberUpdatedState(onClick)
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

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .squircleSurface(color = backgroundColor, cornerRadius = cornerRadius)
            .then(clickableModifier)
            .drawWithContent {
                drawContent()
                if (rippleColor.alpha > 0f) {
                    // Circular press ripple centered in the button, the way COUIMaskRippleDrawable
                    // masks the press tint to a circle over the icon.
                    drawCircle(
                        color = rippleColor,
                        radius = size.minDimension / 2f,
                        center = size.center,
                    )
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

object IconButtonDefaults {

    /**
     * The default minimum width of the [IconButton].
     * COUI `coui_action_menu_item_min_width` (48dp).
     */
    val MinWidth = 48.dp

    /**
     * The default minimum height of the [IconButton].
     * COUI `coui_action_menu_item_min_height` (40dp).
     */
    val MinHeight = 40.dp

    /**
     * The default corner radius of the [IconButton] (COUI coui_toolbar_menu_bg_radius).
     */
    val CornerRadius = 24.dp
}
