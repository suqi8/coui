// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package top.yukonga.miuix.kmp.utils

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
            },
        )
    }
}
