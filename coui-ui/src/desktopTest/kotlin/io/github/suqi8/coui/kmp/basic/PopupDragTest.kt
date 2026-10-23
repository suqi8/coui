// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.overlay.OverlayCascadingListPopup
import io.github.suqi8.coui.kmp.overlay.OverlayListPopup
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.window.WindowListPopup
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Drag-to-select on a popup list, frame by frame: the shared mask must ride its travel spring and
 * never skip, a disabled row must fade it in place, a long press must not end the drag, and the
 * first row must stay selectable.
 */
@OptIn(ExperimentalTestApi::class)
class PopupDragTest {

    private class Sample(val top: Float, val press: Float, val target: PopupListItemHandle?)

    private fun PopupListGestureHost.sample(): Sample = Sample(
        top = mask.current()?.top ?: Float.NaN,
        press = mask.press,
        target = mask.target,
    )

    private fun ComposeUiTest.setRows(
        host: PopupListGestureHost,
        enabled: List<Boolean>,
        onPick: (Int) -> Unit = {},
        submenuAt: Int = -1,
        onLongClick: () -> Unit = {},
    ) {
        setContent {
            COUITheme {
                Box(Modifier.testTag("menu")) {
                    ListPopupColumn(host = host) {
                        enabled.forEachIndexed { index, isEnabled ->
                            PopupDropdownRow(
                                item = DropdownItem(text = "Row $index"),
                                isSelected = false,
                                dropdownColors = DropdownDefaults.dropdownColors(),
                                enabled = isEnabled,
                                hasSubmenu = index == submenuAt,
                                submenuExpanded = false,
                                submenuHeader = false,
                                onClick = { onPick(index) },
                                onLongClick = if (index == submenuAt) onLongClick else null,
                            )
                        }
                    }
                }
            }
        }
        waitForIdle()
    }

    /** Centre of row [index] in list space, which is the root space here. */
    private fun PopupListGestureHost.centerOf(index: Int): Offset {
        val row = sortedItems()[index]
        return Offset(width / 2f, (row.top + row.bottom) / 2f)
    }

    /** The largest share of a row-to-row distance a critically damped travel spring covers in one frame. */
    private val maxTravelStep = 0.3f

    @Test
    fun maskSlidesBetweenEnabledRowsWithoutSkippingAFrame() = runComposeUiTest {
        val host = PopupListGestureHost()
        setRows(host, enabled = listOf(true, true, true))
        mainClock.autoAdvance = false
        val from = host.centerOf(0)
        val to = host.centerOf(1)
        val distance = host.sortedItems()[1].top - host.sortedItems()[0].top

        onRoot().performTouchInput { down(from) }
        mainClock.advanceTimeBy(400)
        val rest = host.sample()
        assertEquals(host.sortedItems()[0].top, rest.top, "mask rests on row 0")

        onRoot().performTouchInput { moveTo(to) }
        var previous = rest.top
        val tops = mutableListOf<Float>()
        repeat(20) {
            mainClock.advanceTimeByFrame()
            val now = host.sample()
            tops += now.top
            assertTrue(
                abs(now.top - previous) <= distance * maxTravelStep,
                "frame $it jumped ${now.top - previous}px of a ${distance}px travel: $tops",
            )
            previous = now.top
        }
        assertEquals(host.sortedItems()[1].top, previous, 0.5f, "mask settles on row 1: $tops")
        onRoot().performTouchInput { up() }
    }

