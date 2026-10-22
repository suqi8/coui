// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * The default text styles for the COUI components.
 *
 * Every default step is a COUI type step: its `textSize`, its weight, and a line height of
 * `fontSize x lineSpacingMultiplier x 1.1719` (Android multiplies the font metrics height, not the font
 * size), so line boxes match ColorOS in dp on every platform.
 *
 * @param main The main text style.
 * @param paragraph The paragraph text style.
 * @param body1 The body1 text style.
 * @param body2 The body2 text style.
 * @param button The button text style.
 * @param footnote1 The footnote1 text style.
 * @param footnote2 The footnote2 text style.
 * @param headline1 The headline1 text style.
 * @param headline2 The headline2 text style.
 * @param subtitle The subtitle text style.
 * @param title1 The title1 text style.
 * @param title2 The title2 text style.
 * @param title3 The title3 text style.
 * @param title4 The title4 text style.
 */
@Stable
class TextStyles(
    main: TextStyle,
    paragraph: TextStyle,
    body1: TextStyle,
    body2: TextStyle,
    button: TextStyle,
    footnote1: TextStyle,
    footnote2: TextStyle,
    headline1: TextStyle,
    headline2: TextStyle,
    subtitle: TextStyle,
    title1: TextStyle,
    title2: TextStyle,
    title3: TextStyle,
    title4: TextStyle,
) {
    var main by mutableStateOf(main, structuralEqualityPolicy())
        internal set
    var paragraph by mutableStateOf(paragraph, structuralEqualityPolicy())
        internal set
    var body1 by mutableStateOf(body1, structuralEqualityPolicy())
        internal set
    var body2 by mutableStateOf(body2, structuralEqualityPolicy())
        internal set
    var button by mutableStateOf(button, structuralEqualityPolicy())
        internal set
    var footnote1 by mutableStateOf(footnote1, structuralEqualityPolicy())
        internal set
    var footnote2 by mutableStateOf(footnote2, structuralEqualityPolicy())
        internal set
    var headline1 by mutableStateOf(headline1, structuralEqualityPolicy())
        internal set
    var headline2 by mutableStateOf(headline2, structuralEqualityPolicy())
        internal set
    var subtitle by mutableStateOf(subtitle, structuralEqualityPolicy())
        internal set
    var title1 by mutableStateOf(title1, structuralEqualityPolicy())
        internal set
    var title2 by mutableStateOf(title2, structuralEqualityPolicy())
        internal set
    var title3 by mutableStateOf(title3, structuralEqualityPolicy())
        internal set
    var title4 by mutableStateOf(title4, structuralEqualityPolicy())
        internal set

    fun copy(
        main: TextStyle = this.main,
        paragraph: TextStyle = this.paragraph,
        body1: TextStyle = this.body1,
        body2: TextStyle = this.body2,
        button: TextStyle = this.button,
        footnote1: TextStyle = this.footnote1,
        footnote2: TextStyle = this.footnote2,
        headline1: TextStyle = this.headline1,
        headline2: TextStyle = this.headline2,
        subtitle: TextStyle = this.subtitle,
        title1: TextStyle = this.title1,
        title2: TextStyle = this.title2,
        title3: TextStyle = this.title3,
        title4: TextStyle = this.title4,
    ): TextStyles = TextStyles(
        main,
        paragraph,
        body1,
        body2,
        button,
        footnote1,
        footnote2,
        headline1,
        headline2,
        subtitle,
        title1,
        title2,
        title3,
        title4,
    )
}

fun defaultTextStyles(
    main: TextStyle = Main,
    paragraph: TextStyle = Paragraph,
    body1: TextStyle = Body1,
    body2: TextStyle = Body2,
    button: TextStyle = Button,
    footnote1: TextStyle = Footnote1,
    footnote2: TextStyle = Footnote2,
    headline1: TextStyle = Headline1,
    headline2: TextStyle = Headline2,
    subtitle: TextStyle = Subtitle,
    title1: TextStyle = Title1,
    title2: TextStyle = Title2,
    title3: TextStyle = Title3,
    title4: TextStyle = Title4,
): TextStyles = TextStyles(
    main,
    paragraph,
    body1,
    body2,
    button,
    footnote1,
    footnote2,
    headline1,
    headline2,
    subtitle,
    title1,
    title2,
    title3,
    title4,
)

/** COUI couiTextAppearanceBodyXL / TextBodyXL 16sp regular (coui_spacing_multiplier_body_xl 1.158). */
private val Main: TextStyle
    get() = couiTextStyle(fontSize = 16f, lineSpacingMultiplier = 1.158f)

/** COUI couiTextAppearanceBodyL 16sp regular (coui_spacing_multiplier_body_l 1.1321). */
private val Paragraph: TextStyle
    get() = couiTextStyle(fontSize = 16f, lineSpacingMultiplier = 1.1321f)

/** COUI couiTextAppearanceBodyXL / TextBodyXL 16sp regular (coui_spacing_multiplier_body_xl 1.158). */
private val Body1: TextStyle
    get() = couiTextStyle(fontSize = 16f, lineSpacingMultiplier = 1.158f)

/** COUI couiTextAppearanceBody / TextBodyM 14sp regular (coui_spacing_multiplier_body_m 1.2245). */
private val Body2: TextStyle
    get() = couiTextStyle(fontSize = 14f, lineSpacingMultiplier = 1.2245f)

