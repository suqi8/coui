// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package catalog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.CancelButton
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.EmptyState
import io.github.suqi8.coui.kmp.basic.InputField
import io.github.suqi8.coui.kmp.basic.SearchBar
import io.github.suqi8.coui.kmp.preference.PreferenceCategory
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_search_empty
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_search_empty_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_search_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_search_results
import io.github.suqi8.coui.kmp.shared.generated.resources.common_cancel
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/**
 * One searchable demo, with its title, category name and search aliases resolved in the current
 * language.
 */
@Immutable
class DemoSearchEntry(
    val demo: Demo,
    val title: String,
    val categoryTitle: String,
    val searchTerms: List<String>,
) {
    /** Whether the demo matches [query] by name, API name, id, category, keyword or alias. */
    fun matches(query: String): Boolean = title.contains(query, ignoreCase = true) ||
        demo.apiName.contains(query, ignoreCase = true) ||
        demo.id.replace('_', ' ').contains(query, ignoreCase = true) ||
        categoryTitle.contains(query, ignoreCase = true) ||
        demo.keywords.any { it.contains(query, ignoreCase = true) } ||
        searchTerms.any { it.contains(query, ignoreCase = true) }
}

/**
 * The search index of every demo, resolved once with `getString` rather than one
 * `stringResource` per demo in composition. Empty until the strings are loaded.
 */
@Composable
fun rememberDemoSearchIndex(): List<DemoSearchEntry> {
    val index by produceState(initialValue = emptyList<DemoSearchEntry>()) {
        value = DemoCatalog.all.map { demo ->
            DemoSearchEntry(
                demo = demo,
                title = getString(demo.title),
                categoryTitle = getString(demo.category.title),
                searchTerms = demo.searchTerms?.let { res ->
                    getString(res).split(',').map { it.trim() }.filter { it.isNotEmpty() }
                }.orEmpty(),
            )
        }
    }
    return index
}

/**
 * Returns the demos matching [query], or an empty list for a blank query.
 */
@Composable
fun rememberDemoSearchResults(query: String, index: List<DemoSearchEntry>): List<DemoSearchEntry> {
    val trimmed = query.trim()
    return remember(trimmed, index) {
        if (trimmed.isEmpty()) emptyList() else index.filter { it.matches(trimmed) }
    }
}

/**
 * The expanded search row that replaces the home top bar while searching: the search field,
 * focused on entry, and a cancel button, pinned below the status bar like the ColorOS Settings
 * search.
 *
 * @param query The search text.
 * @param onQueryChange Called with the new search text.
 * @param onCancel Leaves the search.
 * @param modifier The modifier for the row.
 */
@Composable
fun CatalogSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(focusRequester) { focusRequester.requestFocus() }
    SearchBar(
        modifier = modifier.fillMaxWidth().statusBarsPadding().padding(vertical = 6.dp),
        inputField = {
            InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = {},
                expanded = true,
                onExpandedChange = {},
                modifier = Modifier.focusRequester(focusRequester),
                label = stringResource(Res.string.catalog_search_hint),
            )
        },
        outsideEndAction = {
            CancelButton(onClick = onCancel, text = stringResource(Res.string.common_cancel))
        },
        expanded = true,
        onExpandedChange = { if (!it) onCancel() },
    ) {}
}

/**
 * Adds the search results for [query]: a titled card of matching demos, or an empty state. Adds
 * nothing for a blank query.
 */
fun LazyListScope.demoSearchResults(
    query: String,
    results: List<DemoSearchEntry>,
    onOpen: (Demo) -> Unit,
) {
    if (query.isBlank()) return
    if (results.isEmpty()) {
        item(key = "search_empty") {
            EmptyState(
                title = stringResource(Res.string.catalog_search_empty),
                modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                subtitle = stringResource(Res.string.catalog_search_empty_hint),
            )
        }
        return
    }
    item(key = "search_results") {
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
