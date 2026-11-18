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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.CameraLensBar
import io.github.suqi8.coui.kmp.basic.CameraLensBarDefaults

@Composable
fun CameraLensBarDemo() {
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
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                // A stand-in for the camera viewfinder the bar sits on.
                .background(Brush.verticalGradient(listOf(Color(0xFF6B7F8E), Color(0xFF2A2F33))))
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val three = remember { listOf(0.6f, 1f, 3f).map(CameraLensBarDefaults::formatZoomRatio) }
            var lens by remember { mutableIntStateOf(1) }
            CameraLensBar(
                labels = three,
                selectedIndex = lens,
                onSelectedIndexChange = { lens = it },
            )
            val four = remember { listOf(0.6f, 1f, 3f, 6f).map(CameraLensBarDefaults::formatZoomRatio) }
            var lightLens by remember { mutableIntStateOf(2) }
            CameraLensBar(
                labels = four,
                selectedIndex = lightLens,
                onSelectedIndexChange = { lightLens = it },
                colors = CameraLensBarDefaults.lightBackgroundColors(),
            )
        }
    }
}
