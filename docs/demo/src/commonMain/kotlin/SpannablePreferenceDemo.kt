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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.appendLink
import io.github.suqi8.coui.kmp.preference.SpannablePreference

@Composable
fun SpannablePreferenceDemo() {
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
            var lastAction by remember { mutableStateOf("nothing") }

            Card {
                SpannablePreference(
                    title = "Location services",
                    summary = buildAnnotatedString {
                        append("Last tapped: $lastAction. ")
                        appendLink("Learn more", onClick = { lastAction = "link" })
                    },
                    onClick = { lastAction = "row" },
                )
                SpannablePreference(
                    title = "Disabled",
                    summary = buildAnnotatedString {
                        append("The link of a disabled row is greyed out: ")
                        appendLink("Privacy policy", onClick = {})
                    },
                    enabled = false,
                )
            }
        }
    }
}
