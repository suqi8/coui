// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.AlphabetIndexBar
import io.github.suqi8.coui.kmp.basic.AlphabetIndexBarDefaults
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme

private val IndexDemoAvailableKeys = setOf(AlphabetIndexBarDefaults.CollectKey, "A", "B", "C", "D", "F", "G", "J", "L", "M", "S", "T", "W", "Z", "#")

fun LazyListScope.alphabetIndexBarSection() {
    item(key = "alphabetIndexBar") {
        SmallTitle(text = "AlphabetIndexBar")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            Row(Modifier.fillMaxWidth()) {
                AlphabetIndexBarDemo(
                    label = "Full height",
                    barHeight = 480.dp,
                    availableKeys = IndexDemoAvailableKeys,
                    modifier = Modifier.weight(1f),
                )
                AlphabetIndexBarDemo(
                    label = "Compressed",
                    barHeight = 260.dp,
                    availableKeys = null,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun AlphabetIndexBarDemo(
    label: String,
    barHeight: Dp,
    availableKeys: Set<String>?,
    modifier: Modifier = Modifier,
) {
    var selected by remember { mutableStateOf("-") }
    // The bubble and its shadow reach past the bar's top and bottom rows; the vertical padding
    // keeps them inside the clipping Card.
    Box(
        modifier
            .padding(vertical = 32.dp)
            .height(barHeight),
    ) {
        Text(
            text = "$label\nSelected: $selected",
            color = COUITheme.colorScheme.onSurfaceSecondary,
            style = COUITheme.textStyles.body2,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp),
        )
        AlphabetIndexBar(
            onKeySelected = { selected = it },
            availableKeys = availableKeys,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(barHeight),
        )
    }
}
