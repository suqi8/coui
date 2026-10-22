// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.runtime.Composable
import org.jetbrains.skia.Font
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.FontStyle

// Only Android ships the COUI click sound; elsewhere the picker is silent.
@Composable
internal actual fun rememberNumberPickerClickSound(): () -> Unit = SilentClick

private val SilentClick: () -> Unit = {}

internal actual fun numberPickerVelocityScale(density: Float): Float = density / NumberPickerReferenceDensity

// The platform default typeface, like Android's fresh Paint. When the platform exposes no font
// (e.g. the browser canvas), fall back to Roboto's top/bottom ratios, Android's default typeface.
internal actual fun numberPickerFontTopBottom(textSizePx: Float): FloatArray {
    val typeface = runCatching { FontMgr.default.matchFamilyStyle(null, FontStyle.NORMAL) }.getOrNull()
    if (typeface != null) {
        val metrics = Font(typeface, textSizePx).metrics
        if (metrics.top < 0f && metrics.bottom > 0f) return floatArrayOf(metrics.top, metrics.bottom)
    }
    return floatArrayOf(RobotoTop * textSizePx, RobotoBottom * textSizePx)
}

// Roboto's yMax / yMin over its 2048 units per em, as Android reports in FontMetrics.top / bottom.
private val RobotoTop = -2163f / 2048f
private val RobotoBottom = 555f / 2048f
