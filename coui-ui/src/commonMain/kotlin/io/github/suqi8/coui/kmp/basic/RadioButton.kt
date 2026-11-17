// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A [RadioButton] component with COUI style.
 *
 * Unselected is an outline ring, selected is a primary disc with a solid center dot; toggling
 * replays the COUI AVD transitions (cross-fade with a 1 -> 1.08 -> 1 pulse) while enabled; a
 * disabled radio button jumps to its new state, as coui_btn_radio.xml only animates enabled changes.
 *
 * The button never scales on press. COUI themes a standalone `RadioButton` through the framework
 * `Widget.Material.CompoundButton.RadioButton` style, whose background is the borderless
 * platform ripple; an interactive [RadioButton] therefore shows a centred circular press highlight
 * as large as that ripple (half the 24dp box diagonal). Pass `onClick = null` when a surrounding
 * row handles the click, as the COUI radio and mark preferences do.
 *
 * @param selected Whether the [RadioButton] is currently selected.
 * @param onClick The callback to be called when the [RadioButton] is clicked. The caller is
 *   responsible for updating the state. If `null`, the [RadioButton] is not interactive.
 * @param modifier The modifier to be applied to the [RadioButton].
 * @param colors The [RadioButtonColors] of the [RadioButton].
 * @param enabled Whether the [RadioButton] is enabled.
 */
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    colors: RadioButtonColors = RadioButtonDefaults.radioButtonColors(),
    enabled: Boolean = true,
) {
    val currentOnClickState = rememberUpdatedState(onClick)

    val transition = updateTransition(selected, label = "RadioButtonTransition")

    val color = colors.color(enabled)
    val unselectedColor = colors.unselectedColor(enabled)
    // The COUI AVDs draw the centre #FAFAFA while they play; the resting drawable is #FFFFFF.
    val centerColor = colors.centerColor(enabled, animating = transition.isRunning)

    // The unselected ring fades out when selecting and back in when deselecting.
    val ringAlphaState = transition.animateFloat(
        transitionSpec = {
            if (!enabled) {
                snap()
            } else if (targetState) {
                tween(durationMillis = 300, easing = CouiRadioSelectEasing)
            } else {
                tween(durationMillis = 267, easing = CouiRadioDeselectEasing)
            }
        },
        label = "RingAlpha",
    ) { if (it) 0f else 1f }

    // The primary disc fades in and out.
    val discAlphaState = transition.animateFloat(
        transitionSpec = {
            if (!enabled) {
                snap()
            } else if (targetState) {
                tween(durationMillis = 300, easing = CouiRadioSelectEasing)
            } else {
                tween(durationMillis = 200, easing = CouiRadioDeselectEasing)
            }
        },
        label = "DiscAlpha",
    ) { if (it) 1f else 0f }

    // The center dot scales up from the middle and back down.
    val centerScaleState = transition.animateFloat(
        transitionSpec = {
            if (!enabled) {
                snap()
            } else if (targetState) {
                tween(durationMillis = 300, easing = CouiRadioSelectEasing)
            } else {
                tween(durationMillis = 267, easing = CouiRadioDeselectEasing)
            }
        },
        label = "CenterScale",
    ) { if (it) 1f else 0f }

    // The center dot fades in and out.
    val centerAlphaState = transition.animateFloat(
        transitionSpec = {
            if (!enabled) {
                snap()
            } else if (targetState) {
                tween(durationMillis = 300, easing = CouiRadioSelectEasing)
            } else {
                tween(durationMillis = 200, easing = CouiRadioDeselectEasing)
            }
        },
        label = "CenterAlpha",
    ) { if (it) 1f else 0f }

    // Both AVDs pulse the ring and disc 1 -> 1.08 -> 1 on every toggle; a same-endpoint keyframe
    // animation never runs through updateTransition, so drive it with an Animatable.
    val bounceScale = remember { Animatable(1f) }
    var isFirstComposition by remember { mutableStateOf(true) }
    LaunchedEffect(selected) {
        if (isFirstComposition) {
            isFirstComposition = false
            return@LaunchedEffect
        }
        bounceScale.snapTo(1f)
        // coui_btn_radio.xml only has transitions between the enabled states.
        if (!enabled) return@LaunchedEffect
        val easing = if (selected) CouiRadioSelectEasing else CouiRadioDeselectEasing
        bounceScale.animateTo(
            targetValue = 1f,
            animationSpec = keyframes {
                durationMillis = 300
                1f at 0 using easing
                1.08f at 150 using easing
            },
        )
    }

    val pressColor = COUITheme.colorScheme.press
    val pressIndication = remember(pressColor) { CircularPressIndication(RadioButtonPressRadius, pressColor) }

    val finalModifier = if (onClick != null) {
        Modifier.selectable(
            selected = selected,
            // The framework / AppCompat RadioButton COUI themes plays no haptic feedback.
            onClick = { currentOnClickState.value?.invoke() },
            enabled = enabled,
            role = Role.RadioButton,
            interactionSource = null,
            indication = pressIndication,
        )
    } else {
        Modifier.semantics {
            role = Role.RadioButton
            this.selected = selected
            if (!enabled) disabled()
        }
    }

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            .requiredSize(24.dp)
            // Before the drawing so the press highlight sits behind the button, like a view background.
            .then(finalModifier)
            .drawWithCache {
                // COUI radio geometry in a 24dp viewport (coui_btn_radio_off/on.xml).
                val ringRadius = size.minDimension * (9.2f / 24f)
                val ringStrokeWidth = size.minDimension * (1.3f / 24f)
                val discRadius = size.minDimension * (10f / 24f)
                val centerRadius = size.minDimension * (6f / 24f)
                onDrawBehind {
                    val bounce = bounceScale.value
                    val ringAlpha = ringAlphaState.value
                    if (ringAlpha > 0f) {
                        // The group scale pulse also scales the rendered stroke width.
                        drawCircle(
                            color = unselectedColor,
                            radius = ringRadius * bounce,
                            style = Stroke(width = ringStrokeWidth * bounce),
                            alpha = ringAlpha,
                        )
                    }
                    val discAlpha = discAlphaState.value
                    if (discAlpha > 0f) {
                        drawCircle(
                            color = color,
                            radius = discRadius * bounce,
                            alpha = discAlpha,
                        )
                    }
                    val centerScale = centerScaleState.value
                    val centerAlpha = centerAlphaState.value
                    if (centerScale > 0f && centerAlpha > 0f) {
                        drawCircle(
                            color = centerColor,
                            radius = centerRadius * centerScale,
                            alpha = centerAlpha,
                        )
                    }
                }
            },
    ) {}
}

