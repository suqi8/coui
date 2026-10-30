// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
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
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.ListDetailLayout
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.ArrowRight
import io.github.suqi8.coui.kmp.theme.COUITheme

private val ListDetailDemoItems = listOf(
    "Wi-Fi" to "Connected to Home",
    "Bluetooth" to "On",
    "Display & brightness" to "Auto brightness",
    "Sounds & vibration" to "Ring mode",
    "Notifications & status bar" to "",
    "Battery" to "86%",
    "Storage" to "128 GB used",
    "About device" to "",
)

fun LazyListScope.listDetailLayoutSection() {
    item(key = "listDetailLayout") {
        SmallTitle(text = "ListDetailLayout")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            ListDetailLayoutDemo()
        }
    }
}

@Composable
private fun ListDetailLayoutDemo() {
    var selected by remember { mutableStateOf<Int?>(null) }
    ListDetailLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp),
        list = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                ListDetailDemoItems.forEachIndexed { index, (title, summary) ->
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(COUITheme.colorScheme.surface)
                        .padding(12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // A back button for the single-pane arrangement.
                        IconButton(onClick = { selected = null }) {
                            Icon(
                                imageVector = COUIIcons.Basic.ArrowRight,
                                contentDescription = "Back",
                                modifier = Modifier.graphicsLayer { scaleX = -1f },
                            )
                        }
                        Text(
                            text = ListDetailDemoItems[index].first,
                            modifier = Modifier.padding(start = 8.dp),
                            style = COUITheme.textStyles.title3,
                        )
                    }
                    Text(
                        text = "Detail of \"${ListDetailDemoItems[index].first}\". Narrower than 600dp the " +
                            "layout shows one pane at a time; wider, the list keeps 40% of the width " +
                            "between 280dp and 412dp.",
                        modifier = Modifier.padding(start = 4.dp, top = 12.dp),
                        style = COUITheme.textStyles.body2,
                        color = COUITheme.colorScheme.onSurfaceSecondary,
                    )
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
