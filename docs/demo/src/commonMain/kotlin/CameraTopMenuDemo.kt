// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.CameraTopMenu
import io.github.suqi8.coui.kmp.basic.CameraTopMenuItem
import io.github.suqi8.coui.kmp.basic.CameraTopMenuOptionRow

@Composable
fun CameraTopMenuDemo() {
    var expanded by remember { mutableStateOf<String?>(null) }
    var ratio by remember { mutableIntStateOf(1) }
    var size by remember { mutableIntStateOf(1) }
    var fps by remember { mutableIntStateOf(0) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
                // A dark stand-in for the camera viewfinder the bar sits on.
                .background(Brush.verticalGradient(listOf(Color(0xFF5A4636), Color(0xFF1E1A17))))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { expanded = null },
                )
                .padding(top = 16.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            CameraTopMenu(
                onItemClick = { key -> expanded = if (expanded == key) null else key },
                onOptionSelected = { itemKey, rowKey, index ->
                    when {
                        itemKey == "ratio" -> {
                            ratio = index
                            expanded = null
                        }

                        rowKey == "size" -> size = index

                        else -> fps = index
                    }
                },
                startItems = listOf(
                    CameraTopMenuItem(
                        key = "ratio",
                        icon = if (ratio == 0) DocsSquareIcon else DocsStandardIcon,
                        contentDescription = "Ratio",
                        options = listOf(CameraTopMenuOptionRow(key = "ratio", options = listOf("1:1", "4:3", "16:9", "Full"), selectedIndex = ratio)),
                    ),
                ),
                endItems = listOf(
                    CameraTopMenuItem(
                        key = "video",
                        icon = DocsWideIcon,
                        contentDescription = "Video",
                        options = listOf(
                            CameraTopMenuOptionRow(key = "size", options = listOf("720P", "1080P", "4K"), selectedIndex = size, title = "Size"),
                            CameraTopMenuOptionRow(key = "fps", options = listOf("30", "60"), selectedIndex = fps, title = "FPS"),
                        ),
                    ),
                ),
                expandedItemKey = expanded,
            )
        }
    }
}

private fun docsRatioIcon(name: String, path: String): ImageVector = ImageVector.Builder(name, 20.dp, 20.dp, 20f, 20f).apply {
    addPath(
        pathData = addPathNodes(path),
        stroke = SolidColor(Color.White),
        strokeLineWidth = 1f,
        strokeLineJoin = StrokeJoin.Round,
    )
}.build()

/** `photo_ratio_square`. */
private val DocsSquareIcon: ImageVector by lazy { docsRatioIcon("DocsSquare", "M3.333,3.333h13.333v13.333h-13.333z") }

/** `photo_ratio_standard`. */
private val DocsStandardIcon: ImageVector by lazy { docsRatioIcon("DocsStandard", "M4.167,2.5h11.667v15h-11.667z") }

/** `photo_ratio_16_9`. */
private val DocsWideIcon: ImageVector by lazy { docsRatioIcon("DocsWide", "M5.417,1.958h9.167v16.083h-9.167z") }
