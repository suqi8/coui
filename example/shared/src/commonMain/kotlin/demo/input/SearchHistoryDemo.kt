// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.input

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.SearchHistory
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_accessibility
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_always_on_display
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_app_lock
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_battery_health
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_clear_description
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_cleared
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_dark_mode
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_developer_options
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_do_not_disturb
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_expandable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_expandable_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_folded_lines
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_fonts
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_hotspot
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_last_clicked
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_location
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_long_record
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_none
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_notification_history
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_restore
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_screen_time
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_variant_default
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_search_history_wallpaper
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_bluetooth
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_storage
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_wifi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/** The demo page of `SearchHistory`, listed in its category and found by the search. */
internal val SearchHistoryDemo = Demo(
    id = "search_history",
    apiName = "SearchHistory",
    title = Res.string.demo_search_history_title,
    category = DemoCategory.Input,
    page = DemoPage.Variants { searchHistoryPage() },
    keywords = listOf("COUISearchHistoryView"),
    searchTerms = Res.string.demo_search_history_search_terms,
)

/** The sample search records, in display order. */
private val HistoryRecords: List<StringResource> = listOf(
    Res.string.demo_search_history_wallpaper,
    Res.string.demo_search_history_dark_mode,
    Res.string.sample_bluetooth,
    Res.string.demo_search_history_battery_health,
    Res.string.demo_search_history_always_on_display,
    Res.string.demo_search_history_notification_history,
    Res.string.demo_search_history_screen_time,
    Res.string.demo_search_history_fonts,
    Res.string.sample_wifi,
    Res.string.demo_search_history_do_not_disturb,
    Res.string.sample_storage,
    Res.string.demo_search_history_app_lock,
    Res.string.demo_search_history_developer_options,
    Res.string.demo_search_history_hotspot,
    Res.string.demo_search_history_location,
    Res.string.demo_search_history_accessibility,
)

/** The [SearchHistorySample] click read-out of the long record, which is not in [HistoryRecords]. */
private const val LONG_RECORD = -2

/** The folded line counts the playground offers. */
private val FoldedLines = listOf(1, 2, 3, 4)

private fun LazyListScope.searchHistoryPage() {
    variant(key = "default", title = Res.string.demo_search_history_variant_default) {
        SearchHistorySample(
            recordCount = HistoryRecords.size,
            expandable = true,
            maxLinesFolded = 2,
            longRecord = false,
        )
    }
    playground {
        var count by rememberSaveable { mutableFloatStateOf(HistoryRecords.size.toFloat()) }
        var expandable by rememberSaveable { mutableStateOf(true) }
        var foldedIndex by rememberSaveable { mutableIntStateOf(FoldedLines.indexOf(2)) }
        var longRecord by rememberSaveable { mutableStateOf(false) }
        val recordCount = count.roundToInt()

        PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
            SearchHistorySample(
                recordCount = recordCount,
                expandable = expandable,
                maxLinesFolded = FoldedLines[foldedIndex],
                longRecord = longRecord,
            )
        }
        PlaygroundControls {
            item {
                SliderPreference(
                    value = count,
                    onValueChange = { count = it },
                    title = stringResource(Res.string.common_count),
                    valueText = recordCount.toString(),
                    valueRange = 1f..HistoryRecords.size.toFloat(),
                    steps = HistoryRecords.size - 2,
                )
            }
            item {
                SwitchPreference(
                    checked = expandable,
                    onCheckedChange = { expandable = it },
                    title = stringResource(Res.string.demo_search_history_expandable),
                    summary = stringResource(Res.string.demo_search_history_expandable_summary),
                )
            }
            item {
                OverlayDropdownPreference(
                    items = FoldedLines.map { it.toString() },
                    selectedIndex = foldedIndex,
                    title = stringResource(Res.string.demo_search_history_folded_lines),
                    onSelectedIndexChange = { foldedIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = longRecord,
                    onCheckedChange = { longRecord = it },
                    title = stringResource(Res.string.common_long_text),
                )
            }
        }
    }
}

/**
 * A [SearchHistory] over the first [recordCount] sample records, with a row below it that shows
 * the last clicked record, or restores the records once they are cleared.
 *
 * The clicked record is kept as an index into [HistoryRecords], so switching the language keeps it.
 */
@Composable
private fun SearchHistorySample(
    recordCount: Int,
    expandable: Boolean,
    maxLinesFolded: Int,
    longRecord: Boolean,
) {
    var cleared by rememberSaveable { mutableStateOf(false) }
    var lastClicked by rememberSaveable { mutableIntStateOf(-1) }
    val names = HistoryRecords.take(recordCount).map { stringResource(it) }
    val longName = stringResource(Res.string.demo_search_history_long_record)
    val items = when {
        cleared -> emptyList()
        longRecord -> listOf(longName) + names
        else -> names
    }

    SearchHistory(
        title = stringResource(Res.string.demo_search_history_variant_default),
        items = items,
        onItemClick = { item -> lastClicked = if (item == longName) LONG_RECORD else names.indexOf(item) },
        onClear = {
            cleared = true
            lastClicked = -1
        },
        expandable = expandable,
        maxLinesFolded = maxLinesFolded,
        clearContentDescription = stringResource(Res.string.demo_search_history_clear_description),
    )
    if (cleared) {
        BasicComponent(
            title = stringResource(Res.string.demo_search_history_cleared),
            summary = stringResource(Res.string.demo_search_history_restore),
            onClick = { cleared = false },
        )
    } else {
        BasicComponent(
            title = stringResource(Res.string.demo_search_history_last_clicked),
            summary = when (lastClicked) {
                LONG_RECORD -> longName
                else -> names.getOrNull(lastClicked) ?: stringResource(Res.string.demo_search_history_none)
            },
        )
    }
}
