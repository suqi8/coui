// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.LocalDemoSnackbarHostState
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.SwipeAction
import io.github.suqi8.coui.kmp.basic.SwipeActionItem
import io.github.suqi8.coui.kmp.basic.SwipeActionItemDefaults
import io.github.suqi8.coui.kmp.basic.SwipeActionMenuStyle
import io.github.suqi8.coui.kmp.basic.rememberSwipeActionItemState
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Favorites
import io.github.suqi8.coui.kmp.icon.extended.MoveFile
import io.github.suqi8.coui.kmp.icon.extended.Pin
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_clicked
import io.github.suqi8.coui.kmp.shared.generated.resources.common_delete
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_style
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_action_count
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_action_mark
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_action_min_width
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_action_move
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_action_pin
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_content_both
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_content_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_content_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_content_type
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_delete
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_delete_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_disabled_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_fast_delete
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_fast_delete_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_own_colors
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_own_colors_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_restore
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_row_book_club
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_row_design_review
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_row_flight
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_row_grocery
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_row_gym
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_row_invoice
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_row_lunch
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_row_report
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_summary_delete_only
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_summary_over_slide
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_summary_own_colors
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_summary_swipe
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_variant_rect
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_swipe_action_item_variant_round_rect
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

internal val SwipeActionItemDemo = Demo(
    id = "swipe_action_item",
    apiName = "SwipeActionItem",
    title = Res.string.demo_swipe_action_item_title,
    category = DemoCategory.Layout,
    page = DemoPage.Variants {
        swipeActionItemPlayground()
        variant(key = "rect", title = Res.string.demo_swipe_action_item_variant_rect, inCard = true) {
            RectMenuSample()
        }
        variant(key = "round_rect", title = Res.string.demo_swipe_action_item_variant_round_rect) {
            RoundRectMenuSample()
        }
    },
    keywords = listOf("侧滑", "滑动删除", "swipe", "slide"),
)

private const val CONTENT_TEXT = 0
private const val CONTENT_ICON = 1
private const val CONTENT_BOTH = 2

private const val PLAYGROUND_ROW_COUNT = 3

private val ActionMinWidths = listOf(64.dp, SwipeActionItemDefaults.ActionMinWidth, 96.dp)

private val ActionIcons: List<ImageVector>
    get() = listOf(COUIIcons.Pin, COUIIcons.Favorites, COUIIcons.MoveFile)

/** The labels of the sample actions: pin, mark, move. */
@Composable
private fun actionLabels(): List<String> = listOf(
    stringResource(Res.string.demo_swipe_action_item_action_pin),
    stringResource(Res.string.demo_swipe_action_item_action_mark),
    stringResource(Res.string.demo_swipe_action_item_action_move),
)

