// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import io.github.suqi8.coui.kmp.basic.CameraShutterButton
import io.github.suqi8.coui.kmp.basic.CameraShutterMode
import io.github.suqi8.coui.kmp.basic.CameraShutterStyle
import io.github.suqi8.coui.kmp.basic.CameraSwitchButton
import io.github.suqi8.coui.kmp.basic.CameraSwitchButtonDefaults

@Composable
fun CameraShutterButtonDemo() {
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
                // A dark stand-in for the camera viewfinder the buttons sit on.
                .background(Brush.verticalGradient(listOf(Color(0xFF5A4636), Color(0xFF1E1A17))))
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // A video control bar: pause / resume while recording, the shutter, the camera switch.
            var recording by remember { mutableStateOf(false) }
            var paused by remember { mutableStateOf(false) }
            var turns by remember { mutableIntStateOf(0) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (recording) {
                    CameraSwitchButton(
                        onClick = { paused = !paused },
                        icon = if (paused) CameraSwitchButtonDefaults.ResumeIcon else CameraSwitchButtonDefaults.PauseIcon,
                        size = CameraSwitchButtonDefaults.ControlSize,
                    )
                } else {
                    Spacer(Modifier.size(CameraSwitchButtonDefaults.ControlSize))
                }
                CameraShutterButton(
                    onClick = {
                        recording = !recording
                        paused = false
                    },
                    mode = CameraShutterMode.Video,
                    recording = recording,
                )
                CameraSwitchButton(
                    onClick = { turns++ },
                    enabled = !recording,
                    iconRotation = turns * 90f,
                    size = CameraSwitchButtonDefaults.ControlSize,
                )
            }
            // The three looks in photo mode; tap one to switch all of them to video.
            var video by remember { mutableStateOf(false) }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CameraShutterStyle.entries.forEach { style ->
                    CameraShutterButton(
                        onClick = { video = !video },
                        mode = if (video) CameraShutterMode.Video else CameraShutterMode.Photo,
                        style = style,
                    )
                }
            }
        }
    }
}
