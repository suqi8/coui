// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.VectorizedAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.lerp
import io.github.suqi8.coui.kmp.basic.TopAppBarState.Companion.Saver
import io.github.suqi8.coui.kmp.effect.TouchMotion
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.COUILayoutGrid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * A [TopAppBar] with COUI style that can collapse and expand based on the
 * scroll position of the content below it.
 *
 * The [TopAppBar] can be configured with a title, a navigation icon, and action icons.
 * The large title will collapse when the content is scrolled up and expand when
 * the content is scrolled down. With a [COUIScrollBehavior] the bar trails the scroll with a
 * spring and snaps to expanded or collapsed when the scroll ends, and while it is not fully
 * collapsed it can also be dragged by the bar itself, like the COUI 17 collapsable app bar.
 * A new [title] or [largeTitle] cross-fades in.
 *
 * @param title The title of the [TopAppBar].
 * @param modifier The modifier to be applied to the  [TopAppBar].
 * @param color The background color of the [TopAppBar].
 * @param titleColor The color of the collapsed small title text.
 * @param largeTitle The large title of the [TopAppBar].
 * @param largeTitleColor The color of the expanded large title text.
 * @param subtitle The subtitle displayed below the title bar area.
 * @param subtitleColor The color of the subtitle text.
 * @param dividerColor The color of the hairline divider revealed at the bottom edge while collapsing.
 * @param navigationIcon The [Composable] content that represents the navigation icon.
 * @param actions The [Composable] content that represents the action icons.
 * @param scrollBehavior The [ScrollBehavior] that controls the behavior of the [TopAppBar].
 * @param defaultWindowInsetsPadding Whether to apply default window insets padding to the [TopAppBar].
 * @param showDivider Whether to draw the bottom hairline divider that fades in as the bar collapses.
 *   The divider is always suppressed while a [glow] is set (COUI 17
 *   `AppBarBlurHelper.syncAppBarDividerWithMaterialBlur` -> `setBlurDividerSuppressed`).
 * @param hideSubtitleOnCollapse Whether the subtitle fades out while the bar collapses
 *   (COUICollapsableAppBarLayout `subtitleHideEnable`). When false, the subtitle stays fully
 *   opaque and slides into the collapsed bar, and the collapsed title shrinks to 18dp instead of
 *   24dp (`coui_appbar_title_collapsed_with_subtitle_text_size`).
 * @param titlePadding The horizontal padding of the [TopAppBar]'s title & large title.
 *   [Dp.Unspecified] follows the bar width like COUI (see [TopAppBarDefaults.horizontalPadding]).
 * @param navigationIconPadding The start padding of the navigation icon. [Dp.Unspecified] follows
 *   the bar width like COUI (see [TopAppBarDefaults.horizontalPadding]).
 * @param actionIconPadding The end padding of the action icons. [Dp.Unspecified] follows the bar
 *   width like COUI (see [TopAppBarDefaults.horizontalPadding]).
 * @param largeTitleBottomMargin When specified, anchors the large title to the bottom of the
 *   expanded bar by this margin (COUI `expandedTitleGravity="start|bottom"` with
 *   `expandedTitleMarginBottom`, as on the ColorOS 17 settings home) instead of placing it
 *   [TopAppBarDefaults.LargeTitleTopPadding] below the top.
 * @param fadeLargeTitleOnCollapse Whether the large title fades and scales away as the bar
 *   collapses instead of morphing into the pinned collapsed title. Off by default: the standard
 *   COUI collapsing toolbar morphs the title. The ColorOS 17 settings home
 *   (`SettingsCollapsingToolbarLayout`) is the exception: it drives `alpha 1 -> 0` and
 *   `scale 1 -> 0.7` linearly over the collapse and pins no title.
 * @param glow The bar's backing layer, drawn behind everything else in the bar. Pass the COUI 17
 *   condensed-light backdrop blur here (e.g. `TopAppBarGlow`) together with a transparent [color]
 *   to get the frosted top bar: the bar itself draws no background, and the scrolled content is
 *   blurred beneath it. Pass `null` for a solid bar (and whenever the blur is off), which also
 *   lets [showDivider] take effect.
 * @param searchState Links the bar to a [TopAppBarSearchBar] in [bottomContent], like the COUI 17
 *   settings home: as the search enters, the title shrinks, fades and moves up, the toolbar scrolls
 *   away and the bottom content rises to just below the status bar; see
 *   [rememberTopAppBarSearchState].
 *   `null` keeps the bar as it is.
 * @param bottomContent The [Composable] content displayed below the title bar area.
 */
@Composable
fun TopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = COUITheme.colorScheme.backgroundWithCard,
    titleColor: Color = COUITheme.colorScheme.labelPrimary,
    largeTitle: String = title,
    largeTitleColor: Color = COUITheme.colorScheme.labelPrimary,
    subtitle: String = "",
    subtitleColor: Color = COUITheme.colorScheme.labelSecondaryVariant,
    dividerColor: Color = COUITheme.colorScheme.divider,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: ScrollBehavior? = null,
    defaultWindowInsetsPadding: Boolean = true,
    showDivider: Boolean = true,
    hideSubtitleOnCollapse: Boolean = true,
    titlePadding: Dp = Dp.Unspecified,
    navigationIconPadding: Dp = Dp.Unspecified,
    actionIconPadding: Dp = Dp.Unspecified,
    expandedHeight: Dp = TopAppBarDefaults.ExpandedHeight,
    largeTitleFontSize: TextUnit = TopAppBarDefaults.largeTitleFontSize(),
    largeTitleBottomMargin: Dp = Dp.Unspecified,
    fadeLargeTitleOnCollapse: Boolean = false,
    glow: (@Composable BoxScope.() -> Unit)? = null,
    searchState: TopAppBarSearchState? = null,
    bottomContent: @Composable () -> Unit = {},
) {
    // Wrap the given actions in a Row. The actions and the navigation icon are COUI toolbar items,
    // so tooltips anchored to them use the toolbar spacing.
    val toolbarContainer = rememberTooltipAnchorContainer(TooltipAnchorType.Toolbar)
    val actionsRow =
        @Composable {
            CompositionLocalProvider(LocalTooltipAnchorContainer provides toolbarContainer) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(TopAppBarDefaults.ActionItemSpacing, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions,
                )
            }
        }
    val toolbarNavigationIcon =
        @Composable {
            CompositionLocalProvider(LocalTooltipAnchorContainer provides toolbarContainer, content = navigationIcon)
        }

    // Compose a Surface with a TopAppBarLayout content.
    // The surface's background color is animated as specified above.
    // The height of the app bar is determined by subtracting the bar's height offset from the
    // app bar's defined constant height value (i.e. the ContainerHeight token).
    TopAppBarLayout(
        title = title,
        color = color,
        titleColor = titleColor,
        largeTitle = largeTitle,
        largeTitleColor = largeTitleColor,
        subtitle = subtitle,
        subtitleColor = subtitleColor,
        dividerColor = dividerColor,
        navigationIcon = toolbarNavigationIcon,
        actions = actionsRow,
        titlePadding = titlePadding,
        navigationIconPadding = navigationIconPadding,
        actionIconPadding = actionIconPadding,
        scrollBehavior = scrollBehavior,
        modifier = modifier,
        defaultWindowInsetsPadding = defaultWindowInsetsPadding,
        // AppBarBlurHelper.syncAppBarDividerWithMaterialBlur: the blurred bar suppresses its divider.
        showDivider = showDivider && glow == null,
        hideSubtitleOnCollapse = hideSubtitleOnCollapse,
        expandedHeight = expandedHeight,
        largeTitleFontSize = largeTitleFontSize,
        largeTitleBottomMargin = largeTitleBottomMargin,
        fadeLargeTitleOnCollapse = fadeLargeTitleOnCollapse,
        glow = glow,
        searchState = searchState,
        bottomContent = bottomContent,
    )
}

/**
 * A search bar slot for [TopAppBar.bottomContent], matching the COUI 17 settings home page.
 *
 * It renders a capsule search field inside a fixed-height row, with the COUI page-side
 * horizontal padding, so the bar collapses away with the rest of the expanded title area.
 *
 * A search the user types into should search in place like the settings home, where the capsule
 * itself enters the edit state with a cancel button and rises to the top: use [TopAppBarSearchBar]
 * as [TopAppBar]'s `bottomContent` instead (it brings its own 52dp row and page padding), together
 * with a [TopAppBarSearchState]. Keep this slot for a capsule that only opens something else.
 *
 * @param inputField the search input field (e.g. [SearchBar]'s `InputField`).
 * @param modifier the [Modifier] to be applied to the slot.
 * @param height the height of the slot row. COUI `coui_search_view_height` (52dp).
 * @param insideMargin the horizontal page-side padding of the capsule.
 */
@Composable
fun TopAppBarSearchSlot(
    inputField: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = TopAppBarDefaults.SearchSlotHeight,
    insideMargin: PaddingValues = TopAppBarDefaults.SearchSlotInsideMargin,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(insideMargin),
        contentAlignment = Alignment.CenterStart,
    ) {
        inputField()
    }
}

/**
 * A [SmallTopAppBar] with COUI style.
 *
 * The [SmallTopAppBar] is the standalone, non-collapsing COUI toolbar (`COUIToolbar` with
 * `supportTitleTextAppearance="@style/textAppearanceSecondTitle"`, as the ColorOS 17 settings
 * pages use it). It can be configured with a title, a navigation icon, and action icons. A new
 * [title] cross-fades in.
 *
 * @param title The title of the [SmallTopAppBar].
 * @param modifier The modifier to be applied to the  [SmallTopAppBar].
 * @param color The background color of the [SmallTopAppBar].
 * @param titleColor The color of the title text.
 * @param subtitle The subtitle displayed below the title bar area.
 * @param subtitleColor The color of the subtitle text.
 * @param dividerColor The color of the hairline divider revealed at the bottom edge on scroll.
 * @param navigationIcon The [Composable] content that represents the navigation icon.
 * @param actions The [Composable] content that represents the action icons.
 * @param scrollBehavior The [ScrollBehavior] that controls the behavior of the [SmallTopAppBar].
 * @param defaultWindowInsetsPadding Whether to apply default window insets padding to the [SmallTopAppBar].
 * @param showDivider Whether to draw the bottom hairline divider that fades in as content scrolls
 *   beneath the bar (requires [scrollBehavior] to track the content offset). The divider is always
 *   suppressed while a [glow] is set (COUI 17 `AppBarBlurHelper.syncAppBarDividerWithMaterialBlur`).
 * @param centerTitle Whether to center the title and subtitle on the bar
 *   (`COUIToolbar.setIsTitleCenterStyle`): centered on the whole bar when they fit between the
 *   navigation icon and the actions symmetrically, otherwise centered in the space left between
 *   them, and start-aligned and ellipsized when even that is too narrow.
 * @param titlePadding The horizontal padding of the [SmallTopAppBar]'s title. [Dp.Unspecified]
 *   follows the bar width like COUI (see [TopAppBarDefaults.horizontalPadding]).
 * @param navigationIconPadding The start padding of the navigation icon. [Dp.Unspecified] follows
 *   the bar width like COUI (see [TopAppBarDefaults.horizontalPadding]).
 * @param actionIconPadding The end padding of the action icons. [Dp.Unspecified] follows the bar
 *   width like COUI (see [TopAppBarDefaults.horizontalPadding]).
 * @param glow The bar's backing layer, drawn behind everything else in the bar (e.g.
 *   `TopAppBarGlow` with a transparent [color]). Pass `null` for a solid bar.
 * @param bottomContent The [Composable] content displayed below the title bar area.
 */
