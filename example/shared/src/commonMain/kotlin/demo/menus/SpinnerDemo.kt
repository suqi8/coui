// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.menus

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.OverlaySpinnerPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.preference.WindowSpinnerPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_confirm
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_menus_host
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_menus_host_overlay
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_menus_host_window
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spinner_color_blue
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spinner_color_green
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spinner_color_red
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spinner_color_yellow
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spinner_display
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spinner_display_dialog
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spinner_display_popup
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spinner_grouped
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spinner_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spinner_show_value
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spinner_state_collapsed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spinner_state_expanded
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spinner_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spinner_variant_dialog
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spinner_variant_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spinner_variant_grouped
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spinner_variant_grouped_dialog
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_spinner_variant_popup
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_option
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** The demo page of `OverlaySpinnerPreference`, listed in its category and found by the search. */
internal val SpinnerDemo = Demo(
    id = "spinner",
    apiName = "OverlaySpinnerPreference",
    title = Res.string.demo_spinner_title,
    category = DemoCategory.Menus,
    page = DemoPage.Variants {
        playground { SpinnerPlayground() }
        variant(key = "popup", title = Res.string.demo_spinner_variant_popup) {
            SpinnerHostPair(dialog = false, grouped = false, initialIndex = 0)
        }
        variant(key = "dialog", title = Res.string.demo_spinner_variant_dialog) {
            SpinnerHostPair(dialog = true, grouped = false, initialIndex = 2)
        }
        variant(key = "grouped", title = Res.string.demo_spinner_variant_grouped) {
            SpinnerHostPair(dialog = false, grouped = true)
        }
        variant(key = "grouped_dialog", title = Res.string.demo_spinner_variant_grouped_dialog) {
            SpinnerHostPair(dialog = true, grouped = true)
        }
        variant(key = "disabled", title = Res.string.demo_spinner_variant_disabled) {
            SpinnerHostPair(dialog = false, grouped = false, enabled = false)
        }
    },
    keywords = listOf("WindowSpinnerPreference"),
    searchTerms = Res.string.demo_spinner_search_terms,
)

/** The sample colour swatches of the options; sample data, not UI colours. */
private val SpinnerSwatches = listOf(
    Color(0xFFFF5B29),
    Color(0xFF36D167),
    Color(0xFF3482FF),
    Color(0xFFFFB21D),
)

private val SpinnerColorNames: List<StringResource> = listOf(
    Res.string.demo_spinner_color_red,
    Res.string.demo_spinner_color_green,
    Res.string.demo_spinner_color_blue,
    Res.string.demo_spinner_color_yellow,
)

@Composable
private fun ColumnScope.SpinnerPlayground() {
    var hostIndex by rememberSaveable { mutableIntStateOf(0) }
    var displayIndex by rememberSaveable { mutableIntStateOf(0) }
    var grouped by rememberSaveable { mutableStateOf(false) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var showValue by rememberSaveable { mutableStateOf(true) }
    var showIcons by rememberSaveable { mutableStateOf(true) }

    PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
        SpinnerRow(
            title = stringResource(Res.string.sample_title),
            window = hostIndex == 1,
            dialog = displayIndex == 1,
            grouped = grouped,
            enabled = enabled,
            showValue = showValue,
            showIcons = showIcons,
        )
    }
    val hostItems = listOf(
        stringResource(Res.string.demo_menus_host_overlay),
        stringResource(Res.string.demo_menus_host_window),
    )
    val displayItems = listOf(
        stringResource(Res.string.demo_spinner_display_popup),
        stringResource(Res.string.demo_spinner_display_dialog),
    )
    val hostTitle = stringResource(Res.string.demo_menus_host)
    val displayTitle = stringResource(Res.string.demo_spinner_display)
    val groupedTitle = stringResource(Res.string.demo_spinner_grouped)
    val enabledTitle = stringResource(Res.string.common_enabled)
    val valueTitle = stringResource(Res.string.demo_spinner_show_value)
    val iconTitle = stringResource(Res.string.common_show_icon)
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
                items = displayItems,
                selectedIndex = displayIndex,
                title = displayTitle,
                summary = "dialogButtonString",
                onSelectedIndexChange = { displayIndex = it },
            )
        }
        item {
            SwitchPreference(
                checked = grouped,
                onCheckedChange = { grouped = it },
                title = groupedTitle,
                summary = "entries",
            )
        }
        item {
            SwitchPreference(checked = enabled, onCheckedChange = { enabled = it }, title = enabledTitle)
        }
        item {
            SwitchPreference(
                checked = showValue,
                onCheckedChange = { showValue = it },
                title = valueTitle,
                summary = "showValue",
            )
        }
        item {
            SwitchPreference(checked = showIcons, onCheckedChange = { showIcons = it }, title = iconTitle)
        }
    }
}

/** An overlay and a window spinner with the same configuration. */
@Composable
private fun SpinnerHostPair(
    dialog: Boolean,
    grouped: Boolean,
    enabled: Boolean = true,
    initialIndex: Int = 0,
) {
    val overlayTitle = stringResource(Res.string.demo_menus_host_overlay)
    val windowTitle = stringResource(Res.string.demo_menus_host_window)
    CardListGroup {
        item {
            SpinnerRow(
                title = overlayTitle,
                window = false,
                dialog = dialog,
                grouped = grouped,
                enabled = enabled,
                initialIndex = initialIndex,
            )
        }
        item {
            SpinnerRow(
                title = windowTitle,
                window = true,
                dialog = dialog,
                grouped = grouped,
                enabled = enabled,
                initialIndex = initialIndex + 1,
            )
        }
    }
}

