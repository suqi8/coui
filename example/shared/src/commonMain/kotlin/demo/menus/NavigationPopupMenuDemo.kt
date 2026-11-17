// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.menus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.NavigationBar
import io.github.suqi8.coui.kmp.basic.NavigationBarItem
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Contacts
import io.github.suqi8.coui.kmp.icon.extended.Folder
import io.github.suqi8.coui.kmp.icon.extended.Home
import io.github.suqi8.coui.kmp.icon.extended.Messages
import io.github.suqi8.coui.kmp.icon.extended.More
import io.github.suqi8.coui.kmp.icon.extended.Music
import io.github.suqi8.coui.kmp.icon.extended.Photos
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.layout.NavigationPopupMenuDefaults
import io.github.suqi8.coui.kmp.overlay.OverlayNavigationPopupMenu
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_more
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_menus_host
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_menus_host_overlay
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_menus_host_window
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_popup_menu_current
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_popup_menu_disable_last
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_popup_menu_limit_height
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_popup_menu_material_blur
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_popup_menu_menu_count
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_popup_menu_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_popup_menu_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_popup_menu_variant_overlay
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_popup_menu_variant_window
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_contacts
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_files
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_home
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_messages
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_music
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_photos
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_settings
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.window.WindowNavigationPopupMenu
import org.jetbrains.compose.resources.stringResource

/** The demo page of `OverlayNavigationPopupMenu`, listed in its category and found by the search. */
internal val NavigationPopupMenuDemo = Demo(
    id = "navigation_popup_menu",
    apiName = "OverlayNavigationPopupMenu",
    title = Res.string.demo_navigation_popup_menu_title,
    category = DemoCategory.Menus,
    page = DemoPage.Variants {
        playground { NavigationPopupMenuPlayground() }
        variant(key = "overlay", title = Res.string.demo_navigation_popup_menu_variant_overlay) {
            NavigationPopupMenuContent(window = false, menuCount = 2)
        }
        variant(key = "window", title = Res.string.demo_navigation_popup_menu_variant_window) {
            NavigationPopupMenuContent(window = true, menuCount = 3)
        }
    },
    keywords = listOf("WindowNavigationPopupMenu"),
    searchTerms = Res.string.demo_navigation_popup_menu_search_terms,
)

/** The two bar destinations followed by the ones only reachable through the menu. */
private val DestinationIcons = listOf(
    COUIIcons.Home,
    COUIIcons.Photos,
    COUIIcons.Music,
    COUIIcons.Settings,
    COUIIcons.Folder,
    COUIIcons.Contacts,
    COUIIcons.Messages,
)

/** How many destinations the bar shows before its More item. */
private const val BAR_DESTINATIONS = 2

@Composable
private fun ColumnScope.NavigationPopupMenuPlayground() {
    var hostIndex by rememberSaveable { mutableIntStateOf(0) }
    var menuCount by rememberSaveable { mutableFloatStateOf(3f) }
    var materialBlur by rememberSaveable { mutableStateOf(true) }
    var disableLast by rememberSaveable { mutableStateOf(false) }
    var limitHeight by rememberSaveable { mutableStateOf(false) }

    PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
        NavigationPopupMenuContent(
            window = hostIndex == 1,
            menuCount = menuCount.toInt(),
            materialBlur = materialBlur,
            disableLast = disableLast,
            limitHeight = limitHeight,
        )
    }
    val hostItems = listOf(
        stringResource(Res.string.demo_menus_host_overlay),
        stringResource(Res.string.demo_menus_host_window),
    )
    val hostTitle = stringResource(Res.string.demo_menus_host)
    val countTitle = stringResource(Res.string.demo_navigation_popup_menu_menu_count)
    val blurTitle = stringResource(Res.string.demo_navigation_popup_menu_material_blur)
    val disableTitle = stringResource(Res.string.demo_navigation_popup_menu_disable_last)
    val limitTitle = stringResource(Res.string.demo_navigation_popup_menu_limit_height)
    PlaygroundControls {
        item {
            OverlayDropdownPreference(
                items = hostItems,
                selectedIndex = hostIndex,
                title = hostTitle,
                onSelectedIndexChange = { hostIndex = it },
            )
        }
        item {
            SliderPreference(
                value = menuCount,
                onValueChange = { menuCount = it },
                title = countTitle,
                valueText = menuCount.toInt().toString(),
                valueRange = 1f..(DestinationIcons.size - BAR_DESTINATIONS).toFloat(),
                steps = DestinationIcons.size - BAR_DESTINATIONS - 2,
            )
        }
        item {
            SwitchPreference(
                checked = materialBlur,
                onCheckedChange = { materialBlur = it },
                title = blurTitle,
                summary = "materialBlur",
            )
        }
        item {
            SwitchPreference(checked = disableLast, onCheckedChange = { disableLast = it }, title = disableTitle)
        }
        item {
            SwitchPreference(
                checked = limitHeight,
                onCheckedChange = { limitHeight = it },
                title = limitTitle,
                summary = "maxHeight = 120.dp",
            )
        }
    }
}

