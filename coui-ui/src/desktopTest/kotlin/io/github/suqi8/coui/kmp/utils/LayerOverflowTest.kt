// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class LayerOverflowTest {

    private val shadow = Shadow(radius = 8.dp, color = Color.Black)

    /** The red channel, 0..255, of the pixel [dx] dp left of the 40dp box's left edge at its centre. */
    private fun PixelMap.besideBox(density: Float, dx: Float): Int {
        val x = ((PAD - dx) * density).toInt()
        val y = ((PAD + 20) * density).toInt()
        return (this[x, y].red * 255f).toInt()
    }

    @Test
    fun aFadingLayerCutsOffTheShadowOutsideItsBounds() = runComposeUiTest {
        setContent {
            Box(Modifier.background(Color.White).padding(PAD.dp)) {
                Box(
                    Modifier
                        .size(40.dp)
                        .graphicsLayer { alpha = 0.5f }
                        .dropShadow(RoundedCornerShape(4.dp), shadow)
                        .background(Color.White),
                )
            }
        }
        val pixels = onRoot().captureToImage().toPixelMap()
        // The offscreen layer is the box's own size, so nothing of the shadow survives beside it.
        assertEquals(255, pixels.besideBox(density.density, dx = 3f))
    }

    @Test
    fun anOverflowingLayerKeepsTheShadowWhileFading() = runComposeUiTest {
        setContent {
            Box(Modifier.background(Color.White).padding(PAD.dp)) {
                Box(
                    Modifier
                        .testTag("box")
                        .size(40.dp)
                        .graphicsLayerWithOverflow(shadow.layerOverflow()) { alpha = 0.5f }
                        .dropShadow(RoundedCornerShape(4.dp), shadow)
                        .background(Color.White),
                )
            }
        }
        val pixels = onRoot().captureToImage().toPixelMap()
        val beside = pixels.besideBox(density.density, dx = 3f)
        assertTrue(beside < 245, "the shadow beside the fading box must survive, got $beside")

        // The node keeps its size and place.
        val bounds = onNodeWithTag("box").getBoundsInRoot()
        assertTrue(abs(bounds.left.value - PAD) < 0.5f && abs(bounds.top.value - PAD) < 0.5f, "moved: $bounds")
        assertTrue(abs((bounds.right - bounds.left).value - 40f) < 0.5f, "resized: $bounds")
    }

    @Test
    fun anOverflowingLayerKeepsTheTransformOriginOnTheNode() = runComposeUiTest {
        // Scaled to half about its top-start corner, the box covers only its top-start quarter.
        setContent {
            Box(Modifier.background(Color.White).padding(PAD.dp)) {
                Box(
                    Modifier
                        .size(40.dp)
                        .graphicsLayerWithOverflow(16.dp) {
                            scaleX = 0.5f
                            scaleY = 0.5f
                            transformOrigin = TransformOrigin(0f, 0f)
                        }
                        .background(Color.Black),
                )
            }
        }
        val pixels = onRoot().captureToImage().toPixelMap()
        val d = density.density
        assertEquals(0, (pixels[((PAD + 5) * d).toInt(), ((PAD + 5) * d).toInt()].red * 255f).toInt())
        assertEquals(255, (pixels[((PAD + 30) * d).toInt(), ((PAD + 30) * d).toInt()].red * 255f).toInt())
    }

    private companion object {
        const val PAD = 40
    }
}
