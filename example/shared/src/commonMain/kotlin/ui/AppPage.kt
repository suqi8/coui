// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalScrollBarApi::class)

package ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import app.LocalAppPadding
import app.LocalAppSettings
import app.LocalHostBottomPadding
import app.LocalInDetailPane
import app.LocalIsWideScreen
import io.github.suqi8.coui.kmp.basic.COUIScrollBehavior
import io.github.suqi8.coui.kmp.basic.FabPosition
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.ScrollBehavior
import io.github.suqi8.coui.kmp.basic.SmallTopAppBar
import io.github.suqi8.coui.kmp.basic.SnackbarHost
import io.github.suqi8.coui.kmp.basic.SnackbarHostState
import io.github.suqi8.coui.kmp.basic.ToolbarPosition
import io.github.suqi8.coui.kmp.basic.TopAppBar
import io.github.suqi8.coui.kmp.basic.TopAppBarDefaults
import io.github.suqi8.coui.kmp.basic.TopAppBarSearchState
import io.github.suqi8.coui.kmp.basic.VerticalScrollBar
import io.github.suqi8.coui.kmp.basic.glowScrollOffsetPx
import io.github.suqi8.coui.kmp.basic.rememberScrollBarAdapter
import io.github.suqi8.coui.kmp.blur.LayerBackdrop
import io.github.suqi8.coui.kmp.blur.TopAppBarGlow
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.LocalPopupMaterialBlur
import io.github.suqi8.coui.kmp.interfaces.ExperimentalScrollBarApi
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.overScrollVertical
import io.github.suqi8.coui.kmp.utils.rememberCOUIFlingBehavior
import io.github.suqi8.coui.kmp.utils.scrollEndHaptic
import utils.LocalPageBackdrop
import utils.rememberBlurBackdrop
import utils.rememberMaterialBackdropProvider
import utils.rememberPopupMaterialBlur

/**
 * The top bar layouts an [AppPageScaffold] can use.
 */
enum class AppPageTopBarStyle {
    /** A collapsing large title on narrow windows, a pinned small bar on wide ones. */
    Large,

    /** Always the pinned small bar. */
    Small,

    /** The COUI 17 settings home bar: a 116dp large title at 40sp that collapses into the small title. */
    SettingsHome,
}

/**
 * What the content of an [AppPageScaffold] needs to lay itself out under the page chrome.
 *
 * @property contentPadding The padding that keeps the content clear of the top bar, the bottom
 *   chrome and the window insets. Already includes the 12dp gap below the top bar.
 * @property scrollBehavior The top bar's scroll behavior; connect it with [appPageScroll].
 * @property backdrop The page backdrop recording this page, or null when the blur is off.
 */
@Stable
class AppPageContentScope(
    val contentPadding: PaddingValues,
    val scrollBehavior: ScrollBehavior,
    val backdrop: LayerBackdrop?,
)

/**
 * The scroll modifiers every page list uses: the scroll-end haptic (when enabled in the settings),
 * the COUI over-scroll and the top bar's nested scroll.
 */
@Composable
fun Modifier.appPageScroll(scope: AppPageContentScope): Modifier {
    val haptic = LocalAppSettings.current.enableScrollEndHaptic
    return this
        .then(if (haptic) Modifier.scrollEndHaptic() else Modifier)
        .overScrollVertical()
        .nestedScroll(scope.scrollBehavior.nestedScrollConnection)
        .fillMaxHeight()
}

/**
 * The page scaffold shared by every page of the example app.
 *
 * It records the page content into a page backdrop and provides it as [LocalPageBackdrop] and
 * [LocalPopupMaterialBlur], so dialogs, bottom sheets and menus opened from this page blur it with
 * the COUI 17 material, whether the page is a main tab or pushed onto the back stack. The bottom
 * bar, floating action button and floating toolbar sit outside the recording and get the page
 * backdrop through [LocalMaterialBackdrop], so their material can blur the page as well.
 *
 * @param title The page title.
 * @param modifier The modifier for the scaffold.
 * @param subtitle The top bar subtitle.
 * @param onBack Shows a back button that calls this; hidden inside the wide-screen detail pane.
 * @param navigationIconModifier The modifier for the back button.
 * @param topBarStyle The top bar layout.
 * @param showTopBar Whether the top bar is shown at all.
 * @param titleColor The title colour; [Color.Unspecified] keeps the theme's.
 * @param actions The top bar actions.
 * @param topBarBottomContent Content docked below the top bar title, such as a search slot.
 * @param topBarContainer Wraps the top bar, for demos that anchor something to it.
 * @param topBarSearchState Links the top bar to a search bar in [topBarBottomContent] (large bars only).
 * @param snackbarHostState Shows a snackbar host for this state.
 * @param bottomBar The bottom bar.
 * @param floatingActionButton The floating action button.
 * @param floatingActionButtonPosition Where the floating action button sits.
 * @param floatingToolbar The floating toolbar.
 * @param floatingToolbarPosition Where the floating toolbar sits.
 * @param content The page content.
 */
