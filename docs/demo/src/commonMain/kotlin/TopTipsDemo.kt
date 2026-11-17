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
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.TopTips
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Search
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun TopTipsDemo() {
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
            var closableVisible by remember { mutableStateOf(true) }
            var actionCount by remember { mutableIntStateOf(0) }

            TopTips(
                text = "New security update available",
                modifier = Modifier.fillMaxWidth(),
                actionText = "Update",
                onAction = { actionCount++ },
            )
            TopTips(
                text = "Closable tips with a start icon",
                modifier = Modifier.fillMaxWidth(),
                visible = closableVisible,
                startIcon = { Icon(imageVector = COUIIcons.Basic.Search, contentDescription = null) },
                onClose = { closableVisible = false },
            )
            TopTips(
                text = "Connect to Wi-Fi to continue backing up your photos",
                modifier = Modifier.fillMaxWidth(),
                title = "Backup paused",
                startIcon = { Icon(imageVector = COUIIcons.Basic.Search, contentDescription = null) },
                actionText = "Retry",
                onAction = { actionCount++ },
                onClose = {},
            )
            TopTips(
                text = "A message that is too long to fit on a single line keeps scrolling as a marquee",
                modifier = Modifier.fillMaxWidth(),
                marquee = true,
            )
            Text(
                text = "Actions tapped: $actionCount",
                color = COUITheme.colorScheme.labelSecondary,
            )
            TextButton(
                text = "Show the closable tips again",
                onClick = { closableVisible = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !closableVisible,
            )
        }
    }
}
