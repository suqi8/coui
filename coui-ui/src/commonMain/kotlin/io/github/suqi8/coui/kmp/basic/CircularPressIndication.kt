// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * A centred, unbounded circular press highlight drawn **behind** the content, the press feedback of
 * the small COUI compound buttons.
 *
 * `COUICheckBox` installs a `COUIMaskRippleDrawable` with a circle mask of
 * `checkbox_ripple_bg_radius` (20dp) in `couiColorPress`; that drawable is a framework
 * `RippleDrawable`, so the press plays the platform ripple instead of the `COUIMaskEffectDrawable`
 * springs. This node follows the platform ripple timeline (the same one Compose Material mirrors):
 * the highlight fades in over 75ms while its radius grows from 60% to 100% over 225ms, and fades
 * out over 150ms once released (after the fade-in finished). The ripple origin is pinned to the
 * centre, which on a 24dp control is indistinguishable from the touch point.
 *
 * @param radius The radius of the highlight circle.
 * @param color The highlight color, drawn at full alpha when the press settles.
 */
internal class CircularPressIndication(
    private val radius: Dp,
    private val color: Color,
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode = CircularPressIndicationNode(interactionSource, radius, color)

    override fun hashCode(): Int = 31 * radius.hashCode() + color.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CircularPressIndication) return false
        return radius == other.radius && color == other.color
    }

    private class CircularPressIndicationNode(
        private val interactionSource: InteractionSource,
        private val radius: Dp,
        private val color: Color,
    ) : Modifier.Node(),
        DrawModifierNode {
        private val alpha = Animatable(0f)
        private val growth = Animatable(RippleStartRadiusFraction)
        private var pressJob: Job? = null

        override fun onAttach() {
            coroutineScope.launch {
                var pressed = false
                interactionSource.interactions.collect { interaction ->
                    when (interaction) {
                        is PressInteraction.Press -> if (!pressed) {
                            pressed = true
                            press()
                        }

                        is PressInteraction.Release, is PressInteraction.Cancel -> if (pressed) {
                            pressed = false
                            release()
                        }
                    }
                }
            }
        }

        private fun press() {
            pressJob?.cancel()
            pressJob = coroutineScope.launch {
                growth.snapTo(RippleStartRadiusFraction)
                alpha.snapTo(0f)
                launch { growth.animateTo(1f, tween(RippleGrowDurationMillis, easing = FastOutSlowInEasing)) }
                alpha.animateTo(1f, tween(RippleFadeInDurationMillis, easing = LinearEasing))
            }
        }

        private fun release() {
            val enter = pressJob
            pressJob = coroutineScope.launch {
                // A quick tap still shows the full fade-in before the highlight leaves.
                enter?.join()
                alpha.animateTo(0f, tween(RippleFadeOutDurationMillis, easing = LinearEasing))
            }
        }

        override fun ContentDrawScope.draw() {
            val a = alpha.value
            if (a > 0f) {
                drawCircle(
                    color = color,
                    radius = radius.toPx() * growth.value,
                    center = center,
                    alpha = a.coerceIn(0f, 1f),
                )
            }
            drawContent()
        }
    }
}

// Platform ripple timeline (RippleAnimation in Compose Material, RippleForeground on Android).
private val RippleStartRadiusFraction = 0.6f
private val RippleFadeInDurationMillis = 75
private val RippleGrowDurationMillis = 225
private val RippleFadeOutDurationMillis = 150
