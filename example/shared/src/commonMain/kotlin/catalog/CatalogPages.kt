// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.LocalInDetailPane
import app.LocalNavigator
import demo.framework.LocalDemoSnackbarHostState
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.EmptyState
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.ListDetailLayout
import io.github.suqi8.coui.kmp.basic.ListDetailLayoutDefaults
import io.github.suqi8.coui.kmp.basic.SnackbarHostState
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.rememberTopAppBarSearchState
import io.github.suqi8.coui.kmp.basic.topAppBarSearchContent
import io.github.suqi8.coui.kmp.basic.topAppBarSearchResults
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Scan
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.PreferenceCategory
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.app_tab_components
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_group_more
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_list_empty_detail
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_new
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_entry_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.lab_title
import io.github.suqi8.coui.kmp.theme.COUITheme
import navigation3.Route
import org.jetbrains.compose.resources.stringResource
import ui.AppPage
import ui.AppPageList
import ui.AppPageScaffold
import ui.AppPageTopBarStyle

/**
 * The components tab: the category home on narrow windows, and on wide ones a list-detail layout
 * with every demo listed by category next to the selected demo.
 */
@Composable
fun ComponentsTab(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier) {
        if (ListDetailLayoutDefaults.isSplitMode(maxWidth, maxHeight)) {
            WideCatalog()
        } else {
            CatalogHomePage()
        }
    }
}

/**
 * The components home page, laid out like the ColorOS settings home: a large title with a search
 * bar, then the categories in grouped cards and the lab entry at the end. Tapping the search bar
 * searches in place like the settings home: the title shrinks and fades away, the search bar rises
 * to the top with a cancel button, the categories fade out and the matching demos come up from
 * below.
 */
