// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
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

    private fun ComposeUiTest.setBar(
        selected: () -> Int,
        onClick: (Int) -> Unit,
        disabled: Int = -1,
        visible: () -> Boolean = { true },
    ) {
        setContent {
            COUITheme {
                Box(Modifier.width(400.dp)) {
                    FloatingNavigationBar(
                        modifier = Modifier.testTag("bar"),
                        visible = visible(),
                        defaultWindowInsetsPadding = false,
                    ) {
                        repeat(4) { index ->
                            FloatingNavigationBarItem(
                                selected = selected() == index,
                                onClick = { onClick(index) },
                                icon = COUIIcons.Basic.Check,
                                label = "Item $index",
                                modifier = Modifier.testTag("item$index"),
                                enabled = index != disabled,
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
