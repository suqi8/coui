// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

actual val hasFocusReassignBug: Boolean = false

@Composable
actual fun getRoundedCorner(): Dp = 0.dp

@Composable
actual fun RemovePlatformDialogDefaultEffects() {
    // No-op for Skiko platforms
}
