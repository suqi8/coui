// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.LocalDemoSnackbarHostState
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.LinkText
import io.github.suqi8.coui.kmp.basic.appendLink
import io.github.suqi8.coui.kmp.preference.BottomPreference
import io.github.suqi8.coui.kmp.preference.PagerFooterPreference
import io.github.suqi8.coui.kmp.preference.PagerHeaderPreference
import io.github.suqi8.coui.kmp.preference.SpannablePreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_clicked
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pager_preference_agreement
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pager_preference_disabled_link
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pager_preference_extra_margin
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pager_preference_footer_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pager_preference_header_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pager_preference_learn_more
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pager_preference_links_clicked
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pager_preference_location
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pager_preference_location_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pager_preference_network_settings
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pager_preference_privacy_policy
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pager_preference_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pager_preference_text_with_link
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pager_preference_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pager_preference_user_agreement
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pager_preference_with_link
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_component
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_long_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_title
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

private val PagerTypes = listOf("PagerHeaderPreference", "PagerFooterPreference", "SpannablePreference", "LinkText")

/** The demo page of `PagerHeaderPreference`, listed in its category and found by the search. */
internal val PagerPreferenceDemo = Demo(
    id = "pager_preference",
    apiName = "PagerHeaderPreference",
    title = Res.string.demo_pager_preference_title,
    category = DemoCategory.Preference,
    page = DemoPage.Variants {
        playground {
            var type by rememberSaveable { mutableIntStateOf(0) }
            var showTitle by rememberSaveable { mutableStateOf(false) }
            var longSummary by rememberSaveable { mutableStateOf(false) }
            var withLink by rememberSaveable { mutableStateOf(true) }
            var extraMargin by rememberSaveable { mutableStateOf(true) }
            var enabled by rememberSaveable { mutableStateOf(true) }
            val onLinkClick = rememberLinkClickHandler()
            val title = stringResource(Res.string.sample_title)
            val text = stringResource(if (longSummary) Res.string.sample_long_text else Res.string.demo_pager_preference_header_text)
            val learnMore = stringResource(Res.string.demo_pager_preference_learn_more)
            val annotated = if (withLink) {
                linkedText(
                    Res.string.demo_pager_preference_text_with_link,
                    text,
                    learnMore to { onLinkClick(Res.string.demo_pager_preference_learn_more) },
                )
            } else {
                AnnotatedString(text)
            }
            PlaygroundPreview(
                inCard = type >= 2,
                contentPadding = if (type == 3) PaddingValues(16.dp) else PaddingValues(0.dp),
            ) {
                when (type) {
                    0 -> PagerHeaderPreference(summary = text, title = if (showTitle) title else null)

                    1 -> PagerFooterPreference(
                        summary = annotated,
                        title = if (showTitle) title else null,
                        withExtraMarginBottom = extraMargin,
                    )

                    2 -> CardListGroup {
                        item {
                            SpannablePreference(
                                title = title,
                                summary = annotated,
                                onClick = {},
                                enabled = enabled,
                            )
                        }
                    }

                    else -> LinkText(
                        text = annotated,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enabled,
                        color = COUITheme.colorScheme.labelSecondary,
                        style = COUITheme.textStyles.bodyS,
                    )
                }
            }
            PlaygroundControls {
                literalChoiceControl(Res.string.demo_pref_component, PagerTypes, type) { type = it }
                if (type <= 1) switchControl(Res.string.common_show_title, showTitle) { showTitle = it }
                switchControl(Res.string.demo_pref_long_summary, longSummary) { longSummary = it }
                if (type >= 1) switchControl(Res.string.demo_pager_preference_with_link, withLink) { withLink = it }
                if (type == 1) switchControl(Res.string.demo_pager_preference_extra_margin, extraMargin) { extraMargin = it }
                if (type >= 2) switchControl(Res.string.common_enabled, enabled) { enabled = it }
            }
        }
        variant(key = "header", title = "PagerHeaderPreference", inCard = false) {
            PagerHeaderPreference(summary = stringResource(Res.string.demo_pager_preference_header_text))
        }
        variant(key = "spannable", title = "SpannablePreference") {
            SpannableRows()
        }
        variant(key = "linkText", title = "LinkText") {
            LinkTextExample()
        }
        variant(key = "footer", title = "PagerFooterPreference + BottomPreference", inCard = false) {
            val onLinkClick = rememberLinkClickHandler()
            val networkSettings = stringResource(Res.string.demo_pager_preference_network_settings)
            PagerFooterPreference(
                summary = linkedText(
                    Res.string.demo_pager_preference_footer_text,
                    null,
                    networkSettings to { onLinkClick(Res.string.demo_pager_preference_network_settings) },
                ),
            )
            BottomPreference()
        }
    },
    searchTerms = Res.string.demo_pager_preference_search_terms,
)

