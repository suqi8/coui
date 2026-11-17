// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * The ten theme attributes a COUI theme color family overrides (`Theme.COUI.<Color>` and the
 * `COUIOverlay.Theme.*` presets that `COUIThemeOverlay` applies at runtime), plus the text selection
 * highlight that some families override as well. Everything else (neutrals, surfaces, the hue palette)
 * stays as it is.
 *
 * @param primary COUI couiColorPrimary.
 * @param containerTheme COUI couiColorContainerTheme.
 * @param containerThemeHalftone COUI couiColorContainerThemeHalftone.
 * @param secondary COUI couiColorSecondary (the translucent theme tint; [Colors.secondary]).
 * @param disable COUI couiColorDisable.
 * @param focus COUI couiColorFocus.
 * @param focusOutline COUI couiColorFocusOutline.
 * @param labelTheme COUI couiColorLabelTheme (also couiColorLink).
 * @param primaryText COUI couiColorPrimaryText (also couiColorOnSecondary).
 * @param primaryTextOnPopup COUI couiColorPrimaryTextOnPopup.
 * @param textColorHighlight COUI android:textColorHighlight (`?couiColorSecondary` unless the family overrides it).
 */
@Immutable
data class COUIThemeColorTokens(
    val primary: Color,
    val containerTheme: Color,
    val containerThemeHalftone: Color,
    val secondary: Color,
    val disable: Color,
    val focus: Color,
    val focusOutline: Color,
    val labelTheme: Color,
    val primaryText: Color,
    val primaryTextOnPopup: Color,
    val textColorHighlight: Color,
)

/**
 * A COUI theme color family: the [COUIThemeColorTokens] of the light and of the dark theme.
 *
 * Apply one with [Colors.withThemeColor], [lightColorScheme] / [darkColorScheme] overloads that take a
 * [COUIThemeColor], or `ThemeController(themeColor = ...)`. The official families are in
 * [COUIThemeColors]; [COUIThemeColor.fromColor] derives a family from any single color.
 */
@Immutable
data class COUIThemeColor(
    val light: COUIThemeColorTokens,
    val dark: COUIThemeColorTokens,
) {
    /** The tokens of the light or the dark theme. */
    fun tokens(dark: Boolean): COUIThemeColorTokens = if (dark) this.dark else light

    companion object {
        /**
         * Derives a family from one color the way the COUI single-color presets
         * (`COUIOverlay.Theme.Single.*`, `couiSingle*` colors) are built: the color itself for
         * primary, theme containers and theme labels; 15% (light) / 30% (dark) for
         * couiColorSecondary and couiColorContainerThemeHalftone; 30% for couiColorDisable;
         * 12% / 20% for couiColorFocus; 40% for couiColorFocusOutline; 30% for the text highlight.
         *
         * @param color The theme color of the light theme.
         * @param darkColor The theme color of the dark theme.
         */
        fun fromColor(color: Color, darkColor: Color = color): COUIThemeColor = COUIThemeColor(
            light = singleTokens(color, dark = false),
            dark = singleTokens(darkColor, dark = true),
        )

        private fun singleTokens(color: Color, dark: Boolean): COUIThemeColorTokens {
            val opaque = color.copy(alpha = 1f)
            val halftone = opaque.withAlpha(if (dark) 0x4D else 0x26)
            return COUIThemeColorTokens(
                primary = opaque,
                containerTheme = opaque,
                containerThemeHalftone = halftone,
                secondary = halftone,
                disable = opaque.withAlpha(0x4D),
                focus = opaque.withAlpha(if (dark) 0x33 else 0x1F),
                focusOutline = opaque.withAlpha(0x66),
                labelTheme = opaque,
                primaryText = opaque,
                primaryTextOnPopup = opaque,
                textColorHighlight = opaque.withAlpha(0x4C),
            )
        }
    }
}

/** The app identities a COUI overlay preset array is indexed by (`couiThemeIdentifier`, `couiGreenIdentifier` = 1 .. `couiSkyBlueIdentifier` = 6). */
enum class COUIThemeIdentity {
    Green,
    Red,
    Yellow,
    Blue,
    Orange,
    SkyBlue,
}

/**
 * The preset arrays `COUIThemeOverlay` picks from: `coui_theme_arrays_default_patch` ([Default]) and
 * `coui_theme_arrays_first` .. `coui_theme_arrays_fifth`.
 */
enum class COUIThemeOverlaySet {
    Default,
    First,
    Second,
    Third,
    Fourth,
    Fifth,
}

/**
 * The official COUI theme color families: the static `Theme.COUI.<Color>` families ([Blue] is the
 * default of [lightColorScheme] / [darkColorScheme]) and every `COUIOverlay.Theme.*` preset.
 */
