// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import io.github.suqi8.coui.kmp.basic.FullPageStatementDefaults
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_agreement
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_links
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_privacy
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_result_agreed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_result_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_result_enabled_none
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_result_exited
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_result_none
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_result_opened
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_result_tapped
import org.jetbrains.compose.resources.stringResource

/** The user agreement link of [rememberAgreementLinks]. */
internal const val LINK_AGREEMENT = 0

/** The privacy policy link of [rememberAgreementLinks]. */
internal const val LINK_PRIVACY = 1

// The last action of a statement demo, kept as a code plus an argument so its text follows the
// language: the link id, the tapped row index or the bit mask of the enabled optional features.
internal const val STATEMENT_RESULT_NONE = 0
internal const val STATEMENT_RESULT_AGREED = 1
internal const val STATEMENT_RESULT_EXITED = 2
internal const val STATEMENT_RESULT_LINK = 3
internal const val STATEMENT_RESULT_LIST_ITEM = 4
internal const val STATEMENT_RESULT_ENABLED = 5

/**
 * The "Read the User Agreement and the Privacy Policy." sentence with both names as clickable links
 * styled like COUI statement links.
 *
 * The sentence comes from a template holding `%1$s` (agreement) and `%2$s` (privacy policy), so
 * each language places the links where its grammar wants them.
 *
 * @param onLinkClick Invoked with [LINK_AGREEMENT] or [LINK_PRIVACY].
 */
@Composable
internal fun rememberAgreementLinks(onLinkClick: (link: Int) -> Unit): AnnotatedString {
    val linkStyles = FullPageStatementDefaults.linkStyles()
    val template = stringResource(Res.string.demo_statement_links)
    val agreement = stringResource(Res.string.demo_statement_agreement)
    val privacy = stringResource(Res.string.demo_statement_privacy)
    val currentOnLinkClick by rememberUpdatedState(onLinkClick)
    return remember(linkStyles, template, agreement, privacy) {
        val parts = template.split("%1\$s", "%2\$s")
        buildAnnotatedString {
            append(parts.getOrElse(0) { "" })
            withLink(LinkAnnotation.Clickable(tag = "agreement", styles = linkStyles) { currentOnLinkClick(LINK_AGREEMENT) }) {
                append(agreement)
            }
            append(parts.getOrElse(1) { " " })
            withLink(LinkAnnotation.Clickable(tag = "privacy", styles = linkStyles) { currentOnLinkClick(LINK_PRIVACY) }) {
                append(privacy)
            }
            append(parts.getOrElse(2) { "" })
        }
    }
}

/**
 * Resolves a statement demo result to text.
 *
 * @param code One of the `STATEMENT_RESULT_*` codes.
 * @param arg The argument of [code].
 * @param names The row names a [STATEMENT_RESULT_LIST_ITEM] index or a [STATEMENT_RESULT_ENABLED]
 *   bit mask refers to.
 */
@Composable
internal fun statementResultText(
    code: Int,
    arg: Int,
    names: List<String> = emptyList(),
): String = when (code) {
    STATEMENT_RESULT_AGREED -> stringResource(Res.string.demo_statement_result_agreed)

    STATEMENT_RESULT_EXITED -> stringResource(Res.string.demo_statement_result_exited)

    STATEMENT_RESULT_LINK -> stringResource(
        Res.string.demo_statement_result_opened,
        stringResource(if (arg == LINK_PRIVACY) Res.string.demo_statement_privacy else Res.string.demo_statement_agreement),
    )

    STATEMENT_RESULT_LIST_ITEM -> stringResource(Res.string.demo_statement_result_tapped, names.getOrElse(arg) { "-" })

    STATEMENT_RESULT_ENABLED -> {
        val enabled = names.filterIndexed { index, _ -> (arg and (1 shl index)) != 0 }
        if (enabled.isEmpty()) {
            stringResource(Res.string.demo_statement_result_enabled_none)
        } else {
            stringResource(Res.string.demo_statement_result_enabled, enabled.joinToString())
        }
    }

    else -> stringResource(Res.string.demo_statement_result_none)
}