private fun LazyListScope.swipeActionItemPlayground() {
    playground {
        var styleIndex by rememberSaveable { mutableIntStateOf(SwipeActionMenuStyle.Rect.ordinal) }
        var actionCount by rememberSaveable { mutableIntStateOf(2) }
        var ownColors by rememberSaveable { mutableStateOf(true) }
        var contentType by rememberSaveable { mutableIntStateOf(CONTENT_TEXT) }
        var deleteEnabled by rememberSaveable { mutableStateOf(true) }
        var fastDelete by rememberSaveable { mutableStateOf(false) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        var minWidthIndex by rememberSaveable { mutableIntStateOf(1) }
        val rows = remember { mutableStateListOf(*Array(PLAYGROUND_ROW_COUNT) { it }) }
        val scope = rememberCoroutineScope()
        val snackbarHostState = LocalDemoSnackbarHostState.current

        val labels = actionLabels()
        val ownColorList = listOf(
            COUITheme.colorScheme.primary,
            COUITheme.colorScheme.orange,
            COUITheme.colorScheme.green,
        )
        val actions = List(actionCount) { index ->
            SwipeAction(
                label = if (contentType == CONTENT_ICON) null else labels[index],
                icon = if (contentType == CONTENT_TEXT) null else ActionIcons[index],
                color = if (ownColors) ownColorList[index] else Color.Unspecified,
                contentDescription = labels[index],
            )
        }
        val rowTitles = playgroundRowTitles()
        val rowColor = COUITheme.colorScheme.cardBackground
        val deleteDescription = stringResource(Res.string.common_delete)
        val swipeSummary = stringResource(Res.string.demo_swipe_action_item_summary_swipe)

        PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
            rows.forEach { id ->
                // Key by id so a deleted row's state does not move to its neighbour.
                key(id) {
                    val state = rememberSwipeActionItemState()
                    SwipeActionItem(
                        actions = actions,
                        onActionClick = { index ->
                            scope.launch { state.close() }
                            scope.launch {
                                snackbarHostState.showSnackbar(getString(Res.string.common_clicked, labels[index]))
                            }
                        },
                        state = state,
                        enabled = enabled,
                        onDelete = if (deleteEnabled) {
                            { rows.remove(id) }
                        } else {
                            null
                        },
                        fastDeleteEnabled = fastDelete,
                        deleteContentDescription = deleteDescription,
                        menuStyle = SwipeActionMenuStyle.entries[styleIndex],
                        actionMinWidth = ActionMinWidths[minWidthIndex],
                    ) {
                        BasicComponent(
                            modifier = Modifier.background(rowColor),
                            title = rowTitles[id],
                            summary = swipeSummary,
                        )
                    }
                }
            }
            if (rows.size < PLAYGROUND_ROW_COUNT) {
                BasicComponent(
                    title = stringResource(Res.string.demo_swipe_action_item_restore),
                    onClick = {
                        rows.clear()
                        rows.addAll((0 until PLAYGROUND_ROW_COUNT).toList())
                    },
                )
            }
        }

        val styleTitle = stringResource(Res.string.common_style)
        val countTitle = stringResource(Res.string.demo_swipe_action_item_action_count)
        val contentTitle = stringResource(Res.string.demo_swipe_action_item_content_type)
        val contentLabels = listOf(
            stringResource(Res.string.demo_swipe_action_item_content_text),
            stringResource(Res.string.demo_swipe_action_item_content_icon),
            stringResource(Res.string.demo_swipe_action_item_content_both),
        )
        val ownColorsTitle = stringResource(Res.string.demo_swipe_action_item_own_colors)
        val ownColorsSummary = stringResource(Res.string.demo_swipe_action_item_own_colors_summary)
        val deleteTitle = stringResource(Res.string.demo_swipe_action_item_delete)
        val deleteSummary = stringResource(Res.string.demo_swipe_action_item_delete_summary)
        val fastDeleteTitle = stringResource(Res.string.demo_swipe_action_item_fast_delete)
        val fastDeleteSummary = stringResource(Res.string.demo_swipe_action_item_fast_delete_summary)
        val enabledTitle = stringResource(Res.string.common_enabled)
        val minWidthTitle = stringResource(Res.string.demo_swipe_action_item_action_min_width)
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = SwipeActionMenuStyle.entries.map { it.name },
                    selectedIndex = styleIndex,
                    title = styleTitle,
                    onSelectedIndexChange = { styleIndex = it },
                )
            }
            item {
                SliderPreference(
                    value = actionCount.toFloat(),
                    onValueChange = { actionCount = it.roundToInt() },
                    title = countTitle,
                    valueText = "$actionCount",
                    valueRange = 0f..3f,
                    steps = 2,
                )
            }
            item {
                OverlayDropdownPreference(
                    items = contentLabels,
                    selectedIndex = contentType,
                    title = contentTitle,
                    onSelectedIndexChange = { contentType = it },
                )
            }
            item {
                SwitchPreference(
                    checked = ownColors,
                    onCheckedChange = { ownColors = it },
                    title = ownColorsTitle,
                    summary = ownColorsSummary,
                )
            }
            item {
                OverlayDropdownPreference(
                    items = ActionMinWidths.map { "${it.value.roundToInt()}dp" },
                    selectedIndex = minWidthIndex,
                    title = minWidthTitle,
                    onSelectedIndexChange = { minWidthIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = deleteEnabled,
                    onCheckedChange = { deleteEnabled = it },
                    title = deleteTitle,
                    summary = deleteSummary,
                )
            }
            item {
                SwitchPreference(
                    checked = fastDelete,
                    onCheckedChange = { fastDelete = it },
                    title = fastDeleteTitle,
                    summary = fastDeleteSummary,
                    enabled = deleteEnabled,
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

@Composable
private fun playgroundRowTitles(): List<String> = listOf(
    stringResource(Res.string.demo_swipe_action_item_row_report),
    stringResource(Res.string.demo_swipe_action_item_row_lunch),
    stringResource(Res.string.demo_swipe_action_item_row_flight),
)

@Composable
private fun RectMenuSample() {
    val titles = listOf(
        stringResource(Res.string.demo_swipe_action_item_row_report),
        stringResource(Res.string.demo_swipe_action_item_row_lunch),
        stringResource(Res.string.demo_swipe_action_item_row_flight),
        stringResource(Res.string.demo_swipe_action_item_row_invoice),
        stringResource(Res.string.demo_swipe_action_item_row_design_review),
    )
    val labels = actionLabels()
    val rows = remember { mutableStateListOf(*Array(titles.size) { it }) }
    val scope = rememberCoroutineScope()
    val rowColor = COUITheme.colorScheme.cardBackground
    val deleteDescription = stringResource(Res.string.common_delete)
    val overSlideSummary = stringResource(Res.string.demo_swipe_action_item_summary_over_slide)
    val swipeSummary = stringResource(Res.string.demo_swipe_action_item_summary_swipe)
    rows.forEachIndexed { index, id ->
        // Key by id so a deleted row's state does not move to its neighbour.
        key(id) {
            val state = rememberSwipeActionItemState()
            val actions = when (index % 3) {
                0 -> listOf(SwipeAction(label = labels[0], color = COUITheme.colorScheme.primary))

                1 -> listOf(
                    SwipeAction(label = labels[1], color = COUITheme.colorScheme.primary),
                    SwipeAction(label = labels[2], color = COUITheme.colorScheme.labelSecondary),
                )

                else -> emptyList()
            }
            SwipeActionItem(
                actions = actions,
                onActionClick = { scope.launch { state.close() } },
                state = state,
                onDelete = { rows.remove(id) },
                fastDeleteEnabled = index % 3 == 2,
                deleteContentDescription = deleteDescription,
            ) {
                BasicComponent(
                    modifier = Modifier.background(rowColor),
                    title = titles[id],
                    summary = if (index % 3 == 2) overSlideSummary else swipeSummary,
                )
            }
        }
    }
    if (rows.size < titles.size) {
        BasicComponent(
            title = stringResource(Res.string.demo_swipe_action_item_restore),
            onClick = {
                rows.clear()
                rows.addAll(titles.indices.toList())
            },
        )
    }
    SwipeActionItem(
        actions = emptyList(),
        onActionClick = {},
        enabled = false,
        onDelete = {},
    ) {
        BasicComponent(
            modifier = Modifier.background(rowColor),
            title = stringResource(Res.string.demo_swipe_action_item_disabled),
            summary = stringResource(Res.string.demo_swipe_action_item_disabled_summary),
        )
    }
}

@Composable
private fun RoundRectMenuSample() {
    val titles = listOf(
        stringResource(Res.string.demo_swipe_action_item_row_grocery),
        stringResource(Res.string.demo_swipe_action_item_row_gym),
        stringResource(Res.string.demo_swipe_action_item_row_book_club),
    )
    val summaries = listOf(
        stringResource(Res.string.demo_swipe_action_item_summary_delete_only),
        stringResource(Res.string.demo_swipe_action_item_summary_own_colors),
        stringResource(Res.string.demo_swipe_action_item_summary_over_slide),
    )
    val labels = actionLabels()
    val rows = remember { mutableStateListOf(*Array(titles.size) { it }) }
    val scope = rememberCoroutineScope()
    val rowColor = COUITheme.colorScheme.cardBackground
    val deleteDescription = stringResource(Res.string.common_delete)
    rows.forEachIndexed { index, id ->
        key(id) {
            val state = rememberSwipeActionItemState()
            val actions = when (index % 3) {
                1 -> listOf(
                    SwipeAction(label = labels[1], color = COUITheme.colorScheme.primary),
                    SwipeAction(label = labels[2], color = COUITheme.colorScheme.labelSecondary),
                )

                else -> emptyList()
            }
            SwipeActionItem(
                actions = actions,
                onActionClick = { scope.launch { state.close() } },
                state = state,
                onDelete = { rows.remove(id) },
                fastDeleteEnabled = index % 3 == 2,
                deleteContentDescription = deleteDescription,
                menuStyle = SwipeActionMenuStyle.RoundRect,
            ) {
                BasicComponent(
                    modifier = Modifier.background(rowColor),
                    title = titles[id],
                    summary = summaries[index % 3],
                )
            }
        }
    }
    if (rows.size < titles.size) {
        BasicComponent(
            title = stringResource(Res.string.demo_swipe_action_item_restore),
            onClick = {
                rows.clear()
                rows.addAll(titles.indices.toList())
            },
        )
    }
}
