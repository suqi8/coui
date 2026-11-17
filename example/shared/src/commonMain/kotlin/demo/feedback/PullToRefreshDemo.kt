// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.basic.PullToRefresh
import io.github.suqi8.coui.kmp.basic.PullToRefreshDefaults
import io.github.suqi8.coui.kmp.basic.cardListPositionOf
import io.github.suqi8.coui.kmp.basic.rememberPullToRefreshState
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.PreferenceCategory
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.preference.WindowDropdownPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pull_to_refresh_circle_size
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pull_to_refresh_duration
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pull_to_refresh_list_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pull_to_refresh_localized_texts
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pull_to_refresh_per_refresh
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pull_to_refresh_refresh_now
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pull_to_refresh_reset
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pull_to_refresh_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pull_to_refresh_text_done
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pull_to_refresh_text_pull
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pull_to_refresh_text_refreshing
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pull_to_refresh_text_release
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pull_to_refresh_title
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_item
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_option
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import ui.AppPageList
import ui.AppPageScaffold

/** The demo page of `PullToRefresh`, listed in its category and found by the search. */
internal val PullToRefreshDemo = Demo(
    id = "pull_to_refresh",
    apiName = "PullToRefresh",
    title = Res.string.demo_pull_to_refresh_title,
    category = DemoCategory.Feedback,
    page = DemoPage.FullScreen { onBack -> PullToRefreshPage(onBack = onBack) },
    searchTerms = Res.string.demo_pull_to_refresh_search_terms,
)

private val ListTopShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
private val ListBottomShape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
private val ListSingleShape = RoundedCornerShape(12.dp)

/** The indicator circle sizes to choose from; 19dp is the COUI default. */
private val CircleSizes = listOf(15.dp, PullToRefreshDefaults.circleSize, 24.dp)

private const val INITIAL_ITEM_COUNT = 6

/**
 * A page whose list is wrapped in [PullToRefresh], connected to the page top bar's scroll
 * behaviour. Each refresh waits for the chosen duration and appends rows.
 */
