// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import io.github.suqi8.coui.kmp.basic.DropdownColors
import io.github.suqi8.coui.kmp.basic.DropdownDefaults
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.preciseClickAnchor
import io.github.suqi8.coui.kmp.basic.rememberPreciseClickState
import io.github.suqi8.coui.kmp.popup.WindowDropdownPopup

/**
 * Wraps [content] with a context menu that opens at the pointer in a platform window, after COUI
 * `COUIContextMenu`. The window-layer twin of [OverlayContextMenu]; it needs no scaffold.
 *
 * A long press on [content] (COUI `PreciseLongPressHelper`: the menu is shown at the recorded DOWN
 * point, with the long-press haptic) or a secondary (right) mouse click opens a
 * [WindowDropdownPopup] anchored to that single point instead of below the content
 * (`COUIPopupListWindow.show(view, x, y)` / `COUIIsolatedPopupListWindow`'s 1px dummy anchor): the
 * popup locator places it below or above the point and keeps it inside the window barriers. Once the
 * long press fires, the rest of the gesture is consumed so [content]'s own click does not run.
 *
 * @param entries The menu groups, separated by dividers.
 * @param modifier The modifier to be applied to the wrapper around [content].
 * @param enabled Whether the long press and right click open the menu.
 * @param dropdownColors The [DropdownColors] of the menu rows.
 * @param maxHeight The maximum height of the menu.
 * @param collapseOnSelection Whether a row click dismisses the menu.
 * @param hapticFeedbackEnabled Whether the long press plays the long-press haptic.
 * @param onExpandedChange Called when the menu opens or closes (COUI `MenuShowStateListener`).
 * @param content The content the menu is registered on (COUI `registerForContextMenu`).
 */
@Composable
fun WindowContextMenu(
    entries: List<DropdownEntry>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    maxHeight: Dp? = null,
    collapseOnSelection: Boolean = true,
    hapticFeedbackEnabled: Boolean = true,
    onExpandedChange: ((Boolean) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val preciseClickState = rememberPreciseClickState()
    var expanded by remember { mutableStateOf(false) }
    val currentOnExpandedChange by rememberUpdatedState(onExpandedChange)
    val haptic = LocalHapticFeedback.current
    val currentHapticEnabled by rememberUpdatedState(hapticFeedbackEnabled)
    val nonEmptyEntries = entries.filter { it.items.isNotEmpty() }
    val hasEntries = nonEmptyEntries.isNotEmpty()

    Box(
        modifier = modifier
            .preciseClickAnchor(preciseClickState)
            .contextMenuGesture(enabled = enabled && hasEntries) { longPress ->
                if (longPress && currentHapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                if (!expanded) {
                    expanded = true
                    currentOnExpandedChange?.invoke(true)
                }
            },
        propagateMinConstraints = true,
    ) {
        content()
        if (hasEntries) {
            WindowDropdownPopup(
                entries = nonEmptyEntries,
                show = expanded,
                onDismiss = {
                    if (expanded) {
                        expanded = false
                        currentOnExpandedChange?.invoke(false)
                    }
                },
                onDismissFinished = {},
                maxHeight = maxHeight,
                dropdownColors = dropdownColors,
                collapseOnSelection = collapseOnSelection,
                preciseClickState = preciseClickState,
            )
        }
    }
}
