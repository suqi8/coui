// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.preference

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.LocalDemoSnackbarHostState
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Info
import io.github.suqi8.coui.kmp.icon.extended.Refresh
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.LoadingPreferenceCategory
import io.github.suqi8.coui.kmp.preference.PreferenceCategory
import io.github.suqi8.coui.kmp.preference.PreferenceCategoryDefaults
import io.github.suqi8.coui.kmp.preference.PreferenceCategoryMarginStartType
import io.github.suqi8.coui.kmp.preference.PreferenceCategoryTitleType
import io.github.suqi8.coui.kmp.preference.StatusSwitchingPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_clicked
import io.github.suqi8.coui.kmp.shared.generated.resources.common_edit
import io.github.suqi8.coui.kmp.shared.generated.resources.common_large
import io.github.suqi8.coui.kmp.shared.generated.resources.common_loading
import io.github.suqi8.coui.kmp.shared.generated.resources.common_medium
import io.github.suqi8.coui.kmp.shared.generated.resources.common_size
import io.github.suqi8.coui.kmp.shared.generated.resources.common_small
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_component
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_long_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_badge
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_devices
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_earbuds
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_end_widget
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_info
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_manage
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_margin_start
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_not_connected
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_note_badge
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_note_default
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_note_large
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_note_medium
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_note_medium_button
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_note_single_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_note_small_margin
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_note_text_button
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_note_text_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_note_title_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_paired
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_refresh
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_searching
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_show_badge
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_show_loading_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_show_title_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_speaker
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_title_default
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_title_large
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_title_medium
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_title_medium_button
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_title_single_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_title_small_margin
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_title_text_button
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_title_text_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_title_with_badge
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_title_with_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_widget_none
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_widget_single_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_widget_text_button
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_preference_category_widget_text_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_item
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_title
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_settings
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_title
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

private val CategoryTypes = listOf("PreferenceCategory", "LoadingPreferenceCategory")

/** The demo page of `PreferenceCategory`, listed in its category and found by the search. */
internal val PreferenceCategoryDemo = Demo(
    id = "preference_category",
    apiName = "PreferenceCategory",
    title = Res.string.demo_preference_category_title,
    category = DemoCategory.Preference,
    page = DemoPage.Variants {
        playground {
            var type by rememberSaveable { mutableIntStateOf(0) }
            var titleType by rememberSaveable { mutableIntStateOf(0) }
            var smallMargin by rememberSaveable { mutableStateOf(false) }
            var longTitle by rememberSaveable { mutableStateOf(false) }
            var titleIcon by rememberSaveable { mutableStateOf(false) }
            var badge by rememberSaveable { mutableStateOf(false) }
            var widget by rememberSaveable { mutableIntStateOf(1) }
            var loading by rememberSaveable { mutableStateOf(true) }
            var loadingText by rememberSaveable { mutableStateOf(true) }
            val snackbar = LocalDemoSnackbarHostState.current
            val scope = rememberCoroutineScope()
            val title = stringResource(if (longTitle) Res.string.sample_long_title else Res.string.sample_title)
            val resolvedTitleType = PreferenceCategoryTitleType.entries[titleType]
            val marginStartType = if (smallMargin) PreferenceCategoryMarginStartType.Small else PreferenceCategoryMarginStartType.Large
            val onWidgetClick: (StringResource) -> Unit = { label ->
                scope.launch { snackbar.showSnackbar(getString(Res.string.common_clicked, getString(label))) }
            }
            PlaygroundPreview(inCard = false, contentPadding = PaddingValues(0.dp)) {
                Column {
                    if (type == 0) {
                        PreferenceCategory(
                            title = title,
                            titleType = resolvedTitleType,
                            marginStartType = marginStartType,
                            titleBadge = if (badge) stringResource(Res.string.demo_preference_category_badge) else null,
                            titleIcon = if (titleIcon) {
                                {
                                    PreferenceCategoryDefaults.TitleIcon(
                                        imageVector = COUIIcons.Info,
                                        contentDescription = stringResource(Res.string.demo_preference_category_info),
                                        onClick = { onWidgetClick(Res.string.demo_preference_category_info) },
                                    )
                                }
                            } else {
                                null
                            },
                            endWidget = categoryEndWidget(widget, onWidgetClick),
                        )
                    } else {
                        LoadingPreferenceCategory(
                            title = title,
                            loading = loading,
                            loadingText = if (loadingText) stringResource(Res.string.demo_preference_category_searching) else null,
                            titleType = resolvedTitleType,
                            marginStartType = marginStartType,
                            endContent = categoryEndWidget(widget, onWidgetClick),
                        )
                    }
                    Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                        CardListGroup {
                            repeat(2) { index ->
                                item(key = index) {
                                    ArrowPreference(
                                        title = stringResource(Res.string.sample_item, index + 1),
                                        onClick = {},
                                    )
                                }
                            }
                        }
                    }
                }
            }
            PlaygroundControls {
                literalChoiceControl(Res.string.demo_pref_component, CategoryTypes, type) { type = it }
                if (type == 1) {
                    switchControl(Res.string.common_loading, loading) { loading = it }
                    switchControl(Res.string.demo_preference_category_show_loading_text, loadingText) { loadingText = it }
                }
                choiceControl(
                    Res.string.common_size,
                    listOf(Res.string.common_small, Res.string.common_medium, Res.string.common_large),
                    titleType,
                ) { titleType = it }
                switchControl(Res.string.demo_preference_category_margin_start, smallMargin) { smallMargin = it }
                switchControl(Res.string.demo_pref_long_title, longTitle) { longTitle = it }
                if (type == 0) {
                    switchControl(Res.string.demo_preference_category_show_title_icon, titleIcon) { titleIcon = it }
                    switchControl(Res.string.demo_preference_category_show_badge, badge) { badge = it }
                }
                choiceControl(
                    Res.string.demo_preference_category_end_widget,
                    listOf(
                        Res.string.demo_preference_category_widget_none,
                        Res.string.demo_preference_category_widget_text_button,
                        Res.string.demo_preference_category_widget_single_icon,
                        Res.string.demo_preference_category_widget_text_icon,
                    ),
                    widget,
                ) { widget = it }
            }
        }
        categoryVariants()
        item(key = "loading") {
            LoadingCategoryExample()
        }
    },
    searchTerms = Res.string.demo_preference_category_search_terms,
    isNew = true,
)

