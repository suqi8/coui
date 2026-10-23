// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class PopupGestureTest {

    @Test
    fun dragAcrossHorizontalCellsSelectsTheCellUnderTheFinger() = runComposeUiTest {
        var clicked = -1
        setContent {
            COUITheme {
                Box(Modifier.testTag("menu")) {
                    ListPopupColumn {
                        DropdownHorizontalItems(
                            items = List(5) { DropdownItem(text = "") },
                            onItemClick = { clicked = it },
                        )
                    }
                }
            }
        }
        val menu = onNodeWithTag("menu").getUnclippedBoundsInRoot()
        // Cells: 12dp edge, 40dp wide, 8dp apart, below the 8dp list inset.
        val y = 8f + 20f
        onNodeWithTag("menu").performTouchInput {
            down(Offset((12f + 20f).dp.toPx(), y.dp.toPx()))
            moveTo(Offset((12f + 3 * 48f + 20f).dp.toPx(), y.dp.toPx()))
            up()
        }
        waitForIdle()
        assertEquals(3, clicked, "menu $menu")
    }

    @Test
    fun aLongPressCarriesTheFingerIntoTheSubMenu() = runComposeUiTest {
        var picked = ""
        var opened = false
        setContent {
            COUITheme {
                val relay = remember { PopupGestureRelay() }
                CompositionLocalProvider(LocalPopupGestureRelay provides relay) {
                    Row(Modifier.testTag("menus")) {
                        Box(Modifier.width(200.dp)) {
                            CompositionLocalProvider(LocalPopupListRole provides PopupListRole.Main) {
                                ListPopupColumn {
                                    PopupDropdownRow(
                                        item = DropdownItem(text = "More"),
                                        isSelected = false,
                                        dropdownColors = DropdownDefaults.dropdownColors(),
                                        enabled = true,
                                        hasSubmenu = true,
                                        submenuExpanded = false,
                                        submenuHeader = false,
                                        onClick = {},
                                        onLongClick = {
                                            opened = true
                                            relay.subEntered = true
                                        },
                                    )
                                }
                            }
                        }
                        Box(Modifier.width(200.dp)) {
                            CompositionLocalProvider(LocalPopupListRole provides PopupListRole.Sub) {
                                ListPopupColumn {
                                    listOf("A", "B", "C").forEach { text ->
                                        PopupDropdownRow(
                                            item = DropdownItem(text = text),
                                            isSelected = false,
                                            dropdownColors = DropdownDefaults.dropdownColors(),
                                            enabled = true,
                                            hasSubmenu = false,
                                            submenuExpanded = false,
                                            submenuHeader = false,
                                            onClick = { picked = text },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        onRoot().performTouchInput {
            down(Offset(100.dp.toPx(), 28.dp.toPx()))
            advanceEventTime(800)
        }
        mainClock.advanceTimeBy(800)
        waitForIdle()
        onRoot().performTouchInput {
            // Into the sub list's second row (8dp inset + 40dp + half a row).
            moveTo(Offset(300.dp.toPx(), 68.dp.toPx()))
            moveTo(Offset(300.dp.toPx(), 70.dp.toPx()))
            up()
        }
        waitForIdle()
        assertEquals(true, opened)
        assertEquals("B", picked)
    }
}
