// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import com.suqi8.coui.kmp.shared.generated.resources.Res
import com.suqi8.coui.kmp.shared.generated.resources.icon
import java.awt.Dimension

fun main() = application {
    val state = rememberWindowState(
        size = DpSize(420.dp, 840.dp),
        position = WindowPosition.Aligned(Alignment.Center),
    )
    val isHotReloadMode = System.getProperty("app.mode") == "hot"
    Window(
        state = state,
        onCloseRequest = ::exitApplication,
        alwaysOnTop = isHotReloadMode,
        title = "COUI",
        icon = painterResource(Res.drawable.icon),
    ) {
        window.minimumSize = Dimension(300, 600)
        App()
    }
}
