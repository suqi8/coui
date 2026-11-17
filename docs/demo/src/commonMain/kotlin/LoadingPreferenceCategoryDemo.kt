// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.preference.LoadingPreferenceCategory
import io.github.suqi8.coui.kmp.preference.StatusSwitchingPreference
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.delay

@Composable
fun LoadingPreferenceCategoryDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(vertical = 16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var scanning by remember { mutableStateOf(true) }
            LaunchedEffect(scanning) {
                if (scanning) {
                    delay(3000)
                    scanning = false
                }
            }
            LoadingPreferenceCategory(
                title = "Available devices",
                loading = scanning,
                loadingText = "Searching",
                endContent = {
                    Text(
                        text = "Refresh",
                        modifier = Modifier.clickable { scanning = true },
                        style = COUITheme.textStyles.bodyXS.copy(fontWeight = FontWeight.Medium),
                        color = COUITheme.colorScheme.primary,
                    )
                },
            )
            Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                CardListGroup {
                    item {
                        StatusSwitchingPreference(title = "Living room speaker", status = "Paired")
                    }
                    item {
                        StatusSwitchingPreference(title = "Earbuds", status = "Not connected")
                    }
                }
            }
        }
    }
}
