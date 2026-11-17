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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.overlay.OverlayCascadingListPopup
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The shared press mask of a popup list drawn frame by frame: it must follow a drag-to-select, fade
 * when the finger leaves the panel and follow again once the finger comes back
 * (`COUITouchRecyclerView.attachSharedMaskToView` / `clearSharedMask(true)`), on the example's
 * cascading menu (horizontal cells, group dividers, sub-menu rows).
 */
@OptIn(ExperimentalTestApi::class)
class PopupMaskReentryTest {

    private val gray = Color(0xFF808080)

    private fun ComposeUiTest.rowCenter(text: String): Offset {
        val r = onNodeWithText(text, useUnmergedTree = true).getUnclippedBoundsInRoot()
        return with(density) { Offset((r.left + 120.dp).toPx(), ((r.top + r.bottom) / 2).toPx()) }
    }

    private fun PixelMap.lum(at: Offset): Float {
        var sum = 0f
        var n = 0
        for (dx in -2..2) {
            for (dy in -2..2) {
                val x = (at.x + dx * 3).toInt().coerceIn(0, width - 1)
                val y = (at.y + dy * 3).toInt().coerceIn(0, height - 1)
                val c = this[x, y]
                sum += (c.red + c.green + c.blue) / 3f
                n++
            }
        }
        return sum / n
    }

    @Test
    fun maskFollowsAFingerThatLeavesAndReentersThePanel() = runComposeUiTest {
        val entries = listOf(
            DropdownEntry(horizontal = true, items = listOf(DropdownItem(text = "Edit"), DropdownItem(text = "Sort"))),
            DropdownEntry(
                items = listOf(
                    DropdownItem(text = "New folder"),
                    DropdownItem(
                        text = "Sort by",
                        summary = "Name",
                        children = listOf(DropdownItem(text = "N1"), DropdownItem(text = "N2")),
                    ),
                    DropdownItem(text = "View", children = listOf(DropdownItem(text = "List"), DropdownItem(text = "Grid"))),
                ),
            ),
            DropdownEntry(items = listOf(DropdownItem(text = "Delete", alert = true))),
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
                                backgroundColor = gray,
                                materialBlur = false,
                                materialEffect = false,
                            )
                        }
                    }
                }
            }
        }
        onNodeWithTag("anchor").performClick()
        mainClock.advanceTimeBy(1500)
        waitForIdle()
        val names = listOf("New folder", "Sort by", "View", "Delete")
        fun masked(): String {
            val map = onRoot().captureToImage().toPixelMap()
            return names.minBy { map.lum(rowCenter(it)) }
        }
        fun crawl(from: Offset, to: Offset) {
            onRoot().performTouchInput {
                for (k in 1..15) moveTo(from + (to - from) * (k / 15f), delayMillis = 16)
            }
            mainClock.advanceTimeBy(300)
        }
        val view = rowCenter("View")
        val nf = rowCenter("New folder")
        val del = rowCenter("Delete")
        val below = del + Offset(0f, 150f)
        onRoot().performTouchInput { down(view) }
        mainClock.advanceTimeBy(300)
        assertEquals("View", masked())
        crawl(view, nf)
        assertEquals("New folder", masked())
        crawl(nf, below)
        crawl(below, del)
        assertEquals("Delete", masked())
        crawl(del, nf)
        assertEquals("New folder", masked())
        onRoot().performTouchInput { up() }
        mainClock.advanceTimeBy(500)
    }
}
