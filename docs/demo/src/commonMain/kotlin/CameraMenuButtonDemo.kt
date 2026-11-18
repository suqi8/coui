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
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.CameraEffectButton
import io.github.suqi8.coui.kmp.basic.CameraMenuButton

@Composable
fun CameraMenuButtonDemo() {
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
            var selected by remember { mutableIntStateOf(0) }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CameraMenuButton(
                    onClick = { selected = 0 },
                    icon = DocsBokehIcon,
                    contentDescription = "Bokeh",
                    selected = selected == 0,
                    label = "Bokeh",
                )
                CameraMenuButton(
                    onClick = { selected = 1 },
                    icon = DocsFilterIcon,
                    contentDescription = "Filter",
                    selected = selected == 1,
                    label = "Filter",
                )
                CameraMenuButton(
                    onClick = { selected = 2 },
                    icon = DocsFilterIcon,
                    contentDescription = "Presets",
                    selected = selected == 2,
                    label = "Presets",
                    colorfulBorder = true,
                )
            }
            var filterOn by remember { mutableStateOf(true) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                CameraEffectButton(
                    onClick = {},
                    icon = DocsBokehIcon,
                    contentDescription = "Bokeh",
                )
                CameraEffectButton(
                    onClick = { filterOn = !filterOn },
                    icon = DocsFilterIcon,
                    contentDescription = "Filter",
                    selected = filterOn,
                )
            }
        }
    }
}

/** The camera's bokeh entry icon (`blur_menu_control_button`). */
private val DocsBokehIcon: ImageVector by lazy {
    ImageVector.Builder("DocsBokeh", 24.dp, 24.dp, 24f, 24f).apply {
        addPath(
            pathData = addPathNodes(
                "M16.878,5.082C16.399,4.89 15.717,4.75 15.008,4.793C14.293,4.837 13.484,5.073 12.849,5.708" +
                    "C11.658,6.899 11.493,8.282 11.309,9.832L11.309,9.832C11.291,9.982 11.274,10.133 11.254,10.285" +
                    "C11.222,10.542 11.192,10.787 11.163,11.019H7.855V12.519H10.973C10.724,14.483 10.616,15.245 10.525,15.751" +
                    "C10.239,17.323 9.42,18.01 8.663,18.01C8.201,18.01 7.712,17.893 7.318,17.761C7.126,17.697 6.966,17.633 6.856,17.586" +
                    "C6.8,17.562 6.758,17.543 6.731,17.531L6.702,17.517L6.698,17.514C6.698,17.514 6.698,17.515 6.362,18.185" +
                    "C6.027,18.856 6.027,18.856 6.027,18.856L6.028,18.857L6.028,18.857L6.03,18.858L6.035,18.86L6.049,18.867" +
                    "C6.061,18.873 6.077,18.88 6.097,18.89C6.137,18.909 6.194,18.934 6.265,18.965C6.406,19.025 6.605,19.105 6.844,19.184" +
                    "C7.313,19.341 7.974,19.51 8.663,19.51C10.493,19.51 11.659,17.897 12,16.02C12.102,15.458 12.219,14.622 12.485,12.519" +
                    "H16.136V11.019H12.674C12.696,10.843 12.719,10.661 12.743,10.472C12.758,10.35 12.773,10.232 12.786,10.118" +
                    "L12.787,10.118C12.993,8.439 13.099,7.579 13.909,6.769C14.212,6.466 14.63,6.319 15.099,6.29" +
                    "C15.574,6.261 16.034,6.36 16.321,6.475L16.878,5.082Z",
            ),
            pathFillType = PathFillType.EvenOdd,
            fill = SolidColor(Color.White),
        )
    }.build()
}

/** The camera's filter entry icon (`face_beauty_menu_effect_button`). */
private val DocsFilterIcon: ImageVector by lazy {
    ImageVector.Builder("DocsFilter", 24.dp, 24.dp, 24f, 24f).apply {
        listOf(
            "M15.216,19.905C18.289,19.905 20.779,17.415 20.779,14.342C20.779,11.269 18.289,8.779 15.216,8.779" +
                "C12.144,8.779 9.653,11.269 9.653,14.342C9.653,17.415 12.144,19.905 15.216,19.905Z" to 0.54f,
            "M8.792,19.905C11.865,19.905 14.356,17.415 14.356,14.342C14.356,11.269 11.865,8.779 8.792,8.779" +
                "C5.72,8.779 3.229,11.269 3.229,14.342C3.229,17.415 5.72,19.905 8.792,19.905Z" to 0.54f,
            "M12.004,14.342C15.077,14.342 17.568,11.851 17.568,8.779C17.568,5.706 15.077,3.215 12.004,3.215" +
                "C8.932,3.215 6.441,5.706 6.441,8.779C6.441,11.851 8.932,14.342 12.004,14.342Z" to 1f,
        ).forEach { (path, alpha) ->
            addPath(
                pathData = addPathNodes(path),
                stroke = SolidColor(Color.White),
                strokeAlpha = alpha,
                strokeLineWidth = 1.15f,
            )
        }
    }.build()
}
