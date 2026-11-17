// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.dp
import app.AppNavHost
import app.AppSettings
import app.LocalAppPadding
import app.LocalAppSettings
import app.LocalNavTransitionSettings
import app.NavTransitionSettings
import io.github.suqi8.coui.kmp.squircle.LocalSquircleEnabled
import kotlinx.coroutines.flow.drop
import ui.AppTheme
import ui.ColorMode
import ui.keyColorFor

/**
 * The example app.
 *
 * @param padding The area the platform shell asks the app to keep clear, such as the web safe area.
 * @param onColorModeChange Called when the user picks another colour mode, so the platform shell
 *   can restyle its system bars.
 */
@Composable
fun App(
    padding: PaddingValues = PaddingValues(0.dp),
    onColorModeChange: ((ColorMode) -> Unit)? = null,
) {
    val settings = remember { AppSettings() }
    val navTransitionSettings = remember { NavTransitionSettings() }

    val currentOnColorModeChange by rememberUpdatedState(onColorModeChange)
    LaunchedEffect(settings) {
        snapshotFlow { settings.colorMode }
            .drop(1)
            .collect { currentOnColorModeChange?.invoke(it) }
    }

    AppTheme(
        colorMode = settings.colorMode,
        keyColor = keyColorFor(settings.seedIndex),
        paletteStyle = settings.paletteStyle,
        colorSpec = settings.colorSpec,
    ) {
        CompositionLocalProvider(
            LocalAppSettings provides settings,
            LocalNavTransitionSettings provides navTransitionSettings,
            LocalAppPadding provides padding,
            LocalSquircleEnabled provides settings.enableSquircle,
        ) {
            AppNavHost()
        }
    }
}