@Composable
fun AppPageScaffold(
    title: String,
    modifier: Modifier = Modifier,
    navigationIconModifier: Modifier = Modifier,
    subtitle: String = "",
    onBack: (() -> Unit)? = null,
    topBarStyle: AppPageTopBarStyle = AppPageTopBarStyle.Large,
    showTopBar: Boolean = true,
    titleColor: Color = Color.Unspecified,
    actions: @Composable RowScope.() -> Unit = {},
    topBarBottomContent: @Composable () -> Unit = {},
    topBarContainer: @Composable (topBar: @Composable () -> Unit) -> Unit = { topBar -> topBar() },
    topBarSearchState: TopAppBarSearchState? = null,
    snackbarHostState: SnackbarHostState? = null,
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    floatingToolbar: @Composable () -> Unit = {},
    floatingToolbarPosition: ToolbarPosition = ToolbarPosition.BottomCenter,
    content: @Composable (AppPageContentScope) -> Unit,
) {
    val backdrop = rememberBlurBackdrop()
    val scrollBehavior = COUIScrollBehavior()
    val materialBackdrop = rememberMaterialBackdropProvider(backdrop)
    val popupMaterialBlur = rememberPopupMaterialBlur(backdrop)

    Scaffold(
        modifier = modifier,
        topBar = {
            if (showTopBar) {
                topBarContainer {
                    AppPageTopBar(
                        title = title,
                        subtitle = subtitle,
                        onBack = onBack,
                        navigationIconModifier = navigationIconModifier,
                        style = topBarStyle,
                        scrollBehavior = scrollBehavior,
                        backdrop = backdrop,
                        titleColor = titleColor,
                        actions = actions,
                        searchState = topBarSearchState,
                        bottomContent = topBarBottomContent,
                    )
                }
            }
        },
        bottomBar = {
            CompositionLocalProvider(
                LocalPageBackdrop provides backdrop,
                LocalMaterialBackdrop provides materialBackdrop,
            ) { bottomBar() }
        },
        floatingActionButton = {
            CompositionLocalProvider(
                LocalPageBackdrop provides backdrop,
                LocalMaterialBackdrop provides materialBackdrop,
            ) { floatingActionButton() }
        },
        floatingActionButtonPosition = floatingActionButtonPosition,
        floatingToolbar = {
            CompositionLocalProvider(
                LocalPageBackdrop provides backdrop,
                LocalMaterialBackdrop provides materialBackdrop,
            ) { floatingToolbar() }
        },
        floatingToolbarPosition = floatingToolbarPosition,
        snackbarHost = {
            if (snackbarHostState != null) SnackbarHost(state = snackbarHostState)
        },
    ) { innerPadding ->
        val contentPadding = appPageContentPadding(innerPadding, showTopBar)
        val scope = remember(contentPadding, scrollBehavior, backdrop) {
            AppPageContentScope(contentPadding, scrollBehavior, backdrop)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier),
        ) {
            CompositionLocalProvider(
                LocalPageBackdrop provides backdrop,
                LocalPopupMaterialBlur provides popupMaterialBlur,
            ) {
                content(scope)
            }
        }
    }
}

/**
 * An [AppPageScaffold] whose content is a lazy list with the shared page scroll behaviour, a
 * scroll bar and a 32dp tail gap.
 *
 * @param title The page title.
 * @param modifier The modifier for the scaffold.
 * @param subtitle The top bar subtitle.
 * @param onBack Shows a back button that calls this.
 * @param topBarStyle The top bar layout.
 * @param actions The top bar actions.
 * @param topBarBottomContent Content docked below the top bar title.
 * @param snackbarHostState Shows a snackbar host for this state.
 * @param listState The list state.
 * @param content The list content.
 */
@Composable
fun AppPage(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    onBack: (() -> Unit)? = null,
    topBarStyle: AppPageTopBarStyle = AppPageTopBarStyle.Large,
    actions: @Composable RowScope.() -> Unit = {},
    topBarBottomContent: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState? = null,
    listState: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit,
) {
    AppPageScaffold(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        onBack = onBack,
        topBarStyle = topBarStyle,
        actions = actions,
        topBarBottomContent = topBarBottomContent,
        snackbarHostState = snackbarHostState,
    ) { scope ->
        AppPageList(scope = scope, listState = listState, content = content)
    }
}

/**
 * The lazy list [AppPage] draws, for pages that build on [AppPageScaffold] directly.
 */
@Composable
fun AppPageList(
    scope: AppPageContentScope,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.appPageScroll(scope),
            contentPadding = scope.contentPadding,
            // COUI lists fling through SpringOverScroller, which runs longer than Compose's spline.
            flingBehavior = rememberCOUIFlingBehavior(),
        ) {
            content()
            item(key = "app_page_tail") { Spacer(Modifier.height(32.dp)) }
        }
        VerticalScrollBar(
            adapter = rememberScrollBarAdapter(listState),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            trackPadding = scope.contentPadding,
        )
    }
}

