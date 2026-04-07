// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.BasicComponent
import com.suqi8.coui.kmp.basic.BasicComponentDefaults
import com.suqi8.coui.kmp.basic.SearchBar

@Composable
fun SearchBarDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xfff77062), Color(0xfffe5196)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var searchValue by remember { mutableStateOf("") }
            var active by remember { mutableStateOf(false) }
            val suggestions = remember {
                listOf("Beijing", "Tokyo", "Shanghai", "Singapore", "Shenzhen", "Seoul")
            }
            val filteredSuggestions = remember(searchValue) {
                suggestions.filter { it.contains(searchValue, ignoreCase = true) }
            }

            SearchBar(
                query = searchValue,
                onQueryChange = { searchValue = it },
                onSearch = { active = false },
                onCancel = {
                    active = false
                    searchValue = ""
                },
                active = active,
                onActiveChange = { active = it },
                hintTexts = listOf("Search city", "Search country", "Search region")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredSuggestions.forEach { resultText ->
                        BasicComponent(
                            title = resultText,
                            titleColor = BasicComponentDefaults.titleColor(Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                searchValue = resultText
                                active = false
                            }
                        )
                    }
                }
            }
        }
    }
}
