// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package top.yukonga.miuix.kmp.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.utils.BackEventCompat
import com.suqi8.coui.kmp.utils.BackHandler
import com.suqi8.coui.kmp.utils.Platform
import com.suqi8.coui.kmp.utils.WindowSize

@Composable
@OptIn(ExperimentalComposeUiApi::class)
actual fun getWindowSize(): WindowSize {
    val window = LocalWindowInfo.current
    val windowSize by remember(window) {
        derivedStateOf {
            WindowSize(
                width = window.containerSize.width,
                height = window.containerSize.height
            )
        }
    }
    return windowSize
}

actual fun platform(): Platform = Platform.Desktop

@Composable
actual fun getRoundedCorner(): Dp = 0.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun BackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    androidx.compose.ui.backhandler.BackHandler(enabled = enabled, onBack = onBack)
}

@Composable
actual fun PredictiveBackHandler(
    enabled: Boolean,
    onBackStarted: ((BackEventCompat) -> Unit)?,
    onBackProgressed: ((BackEventCompat) -> Unit)?,
    onBackCancelled: (() -> Unit)?,
    onBack: () -> Unit
) {
    // Desktop doesn't support predictive back gesture, fallback to simple BackHandler
    BackHandler(enabled = enabled, onBack = onBack)
}