/**
 * The top bar of an [AppPageScaffold], with the COUI 17 condensed-light glow when the page is
 * recorded into [backdrop].
 */
@Composable
fun AppPageTopBar(
    title: String,
    scrollBehavior: ScrollBehavior,
    backdrop: LayerBackdrop?,
    modifier: Modifier = Modifier,
    navigationIconModifier: Modifier = Modifier,
    subtitle: String = "",
    onBack: (() -> Unit)? = null,
    style: AppPageTopBarStyle = AppPageTopBarStyle.Large,
    titleColor: Color = Color.Unspecified,
    actions: @Composable RowScope.() -> Unit = {},
    searchState: TopAppBarSearchState? = null,
    bottomContent: @Composable () -> Unit = {},
) {
    val colors = COUITheme.colorScheme
    val isWide = LocalIsWideScreen.current
    val inDetailPane = LocalInDetailPane.current
    val barColor = if (backdrop != null) Color.Transparent else colors.backgroundWithCard
    val resolvedTitleColor = if (titleColor == Color.Unspecified) colors.labelPrimary else titleColor
    // A null glow keeps the bar solid and its divider on; with the glow set the bar suppresses its
    // divider like COUI 17 (AppBarBlurHelper.syncAppBarDividerWithMaterialBlur).
    val glow: (@Composable BoxScope.() -> Unit)? = if (backdrop != null) {
        {
            TopAppBarGlow(
                backdrop = backdrop,
                scrollOffsetPx = scrollBehavior.glowScrollOffsetPx(),
                isDark = colors.isDark,
                baseColor = colors.backgroundWithCard,
                enabled = true,
            )
        }
    } else {
        null
    }
    val navigationIcon: @Composable () -> Unit = {
        if (onBack != null && !inDetailPane) BackNavigationIcon(onClick = onBack, modifier = navigationIconModifier)
    }
    val small = style == AppPageTopBarStyle.Small || inDetailPane || (isWide && style == AppPageTopBarStyle.Large)
    if (small) {
        SmallTopAppBar(
            title = title,
            modifier = modifier,
            subtitle = subtitle,
            color = barColor,
            titleColor = resolvedTitleColor,
            scrollBehavior = scrollBehavior,
            defaultWindowInsetsPadding = !isWide && !inDetailPane,
            glow = glow,
            navigationIcon = navigationIcon,
            actions = actions,
            bottomContent = bottomContent,
        )
    } else {
        val settingsHome = style == AppPageTopBarStyle.SettingsHome
        TopAppBar(
            title = title,
            modifier = modifier,
            subtitle = subtitle,
            color = barColor,
            titleColor = resolvedTitleColor,
            largeTitleColor = resolvedTitleColor,
            scrollBehavior = scrollBehavior,
            // The COUI 17 settings home bar (SettingsHomeTopBar measurements): 116dp tall, a 40sp
            // title with 24dp side and 18dp bottom margins. Like the other pages it morphs into the
            // pinned small title as it collapses, with the search slot pinned below it.
            expandedHeight = if (settingsHome) 116.dp else TopAppBarDefaults.ExpandedHeight,
            largeTitleFontSize = if (settingsHome) 40.sp else TopAppBarDefaults.largeTitleFontSize(),
            titlePadding = if (settingsHome) 24.dp else Dp.Unspecified,
            largeTitleBottomMargin = if (settingsHome) 18.dp else Dp.Unspecified,
            glow = glow,
            navigationIcon = navigationIcon,
            actions = actions,
            searchState = searchState,
            bottomContent = bottomContent,
        )
    }
}

/**
 * Resolves the content padding of a page from its scaffold's inner padding, the bottom chrome of
 * the host and the platform padding.
 */
@Composable
private fun appPageContentPadding(innerPadding: PaddingValues, showTopBar: Boolean): PaddingValues {
    val hostBottom = LocalHostBottomPadding.current
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val appBottom = LocalAppPadding.current.calculateBottomPadding()
    val topGap = if (showTopBar) 12.dp else 0.dp
    return remember(innerPadding, topGap, hostBottom, navBottom, appBottom) {
        AppPageContentPadding(innerPadding, topGap, hostBottom, navBottom, appBottom)
    }
}

/**
 * The page content padding, read from the scaffold padding while the content lays out, so the list
 * moves in the same frame as the collapsing top bar instead of trailing it by a recomposition.
 */
@Stable
private class AppPageContentPadding(
    private val innerPadding: PaddingValues,
    private val topGap: Dp,
    private val hostBottom: Dp,
    private val navBottom: Dp,
    private val appBottom: Dp,
) : PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp = innerPadding.calculateLeftPadding(layoutDirection)

    override fun calculateTopPadding(): Dp = innerPadding.calculateTopPadding() + topGap

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp = innerPadding.calculateRightPadding(layoutDirection)

    override fun calculateBottomPadding(): Dp = max(max(innerPadding.calculateBottomPadding(), hostBottom), navBottom) + appBottom
}
