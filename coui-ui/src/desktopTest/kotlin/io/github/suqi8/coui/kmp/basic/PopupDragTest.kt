// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import io.github.suqi8.coui.kmp.overlay.OverlayListPopup
import io.github.suqi8.coui.kmp.theme.COUITheme
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
    fun aDisabledRowFadesTheMaskInPlaceAndTheNextRowPressesBackIn() = runComposeUiTest {
        val host = PopupListGestureHost()
        var picked = -1
        setRows(host, enabled = listOf(true, false, true), onPick = { picked = it })
        mainClock.autoAdvance = false
        val rows = host.sortedItems()

        onRoot().performTouchInput { down(host.centerOf(0)) }
        mainClock.advanceTimeBy(400)
        assertEquals(1f, host.mask.press, 0.01f)

        // Finger onto the disabled row: COUITouchRecyclerView.clearSharedMask(true) — the mask
        // keeps its bounds on row 0 and only its press fades.
        onRoot().performTouchInput { moveTo(host.centerOf(1)) }
        var last = host.sample()
        repeat(6) {
            mainClock.advanceTimeByFrame()
            val now = host.sample()
            assertEquals(rows[0].top, now.top, "frame $it over the disabled row: mask stays put")
            assertTrue(now.press <= last.press + 1e-4f, "frame $it: the press only fades")
            last = now
        }
        assertTrue(last.press in 0.05f..0.95f, "the mask is mid-fade (${last.press})")

        // Finger onto row 2: attachSharedMaskToView after an exit snaps to the row and presses in
        // from what is left of the fade, never from a full press.
        onRoot().performTouchInput { moveTo(host.centerOf(2)) }
        mainClock.advanceTimeByFrame()
        val entered = host.sample()
        assertEquals(rows[2].top, entered.top, "mask moved to row 2")
        assertTrue(
            entered.press <= last.press + maxTravelStep,
            "press resumed from the fade (${last.press} -> ${entered.press})",
        )
        var press = entered.press
        repeat(20) {
            mainClock.advanceTimeByFrame()
            val now = host.sample()
            assertEquals(rows[2].top, now.top, "frame $it: no travel after the snap")
            assertTrue(now.press - press <= maxTravelStep, "frame $it: press steps smoothly")
            press = now.press
        }
        onRoot().performTouchInput { up() }
        mainClock.advanceTimeBy(500)
        assertEquals(2, picked)
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
}
