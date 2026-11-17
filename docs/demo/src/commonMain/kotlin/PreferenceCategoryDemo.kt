// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Info
import io.github.suqi8.coui.kmp.icon.extended.Refresh
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.preference.PreferenceCategory
import io.github.suqi8.coui.kmp.preference.PreferenceCategoryDefaults
import io.github.suqi8.coui.kmp.preference.PreferenceCategoryTitleType

@Composable
fun PreferenceCategoryDemo() {
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
        ) {
            var clicks by remember { mutableIntStateOf(0) }
            PreferenceCategory(
                title = "Large title",
                titleType = PreferenceCategoryTitleType.Large,
                titleIcon = {
                    PreferenceCategoryDefaults.TitleIcon(
                        imageVector = COUIIcons.Info,
                        contentDescription = "Info",
                        onClick = { clicks++ },
                    )
                },
            )
            PreferenceCategoryDemoCard("Clicks: $clicks")
            PreferenceCategory(
                title = "Medium title",
                titleType = PreferenceCategoryTitleType.Medium,
                endWidget = {
                    PreferenceCategoryDefaults.TextButton(text = "Edit", onClick = { clicks++ })
                },
            )
            PreferenceCategoryDemoCard("Text button widget")
            PreferenceCategory(
                title = "Small title",
                titleBadge = "N",
                endWidget = {
                    PreferenceCategoryDefaults.SingleIcon(
                        imageVector = COUIIcons.Settings,
                        contentDescription = "Settings",
                        onClick = { clicks++ },
                    )
                },
            )
            PreferenceCategoryDemoCard("Badge and single icon widget")
            PreferenceCategory(
                title = "Text with icon",
                endWidget = {
                    PreferenceCategoryDefaults.TextWithIcon(
                        text = "Refresh",
                        icon = COUIIcons.Refresh,
                        onClick = { clicks++ },
                    )
                },
            )
            PreferenceCategoryDemoCard("Text with icon widget")
        }
    }
}

@Composable
private fun PreferenceCategoryDemoCard(text: String) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        insideMargin = PaddingValues(16.dp),
    ) {
        Text(text)
    }
}
