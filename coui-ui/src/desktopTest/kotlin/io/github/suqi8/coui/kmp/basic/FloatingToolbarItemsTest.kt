// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Check
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class FloatingToolbarItemsTest {

    @Test
    fun shownCountFollowsCoui() {
        // Eight icon items with room to spare: six show, the overflow item takes the seventh place.
        assertEquals(6, floatingToolbarShownCount(IntArray(8) { 44 }, overflowWidth = 44, spacing = 4, available = 10_000L, maxVisible = 6))
        // Seven: the last item fits in the overflow item's place.
        assertEquals(7, floatingToolbarShownCount(IntArray(7) { 44 }, overflowWidth = 44, spacing = 4, available = 10_000L, maxVisible = 6))
        // Room for 44 + 4 + 44 + 4 + 44 only: two items, then the overflow item.
        assertEquals(2, floatingToolbarShownCount(IntArray(4) { 44 }, overflowWidth = 44, spacing = 4, available = 140L, maxVisible = 6))
        // A lone item that does not fit leaves only the overflow item.
        assertEquals(0, floatingToolbarShownCount(intArrayOf(200), overflowWidth = 44, spacing = 4, available = 100L, maxVisible = 6))
    }

    @Test
    fun anAddedItemWaitsForTheGapsThenScalesIn() = runComposeUiTest {
        var count by mutableIntStateOf(2)
        setContent {
            COUITheme {
                Box(Modifier.width(360.dp)) {
                    FloatingToolbar(
                        items = List(count) { index ->
                            FloatingToolbarItem(
                                onClick = {},
                                icon = COUIIcons.Basic.Check,
                                contentDescription = "item$index",
                                key = index,
                            )
                        },
                        defaultWindowInsetsPadding = false,
                    )
                }
            }
        }
        mainClock.autoAdvance = false
        mainClock.advanceTimeBy(1000)
        val firstLeft = onNodeWithContentDescription("item0").getBoundsInRoot().left

        count = 3
        mainClock.advanceTimeBy(100)
        // The pending phase: the gaps breathe while the new item waits at scale 0.
        val pending = onNodeWithContentDescription("item2").getBoundsInRoot()
        assertTrue((pending.right - pending.left).value < 1f, "the new item must wait while the gaps close up: $pending")

        mainClock.advanceTimeBy(3000)
        val settled = onNodeWithContentDescription("item2").getBoundsInRoot()
        assertTrue(abs((settled.right - settled.left).value - 44f) < 1f, "the new item must end at its 44dp width: $settled")
        // The centred capsule grew by one item and one gap: its first item moved half of that.
        val shift = (firstLeft - onNodeWithContentDescription("item0").getBoundsInRoot().left).value
        assertTrue(abs(shift - 24f) < 1f, "the capsule must stay centred: it moved by $shift")
    }

    @Test
    fun aRemovedItemScalesOutAndLeaves() = runComposeUiTest {
        var count by mutableIntStateOf(3)
        setContent {
            COUITheme {
                Box(Modifier.width(360.dp)) {
                    FloatingToolbar(
                        items = List(count) { index ->
                            FloatingToolbarItem(
                                onClick = {},
                                icon = COUIIcons.Basic.Check,
                                contentDescription = "item$index",
                                key = index,
                            )
                        },
                        defaultWindowInsetsPadding = false,
                    )
                }
            }
        }
        mainClock.autoAdvance = false
        mainClock.advanceTimeBy(1000)
        val before = onNodeWithContentDescription("item0").getBoundsInRoot().left

        count = 2
        mainClock.advanceTimeBy(100)
        // The pending phase: the leaving item still stands while the gaps open up, so the
        // centred capsule first grows a little.
        val pending = onNodeWithContentDescription("item0").getBoundsInRoot().left
        assertTrue(pending < before, "the gaps must open before the item leaves: $before -> $pending")

        mainClock.advanceTimeBy(3000)
        // The capsule lost one item and one gap: its first item moved half of that the other way.
        val shift = (onNodeWithContentDescription("item0").getBoundsInRoot().left - before).value
        assertTrue(abs(shift - 24f) < 1f, "the capsule must settle centred: it moved by $shift")
    }
}
