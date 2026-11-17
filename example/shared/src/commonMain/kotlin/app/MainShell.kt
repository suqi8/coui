// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import catalog.ComponentsTab
import io.github.suqi8.coui.kmp.basic.NavigationRail
import io.github.suqi8.coui.kmp.basic.NavigationRailItem
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.GridView
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.icon.extended.Theme
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.app_tab_components
import io.github.suqi8.coui.kmp.shared.generated.resources.app_tab_design
import io.github.suqi8.coui.kmp.shared.generated.resources.app_tab_settings
import navigation3.Navigator
import navigation3.Route
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import page.DesignPage
import page.SettingsPage
import utils.rememberBlurBackdrop

/**
 * The tabs of the main shell, in bar order.
 */
enum class MainTab(val label: StringResource) {
    Components(Res.string.app_tab_components),
    Design(Res.string.app_tab_design),
    Settings(Res.string.app_tab_settings),
    ;

    val icon: ImageVector
        get() = when (this) {
            Components -> COUIIcons.GridView
            Design -> COUIIcons.Theme
            Settings -> COUIIcons.Settings
        }
}

/**
 * The three-tab main shell: a floating navigation bar on narrow windows, a navigation rail on wide
 * ones, and the tab pages in a pager between them.
 */
@Composable
fun MainShell() {
    val pagerState = rememberPagerState(pageCount = { MainTab.entries.size })
    val mainPagerState = rememberMainPagerState(pagerState)
    LaunchedEffect(mainPagerState.pagerState.currentPage) {
        mainPagerState.syncPage()
    }
    MainScreenBackHandler(mainPagerState, LocalNavigator.current)

    if (LocalIsWideScreen.current) {
        WideShell(mainPagerState)
    } else {
        CompactShell(mainPagerState)
    }
}

@Composable
private fun CompactShell(mainPagerState: MainPagerState) {
    // The shell records the pager so the floating bar's material can blur the pages under it.
    val backdrop = rememberBlurBackdrop()
    val layoutDirection = LocalLayoutDirection.current
    val appPadding = LocalAppPadding.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            MainBottomBar(
                selected = mainPagerState.selectedPage,
                onSelect = mainPagerState::animateToPage,
                backdrop = backdrop,
                modifier = Modifier.padding(bottom = appPadding.calculateBottomPadding()),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier),
        ) {
            CompositionLocalProvider(LocalHostBottomPadding provides innerPadding.calculateBottomPadding()) {
                MainPager(
                    mainPagerState = mainPagerState,
                    modifier = Modifier
                        .padding(
                            top = appPadding.calculateTopPadding(),
                            start = appPadding.calculateStartPadding(layoutDirection),
                            end = appPadding.calculateEndPadding(layoutDirection),
                        )
                        .imePadding(),
                )
            }
        }
    }
}

@Composable
private fun WideShell(mainPagerState: MainPagerState) {
    Row(Modifier.fillMaxSize()) {
        NavigationRail {
            MainTab.entries.forEachIndexed { index, tab ->
                NavigationRailItem(
                    selected = mainPagerState.selectedPage == index,
                    onClick = { mainPagerState.animateToPage(index) },
                    icon = tab.icon,
                    label = stringResource(tab.label),
                )
            }
        }
        MainPager(
            mainPagerState = mainPagerState,
            modifier = Modifier.weight(1f).imePadding(),
        )
    }
}

@Composable
private fun MainPager(
    mainPagerState: MainPagerState,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        state = mainPagerState.pagerState,
        modifier = modifier,
        verticalAlignment = Alignment.Top,
        // Each tab is a full page scaffold; keep the neighbours composed for a smooth swipe.
        beyondViewportPageCount = 1,
    ) { page ->
        when (MainTab.entries[page]) {
            MainTab.Components -> ComponentsTab()
            MainTab.Design -> DesignPage()
            MainTab.Settings -> SettingsPage()
        }
    }
}

/**
 * Sends the system back gesture to the first tab before it leaves the app.
 */
@Composable
private fun MainScreenBackHandler(
    mainState: MainPagerState,
    navigator: Navigator,
) {
    val isPagerBackHandlerEnabled by remember {
        derivedStateOf {
            navigator.current() is Route.Main && navigator.backStackSize() == 1 && mainState.selectedPage != 0
        }
    }

    val navEventState = rememberNavigationEventState(NavigationEventInfo.None)

    NavigationBackHandler(
        state = navEventState,
        isBackEnabled = isPagerBackHandlerEnabled,
        onBackCompleted = {
            mainState.animateToPage(0)
        },
    )
}
