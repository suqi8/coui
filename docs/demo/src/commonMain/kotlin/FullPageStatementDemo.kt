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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.FullPageStatement
import io.github.suqi8.coui.kmp.basic.FullPageStatementDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun FullPageStatementDemo() {
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
            var result by remember { mutableStateOf("") }
            val linkStyles = FullPageStatementDefaults.linkStyles()
            val content = remember(linkStyles) {
                buildAnnotatedString {
                    append("Welcome! Before you start, please read the ")
                    withLink(LinkAnnotation.Clickable(tag = "agreement", styles = linkStyles) { result = "Opened the User Agreement" }) {
                        append("User Agreement")
                    }
                    append(" and the ")
                    withLink(LinkAnnotation.Clickable(tag = "privacy", styles = linkStyles) { result = "Opened the Privacy Policy" }) {
                        append("Privacy Policy")
                    }
                    append(" carefully. ")
                    repeat(4) {
                        append(
                            "To provide the basic features, this app needs to access the network and store data on your " +
                                "device. We only collect the information required for each feature, and you can review or " +
                                "withdraw your consent at any time in Settings. ",
                        )
                    }
                }
            }

            Card(insideMargin = PaddingValues(0.dp)) {
                FullPageStatement(
                    title = "User Agreement & Privacy Policy",
                    content = content,
                    primaryButtonText = "Agree",
                    onPrimaryButtonClick = { result = "Agreed" },
                    modifier = Modifier.fillMaxWidth(),
                    secondaryButtonText = "Disagree and exit",
                    onSecondaryButtonClick = { result = "Exited" },
                )
            }
            Text(
                text = result.ifEmpty { "Tap a link or a button" },
                color = COUITheme.colorScheme.labelSecondary,
            )
        }
    }
}
