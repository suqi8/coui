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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.preference.CardEntranceItem
import io.github.suqi8.coui.kmp.preference.CardEntrancePreference
import io.github.suqi8.coui.kmp.preference.CardEntranceType

@Composable
fun CardEntrancePreferenceDemo() {
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
            val items = remember {
                listOf(
                    CardEntranceItem(title = "Privacy", summary = "Permissions", onClick = {}),
                    CardEntranceItem(title = "Location", summary = "On", statusOn = true, onClick = {}),
                    CardEntranceItem(
                        title = "Backup & restore",
                        summary = "Last backup: yesterday",
                        type = CardEntranceType.Large,
                        onClick = {},
                    ),
                    CardEntranceItem(title = "More settings", onClick = {}),
                )
            }
            CardEntrancePreference(items = items)
        }
    }
}
