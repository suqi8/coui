// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFirst
import com.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

private object COUIDimens {
    val GapNavTitle = 4.dp
    val GapTitleMenu = 8.dp
    val MenuItemMinWidth = 48.dp

    val PaddingNormalLeftCompat = 16.dp
    val PaddingNormalRightCompat = 16.dp
    val PaddingMenuLeftCompat = 4.dp
    val PaddingMenuRightCompat = 4.dp
    val PaddingCenterCompat = 4.dp

    val PaddingNormalLeftMedium = 24.dp
    val PaddingNormalRightMedium = 24.dp
    val PaddingMenuLeftMedium = 12.dp
    val PaddingMenuRightMedium = 12.dp
    val PaddingCenterMedium = 12.dp

    val PaddingNormalLeftExpanded = 40.dp
    val PaddingNormalRightExpanded = 40.dp
    val PaddingMenuLeftExpanded = 28.dp
    val PaddingMenuRightExpanded = 31.dp
    val PaddingCenterExpanded = 28.dp
}

/**
 * A top app bar that displays information and actions at the top of a screen.
 *
 * This implementation features responsive padding adaptation based on the available width
 * and precise positioning logic for navigation icons, titles, and menu actions.
 *
 * @param title The title text to be displayed.
 * @param modifier The modifier to be applied to the top app bar.
 * @param subtitle The optional subtitle text to be displayed below the title.
 * @param navigationIcon The navigation icon displayed at the start of the top app bar.
 * @param actions The actions displayed at the end of the top app bar.
 * @param windowInsets The window insets to be applied to the top app bar.
 * @param colors The color configuration for the top app bar.
 * @param scrollBehavior The scroll behavior that defines how the top app bar reacts to scrolling.
 * @param isCenterTitle Whether the title should be centered horizontally.
 */
@Composable
fun TopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = COUITopAppBarDefaults.windowInsets,
    colors: COUITopAppBarColors = COUITopAppBarDefaults.topAppBarColors(),
    scrollBehavior: ScrollBehavior? = null,
    isCenterTitle: Boolean = false
) {
    val actionsRow = @Composable {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            actions()
        }
    }

    val heightOffsetLimit = 0f
    SideEffect {
        if (scrollBehavior?.state?.heightOffsetLimit != heightOffsetLimit) {
            scrollBehavior?.state?.heightOffsetLimit = heightOffsetLimit
        }
    }

    val height = LocalDensity.current.run { COUITopAppBarDefaults.ContainerHeight.toPx() }

    Surface(modifier = modifier, color = colors.containerColor(0f)) {
        Column {
            TopAppBarLayout(
                modifier = Modifier
                    .windowInsetsPadding(windowInsets)
                    .height(COUITopAppBarDefaults.ContainerHeight)
                    .clipToBounds(),
                heightPx = height,
                navigationIconContentColor = colors.navigationIconContentColor,
                titleContentColor = colors.titleContentColor,
                actionIconContentColor = colors.actionIconContentColor,
                title = title,
                subtitle = subtitle,
                titleTextStyle = COUITheme.textStyles.title3,
                subtitleTextStyle = COUITheme.textStyles.subtitle,
                titleAlpha = 1f,
                titleVerticalArrangement = Arrangement.Center,
                isCenterTitle = isCenterTitle,
                titleBottomPadding = 0,
                navigationIcon = navigationIcon,
                actions = actionsRow,
            )

            DividerAnimation(scrollBehavior)
        }
    }
}

/**
 * A top app bar that expands to display a large title when scrolled to the top.
 *
 * @param title The title text displayed in the pinned state or as a fallback for the large title.
 * @param modifier The modifier to be applied to the top app bar.
 * @param subtitle The optional subtitle text displayed in the pinned state.
 * @param largeTitle The custom text to be displayed when expanded. Defaults to [title] if null.
 * @param navigationIcon The navigation icon displayed at the start of the top app bar.
 * @param actions The actions displayed at the end of the top app bar.
 * @param windowInsets The window insets to be applied to the top app bar.
 * @param colors The color configuration for the top app bar.
 * @param scrollBehavior The scroll behavior that defines the expansion and collapse logic.
 */
