// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.AnnotatedString.Range
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * Text holding inline COUI links (COUI `COUIClickableSpan`).
 *
 * Links are ordinary [LinkAnnotation]s, typically appended with [appendLink] inside
 * `buildAnnotatedString`. Every link that carries no [TextLinkStyles] of its own is drawn in the
 * COUI link style: `coui_clickable_text_color` is the link color at rest, a 30% alpha tint while the
 * link is pressed and no underline (`COUIClickableSpan.updateDrawState` overrides
 * `ClickableSpan.updateDrawState` without calling super, so the default underline is never drawn).
 * The text keeps no highlight behind the pressed link (`COUIPreferenceUtils.setSummaryView` sets a
 * transparent highlight color).
 *
 * A tap on a link is consumed by the link, so a clickable parent (such as a preference row) does not
 * receive it, while a tap on the rest of the text still reaches the parent, as COUI's summary touch
 * listener returns `false` outside the link span.
 *
 * @param text The text to show, holding [LinkAnnotation]s for the links.
 * @param modifier The modifier to be applied to the [LinkText].
 * @param enabled Whether the links are interactive. A disabled [LinkText] draws its links in
 *   [LinkTextColors.disabledColor] and drops their click handling and link semantics.
 * @param color The color of the non-link text. [Color.Unspecified] falls back to [style]'s color.
 * @param linkColors The [LinkTextColors] applied to links without their own styles.
 * @param style The [TextStyle] of the text.
 * @param textAlign The alignment of the text within its lines.
 * @param overflow How visual overflow is handled.
 * @param maxLines The maximum number of lines of the text.
 */
@Composable
fun LinkText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    linkColors: LinkTextColors = LinkTextDefaults.linkTextColors(),
    style: TextStyle = COUITheme.textStyles.bodyXL,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
) {
    val linkStyles = LinkTextDefaults.linkStyles(linkColors)
    val styledText = remember(text, linkStyles, linkColors.disabledColor, enabled) {
        if (enabled) {
            text.withDefaultLinkStyles(linkStyles)
        } else {
            text.withDisabledLinks(SpanStyle(color = linkColors.disabledColor))
        }
    }
    Text(
        text = styledText,
        modifier = modifier,
        color = color,
        style = style,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines,
    )
}

/**
 * Appends [text] as a clickable link, the Compose counterpart of a `SpannableString` carrying a
 * `COUIClickableSpan` whose `SpannableStrClickListener` is [onClick].
 *
 * The link carries no [TextLinkStyles], so [LinkText] (and the COUI preferences built on it) draws it
 * in the COUI link style. Pass the result to [LinkText]: the plain [Text] gives unstyled links its
 * own underlined style instead.
 *
 * @param text The link text.
 * @param onClick Called when the link is clicked.
 * @param tag The tag of the [LinkAnnotation.Clickable], by default the link text.
 */
fun AnnotatedString.Builder.appendLink(
    text: String,
    onClick: () -> Unit,
    tag: String = text,
) {
    withLink(LinkAnnotation.Clickable(tag = tag) { onClick() }) {
        append(text)
    }
}

object LinkTextDefaults {

    /**
     * The default [LinkTextColors] (COUI `coui_clickable_text_color`).
     *
     * @param color The link color at rest (`?couiColorLink` = `?couiColorLabelTheme`, the theme accent).
     * @param pressedColor The link color while pressed. COUI uses `coui_clickable_text_color_pressed`
     *   #4D2660F5, a 30% alpha tint of the accent; here it follows [color] at 30% alpha so that it
     *   tracks custom and dynamic accents.
     * @param disabledColor The link color when disabled (`coui_clickable_text_color_disabled`
     *   #26000000 / #26FFFFFF, the [io.github.suqi8.coui.kmp.theme.Colors.outline] slot).
     */
    @Composable
    fun linkTextColors(
        color: Color = COUITheme.colorScheme.primary,
        pressedColor: Color = color.copy(alpha = color.alpha * PRESSED_ALPHA),
        disabledColor: Color = COUITheme.colorScheme.outline,
    ): LinkTextColors = remember(color, pressedColor, disabledColor) {
        LinkTextColors(
            color = color,
            pressedColor = pressedColor,
            disabledColor = disabledColor,
        )
    }

    /**
     * The [TextLinkStyles] of a COUI link drawn with [colors], for links built with explicit styles
     * (e.g. `LinkAnnotation.Clickable(tag, styles = LinkTextDefaults.linkStyles()) { … }`) or shown in
     * a plain [Text].
     *
     * @param colors The [LinkTextColors] of the link.
     */
    @Composable
    fun linkStyles(
        colors: LinkTextColors = linkTextColors(),
    ): TextLinkStyles = remember(colors.color, colors.pressedColor) {
        TextLinkStyles(
            style = SpanStyle(color = colors.color),
            pressedStyle = SpanStyle(color = colors.pressedColor),
        )
    }

    /** The alpha of the pressed link color relative to the resting color (#4D = 30%). */
    private const val PRESSED_ALPHA = 0.3f
}

/**
 * The colors of a [LinkText] link (COUI `coui_clickable_text_color` selector).
 *
 * @param color The link color at rest.
 * @param pressedColor The link color while pressed (also the selected state in COUI).
 * @param disabledColor The link color when disabled.
 */
@Immutable
data class LinkTextColors(
    val color: Color,
    val pressedColor: Color,
    val disabledColor: Color,
)

@Suppress("UNCHECKED_CAST")
private fun AnnotatedString.withDefaultLinkStyles(
    linkStyles: TextLinkStyles,
): AnnotatedString = mapAnnotations { range ->
    val link = range.item
    when {
        link is LinkAnnotation.Url && link.styles == null ->
            (range as Range<LinkAnnotation.Url>).copy(link.copy(styles = linkStyles))

        link is LinkAnnotation.Clickable && link.styles == null ->
            (range as Range<LinkAnnotation.Clickable>).copy(link.copy(styles = linkStyles))

        else -> range
    }
}

// A disabled COUI preference also disables its summary TextView: the link keeps its text but takes
// the disabled color and no longer reacts to touch.
private fun AnnotatedString.withDisabledLinks(disabledStyle: SpanStyle): AnnotatedString {
    val links = getLinkAnnotations(0, length)
    if (links.isEmpty()) return this
    return AnnotatedString(
        text = text,
        spanStyles = spanStyles + links.map { Range(disabledStyle, it.start, it.end) },
        paragraphStyles = paragraphStyles,
    )
}
