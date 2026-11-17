// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.DemoSection
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.ResponsiveGrid
import io.github.suqi8.coui.kmp.basic.ResponsiveGridSpec
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_mode
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_layout_simulated_width
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_layout_simulated_width_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_auto
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_card_list_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_card_list_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_child_grid_number
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_child_min_width
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_child_width
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_fixed_height
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_fixed_height_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_grid_number
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_horizontal_gap
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_margin_type
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_min_horizontal_gap
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_parent_child
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_parent_child_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_percent_mode
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_percent_type
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_vertical_gap
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_wider_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_responsive_grid_wider_title
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.GridMarginType
import io.github.suqi8.coui.kmp.utils.PercentWidthDefaults
import io.github.suqi8.coui.kmp.utils.PercentWidthMode
import io.github.suqi8.coui.kmp.utils.PercentWidthType
import io.github.suqi8.coui.kmp.utils.percentWidth
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/** The demo page of `ResponsiveGrid`, listed in its category and found by the search. */
internal val ResponsiveGridDemo = Demo(
    id = "responsive_grid",
    apiName = "ResponsiveGrid",
    title = Res.string.demo_responsive_grid_title,
    category = DemoCategory.Layout,
    page = DemoPage.Variants {
        responsiveGridPlayground()
        // Grid mode: each tile spans two layout-grid columns, so 2 / 4 / 6 tiles per row on
        // compact / medium / expanded widths. The grid brings its own margins.
        variant(key = "grid", title = "ResponsiveGridSpec.Grid", inCard = false) {
            ResponsiveGrid(
                spec = ResponsiveGridSpec.Grid(childGridNumber = 2),
                verticalGap = 12.dp,
                childHeight = 72.dp,
            ) {
                repeat(8) { GridTile(label = "${it + 1}", color = COUITheme.colorScheme.cardBackground) }
            }
        }
        // Specific-gap mode: tiles at least 120dp wide, 8dp apart, stretched to fill the row.
        variant(key = "specific_gap", title = "ResponsiveGridSpec.SpecificGap", inCard = false) {
            ResponsiveGrid(
                spec = ResponsiveGridSpec.SpecificGap(childMinWidth = 120.dp, horizontalGap = 8.dp, childMinHeight = 60.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalGap = 8.dp,
            ) {
                repeat(7) { GridTile(label = "${it + 1}", color = COUITheme.colorScheme.cardBackground) }
            }
        }
        // Specific-size mode: fixed 96dp tiles, the leftover width spread between them.
        variant(key = "specific_size", title = "ResponsiveGridSpec.SpecificSize", inCard = false) {
            ResponsiveGrid(
                spec = ResponsiveGridSpec.SpecificSize(childWidth = 96.dp, minHorizontalGap = 8.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalGap = 8.dp,
                childHeight = 56.dp,
            ) {
                repeat(6) { GridTile(label = "${it + 1}", color = COUITheme.colorScheme.cardBackground) }
            }
        }
        percentWidthPlayground()
        // A card list on the percent-width grid: 4 / 6 / 8 columns wide by the window size,
        // widened by the 16dp card-list special padding.
        variant(key = "percent_width_card_list", title = "percentWidth(CardList)", inCard = false) {
            Box(Modifier.fillMaxWidth().percentWidth(type = PercentWidthType.CardList)) {
                Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                    BasicComponent(
                        title = stringResource(Res.string.demo_responsive_grid_card_list_title),
                        summary = stringResource(Res.string.demo_responsive_grid_card_list_summary),
                    )
                    BasicComponent(
                        title = stringResource(Res.string.demo_responsive_grid_wider_title),
                        summary = stringResource(Res.string.demo_responsive_grid_wider_summary),
                    )
                }
            }
        }
    },
    keywords = listOf("percentWidth"),
    searchTerms = Res.string.demo_responsive_grid_search_terms,
)

private const val MODE_GRID = 0
private const val MODE_SPECIFIC_GAP = 1
private const val MODE_SPECIFIC_SIZE = 2

private val GridModeLabels = listOf("Grid", "SpecificGap", "SpecificSize")

private val PercentGridNumbers = listOf(PercentWidthDefaults.AUTO_GRID_NUMBER, 2, 4, 6, 8, 12)

private fun LazyListScope.responsiveGridPlayground() {
    playground {
        var widthIndex by rememberSaveable { mutableIntStateOf(0) }
        var mode by rememberSaveable { mutableIntStateOf(MODE_GRID) }
        var childGridNumber by rememberSaveable { mutableIntStateOf(2) }
        var marginTypeIndex by rememberSaveable { mutableIntStateOf(GridMarginType.Small.ordinal) }
        var childMinWidth by rememberSaveable { mutableFloatStateOf(120f) }
        var horizontalGap by rememberSaveable { mutableFloatStateOf(8f) }
        var childWidth by rememberSaveable { mutableFloatStateOf(96f) }
        var minHorizontalGap by rememberSaveable { mutableFloatStateOf(8f) }
        var verticalGap by rememberSaveable { mutableFloatStateOf(12f) }
        var count by rememberSaveable { mutableIntStateOf(8) }
        var fixedHeight by rememberSaveable { mutableStateOf(true) }

        val spec = when (mode) {
            MODE_SPECIFIC_GAP -> ResponsiveGridSpec.SpecificGap(childMinWidth = childMinWidth.dp, horizontalGap = horizontalGap.dp)

            MODE_SPECIFIC_SIZE -> ResponsiveGridSpec.SpecificSize(childWidth = childWidth.dp, minHorizontalGap = minHorizontalGap.dp)

            else -> ResponsiveGridSpec.Grid(
                childGridNumber = childGridNumber,
                marginType = GridMarginType.entries[marginTypeIndex],
            )
        }
        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            SimulatedWidthBox(simulatedWidth = SimulatedWidths[widthIndex]) {
                ResponsiveGrid(
                    spec = spec,
                    // Grid mode insets by the grid margin itself; the other modes fill their bounds.
                    modifier = Modifier.padding(horizontal = if (mode == MODE_GRID) 0.dp else 16.dp),
                    verticalGap = verticalGap.dp,
                    childHeight = if (fixedHeight) 72.dp else Dp.Unspecified,
                ) {
                    repeat(count) { GridTile(label = "${it + 1}", color = COUITheme.colorScheme.cardPressed) }
                }
            }
        }

        val widthLabels = simulatedWidthLabels()
        val widthTitle = stringResource(Res.string.demo_layout_simulated_width)
        val widthSummary = stringResource(Res.string.demo_layout_simulated_width_summary)
        val modeTitle = stringResource(Res.string.common_mode)
        val gridNumberTitle = stringResource(Res.string.demo_responsive_grid_child_grid_number)
        val marginTypeTitle = stringResource(Res.string.demo_responsive_grid_margin_type)
        val minWidthTitle = stringResource(Res.string.demo_responsive_grid_child_min_width)
        val horizontalGapTitle = stringResource(Res.string.demo_responsive_grid_horizontal_gap)
        val childWidthTitle = stringResource(Res.string.demo_responsive_grid_child_width)
        val minGapTitle = stringResource(Res.string.demo_responsive_grid_min_horizontal_gap)
        val verticalGapTitle = stringResource(Res.string.demo_responsive_grid_vertical_gap)
        val countTitle = stringResource(Res.string.common_count)
        val fixedHeightTitle = stringResource(Res.string.demo_responsive_grid_fixed_height)
        val fixedHeightSummary = stringResource(Res.string.demo_responsive_grid_fixed_height_summary)
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
                    items = GridModeLabels,
                    selectedIndex = mode,
                    title = modeTitle,
                    onSelectedIndexChange = { mode = it },
                )
            }
            when (mode) {
                MODE_SPECIFIC_GAP -> {
                    item {
                        SliderPreference(
                            value = childMinWidth,
                            onValueChange = { childMinWidth = it },
                            title = minWidthTitle,
                            valueText = "${childMinWidth.roundToInt()}dp",
                            valueRange = 60f..200f,
                            steps = 13,
                        )
                    }
                    item {
                        SliderPreference(
                            value = horizontalGap,
                            onValueChange = { horizontalGap = it },
                            title = horizontalGapTitle,
                            valueText = "${horizontalGap.roundToInt()}dp",
                            valueRange = 0f..24f,
                            steps = 5,
                        )
                    }
                }

                MODE_SPECIFIC_SIZE -> {
                    item {
                        SliderPreference(
                            value = childWidth,
                            onValueChange = { childWidth = it },
                            title = childWidthTitle,
                            valueText = "${childWidth.roundToInt()}dp",
                            valueRange = 48f..160f,
                            steps = 13,
                        )
                    }
                    item {
                        SliderPreference(
                            value = minHorizontalGap,
                            onValueChange = { minHorizontalGap = it },
                            title = minGapTitle,
                            valueText = "${minHorizontalGap.roundToInt()}dp",
                            valueRange = 0f..24f,
                            steps = 5,
                        )
                    }
                }

                else -> {
                    item {
                        OverlayDropdownPreference(
                            items = listOf("1", "2", "3", "4"),
                            selectedIndex = childGridNumber - 1,
                            title = gridNumberTitle,
                            onSelectedIndexChange = { childGridNumber = it + 1 },
                        )
                    }
                    item {
                        OverlayDropdownPreference(
                            items = GridMarginType.entries.map { it.name },
                            selectedIndex = marginTypeIndex,
                            title = marginTypeTitle,
                            onSelectedIndexChange = { marginTypeIndex = it },
                        )
                    }
                }
            }
            item {
                SliderPreference(
                    value = verticalGap,
                    onValueChange = { verticalGap = it },
                    title = verticalGapTitle,
                    valueText = "${verticalGap.roundToInt()}dp",
                    valueRange = 0f..24f,
                    steps = 5,
                )
            }
            item {
                SliderPreference(
                    value = count.toFloat(),
                    onValueChange = { count = it.roundToInt() },
                    title = countTitle,
                    valueText = "$count",
                    valueRange = 1f..24f,
                    steps = 22,
                    showKeyPoints = false,
                )
            }
            item {
                SwitchPreference(
                    checked = fixedHeight,
                    onCheckedChange = { fixedHeight = it },
                    title = fixedHeightTitle,
                    summary = fixedHeightSummary,
                )
            }
        }
    }
}

