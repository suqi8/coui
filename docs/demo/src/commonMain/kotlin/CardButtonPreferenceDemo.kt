// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Backup
import io.github.suqi8.coui.kmp.icon.extended.Contacts
import io.github.suqi8.coui.kmp.icon.extended.Lock
import io.github.suqi8.coui.kmp.icon.extended.Music
import io.github.suqi8.coui.kmp.icon.extended.Photos
import io.github.suqi8.coui.kmp.icon.extended.Scan
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.preference.CardButtonItem
import io.github.suqi8.coui.kmp.preference.CardButtonPreferenceGroup
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun CardButtonPreferenceDemo() {
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val items = remember {
                listOf(
                    CardButtonItem("Scan", icon = { DemoCardButtonIcon(COUIIcons.Scan) }, onClick = {}),
                    CardButtonItem("Photos", icon = { DemoCardButtonIcon(COUIIcons.Photos) }, onClick = {}),
                    CardButtonItem("Music", icon = { DemoCardButtonIcon(COUIIcons.Music) }, onClick = {}),
                    CardButtonItem("Contacts and accounts", icon = { DemoCardButtonIcon(COUIIcons.Contacts) }, onClick = {}),
                    CardButtonItem("Backup", icon = { DemoCardButtonIcon(COUIIcons.Backup) }, onClick = {}),
                    CardButtonItem("Privacy", icon = { DemoCardButtonIcon(COUIIcons.Lock) }, onClick = {}),
                    CardButtonItem("Disabled", icon = { DemoCardButtonIcon(COUIIcons.Settings) }, enabled = false, onClick = {}),
                )
            }
            CardButtonPreferenceGroup(items = items)
        }
    }
}

@Composable
private fun DemoCardButtonIcon(imageVector: ImageVector) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        modifier = Modifier.size(28.dp),
        tint = COUITheme.colorScheme.labelPrimary,
    )
}
