// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentColors
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.DividerDefaults
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.basic.Switch
import io.github.suqi8.coui.kmp.basic.SwitchColors
import io.github.suqi8.coui.kmp.basic.SwitchDefaults
import io.github.suqi8.coui.kmp.preference.internal.WithDividerLayout
import io.github.suqi8.coui.kmp.preference.internal.WithDividerSeparator
import io.github.suqi8.coui.kmp.preference.internal.withDividerTrailingPadding
import io.github.suqi8.coui.kmp.utils.CouiHapticEffect
import io.github.suqi8.coui.kmp.utils.rememberCouiHaptic

/**
 * A row split into a clickable main area and an independent switch, separated by a vertical divider
 * (COUI `COUISwitchWithDividerPreference`, layout `coui_switch_with_divider_preference`), as used by
 * the WLAN and Bluetooth entries: tapping the title opens the detail page while the switch toggles
 * the feature in place.
 *
 * - The main area is a [BasicComponent] (`main_layout`, a `ListSelectedItemLayout`): only it shows
 *   the row press feedback and receives the card-group padding, and a tap on it runs [onClick]
 *   (`OnMainLayoutClickListener.onMainLayoutClick`).
 * - The trailing area (`switch_layout`) is as tall as the main area. A tap anywhere in it toggles the
 *   switch with the COUI switch haptic (`switch_layout` click -> `COUISwitchPreference.onClick`); the
 *   switch itself also handles taps and thumb drags. It draws no press feedback, and unlike
 *   [SwitchPreference] pressing the switch does not press the main area.
 *
 * @param checked The checked state of the switch.
 * @param onCheckedChange The callback when the switch is toggled.
 * @param title The title of the [SwitchWithDividerPreference].
 * @param modifier The modifier to be applied to the [SwitchWithDividerPreference].
 * @param onClick The callback when the main area is clicked; `null` keeps the main area non-clickable.
 * @param titleColor The color of the title.
 * @param summary The summary of the [SwitchWithDividerPreference].
 * @param summaryColor The color of the summary.
 * @param startAction The [Composable] content on the start side of the main area.
 * @param endActions The [Composable] content on the end side of the main area, before the divider.
 * @param bottomAction The [Composable] content at the bottom of the main area.
 * @param switchColors The [SwitchColors] of the switch.
 * @param dividerColor The color of the vertical divider (`?couiColorDivider`).
 * @param insideMargin The margin inside the main area (`coui_switch_with_divider_title_padding_start`
 *   / `_end`, less the card inset at the start).
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra padding.
 * @param holdDownState Used to determine whether the main area is in the pressed state.
 * @param enabled Whether the main area and the switch are enabled.
 */
@Composable
fun SwitchWithDividerPreference(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    startAction: @Composable (() -> Unit)? = null,
    endActions: @Composable (RowScope.() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    switchColors: SwitchColors = SwitchDefaults.switchColors(),
    dividerColor: Color = DividerDefaults.DividerColor,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
) {
    WithDividerLayout(
        modifier = modifier,
        main = {
            BasicComponent(
                insideMargin = insideMargin,
                cardListPosition = cardListPosition,
                title = title,
                titleColor = titleColor,
                summary = summary,
                summaryColor = summaryColor,
                startAction = startAction,
                endActions = endActions,
                bottomAction = bottomAction,
                onClick = onClick,
                holdDownState = holdDownState,
                enabled = enabled,
            )
        },
        trailing = {
            SwitchWithDividerTrailing(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                switchColors = switchColors,
                dividerColor = dividerColor,
            )
        },
    )
}

@Composable
private fun SwitchWithDividerTrailing(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean,
    switchColors: SwitchColors,
    dividerColor: Color,
) {
    val currentChecked by rememberUpdatedState(checked)
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    val couiHaptic = rememberCouiHaptic()
    val currentCouiHaptic by rememberUpdatedState(couiHaptic)
    // A plain tap detector (no semantics) for the area around the switch: accessibility services
    // reach the switch itself, which already exposes the toggle. Taps on the switch are consumed by
    // the switch and never reach this detector.
    val areaTapModifier = if (enabled) {
        Modifier.pointerInput(Unit) {
            detectTapGestures {
                currentOnCheckedChange(!currentChecked)
                currentCouiHaptic(CouiHapticEffect.Switch)
            }
        }
    } else {
        Modifier
    }
    Row(
        modifier = areaTapModifier.withDividerTrailingPadding(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WithDividerSeparator(color = dividerColor)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = switchColors,
            enabled = enabled,
        )
    }
}
