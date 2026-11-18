// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.CameraCountdown
import io.github.suqi8.coui.kmp.basic.CameraCountdownDefaults
import io.github.suqi8.coui.kmp.basic.CameraCountdownRing
import io.github.suqi8.coui.kmp.basic.CameraExposureCountdown

@Composable
fun CameraCountdownDemo() {
    // One repeating 10s cycle drives every countdown on the page.
    var remaining by remember { mutableLongStateOf(10_000L) }
    LaunchedEffect(Unit) {
        while (true) {
            val start = withFrameMillis { it }
            remaining = 10_000L
            while (remaining > 0L) {
                remaining = (10_000L - (withFrameMillis { it } - start)).coerceAtLeast(0L)
            }
        }
    }
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
                // A dark stand-in for the camera viewfinder the countdowns sit on.
                .background(Brush.verticalGradient(listOf(Color(0xFF5A4636), Color(0xFF1E1A17))))
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CameraCountdown(remainingMillis = remaining)
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CameraCountdown(remainingMillis = remaining, compact = true)
                CameraCountdownRing(
                    progress = 1f - remaining / 10_000f,
                    modifier = Modifier.size(CameraCountdownDefaults.RingSize),
                    animated = false,
                )
                CameraExposureCountdown(remainingMillis = remaining)
            }
        }
    }
}
