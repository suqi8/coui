// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.theme.COUITheme
import com.suqi8.coui.kmp.theme.LocalContentColor

// COUIMoveEaseInterpolator
private val COUIMoveEase = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)

/**
 * A [FloatingActionButton] component with COUI style.
 *
 * @param onClick The callback when the [FloatingActionButton] is clicked.
 * @param modifier The modifier to be applied to the [FloatingActionButton].
 * @param shape The shape of the [FloatingActionButton]. Default is [CircleShape].
 * @param containerColor The color of the [FloatingActionButton].
 * @param contentColor The color of the content.
 * @param shadowElevation The shadow elevation of the [FloatingActionButton].
 * @param minWidth The minimum width of the [FloatingActionButton]. Default is 56.dp.
 * @param minHeight The minimum height of the [FloatingActionButton]. Default is 56.dp.
 * @param defaultWindowInsetsPadding Whether to apply default window insets padding to the [FloatingActionButton].
 * @param interactionSource The interaction source to handle click events.
 * @param content The [Composable] content of the [FloatingActionButton].
 */
@Composable
fun FloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape, // OvalShape
    containerColor: Color = COUITheme.colorScheme.primary,
    contentColor: Color = COUITheme.colorScheme.onPrimary,
    shadowElevation: Dp = 8.dp, // coui_floating_button_open_elevation
    minWidth: Dp = 56.dp, // coui_floating_button_size
    minHeight: Dp = 56.dp, // coui_floating_button_size
    defaultWindowInsetsPadding: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    val currentOnClick by rememberUpdatedState(onClick)
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animation Logic
    val scale = remember { Animatable(1f) }
    // Brightness Logic
    val brightnessAlpha = remember { Animatable(0f) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            // Scale to 0.9f
            scale.animateTo(0.9f, tween(200, easing = COUIMoveEase))
            brightnessAlpha.animateTo(0.1f, tween(200, easing = COUIMoveEase))
        } else {
            scale.animateTo(1.0f, tween(340, easing = COUIMoveEase))
            brightnessAlpha.animateTo(0f, tween(340, easing = COUIMoveEase))
        }
    }

    // coui_floating_button_elevation_color
    val shadowColor = COUITheme.colorScheme.fabShadow

    Box(
        modifier = modifier
            .then(
                if (defaultWindowInsetsPadding) {
                    Modifier
                        .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                } else Modifier
            )
            .semantics { role = Role.Button }
            .shadow(
                elevation = shadowElevation,
                shape = shape,
                spotColor = shadowColor,
                ambientColor = shadowColor
            )
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .clip(shape)
            .background(containerColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // COUI handles press feedback manually via scale/brightness
                onClick = currentOnClick
            )
            .defaultMinSize(
                minWidth = minWidth,
                minHeight = minHeight,
            ),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor
        ) {
            content()
        }

        // Press Dimming Overlay
        //
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = brightnessAlpha.value))
        )
    }
}