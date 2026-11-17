// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.menus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
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
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.DropdownDefaults
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownImpl
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.ListPopupColumn
import io.github.suqi8.coui.kmp.basic.PopupPositionProvider
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Copy
import io.github.suqi8.coui.kmp.icon.extended.Edit
import io.github.suqi8.coui.kmp.icon.extended.Folder
import io.github.suqi8.coui.kmp.icon.extended.Share
import io.github.suqi8.coui.kmp.icon.extended.Sort
import io.github.suqi8.coui.kmp.icon.extended.Tune
import io.github.suqi8.coui.kmp.overlay.OverlayCascadingListPopup
import io.github.suqi8.coui.kmp.overlay.OverlayListPopup
import io.github.suqi8.coui.kmp.popup.OverlayDropdownPopup
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_alignment
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_delete
import io.github.suqi8.coui.kmp.shared.generated.resources.common_edit
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_disabled_row
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_limit_height
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_material_blur
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_material_effect
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_new_folder
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_opaque_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_show
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_sort
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_sort_by
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_sort_date
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_sort_name
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_sort_size
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_sub_menus_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_theme_dark
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_theme_light
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_theme_system
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_variant_opaque
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_variant_sub_menus
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_view
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_view_grid
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_view_list
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_popup_window_dim
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_menus_host
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_menus_host_overlay
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_menus_host_window
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_option
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.window.WindowCascadingListPopup
import io.github.suqi8.coui.kmp.window.WindowListPopup
import org.jetbrains.compose.resources.stringResource

/** The demo page of `OverlayListPopup`, listed in its category and found by the search. */
internal val ListPopupDemo = Demo(
    id = "list_popup",
    apiName = "OverlayListPopup",
    title = Res.string.demo_list_popup_title,
    category = DemoCategory.Menus,
    page = DemoPage.Variants {
        playground { ListPopupPlayground() }
        variant(key = "sub_menus", title = Res.string.demo_list_popup_variant_sub_menus) {
            SubMenusContent()
        }
        variant(key = "opaque", title = Res.string.demo_list_popup_variant_opaque) {
            OpaquePopupContent()
        }
    },
    keywords = listOf("ListPopup", "CascadingListPopup"),
    searchTerms = Res.string.demo_list_popup_search_terms,
)

private val ListPopupIcons = listOf(
    COUIIcons.Edit,
    COUIIcons.Copy,
    COUIIcons.Share,
    COUIIcons.Sort,
    COUIIcons.Tune,
    COUIIcons.Folder,
)

/**
 * A button that opens a list popup; the host, alignment, rows and surface come from the parameter
 * rows.
 */
