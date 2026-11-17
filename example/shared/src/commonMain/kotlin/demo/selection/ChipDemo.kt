// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.selection

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import io.github.suqi8.coui.kmp.basic.Chip
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconChip
import io.github.suqi8.coui.kmp.basic.SuggestionChip
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Check
import io.github.suqi8.coui.kmp.icon.basic.Search
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_search
import io.github.suqi8.coui.kmp.shared.generated.resources.common_selected
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.common_visible
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_closable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_closable_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_dark_mode
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_disabled_selected
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_downloads
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_label
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_long_label
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_nearby
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_popular
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_recommended
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_reset
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_show_count
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_show_red_dot
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_type
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_updates
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_variant_badges
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_variant_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_variant_filter
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_variant_suggestion
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_battery
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_bluetooth
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_wallpaper
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal val ChipDemo = Demo(
    id = "chip",
    apiName = "Chip",
    title = Res.string.demo_chip_title,
    category = DemoCategory.Selection,
    page = DemoPage.Variants { chipPage() },
    keywords = listOf("SuggestionChip", "IconChip", "标签", "筛选"),
)

/** The chip components of the playground, by API name. */
private val ChipTypes = listOf("Chip", "SuggestionChip", "IconChip")

/** The search history records of the suggestion variant. */
private val SuggestionRecords: List<StringResource> = listOf(
    Res.string.sample_wallpaper,
    Res.string.demo_chip_dark_mode,
    Res.string.sample_battery,
    Res.string.sample_bluetooth,
)