@Composable
fun SmallTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = COUITheme.colorScheme.backgroundWithCard,
    titleColor: Color = COUITheme.colorScheme.labelPrimary,
    subtitle: String = "",
    subtitleColor: Color = COUITheme.colorScheme.labelSecondaryVariant,
    dividerColor: Color = COUITheme.colorScheme.divider,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: ScrollBehavior? = null,
    defaultWindowInsetsPadding: Boolean = true,
    showDivider: Boolean = true,
    centerTitle: Boolean = false,
    titlePadding: Dp = Dp.Unspecified,
    navigationIconPadding: Dp = Dp.Unspecified,
    actionIconPadding: Dp = Dp.Unspecified,
    glow: (@Composable BoxScope.() -> Unit)? = null,
    bottomContent: @Composable () -> Unit = {},
) {
    SideEffect {
        // Pin the bar: clamp scroll range to 0 so nested content still scrolls even when this bar
        // shares a ScrollBehavior with a collapsible one. Guard against redundant writes.
        scrollBehavior?.state?.let { state ->
            if (state.heightOffsetLimit != 0f) state.heightOffsetLimit = 0f
        }
    }

    // Wrap the given actions in a Row. The actions and the navigation icon are COUI toolbar items,
    // so tooltips anchored to them use the toolbar spacing.
    val toolbarContainer = rememberTooltipAnchorContainer(TooltipAnchorType.Toolbar)
    val actionsRow =
        @Composable {
            CompositionLocalProvider(LocalTooltipAnchorContainer provides toolbarContainer) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(TopAppBarDefaults.ActionItemSpacing, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions,
                )
            }
        }
    val toolbarNavigationIcon =
        @Composable {
            CompositionLocalProvider(LocalTooltipAnchorContainer provides toolbarContainer, content = navigationIcon)
        }

    // Compose a Surface with a SmallTopAppBarLayout content.
    // The surface's background color is animated as specified above.
    // The height of the app bar is determined by subtracting the bar's height offset from the
    // app bar's defined constant height value (i.e. the ContainerHeight token).
    SmallTopAppBarLayout(
        title = title,
        color = color,
        titleColor = titleColor,
        subtitle = subtitle,
        subtitleColor = subtitleColor,
        dividerColor = dividerColor,
        navigationIcon = toolbarNavigationIcon,
        actions = actionsRow,
        titlePadding = titlePadding,
        navigationIconPadding = navigationIconPadding,
        actionIconPadding = actionIconPadding,
        scrollBehavior = scrollBehavior,
        modifier = modifier,
        defaultWindowInsetsPadding = defaultWindowInsetsPadding,
        // AppBarBlurHelper.syncAppBarDividerWithMaterialBlur: the blurred bar suppresses its divider.
        showDivider = showDivider && glow == null,
        centerTitle = centerTitle,
        glow = glow,
        bottomContent = bottomContent,
    )
}

/**
 * Returns a [ScrollBehavior] that adjusts its properties to affect the colors and
 * height of the top app bar.
 *
 * A top app bar that is set up with this [ScrollBehavior] will immediately collapse
 * when the nested content is pulled up, and will expand back the collapsed area when the
 * content is pulled all the way down. A bar left between the two when a scroll or fling ends
 * springs to the nearer one, and a [TopAppBar] that is not fully collapsed can be dragged by the
 * bar itself while the content is at its top.
 *
 * @param state the state object to be used to control or observe the top app bar's scroll
 *   state. See [rememberTopAppBarState] for a state that is remembered across compositions.
 * @param canScroll a callback used to determine whether scroll events are to be handled by this
 *   [ExitUntilCollapsedScrollBehavior]
 * @param snapAnimationSpec an optional [AnimationSpec] that defines how the top app bar snaps
 *   to either fully collapsed or fully extended state when a fling or a drag scrolled it into
 *   an intermediate position. `null` leaves the bar where the scroll stopped.
 * @param flingAnimationSpec an optional [DecayAnimationSpec] that defines how to fling the top
 *   app bar with the velocity a fling of the content leaves over. Only used without
 *   [followScroll]: COUI 17 never flings the bar itself and only snaps it.
 * @param followScroll whether the bar trails each scroll step with a spring, like COUI 17
 *   (`COUICollapsableAppBarLayout.ScrollBehavior` with `mUseSpring`): every step moves
 *   [TopAppBarState.heightOffset] at once, and the bar is drawn at
 *   [TopAppBarState.displayedHeightOffset], which follows it with a critically damped spring
 *   (response 0.2s while collapsing, 0.1s while expanding, 0.15s while the bar itself is
 *   dragged); the content padded below the bar follows with it. `false` draws the bar at the
 *   scroll position directly.
 */
@Suppress("ComposableNaming")
@Composable
fun COUIScrollBehavior(
    state: TopAppBarState = rememberTopAppBarState(),
    canScroll: () -> Boolean = { true },
    snapAnimationSpec: AnimationSpec<Float>? = TopAppBarDefaults.SnapAnimationSpec,
    flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay(),
    followScroll: Boolean = true,
): ScrollBehavior {
    val followScope = rememberCoroutineScope()
    return remember(state, canScroll, snapAnimationSpec, flingAnimationSpec, followScroll, followScope) {
        ExitUntilCollapsedScrollBehavior(
            state = state,
            snapAnimationSpec = snapAnimationSpec,
            flingAnimationSpec = flingAnimationSpec,
            canScroll = canScroll,
            followScope = if (followScroll) followScope else null,
        )
    }
}

/**
 * Creates a [TopAppBarState] that is remembered across compositions.
 *
 * @param initialHeightOffsetLimit the initial value for [TopAppBarState.heightOffsetLimit], which
 *   represents the pixel limit that a top app bar is allowed to collapse when the scrollable
 *   content is scrolled
 * @param initialHeightOffset the initial value for [TopAppBarState.heightOffset]. The initial
 *   offset height offset should be between zero and [initialHeightOffsetLimit].
 * @param initialContentOffset the initial value for [TopAppBarState.contentOffset]
 */
@Composable
fun rememberTopAppBarState(
    initialHeightOffsetLimit: Float = -Float.MAX_VALUE,
    initialHeightOffset: Float = 0f,
    initialContentOffset: Float = 0f,
): TopAppBarState = rememberSaveable(saver = Saver) {
    TopAppBarState(initialHeightOffsetLimit, initialHeightOffset, initialContentOffset)
}

/**
 * A state object that can be hoisted to control and observe the top app bar state. The state is
 * read and updated by a [ScrollBehavior] implementation.
 *
 * In most cases, this state will be created via [rememberTopAppBarState].
 *
 * @param initialHeightOffsetLimit the initial value for [TopAppBarState.heightOffsetLimit]
 * @param initialHeightOffset the initial value for [TopAppBarState.heightOffset]
 * @param initialContentOffset the initial value for [TopAppBarState.contentOffset]
 */
@Stable
class TopAppBarState(
    initialHeightOffsetLimit: Float,
    initialHeightOffset: Float,
    initialContentOffset: Float,
) {

    /**
     * The top app bar's height offset limit in pixels, which represents the limit that a top app
     * bar is allowed to collapse to.
     *
     * Use this limit to coerce the [heightOffset] value when it's updated.
     */
    var heightOffsetLimit = initialHeightOffsetLimit

    /**
     * The top app bar's current height offset in pixels. This height offset is applied to the fixed
     * height of the app bar to control the displayed height when content is being scrolled.
     *
     * A [COUIScrollBehavior] with `followScroll` moves this value at once on each scroll step and
     * draws the bar at [displayedHeightOffset], which catches up with it.
     *
     * Updates to the [heightOffset] value are coerced between zero and [heightOffsetLimit].
     */
    var heightOffset: Float
        get() = _heightOffset.floatValue
        set(newOffset) {
            _heightOffset.floatValue =
                newOffset.coerceIn(minimumValue = heightOffsetLimit, maximumValue = 0f)
        }

    /**
     * The height offset the bar is drawn at, in pixels: [heightOffset] once the bar has settled,
     * and the spring that trails it while a [COUIScrollBehavior] with `followScroll` moves the bar
     * (COUI 17 `COUIViewOffsetHelper.animateTopAndBottomOffset`: the app bar and the content below
     * it are offset by a spring that follows every scroll step). Read it rather than
     * [heightOffset] for anything drawn together with the bar.
     */
    val displayedHeightOffset: Float
        get() {
            val follow = followOffset
            return if (follow.isNaN()) heightOffset else follow.coerceIn(minimumValue = heightOffsetLimit, maximumValue = 0f)
        }

    /** The drawn height offset while the bar trails [heightOffset]; `NaN` once it has caught up. */
    internal var followOffset by mutableFloatStateOf(Float.NaN)

    /**
     * The total offset of the content scrolled under the top app bar.
     *
     * The content offset is used to compute the [overlappedFraction], which can later be read by an
     * implementation.
     *
     * This value is updated by a [ScrollBehavior] whenever a nested scroll connection
     * consumes scroll events. A common implementation would update the value to be the sum of all
     * [NestedScrollConnection.onPostScroll] `consumed.y` values.
     */
    var contentOffset by mutableFloatStateOf(initialContentOffset)

    /**
     * A value that represents the collapsed height percentage of the app bar.
     *
     * A `0.0` represents a fully expanded bar, and `1.0` represents a fully collapsed bar (computed
     * as [heightOffset] / [heightOffsetLimit]).
     */
    val collapsedFraction: Float
        get() =
            if (heightOffsetLimit != 0f) {
                heightOffset / heightOffsetLimit
            } else {
                0f
            }

    /**
     * A value that represents the percentage of the app bar area that is overlapping with the
     * content scrolled behind it.
     *
     * A `0.0` indicates that the app bar does not overlap any content, while `1.0` indicates that
     * the entire visible app bar area overlaps the scrolled content.
     */
    val overlappedFraction: Float
        get() =
            if (heightOffsetLimit != 0f) {
                1 -
                    (
                        (heightOffsetLimit - contentOffset).coerceIn(
                            minimumValue = heightOffsetLimit,
                            maximumValue = 0f,
                        ) / heightOffsetLimit
                        )
            } else {
                0f
            }

    companion object {
        /** The default [Saver] implementation for [TopAppBarState]. */
        val Saver: Saver<TopAppBarState, *> =
            listSaver(
                save = { listOf(it.heightOffsetLimit, it.heightOffset, it.contentOffset) },
                restore = {
                    TopAppBarState(
                        initialHeightOffsetLimit = it[0],
                        initialHeightOffset = it[1],
                        initialContentOffset = it[2],
                    )
                },
            )
    }

    private var _heightOffset = mutableFloatStateOf(initialHeightOffset)
}

