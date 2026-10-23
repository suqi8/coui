// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.basic.RadioButton
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.preference.RadioButtonPreference

fun LazyListScope.radioButtonSection() {
    item(key = "radioButton") {
        SmallTitle(text = "RadioButton")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            RadioButtonRow()
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            SuperRadioButtonDemo()
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            RadioButtonPreference(
                title = "Disabled RadioButton",
                selected = true,
                enabled = false,
                onClick = {},
            )
        }
    }
}

@Composable
private fun RadioButtonRow() {
    var selectedIndex by remember { mutableIntStateOf(0) }

    // Standalone radio buttons show their own circular press highlight.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        RadioButton(
            selected = selectedIndex == 0,
            onClick = { selectedIndex = 0 },
        )
        RadioButton(
            selected = selectedIndex == 1,
            onClick = { selectedIndex = 1 },
        )
        RadioButton(
            selected = false,
            onClick = null,
            enabled = false,
        )
        RadioButton(
            selected = true,
            onClick = null,
            enabled = false,
        )
    }
}

@Composable
private fun SuperRadioButtonDemo() {
    var selectedIndex by remember { mutableIntStateOf(0) }

    Column {
        RadioButtonPreference(
            title = "Option A",
            summary = "Selected: ${selectedIndex == 0}",
            selected = selectedIndex == 0,
            onClick = { selectedIndex = 0 },
        )
        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        RadioButtonPreference(
            title = "Option B",
            summary = "Selected: ${selectedIndex == 1}",
            selected = selectedIndex == 1,
            onClick = { selectedIndex = 1 },
        )
        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        RadioButtonPreference(
            title = "Option C",
            summary = "Selected: ${selectedIndex == 2}",
            selected = selectedIndex == 2,
            onClick = { selectedIndex = 2 },
        )
    }
}
