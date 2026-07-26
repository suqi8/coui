// Copyright 2026, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.FullPageStatement
import com.suqi8.coui.kmp.basic.SmallTitle
import com.suqi8.coui.kmp.basic.Text
import com.suqi8.coui.kmp.theme.COUITheme

fun LazyListScope.fullPageStatementSection() {
    item(key = "fullPageStatement") {
        SmallTitle(text = "FullPageStatement")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            FullPageStatementDemo()
        }
    }
}

@Composable
private fun FullPageStatementDemo() {
    var result by remember { mutableStateOf("Waiting for action…") }

    Column {
        // The statement page is designed to fill a whole window; the demo hosts it in a
        // fixed-height box so it can live inside the component list.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp),
        ) {
            FullPageStatement(
                title = "User Agreement & Privacy Policy",
                content = buildString {
                    append("Welcome! Before you continue, please read the user agreement and privacy policy carefully. ")
                    append("We collect only the data required to provide core features, and we never share your personal information with third parties without your consent. ")
                    repeat(6) {
                        append("You can withdraw your consent at any time in Settings. ")
                        append("Continuing means you have read and agreed to the full statement. ")
                    }
                },
                primaryButtonText = "Agree",
                onPrimaryButtonClick = { result = "Agreed" },
                secondaryButtonText = "Disagree and exit",
                onSecondaryButtonClick = { result = "Exited" },
            )
        }
        Text(
            text = result,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            fontSize = COUITheme.textStyles.body2.fontSize,
            color = COUITheme.colorScheme.onSurfaceVariantSummary,
        )
    }
}
