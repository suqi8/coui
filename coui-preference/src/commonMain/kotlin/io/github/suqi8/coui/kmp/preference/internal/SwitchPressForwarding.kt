// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference.internal

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Mirrors a press on the switch of a switch row into the row's [interactionSource], so the row's
 * press layer shows while the switch itself keeps handling the gesture (COUI 17
 * `COUISwitchTouchEventDelegate`, which hands touches on the switch to `COUISwitch` and drives the
 * row's press mask at the same time).
 *
 * Events are only observed in the initial pass and never consumed.
 */
internal fun Modifier.forwardPressTo(
    interactionSource: MutableInteractionSource,
    enabled: Boolean,
): Modifier = if (!enabled) {
    this
} else {
    this.pointerInput(interactionSource) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            val press = PressInteraction.Press(down.position)
            interactionSource.tryEmit(press)
            var released = false
            try {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    if (event.changes.none { it.pressed }) break
                }
                released = true
            } finally {
                interactionSource.tryEmit(
                    if (released) PressInteraction.Release(press) else PressInteraction.Cancel(press),
                )
            }
        }
    }
}
