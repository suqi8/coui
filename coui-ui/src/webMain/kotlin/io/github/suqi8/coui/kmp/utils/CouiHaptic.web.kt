// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

// No OEM linear motor off Android; map COUI effects to the closest standard Compose haptic.
@Composable
actual fun rememberCouiHaptic(): (CouiHapticEffect) -> Unit {
    val haptic = LocalHapticFeedback.current
    return { effect: CouiHapticEffect ->
        haptic.performHapticFeedback(
            when (effect) {
                CouiHapticEffect.Switch -> HapticFeedbackType.ToggleOn
                CouiHapticEffect.Strength -> HapticFeedbackType.SegmentTick
                CouiHapticEffect.ScrollEdge -> HapticFeedbackType.SegmentTick
                CouiHapticEffect.LongVibrate -> HapticFeedbackType.LongPress
                CouiHapticEffect.KeyboardTouch -> HapticFeedbackType.KeyboardTap
                CouiHapticEffect.ShortTripleVibrate -> HapticFeedbackType.Reject
                CouiHapticEffect.SteppingShort -> HapticFeedbackType.SegmentFrequentTick
                CouiHapticEffect.EdgeShort -> HapticFeedbackType.GestureThresholdActivate
                CouiHapticEffect.EdgeSqueeze -> HapticFeedbackType.GestureEnd
            },
        )
    }
}
