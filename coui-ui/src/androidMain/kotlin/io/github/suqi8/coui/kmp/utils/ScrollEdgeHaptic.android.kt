// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import android.view.HapticFeedbackConstants
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.LocalView

/** `COUIHapticFeedbackConstants.EDGE_LIST_VIBRATE`. */
private const val OPLUS_HAPTIC_EDGE_LIST = 307

internal actual fun CompositionLocalConsumerModifierNode.performScrollEdgeHaptic() {
    val view = currentValueOf(LocalView)
    if (!view.performHapticFeedback(OPLUS_HAPTIC_EDGE_LIST)) {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }
}
