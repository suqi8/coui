// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.luminance
import io.github.suqi8.coui.kmp.effect.LocalPopupMaterialBlur
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * What a popup surface paints: its fill, the modifier drawn inside its clip under the rows, and
 * whether the material effects (shadow edge, spotlight) go on top.
 */
@Immutable
internal class PopupSurface(
    val color: Color,
    val backgroundModifier: Modifier,
    val materialEffect: Boolean,
)

/**
 * Resolves the surface of a list popup, cascading menu or dropdown popup. With [materialBlur] on
 * (the default) and a [LocalPopupMaterialBlur] provider able to draw, the popup shows the COUI 17
 * popup material ([ListPopupDefaults.materialBlurSpec]) on a transparent fill, as ColorOS 17 menus
 * do; otherwise it keeps [backgroundColor], COUI's non-blur popup background.
 *
 * Must be called where the popup is called, not inside its host: the provider is scoped to the call
 * site so dialogs and sheets can turn it off for menus opened over them.
 *
 * The material effects only make sense on a blurred surface (`RoundFrameLayout.initMaterialEffects`
 * runs once the blur is on), so [materialEffect] is honoured when the default blur draws or the
 * caller paints its own blur through a translucent [backgroundColor] and [backgroundModifier].
 */
@Composable
internal fun rememberPopupSurface(
    materialBlur: Boolean,
    backgroundColor: Color,
    materialEffect: Boolean,
    backgroundModifier: Modifier = Modifier,
): PopupSurface {
    val provider = if (materialBlur) LocalPopupMaterialBlur.current else null
    val isDark = COUITheme.colorScheme.background.luminance() < 0.5f
    val blur = remember(provider, isDark) {
        provider?.materialBlur(ListPopupDefaults.materialBlurSpec(isDark), RectangleShape)
    }
    return remember(blur, backgroundColor, backgroundModifier, materialEffect) {
        PopupSurface(
            color = if (blur != null) Color.Transparent else backgroundColor,
            backgroundModifier = if (blur != null) blur.then(backgroundModifier) else backgroundModifier,
            materialEffect = materialEffect && (blur != null || backgroundColor.alpha < 1f),
        )
    }
}
