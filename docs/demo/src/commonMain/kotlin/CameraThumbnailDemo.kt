// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.CameraThumbnail
import io.github.suqi8.coui.kmp.basic.CameraThumbnailTransition

@Composable
fun CameraThumbnailDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        val photos = remember {
            listOf(
                BrushPainter(Brush.linearGradient(listOf(Color(0xFFFFB36B), Color(0xFF7B3FA0)))),
                BrushPainter(Brush.verticalGradient(listOf(Color(0xFF8FD3F4), Color(0xFF1E5D3A)))),
                BrushPainter(Brush.radialGradient(listOf(Color(0xFFFFF1C1), Color(0xFFD9534F)))),
                BrushPainter(Brush.linearGradient(listOf(Color(0xFF2B2E4A), Color(0xFF53C6D9)))),
            )
        }
        var shot by remember { mutableIntStateOf(-1) }
        Row(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                // The camera's bottom bar sits on the black viewfinder.
                .background(Color.Black)
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(48.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // No photo yet shows the placeholder; each shutter press grows the new photo in.
            CameraThumbnail(
                image = if (shot < 0) null else photos[shot % photos.size],
                onClick = {},
                transition = CameraThumbnailTransition.ScaleIn,
            )
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .border(4.dp, Color.White, CircleShape)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { shot++ },
            )
        }
    }
}
