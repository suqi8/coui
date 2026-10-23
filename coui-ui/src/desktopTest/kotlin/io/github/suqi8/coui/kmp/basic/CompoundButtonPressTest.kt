// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class CompoundButtonPressTest {

    /** Holds the control down for a second and returns (rest, held) colors sampled [offsetDp] right of its centre. */
    private fun ComposeUiTest.pressSample(offsetDp: Int, content: @Composable () -> Unit): Pair<Any, Any> {
        setContent {
            COUITheme {
                Box(Modifier.padding(32.dp)) { content() }
            }
        }
        mainClock.autoAdvance = false
        val rest = onRoot().captureToImage().toPixelMap()
        onNodeWithTag("control").performTouchInput { down(center) }
        mainClock.advanceTimeBy(1000)
        val held = onRoot().captureToImage().toPixelMap()
        // The control's centre: 32dp padding + half of the 24dp box.
        val c = ((32 + 12) * density.density).toInt()
        val x = ((32 + 12 + offsetDp) * density.density).toInt()
        return rest[x, c] to held[x, c]
    }

    @Test
    fun checkboxPressDrawsA40dpCircleOutsideTheBox() = runComposeUiTest {
        // 16dp from the centre: outside the 24dp box, inside the 20dp-radius highlight.
        val (rest, held) = pressSample(16) {
            Checkbox(state = ToggleableState.Off, onClick = {}, modifier = Modifier.testTag("control"))
        }
        assertTrue(rest != held, "a held checkbox must show its circular highlight: $rest vs $held")
    }

    @Test
    fun checkboxHighlightStopsAtItsRadius() = runComposeUiTest {
        // 22dp from the centre is outside the 20dp highlight radius.
        val (rest, held) = pressSample(22) {
            Checkbox(state = ToggleableState.Off, onClick = {}, modifier = Modifier.testTag("control"))
        }
        assertEquals(rest, held)
    }

    @Test
    fun nonInteractiveCheckboxHasNoHighlight() = runComposeUiTest {
        val (rest, held) = pressSample(16) {
            Checkbox(state = ToggleableState.Off, onClick = null, modifier = Modifier.testTag("control"))
        }
        assertEquals(rest, held)
    }

    @Test
    fun radioButtonPressDrawsTheBorderlessRippleCircle() = runComposeUiTest {
        // 15dp from the centre: outside the 24dp box, inside the 17dp-radius highlight.
        val (rest, held) = pressSample(15) {
            RadioButton(selected = false, onClick = {}, modifier = Modifier.testTag("control"))
        }
        assertTrue(rest != held, "a held radio button must show its circular highlight: $rest vs $held")
    }

    @Test
    fun radioButtonHighlightStopsAtItsRadius() = runComposeUiTest {
        val (rest, held) = pressSample(19) {
            RadioButton(selected = false, onClick = {}, modifier = Modifier.testTag("control"))
        }
        assertEquals(rest, held)
    }
}
