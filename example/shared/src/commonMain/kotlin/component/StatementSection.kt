// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.FullPageStatement
import io.github.suqi8.coui.kmp.basic.FullPageStatementDefaults
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.layout.StatementPrivacyItem
import io.github.suqi8.coui.kmp.layout.UserStatementListItem
import io.github.suqi8.coui.kmp.overlay.OverlayIndividualStatementDialog
import io.github.suqi8.coui.kmp.overlay.OverlayUserStatementDialog
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.window.WindowIndividualStatementDialog
import io.github.suqi8.coui.kmp.window.WindowUserStatementDialog

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
    item(key = "statementDialogs") {
        SmallTitle(text = "Statement Dialogs")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            StatementDialogsDemo()
        }
    }
}

@Composable
private fun agreementLinks(onLinkClick: (String) -> Unit): AnnotatedString {
    val linkStyles = FullPageStatementDefaults.linkStyles()
    return remember(linkStyles) {
        buildAnnotatedString {
            append("Read the ")
            withLink(LinkAnnotation.Clickable(tag = "agreement", styles = linkStyles) { onLinkClick("User Agreement") }) {
                append("User Agreement")
            }
            append(" and the ")
            withLink(LinkAnnotation.Clickable(tag = "privacy", styles = linkStyles) { onLinkClick("Privacy Policy") }) {
                append("Privacy Policy")
            }
            append(".")
        }
    }
}

@Composable
private fun FullPageStatementDemo() {
    var result by remember { mutableStateOf("Waiting for action…") }
    val links = agreementLinks { result = "Opened $it" }
    val content = remember(links) {
        buildAnnotatedString {
            append("Welcome! Before you continue, please read the user agreement and privacy policy carefully. ")
            append("We collect only the data required to provide core features, and we never share your personal information with third parties without your consent. ")
            append(links)
            repeat(6) {
                append(" You can withdraw your consent at any time in Settings.")
                append(" Continuing means you have read and agreed to the full statement.")
            }
        }
    }

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
                content = content,
                primaryButtonText = "Agree",
                onPrimaryButtonClick = { result = "Agreed" },
                modifier = Modifier.fillMaxSize(),
                secondaryButtonText = "Disagree and exit",
                onSecondaryButtonClick = { result = "Exited" },
            )
        }
        ResultText(result)
    }
}

@Composable
private fun StatementDialogsDemo() {
    var result by remember { mutableStateOf("Waiting for action…") }
    var showUserOverlay by remember { mutableStateOf(false) }
    var showUserWindow by remember { mutableStateOf(false) }
    var showIndividualOverlay by remember { mutableStateOf(false) }
    var showIndividualWindow by remember { mutableStateOf(false) }
    val links = agreementLinks { result = "Opened $it" }
    val userStatement = remember(links) {
        buildAnnotatedString {
            append("To use this app, it needs network access and storage permissions. ")
            append(links)
        }
    }
    val listItems = remember {
        listOf(
            UserStatementListItem(title = "Network", summary = "Sync your notes across devices"),
            UserStatementListItem(title = "Storage", summary = "Save attachments locally"),
        )
    }
    val privacyItems = remember {
        listOf(
            StatementPrivacyItem(title = "Personalized suggestions", summary = "Use your usage data to tailor suggestions"),
            StatementPrivacyItem(title = "Crash reports", summary = "Send anonymous crash logs"),
        )
    }
    val individualStatement = remember(links) {
        buildAnnotatedString {
            append("Choose the optional features to turn on. ")
            append(links)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        TextButton(
            text = "User statement (O)",
            onClick = { showUserOverlay = true },
            modifier = Modifier.fillMaxWidth(),
        )
        TextButton(
            text = "User statement with list (W)",
            onClick = { showUserWindow = true },
            modifier = Modifier.fillMaxWidth(),
        )
        TextButton(
            text = "Individual statement (O)",
            onClick = { showIndividualOverlay = true },
            modifier = Modifier.fillMaxWidth(),
        )
        TextButton(
            text = "Individual statement, plain (W)",
            onClick = { showIndividualWindow = true },
            modifier = Modifier.fillMaxWidth(),
        )
        ResultText(result)
    }

    OverlayUserStatementDialog(
        show = showUserOverlay,
        onConfirm = {
            result = "User statement: agreed"
            showUserOverlay = false
        },
        onExit = {
            result = "User statement: exited"
            showUserOverlay = false
        },
        title = "Welcome to",
        appName = "Notes",
        appMessage = "Write, organize and sync your notes everywhere.",
        statement = userStatement,
        onDismissRequest = { showUserOverlay = false },
    )
    WindowUserStatementDialog(
        show = showUserWindow,
        onConfirm = {
            result = "User statement (W): agreed"
            showUserWindow = false
        },
        onExit = {
            result = "User statement (W): exited"
            showUserWindow = false
        },
        title = "Welcome to",
        appName = "Notes",
        listItems = listItems,
        onListItemClick = { result = "Tapped ${listItems[it].title}" },
        statement = userStatement,
        onDismissRequest = { showUserWindow = false },
    )
    OverlayIndividualStatementDialog(
        show = showIndividualOverlay,
        onConfirm = { checked ->
            result = "Enabled: " + checked.joinToString { it.title }
            showIndividualOverlay = false
        },
        onExit = {
            result = "Individual statement: exited"
            showIndividualOverlay = false
        },
        title = "Optional features",
        statement = individualStatement,
        privacyItems = privacyItems,
        onDismissRequest = { showIndividualOverlay = false },
    )
    WindowIndividualStatementDialog(
        show = showIndividualWindow,
        onConfirm = {
            result = "Individual statement (W): agreed"
            showIndividualWindow = false
        },
        onExit = {
            result = "Individual statement (W): exited"
            showIndividualWindow = false
        },
        title = "Location service",
        statement = individualStatement,
        onDismissRequest = { showIndividualWindow = false },
    )
}

@Composable
private fun ResultText(result: String) {
    Text(
        text = result,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp),
        fontSize = COUITheme.textStyles.bodyM.fontSize,
        color = COUITheme.colorScheme.labelSecondaryVariant,
    )
}
