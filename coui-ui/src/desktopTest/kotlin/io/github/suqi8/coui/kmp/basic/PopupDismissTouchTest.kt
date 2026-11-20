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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.overlay.OverlayListPopup
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * `COUIPopupListWindow.dismiss` makes the popup window untouchable before its exit animation runs,
 * so the page takes touches again while the menu animates out; only a showing popup swallows a tap
 * outside it (to dismiss).
 */
@OptIn(ExperimentalTestApi::class)
class PopupDismissTouchTest {

    @Test
    fun aClosingPopupLetsThePageTakeTouches() = runComposeUiTest {
        var pageClicks = 0
        setContent {
            COUITheme {
                Scaffold {
                    var show by remember { mutableStateOf(false) }
                    Box(Modifier.fillMaxSize()) {
                        Box(Modifier.align(Alignment.TopCenter)) {
                            TextButton(
                                text = "Anchor",
                                onClick = { show = true },
                                modifier = Modifier.testTag("anchor").padding(top = 16.dp),
                            )
                            OverlayListPopup(show = show, onDismissRequest = { show = false }) {
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
                        TextButton(
                            text = "Page",
                            onClick = { pageClicks++ },
                            modifier = Modifier.align(Alignment.BottomCenter).testTag("page").padding(bottom = 40.dp),
                        )
                    }
                }
            }
        }
        onNodeWithTag("anchor").performClick()
        mainClock.advanceTimeBy(1500)
        waitForIdle()
        mainClock.autoAdvance = false

        // A showing popup takes the tap outside it, which only dismisses it.
        onNodeWithTag("page").performClick()
        repeat(3) { mainClock.advanceTimeByFrame() }
        assertEquals(0, pageClicks, "the tap that dismisses the popup does not reach the page")

        // The popup is still animating out, yet the page already takes the next tap.
        onNodeWithTag("page").performClick()
        mainClock.advanceTimeByFrame()
        assertEquals(1, pageClicks, "the page takes touches while the popup leaves")
        mainClock.advanceTimeBy(1000)
    }
}
