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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.FloatingEditToolbar
import io.github.suqi8.coui.kmp.basic.FloatingEditToolbarItem
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Copy
import io.github.suqi8.coui.kmp.icon.extended.Delete
import io.github.suqi8.coui.kmp.icon.extended.Favorites
import io.github.suqi8.coui.kmp.icon.extended.Share

@Composable
fun FloatingEditToolbarDemo() {
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
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var visible by remember { mutableStateOf(true) }
            var copyEnabled by remember { mutableStateOf(true) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(
                    text = if (visible) "Hide toolbar" else "Show toolbar",
                    onClick = { visible = !visible },
                    minWidth = 0.dp,
                )
                TextButton(
                    text = if (copyEnabled) "Disable Copy" else "Enable Copy",
                    onClick = { copyEnabled = !copyEnabled },
                    minWidth = 0.dp,
                )
            }
            FloatingEditToolbar(
                visible = visible,
                defaultWindowInsetsPadding = false,
            ) {
                FloatingEditToolbarItem(onClick = { /* Share */ }, icon = COUIIcons.Share, label = "Share")
                FloatingEditToolbarItem(onClick = { /* Copy */ }, icon = COUIIcons.Copy, label = "Copy", enabled = copyEnabled)
                FloatingEditToolbarItem(onClick = { /* Favorite */ }, icon = COUIIcons.Favorites, label = "Favorite", badge = 0)
                FloatingEditToolbarItem(onClick = { /* Delete */ }, icon = COUIIcons.Delete, label = "Delete")
            }
        }
    }
}