/**
 * The distance, in pixels, the app bar's surface should treat itself as having scrolled — the value
 * that drives the condensed-light glow's alpha.
 *
 * Mirrors `AppBarBlurOverlayBehavior.resolveScrollDistancePx`: a collapsing bar reports its own
 * collapse offset while it is non-zero, and the distance the content has scrolled beneath it
 * otherwise. A pinned bar (see [SmallTopAppBar]) never collapses, so it always reports the content
 * distance; otherwise the glow could never appear on a pinned bar.
 */
@Composable
fun ScrollBehavior.glowScrollOffsetPx(): Float {
    // The drawn offset, so the glow fades in with the bar the content is seen beneath.
    val heightOffset = state.displayedHeightOffset
    return if (heightOffset != 0f) -heightOffset else -state.contentOffset
}

/** Contains default values used by [TopAppBar] and [SmallTopAppBar]. */
object TopAppBarDefaults {
    /**
     * The horizontal padding on a compact (phone) window. COUI 17 `toolbar_normal_padding_left/
     * right_compat`, `toolbar_center_menu_padding_horizontal_compat` (`values-v37`) and
     * `coui_appbar_title_expanded_margin_start/end_compat` (16dp).
     */
    val HorizontalPaddingCompact = 16.dp

    /**
     * The horizontal padding on a medium window. COUI 17 `toolbar_normal_padding_left/right_medium`,
     * `toolbar_center_menu_padding_horizontal_medium` (`values-v37`) and
     * `coui_appbar_title_expanded_margin_start/end_medium` (24dp).
     */
    val HorizontalPaddingMedium = 24.dp

    /**
     * The horizontal padding on an expanded window. COUI 17
     * `toolbar_normal_padding_left/right_expanded`, `toolbar_center_menu_padding_horizontal_expanded`
     * (`values-v37`) and `coui_appbar_title_expanded_margin_start/end_expanded` (40dp).
     */
    val HorizontalPaddingExpanded = 40.dp

    /**
     * The horizontal padding the bars use for the title, the navigation icon and the action icons
     * when those paddings are left [Dp.Unspecified]: [HorizontalPaddingCompact],
     * [HorizontalPaddingMedium] or [HorizontalPaddingExpanded] by the COUI screen tier of the bar.
     *
     * Mirrors `COUIToolbar.calculateToolbarPadding` on a material OS (the same padding with and
     * without menus) and `COUICollapsingToolbarLayout`'s expanded title margins: compact below a
     * 600dp bar width; otherwise `COUIResponsiveUtils.isMediumScreen` / `isLargeScreen` on the bar
     * width and [windowHeight] (`WindowTotalSizeClass`).
     *
     * @param barWidth the measured width of the bar.
     * @param windowHeight the height of the window the bar is shown in.
     */
    fun horizontalPadding(barWidth: Dp, windowHeight: Dp): Dp = when (COUILayoutGrid.screenTier(barWidth, windowHeight)) {
        COUILayoutGrid.ScreenTier.Small -> HorizontalPaddingCompact
        COUILayoutGrid.ScreenTier.Medium -> HorizontalPaddingMedium
        COUILayoutGrid.ScreenTier.Large -> HorizontalPaddingExpanded
    }

    /**
     * The title size of [SmallTopAppBar]: COUI `TextAppearance.COUI.Toolbar.SecondTitle` (18dp,
     * `sans-serif-medium`; a dp size, so it ignores font scale).
     */
    val SmallTitleTextSize = 18.dp

    /** The default collapsed height of the [TopAppBar]. */
    val CollapsedHeight = 52.dp

    /**
     * The default expanded height of the [TopAppBar] when no subtitle is present.
     * COUI `coui_appbar_title_expanded_height` (110dp); the bar grows beyond it when the
     * measured title content requires more space.
     */
    val ExpandedHeight = 110.dp

    /**
     * The expanded large title size: COUI `coui_appbar_title_expanded_text_size` (32dp, a dp size
     * that ignores font scale).
     */
    @Composable
    fun largeTitleFontSize(): TextUnit = with(LocalDensity.current) { 32.dp.toSp() }

    /**
     * The top padding of the expanded large title, measured from the top of the bar.
     * COUI `coui_appbar_title_expanded_margin_top` (54dp).
     */
    val LargeTitleTopPadding = 54.dp

    /**
     * The vertical center height used for [SmallTopAppBar] layout.
     * COUI `coui_appbar_title_toolbar_height` (52dp).
     */
    val SmallTopAppBarCenterHeight = 52.dp

    /**
     * The bottom padding below the large title block when the bar is expanded.
     * COUI `coui_appbar_start_padding_bottom` (12dp, interpolates to 0dp as the bar collapses).
     */
    val LargeTitleBottomPadding = 12.dp

    /** The bottom padding below the subtitle when it overflows the collapsed bar. */
    val SubtitleBottomPadding = 8.dp

    /**
     * The vertical gap between the title and the subtitle.
     * COUI `coui_appbar_subtitle_collapsed_margin_top` (3.5dp).
     */
    val SubtitleMarginTop = 3.5.dp

    /**
     * The horizontal gap between the navigation icon and the collapsed title.
     * COUI 17 `coui_toolbar_gap_between_navigation_and_title`: 10dp in `values-v37` (4dp before).
     */
    val NavigationIconGap = 10.dp

    /**
     * The horizontal spacing between adjacent action icons.
     * COUI 17 `coui_actionbar_menuitemview_item_spacing`: 10dp in `values-v37` (0dp before).
     */
    val ActionItemSpacing = 10.dp

    /**
     * The horizontal gap between the title and the action icons.
     * COUI 17 `coui_toolbar_gap_before_menu` (4dp).
     */
    val ActionIconGap = 4.dp

    /**
     * The height of the [TopAppBarSearchSlot] row. COUI `coui_search_view_height` (52dp).
     */
    val SearchSlotHeight = 52.dp

    /**
     * The horizontal page-side padding of the capsule inside [TopAppBarSearchSlot].
     * COUI `coui_preference_card_margin_horizontal` (16dp).
     */
    val SearchSlotInsideMargin = PaddingValues(horizontal = 16.dp)

    /**
     * The spring that snaps a [TopAppBar] left between expanded and collapsed to the nearer one
     * when a scroll or fling ends: COUI 17 `COUICollapsableAppBarLayoutParams` `attachResponse`
     * 0.25 with `attachBounce` 0 (`COUICollapsableAppBarLayout.ScrollBehavior.animateOffsetWithDuration`
     * springs the offset with it and ignores the duration).
     */
    val SnapAnimationSpec: AnimationSpec<Float> = TouchMotion.spring(response = 0.25f, bounce = 0f, visibilityThreshold = 0.5f)
}

@Stable
interface ScrollBehavior {

    /**
     * A [TopAppBarState] that is attached to this behavior and is read and updated when scrolling
     * happens.
     */
    val state: TopAppBarState

    /**
     * Indicates whether the top app bar is pinned.
     *
     * A pinned app bar will stay fixed in place when content is scrolled and will not react to any
     * drag gestures.
     */
    val isPinned: Boolean

    /**
     * An optional [AnimationSpec] that defines how the top app bar snaps to either fully collapsed
     * or fully extended state when a fling or a drag scrolled it into an intermediate position.
     */
    val snapAnimationSpec: AnimationSpec<Float>?

    /**
     * An optional [DecayAnimationSpec] that defined how to fling the top app bar when the user
     * flings the app bar itself, or the content below it.
     */
    val flingAnimationSpec: DecayAnimationSpec<Float>?

    /**
     * A [NestedScrollConnection] that should be attached to a [Modifier.nestedScroll] in order to
     * keep track of the scroll events.
     */
    val nestedScrollConnection: NestedScrollConnection
}

/**
 * A [ScrollBehavior] that adjusts its properties to affect the colors and height of a top
 * app bar.
 *
 * A top app bar that is set up with this [ScrollBehavior] will immediately collapse when
 * the nested content is pulled up, and will expand back the collapsed area when the content is
 * pulled all the way down.
 *
 * @param state a [TopAppBarState]
 * @param snapAnimationSpec an optional [AnimationSpec] that defines how the top app bar snaps to
 *   either fully collapsed or fully extended state when a fling or a drag scrolled it into an
 *   intermediate position
 * @param flingAnimationSpec an optional [DecayAnimationSpec] that defined how to fling the top app
 *   bar when the user flings the app bar itself, or the content below it
 * @param canScroll a callback used to determine whether scroll events are to be handled by this
 *   [ExitUntilCollapsedScrollBehavior]
 * @param followScope the scope the bar's follow spring runs in, or `null` to draw the bar at the
 *   scroll position directly (see `COUIScrollBehavior`'s `followScroll`).
 */
