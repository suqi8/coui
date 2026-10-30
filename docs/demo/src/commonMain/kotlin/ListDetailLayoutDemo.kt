// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.ListDetailLayout
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.ArrowRight
import io.github.suqi8.coui.kmp.theme.COUITheme

private val ListDetailItems = listOf(
    "Wi-Fi" to "Connected to Home",
    "Bluetooth" to "On",
    "Display & brightness" to "Auto brightness",
    "Sounds & vibration" to "Ring mode",
    "Battery" to "86%",
    "About device" to "",
)

@Composable
fun ListDetailLayoutDemo() {
    var selected by remember { mutableStateOf<Int?>(null) }
    ListDetailLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        list = {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                ListDetailItems.forEachIndexed { index, (title, summary) ->
                    BasicComponent(
                        title = title,
                        summary = summary.ifEmpty { null },
                        onClick = { selected = index },
                        holdDownState = selected == index,
                    )
                }
            }
        },
        detail = selected?.let { index ->
            {
                Column(Modifier.fillMaxSize().padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { selected = null }) {
                            Icon(
                                imageVector = COUIIcons.Basic.ArrowRight,
                                contentDescription = "Back",
                                modifier = Modifier.graphicsLayer { scaleX = -1f },
                            )
                        }
                        Text(
                            text = ListDetailItems[index].first,
                            modifier = Modifier.padding(start = 8.dp),
                            style = COUITheme.textStyles.title3,
                        )
                    }
                }
            }
        },
        emptyDetail = {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Select an item",
                    style = COUITheme.textStyles.body2,
                    color = COUITheme.colorScheme.onSurfaceSecondary,
                )
            }
        },
    )
}
