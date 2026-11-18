// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.TopAppBarSearchBar
import io.github.suqi8.coui.kmp.basic.rememberTopAppBarSearchState
import io.github.suqi8.coui.kmp.basic.topAppBarSearchContent
import io.github.suqi8.coui.kmp.basic.topAppBarSearchResults
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.More
import io.github.suqi8.coui.kmp.icon.extended.Search
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_cancel
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_more
import io.github.suqi8.coui.kmp.shared.generated.resources.common_search
import io.github.suqi8.coui.kmp.shared.generated.resources.common_style
import io.github.suqi8.coui.kmp.shared.generated.resources.common_visible
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_app_bar_accent_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_app_bar_actions
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_app_bar_entry
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_app_bar_search_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_app_bar_search_slot
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_app_bar_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_app_bar_style_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_app_bar_subtitle
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_app_bar_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_app_bar_visible_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_title
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource
import ui.AppPageContentScope
import ui.AppPageList
import ui.AppPageScaffold
import ui.AppPageTopBarStyle

/** The demo page of `TopAppBar`, listed in its category and found by the search. */
internal val TopAppBarDemo = Demo(
    id = "top_app_bar",
    apiName = "TopAppBar",
    title = Res.string.demo_top_app_bar_title,
    category = DemoCategory.Navigation,
    page = DemoPage.FullScreen { onBack -> TopAppBarDemoPage(onBack = onBack) },
    keywords = listOf("SmallTopAppBar", "TopAppBarSearchBar", "TopAppBarSearchState"),
    searchTerms = Res.string.demo_top_app_bar_search_terms,
)

private val TopAppBarDemoStyles = AppPageTopBarStyle.entries
private val TopAppBarDemoStyleNames = TopAppBarDemoStyles.map { it.name }

/**
 * The top bar in its real slot: the page list holds the parameters and a long run of entries to
 * scroll, so the large title collapses and the condensed-light glow shows under the bar.
 */
@Composable
private fun TopAppBarDemoPage(onBack: () -> Unit) {
    var showTopBar by rememberSaveable { mutableStateOf(true) }
    var styleIndex by rememberSaveable { mutableIntStateOf(0) }
    var showSubtitle by rememberSaveable { mutableStateOf(true) }
    var showActions by rememberSaveable { mutableStateOf(true) }
    var showSearch by rememberSaveable { mutableStateOf(false) }
    var accentTitle by rememberSaveable { mutableStateOf(false) }
    var longTitle by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var searching by rememberSaveable { mutableStateOf(false) }
    val searchState = rememberTopAppBarSearchState(active = searching && showSearch)
    // Hoisted, so the controls keep their scroll position while the search hides them.
    val listState = rememberLazyListState()
    val matches = remember(query) {
        val q = query.trim()
        (1..ENTRY_COUNT).filter { q.isEmpty() || it.toString().contains(q) }
    }

    AppPageScaffold(
        title = if (longTitle) stringResource(Res.string.sample_long_title) else stringResource(Res.string.demo_top_app_bar_title),
        subtitle = if (showSubtitle) "TopAppBar" else "",
        onBack = onBack,
        topBarStyle = TopAppBarDemoStyles[styleIndex],
        showTopBar = showTopBar,
        titleColor = if (accentTitle) COUITheme.colorScheme.primary else Color.Unspecified,
        actions = {
            if (showActions) {
                TopAppBarDemoAction(icon = COUIIcons.Search, contentDescription = stringResource(Res.string.common_search))
                TopAppBarDemoAction(icon = COUIIcons.More, contentDescription = stringResource(Res.string.common_more))
            }
        },
        topBarSearchState = searchState,
        topBarBottomContent = {
            if (showSearch) {
                TopAppBarSearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    expanded = searching,
                    onExpandedChange = { active ->
                        searching = active
                        if (!active) query = ""
                    },
                    cancelText = stringResource(Res.string.common_cancel),
                    label = stringResource(Res.string.demo_top_app_bar_search_hint),
                )
            }
        },
    ) { scope ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (searchState.isContentVisible) {
                TopAppBarDemoList(
                    scope = scope,
                    listState = listState,
                    modifier = Modifier.topAppBarSearchContent(searchState),
                ) {
                    PlaygroundControls {
                        item {
                            SwitchPreference(
                                title = stringResource(Res.string.common_visible),
                                summary = stringResource(Res.string.demo_top_app_bar_visible_summary),
                                checked = showTopBar,
                                onCheckedChange = { showTopBar = it },
                            )
                        }
                        item {
                            OverlayDropdownPreference(
                                items = TopAppBarDemoStyleNames,
                                selectedIndex = styleIndex,
                                title = stringResource(Res.string.common_style),
                                summary = stringResource(Res.string.demo_top_app_bar_style_summary),
                                onSelectedIndexChange = { styleIndex = it },
                            )
                        }
                        item {
                            SwitchPreference(
                                title = stringResource(Res.string.demo_top_app_bar_subtitle),
                                checked = showSubtitle,
                                onCheckedChange = { showSubtitle = it },
                            )
                        }
                        item {
                            SwitchPreference(
                                title = stringResource(Res.string.common_long_text),
                                checked = longTitle,
                                onCheckedChange = { longTitle = it },
                            )
                        }
                        item {
                            SwitchPreference(
                                title = stringResource(Res.string.demo_top_app_bar_actions),
                                checked = showActions,
                                onCheckedChange = { showActions = it },
                            )
                        }
                        item {
                            SwitchPreference(
                                title = stringResource(Res.string.demo_top_app_bar_search_slot),
                                checked = showSearch,
                                onCheckedChange = { showSearch = it },
                            )
                        }
                        item {
                            SwitchPreference(
                                title = stringResource(Res.string.demo_top_app_bar_accent_title),
                                checked = accentTitle,
                                onCheckedChange = { accentTitle = it },
                            )
                        }
                    }
                }
            }
            // The search results: the entries whose number contains the query.
            if (searchState.isResultsVisible) {
                AppPageList(scope = scope, modifier = Modifier.topAppBarSearchResults(searchState)) {
                    topAppBarDemoEntries(matches)
                }
            }
        }
    }
}

/** The page list: the parameter controls, then the entries to scroll. */
@Composable
private fun TopAppBarDemoList(
    scope: AppPageContentScope,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    controls: @Composable () -> Unit,
) {
    AppPageList(scope = scope, modifier = modifier, listState = listState) {
        item(key = "controls") { controls() }
        topAppBarDemoEntries((1..ENTRY_COUNT).toList())
    }
}

private fun LazyListScope.topAppBarDemoEntries(numbers: List<Int>) {
    items(numbers.size, key = { "entry_${numbers[it]}" }) { position ->
        BasicComponent(
            modifier = Modifier.padding(horizontal = 16.dp),
            title = stringResource(Res.string.demo_top_app_bar_entry, numbers[position]),
        )
    }
}

private const val ENTRY_COUNT = 40

/** A top bar icon button; COUI 17 floats it on a material panel. */
@Composable
private fun TopAppBarDemoAction(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = {}, modifier = modifier) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = COUITheme.colorScheme.labelPrimary,
        )
    }
}
