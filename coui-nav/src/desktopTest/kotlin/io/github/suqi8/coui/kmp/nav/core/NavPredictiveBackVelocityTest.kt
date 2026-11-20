// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.nav.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.navigationevent.DirectNavigationEventInput
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEventDispatcher
import androidx.navigationevent.NavigationEventDispatcherOwner
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import io.github.suqi8.coui.kmp.nav.transition.NavSettlePhase
import io.github.suqi8.coui.kmp.nav.transition.navGraphicsTransition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private data object VelocityRouteA : NavKey

private data object VelocityRouteB : NavKey

/**
 * The predictive-back commit is seeded with a tracked release velocity (a least-squares fit over
 * the timestamped progress stream, like the reference's velocity tracker), and a finger that
 * rested before lifting commits without a fling.
 */
@OptIn(ExperimentalTestApi::class)
class NavPredictiveBackVelocityTest {

    private class TestOwner : NavigationEventDispatcherOwner {
        override val navigationEventDispatcher = NavigationEventDispatcher()
        val input = DirectNavigationEventInput().also { navigationEventDispatcher.addInput(it) }
    }

    /** Records the release velocity the commit settle publishes. */
    private class Probe {
        var commitVelocity = Float.NaN
    }

    private fun probedTransition(probe: Probe) = navGraphicsTransition { scope ->
        val settle = scope.settle
        if (settle != null && settle.phase == NavSettlePhase.Commit) probe.commitVelocity = settle.releaseVelocity
        translationX = (-scope.relativeDepth).coerceIn(0f, 1f) * scope.layoutSize.width
    }

    /** Sends a back gesture that moves 0.08 progress every 16ms frame up to 0.4. */
    private fun DirectNavigationEventInput.dragBack() {
        backStarted(NavigationEvent(progress = 0f, frameTimeMillis = FRAME_ZERO_MILLIS))
        for (frame in 1..5) {
            backProgressed(NavigationEvent(progress = 0.08f * frame, frameTimeMillis = FRAME_ZERO_MILLIS + 16L * frame))
        }
    }

    @Test
    fun flungBack_seedsTheCommitWithTheTrackedVelocity() = runComposeUiTest {
        val owner = TestOwner()
        val probe = Probe()
        val backStack = navBackStackOf(VelocityRouteA, VelocityRouteB)
        setContent {
            CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides owner) {
                NavDisplay(backStack = backStack, transition = probedTransition(probe), effects = NavDisplayEffects.None) {
                    entry<VelocityRouteA> { Box(Modifier.fillMaxSize()) }
                    entry<VelocityRouteB> { Box(Modifier.fillMaxSize()) }
                }
            }
        }
        waitForIdle()

        runOnIdle {
            owner.input.dragBack()
            owner.input.backCompleted()
        }
        waitForIdle()

        assertEquals(1, backStack.size, "the completed gesture must pop")
        // 0.08 progress per 16ms is 5 progress-units per second toward pop.
        assertEquals(5f, probe.commitVelocity, absoluteTolerance = 0.25f)
    }

    @Test
    fun restedBack_commitsWithoutAFling() = runComposeUiTest {
        val owner = TestOwner()
        val probe = Probe()
        val backStack = navBackStackOf(VelocityRouteA, VelocityRouteB)
        setContent {
            CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides owner) {
                NavDisplay(backStack = backStack, transition = probedTransition(probe), effects = NavDisplayEffects.None) {
                    entry<VelocityRouteA> { Box(Modifier.fillMaxSize()) }
                    entry<VelocityRouteB> { Box(Modifier.fillMaxSize()) }
                }
            }
        }
        waitForIdle()

        runOnIdle { owner.input.dragBack() }
        waitForIdle()
        // The finger rests (no progress events) well past the 40ms pointer-stopped gap, then lifts.
        Thread.sleep(120)
        runOnIdle { owner.input.backCompleted() }
        waitForIdle()

        assertEquals(1, backStack.size, "the completed gesture must pop")
        assertTrue(!probe.commitVelocity.isNaN(), "the commit settle must have been published")
        assertEquals(0f, probe.commitVelocity, absoluteTolerance = 1e-6f)
    }

    private companion object {
        const val FRAME_ZERO_MILLIS = 1_000L
    }
}
