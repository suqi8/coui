// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import io.github.suqi8.coui.kmp.squircle.squircleClip
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlin.math.floor
import kotlin.math.max

/**
 * The COUI banner (`COUIBanner`): in its [BannerType.Pager] form a horizontally paging carousel of
 * rounded cards that scrolls itself and shows a [PageIndicator] over the bottom of the cards; in its
 * [BannerType.List] form a plain horizontal list of the same cards.
 *
 * - **Type**: COUI switches to the list form on windows at least 600dp wide
 *   (`screenWidthDp >= 600` in the constructor and `onConfigurationChanged`), which is what
 *   [BannerDefaults.type] returns. The list form (`COUIBannerRecyclerView`) has no indicator, no
 *   loop and no auto scroll: the cards sit [listItemSpacing] apart behind a [pageMargin] side
 *   padding, and a fling settles with a card at the start edge (`setHorizontalItemAlign`).
 * - **Infinite loop** (`couiInfiniteLoop`, on by default with two or more pages): a copy of the last
 *   page sits before the first and a copy of the first after the last; when the pager settles on a
 *   copy it jumps to the real page without animation (`COUIBannerOnPageChangeCallback`).
 * - **Auto loop** (`couiAutoLoop`): every [loopDurationMillis] + [scrollDurationMillis] the banner
 *   scrolls to the next page in [scrollDurationMillis] on a `PathInterpolator(0.2, 0, 0.1, 1)`.
 *   Touching the banner stops it; lifting the finger restarts the wait (`dispatchTouchEvent`). It
 *   needs the infinite loop (`setInfiniteLoop`). COUI drives this scroll as a `ViewPager2` fake drag
 *   of exactly one page (`COUIViewPager2SlideHelper`), so the new page is only selected once the
 *   drag lands on it (`ScrollEventAdapter.notifyEndFakeDrag`); [onPageChange] follows that.
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
 * @param type The form of the banner, by default the one COUI picks for the window width.
 * @param initialPage The page shown first (in the list form, the card scrolled to the start).
 * @param autoLoop Whether the banner scrolls by itself.
 * @param infiniteLoop Whether the last page is followed by the first.
 * @param showIndicator Whether the page indicator is shown.
 * @param loopDurationMillis How long a page stays before the next auto scroll (`couiLoopDuration`).
 * @param scrollDurationMillis The duration of an auto scroll (`setDuration`).
 * @param cornerRadius The corner radius of the cards.
 * @param pageMargin The gap between cards and the inset of the card from the sides (`couiPageMargin`);
 *   in the list form, the side padding of the list (`COUIBannerRecyclerView`, `dp2px(24)`).
 * @param startItemWidth How far the previous card peeks in (`couiLeftItemWidth`).
 * @param endItemWidth How far the next card peeks in (`couiRightItemWidth`).
 * @param listItemWidth The card width in the list form. COUI leaves it to the app's list item
 *   layout (`onCreateListView`); [Dp.Unspecified] fits two cards in the list's viewport.
 * @param listItemSpacing The gap between cards in the list form (`coui_item_decoration_8dp`).
 * @param scalePages Whether the pager form shrinks the cards beside the current one to
 *   [BannerDefaults.MinPageScale], growing them back as they scroll in, about their inner edge
 *   (`COUIScalePageTransformer`, set with `setPageTransformer`).
 * @param indicatorColors The page indicator colors.
 * @param onPageChange Called with the real page whenever the current page changes; for an auto
 *   scroll, when it lands on the next page.
 * @param pageContent The content of a page, given the real page index.
 */
