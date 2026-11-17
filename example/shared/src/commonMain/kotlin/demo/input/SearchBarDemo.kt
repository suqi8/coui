// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.input

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.CancelButton
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.InputField
import io.github.suqi8.coui.kmp.basic.SearchBar
import io.github.suqi8.coui.kmp.basic.SearchBarDefaults
import io.github.suqi8.coui.kmp.basic.SearchBarType
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Back
import io.github.suqi8.coui.kmp.icon.extended.Mic
import io.github.suqi8.coui.kmp.icon.extended.Scan
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_back
import io.github.suqi8.coui.kmp.shared.generated.resources.common_cancel
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.common_search
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_over_wallpaper
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_cancel
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_cancel_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_hint_dialog
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_hint_search
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_hint_slider
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_hints
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_hints_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_navigation
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_navigation_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_outer
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_scan
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_suggestion
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_type
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_variant_buttons
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_variant_default
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_variant_non_instant
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_voice
import org.jetbrains.compose.resources.stringResource

/** The demo page of `SearchBar`, listed in its category and found by the search. */
internal val SearchBarDemo = Demo(
    id = "search_bar",
    apiName = "SearchBar",
    title = Res.string.demo_search_bar_title,
    category = DemoCategory.Input,
    page = DemoPage.Variants { searchBarPage() },
    keywords = listOf("InputField", "CancelButton", "COUISearchBar"),
    searchTerms = Res.string.demo_search_bar_search_terms,
)

/** The number of suggestions the expanded bars list. */
private const val SUGGESTION_COUNT = 4

