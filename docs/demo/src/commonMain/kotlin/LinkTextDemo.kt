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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.LinkText
import io.github.suqi8.coui.kmp.basic.appendLink
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun LinkTextDemo() {
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
            var clicks by remember { mutableIntStateOf(0) }

            Card {
                LinkText(
                    text = buildAnnotatedString {
                        append("Read the ")
                        appendLink("User Agreement", onClick = { clicks++ })
                        append(" and the ")
                        appendLink("Privacy Policy", onClick = { clicks++ })
                        append(". Links clicked: $clicks")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = COUITheme.colorScheme.labelSecondary,
                    style = COUITheme.textStyles.bodyS,
                )
                LinkText(
                    text = buildAnnotatedString {
                        append("A disabled link: ")
                        appendLink("Terms of service", onClick = {})
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    enabled = false,
                    color = COUITheme.colorScheme.labelSecondary,
                    style = COUITheme.textStyles.bodyS,
                )
            }
        }
    }
}
