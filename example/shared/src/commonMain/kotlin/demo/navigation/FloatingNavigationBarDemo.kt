// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBar
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBarItem
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Search
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_alignment
import io.github.suqi8.coui.kmp.shared.generated.resources.common_center
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_end
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.common_search
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_divider
import io.github.suqi8.coui.kmp.shared.generated.resources.common_start
import io.github.suqi8.coui.kmp.shared.generated.resources.common_visible
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_navigation_bar_expansion
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_navigation_bar_expansion_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_navigation_bar_large_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_navigation_bar_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_badges
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource
import ui.AppPageList
import ui.AppPageScaffold

internal val FloatingNavigationBarDemo = Demo(
    id = "floating_navigation_bar",
    apiName = "FloatingNavigationBar",
    title = Res.string.demo_floating_navigation_bar_title,
    category = DemoCategory.Navigation,
    page = DemoPage.FullScreen { onBack -> FloatingNavigationBarDemoPage(onBack = onBack) },
    keywords = listOf("tab", "底栏", "capsule", "胶囊"),
    isNew = true,
)

private val FloatingNavigationBarDemoAlignments: List<Alignment.Horizontal> = listOf(
    Alignment.CenterHorizontally,
    Alignment.Start,
    Alignment.End,
)

/**
 * The COUI 17 floating capsule bar in the page's bottom bar slot. The scaffold hands it the page
 * backdrop through `LocalMaterialBackdrop`, so its material blurs the page scrolling beneath it.
 */
@Composable
private fun FloatingNavigationBarDemoPage(onBack: () -> Unit) {
    var visible by rememberSaveable { mutableStateOf(true) }
    var alignmentIndex by rememberSaveable { mutableIntStateOf(0) }
    var countIndex by rememberSaveable { mutableIntStateOf(2) }
    var badges by rememberSaveable { mutableStateOf(true) }
    var largeIcon by rememberSaveable { mutableStateOf(true) }
    var expansionShown by rememberSaveable { mutableStateOf(false) }
    var material by rememberSaveable { mutableStateOf(true) }
    var showDivider by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val itemCount = countIndex + NavigationDemoMinCount

    AppPageScaffold(
        title = stringResource(Res.string.demo_floating_navigation_bar_title),
        subtitle = "FloatingNavigationBar",
        onBack = onBack,
        bottomBar = {
            // The COUI 17 show / hide springs run inside the bar, keeping its shadow whole.
            FloatingNavigationBar(
                visible = visible,
                expansionVisible = expansionShown,
                materialEffect = material,
                showDivider = showDivider,
                horizontalAlignment = FloatingNavigationBarDemoAlignments[alignmentIndex],
                expansion = { SearchExpansion(onClick = { expansionShown = false }) },
            ) {
                repeat(itemCount) { index ->
                    FloatingNavigationBarItem(
                        selected = selected.coerceAtMost(itemCount - 1) == index,
                        onClick = { selected = index },
                        icon = NavigationDemoIcons[index],
                        label = stringResource(NavigationDemoLabels[index]),
                        largeIcon = largeIcon && index == 2,
                        badge = when {
                            !badges -> null
                            index == 1 -> 0
                            index == 3 -> 8
                            else -> null
                        },
                    )
                }
            }
        },
    ) { scope ->
        AppPageList(scope = scope) {
            item(key = "controls") {
                val alignmentNames = listOf(
                    stringResource(Res.string.common_center),
                    stringResource(Res.string.common_start),
                    stringResource(Res.string.common_end),
                )
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
                            items = alignmentNames,
                            selectedIndex = alignmentIndex,
                            title = stringResource(Res.string.common_alignment),
                            onSelectedIndexChange = { alignmentIndex = it },
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
                            title = stringResource(Res.string.demo_navigation_badges),
                            checked = badges,
                            onCheckedChange = { badges = it },
                        )
                    }
                    item {
                        SwitchPreference(
                            title = stringResource(Res.string.demo_floating_navigation_bar_large_icon),
                            checked = largeIcon,
                            onCheckedChange = { largeIcon = it },
                        )
                    }
                    item {
                        SwitchPreference(
                            title = stringResource(Res.string.demo_floating_navigation_bar_expansion),
                            summary = stringResource(Res.string.demo_floating_navigation_bar_expansion_summary),
                            checked = expansionShown,
                            onCheckedChange = { expansionShown = it },
                        )
                    }
                    item {
                        SwitchPreference(
                            title = stringResource(Res.string.common_material),
                            checked = material,
                            onCheckedChange = { material = it },
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
            navigationFillerContent()
        }
    }
}

/** The search button the bar's expansion slot shows beside the capsule; a tap hides it again. */
@Composable
private fun SearchExpansion(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(56.dp),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(
            onClick = onClick,
            backgroundColor = COUITheme.colorScheme.secondaryContainer,
            cornerRadius = 28.dp,
            minWidth = 56.dp,
            minHeight = 56.dp,
        ) {
            Icon(imageVector = COUIIcons.Search, contentDescription = stringResource(Res.string.common_search))
        }
    }
}
