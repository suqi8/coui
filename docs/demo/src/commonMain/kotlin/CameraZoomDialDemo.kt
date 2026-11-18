// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.basic.CameraZoomDial
import io.github.suqi8.coui.kmp.basic.Text

@Composable
fun CameraZoomDialDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 400.dp)
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(16.dp))
                // A dark stand-in for the camera viewfinder the dial sits on.
                .background(Brush.verticalGradient(listOf(Color(0xFF5A4636), Color(0xFF1E1A17)))),
            contentAlignment = Alignment.BottomCenter,
        ) {
            var zoom by remember { mutableFloatStateOf(1f) }
            var expanded by remember { mutableStateOf(false) }
            CameraZoomDial(
                value = zoom,
                onValueChange = { zoom = it },
                expanded = expanded,
                onExpandedChange = { expanded = it },
            ) {
                // Long-press or drag these to unfold the dial.
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0x26000000))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    val lenses = listOf(0.6f, 1f, 2f, 3f)
                    val active = lenses.lastOrNull { zoom >= it - 0.001f } ?: lenses.first()
                    lenses.forEach { lens ->
                        val selected = lens == active
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (selected) Color(0xA6000000) else Color(0x26000000))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) { zoom = lens },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = if (selected) ratioText(zoom) + "×" else ratioText(lens),
                                color = if (selected) Color(0xFFFB822A) else Color.White,
                                fontSize = 11.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun ratioText(value: Float): String {
    val tenths = ((value + 1e-5f) * 10f).toInt()
    return if (tenths % 10 == 0) (tenths / 10).toString() else "${tenths / 10}.${tenths % 10}"
}
