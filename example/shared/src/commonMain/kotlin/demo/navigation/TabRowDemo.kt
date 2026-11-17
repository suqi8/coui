// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.TabRow
import io.github.suqi8.coui.kmp.basic.TabRowDefaults
import io.github.suqi8.coui.kmp.basic.TabRowItem
import io.github.suqi8.coui.kmp.basic.TabRowWithContour
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Contacts
import io.github.suqi8.coui.kmp.icon.extended.Home
import io.github.suqi8.coui.kmp.icon.extended.Image
import io.github.suqi8.coui.kmp.icon.extended.Music
import io.github.suqi8.coui.kmp.icon.extended.Photos
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.common_style
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_badges
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_all
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_allowed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_content
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_day
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_denied
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_featured
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_following
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_food
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_images
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_long_tab
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_month
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_pano
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_photo
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_popular
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_portrait
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_recommended
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_style_tiny
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_technology
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_travel
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_variant_contour
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_variant_fixed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_variant_icons
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_variant_scrolling
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_variant_tiny
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_video
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tab_row_week
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_contacts
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_files
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_home
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_music
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_photos
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_settings
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_videos
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** The demo page of `TabRow`, listed in its category and found by the search. */
internal val TabRowDemo = Demo(
    id = "tab_row",
    apiName = "TabRow",
    title = Res.string.demo_tab_row_title,
    category = DemoCategory.Navigation,
    page = DemoPage.Variants {
        playground {
            var styleIndex by rememberSaveable { mutableIntStateOf(0) }
            var countIndex by rememberSaveable { mutableIntStateOf(2) }
            var longText by rememberSaveable { mutableStateOf(false) }
            var showIcons by rememberSaveable { mutableStateOf(false) }
            var badges by rememberSaveable { mutableStateOf(false) }
            var enabled by rememberSaveable { mutableStateOf(true) }
            var selected by rememberSaveable { mutableIntStateOf(0) }
            val count = countIndex + TabRowDemoMinCount
            val labels = List(count) { index ->
                if (longText) {
                    stringResource(Res.string.demo_tab_row_long_tab, index + 1)
                } else {
                    stringResource(TabRowDemoLabels[index])
                }
            }
            val shownSelected = selected.coerceAtMost(count - 1)
            PlaygroundPreview(contentPadding = TabRowPreviewPadding) {
                when (styleIndex) {
                    0 -> TabRow(
                        items = List(count) { index ->
                            TabRowItem(
                                text = labels[index],
                                icon = if (showIcons) TabRowDemoIcons[index] else null,
                                badge = when {
                                    !badges -> null
                                    index == 1 -> 0
                                    index == 2 -> 12
                                    else -> null
                                },
                            )
                        },
                        selectedTabIndex = shownSelected,
                        onTabSelected = { selected = it },
                        enabled = enabled,
                    )

                    1 -> TabRowWithContour(
                        tabs = labels,
                        selectedTabIndex = shownSelected,
                        onTabSelected = { selected = it },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )

                    else -> TabRowWithContour(
                        tabs = labels,
                        selectedTabIndex = shownSelected,
                        onTabSelected = { selected = it },
                        modifier = Modifier.padding(horizontal = 16.dp),
                        height = TabRowDefaults.TabRowWithContourTinyHeight,
                        cornerRadius = TabRowDefaults.TabRowWithContourTinyCornerRadius,
                        contourPadding = TabRowDefaults.TabRowWithContourTinyPadding,
                    )
                }
            }
            PlaygroundControls {
                item {
                    OverlayDropdownPreference(
                        items = listOf("TabRow", "TabRowWithContour", stringResource(Res.string.demo_tab_row_style_tiny)),
                        selectedIndex = styleIndex,
                        title = stringResource(Res.string.common_style),
                        onSelectedIndexChange = { styleIndex = it },
                    )
                }
                item {
                    OverlayDropdownPreference(
                        items = TabRowDemoCountOptions,
                        selectedIndex = countIndex,
                        title = stringResource(Res.string.common_count),
                        onSelectedIndexChange = { countIndex = it },
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.common_long_text),
                        checked = longText,
                        onCheckedChange = { longText = it },
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.common_show_icon),
                        checked = showIcons,
                        onCheckedChange = { showIcons = it },
                        enabled = styleIndex == 0,
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.demo_navigation_badges),
                        checked = badges,
                        onCheckedChange = { badges = it },
                        enabled = styleIndex == 0,
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.common_enabled),
                        checked = enabled,
                        onCheckedChange = { enabled = it },
                        enabled = styleIndex == 0,
                    )
                }
            }
        }
        // COUI sliding tab layout: spread across the row when the labels fit.
        variant(key = "fixed", title = Res.string.demo_tab_row_variant_fixed, inCard = false) {
            var selected by rememberSaveable { mutableIntStateOf(0) }
            TabRow(
                tabs = listOf(
                    stringResource(Res.string.demo_tab_row_day),
                    stringResource(Res.string.demo_tab_row_week),
                    stringResource(Res.string.demo_tab_row_month),
                ),
                selectedTabIndex = selected,
                onTabSelected = { selected = it },
            )
        }
        // Too many labels for the row: it scrolls with fading edges and follows the pager.
        variant(key = "scrolling", title = Res.string.demo_tab_row_variant_scrolling, inCard = false) {
            val tabs = TabRowScrollingLabels.map { stringResource(it) }
            val scope = rememberCoroutineScope()
            val pagerState = rememberPagerState(pageCount = { tabs.size })
            TabRow(
                tabs = tabs,
                selectedTabIndex = pagerState.targetPage,
                onTabSelected = { scope.launch { pagerState.animateScrollToPage(it) } },
                pagerState = pagerState,
            )
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp),
                key = { it },
                pageContent = { page ->
                    Text(
                        text = stringResource(Res.string.demo_tab_row_content, tabs[page]),
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
            )
        }
        // Icon tabs (72dp), red dots and a disabled tab.
        variant(key = "icons", title = Res.string.demo_tab_row_variant_icons, inCard = false) {
            var selected by rememberSaveable { mutableIntStateOf(0) }
            val photos = stringResource(Res.string.sample_photos)
            val music = stringResource(Res.string.sample_music)
            val images = stringResource(Res.string.demo_tab_row_images)
            val settings = stringResource(Res.string.sample_settings)
            TabRow(
                items = listOf(
                    TabRowItem(text = photos, icon = COUIIcons.Photos),
                    TabRowItem(text = music, icon = COUIIcons.Music, badge = 0),
                    TabRowItem(text = images, icon = COUIIcons.Image, badge = 12),
                    TabRowItem(text = settings, icon = COUIIcons.Settings, enabled = false),
                ),
                selectedTabIndex = selected,
                onTabSelected = { selected = it },
            )
        }
        // COUI segment button, as used by the special access page in ColorOS Settings.
        variant(key = "contour", title = Res.string.demo_tab_row_variant_contour) {
            val tabs = listOf(
                stringResource(Res.string.demo_tab_row_all),
                stringResource(Res.string.demo_tab_row_allowed),
                stringResource(Res.string.demo_tab_row_denied),
            )
            val scope = rememberCoroutineScope()
            val pagerState = rememberPagerState(pageCount = { tabs.size })
            Column(modifier = Modifier.padding(16.dp)) {
                TabRowWithContour(
                    tabs = tabs,
                    selectedTabIndex = pagerState.currentPage,
                    onTabSelected = { scope.launch { pagerState.animateScrollToPage(it) } },
                )
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    key = { it },
                    pageContent = { page ->
                        Text(
                            text = stringResource(Res.string.demo_tab_row_content, tabs[page]),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                )
            }
        }
        // SegmentButton.Tiny variant (32dp height, 2dp contour inset).
        variant(key = "tiny", title = Res.string.demo_tab_row_variant_tiny) {
            var selected by rememberSaveable { mutableIntStateOf(0) }
            TabRowWithContour(
                tabs = listOf(
                    stringResource(Res.string.demo_tab_row_photo),
                    stringResource(Res.string.demo_tab_row_video),
                    stringResource(Res.string.demo_tab_row_portrait),
                    stringResource(Res.string.demo_tab_row_pano),
                ),
                selectedTabIndex = selected,
                onTabSelected = { selected = it },
                modifier = Modifier.padding(16.dp),
                height = TabRowDefaults.TabRowWithContourTinyHeight,
                cornerRadius = TabRowDefaults.TabRowWithContourTinyCornerRadius,
                contourPadding = TabRowDefaults.TabRowWithContourTinyPadding,
            )
        }
    },
    searchTerms = Res.string.demo_tab_row_search_terms,
)

private val TabRowPreviewPadding = PaddingValues(vertical = 16.dp)

private val TabRowDemoMinCount = 2
private val TabRowDemoCountOptions = listOf("2", "3", "4", "5", "6", "7")

private val TabRowDemoLabels: List<StringResource> = listOf(
    Res.string.demo_tab_row_all,
    Res.string.sample_photos,
    Res.string.sample_videos,
    Res.string.sample_music,
    Res.string.sample_files,
    Res.string.sample_contacts,
    Res.string.sample_home,
)

private val TabRowDemoIcons: List<ImageVector> = listOf(
    COUIIcons.Home,
    COUIIcons.Photos,
    COUIIcons.Image,
    COUIIcons.Music,
    COUIIcons.Settings,
    COUIIcons.Contacts,
    COUIIcons.Home,
)

private val TabRowScrollingLabels: List<StringResource> = listOf(
    Res.string.demo_tab_row_recommended,
    Res.string.demo_tab_row_following,
    Res.string.demo_tab_row_popular,
    Res.string.demo_tab_row_featured,
    Res.string.demo_tab_row_technology,
    Res.string.demo_tab_row_travel,
    Res.string.demo_tab_row_food,
)
