// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.NavDisplayTransitionEffects
import androidx.savedstate.serialization.SavedStateConfiguration
import catalog.CategoryPage
import catalog.DemoCatalog
import catalog.DemoCategory
import catalog.DemoHostPage
import catalog.SearchPage
import lab.LabPage
import lab.LabRegistry
import lab.NavTestPage
import navigation3.Navigator
import navigation3.Route
import navigation3.RouteSerializersModule
import page.AboutPage
import page.ColorPage
import page.DesignPages
import page.IconsPage
import page.LicensePage
import page.TextStylePage
import utils.FPSMonitor
import utils.shouldShowSplitPane

/**
 * The root navigation host: the back stack, the destinations and the FPS overlay.
 */
@Composable
fun AppNavHost() {
    val savedStateConfig = remember {
        SavedStateConfiguration {
            serializersModule = RouteSerializersModule
        }
    }
    val backStack = rememberNavBackStack(
        configuration = savedStateConfig,
        Route.Main,
    )
    val navigator = remember(backStack) { Navigator(backStack) }
    val settings = LocalAppSettings.current
    val transitions = LocalNavTransitionSettings.current

    Box(Modifier.fillMaxSize()) {
        CompositionLocalProvider(
            LocalNavigator provides navigator,
            LocalIsWideScreen provides shouldShowSplitPane(),
        ) {
            val entryProvider = remember(backStack) {
                entryProvider<NavKey> {
                    entry<Route.Main> { MainShell() }
                    entry<Route.Category> { route ->
                        val category = DemoCategory.byId(route.id)
                        if (category != null) CategoryPage(category = category) else PopMissing(navigator)
                    }
                    entry<Route.Demo> { route ->
                        val demo = DemoCatalog.byId(route.id)
                        if (demo != null) DemoHostPage(demo = demo, onBack = { navigator.pop() }) else PopMissing(navigator)
                    }
                    entry<Route.Search> { SearchPage() }
                    entry<Route.Design> { route ->
                        when (route.page) {
                            DesignPages.ICONS -> IconsPage()
                            DesignPages.COLORS -> ColorPage()
                            DesignPages.TEXT_STYLES -> TextStylePage()
                            else -> PopMissing(navigator)
                        }
                    }
                    entry<Route.About> { AboutPage() }
                    entry<Route.License> { LicensePage() }
                    entry<Route.Lab> { LabPage() }
                    entry<Route.LabPage> { route ->
                        val page = LabRegistry.byId(route.id)
                        if (page != null) page.content() else PopMissing(navigator)
                    }
                    entry<Route.Navigation> { route ->
                        val index = backStack.filterIsInstance<Route.Navigation>().indexOf(route) + 1
                        NavTestPage(index = index)
                    }
                }
            }

            val entries = rememberDecoratedNavEntries(
                backStack = backStack,
                entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
                entryProvider = entryProvider,
            )

            val transitionEffects = NavDisplayTransitionEffects(
                enableCornerClip = transitions.enableCornerClip,
                dimAmount = if (transitions.enableDim) 0.5f else 0f,
                blockInputDuringTransition = transitions.blockInputDuringTransition,
                popDirectionFollowsSwipeEdge = transitions.popDirectionFollowsSwipeEdge,
            )

            NavDisplay(
                entries = entries,
                onBack = { navigator.pop() },
                transitionEffects = transitionEffects,
            )
        }

        AnimatedVisibility(
            visible = settings.showFpsMonitor,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            FPSMonitor(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .captionBarPadding(),
            )
        }
    }
}

/**
 * Pops a destination whose id no longer resolves (a renamed demo in a restored back stack).
 */
@Composable
private fun PopMissing(navigator: Navigator) {
    LaunchedEffect(navigator) { navigator.pop() }
}
