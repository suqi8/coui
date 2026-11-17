// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp
import navigation3.Navigator

val LocalNavigator = staticCompositionLocalOf<Navigator> { error("No navigator found!") }

/** Whether the window is wide enough for the rail and split-pane layouts. */
val LocalIsWideScreen = staticCompositionLocalOf { false }

/**
 * The padding the platform shell asks the app to keep clear (web safe area); pages add it to their
 * content padding.
 */
val LocalAppPadding = staticCompositionLocalOf { PaddingValues(0.dp) }

/**
 * The space a page must leave at its bottom for chrome drawn over it by the host, such as the
 * main shell's floating navigation bar. Zero on pushed pages.
 */
val LocalHostBottomPadding = staticCompositionLocalOf { 0.dp }

/**
 * Whether the page is shown inside the detail pane of the wide-screen list-detail layout. Pages
 * then hide their back button and skip the window insets the outer shell already applies.
 */
val LocalInDetailPane = staticCompositionLocalOf { false }
