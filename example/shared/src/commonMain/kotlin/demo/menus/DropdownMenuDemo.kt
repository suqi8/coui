// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.menus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
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
import io.github.suqi8.coui.kmp.basic.Badge
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Edit
import io.github.suqi8.coui.kmp.icon.extended.SelectAll
import io.github.suqi8.coui.kmp.icon.extended.Sort
import io.github.suqi8.coui.kmp.icon.extended.Tune
import io.github.suqi8.coui.kmp.menu.OverlayDropdownMenu
import io.github.suqi8.coui.kmp.menu.OverlayIconCascadingDropdownMenu
import io.github.suqi8.coui.kmp.menu.OverlayIconDropdownMenu
import io.github.suqi8.coui.kmp.menu.WindowDropdownMenu
import io.github.suqi8.coui.kmp.menu.WindowIconCascadingDropdownMenu
import io.github.suqi8.coui.kmp.menu.WindowIconDropdownMenu
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_cd_select
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_cd_sort
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_cd_view
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_collapse_on_selection
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_content
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_content_grouped
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_content_headers
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_content_single
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_content_variants
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_disabled_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_filter
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_filter_all
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_filter_camera
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_group_option
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_header_ascending
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_header_danger
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_header_date
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_header_descending
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_header_long
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_header_name
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_header_reset
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_header_sort
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_icon_menus_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_item_alert
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_item_all_slots
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_item_all_slots_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_item_count_badge
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_item_description
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_item_description_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_item_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_item_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_item_long
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_item_red_dot
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_item_state_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_limit_height
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_sort_added
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_sort_captured
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_state_collapsed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_state_expanded
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_variant_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_variant_group_headers
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_variant_icon_menus
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_variant_item_variants
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_view_compact
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_view_grouped
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dropdown_menu_view_mode
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_menus_host
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_menus_host_overlay
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_menus_host_window
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_option
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_title
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource

/** The demo page of `OverlayDropdownMenu`, listed in its category and found by the search. */
internal val DropdownMenuDemo = Demo(
    id = "dropdown_menu",
    apiName = "OverlayDropdownMenu",
    title = Res.string.demo_dropdown_menu_title,
    category = DemoCategory.Menus,
    page = DemoPage.Variants {
        playground { DropdownMenuPlayground() }
        variant(key = "item_variants", title = Res.string.demo_dropdown_menu_variant_item_variants) {
            val entries = rememberItemVariantEntries()
            CardListGroup {
                item {
                    OverlayDropdownMenu(
                        entries = entries,
                        title = stringResource(Res.string.demo_menus_host_overlay),
                        summary = "OverlayDropdownMenu",
                        collapseOnSelection = false,
                    )
                }
                item {
                    WindowDropdownMenu(
                        entries = entries,
                        title = stringResource(Res.string.demo_menus_host_window),
                        summary = "WindowDropdownMenu",
                        collapseOnSelection = false,
                    )
                }
            }
        }
        variant(key = "group_headers", title = Res.string.demo_dropdown_menu_variant_group_headers) {
            val entries = rememberHeaderEntries()
            CardListGroup {
                item {
                    OverlayDropdownMenu(
                        entries = entries,
                        title = stringResource(Res.string.demo_menus_host_overlay),
                        summary = "OverlayDropdownMenu",
                        collapseOnSelection = false,
                    )
                }
                item {
                    WindowDropdownMenu(
                        entries = entries,
                        title = stringResource(Res.string.demo_menus_host_window),
                        summary = "WindowDropdownMenu",
                        collapseOnSelection = false,
                    )
                }
            }
        }
        variant(key = "icon_menus", title = Res.string.demo_dropdown_menu_variant_icon_menus) {
            IconMenusContent()
        }
        variant(key = "disabled", title = Res.string.demo_dropdown_menu_variant_disabled) {
            val entries = rememberSingleGroupEntries()
            CardListGroup {
                item {
                    OverlayDropdownMenu(
                        entries = entries,
                        title = stringResource(Res.string.demo_menus_host_overlay),
                        summary = stringResource(Res.string.demo_dropdown_menu_disabled_summary),
                        enabled = false,
                    )
                }
                item {
                    WindowDropdownMenu(
                        entries = entries,
                        title = stringResource(Res.string.demo_menus_host_window),
                        summary = stringResource(Res.string.demo_dropdown_menu_disabled_summary),
                        enabled = false,
                    )
                }
            }
        }
    },
    keywords = listOf("IconDropdownMenu", "WindowDropdownMenu"),
    searchTerms = Res.string.demo_dropdown_menu_search_terms,
)

