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
import io.github.suqi8.coui.kmp.overlay.OverlayListPopup
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The popup touch light (`COUISpotLightEffectDrawable` on `RoundFrameLayout`): it glows under the
 * finger, follows a drag across the rows, and lights the sub menu for a finger relayed into it.
 * Rendered on a mid-gray surface so the source-over white light is measurable.
 */
@OptIn(ExperimentalTestApi::class)
class PopupLightTest {

    private val gray = Color(0xFF808080)
    private val rows = listOf("Alpha", "Beta", "Gamma", "Delta", "Epsilon")

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

    private fun ComposeUiTest.levels(names: List<String>): Map<String, Float> {
        val map = onRoot().captureToImage().toPixelMap()
        return names.associateWith { map.lum(rowCenter(it)) }
    }

    /** Presses Alpha, drags to Delta: the light must sit near the finger each time. */
    private fun ComposeUiTest.dragAndCheck() {
        val rest = levels(rows)
        assertTrue(rest.values.all { it < 0.55f }, "dark at rest: $rest")
        val a = rowCenter("Alpha")
        onRoot().performTouchInput { down(a) }
        mainClock.advanceTimeBy(300)
        val pressed = levels(rows)
        // The pressed row itself carries the press mask, so compare its neighbours.
        assertTrue(pressed.getValue("Beta") > pressed.getValue("Epsilon") + 0.02f, "lit near Alpha: $pressed")
        val d = rowCenter("Delta")
        onRoot().performTouchInput {
            for (s in 1..20) moveTo(a + (d - a) * (s / 20f), delayMillis = 16)
        }
        mainClock.advanceTimeBy(500)
        val moved = levels(rows)
        assertTrue(moved.getValue("Epsilon") > moved.getValue("Alpha") + 0.02f, "followed to Delta: $moved")
        onRoot().performTouchInput { up() }
    }

    @Test
    fun lightFollowsADragAcrossAPlainPopup() = runComposeUiTest {
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
                                onDismissRequest = { show = false },
                                backgroundColor = gray,
                                materialBlur = false,
                            ) {
                                ListPopupColumn {
                                    rows.forEachIndexed { index, text ->
                                        DropdownImpl(
                                            text = text,
                                            optionSize = rows.size,
                                            isSelected = false,
                                            index = index,
                                            onSelectedIndexChange = {},
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
        mainClock.autoAdvance = false
        dragAndCheck()
    }

    @Test
    fun lightFollowsADragAcrossTheMainMenu() = runComposeUiTest {
        val entries = listOf(DropdownEntry(items = rows.map { DropdownItem(text = it) }))
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
                            )
                        }
                    }
                }
            }
        }
        onNodeWithTag("anchor").performClick()
        mainClock.advanceTimeBy(1500)
        waitForIdle()
        mainClock.autoAdvance = false
        dragAndCheck()
    }

    @Test
    fun aFingerRelayedIntoTheSubMenuLightsIt() = runComposeUiTest {
        val subRows = listOf("Sub1", "Sub2", "Sub3", "Sub4")
        val entries = listOf(
            DropdownEntry(
                items = listOf(
                    DropdownItem(text = "Alpha"),
                    DropdownItem(text = "More", children = subRows.map { DropdownItem(text = it) }),
                    DropdownItem(text = "Gamma"),
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
                                backgroundColor = gray,
                                materialBlur = false,
                            )
                        }
                    }
                }
            }
        }
        onNodeWithTag("anchor").performClick()
        mainClock.advanceTimeBy(1500)
        waitForIdle()
        val start = rowCenter("More")
        onRoot().performTouchInput { down(start) }
        mainClock.advanceTimeBy(1500)
        waitForIdle()
        val before = levels(subRows)
        assertTrue(before.values.all { it < 0.55f }, "sub menu dark before the finger enters: $before")
        val target = rowCenter("Sub3")
        onRoot().performTouchInput {
            for (s in 1..30) moveTo(start + (target - start) * (s / 30f), delayMillis = 16)
        }
        mainClock.advanceTimeBy(500)
        val relayed = levels(subRows)
        assertTrue(relayed.getValue("Sub4") > 0.6f, "relayed finger lights the sub menu: $relayed")
    }
}
