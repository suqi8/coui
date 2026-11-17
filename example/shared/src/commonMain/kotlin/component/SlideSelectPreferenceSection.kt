// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.preference.SlideSelectPreference

fun LazyListScope.slideSelectPreferenceSection() {
    item(key = "slideSelectPreference") {
        val fontSizes = remember { listOf("Standard", "Large", "Extra large") }
        val fontSize = remember { mutableIntStateOf(0) }
        val timeouts = remember { listOf("30 seconds", "1 minute", "5 minutes", "Never") }
        val timeout = remember { mutableIntStateOf(1) }

        SmallTitle(text = "SlideSelectPreference")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            CardListGroup {
                item {
                    SlideSelectPreference(
                        title = "Font size",
                        selectionText = fontSizes[fontSize.intValue],
                        summary = "Tap to switch to the next value",
                        onClick = { fontSize.intValue = (fontSize.intValue + 1) % fontSizes.size },
                    )
                }
                item {
                    SlideSelectPreference(
                        title = "Screen timeout",
                        selectionText = timeouts[timeout.intValue],
                        onClick = { timeout.intValue = (timeout.intValue + 1) % timeouts.size },
                    )
                }
                item {
                    SlideSelectPreference(
                        title = "No selection text",
                        onClick = {},
                    )
                }
                item {
                    SlideSelectPreference(
                        title = "Disabled",
                        selectionText = "Standard",
                        enabled = false,
                    )
                }
            }
        }
    }
}
