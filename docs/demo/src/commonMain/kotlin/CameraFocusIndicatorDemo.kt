// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.CameraFocusIndicator
import io.github.suqi8.coui.kmp.basic.rememberCameraFocusIndicatorState

@Composable
fun CameraFocusIndicatorDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        val state = rememberCameraFocusIndicatorState()
        Box(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                // A dark stand-in for the camera preview; tap it to focus, drag up or down for exposure.
                .background(Brush.verticalGradient(listOf(Color(0xFF5A4636), Color(0xFF1E1A17))))
                .onSizeChanged { size ->
                    if (state.focusPosition == null) state.focusAt(Offset(size.width / 2f, size.height / 2f))
                },
        ) {
            CameraFocusIndicator(
                state = state,
                modifier = Modifier.fillMaxSize(),
                autoHide = false,
            )
        }
    }
}
