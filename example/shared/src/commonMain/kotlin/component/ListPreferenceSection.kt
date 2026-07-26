// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.preference.ListPreference
import io.github.suqi8.coui.kmp.preference.ListPreferenceEntry
import io.github.suqi8.coui.kmp.preference.MultiSelectListPreference

fun LazyListScope.listPreferenceSection() {
    item(key = "listPreference") {
        SmallTitle(text = "ListPreference")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            ListPreferenceDemo()
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            MultiSelectListPreferenceDemo()
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            ListPreference(
                entries = remember { listOf(ListPreferenceEntry("Unavailable")) },
                selectedIndex = 0,
                onSelectedIndexChange = {},
                title = "Disabled ListPreference",
                cancelButtonText = "Cancel",
                enabled = false,
            )
        }
    }
}

@Composable
private fun ListPreferenceDemo() {
    val entries = remember {
        listOf(
            ListPreferenceEntry("Light"),
            ListPreferenceEntry("Dark"),
            ListPreferenceEntry("Follow system", summary = "Switch with the system dark mode"),
        )
    }
    var selectedIndex by remember { mutableIntStateOf(2) }

    ListPreference(
        entries = entries,
        selectedIndex = selectedIndex,
        onSelectedIndexChange = { selectedIndex = it },
        title = "Theme",
        cancelButtonText = "Cancel",
        summary = "Single choice bottom panel",
    )
}

@Composable
private fun MultiSelectListPreferenceDemo() {
    val entries = remember {
        listOf(
            ListPreferenceEntry("Photos"),
            ListPreferenceEntry("Documents"),
            ListPreferenceEntry("Contacts", summary = "Contacts and call logs"),
            ListPreferenceEntry("System settings", enabled = false),
        )
    }
    var selectedIndices by remember { mutableStateOf(setOf(0, 1)) }

    MultiSelectListPreference(
        entries = entries,
        selectedIndices = selectedIndices,
        onSelectedIndicesChange = { selectedIndices = it },
        title = "Sync items",
        confirmButtonText = "Confirm",
        cancelButtonText = "Cancel",
        summary = "Multi choice bottom panel",
    )
}