/**
 * One dropdown menu row whose host, entries and options are driven by the parameter rows.
 */
@Composable
private fun ColumnScope.DropdownMenuPlayground() {
    var hostIndex by rememberSaveable { mutableIntStateOf(0) }
    var contentIndex by rememberSaveable { mutableIntStateOf(1) }
    var collapseOnSelection by rememberSaveable { mutableStateOf(false) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var showSummary by rememberSaveable { mutableStateOf(true) }
    var limitHeight by rememberSaveable { mutableStateOf(false) }
    var material by rememberSaveable { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }

    val single = rememberSingleGroupEntries()
    val grouped = rememberGroupedEntries()
    val headers = rememberHeaderEntries()
    val variants = rememberItemVariantEntries()
    val entries = when (contentIndex) {
        0 -> single
        1 -> grouped
        2 -> headers
        else -> variants
    }
    val title = stringResource(Res.string.sample_title)
    val summary = if (showSummary) {
        stringResource(
            if (expanded) Res.string.demo_dropdown_menu_state_expanded else Res.string.demo_dropdown_menu_state_collapsed,
        )
    } else {
        null
    }
    val maxHeight = if (limitHeight) 200.dp else null

    PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
        CompositionLocalProvider(LocalCOUIMaterialEffectEnabled provides material) {
            if (hostIndex == 0) {
                OverlayDropdownMenu(
                    entries = entries,
                    title = title,
                    summary = summary,
                    maxHeight = maxHeight,
                    enabled = enabled,
                    collapseOnSelection = collapseOnSelection,
                    onExpandedChange = { expanded = it },
                )
            } else {
                WindowDropdownMenu(
                    entries = entries,
                    title = title,
                    summary = summary,
                    maxHeight = maxHeight,
                    enabled = enabled,
                    collapseOnSelection = collapseOnSelection,
                    onExpandedChange = { expanded = it },
                )
            }
        }
    }
    val hostItems = listOf(
        stringResource(Res.string.demo_menus_host_overlay),
        stringResource(Res.string.demo_menus_host_window),
    )
    val contentItems = listOf(
        stringResource(Res.string.demo_dropdown_menu_content_single),
        stringResource(Res.string.demo_dropdown_menu_content_grouped),
        stringResource(Res.string.demo_dropdown_menu_content_headers),
        stringResource(Res.string.demo_dropdown_menu_content_variants),
    )
    val collapseTitle = stringResource(Res.string.demo_dropdown_menu_collapse_on_selection)
    val enabledTitle = stringResource(Res.string.common_enabled)
    val summaryTitle = stringResource(Res.string.common_show_summary)
    val limitTitle = stringResource(Res.string.demo_dropdown_menu_limit_height)
    val materialTitle = stringResource(Res.string.common_material)
    val hostTitle = stringResource(Res.string.demo_menus_host)
    val contentTitle = stringResource(Res.string.demo_dropdown_menu_content)
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
                items = contentItems,
                selectedIndex = contentIndex,
                title = contentTitle,
                onSelectedIndexChange = { contentIndex = it },
            )
        }
        item {
            SwitchPreference(
                checked = collapseOnSelection,
                onCheckedChange = { collapseOnSelection = it },
                title = collapseTitle,
            )
        }
        item {
            SwitchPreference(checked = enabled, onCheckedChange = { enabled = it }, title = enabledTitle)
        }
        item {
            SwitchPreference(checked = showSummary, onCheckedChange = { showSummary = it }, title = summaryTitle)
        }
        item {
            SwitchPreference(
                checked = limitHeight,
                onCheckedChange = { limitHeight = it },
                title = limitTitle,
                summary = "maxHeight = 200.dp",
            )
        }
        item {
            SwitchPreference(checked = material, onCheckedChange = { material = it }, title = materialTitle)
        }
    }
}

