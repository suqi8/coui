// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.unit.Dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.StateLayerAnimator
import io.github.suqi8.coui.kmp.utils.defaultFocusLayerColor
import io.github.suqi8.coui.kmp.utils.defaultHoverLayerColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Creates and remembers a [CircularPressIndication] in the theme's state colors: the
 * `COUIMaskRippleDrawable` circle of the small COUI controls (check boxes, radio buttons, the
 * clear and icon buttons, the preference category icons), drawn behind the content.
 *
 * @param radius The radius of the circle (COUI `checkbox_ripple_bg_radius`, `icon_ripple_bg_radius`
 *   or the component's own mask radius).
 * @param color The press highlight (`couiColorPress`).
 * @param hoverColor The hover circle (`couiColorHover`).
 * @param focusColor The keyboard-focus circle (`couiColorFocus`).
 */
@Composable
fun rememberCircularPressIndication(
    radius: Dp,
    color: Color = COUITheme.colorScheme.press,
    hoverColor: Color = COUITheme.colorScheme.hover,
    focusColor: Color = COUITheme.colorScheme.focus,
): Indication = remember(radius, color, hoverColor, focusColor) {
    CircularPressIndication(radius = radius, color = color, hoverColor = hoverColor, focusColor = focusColor)
}

/**
 * A centred, unbounded circular press highlight drawn **behind** the content, the press feedback of
 * the small COUI compound buttons. Use [rememberCircularPressIndication] for the theme colors.
 *
 * `COUICheckBox` installs a `COUIMaskRippleDrawable` with a circle mask of
 * `checkbox_ripple_bg_radius` (20dp) in `couiColorPress`; that drawable is a framework
 * `RippleDrawable`, so the press plays the platform ripple instead of the `COUIMaskEffectDrawable`
 * springs. This node follows the platform ripple timeline (the same one Compose Material mirrors):
 * the highlight fades in over 75ms while its radius grows from 60% to 100% over 225ms, and fades
 * out over 150ms once released (after the fade-in finished). The ripple origin is pinned to the
 * centre, which on a 24dp control is indistinguishable from the touch point.
 *
 * Under the ripple, `COUIMaskRippleDrawable` draws a hover circle (`couiColorHover`) and a focus
 * circle (`couiColorFocus`) of the full [radius], each on a response 0.3 / bounce 0 spring
 * (`StateEffectAnimator`), for a pointer hovering the control and for keyboard focus.
 *
 * The circle is not clipped by the element, so it may reach past small content; keep the element's
 * parent from clipping it.
 *
 * @param radius The radius of the highlight circle.
 * @param color The highlight color, drawn at full alpha when the press settles.
 * @param hoverColor The hover circle (`couiColorHover`). [Color.Unspecified] picks the COUI light or
 *   dark value matching [color].
 * @param focusColor The focus circle (`couiColorFocus`). [Color.Unspecified] picks the COUI light or
 *   dark value matching [color].
 */
@Immutable
class CircularPressIndication(
    private val radius: Dp,
    private val color: Color,
    private val hoverColor: Color = Color.Unspecified,
    private val focusColor: Color = Color.Unspecified,
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        // couiColorPress is black-based in light themes and white-based in dark ones.
        val isDark = color.isSpecified && color.luminance() > 0.5f
        return CircularPressIndicationNode(
            interactionSource = interactionSource,
            radius = radius,
            color = color,
            hoverColor = hoverColor.takeOrElse { defaultHoverLayerColor(isDark) },
            focusColor = focusColor.takeOrElse { defaultFocusLayerColor(isDark) },
        )
    }

    override fun hashCode(): Int {
        var result = radius.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + hoverColor.hashCode()
        result = 31 * result + focusColor.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CircularPressIndication) return false
        return radius == other.radius &&
            color == other.color &&
            hoverColor == other.hoverColor &&
            focusColor == other.focusColor
    }

    private class CircularPressIndicationNode(
        private val interactionSource: InteractionSource,
        private val radius: Dp,
        private val color: Color,
        private val hoverColor: Color,
        private val focusColor: Color,
    ) : Modifier.Node(),
        DrawModifierNode {
        private val alpha = Animatable(0f)
        private val growth = Animatable(RippleStartRadiusFraction)
        private var pressJob: Job? = null
        private val hoverLayer = StateLayerAnimator()
        private val focusLayer = StateLayerAnimator()
        private var hoverJob: Job? = null
        private var focusJob: Job? = null

        override fun onAttach() {
            coroutineScope.launch {
                var pressed = false
                var hovered = false
                var focused = false
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

                        is HoverInteraction.Enter -> if (!hovered) {
                            hovered = true
                            hoverJob = animateLayer(hoverJob, hoverLayer, 1f)
                        }

                        is HoverInteraction.Exit -> if (hovered) {
                            hovered = false
                            hoverJob = animateLayer(hoverJob, hoverLayer, 0f)
                        }

                        is FocusInteraction.Focus -> if (!focused) {
                            focused = true
                            focusJob = animateLayer(focusJob, focusLayer, 1f)
                        }

                        is FocusInteraction.Unfocus -> if (focused) {
                            focused = false
                            focusJob = animateLayer(focusJob, focusLayer, 0f)
                        }
                    }
                }
            }
        }

        private fun animateLayer(previous: Job?, layer: StateLayerAnimator, target: Float): Job {
            previous?.cancel()
            return coroutineScope.launch { layer.animateTo(target) }
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
            val fullRadius = radius.toPx()
            // COUIMaskRippleDrawable.draw: hover, then focus, then the ripple, all behind the content.
            val hover = hoverLayer.value
            if (hover > 0f) drawCircle(color = hoverColor, radius = fullRadius, center = center, alpha = hover.coerceIn(0f, 1f))
            val focus = focusLayer.value
            if (focus > 0f) drawCircle(color = focusColor, radius = fullRadius, center = center, alpha = focus.coerceIn(0f, 1f))
            val a = alpha.value
            if (a > 0f) {
                drawCircle(
                    color = color,
                    radius = fullRadius * growth.value,
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
