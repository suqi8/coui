// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.TabRow
import io.github.suqi8.coui.kmp.basic.TabRowDefaults
import io.github.suqi8.coui.kmp.basic.TabRowItem
import io.github.suqi8.coui.kmp.basic.TabRowWithContour
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Image
import io.github.suqi8.coui.kmp.icon.extended.Music
import io.github.suqi8.coui.kmp.icon.extended.Photos
import io.github.suqi8.coui.kmp.icon.extended.Settings
import kotlinx.coroutines.launch

fun LazyListScope.tabRowSection() {
    item(key = "tabRow") {
        SmallTitle(text = "TabRow")
        // COUI sliding tab layout: spread across the row when the labels fit.
        val fixedTabs = remember { listOf("Day", "Week", "Month") }
        var fixedSelected by remember { mutableIntStateOf(0) }
        TabRow(
            tabs = fixedTabs,
            selectedTabIndex = fixedSelected,
            onTabSelected = { fixedSelected = it },
            modifier = Modifier.padding(bottom = 12.dp),
        )
        // Too many labels for the row: it scrolls with fading edges and follows the pager.
        val scrollingTabs = remember {
            listOf("Recommended", "Following", "Popular", "Featured", "Technology", "Travel", "Food")
        }
        val tabScope = rememberCoroutineScope()
        val tabPagerState = rememberPagerState(pageCount = { scrollingTabs.size })
        TabRow(
            tabs = scrollingTabs,
            selectedTabIndex = tabPagerState.targetPage,
            onTabSelected = {
                tabScope.launch {
                    tabPagerState.animateScrollToPage(it)
                }
            },
            pagerState = tabPagerState,
        )
        HorizontalPager(
            state = tabPagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 16.dp),
            key = { it },
            pageContent = { page ->
                Text(
                    text = "Content of ${scrollingTabs[page]}",
                    modifier = Modifier.fillMaxWidth(),
                )
            },
        )
        // Icon tabs (72dp), red dots and a disabled tab.
        val iconTabs = remember {
            listOf(
                TabRowItem(text = "Photos", icon = COUIIcons.Photos),
                TabRowItem(text = "Music", icon = COUIIcons.Music, badge = 0),
                TabRowItem(text = "Images", icon = COUIIcons.Image, badge = 12),
                TabRowItem(text = "Settings", icon = COUIIcons.Settings, enabled = false),
            )
        }
        var iconSelected by remember { mutableIntStateOf(0) }
        TabRow(
            items = iconTabs,
            selectedTabIndex = iconSelected,
            onTabSelected = { iconSelected = it },
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            insideMargin = PaddingValues(16.dp),
        ) {
            // COUI segment button, as used by the special access page in ColorOS Settings.
            val segmentTabs = remember { listOf("All", "Allowed", "Denied") }
            val scope = rememberCoroutineScope()
            val pagerState = rememberPagerState(pageCount = { segmentTabs.size })
            TabRowWithContour(
                tabs = segmentTabs,
                selectedTabIndex = pagerState.currentPage,
                onTabSelected = {
                    scope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                },
            )
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .padding(bottom = 16.dp),
                userScrollEnabled = true,
                key = { it },
                pageContent = { page ->
                    Text(
                        text = "Content of ${segmentTabs[page]}",
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                },
            )
            // SegmentButton.Tiny variant (32dp height, 2dp contour inset).
            val tinyTabs = remember { listOf("Photo", "Video", "Portrait", "Pano") }
            var tinySelected by remember { mutableIntStateOf(0) }
            TabRowWithContour(
                tabs = tinyTabs,
                selectedTabIndex = tinySelected,
                onTabSelected = { tinySelected = it },
                height = TabRowDefaults.TabRowWithContourTinyHeight,
                cornerRadius = TabRowDefaults.TabRowWithContourTinyCornerRadius,
                contourPadding = TabRowDefaults.TabRowWithContourTinyPadding,
            )
        }
    }
}