@Composable
fun LargeTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    largeTitle: String? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = COUITopAppBarDefaults.windowInsets,
    colors: COUITopAppBarColors = COUITopAppBarDefaults.topAppBarColors(),
    scrollBehavior: ScrollBehavior? = null
) {
    val density = LocalDensity.current
    val actionsRow = @Composable {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            actions()
        }
    }

    val pinnedHeightPx = with(density) { COUITopAppBarDefaults.ContainerHeight.toPx() }
    val maxHeightPx = with(density) { COUITopAppBarDefaults.LargeContainerHeight.toPx() }

    SideEffect {
        val limit = pinnedHeightPx - maxHeightPx
        if (scrollBehavior?.state?.heightOffsetLimit != limit) {
            scrollBehavior?.state?.heightOffsetLimit = limit
        }
    }

    val collapsedFraction = scrollBehavior?.state?.collapsedFraction ?: 0f
    val appBarContainerColor = colors.containerColor(collapsedFraction)

    Surface(modifier = modifier, color = appBarContainerColor) {
        Column {
            TopAppBarLayout(
                modifier = Modifier
                    .windowInsetsPadding(windowInsets)
                    .height(COUITopAppBarDefaults.ContainerHeight)
                    .clipToBounds(),
                heightPx = pinnedHeightPx,
                navigationIconContentColor = colors.navigationIconContentColor,
                titleContentColor = colors.titleContentColor,
                actionIconContentColor = colors.actionIconContentColor,
                title = title,
                subtitle = subtitle,
                titleTextStyle = COUITheme.textStyles.title3,
                subtitleTextStyle = COUITheme.textStyles.subtitle,
                titleAlpha = collapsedFraction,
                titleVerticalArrangement = Arrangement.Center,
                isCenterTitle = false,
                titleBottomPadding = 0,
                navigationIcon = navigationIcon,
                actions = actionsRow,
            )

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .clipToBounds()
            ) {
                val screenWidth = maxWidth
                val startPadding = when {
                    screenWidth < 600.dp -> COUIDimens.PaddingNormalLeftCompat
                    screenWidth < 840.dp -> COUIDimens.PaddingNormalLeftMedium
                    else -> COUIDimens.PaddingNormalLeftExpanded
                }

                Layout(
                    content = {
                        Box(
                            Modifier
                                .padding(horizontal = startPadding)
                                .graphicsLayer {
                                    alpha = (1f - (collapsedFraction * 1.5f)).coerceIn(0f, 1f)
                                    translationY = -(scrollBehavior?.state?.heightOffset ?: 0f) * 0.2f
                                }
                        ) {
                            BasicText(
                                text = largeTitle ?: title,
                                style = COUITheme.textStyles.title1.copy(
                                    color = colors.titleContentColor,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }
                    }
                ) { measurables, constraints ->
                    val placeable = measurables.first().measure(constraints)
                    val offset = scrollBehavior?.state?.heightOffset ?: 0f
                    val currentHeight = (maxHeightPx - pinnedHeightPx + offset).coerceAtLeast(0f).roundToInt()

                    layout(constraints.maxWidth, currentHeight) {
                        val y = currentHeight - placeable.height - 16.dp.roundToPx()
                        placeable.placeRelative(0, y)
                    }
                }
            }

            DividerAnimation(scrollBehavior, startAlpha = 0.8f)
        }
    }
}

@Composable
private fun DividerAnimation(scrollBehavior: ScrollBehavior?, startAlpha: Float = 0f) {
    if (scrollBehavior != null) {
        val contentOffset = scrollBehavior.state.contentOffset
        val threshold = LocalDensity.current.run { 24.dp.toPx() }
        val rawProgress = if (startAlpha > 0) {
            val collapsed = scrollBehavior.state.collapsedFraction
            (collapsed - startAlpha).coerceIn(0f, 0.2f) * 5f
        } else {
            (abs(contentOffset) / threshold).coerceIn(0f, 1f)
        }

        val progress = rawProgress.coerceIn(0f, 1f)

        if (progress > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .graphicsLayer {
                        alpha = progress
                        scaleX = 0.9f + (0.1f * progress)
                    }
                    .background(COUITheme.colorScheme.outline.copy(alpha = 0.1f))
            )
        }
    }
}

