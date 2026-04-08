// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.AdaptiveNavigationScaffold
import com.suqi8.coui.kmp.basic.FloatingActionButton
import com.suqi8.coui.kmp.basic.Icon
import com.suqi8.coui.kmp.basic.IconButton
import com.suqi8.coui.kmp.basic.NavigationBar
import com.suqi8.coui.kmp.basic.NavigationItem
import com.suqi8.coui.kmp.basic.NavigationRail
import com.suqi8.coui.kmp.basic.ScrollBehavior
import com.suqi8.coui.kmp.basic.LargeTopAppBar
import com.suqi8.coui.kmp.basic.TopAppBar
import com.suqi8.coui.kmp.basic.topAppBarScrollBehavior
import com.suqi8.coui.kmp.icon.MiuixIcons
import com.suqi8.coui.kmp.icon.icons.other.GitHub
import com.suqi8.coui.kmp.icon.icons.useful.Delete
import com.suqi8.coui.kmp.icon.icons.useful.Edit
import com.suqi8.coui.kmp.icon.icons.useful.ImmersionMore
import com.suqi8.coui.kmp.icon.icons.useful.NavigatorSwitch
import com.suqi8.coui.kmp.icon.icons.useful.Order
import com.suqi8.coui.kmp.icon.icons.useful.Scan
import com.suqi8.coui.kmp.icon.icons.useful.Settings
import com.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import utils.FPSMonitor

private object UIConstants {
    val WIDE_SCREEN_THRESHOLD = 840.dp
    const val PAGE_COUNT = 4
    const val GITHUB_URL = "https://github.com/compose-miuix-ui/miuix"
    val PAGE_TITLES = listOf("Components", "Dropdowns", "Colors", "Settings")
    val PAGE_SUBTITLES = listOf("Basic controls", "Selection lists", "Theme palette", "Demo options")
}

data class UIState(
    val showFPSMonitor: Boolean = false,
    val showTopAppBar: Boolean = true,
    val showNavigationBar: Boolean = true,
    val showFloatingActionButton: Boolean = false,
    val enablePageUserScroll: Boolean = false,
    val scrollEndHaptic: Boolean = true,
    val isWideScreen: Boolean = false,
)

val LocalPagerState = compositionLocalOf<PagerState> { error("No pager state") }
val LocalHandlePageChange = compositionLocalOf<(Int) -> Unit> { error("No handle page change") }

@Composable
fun UITest(
    colorMode: MutableState<Int>,
) {
    val topAppBarScrollBehaviorList = List(UIConstants.PAGE_COUNT) { topAppBarScrollBehavior() }
    val pagerState = rememberPagerState(pageCount = { UIConstants.PAGE_COUNT })
    val coroutineScope = rememberCoroutineScope()

    val navigationItems = listOf(
        NavigationItem(UIConstants.PAGE_TITLES[0], rememberVectorPainter(MiuixIcons.Useful.NavigatorSwitch)),
        NavigationItem(UIConstants.PAGE_TITLES[1], rememberVectorPainter(MiuixIcons.Useful.Order)),
        NavigationItem(UIConstants.PAGE_TITLES[2], rememberVectorPainter(MiuixIcons.Useful.Scan)),
        NavigationItem(UIConstants.PAGE_TITLES[3], rememberVectorPainter(MiuixIcons.Useful.Settings))
    )

    var uiState by remember { mutableStateOf(UIState()) }
    val handlePageChange: (Int) -> Unit = remember(pagerState, coroutineScope, uiState.isWideScreen) {
        { page ->
            coroutineScope.launch {
                if (uiState.isWideScreen) pagerState.scrollToPage(page)
                else pagerState.animateScrollToPage(page)
            }
        }
    }

    CompositionLocalProvider(
        LocalPagerState provides pagerState,
        LocalHandlePageChange provides handlePageChange
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideScreen = maxWidth > UIConstants.WIDE_SCREEN_THRESHOLD
            uiState = uiState.copy(
                isWideScreen = isWideScreen,
                showNavigationBar = !isWideScreen
            )

            ClockNavigationDemo(
                navigationItems = navigationItems,
                topAppBarScrollBehaviorList = topAppBarScrollBehaviorList,
                uiState = uiState,
                onUiStateChange = { uiState = it },
                colorMode = colorMode
            )
        }
    }

    AnimatedVisibility(
        visible = uiState.showFPSMonitor,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        FPSMonitor(modifier = Modifier.windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Top)))
    }
}

