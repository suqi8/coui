// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package app

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import ui.ColorMode

/**
 * The app-wide settings shown on the settings page. Each field is its own snapshot state, so a
 * composable that reads one field only recomposes when that field changes.
 *
 * Settings are not persisted; they reset when the process restarts.
 */
@Stable
class AppSettings {
    var colorMode by mutableStateOf(ColorMode.System)
    var seedIndex by mutableIntStateOf(0)
    var paletteStyle by mutableIntStateOf(0)
    var colorSpec by mutableIntStateOf(0)

    /** The COUI 17 material blur on bars, panels, popups and buttons. */
    var enableMaterialBlur by mutableStateOf(true)
    var enableSquircle by mutableStateOf(true)
    var enableScrollEndHaptic by mutableStateOf(true)
    var showFpsMonitor by mutableStateOf(false)
}

/**
 * The Navigation 3 transition effects applied by the root `NavDisplay`. They live at app level
 * because only the root display can apply them; the navigation transition demo page edits them.
 */
@Stable
class NavTransitionSettings {
    var enableCornerClip by mutableStateOf(true)
    var enableDim by mutableStateOf(true)
    var blockInputDuringTransition by mutableStateOf(true)
    var popDirectionFollowsSwipeEdge by mutableStateOf(false)
}

/** The app-wide [AppSettings], provided by `App`. */
val LocalAppSettings = staticCompositionLocalOf<AppSettings> { error("No AppSettings provided!") }

/** The [NavTransitionSettings] applied by the root `NavDisplay`, provided by `App`. */
val LocalNavTransitionSettings = staticCompositionLocalOf<NavTransitionSettings> {
    error("No NavTransitionSettings provided!")
}
