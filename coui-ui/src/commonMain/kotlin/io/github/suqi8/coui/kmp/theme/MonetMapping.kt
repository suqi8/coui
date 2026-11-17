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
 * The theme slots follow the COUI single-color rule ([COUIThemeColor.fromColor]) and the label,
 * container and state slots keep their COUI alphas over the tonal onSurface; the surfaces take the
 * tonal surface roles.
 *
 * @param roles The Monet color roles.
 * @param dark Whether the color scheme is for dark mode.
 * @return The mapped COUI [Colors].
 */
internal fun mapMd3RolesToCOUIColorsCommon(roles: MonetRoles, dark: Boolean): Colors {
    val baseSurface = roles.surface
    val sliderBackground = ensureOpaqueOver(roles.primary.copy(alpha = 0.2f), baseSurface)
    val disabledSecondaryVariantOpaque = ensureOpaqueOver(roles.surfaceContainerHigh.copy(alpha = 0.6f), baseSurface)

    // COUI has no tonal mapping: a system theme color only replaces the ten theme attributes of a
    // COUIOverlay.Theme.Single.* preset (COUIThemeOverlay.resolveThemeStyle), so the theme slots follow
    // the single-color rule and the labels keep the translucent couiColorLabel* / couiColorDisabledNeutral
    // alphas, laid over the tonal onSurface instead of black / white.
    val theme = COUIThemeColor.fromColor(roles.primary).tokens(dark)
    val labelSecondary = roles.onSurface.withAlpha(0x8A)
    val labelSecondaryVariant = roles.onSurface.withAlpha(0x66)
    val labelTertiary = roles.onSurface.withAlpha(if (dark) 0x4D else 0x42)
    val elevatedWithCard = if (dark) roles.background else roles.surface

    return Colors(
        primary = roles.primary,
        onPrimary = roles.onPrimary,
        error = roles.error,
        onError = roles.onError,
        // COUI coui_button_main_disable_text (white 54% / 16%).
        disabledOnPrimaryButton = Color.White.withAlpha(if (dark) 0x29 else 0x8A),
        secondaryVariant = roles.surfaceContainerHigh,
        disabledSecondaryVariant = disabledSecondaryVariantOpaque,
        secondaryContainer = roles.secondaryContainer,
        // COUI couiColorSecondary (the theme color at 15% / 25%).
        secondary = theme.secondary,
        surfaceContainerHighest = roles.surfaceContainerHighest,
        outline = roles.outline,
        sliderBackground = sliderBackground,
        dialogButton = roles.surfaceContainerHigh,
        dragHandle = roles.outlineVariant,
        secondaryButton = roles.surfaceContainerHigh,
        disabledSecondaryButton = disabledSecondaryVariantOpaque,
        // COUI coui_btn_secondary_text_color_disable (black 26% / white #28).
        disabledOnSecondaryButton = roles.onSurface.withAlpha(if (dark) 0x28 else 0x42),
        transparentButton = Color.White.copy(alpha = 0.2f),
        onTransparentButton = Color.White,
        disabledOnTransparentButton = Color.White.copy(alpha = 0.3f),
        backgroundTop = roles.surfaceContainerHighest,
        primaryTextOnPopup = theme.primaryTextOnPopup,
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
        // COUI coui_textinput_stroke_color_disabled (black 10% / white 10%).
        disabledTextFieldBorder = roles.onSurface.copy(alpha = 0.1f),
        // COUI couiColorQuarternaryNeutral (black 8% / white 15%), couiColorContainer8 (black 8% /
        // white 10%), chip_unchecked_text_disable_color (black 26% / white 16%) and
        // couiColorFocusOutline (the theme color at 40%).
        quarternaryNeutral = roles.onSurface.copy(alpha = if (dark) 0.15f else 0.08f),
        container8 = roles.onSurface.copy(alpha = if (dark) 0.1f else 0.08f),
        disabledChipText = roles.onSurface.copy(alpha = if (dark) 0.16f else 0.26f),
        focusOutline = theme.focusOutline,
        primaryText = theme.primaryText,
        labelTheme = theme.labelTheme,
        link = theme.labelTheme,
        disable = theme.disable,
        containerTheme = theme.containerTheme,
        containerThemeHalftone = theme.containerThemeHalftone,
        textColorHighlight = theme.secondary,
        errorDisabled = roles.error.withAlpha(0x4D),
        // COUI couiColorErrorTextBg (red 30% / opaque).
        errorTextBg = roles.error.copy(alpha = if (dark) 1f else 0.3f),
        success = if (dark) Color(0xFF24B232) else Color(0xFF00BD13),
        tips = if (dark) Color(0xFF1A8CFF) else Color(0xFF0080FF),
        additional = if (dark) Color(0xFF1A8CFF) else Color(0xFF0080FF),
        assist = Color(0xFF5C93E5),
        // The surface slots take the tonal surfaces their static values coincide with.
        background = roles.background,
        surface = roles.background,
        backgroundElevated = roles.background,
        backgroundElevatedWithCard = elevatedWithCard,
        backgroundWithCard = roles.surface,
        surfaceTop = roles.surfaceContainerHighest,
        surfaceWithCard = elevatedWithCard,
        bar = roles.background,
        bottomBar = roles.background,
        card = roles.surfaceContainer,
        cardBackground = roles.surfaceContainer,
        cardPressed = roles.surfaceContainerHigh,
        labelPrimary = roles.onSurface,
        labelPrimaryVariant = roles.onSurface.withAlpha(0xB8),
        labelSecondary = labelSecondary,
        labelSecondaryVariant = labelSecondaryVariant,
        labelTertiary = labelTertiary,
        labelQuaternary = roles.onSurface.withAlpha(if (dark) 0x26 else 0x14),
        labelOnColor = roles.onPrimary,
        disabledNeutral = labelTertiary,
        hintNeutral = labelTertiary,
        container4 = roles.onSurface.withAlpha(if (dark) 0x1A else 0x0A),
        container12 = roles.onSurface.withAlpha(if (dark) 0x33 else 0x1F),
        container16 = roles.onSurface.withAlpha(if (dark) 0x40 else 0x29),
        containerSolid = roles.surfaceContainerHigh,
        fillSolid = roles.surfaceContainerHigh,
        fillThin = roles.onSurface.withAlpha(if (dark) 0x14 else 0x0A),
        controls = roles.onSurface.withAlpha(if (dark) 0x40 else 0x29),
        divider = roles.outlineVariant,
        mask = if (dark) Color.Black.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.2f),
        pressBackground = roles.onSurface.withAlpha(if (dark) 0x26 else 0x14),
        ripplePressBackground = if (dark) Color(0x80FFFFFF) else Color(0x99B4B4B4),
        // The hue palette is fixed in COUI; no theme overlay replaces it.
        red = if (dark) Color(0xFFEB493D) else Color(0xFFEB3B2F),
        redVariant = if (dark) Color(0xFFCC615A) else Color(0xFFE56E65),
        containerRed = if (dark) Color(0xFF3A3030) else Color(0xFFEEDADC),
        orange = if (dark) Color(0xFFF08222) else Color(0xFFFF7700),
        orangeVariant = if (dark) Color(0xFFCC7F3D) else Color(0xFFE58F45),
        containerOrange = if (dark) Color(0xFF39332C) else Color(0xFFFAE7D6),
        yellow = if (dark) Color(0xFFE5A100) else Color(0xFFFFB200),
        yellowVariant = if (dark) Color(0xFFC29427) else Color(0xFFE5AF2E),
        containerYellow = if (dark) Color(0xFF373530) else Color(0xFFF6E7C6),
        green = if (dark) Color(0xFF24B232) else Color(0xFF00BD13),
        greenVariant = if (dark) Color(0xFF43A84E) else Color(0xFF4BBD57),
        containerGreen = if (dark) Color(0xFF353E36) else Color(0xFFBADECA),
        mint = if (dark) Color(0xFF1AB883) else Color(0xFF00CC88),
        mintVariant = if (dark) Color(0xFF32A881) else Color(0xFF39BD91),
        containerMint = if (dark) Color(0xFF394342) else Color(0xFFBADEDB),
        cyan = if (dark) Color(0xFF25A9B8) else Color(0xFF1DBACC),
        cyanVariant = if (dark) Color(0xFF329DA8) else Color(0xFF39AFBD),
        containerCyan = if (dark) Color(0xFF343E3D) else Color(0xFFBEDADD),
        azure = if (dark) Color(0xFF209DE5) else Color(0xFF00A1FF),
        azureVariant = if (dark) Color(0xFF3D98CC) else Color(0xFF45ABE5),
        containerAzure = if (dark) Color(0xFF373F42) else Color(0xFFCEE5EB),
        blue = if (dark) Color(0xFF1A8CFF) else Color(0xFF0080FF),
        blueVariant = Color(0xFF5C93E5),
        containerBlue = if (dark) Color(0xFF323739) else Color(0xFFC9DAE4),
        violet = if (dark) Color(0xFF7161FF) else Color(0xFF5A47FF),
        violetVariant = Color(0xFF7E73E5),
        containerViolet = if (dark) Color(0xFF3A3A43) else Color(0xFFD6D5EE),
        purple = if (dark) Color(0xFFAC33FF) else Color(0xFFA723FF),
        purpleVariant = if (dark) Color(0xFFA466CC) else Color(0xFFB873E5),
        containerPurple = if (dark) Color(0xFF3A3540) else Color(0xFFE0D6EB),
        // COUI coui_button_neutral_color is a fixed neutral, not a theme role.
        neutralButton = if (dark) Color(0x4CFFFFFF) else Color(0x0F000000),
        isDark = dark,
    )
}