/**
 * The icon-button menus a top bar uses: a cascading menu, a grouped single-choice menu and a
 * multi-choice menu, in an overlay and a window row.
 */
@Composable
private fun ColumnScope.IconMenusContent() {
    val cascading = rememberCascadingEntries()
    val grouped = rememberGroupedEntries()
    val multi = rememberMultiSelectEntries()
    val viewDescription = stringResource(Res.string.demo_dropdown_menu_cd_view)
    val sortDescription = stringResource(Res.string.demo_dropdown_menu_cd_sort)
    val selectDescription = stringResource(Res.string.demo_dropdown_menu_cd_select)
    Text(
        text = stringResource(Res.string.demo_dropdown_menu_icon_menus_summary),
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
        color = COUITheme.colorScheme.labelSecondaryVariant,
        style = COUITheme.textStyles.bodyM,
    )
    IconMenuRow(label = stringResource(Res.string.demo_menus_host_overlay)) {
        OverlayIconCascadingDropdownMenu(entries = cascading, collapseOnSelection = true) {
            Icon(imageVector = COUIIcons.Tune, contentDescription = viewDescription)
        }
        OverlayIconDropdownMenu(entries = grouped, collapseOnSelection = false) {
            Icon(imageVector = COUIIcons.Sort, contentDescription = sortDescription)
        }
        OverlayIconDropdownMenu(entries = multi, collapseOnSelection = false) {
            Icon(imageVector = COUIIcons.SelectAll, contentDescription = selectDescription)
        }
    }
    IconMenuRow(label = stringResource(Res.string.demo_menus_host_window)) {
        WindowIconCascadingDropdownMenu(entries = cascading, collapseOnSelection = true) {
            Icon(imageVector = COUIIcons.Tune, contentDescription = viewDescription)
        }
        WindowIconDropdownMenu(entries = grouped, collapseOnSelection = false) {
            Icon(imageVector = COUIIcons.Sort, contentDescription = sortDescription)
        }
        WindowIconDropdownMenu(entries = multi, collapseOnSelection = false) {
            Icon(imageVector = COUIIcons.SelectAll, contentDescription = selectDescription)
        }
    }
}

@Composable
private fun IconMenuRow(
    label: String,
    modifier: Modifier = Modifier,
    menus: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = COUITheme.textStyles.bodyL,
        )
        menus()
    }
}

/** One group of four options. */
@Composable
private fun rememberSingleGroupEntries(): List<DropdownEntry> {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val labels = (1..4).map { stringResource(Res.string.sample_option, it) }
    return remember(labels, selected) {
        listOf(
            DropdownEntry(
                items = labels.mapIndexed { index, text ->
                    DropdownItem(text = text, selected = selected == index, onClick = { selected = index })
                },
            ),
        )
    }
}

/** Three single-choice groups; every other row of the last group is disabled. */
@Composable
private fun rememberGroupedEntries(): List<DropdownEntry> {
    var selectedA by rememberSaveable { mutableIntStateOf(0) }
    var selectedB by rememberSaveable { mutableIntStateOf(1) }
    var selectedC by rememberSaveable { mutableIntStateOf(2) }
    val labelsA = (1..2).map { stringResource(Res.string.demo_dropdown_menu_group_option, "A", it) }
    val labelsB = (1..3).map { stringResource(Res.string.demo_dropdown_menu_group_option, "B", it) }
    val labelsC = (1..4).map { stringResource(Res.string.demo_dropdown_menu_group_option, "C", it) }
    return remember(labelsA, labelsB, labelsC, selectedA, selectedB, selectedC) {
        listOf(
            DropdownEntry(
                items = labelsA.mapIndexed { index, text ->
                    DropdownItem(text = text, selected = selectedA == index, onClick = { selectedA = index })
                },
            ),
            DropdownEntry(
                items = labelsB.mapIndexed { index, text ->
                    DropdownItem(text = text, selected = selectedB == index, onClick = { selectedB = index })
                },
            ),
            DropdownEntry(
                items = labelsC.mapIndexed { index, text ->
                    DropdownItem(
                        text = text,
                        enabled = index % 2 == 0,
                        selected = selectedC == index,
                        onClick = { selectedC = index },
                    )
                },
            ),
        )
    }
}

