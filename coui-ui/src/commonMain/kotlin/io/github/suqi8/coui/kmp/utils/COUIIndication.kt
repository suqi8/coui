// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import io.github.suqi8.coui.kmp.interfaces.HoldDownInteraction
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

// COUI couiColorPress #1F000000 / #33FFFFFF and couiColorHover #14000000 / #26FFFFFF.
private val DefaultPressLight = Color(0x1F000000)
private val DefaultPressDark = Color(0x33FFFFFF)
private val DefaultHoverLight = Color(0x14000000)
private val DefaultHoverDark = Color(0x26FFFFFF)

// COUI couiColorFocus (coui_color_focus_blue) #1F0080FF / #331A8CFF.
private val DefaultFocusLight = Color(0x1F0080FF)
private val DefaultFocusDark = Color(0x331A8CFF)

// COUI: COUISpringForce response 0.3 / bounce 0 -> stiffness (2 * PI / 0.3)^2 = 438.65, damping ratio 1.
private val StateLayerSpring = spring<Float>(dampingRatio = 1f, stiffness = 438.65f)

/**
 * The press progress a quick tap is carried to before the layer fades out
 * (COUIMaskEffectDrawable DEFAULT_MIN_PROGRESS_FOR_TOUCH_ENTER_ANIMATION).
 */
private val PressMinVisibleProgress = 0.7f

/**
 * An interaction for the slide-to-select gesture of a list: the finger, pressed on one item, slides
 * onto another, which becomes the touch target (`COUITouchListView` calls
 * `COUIMaskEffectDrawable.setTouchSelectEntered` / `setTouchSelectExited`, touch type
 * `TOUCH_TYPE_SELECTED`). [COUIIndication] shows or clears the press layer of such an item at once,
 * without the spring (`COUIMaskEffectDrawable.onViewStateChanged`, touch type 1).
 *
 * @see Enter
 * @see Exit
 */
interface COUITouchSelectInteraction : Interaction {
    /** The sliding finger entered the item. */
    class Enter : COUITouchSelectInteraction

    /**
     * The sliding finger left the item, or the gesture ended.
     *
     * @property enter The [Enter] interaction this one ends.
     */
    class Exit(
        val enter: Enter,
    ) : COUITouchSelectInteraction
}

/**
 * An interaction reporting the selected state of a container item. [COUIIndication] draws a
 * selected item with its focus layer, as `COUIMaskEffectDrawable` does for `state_selected` on a
 * container mask (`mMaskType == 1`, `mEnableSelectedState` on by default).
 *
 * Emit it with [SelectedInteractionEffect], or emit [Select] / [Unselect] directly.
 *
 * @see Select
 * @see Unselect
 */
interface COUISelectInteraction : Interaction {
    /** The item became selected. */
    class Select : COUISelectInteraction

    /**
     * The item stopped being selected.
     *
     * @property select The [Select] interaction this one ends.
     */
    class Unselect(
        val select: Select,
    ) : COUISelectInteraction
}

/**
 * Emits [COUISelectInteraction]s into [interactionSource] that follow [selected], so a
 * [COUIIndication] on the same source draws its focus layer while the item is selected. The
 * selection is withdrawn when this effect leaves the composition.
 *
 * @param selected Whether the item is selected.
 * @param interactionSource The interaction source the item's indication reads.
 */
@Composable
fun SelectedInteractionEffect(
    selected: Boolean,
    interactionSource: MutableInteractionSource,
) {
    val holder = remember(interactionSource) { SelectInteractionHolder() }
    LaunchedEffect(selected, interactionSource) {
        val previous = holder.select
        if (selected && previous == null) {
            val select = COUISelectInteraction.Select()
            holder.select = select
            interactionSource.emit(select)
        } else if (!selected && previous != null) {
            holder.select = null
            interactionSource.emit(COUISelectInteraction.Unselect(previous))
        }
    }
    DisposableEffect(interactionSource) {
        onDispose {
            val previous = holder.select ?: return@onDispose
            holder.select = null
            interactionSource.tryEmit(COUISelectInteraction.Unselect(previous))
        }
    }
}

/** The [COUISelectInteraction.Select] a [SelectedInteractionEffect] has emitted and not yet ended. */
private class SelectInteractionHolder {
    var select: COUISelectInteraction.Select? = null
}

/**
 * COUI default [Indication]: the rectangular state layers of `COUIMaskEffectDrawable`.
 *
 * Hover, focus and press are three independent layers, each driven by its own response 0.3 /
 * bounce 0 spring and drawn in that order over the content. A release that comes before the press
 * layer reached 70% of its progress first carries it on to 70% so quick taps stay visible.
 *
 * The focus layer also shows while a [COUISelectInteraction] reports the item selected, and a
 * [COUITouchSelectInteraction] shows or clears the press layer without animation.
 *
 * Colors left [Color.Unspecified] fall back to the COUI light or dark values, picked by the
 * luminance of [color] — the content color (e.g. `labelPrimary`), which is bright in dark theme.
 * [COUITheme][io.github.suqi8.coui.kmp.theme.COUITheme] passes the `press`, `hover` and `focus`
 * slots of its color scheme.
 *
 * @param color The content color used to pick the light or dark fallbacks.
 * @param pressColor The pressed layer (COUI couiColorPress).
 * @param hoverColor The hovered layer (COUI couiColorHover).
 * @param focusColor The focused layer (COUI couiColorFocus).
 */
