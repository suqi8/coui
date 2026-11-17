// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.preference.PagerHeaderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference

@Composable
fun PagerHeaderPreferenceDemo() {
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
            var enabled by remember { mutableStateOf(true) }

            Column {
                // The demo column already insets the content by 16dp, so the header uses a 16dp
                // horizontal margin to line up with the text inside the card.
                PagerHeaderPreference(
                    title = "Battery saver",
                    summary = "Limits background activity and visual effects to extend battery life.",
                    insideMargin = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                )
                Card {
                    SwitchPreference(
                        checked = enabled,
                        onCheckedChange = { enabled = it },
                        title = "Battery saver",
                    )
                }
            }
        }
    }
}
