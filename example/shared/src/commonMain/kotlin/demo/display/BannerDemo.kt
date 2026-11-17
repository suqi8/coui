// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.display

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import io.github.suqi8.coui.kmp.basic.Banner
import io.github.suqi8.coui.kmp.basic.BannerDefaults
import io.github.suqi8.coui.kmp.basic.BannerType
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_mode
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_banner_auto_loop
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_banner_infinite_loop
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_banner_page_fonts
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_banner_page_icons
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_banner_page_picks
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_banner_page_ringtones
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_banner_page_themes
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_banner_page_wallpapers
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_banner_peek
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_banner_scale_pages
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_banner_show_indicator
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_banner_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_banner_type_auto
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_banner_variant_list
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_banner_variant_loop
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_banner_variant_peek
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal val BannerDemo = Demo(
    id = "banner",
    apiName = "Banner",
    title = Res.string.demo_banner_title,
    category = DemoCategory.Display,
    page = DemoPage.Variants { bannerPage() },
    keywords = listOf("轮播", "carousel", "pager"),
)

/** The page titles of the sample banner. */
private val BannerPageTitles: List<StringResource> = listOf(
    Res.string.demo_banner_page_themes,
    Res.string.demo_banner_page_wallpapers,
    Res.string.demo_banner_page_picks,
    Res.string.demo_banner_page_fonts,
    Res.string.demo_banner_page_ringtones,
    Res.string.demo_banner_page_icons,
)

/** The number of pages of the fixed variants. */
private const val VARIANT_PAGE_COUNT = 4

private fun LazyListScope.bannerPage() {
    playground {
        var typeIndex by rememberSaveable { mutableIntStateOf(0) }
        var pageCount by rememberSaveable { mutableFloatStateOf(VARIANT_PAGE_COUNT.toFloat()) }
        var autoLoop by rememberSaveable { mutableStateOf(true) }
        var infiniteLoop by rememberSaveable { mutableStateOf(true) }
        var showIndicator by rememberSaveable { mutableStateOf(true) }
        var peek by rememberSaveable { mutableStateOf(false) }
        var scalePages by rememberSaveable { mutableStateOf(false) }

        val count = pageCount.toInt()
        // Banner page artwork: theme accent colours stand in for the banner images.
        PlaygroundPreview(inCard = false, contentPadding = PaddingValues(0.dp)) {
            key(typeIndex, count, infiniteLoop) {
                Banner(
                    pageCount = count,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (typeIndex == 2) 120.dp else 160.dp),
                    type = when (typeIndex) {
                        1 -> BannerType.Pager
                        2 -> BannerType.List
                        else -> BannerDefaults.type()
                    },
                    autoLoop = autoLoop,
                    infiniteLoop = infiniteLoop,
                    showIndicator = showIndicator,
                    pageMargin = if (peek) 8.dp else BannerDefaults.PageMargin,
                    startItemWidth = if (peek) 16.dp else 0.dp,
                    endItemWidth = if (peek) 16.dp else 0.dp,
                    scalePages = scalePages,
                ) { page ->
                    BannerDemoPage(page = page)
                }
            }
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_mode),
                    summary = "type",
                    items = listOf(stringResource(Res.string.demo_banner_type_auto), "Pager", "List"),
                    selectedIndex = typeIndex,
                    onSelectedIndexChange = { typeIndex = it },
                )
            }
            item {
                SliderPreference(
                    value = pageCount,
                    onValueChange = { pageCount = it },
                    title = stringResource(Res.string.common_count),
                    valueText = count.toString(),
                    valueRange = 2f..BannerPageTitles.size.toFloat(),
                    steps = BannerPageTitles.size - 3,
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_banner_auto_loop),
                    summary = "autoLoop",
                    checked = autoLoop,
                    onCheckedChange = { autoLoop = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_banner_infinite_loop),
                    summary = "infiniteLoop",
                    checked = infiniteLoop,
                    onCheckedChange = { infiniteLoop = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_banner_show_indicator),
                    summary = "showIndicator",
                    checked = showIndicator,
                    onCheckedChange = { showIndicator = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_banner_peek),
                    summary = "startItemWidth / endItemWidth",
                    checked = peek,
                    onCheckedChange = { peek = it },
                    enabled = typeIndex != 2,
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_banner_scale_pages),
                    summary = "scalePages",
                    checked = scalePages,
                    onCheckedChange = { scalePages = it },
                    enabled = typeIndex != 2,
                )
            }
        }
    }
    variant(key = "loop", title = Res.string.demo_banner_variant_loop, inCard = false) {
        Banner(
            pageCount = VARIANT_PAGE_COUNT,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .height(160.dp),
        ) { page ->
            BannerDemoPage(page = page)
        }
    }
    // Neighbouring cards peeking in and scaled down, without the auto loop.
    variant(key = "peek", title = Res.string.demo_banner_variant_peek, inCard = false) {
        Banner(
            pageCount = VARIANT_PAGE_COUNT,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .height(140.dp),
            type = BannerType.Pager,
            autoLoop = false,
            pageMargin = 8.dp,
            startItemWidth = 16.dp,
            endItemWidth = 16.dp,
            scalePages = true,
        ) { page ->
            BannerDemoPage(page = page)
        }
    }
    // The list form COUI uses from 600dp, forced here on any width.
    variant(key = "list", title = Res.string.demo_banner_variant_list, inCard = false) {
        Banner(
            pageCount = VARIANT_PAGE_COUNT,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .height(120.dp),
            type = BannerType.List,
        ) { page ->
            BannerDemoPage(page = page)
        }
    }
}

/** One sample banner page: a theme accent colour with the page title on it. */
@Composable
private fun BannerDemoPage(page: Int) {
    val colors = COUITheme.colorScheme
    val color = when (page % BannerPageTitles.size) {
        0 -> colors.blue
        1 -> colors.orange
        2 -> colors.green
        3 -> colors.violet
        4 -> colors.cyan
        else -> colors.red
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = stringResource(BannerPageTitles[page % BannerPageTitles.size]), color = colors.labelOnColor)
    }
}
