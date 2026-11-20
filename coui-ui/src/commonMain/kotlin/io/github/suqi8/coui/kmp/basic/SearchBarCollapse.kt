// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The geometry of a COUI search bar partway through its scroll-driven collapse.
 *
 * `COUISearchBar.setSearchViewAnimateHeightPercent` takes a single percent — 1 fully expanded, 0
 * fully collapsed — and derives everything else from it. The capsule shrinks while simultaneously
 * *widening* toward the screen edges and tinting to the divider color, so that by the time it is
 * one pixel tall it reads as the list's divider line rather than as a flattened search box.
 *
 * The three phases are worth keeping in mind when reading the numbers:
 * - `1.0..0.75` — content fades out while the capsule barely moves.
 * - `0.75..0.3` — content is already invisible; the capsule collapses and spreads outward.
 * - `0.3..0.0` — the capsule is a hairline; only width and color are still interpolating.
 *
 * @property capsuleHeight the height of the capsule background.
 * @property contentAlpha the opacity of the icons, hint and query text.
 * @property extraHorizontalWidth how far the capsule extends past each of its resting edges.
 * @property capsuleColor the capsule fill, morphing toward the divider color as it flattens.
 * @property translationY how far down the bar sits, keeping it centered as it loses height.
 */
data class SearchBarCollapseGeometry(
    val capsuleHeight: Dp,
    val contentAlpha: Float,
    val extraHorizontalWidth: Dp,
    val capsuleColor: Color,
    val translationY: Dp,
)

/**
 * Computes the collapse geometry for a given [percent].
 *
 * Mirrors `COUISearchBar.setSearchViewAnimateHeightPercent`, `clampSearchViewHeight` and
 * `clampMarginValue` of ColorOS 16 (COUI 17 keeps the method only as a deprecated no-op).
 *
 * @param percent 1 when fully expanded, 0 when fully collapsed. Values outside are clamped.
 * @param restingHeight the capsule's resting height, COUI `coui_search_bar_normal_background_height`.
 * @param barHeight the whole bar's resting height, COUI `coui_search_bar_height`.
 * @param horizontalPadding the bar's horizontal padding, which the capsule grows into on both sides
 *   (`mExtraHorizontalBackground = getInternalPaddingEnd() * (1 - clampMarginValue)`), so a fully
 *   collapsed capsule spans the bar's whole width. Defaults to the compact responsive padding,
 *   [SearchBarDefaults.HorizontalPaddingCompact]; an [InputField] inside a [SearchBar] uses the
 *   bar's own resolved padding instead.
 * @param normalColor the capsule's resting fill.
 * @param dividerColor the color the flattened capsule ends up as, COUI `coui_color_divider`.
 * @param minHeight the floor on the capsule height. COUI
 *   `coui_search_view_collapsed_min_height` is 1 physical pixel rather than 1dp, so callers that
 *   care should convert a pixel for the current density instead of taking this default.
 */
fun searchBarCollapseGeometry(
    percent: Float,
    restingHeight: Dp = SearchBarDefaults.InputFieldMinHeight,
    barHeight: Dp = SearchBarCollapseDefaults.BarHeight,
    horizontalPadding: Dp = SearchBarDefaults.HorizontalPaddingCompact,
    normalColor: Color,
    dividerColor: Color,
    minHeight: Dp = SearchBarCollapseDefaults.CollapsedMinHeight,
): SearchBarCollapseGeometry {
    val p = percent.coerceIn(0f, 1f)

    // clampSearchViewHeight: (p / 0.7) - 0.3/0.7, i.e. 0 at p = 0.3 and 1 at p = 1. Deliberately
    // allowed to go negative below 0.3 — the height floor absorbs it.
    val heightFraction = (p / HEIGHT_RAMP_SPAN) - HEIGHT_RAMP_OFFSET

    // clampMarginValue: the width and color interpolation both finish by p = 0.3.
    val marginFraction = (p / MARGIN_RAMP_SPAN).coerceIn(0f, 1f)

    return SearchBarCollapseGeometry(
        capsuleHeight = maxOf(minHeight, restingHeight * heightFraction),
        // Applied unclamped by COUI, which relies on the platform clamping at draw time. Clamp
        // here so callers can feed this straight into a graphicsLayer, which does not.
        contentAlpha = ((p - CONTENT_FADE_START) / (1f - CONTENT_FADE_START)).coerceIn(0f, 1f),
        extraHorizontalWidth = horizontalPadding * searchBarCollapseWidening(p),
        capsuleColor = lerp(dividerColor, normalColor, marginFraction),
        translationY = maxOf(0.dp, (barHeight / 2f) * (1f - p)),
    )
}

/**
 * The share of the bar's horizontal padding a collapsing capsule has grown into at [percent]:
 * `1 - clampMarginValue(percent)`, 0 down to 30% and 1 when fully collapsed.
 */
internal fun searchBarCollapseWidening(percent: Float): Float = 1f - (percent.coerceIn(0f, 1f) / MARGIN_RAMP_SPAN).coerceIn(0f, 1f)

/**
 * Converts a scroll offset into the collapse percent that [searchBarCollapseGeometry] takes.
 *
 * Mirrors `ListSearchBehavior.updateSearchView`: the bar is untouched until the list has scrolled
 * past [startOffset], then collapses linearly over [range]. Every COUI behavior uses this shape;
 * only the range differs — 58dp driving a list, 64dp on the homepage, 50dp in the app list.
 *
 * COUI additionally snaps the *list* to either end when scrolling settles mid-collapse, at the
 * halfway point. That belongs to the scroll container rather than here, since it is the list that
 * animates and the bar merely follows.
 *
 * @param scrollOffset how far the list has scrolled.
 * @param startOffset the slack before the collapse begins, COUI
 *   `app_list_search_view_margin_bottom`.
 * @param range the distance over which the collapse completes.
 */
fun searchBarCollapsePercent(
    scrollOffset: Dp,
    startOffset: Dp = SearchBarCollapseDefaults.ScrollStartOffset,
    range: Dp = SearchBarCollapseDefaults.ScrollRange,
): Float = 1f - ((scrollOffset - startOffset) / range).coerceIn(0f, 1f)

/** Default values for the COUI search bar's scroll-driven collapse. */
object SearchBarCollapseDefaults {
    /** COUI `coui_search_bar_height`, the whole bar rather than the capsule inside it. */
    val BarHeight = 52.dp

    /**
     * COUI `coui_search_view_collapsed_min_height` is 1 physical pixel, not 1dp. This default
     * stands in for it at typical densities; callers wanting the exact behavior should convert a
     * single pixel for the current density.
     */
    val CollapsedMinHeight = 1.dp

    /** COUI `app_list_search_view_margin_bottom`, the slack before the collapse starts. */
    val ScrollStartOffset = 10.dp

    /**
     * COUI `ListSearchBehavior.mScrollSearchTotalRange`: the 52dp bar plus a 16dp gap, less the
     * 10dp of slack already consumed by [ScrollStartOffset].
     */
    val ScrollRange = 58.dp
}

/** `clampSearchViewHeight` divides by 0.7, so the capsule finishes shrinking at 30%. */
private const val HEIGHT_RAMP_SPAN = 0.7f

/** The 0.3/0.7 term of `clampSearchViewHeight`. */
private const val HEIGHT_RAMP_OFFSET = 0.42857146f

/** `clampMarginValue` divides by 0.3: width and color finish interpolating there. */
private const val MARGIN_RAMP_SPAN = 0.3f

/** Content alpha is `(p - 0.75) * 4`, so it is fully transparent from 75% down. */
private const val CONTENT_FADE_START = 0.75f
