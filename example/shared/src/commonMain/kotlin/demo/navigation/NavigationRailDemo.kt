// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.NavigationRail
import io.github.suqi8.coui.kmp.basic.NavigationRailDisplayMode
import io.github.suqi8.coui.kmp.basic.NavigationRailItem
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Search
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_mode
import io.github.suqi8.coui.kmp.shared.generated.resources.common_search
import io.github.suqi8.coui.kmp.shared.generated.resources.common_selected_value
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_divider
import io.github.suqi8.coui.kmp.shared.generated.resources.common_visible
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_rail_header
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_rail_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_rail_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_rail_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_rail_variant_current
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource
import ui.AppPageList
import ui.AppPageScaffold

/** The demo page of `NavigationRail`, listed in its category and found by the search. */
internal val NavigationRailDemo = Demo(
    id = "navigation_rail",
    apiName = "NavigationRail",
    title = Res.string.demo_navigation_rail_title,
    category = DemoCategory.Navigation,
    page = DemoPage.FullScreen { onBack -> NavigationRailDemoPage(onBack = onBack) },
    searchTerms = Res.string.demo_navigation_rail_search_terms,
)

private val NavigationRailDemoModes = NavigationRailDisplayMode.entries
private val NavigationRailDemoModeNames = NavigationRailDemoModes.map { it.name }

/**
 * The navigation rail beside the page, as the wide-screen shell lays it out.
 */
@Composable
private fun NavigationRailDemoPage(onBack: () -> Unit) {
    var visible by rememberSaveable { mutableStateOf(true) }
    var modeIndex by rememberSaveable { mutableIntStateOf(0) }
    var countIndex by rememberSaveable { mutableIntStateOf(3) }
    var showHeader by rememberSaveable { mutableStateOf(true) }
    var showDivider by rememberSaveable { mutableStateOf(true) }
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val itemCount = countIndex + NavigationDemoMinCount
    val shownSelected = selected.coerceAtMost(itemCount - 1)

    Row(modifier = Modifier.fillMaxSize()) {
        if (visible) {
            NavigationRail(
                header = if (showHeader) {
                    {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = COUIIcons.Search,
                                contentDescription = stringResource(Res.string.common_search),
                                tint = COUITheme.colorScheme.labelPrimary,
                            )
                        }
                    }
                } else {
                    null
                },
                showDivider = showDivider,
                mode = NavigationRailDemoModes[modeIndex],
            ) {
                repeat(itemCount) { index ->
                    NavigationRailItem(
                        selected = shownSelected == index,
                        onClick = { selected = index },
                        icon = NavigationDemoIcons[index],
                        label = stringResource(NavigationDemoLabels[index]),
                    )
                }
            }
        }
        AppPageScaffold(
            title = stringResource(Res.string.demo_navigation_rail_title),
            modifier = Modifier.weight(1f),
            subtitle = "NavigationRail",
            onBack = onBack,
        ) { scope ->
            AppPageList(scope = scope) {
                variant(key = "current", title = Res.string.demo_navigation_rail_variant_current) {
                    BasicComponent(
                        title = stringResource(Res.string.common_selected_value, stringResource(NavigationDemoLabels[shownSelected])),
                        summary = stringResource(Res.string.demo_navigation_rail_hint),
                    )
                }
                item(key = "controls") {
                    PlaygroundControls {
                        item {
                            SwitchPreference(
                                title = stringResource(Res.string.common_visible),
                                checked = visible,
                                onCheckedChange = { visible = it },
                            )
                        }
                        item {
                            OverlayDropdownPreference(
                                items = NavigationRailDemoModeNames,
                                selectedIndex = modeIndex,
                                title = stringResource(Res.string.common_mode),
                                onSelectedIndexChange = { modeIndex = it },
                            )
                        }
                        item {
                            OverlayDropdownPreference(
                                items = NavigationDemoCountOptions,
                                selectedIndex = countIndex,
                                title = stringResource(Res.string.common_count),
                                onSelectedIndexChange = { countIndex = it },
                            )
                        }
                        item {
                            SwitchPreference(
                                title = stringResource(Res.string.demo_navigation_rail_header),
                                checked = showHeader,
                                onCheckedChange = { showHeader = it },
                            )
                        }
                        item {
                            SwitchPreference(
                                title = stringResource(Res.string.common_show_divider),
                                checked = showDivider,
                                onCheckedChange = { showDivider = it },
                            )
                        }
                    }
                }
                navigationFillerContent(rows = 12)
            }
        }
    }
}
