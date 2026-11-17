// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.ListDetailLayout
import io.github.suqi8.coui.kmp.basic.ListDetailLayoutDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.ArrowRight
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_back
import io.github.suqi8.coui.kmp.shared.generated.resources.common_on
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_layout_simulated_width
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_layout_simulated_width_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_detail_layout_about
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_detail_layout_detail_body
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_detail_layout_display_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_detail_layout_divider_thickness
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_detail_layout_empty
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_detail_layout_list_fraction
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_detail_layout_max_list_width
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_detail_layout_min_list_width
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_detail_layout_none
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_detail_layout_selected
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_detail_layout_show_empty_detail
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_detail_layout_show_empty_detail_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_detail_layout_sound_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_detail_layout_storage_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_detail_layout_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_detail_layout_variant_default
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_detail_layout_wifi_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_battery
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_bluetooth
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_display
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_notifications
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_sound
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_storage
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_wifi
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

internal val ListDetailLayoutDemo = Demo(
    id = "list_detail_layout",
    apiName = "ListDetailLayout",
    title = Res.string.demo_list_detail_layout_title,
    category = DemoCategory.Layout,
    page = DemoPage.Variants {
        listDetailLayoutPlayground()
        variant(key = "default", title = Res.string.demo_list_detail_layout_variant_default) {
            var selected by rememberSaveable { mutableIntStateOf(NO_SELECTION) }
            ListDetailSample(
                selected = selected,
                onSelectedChange = { selected = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PreviewHeight),
            )
        }
    },
    keywords = listOf("双栏", "平板", "列表", "详情", "split", "master-detail"),
)

private const val NO_SELECTION = -1

private val PreviewHeight = 420.dp

private val MinListWidths = listOf(200.dp, ListDetailLayoutDefaults.MinListWidth, 320.dp)

private val MaxListWidths = listOf(320.dp, ListDetailLayoutDefaults.MaxListWidth, 520.dp)

private val DividerThicknesses = listOf(ListDetailLayoutDefaults.DividerThickness, 1.dp, 4.dp)

private fun LazyListScope.listDetailLayoutPlayground() {
    playground {
        // 840dp by default, so the split shows on a phone too.
        var widthIndex by rememberSaveable { mutableIntStateOf(3) }
        var fraction by rememberSaveable { mutableFloatStateOf(ListDetailLayoutDefaults.ListWidthFraction) }
        var minWidthIndex by rememberSaveable { mutableIntStateOf(1) }
        var maxWidthIndex by rememberSaveable { mutableIntStateOf(1) }
        var dividerIndex by rememberSaveable { mutableIntStateOf(0) }
        var showEmptyDetail by rememberSaveable { mutableStateOf(true) }
        var selected by rememberSaveable { mutableIntStateOf(NO_SELECTION) }

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            SimulatedWidthBox(simulatedWidth = SimulatedWidths[widthIndex], height = PreviewHeight) {
                ListDetailSample(
                    selected = selected,
                    onSelectedChange = { selected = it },
                    modifier = Modifier.fillMaxSize(),
                    listWidthFraction = fraction,
                    minListWidth = MinListWidths[minWidthIndex],
                    maxListWidth = MaxListWidths[maxWidthIndex],
                    dividerThickness = DividerThicknesses[dividerIndex],
                    showEmptyDetail = showEmptyDetail,
                )
            }
        }

        val widthLabels = simulatedWidthLabels()
        val selectionLabels = listOf(stringResource(Res.string.demo_list_detail_layout_none)) +
            settingsItems().map { it.first }
        val widthTitle = stringResource(Res.string.demo_layout_simulated_width)
        val widthSummary = stringResource(Res.string.demo_layout_simulated_width_summary)
        val selectedTitle = stringResource(Res.string.demo_list_detail_layout_selected)
        val fractionTitle = stringResource(Res.string.demo_list_detail_layout_list_fraction)
        val minWidthTitle = stringResource(Res.string.demo_list_detail_layout_min_list_width)
        val maxWidthTitle = stringResource(Res.string.demo_list_detail_layout_max_list_width)
        val dividerTitle = stringResource(Res.string.demo_list_detail_layout_divider_thickness)
        val emptyTitle = stringResource(Res.string.demo_list_detail_layout_show_empty_detail)
        val emptySummary = stringResource(Res.string.demo_list_detail_layout_show_empty_detail_summary)
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = widthLabels,
                    selectedIndex = widthIndex,
                    title = widthTitle,
                    summary = widthSummary,
                    onSelectedIndexChange = { widthIndex = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    items = selectionLabels,
                    selectedIndex = selected + 1,
                    title = selectedTitle,
                    onSelectedIndexChange = { selected = it - 1 },
                )
            }
            item {
                SliderPreference(
                    value = fraction,
                    onValueChange = { fraction = it },
                    title = fractionTitle,
                    valueText = "${(fraction * 100).roundToInt()}%",
                    valueRange = 0.2f..0.8f,
                    steps = 11,
                )
            }
            item {
                OverlayDropdownPreference(
                    items = MinListWidths.map { it.label() },
                    selectedIndex = minWidthIndex,
                    title = minWidthTitle,
                    onSelectedIndexChange = { minWidthIndex = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    items = MaxListWidths.map { it.label() },
                    selectedIndex = maxWidthIndex,
                    title = maxWidthTitle,
                    onSelectedIndexChange = { maxWidthIndex = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    items = DividerThicknesses.map { it.label() },
                    selectedIndex = dividerIndex,
                    title = dividerTitle,
                    onSelectedIndexChange = { dividerIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = showEmptyDetail,
                    onCheckedChange = { showEmptyDetail = it },
                    title = emptyTitle,
                    summary = emptySummary,
                )
            }
        }
    }
}

