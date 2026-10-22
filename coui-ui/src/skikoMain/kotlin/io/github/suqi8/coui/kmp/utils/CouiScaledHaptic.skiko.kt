// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

// Compose haptics off Android have no intensity, so the plain effect is played.
@Composable
actual fun rememberCouiScaledHaptic(): (effect: CouiHapticEffect, intensity: Float) -> Unit {
    val haptic = rememberCouiHaptic()
    return remember(haptic) { { effect: CouiHapticEffect, _: Float -> haptic(effect) } }
}
