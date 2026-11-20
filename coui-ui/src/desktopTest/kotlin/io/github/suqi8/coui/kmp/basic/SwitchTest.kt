// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SwitchTest {

    @Test
    fun stretchFollowsThePositionCurve() {
        assertEquals(1f, SwitchMotion.thumbStretch(0f), 1e-4f)
        assertEquals(1.3f, SwitchMotion.thumbStretch(0.34725848f), 1e-4f)
        assertEquals(1f, SwitchMotion.thumbStretch(1f), 1e-4f)
        // Spring overshoot past the end squashes the thumb a little.
        assertTrue(SwitchMotion.thumbStretch(1.046f) < 1f)
    }

    @Test
    fun releaseAttachesToTheNearerEndUnlessFlung() {
        assertEquals(0f, SwitchMotion.attachTarget(0.4f, velocity = 0f, velocityLimit = 14f))
        assertEquals(1f, SwitchMotion.attachTarget(0.6f, velocity = 0f, velocityLimit = 14f))
        assertEquals(1f, SwitchMotion.attachTarget(0.1f, velocity = 14f, velocityLimit = 14f))
        assertEquals(0f, SwitchMotion.attachTarget(0.9f, velocity = -20f, velocityLimit = 14f))
        assertEquals(1f, SwitchMotion.attachTarget(0.9f, velocity = -5f, velocityLimit = 14f))
    }

    @Test
    fun blendIsPerChannelArgb() {
        val blended = SwitchMotion.blendArgb(Color(0x29000000), Color(0xFF0080FF), 0.5f)
        assertEquals((0x29 / 255f + 1f) / 2f, blended.alpha, 0.01f)
        assertEquals(0.5f, blended.blue, 0.01f)
    }

    @Test
    fun trackIsTheCoui17Size() = runComposeUiTest {
        setContent {
            COUITheme {
                Switch(checked = false, onCheckedChange = {}, modifier = Modifier.testTag("switch"))
            }
        }
        onNodeWithTag("switch").assertWidthIsEqualTo(44.dp).assertHeightIsEqualTo(24.dp)
    }

    @Test
    fun tapToggles() = runComposeUiTest {
        var checked by mutableStateOf(false)
        setContent {
            COUITheme {
                Switch(checked = checked, onCheckedChange = { checked = it }, modifier = Modifier.testTag("switch"))
            }
        }
        onNodeWithTag("switch").performClick()
        waitForIdle()
        assertTrue(checked)
    }

    @Test
    fun draggingTheThumbAcrossChecksOnceWithoutATap() = runComposeUiTest {
        var checked by mutableStateOf(false)
        var calls by mutableIntStateOf(0)
        setDraggableSwitch(initial = { checked }) {
            calls++
            checked = it
        }
        onNodeWithTag("switch").performTouchInput {
            down(centerLeft + Offset(8f, 0f))
            repeat(10) { moveBy(Offset(width / 10f, 0f)) }
        }
        // Hold still so the drag spring catches up and the release carries no fling.
        mainClock.advanceTimeBy(1000)
        onNodeWithTag("switch").performTouchInput { up() }
        mainClock.advanceTimeBy(2000)
        assertTrue(checked, "dragging to the on end must check the switch")
        assertEquals(1, calls, "the drag must not also fire the tap toggle")
    }

    @Test
    fun aDragReleasedNearItsStartKeepsTheState() = runComposeUiTest {
        var checked by mutableStateOf(true)
        var calls by mutableIntStateOf(0)
        setDraggableSwitch(initial = { checked }) {
            calls++
            checked = it
        }
        onNodeWithTag("switch").performTouchInput {
            down(centerRight - Offset(8f, 0f))
            // Past the touch slop, but less than half the travel: the thumb follows the finger one
            // to one, so 18% of the 44dp track is 8dp of the 20dp travel.
            repeat(5) { moveBy(Offset(-width * 0.036f, 0f)) }
        }
        mainClock.advanceTimeBy(1000)
        onNodeWithTag("switch").performTouchInput { up() }
        mainClock.advanceTimeBy(2000)
        assertTrue(checked, "a drag released nearer the on end must leave the switch on")
        assertEquals(0, calls, "neither the drag nor a tap may report a change")
    }

    @Test
    fun theThumbFollowsTheFingerOneToOne() = runComposeUiTest {
        var checked by mutableStateOf(false)
        setDraggableSwitch(initial = { checked }) { checked = it }
        onNodeWithTag("switch").performTouchInput {
            down(centerLeft + Offset(8f, 0f))
            // 27.5% of the 44dp track is 12dp: past the middle of the 20dp travel
            // (COUISpringDragHelper positions the thumb from the raw finger delta).
            repeat(5) { moveBy(Offset(width * 0.055f, 0f)) }
        }
        mainClock.advanceTimeBy(1000)
        onNodeWithTag("switch").performTouchInput { up() }
        mainClock.advanceTimeBy(2000)
        assertTrue(checked, "a drag over more than half the travel must settle on the on end")
    }

    private fun ComposeUiTest.setDraggableSwitch(initial: () -> Boolean, onCheckedChange: (Boolean) -> Unit) {
        setContent {
            // The ColorOS reference density, so the track is wide compared to the touch slop.
            CompositionLocalProvider(LocalDensity provides Density(3.5f)) {
                COUITheme {
                    Box(Modifier.padding(32.dp)) {
                        Switch(checked = initial(), onCheckedChange = onCheckedChange, modifier = Modifier.testTag("switch"))
                    }
                }
            }
        }
        mainClock.autoAdvance = false
    }
}