@Composable
private fun ColumnScope.ListPopupPlayground() {
    var hostIndex by rememberSaveable { mutableIntStateOf(0) }
    var alignIndex by rememberSaveable { mutableIntStateOf(0) }
    var itemCount by rememberSaveable { mutableFloatStateOf(4f) }
    var windowDim by rememberSaveable { mutableStateOf(false) }
    var materialBlur by rememberSaveable { mutableStateOf(true) }
    var materialEffect by rememberSaveable { mutableStateOf(true) }
    var limitHeight by rememberSaveable { mutableStateOf(false) }
    var showIcons by rememberSaveable { mutableStateOf(false) }
    var disableSecond by rememberSaveable { mutableStateOf(true) }
    var selected by rememberSaveable { mutableIntStateOf(0) }
    var show by remember { mutableStateOf(false) }

    val aligns = PopupPositionProvider.Align.entries
    val alignment = aligns[alignIndex]
    val count = itemCount.toInt()
    val maxHeight = if (limitHeight) 180.dp else null
    val onSelect: (Int) -> Unit = {
        selected = it
        show = false
    }

    PlaygroundPreview {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box {
                TextButton(text = stringResource(Res.string.demo_list_popup_show), onClick = { show = true })
                if (hostIndex == 0) {
                    OverlayListPopup(
                        show = show,
                        alignment = alignment,
                        enableWindowDim = windowDim,
                        onDismissRequest = { show = false },
                        maxHeight = maxHeight,
                        materialBlur = materialBlur,
                        materialEffect = materialEffect,
                    ) {
                        ListPopupRows(
                            count = count,
                            selected = selected,
                            showIcons = showIcons,
                            disableSecond = disableSecond,
                            onSelect = onSelect,
                        )
                    }
                } else {
                    WindowListPopup(
                        show = show,
                        alignment = alignment,
                        enableWindowDim = windowDim,
                        onDismissRequest = { show = false },
                        maxHeight = maxHeight,
                        materialBlur = materialBlur,
                        materialEffect = materialEffect,
                    ) {
                        ListPopupRows(
                            count = count,
                            selected = selected,
                            showIcons = showIcons,
                            disableSecond = disableSecond,
                            onSelect = onSelect,
                        )
                    }
                }
            }
        }
    }
    val hostItems = listOf(
        stringResource(Res.string.demo_menus_host_overlay),
        stringResource(Res.string.demo_menus_host_window),
    )
    val alignItems = aligns.map { it.name }
    val hostTitle = stringResource(Res.string.demo_menus_host)
    val alignTitle = stringResource(Res.string.common_alignment)
    val countTitle = stringResource(Res.string.common_count)
    val dimTitle = stringResource(Res.string.demo_list_popup_window_dim)
    val blurTitle = stringResource(Res.string.demo_list_popup_material_blur)
    val effectTitle = stringResource(Res.string.demo_list_popup_material_effect)
    val limitTitle = stringResource(Res.string.demo_list_popup_limit_height)
    val iconTitle = stringResource(Res.string.common_show_icon)
    val disabledTitle = stringResource(Res.string.demo_list_popup_disabled_row)
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
            OverlayDropdownPreference(
                items = alignItems,
                selectedIndex = alignIndex,
                title = alignTitle,
                summary = "alignment",
                onSelectedIndexChange = { alignIndex = it },
            )
        }
        item {
            SliderPreference(
                value = itemCount,
                onValueChange = { itemCount = it },
                title = countTitle,
                valueText = count.toString(),
                valueRange = 1f..12f,
                steps = 10,
            )
        }
        item {
            SwitchPreference(
                checked = windowDim,
                onCheckedChange = { windowDim = it },
                title = dimTitle,
                summary = "enableWindowDim",
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
            SwitchPreference(
                checked = materialEffect,
                onCheckedChange = { materialEffect = it },
                title = effectTitle,
                summary = "materialEffect",
            )
        }
        item {
            SwitchPreference(
                checked = limitHeight,
                onCheckedChange = { limitHeight = it },
                title = limitTitle,
                summary = "maxHeight = 180.dp",
            )
        }
        item {
            SwitchPreference(checked = showIcons, onCheckedChange = { showIcons = it }, title = iconTitle)
        }
        item {
            SwitchPreference(checked = disableSecond, onCheckedChange = { disableSecond = it }, title = disabledTitle)
        }
    }
}

/** The rows of the playground popup, built from [DropdownImpl] in a [ListPopupColumn]. */
@Composable
private fun ListPopupRows(
    count: Int,
    selected: Int,
    showIcons: Boolean,
    disableSecond: Boolean,
    onSelect: (Int) -> Unit,
) {
    val iconTint = COUITheme.colorScheme.labelPrimary
    ListPopupColumn {
        repeat(count) { index ->
            val text = stringResource(Res.string.sample_option, index + 1)
            val enabled = !(disableSecond && index == 1)
            val item = remember(text, enabled, showIcons, iconTint) {
                val rowIcon: @Composable (Modifier) -> Unit = { modifier ->
                    Icon(
                        modifier = modifier,
                        imageVector = ListPopupIcons[index % ListPopupIcons.size],
                        tint = iconTint,
                        contentDescription = null,
                    )
                }
                DropdownItem(text = text, enabled = enabled, icon = if (showIcons) rowIcon else null)
            }
            DropdownImpl(
                item = item,
                optionSize = count,
                isSelected = selected == index,
                index = index,
                reserveIconSlot = showIcons,
                onSelectedIndexChange = onSelect,
            )
        }
    }
}

/**
 * A sub menu (covering the main menu on a compact window, beside it on a wide one), a long press
 * on a sub-menu row and a horizontal icon group, in an overlay and a window popup.
 */
