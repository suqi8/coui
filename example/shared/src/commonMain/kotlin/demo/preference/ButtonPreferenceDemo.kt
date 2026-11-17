// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.LocalDemoSnackbarHostState
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Backup
import io.github.suqi8.coui.kmp.icon.extended.Contacts
import io.github.suqi8.coui.kmp.icon.extended.Lock
import io.github.suqi8.coui.kmp.icon.extended.Music
import io.github.suqi8.coui.kmp.icon.extended.Photos
import io.github.suqi8.coui.kmp.icon.extended.Scan
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.preference.ButtonPreference
import io.github.suqi8.coui.kmp.preference.CardButtonItem
import io.github.suqi8.coui.kmp.preference.CardButtonPreferenceGroup
import io.github.suqi8.coui.kmp.preference.RecommendedItem
import io.github.suqi8.coui.kmp.preference.RecommendedPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_clicked
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_arrow_preference_clickable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_preference_account
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_preference_action
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_preference_backup
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_preference_both_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_preference_button_clicked
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_preference_columns
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_preference_contacts_accounts
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_preference_long_button
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_preference_long_button_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_preference_recommended_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_preference_scan
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_preference_sign_in
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_preference_sign_in_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_preference_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_component
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_long_title
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_battery
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_display
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_title
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_music
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_notifications
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_photos
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_privacy
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_sound
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_storage
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_title
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_wallpaper
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

private val ButtonTypes = listOf("ButtonPreference", "CardButtonPreferenceGroup", "RecommendedPreference")
private val CardButtonColumns = listOf(3, 4, 5)
private val CardButtonCounts = listOf(2, 4, 6, 8)
private val RecommendedCounts = listOf(1, 2, 3, 5)

/** The sample entries of the card buttons, in order. */
private val CardButtonSamples: List<Pair<StringResource, ImageVector>> = listOf(
    Res.string.demo_button_preference_scan to COUIIcons.Scan,
    Res.string.sample_photos to COUIIcons.Photos,
    Res.string.sample_music to COUIIcons.Music,
    Res.string.demo_button_preference_contacts_accounts to COUIIcons.Contacts,
    Res.string.demo_button_preference_backup to COUIIcons.Backup,
    Res.string.sample_privacy to COUIIcons.Lock,
    Res.string.sample_storage to COUIIcons.Settings,
    Res.string.sample_notifications to COUIIcons.Settings,
)

/** The sample entries of the recommendation card, in order. */
private val RecommendedSamples: List<StringResource> = listOf(
    Res.string.sample_display,
    Res.string.sample_wallpaper,
    Res.string.sample_battery,
    Res.string.sample_sound,
    Res.string.sample_storage,
)

