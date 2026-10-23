// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Check
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class FloatingNavigationBarTest {

    @Test
    fun itemsGetTheMaximumWidthWhenTheyFit() {
        // 4 x 70 + 2 x 12 + 3 x 8 = 328.
        val geometry = floatingNavigationBarLayout(itemCount = 4, maxWidth = 400, itemMaxWidth = 70, innerPadding = 12, itemSpacing = 8)
        assertEquals(70, geometry.itemWidth)
        assertEquals(328, geometry.width)
        assertEquals(12, geometry.itemStart(0))
        assertEquals(12 + 3 * 78, geometry.itemStart(3))
    }

    @Test
    fun itemsShrinkEquallyWhenTheyDoNotFit() {
        // (300 - 24 - 32) / 5 = 48 (integer division, as BaseBottomFloatingMenuView.onMeasure).
        val geometry = floatingNavigationBarLayout(itemCount = 5, maxWidth = 300, itemMaxWidth = 70, innerPadding = 12, itemSpacing = 8)
        assertEquals(48, geometry.itemWidth)
        assertEquals(48 * 5 + 24 + 32, geometry.width)
    }

    @Test
    fun itemCentresSpreadWithTheDrawnWidth() {
        val geometry = floatingNavigationBarLayout(itemCount = 3, maxWidth = 1000, itemMaxWidth = 70, innerPadding = 12, itemSpacing = 8)
        // At the final width the centres sit on the item slots.
        assertEquals(12f + 35f, geometry.itemCenter(0, geometry.width.toFloat()))
        assertEquals(12f + 78f * 2 + 35f, geometry.itemCenter(2, geometry.width.toFloat()))
        // Drawn wider (still springing from 4 items), the slots widen equally.
        val wider = geometry.width + 30f
        assertEquals(12f + 40f, geometry.itemCenter(0, wider))
    }

    @Test
    fun expansionPositionsFollowCoui() {
        // Inactive: centred capsule, the slot hidden behind its end.
        assertEquals(20 to 292, floatingNavigationBarPositions(368, 328, 56, 12, expansionActive = false, compact = true, alignment = 0))
        // Active on a compact screen: capsule at the start, slot at the end.
        assertEquals(0 to 312, floatingNavigationBarPositions(368, 276, 56, 12, expansionActive = true, compact = true, alignment = 0))
        // Active on a wide screen: the pair centred.
        assertEquals(150 to 490, floatingNavigationBarPositions(696, 328, 56, 12, expansionActive = true, compact = false, alignment = 0))
    }

    @Test
    fun expansionAlphaFadesOnlyPastHalfCovered() {
        assertEquals(1f, floatingNavigationExpansionAlpha(0f))
        assertEquals(1f, floatingNavigationExpansionAlpha(0.5f))
        assertEquals(0.5f, floatingNavigationExpansionAlpha(0.75f))
        assertEquals(0f, floatingNavigationExpansionAlpha(1f))
    }

    @Test
    fun removingAnItemSpringsTheCapsuleWidth() = runComposeUiTest {
        var count by mutableIntStateOf(4)
        setBar(selected = { 0 }, onClick = {}, count = { count })
        mainClock.advanceTimeBy(1000)
        val before = onNodeWithTag("item0").getBoundsInRoot().left
        mainClock.autoAdvance = false
        count = 3
        mainClock.advanceTimeBy(120)
        val during = onNodeWithTag("item0").getBoundsInRoot().left
        mainClock.advanceTimeBy(3000)
        val after = onNodeWithTag("item0").getBoundsInRoot().left
        // 3 items: a 250dp capsule centred in 368dp -> item 0 at 16 + 59 + 12.
        assertClose(87.dp, after, "settled item 0")
        assertTrue(during > before && during < after, "item 0 must move on a spring: $before -> $during -> $after")
    }

    @Test
    fun showingTheExpansionViewSlidesItOutAndShrinksTheCapsule() = runComposeUiTest {
        var shown by mutableStateOf(false)
        setBar(selected = { 0 }, onClick = {}, withExpansion = true, expansionVisible = { shown })
        mainClock.advanceTimeBy(1000)
        shown = true
        mainClock.advanceTimeBy(3000)
        val expansion = onNodeWithTag("expansion").getBoundsInRoot()
        // Compact: the 56dp slot at the end edge, 16dp in from the 400dp container.
        assertClose(384.dp, expansion.right, "expansion end")
        assertClose(56.dp, expansion.right - expansion.left, "expansion size")
        // The capsule now has 368 - 56 - 12 = 300dp: four 63dp items, starting at the edge.
        val first = onNodeWithTag("item0").getBoundsInRoot()
        assertClose(63.dp, first.right - first.left, "shrunk item width")
        assertClose(28.dp, first.left, "capsule at the start edge")
    }

    @Test
    fun aLargeIconRisesAboveTheCapsuleAndTakesTaps() = runComposeUiTest {
        val clicks = mutableListOf<Int>()
        setBar(selected = { 0 }, onClick = { clicks += it }, largeIcon = 2)
        mainClock.advanceTimeBy(1000)
        val item = onNodeWithTag("item2").getBoundsInRoot()
        val x = with(density) { ((item.left + item.right) / 2).toPx() }
        // 14dp above the item top: inside the 40dp icon, above the 56dp capsule.
        val y = with(density) { (item.top - 12.dp).toPx() }
        onNodeWithTag("bar").performTouchInput { click(Offset(x, y)) }
        mainClock.advanceTimeBy(100)
        assertEquals(listOf(2), clicks)
    }

    @Test
    fun capsuleAndItemsHaveTheCoui17Size() = runComposeUiTest {
        setBar(selected = { 0 }, onClick = {})
        mainClock.advanceTimeBy(1000)
        val bar = onNodeWithTag("bar").getBoundsInRoot()
        // The capsule is centred in an 88dp container: 16dp above and below.
        assertClose(88.dp, bar.bottom - bar.top, "container height")
        val first = onNodeWithTag("item0").getBoundsInRoot()
        val last = onNodeWithTag("item3").getBoundsInRoot()
        assertClose(70.dp, first.right - first.left, "item width")
        assertClose(48.dp, first.bottom - first.top, "item height")
        // 16dp container padding + 4dp between the 56dp capsule and the 48dp item.
        assertClose(20.dp, first.top - bar.top, "item top")
        // Items are 70dp apart plus the 8dp spacing; the capsule is 328dp wide and centred.
        assertClose(78.dp * 3, last.left - first.left, "item pitch")
        val capsuleLeft = first.left - 12.dp
        val capsuleRight = last.right + 12.dp
        assertClose(328.dp, capsuleRight - capsuleLeft, "capsule width")
        assertClose(capsuleLeft - bar.left, bar.right - capsuleRight, "centring")
    }

    @Test
    fun slidingAcrossItemsMovesTheSelectionAndCommitsOnRelease() = runComposeUiTest {
        var selected by mutableIntStateOf(0)
        val clicks = mutableListOf<Int>()
        setBar(selected = { selected }, onClick = {
            clicks += it
            selected = it
        })
        mainClock.autoAdvance = false

        val first = onNodeWithTag("item0").getBoundsInRoot()
        val third = onNodeWithTag("item2").getBoundsInRoot()
        val y = with(density) { ((first.top + first.bottom) / 2).toPx() }
        val startX = with(density) { ((first.left + first.right) / 2).toPx() }
        val endX = with(density) { ((third.left + third.right) / 2).toPx() }

        onNodeWithTag("bar").performTouchInput {
            down(Offset(startX, y))
            moveTo(Offset((startX + endX) / 2, y))
            moveTo(Offset(endX, y))
        }
        mainClock.advanceTimeByFrame()
        // The selection follows the finger before anything is committed.
        onNodeWithTag("item2").assertIsSelected()
        onNodeWithTag("item0").assertIsNotSelected()
        assertTrue(clicks.isEmpty(), "a slide must not commit before release: $clicks")

        onNodeWithTag("bar").performTouchInput { up() }
        mainClock.advanceTimeByFrame()
        assertEquals(listOf(2), clicks)
        onNodeWithTag("item2").assertIsSelected()
    }

    @Test
    fun aTapClicksOnceEvenWithJitter() = runComposeUiTest {
        var selected by mutableIntStateOf(0)
        val clicks = mutableListOf<Int>()
        setBar(selected = { selected }, onClick = {
            clicks += it
            selected = it
        })

        onNodeWithTag("item1").performTouchInput {
            down(center)
            moveBy(Offset(2f, 1f))
            up()
        }
        mainClock.advanceTimeBy(100)
        assertEquals(listOf(1), clicks)
        onNodeWithTag("item1").assertIsSelected()
    }

    @Test
    fun aSlideSkipsDisabledItems() = runComposeUiTest {
        var selected by mutableIntStateOf(0)
        val clicks = mutableListOf<Int>()
        setBar(selected = { selected }, onClick = {
            clicks += it
            selected = it
        }, disabled = 1)
        mainClock.autoAdvance = false

        val first = onNodeWithTag("item0").getBoundsInRoot()
        val second = onNodeWithTag("item1").getBoundsInRoot()
        val y = with(density) { ((first.top + first.bottom) / 2).toPx() }
        onNodeWithTag("bar").performTouchInput {
            down(Offset(with(density) { ((first.left + first.right) / 2).toPx() }, y))
            moveTo(Offset(with(density) { ((second.left + second.right) / 2).toPx() }, y))
        }
        mainClock.advanceTimeByFrame()
        onNodeWithTag("item0").assertIsSelected()
        onNodeWithTag("bar").performTouchInput { up() }
        mainClock.advanceTimeByFrame()
        assertTrue(clicks.isEmpty(), "a disabled item must not be selected by a slide: $clicks")
    }

    @Test
    fun aHiddenBarTakesNoTouches() = runComposeUiTest {
        var visible by mutableStateOf(true)
        val clicks = mutableListOf<Int>()
        setBar(selected = { 0 }, onClick = { clicks += it }, visible = { visible })
        val second = onNodeWithTag("item1").getBoundsInRoot()
        visible = false
        mainClock.advanceTimeBy(2000)
        onNodeWithTag("bar").performTouchInput {
            click(Offset(with(density) { ((second.left + second.right) / 2).toPx() }, with(density) { ((second.top + second.bottom) / 2).toPx() }))
        }
        mainClock.advanceTimeBy(100)
        assertTrue(clicks.isEmpty(), "a hidden bar must not click: $clicks")
    }

    @Test
    fun theClassicBarSlidesOutOfItsLayout() = runComposeUiTest {
        var visible by mutableStateOf(true)
        setContent {
            COUITheme {
                Box(Modifier.width(400.dp)) {
                    NavigationBar(modifier = Modifier.testTag("classic"), visible = visible, defaultWindowInsetsPadding = false) {
                        NavigationBarItem(selected = true, onClick = {}, icon = COUIIcons.Basic.Check, label = "A", badge = 3)
                        NavigationBarItem(selected = false, onClick = {}, icon = COUIIcons.Basic.Check, label = "B", badge = 0)
                    }
                }
            }
        }
        mainClock.advanceTimeBy(500)
        val shown = onNodeWithTag("classic").getBoundsInRoot()
        assertTrue(shown.bottom - shown.top > 50.dp, "the shown bar keeps its height: ${shown.bottom - shown.top}")
        mainClock.autoAdvance = false
        visible = false
        mainClock.advanceTimeBy(100)
        val half = onNodeWithTag("classic").getBoundsInRoot()
        mainClock.advanceTimeBy(400)
        val hidden = onNodeWithTag("classic").getBoundsInRoot()
        assertTrue(half.bottom - half.top in 1.dp..(shown.bottom - shown.top), "hiding shrinks the layout gradually: ${half.bottom - half.top}")
        assertClose(0.dp, hidden.bottom - hidden.top, "hidden height")
    }

    @Test
    fun addingItemsKeepsTheExistingItemsInPlaceAndEasesThemOver() = runComposeUiTest {
        var count by mutableIntStateOf(3)
        setBar(selected = { 0 }, onClick = {}, count = { count }, largeIcon = 2)
        mainClock.advanceTimeBy(1000)
        mainClock.autoAdvance = false
        // 3 -> 4 grows the capsule; 4 -> 5 no longer fits and shrinks every item instead.
        for (target in listOf(4, 5, 4, 3)) {
            val before = (0 until minOf(count, target)).map { onNodeWithTag("item$it").getBoundsInRoot() }
            count = target
            mainClock.advanceTimeByFrame()
            mainClock.advanceTimeByFrame()
            before.forEachIndexed { index, old ->
                val now = onNodeWithTag("item$index").getBoundsInRoot()
                val oldCenter = (old.left + old.right) / 2
                val newCenter = (now.left + now.right) / 2
                assertTrue(
                    abs((newCenter - oldCenter).value) < 6f,
                    "item $index must not jump when the count goes to $target: $oldCenter -> $newCenter",
                )
            }
            mainClock.advanceTimeBy(3000)
        }
        // Back at 3 items: the 250dp capsule centred in 368dp.
        assertClose(87.dp, onNodeWithTag("item0").getBoundsInRoot().left, "settled item 0")
    }

    private fun ComposeUiTest.setBar(
        selected: () -> Int,
        onClick: (Int) -> Unit,
        disabled: Int = -1,
        visible: () -> Boolean = { true },
        count: () -> Int = { 4 },
        largeIcon: Int = -1,
        expansionVisible: () -> Boolean = { false },
        withExpansion: Boolean = false,
    ) {
        setContent {
            COUITheme {
                Box(Modifier.width(400.dp)) {
                    FloatingNavigationBar(
                        modifier = Modifier.testTag("bar"),
                        visible = visible(),
                        expansionVisible = expansionVisible(),
                        defaultWindowInsetsPadding = false,
                        expansion = if (withExpansion) {
                            { Box(Modifier.size(56.dp).testTag("expansion")) }
                        } else {
                            null
                        },
                    ) {
                        repeat(count()) { index ->
                            FloatingNavigationBarItem(
                                selected = selected() == index,
                                onClick = { onClick(index) },
                                icon = COUIIcons.Basic.Check,
                                label = "Item $index",
                                modifier = Modifier.testTag("item$index"),
                                enabled = index != disabled,
                                largeIcon = index == largeIcon,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun assertClose(expected: Dp, actual: Dp, what: String) {
        assertTrue(abs((expected - actual).value) < 0.5f, "$what: expected $expected, got $actual")
    }
}