private fun LazyListScope.percentWidthPlayground() {
    item(key = "percent_width") {
        var widthIndex by rememberSaveable { mutableIntStateOf(0) }
        var gridNumberIndex by rememberSaveable { mutableIntStateOf(0) }
        var typeIndex by rememberSaveable { mutableIntStateOf(PercentWidthType.CardList.ordinal) }
        var marginTypeIndex by rememberSaveable { mutableIntStateOf(GridMarginType.Large.ordinal) }
        var modeIndex by rememberSaveable { mutableIntStateOf(PercentWidthMode.Padding.ordinal) }
        var parentChildHierarchy by rememberSaveable { mutableStateOf(false) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        val type = PercentWidthType.entries[typeIndex]

        Column {
            // The preview sits on the page background, as a percent-width page would.
            DemoSection(title = "percentWidth", inCard = false) {
                SimulatedWidthBox(simulatedWidth = SimulatedWidths[widthIndex]) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(COUITheme.colorScheme.fillThin)
                            .padding(vertical = 12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .percentWidth(
                                    gridNumber = PercentGridNumbers[gridNumberIndex],
                                    type = type,
                                    marginType = GridMarginType.entries[marginTypeIndex],
                                    mode = PercentWidthMode.entries[modeIndex],
                                    parentChildHierarchy = parentChildHierarchy,
                                    enabled = enabled,
                                ),
                        ) {
                            // The rows' own inset that the list / card-list special padding lines up.
                            val inset = when (type) {
                                PercentWidthType.Default -> 0.dp
                                PercentWidthType.List -> PercentWidthDefaults.ListSpecialPadding
                                PercentWidthType.CardList -> PercentWidthDefaults.CardSpecialPadding
                            }
                            Card(modifier = Modifier.padding(horizontal = inset)) {
                                BasicComponent(
                                    title = stringResource(Res.string.demo_responsive_grid_card_list_title),
                                    summary = stringResource(Res.string.demo_responsive_grid_card_list_summary),
                                )
                                BasicComponent(
                                    title = stringResource(Res.string.demo_responsive_grid_wider_title),
                                    summary = stringResource(Res.string.demo_responsive_grid_wider_summary),
                                )
                            }
                        }
                    }
                }
            }

            val widthLabels = simulatedWidthLabels()
            val autoLabel = stringResource(Res.string.demo_responsive_grid_auto)
            val widthTitle = stringResource(Res.string.demo_layout_simulated_width)
            val widthSummary = stringResource(Res.string.demo_layout_simulated_width_summary)
            val gridNumberTitle = stringResource(Res.string.demo_responsive_grid_grid_number)
            val typeTitle = stringResource(Res.string.demo_responsive_grid_percent_type)
            val marginTypeTitle = stringResource(Res.string.demo_responsive_grid_margin_type)
            val modeTitle = stringResource(Res.string.demo_responsive_grid_percent_mode)
            val parentChildTitle = stringResource(Res.string.demo_responsive_grid_parent_child)
            val parentChildSummary = stringResource(Res.string.demo_responsive_grid_parent_child_summary)
            val enabledTitle = stringResource(Res.string.common_enabled)
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
                        items = PercentGridNumbers.map { if (it == PercentWidthDefaults.AUTO_GRID_NUMBER) autoLabel else "$it" },
                        selectedIndex = gridNumberIndex,
                        title = gridNumberTitle,
                        onSelectedIndexChange = { gridNumberIndex = it },
                    )
                }
                item {
                    OverlayDropdownPreference(
                        items = PercentWidthType.entries.map { it.name },
                        selectedIndex = typeIndex,
                        title = typeTitle,
                        onSelectedIndexChange = { typeIndex = it },
                    )
                }
                item {
                    OverlayDropdownPreference(
                        items = GridMarginType.entries.map { it.name },
                        selectedIndex = marginTypeIndex,
                        title = marginTypeTitle,
                        onSelectedIndexChange = { marginTypeIndex = it },
                    )
                }
                item {
                    OverlayDropdownPreference(
                        items = PercentWidthMode.entries.map { it.name },
                        selectedIndex = modeIndex,
                        title = modeTitle,
                        onSelectedIndexChange = { modeIndex = it },
                    )
                }
                item {
                    SwitchPreference(
                        checked = parentChildHierarchy,
                        onCheckedChange = { parentChildHierarchy = it },
                        title = parentChildTitle,
                        summary = parentChildSummary,
                    )
                }
                item {
                    SwitchPreference(
                        checked = enabled,
                        onCheckedChange = { enabled = it },
                        title = enabledTitle,
                    )
                }
            }
        }
    }
}

@Composable
private fun GridTile(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, style = COUITheme.textStyles.bodyM)
    }
}