private fun LazyListScope.searchBarPage() {
    // The search item of the old components home: hint carousel, cancel button and suggestions.
    variant(key = "default", title = Res.string.demo_search_bar_variant_default, inCard = false) {
        DemoSearchBar(showHints = true)
    }
    playground {
        var typeIndex by rememberSaveable { mutableIntStateOf(SearchBarType.Instant.ordinal) }
        var showHints by rememberSaveable { mutableStateOf(true) }
        var showCancel by rememberSaveable { mutableStateOf(true) }
        var showVoice by rememberSaveable { mutableStateOf(false) }
        var showOuter by rememberSaveable { mutableStateOf(false) }
        var showNavigation by rememberSaveable { mutableStateOf(false) }
        val defaultMaterial = LocalCOUIMaterialEffectEnabled.current
        var material by rememberSaveable { mutableStateOf(defaultMaterial) }
        var overImage by rememberSaveable { mutableStateOf(false) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        val type = SearchBarType.entries[typeIndex]

        PlaygroundPreview(inCard = false, contentPadding = PaddingValues(0.dp)) {
            MaterialStage(
                overImage = overImage,
                modifier = if (overImage) Modifier.padding(horizontal = 16.dp) else Modifier,
            ) {
                DemoSearchBar(
                    type = type,
                    enabled = enabled,
                    material = material,
                    showHints = showHints,
                    showCancel = showCancel,
                    showVoice = showVoice,
                    showOuter = showOuter,
                    showNavigation = showNavigation,
                )
            }
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = SearchBarType.entries.map { it.name },
                    selectedIndex = typeIndex,
                    title = stringResource(Res.string.demo_search_bar_type),
                    onSelectedIndexChange = { typeIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = showHints,
                    onCheckedChange = { showHints = it },
                    title = stringResource(Res.string.demo_search_bar_hints),
                    summary = stringResource(Res.string.demo_search_bar_hints_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = showCancel,
                    onCheckedChange = { showCancel = it },
                    title = stringResource(Res.string.demo_search_bar_cancel),
                    summary = stringResource(Res.string.demo_search_bar_cancel_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = showVoice,
                    onCheckedChange = { showVoice = it },
                    title = stringResource(Res.string.demo_search_bar_voice),
                )
            }
            item {
                SwitchPreference(
                    checked = showOuter,
                    onCheckedChange = { showOuter = it },
                    title = stringResource(Res.string.demo_search_bar_outer),
                )
            }
            item {
                SwitchPreference(
                    checked = showNavigation,
                    onCheckedChange = { showNavigation = it },
                    title = stringResource(Res.string.demo_search_bar_navigation),
                    summary = stringResource(Res.string.demo_search_bar_navigation_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = material,
                    onCheckedChange = { material = it },
                    title = stringResource(Res.string.common_material),
                )
            }
            item {
                SwitchPreference(
                    checked = overImage,
                    onCheckedChange = { overImage = it },
                    title = stringResource(Res.string.demo_input_over_wallpaper),
                )
            }
            item {
                SwitchPreference(
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                    title = stringResource(Res.string.common_enabled),
                )
            }
        }
    }
    variant(key = "buttons", title = Res.string.demo_search_bar_variant_buttons, inCard = false) {
        DemoSearchBar(showVoice = true, showOuter = true)
    }
    variant(key = "non_instant", title = Res.string.demo_search_bar_variant_non_instant, inCard = false) {
        DemoSearchBar(type = SearchBarType.NonInstant, showNavigation = true, showOuter = true)
    }
    variant(key = "disabled", title = Res.string.common_disabled, inCard = false) {
        DemoSearchBar(enabled = false, showVoice = true)
    }
}

/**
 * A [SearchBar] with an [InputField], an optional [CancelButton] and a suggestion list while it is
 * expanded. It keeps its own query and expanded state.
 */
@Composable
private fun DemoSearchBar(
    modifier: Modifier = Modifier,
    type: SearchBarType = SearchBarType.Instant,
    enabled: Boolean = true,
    material: Boolean = LocalCOUIMaterialEffectEnabled.current,
    showHints: Boolean = false,
    showCancel: Boolean = true,
    showVoice: Boolean = false,
    showOuter: Boolean = false,
    showNavigation: Boolean = false,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val hintSearch = stringResource(Res.string.demo_search_bar_hint_search)
    val hintSlider = stringResource(Res.string.demo_search_bar_hint_slider)
    val hintDialog = stringResource(Res.string.demo_search_bar_hint_dialog)
    // COUIHintAnimationLayout: the hint carousel while the field is empty.
    val hints = remember(showHints, hintSearch, hintSlider, hintDialog) {
        if (showHints) listOf(hintSearch, hintSlider, hintDialog) else emptyList()
    }
    val suggestions = List(SUGGESTION_COUNT) { stringResource(Res.string.demo_search_bar_suggestion, it + 1) }
    val shown = if (query.isBlank()) suggestions else suggestions.filter { it.contains(query, ignoreCase = true) }

    val voiceDescription = stringResource(Res.string.demo_search_bar_voice)
    val voiceButton: (@Composable () -> Unit)? = if (showVoice) {
        {
            // A 36dp flat icon button inside the capsule (COUI setInnerPrimaryButton).
            IconButton(
                onClick = {},
                enabled = enabled,
                material = false,
                minHeight = 36.dp,
                minWidth = 36.dp,
            ) {
                Icon(imageVector = COUIIcons.Mic, contentDescription = voiceDescription)
            }
        }
    } else {
        null
    }
    val scanDescription = stringResource(Res.string.demo_search_bar_scan)
    val outerButton: (@Composable () -> Unit)? = if (showOuter) {
        {
            IconButton(
                onClick = {},
                enabled = enabled,
                material = material,
                minHeight = SearchBarDefaults.OuterButtonSize,
                minWidth = SearchBarDefaults.OuterButtonSize,
            ) {
                Icon(imageVector = COUIIcons.Scan, contentDescription = scanDescription)
            }
        }
    } else {
        null
    }
    val backDescription = stringResource(Res.string.common_back)
    val navigationButton: (@Composable () -> Unit)? = if (showNavigation) {
        {
            IconButton(
                onClick = { expanded = false },
                enabled = enabled,
                material = material,
                minHeight = SearchBarDefaults.OuterButtonSize,
                minWidth = SearchBarDefaults.OuterButtonSize,
            ) {
                Icon(imageVector = COUIIcons.Back, contentDescription = backDescription)
            }
        }
    } else {
        null
    }
    val cancelText = stringResource(Res.string.common_cancel)
    val cancelButton: (@Composable () -> Unit)? = if (showCancel) {
        {
            CancelButton(
                onClick = {
                    query = ""
                    expanded = false
                },
                text = cancelText,
                enabled = enabled,
                material = material,
            )
        }
    } else {
        null
    }

    SearchBar(
        inputField = {
            InputField(
                query = query,
                onQueryChange = { query = it },
                onSearch = { expanded = false },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                label = stringResource(Res.string.common_search),
                enabled = enabled,
                material = material,
                innerPrimaryButton = voiceButton,
                hints = hints,
            )
        },
        onExpandedChange = { expanded = it },
        modifier = modifier,
        expanded = expanded,
        type = type,
        navigationButton = navigationButton,
        outerPrimaryButton = outerButton,
        outsideEndAction = cancelButton,
    ) {
        if (shown.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                shown.forEach { suggestion ->
                    BasicComponent(
                        title = suggestion,
                        onClick = {
                            query = suggestion
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}
