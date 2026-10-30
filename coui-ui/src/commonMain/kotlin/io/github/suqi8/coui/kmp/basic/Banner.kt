// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.squircle.squircleClip
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.floor
import kotlin.math.max

/**
 * The COUI banner (`COUIBanner`, type `TYPE_PAGER`): a horizontally paging carousel of rounded
 * cards that scrolls itself and shows a [PageIndicator] over the bottom of the cards.
 *
 * - **Infinite loop** (`couiInfiniteLoop`, on by default with two or more pages): a copy of the last
 *   page sits before the first and a copy of the first after the last; when the pager settles on a
 *   copy it jumps to the real page without animation (`COUIBannerOnPageChangeCallback`).
 * - **Auto loop** (`couiAutoLoop`): every [loopDurationMillis] + [scrollDurationMillis] the banner
 *   scrolls to the next page in [scrollDurationMillis] on a `PathInterpolator(0.2, 0, 0.1, 1)`.
 *   Touching the banner stops it; lifting the finger restarts the wait (`dispatchTouchEvent`). It
 *   needs the infinite loop (`setInfiniteLoop`).
 * - **Layout** (`COUIBanner.t`): each card is inset by [startItemWidth] + [pageMargin] and
 *   [endItemWidth] + [pageMargin], neighbours [pageMargin] apart, so the neighbours peek in by the
 *   item widths (`COUIMarginPageTransformer`).
 * - **Indicator**: centred 10dp above the bottom, in the dark-background colors of
 *   `coui_banner_content_layout`. Its dots are not clickable. While the pager wraps from a copy, the
 *   indicator holds the last dot and then jumps, as COUI's does.
 *
 * @param pageCount The number of pages.
 * @param modifier The modifier applied to the banner. Give it a height to size the cards; without
 *   one the pages are measured at their own height.
 * @param initialPage The page shown first.
 * @param autoLoop Whether the banner scrolls by itself.
 * @param infiniteLoop Whether the last page is followed by the first.
 * @param showIndicator Whether the page indicator is shown.
 * @param loopDurationMillis How long a page stays before the next auto scroll (`couiLoopDuration`).
 * @param scrollDurationMillis The duration of an auto scroll (`setDuration`).
 * @param cornerRadius The corner radius of the cards.
 * @param pageMargin The gap between cards and the inset of the card from the sides (`couiPageMargin`).
 * @param startItemWidth How far the previous card peeks in (`couiLeftItemWidth`).
 * @param endItemWidth How far the next card peeks in (`couiRightItemWidth`).
 * @param indicatorColors The page indicator colors.
 * @param onPageChange Called with the real page whenever the current page changes.
 * @param pageContent The content of a page, given the real page index.
 */
