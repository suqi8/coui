// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.interfaces.HoldDownObserver
import com.suqi8.coui.kmp.interfaces.collectIsHeldDownAsState
import com.suqi8.coui.kmp.squircle.squircleSurface
import com.suqi8.coui.kmp.theme.COUITheme
import com.suqi8.coui.kmp.theme.LocalContentColor
import com.suqi8.coui.kmp.utils.PressFeedbackType
import com.suqi8.coui.kmp.utils.SinkFeedback
import com.suqi8.coui.kmp.utils.TiltFeedback
import com.suqi8.coui.kmp.utils.pressable
import kotlin.coroutines.cancellation.CancellationException

/**
 * A [Card] component with COUI style.
 * Card contain content and actions that relate information about a subject.
 *
 * This [Card] does not handle input events
 *
 * @param modifier The modifier to be applied to the [Card].
 * @param cornerRadius The corner radius of the [Card].
 * @param insideMargin The margin inside the [Card].
 * @param colors [CardColors] that will be used to resolve the color(s) used for the [Card].
 * @param content The [Composable] content of the [Card].
 */
@Composable
@NonRestartableComposable
fun Card(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CardDefaults.CornerRadius,
    insideMargin: PaddingValues = CardDefaults.InsideMargin,
    colors: CardColors = CardDefaults.defaultColors(),
    content: @Composable ColumnScope.() -> Unit,
) {
    BasicCard(
        color = colors.color,
        contentColor = colors.contentColor,
        modifier = modifier,
        cornerRadius = cornerRadius,
    ) {
        Column(
            modifier = Modifier.padding(insideMargin),
            content = content,
        )
    }
}

/**
 * A [Card] component with COUI style.
 * Card contain contain content and actions that relate information about a subject.
 *
 * This [Card] handles input events
 *
 * @param modifier The modifier to be applied to the [Card].
 * @param cornerRadius The corner radius of the [Card].
 * @param insideMargin The margin inside the [Card].
 * @param colors [CardColors] that will be used to resolve the color(s) used for the [Card].
 * @param pressFeedbackType The press feedback type of the [Card]. Defaults to
 *   [PressFeedbackType.Tint], the COUI card press feedback that animates the fill towards
 *   [CardColors.pressedColor] while pressed or held down.
 * @param showIndication Whether to show indication of the [Card].
 * @param holdDownState Whether the [Card] is in a hold-down state.
 * @param onClick The callback to be invoked when the [Card] is clicked.
 * @param onLongPress The callback to be invoked when the [Card] is long pressed.
 * @param content The [Composable] content of the [Card].
 */
@Composable
fun Card(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CardDefaults.CornerRadius,
    insideMargin: PaddingValues = CardDefaults.InsideMargin,
    colors: CardColors = CardDefaults.defaultColors(),
    pressFeedbackType: PressFeedbackType = PressFeedbackType.Tint,
    showIndication: Boolean = false,
    holdDownState: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnLongPress by rememberUpdatedState(onLongPress)

    HoldDownObserver(holdDownState, interactionSource)

    val pressFeedback = remember(pressFeedbackType) {
        when (pressFeedbackType) {
            PressFeedbackType.None, PressFeedbackType.Tint -> null
            PressFeedbackType.Sink -> SinkFeedback()
            PressFeedbackType.Tilt -> TiltFeedback()
        }
    }

    // COUI card press feedback (ColorOS 16 COUICardListSelectedItemLayout): the card fill animates
    // towards couiColorCardPressed while pressed, driven by the critically damped COUI spring
    // (COUISpringForce response 0.3s / bounce 0, i.e. stiffness (2 * PI / 0.3)^2 ~= 438.65) of the
    // COUIMaskEffectDrawable press animator; releasing early keeps the press-in animation running
    // until 70% progress before it fades out. Cards never scale on press: ListSelectedItemLayout
    // ships an opt-in scale effect that ColorOS 16 never enables, and the legacy 150ms/367ms tween
    // pair is @Deprecated dead code. A locked press (setIsSelected) maps to [holdDownState].
    val tintEnabled = pressFeedbackType == PressFeedbackType.Tint
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHeldDown by interactionSource.collectIsHeldDownAsState()
    val tintProgress = remember { Animatable(0f) }
    LaunchedEffect(tintEnabled, isPressed, isHeldDown) {
        if (!tintEnabled) {
            tintProgress.snapTo(0f)
        } else if (isPressed || isHeldDown) {
            tintProgress.animateTo(1f, CardPressFeedbackSpring)
        } else if (tintProgress.value > 0f) {
            if (tintProgress.value < PressedTintMinVisibleProgress) {
                // COUIMaskEffectDrawable.animateToProgressUntil: keep the press-in animation
                // running until the tint is clearly visible, then reverse from there.
                try {
                    tintProgress.animateTo(1f, CardPressFeedbackSpring) {
                        if (value >= PressedTintMinVisibleProgress) throw CardPressTintThresholdReached()
                    }
                } catch (_: CardPressTintThresholdReached) {
                    // Minimum visible progress reached; fall through to the fade-out.
                }
            }
            tintProgress.animateTo(0f, CardPressFeedbackSpring)
        }
    }
    val fillColor = if (tintProgress.value > 0f) {
        lerp(colors.color, colors.pressedColor, tintProgress.value)
    } else {
        colors.color
    }

    val usedInteractionSource = if (pressFeedback != null) interactionSource else null
    val indicationToUse = if (showIndication) LocalIndication.current else null

    val hasOnClick = onClick != null
    val hasLongPress = onLongPress != null
    val isClickable = hasOnClick || hasLongPress
    val clickableModifier = remember(isClickable, hasLongPress, interactionSource, indicationToUse) {
        if (isClickable) {
            Modifier.combinedClickable(
                interactionSource = interactionSource,
                indication = indicationToUse,
                onClick = { currentOnClick?.invoke() },
                onLongClick = if (hasLongPress) {
                    { currentOnLongPress?.invoke() }
                } else {
                    null
                },
            )
        } else {
            Modifier
        }
    }

    BasicCard(
        color = fillColor,
        contentColor = colors.contentColor,
        modifier = modifier.pressable(
            interactionSource = usedInteractionSource,
            indication = pressFeedback,
            delay = null,
        ),
        cornerRadius = cornerRadius,
    ) {
        Column(
            modifier = Modifier
                .then(clickableModifier)
                .padding(insideMargin),
            content = content,
        )
    }
}

