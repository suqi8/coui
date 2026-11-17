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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.ColorSwatchPicker
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme

private val swatchNames = listOf("Blue", "Green", "Orange", "Red", "Purple")

@Composable
fun ColorSwatchPickerDemo() {
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
            // Sample data colors for the swatches.
            val colors = remember {
                listOf(
                    Color(0xFF0066FF),
                    Color(0xFF24B232),
                    Color(0xFFF08222),
                    Color(0xFFDB382C),
                    Color(0xFF8A4FFF),
                )
            }
            var selectedIndex by remember { mutableIntStateOf(0) }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ColorSwatchPicker(
                        colors = colors,
                        selectedIndex = selectedIndex,
                        onSwatchSelected = { selectedIndex = it },
                    )
                    Text(
                        text = "Selected: ${swatchNames.getOrElse(selectedIndex) { "None" }}",
                        color = COUITheme.colorScheme.labelSecondary,
                        style = COUITheme.textStyles.bodyM,
                    )
                }
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ColorSwatchPicker(
                        colors = colors,
                        selectedIndex = selectedIndex,
                        onSwatchSelected = { selectedIndex = it },
                        enabled = false,
                    )
                    Text(
                        text = "Disabled",
                        color = COUITheme.colorScheme.labelSecondary,
                        style = COUITheme.textStyles.bodyM,
                    )
                }
            }
        }
    }
}
