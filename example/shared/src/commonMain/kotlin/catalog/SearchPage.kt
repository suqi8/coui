// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package catalog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import app.LocalAppSettings
import app.LocalNavigator
import io.github.suqi8.coui.kmp.basic.CancelButton
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.EmptyState
import io.github.suqi8.coui.kmp.basic.InputField
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.SearchBar
import io.github.suqi8.coui.kmp.preference.PreferenceCategory
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_search_empty
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_search_empty_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_search_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_search_results
import io.github.suqi8.coui.kmp.shared.generated.resources.common_cancel
import io.github.suqi8.coui.kmp.utils.overScrollVertical
import io.github.suqi8.coui.kmp.utils.scrollEndHaptic
import navigation3.Route
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/**
 * One searchable demo, with its title and category name resolved in the current language.
 */
@Immutable
private class SearchEntry(
    val demo: Demo,
    val title: String,
    val categoryTitle: String,
) {
    fun matches(query: String): Boolean = title.contains(query, ignoreCase = true) ||
        demo.apiName.contains(query, ignoreCase = true) ||
        demo.id.replace('_', ' ').contains(query, ignoreCase = true) ||
        categoryTitle.contains(query, ignoreCase = true) ||
        demo.keywords.any { it.contains(query, ignoreCase = true) }
}

/**
 * The component search page: an expanded search bar with a cancel button, opened from the
 * components home. Matches localized names, API names, categories and keywords.
 */
@Composable
fun SearchPage(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.current
    var query by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    // Resolved once per page with getString, not one stringResource per demo in composition.
    val index by produceState(initialValue = emptyList<SearchEntry>()) {
        value = DemoCatalog.all.map { demo ->
            SearchEntry(demo, getString(demo.title), getString(demo.category.title))
        }
    }
    val trimmed = query.trim()
    val results = remember(trimmed, index) {
        if (trimmed.isEmpty()) emptyList() else index.filter { it.matches(trimmed) }
    }
    LaunchedEffect(focusRequester) { focusRequester.requestFocus() }

    Scaffold(
        modifier = modifier,
        topBar = {
            SearchBar(
                modifier = Modifier.statusBarsPadding().padding(top = 8.dp),
                inputField = {
                    InputField(
                        query = query,
                        onQueryChange = { query = it },
                        onSearch = {},
                        expanded = true,
                        onExpandedChange = {},
                        modifier = Modifier.focusRequester(focusRequester),
                        label = stringResource(Res.string.catalog_search_hint),
                    )
                },
                outsideEndAction = {
                    CancelButton(onClick = { navigator.pop() }, text = stringResource(Res.string.common_cancel))
                },
                expanded = true,
                onExpandedChange = { if (!it) navigator.pop() },
            ) {}
        },
    ) { innerPadding ->
        when {
            trimmed.isEmpty() -> Unit

            results.isEmpty() -> Column(Modifier.fillMaxSize().padding(innerPadding)) {
                EmptyState(
                    title = stringResource(Res.string.catalog_search_empty),
                    modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                    subtitle = stringResource(Res.string.catalog_search_empty_hint),
                )
            }

            else -> SearchResults(
                results = results,
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 32.dp,
                ),
                onOpen = { navigator.push(Route.Demo(it.id)) },
            )
        }
    }
}

@Composable
private fun SearchResults(
    results: List<SearchEntry>,
    contentPadding: PaddingValues,
    onOpen: (Demo) -> Unit,
) {
    val haptic = LocalAppSettings.current.enableScrollEndHaptic
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .then(if (haptic) Modifier.scrollEndHaptic() else Modifier)
            .overScrollVertical(),
        contentPadding = contentPadding,
    ) {
        item(key = "results") {
            PreferenceCategory(title = stringResource(Res.string.catalog_search_results, results.size))
            Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                CardListGroup {
                    results.forEach { entry ->
                        item(key = entry.demo.id) {
                            DemoRow(demo = entry.demo, onClick = { onOpen(entry.demo) })
                        }
                    }
                }
            }
        }
    }
}