    @Test
    fun theMaskSlidesAcrossADisabledRowWithoutFading() = runComposeUiTest {
        val host = PopupListGestureHost()
        var picked = -1
        setRows(host, enabled = listOf(true, false, true), onPick = { picked = it })
        mainClock.autoAdvance = false
        val rows = host.sortedItems()
        val distance = rows[1].top - rows[0].top

        onRoot().performTouchInput { down(host.centerOf(0)) }
        mainClock.advanceTimeBy(400)
        assertEquals(1f, host.mask.press, 0.01f)

        // Deviation from COUI (which fades the mask over a disabled row and snaps it onto the next
        // enabled row): the pressed mask rides the travel spring onto the disabled row and on to
        // row 2, every frame within one spring step, at full press.
        // A re-target mid-travel restarts the spring towards a target up to ~1.25 rows away.
        val maxStep = distance * 1.25f * maxTravelStep
        val tops = mutableListOf<Float>()
        var previous = host.sample().top
        fun advance(frames: Int) = repeat(frames) {
            mainClock.advanceTimeByFrame()
            val now = host.sample()
            tops += now.top
            assertTrue(
                abs(now.top - previous) <= maxStep,
                "frame ${tops.size} jumped ${now.top - previous}px of a ${distance}px row: $tops",
            )
            assertTrue(now.press >= 0.99f, "frame ${tops.size}: the press never drops (${now.press})")
            previous = now.top
        }
        onRoot().performTouchInput { moveTo(host.centerOf(1)) }
        advance(4)
        onRoot().performTouchInput { moveTo(host.centerOf(2)) }
        advance(20)
        assertEquals(rows[2].top, previous, 0.5f, "mask settles on row 2: $tops")

        onRoot().performTouchInput { up() }
        mainClock.advanceTimeBy(500)
        assertEquals(2, picked)
    }

    @Test
    fun releasingOverADisabledRowSelectsNothing() = runComposeUiTest {
        val host = PopupListGestureHost()
        var picked = -1
        setRows(host, enabled = listOf(true, false, true), onPick = { picked = it })
        onRoot().performTouchInput {
            down(host.centerOf(0))
            moveTo(host.centerOf(1))
        }
        mainClock.advanceTimeBy(300)
        assertEquals(host.sortedItems()[1].top, host.mask.current()?.top, "the mask followed onto the disabled row")
        onRoot().performTouchInput { up() }
        mainClock.advanceTimeBy(500)
        assertEquals(-1, picked)
    }

    @Test
    fun aLongPressOnAPlainRowKeepsTheDragAlive() = runComposeUiTest {
        val host = PopupListGestureHost()
        var picked = -1
        setRows(host, enabled = listOf(true, true, true, true), onPick = { picked = it })
        onRoot().performTouchInput {
            down(host.centerOf(0))
            advanceEventTime(1200)
            moveTo(host.centerOf(1))
            moveTo(host.centerOf(3))
        }
        mainClock.advanceTimeBy(300)
        assertEquals(host.sortedItems()[3], host.mask.target, "the mask followed the finger")
        onRoot().performTouchInput { up() }
        waitForIdle()
        assertEquals(3, picked)
    }

    @Test
    fun theSubMenuLongPressCountsDownOnARowReachedByDragging() = runComposeUiTest {
        val host = PopupListGestureHost()
        var opened = 0
        setRows(host, enabled = listOf(true, true, true), submenuAt = 2, onLongClick = { opened++ })
        onRoot().performTouchInput {
            down(host.centerOf(0))
            moveTo(host.centerOf(2))
            // Moving inside the row does not restart or cancel the countdown.
            moveTo(host.centerOf(2) + Offset(30f, 4f))
        }
        mainClock.advanceTimeBy(800)
        onRoot().performTouchInput { up() }
        waitForIdle()
        assertEquals(1, opened)
    }

    @Test
    fun dragUpToTheFirstRowSelectsIt() = runComposeUiTest {
        val host = PopupListGestureHost()
        var picked = -1
        setRows(host, enabled = listOf(true, true, true, true), onPick = { picked = it })
        val start = host.centerOf(3)
        val end = host.centerOf(0)
        onRoot().performTouchInput {
            down(start)
            for (step in 1..12) moveTo(start + (end - start) * (step / 12f))
            up()
        }
        waitForIdle()
        assertEquals(0, picked)
    }

