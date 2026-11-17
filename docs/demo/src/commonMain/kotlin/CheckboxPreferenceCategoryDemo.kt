// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.preference.CheckboxPreference
import io.github.suqi8.coui.kmp.preference.CheckboxPreferenceCategory

@Composable
fun CheckboxPreferenceCategoryDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(vertical = 16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val options = remember { listOf("Photos", "Contacts", "Messages") }
            val checked = remember { mutableStateListOf(true, false, false) }
            val categoryState = when (checked.count { it }) {
                0 -> ToggleableState.Off
                checked.size -> ToggleableState.On
                else -> ToggleableState.Indeterminate
            }
            CheckboxPreferenceCategory(
                title = "Items to migrate",
                state = categoryState,
                onStateChange = { newState ->
                    val value = newState == ToggleableState.On
                    for (i in checked.indices) checked[i] = value
                },
            )
            Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                CardListGroup {
                    options.forEachIndexed { index, option ->
                        item(key = option) {
                            CheckboxPreference(
                                title = option,
                                checked = checked[index],
                                onCheckedChange = { checked[index] = it },
                            )
                        }
                    }
                }
            }
        }
    }
}
