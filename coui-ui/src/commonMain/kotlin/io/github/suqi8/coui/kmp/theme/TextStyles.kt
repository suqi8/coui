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
 * @param displayL COUI couiTextDisplayL: 72sp sans-serif-medium.
 * @param display COUI couiTextAppearanceDisplay: 48sp sans-serif-medium, no line spacing multiplier.
 * @param displayM COUI couiTextDisplayM: 36sp, textFontWeight 600.
 * @param displayS COUI couiTextDisplayS: 32sp, textFontWeight 600.
 * @param displayXS COUI couiTextDisplayXS: 24sp, textFontWeight 600.
 * @param headlineL COUI couiTextHeadlineL: 28sp sans-serif-medium.
 * @param headlineM COUI couiTextHeadlineM: 20sp sans-serif-medium.
 * @param headlineS COUI couiTextHeadlineS: 18sp, textFontWeight 550.
 * @param headlineXS COUI couiTextHeadlineXS: 16sp sans-serif-medium.
 * @param bodyXL COUI couiTextBodyXL: 16sp regular.
 * @param bodyL COUI couiTextBodyL: 15sp regular.
 * @param bodyM COUI couiTextBodyM: 14sp regular.
 * @param bodyS COUI couiTextBodyS: 13sp regular.
 * @param bodyXS COUI couiTextBodyXS: 12sp regular.
 * @param buttonL COUI couiTextButtonL: 16sp sans-serif-medium.
 * @param buttonM COUI couiTextButtonM: 14sp sans-serif-medium.
 * @param buttonS COUI couiTextButtonS: 12sp sans-serif-medium.
 * @param description COUI couiTextDescription: 12sp sans-serif-medium.
 * @param caption COUI couiTextCaption: 10sp regular.
 * @param tag COUI couiTextTag: 10sp sans-serif-medium.
 */
@Stable
class TextStyles(
    displayL: TextStyle = DisplayL,
    display: TextStyle = Display,
    displayM: TextStyle = DisplayM,
    displayS: TextStyle = DisplayS,
    displayXS: TextStyle = DisplayXS,
    headlineL: TextStyle = HeadlineL,
    headlineM: TextStyle = HeadlineM,
    headlineS: TextStyle = HeadlineS,
    headlineXS: TextStyle = HeadlineXS,
    bodyXL: TextStyle = BodyXL,
    bodyL: TextStyle = BodyL,
    bodyM: TextStyle = BodyM,
    bodyS: TextStyle = BodyS,
    bodyXS: TextStyle = BodyXS,
    buttonL: TextStyle = ButtonL,
    buttonM: TextStyle = ButtonM,
    buttonS: TextStyle = ButtonS,
    description: TextStyle = Description,
    caption: TextStyle = Caption,
    tag: TextStyle = Tag,
) {
    var displayL by mutableStateOf(displayL, structuralEqualityPolicy())
        internal set
    var display by mutableStateOf(display, structuralEqualityPolicy())
        internal set
    var displayM by mutableStateOf(displayM, structuralEqualityPolicy())
        internal set
    var displayS by mutableStateOf(displayS, structuralEqualityPolicy())
        internal set
    var displayXS by mutableStateOf(displayXS, structuralEqualityPolicy())
        internal set
    var headlineL by mutableStateOf(headlineL, structuralEqualityPolicy())
        internal set
    var headlineM by mutableStateOf(headlineM, structuralEqualityPolicy())
        internal set
    var headlineS by mutableStateOf(headlineS, structuralEqualityPolicy())
        internal set
    var headlineXS by mutableStateOf(headlineXS, structuralEqualityPolicy())
        internal set
    var bodyXL by mutableStateOf(bodyXL, structuralEqualityPolicy())
        internal set
    var bodyL by mutableStateOf(bodyL, structuralEqualityPolicy())
        internal set
    var bodyM by mutableStateOf(bodyM, structuralEqualityPolicy())
        internal set
    var bodyS by mutableStateOf(bodyS, structuralEqualityPolicy())
        internal set
    var bodyXS by mutableStateOf(bodyXS, structuralEqualityPolicy())
        internal set
    var buttonL by mutableStateOf(buttonL, structuralEqualityPolicy())
        internal set
    var buttonM by mutableStateOf(buttonM, structuralEqualityPolicy())
        internal set
    var buttonS by mutableStateOf(buttonS, structuralEqualityPolicy())
        internal set
    var description by mutableStateOf(description, structuralEqualityPolicy())
        internal set
    var caption by mutableStateOf(caption, structuralEqualityPolicy())
        internal set
    var tag by mutableStateOf(tag, structuralEqualityPolicy())
        internal set

    fun copy(
        displayL: TextStyle = this.displayL,
        display: TextStyle = this.display,
        displayM: TextStyle = this.displayM,
        displayS: TextStyle = this.displayS,
        displayXS: TextStyle = this.displayXS,
        headlineL: TextStyle = this.headlineL,
        headlineM: TextStyle = this.headlineM,
        headlineS: TextStyle = this.headlineS,
        headlineXS: TextStyle = this.headlineXS,
        bodyXL: TextStyle = this.bodyXL,
        bodyL: TextStyle = this.bodyL,
        bodyM: TextStyle = this.bodyM,
        bodyS: TextStyle = this.bodyS,
        bodyXS: TextStyle = this.bodyXS,
        buttonL: TextStyle = this.buttonL,
        buttonM: TextStyle = this.buttonM,
        buttonS: TextStyle = this.buttonS,
        description: TextStyle = this.description,
        caption: TextStyle = this.caption,
        tag: TextStyle = this.tag,
    ): TextStyles = TextStyles(
        displayL = displayL,
        display = display,
        displayM = displayM,
        displayS = displayS,
        displayXS = displayXS,
        headlineL = headlineL,
        headlineM = headlineM,
        headlineS = headlineS,
        headlineXS = headlineXS,
        bodyXL = bodyXL,
        bodyL = bodyL,
        bodyM = bodyM,
        bodyS = bodyS,
        bodyXS = bodyXS,
        buttonL = buttonL,
        buttonM = buttonM,
        buttonS = buttonS,
        description = description,
        caption = caption,
        tag = tag,
    )
}