    @Test
    fun dragUpToTheFirstRowOfAMenuOpenedByTapSelectsIt() = runComposeUiTest {
        var picked = -1
        val items = List(4) { "Option $it" }
        setContent {
            COUITheme {
                Scaffold {
                    var show by remember { mutableStateOf(false) }
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                        Box {
                            TextButton(
                                text = "Anchor",
                                onClick = { show = true },
                                modifier = Modifier.testTag("anchor").padding(top = 16.dp),
                            )
                            OverlayListPopup(
                                show = show,
                                alignment = PopupPositionProvider.Align.TopStart,
                                onDismissRequest = { show = false },
                            ) {
                                ListPopupColumn {
                                    items.forEachIndexed { index, text ->
                                        DropdownImpl(
                                            text = text,
                                            optionSize = items.size,
                                            isSelected = false,
                                            index = index,
                                            onSelectedIndexChange = {
                                                picked = it
                                                show = false
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        onNodeWithTag("anchor").performClick()
        mainClock.advanceTimeBy(1500)
        waitForIdle()
        val first = onNodeWithText("Option 0", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val last = onNodeWithText("Option 3", useUnmergedTree = true).getUnclippedBoundsInRoot()
        onRoot().performTouchInput {
            val start = Offset(last.left.toPx() + 4.dp.toPx(), ((last.top + last.bottom) / 2).toPx())
            val end = Offset(first.left.toPx() + 4.dp.toPx(), ((first.top + first.bottom) / 2).toPx())
            down(start)
            for (step in 1..12) moveTo(start + (end - start) * (step / 12f))
            up()
        }
        mainClock.advanceTimeBy(1000)
        waitForIdle()
        assertEquals(0, picked)
    }

    /** Opens an [OverlayListPopup] from an anchor tap and returns the row bounds in root pixels. */
    private fun ComposeUiTest.openOverlayMenu(rows: Int, pageScrolls: Boolean, window: Boolean = false, onPick: (Int) -> Unit) {
        val items = List(rows) { "Option $it" }
        setContent {
            COUITheme {
                Scaffold {
                    var show by remember { mutableStateOf(false) }
                    val page = if (pageScrolls) Modifier.verticalScroll(rememberScrollState()) else Modifier
                    Column(page.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box {
                            TextButton(
                                text = "Anchor",
                                onClick = { show = true },
                                modifier = Modifier.testTag("anchor").padding(top = 16.dp),
                            )
                            val menu: @Composable () -> Unit = {
                                ListPopupColumn {
                                    items.forEachIndexed { index, text ->
                                        DropdownImpl(
                                            text = text,
                                            optionSize = items.size,
                                            isSelected = false,
                                            index = index,
                                            onSelectedIndexChange = {
                                                onPick(it)
                                                show = false
                                            },
                                        )
                                    }
                                }
                            }
                            if (window) {
                                WindowListPopup(
                                    show = show,
                                    alignment = PopupPositionProvider.Align.TopStart,
                                    onDismissRequest = { show = false },
                                    content = menu,
                                )
                            } else {
                                OverlayListPopup(
                                    show = show,
                                    alignment = PopupPositionProvider.Align.TopStart,
                                    onDismissRequest = { show = false },
                                    content = menu,
                                )
                            }
                        }
                        if (pageScrolls) Box(Modifier.height(3000.dp))
                    }
                }
            }
        }
        onNodeWithTag("anchor").performClick()
        mainClock.advanceTimeBy(1500)
        waitForIdle()
    }

    /** Holds row [from] past every long-press timeout, then drags to row [to] in 2px finger steps. */
    private fun ComposeUiTest.holdAndCrawl(from: Int, to: Int) {
        val a = onNodeWithText("Option $from", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val b = onNodeWithText("Option $to", useUnmergedTree = true).getUnclippedBoundsInRoot()
        // Inject on the row itself, so the gesture reaches whichever window hosts the popup.
        onNodeWithText("Option $from").performTouchInput {
            val start = Offset(4.dp.toPx(), height / 2f)
            val end = start + Offset(0f, ((b.top + b.bottom) / 2 - (a.top + a.bottom) / 2).toPx())
            down(start)
            advanceEventTime(1200)
            val steps = (abs(end.y - start.y) / 2f).toInt().coerceAtLeast(1)
            for (step in 1..steps) moveTo(start + (end - start) * (step / steps.toFloat()), delayMillis = 8)
            up()
        }
        mainClock.advanceTimeBy(1000)
        waitForIdle()
    }

    @Test
    fun holdingARowThenCrawlingSelectsTheRowUnderTheFinger() = runComposeUiTest {
        var picked = -1
        openOverlayMenu(rows = 5, pageScrolls = false) { picked = it }
        holdAndCrawl(from = 0, to = 3)
        assertEquals(3, picked)
    }

    @Test
    fun holdingARowThenCrawlingUpwardsOverAScrollingPageSelects() = runComposeUiTest {
        var picked = -1
        openOverlayMenu(rows = 5, pageScrolls = true) { picked = it }
        holdAndCrawl(from = 4, to = 1)
        assertEquals(1, picked)
    }

    @Test
    fun holdingARowOfAWindowPopupThenCrawlingSelects() = runComposeUiTest {
        var picked = -1
        openOverlayMenu(rows = 5, pageScrolls = true, window = true) { picked = it }
        holdAndCrawl(from = 0, to = 3)
        assertEquals(3, picked)
    }
    private fun ComposeUiTest.openCascadingMenu(onPick: (String) -> Unit) {
        val entries = listOf(
            DropdownEntry(
                items = listOf(
                    DropdownItem(text = "Copy", onClick = { onPick("Copy") }),
                    DropdownItem(
                        text = "More",
                        children = listOf("Alpha", "Beta", "Gamma").map { DropdownItem(text = it, onClick = { onPick(it) }) },
                    ),
                    DropdownItem(text = "Paste", onClick = { onPick("Paste") }),
                    DropdownItem(text = "Share", onClick = { onPick("Share") }),
                ),
            ),
        )
        setContent {
            COUITheme {
                Scaffold {
                    var show by remember { mutableStateOf(false) }
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                        Box {
                            TextButton(
                                text = "Anchor",
                                onClick = { show = true },
                                modifier = Modifier.testTag("anchor").padding(top = 16.dp),
                            )
                            OverlayCascadingListPopup(
                                show = show,
                                entries = entries,
                                onDismissRequest = { show = false },
                            )
                        }
                    }
                }
            }
        }
        onNodeWithTag("anchor").performClick()
        mainClock.advanceTimeBy(1500)
        waitForIdle()
    }

    private fun ComposeUiTest.centerOf(text: String): Offset {
        val r = onNodeWithText(text, useUnmergedTree = true).getUnclippedBoundsInRoot()
        return with(density) { Offset((r.left + 4.dp).toPx(), ((r.top + r.bottom) / 2).toPx()) }
    }

    private fun ComposeUiTest.crawl(from: Offset, to: Offset) {
        onRoot().performTouchInput {
            val steps = (maxOf(abs(to.y - from.y), abs(to.x - from.x)) / 2f).toInt().coerceAtLeast(1)
            for (step in 1..steps) moveTo(from + (to - from) * (step / steps.toFloat()), delayMillis = 8)
        }
    }

    @Test
    fun holdingAPlainRowOfACascadingMenuThenCrawlingSelects() = runComposeUiTest {
        var picked = ""
        openCascadingMenu { picked = it }
        val start = centerOf("Copy")
        onRoot().performTouchInput { down(start) }
        mainClock.advanceTimeBy(1200)
        crawl(start, centerOf("Share"))
        onRoot().performTouchInput { up() }
        mainClock.advanceTimeBy(1000)
        assertEquals("Share", picked)
    }

    @Test
    fun holdingASubMenuRowThenCrawlingIntoTheSubMenuSelects() = runComposeUiTest {
        var picked = ""
        openCascadingMenu { picked = it }
        val start = centerOf("More")
        onRoot().performTouchInput { down(start) }
        mainClock.advanceTimeBy(1500)
        waitForIdle()
        val beta = centerOf("Beta")
        crawl(start, beta)
        onRoot().performTouchInput { up() }
        mainClock.advanceTimeBy(1000)
        assertEquals("Beta", picked)
    }

    @Test
    fun holdingASubMenuRowThenCrawlingToAnotherMainRowSelectsIt() = runComposeUiTest {
        var picked = ""
        openCascadingMenu { picked = it }
        val start = centerOf("More")
        onRoot().performTouchInput { down(start) }
        mainClock.advanceTimeBy(1500)
        waitForIdle()
        crawl(start, centerOf("Share"))
        onRoot().performTouchInput { up() }
        mainClock.advanceTimeBy(1000)
        assertEquals("Share", picked)
    }

    @Test
    fun liftingOnTheLongPressedRowSelectsNothing() = runComposeUiTest {
        var picked = ""
        openCascadingMenu { picked = it }
        val start = centerOf("More")
        onRoot().performTouchInput { down(start) }
        mainClock.advanceTimeBy(1500)
        onRoot().performTouchInput { up() }
        mainClock.advanceTimeBy(1000)
        assertEquals("", picked)
        // The sub menu the long press opened stays up.
        onNodeWithText("Beta", useUnmergedTree = true).assertExists()
    }
}