/** COUI couiTextAppearanceButtonL 16sp sans-serif-medium (coui_spacing_multiplier_button_l 1.1579). */
private val Button: TextStyle
    get() = couiTextStyle(fontSize = 16f, lineSpacingMultiplier = 1.1579f, fontWeight = FontWeight.Medium)

/**
 * COUI couiTextAppearanceArticleBody / TextBodyS 13sp regular (coui_spacing_multiplier_body_s 1.1739), the
 * appearance behind TextAppearance.COUI.Preference.Summary and the list assignment text.
 */
private val Footnote1: TextStyle
    get() = couiTextStyle(fontSize = 13f, lineSpacingMultiplier = 1.1739f)

/** COUI couiTextAppearanceDescription / TextBodyXS 12sp regular (coui_spacing_multiplier_body_xs 1.143). */
private val Footnote2: TextStyle
    get() = couiTextStyle(fontSize = 12f, lineSpacingMultiplier = 1.143f)

/**
 * COUI couiTextAppearanceHeadline6 16sp sans-serif-medium (coui_spacing_multiplier_headline_xs 1.158), the
 * appearance behind TextAppearance.COUI.List.Title. Confirmed on device: a ColorOS 16 preference title node
 * measures 21.71dp at 560 dpi = 16sp x 1.158 x [COUIFontMetricsHeight].
 */
private val Headline1: TextStyle
    get() = couiTextStyle(fontSize = 16f, lineSpacingMultiplier = 1.158f, fontWeight = FontWeight.Medium)

/** COUI couiTextAppearanceHeadline6 16sp sans-serif-medium (coui_spacing_multiplier_headline_xs 1.158). */
private val Headline2: TextStyle
    get() = couiTextStyle(fontSize = 16f, lineSpacingMultiplier = 1.158f, fontWeight = FontWeight.Medium)

/** COUI couiTextAppearanceButton / TextButtonM 14sp sans-serif-medium (coui_spacing_multiplier_button_m 1.2245). */
private val Subtitle: TextStyle
    get() = couiTextStyle(fontSize = 14f, lineSpacingMultiplier = 1.2245f, fontWeight = FontWeight.Medium)

/** COUI couiTextAppearanceHeadline1 32sp, textFontWeight 600 (coui_spacing_multiplier_display_s 1.2322). */
private val Title1: TextStyle
    get() = couiTextStyle(fontSize = 32f, lineSpacingMultiplier = 1.2322f, fontWeight = FontWeight.SemiBold)

/** COUI couiTextAppearanceHeadline3 24sp, textFontWeight 600 (coui_spacing_multiplier_display_xs 1.2). */
private val Title2: TextStyle
    get() = couiTextStyle(fontSize = 24f, lineSpacingMultiplier = 1.2f, fontWeight = FontWeight.SemiBold)

/** COUI couiTextAppearanceHeadline4 20sp sans-serif-medium (coui_spacing_multiplier_headline_m 1.1831). */
private val Title3: TextStyle
    get() = couiTextStyle(fontSize = 20f, lineSpacingMultiplier = 1.1831f, fontWeight = FontWeight.Medium)

/** COUI couiTextAppearanceHeadline5 18sp sans-serif-medium (coui_spacing_multiplier_headline_s 1.1429). */
private val Title4: TextStyle
    get() = couiTextStyle(fontSize = 18f, lineSpacingMultiplier = 1.1429f, fontWeight = FontWeight.Medium)

/**
 * The height of the font's line box (ascent + descent) in em: Roboto's hhea 1900 + 500 over 2048 units.
 *
 * Android's `lineSpacingMultiplier` scales this metrics height, not the font size, so a COUI line is
 * `fontSize x multiplier x 1.1719` tall. Compose's `lineHeight` is an absolute line box, so the factor
 * is folded in here and every platform gets the same line heights as ColorOS, whatever font it renders.
 */
private val COUIFontMetricsHeight = 1.1719f

/**
 * Builds a COUI type step from its `textSize` and `android:lineSpacingMultiplier`
 * (the `values-v35` coui_spacing_multiplier_* dimens, which ColorOS 16 / 17 use).
 */
private fun couiTextStyle(
    fontSize: Float,
    lineSpacingMultiplier: Float,
    fontWeight: FontWeight? = null,
): TextStyle = TextStyle(
    fontSize = fontSize.sp,
    fontWeight = fontWeight,
    lineHeight = (lineSpacingMultiplier * COUIFontMetricsHeight).em,
    lineHeightStyle = COUILineHeightStyle,
)

/**
 * Keeps the extra leading on single-line text, as an Android TextView does, instead of Compose's default
 * trim, and splits it above and below the glyphs in proportion to the font's ascent and descent. That
 * keeps the glyphs within a fraction of a dp of where ColorOS draws them (`includeFontPadding` puts
 * most of the spare room above the ascent).
 */
private val COUILineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Proportional,
    trim = LineHeightStyle.Trim.None,
)

@Stable
internal fun TextStyles.updateTextStylesFrom(other: TextStyles) {
    main = other.main
    paragraph = other.paragraph
    body1 = other.body1
    body2 = other.body2
    button = other.button
    footnote1 = other.footnote1
    footnote2 = other.footnote2
    headline1 = other.headline1
    headline2 = other.headline2
    subtitle = other.subtitle
    title1 = other.title1
    title2 = other.title2
    title3 = other.title3
    title4 = other.title4
}

internal val LocalTextStyles = staticCompositionLocalOf { defaultTextStyles() }
