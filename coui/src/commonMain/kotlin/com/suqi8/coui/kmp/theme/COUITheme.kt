// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import com.suqi8.coui.kmp.utils.MiuixIndication

/**
 * The default theme that provides color and text styles for the Miuix components.
 *
 * @param colors The color scheme for the Miuix components.
 * @param textStyles The text styles for the Miuix components.
 * @param content The content of the Miuix theme.
 */
@Composable
fun COUITheme(
    colors: Colors = COUITheme.colorScheme,
    textTypography: Typography = COUITheme.textStyles,
    content: @Composable () -> Unit
) {
    val COUIColors = remember(colors) {
        colors.copy().apply { updateColorsFrom(colors) }
    }
    val COUITypography = remember(textTypography) { textTypography }


    val COUIIndication = remember(colors.onBackground) {
        MiuixIndication(color = colors.onBackground)
    }
    CompositionLocalProvider(
        LocalColors provides COUIColors,
        LocalTypography provides COUITypography,
        LocalIndication provides COUIIndication
    ) {
        content()
    }
}

object COUITheme {
    val colorScheme: Colors
        @Composable @ReadOnlyComposable get() = LocalColors.current

    val textStyles: Typography
        @Composable @ReadOnlyComposable get() = LocalTypography.current
}
