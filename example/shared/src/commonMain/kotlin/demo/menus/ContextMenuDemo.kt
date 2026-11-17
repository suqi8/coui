// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.menus

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Copy
import io.github.suqi8.coui.kmp.icon.extended.Delete
import io.github.suqi8.coui.kmp.icon.extended.Rename
import io.github.suqi8.coui.kmp.icon.extended.Share
import io.github.suqi8.coui.kmp.menu.OverlayContextMenu
import io.github.suqi8.coui.kmp.menu.WindowContextMenu
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_copy
import io.github.suqi8.coui.kmp.shared.generated.resources.common_delete
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.common_share
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_context_menu_collapse
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_context_menu_disabled_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_context_menu_haptic
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_context_menu_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_context_menu_last_action
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_context_menu_none
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_context_menu_rename
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_context_menu_show_delete
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_context_menu_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_context_menu_variant_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_context_menu_variant_hosts
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_context_menu_window_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_menus_host
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_menus_host_overlay
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_menus_host_window
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource

internal val ContextMenuDemo = Demo(
    id = "context_menu",
    apiName = "OverlayContextMenu",
    title = Res.string.demo_context_menu_title,
    category = DemoCategory.Menus,
    page = DemoPage.Variants {
        playground { ContextMenuPlayground() }
        variant(key = "hosts", title = Res.string.demo_context_menu_variant_hosts) {
            var lastAction by rememberSaveable { mutableIntStateOf(-1) }
            val entries = rememberContextMenuEntries(
                showIcons = false,
                showDelete = true,
                onAction = { lastAction = it },
            )
            val overlayTitle = stringResource(Res.string.demo_menus_host_overlay)
            val windowTitle = stringResource(Res.string.demo_menus_host_window)
            val lastActionText = lastActionSummary(lastAction)
            val windowSummary = stringResource(Res.string.demo_context_menu_window_summary)
            CardListGroup {
                item {
                    OverlayContextMenu(entries = entries) {
                        BasicComponent(title = overlayTitle, summary = lastActionText, onClick = {})
                    }
                }
                item {
                    WindowContextMenu(entries = entries) {
                        BasicComponent(title = windowTitle, summary = windowSummary, onClick = {})
                    }
                }
            }
        }
        variant(key = "disabled", title = Res.string.demo_context_menu_variant_disabled) {
            val entries = rememberContextMenuEntries(showIcons = false, showDelete = true, onAction = {})
            OverlayContextMenu(entries = entries, enabled = false) {
                BasicComponent(
                    title = stringResource(Res.string.demo_context_menu_hint),
                    summary = stringResource(Res.string.demo_context_menu_disabled_summary),
                    enabled = false,
                )
            }
        }
    },
    keywords = listOf("长按", "菜单", "long press", "right click", "WindowContextMenu", "右键"),
)

/**
 * A row that opens a context menu on a long press or a right click; the host and the menu options
 * come from the parameter rows.
 */
