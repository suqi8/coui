// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalScrollBarApi::class)

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import io.github.suqi8.coui.kmp.basic.InputField
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.SearchBar
import io.github.suqi8.coui.kmp.basic.ToolbarFloatingSearchBar
import io.github.suqi8.coui.kmp.basic.ToolbarSearchAnchor
import io.github.suqi8.coui.kmp.basic.rememberToolbarSearchState
import io.github.suqi8.coui.kmp.interfaces.ExperimentalScrollBarApi
import utils.AdaptiveTopAppBar
import utils.pageContentPadding
import utils.pageScrollModifiers

/**
 * Demonstrates a search bar that floats up into the toolbar when it expands, the way
 * `COUISearchBar.setAtFrontToolBar` links the two.
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
            ToolbarSearchAnchor(state = searchState) {
                AdaptiveTopAppBar(
                    title = "Toolbar search",
                    showTopAppBar = appState.showTopAppBar,
                    isWideScreen = isWideScreen,
                    scrollBehavior = topAppBarScrollBehavior,
                    navigationIcon = { BackNavigationIcon(onClick = { navigator.pop() }) },
                )
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
            item(key = "search") {
                ToolbarFloatingSearchBar(state = searchState) {
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
                    ) {
                        Column {
                            results.take(6).forEach { result ->
                                BasicComponent(
                                    title = result,
                                    onClick = {
                                        query = result
                                        searchState.collapse()
                                    },
                                )
                            }
                        }
                    }
                }
            }
            if (!searchState.expanded) {
                items(entries.size, key = { entries[it] }) { index ->
                    BasicComponent(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        title = entries[index],
                    )
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}
