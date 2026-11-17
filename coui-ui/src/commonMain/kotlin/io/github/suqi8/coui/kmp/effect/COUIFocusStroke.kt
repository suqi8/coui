// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme

/** Defaults for [couiFocusStroke]. */
object COUIFocusStrokeDefaults {
    /**
     * The visible ring width, `default_focus_stroke_radius` (3dp). `COUIStrokeDrawable` strokes
     * the outline with a paint twice as wide and clips one half away.
     */
    val StrokeWidth: Dp = 3.dp
}

// COUIStrokeDrawable focus animator: COUISpringForce response 0.3 / bounce 0 -> stiffness 438.65.
private val FocusStrokeSpring = spring<Float>(dampingRatio = 1f, stiffness = 438.65f)

/**
 * The COUI keyboard-focus ring, after `COUIStrokeDrawable`, which `COUIButton`, `COUISwitch`,
 * `COUIFloatingButton`, `COUISegmentButtonLayout`, `COUIStepperView` and the chips layer over
 * their background.
 *
 * While [interactionSource] reports focus, the outline of [shape] is stroked [strokeWidth] wide in
 * [color] (`?couiColorFocusOutline`), outside the shape by default (`TYPE_OUTER`, a clip with
 * `Region.Op.DIFFERENCE`) or inside it when [inner] is `true` (`TYPE_INNER`, the segment button
 * items). The ring fades in and out on a response 0.3 / bounce 0 spring, and is cleared at once
 * when [enabled] turns `false`. `COUIStrokeDrawable` interpolates the paint colour from fully
 * transparent; here the ring's alpha is animated instead.
 *
 * An outer ring is drawn past the bounds of the node, so the ring must not be clipped by the node
 * or a parent layer.
 *
 * @param interactionSource The interaction source that reports the focus state.
 * @param shape The outline the ring follows, the same shape the component's background uses.
 * @param enabled Whether the component is enabled; a disabled component never shows the ring.
 * @param inner Whether the ring is drawn inside the outline instead of around it.
 * @param color The ring colour.
 * @param strokeWidth The visible ring width.
 */
@Composable
fun Modifier.couiFocusStroke(
    interactionSource: InteractionSource,
    shape: Shape,
    enabled: Boolean = true,
    inner: Boolean = false,
    color: Color = COUITheme.colorScheme.focusOutline,
    strokeWidth: Dp = COUIFocusStrokeDefaults.StrokeWidth,
): Modifier {
    val focused by interactionSource.collectIsFocusedAsState()
    val progress = remember { Animatable(0f) }
    LaunchedEffect(enabled, focused) {
        if (!enabled) {
            progress.snapTo(0f)
        } else {
            progress.animateTo(if (focused) 1f else 0f, FocusStrokeSpring)
        }
    }
    return this.drawWithCache {
        val path = Path().apply { addOutline(shape.createOutline(size, layoutDirection, this@drawWithCache)) }
        val stroke = Stroke(width = strokeWidth.toPx() * 2f)
        val clipOp = if (inner) ClipOp.Intersect else ClipOp.Difference
        onDrawWithContent {
            drawContent()
            val alpha = progress.value
            if (alpha > 0f) {
                clipPath(path, clipOp) {
                    drawPath(path = path, color = color, alpha = alpha.coerceIn(0f, 1f), style = stroke)
                }
            }
        }
    }
}
