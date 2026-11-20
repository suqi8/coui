// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.layout

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp

/**
 * The vertical fading edges a COUI dialog scroll view draws (`android:requiresFadingEdge="vertical"`
 * on the title, message and list scroll views): the content fades out over [fadeLength] at an edge
 * that still has content beyond it. Like `View.getTopFadingEdgeStrength` /
 * `getBottomFadingEdgeStrength` of a scroll view, a fade grows with the scrolled distance and only
 * reaches its full length once the content is [fadeLength] or more past that edge.
 *
 * Apply it before the `verticalScroll` that [scrollState] drives, so the fade stays on the viewport.
 */
internal fun Modifier.dialogFadingEdges(scrollState: ScrollState, fadeLength: Dp): Modifier = this
    .graphicsLayer {
        // The DstIn fades need their own layer, but only content that scrolls ever fades.
        compositingStrategy = if (scrollState.maxValue > 0) CompositingStrategy.Offscreen else CompositingStrategy.Auto
    }
    .drawWithContent {
        drawContent()
        val maxValue = scrollState.maxValue
        val fadePx = fadeLength.toPx().coerceAtMost(size.height / 2f)
        if (fadePx <= 0f || maxValue <= 0) return@drawWithContent
        val topLength = fadePx * (scrollState.value / fadePx).coerceIn(0f, 1f)
        if (topLength > 0f) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    startY = 0f,
                    endY = topLength,
                ),
                size = Size(size.width, topLength),
                blendMode = BlendMode.DstIn,
            )
        }
        val bottomLength = fadePx * ((maxValue - scrollState.value) / fadePx).coerceIn(0f, 1f)
        if (bottomLength > 0f) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Black, Color.Transparent),
                    startY = size.height - bottomLength,
                    endY = size.height,
                ),
                topLeft = Offset(0f, size.height - bottomLength),
                size = Size(size.width, bottomLength),
                blendMode = BlendMode.DstIn,
            )
        }
    }