@Composable
private fun SubMenusContent() {
    var overlayShown by remember { mutableStateOf(false) }
    var windowShown by remember { mutableStateOf(false) }
    var sortIndex by rememberSaveable { mutableIntStateOf(0) }
    val iconTint = COUITheme.colorScheme.labelPrimary
    val edit = stringResource(Res.string.common_edit)
    val sort = stringResource(Res.string.demo_list_popup_sort)
    val newFolder = stringResource(Res.string.demo_list_popup_new_folder)
    val sortBy = stringResource(Res.string.demo_list_popup_sort_by)
    val sortLabels = listOf(
        stringResource(Res.string.demo_list_popup_sort_name),
        stringResource(Res.string.demo_list_popup_sort_date),
        stringResource(Res.string.demo_list_popup_sort_size),
    )
    val view = stringResource(Res.string.demo_list_popup_view)
    val viewList = stringResource(Res.string.demo_list_popup_view_list)
    val viewGrid = stringResource(Res.string.demo_list_popup_view_grid)
    val delete = stringResource(Res.string.common_delete)
    val entries = remember(edit, sort, newFolder, sortBy, sortLabels, view, viewList, viewGrid, delete, sortIndex, iconTint) {
        listOf(
            DropdownEntry(
                horizontal = true,
                items = listOf(COUIIcons.Edit to edit, COUIIcons.Sort to sort).map { (icon, label) ->
                    DropdownItem(
                        text = label,
                        icon = { modifier -> Icon(modifier = modifier, imageVector = icon, tint = iconTint, contentDescription = null) },
                    )
                },
            ),
            DropdownEntry(
                items = listOf(
                    DropdownItem(text = newFolder),
                    DropdownItem(
                        text = sortBy,
                        summary = sortLabels[sortIndex],
                        children = sortLabels.mapIndexed { index, text ->
                            DropdownItem(text = text, selected = sortIndex == index, onClick = { sortIndex = index })
                        },
                    ),
                    DropdownItem(
                        text = view,
                        children = listOf(DropdownItem(text = viewList), DropdownItem(text = viewGrid)),
                    ),
                ),
            ),
            DropdownEntry(items = listOf(DropdownItem(text = delete, alert = true))),
        )
    }
    val summary = stringResource(Res.string.demo_list_popup_sub_menus_summary)
    val overlayTitle = stringResource(Res.string.demo_menus_host_overlay)
    val windowTitle = stringResource(Res.string.demo_menus_host_window)
    CardListGroup {
        item {
            BasicComponent(
                title = overlayTitle,
                summary = summary,
                onClick = { overlayShown = true },
                holdDownState = overlayShown,
                endActions = {
                    OverlayCascadingListPopup(
                        show = overlayShown,
                        entries = entries,
                        onDismissRequest = { overlayShown = false },
                    )
                },
            )
        }
        item {
            BasicComponent(
                title = windowTitle,
                summary = "WindowCascadingListPopup",
                onClick = { windowShown = true },
                holdDownState = windowShown,
                endActions = {
                    WindowCascadingListPopup(
                        show = windowShown,
                        entries = entries,
                        onDismissRequest = { windowShown = false },
                    )
                },
            )
        }
    }
}

/**
 * The opaque popup (`materialBlur = false`), which keeps the shadow-edge stroke and touch
 * spotlight of the blurred ones.
 */
@Composable
private fun OpaquePopupContent() {
    var shown by remember { mutableStateOf(false) }
    var themeIndex by rememberSaveable { mutableIntStateOf(0) }
    val labels = listOf(
        stringResource(Res.string.demo_list_popup_theme_system),
        stringResource(Res.string.demo_list_popup_theme_light),
        stringResource(Res.string.demo_list_popup_theme_dark),
    )
    val entry = remember(labels, themeIndex) {
        DropdownEntry(
            items = labels.mapIndexed { index, text ->
                DropdownItem(text = text, selected = themeIndex == index, onClick = { themeIndex = index })
            },
        )
    }
    BasicComponent(
        title = labels[themeIndex],
        summary = stringResource(Res.string.demo_list_popup_opaque_summary),
        onClick = { shown = true },
        holdDownState = shown,
        endActions = {
            OverlayDropdownPopup(
                entry = entry,
                show = shown,
                onDismiss = { shown = false },
                onDismissFinished = {},
                maxHeight = null,
                dropdownColors = DropdownDefaults.dropdownColors(),
                renderInRootScaffold = true,
                materialBlur = false,
            )
        },
    )
}
