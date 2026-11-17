// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.PageIndicator
import io.github.suqi8.coui.kmp.basic.PageIndicatorDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_back
import io.github.suqi8.coui.kmp.shared.generated.resources.common_color
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_default
import io.github.suqi8.coui.kmp.shared.generated.resources.common_next
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_page_indicator_animated
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_page_indicator_color_theme
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_page_indicator_dot_click
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_page_indicator_page
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_page_indicator_position
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_page_indicator_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_page_indicator_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_page_indicator_variant_many
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_page_indicator_variant_pager
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource

/** The demo page of `PageIndicator`, listed in its category and found by the search. */
internal val PageIndicatorDemo = Demo(
    id = "page_indicator",
    apiName = "PageIndicator",
    title = Res.string.demo_page_indicator_title,
    category = DemoCategory.Display,
    page = DemoPage.Variants { pageIndicatorPage() },
    searchTerms = Res.string.demo_page_indicator_search_terms,
)

/** The page count of the many-pages variant. */
private const val MANY_PAGES = 12

private fun LazyListScope.pageIndicatorPage() {
    playground {
        var pageCount by rememberSaveable { mutableFloatStateOf(5f) }
        var current by rememberSaveable { mutableIntStateOf(0) }
        var animated by rememberSaveable { mutableStateOf(true) }
        var dotClick by rememberSaveable { mutableStateOf(true) }
        var themeColors by rememberSaveable { mutableStateOf(false) }

        val count = pageCount.toInt()
        val page = current.coerceIn(0, count - 1)
        PlaygroundPreview {
            IndicatorWithButtons(
                pageCount = count,
                currentPage = page,
                onPageChange = { current = it },
                animated = animated,
                dotClickable = dotClick,
                themeColors = themeColors,
            )
        }
        PlaygroundControls {
            item {
                SliderPreference(
                    value = pageCount,
                    onValueChange = { pageCount = it },
                    title = stringResource(Res.string.common_count),
                    summary = "pageCount",
                    valueText = count.toString(),
                    valueRange = 2f..MANY_PAGES.toFloat(),
                    steps = MANY_PAGES - 3,
                )
            }
            item {
                SliderPreference(
                    value = page.toFloat(),
                    onValueChange = { current = it.toInt() },
                    title = stringResource(Res.string.demo_page_indicator_position),
                    summary = "currentPage",
                    valueText = (page + 1).toString(),
                    valueRange = 0f..(count - 1).toFloat(),
                    steps = (count - 2).coerceAtLeast(0),
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_page_indicator_animated),
                    summary = "animated",
                    checked = animated,
                    onCheckedChange = { animated = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_page_indicator_dot_click),
                    summary = "onDotClick",
                    checked = dotClick,
                    onCheckedChange = { dotClick = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_color),
                    summary = "colors",
                    items = listOf(
                        stringResource(Res.string.common_default),
                        stringResource(Res.string.demo_page_indicator_color_theme),
                    ),
                    selectedIndex = if (themeColors) 1 else 0,
                    onSelectedIndexChange = { themeColors = it == 1 },
                )
            }
        }
    }
    // Following a pager: swipe to see the trace, tap a dot to jump.
    variant(key = "pager", title = Res.string.demo_page_indicator_variant_pager) {
        val pagerState = rememberPagerState { 4 }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
        ) { index ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = stringResource(Res.string.demo_page_indicator_page, index + 1))
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            PageIndicator(pagerState = pagerState)
        }
    }
    // A plain position with more than five pages: the window of full dots follows it.
    variant(key = "many", title = Res.string.demo_page_indicator_variant_many) {
        var current by remember { mutableIntStateOf(0) }
        Column(modifier = Modifier.padding(16.dp)) {
            IndicatorWithButtons(
                pageCount = MANY_PAGES,
                currentPage = current,
                onPageChange = { current = it },
            )
        }
    }
}

/** A position-driven indicator with previous / next buttons below it. */
@Composable
private fun IndicatorWithButtons(
    pageCount: Int,
    currentPage: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    dotClickable: Boolean = true,
    themeColors: Boolean = false,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PageIndicator(
            pageCount = pageCount,
            currentPage = currentPage,
            animated = animated,
            colors = if (themeColors) {
                PageIndicatorDefaults.colors(
                    dotColor = COUITheme.colorScheme.primary.copy(alpha = 0.3f),
                    traceColor = COUITheme.colorScheme.primary,
                )
            } else {
                PageIndicatorDefaults.colors()
            },
            onDotClick = if (dotClickable) onPageChange else null,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(
                text = stringResource(Res.string.common_back),
                onClick = { if (currentPage > 0) onPageChange(currentPage - 1) },
                modifier = Modifier.weight(1f),
                enabled = currentPage > 0,
            )
            TextButton(
                text = stringResource(Res.string.common_next),
                onClick = { if (currentPage < pageCount - 1) onPageChange(currentPage + 1) },
                modifier = Modifier.weight(1f),
                enabled = currentPage < pageCount - 1,
            )
        }
    }
}
