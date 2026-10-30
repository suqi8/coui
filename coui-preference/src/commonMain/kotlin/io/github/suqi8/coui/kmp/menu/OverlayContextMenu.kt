// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.menu

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import io.github.suqi8.coui.kmp.basic.DropdownColors
import io.github.suqi8.coui.kmp.basic.DropdownDefaults
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.preciseClickAnchor
import io.github.suqi8.coui.kmp.basic.rememberPreciseClickState
import io.github.suqi8.coui.kmp.popup.OverlayDropdownPopup

/**
 * Wraps [content] with a context menu that opens at the pointer, after COUI `COUIContextMenu`.
 *
 * A long press on [content] (COUI `PreciseLongPressHelper`: the menu is shown at the recorded DOWN
 * point, with the long-press haptic) or a secondary (right) mouse click opens an
 * [OverlayDropdownPopup] anchored to that single point instead of below the content
 * (`COUIPopupListWindow.show(view, x, y)` / `COUIIsolatedPopupListWindow`'s 1px dummy anchor): the
 * popup locator places it below or above the point and keeps it inside the window barriers. Once the
 * long press fires, the rest of the gesture is consumed so [content]'s own click does not run.
 *
 * @param entries The menu groups, separated by dividers.
 * @param modifier The modifier to be applied to the wrapper around [content].
 * @param enabled Whether the long press and right click open the menu.
 * @param dropdownColors The [DropdownColors] of the menu rows.
 * @param maxHeight The maximum height of the menu.
 * @param renderInRootScaffold Whether the popup renders in the root scaffold overlay.
 * @param collapseOnSelection Whether a row click dismisses the menu.
 * @param hapticFeedbackEnabled Whether the long press plays the long-press haptic.
 * @param onExpandedChange Called when the menu opens or closes (COUI `MenuShowStateListener`).
 * @param content The content the menu is registered on (COUI `registerForContextMenu`).
 */
@Composable
fun OverlayContextMenu(
    entries: List<DropdownEntry>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    maxHeight: Dp? = null,
    renderInRootScaffold: Boolean = true,
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
            OverlayDropdownPopup(
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
                renderInRootScaffold = renderInRootScaffold,
                collapseOnSelection = collapseOnSelection,
                preciseClickState = preciseClickState,
            )
        }
    }
}

/**
 * Opens a context menu on a long press or a secondary mouse click, observing in
 * [PointerEventPass.Initial] so it works over clickable content. [onOpen] receives `true` for a
 * long press and `false` for a secondary click. After either, the rest of the gesture is consumed so
 * the content does not also see a click.
 */
internal fun Modifier.contextMenuGesture(enabled: Boolean, onOpen: (longPress: Boolean) -> Unit): Modifier {
    if (!enabled) return this
    return this.pointerInput(Unit) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            if (currentEvent.buttons.isSecondaryPressed) {
                down.consume()
                onOpen(false)
                consumeUntilUp()
                return@awaitEachGesture
            }
            // Null on timeout: the pointer stayed down and within the touch slop for the whole
            // long-press timeout, the View.OnLongClickListener condition.
            val released = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                var ended = false
                while (!ended) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val change = event.changes.firstOrNull { it.id == down.id }
                    ended = change == null ||
                        !change.pressed ||
                        change.isConsumed ||
                        (change.position - down.position).getDistance() > viewConfiguration.touchSlop
                }
                true
            }
            if (released == null) {
                onOpen(true)
                consumeUntilUp()
            }
        }
    }
}

/** Consumes every pointer change until all pointers are up. */
private suspend fun AwaitPointerEventScope.consumeUntilUp() {
    while (true) {
        val event = awaitPointerEvent(PointerEventPass.Initial)
        event.changes.forEach { it.consume() }
        if (event.changes.none { it.pressed }) break
    }
}
