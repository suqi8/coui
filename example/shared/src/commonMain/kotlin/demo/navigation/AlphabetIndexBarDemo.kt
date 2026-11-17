// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.AlphabetIndexBar
import io.github.suqi8.coui.kmp.basic.AlphabetIndexBarDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_selected_value
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_alphabet_index_bar_collect_key
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_alphabet_index_bar_compressed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_alphabet_index_bar_full_height
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_alphabet_index_bar_haptic
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_alphabet_index_bar_height
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_alphabet_index_bar_list_row
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_alphabet_index_bar_partial_keys
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_alphabet_index_bar_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_alphabet_index_bar_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_alphabet_index_bar_variant_following
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_alphabet_index_bar_variant_heights
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

/** The demo page of `AlphabetIndexBar`, listed in its category and found by the search. */
internal val AlphabetIndexBarDemo = Demo(
    id = "alphabet_index_bar",
    apiName = "AlphabetIndexBar",
    title = Res.string.demo_alphabet_index_bar_title,
    category = DemoCategory.Navigation,
    page = DemoPage.Variants {
        playground {
            var barHeight by rememberSaveable { mutableFloatStateOf(420f) }
            var showCollectKey by rememberSaveable { mutableStateOf(true) }
            var partialKeys by rememberSaveable { mutableStateOf(true) }
            var haptic by rememberSaveable { mutableStateOf(true) }
            var selected by rememberSaveable { mutableStateOf("-") }
            PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
                // The bubble and its shadow reach past the bar's top and bottom rows; the vertical
                // padding keeps them inside the clipping card.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                        .height(barHeight.dp),
                ) {
                    IndexSelectionText(
                        text = stringResource(Res.string.common_selected_value, selected),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp),
                    )
                    AlphabetIndexBar(
                        onKeySelected = { selected = it },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .height(barHeight.dp),
                        availableKeys = if (partialKeys) IndexDemoAvailableKeys else null,
                        showCollectKey = showCollectKey,
                        hapticFeedbackEnabled = haptic,
                    )
                }
            }
            PlaygroundControls {
                item {
                    SliderPreference(
                        value = barHeight,
                        onValueChange = { barHeight = it },
                        title = stringResource(Res.string.demo_alphabet_index_bar_height),
                        valueText = "${barHeight.toInt()}dp",
                        valueRange = 200f..520f,
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.demo_alphabet_index_bar_collect_key),
                        checked = showCollectKey,
                        onCheckedChange = { showCollectKey = it },
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.demo_alphabet_index_bar_partial_keys),
                        checked = partialKeys,
                        onCheckedChange = { partialKeys = it },
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.demo_alphabet_index_bar_haptic),
                        checked = haptic,
                        onCheckedChange = { haptic = it },
                    )
                }
            }
        }
        variant(key = "heights", title = Res.string.demo_alphabet_index_bar_variant_heights) {
            Row(Modifier.fillMaxWidth()) {
                IndexBarSample(
                    label = stringResource(Res.string.demo_alphabet_index_bar_full_height),
                    barHeight = 480.dp,
                    availableKeys = IndexDemoAvailableKeys,
                    modifier = Modifier.weight(1f),
                )
                IndexBarSample(
                    label = stringResource(Res.string.demo_alphabet_index_bar_compressed),
                    barHeight = 260.dp,
                    availableKeys = null,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        variant(key = "following", title = Res.string.demo_alphabet_index_bar_variant_following) {
            FollowingListSample()
        }
    },
    searchTerms = Res.string.demo_alphabet_index_bar_search_terms,
)

private val IndexDemoAvailableKeys = setOf(
    AlphabetIndexBarDefaults.CollectKey,
    "A", "B", "C", "D", "F", "G", "J", "L", "M", "S", "T", "W", "Z", "#",
)

/** Each section of the following list holds this many rows. */
private val RowsPerSection = 3

private val FollowDemoSections = AlphabetIndexBarDefaults.Keys.filter { it in IndexDemoAvailableKeys }

/** A list whose first visible section drives the bar's highlight through `selectedKey`. */
@Composable
private fun FollowingListSample(modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val firstSection by remember {
        derivedStateOf { FollowDemoSections[listState.firstVisibleItemIndex / RowsPerSection] }
    }
    Box(
        modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
            .height(320.dp),
    ) {
        LazyColumn(state = listState, modifier = Modifier.fillMaxWidth()) {
            FollowDemoSections.forEach { section ->
                items(RowsPerSection) { index ->
                    Text(
                        text = stringResource(Res.string.demo_alphabet_index_bar_list_row, section, index + 1),
                        style = COUITheme.textStyles.bodyM,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                }
            }
        }
        AlphabetIndexBar(
            onKeySelected = { key ->
                val section = FollowDemoSections.indexOf(key)
                if (section >= 0) scope.launch { listState.scrollToItem(section * RowsPerSection) }
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(320.dp),
            selectedKey = firstSection,
            availableKeys = IndexDemoAvailableKeys,
            showCollectKey = false,
        )
    }
}

@Composable
private fun IndexBarSample(
    label: String,
    barHeight: Dp,
    availableKeys: Set<String>?,
    modifier: Modifier = Modifier,
) {
    var selected by rememberSaveable { mutableStateOf("-") }
    // The bubble and its shadow reach past the bar's top and bottom rows; the vertical padding
    // keeps them inside the clipping card.
    Box(
        modifier
            .padding(vertical = 32.dp)
            .height(barHeight),
    ) {
        IndexSelectionText(
            text = label + "\n" + stringResource(Res.string.common_selected_value, selected),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp),
        )
        AlphabetIndexBar(
            onKeySelected = { selected = it },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(barHeight),
            availableKeys = availableKeys,
        )
    }
}

@Composable
private fun IndexSelectionText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        color = COUITheme.colorScheme.labelSecondary,
        style = COUITheme.textStyles.bodyM,
    )
}
