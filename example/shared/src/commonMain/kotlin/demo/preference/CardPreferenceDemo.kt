// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.preference

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Backup
import io.github.suqi8.coui.kmp.icon.extended.Location
import io.github.suqi8.coui.kmp.icon.extended.Lock
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.preference.AppInfoPreference
import io.github.suqi8.coui.kmp.preference.CardEntranceItem
import io.github.suqi8.coui.kmp.preference.CardEntrancePreference
import io.github.suqi8.coui.kmp.preference.CardEntranceType
import io.github.suqi8.coui.kmp.preference.CardInstructionIllustration
import io.github.suqi8.coui.kmp.preference.CardInstructionPage
import io.github.suqi8.coui.kmp.preference.CardInstructionPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_copy
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_on
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_app_name
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_app_version
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_backup
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_backup_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_copied
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_large_item
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_left_edge
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_location
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_long_app_name
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_long_name
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_more_settings
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_page1_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_page1_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_page2_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_page2_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_page3_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_pages
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_permissions
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_right_edge
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_show_copy
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_status_on
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_card_preference_unavailable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_component
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_privacy
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

private val CardTypes = listOf("AppInfoPreference", "CardInstructionPreference", "CardEntrancePreference")
private val InstructionPageCounts = listOf("1", "2", "3")
private val EntranceCounts = listOf("1", "2", "3", "4", "5")