/** Formats a dp value without a trailing `.0`, e.g. `280dp` or `0.33dp`. */
private fun Dp.label(): String {
    val rounded = value.roundToInt()
    return if (value == rounded.toFloat()) "${rounded}dp" else "${value}dp"
}

/** The sample settings rows: title and optional summary. */
@Composable
private fun settingsItems(): List<Pair<String, String?>> = listOf(
    stringResource(Res.string.sample_wifi) to stringResource(Res.string.demo_list_detail_layout_wifi_summary),
    stringResource(Res.string.sample_bluetooth) to stringResource(Res.string.common_on),
    stringResource(Res.string.sample_display) to stringResource(Res.string.demo_list_detail_layout_display_summary),
    stringResource(Res.string.sample_sound) to stringResource(Res.string.demo_list_detail_layout_sound_summary),
    stringResource(Res.string.sample_notifications) to null,
    stringResource(Res.string.sample_battery) to "86%",
    stringResource(Res.string.sample_storage) to stringResource(Res.string.demo_list_detail_layout_storage_summary, "128 GB"),
    stringResource(Res.string.demo_list_detail_layout_about) to null,
)

@Composable
private fun ListDetailSample(
    selected: Int,
    onSelectedChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    listWidthFraction: Float = ListDetailLayoutDefaults.ListWidthFraction,
    minListWidth: Dp = ListDetailLayoutDefaults.MinListWidth,
    maxListWidth: Dp = ListDetailLayoutDefaults.MaxListWidth,
    dividerThickness: Dp = ListDetailLayoutDefaults.DividerThickness,
    showEmptyDetail: Boolean = true,
) {
    val items = settingsItems()
    val selectedItem = items.getOrNull(selected)
    ListDetailLayout(
        list = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                items.forEachIndexed { index, (title, summary) ->
                    BasicComponent(
                        title = title,
                        summary = summary,
                        onClick = { onSelectedChange(index) },
                        holdDownState = selected == index,
                    )
                }
            }
        },
        detail = selectedItem?.let { item ->
            { DetailPane(title = item.first, onBack = { onSelectedChange(NO_SELECTION) }) }
        },
        modifier = modifier,
        listWidthFraction = listWidthFraction,
        minListWidth = minListWidth,
        maxListWidth = maxListWidth,
        dividerThickness = dividerThickness,
        emptyDetail = {
            if (showEmptyDetail) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(Res.string.demo_list_detail_layout_empty),
                        style = COUITheme.textStyles.bodyM,
                        color = COUITheme.colorScheme.labelSecondary,
                    )
                }
            }
        },
    )
}

@Composable
private fun DetailPane(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(COUITheme.colorScheme.backgroundWithCard)
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // A back button for the single-pane arrangement; in the split it clears the selection.
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = COUIIcons.Basic.ArrowRight,
                    contentDescription = stringResource(Res.string.common_back),
                    modifier = Modifier.graphicsLayer { scaleX = -1f },
                )
            }
            Text(
                text = title,
                modifier = Modifier.padding(start = 8.dp),
                style = COUITheme.textStyles.headlineM,
            )
        }
        Text(
            text = stringResource(Res.string.demo_list_detail_layout_detail_body, title),
            modifier = Modifier.padding(start = 4.dp, top = 12.dp),
            style = COUITheme.textStyles.bodyM,
            color = COUITheme.colorScheme.labelSecondary,
        )
    }
}
