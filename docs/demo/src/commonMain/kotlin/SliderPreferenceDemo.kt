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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.preference.SliderPreference
import kotlin.math.roundToInt

@Composable
fun SliderPreferenceDemo() {
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
            var volume by remember { mutableFloatStateOf(0.6f) }
            var fontSize by remember { mutableFloatStateOf(16f) }
            var disabledValue by remember { mutableFloatStateOf(0.4f) }

            Card {
                CardListGroup {
                    item {
                        SliderPreference(
                            value = volume,
                            onValueChange = { volume = it },
                            title = "Volume",
                            valueText = "${(volume * 100).roundToInt()}%",
                        )
                    }
                    item {
                        SliderPreference(
                            value = fontSize,
                            onValueChange = { fontSize = it },
                            title = "Font size",
                            summary = "Current: ${fontSize.roundToInt()}sp",
                            valueRange = 12f..20f,
                            steps = 3,
                        )
                    }
                    item {
                        SliderPreference(
                            value = disabledValue,
                            onValueChange = { disabledValue = it },
                            title = "Disabled",
                            summary = "This slider is currently unavailable",
                            enabled = false,
                        )
                    }
                }
            }
        }
    }
}
