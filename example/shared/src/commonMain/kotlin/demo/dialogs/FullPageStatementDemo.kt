// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import io.github.suqi8.coui.kmp.basic.FullPageStatement
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_full_page_statement_agree
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_full_page_statement_body
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_full_page_statement_body_more
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_full_page_statement_button_width
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_full_page_statement_button_width_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_full_page_statement_disagree
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_full_page_statement_height
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_full_page_statement_long_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_full_page_statement_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_full_page_statement_secondary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_full_page_statement_statement_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_full_page_statement_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_full_page_statement_width_auto
import org.jetbrains.compose.resources.stringResource

/** The demo page of `FullPageStatement`, listed in its category and found by the search. */
internal val FullPageStatementDemo = Demo(
    id = "full_page_statement",
    apiName = "FullPageStatement",
    title = Res.string.demo_full_page_statement_title,
    category = DemoCategory.Dialogs,
    page = DemoPage.Variants {
        playground {
            FullPageStatementPlayground()
        }
    },
    searchTerms = Res.string.demo_full_page_statement_search_terms,
)

/** The fixed primary button widths of the playground after "auto"; numbers stay literals. */
private val ButtonWidths = listOf(200, 280)

@Composable
private fun ColumnScope.FullPageStatementPlayground() {
    var height by rememberSaveable { mutableFloatStateOf(420f) }
    var secondary by rememberSaveable { mutableStateOf(true) }
    var longContent by rememberSaveable { mutableStateOf(true) }
    var widthIndex by rememberSaveable { mutableIntStateOf(0) }
    var resultCode by rememberSaveable { mutableIntStateOf(STATEMENT_RESULT_NONE) }
    var resultArg by rememberSaveable { mutableIntStateOf(0) }

    val links = rememberAgreementLinks { link ->
        resultCode = STATEMENT_RESULT_LINK
        resultArg = link
    }
    val body = stringResource(Res.string.demo_full_page_statement_body)
    val more = stringResource(Res.string.demo_full_page_statement_body_more)
    val content = remember(links, body, more, longContent) {
        buildAnnotatedString {
            append(body)
            append(" ")
            append(links)
            if (longContent) {
                repeat(6) {
                    append(" ")
                    append(more)
                }
            }
        }
    }

    val widthItems = listOf(stringResource(Res.string.demo_full_page_statement_width_auto)) +
        ButtonWidths.map { "$it dp" }

    PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
        // The statement page is designed to fill a whole window; the demo hosts it in a box of
        // adjustable height so it can live inside the component list.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height.dp),
        ) {
            FullPageStatement(
                title = stringResource(Res.string.demo_full_page_statement_statement_title),
                content = content,
                primaryButtonText = stringResource(Res.string.demo_full_page_statement_agree),
                onPrimaryButtonClick = { resultCode = STATEMENT_RESULT_AGREED },
                modifier = Modifier.fillMaxSize(),
                secondaryButtonText = if (secondary) stringResource(Res.string.demo_full_page_statement_disagree) else null,
                onSecondaryButtonClick = if (secondary) {
                    { resultCode = STATEMENT_RESULT_EXITED }
                } else {
                    null
                },
                primaryButtonWidth = ButtonWidths.getOrNull(widthIndex - 1)?.dp,
            )
        }
        DialogDemoCaption(
            text = statementResultText(code = resultCode, arg = resultArg),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
        )
    }
    PlaygroundControls {
        item {
            SliderPreference(
                value = height,
                onValueChange = { height = it },
                title = stringResource(Res.string.demo_full_page_statement_height),
                valueText = "${height.toInt()} dp",
                valueRange = 320f..640f,
            )
        }
        dialogSwitchRow(
            title = Res.string.demo_full_page_statement_secondary,
            checked = secondary,
            onCheckedChange = { secondary = it },
        )
        dialogSwitchRow(
            title = Res.string.common_long_text,
            checked = longContent,
            onCheckedChange = { longContent = it },
            summary = Res.string.demo_full_page_statement_long_summary,
        )
        dialogDropdownRow(
            title = Res.string.demo_full_page_statement_button_width,
            items = widthItems,
            selectedIndex = widthIndex,
            onSelectedIndexChange = { widthIndex = it },
            summary = Res.string.demo_full_page_statement_button_width_summary,
        )
    }
}
