// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalScrollBarApi::class)

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.COUIScrollBehavior
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardDefaults
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.VerticalScrollBar
import io.github.suqi8.coui.kmp.basic.rememberScrollBarAdapter
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.interfaces.ExperimentalScrollBarApi
import io.github.suqi8.coui.kmp.theme.COUITheme
import lazyfont.LazyText
import utils.AdaptiveTopAppBar
import utils.BlurredBar
import utils.pageContentPadding
import utils.pageScrollModifiers
import utils.rememberBlurBackdrop

@Immutable
private data class TextStyleEntry(
    val name: String,
    val style: TextStyle,
    val description: String,
)

private const val SAMPLE_TEXT_CN = "天地玄黄 宇宙洪荒"
private const val SAMPLE_TEXT_EN = "The Quick Brown Fox Jumps"
private const val SAMPLE_TEXT_NUM = "0123456789 !@#$%&"

@Composable
fun TextStylePage(
    padding: PaddingValues,
) {
    val appState = LocalAppState.current
    val isWideScreen = LocalIsWideScreen.current
    val topAppBarScrollBehavior = COUIScrollBehavior()

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

    val backdrop = rememberBlurBackdrop()
    val blurActive = backdrop != null
    val barColor = if (blurActive) Color.Transparent else COUITheme.colorScheme.backgroundWithCard

    Scaffold(
        topBar = {
            BlurredBar(backdrop, blurActive) {
                AdaptiveTopAppBar(
                    title = "Text Style",
                    showTopAppBar = appState.showTopAppBar,
                    isWideScreen = isWideScreen,
                    scrollBehavior = topAppBarScrollBehavior,
                    color = barColor,
                    backdrop = backdrop,
                    blurActive = blurActive,
                )
            }
        },
    ) { innerPadding ->
        val lazyListState = rememberLazyListState()
        val contentPadding = pageContentPadding(innerPadding, padding, isWideScreen, extraTop = 12.dp)
        Box(modifier = if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.pageScrollModifiers(
                    appState.enableScrollEndHaptic,
                    appState.showTopAppBar,
                    topAppBarScrollBehavior,
                ),
                contentPadding = contentPadding,
            ) {
                item(key = "title_header") {
                    SmallTitle("Title Styles")
                }
                item(key = "title_card") {
                    Card(
                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                        colors = CardDefaults.defaultColors(color = COUITheme.colorScheme.cardBackground),
                        cornerRadius = 16.dp,
                        insideMargin = PaddingValues(horizontal = 16.dp),
                    ) {
                        styleEntries.subList(0, 4).forEachIndexed { index, entry ->
                            if (index > 0) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                            }
                            TextStyleItem(entry)
                        }
                    }
                }
                item(key = "headline_header") {
                    SmallTitle("Headline Styles")
                }
                item(key = "headline_card") {
                    Card(
                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                        colors = CardDefaults.defaultColors(color = COUITheme.colorScheme.cardBackground),
                        cornerRadius = 16.dp,
                        insideMargin = PaddingValues(horizontal = 16.dp),
                    ) {
                        styleEntries.subList(4, 6).forEachIndexed { index, entry ->
                            if (index > 0) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                            }
                            TextStyleItem(entry)
                        }
                    }
                }
                item(key = "body_header") {
                    SmallTitle("Body Styles")
                }
                item(key = "body_card") {
                    Card(
                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                        colors = CardDefaults.defaultColors(color = COUITheme.colorScheme.cardBackground),
                        cornerRadius = 16.dp,
                        insideMargin = PaddingValues(horizontal = 16.dp),
                    ) {
                        styleEntries.subList(6, 12).forEachIndexed { index, entry ->
                            if (index > 0) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                            }
                            TextStyleItem(entry)
                        }
                    }
                }
                item(key = "footnote_header") {
                    SmallTitle("Footnote Styles")
                }
                item(key = "footnote_card") {
                    Card(
                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                        colors = CardDefaults.defaultColors(color = COUITheme.colorScheme.cardBackground),
                        cornerRadius = 16.dp,
                        insideMargin = PaddingValues(horizontal = 16.dp),
                    ) {
                        styleEntries.subList(12, 14).forEachIndexed { index, entry ->
                            if (index > 0) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                            }
                            TextStyleItem(entry)
                        }
                    }
                }
                item(key = "all_header") {
                    SmallTitle("All Styles Overview")
                }
                item(key = "all_card") {
                    Card(
                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp),
                        colors = CardDefaults.defaultColors(color = COUITheme.colorScheme.cardBackground),
                        cornerRadius = 16.dp,
                        insideMargin = PaddingValues(horizontal = 16.dp),
                    ) {
                        styleEntries.forEachIndexed { index, entry ->
                            if (index > 0) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                            }
                            TextStyleItem(entry)
                        }
                    }
                }
            }
            VerticalScrollBar(
                adapter = rememberScrollBarAdapter(lazyListState),
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                trackPadding = contentPadding,
            )
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
            text = SAMPLE_TEXT_CN,
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
