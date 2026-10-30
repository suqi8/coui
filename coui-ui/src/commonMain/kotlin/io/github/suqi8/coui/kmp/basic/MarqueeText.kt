// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.LocalTextStyles

/**
 * A [MarqueeText] component with COUI style: single-line text that scrolls endlessly when it does
 * not fit, with both edges faded, after COUI `COUIMarqueeTextView` (the content view of
 * `COUIDefaultTopTips` / `COUIMarqueeTopTips`).
 *
 * - **Fit**: when the text fits, it is drawn still and unfaded (`setActualMarqueeByMeasured(false)`
 *   zeroes the fading edge strength).
 * - **Scroll**: otherwise the text starts [startOffset] in from the start edge
 *   (`coui_top_tips_scroll_text_start_location`) and, after [initialDelayMillis], scrolls
 *   continuously at [velocity] (`coui_top_tips_scroll_speed` per second: COUI moves
 *   `25dp / refreshRate` per frame) with copies [spacing] apart (`coui_top_tips_scroll_text_interval`)
 *   and no pause between loops. RTL text scrolls the other way.
 * - **Fade**: while scrolling both edges fade over [fadingEdgeLength]
 *   (`coui_top_tips_fading_edge_size`; `getLeft/RightFadingEdgeStrength` are both 1).
 *
 * With [enabled] off the text is laid out as a plain, wrapping [Text] (`setMarqueeEnable(false)`).
 *
 * @param text The text to be displayed.
 * @param modifier The modifier to be applied to the [MarqueeText].
 * @param enabled Whether the marquee is on (COUI `setMarqueeEnable`). When off the text wraps.
 * @param color The text color; [Color.Unspecified] falls back to [style] and the content color.
 * @param style The [TextStyle] of the text.
 * @param velocity The scroll speed, in dp per second.
 * @param initialDelayMillis The delay before the first scroll (COUI `DEFAULT_SCROLL_DELAY_DURATION`).
 * @param spacing The gap between the end of the text and the start of its next copy.
 * @param startOffset The inset of the text from the start edge before it scrolls.
 * @param fadingEdgeLength The length of the fade at each edge while scrolling.
 */
@Composable
fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyles.current.main,
    velocity: Dp = MarqueeTextDefaults.Velocity,
    initialDelayMillis: Int = MarqueeTextDefaults.InitialDelayMillis,
    spacing: Dp = MarqueeTextDefaults.Spacing,
    startOffset: Dp = MarqueeTextDefaults.StartOffset,
    fadingEdgeLength: Dp = MarqueeTextDefaults.FadingEdgeLength,
) {
    if (!enabled) {
        Text(text = text, modifier = modifier, color = color, style = style)
        return
    }
    var containerWidth by remember { mutableIntStateOf(0) }
    var textWidth by remember { mutableIntStateOf(0) }
    // COUIMarqueeTextView.onMeasure: the marquee (and its fading edges) only runs when the measured
    // text is wider than the view.
    val overflowing = containerWidth in 1 until textWidth
    // COUI draws each copy as `text + spaces(80dp)` starting 10dp in, so one loop is text + 80dp:
    // the start offset is taken out of the marquee spacing and put in front of the text instead.
    val loopSpacing = (spacing - startOffset).coerceAtLeast(0.dp)
    Text(
        text = text,
        color = color,
        style = style,
        maxLines = 1,
        softWrap = false,
        onTextLayout = { textWidth = it.size.width },
        modifier = modifier
            .onSizeChanged { containerWidth = it.width }
            .then(if (overflowing) Modifier.marqueeFadingEdges(fadingEdgeLength) else Modifier)
            .basicMarquee(
                iterations = Int.MAX_VALUE,
                repeatDelayMillis = 0,
                initialDelayMillis = initialDelayMillis,
                spacing = MarqueeSpacing(loopSpacing),
                velocity = velocity,
            )
            .then(if (overflowing) Modifier.padding(start = startOffset) else Modifier),
    )
}

/**
 * Fades both horizontal edges of the content to transparent over [length], the Android
 * `TextView` horizontal fading edge at strength 1.
 */
private fun Modifier.marqueeFadingEdges(length: Dp): Modifier = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val fade = length.toPx().coerceAtMost(size.width / 2f)
        if (fade <= 0f) return@drawWithContent
        drawRect(
            brush = Brush.horizontalGradient(0f to Color.Transparent, 1f to Color.Black, startX = 0f, endX = fade),
            size = Size(fade, size.height),
            blendMode = BlendMode.DstIn,
        )
        drawRect(
            brush = Brush.horizontalGradient(
                0f to Color.Black,
                1f to Color.Transparent,
                startX = size.width - fade,
                endX = size.width,
            ),
            topLeft = Offset(size.width - fade, 0f),
            size = Size(fade, size.height),
            blendMode = BlendMode.DstIn,
        )
    }

/** Contains default values used by [MarqueeText]. */
object MarqueeTextDefaults {

    /** The scroll speed per second (COUI `coui_top_tips_scroll_speed`). */
    val Velocity = 25.dp

    /** The delay before the first scroll (COUIMarqueeTextView `DEFAULT_SCROLL_DELAY_DURATION`). */
    val InitialDelayMillis = 1000

    /** The gap between consecutive copies (COUI `coui_top_tips_scroll_text_interval`). */
    val Spacing = 80.dp

    /** The resting inset of the text from the start edge (COUI `coui_top_tips_scroll_text_start_location`). */
    val StartOffset = 10.dp

    /** The length of each fading edge (COUI `coui_top_tips_fading_edge_size`). */
    val FadingEdgeLength = 26.dp
}