@Composable
fun Banner(
    pageCount: Int,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    autoLoop: Boolean = true,
    infiniteLoop: Boolean = true,
    showIndicator: Boolean = true,
    loopDurationMillis: Long = BannerDefaults.LoopDurationMillis,
    scrollDurationMillis: Int = BannerDefaults.ScrollDurationMillis,
    cornerRadius: Dp = BannerDefaults.CornerRadius,
    pageMargin: Dp = BannerDefaults.PageMargin,
    startItemWidth: Dp = 0.dp,
    endItemWidth: Dp = 0.dp,
    indicatorColors: PageIndicatorColors = BannerDefaults.indicatorColors(),
    onPageChange: ((Int) -> Unit)? = null,
    pageContent: @Composable (page: Int) -> Unit,
) {
    val loop = infiniteLoop && pageCount > 1
    val virtualCount = if (loop) pageCount + 2 else pageCount
    // setStartPosition: the first real page sits at 1 behind the copy of the last one.
    val pagerState = rememberPagerState(
        initialPage = if (loop) initialPage.coerceIn(0, pageCount - 1) + 1 else initialPage.coerceIn(0, max(0, pageCount - 1)),
    ) { virtualCount }
    var touching by remember { mutableStateOf(false) }
    val currentOnPageChange by rememberUpdatedState(onPageChange)
    // The auto scroll runs outside the loop task so a touch only cancels the pending wait.
    val scrollScope = rememberCoroutineScope()

    // onPageScrollStateChanged(IDLE): settled on a copy, jump to the page it copies.
    LaunchedEffect(pagerState, loop, pageCount) {
        if (!loop) return@LaunchedEffect
        snapshotFlow { pagerState.isScrollInProgress to pagerState.settledPage }.collect { (scrolling, page) ->
            if (scrolling) return@collect
            when (page) {
                0 -> pagerState.scrollToPage(pageCount)
                pageCount + 1 -> pagerState.scrollToPage(1)
            }
        }
    }
    LaunchedEffect(pagerState, loop, pageCount) {
        snapshotFlow { bannerRealPage(pagerState.currentPage, loop, pageCount) }
            .distinctUntilChanged()
            .collect { currentOnPageChange?.invoke(it) }
    }
    // startLoopTask: post the next scroll after loopDuration + duration. A touch removes the
    // pending task (dispatchTouchEvent ACTION_DOWN) but not a scroll already running: a tap
    // must not leave the pager stopped between two pages. A drag takes over the scroll anyway.
    if (autoLoop && loop && !touching) {
        LaunchedEffect(pagerState, pageCount, loopDurationMillis, scrollDurationMillis) {
            while (true) {
                delay(loopDurationMillis + scrollDurationMillis)
                scrollScope.launch {
                    if (pagerState.currentPage >= pageCount + 1) pagerState.scrollToPage(1)
                    pagerState.animateScrollToPage(
                        page = pagerState.currentPage + 1,
                        animationSpec = tween(durationMillis = scrollDurationMillis, easing = BannerScrollEasing),
                    )
                }.join()
            }
        }
    }

    Box(
        modifier = modifier.pointerInput(Unit) {
            // dispatchTouchEvent: observe every touch without taking it from the pager.
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                touching = true
                do {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                } while (event.changes.any { it.pressed })
                touching = false
            }
        },
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = startItemWidth + pageMargin, end = endItemWidth + pageMargin),
            pageSpacing = pageMargin,
            beyondViewportPageCount = BANNER_OFFSCREEN_PAGE_LIMIT,
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .squircleClip(cornerRadius),
            ) {
                pageContent(bannerRealPage(page, loop, pageCount))
            }
        }
        if (showIndicator && pageCount > 0) {
            PageIndicatorLayout(
                pageCount = pageCount,
                position = {
                    // onPageScrolled: the real page left of the view and the offset towards the next.
                    val value = pagerState.currentPage + pagerState.currentPageOffsetFraction
                    val index = floor(value).toInt()
                    bannerRealPage(index, loop, pageCount) + (value - index)
                },
                onDotClick = null,
                colors = indicatorColors,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = BannerDefaults.IndicatorBottomMargin),
            )
        }
    }
}

/** Contains default values used by [Banner]. */
object BannerDefaults {
    /** How long a page stays before the next auto scroll: `couiLoopDuration`, 5 seconds. */
    val LoopDurationMillis: Long = 5000L

    /** The auto scroll duration (`COUIBanner` `mDuration`, 950ms). */
    val ScrollDurationMillis: Int = 950

    /** The default card corner radius, the card list radius of [CardDefaults.CornerRadius]. */
    val CornerRadius = CardDefaults.CornerRadius

    /** The gap between cards and their side inset (`COUIBannerUtil`, `dp2px(24)`). */
    val PageMargin = 24.dp

    /** The indicator's distance from the bottom (`coui_banner_content_layout`, `layout_marginBottom`). */
    val IndicatorBottomMargin = 10.dp

    /**
     * The indicator colors of `coui_banner_content_layout`, fixed for the image behind them: dots
     * `coui_page_indicator_dot_color_in_dark_bg` (`#4DFFFFFF`), trace
     * `coui_color_label_primary_dark` (`#E6FFFFFF`).
     *
     * @param dotColor The color of the dots.
     * @param traceColor The color of the current dot and the trace.
     */
    @Composable
    fun indicatorColors(
        dotColor: Color = IndicatorDotColor,
        traceColor: Color = IndicatorTraceColor,
    ): PageIndicatorColors = PageIndicatorDefaults.colors(dotColor = dotColor, traceColor = traceColor)
}

private val IndicatorDotColor = Color(0x4DFFFFFF)
private val IndicatorTraceColor = Color(0xE6FFFFFF)

/** `COUIBanner` `mInterpolator`: `PathInterpolator(0.2, 0, 0.1, 1)`. */
private val BannerScrollEasing = CubicBezierEasing(0.2f, 0f, 0.1f, 1f)

/** `ViewPager2.setOffscreenPageLimit(2)`. */
private const val BANNER_OFFSCREEN_PAGE_LIMIT = 2

/** `COUIBannerUtil.getRealPosition`: the real page of a pager page when the loop copies are on. */
internal fun bannerRealPage(page: Int, loop: Boolean, pageCount: Int): Int = when {
    !loop -> page
    page == 0 -> pageCount - 1
    page == pageCount + 1 -> 0
    else -> page - 1
}
