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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.CameraHint
import io.github.suqi8.coui.kmp.basic.CameraHintBackground
import io.github.suqi8.coui.kmp.basic.CameraHintDefaults
import io.github.suqi8.coui.kmp.basic.CameraOutlinedText

@Composable
fun CameraHintDemo() {
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
                // A bright stand-in for the camera viewfinder the hints sit on.
                .background(Brush.verticalGradient(listOf(Color(0xFFB9D7EA), Color(0xFF6B8E5A))))
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CameraHint(text = "Hold the shutter to shoot a burst")
            CameraHint(text = "Keep the phone steady", background = CameraHintBackground.Light)
            CameraHint(text = "Point at the subject", background = CameraHintBackground.None)
            CameraOutlinedText(text = "Slide to switch filters")
            CameraOutlinedText(
                text = "Light and shadow, retold",
                brush = Brush.horizontalGradient(CameraHintDefaults.GuideGradientColors),
                textSize = 20.dp,
                fontWeight = FontWeight.Bold,
                strokeWidth = 0.dp,
            )
        }
    }
}