/**
 * One spinner preference row: overlay or window, popup or dialog, one list or three groups. It
 * keeps its own selection.
 */
@Composable
private fun SpinnerRow(
    title: String,
    window: Boolean,
    dialog: Boolean,
    grouped: Boolean,
    enabled: Boolean = true,
    showValue: Boolean = true,
    showIcons: Boolean = true,
    initialIndex: Int = 0,
) {
    var selected by rememberSaveable { mutableIntStateOf(initialIndex) }
    var group1 by rememberSaveable { mutableIntStateOf(0) }
    var group2 by rememberSaveable { mutableIntStateOf(0) }
    var group3 by rememberSaveable { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) }

    val items = rememberSpinnerItems(showIcons)
    val optionLabels = items.indices.map { stringResource(Res.string.sample_option, it + 1) }
    val entries = remember(items, optionLabels, group1, group2, group3) {
        listOf(
            DropdownEntry(
                items = items.take(2).mapIndexed { index, item ->
                    item.copy(selected = group1 == index, onClick = { group1 = index })
                },
            ),
            DropdownEntry(
                items = items.drop(2).mapIndexed { index, item ->
                    item.copy(selected = group2 == index, onClick = { group2 = index })
                },
            ),
            DropdownEntry(
                items = items.mapIndexed { index, item ->
                    item.copy(
                        text = optionLabels[index],
                        enabled = index % 2 == 0,
                        selected = group3 == index,
                        onClick = { group3 = index },
                    )
                },
            ),
        )
    }
    val summary = stringResource(
        if (expanded) Res.string.demo_spinner_state_expanded else Res.string.demo_spinner_state_collapsed,
    )
    val dialogButton = stringResource(Res.string.common_confirm)
    val onExpandedChange: (Boolean) -> Unit = { expanded = it }
    val onSelectedIndexChange: (Int) -> Unit = { selected = it }

    when {
        !grouped && !dialog && !window -> OverlaySpinnerPreference(
            items = items,
            selectedIndex = selected,
            title = title,
            summary = summary,
            enabled = enabled,
            showValue = showValue,
            onExpandedChange = onExpandedChange,
            onSelectedIndexChange = onSelectedIndexChange,
        )

        !grouped && !dialog -> WindowSpinnerPreference(
            items = items,
            selectedIndex = selected,
            title = title,
            summary = summary,
            enabled = enabled,
            showValue = showValue,
            onExpandedChange = onExpandedChange,
            onSelectedIndexChange = onSelectedIndexChange,
        )

        !grouped && !window -> OverlaySpinnerPreference(
            items = items,
            selectedIndex = selected,
            title = title,
            dialogButtonString = dialogButton,
            summary = summary,
            enabled = enabled,
            showValue = showValue,
            onExpandedChange = onExpandedChange,
            onSelectedIndexChange = onSelectedIndexChange,
        )

        !grouped -> WindowSpinnerPreference(
            items = items,
            selectedIndex = selected,
            title = title,
            dialogButtonString = dialogButton,
            summary = summary,
            enabled = enabled,
            showValue = showValue,
            onExpandedChange = onExpandedChange,
            onSelectedIndexChange = onSelectedIndexChange,
        )

        !dialog && !window -> OverlaySpinnerPreference(
            entries = entries,
            title = title,
            summary = summary,
            enabled = enabled,
            showValue = showValue,
            collapseOnSelection = false,
            onExpandedChange = onExpandedChange,
        )

        !dialog -> WindowSpinnerPreference(
            entries = entries,
            title = title,
            summary = summary,
            enabled = enabled,
            showValue = showValue,
            collapseOnSelection = false,
            onExpandedChange = onExpandedChange,
        )

        !window -> OverlaySpinnerPreference(
            entries = entries,
            title = title,
            dialogButtonString = dialogButton,
            summary = summary,
            enabled = enabled,
            showValue = showValue,
            onExpandedChange = onExpandedChange,
        )

        else -> WindowSpinnerPreference(
            entries = entries,
            title = title,
            dialogButtonString = dialogButton,
            summary = summary,
            enabled = enabled,
            showValue = showValue,
            onExpandedChange = onExpandedChange,
        )
    }
}

/** Four options, each with a colour swatch and the colour name as its description. */
@Composable
private fun rememberSpinnerItems(showIcons: Boolean): List<DropdownItem> {
    val labels = SpinnerSwatches.indices.map { stringResource(Res.string.sample_option, it + 1) }
    val colorNames = SpinnerColorNames.map { stringResource(it) }
    return remember(labels, colorNames, showIcons) {
        SpinnerSwatches.mapIndexed { index, swatch ->
            val swatchIcon: @Composable (Modifier) -> Unit = {
                Icon(
                    painter = RoundedRectanglePainter(),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 12.dp),
                    tint = swatch,
                )
            }
            DropdownItem(
                text = labels[index],
                summary = colorNames[index],
                icon = if (showIcons) swatchIcon else null,
            )
        }
    }
}

/** A rounded square that [Icon] tints with the swatch colour. */
private class RoundedRectanglePainter(
    private val cornerRadius: Dp = 6.dp,
) : Painter() {
    override val intrinsicSize = Size.Unspecified

    override fun DrawScope.onDraw() {
        drawRoundRect(
            color = Color.White,
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
        )
    }
}
