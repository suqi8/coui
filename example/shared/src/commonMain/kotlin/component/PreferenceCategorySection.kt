// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Info
import io.github.suqi8.coui.kmp.icon.extended.Refresh
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.preference.PreferenceCategory
import io.github.suqi8.coui.kmp.preference.PreferenceCategoryDefaults
import io.github.suqi8.coui.kmp.preference.PreferenceCategoryMarginStartType
import io.github.suqi8.coui.kmp.preference.PreferenceCategoryTitleType

fun LazyListScope.preferenceCategorySection() {
    item(key = "preferenceCategory") {
        var clicks by remember { mutableIntStateOf(0) }

        SmallTitle(text = "PreferenceCategory: $clicks")
        PreferenceCategoryCard("Default small title")

        PreferenceCategory(
            title = "Medium title",
            titleType = PreferenceCategoryTitleType.Medium,
        )
        PreferenceCategoryCard("14sp, couiColorPrimaryNeutral")

        PreferenceCategory(
            title = "Large title",
            titleType = PreferenceCategoryTitleType.Large,
        )
        PreferenceCategoryCard("16sp, couiColorPrimaryNeutral")

        PreferenceCategory(
            title = "Small margin start",
            marginStartType = PreferenceCategoryMarginStartType.Small,
        )
        PreferenceCategoryCard("Title starts 16dp in")

        PreferenceCategory(
            title = "With title icon",
            titleIcon = {
                PreferenceCategoryDefaults.TitleIcon(
                    imageVector = COUIIcons.Info,
                    contentDescription = "Info",
                    onClick = { clicks++ },
                )
            },
        )
        PreferenceCategoryCard("The small title turns couiColorPrimaryNeutral")

        PreferenceCategory(
            title = "With badge",
            titleBadge = "N",
        )
        PreferenceCategoryCard("A text red dot after the title")

        PreferenceCategory(
            title = "Text button",
            endWidget = {
                PreferenceCategoryDefaults.TextButton(text = "Edit", onClick = { clicks++ })
            },
        )
        PreferenceCategoryCard("couiColorPrimary label")

        PreferenceCategory(
            title = "Single icon",
            endWidget = {
                PreferenceCategoryDefaults.SingleIcon(
                    imageVector = COUIIcons.Settings,
                    contentDescription = "Settings",
                    onClick = { clicks++ },
                )
            },
        )
        PreferenceCategoryCard("16dp circular press mask")

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
        PreferenceCategoryCard("Clickable: couiColorPrimaryNeutral text")

        PreferenceCategory(
            title = "Medium title with text button",
            titleType = PreferenceCategoryTitleType.Medium,
            endWidget = {
                PreferenceCategoryDefaults.TextButton(text = "Manage", onClick = { clicks++ })
            },
        )
        PreferenceCategoryCard("The widget text grows to 14sp")
    }
}

@Composable
private fun PreferenceCategoryCard(text: String) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        insideMargin = PaddingValues(16.dp),
    ) {
        Text(text)
    }
}
