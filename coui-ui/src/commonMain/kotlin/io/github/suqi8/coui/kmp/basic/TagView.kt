// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalContentColor

/**
 * A [TagView] component with COUI style: a small text tag such as "NEW" or "BETA", after COUI
 * `COUITagView` on its `COUITagBackgroundView`.
 *
 * The tag is a single-line label on a filled, clipped background (`COUITagBackgroundView` draws a
 * `MaterialShapeDrawable` with per-corner radii and clips its children to the same path), padded 3dp
 * on each side (`coui_tag_view_layout`). The label is 10dp, so the tag ignores the user font scale
 * like the original (`coui_default_tag_textsize` is a dp dimension), in sans-serif-medium
 * (`couiTextTag`) with the ColorOS 15+ line spacing multiplier of 1.2
 * (`values-v35` `coui_spacing_multiplier_tag`).
 *
 * @param text The text of the [TagView].
 * @param modifier The modifier to be applied to the [TagView].
 * @param shape The background and clip shape. Pass a [RoundedCornerShape] with per-corner sizes for
 *   COUI's `couiTagTLCornerRadius` / `TR` / `BL` / `BR`.
 * @param colors The [TagViewColors] of the [TagView].
 * @param strokeWidth The width of the outline (COUI `couiTagStrokeWidth`, 0 by default).
 * @param textSize The size of the label, in dp like COUI `couiTagViewTextSize`.
 * @param insideMargin The margin inside the [TagView].
 * @param leadingIcon An optional icon before the label (COUI `couiTagViewLeftDrawable`), laid out in
 *   a 5dp x 10dp box with 3dp vertical and 2dp end margins. [LocalContentColor] is the label color.
 * @param trailingIcon An optional icon after the label (COUI `couiDrawableTagViewImage`), laid out at
 *   its own size. [LocalContentColor] is the label color.
 */
@Composable
fun TagView(
    text: String,
    modifier: Modifier = Modifier,
    shape: Shape = TagViewDefaults.Shape,
    colors: TagViewColors = TagViewDefaults.tagViewColors(),
    strokeWidth: Dp = TagViewDefaults.StrokeWidth,
    textSize: Dp = TagViewDefaults.TextSize,
    insideMargin: PaddingValues = TagViewDefaults.InsideMargin,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val density = LocalDensity.current
    val style = remember(textSize, density) {
        TextStyle(
            fontSize = with(density) { textSize.toSp() },
            fontWeight = FontWeight.Medium,
            lineHeight = (TAG_LINE_SPACING_MULTIPLIER * TAG_FONT_METRICS_HEIGHT).em,
        )
    }
    Row(
        modifier = modifier
            .clip(shape)
            .background(color = colors.containerColor, shape = shape)
            .then(if (strokeWidth > 0.dp) Modifier.border(strokeWidth, colors.strokeColor, shape) else Modifier)
            .padding(insideMargin),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalContentColor provides colors.contentColor) {
            if (leadingIcon != null) {
                Box(
                    modifier = Modifier
                        .padding(top = 3.dp, bottom = 3.dp, end = 2.dp)
                        .size(width = TagViewDefaults.LeadingIconWidth, height = TagViewDefaults.LeadingIconHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    leadingIcon()
                }
            }
            Text(
                text = text,
                color = colors.contentColor,
                style = style,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (trailingIcon != null) {
                Box(contentAlignment = Alignment.Center) {
                    trailingIcon()
                }
            }
        }
    }
}

/**
 * Colors of a [TagView].
 *
 * @param containerColor The background fill (COUI `couiTagBackgroundColor`).
 * @param contentColor The label color (COUI `couiTagViewTextColor`).
 * @param strokeColor The outline color (COUI `couiTagStrokeColor`), drawn when the stroke width is
 *   positive.
 */
@Immutable
data class TagViewColors(
    val containerColor: Color,
    val contentColor: Color,
    val strokeColor: Color,
)

/** Contains default values used by [TagView]. */
object TagViewDefaults {

    /**
     * The default corner radius. COUI `couiTagCornerRadius` defaults to 0 and the shipped theme
     * sets no tag style, so every host picks its own radius; 4dp is the library default for a
     * 14dp-high tag.
     */
    val CornerRadius = 4.dp

    /** The default [TagView] shape: a [RoundedCornerShape] of [CornerRadius]. */
    val Shape: Shape = RoundedCornerShape(CornerRadius)

    /** The default outline width (COUI `couiTagStrokeWidth`, 0). */
    val StrokeWidth = 0.dp

    /** The default label size (COUI `coui_default_tag_textsize`, a dp dimension). */
    val TextSize = 10.dp

    /** The default inside margin (COUI `coui_tag_view_layout` paddingStart / paddingEnd 3dp). */
    val InsideMargin = PaddingValues(horizontal = 3.dp)

    /** The width of the leading icon box (COUI `tagLeftImageView`, 5dp). */
    val LeadingIconWidth = 5.dp

    /** The height of the leading icon box (COUI `tagLeftImageView`, 10dp). */
    val LeadingIconHeight = 10.dp

    /**
     * The default [TagViewColors]. COUI reads the fill from `couiColorBackgroundWithTag`, which the
     * shipped theme leaves unset, so the theme primary color is used; the label is
     * `coui_color_white` (the `couiTagViewTextColor` fallback) and the outline is transparent.
     */
    @Composable
    fun tagViewColors(
        containerColor: Color = COUITheme.colorScheme.primary,
        contentColor: Color = Color.White,
        strokeColor: Color = Color.Transparent,
    ): TagViewColors = remember(containerColor, contentColor, strokeColor) {
        TagViewColors(
            containerColor = containerColor,
            contentColor = contentColor,
            strokeColor = strokeColor,
        )
    }
}

/** `values-v35` `coui_spacing_multiplier_tag`. */
private const val TAG_LINE_SPACING_MULTIPLIER = 1.2f

/** The Android font metrics height factor the COUI type steps are measured with. */
private const val TAG_FONT_METRICS_HEIGHT = 1.1719f
