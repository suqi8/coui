// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties

actual fun platform(): Platform = Platform.WasmJs

@Composable
actual fun platformDialogProperties(): DialogProperties = DialogProperties(
    dismissOnBackPress = false,
    usePlatformDefaultWidth = false,
    scrimColor = Color.Transparent,
)
