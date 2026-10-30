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
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.preference.EditTextPreference

@Composable
fun EditTextPreferenceDemo() {
    Scaffold {
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
                var nickname by remember { mutableStateOf("Suqi") }
                Card {
                    CardListGroup {
                        item {
                            EditTextPreference(
                                value = nickname,
                                onValueChange = { nickname = it },
                                title = "Nickname",
                                positiveButtonText = "OK",
                                negativeButtonText = "Cancel",
                                hint = "Enter a nickname",
                            )
                        }
                        item {
                            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            EditTextPreference(
                                value = "Locked",
                                onValueChange = {},
                                title = "Disabled",
                                positiveButtonText = "OK",
                                negativeButtonText = "Cancel",
                                enabled = false,
                            )
                        }
                    }
                }
            }
        }
    }
}