fun defaultTextStyles(
    displayL: TextStyle = DisplayL,
    display: TextStyle = Display,
    displayM: TextStyle = DisplayM,
    displayS: TextStyle = DisplayS,
    displayXS: TextStyle = DisplayXS,
    headlineL: TextStyle = HeadlineL,
    headlineM: TextStyle = HeadlineM,
    headlineS: TextStyle = HeadlineS,
    headlineXS: TextStyle = HeadlineXS,
    bodyXL: TextStyle = BodyXL,
    bodyL: TextStyle = BodyL,
    bodyM: TextStyle = BodyM,
    bodyS: TextStyle = BodyS,
    bodyXS: TextStyle = BodyXS,
    buttonL: TextStyle = ButtonL,
    buttonM: TextStyle = ButtonM,
    buttonS: TextStyle = ButtonS,
    description: TextStyle = Description,
    caption: TextStyle = Caption,
    tag: TextStyle = Tag,
): TextStyles = TextStyles(
    displayL = displayL,
    display = display,
    displayM = displayM,
    displayS = displayS,
    displayXS = displayXS,
    headlineL = headlineL,
    headlineM = headlineM,
    headlineS = headlineS,
    headlineXS = headlineXS,
    bodyXL = bodyXL,
    bodyL = bodyL,
    bodyM = bodyM,
    bodyS = bodyS,
    bodyXS = bodyXS,
    buttonL = buttonL,
    buttonM = buttonM,
    buttonS = buttonS,
    description = description,
    caption = caption,
    tag = tag,
)

/** COUI couiTextDisplayL 72sp sans-serif-medium (coui_spacing_multiplier_display_l 1.1384). */
private val DisplayL: TextStyle
    get() = couiTextStyle(fontSize = 72f, lineSpacingMultiplier = 1.1384f, fontWeight = FontWeight.Medium)

/** COUI couiTextAppearanceDisplay 48sp sans-serif-medium (no lineSpacingMultiplier, so 1.0). */
private val Display: TextStyle
    get() = couiTextStyle(fontSize = 48f, lineSpacingMultiplier = 1f, fontWeight = FontWeight.Medium)

/** COUI couiTextDisplayM 36sp, textFontWeight 600 (coui_spacing_multiplier_display_m 1.191). */
private val DisplayM: TextStyle
    get() = couiTextStyle(fontSize = 36f, lineSpacingMultiplier = 1.191f, fontWeight = FontWeight.SemiBold)

/** COUI couiTextDisplayS 32sp, textFontWeight 600 (coui_spacing_multiplier_display_s 1.2322). */
private val DisplayS: TextStyle
    get() = couiTextStyle(fontSize = 32f, lineSpacingMultiplier = 1.2322f, fontWeight = FontWeight.SemiBold)

/** COUI couiTextDisplayXS 24sp, textFontWeight 600 (coui_spacing_multiplier_display_xs 1.2). */
private val DisplayXS: TextStyle
    get() = couiTextStyle(fontSize = 24f, lineSpacingMultiplier = 1.2f, fontWeight = FontWeight.SemiBold)

/** COUI couiTextHeadlineL 28sp sans-serif-medium (coui_spacing_multiplier_headline_l 1.2245). */
private val HeadlineL: TextStyle
    get() = couiTextStyle(fontSize = 28f, lineSpacingMultiplier = 1.2245f, fontWeight = FontWeight.Medium)

