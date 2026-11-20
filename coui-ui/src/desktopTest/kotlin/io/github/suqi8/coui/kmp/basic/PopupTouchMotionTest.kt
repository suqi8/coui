// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.TouchMotionState
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The popup panel's touch motion (`BasePopupMenuAnimationController.makeAnimator`) is fed every
 * event of the gesture by `RoundFrameLayout.dispatchTouchEvent`, so it must keep following a finger
 * whose drag a scrolling list consumes, and a list that fits must not overscroll its rows
 * (`COUITouchRecyclerView.updateOverScrollMode`): the panel moves, not the rows inside it.
 */
@OptIn(ExperimentalTestApi::class)
class PopupTouchMotionTest {

    @Test
    fun thePanelFollowsADragTheListConsumes() = runComposeUiTest {
        val state = TouchMotionState()
        setContent {
            COUITheme {
                Box(
                    Modifier
                        .testTag("panel")
                        .size(200.dp, 240.dp)
                        .popupTouchMotion(state = state),
                ) {
                    // A list that scrolls consumes every vertical drag past the touch slop.
                    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        Box(Modifier.fillMaxWidth().height(1000.dp))
                    }
                }
            }
        }
        mainClock.autoAdvance = false
        onNodeWithTag("panel").performTouchInput {
            down(Offset(100.dp.toPx(), 60.dp.toPx()))
            for (step in 1..20) moveBy(Offset(0f, 6.dp.toPx()), delayMillis = 16)
        }
        mainClock.advanceTimeBy(600)
        val translation = state.translationY.value
        assertTrue(translation > 1f, "the panel follows the finger past the touch slop: $translation")
        assertTrue(
            state.scaleY.value > 1.001f && state.scaleX.value < 0.999f,
            "the panel stretches along the drag and narrows across it: ${state.scaleX.value} x ${state.scaleY.value}",
        )

        onNodeWithTag("panel").performTouchInput { up() }
        mainClock.advanceTimeBy(2000)
        assertEquals(0f, state.translationY.value, 0.5f, "the panel springs home")
        assertEquals(1f, state.scaleY.value, 0.002f, "the stretch springs back")
    }

    @Test
    fun aListThatFitsKeepsItsRowsStillUnderADrag() = runComposeUiTest {
        setContent {
            COUITheme {
                Box(Modifier.testTag("menu")) {
                    ListPopupColumn {
                        repeat(3) { index ->
                            DropdownImpl(
                                text = "Row $index",
                                optionSize = 3,
                                isSelected = false,
                                index = index,
                                onSelectedIndexChange = {},
                            )
                        }
                    }
                }
            }
        }
        val before = onNodeWithText("Row 1", useUnmergedTree = true).getUnclippedBoundsInRoot()
        onNodeWithTag("menu").performTouchInput {
            down(Offset(40.dp.toPx(), 28.dp.toPx()))
            for (step in 1..20) moveBy(Offset(0f, 5.dp.toPx()), delayMillis = 16)
        }
        mainClock.advanceTimeBy(500)
        val during = onNodeWithText("Row 1", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(before.top.value, during.top.value, 0.5f, "a list with no scroll range does not overscroll its rows")
        onNodeWithTag("menu").performTouchInput { up() }
    }
}
