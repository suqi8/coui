// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.theme.COUITheme

/**
 * A [FloatingActionButton] component with COUI style.
 *
 * While pressed, the whole button springs down to [FloatingActionButtonDefaults.PressedScale]
 * (COUI `COUIStateEffectDrawable.enableScaleEffect` -> `COUIPressFeedbackHelper`, spring
 * response 0.3 / bounce 0); the pressed tint overlay comes from `LocalIndication`.
 *
 * @param onClick The callback when the [FloatingActionButton] is clicked.
 * @param modifier The modifier to be applied to the [FloatingActionButton].
 * @param shape The shape of the [FloatingActionButton].
 * @param containerColor The color of the [FloatingActionButton].
 * @param shadowElevation The shadow elevation of the [FloatingActionButton].
 * @param minWidth The minimum width of the [FloatingActionButton].
 * @param minHeight The minimum height of the [FloatingActionButton].
 * @param interactionSource The [MutableInteractionSource] to be used for the [FloatingActionButton].
 * @param content The [Composable] content of the [FloatingActionButton].
 */
@Composable
fun FloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    containerColor: Color = COUITheme.colorScheme.primary,
    shadowElevation: Dp = FloatingActionButtonDefaults.ShadowElevation,
    minWidth: Dp = FloatingActionButtonDefaults.MinWidth,
    minHeight: Dp = FloatingActionButtonDefaults.MinHeight,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) FloatingActionButtonDefaults.PressedScale else 1f,
        // COUISpringForce: response 0.3 -> stiffness (2 * PI / 0.3)^2 = 438.65, bounce 0 -> damping 1.
        animationSpec = spring(dampingRatio = 1f, stiffness = 438.65f),
        label = "fabPressScale",
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .semantics { role = Role.Button }
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            },
        shape = shape,
        color = containerColor,
        shadowElevation = shadowElevation,
        interactionSource = interactionSource,
    ) {
        Box(
            modifier = Modifier.defaultMinSize(
                minWidth = minWidth,
                minHeight = minHeight,
            ),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

/** Contains default values used by [FloatingActionButton]. */
object FloatingActionButtonDefaults {
    /** The default minimum width of the [FloatingActionButton] (COUI coui_floating_button_size). */
    val MinWidth = 56.dp

    /** The default minimum height of the [FloatingActionButton] (COUI coui_floating_button_size). */
    val MinHeight = 56.dp

    /**
     * The default shadow elevation of the [FloatingActionButton].
     *
     * COUI 16 applies a constant elevation via `ShadowUtils.setElevationToFloatingActionButton`
     * with `support_shadow_size_level_three` (12dp) on the standard light model; the old
     * `coui_floating_button_close/open_elevation` dimens (5/8dp) are no longer referenced.
     */
    val ShadowElevation = 12.dp

    /**
     * The scale the button springs down to while pressed (COUI `COUIPressFeedbackHelper`
     * base scale end ratio 0.92; COUI interpolates 0.92..0.98 by view area, a 56dp
     * button resolves to ~0.925).
     */
    val PressedScale = 0.92f
}
