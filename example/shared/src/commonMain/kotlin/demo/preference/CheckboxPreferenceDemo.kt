// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.preference

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.LocalDemoSnackbarHostState
import demo.framework.PlaygroundControls
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.preference.CheckboxLocation
import io.github.suqi8.coui.kmp.preference.CheckboxPreference
import io.github.suqi8.coui.kmp.preference.CheckboxPreferenceCategory
import io.github.suqi8.coui.kmp.preference.CheckboxWithDividerPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_end
import io.github.suqi8.coui.kmp.shared.generated.resources.common_position
import io.github.suqi8.coui.kmp.shared.generated.resources.common_selected_value
import io.github.suqi8.coui.kmp.shared.generated.resources.common_start
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_checkbox_preference_at_end
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_checkbox_preference_at_start
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_checkbox_preference_category_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_checkbox_preference_choose_albums
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_checkbox_preference_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_checkbox_preference_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_both_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_component
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_opened_page
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_contacts
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_messages
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_photos
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

private val CheckboxTypes = listOf("CheckboxPreference", "CheckboxWithDividerPreference")

/** The demo page of `CheckboxPreference`, listed in its category and found by the search. */
internal val CheckboxPreferenceDemo = Demo(
    id = "checkbox_preference",
    apiName = "CheckboxPreference",
    title = Res.string.demo_checkbox_preference_title,
    category = DemoCategory.Preference,
    page = DemoPage.Variants {
        playground {
            val state = rememberPreferenceRowState()
            var type by rememberSaveable { mutableIntStateOf(0) }
            var location by rememberSaveable { mutableIntStateOf(1) }
            var checked by rememberSaveable { mutableStateOf(true) }
            val snackbar = LocalDemoSnackbarHostState.current
            val scope = rememberCoroutineScope()
            val title = state.title()
            val summary = state.summary()
            val openedMessage = stringResource(Res.string.demo_pref_opened_page, title)
            val startAction: (@Composable () -> Unit)? = if (state.showIcon) {
                { PreferenceStartIcon() }
            } else {
                null
            }
            PreferencePreview {
                item {
                    if (type == 0) {
                        CheckboxPreference(
                            title = title,
                            checked = checked,
                            onCheckedChange = { checked = it },
                            summary = summary,
                            startAction = startAction,
                            endActions = { if (state.showEndText) PreferenceEndText(enabled = state.enabled) },
                            checkboxLocation = if (location == 0) CheckboxLocation.Start else CheckboxLocation.End,
                            enabled = state.enabled,
                        )
                    } else {
                        CheckboxWithDividerPreference(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            title = title,
                            onClick = { scope.launch { snackbar.showSnackbar(openedMessage) } },
                            summary = summary,
                            startAction = startAction,
                            endActions = { if (state.showEndText) PreferenceEndText(enabled = state.enabled) },
                            enabled = state.enabled,
                        )
                    }
                }
            }
            PlaygroundControls {
                literalChoiceControl(Res.string.demo_pref_component, CheckboxTypes, type) { type = it }
                if (type == 0) {
                    choiceControl(
                        Res.string.common_position,
                        listOf(Res.string.common_start, Res.string.common_end),
                        location,
                    ) { location = it }
                }
                preferenceRowControls(state)
            }
        }
        variant(key = "checkbox", title = "CheckboxPreference") {
            CheckboxRows()
        }
        variant(key = "divider", title = "CheckboxWithDividerPreference") {
            CheckboxWithDividerRows()
        }
        variant(key = "category", title = "CheckboxPreferenceCategory", inCard = false) {
            CheckboxCategoryGroup()
        }
    },
    searchTerms = Res.string.demo_checkbox_preference_search_terms,
)

/**
 * Checkbox rows with the box at either end, and a disabled row.
 */
@Composable
private fun CheckboxRows() {
    var endChecked by rememberSaveable { mutableStateOf(false) }
    var startChecked by rememberSaveable { mutableStateOf(true) }
    CardListGroup {
        item {
            CheckboxPreference(
                title = stringResource(Res.string.demo_checkbox_preference_at_end),
                checked = endChecked,
                onCheckedChange = { endChecked = it },
                endActions = { PreferenceEndText(text = endChecked.toString()) },
                checkboxLocation = CheckboxLocation.End,
            )
        }
        item {
            CheckboxPreference(
                title = stringResource(Res.string.demo_checkbox_preference_at_start),
                checked = startChecked,
                onCheckedChange = { startChecked = it },
                summary = stringResource(Res.string.common_selected_value, startChecked.toString()),
                checkboxLocation = CheckboxLocation.Start,
            )
        }
        item {
            CheckboxPreference(
                title = stringResource(Res.string.common_disabled),
                checked = true,
                onCheckedChange = {},
                enabled = false,
            )
        }
    }
}

/**
 * Rows whose main area opens a page and whose checkbox toggles the setting.
 */
@Composable
private fun CheckboxWithDividerRows() {
    var photos by rememberSaveable { mutableStateOf(true) }
    var contacts by rememberSaveable { mutableStateOf(false) }
    val snackbar = LocalDemoSnackbarHostState.current
    val scope = rememberCoroutineScope()
    val photosTitle = stringResource(Res.string.sample_photos)
    val contactsTitle = stringResource(Res.string.sample_contacts)
    CardListGroup {
        item {
            CheckboxWithDividerPreference(
                checked = photos,
                onCheckedChange = { photos = it },
                title = photosTitle,
                summary = stringResource(Res.string.demo_checkbox_preference_choose_albums),
                onClick = {
                    scope.launch { snackbar.showSnackbar(getString(Res.string.demo_pref_opened_page, photosTitle)) }
                },
            )
        }
        item {
            CheckboxWithDividerPreference(
                checked = contacts,
                onCheckedChange = { contacts = it },
                title = contactsTitle,
                onClick = {
                    scope.launch { snackbar.showSnackbar(getString(Res.string.demo_pref_opened_page, contactsTitle)) }
                },
            )
        }
        item {
            CheckboxWithDividerPreference(
                checked = true,
                onCheckedChange = {},
                title = stringResource(Res.string.common_disabled),
                summary = stringResource(Res.string.demo_pref_both_disabled),
                onClick = {},
                enabled = false,
            )
        }
    }
}

/**
 * A category title whose checkbox selects every row below it; it turns indeterminate when only
 * some rows are checked.
 */
@Composable
private fun ColumnScope.CheckboxCategoryGroup() {
    val options = listOf(
        stringResource(Res.string.sample_photos),
        stringResource(Res.string.sample_contacts),
        stringResource(Res.string.sample_messages),
    )
    val checked = remember { mutableStateListOf(true, false, false) }
    val categoryState = when (checked.count { it }) {
        0 -> ToggleableState.Off
        checked.size -> ToggleableState.On
        else -> ToggleableState.Indeterminate
    }
    CheckboxPreferenceCategory(
        title = stringResource(Res.string.demo_checkbox_preference_category_title),
        state = categoryState,
        onStateChange = { newState ->
            val value = newState == ToggleableState.On
            for (i in checked.indices) checked[i] = value
        },
    )
    Card(modifier = Modifier.padding(horizontal = 16.dp)) {
        CardListGroup {
            options.forEachIndexed { index, option ->
                item(key = index) {
                    CheckboxPreference(
                        title = option,
                        checked = checked[index],
                        onCheckedChange = { checked[index] = it },
                    )
                }
            }
        }
    }
}
