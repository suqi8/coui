// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.theme

import androidx.compose.ui.graphics.Color

/**
 * A data class representing the Monet color roles.
 */
internal data class MonetRoles(
    val primary: Color,
    val onPrimary: Color,
    val primaryFixed: Color,
    val onPrimaryFixed: Color,
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,
    val outline: Color,
    val outlineVariant: Color,
    val onSurfaceVariant: Color,
)

/** Composites a foreground color over a background color using alpha blending.
 *
 * @param fg The foreground color.
 * @param bg The background color.
 * @return The resulting color after compositing.
 */
private fun compositeOver(fg: Color, bg: Color): Color {
    val fa = fg.alpha
    val ba = bg.alpha
    val outA = fa + ba * (1f - fa)
    if (outA == 0f) return Color(0f, 0f, 0f, 0f)
    val r = (fg.red * fa + bg.red * ba * (1f - fa)) / outA
    val g = (fg.green * fa + bg.green * ba * (1f - fa)) / outA
    val b = (fg.blue * fa + bg.blue * ba * (1f - fa)) / outA
    return Color(r, g, b, outA)
}

/** Composites a foreground color over a background color and returns an opaque color.
 *
 * @param fg The foreground color.
 * @param bg The background color.
 * @return The resulting opaque color after compositing.
 */
private fun opaqueOver(fg: Color, bg: Color): Color {
    val c = compositeOver(fg, bg)
    return Color(c.red, c.green, c.blue, 1f)
}

/** Ensures that the foreground color is opaque over the background color.
 *
 * @param fg The foreground color.
 * @param bg The background color.
 * @return The resulting opaque color.
 */
private fun ensureOpaqueOver(fg: Color, bg: Color): Color = if (fg.alpha >= 1f) fg else opaqueOver(fg, bg)

/** Maps Monet color roles to COUI color scheme.
 *
 * This function flattens colors with alpha transparency over appropriate background colors
 * to ensure all colors in the resulting [Colors] are opaque.
 *
 * @param roles The Monet color roles.
 * @param dark Whether the color scheme is for dark mode.
 * @return The mapped COUI [Colors].
 */
