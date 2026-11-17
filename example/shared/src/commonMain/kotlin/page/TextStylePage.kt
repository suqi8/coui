// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalScrollBarApi::class)

package page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import app.LocalNavigator
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardDefaults
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.VerticalScrollBar
import io.github.suqi8.coui.kmp.basic.rememberScrollBarAdapter
import io.github.suqi8.coui.kmp.interfaces.ExperimentalScrollBarApi
import io.github.suqi8.coui.kmp.preference.PreferenceCategory
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.design_text_group_body
import io.github.suqi8.coui.kmp.shared.generated.resources.design_text_group_button
import io.github.suqi8.coui.kmp.shared.generated.resources.design_text_group_footnote
import io.github.suqi8.coui.kmp.shared.generated.resources.design_text_group_headline
import io.github.suqi8.coui.kmp.shared.generated.resources.design_text_group_title
import io.github.suqi8.coui.kmp.shared.generated.resources.design_text_sample_cjk
import io.github.suqi8.coui.kmp.shared.generated.resources.design_text_styles
import io.github.suqi8.coui.kmp.theme.COUITheme
import lazyfont.LazyText
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import ui.AppPage

@Immutable
private data class TextStyleEntry(
    val name: String,
    val style: TextStyle,
    val description: String,
)

private const val SAMPLE_TEXT_EN = "The Quick Brown Fox Jumps"
private const val SAMPLE_TEXT_NUM = "0123456789 !@#$%&"

/**
 * The type scale reference: each COUI text style with CJK, Latin and numeric samples.
 */
@Composable
fun TextStylePage(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.current
    val textStyles = COUITheme.textStyles

    val styleEntries = remember(textStyles) {
        listOf(
            TextStyleEntry("displayL", textStyles.displayL, "72sp / Medium"),
            TextStyleEntry("display", textStyles.display, "48sp / Medium"),
            TextStyleEntry("displayM", textStyles.displayM, "36sp / SemiBold"),
            TextStyleEntry("displayS", textStyles.displayS, "32sp / SemiBold"),
            TextStyleEntry("displayXS", textStyles.displayXS, "24sp / SemiBold"),
            TextStyleEntry("headlineL", textStyles.headlineL, "28sp / Medium"),
            TextStyleEntry("headlineM", textStyles.headlineM, "20sp / Medium"),
            TextStyleEntry("headlineS", textStyles.headlineS, "18sp / 550"),
            TextStyleEntry("headlineXS", textStyles.headlineXS, "16sp / Medium"),
            TextStyleEntry("bodyXL", textStyles.bodyXL, "16sp"),
            TextStyleEntry("bodyL", textStyles.bodyL, "15sp"),
            TextStyleEntry("bodyM", textStyles.bodyM, "14sp"),
            TextStyleEntry("bodyS", textStyles.bodyS, "13sp"),
            TextStyleEntry("bodyXS", textStyles.bodyXS, "12sp"),
            TextStyleEntry("buttonL", textStyles.buttonL, "16sp / Medium"),
            TextStyleEntry("buttonM", textStyles.buttonM, "14sp / Medium"),
            TextStyleEntry("buttonS", textStyles.buttonS, "12sp / Medium"),
            TextStyleEntry("description", textStyles.description, "12sp / Medium"),
            TextStyleEntry("caption", textStyles.caption, "10sp"),
            TextStyleEntry("tag", textStyles.tag, "10sp / Medium"),
        )
    }

    AppPage(
        title = stringResource(Res.string.design_text_styles),
        modifier = modifier,
        onBack = { navigator.pop() },
    ) {
        styleGroup("title", Res.string.design_text_group_title) { styleEntries.subList(0, 5) }
        styleGroup("headline", Res.string.design_text_group_headline) { styleEntries.subList(5, 9) }
        styleGroup("body", Res.string.design_text_group_body) { styleEntries.subList(9, 14) }
        styleGroup("button", Res.string.design_text_group_button) { styleEntries.subList(14, 17) }
        styleGroup("footnote", Res.string.design_text_group_footnote) { styleEntries.subList(17, 20) }
    }
}

private fun LazyListScope.styleGroup(
    key: String,
    title: StringResource,
    entries: () -> List<TextStyleEntry>,
) {
    item(key = key) {
        PreferenceCategory(title = stringResource(title))
        Card(
            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
            insideMargin = PaddingValues(horizontal = 16.dp),
        ) {
            entries().forEachIndexed { index, entry ->
                if (index > 0) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                }
                TextStyleItem(entry)
            }
        }
    }
}

@Composable
private fun TextStyleItem(entry: TextStyleEntry) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LazyText(
                text = entry.name,
                style = COUITheme.textStyles.bodyS,
                color = COUITheme.colorScheme.labelPrimary,
                modifier = Modifier.weight(1f),
            )
            LazyText(
                text = entry.description,
                style = COUITheme.textStyles.bodyXS,
                color = COUITheme.colorScheme.labelSecondaryVariant,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyText(
            text = stringResource(Res.string.design_text_sample_cjk),
            style = entry.style,
            color = COUITheme.colorScheme.labelPrimary,
        )
        Spacer(modifier = Modifier.height(2.dp))
        LazyText(
            text = SAMPLE_TEXT_EN,
            style = entry.style,
            color = COUITheme.colorScheme.labelPrimary,
        )
        Spacer(modifier = Modifier.height(2.dp))
        LazyText(
            text = SAMPLE_TEXT_NUM,
            style = entry.style,
            color = COUITheme.colorScheme.labelSecondaryVariant,
        )
    }
}