@Composable
fun CatalogHomePage(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.current
    var searching by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    val index = rememberDemoSearchIndex()
    val results = rememberDemoSearchResults(query, index)
    val searchState = rememberTopAppBarSearchState(active = searching)
    // Hoisted, so the categories keep their scroll position while the search hides them.
    val homeListState = rememberLazyListState()

    AppPageScaffold(
        title = stringResource(Res.string.app_tab_components),
        modifier = modifier,
        topBarStyle = AppPageTopBarStyle.SettingsHome,
        topBarSearchState = searchState,
        topBarBottomContent = {
            CatalogSearchBar(
                query = query,
                onQueryChange = { query = it },
                expanded = searching,
                onExpandedChange = { active ->
                    searching = active
                    if (!active) query = ""
                },
            )
        },
    ) { scope ->
        Box(modifier = Modifier.fillMaxSize()) {
            // The categories fade out behind the search; the results come up from below.
            if (searchState.isContentVisible) {
                AppPageList(
                    scope = scope,
                    modifier = Modifier.topAppBarSearchContent(searchState),
                    listState = homeListState,
                ) {
                    DemoGroup.entries.forEach { group ->
                        item(key = "group_${group.name}") {
                            PreferenceCategory(title = stringResource(group.title))
                            Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                                CardListGroup {
                                    DemoCategory.entries.filter { it.group == group }.forEach { category ->
                                        item(key = category.id) {
                                            CategoryRow(
                                                category = category,
                                                onClick = { navigator.push(Route.Category(category.id)) },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    labEntry(onClick = { navigator.push(Route.Lab) })
                }
            }
            if (searchState.isResultsVisible) {
                AppPageList(scope = scope, modifier = Modifier.topAppBarSearchResults(searchState)) {
                    demoSearchResults(query = query, results = results, onOpen = { navigator.push(Route.Demo(it.id)) })
                }
            }
        }
    }
}

/**
 * A category page: the category's demos in one card.
 */
@Composable
fun CategoryPage(
    category: DemoCategory,
    modifier: Modifier = Modifier,
) {
    val navigator = LocalNavigator.current
    val demos = remember(category) { DemoCatalog.byCategory(category) }
    AppPage(
        title = stringResource(category.title),
        modifier = modifier,
        onBack = { navigator.pop() },
    ) {
        item(key = "demos") {
            Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                CardListGroup {
                    demos.forEach { demo ->
                        item(key = demo.id) {
                            DemoRow(demo = demo, onClick = { navigator.push(Route.Demo(demo.id)) })
                        }
                    }
                }
            }
        }
    }
}

/**
 * A demo page: the variants list with a snackbar host, or the demo's own full-screen page.
 */
@Composable
fun DemoHostPage(
    demo: Demo,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (val page = demo.page) {
        is DemoPage.Variants -> {
            val snackbarHostState = remember { SnackbarHostState() }
            CompositionLocalProvider(LocalDemoSnackbarHostState provides snackbarHostState) {
                AppPage(
                    title = stringResource(demo.title),
                    modifier = modifier,
                    subtitle = demo.apiName,
                    onBack = onBack,
                    snackbarHostState = snackbarHostState,
                    content = page.content,
                )
            }
        }

        is DemoPage.FullScreen -> page.content(onBack)
    }
}

/**
 * The wide-screen catalog: every demo grouped by category in the list pane, the selected demo in
 * the detail pane. Full-screen demos own the page slots, so they are pushed instead.
 */
@Composable
private fun WideCatalog() {
    val navigator = LocalNavigator.current
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = selectedId?.let { DemoCatalog.byId(it) }
    ListDetailLayout(
        list = {
            AppPage(title = stringResource(Res.string.app_tab_components)) {
                DemoCategory.entries.forEach { category ->
                    item(key = "category_${category.id}") {
                        PreferenceCategory(title = stringResource(category.title))
                        Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                            CardListGroup {
                                DemoCatalog.byCategory(category).forEach { demo ->
                                    item(key = demo.id) {
                                        DemoRow(
                                            demo = demo,
                                            onClick = {
                                                if (demo.page is DemoPage.FullScreen) {
                                                    navigator.push(Route.Demo(demo.id))
                                                } else {
                                                    selectedId = demo.id
                                                }
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                labEntry(onClick = { navigator.push(Route.Lab) })
            }
        },
        detail = selected?.let { demo ->
            {
                CompositionLocalProvider(LocalInDetailPane provides true) {
                    key(demo.id) {
                        DemoHostPage(demo = demo, onBack = { selectedId = null })
                    }
                }
            }
        },
        emptyDetail = {
            EmptyState(title = stringResource(Res.string.catalog_list_empty_detail))
        },
    )
}

@Composable
private fun CategoryRow(
    category: DemoCategory,
    onClick: () -> Unit,
) {
    val count = remember(category) { DemoCatalog.byCategory(category).size }
    ArrowPreference(
        title = stringResource(category.title),
        summary = stringResource(category.summary),
        startAction = {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = COUITheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
        },
        endActions = {
            Text(
                text = count.toString(),
                style = COUITheme.textStyles.bodyM,
                color = COUITheme.colorScheme.labelTertiary,
            )
        },
        onClick = onClick,
    )
}

/**
 * A demo row: the localized name, the API name as the summary and a badge for COUI 17 components.
 *
 * @param demo The demo.
 * @param onClick Opens the demo.
 * @param modifier The modifier for the row.
 */
@Composable
fun DemoRow(
    demo: Demo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ArrowPreference(
        title = stringResource(demo.title),
        modifier = modifier,
        summary = demo.apiName,
        endActions = {
            if (demo.isNew) {
                Text(
                    text = stringResource(Res.string.catalog_new),
                    style = COUITheme.textStyles.bodyS,
                    color = COUITheme.colorScheme.primary,
                )
                Spacer(Modifier.width(4.dp))
            }
        },
        onClick = onClick,
    )
}

private fun LazyListScope.labEntry(onClick: () -> Unit) {
    item(key = "group_more") {
        PreferenceCategory(title = stringResource(Res.string.catalog_group_more))
        Card(modifier = Modifier.padding(horizontal = 16.dp)) {
            CardListGroup {
                item {
                    ArrowPreference(
                        title = stringResource(Res.string.lab_title),
                        summary = stringResource(Res.string.lab_entry_summary),
                        startAction = {
                            Icon(
                                imageVector = COUIIcons.Scan,
                                contentDescription = null,
                                tint = COUITheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp),
                            )
                        },
                        onClick = onClick,
                    )
                }
            }
        }
    }
}
