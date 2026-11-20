// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class TouchMotionGestureTest {

    private fun ComposeUiTest.setButton() {
        setContent {
            COUITheme {
                Box(Modifier.padding(64.dp)) {
                    TextButton(text = "Button", onClick = {}, modifier = Modifier.testTag("button"))
                }
            }
        }
        mainClock.autoAdvance = false
    }

    private fun ComposeUiTest.centerX(): Float {
        val bounds = onNodeWithTag("button").getBoundsInRoot()
        return ((bounds.left + bounds.right) / 2).value
    }

    private fun ComposeUiTest.width(): Float {
        val bounds = onNodeWithTag("button").getBoundsInRoot()
        return (bounds.right - bounds.left).value
    }

    @Test
    fun draggingTheButtonStretchesAndFollowsTheFingerThenSpringsBack() = runComposeUiTest {
        setButton()
        val rest = onNodeWithTag("button").getBoundsInRoot()

        onNodeWithTag("button").performTouchInput {
            down(center)
            moveBy(Offset(40f, 0f))
            moveBy(Offset(40f, 0f))
        }
        mainClock.advanceTimeBy(1000)
        val held = onNodeWithTag("button").getBoundsInRoot()
        val restCenter = (rest.left + rest.right) / 2
        val heldCenter = (held.left + held.right) / 2
        assertTrue(heldCenter > restCenter, "the rubber band must pull the button right: $restCenter -> $heldCenter")
        // The deformation stretches along the drag and squeezes across it (the uniform press scale
        // keeps the aspect ratio).
        val restRatio = (rest.right - rest.left) / (rest.bottom - rest.top)
        val heldRatio = (held.right - held.left) / (held.bottom - held.top)
        assertTrue(heldRatio > restRatio, "the drag must stretch the button: $restRatio -> $heldRatio")

        onNodeWithTag("button").performTouchInput { up() }
        mainClock.advanceTimeBy(3000)
        val released = onNodeWithTag("button").getBoundsInRoot()
        assertTrue(abs(((released.left + released.right) / 2 - restCenter).value) < 0.5f, "the button must spring home")
    }

    @Test
    fun aQuickTapKeepsSinkingAfterTheRelease() = runComposeUiTest {
        // COUIPressAnimatorUnit.backAnimation re-targets the running press spring, so the release
        // takes over the press-in's momentum: the button shrinks on for a few frames after the
        // finger lifts, then the release spring bounces it back.
        setButton()
        val restWidth = width()
        onNodeWithTag("button").performTouchInput { down(center) }
        mainClock.advanceTimeBy(80)
        onNodeWithTag("button").performTouchInput { up() }
        mainClock.advanceTimeBy(32)
        val early = width()
        mainClock.advanceTimeBy(32)
        val later = width()
        assertTrue(later < early - 0.5f, "the released button must keep sinking: $early -> $later")

        mainClock.advanceTimeBy(3000)
        assertTrue(abs(width() - restWidth) < 0.05f, "the button must settle at its rest size: $restWidth vs ${width()}")
    }

    @Test
    fun touchingASpringingButtonCatchesItWhereItIs() = runComposeUiTest {
        // COUIITouchMotionUnit.handleTouchDown cancels the running rubber spring: a finger that
        // lands while the button springs back holds it in place.
        setButton()
        val restCenter = centerX()
        onNodeWithTag("button").performTouchInput {
            down(center)
            moveBy(Offset(40f, 0f))
            moveBy(Offset(40f, 0f))
        }
        mainClock.advanceTimeBy(1000)
        onNodeWithTag("button").performTouchInput { up() }
        mainClock.advanceTimeBy(48)
        assertTrue(centerX() > restCenter + 0.5f, "the button must still be springing back")

        onNodeWithTag("button").performTouchInput { down(center) }
        mainClock.advanceTimeBy(32)
        val caught = centerX()
        mainClock.advanceTimeBy(48)
        assertTrue(abs(centerX() - caught) < 0.05f, "the touched button must hold still: $caught vs ${centerX()}")
        assertTrue(caught > restCenter + 0.1f, "the button must be held away from its rest position")

        onNodeWithTag("button").performTouchInput { up() }
        mainClock.advanceTimeBy(3000)
        assertTrue(abs(centerX() - restCenter) < 0.5f, "the released button must spring home")
    }
}
