// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import kotlin.math.abs
import kotlin.math.max

/*
 * A graphics layer renders into an offscreen buffer the size of its bounds while its alpha is below
 * 1, a render effect is set or its compositing strategy is offscreen. Whatever its content draws
 * past those bounds, a drop shadow above all, is cut off for as long as that lasts: a fading or
 * blurring element loses its shadow mid-animation and gets it back once it settles. The modifiers
 * below grow the bounds of one layer by an overflow on every side, so out-of-bounds drawing stays
 * inside the buffer, without moving or resizing the node in its parent's layout.
 */

/**
 * The opening half of a layer overflow: lays the rest of the chain out [overflow] larger on every
 * side, reports the original size and places it [overflow] up and to the start, so the node keeps
 * its size and position. Put the layer right after it and [layerOverflowInner] right after the layer.
 */
internal fun Modifier.layerOverflowOuter(overflow: Dp): Modifier = if (overflow <= 0.dp) {
    this
} else {
    layout { measurable, constraints ->
        val extra = overflow.roundToPx()
        val placeable = measurable.measure(constraints.offset(horizontal = 2 * extra, vertical = 2 * extra))
        layout(placeable.width - 2 * extra, placeable.height - 2 * extra) {
            placeable.place(-extra, -extra)
        }
    }
}

/**
 * The closing half of a layer overflow, see [layerOverflowOuter]: pads the rest of the chain by
 * [overflow] on every side, which is what grows the layer between the two halves.
 */
internal fun Modifier.layerOverflowInner(overflow: Dp): Modifier = if (overflow <= 0.dp) {
    this
} else {
    layout { measurable, constraints ->
        val extra = overflow.roundToPx()
        val placeable = measurable.measure(constraints.offset(horizontal = -2 * extra, vertical = -2 * extra))
        layout(placeable.width + 2 * extra, placeable.height + 2 * extra) {
            placeable.place(extra, extra)
        }
    }
}

/**
 * A [graphicsLayer] whose bounds reach [overflow] past the node on every side, so a shadow drawn by
 * the content outside the node survives while the layer fades, blurs or renders offscreen. The node
 * keeps its size and position, and the [TransformOrigin] [block] sets stays relative to the node's
 * own bounds.
 */
internal fun Modifier.graphicsLayerWithOverflow(
    overflow: Dp,
    block: GraphicsLayerScope.() -> Unit,
): Modifier = if (overflow <= 0.dp) {
    graphicsLayer(block)
} else {
    this
        .layerOverflowOuter(overflow)
        .graphicsLayer {
            block()
            val extra = overflow.roundToPx().toFloat()
            val width = size.width
            val height = size.height
            if (width > 0f && height > 0f) {
                val origin = transformOrigin
                transformOrigin = TransformOrigin(
                    pivotFractionX = (origin.pivotFractionX * (width - 2 * extra) + extra) / width,
                    pivotFractionY = (origin.pivotFractionY * (height - 2 * extra) + extra) / height,
                )
            }
        }
        .layerOverflowInner(overflow)
}

/**
 * How far this drop shadow visibly reaches past its outline: the blur fades out within about three
 * sigmas (a blur radius r is a Gaussian sigma of about 0.58r), pushed out by the spread and offset.
 */
internal fun Shadow.layerOverflow(): Dp = radius * SHADOW_REACH_PER_RADIUS + spread + max(abs(offset.x.value), abs(offset.y.value)).dp

private const val SHADOW_REACH_PER_RADIUS = 1.75f
