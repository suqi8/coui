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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.IntentSlider
import io.github.suqi8.coui.kmp.basic.IntentSliderSize
import io.github.suqi8.coui.kmp.basic.TextButton

@Composable
fun IntentSliderDemo() {
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
            var value1 by remember { mutableFloatStateOf(0.3f) }
            var value2 by remember { mutableFloatStateOf(0.6f) }
            var value3 by remember { mutableFloatStateOf(0.4f) }
            var paused by remember { mutableStateOf(true) }
            IntentSlider(
                value = value1,
                onValueChange = { value1 = it },
                secondaryValue = 0.5f,
            )
            IntentSlider(
                value = value2,
                onValueChange = { value2 = it },
                showThumb = true,
                paused = paused,
                size = IntentSliderSize.Large,
            )
            TextButton(
                text = if (paused) "Play" else "Pause",
                onClick = { paused = !paused },
            )
            // The fixed palette is meant for media content; without a backdrop provider it keeps
            // its translucent white background fill.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(listOf(Color(0xFF3A6073), Color(0xFF16222A))),
                        shape = RoundedCornerShape(16.dp),
                    )
                    .padding(vertical = 16.dp),
            ) {
                IntentSlider(
                    value = value3,
                    onValueChange = { value3 = it },
                    followSystemTheme = false,
                    showThumb = true,
                    secondaryValue = 0.7f,
                )
            }
            IntentSlider(
                value = 0.5f,
                onValueChange = {},
                enabled = false,
            )
        }
    }
}
