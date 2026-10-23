// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class TooltipTest {

    @Test
    fun tooltipShowsAboveTheAnchorAndTheCloseIconDismissesIt() = runComposeUiTest {
        var closed = false
        setContent {
            COUITheme {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    TooltipBox(
                        state = rememberTooltipState(initialIsVisible = true),
                        tooltip = { Tooltip(text = "Hello tooltip", onCloseClick = { closed = true }) },
                    ) {
                        Box(Modifier.size(48.dp).testTag("anchor"))
                    }
                }
            }
        }
        waitForIdle()
        val anchor = onNodeWithTag("anchor").fetchSemanticsNode().boundsInWindow
        val text = onNodeWithText("Hello tooltip").fetchSemanticsNode().boundsInWindow
        assertTrue(text.bottom <= anchor.top, "tooltip text $text must sit above the anchor $anchor")
        // The text is centred on the anchor, give or take the 42dp close-icon end margin.
        assertTrue(text.left < anchor.center.x, "tooltip $text must straddle the anchor centre ${anchor.center}")

        onNodeWithContentDescription("Close this tip").performClick()
        waitForIdle()
        assertTrue(closed, "the close icon must call onCloseClick")
        onNodeWithText("Hello tooltip").assertDoesNotExist()
    }

    @Test
    fun belowPlacementPutsTheTooltipUnderTheAnchor() = runComposeUiTest {
        setContent {
            COUITheme {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    TooltipBox(
                        state = rememberTooltipState(initialIsVisible = true),
                        tooltip = { Tooltip(text = "Below", mode = TooltipMode.Info) },
                        placement = TooltipAnchorPosition.Below,
                    ) {
                        Box(Modifier.size(48.dp).testTag("anchor"))
                    }
                }
            }
        }
        waitForIdle()
        val anchor = onNodeWithTag("anchor").fetchSemanticsNode().boundsInWindow
        val text = onNodeWithText("Below").fetchSemanticsNode().boundsInWindow
        assertTrue(text.top >= anchor.bottom, "tooltip text $text must sit below the anchor $anchor")
        onNodeWithContentDescription("Close this tip").assertDoesNotExist()
    }
}