/**
 * A navigation bar with two destinations and a More item that opens the navigation popup menu
 * with [menuCount] further destinations. The box around the bar is the menu's anchor.
 */
@Composable
private fun NavigationPopupMenuContent(
    window: Boolean,
    menuCount: Int,
    modifier: Modifier = Modifier,
    materialBlur: Boolean = true,
    disableLast: Boolean = false,
    limitHeight: Boolean = false,
) {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }
    val labels = listOf(
        stringResource(Res.string.sample_home),
        stringResource(Res.string.sample_photos),
        stringResource(Res.string.sample_music),
        stringResource(Res.string.sample_settings),
        stringResource(Res.string.sample_files),
        stringResource(Res.string.sample_contacts),
        stringResource(Res.string.sample_messages),
    )
    val total = (BAR_DESTINATIONS + menuCount).coerceAtMost(labels.size)
    // A destination that left the menu cannot stay selected.
    val current = if (selected < total) selected else 0
    val menuItems = (BAR_DESTINATIONS until total).map { index ->
        DropdownItem(
            text = labels[index],
            enabled = !(disableLast && index == total - 1),
            selected = current == index,
            onClick = { selected = index },
            icon = { iconModifier ->
                Icon(imageVector = DestinationIcons[index], contentDescription = null, modifier = iconModifier)
            },
        )
    }
    val maxHeight = if (limitHeight) 120.dp else NavigationPopupMenuDefaults.MaxHeight
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .padding(16.dp),
            contentAlignment = Alignment.TopStart,
        ) {
            Text(
                text = stringResource(Res.string.demo_navigation_popup_menu_current, labels[current]),
                color = COUITheme.colorScheme.labelSecondaryVariant,
                style = COUITheme.textStyles.bodyM,
            )
        }
        Box {
            NavigationBar(defaultWindowInsetsPadding = false) {
                NavigationBarItem(
                    selected = current == 0,
                    onClick = { selected = 0 },
                    icon = DestinationIcons[0],
                    label = labels[0],
                )
                NavigationBarItem(
                    selected = current == 1,
                    onClick = { selected = 1 },
                    icon = DestinationIcons[1],
                    label = labels[1],
                )
                NavigationBarItem(
                    selected = current >= BAR_DESTINATIONS,
                    onClick = { showMenu = true },
                    icon = COUIIcons.More,
                    label = stringResource(Res.string.common_more),
                )
            }
            if (window) {
                WindowNavigationPopupMenu(
                    show = showMenu,
                    items = menuItems,
                    onDismissRequest = { showMenu = false },
                    maxHeight = maxHeight,
                    materialBlur = materialBlur,
                )
            } else {
                OverlayNavigationPopupMenu(
                    show = showMenu,
                    items = menuItems,
                    onDismissRequest = { showMenu = false },
                    maxHeight = maxHeight,
                    materialBlur = materialBlur,
                )
            }
        }
    }
}