/**
 * The end widget for the playground's choice: none, a text button, a single icon or text with an
 * icon.
 */
private fun categoryEndWidget(
    widget: Int,
    onClick: (StringResource) -> Unit,
): (@Composable RowScope.() -> Unit)? = when (widget) {
    1 -> {
        {
            PreferenceCategoryDefaults.TextButton(
                text = stringResource(Res.string.common_edit),
                onClick = { onClick(Res.string.common_edit) },
            )
        }
    }

    2 -> {
        {
            PreferenceCategoryDefaults.SingleIcon(
                imageVector = COUIIcons.Settings,
                contentDescription = stringResource(Res.string.sample_settings),
                onClick = { onClick(Res.string.sample_settings) },
            )
        }
    }

    3 -> {
        {
            PreferenceCategoryDefaults.TextWithIcon(
                text = stringResource(Res.string.demo_preference_category_manage),
                icon = COUIIcons.Refresh,
                onClick = { onClick(Res.string.demo_preference_category_manage) },
            )
        }
    }

    else -> null
}

/**
 * Every title style and end widget of [PreferenceCategory], each above a card describing it.
 */
private fun LazyListScope.categoryVariants() {
    item(key = "default") {
        PreferenceCategory(title = stringResource(Res.string.demo_preference_category_title_default))
        CategoryNoteCard(Res.string.demo_preference_category_note_default)
    }
    item(key = "medium") {
        PreferenceCategory(
            title = stringResource(Res.string.demo_preference_category_title_medium),
            titleType = PreferenceCategoryTitleType.Medium,
        )
        CategoryNoteCard(Res.string.demo_preference_category_note_medium)
    }
    item(key = "large") {
        PreferenceCategory(
            title = stringResource(Res.string.demo_preference_category_title_large),
            titleType = PreferenceCategoryTitleType.Large,
        )
        CategoryNoteCard(Res.string.demo_preference_category_note_large)
    }
    item(key = "smallMargin") {
        PreferenceCategory(
            title = stringResource(Res.string.demo_preference_category_title_small_margin),
            marginStartType = PreferenceCategoryMarginStartType.Small,
        )
        CategoryNoteCard(Res.string.demo_preference_category_note_small_margin)
    }
    item(key = "titleIcon") {
        val onClick = rememberClickedSnackbar(Res.string.demo_preference_category_info)
        PreferenceCategory(
            title = stringResource(Res.string.demo_preference_category_title_with_icon),
            titleIcon = {
                PreferenceCategoryDefaults.TitleIcon(
                    imageVector = COUIIcons.Info,
                    contentDescription = stringResource(Res.string.demo_preference_category_info),
                    onClick = onClick,
                )
            },
        )
        CategoryNoteCard(Res.string.demo_preference_category_note_title_icon)
    }
    item(key = "badge") {
        PreferenceCategory(
            title = stringResource(Res.string.demo_preference_category_title_with_badge),
            titleBadge = stringResource(Res.string.demo_preference_category_badge),
        )
        CategoryNoteCard(Res.string.demo_preference_category_note_badge)
    }
    item(key = "textButton") {
        val onClick = rememberClickedSnackbar(Res.string.common_edit)
        PreferenceCategory(
            title = stringResource(Res.string.demo_preference_category_title_text_button),
            endWidget = {
                PreferenceCategoryDefaults.TextButton(text = stringResource(Res.string.common_edit), onClick = onClick)
            },
        )
        CategoryNoteCard(Res.string.demo_preference_category_note_text_button)
    }
    item(key = "singleIcon") {
        val onClick = rememberClickedSnackbar(Res.string.sample_settings)
        PreferenceCategory(
            title = stringResource(Res.string.demo_preference_category_title_single_icon),
            endWidget = {
                PreferenceCategoryDefaults.SingleIcon(
                    imageVector = COUIIcons.Settings,
                    contentDescription = stringResource(Res.string.sample_settings),
                    onClick = onClick,
                )
            },
        )
        CategoryNoteCard(Res.string.demo_preference_category_note_single_icon)
    }
    item(key = "textIcon") {
        val onClick = rememberClickedSnackbar(Res.string.demo_preference_category_manage)
        PreferenceCategory(
            title = stringResource(Res.string.demo_preference_category_title_text_icon),
            endWidget = {
                PreferenceCategoryDefaults.TextWithIcon(
                    text = stringResource(Res.string.demo_preference_category_manage),
                    icon = COUIIcons.Refresh,
                    onClick = onClick,
                )
            },
        )
        CategoryNoteCard(Res.string.demo_preference_category_note_text_icon)
    }
    item(key = "mediumButton") {
        val onClick = rememberClickedSnackbar(Res.string.demo_preference_category_manage)
        PreferenceCategory(
            title = stringResource(Res.string.demo_preference_category_title_medium_button),
            titleType = PreferenceCategoryTitleType.Medium,
            endWidget = {
                PreferenceCategoryDefaults.TextButton(
                    text = stringResource(Res.string.demo_preference_category_manage),
                    onClick = onClick,
                )
            },
        )
        CategoryNoteCard(Res.string.demo_preference_category_note_medium_button)
    }
}