internal val ButtonPreferenceDemo = Demo(
    id = "button_preference",
    apiName = "ButtonPreference",
    title = Res.string.demo_button_preference_title,
    category = DemoCategory.Preference,
    page = DemoPage.Variants {
        playground {
            val state = rememberPreferenceRowState()
            var type by rememberSaveable { mutableIntStateOf(0) }
            var longButton by rememberSaveable { mutableStateOf(false) }
            var rowClickable by rememberSaveable { mutableStateOf(false) }
            var columnsIndex by rememberSaveable { mutableIntStateOf(1) }
            var cardCountIndex by rememberSaveable { mutableIntStateOf(3) }
            var recommendedCountIndex by rememberSaveable { mutableIntStateOf(2) }
            var lastItemDisabled by rememberSaveable { mutableStateOf(false) }
            val onClicked = rememberClickedMessage()
            val title = state.title()
            PlaygroundPreview(
                inCard = type == 0,
                contentPadding = if (type == 0) PaddingValues(0.dp) else PaddingValues(horizontal = 16.dp),
            ) {
                when (type) {
                    0 -> CardListGroup {
                        item {
                            ButtonPreference(
                                title = title,
                                buttonText = stringResource(
                                    if (longButton) {
                                        Res.string.demo_button_preference_long_button_text
                                    } else {
                                        Res.string.demo_button_preference_action
                                    },
                                ),
                                onButtonClick = { onClicked(Res.string.demo_button_preference_action) },
                                summary = state.summary(),
                                startAction = if (state.showIcon) {
                                    { PreferenceStartIcon() }
                                } else {
                                    null
                                },
                                endActions = { if (state.showEndText) PreferenceEndText(enabled = state.enabled) },
                                onClick = if (rowClickable) {
                                    { onClicked(Res.string.sample_title) }
                                } else {
                                    null
                                },
                                enabled = state.enabled,
                            )
                        }
                    }

                    1 -> {
                        val count = CardButtonCounts[cardCountIndex]
                        val longTitle = stringResource(Res.string.sample_long_title)
                        val items = CardButtonSamples.take(count).mapIndexed { index, (label, icon) ->
                            CardButtonItem(
                                title = if (state.longTitle && index == 0) longTitle else stringResource(label),
                                icon = { CardButtonIcon(icon) },
                                enabled = !(lastItemDisabled && index == count - 1),
                                onClick = { onClicked(label) },
                            )
                        }
                        CardButtonPreferenceGroup(items = items, columns = CardButtonColumns[columnsIndex])
                    }

                    else -> {
                        val items = RecommendedSamples.take(RecommendedCounts[recommendedCountIndex]).map { label ->
                            RecommendedItem(text = stringResource(label), onClick = { onClicked(label) })
                        }
                        RecommendedPreference(
                            title = if (state.longTitle) title else stringResource(Res.string.demo_button_preference_recommended_title),
                            items = items,
                        )
                    }
                }
            }
            PlaygroundControls {
                literalChoiceControl(Res.string.demo_pref_component, ButtonTypes, type) { type = it }
                when (type) {
                    0 -> {
                        switchControl(Res.string.demo_button_preference_long_button, longButton) { longButton = it }
                        switchControl(Res.string.demo_arrow_preference_clickable, rowClickable) { rowClickable = it }
                        preferenceRowControls(state)
                    }

                    1 -> {
                        literalChoiceControl(
                            Res.string.demo_button_preference_columns,
                            CardButtonColumns.map { it.toString() },
                            columnsIndex,
                        ) { columnsIndex = it }
                        literalChoiceControl(
                            Res.string.common_count,
                            CardButtonCounts.map { it.toString() },
                            cardCountIndex,
                        ) { cardCountIndex = it }
                        switchControl(Res.string.demo_pref_long_title, state.longTitle) { state.longTitle = it }
                        switchControl(Res.string.common_disabled, lastItemDisabled) { lastItemDisabled = it }
                    }

                    else -> {
                        literalChoiceControl(
                            Res.string.common_count,
                            RecommendedCounts.map { it.toString() },
                            recommendedCountIndex,
                        ) { recommendedCountIndex = it }
                        switchControl(Res.string.demo_pref_long_title, state.longTitle) { state.longTitle = it }
                    }
                }
            }
        }
        variant(key = "button", title = "ButtonPreference") {
            AccountRows()
        }
        variant(key = "cardButton", title = "CardButtonPreferenceGroup", inCard = false) {
            CardButtonExample(modifier = Modifier.padding(horizontal = 16.dp))
        }
        variant(key = "recommended", title = "RecommendedPreference", inCard = false) {
            RecommendedExample(modifier = Modifier.padding(horizontal = 16.dp))
        }
    },
    keywords = listOf("设置", "button", "按钮", "推荐", "recommended"),
)

/**
 * Returns a handler that shows "Clicked: label" in the page snackbar.
 */
@Composable
private fun rememberClickedMessage(): (StringResource) -> Unit {
    val snackbar = LocalDemoSnackbarHostState.current
    val scope = rememberCoroutineScope()
    return remember(snackbar, scope) {
        val handler: (StringResource) -> Unit = { label ->
            scope.launch { snackbar.showSnackbar(getString(Res.string.common_clicked, getString(label))) }
        }
        handler
    }
}

/**
 * An inline small button whose click is independent from the row, and a disabled row.
 */
@Composable
private fun AccountRows() {
    var signInCount by rememberSaveable { mutableIntStateOf(0) }
    CardListGroup {
        item {
            ButtonPreference(
                title = stringResource(Res.string.demo_button_preference_account),
                buttonText = stringResource(Res.string.demo_button_preference_sign_in),
                onButtonClick = { signInCount++ },
                summary = if (signInCount > 0) {
                    stringResource(Res.string.demo_button_preference_button_clicked, signInCount)
                } else {
                    stringResource(Res.string.demo_button_preference_sign_in_summary)
                },
            )
        }
        item {
            ButtonPreference(
                title = stringResource(Res.string.common_disabled),
                buttonText = stringResource(Res.string.demo_button_preference_action),
                onButtonClick = {},
                summary = stringResource(Res.string.demo_button_preference_both_disabled),
                enabled = false,
            )
        }
    }
}

@Composable
private fun CardButtonExample(modifier: Modifier = Modifier) {
    val onClicked = rememberClickedMessage()
    val items = CardButtonSamples.take(6).map { (label, icon) ->
        CardButtonItem(
            title = stringResource(label),
            icon = { CardButtonIcon(icon) },
            onClick = { onClicked(label) },
        )
    } + CardButtonItem(
        title = stringResource(Res.string.common_disabled),
        icon = { CardButtonIcon(COUIIcons.Settings) },
        enabled = false,
        onClick = {},
    )
    CardButtonPreferenceGroup(items = items, modifier = modifier)
}

@Composable
private fun RecommendedExample(modifier: Modifier = Modifier) {
    val onClicked = rememberClickedMessage()
    val items = RecommendedSamples.take(3).map { label ->
        RecommendedItem(text = stringResource(label), onClick = { onClicked(label) })
    }
    RecommendedPreference(
        title = stringResource(Res.string.demo_button_preference_recommended_title),
        items = items,
        modifier = modifier,
    )
}

@Composable
private fun CardButtonIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        modifier = modifier.size(28.dp),
        tint = COUITheme.colorScheme.labelPrimary,
    )
}