private class ExitUntilCollapsedScrollBehavior(
    override val state: TopAppBarState,
    override val snapAnimationSpec: AnimationSpec<Float>?,
    override val flingAnimationSpec: DecayAnimationSpec<Float>?,
    val canScroll: () -> Boolean = { true },
    private val followScope: CoroutineScope? = null,
) : ScrollBehavior {
    override val isPinned: Boolean = false

    /** The spring the bar is drawn by while it trails [TopAppBarState.heightOffset]. */
    private val follower = followScope?.let { TopAppBarFollowSpring(state, it) }

    /**
     * Moves the bar's height offset to [offset]. Without a follow scope the bar is drawn there at
     * once; otherwise the drawn offset ([TopAppBarState.displayedHeightOffset]) catches up with
     * [spec], keeping the velocity of a follow already running
     * (`COUIViewOffsetHelper.animateTopAndBottomOffset`: `animateToFinalPosition` on the running
     * spring).
     */
    fun moveHeightOffset(offset: Float, spec: AnimationSpec<Float>) {
        val spring = follower
        if (spring == null) {
            state.heightOffset = offset
            return
        }
        val from = state.displayedHeightOffset
        val previous = state.heightOffset
        state.heightOffset = offset
        if (state.heightOffset == previous && (spring.isRunning || from == previous)) return
        spring.follow(from, spec)
    }

    /**
     * Whether a drag on the bar itself may move it now: the bar can collapse and is not fully
     * collapsed (`COUICollapsableAppBarLayout.ScrollBehavior.canDragView`; a bar only stops short of
     * collapsed while the content below it is at its top).
     */
    fun canDragBar(): Boolean = canScroll() && state.heightOffsetLimit < 0f && state.heightOffset > state.heightOffsetLimit

    /**
     * Drags the bar itself by [delta] pixels (`COUIHeaderBehavior.onTouchEvent` scrolling the
     * header, followed with the `followHeaderResponse` spring).
     */
    fun dragBar(delta: Float) {
        moveHeightOffset(state.heightOffset + delta, FollowHeaderSpec)
    }

    /**
     * Settles the bar once a scroll, fling or drag of the bar has ended, and returns the velocity it
     * consumed. With a follow scope it never flings the bar on and only springs a bar left between
     * expanded and collapsed to the nearer one with [snapAnimationSpec]
     * (`COUICollapsableAppBarLayout.ScrollBehavior.fling` and `snapToChildIfNeeded`, the midpoint
     * deciding); otherwise it runs [settleAppBar].
     */
    suspend fun settle(velocity: Float): Velocity {
        if (followScope == null) return settleAppBar(state, velocity, flingAnimationSpec, snapAnimationSpec)
        val spec = snapAnimationSpec ?: return Velocity.Zero
        val offset = state.heightOffset
        val limit = state.heightOffsetLimit
        if (offset < 0f && offset > limit) {
            moveHeightOffset(if (state.collapsedFraction < 0.5f) 0f else limit, spec)
        }
        return Velocity.Zero
    }

    override var nestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Don't intercept if scrolling down.
                if (!canScroll() || available.y > 0) return Offset.Zero
                val prevHeightOffset = state.heightOffset
                moveHeightOffset(prevHeightOffset + available.y, FollowCollapseSpec)
                return if (prevHeightOffset != state.heightOffset) {
                    // We're in the middle of top app bar collapse or expand.
                    // Consume only the scroll on the Y axis.
                    available.copy(x = 0f)
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (!canScroll()) return Offset.Zero
                state.contentOffset += consumed.y

                if (available.y < 0f || consumed.y < 0f) {
                    // When scrolling up, just update the state's height offset.
                    val oldHeightOffset = state.heightOffset
                    moveHeightOffset(oldHeightOffset + consumed.y, FollowCollapseSpec)
                    return Offset(0f, state.heightOffset - oldHeightOffset)
                }

                if (available.y > 0f) {
                    // Adjust the height offset in case the consumed delta Y is less than what was
                    // recorded as available delta Y in the pre-scroll.
                    val oldHeightOffset = state.heightOffset
                    moveHeightOffset(oldHeightOffset + available.y, FollowExpandSpec)
                    return Offset(0f, state.heightOffset - oldHeightOffset)
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (available.y > 0) {
                    // Reset the total content offset to zero when scrolling all the way down. This
                    // will eliminate some float precision inaccuracies.
                    state.contentOffset = 0f
                }
                val superConsumed = super.onPostFling(consumed, available)
                return superConsumed + settle(available.y)
            }
        }
}

/**
 * Draws a top app bar after its [TopAppBarState.heightOffset] (COUI 17
 * `COUIViewOffsetHelper.mScrollSpringAnimation`): one frame loop in [scope] runs while the drawn
 * offset, [TopAppBarState.followOffset], has not caught up. Each frame it reads the bar's offset,
 * and a new offset or a new spec restarts the animation from where the bar is drawn with its
 * current velocity, like `animateToFinalPosition` on the running spring. For a spring that is
 * exact, so the bar can follow a scroll that moves its offset every frame.
 */
private class TopAppBarFollowSpring(
    private val state: TopAppBarState,
    private val scope: CoroutineScope,
) {
    private var sourceSpec: AnimationSpec<Float>? = null
    private var spec: VectorizedAnimationSpec<AnimationVector1D>? = null
    private var restartPending = false
    private var value = 0f
    private var velocity = 0f
    private var startValue = 0f
    private var startVelocity = 0f
    private var target = 0f
    private var playTimeNanos = 0L
    private var durationNanos = 0L

    /** Whether a follow is running. */
    var isRunning = false
        private set

    /**
     * Follows the bar's offset with [animationSpec]; a follow that is not running yet starts from
     * [from], where the bar is drawn now.
     */
    fun follow(from: Float, animationSpec: AnimationSpec<Float>) {
        if (animationSpec !== sourceSpec) {
            sourceSpec = animationSpec
            spec = animationSpec.vectorize(Float.VectorConverter)
            restartPending = true
        }
        if (isRunning) return
        isRunning = true
        restartPending = true
        value = from
        velocity = 0f
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                state.followOffset = value
                var lastFrameNanos = Long.MIN_VALUE
                while (true) {
                    val frameNanos = withFrameNanos { it }
                    // The first frame only takes the time, like COUIDynamicAnimation's first frame.
                    if (lastFrameNanos != Long.MIN_VALUE && !step(frameNanos - lastFrameNanos)) break
                    lastFrameNanos = frameNanos
                    state.followOffset = value
                }
            } finally {
                isRunning = false
                state.followOffset = Float.NaN
            }
        }
    }

    /** Advances the follow by [frameNanos]; `false` once it has settled on the bar's offset. */
    private fun step(frameNanos: Long): Boolean {
        val vectorized = spec ?: return false
        val latest = state.heightOffset
        if (restartPending || latest != target) {
            restartPending = false
            startValue = value
            startVelocity = velocity
            target = latest
            playTimeNanos = 0L
            durationNanos = vectorized.getDurationNanos(
                AnimationVector1D(startValue),
                AnimationVector1D(target),
                AnimationVector1D(startVelocity),
            )
        }
        playTimeNanos += frameNanos
        if (playTimeNanos >= durationNanos) {
            value = target
            velocity = 0f
            return false
        }
        val start = AnimationVector1D(startValue)
        val end = AnimationVector1D(target)
        val initialVelocity = AnimationVector1D(startVelocity)
        value = vectorized.getValueFromNanos(playTimeNanos, start, end, initialVelocity).value
        velocity = vectorized.getVelocityFromNanos(playTimeNanos, start, end, initialVelocity).value
        return true
    }
}

/**
 * Settles the app bar to a stable state (fully expanded or collapsed) by animating
 * its height offset.
 *
 * This function is invoked after a drag or fling gesture, using the provided velocity
 * to drive a decay animation, followed by a snap animation if the bar is left in an
 * intermediate state.
 *
 * @param state The [TopAppBarState] that holds the current and target height offsets.
 * @param velocity The velocity from the fling gesture to be consumed.
 * @param flingAnimationSpec The [DecayAnimationSpec] for the fling animation.
 * @param snapAnimationSpec The [AnimationSpec] for the final snap to a stable state.
 * @return The [Velocity] that was actually consumed by the fling decay animation. This
 * ensures accurate reporting within the nested scroll system, allowing any unconsumed
 * velocity to be propagated to parent consumers.
 */
private suspend fun settleAppBar(
    state: TopAppBarState,
    velocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
    snapAnimationSpec: AnimationSpec<Float>?,
): Velocity {
    // Check if the app bar is completely collapsed/expanded. If so, no need to settle the app bar,
    // and just return Zero Velocity.
    // Note that we don't check for 0f due to float precision with the collapsedFraction
    // calculation.
    if (state.collapsedFraction < 0.01f || state.collapsedFraction == 1f) {
        return Velocity.Zero
    }
    var remainingVelocity = velocity
    // In case there is an initial velocity that was left after a previous user fling, animate to
    // continue the motion to expand or collapse the app bar.
    if (flingAnimationSpec != null && abs(velocity) > 1f) {
        var lastValue = 0f
        AnimationState(initialValue = 0f, initialVelocity = velocity).animateDecay(
            flingAnimationSpec,
        ) {
            val delta = value - lastValue
            val initialHeightOffset = state.heightOffset
            state.heightOffset = initialHeightOffset + delta
            val consumed = abs(initialHeightOffset - state.heightOffset)
            lastValue = value
            remainingVelocity = this.velocity
            // avoid rounding errors and stop if anything is unconsumed
            if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
        }
    }
    // Snap if animation specs were provided.
    if (snapAnimationSpec != null) {
        if (state.heightOffset < 0 && state.heightOffset > state.heightOffsetLimit) {
            AnimationState(initialValue = state.heightOffset).animateTo(
                if (state.collapsedFraction < 0.5f) {
                    0f
                } else {
                    state.heightOffsetLimit
                },
                animationSpec = snapAnimationSpec,
            ) {
                state.heightOffset = value
            }
        }
    }
    return Velocity(0f, velocity - remainingVelocity)
}

/**
 * The base [Layout] for [TopAppBar]. This function lays out a [TopAppBar] navigation icon
 * (leading icon), a title (header), and action icons (trailing icons). Note that the navigation and
 * the actions are optional.
 *
 * @param title the [TopAppBar] title (header).
 * @param color the background color of the [TopAppBar].
 * @param titleColor the color of the collapsed small title text.
 * @param largeTitleColor the color of the expanded large title text.
 * @param subtitle the subtitle text displayed below the title bar area.
 * @param subtitleColor the color of the subtitle text.
 * @param dividerColor the color of the bottom hairline divider.
 * @param navigationIcon a navigation icon [Composable].
 * @param actions actions [Composable].
 * @param titlePadding the horizontal padding of the [TopAppBar]'s title & large title
 *   ([Dp.Unspecified] = [TopAppBarDefaults.horizontalPadding]).
 * @param navigationIconPadding the start padding of the navigation icon
 *   ([Dp.Unspecified] = [TopAppBarDefaults.horizontalPadding]).
 * @param actionIconPadding the end padding of the action icons
 *   ([Dp.Unspecified] = [TopAppBarDefaults.horizontalPadding]).
 * @param scrollBehavior the [ScrollBehavior] that drives the collapse/expand state. The layout
 *   reads its `displayedHeightOffset` directly inside the measure pass (so scroll only
 *   re-measures, never recomposes this subtree) and writes the matching `heightOffsetLimit` from
 *   the measured expanded height. A [COUIScrollBehavior] also lets the bar be dragged.
 * @param modifier the [Modifier] to be applied to this layout.
 * @param largeTitle the large title of the [TopAppBar], if not specified, it will be the same as title.
 * @param defaultWindowInsetsPadding whether to apply default window insets padding to the [TopAppBar].
 * @param showDivider whether to draw the bottom hairline divider while collapsing.
 * @param hideSubtitleOnCollapse whether the subtitle fades out while the bar collapses.
 * @param searchState the search transition that lifts [bottomContent] over the title area.
 * @param bottomContent the composable content displayed below the title bar area.
 */
