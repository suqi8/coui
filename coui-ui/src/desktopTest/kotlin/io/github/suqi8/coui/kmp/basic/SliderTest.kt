// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SliderTest {

    @Test
    fun glitterAlphaFollowsTheCouiCurve() {
        assertEquals(0f, sliderGlitterAlpha(0f))
        assertEquals(0f, sliderGlitterAlpha(-10f))
        // computeGlitterEffectAlpha(360) = 255 * (1 - 1 / 85).
        assertEquals(1f - 1f / 85f, sliderGlitterAlpha(360f), 1e-4f)
        assertTrue(sliderGlitterAlpha(30f) < sliderGlitterAlpha(60f))
    }

    @Test
    fun glassRimGlowsAtTheEdgeAndFadesInwards() {
        val edge = sliderGlassRimAlpha(0f)
        val strokeEdge = sliderGlassRimAlpha(5f)
        val deep = sliderGlassRimAlpha(15f)
        assertEquals(0.86f, edge, 0.02f)
        assertEquals(0.5f, strokeEdge, 0.02f)
        assertTrue(deep < 0.02f, "the rim glow must fade out 15px inside the edge: $deep")
    }

    @Test
    fun tappingTheTrackJumpsToTheTappedValue() = runComposeUiTest {
        var value by mutableFloatStateOf(0f)
        var finished by mutableIntStateOf(0)
        setContent {
            COUITheme {
                Slider(
                    value = value,
                    onValueChange = { value = it },
                    onValueChangeFinished = { finished++ },
                    modifier = Modifier.width(300.dp).testTag("slider"),
                )
            }
        }
        // The cap centres sit 14dp in from either end of the 300dp track.
        onNodeWithTag("slider").performTouchInput {
            click(Offset((14 + 0.75f * 272).dp.toPx(), centerY))
        }
        waitForIdle()
        assertEquals(0.75f, value, 0.01f)
        assertEquals(1, finished)
    }

    @Test
    fun aFastReleaseKeepsTheProgressGliding() = runComposeUiTest {
        var value by mutableFloatStateOf(0.2f)
        var finished by mutableIntStateOf(0)
        setContent {
            COUITheme {
                Slider(
                    value = value,
                    onValueChange = { value = it },
                    onValueChangeFinished = { finished++ },
                    modifier = Modifier.width(300.dp).testTag("slider"),
                )
            }
        }
        onNodeWithTag("slider").performTouchInput {
            val y = centerY
            swipe(Offset((14 + 0.2f * 272).dp.toPx(), y), Offset((14 + 0.45f * 272).dp.toPx(), y), durationMillis = 40)
        }
        waitForIdle()
        assertTrue(value > 0.5f, "the fling must carry the value past the release point: $value")
        assertEquals(1, finished)
    }

    @Test
    fun pressingEnlargesTheTrack() = runComposeUiTest {
        setContent {
            COUITheme {
                Box(Modifier.padding(16.dp)) {
                    Slider(
                        value = 0f,
                        onValueChange = {},
                        modifier = Modifier.width(200.dp).testTag("slider"),
                    )
                }
            }
        }
        mainClock.autoAdvance = false
        val rest = onRoot().captureToImage().toPixelMap()

        onNodeWithTag("slider").performTouchInput { down(Offset(150.dp.toPx(), centerY)) }
        mainClock.advanceTimeBy(1000)
        val held = onRoot().captureToImage().toPixelMap()

        // 2dp above the 20dp track, centred in the 28dp layout: only the enlarged background reaches it.
        val x = ((16 + 150) * density.density).toInt()
        val y = ((16 + 4 - 2) * density.density).toInt()
        assertTrue(rest[x, y] != held[x, y], "pressing must grow the track: ${rest[x, y]} vs ${held[x, y]}")
    }

    @Test
    fun theMaterialThumbTurnsToGlassWhilePressed() = runComposeUiTest {
        setContent {
            COUITheme {
                Box(Modifier.padding(16.dp)) {
                    Slider(
                        value = 0.5f,
                        onValueChange = {},
                        modifier = Modifier.width(200.dp).testTag("slider"),
                        material = true,
                    )
                }
            }
        }
        mainClock.autoAdvance = false
        // The thumb centre: 16dp padding + 14dp cap inset + half of the 172dp track.
        val x = ((16 + 14 + 86) * density.density).toInt()
        val y = ((16 + 14) * density.density).toInt()
        val rest = onRoot().captureToImage().toPixelMap()[x, y]

        onNodeWithTag("slider").performTouchInput { down(Offset((14 + 86).dp.toPx(), centerY)) }
        mainClock.advanceTimeBy(1000)
        val held = onRoot().captureToImage().toPixelMap()[x, y]

        assertEquals(1f, rest.red, 0.01f)
        assertTrue(held.red < 0.99f, "the pressed glass thumb must let the progress show through: $held")
    }

    @Test
    fun theLayoutReservesTheEnlargedTrackHeight() = runComposeUiTest {
        setContent {
            COUITheme {
                Slider(value = 0.5f, onValueChange = {}, modifier = Modifier.width(200.dp).testTag("slider"))
            }
        }
        val bounds = onNodeWithTag("slider").getUnclippedBoundsInRoot()
        assertEquals(28f, (bounds.bottom - bounds.top).value, 0.5f)
    }

    @Test
    fun draggingPullsTheWholeSliderOnTheRubberBand() = runComposeUiTest {
        setContent {
            COUITheme {
                Box(Modifier.padding(16.dp)) {
                    Slider(value = 0.5f, onValueChange = {}, modifier = Modifier.width(300.dp).testTag("slider"))
                }
            }
        }
        mainClock.autoAdvance = false
        onNodeWithTag("slider").performTouchInput {
            down(Offset(150.dp.toPx(), centerY))
            moveBy(Offset(100.dp.toPx(), 0f))
        }
        mainClock.advanceTimeBy(1000)
        val dragged = onNodeWithTag("slider").getUnclippedBoundsInRoot()
        // TouchMotionRubberBand: (1 - 1 / (0.05 * 100 / 28 + 1)) * 28dp = 4.2dp.
        assertEquals(16f + 4.2f, dragged.left.value, 0.5f)

        onNodeWithTag("slider").performTouchInput { up() }
        mainClock.advanceTimeBy(3000)
        assertEquals(16f, onNodeWithTag("slider").getUnclippedBoundsInRoot().left.value, 0.1f)
    }
}
