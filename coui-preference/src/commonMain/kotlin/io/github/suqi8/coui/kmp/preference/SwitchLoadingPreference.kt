// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentColors
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.basic.Switch
import io.github.suqi8.coui.kmp.basic.SwitchColors
import io.github.suqi8.coui.kmp.basic.SwitchDefaults
import io.github.suqi8.coui.kmp.preference.internal.EndActionsSlot
import io.github.suqi8.coui.kmp.utils.CouiHapticEffect
import io.github.suqi8.coui.kmp.utils.rememberCouiHaptic

/**
 * A switch with a title, a summary and a loading state, mirroring COUISwitchLoadingPreference.
 *
 * While [isLoading] is `true` the switch thumb shows the COUI loading spinner and the row cannot be
 * toggled. The usual flow is: the user taps the row, [onCheckedChange] fires, the caller flips
 * [isLoading] on, performs the asynchronous work, then updates [checked] and turns [isLoading] off
 * again.
 *
 * As in COUI, the whole row is the touch target, the switch included: the switch of
 * `coui_preference_widget_switchload` is neither clickable nor focusable and, unlike
 * [SwitchPreference], gets no `COUISwitchTouchEventDelegate`, so a touch on it presses and clicks the
 * row and the thumb cannot be dragged. A row click plays no haptic by itself
 * (`COUISwitchLoadingPreference.onClick` only arms the switch's tactile feedback and starts loading);
 * the COUI 302 haptic plays once [checked] actually flips afterwards, as `COUISwitch.stopLoading`
 * toggles the switch and `setChecked` performs the armed feedback.
 *
 * @param checked The checked state of the [SwitchLoadingPreference].
 * @param onCheckedChange The callback when the checked state of the [SwitchLoadingPreference] is
 *   changed. Not invoked while [isLoading] is `true`.
 * @param title The title of the [SwitchLoadingPreference].
 * @param modifier The modifier to be applied to the [SwitchLoadingPreference].
 * @param isLoading Whether the switch shows the COUI loading spinner on its thumb. While loading
 *   the preference cannot be toggled.
 * @param titleColor The color of the title.
 * @param summary The summary of the [SwitchLoadingPreference].
 * @param summaryColor The color of the summary.
 * @param startAction The [Composable] content on the start side of the [SwitchLoadingPreference].
 * @param endActions The [Composable] content on the end side of the [SwitchLoadingPreference].
 * @param bottomAction The [Composable] content at the bottom of the [SwitchLoadingPreference].
 * @param switchColors The [SwitchColors] of the [SwitchLoadingPreference].
 * @param insideMargin The margin inside the [SwitchLoadingPreference].
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra padding.
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param enabled Whether the [SwitchLoadingPreference] is clickable.
 */
@Composable
fun SwitchLoadingPreference(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    startAction: @Composable (() -> Unit)? = null,
    endActions: @Composable RowScope.() -> Unit = {},
    bottomAction: (@Composable () -> Unit)? = null,
    switchColors: SwitchColors = SwitchDefaults.switchColors(),
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
) {
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    val couiHaptic = rememberCouiHaptic()
    val feedback = remember { SwitchLoadingPreferenceFeedback(checked) }
    // COUISwitch.setChecked -> performFeedBack: the feedback armed by the row click plays when the
    // switch flips, i.e. once the caller has finished loading and updated `checked`.
    SideEffect {
        if (feedback.lastChecked != checked) {
            feedback.lastChecked = checked
            if (feedback.armed) {
                feedback.armed = false
                couiHaptic(CouiHapticEffect.Switch)
            }
        }
    }
    BasicComponent(
        modifier = modifier,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        startAction = startAction,
        endActions = {
            EndActionsSlot(endSpacing = 8.dp) { endActions() }
            // coui_preference_widget_switchload: a passive COUISwitch (clickable / focusable false).
            Switch(
                checked = checked,
                onCheckedChange = null,
                enabled = enabled,
                isLoading = isLoading,
                colors = switchColors,
            )
        },
        bottomAction = bottomAction,
        onClick = {
            if (enabled && !isLoading) {
                // COUISwitchLoadingPreference.onClick: setTactileFeedbackEnabled(true) + startLoading().
                feedback.armed = true
                currentOnCheckedChange(!checked)
            }
        },
        role = Role.Switch,
        holdDownState = holdDownState,
        enabled = enabled,
    )
}

/**
 * The tactile feedback a row click arms (`COUISwitch.setTactileFeedbackEnabled(true)`), consumed by
 * the next change of the checked state, and the checked state that change is measured against.
 */
private class SwitchLoadingPreferenceFeedback(var lastChecked: Boolean) {
    var armed: Boolean = false
}