@Composable
private fun TopAppBarLayout(
    title: String,
    color: Color,
    titleColor: Color,
    largeTitleColor: Color,
    subtitle: String,
    subtitleColor: Color,
    dividerColor: Color,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable () -> Unit,
    titlePadding: Dp,
    navigationIconPadding: Dp,
    actionIconPadding: Dp,
    scrollBehavior: ScrollBehavior?,
    modifier: Modifier = Modifier,
    largeTitle: String = title,
    defaultWindowInsetsPadding: Boolean = true,
    showDivider: Boolean = true,
    hideSubtitleOnCollapse: Boolean = true,
    expandedHeight: Dp = TopAppBarDefaults.ExpandedHeight,
    largeTitleFontSize: TextUnit = TopAppBarDefaults.largeTitleFontSize(),
    largeTitleBottomMargin: Dp = Dp.Unspecified,
    fadeLargeTitleOnCollapse: Boolean = false,
    glow: (@Composable BoxScope.() -> Unit)? = null,
    searchState: TopAppBarSearchState? = null,
    bottomContent: @Composable () -> Unit = {},
) {
    // Producer lambdas — reads stay in layout/draw phases so scroll never recomposes this subtree.
    // The bar is laid out at the drawn offset, which trails the scroll offset with the follow
    // spring (COUIViewOffsetHelper), so every collapse visual below moves with it.
    val scrolledOffset = remember(scrollBehavior) {
        { scrollBehavior?.state?.displayedHeightOffset ?: 0f }
    }
    // Divider progress: COUIDividerAppBarLayout fades the divider in over the whole
    // collapse range (alpha 0 -> 1, horizontal margin 24dp -> 0dp), dispatched from the same
    // animated offset as the bar.
    val dividerFraction = remember(scrollBehavior) {
        {
            val state = scrollBehavior?.state
            if (state == null || state.heightOffsetLimit == 0f) {
                0f
            } else {
                (state.displayedHeightOffset / state.heightOffsetLimit).coerceIn(0f, 1f)
            }
        }
    }
    // Dragging the bar itself (COUIHeaderBehavior.onTouchEvent): it follows the finger with the
    // header follow spring and snaps like a scroll on release. Whether a drag may move the bar is
    // decided when it starts (canDragView, read on ACTION_DOWN).
    val barDragBehavior = scrollBehavior as? ExitUntilCollapsedScrollBehavior
    val barDragAllowed = remember { mutableStateOf(false) }
    val barDragState = rememberDraggableState { delta ->
        if (barDragAllowed.value) barDragBehavior?.dragBar(delta)
    }
    val updateHeightOffsetLimit = remember(scrollBehavior) {
        { height: Int ->
            scrollBehavior?.state?.let { state ->
                val limit = -height.toFloat()
                if (state.heightOffsetLimit != limit) state.heightOffsetLimit = limit
            }
            Unit
        }
    }

    // CollapsingTextHelper lerps the text size from expanded to collapsed; replicated
    // as a scale factor around the title's top-start corner. A subtitle that stays visible at
    // full collapse shrinks the collapsed title (COUICollapsingToolbarLayout.hasPersistentSubtitle).
    val hasPersistentSubtitle = subtitle.isNotEmpty() && !hideSubtitleOnCollapse
    val collapsedTitleTextSize = with(LocalDensity.current) {
        (if (hasPersistentSubtitle) CollapsedTitleWithSubtitleTextSize else CollapsedTitleTextSize).toSp()
    }
    val collapsedTitleScale = remember(largeTitleFontSize, collapsedTitleTextSize) {
        if (largeTitleFontSize.value > 0f) {
            collapsedTitleTextSize.value / largeTitleFontSize.value
        } else {
            1f
        }
    }

    val animatedTitleColor by animateColorAsState(
        targetValue = titleColor,
        animationSpec = tween(durationMillis = 50),
    )
    val animatedLargeTitleColor by animateColorAsState(
        targetValue = largeTitleColor,
        animationSpec = tween(durationMillis = 50),
    )
    val animatedSubtitleColor by animateColorAsState(
        targetValue = subtitleColor,
        animationSpec = tween(durationMillis = 50),
    )

    // Reserved for the glow band's height calculation below; read here because the inset is only
    // available as a composable value.
    val statusBarInset = WindowInsets.systemBars.only(WindowInsetsSides.Top).asPaddingValues().calculateTopPadding()
    // The window height picks the responsive padding tier together with the bar width.
    val windowHeightPx = LocalWindowInfo.current.containerSize.height

    Layout(
        {
            // Backing layer: the condensed-light backdrop blur sits behind everything else in the
            // bar, spanning the collapsed toolbar band so the blur's gradient terminates at the
            // bar's bottom edge instead of the expanded title's. It must fill the band: a bare
            // `fillMaxWidth` would measure to 0dp because the slot's only child uses
            // `matchParentSize`, which never contributes to its parent's size.
            if (glow != null) {
                Box(
                    Modifier
                        .layoutId("glow")
                        .fillMaxSize(),
                    content = glow,
                )
            }
            Box(Modifier.layoutId("navigationIcon")) {
                navigationIcon()
            }
            // Collapsed title: only shown once the bar is fully collapsed; until then the
            // large title morphs into it. In fade mode the large title fades away instead, so
            // this slot stays empty. A new title cross-fades in (switchTitleContentWithAnimation).
            Box(Modifier.layoutId("title")) {
                if (!fadeLargeTitleOnCollapse) {
                    TopAppBarTitleCrossfade(text = title) { text ->
                        Text(
                            text = text,
                            color = animatedTitleColor,
                            fontSize = collapsedTitleTextSize,
                            fontWeight = TitleFontWeight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            softWrap = false,
                        )
                    }
                }
            }
            Box(Modifier.layoutId("actionIcons")) {
                actions()
            }
            // Expanded title: position and scale are driven by the collapse fraction in the
            // placement pass (CollapsingTextHelper SCALE mode).
            Box(Modifier.layoutId("largeTitle")) {
                TopAppBarTitleCrossfade(text = largeTitle) { text ->
                    Text(
                        text = text,
                        color = animatedLargeTitleColor,
                        fontSize = largeTitleFontSize,
                        fontWeight = TitleFontWeight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                    )
                }
            }
            if (subtitle.isNotEmpty()) {
                // Subtitle: alpha and collapsed position are driven in the placement pass
                // (COUICollapsableAppBarLayout.adjustSubtitleIfNeed).
                Box(Modifier.layoutId("subtitle")) {
                    Text(
                        text = subtitle,
                        color = animatedSubtitleColor,
                        style = COUITheme.textStyles.bodyM,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                    )
                }
            }
            Box(Modifier.layoutId("bottomContent")) {
                bottomContent()
            }
        },
        modifier = modifier
            .background(color)
            // Clip before the top inset padding: the glow is placed up into the status bar area,
            // which a clip inside the padding would cut away, leaving the status bar bare. The clip
            // also leaves room below the bar for the glow's extra strip and the buttons' shadow.
            .clip(BarClipShape)
            .then(
                if (defaultWindowInsetsPadding) {
                    Modifier
                        .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                } else {
                    Modifier
                },
            )
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
            .pointerInput(Unit) {
                detectTapGestures { /* Consume click */ }
            }
            .then(
                if (barDragBehavior != null) {
                    Modifier.draggable(
                        state = barDragState,
                        orientation = Orientation.Vertical,
                        onDragStarted = {
                            barDragAllowed.value = searchState?.isActive != true && barDragBehavior.canDragBar()
                        },
                        onDragStopped = { velocity ->
                            if (barDragAllowed.value) {
                                barDragAllowed.value = false
                                barDragBehavior.settle(velocity)
                            }
                        },
                    )
                } else {
                    Modifier
                },
            )
            .then(
                if (showDivider) {
                    Modifier.drawWithContent {
                        drawContent()
                        // COUIDividerAppBarLayout: hairline at the app bar's bottom edge,
                        // alpha 0 -> 1 and horizontal margin 24dp -> 0dp over the collapse. It rides
                        // the expanded bottom padding up and settles on the edge as the bar
                        // collapses (COUICollapsableAppBarLayout.adjustPaddingBottom -> translateDivider:
                        // -(startPaddingBottom + (endPaddingBottom - startPaddingBottom) * fraction)).
                        val fraction = dividerFraction()
                        if (fraction > 0f) {
                            val inset = lerp(DividerExpandedMarginHorizontal.toPx(), 0f, fraction)
                            val stroke = DividerThickness.toPx()
                            val lift = TopAppBarDefaults.LargeTitleBottomPadding.toPx() * (1f - fraction)
                            val y = size.height - stroke / 2f - lift
                            drawLine(
                                color = dividerColor,
                                start = Offset(inset, y),
                                end = Offset(size.width - inset, y),
                                strokeWidth = stroke,
                                alpha = fraction,
                            )
                        }
                    }
                } else {
                    Modifier
                },
            ),
    ) { measurables, constraints ->
        val navigationIconPlaceable =
            measurables
                .fastFirst { it.layoutId == "navigationIcon" }
                .measure(constraints.copy(minWidth = 0, minHeight = 0))

        val actionIconsPlaceable =
            measurables
                .fastFirst { it.layoutId == "actionIcons" }
                .measure(constraints.copy(minWidth = 0, minHeight = 0))

        // The status bar inset the layout's top padding reserves above this content box (applied
        // regardless of defaultWindowInsetsPadding, which only covers the horizontal insets); the
        // glow reaches up into it (compensated in the placement below).
        val statusBarInsetPx = statusBarInset.roundToPx()

        val tierPadding = TopAppBarDefaults.horizontalPadding(constraints.maxWidth.toDp(), windowHeightPx.toDp())
        val titlePaddingPx = (if (titlePadding.isSpecified) titlePadding else tierPadding).roundToPx()
        val navigationIconPaddingPx = (if (navigationIconPadding.isSpecified) navigationIconPadding else tierPadding).roundToPx()
        val actionIconPaddingPx = (if (actionIconPadding.isSpecified) actionIconPadding else tierPadding).roundToPx()

        // Collapsed title bounds: start-aligned after the navigation icon; falls back to the
        // title margin on sides without an icon.
        val hasNavigationIcon = navigationIconPlaceable.width > 0
        val hasActions = actionIconsPlaceable.width > 0
        val collapsedTitleX = if (hasNavigationIcon) {
            navigationIconPaddingPx + navigationIconPlaceable.width + TopAppBarDefaults.NavigationIconGap.roundToPx()
        } else {
            titlePaddingPx
        }
        val collapsedTitleEnd = if (hasActions) {
            constraints.maxWidth - actionIconPaddingPx - actionIconsPlaceable.width - TopAppBarDefaults.ActionIconGap.roundToPx()
        } else {
            constraints.maxWidth - titlePaddingPx
        }
        val collapsedTitleMaxWidth = (collapsedTitleEnd - collapsedTitleX).coerceAtLeast(0)

        val titlePlaceable =
            measurables
                .fastFirst { it.layoutId == "title" }
                .measure(constraints.copy(minWidth = 0, maxWidth = collapsedTitleMaxWidth, minHeight = 0))

        val largeTitleMaxWidth = (constraints.maxWidth - 2 * titlePaddingPx).coerceAtLeast(0)
        val largeTitleMeasurable = measurables.fastFirst { it.layoutId == "largeTitle" }
        val largeTitleConstraints = constraints.copy(minWidth = 0, maxWidth = largeTitleMaxWidth, minHeight = 0)
        // A large title too long for the expanded bar is laid out again on every step of the morph,
        // ellipsized to the bounds it is passing through scaled back up, so the shrinking title
        // never runs past the collapsed slot (COUICollapsingToolbarLayout.updateTextLayoutIfNeed ->
        // createCouiStaticLayout: width = currentBounds.width / scale, for a title that shows an
        // ellipsis). A title that fits keeps its expanded layout, measured once here.
        val ellipsizeWhileMorphing = !fadeLargeTitleOnCollapse &&
            largeTitleMeasurable.maxIntrinsicWidth(Constraints.Infinity) > largeTitleMaxWidth
        val fittingLargeTitlePlaceable = if (ellipsizeWhileMorphing) null else largeTitleMeasurable.measure(largeTitleConstraints)
        // A single line, so its height does not depend on the width it is finally laid out at.
        val largeTitleHeight = fittingLargeTitlePlaceable?.height ?: largeTitleMeasurable.maxIntrinsicHeight(largeTitleMaxWidth)

        val subtitlePlaceable =
            measurables
                .firstOrNull { it.layoutId == "subtitle" }
                ?.measure(constraints.copy(minWidth = 0, maxWidth = largeTitleMaxWidth, minHeight = 0))

        val bottomContentPlaceable =
            measurables
                .fastFirst { it.layoutId == "bottomContent" }
                .measure(constraints.copy(minWidth = 0, minHeight = 0))

        val collapsedHeight = TopAppBarDefaults.CollapsedHeight.roundToPx()
        val largeTitleTop = if (largeTitleBottomMargin.isSpecified) {
            expandedHeight.roundToPx() - largeTitleBottomMargin.roundToPx() - largeTitleHeight
        } else {
            TopAppBarDefaults.LargeTitleTopPadding.roundToPx()
        }
        val subtitleMarginTopPx = TopAppBarDefaults.SubtitleMarginTop.roundToPx()
        val largeTitleBottomPaddingPx = TopAppBarDefaults.LargeTitleBottomPadding.roundToPx()
        val subtitleHeight = subtitlePlaceable?.height ?: 0

        // Expanded bar height (COUICollapsableAppBarLayout.onMeasure): the fixed 107dp grows by
        // the subtitle height, with the 12dp expanded bottom padding folded in.
        val expandedContentHeight = largeTitleTop + largeTitleHeight +
            (subtitlePlaceable?.let { subtitleMarginTopPx + it.height } ?: 0)
        val expandedBarHeight = maxOf(
            expandedHeight.roundToPx() + subtitleHeight,
            expandedContentHeight + largeTitleBottomPaddingPx,
        )
        val expansion = (expandedBarHeight - collapsedHeight).coerceAtLeast(0)
        updateHeightOffsetLimit(expansion)

        val offset = scrolledOffset()
        val offsetPx = if (offset.isNaN()) 0 else offset.roundToInt()
        val collapseFraction = if (expansion > 0) {
            (abs(offsetPx) / expansion.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
        // The bar height follows the drawn offset linearly; only the title morph below is
        // eased (COUIEaseInterpolator on the CollapsingTextHelper position/text size).
        val barHeight = (collapsedHeight + expansion + offsetPx).coerceAtLeast(collapsedHeight)
        val easedFraction = CollapsingTitleEasing.transform(collapseFraction)

        val verticalCenter = collapsedHeight / 2
        // Collapsed title: vertically centered in the toolbar area; shifted up above a subtitle
        // that stays visible at full collapse (COUICollapsingToolbarLayout.translateTitleIfNeed).
        val subtitleVisibleWhenCollapsed = subtitlePlaceable != null && !hideSubtitleOnCollapse
        val collapsedTitleY = if (subtitleVisibleWhenCollapsed) {
            (collapsedHeight - subtitleHeight - subtitleMarginTopPx - titlePlaceable.height).coerceAtLeast(0)
        } else {
            verticalCenter - titlePlaceable.height / 2
        }

        // Large title morph (CollapsingTextHelper.calculateOffsets): eased lerp between the
        // expanded and collapsed slots, shifted by the drawn offset.
        val largeTitleX = lerp(titlePaddingPx.toFloat(), collapsedTitleX.toFloat(), easedFraction)
        val largeTitleY =
            lerp(largeTitleTop.toFloat(), (collapsedTitleY + expansion).toFloat(), easedFraction) + offsetPx
        // Fade mode (COUI 17 collapsing toolbar): the large title keeps its own text size and only
        // alpha (1 -> 0) and scale (1 -> 0.7) move, both linearly on the raw expansion fraction —
        // the eased blend below only drives the morph's position.
        val largeTitleScale = if (fadeLargeTitleOnCollapse) {
            lerp(1f, FadedLargeTitleScale, collapseFraction)
        } else {
            lerp(1f, collapsedTitleScale, easedFraction)
        }
        val collapsed = !fadeLargeTitleOnCollapse && collapseFraction >= CollapsedTitleSwapFraction
        val pivotX = if (layoutDirection == LayoutDirection.Rtl) 1f else 0f
        val largeTitlePlaceable = fittingLargeTitlePlaceable ?: run {
            // The bounds lerp from the expanded to the collapsed slot with the morph's position
            // easing (COUICollapsingTextHelper.interpolateBounds).
            val boundsWidth = lerp(largeTitleMaxWidth.toFloat(), collapsedTitleMaxWidth.toFloat(), easedFraction)
            val layoutWidth = (boundsWidth / largeTitleScale).roundToInt().coerceIn(0, largeTitleMaxWidth)
            largeTitleMeasurable.measure(largeTitleConstraints.copy(maxWidth = layoutWidth))
        }

        // Search transition (HomepageImpl.animToEdit): the title area scrolls away above the bottom
        // content, which rises to the top of the bar, while the titles also shrink, fade and move up
        // (CollapsingToolbarLayout.setTitleTransform). Read here, so it only re-lays out the bar.
        val searchProgress = searchState?.progress ?: 0f
        val searchAlpha = searchState?.contentAlpha ?: 1f
        val contentTop = (barHeight * (1f - searchProgress)).roundToInt()
        val areaShift = contentTop - barHeight
        val titleShift = areaShift - (TopAppBarSearchDefaults.TitleTravel.toPx() * searchProgress).roundToInt()
        val titleSearchScale = lerp(TopAppBarSearchDefaults.TitleEndScale, 1f, searchAlpha)

        val layoutHeight = contentTop + bottomContentPlaceable.height
        val glowPlaceable = measureGlow(measurables, constraints.maxWidth, statusBarInsetPx + layoutHeight)

        layout(constraints.maxWidth, layoutHeight) {
            // Backing layer (condensed-light glow): spans the collapsed toolbar band, including the
            // status bar area the layout's top inset padding reserves above this content box.
            glowPlaceable?.placeRelative(x = 0, y = -statusBarInsetPx)

            // The titles go beneath the toolbar buttons, which in turn go beneath the subtitle: the
            // collapsing title is drawn in COUICollapsingToolbarLayout.onDraw, before its toolbar
            // child, and the subtitle is a later sibling of the collapsing toolbar.

            // Large title: translates and scales into the collapsed title; swapped for the
            // dedicated collapsed title only at full collapse.
            largeTitlePlaceable.placeRelativeWithLayer(
                x = largeTitleX.roundToInt(),
                y = largeTitleY.roundToInt() + titleShift,
            ) {
                transformOrigin = TransformOrigin(pivotX, 0f)
                scaleX = largeTitleScale * titleSearchScale
                scaleY = largeTitleScale * titleSearchScale
                alpha = searchAlpha * when {
                    fadeLargeTitleOnCollapse -> 1f - collapseFraction
                    collapsed -> 0f
                    else -> 1f
                }
            }

            // Collapsed title (start-aligned, visible only at full collapse).
            titlePlaceable.placeRelativeWithLayer(
                x = collapsedTitleX,
                y = collapsedTitleY + titleShift,
            ) {
                transformOrigin = TransformOrigin(pivotX, 0f)
                scaleX = titleSearchScale
                scaleY = titleSearchScale
                alpha = if (collapsed) searchAlpha else 0f
            }

            // Navigation icon
            navigationIconPlaceable.placeRelativeWithLayer(
                x = navigationIconPaddingPx,
                y = verticalCenter - navigationIconPlaceable.height / 2 + areaShift,
            ) {
                alpha = searchAlpha
            }

            // Action icons
            actionIconsPlaceable.placeRelativeWithLayer(
                x = constraints.maxWidth - actionIconPaddingPx - actionIconsPlaceable.width,
                y = verticalCenter - actionIconsPlaceable.height / 2 + areaShift,
            ) {
                alpha = searchAlpha
            }

            // Subtitle (COUICollapsableAppBarLayout.adjustSubtitleIfNeed): rides the scroll 1:1,
            // lifted by the interpolating bottom padding; fades out only when hideSubtitleOnCollapse.
            subtitlePlaceable?.placeRelativeWithLayer(
                x = titlePaddingPx,
                y = expandedBarHeight - subtitleHeight -
                    (largeTitleBottomPaddingPx * (1f - collapseFraction)).roundToInt() + offsetPx + titleShift,
            ) {
                alpha = (if (hideSubtitleOnCollapse) 1f - collapseFraction else 1f) * searchAlpha
            }

            // Bottom content (pinned, below the bar)
            bottomContentPlaceable.placeRelative(x = 0, y = contentTop)
        }
    }
}

/**
 * The base [Layout] for [SmallTopAppBar]. This function lays out a [SmallTopAppBar] navigation icon
 * (leading icon), a title (header), and action icons (trailing icons). Note that the navigation and
 * the actions are optional.
 *
 * @param title the [SmallTopAppBar] title (header).
 * @param color the background color of the [SmallTopAppBar].
 * @param titleColor the color of the title text.
 * @param subtitle the subtitle text displayed below the title bar area.
 * @param subtitleColor the color of the subtitle text.
 * @param dividerColor the color of the bottom hairline divider.
 * @param navigationIcon a navigation icon [Composable].
 * @param actions actions [Composable].
 * @param titlePadding the horizontal padding of the [SmallTopAppBar]'s title
 *   ([Dp.Unspecified] = [TopAppBarDefaults.horizontalPadding]).
 * @param navigationIconPadding the start padding of the navigation icon
 *   ([Dp.Unspecified] = [TopAppBarDefaults.horizontalPadding]).
 * @param actionIconPadding the end padding of the action icons
 *   ([Dp.Unspecified] = [TopAppBarDefaults.horizontalPadding]).
 * @param scrollBehavior the [ScrollBehavior] whose content offset drives the divider reveal.
 * @param modifier the [Modifier] to be applied to this layout.
 * @param defaultWindowInsetsPadding whether to apply default window insets padding to the [SmallTopAppBar].
 * @param showDivider whether to draw the bottom hairline divider on scroll.
 * @param centerTitle whether to center the title and subtitle (`COUIToolbar` title center style).
 * @param glow the bar's backing layer, drawn behind everything else in the bar.
 * @param bottomContent the composable content displayed below the title bar area.
 */
@Composable
private fun SmallTopAppBarLayout(
    title: String,
    color: Color,
    titleColor: Color,
    subtitle: String,
    subtitleColor: Color,
    dividerColor: Color,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable () -> Unit,
    titlePadding: Dp,
    navigationIconPadding: Dp,
    actionIconPadding: Dp,
    scrollBehavior: ScrollBehavior?,
    modifier: Modifier = Modifier,
    defaultWindowInsetsPadding: Boolean = true,
    showDivider: Boolean = true,
    centerTitle: Boolean = false,
    glow: (@Composable BoxScope.() -> Unit)? = null,
    bottomContent: @Composable () -> Unit = {},
) {
    val titleTextSize = with(LocalDensity.current) { TopAppBarDefaults.SmallTitleTextSize.toSp() }
    val animatedTitleColor by animateColorAsState(
        targetValue = titleColor,
        animationSpec = tween(durationMillis = 50),
    )
    val animatedSubtitleColor by animateColorAsState(
        targetValue = subtitleColor,
        animationSpec = tween(durationMillis = 50),
    )

    // Reserved for the glow band's height calculation below; read here because the inset is only
    // available as a composable value.
    val statusBarInset = WindowInsets.systemBars.only(WindowInsetsSides.Top).asPaddingValues().calculateTopPadding()
    // The window height picks the responsive padding tier together with the bar width.
    val windowHeightPx = LocalWindowInfo.current.containerSize.height

    // Distance the content has scrolled beneath the pinned bar, in pixels. Drives the
    // divider like COUI's second-page toolbar behaviors (alpha over the first 10dp,
    // margin 24dp -> 0dp over the next 25dp after a 10dp threshold).
    val scrolledDistance = remember(scrollBehavior) {
        { -(scrollBehavior?.state?.contentOffset ?: 0f) }
    }

    Layout(
        {
            // Backing layer: the condensed-light backdrop blur sits behind everything else in the
            // bar, spanning the pinned bar height. `fillMaxSize` is required — the glow's only
            // child uses `matchParentSize`, which contributes no size of its own.
            if (glow != null) {
                Box(
                    Modifier
                        .layoutId("glow")
                        .fillMaxSize(),
                    content = glow,
                )
            }
            Box(Modifier.layoutId("navigationIcon")) {
                navigationIcon()
            }
            // Title: TextAppearance.COUI.Toolbar.SecondTitle (18dp, sans-serif-medium). A new
            // title cross-fades in (ToolbarTextTransitionController.startCrossFade); centered
            // titles fade about their common center.
            Box(Modifier.layoutId("title")) {
                TopAppBarTitleCrossfade(
                    text = title,
                    contentAlignment = if (centerTitle) Alignment.TopCenter else Alignment.TopStart,
                ) { text ->
                    Text(
                        text = text,
                        color = animatedTitleColor,
                        maxLines = 1,
                        fontSize = titleTextSize,
                        fontWeight = SmallTitleFontWeight,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                    )
                }
            }
            Box(Modifier.layoutId("actionIcons")) {
                actions()
            }
            if (subtitle.isNotEmpty()) {
                // Subtitle: coui_toolbar_subtitle_text_size, single line.
                Box(Modifier.layoutId("subtitle")) {
                    Text(
                        text = subtitle,
                        color = animatedSubtitleColor,
                        style = COUITheme.textStyles.bodyM,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                    )
                }
            }
            Box(Modifier.layoutId("bottomContent")) {
                bottomContent()
            }
        },
        modifier = modifier
            .background(color)
            // Clip before the top inset padding: the glow is placed up into the status bar area,
            // which a clip inside the padding would cut away, leaving the status bar bare. The clip
            // also leaves room below the bar for the glow's extra strip and the buttons' shadow.
            .clip(BarClipShape)
            .then(
                if (defaultWindowInsetsPadding) {
                    Modifier
                        .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                } else {
                    Modifier
                },
            )
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
            .pointerInput(Unit) {
                detectTapGestures { /* Consume click */ }
            }
            .then(
                if (showDivider) {
                    Modifier.drawWithContent {
                        drawContent()
                        // COUI second-page toolbar divider: fades in over the first 10dp of
                        // content scroll, then shrinks its horizontal margin 24dp -> 0dp
                        // over the following 25dp.
                        val distance = scrolledDistance()
                        val alphaFraction = (distance / DividerAlphaChangeOffset.toPx()).coerceIn(0f, 1f)
                        if (alphaFraction > 0f) {
                            val marginFraction =
                                ((distance - DividerWidthStartCountOffset.toPx()) / DividerWidthChangeOffset.toPx())
                                    .coerceIn(0f, 1f)
                            val inset = lerp(DividerExpandedMarginHorizontal.toPx(), 0f, marginFraction)
                            val stroke = DividerThickness.toPx()
                            val y = size.height - stroke / 2f
                            drawLine(
                                color = dividerColor,
                                start = Offset(inset, y),
                                end = Offset(size.width - inset, y),
                                strokeWidth = stroke,
                                alpha = alphaFraction,
                            )
                        }
                    }
                } else {
                    Modifier
                },
            ),
    ) { measurables, constraints ->
        val navigationIconPlaceable =
            measurables
                .fastFirst { it.layoutId == "navigationIcon" }
                .measure(constraints.copy(minWidth = 0, minHeight = 0))

        val actionIconsPlaceable =
            measurables
                .fastFirst { it.layoutId == "actionIcons" }
                .measure(constraints.copy(minWidth = 0, minHeight = 0))

        // The status bar inset the layout's top padding reserves above this content box (applied
        // regardless of defaultWindowInsetsPadding, which only covers the horizontal insets); the
        // glow reaches up into it (compensated in the placement below).
        val statusBarInsetPx = statusBarInset.roundToPx()

        val tierPadding = TopAppBarDefaults.horizontalPadding(constraints.maxWidth.toDp(), windowHeightPx.toDp())
        val titlePaddingPx = (if (titlePadding.isSpecified) titlePadding else tierPadding).roundToPx()
        val navigationIconPaddingPx = (if (navigationIconPadding.isSpecified) navigationIconPadding else tierPadding).roundToPx()
        val actionIconPaddingPx = (if (actionIconPadding.isSpecified) actionIconPadding else tierPadding).roundToPx()

        // Title bounds: after the navigation icon and before the actions (COUIToolbar
        // mTitlePosition); falls back to the title margin on sides without an icon.
        val hasNavigationIcon = navigationIconPlaceable.width > 0
        val hasActions = actionIconsPlaceable.width > 0
        val titleX = if (hasNavigationIcon) {
            navigationIconPaddingPx + navigationIconPlaceable.width + TopAppBarDefaults.NavigationIconGap.roundToPx()
        } else {
            titlePaddingPx
        }
        val titleEnd = if (hasActions) {
            constraints.maxWidth - actionIconPaddingPx - actionIconsPlaceable.width - TopAppBarDefaults.ActionIconGap.roundToPx()
        } else {
            constraints.maxWidth - titlePaddingPx
        }
        val titleMaxWidth = (titleEnd - titleX).coerceAtLeast(0)

        val titlePlaceable =
            measurables
                .fastFirst { it.layoutId == "title" }
                .measure(constraints.copy(minWidth = 0, maxWidth = titleMaxWidth, minHeight = 0))

        val subtitlePlaceable =
            measurables
                .firstOrNull { it.layoutId == "subtitle" }
                ?.measure(constraints.copy(minWidth = 0, maxWidth = titleMaxWidth, minHeight = 0))

        val bottomContentPlaceable =
            measurables
                .fastFirst { it.layoutId == "bottomContent" }
                .measure(constraints.copy(minWidth = 0, minHeight = 0))

        val collapsedHeight = TopAppBarDefaults.CollapsedHeight.roundToPx()
        val verticalCenter = TopAppBarDefaults.SmallTopAppBarCenterHeight.roundToPx() / 2
        val subtitleMarginTopPx = TopAppBarDefaults.SubtitleMarginTop.roundToPx()

        // Title stack: vertically centered in the 52dp toolbar area; the subtitle sits
        // 3.5dp below the title, matching the collapsed COUI app bar stack.
        val stackHeight = titlePlaceable.height +
            (subtitlePlaceable?.let { subtitleMarginTopPx + it.height } ?: 0)
        val titleY = if (subtitlePlaceable != null) {
            ((TopAppBarDefaults.SmallTopAppBarCenterHeight.roundToPx() - stackHeight) / 2).coerceAtLeast(0)
        } else {
            verticalCenter - titlePlaceable.height / 2
        }
        val subtitleY = titleY + titlePlaceable.height + subtitleMarginTopPx
        // Center style (COUIToolbar.onLayout with mIsTitleCenterStyle): both lines are centered on
        // the whole bar when the wider one fits the symmetric space left by the wider side;
        // otherwise each is centered in the [titleX, titleEnd] gap, or pinned to its start when
        // even the gap is too narrow.
        val symmetricWidth = constraints.maxWidth - 2 * maxOf(titleX, constraints.maxWidth - titleEnd)
        val centerOnBar = maxOf(titlePlaceable.width, subtitlePlaceable?.width ?: 0) <= symmetricWidth
        val textX: (Int) -> Int = { width ->
            when {
                !centerTitle -> titleX
                centerOnBar -> (constraints.maxWidth - width) / 2
                width >= titleMaxWidth -> titleX
                else -> titleX + (titleMaxWidth - width) / 2
            }
        }
        val subtitleBottomPadding = if (subtitlePlaceable != null) TopAppBarDefaults.SubtitleBottomPadding.roundToPx() else 0
        val contentTop = maxOf(collapsedHeight, stackHeight + subtitleBottomPadding)
        val layoutHeight = contentTop + bottomContentPlaceable.height
        val glowPlaceable = measureGlow(measurables, constraints.maxWidth, statusBarInsetPx + layoutHeight)

        layout(constraints.maxWidth, layoutHeight) {
            // Backing layer (condensed-light glow): spans the pinned bar band, including the status
            // bar area the layout's top inset padding reserves above this content box.
            glowPlaceable?.placeRelative(x = 0, y = -statusBarInsetPx)

            // Navigation icon
            navigationIconPlaceable.placeRelative(
                x = navigationIconPaddingPx,
                y = verticalCenter - navigationIconPlaceable.height / 2,
            )

            // Title (start-aligned, or centered in center style)
            titlePlaceable.placeRelative(
                x = textX(titlePlaceable.width),
                y = titleY,
            )

            // Action icons
            actionIconsPlaceable.placeRelative(
                x = constraints.maxWidth - actionIconPaddingPx - actionIconsPlaceable.width,
                y = verticalCenter - actionIconsPlaceable.height / 2,
            )

            // Subtitle (3.5dp below the title, aligned like it)
            subtitlePlaceable?.let {
                it.placeRelative(
                    x = textX(it.width),
                    y = subtitleY,
                )
            }

            // Bottom content (below the toolbar area)
            bottomContentPlaceable.placeRelative(x = 0, y = contentTop)
        }
    }
}

/**
 * Shows [text] through [content] and cross-fades to a new one whenever [text] changes, like the
 * COUI 17 toolbar title switch (`COUICollapsingToolbarLayout.switchTitleContentWithAnimation` ->
 * `ToolbarTextTransitionController.startCrossFade`): the old title fades out and the new one fades
 * in at the same place, both with the response 0.2 / bounce 0 alpha spring. A change while a
 * cross-fade runs continues the outgoing title from the alpha it had reached.
 *
 * The alphas are read in the draw phase, so the fade itself never recomposes the title.
 *
 * @param text the title to show.
 * @param modifier the [Modifier] to be applied to the titles' container.
 * @param contentAlignment where the two titles sit against each other while they cross-fade.
 * @param content draws one title.
 */
@Composable
private fun TopAppBarTitleCrossfade(
    text: String,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable (String) -> Unit,
) {
    var shownText by remember { mutableStateOf(text) }
    var leavingText by remember { mutableStateOf<String?>(null) }
    val enterAlpha = remember { Animatable(1f, visibilityThreshold = TitleCrossfadeVisibilityThreshold) }
    val exitAlpha = remember { Animatable(0f, visibilityThreshold = TitleCrossfadeVisibilityThreshold) }
    LaunchedEffect(text) {
        if (text == shownText) return@LaunchedEffect
        // The outgoing title leaves from the alpha it had reached (startCrossFade continues a view
        // that is not at rest), the incoming one starts from transparent.
        val from = enterAlpha.value
        leavingText = shownText
        shownText = text
        exitAlpha.snapTo(from)
        enterAlpha.snapTo(0f)
        coroutineScope {
            launch { exitAlpha.animateTo(0f, TitleCrossfadeSpec) }
            launch { enterAlpha.animateTo(1f, TitleCrossfadeSpec) }
        }
        leavingText = null
    }
    val leaving = leavingText
    val titles = if (leaving != null) listOf(leaving, shownText) else listOf(shownText)
    Box(modifier = modifier, contentAlignment = contentAlignment) {
        titles.forEach { title ->
            // Keyed by the text, so the outgoing title keeps its node and laid-out text as it leaves.
            key(title) {
                val isLeaving = title == leaving
                Box(
                    if (isLeaving) {
                        // The outgoing title is decoration only; accessibility reads the new one.
                        Modifier
                            .clearAndSetSemantics {}
                            .graphicsLayer { alpha = exitAlpha.value }
                    } else {
                        Modifier.graphicsLayer { alpha = enterAlpha.value }
                    },
                ) {
                    content(title)
                }
            }
        }
    }
}

/** The title cross-fade's minimum visible alpha change, `ToolbarGroupTransitionParams` (1/256). */
private val TitleCrossfadeVisibilityThreshold = 1f / 256f

/**
 * The weight shared by the expanded and the collapsed title. `COUIAppbarTitleStyle.Expanded` is
 * `sans-serif-semibold`; the large title is swapped for the collapsed one at full collapse
 * (`COUICollapsingTextHelper` changes typeface only when the fraction reaches 1), so both use the
 * same weight to keep that swap from showing as a jump in stroke weight.
 */
private val TitleFontWeight = FontWeight.SemiBold

/** COUI `coui_appbar_title_collapsed_text_size` (24dp, sans-serif-medium; a dp size, so it ignores font scale). */
private val CollapsedTitleTextSize = 24.dp

/**
 * COUI `coui_appbar_title_collapsed_with_subtitle_text_size` (18dp): the collapsed title size
 * `COUICollapsingToolbarLayout` switches to while a subtitle stays visible at full collapse.
 */
private val CollapsedTitleWithSubtitleTextSize = 18.dp

/**
 * The weight of the [SmallTopAppBar] title: `TextAppearance.COUI.Toolbar.SecondTitle` is
 * `sans-serif-medium` with `textStyle normal`. The bar never collapses, so no typeface swap has to
 * line up with the collapsing title's [TitleFontWeight].
 */
private val SmallTitleFontWeight = FontWeight.Medium

/**
 * COUIEaseInterpolator: cubic-bezier(0.33, 0, 0.67, 1), applied to the collapsing title's
 * position and scale.
 */
private val CollapsingTitleEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** The pixel distance at which the bar's follow springs count as settled. */
private val FollowVisibilityThreshold = 0.5f

/**
 * The follow spring while a scroll collapses the bar: COUI 17 `COUICollapsableAppBarLayoutParams`
 * `followListResponseUp` 0.2 with `followBounce` 0 (`ScrollBehavior.scroll` for `dy > 0`).
 */
private val FollowCollapseSpec = TouchMotion.spring(response = 0.2f, bounce = 0f, visibilityThreshold = FollowVisibilityThreshold)

/**
 * The follow spring while a scroll expands the bar: `followListResponseDown` 0.1 with
 * `followBounce` 0 (`ScrollBehavior.scroll` for `dy < 0`).
 */
private val FollowExpandSpec = TouchMotion.spring(response = 0.1f, bounce = 0f, visibilityThreshold = FollowVisibilityThreshold)

/**
 * The follow spring while the bar itself is dragged: `followHeaderResponse` 0.15 with
 * `followHeaderBounce` 0 (`ScrollBehavior.scroll` from `COUIHeaderBehavior.onTouchEvent`).
 */
private val FollowHeaderSpec = TouchMotion.spring(response = 0.15f, bounce = 0f, visibilityThreshold = FollowVisibilityThreshold)

/**
 * The title cross-fade spring, both ways: COUI 17 `ToolbarGroupTransitionParams`
 * `textEnterAlphaSpring` / `textExitAlphaSpring` (response 0.2, bounce 0) with the 1/256 minimum
 * visible alpha change (`ToolbarTextTransitionController.startCrossFade`).
 */
private val TitleCrossfadeSpec = TouchMotion.spring(response = 0.2f, bounce = 0f, visibilityThreshold = TitleCrossfadeVisibilityThreshold)

/**
 * The collapsed fraction at which the morphing large title is swapped for the dedicated
 * collapsed title (CollapsingTextHelper isClose(fraction, 1f)).
 */
private val CollapsedTitleSwapFraction = 0.9999f

/**
 * End scale of the fading large title in fade mode
 * (SettingsCollapsingToolbarLayout.OnOffsetChangedListener: `AnimationUtils.lerp(1.0f, 0.7f, f)`).
 */
private val FadedLargeTitleScale = 0.7f

/**
 * Measures the glow slot as `AppBarBlurOverlayBehavior.updateOverlayBounds` sizes its overlay: the
 * whole bar, [barHeight] including the status bar, plus [GlowExtraHeight] below it.
 */
private fun Density.measureGlow(measurables: List<Measurable>, width: Int, barHeight: Int): Placeable? = measurables
    .firstOrNull { it.layoutId == "glow" }
    ?.measure(Constraints.fixed(width, barHeight + GlowExtraHeight.roundToPx()))

/**
 * `AppBarBlurOverlayView.computeExtraHeightPx` with `scrollDrivenAlphaEnabled`: the overlay runs
 * 24dp past the bottom of the app bar, so its gradient dissolves over the content rather than
 * ending at the bar's edge.
 */
private val GlowExtraHeight = 24.dp

/**
 * Clips the bar's top and sides but lets [GlowExtraHeight] spill below its bottom edge: the glow
 * extends that far, and the action buttons' elevation shadow falls into the same strip.
 */
private val BarClipShape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val overflow = with(density) { GlowExtraHeight.toPx() }
        return Outline.Rectangle(Rect(0f, 0f, size.width, size.height + overflow))
    }
}

/** COUI: toolbar_divider_height = 0.33dp. */
private val DividerThickness = 0.33.dp

/** COUI: coui_appbar_divider_expanded_margin_horizontal = 24dp (collapsed = 0dp). */
private val DividerExpandedMarginHorizontal = 24.dp

/** COUI: preference_line_alpha_range_change_offset = 10dp (pinned bar divider alpha ramp). */
private val DividerAlphaChangeOffset = 10.dp

/** COUI: preference_divider_width_start_count_offset = 10dp. */
private val DividerWidthStartCountOffset = 10.dp

/** COUI: preference_divider_width_change_offset = 25dp. */
private val DividerWidthChangeOffset = 25.dp
