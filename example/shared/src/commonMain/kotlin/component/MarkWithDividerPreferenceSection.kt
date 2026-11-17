// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.preference.MarkWithDividerPreference

fun LazyListScope.markWithDividerPreferenceSection() {
    item(key = "markWithDividerPreference") {
        val networks = remember { listOf("Home-5G", "Office", "Guest") }
        val selected = remember { mutableIntStateOf(0) }
        // The last opened detail page, shown in the summary so main-area taps have visible feedback.
        val opened = remember { mutableStateOf<String?>(null) }

        SmallTitle(text = "MarkWithDividerPreference")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            CardListGroup {
                networks.forEachIndexed { index, name ->
                    item(key = name) {
                        MarkWithDividerPreference(
                            checked = selected.intValue == index,
                            onCheckedChange = { if (it) selected.intValue = index },
                            title = name,
                            summary = if (opened.value == name) "Opened the $name page" else "Tap the row for details",
                            onClick = { opened.value = name },
                        )
                    }
                }
                item(key = "disabled") {
                    MarkWithDividerPreference(
                        checked = true,
                        onCheckedChange = {},
                        title = "Disabled",
                        summary = "Both parts are disabled",
                        onClick = {},
                        enabled = false,
                    )
                }
            }
        }
    }
}
