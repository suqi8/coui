// Copyright 2026, compose-coui-ui contributors
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.preference.MarkLocation
import io.github.suqi8.coui.kmp.preference.MarkPreference

@Composable
fun MarkPreferenceDemo() {
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
            val options = listOf("Every day", "Workdays only", "Never")
            var selected by remember { mutableIntStateOf(0) }
            var headSelected by remember { mutableIntStateOf(0) }

            Card {
                CardListGroup {
                    options.forEachIndexed { index, option ->
                        item {
                            MarkPreference(
                                title = option,
                                checked = selected == index,
                                onClick = { selected = index },
                            )
                        }
                    }
                }
            }
            Card {
                CardListGroup {
                    item {
                        MarkPreference(
                            title = "Head mark",
                            summary = "MarkLocation.Start",
                            checked = headSelected == 0,
                            onClick = { headSelected = 0 },
                            markLocation = MarkLocation.Start,
                        )
                    }
                    item {
                        MarkPreference(
                            title = "Another head mark",
                            checked = headSelected == 1,
                            onClick = { headSelected = 1 },
                            markLocation = MarkLocation.Start,
                        )
                    }
                    item {
                        MarkPreference(
                            title = "Disabled",
                            checked = true,
                            onClick = {},
                            enabled = false,
                        )
                    }
                }
            }
        }
    }
}
