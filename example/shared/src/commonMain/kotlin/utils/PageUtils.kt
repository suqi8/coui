// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package utils

import androidx.compose.runtime.Composable
import app.LocalAppSettings
import io.github.suqi8.coui.kmp.blur.LayerBackdrop
import io.github.suqi8.coui.kmp.blur.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * Returns a backdrop that records a page over its window background, or null when the material
 * blur setting is off or runtime shaders are unavailable.
 */
@Composable
fun rememberBlurBackdrop(): LayerBackdrop? {
    if (!LocalAppSettings.current.enableMaterialBlur || !isRuntimeShaderSupported()) return null
    val surfaceColor = COUITheme.colorScheme.backgroundWithCard
    return rememberLayerBackdrop {
        drawRect(surfaceColor)
        drawContent()
    }
}
