// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.CameraGridOverlay
import io.github.suqi8.coui.kmp.basic.CameraGridType
import io.github.suqi8.coui.kmp.basic.CameraLevelIndicator
import io.github.suqi8.coui.kmp.basic.Slider
import io.github.suqi8.coui.kmp.basic.rememberCameraLevelState

@Composable
fun CameraLevelIndicatorDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 320.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            var roll by remember { mutableFloatStateOf(4f) }
            val levelState = rememberCameraLevelState()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .clip(RoundedCornerShape(16.dp))
                    // A dark stand-in for the camera viewfinder.
                    .background(Brush.verticalGradient(listOf(Color(0xFF5A4636), Color(0xFF1E1A17)))),
            ) {
                CameraGridOverlay(
                    type = CameraGridType.Grid,
                    levelState = levelState,
                    modifier = Modifier.matchParentSize(),
                )
                CameraLevelIndicator(
                    rollDegrees = roll,
                    pitchDegrees = 0f,
                    state = levelState,
                    modifier = Modifier.matchParentSize(),
                )
            }
            // Simulates the device roll.
            Slider(
                value = roll,
                onValueChange = { roll = it },
                valueRange = -15f..15f,
            )
        }
    }
}
