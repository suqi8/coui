// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Badge
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Edit
import io.github.suqi8.coui.kmp.icon.extended.Sort
import io.github.suqi8.coui.kmp.menu.OverlayDropdownMenu
import io.github.suqi8.coui.kmp.menu.WindowDropdownMenu
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.WindowDropdownPreference
import io.github.suqi8.coui.kmp.theme.COUITheme

fun LazyListScope.dropdownSection() {
    item(key = "dropdown") {
        var overlayDropdownOptionSelected by remember { mutableIntStateOf(0) }
        var windowDropdownOptionSelected by remember { mutableIntStateOf(0) }
        var overlayExpanded by remember { mutableStateOf(false) }
        var windowExpanded by remember { mutableStateOf(false) }
        val dropdownOptions = remember { listOf("Option 1", "Option 2", "Option 3", "Option 4") }
        val dropdownLongOptions = remember {
            listOf(
                "Option 1",
                "Long Option 2",
                "Long Long Option 3",
                "Long Long Long Option 4",
                "Long Long Long Long Option 5",
                "Long Long Long Long Long Option 6",
                "Long Long Long Long Long Long Option 7",
                "Long Long Long Long Long Long Long Option 8",
                "Long Long Long Long Long Long Long Long Option 9",
                "Long Long Long Long Long Long Long Long Long Option 10",
                "Long Long Long Long Long Long Long Long Long Long Option 11",
                "Long Long Long Long Long Long Long Long Long Long Long Option 12",
            )
        }

        var overlayGroupedExpanded by remember { mutableStateOf(false) }
        var overlayGroup1DropdownOptionSelected by remember { mutableIntStateOf(0) }
        var overlayGroup2DropdownOptionSelected by remember { mutableIntStateOf(0) }
        var overlayGroup3DropdownOptionSelected by remember { mutableIntStateOf(0) }
        val overlayMultiGroupOptions = remember(
            overlayGroup1DropdownOptionSelected,
            overlayGroup2DropdownOptionSelected,
            overlayGroup3DropdownOptionSelected,
        ) {
            listOf(
                DropdownEntry(
                    items = listOf("Option A-1", "Option A-2")
                        .mapIndexed { index, text ->
                            DropdownItem(
                                text = text,
                                selected = overlayGroup1DropdownOptionSelected == index,
                                onClick = { overlayGroup1DropdownOptionSelected = index },
                            )
                        },
                ),
                DropdownEntry(
                    items = listOf("Option B-1", "Option B-2", "Option B-3")
                        .mapIndexed { index, text ->
                            DropdownItem(
                                text = text,
                                selected = overlayGroup2DropdownOptionSelected == index,
                                onClick = { overlayGroup2DropdownOptionSelected = index },
                            )
                        },
                ),
                DropdownEntry(
                    items = listOf("Option C-1", "Option C-2", "Option C-3", "Option C-4")
                        .mapIndexed { index, string ->
                            DropdownItem(
                                text = string,
                                enabled = index % 2 == 0,
                                selected = overlayGroup3DropdownOptionSelected == index,
                                onClick = { overlayGroup3DropdownOptionSelected = index },
                            )
                        },
                ),
            )
        }

        var windowGroupedExpanded by remember { mutableStateOf(false) }
        var windowGroup1DropdownOptionSelected by remember { mutableIntStateOf(0) }
        var windowGroup2DropdownOptionSelected by remember { mutableIntStateOf(0) }
        var windowGroup3DropdownOptionSelected by remember { mutableIntStateOf(0) }
        val windowMultiGroupOptions = remember(
            windowGroup1DropdownOptionSelected,
            windowGroup2DropdownOptionSelected,
            windowGroup3DropdownOptionSelected,
        ) {
            listOf(
                DropdownEntry(
                    items = listOf("Option A-1", "Option A-2")
                        .mapIndexed { index, text ->
                            DropdownItem(
                                text = text,
                                selected = windowGroup1DropdownOptionSelected == index,
                                onClick = { windowGroup1DropdownOptionSelected = index },
                            )
                        },
                ),
                DropdownEntry(
                    items = listOf("Option B-1", "Option B-2", "Option B-3")
                        .mapIndexed { index, text ->
                            DropdownItem(
                                text = text,
                                selected = windowGroup2DropdownOptionSelected == index,
                                onClick = { windowGroup2DropdownOptionSelected = index },
                            )
                        },
                ),
                DropdownEntry(
                    items = listOf("Option C-1", "Option C-2", "Option C-3", "Option C-4")
                        .mapIndexed { index, string ->
                            DropdownItem(
                                text = string,
                                enabled = index % 2 == 0,
                                selected = windowGroup3DropdownOptionSelected == index,
                                onClick = { windowGroup3DropdownOptionSelected = index },
                            )
                        },
                ),
            )
        }

        SmallTitle(text = "Dropdown")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            OverlayDropdownPreference(
                title = "DropdownPref (O)",
                summary = if (overlayExpanded) "Expanded" else "Collapsed",
                items = dropdownOptions,
                selectedIndex = overlayDropdownOptionSelected,
                onSelectedIndexChange = { newOption ->
                    overlayDropdownOptionSelected = newOption
                },
                onExpandedChange = { overlayExpanded = it },
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            WindowDropdownPreference(
                title = "DropdownPref (W)",
                summary = if (windowExpanded) "Expanded" else "Collapsed",
                items = dropdownLongOptions,
                selectedIndex = windowDropdownOptionSelected,
                onSelectedIndexChange = { newOption ->
                    windowDropdownOptionSelected = newOption
                },
                onExpandedChange = { windowExpanded = it },
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            OverlayDropdownPreference(
                title = "Grouped DropdownPref (O)",
                summary = if (overlayGroupedExpanded) "Expanded" else "Collapsed",
                entries = overlayMultiGroupOptions,
                collapseOnSelection = false,
                onExpandedChange = { overlayGroupedExpanded = it },
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            WindowDropdownPreference(
                title = "Grouped DropdownPref (W)",
                summary = if (windowGroupedExpanded) "Expanded" else "Collapsed",
                entries = windowMultiGroupOptions,
                collapseOnSelection = false,
                onExpandedChange = { windowGroupedExpanded = it },
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            OverlayDropdownPreference(
                title = "Disabled DropdownPref (O)",
                items = listOf("Option 1"),
                selectedIndex = 0,
                onSelectedIndexChange = {},
                enabled = false,
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            WindowDropdownPreference(
                title = "Disabled DropdownPref (W)",
                items = listOf("Option 1"),
                selectedIndex = 0,
                onSelectedIndexChange = {},
                enabled = false,
            )
        }

        DropdownItemVariants()
    }
}

/**
 * Shows every COUI popup item variant in one popup each: leading icon, description, hint slot
 * (dot and count badge), group header, alert item, and a disabled row whose hint is suppressed.
 */
@Composable
private fun DropdownItemVariants() {
    var variantsSelected by remember { mutableIntStateOf(0) }
    var headersSelected by remember { mutableIntStateOf(0) }
    val iconTint = COUITheme.colorScheme.onSurfaceContainer

    val variantEntries = remember(variantsSelected, iconTint) {
        listOf(
            DropdownEntry(
                items = listOf(
                    DropdownItem(
                        text = "Icon row",
                        icon = { modifier ->
                            Icon(
                                modifier = modifier,
                                imageVector = COUIIcons.Edit,
                                tint = iconTint,
                                contentDescription = null,
                            )
                        },
                        selected = variantsSelected == 0,
                        onClick = { variantsSelected = 0 },
                    ),
                    DropdownItem(
                        text = "Row with description",
                        summary = "A second line that explains what this option changes",
                        selected = variantsSelected == 1,
                        onClick = { variantsSelected = 1 },
                    ),
                    DropdownItem(
                        text = "Row with red dot",
                        hint = { Badge() },
                        selected = variantsSelected == 2,
                        onClick = { variantsSelected = 2 },
                    ),
                    DropdownItem(
                        text = "Row with count badge",
                        hint = { Badge(count = 12) },
                        selected = variantsSelected == 3,
                        onClick = { variantsSelected = 3 },
                    ),
                    DropdownItem(
                        text = "Icon, description and hint",
                        summary = "All three slots at once",
                        icon = { modifier ->
                            Icon(
                                modifier = modifier,
                                imageVector = COUIIcons.Sort,
                                tint = iconTint,
                                contentDescription = null,
                            )
                        },
                        hint = { Badge(count = 3) },
                        selected = variantsSelected == 4,
                        onClick = { variantsSelected = 4 },
                    ),
                    DropdownItem(
                        text = "Disabled row (hint hidden)",
                        hint = { Badge(count = 9) },
                        enabled = false,
                    ),
                    DropdownItem(
                        text = "Very long title that runs past the 248dp clamp and therefore " +
                            "wraps onto a third line before it finally ellipsizes",
                        selected = variantsSelected == 6,
                        onClick = { variantsSelected = 6 },
                    ),
                    DropdownItem(
                        text = "Delete everything",
                        alert = true,
                        selected = variantsSelected == 7,
                        onClick = { variantsSelected = 7 },
                    ),
                ),
            ),
        )
    }

    val headerEntries = remember(headersSelected) {
        listOf(
            DropdownEntry(
                title = "Sort by",
                items = listOf("Name", "Date modified").mapIndexed { index, text ->
                    DropdownItem(
                        text = text,
                        selected = headersSelected == index,
                        onClick = { headersSelected = index },
                    )
                },
            ),
            DropdownEntry(
                title = "A group header long enough to need its own second line before ellipsizing",
                items = listOf(
                    DropdownItem(text = "Ascending", hint = { Badge() }),
                    DropdownItem(text = "Descending"),
                ),
            ),
            DropdownEntry(
                title = "Danger zone",
                items = listOf(DropdownItem(text = "Reset all settings", alert = true)),
            ),
        )
    }

    SmallTitle(text = "Dropdown Item Variants")
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
    ) {
        OverlayDropdownMenu(
            entries = variantEntries,
            title = "Item variants (O)",
            summary = "Icon, description, hint, alert, clamping",
            collapseOnSelection = false,
        )
        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        WindowDropdownMenu(
            entries = variantEntries,
            title = "Item variants (W)",
            summary = "Same rows in a window popup",
            collapseOnSelection = false,
        )
        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        OverlayDropdownMenu(
            entries = headerEntries,
            title = "Group headers (O)",
            summary = "Non-clickable 32dp header rows",
            collapseOnSelection = false,
        )
        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        WindowDropdownMenu(
            entries = headerEntries,
            title = "Group headers (W)",
            summary = "Headers plus group dividers",
            collapseOnSelection = false,
        )
    }
}
