// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Check
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.darkColorScheme
import kotlin.test.Test
import kotlin.test.assertTrue

/** Shadows must stay whole while the element carrying them fades, scales or blurs. */
@OptIn(ExperimentalTestApi::class)
class AnimatedShadowTest {

    private fun PixelMap.red(xDp: Float, yDp: Float, density: Float): Int = (this[(xDp * density).toInt(), (yDp * density).toInt()].red * 255f).toInt()

    @Test
    fun aFadingPopupKeepsItsShadow() = runComposeUiTest {
        var alpha by mutableFloatStateOf(1f)
        setContent {
            // The dark scheme casts the denser popup shadow; a white page shows it.
            COUITheme(colors = darkColorScheme()) {
                Box(Modifier.background(Color.White).padding(60.dp)) {
                    ListPopupContent(
                        popupContentSize = IntSize.Zero,
                        onPopupContentSizeChange = {},
                        fractionProgress = { 1f },
                        alphaProgress = { alpha },
                        popupLayoutPosition = PopupLayoutPosition(showBelow = true, showAbove = false, isRightAligned = false),
                        localTransformOrigin = TransformOrigin.Center,
                        modifier = Modifier.testTag("popup"),
                    ) {
                        Box(Modifier.size(160.dp, 120.dp))
                    }
                }
            }
        }
        val bounds = onNodeWithTag("popup").getBoundsInRoot()
        val x = bounds.left.value - 4f
        val y = (bounds.top.value + bounds.bottom.value) / 2f
        val rest = onRoot().captureToImage().toPixelMap().red(x, y, density.density)

        alpha = 0.5f
        waitForIdle()
        val fading = onRoot().captureToImage().toPixelMap().red(x, y, density.density)

        assertTrue(rest < 250, "the resting popup must cast a shadow beside it, got $rest")
        assertTrue(fading < 254 && fading > rest, "the half-faded popup must keep a fainter shadow beside it: rest $rest, fading $fading")
    }

    @Test
    fun aHidingFloatingNavigationBarKeepsItsShadow() = runComposeUiTest {
        var visible by mutableStateOf(true)
        setContent {
            COUITheme {
                Box(Modifier.background(Color.White).width(400.dp).padding(bottom = 60.dp)) {
                    FloatingNavigationBar(
                        modifier = Modifier.testTag("bar"),
                        visible = visible,
                        defaultWindowInsetsPadding = false,
                    ) {
                        repeat(3) { index ->
                            FloatingNavigationBarItem(
                                selected = index == 0,
                                onClick = {},
                                icon = COUIIcons.Basic.Check,
                                label = "Item $index",
                            )
                        }
                    }
                }
            }
        }
        mainClock.autoAdvance = false
        mainClock.advanceTimeBy(1000)
        val d = density.density
        val bar = onNodeWithTag("bar").getBoundsInRoot()
        // 8dp below the capsule (16dp top margin + 56dp high), at the bar's centre.
        val x = (bar.left.value + bar.right.value) / 2f
        val y = bar.top.value + 16f + 56f + 8f
        val rest = onRoot().captureToImage().toPixelMap().red(x, y, d)

        visible = false
        mainClock.advanceTimeBy(48)
        val hiding = onRoot().captureToImage().toPixelMap().red(x, y, d)

        assertTrue(rest < 252, "the capsule must cast a shadow below it, got $rest")
        assertTrue(hiding < 254, "the hiding capsule must keep its shadow below it, got $hiding")
    }

    @Test
    fun aFloatingToolbarScalesOutWithItsShadowAndHides() = runComposeUiTest {
        var visible by mutableStateOf(true)
        setContent {
            COUITheme {
                Box(Modifier.background(Color.White).padding(40.dp)) {
                    FloatingToolbar(
                        modifier = Modifier.testTag("toolbar"),
                        visible = visible,
                        color = Color.Black,
                        outSidePadding = PaddingValues(0.dp),
                    ) {
                        Box(Modifier.size(120.dp, 44.dp))
                    }
                }
            }
        }
        mainClock.autoAdvance = false
        mainClock.advanceTimeBy(1000)
        val d = density.density
        val bounds = onNodeWithTag("toolbar").getBoundsInRoot()
        val cx = (bounds.left.value + bounds.right.value) / 2f
        val cy = (bounds.top.value + bounds.bottom.value) / 2f
        // 4dp below the toolbar, where only its shadow reaches.
        val below = bounds.bottom.value + 4f
        val rest = onRoot().captureToImage().toPixelMap()
        assertTrue(rest.red(cx, cy, d) < 10, "the toolbar must be drawn at rest")
        assertTrue(rest.red(cx, below, d) < 254, "the toolbar must cast a shadow below it")

        visible = false
        mainClock.advanceTimeBy(1000)
        val hidden = onRoot().captureToImage().toPixelMap()
        assertTrue(hidden.red(cx, cy, d) == 255 && hidden.red(cx, below, d) == 255, "a hidden toolbar draws nothing")
        // It keeps its place in the layout.
        val hiddenBounds = onNodeWithTag("toolbar", useUnmergedTree = true).getBoundsInRoot()
        assertTrue(hiddenBounds == bounds, "the hidden toolbar must keep its bounds: $bounds vs $hiddenBounds")
    }
}
