// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import androidx.compose.runtime.Composable

@Composable
actual fun WindowNavigationEventScope(content: @Composable () -> Unit) {
    // Dialogs are layered inside the host window; there is no window boundary to rebind across.
    content()
}
