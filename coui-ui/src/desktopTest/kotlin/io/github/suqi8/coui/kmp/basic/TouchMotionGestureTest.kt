// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
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

    @Test
    fun draggingTheButtonStretchesAndFollowsTheFingerThenSpringsBack() = runComposeUiTest {
        setContent {
            COUITheme {
                Box(Modifier.padding(64.dp)) {
                    TextButton(text = "Button", onClick = {}, modifier = Modifier.testTag("button"))
                }
            }
        }
        mainClock.autoAdvance = false
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
}