/**
 * A [BasicCard] component.
 *
 * @param color The background color of the [BasicCard].
 * @param contentColor The content color of the [BasicCard].
 * @param modifier The modifier to be applied to the [BasicCard].
 * @param cornerRadius The corner radius of the [BasicCard].
 * @param content The [Composable] content of the [BasicCard].
 */
@Composable
private fun BasicCard(
    color: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CardDefaults.CornerRadius,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
    ) {
        Box(
            modifier = modifier
                .semantics(mergeDescendants = false) {
                    isTraversalGroup = true
                }
                .squircleSurface(color = color, cornerRadius = cornerRadius),
            propagateMinConstraints = true,
        ) {
            content()
        }
    }
}

object CardDefaults {

    /**
     * The default corner radius of the [Card].
     *
     * COUI card lists resolve their radius from the couiRoundCornerM attr, which the ColorOS 16
     * Settings theme maps to coui_round_corner_m = 12dp (COUICardListSelectedItemLayout constructor,
     * settings sources line 332: getDimensionPixelOffset(couiCardRadius, getAttrDimens(context,
     * couiRoundCornerM)); styles.xml line 11804). The 17dp coui_card_list_os_16_1_radius_17_dp dimen
     * only exists in the unshipped uxdesign 16.1 library and is not consumed by Settings, so 12dp is
     * the ground-truth card radius. Verified against device screenshots: at density 3.5 the corner
     * arc height of the Wi-Fi settings card is ~30px = ~8.6dp, matching a 12dp squircle corner.
     */
    val CornerRadius = 12.dp

    /**
     * The default margin inside the [Card].
     */
    val InsideMargin = PaddingValues(0.dp)

    /**
     * The default colors width of the [Card].
     *
     * [pressedColor] follows couiColorCardPressed (#E6E6E6 light / #33FFFFFF dark), the fill a
     * COUI card animates towards while pressed.
     */
    @Composable
    fun defaultColors(
        color: Color = COUITheme.colorScheme.surfaceContainer,
        contentColor: Color = COUITheme.colorScheme.onSurfaceContainer,
        pressedColor: Color = COUITheme.colorScheme.surfaceContainerHigh,
    ): CardColors = remember(color, contentColor, pressedColor) {
        CardColors(
            color = color,
            contentColor = contentColor,
            pressedColor = pressedColor,
        )
    }
}

/**
 * Colors used by a [Card].
 *
 * @param color The background color of the card.
 * @param contentColor The content color of the card.
 * @param pressedColor The fill color the card animates towards while pressed, used by
 *   [PressFeedbackType.Tint] (couiColorCardPressed).
 */
@Immutable
data class CardColors(
    val color: Color,
    val contentColor: Color,
    val pressedColor: Color,
)

/**
 * The COUI card press feedback spring: COUISpringForce(response = 0.3f, bounce = 0f), which maps to
 * a critically damped spring with stiffness (2 * PI / 0.3)^2 ~= 438.65. Drives the press tint of
 * COUICardListSelectedItemLayout (the COUIMaskEffectDrawable press StateEffectAnimator).
 */
private val CardPressFeedbackSpring = spring<Float>(dampingRatio = 1f, stiffness = 438.65f)

/**
 * A release before the press tint reaches this progress defers the fade-out until it does, so
 * quick taps still flash visibly (COUIMaskEffectDrawable DEFAULT_MIN_PROGRESS_FOR_TOUCH_ENTER_ANIMATION).
 */
private val PressedTintMinVisibleProgress = 0.7f

/** Thrown to stop the deferred press-in tint animation once [PressedTintMinVisibleProgress] is reached. */
private class CardPressTintThresholdReached : CancellationException("Press tint reached the minimum visible progress")
