// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.input

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.DemoSection
import demo.framework.PlaygroundControls
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.CancelButton
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.InputField
import io.github.suqi8.coui.kmp.basic.SearchBar
import io.github.suqi8.coui.kmp.basic.ToolbarFloatingSearchBar
import io.github.suqi8.coui.kmp.basic.ToolbarSearchAnchor
import io.github.suqi8.coui.kmp.basic.rememberToolbarSearchState
import io.github.suqi8.coui.kmp.basic.toolbarSearchEntry
import io.github.suqi8.coui.kmp.basic.toolbarSearchItem
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Search
import io.github.suqi8.coui.kmp.icon.extended.Tune
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_cancel
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.common_more
import io.github.suqi8.coui.kmp.shared.generated.resources.common_search
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_cancel
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_hint_dialog
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_hint_search
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_hint_slider
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_bar_hints
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_toolbar_search_expand
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_toolbar_search_expand_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_toolbar_search_material_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_toolbar_search_no_result
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_toolbar_search_results
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_toolbar_search_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_toolbar_search_top_bar
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_item
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource
import ui.AppPageList
import ui.AppPageScaffold
import ui.AppPageTopBarStyle

internal val ToolbarSearchDemo = Demo(
    id = "toolbar_search",
    apiName = "ToolbarSearch",
    title = Res.string.demo_toolbar_search_title,
    category = DemoCategory.Input,
    page = DemoPage.FullScreen { onBack -> ToolbarSearchPage(onBack = onBack) },
    keywords = listOf("搜索", "search", "ToolbarFloatingSearchBar", "showInToolBar"),
    isNew = true,
)

/** The number of sample results. */
private const val RESULT_COUNT = 30

/** The top bar styles the page offers. */
private val TopBarStyles = listOf(AppPageTopBarStyle.Large, AppPageTopBarStyle.Small)

/**
 * A search bar that expands out of the toolbar's search icon, the way COUI 17
 * `COUISearchBar.showInToolBar` does: the bar wraps the page's top bar in a [ToolbarSearchAnchor],
 * floats a [ToolbarFloatingSearchBar] over it and fades the title with
 * `ToolbarSearchState.toolbarTitleAlpha`.
 */
@Composable
private fun ToolbarSearchPage(onBack: () -> Unit) {
    val searchState = rememberToolbarSearchState()
    var query by rememberSaveable { mutableStateOf("") }
    val defaultMaterial = LocalCOUIMaterialEffectEnabled.current
    var material by rememberSaveable { mutableStateOf(defaultMaterial) }
    var showCancel by rememberSaveable { mutableStateOf(true) }
    var showHints by rememberSaveable { mutableStateOf(false) }
    var styleIndex by rememberSaveable { mutableIntStateOf(0) }

    val hintSearch = stringResource(Res.string.demo_search_bar_hint_search)
    val hintSlider = stringResource(Res.string.demo_search_bar_hint_slider)
    val hintDialog = stringResource(Res.string.demo_search_bar_hint_dialog)
    val hints = remember(showHints, hintSearch, hintSlider, hintDialog) {
        if (showHints) listOf(hintSearch, hintSlider, hintDialog) else emptyList()
    }
    val cancelText = stringResource(Res.string.common_cancel)
    val cancelButton: (@Composable () -> Unit)? = if (showCancel) {
        {
            CancelButton(
                onClick = {
                    query = ""
                    searchState.collapse()
                },
                text = cancelText,
                material = material,
            )
        }
    } else {
        null
    }
    val onExpandedChange: (Boolean) -> Unit = { if (it) searchState.expand() else searchState.collapse() }
    val titleColor = COUITheme.colorScheme.labelPrimary

    AppPageScaffold(
        title = stringResource(Res.string.demo_toolbar_search_title),
        subtitle = "ToolbarSearch",
        onBack = onBack,
        navigationIconModifier = Modifier.toolbarSearchItem(searchState),
        topBarStyle = TopBarStyles[styleIndex],
        titleColor = titleColor.copy(alpha = titleColor.alpha * searchState.toolbarTitleAlpha),
        actions = {
            IconButton(
                onClick = { searchState.expand() },
                modifier = Modifier.toolbarSearchEntry(searchState),
            ) {
                Icon(imageVector = COUIIcons.Basic.Search, contentDescription = stringResource(Res.string.common_search))
            }
            IconButton(
                onClick = {},
                modifier = Modifier.toolbarSearchItem(searchState),
            ) {
                Icon(imageVector = COUIIcons.Tune, contentDescription = stringResource(Res.string.common_more))
            }
        },
        topBarContainer = { topBar ->
            Box {
                ToolbarSearchAnchor(state = searchState) {
                    topBar()
                }
                ToolbarFloatingSearchBar(
                    state = searchState,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                ) {
                    SearchBar(
                        inputField = {
                            InputField(
                                query = query,
                                onQueryChange = { query = it },
                                onSearch = {},
                                expanded = searchState.expanded,
                                onExpandedChange = onExpandedChange,
                                label = stringResource(Res.string.common_search),
                                material = material,
                                hints = hints,
                            )
                        },
                        onExpandedChange = onExpandedChange,
                        modifier = Modifier.fillMaxWidth(),
                        expanded = searchState.expanded,
                        outsideEndAction = cancelButton,
                    ) {}
                }
            }
        },
    ) { scope ->
        AppPageList(scope = scope) {
            item(key = "parameters") {
                PlaygroundControls {
                    item {
                        ArrowPreference(
                            title = stringResource(Res.string.demo_toolbar_search_expand),
                            summary = stringResource(Res.string.demo_toolbar_search_expand_summary),
                            onClick = { searchState.expand() },
                        )
                    }
                    item {
                        SwitchPreference(
                            checked = material,
                            onCheckedChange = { material = it },
                            title = stringResource(Res.string.common_material),
                            summary = stringResource(Res.string.demo_toolbar_search_material_summary),
                        )
                    }
                    item {
                        SwitchPreference(
                            checked = showCancel,
                            onCheckedChange = { showCancel = it },
                            title = stringResource(Res.string.demo_search_bar_cancel),
                        )
                    }
                    item {
                        SwitchPreference(
                            checked = showHints,
                            onCheckedChange = { showHints = it },
                            title = stringResource(Res.string.demo_search_bar_hints),
                        )
                    }
                    item {
                        OverlayDropdownPreference(
                            items = TopBarStyles.map { it.name },
                            selectedIndex = styleIndex,
                            title = stringResource(Res.string.demo_toolbar_search_top_bar),
                            onSelectedIndexChange = { styleIndex = it },
                        )
                    }
                }
            }
            item(key = "results") {
                val results = List(RESULT_COUNT) { stringResource(Res.string.sample_item, it + 1) }
                val shown = if (query.isBlank()) results else results.filter { it.contains(query, ignoreCase = true) }
                DemoSection(title = stringResource(Res.string.demo_toolbar_search_results)) {
                    if (shown.isEmpty()) {
                        BasicComponent(title = stringResource(Res.string.demo_toolbar_search_no_result))
                    } else {
                        shown.forEach { result ->
                            BasicComponent(
                                title = result,
                                onClick = { query = result },
                            )
                        }
                    }
                }
            }
        }
    }
}