/**
 * Returns a click handler that shows "Clicked: [label]" in the page snackbar.
 */
@Composable
private fun rememberClickedSnackbar(label: StringResource): () -> Unit {
    val snackbar = LocalDemoSnackbarHostState.current
    val scope = rememberCoroutineScope()
    return remember(snackbar, scope, label) {
        val handler: () -> Unit = {
            scope.launch { snackbar.showSnackbar(getString(Res.string.common_clicked, getString(label))) }
        }
        handler
    }
}

@Composable
private fun CategoryNoteCard(
    text: StringResource,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        insideMargin = PaddingValues(16.dp),
    ) {
        Text(stringResource(text))
    }
}

/**
 * A category that spins while it "searches", then offers a refresh action in place of the spinner.
 */
@Composable
private fun LoadingCategoryExample(modifier: Modifier = Modifier) {
    var scanning by remember { mutableStateOf(true) }
    LaunchedEffect(scanning) {
        if (scanning) {
            delay(3000)
            scanning = false
        }
    }
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        LoadingPreferenceCategory(
            title = stringResource(Res.string.demo_preference_category_devices),
            loading = scanning,
            loadingText = stringResource(Res.string.demo_preference_category_searching),
            endContent = {
                Text(
                    text = stringResource(Res.string.demo_preference_category_refresh),
                    modifier = Modifier.clickable { scanning = true },
                    style = COUITheme.textStyles.bodyXS.copy(fontWeight = FontWeight.Medium),
                    color = COUITheme.colorScheme.primary,
                )
            },
        )
        Card(modifier = Modifier.padding(horizontal = 16.dp)) {
            CardListGroup {
                item {
                    StatusSwitchingPreference(
                        title = stringResource(Res.string.demo_preference_category_speaker),
                        status = stringResource(Res.string.demo_preference_category_paired),
                    )
                }
                item {
                    StatusSwitchingPreference(
                        title = stringResource(Res.string.demo_preference_category_earbuds),
                        status = stringResource(Res.string.demo_preference_category_not_connected),
                    )
                }
            }
        }
    }
}