@Composable
private fun ClockNavigationDemo(
    navigationItems: List<NavigationItem>,
    topAppBarScrollBehaviorList: List<ScrollBehavior>,
    uiState: UIState,
    onUiStateChange: (UIState) -> Unit,
    colorMode: MutableState<Int>,
) {
    val currentPage = LocalPagerState.current.currentPage
    val currentScrollBehavior = topAppBarScrollBehaviorList[currentPage]
    AdaptiveNavigationScaffold(
        isWideScreen = uiState.isWideScreen,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AnimatedVisibility(
                visible = uiState.showTopAppBar,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                when (currentPage) {
                    1, 2 -> LargeTopAppBar(
                        title = UIConstants.PAGE_TITLES[currentPage],
                        subtitle = if (currentPage == 2) UIConstants.PAGE_SUBTITLES[currentPage] else null,
                        largeTitle = UIConstants.PAGE_TITLES[currentPage],
                        scrollBehavior = currentScrollBehavior,
                        actions = { TopAppBarActions() }
                    )
                    else -> TopAppBar(
                        title = UIConstants.PAGE_TITLES[currentPage],
                        subtitle = if (currentPage == 3) null else UIConstants.PAGE_SUBTITLES[currentPage],
                        scrollBehavior = currentScrollBehavior,
                        actions = { TopAppBarActions() }
                    )
                }
            }
        },
        navigationBar = {
            if (uiState.showNavigationBar) {
                NavigationBar(
                    items = navigationItems,
                    selectedIndex = currentPage,
                    onItemSelected = LocalHandlePageChange.current,
                )
            }
        },
        navigationRail = {
            if (uiState.isWideScreen) {
                NavigationRail(
                    items = navigationItems,
                    selectedIndex = currentPage,
                    onItemSelected = LocalHandlePageChange.current,
                )
            }
        }
    ) { padding ->
        AppPager(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)),
            topAppBarScrollBehaviorList = topAppBarScrollBehaviorList,
            padding = padding,
            uiState = uiState,
            onUiStateChange = onUiStateChange,
            colorMode = colorMode,
        )
    }
}

@Composable
private fun GithubFloatingActionButton(show: Boolean) {
    AnimatedVisibility(
        visible = show,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val uriHandler = LocalUriHandler.current
        FloatingActionButton(
            onClick = { uriHandler.openUri(UIConstants.GITHUB_URL) }
        ) {
            Icon(
                imageVector = MiuixIcons.Other.GitHub,
                contentDescription = "GitHub"
            )
        }
    }
}

@Composable
private fun TopAppBarActions() {
    IconButton(onClick = {}) {
        Icon(
            imageVector = MiuixIcons.Useful.ImmersionMore,
            tint = COUITheme.colorScheme.onBackground,
            contentDescription = "More"
        )
    }
}

@Composable
fun AppPager(
    modifier: Modifier = Modifier,
    topAppBarScrollBehaviorList: List<ScrollBehavior>,
    padding: PaddingValues,
    uiState: UIState,
    onUiStateChange: (UIState) -> Unit,
    colorMode: MutableState<Int>
) {
    HorizontalPager(
        state = LocalPagerState.current,
        modifier = modifier,
        userScrollEnabled = uiState.enablePageUserScroll,
        beyondViewportPageCount = 1,
        overscrollEffect = null,
    ) { page ->
        when (page) {
            0 -> MainPage(
                topAppBarScrollBehavior = topAppBarScrollBehaviorList[0],
                padding = padding,
                scrollEndHaptic = uiState.scrollEndHaptic,
            )
            1 -> SecondPage(
                topAppBarScrollBehavior = topAppBarScrollBehaviorList[1],
                padding = padding,
                scrollEndHaptic = uiState.scrollEndHaptic,
            )
            2 -> ThirdPage(
                topAppBarScrollBehavior = topAppBarScrollBehaviorList[2],
                padding = padding,
                scrollEndHaptic = uiState.scrollEndHaptic,
            )
            else -> FourthPage(
                topAppBarScrollBehavior = topAppBarScrollBehaviorList[3],
                padding = padding,
                showFPSMonitor = uiState.showFPSMonitor,
                onShowFPSMonitorChange = { onUiStateChange(uiState.copy(showFPSMonitor = it)) },
                showTopAppBar = uiState.showTopAppBar,
                onShowTopAppBarChange = { onUiStateChange(uiState.copy(showTopAppBar = it)) },
                showNavigationBar = uiState.showNavigationBar,
                onShowNavigationBarChange = { onUiStateChange(uiState.copy(showNavigationBar = it)) },
                useFloatingNavigationBar = false,
                onUseFloatingNavigationBarChange = {},
                floatingNavigationBarMode = 0,
                onFloatingNavigationBarModeChange = {},
                floatingNavigationBarPosition = 0,
                onFloatingNavigationBarPositionChange = {},
                showFloatingToolbar = false,
                onShowFloatingToolbarChange = {},
                floatingToolbarPosition = 0,
                onFloatingToolbarPositionChange = {},
                floatingToolbarOrientation = 0,
                onFloatingToolbarOrientationChange = {},
                showFloatingActionButton = uiState.showFloatingActionButton,
                onShowFloatingActionButtonChange = { onUiStateChange(uiState.copy(showFloatingActionButton = it)) },
                fabPosition = 2,
                onFabPositionChange = {},
                enablePageUserScroll = uiState.enablePageUserScroll,
                onEnablePageUserScrollChange = { onUiStateChange(uiState.copy(enablePageUserScroll = it)) },
                scrollEndHaptic = uiState.scrollEndHaptic,
                onScrollEndHapticChange = { onUiStateChange(uiState.copy(scrollEndHaptic = it)) },
                isWideScreen = uiState.isWideScreen,
                colorMode = colorMode,
            )
        }
    }
}