/** The demo page of `AppInfoPreference`, listed in its category and found by the search. */
internal val CardPreferenceDemo = Demo(
    id = "card_preference",
    apiName = "AppInfoPreference",
    title = Res.string.demo_card_preference_title,
    category = DemoCategory.Preference,
    page = DemoPage.Variants {
        playground {
            var type by rememberSaveable { mutableIntStateOf(0) }
            var longName by rememberSaveable { mutableStateOf(false) }
            var showIcon by rememberSaveable { mutableStateOf(true) }
            var showCopy by rememberSaveable { mutableStateOf(true) }
            var pageCountIndex by rememberSaveable { mutableIntStateOf(2) }
            var pageTitles by rememberSaveable { mutableStateOf(true) }
            var entranceCountIndex by rememberSaveable { mutableIntStateOf(4) }
            var largeItem by rememberSaveable { mutableStateOf(true) }
            var statusOn by rememberSaveable { mutableStateOf(true) }
            var enabled by rememberSaveable { mutableStateOf(true) }
            val snackbar = LocalDemoSnackbarHostState.current
            val scope = rememberCoroutineScope()
            val onCopy: () -> Unit = { scope.launch { snackbar.showSnackbar(getString(Res.string.demo_card_preference_copied)) } }
            PlaygroundPreview(
                inCard = false,
                contentPadding = if (type == 0) PaddingValues(0.dp) else PaddingValues(horizontal = 16.dp),
            ) {
                when (type) {
                    0 -> AppInfoPreference(
                        appName = stringResource(
                            if (longName) Res.string.demo_card_preference_long_app_name else Res.string.demo_card_preference_app_name,
                        ),
                        appVersion = stringResource(Res.string.demo_card_preference_app_version, "17.0.0"),
                        icon = if (showIcon) {
                            { DemoAppIcon() }
                        } else {
                            null
                        },
                        copyText = if (showCopy) stringResource(Res.string.common_copy) else null,
                        onCopyClick = if (showCopy) onCopy else null,
                    )

                    1 -> {
                        val pages = instructionPages().take(pageCountIndex + 1).map { page ->
                            if (pageTitles) page else page.copy(title = null)
                        }
                        key(pages.size) {
                            CardInstructionPreference(pages = pages)
                        }
                    }

                    else -> {
                        val items = entranceItems().take(entranceCountIndex + 1).mapIndexed { index, item ->
                            item.copy(
                                type = if (largeItem && index == 2) CardEntranceType.Large else CardEntranceType.Small,
                                statusOn = statusOn && index == 1,
                                enabled = enabled,
                            )
                        }
                        CardEntrancePreference(items = items)
                    }
                }
            }
            PlaygroundControls {
                literalChoiceControl(Res.string.demo_pref_component, CardTypes, type) { type = it }
                when (type) {
                    0 -> {
                        switchControl(Res.string.demo_card_preference_long_name, longName) { longName = it }
                        switchControl(Res.string.common_show_icon, showIcon) { showIcon = it }
                        switchControl(Res.string.demo_card_preference_show_copy, showCopy) { showCopy = it }
                    }

                    1 -> {
                        literalChoiceControl(Res.string.demo_card_preference_pages, InstructionPageCounts, pageCountIndex) {
                            pageCountIndex = it
                        }
                        switchControl(Res.string.common_show_title, pageTitles) { pageTitles = it }
                    }

                    else -> {
                        literalChoiceControl(Res.string.common_count, EntranceCounts, entranceCountIndex) {
                            entranceCountIndex = it
                        }
                        switchControl(Res.string.demo_card_preference_large_item, largeItem) { largeItem = it }
                        switchControl(Res.string.demo_card_preference_status_on, statusOn) { statusOn = it }
                        switchControl(Res.string.common_enabled, enabled) { enabled = it }
                    }
                }
            }
        }
        variant(key = "appInfo", title = "AppInfoPreference", inCard = false) {
            val snackbar = LocalDemoSnackbarHostState.current
            val scope = rememberCoroutineScope()
            AppInfoPreference(
                appName = stringResource(Res.string.demo_card_preference_app_name),
                appVersion = stringResource(Res.string.demo_card_preference_app_version, "17.0.0"),
                icon = { DemoAppIcon() },
                copyText = stringResource(Res.string.common_copy),
                onCopyClick = {
                    scope.launch { snackbar.showSnackbar(getString(Res.string.demo_card_preference_copied)) }
                },
            )
        }
        variant(key = "instruction", title = "CardInstructionPreference", inCard = false) {
            CardInstructionPreference(
                pages = instructionPages(),
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        variant(key = "entrance", title = "CardEntrancePreference", inCard = false) {
            val disabled = CardEntranceItem(
                title = stringResource(Res.string.common_disabled),
                summary = stringResource(Res.string.demo_card_preference_unavailable),
                icon = { DemoEntranceIcon(COUIIcons.Settings) },
                enabled = false,
            )
            CardEntrancePreference(
                items = entranceItems() + disabled,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    },
    searchTerms = Res.string.demo_card_preference_search_terms,
)

/**
 * Three instruction pages: one illustration, two titled illustrations, and a long text without a
 * title that scrolls.
 */
@Composable
private fun instructionPages(): List<CardInstructionPage> {
    val primary = COUITheme.colorScheme.primary
    val page1Title = stringResource(Res.string.demo_card_preference_page1_title)
    val page1Summary = stringResource(Res.string.demo_card_preference_page1_summary)
    val page2Title = stringResource(Res.string.demo_card_preference_page2_title)
    val page2Summary = stringResource(Res.string.demo_card_preference_page2_summary)
    val page3Summary = stringResource(Res.string.demo_card_preference_page3_summary)
    val leftEdge = stringResource(Res.string.demo_card_preference_left_edge)
    val rightEdge = stringResource(Res.string.demo_card_preference_right_edge)
    return remember(primary, page1Title, page1Summary, page2Title, page2Summary, page3Summary, leftEdge, rightEdge) {
        listOf(
            CardInstructionPage(
                illustrations = listOf(
                    CardInstructionIllustration(content = { DemoIllustration(primary, CircleShape) }),
                ),
                title = page1Title,
                summary = page1Summary,
            ),
            CardInstructionPage(
                illustrations = listOf(
                    CardInstructionIllustration(
                        content = { DemoIllustration(primary, RoundedCornerShape(16.dp)) },
                        title = leftEdge,
                    ),
                    CardInstructionIllustration(
                        content = { DemoIllustration(primary.copy(alpha = 0.6f), RoundedCornerShape(16.dp)) },
                        title = rightEdge,
                    ),
                ),
                title = page2Title,
                summary = page2Summary,
            ),
            CardInstructionPage(
                illustrations = listOf(
                    CardInstructionIllustration(content = { DemoIllustration(primary.copy(alpha = 0.4f), CircleShape) }),
                ),
                summary = page3Summary,
            ),
        )
    }
}

/**
 * Four entrance cards: small ones with and without a summary, an "on" status and a large one.
 */
@Composable
private fun entranceItems(): List<CardEntranceItem> = listOf(
    CardEntranceItem(
        title = stringResource(Res.string.sample_privacy),
        summary = stringResource(Res.string.demo_card_preference_permissions),
        icon = { DemoEntranceIcon(COUIIcons.Lock) },
        onClick = {},
    ),
    CardEntranceItem(
        title = stringResource(Res.string.demo_card_preference_location),
        summary = stringResource(Res.string.common_on),
        icon = { DemoEntranceIcon(COUIIcons.Location) },
        statusOn = true,
        onClick = {},
    ),
    CardEntranceItem(
        title = stringResource(Res.string.demo_card_preference_backup),
        summary = stringResource(Res.string.demo_card_preference_backup_summary),
        type = CardEntranceType.Large,
        icon = { DemoEntranceIcon(COUIIcons.Backup) },
        onClick = {},
    ),
    CardEntranceItem(
        title = stringResource(Res.string.demo_card_preference_more_settings),
        icon = { DemoEntranceIcon(COUIIcons.Settings) },
        onClick = {},
    ),
)

@Composable
private fun DemoIllustration(
    color: Color,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(96.dp)
            .background(color, shape),
    )
}

@Composable
private fun DemoEntranceIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .background(COUITheme.colorScheme.primary, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = COUITheme.colorScheme.onPrimary,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun DemoAppIcon(modifier: Modifier = Modifier) {
    val primary = COUITheme.colorScheme.primary
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(primary, primary.copy(alpha = 0.55f)))),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = COUIIcons.Settings,
            contentDescription = null,
            tint = COUITheme.colorScheme.onPrimary,
            modifier = Modifier.size(32.dp),
        )
    }
}
