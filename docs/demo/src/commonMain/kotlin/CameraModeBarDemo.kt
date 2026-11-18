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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.CameraModeBar
import io.github.suqi8.coui.kmp.basic.CameraModeTitle

@Composable
fun CameraModeBarDemo() {
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
                // A dark stand-in for the camera viewfinder the strip sits on.
                .background(Brush.verticalGradient(listOf(Color(0xFF3B4A5A), Color(0xFF101418))))
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val modes = remember { listOf("Night", "Portrait", "Photo", "Video", "Pro", "Pano", "Slow-mo", "More") }
            var index by remember { mutableIntStateOf(2) }
            var titleOpen by remember { mutableStateOf(false) }
            if (titleOpen) {
                CameraModeTitle(
                    onClick = {
                        titleOpen = false
                        index = 2
                    },
                    title = "Slow-mo",
                )
            } else {
                CameraModeBar(
                    modes = modes,
                    selectedIndex = index,
                    onSelectedIndexChange = {
                        index = it
                        if (it == modes.lastIndex) titleOpen = true
                    },
                )
            }
        }
    }
}
