// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.LocalHapticFeedback

// No OEM linear motor off Android: the same fallback as CouiHapticEffect.ScrollEdge.
internal actual fun CompositionLocalConsumerModifierNode.performScrollEdgeHaptic() {
    currentValueOf(LocalHapticFeedback).performHapticFeedback(HapticFeedbackType.SegmentTick)
}