@Immutable
class COUIIndication(
    private val color: Color = Color.Black,
    private val pressColor: Color = Color.Unspecified,
    private val hoverColor: Color = Color.Unspecified,
    private val focusColor: Color = Color.Unspecified,
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        val isDarkTheme = color.luminance() > 0.5f
        return COUIIndicationInstance(
            interactionSource = interactionSource,
            pressColor = pressColor.takeOrElse { if (isDarkTheme) DefaultPressDark else DefaultPressLight },
            hoverColor = hoverColor.takeOrElse { if (isDarkTheme) DefaultHoverDark else DefaultHoverLight },
            focusColor = focusColor.takeOrElse { if (isDarkTheme) DefaultFocusDark else DefaultFocusLight },
        )
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + pressColor.hashCode()
        result = 31 * result + hoverColor.hashCode()
        result = 31 * result + focusColor.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is COUIIndication) return false
        return color == other.color &&
            pressColor == other.pressColor &&
            hoverColor == other.hoverColor &&
            focusColor == other.focusColor
    }

    private class COUIIndicationInstance(
        private val interactionSource: InteractionSource,
        private val pressColor: Color,
        private val hoverColor: Color,
        private val focusColor: Color,
    ) : Modifier.Node(),
        DrawModifierNode {
        private var isPressed = false
        private var isHovered = false
        private var isFocused = false
        private var isSelected = false
        private var isTouchSelected = false
        private var isHoldDown = false
        private val pressProgress = Animatable(0f)
        private val hoverProgress = Animatable(0f)
        private val focusProgress = Animatable(0f)
        private var pressJob: Job? = null
        private var hoverJob: Job? = null
        private var focusJob: Job? = null

        private fun animateLayer(previous: Job?, layer: Animatable<Float, AnimationVector1D>, target: Float): Job {
            previous?.cancel()
            return coroutineScope.launch { layer.animateTo(target, StateLayerSpring) }
        }

        private fun updateFocus() {
            focusJob = animateLayer(focusJob, focusProgress, if (isFocused || isSelected) 1f else 0f)
        }

        private fun snapPress(target: Float) {
            pressJob?.cancel()
            pressJob = coroutineScope.launch { pressProgress.snapTo(target) }
        }

        private fun updatePress() {
            // Hold-down keeps the press layer visible after the press is released.
            val active = isPressed || isHoldDown || isTouchSelected
            pressJob?.cancel()
            pressJob = coroutineScope.launch {
                if (active) {
                    pressProgress.animateTo(1f, StateLayerSpring)
                } else if (pressProgress.value > 0f) {
                    if (pressProgress.value < PressMinVisibleProgress) {
                        try {
                            pressProgress.animateTo(1f, StateLayerSpring) {
                                if (value >= PressMinVisibleProgress) throw PressMinVisibleProgressReached()
                            }
                        } catch (_: PressMinVisibleProgressReached) {
                            // Minimum visible progress reached; fall through to the fade-out.
                        }
                    }
                    pressProgress.animateTo(0f, StateLayerSpring)
                }
            }
        }

        override fun onAttach() {
            coroutineScope.launch {
                interactionSource.interactions.collect { interaction ->
                    when (interaction) {
                        is PressInteraction.Press -> if (!isPressed) {
                            isPressed = true
                            updatePress()
                        }

                        is PressInteraction.Release, is PressInteraction.Cancel -> if (isPressed) {
                            isPressed = false
                            updatePress()
                        }

                        is HoldDownInteraction.HoldDown -> if (!isHoldDown) {
                            isHoldDown = true
                            updatePress()
                        }

                        is HoldDownInteraction.Release -> if (isHoldDown) {
                            isHoldDown = false
                            updatePress()
                        }

                        is HoverInteraction.Enter -> if (!isHovered) {
                            isHovered = true
                            hoverJob = animateLayer(hoverJob, hoverProgress, 1f)
                        }

                        is HoverInteraction.Exit -> if (isHovered) {
                            isHovered = false
                            hoverJob = animateLayer(hoverJob, hoverProgress, 0f)
                        }

                        is FocusInteraction.Focus -> if (!isFocused) {
                            isFocused = true
                            updateFocus()
                        }

                        is FocusInteraction.Unfocus -> if (isFocused) {
                            isFocused = false
                            updateFocus()
                        }

                        is COUISelectInteraction.Select -> if (!isSelected) {
                            isSelected = true
                            updateFocus()
                        }

                        is COUISelectInteraction.Unselect -> if (isSelected) {
                            isSelected = false
                            updateFocus()
                        }

                        // COUIMaskEffectDrawable: a touch-select enter / exit moves the press
                        // layer without animation.
                        is COUITouchSelectInteraction.Enter -> if (!isTouchSelected) {
                            isTouchSelected = true
                            snapPress(1f)
                        }

                        is COUITouchSelectInteraction.Exit -> if (isTouchSelected) {
                            isTouchSelected = false
                            if (isPressed || isHoldDown) updatePress() else snapPress(0f)
                        }
                    }
                }
            }
        }

        override fun ContentDrawScope.draw() {
            drawContent()
            val hover = hoverProgress.value
            if (hover > 0f) drawRect(color = hoverColor, alpha = hover.coerceAtMost(1f))
            val focus = focusProgress.value
            if (focus > 0f) drawRect(color = focusColor, alpha = focus.coerceAtMost(1f))
            val press = pressProgress.value
            if (press > 0f) drawRect(color = pressColor, alpha = press.coerceAtMost(1f))
        }
    }
}

/** Thrown to stop the deferred press-in animation once [PressMinVisibleProgress] is reached. */
private class PressMinVisibleProgressReached : CancellationException("Press layer reached the minimum visible progress")
