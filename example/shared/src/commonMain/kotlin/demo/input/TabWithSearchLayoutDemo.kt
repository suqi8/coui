// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.input

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.InputField
import io.github.suqi8.coui.kmp.basic.SearchBar
import io.github.suqi8.coui.kmp.basic.TabRow
import io.github.suqi8.coui.kmp.basic.TabWithSearchLayout
import io.github.suqi8.coui.kmp.basic.TabWithSearchLayoutDefaults
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.common_search
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_with_search_layout_search_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_with_search_layout_tab_all
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_with_search_layout_tabs_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_with_search_layout_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_with_search_layout_variant_compact
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_with_search_layout_variant_wide
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_with_search_layout_wide_note
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_with_search_layout_width
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_with_search_layout_width_fit
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_albums
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_music
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_photos
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_videos
import org.jetbrains.compose.resources.stringResource

internal val TabWithSearchLayoutDemo = Demo(
    id = "tab_with_search_layout",
    apiName = "TabWithSearchLayout",
    title = Res.string.demo_tab_with_search_layout_title,
    category = DemoCategory.Input,
    page = DemoPage.Variants { tabWithSearchLayoutPage() },
    keywords = listOf("标签", "搜索", "COUITabWithSearchLayout"),
)

/** The simulated layout widths of the playground; [Dp.Unspecified] fills the card. */
private val LayoutWidths = listOf(Dp.Unspecified, 720.dp, 1080.dp)

/** The tab counts the playground offers. */
private val TabCounts = listOf(2, 3, 4, 5)

private fun LazyListScope.tabWithSearchLayoutPage() {
    variant(key = "compact", title = Res.string.demo_tab_with_search_layout_variant_compact) {
        // Compact: the search bar above the tabs.
        TabWithSearchSample(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
    }
    variant(key = "wide", title = Res.string.demo_tab_with_search_layout_variant_wide) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            InputCaption(
                text = stringResource(Res.string.demo_tab_with_search_layout_wide_note),
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                TabWithSearchSample(modifier = Modifier.width(720.dp))
            }
        }
    }
    playground {
        var widthIndex by rememberSaveable { mutableIntStateOf(0) }
        var tabCountIndex by rememberSaveable { mutableIntStateOf(2) }
        var tabsEnabled by rememberSaveable { mutableStateOf(true) }
        var searchEnabled by rememberSaveable { mutableStateOf(true) }
        val defaultMaterial = LocalCOUIMaterialEffectEnabled.current
        var material by rememberSaveable { mutableStateOf(defaultMaterial) }
        val width = LayoutWidths[widthIndex]

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            if (!width.isSpecified) {
                TabWithSearchSample(
                    modifier = Modifier.fillMaxWidth(),
                    tabCount = TabCounts[tabCountIndex],
                    tabsEnabled = tabsEnabled,
                    searchEnabled = searchEnabled,
                    material = material,
                )
            } else {
                // Wider than the card: scroll sideways to see the side-by-side layout.
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    TabWithSearchSample(
                        modifier = Modifier.width(width),
                        tabCount = TabCounts[tabCountIndex],
                        tabsEnabled = tabsEnabled,
                        searchEnabled = searchEnabled,
                        material = material,
                    )
                }
            }
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = LayoutWidths.map { if (!it.isSpecified) stringResource(Res.string.demo_tab_with_search_layout_width_fit) else "${it.value.toInt()}dp" },
                    selectedIndex = widthIndex,
                    title = stringResource(Res.string.demo_tab_with_search_layout_width),
                    onSelectedIndexChange = { widthIndex = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    items = TabCounts.map { it.toString() },
                    selectedIndex = tabCountIndex,
                    title = stringResource(Res.string.common_count),
                    onSelectedIndexChange = { tabCountIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = tabsEnabled,
                    onCheckedChange = { tabsEnabled = it },
                    title = stringResource(Res.string.demo_tab_with_search_layout_tabs_enabled),
                )
            }
            item {
                SwitchPreference(
                    checked = searchEnabled,
                    onCheckedChange = { searchEnabled = it },
                    title = stringResource(Res.string.demo_tab_with_search_layout_search_enabled),
                )
            }
            item {
                SwitchPreference(
                    checked = material,
                    onCheckedChange = { material = it },
                    title = stringResource(Res.string.common_material),
                )
            }
        }
    }
}

/**
 * A [TabWithSearchLayout] of [tabCount] sample tabs and a search bar. Below 600dp the search bar
 * sits above the tabs; from 600dp they sit side by side.
 */
@Composable
private fun TabWithSearchSample(
    modifier: Modifier = Modifier,
    tabCount: Int = 4,
    tabsEnabled: Boolean = true,
    searchEnabled: Boolean = true,
    material: Boolean = LocalCOUIMaterialEffectEnabled.current,
) {
    val labels = listOf(
        stringResource(Res.string.demo_tab_with_search_layout_tab_all),
        stringResource(Res.string.sample_photos),
        stringResource(Res.string.sample_videos),
        stringResource(Res.string.sample_albums),
        stringResource(Res.string.sample_music),
    )
    val tabs = labels.take(tabCount)
    var selected by rememberSaveable { mutableIntStateOf(0) }
    var query by rememberSaveable { mutableStateOf("") }
    TabWithSearchLayout(
        tabs = {
            TabRow(
                tabs = tabs,
                selectedTabIndex = selected.coerceAtMost(tabs.lastIndex),
                onTabSelected = { selected = it },
                enabled = tabsEnabled,
            )
        },
        modifier = modifier,
    ) {
        SearchBar(
            inputField = {
                InputField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    label = stringResource(Res.string.common_search),
                    enabled = searchEnabled,
                    material = material,
                )
            },
            onExpandedChange = {},
            insideMargin = TabWithSearchLayoutDefaults.SearchBarInsideMargin,
        ) {}
    }
}
