// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference.internal

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Calls [onTouchDown] as soon as a finger lands anywhere on the element, like an
 * `View.OnTouchListener` that reacts to `ACTION_DOWN` and returns `false` (for example the item
 * touch listener of `COUICheckBoxPreference`, which plays the 302 haptic there).
 *
 * The down is only observed in the initial pass and never consumed, so the row's own click and
 * press handling and any scrolling parent keep working. [onTouchDown] is captured when the element
 * is first attached: read changing values through a `State` inside it.
 */
internal fun Modifier.onTouchDown(
    enabled: Boolean,
    onTouchDown: () -> Unit,
): Modifier = if (!enabled) {
    this
} else {
    this.pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            onTouchDown()
        }
    }
}