@Composable
private fun ColumnScope.ContextMenuPlayground() {
    var hostIndex by rememberSaveable { mutableIntStateOf(0) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var haptic by rememberSaveable { mutableStateOf(true) }
    var showIcons by rememberSaveable { mutableStateOf(true) }
    var showDelete by rememberSaveable { mutableStateOf(true) }
    var collapse by rememberSaveable { mutableStateOf(true) }
    var material by rememberSaveable { mutableStateOf(true) }
    var lastAction by rememberSaveable { mutableIntStateOf(-1) }

    val entries = rememberContextMenuEntries(
        showIcons = showIcons,
        showDelete = showDelete,
        onAction = { lastAction = it },
    )
    val title = stringResource(Res.string.demo_context_menu_hint)
    val summary = lastActionSummary(lastAction)
    PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
        CompositionLocalProvider(LocalCOUIMaterialEffectEnabled provides material) {
            if (hostIndex == 0) {
                OverlayContextMenu(
                    entries = entries,
                    enabled = enabled,
                    collapseOnSelection = collapse,
                    hapticFeedbackEnabled = haptic,
                ) {
                    BasicComponent(title = title, summary = summary, enabled = enabled, onClick = {})
                }
            } else {
                WindowContextMenu(
                    entries = entries,
                    enabled = enabled,
                    collapseOnSelection = collapse,
                    hapticFeedbackEnabled = haptic,
                ) {
                    BasicComponent(title = title, summary = summary, enabled = enabled, onClick = {})
                }
            }
        }
    }
    val hostItems = listOf(
        stringResource(Res.string.demo_menus_host_overlay),
        stringResource(Res.string.demo_menus_host_window),
    )
    val hostTitle = stringResource(Res.string.demo_menus_host)
    val enabledTitle = stringResource(Res.string.common_enabled)
    val hapticTitle = stringResource(Res.string.demo_context_menu_haptic)
    val iconTitle = stringResource(Res.string.common_show_icon)
    val deleteTitle = stringResource(Res.string.demo_context_menu_show_delete)
    val collapseTitle = stringResource(Res.string.demo_context_menu_collapse)
    val materialTitle = stringResource(Res.string.common_material)
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
            SwitchPreference(checked = enabled, onCheckedChange = { enabled = it }, title = enabledTitle)
        }
        item {
            SwitchPreference(
                checked = haptic,
                onCheckedChange = { haptic = it },
                title = hapticTitle,
                summary = "hapticFeedbackEnabled",
            )
        }
        item {
            SwitchPreference(checked = showIcons, onCheckedChange = { showIcons = it }, title = iconTitle)
        }
        item {
            SwitchPreference(checked = showDelete, onCheckedChange = { showDelete = it }, title = deleteTitle)
        }
        item {
            SwitchPreference(
                checked = collapse,
                onCheckedChange = { collapse = it },
                title = collapseTitle,
                summary = "collapseOnSelection",
            )
        }
        item {
            SwitchPreference(checked = material, onCheckedChange = { material = it }, title = materialTitle)
        }
    }
}

/** "Last action: …" for an action id from [rememberContextMenuEntries], or -1 for none. */
@Composable
private fun lastActionSummary(action: Int): String {
    val label = when (action) {
        0 -> stringResource(Res.string.common_copy)
        1 -> stringResource(Res.string.common_share)
        2 -> stringResource(Res.string.demo_context_menu_rename)
        3 -> stringResource(Res.string.common_delete)
        else -> stringResource(Res.string.demo_context_menu_none)
    }
    return stringResource(Res.string.demo_context_menu_last_action, label)
}

/**
 * Copy, share and rename, then an alert delete group. [onAction] receives the action id: 0 copy,
 * 1 share, 2 rename, 3 delete.
 */
@Composable
private fun rememberContextMenuEntries(
    showIcons: Boolean,
    showDelete: Boolean,
    onAction: (Int) -> Unit,
): List<DropdownEntry> {
    val iconTint = COUITheme.colorScheme.labelPrimary
    val labels = listOf(
        stringResource(Res.string.common_copy),
        stringResource(Res.string.common_share),
        stringResource(Res.string.demo_context_menu_rename),
        stringResource(Res.string.common_delete),
    )
    val icons = listOf(COUIIcons.Copy, COUIIcons.Share, COUIIcons.Rename, COUIIcons.Delete)
    val currentOnAction by rememberUpdatedState(onAction)
    return remember(labels, showIcons, showDelete, iconTint) {
        val items = labels.mapIndexed { index, text ->
            val rowIcon: @Composable (Modifier) -> Unit = { modifier ->
                Icon(
                    modifier = modifier,
                    imageVector = icons[index],
                    tint = iconTint,
                    contentDescription = null,
                )
            }
            DropdownItem(
                text = text,
                icon = if (showIcons) rowIcon else null,
                alert = index == 3,
                onClick = { currentOnAction(index) },
            )
        }
        buildList {
            add(DropdownEntry(items = items.take(3)))
            if (showDelete) add(DropdownEntry(items = items.drop(3)))
        }
    }
}
