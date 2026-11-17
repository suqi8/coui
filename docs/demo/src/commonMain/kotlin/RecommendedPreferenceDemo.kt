// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.preference.RecommendedItem
import io.github.suqi8.coui.kmp.preference.RecommendedPreference
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun RecommendedPreferenceDemo() {
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
            var opened by remember { mutableStateOf<String?>(null) }
            val items = remember {
                listOf(
                    RecommendedItem(text = "Display & brightness", onClick = { opened = "Display & brightness" }),
                    RecommendedItem(text = "Wallpapers & style", onClick = { opened = "Wallpapers & style" }),
                    RecommendedItem(text = "Battery", onClick = { opened = "Battery" }),
                )
            }

            RecommendedPreference(
                title = "You might be looking for:",
                items = items,
            )
            Text(
                text = opened?.let { "Opened: $it" } ?: "Tap a row to open it",
                color = COUITheme.colorScheme.labelSecondary,
                fontSize = COUITheme.textStyles.bodyS.fontSize,
            )
        }
    }
}
