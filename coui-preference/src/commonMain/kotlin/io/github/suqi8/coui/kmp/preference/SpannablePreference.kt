// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentColors
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.LinkText
import io.github.suqi8.coui.kmp.basic.LinkTextColors
import io.github.suqi8.coui.kmp.basic.LinkTextDefaults
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A preference row whose summary holds inline links (COUI `COUISpannablePreference`).
 *
 * `Preference.COUI.COUISpannablePreference` is a plain `COUIPreference` with `couiEnalbeClickSpan`
 * set, which makes `COUIPreferenceUtils.setSummaryView` give the summary a `LinkMovementMethod`.
 * Here the summary is an [AnnotatedString] drawn with [LinkText]: append links with
 * [io.github.suqi8.coui.kmp.basic.appendLink]. A tap on a link runs the link's callback only; a tap
 * anywhere else runs [onClick].
 *
 * @param title The title of the [SpannablePreference].
 * @param summary The summary of the [SpannablePreference], holding the links.
 * @param modifier The modifier to be applied to the [SpannablePreference].
 * @param titleColor The color of the title.
 * @param summaryColor The color of the non-link summary text.
 * @param linkColors The [LinkTextColors] of the summary links.
 * @param startAction The [Composable] content on the start side of the [SpannablePreference].
 * @param endActions The [Composable] content on the end side of the [SpannablePreference].
 * @param bottomAction The [Composable] content at the bottom of the [SpannablePreference].
 * @param insideMargin The margin inside the [SpannablePreference].
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra padding.
 * @param onClick The callback when the row (outside the links) is clicked; `null` keeps the row
 *   non-clickable while the links stay interactive.
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param enabled Whether the [SpannablePreference] and its links are enabled.
 */
@Composable
fun SpannablePreference(
    title: String,
    summary: AnnotatedString,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    linkColors: LinkTextColors = LinkTextDefaults.linkTextColors(),
    startAction: @Composable (() -> Unit)? = null,
    endActions: @Composable (RowScope.() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    onClick: (() -> Unit)? = null,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
) {
    BasicComponent(
        modifier = modifier,
        startAction = startAction,
        endActions = endActions,
        bottomAction = bottomAction,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        onClick = onClick,
        holdDownState = holdDownState,
        enabled = enabled,
    ) {
        // Same title / summary text as BasicComponent: COUIPreferenceTitleStyle and
        // COUIPreferenceSummaryStyle (TextAppearance.COUI.Preference.Summary, 13sp ArticleBody).
        Text(
            text = title,
            style = COUITheme.textStyles.headlineXS,
            color = if (enabled) titleColor.color else titleColor.disabledColor,
        )
        LinkText(
            text = summary,
            enabled = enabled,
            color = if (enabled) summaryColor.color else summaryColor.disabledColor,
            linkColors = linkColors,
            style = COUITheme.textStyles.bodyS,
        )
    }
}
