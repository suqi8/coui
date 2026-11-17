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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.preference.RangeSliderPreference
import kotlin.math.roundToInt

@Composable
fun RangeSliderPreferenceDemo() {
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
            var priceRange by remember { mutableStateOf(100f..500f) }
            var percentRange by remember { mutableStateOf(20f..80f) }

            Card {
                CardListGroup {
                    item {
                        RangeSliderPreference(
                            value = priceRange,
                            onValueChange = { priceRange = it },
                            title = "Price range",
                            valueText = "${priceRange.start.roundToInt()} - ${priceRange.endInclusive.roundToInt()}",
                            valueRange = 0f..1000f,
                        )
                    }
                    item {
                        RangeSliderPreference(
                            value = percentRange,
                            onValueChange = { percentRange = it },
                            title = "Key points",
                            summary = "${percentRange.start.roundToInt()}% - ${percentRange.endInclusive.roundToInt()}%",
                            valueRange = 0f..100f,
                            showKeyPoints = true,
                            keyPoints = listOf(0f, 20f, 40f, 60f, 80f, 100f),
                            magnetThreshold = 0.03f,
                        )
                    }
                    item {
                        RangeSliderPreference(
                            value = 0.3f..0.7f,
                            onValueChange = {},
                            title = "Disabled",
                            enabled = false,
                        )
                    }
                }
            }
        }
    }
}
