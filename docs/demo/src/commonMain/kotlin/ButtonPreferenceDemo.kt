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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.preference.ButtonPreference

@Composable
fun ButtonPreferenceDemo() {
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
            var signedIn by remember { mutableStateOf(false) }
            var cleanCount by remember { mutableIntStateOf(0) }
            var rowClicks by remember { mutableIntStateOf(0) }

            Card {
                CardListGroup {
                    item {
                        ButtonPreference(
                            title = "Account",
                            summary = if (signedIn) "Signed in, data is syncing" else "Sign in to sync your data",
                            buttonText = if (signedIn) "Sign out" else "Sign in",
                            onButtonClick = { signedIn = !signedIn },
                        )
                    }
                    item {
                        ButtonPreference(
                            title = "Storage",
                            summary = "Cleaned $cleanCount times, row tapped $rowClicks times",
                            buttonText = "Clean",
                            onButtonClick = { cleanCount++ },
                            onClick = { rowClicks++ },
                        )
                    }
                    item {
                        ButtonPreference(
                            title = "Disabled",
                            summary = "Row and button are disabled",
                            buttonText = "Action",
                            onButtonClick = {},
                            enabled = false,
                        )
                    }
                }
            }
        }
    }
}