@Composable
private fun TopAppBarLayout(
    modifier: Modifier,
    heightPx: Float,
    navigationIconContentColor: Color,
    titleContentColor: Color,
    actionIconContentColor: Color,
    title: String,
    subtitle: String?,
    titleTextStyle: TextStyle,
    subtitleTextStyle: TextStyle,
    titleAlpha: Float,
    titleVerticalArrangement: Arrangement.Vertical,
    isCenterTitle: Boolean,
    titleBottomPadding: Int,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        val maxWidthDp = maxWidth

        Layout(
            {
                Box(Modifier.layoutId("navigationIcon")) {
                    CompositionLocalProvider(LocalContentColor provides navigationIconContentColor) {
                        navigationIcon()
                    }
                }
                Box(
                    Modifier
                        .layoutId("title")
                        .graphicsLayer(alpha = titleAlpha)
                ) {
                    Column(
                        verticalArrangement = titleVerticalArrangement,
                        horizontalAlignment = if (isCenterTitle) Alignment.CenterHorizontally else Alignment.Start
                    ) {
                        BasicText(
                            text = title,
                            style = titleTextStyle.copy(color = titleContentColor),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (subtitle != null) {
                            BasicText(
                                text = subtitle,
                                style = subtitleTextStyle.copy(
                                    color = titleContentColor.copy(alpha = 0.6f),
                                    fontSize = 12.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                Box(Modifier.layoutId("actionIcons")) {
                    CompositionLocalProvider(LocalContentColor provides actionIconContentColor) {
                        actions()
                    }
                }
            }
        ) { measurables, constraints ->
            val navPlaceable = measurables.fastFirst { it.layoutId == "navigationIcon" }
                .measure(constraints.copy(minWidth = 0))
            val actionsPlaceable = measurables.fastFirst { it.layoutId == "actionIcons" }
                .measure(constraints.copy(minWidth = 0))

            val hasNav = navPlaceable.width > 0
            val hasActions = actionsPlaceable.width > 0

            var paddingStartPx = 0
            var paddingEndPx = 0

            if (maxWidthDp < 600.dp) {
                paddingStartPx =
                    (if (hasNav) COUIDimens.PaddingMenuLeftCompat else COUIDimens.PaddingNormalLeftCompat).roundToPx()
                paddingEndPx =
                    (if (hasActions) COUIDimens.PaddingMenuRightCompat else COUIDimens.PaddingNormalRightCompat).roundToPx()
                if (isCenterTitle) {
                    val centerPadding = COUIDimens.PaddingCenterCompat.roundToPx()
                    paddingStartPx = centerPadding
                    paddingEndPx = centerPadding
                }
            } else if (maxWidthDp < 840.dp) {
                paddingStartPx =
                    (if (hasNav) COUIDimens.PaddingMenuLeftMedium else COUIDimens.PaddingNormalLeftMedium).roundToPx()
                paddingEndPx =
                    (if (hasActions) COUIDimens.PaddingMenuRightMedium else COUIDimens.PaddingNormalRightMedium).roundToPx()
                if (isCenterTitle) {
                    val centerPadding = COUIDimens.PaddingCenterMedium.roundToPx()
                    paddingStartPx = centerPadding
                    paddingEndPx = centerPadding
                }
            } else {
                paddingStartPx =
                    (if (hasNav) COUIDimens.PaddingMenuLeftExpanded else COUIDimens.PaddingNormalLeftExpanded).roundToPx()
                paddingEndPx =
                    (if (hasActions) COUIDimens.PaddingMenuRightExpanded else COUIDimens.PaddingNormalRightExpanded).roundToPx()
                if (isCenterTitle) {
                    val centerPadding = COUIDimens.PaddingCenterExpanded.roundToPx()
                    paddingStartPx = centerPadding
                    paddingEndPx = centerPadding
                }
            }

            val gapNavTitle = if (hasNav) COUIDimens.GapNavTitle.roundToPx() else 0
            val gapTitleActions = if (hasActions) COUIDimens.GapTitleMenu.roundToPx() else 0

            val maxTitleWidth = (constraints.maxWidth
                    - paddingStartPx
                    - navPlaceable.width
                    - gapNavTitle
                    - gapTitleActions
                    - actionsPlaceable.width
                    - paddingEndPx).coerceAtLeast(0)

            val titlePlaceable = measurables.fastFirst { it.layoutId == "title" }
                .measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth))

            val layoutHeight = heightPx.roundToInt()

            layout(constraints.maxWidth, layoutHeight) {
                val navY = (layoutHeight - navPlaceable.height) / 2
                navPlaceable.placeRelative(x = paddingStartPx, y = navY)

                val actionsY = (layoutHeight - actionsPlaceable.height) / 2
                val actionsX = constraints.maxWidth - paddingEndPx - actionsPlaceable.width
                actionsPlaceable.placeRelative(x = actionsX, y = actionsY)

                val titleY = (layoutHeight - titlePlaceable.height) / 2 - titleBottomPadding
                var titleX: Int

                val navEndLimit = paddingStartPx + navPlaceable.width + gapNavTitle
                val actionsStartLimit = actionsX - gapTitleActions

                if (isCenterTitle) {
                    titleX = (constraints.maxWidth - titlePlaceable.width) / 2

                    if (titleX < navEndLimit) {
                        titleX = navEndLimit
                    }
                    if (titleX + titlePlaceable.width > actionsStartLimit) {
                        titleX = actionsStartLimit - titlePlaceable.width
                    }
                    if (titleX < navEndLimit) {
                        titleX = navEndLimit
                    }
                } else {
                    titleX = navEndLimit
                }

                titlePlaceable.placeRelative(x = titleX, y = titleY)
            }
        }
    }
}

/**
 * A behavior that defines how the top app bar should react to scrolling.
 */
@Stable
interface ScrollBehavior {
    val state: TopAppBarState
    val snapAnimationSpec: AnimationSpec<Float>?
    val flingAnimationSpec: DecayAnimationSpec<Float>?
    val nestedScrollConnection: NestedScrollConnection
}

/**
 * A state object to be hoisted to control and observe the top app bar state.
 */
@Stable
class TopAppBarState(
    initialHeightOffsetLimit: Float,
    initialHeightOffset: Float,
    initialContentOffset: Float
) {
    var heightOffsetLimit by mutableFloatStateOf(initialHeightOffsetLimit)

    var heightOffset: Float
        get() = _heightOffset.floatValue
        set(newOffset) {
            _heightOffset.floatValue = newOffset.coerceIn(
                minimumValue = heightOffsetLimit,
                maximumValue = 0f
            )
        }

    var contentOffset by mutableFloatStateOf(initialContentOffset)

    val collapsedFraction: Float
        get() = if (heightOffsetLimit != 0f) {
            heightOffset / heightOffsetLimit
        } else {
            0f
        }

    companion object {
        val Saver: Saver<TopAppBarState, *> = listSaver(
            save = { listOf(it.heightOffsetLimit, it.heightOffset, it.contentOffset) },
            restore = {
                TopAppBarState(
                    initialHeightOffsetLimit = it[0],
                    initialHeightOffset = it[1],
                    initialContentOffset = it[2]
                )
            }
        )
    }

    private var _heightOffset = mutableFloatStateOf(initialHeightOffset)
}

/**
 * Remembers the [TopAppBarState] for the top app bar.
 */
@Composable
fun rememberTopAppBarState(
    initialHeightOffsetLimit: Float = -Float.MAX_VALUE,
    initialHeightOffset: Float = 0f,
    initialContentOffset: Float = 0f
): TopAppBarState {
    return rememberSaveable(saver = TopAppBarState.Saver) {
        TopAppBarState(initialHeightOffsetLimit, initialHeightOffset, initialContentOffset)
    }
}

/**
 * Returns a [ScrollBehavior] that adjusts the top app bar's height and alpha based on scroll events.
 */
@Composable
fun topAppBarScrollBehavior(
    state: TopAppBarState = rememberTopAppBarState(),
    canScroll: () -> Boolean = { true },
    snapAnimationSpec: AnimationSpec<Float>? = spring(stiffness = 2500f),
    flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay()
): ScrollBehavior = remember(state, canScroll, snapAnimationSpec, flingAnimationSpec) {
    ExitUntilCollapsedScrollBehavior(
        state = state,
        snapAnimationSpec = snapAnimationSpec,
        flingAnimationSpec = flingAnimationSpec,
        canScroll = canScroll
    )
}

private class ExitUntilCollapsedScrollBehavior(
    override val state: TopAppBarState,
    override val snapAnimationSpec: AnimationSpec<Float>?,
    override val flingAnimationSpec: DecayAnimationSpec<Float>?,
    val canScroll: () -> Boolean
) : ScrollBehavior {
    override val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (!canScroll()) return Offset.Zero

            val prevHeightOffset = state.heightOffset
            state.heightOffset = state.heightOffset + available.y

            return if (prevHeightOffset != state.heightOffset) {
                available.copy(x = 0f)
            } else {
                Offset.Zero
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            if (!canScroll()) return Offset.Zero
            state.contentOffset += consumed.y

            if (available.y < 0f || consumed.y < 0f) {
                val oldHeightOffset = state.heightOffset
                state.heightOffset = state.heightOffset + consumed.y
                return Offset(0f, state.heightOffset - oldHeightOffset)
            }
            if (available.y > 0f) {
                val oldHeightOffset = state.heightOffset
                state.heightOffset = state.heightOffset + available.y
                return Offset(0f, state.heightOffset - oldHeightOffset)
            }
            return Offset.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            val superConsumed = super.onPostFling(consumed, available)
            return superConsumed + settleAppBar(state, available.y, flingAnimationSpec, snapAnimationSpec)
        }
    }
}

private suspend fun settleAppBar(
    state: TopAppBarState,
    velocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
    snapAnimationSpec: AnimationSpec<Float>?
): Velocity {
    if (state.collapsedFraction < 0.01f || state.collapsedFraction == 1f) {
        return Velocity.Zero
    }
    var remainingVelocity = velocity
    if (flingAnimationSpec != null && abs(velocity) > 1f) {
        var lastValue = 0f
        AnimationState(initialValue = 0f, initialVelocity = velocity)
            .animateDecay(flingAnimationSpec) {
                val delta = value - lastValue
                val initialHeightOffset = state.heightOffset
                state.heightOffset = initialHeightOffset + delta
                val consumed = abs(initialHeightOffset - state.heightOffset)
                lastValue = value
                remainingVelocity = this.velocity
                if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
            }
    }
    if (snapAnimationSpec != null) {
        if (state.heightOffset < 0 && state.heightOffset > state.heightOffsetLimit) {
            AnimationState(initialValue = state.heightOffset).animateTo(
                if (state.collapsedFraction < 0.5f) 0f else state.heightOffsetLimit,
                animationSpec = snapAnimationSpec
            ) {
                state.heightOffset = value
            }
        }
    }
    return Velocity(0f, velocity - remainingVelocity)
}

@Composable
fun Surface(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.background(color = color)
    ) {
        content()
    }
}

val LocalContentColor = compositionLocalOf { Color.Black }

@Stable
class COUITopAppBarColors(
    val containerColor: Color,
    val scrolledContainerColor: Color,
    val navigationIconContentColor: Color,
    val titleContentColor: Color,
    val actionIconContentColor: Color,
) {
    @Composable
    fun containerColor(colorTransitionFraction: Float): Color {
        return lerp(
            containerColor,
            scrolledContainerColor,
            colorTransitionFraction.coerceIn(0f, 1f)
        )
    }

    private fun lerp(start: Color, stop: Color, fraction: Float): Color {
        return androidx.compose.ui.graphics.lerp(start, stop, fraction)
    }
}

object COUITopAppBarDefaults {
    val ContainerHeight = 56.dp
    val LargeContainerHeight = 116.dp

    val windowInsets: WindowInsets
        @Composable
        get() {
            val density = LocalDensity.current
            val topPx = WindowInsets.statusBars.getTop(density)
            return WindowInsets(
                left = 0,
                top = topPx,
                right = 0,
                bottom = 0
            )
        }

    @Composable
    fun topAppBarColors(
        containerColor: Color = COUITheme.colorScheme.background,
        scrolledContainerColor: Color = COUITheme.colorScheme.surface,
        navigationIconContentColor: Color = COUITheme.colorScheme.onBackground,
        titleContentColor: Color = COUITheme.colorScheme.onBackground,
        actionIconContentColor: Color = COUITheme.colorScheme.onBackground
    ): COUITopAppBarColors =
        COUITopAppBarColors(
            containerColor,
            scrolledContainerColor,
            navigationIconContentColor,
            titleContentColor,
            actionIconContentColor
        )
}