object COUIThemeColors {
    /** COUI Theme.COUI.Blue / Theme.COUI.Dark.Blue. */
    val Blue = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF0080FF),
            containerTheme = Color(0xFF0080FF),
            containerThemeHalftone = Color(0x260080FF),
            secondary = Color(0x260080FF),
            disable = Color(0x4D0080FF),
            focus = Color(0x1F0080FF),
            focusOutline = Color(0x660080FF),
            labelTheme = Color(0xFF0080FF),
            primaryText = Color(0xFF0080FF),
            primaryTextOnPopup = Color(0xFF0080FF),
            textColorHighlight = Color(0x260080FF), // ?couiColorSecondary
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF1A8CFF),
            containerTheme = Color(0xFF1A8CFF),
            containerThemeHalftone = Color(0x401A8CFF),
            secondary = Color(0x401A8CFF),
            disable = Color(0x4D1A8CFF),
            focus = Color(0x331A8CFF),
            focusOutline = Color(0x665C9DFF),
            labelTheme = Color(0xFF1A8CFF),
            primaryText = Color(0xFF5C9DFF),
            primaryTextOnPopup = Color(0xFF5C9DFF),
            textColorHighlight = Color(0x401A8CFF), // ?couiColorSecondary
        ),
    )

    /** COUI Theme.COUI.Green. */
    val Green = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF00BD13),
            containerTheme = Color(0xFF00BD13),
            containerThemeHalftone = Color(0x2600BD13),
            secondary = Color(0x2600BD13),
            disable = Color(0x4D00BD13),
            focus = Color(0x1F00BD13),
            focusOutline = Color(0x6600990F),
            labelTheme = Color(0xFF00990F),
            primaryText = Color(0xFF00990F),
            primaryTextOnPopup = Color(0xFF00990F),
            textColorHighlight = Color(0x2600BD13), // ?couiColorSecondary
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF24B232),
            containerTheme = Color(0xFF24B232),
            containerThemeHalftone = Color(0x4024B232),
            secondary = Color(0x4024B232),
            disable = Color(0x4D24B232),
            focus = Color(0x3324B232),
            focusOutline = Color(0x6624B232),
            labelTheme = Color(0xFF24B232),
            primaryText = Color(0xFF24B232),
            primaryTextOnPopup = Color(0xFF24B232),
            textColorHighlight = Color(0x4024B232), // ?couiColorSecondary
        ),
    )

    /** COUI Theme.COUI.Red. */
    val Red = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFEB3B2F),
            containerTheme = Color(0xFFEB3B2F),
            containerThemeHalftone = Color(0x26EB3B2F),
            secondary = Color(0x26EB3B2F),
            disable = Color(0x4DEB3B2F),
            focus = Color(0x1FEB3B2F),
            focusOutline = Color(0x66DB382C),
            labelTheme = Color(0xFFDB382C),
            primaryText = Color(0xFFDB382C),
            primaryTextOnPopup = Color(0xFFDB382C),
            textColorHighlight = Color(0x4CEC3E50),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFEB493D),
            containerTheme = Color(0xFFEB493D),
            containerThemeHalftone = Color(0x40EB493D),
            secondary = Color(0x40EB493D),
            disable = Color(0x4DEB493D),
            focus = Color(0x33EB493D),
            focusOutline = Color(0x66FF6C61),
            labelTheme = Color(0xFFFF6C61),
            primaryText = Color(0xFFFF6C61),
            primaryTextOnPopup = Color(0xFFFF6C61),
            textColorHighlight = Color(0x4CEC3E50),
        ),
    )

    /** COUI Theme.COUI.Orange. */
    val Orange = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFFF7700),
            containerTheme = Color(0xFFFF7700),
            containerThemeHalftone = Color(0x26FF7700),
            secondary = Color(0x26FF7700),
            disable = Color(0x4DFF7700),
            focus = Color(0x1FFF7700),
            focusOutline = Color(0x66EB6E00),
            labelTheme = Color(0xFFEB6E00),
            primaryText = Color(0xFFEB6E00),
            primaryTextOnPopup = Color(0xFFEB6E00),
            textColorHighlight = Color(0x4CFD8326),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFF08222),
            containerTheme = Color(0xFFF08222),
            containerThemeHalftone = Color(0x40F08222),
            secondary = Color(0x40F08222),
            disable = Color(0x66F08222),
            focus = Color(0x33F08222),
            focusOutline = Color(0x66F08222),
            labelTheme = Color(0xFFF08222),
            primaryText = Color(0xFFF08222),
            primaryTextOnPopup = Color(0xFFF08222),
            textColorHighlight = Color(0x4CFD8326),
        ),
    )

    /** COUI Theme.COUI.Yellow. */
    val Yellow = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFFFB200),
            containerTheme = Color(0xFFFFB200),
            containerThemeHalftone = Color(0x26FFB200),
            secondary = Color(0x26FFB200),
            disable = Color(0x4DFFB200),
            focus = Color(0x1FFFB200),
            focusOutline = Color(0x66DB9A00),
            labelTheme = Color(0xFFDB9A00),
            primaryText = Color(0xFFDB9A00),
            primaryTextOnPopup = Color(0xFFDB9A00),
            textColorHighlight = Color(0x4CFFBB0E),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFE5A100),
            containerTheme = Color(0xFFE5A100),
            containerThemeHalftone = Color(0x40E5A100),
            secondary = Color(0x40E5A100),
            disable = Color(0x66E5A100),
            focus = Color(0x33E5A100),
            focusOutline = Color(0x66E5A100),
            labelTheme = Color(0xFFE5A100),
            primaryText = Color(0xFFE5A100),
            primaryTextOnPopup = Color(0xFFE5A100),
            textColorHighlight = Color(0x4CFFBB0E),
        ),
    )

    /** COUI COUIOverlay.Theme.Green.Default. */
    val GreenDefault = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF2FB152),
            containerTheme = Color(0xFF2FB152),
            containerThemeHalftone = Color(0x2639BF56),
            secondary = Color(0x2639BF56),
            disable = Color(0x4D2FB152),
            focus = Color(0x1F2FB152),
            focusOutline = Color(0x662FB152),
            labelTheme = Color(0xFF2FB152),
            primaryText = Color(0xFF2FB152),
            primaryTextOnPopup = Color(0xFF2FB152),
            textColorHighlight = Color(0x4C2EC84E),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF2EB85A),
            containerTheme = Color(0xFF2EB85A),
            containerThemeHalftone = Color(0x4D2AD181),
            secondary = Color(0x4D2AD181),
            disable = Color(0x4D2EB85A),
            focus = Color(0x1F2EB85A),
            focusOutline = Color(0x662EB85A),
            labelTheme = Color(0xFF2EB85A),
            primaryText = Color(0xFF2EB85A),
            primaryTextOnPopup = Color(0xFF2EB85A),
            textColorHighlight = Color(0x4C2EC84E),
        ),
    )

    /** COUI COUIOverlay.Theme.Green.First. */
    val GreenFirst = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF2BD982),
            containerTheme = Color(0xFF2BD982),
            containerThemeHalftone = Color(0x262BD982),
            secondary = Color(0x262BD982),
            disable = Color(0x4D2BD982),
            focus = Color(0x1F2BD982),
            focusOutline = Color(0x662BD982),
            labelTheme = Color(0xFF2BD982),
            primaryText = Color(0xFF2BD982),
            primaryTextOnPopup = Color(0xFF2BD982),
            textColorHighlight = Color(0x4C2BD982),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF00C570),
            containerTheme = Color(0xFF00C570),
            containerThemeHalftone = Color(0x4D00C570),
            secondary = Color(0x4D00C570),
            disable = Color(0x4D00C570),
            focus = Color(0x3300C570),
            focusOutline = Color(0x6600C570),
            labelTheme = Color(0xFF00C570),
            primaryText = Color(0xFF00C570),
            primaryTextOnPopup = Color(0xFF00C570),
            textColorHighlight = Color(0x4C2BD982),
        ),
    )

    /** COUI COUIOverlay.Theme.Green.Second. */
    val GreenSecond = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF59865E),
            containerTheme = Color(0xFF59865E),
            containerThemeHalftone = Color(0x2659865E),
            secondary = Color(0x2659865E),
            disable = Color(0x4D59865E),
            focus = Color(0x1F59865E),
            focusOutline = Color(0x6659865E),
            labelTheme = Color(0xFF59865E),
            primaryText = Color(0xFF59865E),
            primaryTextOnPopup = Color(0xFF59865E),
            textColorHighlight = Color(0x4C59865E),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF59865E),
            containerTheme = Color(0xFF59865E),
            containerThemeHalftone = Color(0x4D59865E),
            secondary = Color(0x4D59865E),
            disable = Color(0x4D59865E),
            focus = Color(0x3359865E),
            focusOutline = Color(0x6659865E),
            labelTheme = Color(0xFF59865E),
            primaryText = Color(0xFF59865E),
            primaryTextOnPopup = Color(0xFF59865E),
            textColorHighlight = Color(0x4C59865E),
        ),
    )

    /** COUI COUIOverlay.Theme.Green.Third. */
    val GreenThird = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF4FCB14),
            containerTheme = Color(0xFF4FCB14),
            containerThemeHalftone = Color(0x264FCB14),
            secondary = Color(0x264FCB14),
            disable = Color(0x4D4FCB14),
            focus = Color(0x1F4FCB14),
            focusOutline = Color(0x664FCB14),
            labelTheme = Color(0xFF4FCB14),
            primaryText = Color(0xFF4FCB14),
            primaryTextOnPopup = Color(0xFF4FCB14),
            textColorHighlight = Color(0x4C4FCB14),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF1EA800),
            containerTheme = Color(0xFF1EA800),
            containerThemeHalftone = Color(0x4D1EA800),
            secondary = Color(0x4D1EA800),
            disable = Color(0x4D1EA800),
            focus = Color(0x331EA800),
            focusOutline = Color(0x661EA800),
            labelTheme = Color(0xFF1EA800),
            primaryText = Color(0xFF1EA800),
            primaryTextOnPopup = Color(0xFF1EA800),
            textColorHighlight = Color(0x4C4FCB14),
        ),
    )

    /** COUI COUIOverlay.Theme.Green.Fourth. */
    val GreenFourth = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF8F9974),
            containerTheme = Color(0xFF8F9974),
            containerThemeHalftone = Color(0x268F9974),
            secondary = Color(0x268F9974),
            disable = Color(0x4D8F9974),
            focus = Color(0x1F8F9974),
            focusOutline = Color(0x668F9974),
            labelTheme = Color(0xFF8F9974),
            primaryText = Color(0xFF8F9974),
            primaryTextOnPopup = Color(0xFF8F9974),
            textColorHighlight = Color(0x4C8F9974),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF8F9974),
            containerTheme = Color(0xFF8F9974),
            containerThemeHalftone = Color(0x4D8F9974),
            secondary = Color(0x4D8F9974),
            disable = Color(0x4D8F9974),
            focus = Color(0x338F9974),
            focusOutline = Color(0x668F9974),
            labelTheme = Color(0xFF8F9974),
            primaryText = Color(0xFF8F9974),
            primaryTextOnPopup = Color(0xFF8F9974),
            textColorHighlight = Color(0x4C8F9974),
        ),
    )

    /** COUI COUIOverlay.Theme.Green.Fifth. */
    val GreenFifth = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF2D40E9),
            containerTheme = Color(0xFF2D40E9),
            containerThemeHalftone = Color(0x262D40E9),
            secondary = Color(0x262D40E9),
            disable = Color(0x4D2D40E9),
            focus = Color(0x1F2D40E9),
            focusOutline = Color(0x662D40E9),
            labelTheme = Color(0xFF2D40E9),
            primaryText = Color(0xFF2D40E9),
            primaryTextOnPopup = Color(0xFF2D40E9),
            textColorHighlight = Color(0x4C2D40E9),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF4C65FF),
            containerTheme = Color(0xFF5476FF),
            containerThemeHalftone = Color(0x404C65FF),
            secondary = Color(0x404C65FF),
            disable = Color(0x4D4C65FF),
            focus = Color(0x334C65FF),
            focusOutline = Color(0x664C65FF),
            labelTheme = Color(0xFF4C65FF),
            primaryText = Color(0xFF5476FF),
            primaryTextOnPopup = Color(0xFF6B97FF),
            textColorHighlight = Color(0x4C2D40E9),
        ),
    )

    /** COUI COUIOverlay.Theme.Red.Default. */
    val RedDefault = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFDF3F3A),
            containerTheme = Color(0xFFDF3F3A),
            containerThemeHalftone = Color(0x26EA3447),
            secondary = Color(0x26EA3447),
            disable = Color(0x4DDF3F3A),
            focus = Color(0x1FDF3F3A),
            focusOutline = Color(0x66DF3F3A),
            labelTheme = Color(0xFFDF3F3A),
            primaryText = Color(0xFFDF3F3A),
            primaryTextOnPopup = Color(0xFFDF3F3A),
            textColorHighlight = Color(0x4CEC3E50),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFED413B),
            containerTheme = Color(0xFFED413B),
            containerThemeHalftone = Color(0x33EA3447),
            secondary = Color(0x33EA3447),
            disable = Color(0x4DED413B),
            focus = Color(0x1FED413B),
            focusOutline = Color(0x66ED413B),
            labelTheme = Color(0xFFED413B),
            primaryText = Color(0xFFED413B),
            primaryTextOnPopup = Color(0xFFED413B),
            textColorHighlight = Color(0x4CEC3E50),
        ),
    )

    /** COUI COUIOverlay.Theme.Red.First. */
    val RedFirst = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFF23D5B),
            containerTheme = Color(0xFFF23D5B),
            containerThemeHalftone = Color(0x26F23D5B),
            secondary = Color(0x26F23D5B),
            disable = Color(0x4DF23D5B),
            focus = Color(0x1FF23D5B),
            focusOutline = Color(0x66F23D5B),
            labelTheme = Color(0xFFF23D5B),
            primaryText = Color(0xFFF23D5B),
            primaryTextOnPopup = Color(0xFFF23D5B),
            textColorHighlight = Color(0x4CF23D5B),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFF23D5B),
            containerTheme = Color(0xFFF23D5B),
            containerThemeHalftone = Color(0x4DF23D5B),
            secondary = Color(0x4DF23D5B),
            disable = Color(0x4DF23D5B),
            focus = Color(0x33F23D5B),
            focusOutline = Color(0x66F23D5B),
            labelTheme = Color(0xFFF23D5B),
            primaryText = Color(0xFFF23D5B),
            primaryTextOnPopup = Color(0xFFF23D5B),
            textColorHighlight = Color(0x4CF23D5B),
        ),
    )

    /** COUI COUIOverlay.Theme.Red.Second. */
    val RedSecond = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFB85B57),
            containerTheme = Color(0xFFB85B57),
            containerThemeHalftone = Color(0x26B85B57),
            secondary = Color(0x26B85B57),
            disable = Color(0x4DB85B57),
            focus = Color(0x1FB85B57),
            focusOutline = Color(0x66B85B57),
            labelTheme = Color(0xFFB85B57),
            primaryText = Color(0xFFB85B57),
            primaryTextOnPopup = Color(0xFFB85B57),
            textColorHighlight = Color(0x4CB85B57),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFB85B57),
            containerTheme = Color(0xFFB85B57),
            containerThemeHalftone = Color(0x4DB85B57),
            secondary = Color(0x4DB85B57),
            disable = Color(0x4DB85B57),
            focus = Color(0x33B85B57),
            focusOutline = Color(0x66B85B57),
            labelTheme = Color(0xFFB85B57),
            primaryText = Color(0xFFB85B57),
            primaryTextOnPopup = Color(0xFFB85B57),
            textColorHighlight = Color(0x4CB85B57),
        ),
    )

    /** COUI COUIOverlay.Theme.Red.Third. */
    val RedThird = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFE5006E),
            containerTheme = Color(0xFFE5006E),
            containerThemeHalftone = Color(0x26E5006E),
            secondary = Color(0x26E5006E),
            disable = Color(0x4DE5006E),
            focus = Color(0x1FE5006E),
            focusOutline = Color(0x66E5006E),
            labelTheme = Color(0xFFE5006E),
            primaryText = Color(0xFFE5006E),
            primaryTextOnPopup = Color(0xFFE5006E),
            textColorHighlight = Color(0x4CE5006E),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFDE2B70),
            containerTheme = Color(0xFFDE2B70),
            containerThemeHalftone = Color(0x4DDE2B70),
            secondary = Color(0x4DDE2B70),
            disable = Color(0x4DDE2B70),
            focus = Color(0x33DE2B70),
            focusOutline = Color(0x66DE2B70),
            labelTheme = Color(0xFFDE2B70),
            primaryText = Color(0xFFDE2B70),
            primaryTextOnPopup = Color(0xFFDE2B70),
            textColorHighlight = Color(0x4CE5006E),
        ),
    )

    /** COUI COUIOverlay.Theme.Red.Fourth. */
    val RedFourth = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF9A706C),
            containerTheme = Color(0xFF9A706C),
            containerThemeHalftone = Color(0x269A706C),
            secondary = Color(0x269A706C),
            disable = Color(0x4D9A706C),
            focus = Color(0x1F9A706C),
            focusOutline = Color(0x669A706C),
            labelTheme = Color(0xFF9A706C),
            primaryText = Color(0xFF9A706C),
            primaryTextOnPopup = Color(0xFF9A706C),
            textColorHighlight = Color(0x4C9A706C),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF9A706C),
            containerTheme = Color(0xFF9A706C),
            containerThemeHalftone = Color(0x4D9A706C),
            secondary = Color(0x4D9A706C),
            disable = Color(0x4D9A706C),
            focus = Color(0x339A706C),
            focusOutline = Color(0x669A706C),
            labelTheme = Color(0xFF9A706C),
            primaryText = Color(0xFF9A706C),
            primaryTextOnPopup = Color(0xFF9A706C),
            textColorHighlight = Color(0x4C9A706C),
        ),
    )

    /** COUI COUIOverlay.Theme.Red.Fifth. */
    val RedFifth = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFE32E27),
            containerTheme = Color(0xFFE32E27),
            containerThemeHalftone = Color(0x26E32E27),
            secondary = Color(0x26E32E27),
            disable = Color(0x4DE32E27),
            focus = Color(0x1FE32E27),
            focusOutline = Color(0x66E32E27),
            labelTheme = Color(0xFFE32E27),
            primaryText = Color(0xFFE32E27),
            primaryTextOnPopup = Color(0xFFE32E27),
            textColorHighlight = Color(0x4CE32E27),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFEA3939),
            containerTheme = Color(0xFFEA3939),
            containerThemeHalftone = Color(0x40EA3939),
            secondary = Color(0x40EA3939),
            disable = Color(0x4DEA3939),
            focus = Color(0x33EA3939),
            focusOutline = Color(0x66EA3939),
            labelTheme = Color(0xFFEA3939),
            primaryText = Color(0xFFEA3939),
            primaryTextOnPopup = Color(0xFFEA3939),
            textColorHighlight = Color(0x4CE32E27),
        ),
    )

    /** COUI COUIOverlay.Theme.Yellow.Default. */
    val YellowDefault = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFFFB200),
            containerTheme = Color(0xFFFFB200),
            containerThemeHalftone = Color(0x26FAC000),
            secondary = Color(0x26FAC000),
            disable = Color(0x4DFFB200),
            focus = Color(0x1FFFB200),
            focusOutline = Color(0x66FFB200),
            labelTheme = Color(0xFFFFB200),
            primaryText = Color(0xFFFFB200),
            primaryTextOnPopup = Color(0xFFFFB200),
            textColorHighlight = Color(0x4CFFBB0E),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFFFBA19),
            containerTheme = Color(0xFFFFBA19),
            containerThemeHalftone = Color(0x4DFAC000),
            secondary = Color(0x4DFAC000),
            disable = Color(0x4DFFBA19),
            focus = Color(0x1FFFBA19),
            focusOutline = Color(0x66FFBA19),
            labelTheme = Color(0xFFFFBA19),
            primaryText = Color(0xFFFFBA19),
            primaryTextOnPopup = Color(0xFFFFBA19),
            textColorHighlight = Color(0x4CFFBB0E),
        ),
    )

    /** COUI COUIOverlay.Theme.Yellow.First. */
    val YellowFirst = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFFFCC4D),
            containerTheme = Color(0xFFFFCC4D),
            containerThemeHalftone = Color(0x26FFCC4D),
            secondary = Color(0x26FFCC4D),
            disable = Color(0x4DFFCC4D),
            focus = Color(0x1FFFCC4D),
            focusOutline = Color(0x66FFCC4D),
            labelTheme = Color(0xFFFFCC4D),
            primaryText = Color(0xFFFFCC4D),
            primaryTextOnPopup = Color(0xFFFFCC4D),
            textColorHighlight = Color(0x4CFFCC4D),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFF2BF41),
            containerTheme = Color(0xFFF2BF41),
            containerThemeHalftone = Color(0x4DF2BF41),
            secondary = Color(0x4DF2BF41),
            disable = Color(0x4DF2BF41),
            focus = Color(0x33F2BF41),
            focusOutline = Color(0x66F2BF41),
            labelTheme = Color(0xFFF2BF41),
            primaryText = Color(0xFFF2BF41),
            primaryTextOnPopup = Color(0xFFF2BF41),
            textColorHighlight = Color(0x4CFFCC4D),
        ),
    )

    /** COUI COUIOverlay.Theme.Yellow.Second. */
    val YellowSecond = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFB28E2B),
            containerTheme = Color(0xFFB28E2B),
            containerThemeHalftone = Color(0x26B28E2B),
            secondary = Color(0x26B28E2B),
            disable = Color(0x4DB28E2B),
            focus = Color(0x1FB28E2B),
            focusOutline = Color(0x66B28E2B),
            labelTheme = Color(0xFFB28E2B),
            primaryText = Color(0xFFB28E2B),
            primaryTextOnPopup = Color(0xFFB28E2B),
            textColorHighlight = Color(0x4CB28E2B),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFB28E2B),
            containerTheme = Color(0xFFB28E2B),
            containerThemeHalftone = Color(0x4DB28E2B),
            secondary = Color(0x4DB28E2B),
            disable = Color(0x4DB28E2B),
            focus = Color(0x33B28E2B),
            focusOutline = Color(0x66B28E2B),
            labelTheme = Color(0xFFB28E2B),
            primaryText = Color(0xFFB28E2B),
            primaryTextOnPopup = Color(0xFFB28E2B),
            textColorHighlight = Color(0x4CB28E2B),
        ),
    )

    /** COUI COUIOverlay.Theme.Yellow.Third. */
    val YellowThird = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFFFC61A),
            containerTheme = Color(0xFFFFC61A),
            containerThemeHalftone = Color(0x26FFC61A),
            secondary = Color(0x26FFC61A),
            disable = Color(0x4DFFC61A),
            focus = Color(0x1FFFC61A),
            focusOutline = Color(0x66FFC61A),
            labelTheme = Color(0xFFFFC61A),
            primaryText = Color(0xFFFFC61A),
            primaryTextOnPopup = Color(0xFFFFC61A),
            textColorHighlight = Color(0x4CFFC61A),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFFFC61A),
            containerTheme = Color(0xFFFFC61A),
            containerThemeHalftone = Color(0x4DFFC61A),
            secondary = Color(0x4DFFC61A),
            disable = Color(0x4DFFC61A),
            focus = Color(0x33FFC61A),
            focusOutline = Color(0x66FFC61A),
            labelTheme = Color(0xFFFFC61A),
            primaryText = Color(0xFFFFC61A),
            primaryTextOnPopup = Color(0xFFFFC61A),
            textColorHighlight = Color(0x4CFFC61A),
        ),
    )

    /** COUI COUIOverlay.Theme.Yellow.Fourth. */
    val YellowFourth = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF99856B),
            containerTheme = Color(0xFF99856B),
            containerThemeHalftone = Color(0x2699856B),
            secondary = Color(0x2699856B),
            disable = Color(0x4D99856B),
            focus = Color(0x1F99856B),
            focusOutline = Color(0x6699856B),
            labelTheme = Color(0xFF99856B),
            primaryText = Color(0xFF99856B),
            primaryTextOnPopup = Color(0xFF99856B),
            textColorHighlight = Color(0x4C99856B),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF99856B),
            containerTheme = Color(0xFF99856B),
            containerThemeHalftone = Color(0x4D99856B),
            secondary = Color(0x4D99856B),
            disable = Color(0x4D99856B),
            focus = Color(0x3399856B),
            focusOutline = Color(0x6699856B),
            labelTheme = Color(0xFF99856B),
            primaryText = Color(0xFF99856B),
            primaryTextOnPopup = Color(0xFF99856B),
            textColorHighlight = Color(0x4C99856B),
        ),
    )

    /** COUI COUIOverlay.Theme.Yellow.Fifth. */
    val YellowFifth = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFFFC014),
            containerTheme = Color(0xFFFFC014),
            containerThemeHalftone = Color(0x26FFC014),
            secondary = Color(0x26FFC014),
            disable = Color(0x4DFFC014),
            focus = Color(0x1FFFC014),
            focusOutline = Color(0x66FFC014),
            labelTheme = Color(0xFFFFC014),
            primaryText = Color(0xFFFFC014),
            primaryTextOnPopup = Color(0xFFFFC014),
            textColorHighlight = Color(0x4CFFC014),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFFAC637),
            containerTheme = Color(0xFFFAC637),
            containerThemeHalftone = Color(0x40FAC637),
            secondary = Color(0x40FAC637),
            disable = Color(0x4DFAC637),
            focus = Color(0x33FAC637),
            focusOutline = Color(0x66FAC637),
            labelTheme = Color(0xFFFAC637),
            primaryText = Color(0xFFFAC637),
            primaryTextOnPopup = Color(0xFFFAC637),
            textColorHighlight = Color(0x4CFFC014),
        ),
    )

    /** COUI COUIOverlay.Theme.Blue.Default. */
    val BlueDefault = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF006AFF),
            containerTheme = Color(0xFF006AFF),
            containerThemeHalftone = Color(0x26007AFF),
            secondary = Color(0x26007AFF),
            disable = Color(0x4D006AFF),
            focus = Color(0x1F006AFF),
            focusOutline = Color(0x66006AFF),
            labelTheme = Color(0xFF006AFF),
            primaryText = Color(0xFF006AFF),
            primaryTextOnPopup = Color(0xFF006AFF),
            textColorHighlight = Color(0x4C2660F5),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF1875F5),
            containerTheme = Color(0xFF1875F5),
            containerThemeHalftone = Color(0x4D007AFF),
            secondary = Color(0x4D007AFF),
            disable = Color(0x4D1875F5),
            focus = Color(0x1F1875F5),
            focusOutline = Color(0x661875F5),
            labelTheme = Color(0xFF1875F5),
            primaryText = Color(0xFF1875F5),
            primaryTextOnPopup = Color(0xFF1875F5),
            textColorHighlight = Color(0x4C2660F5),
        ),
    )

    /** COUI COUIOverlay.Theme.Blue.First. */
    val BlueFirst = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF36D9D9),
            containerTheme = Color(0xFF36D9D9),
            containerThemeHalftone = Color(0x2636D9D9),
            secondary = Color(0x2636D9D9),
            disable = Color(0x4D36D9D9),
            focus = Color(0x1F36D9D9),
            focusOutline = Color(0x6636D9D9),
            labelTheme = Color(0xFF36D9D9),
            primaryText = Color(0xFF36D9D9),
            primaryTextOnPopup = Color(0xFF36D9D9),
            textColorHighlight = Color(0x4C36D9D9),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF00BFC0),
            containerTheme = Color(0xFF00BFC0),
            containerThemeHalftone = Color(0x4D00BFC0),
            secondary = Color(0x4D00BFC0),
            disable = Color(0x4D00BFC0),
            focus = Color(0x3300BFC0),
            focusOutline = Color(0x6600BFC0),
            labelTheme = Color(0xFF00BFC0),
            primaryText = Color(0xFF00BFC0),
            primaryTextOnPopup = Color(0xFF00BFC0),
            textColorHighlight = Color(0x4C36D9D9),
        ),
    )

    /** COUI COUIOverlay.Theme.Blue.Second. */
    val BlueSecond = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF437BD6),
            containerTheme = Color(0xFF437BD6),
            containerThemeHalftone = Color(0x26437BD6),
            secondary = Color(0x26437BD6),
            disable = Color(0x4D437BD6),
            focus = Color(0x1F437BD6),
            focusOutline = Color(0x66437BD6),
            labelTheme = Color(0xFF437BD6),
            primaryText = Color(0xFF437BD6),
            primaryTextOnPopup = Color(0xFF437BD6),
            textColorHighlight = Color(0x4D437BD6),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF578ADA),
            containerTheme = Color(0xFF578ADA),
            containerThemeHalftone = Color(0x4D578ADA),
            secondary = Color(0x4D578ADA),
            disable = Color(0x4D578ADA),
            focus = Color(0x33578ADA),
            focusOutline = Color(0x66578ADA),
            labelTheme = Color(0xFF578ADA),
            primaryText = Color(0xFF578ADA),
            primaryTextOnPopup = Color(0xFF578ADA),
            textColorHighlight = Color(0x4D437BD6),
        ),
    )

    /** COUI COUIOverlay.Theme.Blue.Third. */
    val BlueThird = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF2167FD),
            containerTheme = Color(0xFF2167FD),
            containerThemeHalftone = Color(0x262167FD),
            secondary = Color(0x262167FD),
            disable = Color(0x4D2167FD),
            focus = Color(0x1F2167FD),
            focusOutline = Color(0x662167FD),
            labelTheme = Color(0xFF2167FD),
            primaryText = Color(0xFF2167FD),
            primaryTextOnPopup = Color(0xFF2167FD),
            textColorHighlight = Color(0x4D2167FD),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF4D8EFF),
            containerTheme = Color(0xFF4D8EFF),
            containerThemeHalftone = Color(0x4D4D8EFF),
            secondary = Color(0x4D4D8EFF),
            disable = Color(0x4D4D8EFF),
            focus = Color(0x334D8EFF),
            focusOutline = Color(0x664D8EFF),
            labelTheme = Color(0xFF4D8EFF),
            primaryText = Color(0xFF4D8EFF),
            primaryTextOnPopup = Color(0xFF4D8EFF),
            textColorHighlight = Color(0x4D2167FD),
        ),
    )

    /** COUI COUIOverlay.Theme.Blue.Fourth. */
    val BlueFourth = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF828EA6),
            containerTheme = Color(0xFF828EA6),
            containerThemeHalftone = Color(0x26828EA6),
            secondary = Color(0x26828EA6),
            disable = Color(0x4D828EA6),
            focus = Color(0x1F828EA6),
            focusOutline = Color(0x66828EA6),
            labelTheme = Color(0xFF828EA6),
            primaryText = Color(0xFF828EA6),
            primaryTextOnPopup = Color(0xFF828EA6),
            textColorHighlight = Color(0x4D828EA6),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF929CAF),
            containerTheme = Color(0xFF929CAF),
            containerThemeHalftone = Color(0x4D929CAF),
            secondary = Color(0x4D929CAF),
            disable = Color(0x4D929CAF),
            focus = Color(0x33929CAF),
            focusOutline = Color(0x66929CAF),
            labelTheme = Color(0xFF929CAF),
            primaryText = Color(0xFF929CAF),
            primaryTextOnPopup = Color(0xFF929CAF),
            textColorHighlight = Color(0x4D828EA6),
        ),
    )

    /** COUI COUIOverlay.Theme.Blue.Fifth. */
    val BlueFifth = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF00BD13),
            containerTheme = Color(0xFF00BD13),
            containerThemeHalftone = Color(0x2600BD13),
            secondary = Color(0x2600BD13),
            disable = Color(0x4D00BD13),
            focus = Color(0x1F00BD13),
            focusOutline = Color(0x6600BD13),
            labelTheme = Color(0xFF00BD13),
            primaryText = Color(0xFF00BD13),
            primaryTextOnPopup = Color(0xFF00BD13),
            textColorHighlight = Color(0x4C00BD13),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF24B232),
            containerTheme = Color(0xFF24B232),
            containerThemeHalftone = Color(0x4024B232),
            secondary = Color(0x4024B232),
            disable = Color(0x4D24B232),
            focus = Color(0x3324B232),
            focusOutline = Color(0x6624B232),
            labelTheme = Color(0xFF24B232),
            primaryText = Color(0xFF24B232),
            primaryTextOnPopup = Color(0xFF24B232),
            textColorHighlight = Color(0x4C00BD13),
        ),
    )

    /** COUI COUIOverlay.Theme.Orange.Default. */
    val OrangeDefault = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFFB7C46),
            containerTheme = Color(0xFFFB7C46),
            containerThemeHalftone = Color(0x26FF6436),
            secondary = Color(0x26FF6436),
            disable = Color(0x4DFB7C46),
            focus = Color(0x1FFB7C46),
            focusOutline = Color(0x66FB7C46),
            labelTheme = Color(0xFFFB7C46),
            primaryText = Color(0xFFFB7C46),
            primaryTextOnPopup = Color(0xFFFB7C46),
            textColorHighlight = Color(0x4CFD8326),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFFE7C48),
            containerTheme = Color(0xFFFE7C48),
            containerThemeHalftone = Color(0x26FF6436),
            secondary = Color(0x26FF6436),
            disable = Color(0x4DFE7C48),
            focus = Color(0x1FFE7C48),
            focusOutline = Color(0x66FE7C48),
            labelTheme = Color(0xFFFE7C48),
            primaryText = Color(0xFFFE7C48),
            primaryTextOnPopup = Color(0xFFFE7C48),
            textColorHighlight = Color(0x4CFD8326),
        ),
    )

    /** COUI COUIOverlay.Theme.Orange.First. */
    val OrangeFirst = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFFF7733),
            containerTheme = Color(0xFFFF7733),
            containerThemeHalftone = Color(0x26FF7733),
            secondary = Color(0x26FF7733),
            disable = Color(0x4DFF7733),
            focus = Color(0x1FFF7733),
            focusOutline = Color(0x66FF7733),
            labelTheme = Color(0xFFFF7733),
            primaryText = Color(0xFFFF7733),
            primaryTextOnPopup = Color(0xFFFF7733),
            textColorHighlight = Color(0x4CFF7733),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFED6825),
            containerTheme = Color(0xFFED6825),
            containerThemeHalftone = Color(0x4DED6825),
            secondary = Color(0x4DED6825),
            disable = Color(0x4DED6825),
            focus = Color(0x33ED6825),
            focusOutline = Color(0x66ED6825),
            labelTheme = Color(0xFFED6825),
            primaryText = Color(0xFFED6825),
            primaryTextOnPopup = Color(0xFFED6825),
            textColorHighlight = Color(0x4CFF7733),
        ),
    )

    /** COUI COUIOverlay.Theme.Orange.Second. */
    val OrangeSecond = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFAF6533),
            containerTheme = Color(0xFFAF6533),
            containerThemeHalftone = Color(0x26AF6533),
            secondary = Color(0x26AF6533),
            disable = Color(0x4DAF6533),
            focus = Color(0x1FAF6533),
            focusOutline = Color(0x66AF6533),
            labelTheme = Color(0xFFAF6533),
            primaryText = Color(0xFFAF6533),
            primaryTextOnPopup = Color(0xFFAF6533),
            textColorHighlight = Color(0x4CAF6533),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFAF6533),
            containerTheme = Color(0xFFAF6533),
            containerThemeHalftone = Color(0x4DAF6533),
            secondary = Color(0x4DAF6533),
            disable = Color(0x4DAF6533),
            focus = Color(0x33AF6533),
            focusOutline = Color(0x66AF6533),
            labelTheme = Color(0xFFAF6533),
            primaryText = Color(0xFFAF6533),
            primaryTextOnPopup = Color(0xFFAF6533),
            textColorHighlight = Color(0x4CAF6533),
        ),
    )

    /** COUI COUIOverlay.Theme.Orange.Third. */
    val OrangeThird = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFFF661A),
            containerTheme = Color(0xFFFF661A),
            containerThemeHalftone = Color(0x26FF661A),
            secondary = Color(0x26FF661A),
            disable = Color(0x4DFF661A),
            focus = Color(0x1FFF661A),
            focusOutline = Color(0x66FF661A),
            labelTheme = Color(0xFFFF661A),
            primaryText = Color(0xFFFF661A),
            primaryTextOnPopup = Color(0xFFFF661A),
            textColorHighlight = Color(0x4CFF661A),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFFF661A),
            containerTheme = Color(0xFFFF661A),
            containerThemeHalftone = Color(0x4DFF661A),
            secondary = Color(0x4DFF661A),
            disable = Color(0x4DFF661A),
            focus = Color(0x33FF661A),
            focusOutline = Color(0x66FF661A),
            labelTheme = Color(0xFFFF661A),
            primaryText = Color(0xFFFF661A),
            primaryTextOnPopup = Color(0xFFFF661A),
            textColorHighlight = Color(0x4CFF661A),
        ),
    )

    /** COUI COUIOverlay.Theme.Orange.Fourth. */
    val OrangeFourth = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF99725C),
            containerTheme = Color(0xFF99725C),
            containerThemeHalftone = Color(0x2699725C),
            secondary = Color(0x2699725C),
            disable = Color(0x4D99725C),
            focus = Color(0x1F99725C),
            focusOutline = Color(0x6699725C),
            labelTheme = Color(0xFF99725C),
            primaryText = Color(0xFF99725C),
            primaryTextOnPopup = Color(0xFF99725C),
            textColorHighlight = Color(0x4C99725C),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF99725C),
            containerTheme = Color(0xFF99725C),
            containerThemeHalftone = Color(0x4D99725C),
            secondary = Color(0x4D99725C),
            disable = Color(0x4D99725C),
            focus = Color(0x3399725C),
            focusOutline = Color(0x6699725C),
            labelTheme = Color(0xFF99725C),
            primaryText = Color(0xFF99725C),
            primaryTextOnPopup = Color(0xFF99725C),
            textColorHighlight = Color(0x4C99725C),
        ),
    )

    /** COUI COUIOverlay.Theme.Orange.Fifth. */
    val OrangeFifth = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFFB6A35),
            containerTheme = Color(0xFFFB6A35),
            containerThemeHalftone = Color(0x26FB6A35),
            secondary = Color(0x26FB6A35),
            disable = Color(0x4DFB6A35),
            focus = Color(0x1FFB6A35),
            focusOutline = Color(0x66FB6A35),
            labelTheme = Color(0xFFFB6A35),
            primaryText = Color(0xFFFB6A35),
            primaryTextOnPopup = Color(0xFFFB6A35),
            textColorHighlight = Color(0x4CFB6A35),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFFE7848),
            containerTheme = Color(0xFFFE7848),
            containerThemeHalftone = Color(0x40FE7848),
            secondary = Color(0x40FE7848),
            disable = Color(0x4DFE7848),
            focus = Color(0x33FE7848),
            focusOutline = Color(0x66FE7848),
            labelTheme = Color(0xFFFE7848),
            primaryText = Color(0xFFFE7848),
            primaryTextOnPopup = Color(0xFFFE7848),
            textColorHighlight = Color(0x4CFB6A35),
        ),
    )

    /** COUI COUIOverlay.Theme.SkyBlue.Default. */
    val SkyBlueDefault = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF38D7D5),
            containerTheme = Color(0xFF38D7D5),
            containerThemeHalftone = Color(0x2638D7D5),
            secondary = Color(0x2638D7D5),
            disable = Color(0x4D38D7D5),
            focus = Color(0x1F38D7D5),
            focusOutline = Color(0x6638D7D5),
            labelTheme = Color(0xFF38D7D5),
            primaryText = Color(0xFF38D7D5),
            primaryTextOnPopup = Color(0xFF38D7D5),
            textColorHighlight = Color(0x4C38D7D5),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF38D7D5),
            containerTheme = Color(0xFF38D7D5),
            containerThemeHalftone = Color(0x4D38D7D5),
            secondary = Color(0x4D38D7D5),
            disable = Color(0x4D38D7D5),
            focus = Color(0x1F38D7D5),
            focusOutline = Color(0x6638D7D5),
            labelTheme = Color(0xFF38D7D5),
            primaryText = Color(0xFF38D7D5),
            primaryTextOnPopup = Color(0xFF38D7D5),
            textColorHighlight = Color(0x4C38D7D5),
        ),
    )

    /** COUI COUIOverlay.Theme.SkyBlue.First. */
    val SkyBlueFirst = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF4696B4),
            containerTheme = Color(0xFF4696B4),
            containerThemeHalftone = Color(0x264696B4),
            secondary = Color(0x264696B4),
            disable = Color(0x4D4696B4),
            focus = Color(0x1F4696B4),
            focusOutline = Color(0x664696B4),
            labelTheme = Color(0xFF4696B4),
            primaryText = Color(0xFF4696B4),
            primaryTextOnPopup = Color(0xFF4696B4),
            textColorHighlight = Color(0x4C4696B4),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF4696B4),
            containerTheme = Color(0xFF4696B4),
            containerThemeHalftone = Color(0x4D4696B4),
            secondary = Color(0x4D4696B4),
            disable = Color(0x4D4696B4),
            focus = Color(0x334696B4),
            focusOutline = Color(0x664696B4),
            labelTheme = Color(0xFF4696B4),
            primaryText = Color(0xFF4696B4),
            primaryTextOnPopup = Color(0xFF4696B4),
            textColorHighlight = Color(0x4C4696B4),
        ),
    )

    /** COUI COUIOverlay.Theme.SkyBlue.Second. */
    val SkyBlueSecond = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFFFCB1B),
            containerTheme = Color(0xFFFFCB1B),
            containerThemeHalftone = Color(0x26FFCB1B),
            secondary = Color(0x26FFCB1B),
            disable = Color(0x4DFFCB1B),
            focus = Color(0x1FFFCB1B),
            focusOutline = Color(0x66FFCB1B),
            labelTheme = Color(0xFFFFCB1B),
            primaryText = Color(0xFFFFCB1B),
            primaryTextOnPopup = Color(0xFFFFCB1B),
            textColorHighlight = Color(0x4CFFCB1B),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFFFCB1B),
            containerTheme = Color(0xFFFFCB1B),
            containerThemeHalftone = Color(0x4DFFCB1B),
            secondary = Color(0x4DFFCB1B),
            disable = Color(0x4DFFCB1B),
            focus = Color(0x33FFCB1B),
            focusOutline = Color(0x66FFCB1B),
            labelTheme = Color(0xFFFFCB1B),
            primaryText = Color(0xFFFFCB1B),
            primaryTextOnPopup = Color(0xFFFFCB1B),
            textColorHighlight = Color(0x4CFFCB1B),
        ),
    )

    /** COUI COUIOverlay.Theme.SkyBlue.Third. */
    val SkyBlueThird = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFDDB4B5),
            containerTheme = Color(0xFFDDB4B5),
            containerThemeHalftone = Color(0x26DDB4B5),
            secondary = Color(0x260080FF), // not overridden: Theme.COUI couiColorSecondary
            disable = Color(0x4DDDB4B5),
            focus = Color(0x1FDDB4B5),
            focusOutline = Color(0x66DDB4B5),
            labelTheme = Color(0xFFDDB4B5),
            primaryText = Color(0xFFDDB4B5),
            primaryTextOnPopup = Color(0x26DDB4B5),
            textColorHighlight = Color(0x4CDDB4B5),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFDDB4B5),
            containerTheme = Color(0xFFDDB4B5),
            containerThemeHalftone = Color(0x4DDDB4B5),
            secondary = Color(0x401A8CFF), // not overridden: Theme.COUI couiColorSecondary
            disable = Color(0x4DDDB4B5),
            focus = Color(0x33DDB4B5),
            focusOutline = Color(0x66DDB4B5),
            labelTheme = Color(0xFFDDB4B5),
            primaryText = Color(0xFFDDB4B5),
            primaryTextOnPopup = Color(0x4DDDB4B5),
            textColorHighlight = Color(0x4CDDB4B5),
        ),
    )

    /** COUI COUIOverlay.Theme.SkyBlue.Fourth. */
    val SkyBlueFourth = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFD7C9AF),
            containerTheme = Color(0xFFD7C9AF),
            containerThemeHalftone = Color(0x26D7C9AF),
            secondary = Color(0x26D7C9AF),
            disable = Color(0x4DD7C9AF),
            focus = Color(0x1FD7C9AF),
            focusOutline = Color(0x66D7C9AF),
            labelTheme = Color(0xFFD7C9AF),
            primaryText = Color(0xFFD7C9AF),
            primaryTextOnPopup = Color(0xFFD7C9AF),
            textColorHighlight = Color(0x4CD7C9AF),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFD7C9AF),
            containerTheme = Color(0xFFD7C9AF),
            containerThemeHalftone = Color(0x4DD7C9AF),
            secondary = Color(0x4DD7C9AF),
            disable = Color(0x4DD7C9AF),
            focus = Color(0x33D7C9AF),
            focusOutline = Color(0x66D7C9AF),
            labelTheme = Color(0xFFD7C9AF),
            primaryText = Color(0xFFD7C9AF),
            primaryTextOnPopup = Color(0xFFD7C9AF),
            textColorHighlight = Color(0x4CD7C9AF),
        ),
    )

    /** COUI COUIOverlay.Theme.SkyBlue.Fifth. */
    val SkyBlueFifth = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF952AF4),
            containerTheme = Color(0xFF952AF4),
            containerThemeHalftone = Color(0x26952AF4),
            secondary = Color(0x26952AF4),
            disable = Color(0x4D952AF4),
            focus = Color(0x1F952AF4),
            focusOutline = Color(0x66952AF4),
            labelTheme = Color(0xFF952AF4),
            primaryText = Color(0xFF952AF4),
            primaryTextOnPopup = Color(0xFF952AF4),
            textColorHighlight = Color(0x4C4696B4),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFA539FF),
            containerTheme = Color(0xFFA539FF),
            containerThemeHalftone = Color(0x40A539FF),
            secondary = Color(0x40A539FF),
            disable = Color(0x4DA539FF),
            focus = Color(0x33A539FF),
            focusOutline = Color(0x66A539FF),
            labelTheme = Color(0xFFA539FF),
            primaryText = Color(0xFFA539FF),
            primaryTextOnPopup = Color(0xFFA539FF),
            textColorHighlight = Color(0x4C4696B4),
        ),
    )

    /** COUI COUIOverlay.Theme.Single.First. */
    val SingleFirst = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFFF6436),
            containerTheme = Color(0xFFFF6436),
            containerThemeHalftone = Color(0x26FF6436),
            secondary = Color(0x26FF6436),
            disable = Color(0x4DFF6436),
            focus = Color(0x1FFF6436),
            focusOutline = Color(0x66FF6436),
            labelTheme = Color(0xFFFF6436),
            primaryText = Color(0xFFFF6436),
            primaryTextOnPopup = Color(0xFFFF6436),
            textColorHighlight = Color(0x4CFF6436),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFFF6436),
            containerTheme = Color(0xFFFF6436),
            containerThemeHalftone = Color(0x4DFF6436),
            secondary = Color(0x4DFF6436),
            disable = Color(0x4DFF6436),
            focus = Color(0x33FF6436),
            focusOutline = Color(0x66FF6436),
            labelTheme = Color(0xFFFF6436),
            primaryText = Color(0xFFFF6436),
            primaryTextOnPopup = Color(0xFFFF6436),
            textColorHighlight = Color(0x4CFF6436),
        ),
    )

    /** COUI COUIOverlay.Theme.Single.Second. */
    val SingleSecond = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFFFCB1B),
            containerTheme = Color(0xFFFFCB1B),
            containerThemeHalftone = Color(0x26FFCB1B),
            secondary = Color(0x26FFCB1B),
            disable = Color(0x4DFFCB1B),
            focus = Color(0x1FFFCB1B),
            focusOutline = Color(0x66FFCB1B),
            labelTheme = Color(0xFFFFCB1B),
            primaryText = Color(0xFFFFCB1B),
            primaryTextOnPopup = Color(0xFFFFCB1B),
            textColorHighlight = Color(0x4CFFCB1B),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFFFCB1B),
            containerTheme = Color(0xFFFFCB1B),
            containerThemeHalftone = Color(0x4DFFCB1B),
            secondary = Color(0x4DFFCB1B),
            disable = Color(0x4DFFCB1B),
            focus = Color(0x33FFCB1B),
            focusOutline = Color(0x66FFCB1B),
            labelTheme = Color(0xFFFFCB1B),
            primaryText = Color(0xFFFFCB1B),
            primaryTextOnPopup = Color(0xFFFFCB1B),
            textColorHighlight = Color(0x4CFFCB1B),
        ),
    )

    /** COUI COUIOverlay.Theme.Single.Third. */
    val SingleThird = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF38D7D5),
            containerTheme = Color(0xFF38D7D5),
            containerThemeHalftone = Color(0x2638D7D5),
            secondary = Color(0x2638D7D5),
            disable = Color(0x4D38D7D5),
            focus = Color(0x1F38D7D5),
            focusOutline = Color(0x6638D7D5),
            labelTheme = Color(0xFF38D7D5),
            primaryText = Color(0xFF38D7D5),
            primaryTextOnPopup = Color(0xFF38D7D5),
            textColorHighlight = Color(0x4C38D7D5),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF38D7D5),
            containerTheme = Color(0xFF38D7D5),
            containerThemeHalftone = Color(0x4D38D7D5),
            secondary = Color(0x4D38D7D5),
            disable = Color(0x4D38D7D5),
            focus = Color(0x3338D7D5),
            focusOutline = Color(0x6638D7D5),
            labelTheme = Color(0xFF38D7D5),
            primaryText = Color(0xFF38D7D5),
            primaryTextOnPopup = Color(0xFF38D7D5),
            textColorHighlight = Color(0x4C38D7D5),
        ),
    )

    /** COUI COUIOverlay.Theme.Single.Fourth. */
    val SingleFourth = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF66B92C),
            containerTheme = Color(0xFF66B92C),
            containerThemeHalftone = Color(0x2666B92C),
            secondary = Color(0x2666B92C),
            disable = Color(0x4D66B92C),
            focus = Color(0x1F66B92C),
            focusOutline = Color(0x6666B92C),
            labelTheme = Color(0xFF66B92C),
            primaryText = Color(0xFF66B92C),
            primaryTextOnPopup = Color(0xFF66B92C),
            textColorHighlight = Color(0x4C66B92C),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF66B92C),
            containerTheme = Color(0xFF66B92C),
            containerThemeHalftone = Color(0x4D66B92C),
            secondary = Color(0x4D66B92C),
            disable = Color(0x4D66B92C),
            focus = Color(0x3366B92C),
            focusOutline = Color(0x6666B92C),
            labelTheme = Color(0xFF66B92C),
            primaryText = Color(0xFF66B92C),
            primaryTextOnPopup = Color(0xFF66B92C),
            textColorHighlight = Color(0x4C66B92C),
        ),
    )

    /** COUI COUIOverlay.Theme.Single.Fifth. */
    val SingleFifth = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF4696B4),
            containerTheme = Color(0xFF4696B4),
            containerThemeHalftone = Color(0x264696B4),
            secondary = Color(0x264696B4),
            disable = Color(0x4D4696B4),
            focus = Color(0x1F4696B4),
            focusOutline = Color(0x664696B4),
            labelTheme = Color(0xFF4696B4),
            primaryText = Color(0xFF4696B4),
            primaryTextOnPopup = Color(0xFF4696B4),
            textColorHighlight = Color(0x4C4696B4),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF4696B4),
            containerTheme = Color(0xFF4696B4),
            containerThemeHalftone = Color(0x4D4696B4),
            secondary = Color(0x4D4696B4),
            disable = Color(0x4D4696B4),
            focus = Color(0x334696B4),
            focusOutline = Color(0x664696B4),
            labelTheme = Color(0xFF4696B4),
            primaryText = Color(0xFF4696B4),
            primaryTextOnPopup = Color(0xFF4696B4),
            textColorHighlight = Color(0x4C4696B4),
        ),
    )

    /** COUI COUIOverlay.Theme.Single.Sixth. */
    val SingleSixth = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFCD2638),
            containerTheme = Color(0xFFCD2638),
            containerThemeHalftone = Color(0x26CD2638),
            secondary = Color(0x26CD2638),
            disable = Color(0x4DCD2638),
            focus = Color(0x1FCD2638),
            focusOutline = Color(0x66CD2638),
            labelTheme = Color(0xFFCD2638),
            primaryText = Color(0xFFCD2638),
            primaryTextOnPopup = Color(0xFFCD2638),
            textColorHighlight = Color(0x4CCD2638),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFCD2638),
            containerTheme = Color(0xFFCD2638),
            containerThemeHalftone = Color(0x4DCD2638),
            secondary = Color(0x4DCD2638),
            disable = Color(0x4DCD2638),
            focus = Color(0x33CD2638),
            focusOutline = Color(0x66CD2638),
            labelTheme = Color(0xFFCD2638),
            primaryText = Color(0xFFCD2638),
            primaryTextOnPopup = Color(0xFFCD2638),
            textColorHighlight = Color(0x4CCD2638),
        ),
    )

    /** COUI COUIOverlay.Theme.Single.Seventh. */
    val SingleSeventh = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF007AFF),
            containerTheme = Color(0xFF007AFF),
            containerThemeHalftone = Color(0x26007AFF),
            secondary = Color(0x26007AFF),
            disable = Color(0x4D007AFF),
            focus = Color(0x1F007AFF),
            focusOutline = Color(0x66007AFF),
            labelTheme = Color(0xFF007AFF),
            primaryText = Color(0xFF007AFF),
            primaryTextOnPopup = Color(0xFF007AFF),
            textColorHighlight = Color(0x4C007AFF),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF007AFF),
            containerTheme = Color(0xFF007AFF),
            containerThemeHalftone = Color(0x4D007AFF),
            secondary = Color(0x4D007AFF),
            disable = Color(0x4D007AFF),
            focus = Color(0x33007AFF),
            focusOutline = Color(0x66007AFF),
            labelTheme = Color(0xFF007AFF),
            primaryText = Color(0xFF007AFF),
            primaryTextOnPopup = Color(0xFF007AFF),
            textColorHighlight = Color(0x4C007AFF),
        ),
    )

    /** COUI COUIOverlay.Theme.Single.Eighth. */
    val SingleEighth = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF7E70FF),
            containerTheme = Color(0xFF7E70FF),
            containerThemeHalftone = Color(0x267E70FF),
            secondary = Color(0x267E70FF),
            disable = Color(0x4D7E70FF),
            focus = Color(0x1F7E70FF),
            focusOutline = Color(0x667E70FF),
            labelTheme = Color(0xFF7E70FF),
            primaryText = Color(0xFF7E70FF),
            primaryTextOnPopup = Color(0xFF7E70FF),
            textColorHighlight = Color(0x4C7E70FF),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF7E70FF),
            containerTheme = Color(0xFF7E70FF),
            containerThemeHalftone = Color(0x4D7E70FF),
            secondary = Color(0x4D7E70FF),
            disable = Color(0x4D7E70FF),
            focus = Color(0x337E70FF),
            focusOutline = Color(0x667E70FF),
            labelTheme = Color(0xFF7E70FF),
            primaryText = Color(0xFF7E70FF),
            primaryTextOnPopup = Color(0xFF7E70FF),
            textColorHighlight = Color(0x4C7E70FF),
        ),
    )

    /** COUI COUIOverlay.Theme.Single.Ninth. */
    val SingleNinth = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFFC35570),
            containerTheme = Color(0xFFC35570),
            containerThemeHalftone = Color(0x26C35570),
            secondary = Color(0x26C35570),
            disable = Color(0x4DC35570),
            focus = Color(0x1FC35570),
            focusOutline = Color(0x66C35570),
            labelTheme = Color(0xFFC35570),
            primaryText = Color(0xFFC35570),
            primaryTextOnPopup = Color(0xFFC35570),
            textColorHighlight = Color(0x4CC35570),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFFC35570),
            containerTheme = Color(0xFFC35570),
            containerThemeHalftone = Color(0x4DC35570),
            secondary = Color(0x4DC35570),
            disable = Color(0x4DC35570),
            focus = Color(0x33C35570),
            focusOutline = Color(0x66C35570),
            labelTheme = Color(0xFFC35570),
            primaryText = Color(0xFFC35570),
            primaryTextOnPopup = Color(0xFFC35570),
            textColorHighlight = Color(0x4CC35570),
        ),
    )

    /** COUI COUIOverlay.Theme.Single.Tenth. */
    val SingleTenth = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF63E5A7),
            containerTheme = Color(0xFF63E5A7),
            containerThemeHalftone = Color(0x2663E5A7),
            secondary = Color(0x2663E5A7),
            disable = Color(0x4D63E5A7),
            focus = Color(0x1F63E5A7),
            focusOutline = Color(0x6663E5A7),
            labelTheme = Color(0xFF63E5A7),
            primaryText = Color(0xFF63E5A7),
            primaryTextOnPopup = Color(0xFF63E5A7),
            textColorHighlight = Color(0x4C63E5A7),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF63E5A7),
            containerTheme = Color(0xFF63E5A7),
            containerThemeHalftone = Color(0x4D63E5A7),
            secondary = Color(0x4D63E5A7),
            disable = Color(0x4D63E5A7),
            focus = Color(0x3363E5A7),
            focusOutline = Color(0x6663E5A7),
            labelTheme = Color(0xFF63E5A7),
            primaryText = Color(0xFF63E5A7),
            primaryTextOnPopup = Color(0xFF63E5A7),
            textColorHighlight = Color(0x4C63E5A7),
        ),
    )

    /** COUI COUIOverlay.Theme.Default.Patch.R. */
    val DefaultPatchR = COUIThemeColor(
        light = COUIThemeColorTokens(
            primary = Color(0xFF347CFF),
            containerTheme = Color(0xFF347CFF),
            containerThemeHalftone = Color(0xFFD1EAFF),
            secondary = Color(0xFFD1EAFF),
            disable = Color(0x4D347CFF),
            focus = Color(0x1F347CFF),
            focusOutline = Color(0x66347CFF),
            labelTheme = Color(0xFF347CFF),
            primaryText = Color(0xFF347CFF),
            primaryTextOnPopup = Color(0xFF347CFF),
            textColorHighlight = Color(0x4C347CFF),
        ),
        dark = COUIThemeColorTokens(
            primary = Color(0xFF347CFF),
            containerTheme = Color(0xFF347CFF),
            containerThemeHalftone = Color(0xFF0B3F9E),
            secondary = Color(0xFF0B3F9E),
            disable = Color(0x4D347CFF),
            focus = Color(0x1F347CFF),
            focusOutline = Color(0x66347CFF),
            labelTheme = Color(0xFF347CFF),
            primaryText = Color(0xFF347CFF),
            primaryTextOnPopup = Color(0xFF347CFF),
            textColorHighlight = Color(0x4C347CFF),
        ),
    )

    /** The ten single-color presets in `coui_theme_arrays_single` order. */
    val Singles: List<COUIThemeColor>
        get() = listOf(
            SingleFirst,
            SingleSecond,
            SingleThird,
            SingleFourth,
            SingleFifth,
            SingleSixth,
            SingleSeventh,
            SingleEighth,
            SingleNinth,
            SingleTenth,
        )

    /**
     * The overlay preset `COUIThemeOverlay` applies to an app of [identity] for the preset array [set]
     * (`coui_theme_arrays_default_patch` / `coui_theme_arrays_first` .. `_fifth`, indexed by
     * `couiThemeIdentifier - 1`).
     */
    fun overlay(identity: COUIThemeIdentity, set: COUIThemeOverlaySet): COUIThemeColor = when (identity) {
        COUIThemeIdentity.Green -> pick(set, GreenDefault, GreenFirst, GreenSecond, GreenThird, GreenFourth, GreenFifth)
        COUIThemeIdentity.Red -> pick(set, RedDefault, RedFirst, RedSecond, RedThird, RedFourth, RedFifth)
        COUIThemeIdentity.Yellow -> pick(set, YellowDefault, YellowFirst, YellowSecond, YellowThird, YellowFourth, YellowFifth)
        COUIThemeIdentity.Blue -> pick(set, BlueDefault, BlueFirst, BlueSecond, BlueThird, BlueFourth, BlueFifth)
        COUIThemeIdentity.Orange -> pick(set, OrangeDefault, OrangeFirst, OrangeSecond, OrangeThird, OrangeFourth, OrangeFifth)
        COUIThemeIdentity.SkyBlue -> pick(set, SkyBlueDefault, SkyBlueFirst, SkyBlueSecond, SkyBlueThird, SkyBlueFourth, SkyBlueFifth)
    }

    @Suppress("LongParameterList")
    private fun pick(
        set: COUIThemeOverlaySet,
        default: COUIThemeColor,
        first: COUIThemeColor,
        second: COUIThemeColor,
        third: COUIThemeColor,
        fourth: COUIThemeColor,
        fifth: COUIThemeColor,
    ): COUIThemeColor = when (set) {
        COUIThemeOverlaySet.Default -> default
        COUIThemeOverlaySet.First -> first
        COUIThemeOverlaySet.Second -> second
        COUIThemeOverlaySet.Third -> third
        COUIThemeOverlaySet.Fourth -> fourth
        COUIThemeOverlaySet.Fifth -> fifth
    }
}