/** COUI couiTextHeadlineM 20sp sans-serif-medium (coui_spacing_multiplier_headline_m 1.1831). */
private val HeadlineM: TextStyle
    get() = couiTextStyle(fontSize = 20f, lineSpacingMultiplier = 1.1831f, fontWeight = FontWeight.Medium)

/**
 * COUI couiTextHeadlineS 18sp, sans-serif-medium with textFontWeight 550, which takes precedence over the
 * family weight (coui_spacing_multiplier_headline_s 1.1429).
 */
private val HeadlineS: TextStyle
    get() = couiTextStyle(fontSize = 18f, lineSpacingMultiplier = 1.1429f, fontWeight = FontWeight(550))

/**
 * COUI couiTextHeadlineXS 16sp sans-serif-medium (coui_spacing_multiplier_headline_xs 1.158), the step behind
 * TextAppearance.COUI.List.Title. Confirmed on device: a ColorOS 16 preference title node measures 21.71dp at
 * 560 dpi = 16sp x 1.158 x [COUIFontMetricsHeight].
 */
private val HeadlineXS: TextStyle
    get() = couiTextStyle(fontSize = 16f, lineSpacingMultiplier = 1.158f, fontWeight = FontWeight.Medium)

/** COUI couiTextBodyXL 16sp regular (coui_spacing_multiplier_body_xl 1.158). */
private val BodyXL: TextStyle
    get() = couiTextStyle(fontSize = 16f, lineSpacingMultiplier = 1.158f)

/** COUI couiTextBodyL 15sp regular (coui_spacing_multiplier_body_l 1.1321). */
private val BodyL: TextStyle
    get() = couiTextStyle(fontSize = 15f, lineSpacingMultiplier = 1.1321f)

/** COUI couiTextBodyM 14sp regular (coui_spacing_multiplier_body_m 1.2245). */
private val BodyM: TextStyle
    get() = couiTextStyle(fontSize = 14f, lineSpacingMultiplier = 1.2245f)

/**
 * COUI couiTextBodyS 13sp regular (coui_spacing_multiplier_body_s 1.1739), the step behind
 * TextAppearance.COUI.Preference.Summary and the list assignment text.
 */
private val BodyS: TextStyle
    get() = couiTextStyle(fontSize = 13f, lineSpacingMultiplier = 1.1739f)

/** COUI couiTextBodyXS 12sp regular (coui_spacing_multiplier_body_xs 1.143). */
private val BodyXS: TextStyle
    get() = couiTextStyle(fontSize = 12f, lineSpacingMultiplier = 1.143f)

/** COUI couiTextButtonL 16sp sans-serif-medium (coui_spacing_multiplier_button_l 1.1579). */
private val ButtonL: TextStyle
    get() = couiTextStyle(fontSize = 16f, lineSpacingMultiplier = 1.1579f, fontWeight = FontWeight.Medium)

/** COUI couiTextButtonM 14sp sans-serif-medium (coui_spacing_multiplier_button_m 1.2245). */
private val ButtonM: TextStyle
    get() = couiTextStyle(fontSize = 14f, lineSpacingMultiplier = 1.2245f, fontWeight = FontWeight.Medium)

/** COUI couiTextButtonS 12sp sans-serif-medium (coui_spacing_multiplier_button_s 1.143). */
private val ButtonS: TextStyle
    get() = couiTextStyle(fontSize = 12f, lineSpacingMultiplier = 1.143f, fontWeight = FontWeight.Medium)

/** COUI couiTextDescription 12sp sans-serif-medium (coui_spacing_multiplier_button_s 1.143). */
private val Description: TextStyle
    get() = couiTextStyle(fontSize = 12f, lineSpacingMultiplier = 1.143f, fontWeight = FontWeight.Medium)

/** COUI couiTextCaption 10sp regular (coui_spacing_multiplier_cap 1.2). */
private val Caption: TextStyle
    get() = couiTextStyle(fontSize = 10f, lineSpacingMultiplier = 1.2f)

/** COUI couiTextTag 10sp sans-serif-medium (coui_spacing_multiplier_tag 1.2). */
private val Tag: TextStyle
    get() = couiTextStyle(fontSize = 10f, lineSpacingMultiplier = 1.2f, fontWeight = FontWeight.Medium)

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
    displayL = other.displayL
    display = other.display
    displayM = other.displayM
    displayS = other.displayS
    displayXS = other.displayXS
    headlineL = other.headlineL
    headlineM = other.headlineM
    headlineS = other.headlineS
    headlineXS = other.headlineXS
    bodyXL = other.bodyXL
    bodyL = other.bodyL
    bodyM = other.bodyM
    bodyS = other.bodyS
    bodyXS = other.bodyXS
    buttonL = other.buttonL
    buttonM = other.buttonM
    buttonS = other.buttonS
    description = other.description
    caption = other.caption
    tag = other.tag
}

internal val LocalTextStyles = staticCompositionLocalOf { defaultTextStyles() }
