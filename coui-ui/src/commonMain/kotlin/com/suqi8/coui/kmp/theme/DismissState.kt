// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.theme

import androidx.compose.runtime.staticCompositionLocalOf
import com.suqi8.coui.kmp.overlay.OverlayBottomSheet
import com.suqi8.coui.kmp.overlay.OverlayDialog
import com.suqi8.coui.kmp.overlay.OverlayListPopup
import com.suqi8.coui.kmp.window.WindowBottomSheet
import com.suqi8.coui.kmp.window.WindowDialog
import com.suqi8.coui.kmp.window.WindowListPopup

/**
 * CompositionLocal that provides a dismiss request function for overlay components.
 *
 * This is automatically provided by all overlay components ([OverlayDialog], [WindowDialog],
 * [OverlayBottomSheet], [WindowBottomSheet], [OverlayListPopup], [WindowListPopup]).
 *
 * Call the provided function to request dismissal from inside overlay content:
 * ```kotlin
 * val dismiss = LocalDismissState.current
 * Button(onClick = { dismiss?.invoke() }) { Text("Close") }
 * ```
 */
val LocalDismissState = staticCompositionLocalOf<(() -> Unit)?> { null }
