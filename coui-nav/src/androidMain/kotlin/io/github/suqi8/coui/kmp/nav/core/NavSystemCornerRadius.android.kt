// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.nav.core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.RoundedCorner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Reads the device screen corner radius. Prefers the standard [RoundedCorner] window-insets API
 * (API 31+, bottom-left position); otherwise falls back to the framework `rounded_corner_radius_bottom`
 * dimen, and finally to `0.dp` for flat-corner screens. A split-screen or freeform window does not
 * sit in the screen corners, so it gets no rounding.
 */
@Composable
actual fun rememberNavSystemCornerRadius(): Dp {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val view = LocalView.current
    val insets = view.rootWindowInsets
    val inMultiWindow = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
        context.findActivity()?.isInMultiWindowMode == true
    val radiusPx = remember(context, view, insets, inMultiWindow) {
        if (inMultiWindow) {
            0
        } else {
            val fromInsets = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                insets?.getRoundedCorner(RoundedCorner.POSITION_BOTTOM_LEFT)?.radius?.takeIf { it > 0 }
            } else {
                null
            }
            fromInsets ?: bottomCornerRadiusFromResources(context)
        }
    }
    return (radiusPx / density).dp
}

/** Framework `android`-package dimen fallback for the bottom screen corner radius; 0 when absent. */
@SuppressLint("DiscouragedApi")
private fun bottomCornerRadiusFromResources(context: Context): Int {
    val id = context.resources.getIdentifier("rounded_corner_radius_bottom", "dimen", "android")
    return if (id > 0) context.resources.getDimensionPixelSize(id) else 0
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
