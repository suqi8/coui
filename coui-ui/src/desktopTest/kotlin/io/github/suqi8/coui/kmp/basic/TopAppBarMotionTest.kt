// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The COUI 17 collapsable app bar motion of [COUIScrollBehavior]: the bar trails each scroll step
 * with the follow spring, snaps to the nearer end with the attach spring, and can be dragged by the
 * bar itself.
 */
@OptIn(ExperimentalTestApi::class)
class TopAppBarMotionTest {

    @Test
    fun theBarTrailsAScrollStepAndCatchesUp() = runComposeUiTest {
        val behavior = setBehavior(followScroll = true)
        mainClock.autoAdvance = false
        runOnUiThread {
            behavior.state.heightOffsetLimit = -200f
            behavior.nestedScrollConnection.onPreScroll(Offset(0f, -60f), NestedScrollSource.UserInput)
            // The scroll is consumed at once, the bar is still drawn where it was.
            assertEquals(-60f, behavior.state.heightOffset)
            assertEquals(0f, behavior.state.displayedHeightOffset)
        }
        // The first frame only takes the time; the spring moves from the next one.
        mainClock.advanceTimeByFrame()
        mainClock.advanceTimeBy(32)
        runOnUiThread {
            val drawn = behavior.state.displayedHeightOffset
            assertTrue(drawn < 0f && drawn > -60f, "the bar must trail the scroll: drawn at $drawn")
        }
        mainClock.advanceTimeBy(1000)
        runOnUiThread { assertEquals(-60f, behavior.state.displayedHeightOffset) }
    }

    @Test
    fun withoutFollowScrollTheBarIsDrawnAtTheScrollPosition() = runComposeUiTest {
        val behavior = setBehavior(followScroll = false)
        runOnUiThread {
            behavior.state.heightOffsetLimit = -200f
            behavior.nestedScrollConnection.onPreScroll(Offset(0f, -60f), NestedScrollSource.UserInput)
            assertEquals(-60f, behavior.state.displayedHeightOffset)
        }
    }

    @Test
    fun aBarLeftBetweenTheEndsSpringsToTheNearerOne() = runComposeUiTest {
        val behavior = setBehavior(followScroll = true)
        mainClock.autoAdvance = false
        runOnUiThread {
            behavior.state.heightOffsetLimit = -200f
            behavior.nestedScrollConnection.onPreScroll(Offset(0f, -120f), NestedScrollSource.UserInput)
            runBlocking { behavior.nestedScrollConnection.onPostFling(Velocity.Zero, Velocity.Zero) }
            // Past the midpoint: the offset jumps to collapsed and the drawn bar springs after it.
            assertEquals(-200f, behavior.state.heightOffset)
        }
        mainClock.advanceTimeBy(1000)
        runOnUiThread {
            assertEquals(-200f, behavior.state.displayedHeightOffset)
            behavior.nestedScrollConnection.onPostScroll(Offset.Zero, Offset(0f, 140f), NestedScrollSource.UserInput)
            runBlocking { behavior.nestedScrollConnection.onPostFling(Velocity.Zero, Velocity(0f, 1f)) }
            // Short of the midpoint: back to expanded.
            assertEquals(0f, behavior.state.heightOffset)
        }
        mainClock.advanceTimeBy(1000)
        runOnUiThread { assertEquals(0f, behavior.state.displayedHeightOffset) }
    }

    @Test
    fun draggingTheBarItselfCollapsesItAndSnapsOnRelease() = runComposeUiTest {
        lateinit var behavior: ScrollBehavior
        setContent {
            COUITheme {
                behavior = COUIScrollBehavior()
                Box(Modifier.width(360.dp)) {
                    TopAppBar(
                        title = "Title",
                        modifier = Modifier.testTag("bar"),
                        scrollBehavior = behavior,
                        defaultWindowInsetsPadding = false,
                    )
                }
            }
        }
        mainClock.advanceTimeBy(500)
        val limit = runOnUiThread { behavior.state.heightOffsetLimit }
        assertTrue(limit < 0f, "the bar must report its collapse range, got $limit")
        onNodeWithTag("bar").performTouchInput {
            down(center)
            // Past the touch slop, then 70% of the collapse range up.
            moveBy(Offset(0f, -viewConfiguration.touchSlop))
            moveBy(Offset(0f, limit * 0.7f))
            up()
        }
        mainClock.advanceTimeBy(1000)
        runOnUiThread {
            assertEquals(limit, behavior.state.heightOffset)
            assertEquals(limit, behavior.state.displayedHeightOffset)
        }
    }

    private fun ComposeUiTest.setBehavior(followScroll: Boolean): ScrollBehavior {
        lateinit var behavior: ScrollBehavior
        setContent {
            behavior = COUIScrollBehavior(followScroll = followScroll)
        }
        waitForIdle()
        return behavior
    }
}
