// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.LinkTextColors
import io.github.suqi8.coui.kmp.basic.LinkTextDefaults
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * The description at the bottom of a settings page, optionally holding inline links (COUI
 * `COUIPagerFooterPreference`, layout `coui_pager_footer_preference`).
 *
 * It sits on the page background below the last card: an optional title above the description in
 * 12sp `coui_preference_header_footer_textsize`. Links appended with
 * [io.github.suqi8.coui.kmp.basic.appendLink] are clickable in the COUI link style, which is what
 * `couiEnalbeClickSpan` (`mIsEnableClickSpan`) turns on in COUI; a [summary] without links is plain
 * text.
 *
 * @param summary The description text, holding the links.
 * @param modifier The modifier to be applied to the [PagerFooterPreference].
 * @param title The optional title above the description.
 * @param withExtraMarginBottom Whether the footer keeps [PagerFooterPreferenceDefaults.MarginBottom]
 *   below it (COUI `withExtraMarginBottom`, `true` by default).
 * @param titleColor The color of the title.
 * @param summaryColor The color of the non-link description text.
 * @param linkColors The [LinkTextColors] of the description links.
 * @param insideMargin The padding around the text.
 */
@Composable
fun PagerFooterPreference(
    summary: AnnotatedString,
    modifier: Modifier = Modifier,
    title: String? = null,
    withExtraMarginBottom: Boolean = true,
    titleColor: Color = PagerFooterPreferenceDefaults.TitleColor,
    summaryColor: Color = PagerFooterPreferenceDefaults.SummaryColor,
    linkColors: LinkTextColors = LinkTextDefaults.linkTextColors(),
    insideMargin: PaddingValues = PagerFooterPreferenceDefaults.InsideMargin,
) {
    PagerDescription(
        summary = summary,
        title = title,
        titleColor = titleColor,
        summaryColor = summaryColor,
        linkColors = linkColors,
        insideMargin = insideMargin,
        // COUIPagerFooterPreference.onBindViewHolder: bottomMargin = support_preference_footer_preference_margin_bottom
        // when mWithExtraMarginBottom, 0 otherwise.
        modifier = modifier.padding(
            bottom = if (withExtraMarginBottom) PagerFooterPreferenceDefaults.MarginBottom else 0.dp,
        ),
    )
}

/**
 * The description at the bottom of a settings page, as plain text (COUI `COUIPagerFooterPreference`).
 *
 * @param summary The description text.
 * @param modifier The modifier to be applied to the [PagerFooterPreference].
 * @param title The optional title above the description.
 * @param withExtraMarginBottom Whether the footer keeps [PagerFooterPreferenceDefaults.MarginBottom]
 *   below it.
 * @param titleColor The color of the title.
 * @param summaryColor The color of the description.
 * @param insideMargin The padding around the text.
 */
@Composable
@NonRestartableComposable
fun PagerFooterPreference(
    summary: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    withExtraMarginBottom: Boolean = true,
    titleColor: Color = PagerFooterPreferenceDefaults.TitleColor,
    summaryColor: Color = PagerFooterPreferenceDefaults.SummaryColor,
    insideMargin: PaddingValues = PagerFooterPreferenceDefaults.InsideMargin,
) {
    PagerDescription(
        summary = AnnotatedString(summary),
        title = title,
        titleColor = titleColor,
        summaryColor = summaryColor,
        linkColors = null,
        insideMargin = insideMargin,
        modifier = modifier.padding(
            bottom = if (withExtraMarginBottom) PagerFooterPreferenceDefaults.MarginBottom else 0.dp,
        ),
    )
}

object PagerFooterPreferenceDefaults {

    /** The space below the footer when `withExtraMarginBottom` is on (COUI support_preference_footer_preference_margin_bottom). */
    val MarginBottom = 24.dp

    /**
     * The padding around the text: 32dp from the screen edges (support_preference_title_padding_start
     * / support_preference_title_padding_end) and 8dp above and below
     * (support_preference_header_preference_padding_vertical, shared with the header).
     */
    val InsideMargin = PaddingValues(horizontal = 32.dp, vertical = 8.dp)

    /** The title color (COUIPreferenceTitleStyle, coui_preference_title_color). */
    val TitleColor: Color
        @Composable get() = COUITheme.colorScheme.labelPrimary

    /** The description color (coui_preference_secondary_text_color). */
    val SummaryColor: Color
        @Composable get() = COUITheme.colorScheme.labelSecondary
}