/** Two groups where any number of rows can be checked; the checked rows are kept as ids. */
@Composable
private fun rememberMultiSelectEntries(): List<DropdownEntry> {
    // Ids 0-1 are group A, 2-4 group B.
    var checked by remember { mutableStateOf(setOf(0, 3, 4)) }
    val labelsA = (1..2).map { stringResource(Res.string.demo_dropdown_menu_group_option, "A", it) }
    val labelsB = (1..3).map { stringResource(Res.string.demo_dropdown_menu_group_option, "B", it) }
    return remember(labelsA, labelsB, checked) {
        fun item(id: Int, text: String) = DropdownItem(
            text = text,
            selected = id in checked,
            onClick = { checked = if (id in checked) checked - id else checked + id },
        )
        listOf(
            DropdownEntry(items = labelsA.mapIndexed { index, text -> item(index, text) }),
            DropdownEntry(items = labelsB.mapIndexed { index, text -> item(labelsA.size + index, text) }),
        )
    }
}

/** A sort group and two rows that open sub menus. */
@Composable
private fun rememberCascadingEntries(): List<DropdownEntry> {
    var sortIndex by rememberSaveable { mutableIntStateOf(0) }
    var viewIndex by rememberSaveable { mutableIntStateOf(0) }
    var filterIndex by rememberSaveable { mutableIntStateOf(0) }
    val sortLabels = listOf(
        stringResource(Res.string.demo_dropdown_menu_sort_captured),
        stringResource(Res.string.demo_dropdown_menu_sort_added),
    )
    val viewLabels = listOf(
        stringResource(Res.string.demo_dropdown_menu_view_grouped),
        stringResource(Res.string.demo_dropdown_menu_view_compact),
    )
    val filterLabels = listOf(
        stringResource(Res.string.demo_dropdown_menu_filter_all),
        stringResource(Res.string.demo_dropdown_menu_filter_camera),
    )
    val viewMode = stringResource(Res.string.demo_dropdown_menu_view_mode)
    val filter = stringResource(Res.string.demo_dropdown_menu_filter)
    return remember(sortLabels, viewLabels, filterLabels, viewMode, filter, sortIndex, viewIndex, filterIndex) {
        listOf(
            DropdownEntry(
                items = sortLabels.mapIndexed { index, label ->
                    DropdownItem(text = label, selected = sortIndex == index, onClick = { sortIndex = index })
                },
            ),
            DropdownEntry(
                items = listOf(
                    DropdownItem(
                        text = viewMode,
                        children = viewLabels.mapIndexed { index, label ->
                            DropdownItem(text = label, selected = viewIndex == index, onClick = { viewIndex = index })
                        },
                    ),
                    DropdownItem(
                        text = filter,
                        children = filterLabels.mapIndexed { index, label ->
                            DropdownItem(text = label, selected = filterIndex == index, onClick = { filterIndex = index })
                        },
                    ),
                ),
            ),
        )
    }
}

/**
 * Every COUI popup item variant in one popup: leading icon (rows without one keep the icon slot so
 * the titles line up), description, hint slot (dot and count badge), all three slots, a custom
 * state icon, a disabled row whose hint is suppressed, a clamped long title and an alert row.
 */
