// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.LinkText
import io.github.suqi8.coui.kmp.basic.LinkTextColors
import io.github.suqi8.coui.kmp.basic.LinkTextDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * The description at the top of a settings page (COUI `COUIPagerHeaderPreference`, layout
 * `coui_pager_header_preference`).
 *
 * It sits on the page background above the first card: an optional title in
 * `COUIPreferenceTitleStyle` above the description in `COUIPreferenceDescriptionStyle`
 * (`TextAppearance.COUI.Preference.Description`, 12sp `couiTextBodyXS` in
 * `coui_preference_secondary_text_color`).
 *
 * @param summary The description text.
 * @param modifier The modifier to be applied to the [PagerHeaderPreference].
 * @param title The optional title above the description.
 * @param titleColor The color of the title.
 * @param summaryColor The color of the description.
 * @param insideMargin The padding around the text (`support_preference_title_padding_start/end`,
 *   `support_preference_header_preference_padding_vertical`).
 */
@Composable
@NonRestartableComposable
fun PagerHeaderPreference(
    summary: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color = PagerHeaderPreferenceDefaults.TitleColor,
    summaryColor: Color = PagerHeaderPreferenceDefaults.SummaryColor,
    insideMargin: PaddingValues = PagerHeaderPreferenceDefaults.InsideMargin,
) {
    PagerDescription(
        summary = AnnotatedString(summary),
        title = title,
        titleColor = titleColor,
        summaryColor = summaryColor,
        linkColors = null,
        insideMargin = insideMargin,
        // coui_pager_header_preference: layout_marginTop = support_preference_header_preference_margin_top.
        modifier = modifier.padding(top = PagerHeaderPreferenceDefaults.MarginTop),
    )
}

object PagerHeaderPreferenceDefaults {

    /** The space above the header (COUI support_preference_header_preference_margin_top). */
    val MarginTop = 12.dp

    /**
     * The padding around the text: 32dp from the screen edges (support_preference_title_padding_start
     * / support_preference_title_padding_end, which line up with the text inside cards) and 8dp above
     * and below (support_preference_header_preference_padding_vertical).
     */
    val InsideMargin = PaddingValues(horizontal = 32.dp, vertical = 8.dp)

    /** The title color (COUIPreferenceTitleStyle, coui_preference_title_color). */
    val TitleColor: Color
        @Composable get() = COUITheme.colorScheme.labelPrimary

    /** The description color (coui_preference_secondary_text_color). */
    val SummaryColor: Color
        @Composable get() = COUITheme.colorScheme.labelSecondary
}

/**
 * The shared body of the pager header and footer: the RelativeLayout of
 * `coui_pager_header_preference` / `coui_pager_footer_preference`, title above summary.
 */
@Composable
internal fun PagerDescription(
    summary: AnnotatedString,
    title: String?,
    titleColor: Color,
    summaryColor: Color,
    linkColors: LinkTextColors?,
    insideMargin: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(insideMargin),
    ) {
        if (title != null) {
            Text(
                text = title,
                style = COUITheme.textStyles.headlineXS,
                color = titleColor,
            )
        }
        if (summary.isNotEmpty()) {
            LinkText(
                text = summary,
                color = summaryColor,
                linkColors = linkColors ?: LinkTextDefaults.linkTextColors(),
                // coui_preference_header_footer_textsize 12sp = couiTextBodyXS.
                style = COUITheme.textStyles.bodyXS,
            )
        }
    }
}
