// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ListPopupColumnTest {

    private fun assertDp(expected: Dp, actual: Dp, what: String) {
        assertTrue(abs(expected.value - actual.value) < 0.6f, "$what: expected $expected, was $actual")
    }

    @Test
    fun shortRowsUseTheMinimumWidthAndFortyDpRows() = runComposeUiTest {
        setContent {
            COUITheme {
                Box(Modifier.testTag("menu")) {
                    ListPopupColumn {
                        listOf("One", "Two", "Three").forEachIndexed { index, text ->
                            DropdownImpl(
                                item = DropdownItem(text = text),
                                optionSize = 3,
                                isSelected = index == 0,
                                index = index,
                                onSelectedIndexChange = {},
                            )
                        }
                    }
                }
            }
        }
        val menu = onNodeWithTag("menu").getUnclippedBoundsInRoot()
        assertDp(168.dp, menu.width, "menu width")
        // 8dp list inset + three 40dp rows + 8dp list inset.
        assertDp(136.dp, menu.height, "menu height")
        val two = onNodeWithText("Two", useUnmergedTree = true).getUnclippedBoundsInRoot()
        // Second row: inset 8 + first row 40, title centred in the 40dp row.
        assertDp(68.dp, (two.top + two.bottom) / 2, "second row centre")
        assertDp(20.dp, two.left, "row start padding")
    }

    @Test
    fun aSubmenuRowWidensTheMenu() = runComposeUiTest {
        setContent {
            COUITheme {
                Box(Modifier.testTag("menu")) {
                    ListPopupColumn {
                        DropdownImpl(
                            item = DropdownItem(text = "More"),
                            optionSize = 1,
                            isSelected = false,
                            index = 0,
                            hasSubmenu = true,
                            onSelectedIndexChange = {},
                        )
                    }
                }
            }
        }
        assertDp(196.dp, onNodeWithTag("menu").getUnclippedBoundsInRoot().width, "menu width")
    }

    @Test
    fun theViewportEndsOnAWholeRow() = runComposeUiTest {
        setContent {
            COUITheme {
                Box(Modifier.heightIn(max = 130.dp).testTag("menu")) {
                    ListPopupColumn {
                        repeat(6) { index ->
                            DropdownImpl(
                                item = DropdownItem(text = "Item $index"),
                                optionSize = 6,
                                isSelected = false,
                                index = index,
                                onSelectedIndexChange = {},
                            )
                        }
                    }
                }
            }
        }
        // 16dp of insets + two whole 40dp rows; a third would overflow 130dp.
        assertDp(96.dp, onNodeWithTag("menu").getUnclippedBoundsInRoot().height, "viewport height")
    }
}