/**
 * Returns a copy of this scheme with the theme attributes of [themeColor] applied, the way a COUI
 * theme overlay replaces exactly those attributes: [Colors.primary] and every slot that carries the
 * theme color follow it (the disabled theme color, focus, focus outline, the theme tint
 * [Colors.secondary], theme-colored text, popup accents, the text highlight). The light or
 * dark tokens are chosen by [Colors.isDark].
 */
fun Colors.withThemeColor(themeColor: COUIThemeColor): Colors {
    val t = themeColor.tokens(isDark)
    return copy(
        primary = t.primary,
        secondary = t.secondary,
        primaryTextOnPopup = t.primaryTextOnPopup,
        focus = t.focus,
        focusOutline = t.focusOutline,
        primaryText = t.primaryText,
        labelTheme = t.labelTheme,
        link = t.labelTheme,
        disable = t.disable,
        containerTheme = t.containerTheme,
        containerThemeHalftone = t.containerThemeHalftone,
        textColorHighlight = t.textColorHighlight,
    )
}

/** The COUI light scheme in the theme color family [themeColor] (for example [COUIThemeColors.Green]). */
fun lightColorScheme(themeColor: COUIThemeColor): Colors = lightColorScheme().withThemeColor(themeColor)

/** The COUI dark scheme in the theme color family [themeColor] (for example [COUIThemeColors.Green]). */
fun darkColorScheme(themeColor: COUIThemeColor): Colors = darkColorScheme().withThemeColor(themeColor)

internal fun Color.withAlpha(alpha: Int): Color = copy(alpha = alpha / 255f)
