// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalScrollBarApi::class)

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import component.BackNavigationIcon
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.COUIScrollBehavior
import io.github.suqi8.coui.kmp.basic.CancelButton
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.InputField
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.SearchBar
import io.github.suqi8.coui.kmp.basic.ToolbarFloatingSearchBar
import io.github.suqi8.coui.kmp.basic.ToolbarSearchAnchor
import io.github.suqi8.coui.kmp.basic.rememberToolbarSearchState
import io.github.suqi8.coui.kmp.basic.toolbarSearchEntry
import io.github.suqi8.coui.kmp.basic.toolbarSearchItem
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Search
import io.github.suqi8.coui.kmp.interfaces.ExperimentalScrollBarApi
import io.github.suqi8.coui.kmp.theme.COUITheme
import utils.AdaptiveTopAppBar
import utils.pageContentPadding
import utils.pageScrollModifiers

/**
 * Demonstrates a search bar that expands out of the toolbar's search icon, the way COUI 17
 * `COUISearchBar.showInToolBar` does.
 */
@Composable
fun ToolbarSearchPage(
    padding: PaddingValues,
) {
    val navigator = LocalNavigator.current
    val appState = LocalAppState.current
    val isWideScreen = LocalIsWideScreen.current
    val topAppBarScrollBehavior = COUIScrollBehavior()
    val lazyListState = rememberLazyListState()

    val searchState = rememberToolbarSearchState()
    var query by rememberSaveable { mutableStateOf("") }

    val entries = remember { List(30) { "Result item $it" } }
    val results = remember(query, entries) {
        if (query.isBlank()) entries else entries.filter { it.contains(query, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            Box {
                ToolbarSearchAnchor(state = searchState) {
                    val titleColor = COUITheme.colorScheme.labelPrimary
                    AdaptiveTopAppBar(
                        title = "Toolbar search",
                        titleColor = titleColor.copy(alpha = titleColor.alpha * searchState.toolbarTitleAlpha),
                        showTopAppBar = appState.showTopAppBar,
                        isWideScreen = isWideScreen,
                        scrollBehavior = topAppBarScrollBehavior,
                        navigationIcon = {
                            BackNavigationIcon(
                                modifier = Modifier.toolbarSearchItem(searchState),
                                onClick = { navigator.pop() },
                            )
                        },
                        actions = {
                            IconButton(
                                modifier = Modifier.toolbarSearchEntry(searchState),
                                onClick = { searchState.expand() },
                            ) {
                                Icon(imageVector = COUIIcons.Basic.Search, contentDescription = "Search")
                            }
                        },
                    )
                }
                ToolbarFloatingSearchBar(
                    state = searchState,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                ) {
                    SearchBar(
                        modifier = Modifier.fillMaxWidth(),
                        inputField = {
                            InputField(
                                query = query,
                                onQueryChange = { query = it },
                                onSearch = {},
                                expanded = searchState.expanded,
                                onExpandedChange = { if (it) searchState.expand() else searchState.collapse() },
                                label = "Search",
                            )
                        },
                        outsideEndAction = {
                            CancelButton(
                                onClick = {
                                    query = ""
                                    searchState.collapse()
                                },
                            )
                        },
                        expanded = searchState.expanded,
                        onExpandedChange = { if (it) searchState.expand() else searchState.collapse() },
                    ) {}
                }
            }
        },
    ) { innerPadding ->
        val contentPadding = pageContentPadding(innerPadding, padding, isWideScreen)
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.pageScrollModifiers(
                appState.enableScrollEndHaptic,
                appState.showTopAppBar,
                topAppBarScrollBehavior,
            ),
            contentPadding = contentPadding,
        ) {
            items(results.size, key = { results[it] }) { index ->
                BasicComponent(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = results[index],
                    onClick = { query = results[index] },
                )
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}
