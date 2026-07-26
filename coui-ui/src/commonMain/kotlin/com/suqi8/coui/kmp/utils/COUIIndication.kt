// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import com.suqi8.coui.kmp.interfaces.HoldDownInteraction
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

// COUI state layer tokens (colors.xml): coui_color_hover #14000000 / coui_color_hover_dark
// #26FFFFFF, coui_color_press #1F000000 / coui_color_press_dark #33FFFFFF.
private const val HOVER_ALPHA_LIGHT = 0x14 / 255f
private const val HOVER_ALPHA_DARK = 0x26 / 255f
private const val PRESS_ALPHA_LIGHT = 0x1F / 255f
private const val PRESS_ALPHA_DARK = 0x33 / 255f

// Real COUI focus feedback is a COUIStrokeDrawable stroke ring; keep the translucent layer
// as an approximation.
private const val FOCUS_ALPHA_DELTA = 0.08f

// COUIMaskEffectDrawable animates its state layers with COUISpringForce response 0.3 / bounce 0
// -> stiffness (2 * PI / 0.3)^2 = 438.65, damping ratio 1 (critically damped).
private val StateLayerSpring = spring<Float>(dampingRatio = 1f, stiffness = 438.65f)

/**
 * COUI default [Indication] that draws a rectangular COUI state layer overlay.
 *
 * The overlay follows the COUI press/hover tokens: black at 12.2% (press, `coui_color_press`)
 * / 7.8% (hover, `coui_color_hover`) in light theme, and white at 20% (press,
 * `coui_color_press_dark`) / 14.9% (hover, `coui_color_hover_dark`) in dark theme. The theme
 * is inferred from the luminance of [color] — the content color (e.g. `onBackground`), which
 * is bright in dark theme. State transitions animate with the COUI mask effect spring
 * (response 0.3 / bounce 0).
 */
@Immutable
class COUIIndication(
    private val color: Color = Color.Black,
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode = COUIIndicationInstance(interactionSource, isDarkTheme = color.luminance() > 0.5f)

    override fun hashCode(): Int = color.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is COUIIndication) return false
        if (color != other.color) return false
        return true
    }

    private class COUIIndicationInstance(
        private val interactionSource: InteractionSource,
        isDarkTheme: Boolean,
    ) : Modifier.Node(),
        DrawModifierNode {
        private val overlayColor = if (isDarkTheme) Color.White else Color.Black
        private val hoverAlpha = if (isDarkTheme) HOVER_ALPHA_DARK else HOVER_ALPHA_LIGHT
        private val pressAlpha = if (isDarkTheme) PRESS_ALPHA_DARK else PRESS_ALPHA_LIGHT
        private var isPressed = false
        private var isHovered = false
        private var isFocused = false
        private var isHoldDown = false
        private val animatedAlpha = Animatable(0f)
        private var pressedAnimation: Job? = null
        private var restingAnimation: Job? = null

        private fun updateStates() {
            var targetAlpha = 0.0f
            if (isHovered) targetAlpha += hoverAlpha
            if (isFocused) targetAlpha += FOCUS_ALPHA_DELTA
            // Hold-down keeps the press layer visible after the press is released.
            if (isPressed || isHoldDown) targetAlpha += pressAlpha

            if (targetAlpha == 0.0f) {
                restingAnimation?.cancel()
                restingAnimation =
                    coroutineScope.launch {
                        if (coroutineContext.isActive) {
                            pressedAnimation?.join()
                            animatedAlpha.animateTo(
                                targetValue = targetAlpha,
                                animationSpec = StateLayerSpring,
                            )
                        }
                    }
            } else {
                pressedAnimation?.cancel()
                restingAnimation?.cancel()
                pressedAnimation =
                    coroutineScope.launch {
                        animatedAlpha.animateTo(
                            targetValue = targetAlpha,
                            animationSpec = StateLayerSpring,
                        )
                    }
            }
        }

        override fun onAttach() {
            coroutineScope.launch {
                interactionSource.interactions.collect { interaction ->
                    val previousPressed = isPressed
                    val previousHovered = isHovered
                    val previousFocused = isFocused
                    val previousHoldDown = isHoldDown

                    when (interaction) {
                        is PressInteraction.Press -> isPressed = true
                        is PressInteraction.Release, is PressInteraction.Cancel -> isPressed = false
                        is HoverInteraction.Enter -> isHovered = true
                        is HoverInteraction.Exit -> isHovered = false
                        is FocusInteraction.Focus -> isFocused = true
                        is FocusInteraction.Unfocus -> isFocused = false
                        is HoldDownInteraction.HoldDown -> isHoldDown = true
                        is HoldDownInteraction.Release -> isHoldDown = false
                        else -> return@collect
                    }

                    val stateChanged = previousPressed != isPressed ||
                        previousHovered != isHovered ||
                        previousFocused != isFocused ||
                        previousHoldDown != isHoldDown

                    if (stateChanged) {
                        updateStates()
                    }
                }
            }
        }

        override fun ContentDrawScope.draw() {
            drawContent()
            val alpha = animatedAlpha.value
            if (alpha > 0f) {
                drawRect(color = overlayColor, alpha = alpha, size = size)
            }
        }
    }
}