internal fun mapMd3RolesToCOUIColorsCommon(roles: MonetRoles, dark: Boolean): Colors {
    val baseSurface = roles.surface
    val baseSurfaceContainerHigh = roles.surfaceContainerHigh
    val onSurfaceSecondaryOpaque = ensureOpaqueOver(roles.onSurface.copy(alpha = 0.8f), baseSurface)
    val onSurfaceContainerHighOpaque = ensureOpaqueOver(roles.onSurface.copy(alpha = 0.8f), baseSurfaceContainerHigh)
    val sliderBackground = ensureOpaqueOver(roles.primary.copy(alpha = 0.2f), baseSurface)
    val disabledPrimaryOpaque = ensureOpaqueOver(roles.primary.copy(alpha = 0.38f), baseSurface)
    val disabledOnPrimaryOpaque = ensureOpaqueOver(roles.onPrimary.copy(alpha = 0.38f), disabledPrimaryOpaque)
    val disabledPrimaryButtonOpaque = ensureOpaqueOver(roles.primary.copy(alpha = 0.38f), baseSurface)
    val disabledOnPrimaryButtonOpaque = ensureOpaqueOver(roles.onPrimary.copy(alpha = 0.6f), disabledPrimaryButtonOpaque)
    val disabledPrimarySliderOpaque = ensureOpaqueOver(roles.primary.copy(alpha = 0.38f), baseSurface)
    val disabledSecondaryOpaque = ensureOpaqueOver(roles.outlineVariant.copy(alpha = 0.5f), baseSurface)
    val disabledOnSecondaryOpaque = ensureOpaqueOver(roles.onSurface.copy(alpha = 0.38f), disabledSecondaryOpaque)
    val disabledSecondaryVariantOpaque = ensureOpaqueOver(roles.surfaceContainerHigh.copy(alpha = 0.6f), baseSurface)
    val disabledOnSecondaryVariantOpaque = ensureOpaqueOver(roles.onSurface.copy(alpha = 0.38f), disabledSecondaryVariantOpaque)

    return Colors(
        primary = roles.primary,
        onPrimary = roles.onPrimary,
        primaryVariant = roles.primaryFixed,
        onPrimaryVariant = roles.onPrimaryFixed,
        error = roles.error,
        onError = roles.onError,
        errorContainer = roles.errorContainer,
        onErrorContainer = roles.onErrorContainer,
        disabledPrimary = disabledPrimaryOpaque,
        disabledOnPrimary = disabledOnPrimaryOpaque,
        disabledPrimaryButton = disabledPrimaryButtonOpaque,
        disabledOnPrimaryButton = disabledOnPrimaryButtonOpaque,
        disabledPrimarySlider = disabledPrimarySliderOpaque,
        primaryContainer = roles.primaryContainer,
        onPrimaryContainer = roles.onPrimaryContainer,
        secondary = roles.outlineVariant,
        onSecondary = roles.outline,
        secondaryVariant = roles.surfaceContainerHigh,
        onSecondaryVariant = roles.onSurface,
        disabledSecondary = disabledSecondaryOpaque,
        disabledOnSecondary = disabledOnSecondaryOpaque,
        disabledSecondaryVariant = disabledSecondaryVariantOpaque,
        disabledOnSecondaryVariant = disabledOnSecondaryVariantOpaque,
        secondaryContainer = roles.secondaryContainer,
        onSecondaryContainer = roles.onSecondaryContainer,
        secondaryContainerVariant = roles.surfaceContainerHighest,
        onSecondaryContainerVariant = roles.onSurfaceVariant,
        tertiaryContainer = roles.tertiaryContainer,
        onTertiaryContainer = roles.onTertiaryContainer,
        tertiaryContainerVariant = roles.onTertiaryContainer,
        background = roles.background,
        onBackground = roles.onBackground,
        onBackgroundVariant = roles.primary,
        surface = roles.surface,
        onSurface = roles.onSurface,
        surfaceVariant = roles.surfaceVariant,
        onSurfaceSecondary = onSurfaceSecondaryOpaque,
        onSurfaceVariantSummary = roles.onSurfaceVariant,
        onSurfaceVariantActions = roles.onSurfaceVariant,
        disabledOnSurface = roles.onSurface,
        surfaceContainer = roles.surfaceContainer,
        onSurfaceContainer = roles.onSurface,
        onSurfaceContainerVariant = roles.onSurfaceVariant,
        surfaceContainerHigh = roles.surfaceContainerHigh,
        onSurfaceContainerHigh = onSurfaceContainerHighOpaque,
        surfaceContainerHighest = roles.surfaceContainerHighest,
        onSurfaceContainerHighest = roles.onSurface,
        outline = roles.outline,
        dividerLine = roles.outlineVariant,
        windowDimming = if (dark) Color.Black.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.2f),
        // COUI couiColorLabelTertiary off the progress and couiColorLabelOnColor on it.
        sliderKeyPoint = roles.onSurface.copy(alpha = if (dark) 0.3f else 0.26f),
        sliderKeyPointForeground = roles.onPrimary,
        sliderBackground = sliderBackground,
        dialogButton = roles.surfaceContainerHigh,
        dragHandle = roles.outlineVariant,
        secondaryButton = roles.surfaceContainerHigh,
        disabledSecondaryButton = disabledSecondaryVariantOpaque,
        disabledOnSecondaryButton = disabledOnSecondaryVariantOpaque,
        transparentButton = Color.White.copy(alpha = 0.2f),
        onTransparentButton = Color.White,
        disabledOnTransparentButton = Color.White.copy(alpha = 0.3f),
        backgroundTop = roles.surfaceContainerHighest,
        primaryTextOnPopup = roles.primary,
        // State layers stay translucent: they are drawn over the component, as COUI couiColorPress /
        // couiColorHover (neutral 12% / 20% and 8% / 15%) and couiColorFocus (primary 12% / 20%).
        press = roles.onSurface.copy(alpha = if (dark) 0.2f else 0.12f),
        hover = roles.onSurface.copy(alpha = if (dark) 0.15f else 0.08f),
        focus = roles.primary.copy(alpha = if (dark) 0.2f else 0.12f),
        // COUI coui_progressbar_color_disabled (#E6E6E6 / #262626, opaque) and
        // coui_progressbar_background_color_disabled (black 3% / white 20%).
        disabledProgress = roles.surfaceContainerHigh,
        disabledProgressBackground = roles.onSurface.copy(alpha = if (dark) 0.2f else 0.03f),
        // COUI coui_seekbar_thumb_disable_color (white 54% / 16%), coui_seekbar_inactive_mark_disable_color
        // (black 26% / white 16%) and coui_seekbar_thumb_shadow_color (black 10% / 25%).
        disabledSliderThumb = Color.White.copy(alpha = if (dark) 0.16f else 0.54f),
        disabledSliderKeyPoint = roles.onSurface.copy(alpha = if (dark) 0.16f else 0.26f),
        sliderThumbShadow = Color.Black.copy(alpha = if (dark) 0.25f else 0.1f),
        // COUI couiColorErrorTextBg (red 30% / opaque) and coui_textinput_stroke_color_disabled
        // (black 10% / white 10%).
        textFieldError = roles.error.copy(alpha = if (dark) 1f else 0.3f),
        disabledTextFieldBorder = roles.onSurface.copy(alpha = 0.1f),
        // COUI couiColorQuarternaryNeutral (black 8% / white 15%), couiColorContainer8 (black 8% /
        // white 10%), chip_unchecked_text_disable_color (black 26% / white 16%) and
        // couiColorFocusOutline (the theme color at 40%).
        quarternaryNeutral = roles.onSurface.copy(alpha = if (dark) 0.15f else 0.08f),
        container8 = roles.onSurface.copy(alpha = if (dark) 0.1f else 0.08f),
        disabledChipText = roles.onSurface.copy(alpha = if (dark) 0.16f else 0.26f),
        focusOutline = roles.primary.copy(alpha = 0.4f),
    )
}