/**
 * Returns a handler that shows "Clicked: label" in the page snackbar.
 */
@Composable
private fun rememberLinkClickHandler(): (StringResource) -> Unit {
    val snackbar = LocalDemoSnackbarHostState.current
    val scope = rememberCoroutineScope()
    return remember(snackbar, scope) {
        val handler: (StringResource) -> Unit = { label ->
            scope.launch { snackbar.showSnackbar(getString(Res.string.common_clicked, getString(label))) }
        }
        handler
    }
}

/**
 * A row with an inline link in its summary, and a disabled one whose link is greyed out.
 */
@Composable
private fun SpannableRows() {
    val onLinkClick = rememberLinkClickHandler()
    val learnMore = stringResource(Res.string.demo_pager_preference_learn_more)
    val privacyPolicy = stringResource(Res.string.demo_pager_preference_privacy_policy)
    val locationSummary = linkedText(
        Res.string.demo_pager_preference_location_text,
        null,
        learnMore to { onLinkClick(Res.string.demo_pager_preference_learn_more) },
    )
    val disabledSummary = linkedText(Res.string.demo_pager_preference_disabled_link, null, privacyPolicy to {})
    CardListGroup {
        item {
            SpannablePreference(
                title = stringResource(Res.string.demo_pager_preference_location),
                summary = locationSummary,
                onClick = { onLinkClick(Res.string.demo_pager_preference_location) },
            )
        }
        item {
            SpannablePreference(
                title = stringResource(Res.string.common_disabled),
                summary = disabledSummary,
                enabled = false,
            )
        }
    }
}

/**
 * Body text with two links that count their clicks.
 */
@Composable
private fun LinkTextExample(modifier: Modifier = Modifier) {
    var clicks by rememberSaveable { mutableIntStateOf(0) }
    val userAgreement = stringResource(Res.string.demo_pager_preference_user_agreement)
    val privacyPolicy = stringResource(Res.string.demo_pager_preference_privacy_policy)
    val clicked = stringResource(Res.string.demo_pager_preference_links_clicked, clicks)
    val agreement = linkedText(
        Res.string.demo_pager_preference_agreement,
        null,
        userAgreement to { clicks += 1 },
        privacyPolicy to { clicks += 1 },
    )
    LinkText(
        text = buildAnnotatedString {
            append(agreement)
            append("\n")
            append(clicked)
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = COUITheme.colorScheme.labelSecondary,
        style = COUITheme.textStyles.bodyS,
    )
}

/** Marks where a link goes in a formatted template; never part of real text. */
private const val LINK_MARK = '\u0001'

/**
 * Formats [template] and turns its placeholders into links, so translations decide where the links
 * sit and how they are spaced. The template's first placeholder is [text] when it is given; the
 * remaining placeholders are the [links], in order.
 */
@Composable
private fun linkedText(
    template: StringResource,
    text: String?,
    vararg links: Pair<String, () -> Unit>,
): AnnotatedString {
    val marks = List(links.size) { LINK_MARK.toString() }
    val args: List<Any> = if (text != null) listOf(text) + marks else marks
    val parts = stringResource(template, *args.toTypedArray()).split(LINK_MARK)
    return buildAnnotatedString {
        parts.forEachIndexed { index, part ->
            append(part)
            links.getOrNull(index)?.let { (label, onClick) -> appendLink(label, onClick = onClick) }
        }
    }
}
