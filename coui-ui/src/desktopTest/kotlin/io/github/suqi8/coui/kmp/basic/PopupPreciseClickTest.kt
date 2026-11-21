// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.overlay.OverlayListPopup
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * A dropdown row opens its menu at the finger (`PreciseClickHelper`), with the menu hosted in the
 * row's trailing slot as the dropdown preferences do, and forgets the point once the menu has gone.
 */
@OptIn(ExperimentalTestApi::class)
class PopupPreciseClickTest {

    @Test
    fun aTapWhoseDownLandsWhileTheMenuLeavesStillOpensAtTheFinger() = runComposeUiTest {
        setContent {
            COUITheme {
                Scaffold {
                    var show by remember { mutableStateOf(false) }
                    val precise = rememberPreciseClickState()
                    Box(Modifier.fillMaxSize().padding(top = 80.dp)) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("row")
                                .preciseClickAnchor(precise)
                                .clickable { show = !show },
                        ) {
                            Box(Modifier.align(Alignment.CenterEnd).size(24.dp)) {
                                OverlayListPopup(
                                    show = show,
                                    onDismissRequest = { show = false },
                                    onDismissFinished = { precise.clear() },
                                    preciseClickState = precise,
                                ) {
                                    ListPopupColumn {
                                        DropdownImpl(
                                            text = "Option",
                                            optionSize = 1,
                                            isSelected = false,
                                            index = 0,
                                            onSelectedIndexChange = { show = false },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        val rowWidth = onNodeWithTag("row").getBoundsInRoot().right
        val left = Offset(40f, 28f)

        onNodeWithTag("row").performTouchInput { click(left) }
        mainClock.advanceTimeBy(1500)
        waitForIdle()
        assertTrue(onNodeWithText("Option").getBoundsInRoot().left < rowWidth / 2, "the menu opens at the finger")

        // Driven by hand: an idle wait would run the exit to its end before the next down.
        mainClock.autoAdvance = false
        onNodeWithText("Option").performClick()
        mainClock.advanceTimeBy(100)
        // The next tap goes down while the menu is still leaving and lifts after it has gone, so
        // the dismissal finishes between the down and the click.
        onNodeWithTag("row").performTouchInput { down(left) }
        mainClock.advanceTimeBy(2000)
        onNodeWithTag("row").performTouchInput { up() }
        mainClock.advanceTimeBy(1500)
        waitForIdle()
        assertTrue(
            onNodeWithText("Option").getBoundsInRoot().left < rowWidth / 2,
            "the reopened menu is at the finger, not on the anchor at the row's end",
        )
    }
}