private fun LazyListScope.chipPage() {
    playground {
        var typeIndex by rememberSaveable { mutableIntStateOf(0) }
        var selected by rememberSaveable { mutableStateOf(true) }
        var longLabel by rememberSaveable { mutableStateOf(false) }
        var showIcon by rememberSaveable { mutableStateOf(false) }
        var showCount by rememberSaveable { mutableStateOf(false) }
        var showRedDot by rememberSaveable { mutableStateOf(false) }
        var closable by rememberSaveable { mutableStateOf(false) }
        var visible by rememberSaveable { mutableStateOf(true) }
        var enabled by rememberSaveable { mutableStateOf(true) }

        PlaygroundPreview {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (visible) {
                    ChipPreview(
                        typeIndex = typeIndex,
                        selected = selected,
                        onSelectedChange = { selected = it },
                        label = stringResource(if (longLabel) Res.string.demo_chip_long_label else Res.string.demo_chip_label),
                        showIcon = showIcon,
                        count = if (showCount) "12" else null,
                        showRedDot = showRedDot,
                        onClose = if (closable) {
                            { visible = false }
                        } else {
                            null
                        },
                        enabled = enabled,
                    )
                }
            }
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = ChipTypes,
                    selectedIndex = typeIndex,
                    title = stringResource(Res.string.demo_chip_type),
                    onSelectedIndexChange = { typeIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = selected,
                    onCheckedChange = { selected = it },
                    title = stringResource(Res.string.common_selected),
                    enabled = typeIndex == 0,
                )
            }
            item {
                SwitchPreference(
                    checked = longLabel,
                    onCheckedChange = { longLabel = it },
                    title = stringResource(Res.string.common_long_text),
                    enabled = typeIndex != 2,
                )
            }
            item {
                SwitchPreference(
                    checked = showIcon,
                    onCheckedChange = { showIcon = it },
                    title = stringResource(Res.string.common_show_icon),
                    enabled = typeIndex != 2,
                )
            }
            item {
                SwitchPreference(
                    checked = showCount,
                    onCheckedChange = { showCount = it },
                    title = stringResource(Res.string.demo_chip_show_count),
                    enabled = typeIndex != 2,
                )
            }
            item {
                SwitchPreference(
                    checked = showRedDot,
                    onCheckedChange = { showRedDot = it },
                    title = stringResource(Res.string.demo_chip_show_red_dot),
                    enabled = typeIndex != 2,
                )
            }
            item {
                SwitchPreference(
                    checked = closable,
                    onCheckedChange = { closable = it },
                    title = stringResource(Res.string.demo_chip_closable),
                    summary = stringResource(Res.string.demo_chip_closable_summary),
                    enabled = typeIndex != 2,
                )
            }
            item {
                SwitchPreference(
                    checked = visible,
                    onCheckedChange = { visible = it },
                    title = stringResource(Res.string.common_visible),
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
    variant(key = "filter", title = Res.string.demo_chip_variant_filter) {
        FilterChips()
    }
    variant(key = "badges", title = Res.string.demo_chip_variant_badges) {
        BadgeChips()
    }
    variant(key = "suggestion", title = Res.string.demo_chip_variant_suggestion) {
        SuggestionChips()
    }
    variant(key = "disabled", title = Res.string.demo_chip_variant_disabled) {
        DisabledChips()
    }
}

/**
 * The previewed chip: a [Chip], a [SuggestionChip] or an [IconChip] by [typeIndex].
 */
@Composable
private fun ChipPreview(
    typeIndex: Int,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    label: String,
    showIcon: Boolean,
    count: String?,
    showRedDot: Boolean,
    onClose: (() -> Unit)?,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val icon: (@Composable () -> Unit)? = if (showIcon) {
        { CheckIcon() }
    } else {
        null
    }
    when (typeIndex) {
        0 -> Chip(
            selected = selected,
            onClick = { onSelectedChange(!selected) },
            label = label,
            modifier = modifier,
            enabled = enabled,
            showRedDot = showRedDot,
            icon = icon,
            onClose = onClose,
            count = count,
        )

        1 -> SuggestionChip(
            onClick = {},
            label = label,
            modifier = modifier,
            enabled = enabled,
            showRedDot = showRedDot,
            icon = icon,
            onClose = onClose,
            count = count,
        )

        else -> IconChip(
            onClick = {},
            modifier = modifier,
            enabled = enabled,
        ) {
            Icon(
                imageVector = COUIIcons.Basic.Search,
                contentDescription = stringResource(Res.string.common_search),
            )
        }
    }
}

@Composable
private fun CheckIcon() {
    Icon(
        imageVector = COUIIcons.Basic.Check,
        contentDescription = null,
        modifier = Modifier.size(16.dp),
    )
}

@Composable
private fun ChipRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}

@Composable
private fun FilterChips() {
    var recommended by remember { mutableStateOf(true) }
    var nearby by remember { mutableStateOf(false) }
    var popular by remember { mutableStateOf(false) }

    ChipRow {
        Chip(
            selected = recommended,
            onClick = { recommended = !recommended },
            label = stringResource(Res.string.demo_chip_recommended),
            icon = { CheckIcon() },
        )
        Chip(
            selected = nearby,
            onClick = { nearby = !nearby },
            label = stringResource(Res.string.demo_chip_nearby),
        )
        Chip(
            selected = popular,
            onClick = { popular = !popular },
            label = stringResource(Res.string.demo_chip_popular),
        )
    }
}

@Composable
private fun BadgeChips() {
    var downloads by remember { mutableStateOf(false) }
    var closableVisible by remember { mutableStateOf(true) }

    ChipRow {
        Chip(
            selected = downloads,
            onClick = { downloads = !downloads },
            label = stringResource(Res.string.demo_chip_downloads),
            count = "12",
        )
        Chip(
            selected = false,
            onClick = {},
            label = stringResource(Res.string.demo_chip_updates),
            showRedDot = true,
        )
        if (closableVisible) {
            Chip(
                selected = false,
                onClick = {},
                label = stringResource(Res.string.demo_chip_closable),
                onClose = { closableVisible = false },
            )
        } else {
            TextButton(
                text = stringResource(Res.string.demo_chip_reset),
                onClick = { closableVisible = true },
            )
        }
    }
}

@Composable
private fun SuggestionChips() {
    // Indices into SuggestionRecords, so the list survives a language change.
    val records = remember { mutableStateListOf(*SuggestionRecords.indices.toList().toTypedArray()) }

    ChipRow {
        IconChip(onClick = {}) {
            Icon(
                imageVector = COUIIcons.Basic.Search,
                contentDescription = stringResource(Res.string.common_search),
            )
        }
        records.forEach { index ->
            SuggestionChip(
                onClick = {},
                label = stringResource(SuggestionRecords[index]),
                onClose = { records.remove(index) },
            )
        }
        if (records.isEmpty()) {
            TextButton(
                text = stringResource(Res.string.demo_chip_reset),
                onClick = { records.addAll(SuggestionRecords.indices) },
            )
        }
    }
}

@Composable
private fun DisabledChips() {
    ChipRow {
        Chip(
            selected = false,
            onClick = {},
            label = stringResource(Res.string.demo_chip_disabled),
            enabled = false,
        )
        Chip(
            selected = true,
            onClick = {},
            label = stringResource(Res.string.demo_chip_disabled_selected),
            enabled = false,
        )
    }
}