object RadioButtonDefaults {
    /**
     * The default radio colors (COUI `coui_btn_radio_*.xml`).
     *
     * - The unselected ring is couiColorLabelTertiary #42000000 / #4DFFFFFF, and
     *   couiColorQuarternaryNeutral #14000000 / #26FFFFFF when disabled.
     * - The selected disc is couiColorPrimary, at 30% (`fillAlpha` 0.3) when disabled.
     * - The centre dot is #FFFFFF at rest and #FAFAFA while a toggle animation plays (both with no
     *   night variant), and `coui_btn_radio_color_on_disabled` #8AFFFFFF / #29FFFFFF when disabled.
     */
    @Composable
    fun radioButtonColors(
        selectedColor: Color = COUITheme.colorScheme.primary,
        disabledSelectedColor: Color = COUITheme.colorScheme.disable,
        unselectedColor: Color = COUITheme.colorScheme.labelTertiary,
        disabledUnselectedColor: Color = COUITheme.colorScheme.quarternaryNeutral,
        // The COUI selected center is hardcoded #ffffff with no night variant.
        centerColor: Color = Color.White,
        disabledCenterColor: Color = COUITheme.colorScheme.disabledOnPrimaryButton,
        // The COUI toggle AVDs hardcode the center as #fafafa with no night variant.
        animatingCenterColor: Color = DefaultAnimatingCenterColor,
    ): RadioButtonColors = remember(
        selectedColor,
        disabledSelectedColor,
        unselectedColor,
        disabledUnselectedColor,
        centerColor,
        disabledCenterColor,
        animatingCenterColor,
    ) {
        RadioButtonColors(
            selectedColor = selectedColor,
            disabledSelectedColor = disabledSelectedColor,
            unselectedColor = unselectedColor,
            disabledUnselectedColor = disabledUnselectedColor,
            centerColor = centerColor,
            disabledCenterColor = disabledCenterColor,
            animatingCenterColor = animatingCenterColor,
        )
    }
}

@Immutable
data class RadioButtonColors(
    private val selectedColor: Color,
    private val disabledSelectedColor: Color,
    private val unselectedColor: Color,
    private val disabledUnselectedColor: Color,
    private val centerColor: Color,
    private val disabledCenterColor: Color,
    private val animatingCenterColor: Color = DefaultAnimatingCenterColor,
) {
    @Stable
    internal fun color(enabled: Boolean): Color = if (enabled) selectedColor else disabledSelectedColor

    @Stable
    internal fun unselectedColor(enabled: Boolean): Color = if (enabled) unselectedColor else disabledUnselectedColor

    @Stable
    internal fun centerColor(enabled: Boolean, animating: Boolean = false): Color = when {
        !enabled -> disabledCenterColor
        animating -> animatingCenterColor
        else -> centerColor
    }
}

// $coui_btn_radio_off_to_on_animation__0.xml / $coui_btn_radio_on_to_off_animation__0.xml center fill.
private val DefaultAnimatingCenterColor = Color(0xFFFAFAFA)

// The borderless framework ripple radius on the 24dp button: half its diagonal, sqrt(12^2 + 12^2).
private val RadioButtonPressRadius = 17.dp

// COUI radio toggle easings (coui_btn_radio_off_to_on / on_to_off animation path interpolators).
private val CouiRadioSelectEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)
private val CouiRadioDeselectEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)
