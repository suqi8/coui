// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package catalog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.EmptyState
import io.github.suqi8.coui.kmp.basic.TopAppBarSearchBar
import io.github.suqi8.coui.kmp.basic.TopAppBarSearchState
import io.github.suqi8.coui.kmp.preference.PreferenceCategory
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_search_empty
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_search_empty_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_search_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_search_hint_dialog
import io.github.suqi8.coui.kmp.shared.generated.resources.catalog_search_hint_slider
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
 * The [TopAppBarSearchBar] of the components home, with its rotating hints, docked below the top bar title like the ColorOS
 * Settings search: tapping the capsule enters the search in place, where the capsule gains a
 * cancel button and the top bar lifts it to the top (see [TopAppBarSearchState]); cancel or back
 * leaves it again.
 *
 * @param query The search text.
 * @param onQueryChange Called with the new search text.
 * @param expanded Whether the search is active.
 * @param onExpandedChange Called to enter or leave the search.
 * @param modifier The modifier for the bar.
 */
@Composable
fun CatalogSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hints = listOf(
        stringResource(Res.string.catalog_search_hint),
        stringResource(Res.string.catalog_search_hint_slider),
        stringResource(Res.string.catalog_search_hint_dialog),
    )
    TopAppBarSearchBar(
        query = query,
        onQueryChange = onQueryChange,
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        cancelText = stringResource(Res.string.common_cancel),
        modifier = modifier,
        label = stringResource(Res.string.catalog_search_hint),
        hints = hints,
    )
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
