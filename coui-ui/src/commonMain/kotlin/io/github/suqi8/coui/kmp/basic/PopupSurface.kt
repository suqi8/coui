// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import io.github.suqi8.coui.kmp.effect.LocalPopupMaterialBlur
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * What a popup surface paints: its fill, the modifier drawn inside its clip under the rows, and
 * whether the material effects (shadow edge, spotlight) go on top.
 *
 * @property blurred Whether the default popup material blur draws (`RoundFrameLayout`
 *   `getUseBackgroundBlur`), which hides a fading sub menu below 10% alpha.
 */
@Immutable
internal class PopupSurface(
    val color: Color,
    val backgroundModifier: Modifier,
    val materialEffect: Boolean,
    val blurred: Boolean = false,
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
 * [materialEffect] (the shadow-edge stroke and the touch spotlight of `RoundFrameLayout`) is honoured
 * on every surface, blurred or not. COUI only starts them once the blur is on
 * (`RoundFrameLayout.onAttachedToWindow` → `initMaterialEffects`); this library draws them on the
 * opaque fallback too, with the same parameters, so a menu opened where no blur is available (inside
 * a dialog or sheet, without a [LocalPopupMaterialBlur] provider, or with `materialBlur = false`)
 * still lights up. Both are additive white light (source-over stroke, overlay spotlight), so they
 * show on the blurred and the dark surfaces and fade out against a pure white light-scheme fill.
 */
@Composable
internal fun rememberPopupSurface(
    materialBlur: Boolean,
    backgroundColor: Color,
    materialEffect: Boolean,
    backgroundModifier: Modifier = Modifier,
): PopupSurface {
    val provider = if (materialBlur) LocalPopupMaterialBlur.current else null
    val isDark = COUITheme.colorScheme.isDark
    val blur = remember(provider, isDark) {
        provider?.materialBlur(ListPopupDefaults.materialBlurSpec(isDark), RectangleShape)
    }
    return remember(blur, backgroundColor, backgroundModifier, materialEffect) {
        PopupSurface(
            color = if (blur != null) Color.Transparent else backgroundColor,
            backgroundModifier = if (blur != null) blur.then(backgroundModifier) else backgroundModifier,
            materialEffect = materialEffect,
            blurred = blur != null,
        )
    }
}