@Composable
private fun PullToRefreshPage(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isRefreshing by rememberSaveable { mutableStateOf(false) }
    var durationMillis by rememberSaveable { mutableFloatStateOf(1000f) }
    var perRefresh by rememberSaveable { mutableFloatStateOf(6f) }
    var itemCount by rememberSaveable { mutableIntStateOf(INITIAL_ITEM_COUNT) }
    var circleIndex by rememberSaveable { mutableIntStateOf(1) }
    var localizedTexts by rememberSaveable { mutableStateOf(true) }
    var dropdownSelected by rememberSaveable { mutableIntStateOf(0) }
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(durationMillis.toLong())
            itemCount += perRefresh.toInt()
            isRefreshing = false
        }
    }

    val refreshTexts = listOf(
        stringResource(Res.string.demo_pull_to_refresh_text_pull),
        stringResource(Res.string.demo_pull_to_refresh_text_release),
        stringResource(Res.string.demo_pull_to_refresh_text_refreshing),
        stringResource(Res.string.demo_pull_to_refresh_text_done),
    )
    val options = (1..4).map { stringResource(Res.string.sample_option, it) }

    AppPageScaffold(
        title = stringResource(Res.string.demo_pull_to_refresh_title),
        modifier = modifier,
        subtitle = "PullToRefresh",
        onBack = onBack,
    ) { scope ->
        PullToRefresh(
            isRefreshing = isRefreshing,
            onRefresh = { isRefreshing = true },
            pullToRefreshState = pullToRefreshState,
            contentPadding = scope.contentPadding,
            topAppBarScrollBehavior = scope.scrollBehavior,
            circleSize = CircleSizes[circleIndex],
            refreshTexts = if (localizedTexts) refreshTexts else PullToRefreshDefaults.refreshTexts,
        ) {
            AppPageList(scope = scope) {
                item(key = "controls") {
                    val durationTitle = stringResource(Res.string.demo_pull_to_refresh_duration)
                    val perRefreshTitle = stringResource(Res.string.demo_pull_to_refresh_per_refresh)
                    val circleTitle = stringResource(Res.string.demo_pull_to_refresh_circle_size)
                    val textsTitle = stringResource(Res.string.demo_pull_to_refresh_localized_texts)
                    val refreshNowTitle = stringResource(Res.string.demo_pull_to_refresh_refresh_now)
                    val resetTitle = stringResource(Res.string.demo_pull_to_refresh_reset)
                    PlaygroundControls {
                        item {
                            SliderPreference(
                                value = durationMillis,
                                onValueChange = { durationMillis = it },
                                title = durationTitle,
                                valueText = "${durationMillis.toInt()} ms",
                                valueRange = 200f..5000f,
                                steps = 23,
                            )
                        }
                        item {
                            SliderPreference(
                                value = perRefresh,
                                onValueChange = { perRefresh = it },
                                title = perRefreshTitle,
                                valueText = perRefresh.toInt().toString(),
                                valueRange = 1f..12f,
                                steps = 10,
                            )
                        }
                        item {
                            OverlayDropdownPreference(
                                items = CircleSizes.map { "${it.value.toInt()}dp" },
                                selectedIndex = circleIndex,
                                title = circleTitle,
                                summary = "circleSize",
                                onSelectedIndexChange = { circleIndex = it },
                            )
                        }
                        item {
                            SwitchPreference(
                                checked = localizedTexts,
                                onCheckedChange = { localizedTexts = it },
                                title = textsTitle,
                                summary = "refreshTexts",
                            )
                        }
                        item {
                            ArrowPreference(
                                title = refreshNowTitle,
                                summary = "isRefreshing = true",
                                onClick = { isRefreshing = true },
                                enabled = !isRefreshing,
                            )
                        }
                        item {
                            ArrowPreference(
                                title = resetTitle,
                                onClick = { itemCount = INITIAL_ITEM_COUNT },
                            )
                        }
                    }
                }
                item(key = "list_title") {
                    PreferenceCategory(title = stringResource(Res.string.demo_pull_to_refresh_list_title, itemCount))
                }
                items(count = itemCount, key = { "row_$it" }) { index ->
                    RefreshListRow(
                        index = index,
                        count = itemCount,
                        options = options,
                        selected = dropdownSelected,
                        onSelectedChange = { dropdownSelected = it },
                    )
                }
            }
        }
    }
}

/**
 * One row of the refreshed list. The card spans lazy items, so a `CardListGroup` cannot number
 * these rows; the shape and the position are resolved from the index instead.
 */
@Composable
private fun RefreshListRow(
    index: Int,
    count: Int,
    options: List<String>,
    selected: Int,
    onSelectedChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isFirst = index == 0
    val isLast = index == count - 1
    val shape = when {
        isFirst && isLast -> ListSingleShape
        isFirst -> ListTopShape
        isLast -> ListBottomShape
        else -> RectangleShape
    }
    val position = cardListPositionOf(index, count)
    val title = stringResource(Res.string.sample_item, index + 1)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(shape)
            .background(COUITheme.colorScheme.cardBackground),
    ) {
        // Alternate the two popup hosts, to try menus opened from a refreshing list.
        if (index % 2 == 0) {
            OverlayDropdownPreference(
                items = options,
                selectedIndex = selected,
                title = title,
                summary = "OverlayDropdownPreference",
                cardListPosition = position,
                onSelectedIndexChange = onSelectedChange,
            )
        } else {
            WindowDropdownPreference(
                items = options,
                selectedIndex = selected,
                title = title,
                summary = "WindowDropdownPreference",
                cardListPosition = position,
                onSelectedIndexChange = onSelectedChange,
            )
        }
        if (!isLast) {
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        }
    }
}
