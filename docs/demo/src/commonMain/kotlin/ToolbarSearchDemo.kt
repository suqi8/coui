// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.CancelButton
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.InputField
import io.github.suqi8.coui.kmp.basic.SearchBar
import io.github.suqi8.coui.kmp.basic.SmallTopAppBar
import io.github.suqi8.coui.kmp.basic.ToolbarFloatingSearchBar
import io.github.suqi8.coui.kmp.basic.ToolbarSearchAnchor
import io.github.suqi8.coui.kmp.basic.rememberToolbarSearchState
import io.github.suqi8.coui.kmp.basic.toolbarSearchEntry
import io.github.suqi8.coui.kmp.basic.toolbarSearchItem
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Search
import io.github.suqi8.coui.kmp.icon.extended.Back
import io.github.suqi8.coui.kmp.icon.extended.More
import io.github.suqi8.coui.kmp.theme.COUITheme

private val SampleContacts = listOf(
    "Alice",
    "Bob",
    "Charlie",
    "Diana",
    "Ethan",
    "Fiona",
    "George",
    "Hannah",
    "Ivan",
    "Julia",
    "Kevin",
    "Laura",
)

@Composable
fun ToolbarSearchDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val searchState = rememberToolbarSearchState()
            var query by remember { mutableStateOf("") }
            val onExpandedChange: (Boolean) -> Unit = { if (it) searchState.expand() else searchState.collapse() }
            val titleColor = COUITheme.colorScheme.labelPrimary
            val shown = if (query.isBlank()) SampleContacts else SampleContacts.filter { it.contains(query, ignoreCase = true) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
            ) {
                Box {
                    ToolbarSearchAnchor(state = searchState) {
                        SmallTopAppBar(
                            title = "Contacts",
                            titleColor = titleColor.copy(alpha = titleColor.alpha * searchState.toolbarTitleAlpha),
                            defaultWindowInsetsPadding = false,
                            navigationIcon = {
                                IconButton(
                                    onClick = {},
                                    modifier = Modifier.toolbarSearchItem(searchState),
                                ) {
                                    Icon(COUIIcons.Back, contentDescription = "Back")
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = { searchState.expand() },
                                    modifier = Modifier.toolbarSearchEntry(searchState),
                                ) {
                                    Icon(COUIIcons.Basic.Search, contentDescription = "Search")
                                }
                                IconButton(
                                    onClick = {},
                                    modifier = Modifier.toolbarSearchItem(searchState),
                                ) {
                                    Icon(COUIIcons.More, contentDescription = "More")
                                }
                            },
                        )
                    }
                    ToolbarFloatingSearchBar(state = searchState) {
                        SearchBar(
                            inputField = {
                                InputField(
                                    query = query,
                                    onQueryChange = { query = it },
                                    onSearch = {},
                                    expanded = searchState.expanded,
                                    onExpandedChange = onExpandedChange,
                                    label = "Search contacts",
                                )
                            },
                            onExpandedChange = onExpandedChange,
                            modifier = Modifier.fillMaxWidth(),
                            expanded = searchState.expanded,
                            outsideEndAction = {
                                CancelButton(
                                    onClick = {
                                        query = ""
                                        searchState.collapse()
                                    },
                                )
                            },
                        ) {}
                    }
                }
                LazyColumn(modifier = Modifier.weight(1f)) {
                    if (shown.isEmpty()) {
                        item {
                            BasicComponent(title = "No results")
                        }
                    } else {
                        items(shown) { name ->
                            BasicComponent(
                                title = name,
                                onClick = { query = name },
                            )
                        }
                    }
                }
            }
        }
    }
}