@Composable
private fun rememberItemVariantEntries(): List<DropdownEntry> {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val iconTint = COUITheme.colorScheme.labelPrimary
    val texts = listOf(
        stringResource(Res.string.demo_dropdown_menu_item_icon),
        stringResource(Res.string.demo_dropdown_menu_item_description),
        stringResource(Res.string.demo_dropdown_menu_item_description_summary),
        stringResource(Res.string.demo_dropdown_menu_item_red_dot),
        stringResource(Res.string.demo_dropdown_menu_item_count_badge),
        stringResource(Res.string.demo_dropdown_menu_item_all_slots),
        stringResource(Res.string.demo_dropdown_menu_item_all_slots_summary),
        stringResource(Res.string.demo_dropdown_menu_item_state_icon),
        stringResource(Res.string.demo_dropdown_menu_item_disabled),
        stringResource(Res.string.demo_dropdown_menu_item_long),
        stringResource(Res.string.demo_dropdown_menu_item_alert),
    )
    return remember(texts, selected, iconTint) {
        listOf(
            DropdownEntry(
                items = listOf(
                    DropdownItem(
                        text = texts[0],
                        icon = { modifier ->
                            Icon(modifier = modifier, imageVector = COUIIcons.Edit, tint = iconTint, contentDescription = null)
                        },
                        selected = selected == 0,
                        onClick = { selected = 0 },
                    ),
                    DropdownItem(
                        text = texts[1],
                        summary = texts[2],
                        selected = selected == 1,
                        onClick = { selected = 1 },
                    ),
                    DropdownItem(
                        text = texts[3],
                        hint = { Badge() },
                        selected = selected == 2,
                        onClick = { selected = 2 },
                    ),
                    DropdownItem(
                        text = texts[4],
                        hint = { Badge(count = 12) },
                        selected = selected == 3,
                        onClick = { selected = 3 },
                    ),
                    DropdownItem(
                        text = texts[5],
                        summary = texts[6],
                        icon = { modifier ->
                            Icon(modifier = modifier, imageVector = COUIIcons.Sort, tint = iconTint, contentDescription = null)
                        },
                        hint = { Badge(count = 3) },
                        selected = selected == 4,
                        onClick = { selected = 4 },
                    ),
                    DropdownItem(
                        text = texts[7],
                        stateIcon = COUIIcons.Sort,
                        selected = selected == 5,
                        onClick = { selected = 5 },
                    ),
                    DropdownItem(
                        text = texts[8],
                        hint = { Badge(count = 9) },
                        enabled = false,
                    ),
                    DropdownItem(
                        text = texts[9],
                        selected = selected == 6,
                        onClick = { selected = 6 },
                    ),
                    DropdownItem(
                        text = texts[10],
                        alert = true,
                        selected = selected == 7,
                        onClick = { selected = 7 },
                    ),
                ),
            ),
        )
    }
}

/** Groups with non-clickable header rows: a short one, a wrapping one and an alert group. */
@Composable
private fun rememberHeaderEntries(): List<DropdownEntry> {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val sortTitle = stringResource(Res.string.demo_dropdown_menu_header_sort)
    val sortLabels = listOf(
        stringResource(Res.string.demo_dropdown_menu_header_name),
        stringResource(Res.string.demo_dropdown_menu_header_date),
    )
    val longTitle = stringResource(Res.string.demo_dropdown_menu_header_long)
    val ascending = stringResource(Res.string.demo_dropdown_menu_header_ascending)
    val descending = stringResource(Res.string.demo_dropdown_menu_header_descending)
    val dangerTitle = stringResource(Res.string.demo_dropdown_menu_header_danger)
    val reset = stringResource(Res.string.demo_dropdown_menu_header_reset)
    return remember(sortTitle, sortLabels, longTitle, ascending, descending, dangerTitle, reset, selected) {
        listOf(
            DropdownEntry(
                title = sortTitle,
                items = sortLabels.mapIndexed { index, text ->
                    DropdownItem(text = text, selected = selected == index, onClick = { selected = index })
                },
            ),
            DropdownEntry(
                title = longTitle,
                items = listOf(
                    DropdownItem(text = ascending, hint = { Badge() }),
                    DropdownItem(text = descending),
                ),
            ),
            DropdownEntry(
                title = dangerTitle,
                items = listOf(DropdownItem(text = reset, alert = true)),
            ),
        )
    }
}
