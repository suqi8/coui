// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.InputField
import io.github.suqi8.coui.kmp.basic.SearchBar
import io.github.suqi8.coui.kmp.basic.TabRow
import io.github.suqi8.coui.kmp.basic.TabWithSearchLayout
import io.github.suqi8.coui.kmp.basic.TabWithSearchLayoutDefaults

@Composable
fun TabWithSearchLayoutDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.TopCenter,
    ) {
        val tabs = remember { listOf("All", "Photos", "Videos", "Albums") }
        var selected by remember { mutableIntStateOf(0) }
        var query by remember { mutableStateOf("") }
        // Narrower than 600dp the search bar sits above the tabs; wider, beside them.
        TabWithSearchLayout(
            tabs = {
                TabRow(
                    tabs = tabs,
                    selectedTabIndex = selected,
                    onTabSelected = { selected = it },
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        ) {
            SearchBar(
                inputField = {
                    InputField(
                        query = query,
                        onQueryChange = { query = it },
                        onSearch = {},
                        expanded = false,
                        onExpandedChange = {},
                        label = "Search",
                    )
                },
                onExpandedChange = {},
                insideMargin = TabWithSearchLayoutDefaults.SearchBarInsideMargin,
            ) {}
        }
    }
}