@Composable
fun Banner(
    pageCount: Int,
    modifier: Modifier = Modifier,
    type: BannerType = BannerDefaults.type(),
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
    listItemWidth: Dp = Dp.Unspecified,
    listItemSpacing: Dp = BannerDefaults.ListItemSpacing,
    scalePages: Boolean = false,
    indicatorColors: PageIndicatorColors = BannerDefaults.indicatorColors(),
    onPageChange: ((Int) -> Unit)? = null,
    pageContent: @Composable (page: Int) -> Unit,
) {
    if (type == BannerType.List) {
        BannerList(
            pageCount = pageCount,
            modifier = modifier,
            initialPage = initialPage,
            cornerRadius = cornerRadius,
            sidePadding = pageMargin,
            itemWidth = listItemWidth,
            itemSpacing = listItemSpacing,
            pageContent = pageContent,
        )
        return
    }
    val loop = infiniteLoop && pageCount > 1
    val virtualCount = if (loop) pageCount + 2 else pageCount
    // setStartPosition: the first real page sits at 1 behind the copy of the last one.
    val pagerState = rememberPagerState(
        initialPage = if (loop) initialPage.coerceIn(0, pageCount - 1) + 1 else initialPage.coerceIn(0, max(0, pageCount - 1)),
    ) { virtualCount }
    var touching by remember { mutableStateOf(false) }
    // Whether the auto scroll (COUI's fake drag) is moving the pager.
    var autoScrolling by remember { mutableStateOf(false) }
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
    // onPageSelected: a fake drag dispatches no selection while it moves, only when it ends on
    // the next page (ScrollEventAdapter.notifyEndFakeDrag); a real drag taking over ends it.
    LaunchedEffect(pagerState, loop, pageCount) {
        snapshotFlow { if (autoScrolling) NO_PAGE else bannerRealPage(pagerState.currentPage, loop, pageCount) }
            .filter { it != NO_PAGE }
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
                    // COUIViewPager2SlideHelper.e: a fake drag of one page (the pager width less
                    // both paddings) over setDuration on setInterpolator. Its release velocity is
                    // ~0 at the flat end of the curve, so endFakeDrag snaps in place: the same
                    // path as a tween to the next page.
                    autoScrolling = true
                    try {
                        pagerState.animateScrollToPage(
                            page = pagerState.currentPage + 1,
                            animationSpec = tween(durationMillis = scrollDurationMillis, easing = BannerScrollEasing),
                        )
                    } finally {
                        autoScrolling = false
                    }
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
                    .then(
                        if (scalePages) {
                            Modifier.graphicsLayer {
                                // ViewPager2 transformPage position: 0 current, -1 left, 1 right.
                                val position = page - (pagerState.currentPage + pagerState.currentPageOffsetFraction)
                                val scale = bannerPageScale(position)
                                scaleX = scale
                                scaleY = scale
                                transformOrigin = TransformOrigin(bannerPagePivotX(position), 0.5f)
                            }
                        } else {
                            Modifier
                        },
                    )
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

/**
 * The [BannerType.List] form (`COUIBannerRecyclerView`): cards [itemWidth] wide (two per viewport
 * when unspecified) and as high as the banner.
 */
@Composable
private fun BannerList(
    pageCount: Int,
    initialPage: Int,
    cornerRadius: Dp,
    sidePadding: Dp,
    itemWidth: Dp,
    itemSpacing: Dp,
    modifier: Modifier = Modifier,
    pageContent: @Composable (page: Int) -> Unit,
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialPage.coerceIn(0, max(0, pageCount - 1)))
    BoxWithConstraints(modifier = modifier) {
        val width = if (itemWidth.isSpecified) {
            itemWidth
        } else {
            ((maxWidth - sidePadding * 2 - itemSpacing) / 2).coerceAtLeast(0.dp)
        }
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = sidePadding),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState, snapPosition = SnapPosition.Start),
        ) {
            items(count = pageCount) { page ->
                Box(
                    modifier = Modifier
                        .width(width)
                        .then(if (constraints.hasBoundedHeight) Modifier.fillParentMaxHeight() else Modifier)
                        .squircleClip(cornerRadius),
                ) {
                    pageContent(page)
                }
            }
        }
    }
}

/**
 * The forms of a [Banner] (`COUIBanner.setType`).
 */
enum class BannerType {
    /** The auto-scrolling, looping carousel with a page indicator (`TYPE_PAGER`, 0). */
    Pager,

    /** A horizontal list of cards with no indicator and no auto scroll (type 1, `COUIBannerRecyclerView`). */
    List,
}

/** Contains default values used by [Banner]. */
object BannerDefaults {
    /** From this window width the banner takes the [BannerType.List] form (`COUIBanner`, `screenWidthDp >= 600`). */
    val ListTypeBreakpoint = 600.dp

    /** The scale of the cards beside the current one with `scalePages` (`COUIScalePageTransformer`). */
    val MinPageScale = 0.85f

    /** The gap between cards in the list form (`COUIBannerRecyclerView`, `coui_item_decoration_8dp`). */
    val ListItemSpacing = 8.dp

    /** The form COUI picks for the current window: [BannerType.List] from [ListTypeBreakpoint], else [BannerType.Pager]. */
    @Composable
    fun type(): BannerType = if (LocalWindowInfo.current.containerDpSize.width >= ListTypeBreakpoint) BannerType.List else BannerType.Pager

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

/** No page reported while the auto scroll runs. */
private const val NO_PAGE = -1

/** `ViewPager2.setOffscreenPageLimit(2)`. */
private const val BANNER_OFFSCREEN_PAGE_LIMIT = 2

/** `COUIBannerUtil.getRealPosition`: the real page of a pager page when the loop copies are on. */
internal fun bannerRealPage(page: Int, loop: Boolean, pageCount: Int): Int = when {
    !loop -> page
    page == 0 -> pageCount - 1
    page == pageCount + 1 -> 0
    else -> page - 1
}

/** `COUIScalePageTransformer.transformPage`: the scale of a page at [position]. */
internal fun bannerPageScale(position: Float): Float {
    val min = BannerDefaults.MinPageScale
    return when {
        position < -1f || position > 1f -> min
        position < 0f -> (1f - min) * (position + 1f) + min
        else -> (1f - min) * (1f - position) + min
    }
}

/** `COUIScalePageTransformer.transformPage`: the horizontal pivot of a page at [position], as a fraction of its width. */
internal fun bannerPagePivotX(position: Float): Float = when {
    position < -1f -> 1f
    position > 1f -> 0f
    position < 0f -> -position * 0.5f + 0.5f
    else -> (1f - position) * 0.5f
}
