// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.utils.CouiHapticEffect
import io.github.suqi8.coui.kmp.utils.rememberCouiHaptic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * The zoom dial of the camera, the port of the camera's arc zoom bar (`ZoomArcSeekBar`, the
 * `ZoomSeekBar` subclass drawn by `CameraSeekBar`) in its expanded state: the dark dome that unfolds
 * over the lens buttons when they are long-pressed or dragged.
 *
 * The dome is the top cap of a [radius] circle centered [radius] below the top edge of the dial, a 50%
 * black disc with a 0.7dp 10% white rim. Along its top runs a logarithmic ruler: one 0.66dp x 10dp
 * radial tick every 0.1x from the first to the last entry of [stops], every octave of zoom taking the
 * same angle (the range up to 15x spans 90°, or 60° when the dial ends at 6x or below). Each entry of
 * [stops] is a full-white major tick with its value written under it; whole values are major too
 * (only multiples of 10, and of 5 above 10x, once the dial reaches past 30x), the other ticks are 15%
 * white. From 15x on, major ticks dim towards the end of the dial and the minor ones merge into one
 * translucent band. Ticks and labels fade out over 8° where the dome meets the edges of the dial. The
 * current zoom sits under a fixed accent tick at the top, written in accent above it with a "×" sign;
 * stop labels sliding under that value fade out where they meet it.
 *
 * Dragging rotates the ruler with the finger around the circle center, so the ticks under the finger
 * follow it: dragging to the right lowers the zoom. Moving slower than 200px/s, the dial stops on
 * each entry of [stops] it crosses and holds there until the finger moves on by a few dp. Past either
 * end the ruler stretches on a rubber band (4% of the finger travel, up to 2.1% of the range, the
 * ticks spreading out) and springs back in 500ms when released. Crossing an entry of [stops] (or a
 * multiple of 5 above 20x) plays a tick haptic, reaching the end of the rubber band an edge haptic.
 * Touching the label of a stop glides the dial there. A steep downward swipe, or 1.5s without a
 * touch, asks to collapse the dial through [onExpandedChange].
 *
 * [content] is the collapsed control under the dome, typically the lens buttons: it is centered
 * [CameraZoomDialDefaults.ContentCenterY] below the top of the dial and fades out while the dial is
 * expanded. Long-pressing it, dragging it sideways or swiping it up asks to expand the dial; a sideways
 * drag keeps rotating the dial in the same gesture.
 *
 * Expanding fades the dome and the ruler in over 250ms and grows the dome by 33dp on a spring
 * (350ms), collapsing reverses it in 280ms. The dial fills the width it is given; its height is the
 * part of the dome inside that width, at least [CameraZoomDialDefaults.MinHeight] and enough for
 * [content]. It always uses its fixed light-on-dark palette, since it sits over the camera preview.
 *
 * @param value The current zoom ratio, between the first and the last entry of [stops].
 * @param onValueChange Called with the new zoom ratio while the dial is dragged or glides to a stop.
 * @param expanded Whether the dial is unfolded.
 * @param onExpandedChange Called when a gesture asks to expand the dial or when it should collapse.
 * @param modifier The modifier to be applied to the dial.
 * @param enabled Whether the dial and the expand gestures accept input.
 * @param stops The zoom ratios of the lenses and other marked stops, in ascending order; the first and
 *   the last entry are the ends of the dial. Values on the 0.1x grid line up with its ticks.
 * @param radius The radius of the dome (`zoom_arc_seekbar_bg_circle_radius`).
 * @param colors The colors of the dial, see [CameraZoomDialDefaults.cameraZoomDialColors].
 * @param content The collapsed control under the dome, such as the lens buttons.
 */
@Composable
fun CameraZoomDial(
    value: Float,
    onValueChange: (Float) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    stops: List<Float> = CameraZoomDialDefaults.Stops,
    radius: Dp = CameraZoomDialDefaults.Radius,
    colors: CameraZoomDialColors = CameraZoomDialDefaults.cameraZoomDialColors(),
    content: @Composable () -> Unit = {},
) {
    val scale = remember(stops) { CameraZoomDialScale(stops) }
    val density = LocalDensity.current
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val haptic = rememberCouiHaptic()
    val scope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer()
    val metrics = remember(density, radius) { CameraZoomDialMetrics(density, radius) }
    val state = remember { CameraZoomDialState(scale, value) }

    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnExpandedChange by rememberUpdatedState(onExpandedChange)
    val currentExpanded by rememberUpdatedState(expanded)
    val currentHaptic by rememberUpdatedState(haptic)
    state.rtl = rtl
    state.onUserValue = { currentOnValueChange(it) }
    state.haptic = { currentHaptic(it) }

    LaunchedEffect(value, scale) {
        if (state.scale !== scale) {
            state.scale = scale
            state.jumpTo(value)
        } else if (!state.pointerDown && !state.animating && abs(value - state.reportedValue) > VALUE_EPSILON) {
            state.jumpTo(value)
        }
    }

    // CameraSeekBar: P (collapsed control alpha), Q (dome alpha), R (unfold) and z0 (ruler alpha).
    val contentAlpha = remember { Animatable(if (expanded) 0f else 1f) }
    val domeAlpha = remember { Animatable(if (expanded) 1f else 0f) }
    val unfold = remember { Animatable(if (expanded) 1f else COLLAPSED_UNFOLD) }
    val rulerAlpha = remember { Animatable(if (expanded) 1f else 0f) }
    LaunchedEffect(expanded) {
        if (expanded) {
            // performExpand: a (180ms, +70ms), b, d (250ms, +70ms) and c (350ms spring, +70ms).
            launch { contentAlpha.animateTo(0f, tween(180, EXPAND_DELAY, FadeOutEasing)) }
            launch { domeAlpha.animateTo(1f, tween(250, EXPAND_DELAY, FadeInEasing)) }
            launch { unfold.animateTo(1f, tween(350, EXPAND_DELAY, UnfoldEasing)) }
            launch { rulerAlpha.animateTo(1f, tween(250, EXPAND_DELAY, FadeInEasing)) }
            if (!state.pointerDown) state.scheduleCollapse(scope) { if (currentExpanded) currentOnExpandedChange(false) }
        } else {
            state.cancelCollapse()
            // g5: e (250ms), f (280ms), g (280ms spring) and h (180ms).
            launch { contentAlpha.animateTo(1f, tween(250, easing = FadeInEasing)) }
            launch { domeAlpha.animateTo(0f, tween(280, easing = FadeOutEasing)) }
            launch { unfold.animateTo(COLLAPSED_UNFOLD, tween(280, easing = FoldEasing)) }
            launch { rulerAlpha.animateTo(0f, tween(180, easing = FadeOutEasing)) }
        }
    }

    val labelStyle = remember(density, colors.labelColor) {
        TextStyle(
            color = colors.labelColor,
            fontSize = (CameraZoomDialDefaults.LabelTextSize.value / density.fontScale).sp,
            fontWeight = LabelFontWeight,
        )
    }
    val valueStyle = remember(density, colors.valueColor) {
        TextStyle(
            color = colors.valueColor,
            fontSize = (CameraZoomDialDefaults.ValueTextSize.value / density.fontScale).sp,
            fontWeight = FontWeight.Bold,
        )
    }
    val layouts = remember(textMeasurer, labelStyle, valueStyle) {
        CameraZoomDialTextCache(textMeasurer, labelStyle, valueStyle)
    }

    Layout(
        content = {
            Box(modifier = Modifier.graphicsLayer { alpha = contentAlpha.value }) {
                content()
            }
            if (expanded) {
                // While the dial is unfolded its own gestures take every touch from the collapsed control.
                Box(modifier = Modifier.pointerInput(Unit) { awaitEachGesture { awaitFirstDown(requireUnconsumed = false) } })
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                stateDescription = formatZoom(state.displayValue) + MULTIPLY_SIGN
                progressBarRangeInfo = ProgressBarRangeInfo(
                    current = value.coerceIn(scale.minZoom, scale.maxZoom),
                    range = scale.minZoom..scale.maxZoom,
                )
                if (enabled) {
                    setProgress { target ->
                        currentOnValueChange(target.coerceIn(scale.minZoom, scale.maxZoom))
                        true
                    }
                } else {
                    disabled()
                }
            }
            .pointerInput(enabled, metrics) {
                if (!enabled) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val center = Offset(size.width / 2f, metrics.radius)
                    val width = size.width.toFloat()
                    val height = size.height.toFloat()
                    var tracked = false
                    var swipedDown = false
                    try {
                        if (currentExpanded) {
                            // Hd: only touches inside the dome turn it.
                            val inside = down.position.y in 0f..height && (down.position - center).getDistance() < metrics.radius
                            if (!inside) return@awaitEachGesture
                            state.pointerDown = true
                            state.cancelCollapse()
                            state.cancelAnimation()
                            tracked = true
                            // DISCZOOM_VALUEDOWN: touching a stop label glides there.
                            state.stopAt(down.position, metrics.labelTouch)?.let { state.glideTo(scope, it) }
                            swipedDown = trackCameraZoomDial(
                                pointerId = down.id,
                                start = down.position,
                                state = state,
                                center = center,
                                width = width,
                                height = height,
                                canFollow = { true },
                                onSwipeDown = { currentOnExpandedChange(false) },
                            )
                        } else {
                            if (!state.contentHit(down.position, metrics)) return@awaitEachGesture
                            state.pointerDown = true
                            var resolved = false
                            val slop = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                                awaitTouchSlopOrCancellation(down.id) { change, _ -> change.consume() }
                                    .also { resolved = true }
                            }
                            // onLongPress and onScroll of the collapsed bar: an upward swipe or a long
                            // press expands and turns the dial once it has unfolded, a sideways drag
                            // turns it at once.
                            val followNow: Boolean? = when {
                                !resolved -> false

                                slop == null -> null

                                else -> {
                                    val over = slop.position - down.position
                                    when {
                                        -over.y >= SWIPE_THRESHOLD_PX -> false
                                        over.y > 0f -> if (abs(over.y) / abs(over.x) < TAN_30) true else null
                                        else -> true
                                    }
                                }
                            }
                            if (followNow == null) return@awaitEachGesture
                            state.cancelAnimation()
                            currentOnExpandedChange(true)
                            tracked = true
                            swipedDown = trackCameraZoomDial(
                                pointerId = down.id,
                                start = slop?.position ?: down.position,
                                state = state,
                                center = center,
                                width = width,
                                height = height,
                                canFollow = { followNow || !(unfold.isRunning && unfold.targetValue == 1f) },
                                onSwipeDown = { currentOnExpandedChange(false) },
                            )
                        }
                    } finally {
                        state.pointerDown = false
                        if (tracked) {
                            state.release(scope)
                            if (!swipedDown) {
                                state.scheduleCollapse(scope) { if (currentExpanded) currentOnExpandedChange(false) }
                            }
                        }
                    }
                }
            }
            .drawBehind {
                drawCameraZoomDial(
                    state = state,
                    metrics = metrics,
                    colors = colors,
                    layouts = layouts,
                    domeAlpha = domeAlpha.value,
                    unfold = unfold.value,
                    rulerAlpha = rulerAlpha.value,
                )
            },
    ) { measurables, constraints ->
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth else FallbackWidth.roundToPx()
        val control = measurables[0].measure(Constraints(maxWidth = width))
        val half = width / 2f
        val r = metrics.radius
        // The dial covers the dome down to where it meets the side edges (iSqrt in qo/a0), and the
        // collapsed control.
        val cap = if (half < r) r - sqrt(r * r - half * half) else r
        val height = max(max(cap, metrics.minHeight), metrics.contentCenterY + control.height / 2f)
            .roundToInt()
            .coerceIn(constraints.minHeight, constraints.maxHeight)
        val blocker = measurables.getOrNull(1)?.measure(Constraints.fixed(width, height))
        layout(width, height) {
            val x = (width - control.width) / 2
            val y = (metrics.contentCenterY - control.height / 2f).roundToInt()
            state.contentBounds = Rect(
                left = x.toFloat(),
                top = y.toFloat(),
                right = (x + control.width).toFloat(),
                bottom = (y + control.height).toFloat(),
            )
            control.place(x, y)
            blocker?.place(0, 0)
        }
    }
}

/**
 * Follows one pointer on the dial (the ACTION_MOVE branch of `ZoomSeekBar.onTouchEvent`) until it is
 * lifted, consuming its events before the collapsed control sees them. A steep downward move is not a
 * turn; if the dial has not been turned yet it collapses the dial (`onScroll` while expanded). Returns
 * whether it did.
 */
private suspend fun AwaitPointerEventScope.trackCameraZoomDial(
    pointerId: PointerId,
    start: Offset,
    state: CameraZoomDialState,
    center: Offset,
    width: Float,
    height: Float,
    canFollow: () -> Boolean,
    onSwipeDown: () -> Unit,
): Boolean {
    val tracker = VelocityTracker()
    var previous = start
    var turned = false
    var swipedDown = false
    while (true) {
        val event = awaitPointerEvent(PointerEventPass.Initial)
        val change = event.changes.firstOrNull { it.id == pointerId } ?: return swipedDown
        change.consume()
        if (!change.pressed) return swipedDown
        tracker.addPosition(change.uptimeMillis, change.position)
        if (swipedDown) continue
        val position = change.position
        val dx = position.x - previous.x
        val dy = position.y - previous.y
        if (dx == 0f && dy == 0f) continue
        // Still unfolding after a long press, or a jitter while gliding to a touched label: not a turn.
        if (!canFollow() || (!turned && state.animating && (position - start).getDistance() < viewConfiguration.touchSlop)) {
            previous = position
            continue
        }
        if (dy > 0f && abs(dx) / dy < TAN_30 && position.y < height) {
            if (!turned) {
                val total = position - start
                if (total.y > SWIPE_THRESHOLD_PX && total.getDistance() > viewConfiguration.touchSlop && abs(total.x) / total.y < TAN_30) {
                    swipedDown = true
                    onSwipeDown()
                }
            }
            previous = position
            continue
        }
        turned = true
        state.turn(previous, position, center, abs(tracker.calculateVelocity().x), width)
        previous = position
    }
}

/** Draws the dome, the ruler and the current value (`ZoomArcSeekBar.gh`). */
private fun DrawScope.drawCameraZoomDial(
    state: CameraZoomDialState,
    metrics: CameraZoomDialMetrics,
    colors: CameraZoomDialColors,
    layouts: CameraZoomDialTextCache,
    domeAlpha: Float,
    unfold: Float,
    rulerAlpha: Float,
) {
    state.labelAnchors.clear()
    // CameraSeekBar.i0: nothing is drawn while the dome is fully faded out.
    if (domeAlpha <= 0f) return
    val scale = state.scale
    val progress = state.progress
    val rtl = state.rtl
    val r = metrics.radius
    val center = Offset(size.width / 2f, r)
    val shrink = (1f - unfold) * metrics.unfoldDistance

    // The dome (f7 fill, g7 rim), its radius growing by 33dp while it unfolds.
    val domeRadius = r - shrink
    drawCircle(colors.containerColor.fade(domeAlpha), radius = domeRadius - metrics.border, center = center)
    drawCircle(
        color = colors.borderColor.fade(domeAlpha),
        radius = domeRadius - metrics.border / 2f,
        center = center,
        style = Stroke(metrics.border),
    )

    val tickRadius = metrics.tickRadius - shrink
    val labelRadius = tickRadius - metrics.tickLength / 2f - metrics.labelMargin
    val halfTick = metrics.tickLength / 2f

    // ZoomArcSeekBar.qh: the ticks fade in from 5° above where the dome meets the side edges, over 8°.
    val half = size.width / 2f
    val edgeSine = if (half < r) sqrt(r * r - half * half) / r else 0f
    val fadeStart = toDegrees(edgeSine) + EDGE_FADE_MARGIN
    val fadeEnd = fadeStart + EDGE_FADE_RANGE

    // Past either end the ruler stretches: 900° per unit of overscroll.
    val overscroll = when {
        progress < 0f -> -progress
        progress > 1f -> progress - 1f
        else -> 0f
    }
    val span = scale.spanDeg + overscroll * OVERSCROLL_STRETCH

    // The current value at the top, and the box its text takes (for the label fade).
    val display = state.displayValue
    val readoutText = formatZoom(display)
    val readoutLayout = layouts.value(readoutText)
    val readoutBox = layouts.value(readoutPlaceholder(display)).size.width / 2f
    val readoutSize = metrics.valueTextSize
    val readoutX = center.x
    val readoutY = center.y - labelRadius

    val bandSegments = ArrayList<FloatArray>()
    var bandKey = -1
    for (i in 0 until scale.count) {
        val position = scale.positions[i]
        val angle = if (rtl) 90f + span * (position - progress) else 90f + span * (progress - position)
        val base = edgeAlpha(angle, fadeStart, fadeEnd) * rulerAlpha
        if (base <= 0f) continue
        val flags = scale.flags[i]
        val major = flags and FLAG_MAJOR != 0
        val dense = flags and FLAG_DENSE != 0
        val tickAlpha = when {
            !major -> base * MINOR_TICK_ALPHA
            !dense -> base
            else -> base * DENSE_MAJOR_TICK_ALPHA * scale.dimming[i]
        }
        val radians = toRadians(angle)
        val cosA = cos(radians)
        val sinA = sin(radians)
        if (flags and FLAG_BAND != 0) {
            val key = ((if (major) MINOR_TICK_ALPHA * scale.dimming[i] else MINOR_TICK_ALPHA) * base * 255f).toInt()
            val last = bandSegments.lastOrNull()
            if (last != null && key == bandKey) {
                last[2] = angle
            } else {
                bandSegments += floatArrayOf(key.toFloat(), last?.get(2) ?: angle, angle)
            }
            bandKey = key
        }
        if (flags and FLAG_LINE != 0) {
            drawLine(
                color = colors.tickColor.fade(tickAlpha),
                start = Offset(center.x + cosA * (tickRadius - halfTick), center.y - sinA * (tickRadius - halfTick)),
                end = Offset(center.x + cosA * (tickRadius + halfTick), center.y - sinA * (tickRadius + halfTick)),
                strokeWidth = metrics.tickWidth,
            )
        }
        if (flags and FLAG_STOP != 0) {
            val labelX = center.x + cosA * labelRadius
            val labelY = center.y - sinA * labelRadius
            val label = layouts.label(formatZoom(scale.values[i]))
            val labelHalf = label.size.width / 2f
            // A label sliding under the current value fades out where their boxes meet, over 4dp.
            val hidden = overlapFraction(
                readoutX - readoutBox,
                readoutY,
                readoutX + readoutBox,
                readoutY + readoutBox * 2f,
                labelX - labelHalf,
                labelY,
                labelX + labelHalf,
                labelY + metrics.labelTextSize,
            )
            val ramp = max(
                hidden,
                overlapFraction(
                    readoutX - readoutBox - metrics.labelFadeMargin,
                    readoutY,
                    readoutX + readoutBox + metrics.labelFadeMargin,
                    readoutY + readoutSize,
                    labelX - labelHalf,
                    labelY,
                    labelX + labelHalf,
                    labelY + metrics.labelTextSize,
                ),
            )
            val topLeft = Offset(labelX - labelHalf, labelY + metrics.labelBaselineOffset - label.firstBaseline)
            rotate(degrees = 90f - angle, pivot = Offset(labelX, labelY)) {
                if (hidden == 0f && ramp == 0f) {
                    drawText(label, color = colors.labelColor.fade(base), topLeft = topLeft)
                } else {
                    val from = if (readoutX < labelX) labelX - labelHalf else labelX + labelHalf
                    val to = if (readoutX < labelX) labelX + labelHalf else labelX - labelHalf
                    val brush = Brush.linearGradient(
                        0f to Color.Transparent,
                        hidden to Color.Transparent,
                        ramp to colors.labelColor,
                        1f to colors.labelColor,
                        start = Offset(from, labelY),
                        end = Offset(to, labelY),
                    )
                    drawText(label, brush = brush, topLeft = topLeft, alpha = base)
                }
            }
            state.labelAnchors += CameraZoomDialLabelAnchor(scale.values[i], labelX, labelY + metrics.labelBaselineOffset)
        }
    }

    // From 15x on the minor ticks are one translucent band (G7, as wide as a tick is long).
    if (bandSegments.isNotEmpty()) {
        val topLeft = Offset(center.x - tickRadius, center.y - tickRadius)
        val arcSize = Size(tickRadius * 2f, tickRadius * 2f)
        val stroke = Stroke(metrics.tickLength)
        for (segment in bandSegments) {
            val sweep = abs(segment[1] - segment[2])
            if (segment[0] <= 0f || sweep <= 0f) continue
            drawArc(
                color = colors.tickColor.fade(segment[0] / 255f),
                startAngle = -max(segment[1], segment[2]),
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke,
            )
        }
    }

    // The current value in accent with the "×" sign after it (multiple_arc_zoom_*), and the indicator.
    if (rulerAlpha > 0f) {
        val valueColor = colors.valueColor.fade(rulerAlpha)
        val glyphOffset = metrics.glyphWidth + metrics.multiplySpacing
        drawText(
            readoutLayout,
            color = valueColor,
            topLeft = Offset(
                readoutX - glyphOffset / 2f - readoutLayout.size.width / 2f,
                readoutY + metrics.labelBaselineOffset - readoutLayout.firstBaseline,
            ),
        )
        val labelWidth = layouts.label(readoutText).size.width
        translate(
            left = readoutX + (labelWidth + metrics.multiplySpacing - metrics.glyphWidth) / 2f,
            top = readoutY - metrics.glyphHeight / 2f,
        ) {
            drawPath(metrics.multiplySign, valueColor)
        }
        drawLine(
            color = colors.indicatorColor.fade(rulerAlpha),
            start = Offset(center.x, center.y - (tickRadius - halfTick)),
            end = Offset(center.x, center.y - (tickRadius + halfTick)),
            strokeWidth = metrics.indicatorWidth,
        )
    }
}

/** `ZoomArcSeekBar.lh`: the alpha of a tick at [angle] degrees, fading in from both ends. */
private fun edgeAlpha(angle: Float, fadeStart: Float, fadeEnd: Float): Float = when {
    angle >= fadeEnd && angle <= 180f - fadeEnd -> 1f
    angle >= fadeStart && angle < fadeEnd -> (angle - fadeStart) / (fadeEnd - fadeStart)
    angle > 180f - fadeEnd && angle <= 180f - fadeStart -> (180f - fadeStart - angle) / (fadeEnd - fadeStart)
    else -> 0f
}

/**
 * The share of the second box's width covered by the first, or 0 when they do not overlap
 * (`Rect.intersect` in `ZoomArcSeekBar.gh`).
 */
private fun overlapFraction(
    left1: Float,
    top1: Float,
    right1: Float,
    bottom1: Float,
    left2: Float,
    top2: Float,
    right2: Float,
    bottom2: Float,
): Float {
    val left = max(left1, left2)
    val right = min(right1, right2)
    val top = max(top1, top2)
    val bottom = min(bottom1, bottom2)
    val width = right2 - left2
    if (left >= right || top >= bottom || width <= 0f) return 0f
    return ((right - left) / width).coerceIn(0f, 1f)
}

private fun Color.fade(alpha: Float): Color = copy(alpha = this.alpha * alpha.coerceIn(0f, 1f))

private fun toRadians(degrees: Float): Float = degrees * (PI.toFloat() / 180f)

private fun toDegrees(radians: Float): Float = radians * (180f / PI.toFloat())

/** `ZoomSeekBar.Fc`: the zoom ratio rounded down to 0.1. */
private fun floorTenth(value: Float): Float = ((value + 1e-5f) * 10f).toInt() / 10f

/** `dr.n0.b`: "#.#" rounded down, so 2.0 reads "2" and 2.56 reads "2.5". */
private fun formatZoom(value: Float): String {
    val tenths = ((value + 1e-5f) * 10f).toInt()
    return if (tenths % 10 == 0) (tenths / 10).toString() else "${tenths / 10}.${tenths % 10}"
}

/** The box the current value is measured with: a "#" per character of the float, then "×". */
private fun readoutPlaceholder(value: Float): String {
    val tenths = ((value + 1e-5f) * 10f).toInt()
    val length = (tenths / 10).toString().length + 2
    return "#".repeat(length) + MULTIPLY_SIGN
}

/** The zoom range, its angle and its ticks, from `ZoomSeekBar.ke` and `getRightAngleFactor`. */
private class CameraZoomDialScale(stops: List<Float>) {
    val stopValues: FloatArray
    val stopPositions: FloatArray
    val minZoom: Float
    val maxZoom: Float
    val spanDeg: Float
    val count: Int
    val values: FloatArray
    val positions: FloatArray
    val flags: IntArray
    val dimming: FloatArray

    private val logRange: Float

    init {
        val sorted = stops.filter { it > 0f }.distinct().sorted()
        require(sorted.size >= 2) { "CameraZoomDial needs at least two positive stops" }
        stopValues = sorted.toFloatArray()
        minZoom = sorted.first()
        maxZoom = sorted.last()
        logRange = ln(maxZoom / minZoom)
        // getRightAngleFactor: the range up to 15x spans 90° (60° when the dial ends at 6x or below).
        val rightAngle = if (maxZoom > WIDE_RANGE_FROM) WIDE_RANGE_ANGLE else NARROW_RANGE_ANGLE
        val reference = min(maxZoom, DENSE_FROM)
        spanDeg = if (reference > minZoom) rightAngle * logRange / ln(reference / minZoom) else rightAngle
        stopPositions = FloatArray(sorted.size) { positionOf(sorted[it]) }

        val grid = ceil(minZoom * 10f - 1e-3f).toInt()..floor(maxZoom * 10f + 1e-3f).toInt()
        val all = (grid.map { it / 10f } + sorted).sorted().fold(ArrayList<Float>()) { list, v ->
            if (list.isEmpty() || abs(list.last() - v) > 1e-4f) list.add(v)
            list
        }
        count = all.size
        values = all.toFloatArray()
        positions = FloatArray(count) { positionOf(values[it]) }
        // gh: from 15x on the major ticks dim linearly to half at the end of the dial.
        val dimStep = if (maxZoom > 5f && maxZoom > DENSE_FROM) 0.5f / (maxZoom - DENSE_FROM) else 0f
        dimming = FloatArray(count) { 1f - (values[it] - DENSE_FROM).coerceAtLeast(0f) * dimStep }
        flags = IntArray(count) { index ->
            val v = values[index]
            val tenths = (v * 10f).roundToInt()
            val onGrid = abs(tenths / 10f - v) < 1e-4f
            val isStop = sorted.any { abs(it - v) < 1e-4f }
            val major = isStop || if (maxZoom > 30f) {
                tenths == DENSE_FROM_TENTHS || (onGrid && tenths % 100 == 0) || (onGrid && tenths > 100 && tenths % 50 == 0)
            } else {
                onGrid && tenths % 10 == 0
            }
            val dense = v >= DENSE_FROM
            var f = 0
            if (isStop) f = f or FLAG_STOP
            if (major) f = f or FLAG_MAJOR
            if (dense) f = f or FLAG_DENSE
            if (major || !dense) f = f or FLAG_LINE
            if (dense && (!major || tenths == DENSE_FROM_TENTHS)) f = f or FLAG_BAND
            f
        }
    }

    /** The position of [zoom] on the dial, 0 at the first stop and 1 at the last. */
    fun positionOf(zoom: Float): Float = ln(zoom.coerceAtLeast(minZoom) / minZoom) / logRange

    /** `ZoomSeekBar.Ac`: the zoom ratio at [position], rounded to 4 decimals. */
    fun valueAt(position: Float): Float {
        val raw = minZoom * (maxZoom / minZoom).pow(position.coerceIn(0f, 1f))
        return (raw * 10000f).roundToInt() / 10000f
    }
}

/** A stop label drawn in the last frame and the point a touch must be near (`H7`). */
private class CameraZoomDialLabelAnchor(val value: Float, val x: Float, val y: Float)

/** The dial position and its gestures (`ZoomSeekBar` f1, n2, N0 and the release animators). */
@Stable
private class CameraZoomDialState(initialScale: CameraZoomDialScale, initialValue: Float) {
    var scale by mutableStateOf(initialScale)

    /** `f1`: 0 at the first stop, 1 at the last, overshooting by up to 2.1% on the rubber band. */
    var progress by mutableFloatStateOf(initialScale.positionOf(initialValue))
        private set

    /** `A3`: the zoom ratio written at the top, rounded down to 0.1. */
    var displayValue by mutableFloatStateOf(floorTenth(initialValue))
        private set

    var reportedValue = initialValue
        private set
    var pointerDown = false
    var rtl = false
    var onUserValue: (Float) -> Unit = {}
    var haptic: (CouiHapticEffect) -> Unit = {}
    var contentBounds = Rect.Zero
    val labelAnchors = ArrayList<CameraZoomDialLabelAnchor>()

    private var snapped = false
    private var snapPoint = Offset.Zero
    private var edgeHapticArmed = true
    private var lastHapticValue = initialValue
    private var job: Job? = null
    private var collapseJob: Job? = null

    val animating: Boolean get() = job?.isActive == true

    fun jumpTo(value: Float) {
        cancelAnimation()
        progress = scale.positionOf(value.coerceIn(scale.minZoom, scale.maxZoom))
        reportedValue = value
        lastHapticValue = value
        displayValue = floorTenth(value.coerceIn(scale.minZoom, scale.maxZoom))
    }

    fun cancelAnimation() {
        job?.cancel()
        job = null
    }

    fun cancelCollapse() {
        collapseJob?.cancel()
        collapseJob = null
    }

    /** `getCollapseDelay`: the dial asks to collapse 1.5s after the last touch. */
    fun scheduleCollapse(scope: CoroutineScope, onCollapse: () -> Unit) {
        cancelCollapse()
        collapseJob = scope.launch {
            delay(CameraZoomDialDefaults.CollapseDelayMillis)
            onCollapse()
        }
    }

    fun contentHit(position: Offset, metrics: CameraZoomDialMetrics): Boolean {
        val bounds = contentBounds
        if (bounds.width <= 0f || bounds.height <= 0f) return false
        // td: the switch bar plus 17dp / 12dp of hot area around it.
        return position.x >= bounds.left - metrics.hotAreaHorizontal &&
            position.x <= bounds.right + metrics.hotAreaHorizontal &&
            position.y >= bounds.top - metrics.hotAreaVertical &&
            position.y <= bounds.bottom + metrics.hotAreaVertical
    }

    /** `ZoomArcSeekBar.je`: the stop whose label is within [touch] (Manhattan distance) of [position]. */
    fun stopAt(position: Offset, touch: Float): Float? = labelAnchors.firstOrNull {
        abs(it.x - position.x) + abs(it.y - position.y) <= touch
    }?.value

    /** Turns the dial by the angle [from] -> [to] makes around [center]. */
    fun turn(from: Offset, to: Offset, center: Offset, velocityX: Float, width: Float) {
        var delta = atan2(to.y - center.y, to.x - center.x) - atan2(from.y - center.y, from.x - center.x)
        if (delta > PI) {
            delta -= (2 * PI).toFloat()
        } else if (delta < -PI) {
            delta += (2 * PI).toFloat()
        }
        if (delta == 0f) return
        cancelAnimation()
        // A clockwise turn (to the right at the top) lowers the zoom; mirrored in RTL.
        val lower = if (rtl) delta < 0f else delta > 0f
        val p = progress
        if ((lower && p <= -MAX_OVERSCROLL) || (!lower && p >= 1f + MAX_OVERSCROLL)) return
        var step = abs(toDegrees(delta)) / scale.spanDeg
        if ((p >= 1f && !lower) || (p <= 0f && lower)) step *= OVERSCROLL_RESISTANCE
        var target = if (lower) max(p - step, -MAX_OVERSCROLL) else min(p + step, 1f + MAX_OVERSCROLL)

        // n2: held on a stop until the finger has moved on far enough (farther below 10x).
        if (snapped) {
            val release = (SNAP_RELEASE_BASE + SNAP_RELEASE_SLOPE * velocityX / SNAP_VELOCITY) *
                width * SNAP_RELEASE_WIDTH_RATIO * (if (displayValue > 10f) 1f else 2f) / 3f
            if ((to - snapPoint).getDistance() <= release) return
        }
        val high = max(p, target)
        var crossed = -1f
        if (p != target) {
            for (stop in scale.stopPositions) {
                if ((stop > p && stop <= target) || (stop < p && stop >= target)) {
                    crossed = stop
                    break
                }
                if (stop > high) break
            }
        }
        if (crossed >= 0f && velocityX < SNAP_VELOCITY) {
            snapped = true
            snapPoint = to
            target = crossed
        } else {
            snapped = false
        }
        applyProgress(target)
        if (edgeHapticArmed && (target <= -MAX_OVERSCROLL + 1e-6f || target >= 1f + MAX_OVERSCROLL - 1e-6f)) {
            edgeHapticArmed = false
            haptic(CouiHapticEffect.EdgeShort)
        }
        report(fromGesture = true)
    }

    /** `pe`: springs the rubber band back in 500ms. */
    fun release(scope: CoroutineScope) {
        snapped = false
        edgeHapticArmed = true
        val from = progress
        val to = when {
            from < 0f -> 0f
            from > 1f -> 1f
            else -> return
        }
        launch(scope) {
            runTween(REBOUND_DURATION, MoveEaseEasing) { applyProgress(from + (to - from) * it) }
            applyProgress(to)
        }
    }

    /** Glides to the stop [value] after its label was touched. */
    fun glideTo(scope: CoroutineScope, value: Float) {
        val from = progress
        val to = scale.positionOf(value)
        haptic(CouiHapticEffect.Strength)
        launch(scope) {
            runTween(GLIDE_DURATION, MoveEaseEasing) {
                applyProgress(from + (to - from) * it)
                report(fromGesture = false)
            }
            applyProgress(to)
            report(fromGesture = false)
        }
    }

    private fun applyProgress(value: Float) {
        progress = value.coerceIn(-MAX_OVERSCROLL, 1f + MAX_OVERSCROLL)
        // CameraSeekBar.i0: the value written at the top only moves once the zoom is 0.1 away from it.
        val raw = scale.valueAt(progress)
        val shown = displayValue
        val next = if (abs(raw - shown) < 0.1f && floorTenth(raw) != shown) shown else raw
        displayValue = floorTenth(next.coerceIn(scale.minZoom, scale.maxZoom))
    }

    private fun report(fromGesture: Boolean) {
        val value = scale.valueAt(progress)
        if (value == reportedValue) return
        if (fromGesture && crossesTick(lastHapticValue, value)) haptic(CouiHapticEffect.Strength)
        lastHapticValue = value
        reportedValue = value
        onUserValue(value)
    }

    /** `ZoomUIManager.r`: a stop between both ends, or a multiple of 5 above 20x, was crossed. */
    private fun crossesTick(from: Float, to: Float): Boolean {
        val stops = scale.stopValues
        for (i in 1 until stops.size - 1) {
            val stop = stops[i]
            if ((stop > from && stop <= to) || (stop < from && stop >= to)) return true
        }
        if (to > HAPTIC_STEP_FROM) {
            val step = floor(to / HAPTIC_STEP) * HAPTIC_STEP
            if (step > from && step <= to) return true
            if (step + HAPTIC_STEP < from && step + HAPTIC_STEP >= to) return true
        }
        return false
    }

    private fun launch(scope: CoroutineScope, block: suspend () -> Unit) {
        cancelAnimation()
        job = scope.launch { block() }
    }

    private suspend fun runTween(duration: Int, easing: Easing, onFrame: (Float) -> Unit) {
        val start = withFrameMillis { it }
        while (true) {
            val elapsed = withFrameMillis { it } - start
            val fraction = (elapsed / duration.toFloat()).coerceAtMost(1f)
            onFrame(easing.transform(fraction))
            if (fraction >= 1f) return
        }
    }
}

/** The dimensions of the dial in pixels, from the camera's `zoom_arc_seekbar_*` dimensions. */
private class CameraZoomDialMetrics(private val density: Density, radiusDp: Dp) {
    val radius = radiusDp.px()
    val unfoldDistance = CameraZoomDialDefaults.UnfoldDistance.px()
    val border = CameraZoomDialDefaults.BorderWidth.px()
    val tickLength = CameraZoomDialDefaults.TickLength.px()
    val tickWidth = CameraZoomDialDefaults.TickWidth.px()
    val indicatorWidth = CameraZoomDialDefaults.IndicatorWidth.px()

    /** `D7`: the tick centers run 8dp and half a tick inside the rim. */
    val tickRadius = radius - tickLength / 2f - CameraZoomDialDefaults.TickPaddingTop.px()
    val labelMargin = CameraZoomDialDefaults.LabelMarginTop.px()
    val labelBaselineOffset = CameraZoomDialDefaults.LabelBaselineOffset.px()
    val labelTextSize = CameraZoomDialDefaults.LabelTextSize.px()
    val valueTextSize = CameraZoomDialDefaults.ValueTextSize.px()
    val labelFadeMargin = CameraZoomDialDefaults.LabelFadeMargin.px()
    val multiplySpacing = CameraZoomDialDefaults.MultiplySignSpacing.px()
    val glyphWidth = MULTIPLY_SIGN_WIDTH.dp.px()
    val glyphHeight = MULTIPLY_SIGN_HEIGHT.dp.px()
    val labelTouch = CameraZoomDialDefaults.LabelTouchSize.px()
    val hotAreaHorizontal = CameraZoomDialDefaults.HotAreaHorizontal.px()
    val hotAreaVertical = CameraZoomDialDefaults.HotAreaVertical.px()
    val contentCenterY = CameraZoomDialDefaults.ContentCenterY.px()
    val minHeight = CameraZoomDialDefaults.MinHeight.px()
    val multiplySign = multiplySignPath(1.dp.px())

    private fun Dp.px(): Float = with(density) { toPx() }
}

/** Measures the stop labels and the current value once per text (the measurer caches only a few). */
private class CameraZoomDialTextCache(
    private val measurer: TextMeasurer,
    private val labelStyle: TextStyle,
    private val valueStyle: TextStyle,
) {
    private val labels = HashMap<String, TextLayoutResult>()
    private val values = HashMap<String, TextLayoutResult>()

    fun label(text: String): TextLayoutResult = labels.getOrPut(text) { measurer.measure(text, labelStyle) }

    fun value(text: String): TextLayoutResult = values.getOrPut(text) { measurer.measure(text, valueStyle) }
}

/** The "×" of `multiple_arc_zoom_*` (8dp x 15dp viewport), scaled by [unit] px per dp. */
private fun multiplySignPath(unit: Float): Path = Path().apply {
    val points = floatArrayOf(
        1.5899f, 4.1675f, 0.4613f, 5.2961f, 2.8652f, 7.7f, 0.4609f, 10.1043f,
        1.5918f, 11.2352f, 3.9961f, 8.8309f, 6.3991f, 11.2339f, 7.5277f, 10.1053f,
        5.1247f, 7.7023f, 7.5296f, 5.2974f, 6.3987f, 4.1665f, 3.9938f, 6.5714f,
    )
    moveTo(points[0] * unit, points[1] * unit)
    var i = 2
    while (i < points.size) {
        lineTo(points[i] * unit, points[i + 1] * unit)
        i += 2
    }
    close()
}

/**
 * `COUISpringInterpolator(response, bounce)` with no initial velocity: a damped spring of angular
 * frequency 2π / [response] and damping ratio 1 - [bounce], over the animation fraction, normalized to
 * end at 1.
 */
private class CameraZoomDialSpringEasing(response: Double, bounce: Double) : Easing {
    private val omega = 2.0 * PI / response
    private val damping = 1.0 - bounce
    private val end = raw(1.0).let { if (it == 0.0 || it.isNaN()) 1.0 else it }

    private fun raw(t: Double): Double {
        val time = t.coerceAtLeast(0.0)
        val decay = exp(-damping * omega * time)
        return if (damping < 1.0) {
            val damped = sqrt(1.0 - damping * damping) * omega
            val k = damping * omega / damped
            1.0 - decay * (cos(damped * time) + k * sin(damped * time))
        } else {
            1.0 - exp(-omega * time) * (omega * time + 1.0)
        }
    }

    override fun transform(fraction: Float): Float = (raw(fraction.toDouble()) / end).toFloat()
}

/** `COUIInEaseInterpolator` (0, 0, 0.1, 1), the camera's fade-out curve. */
private val FadeOutEasing = CubicBezierEasing(0f, 0f, 0.1f, 1f)

/** `COUIOutEaseInterpolator` (0.3, 0, 1, 1), the camera's fade-in curve. */
private val FadeInEasing = CubicBezierEasing(0.3f, 0f, 1f, 1f)

/** `c9.a.e` (0.3, 0, 0.1, 1): the rubber-band rebound. */
private val MoveEaseEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** `COUISpringInterpolator(0.85, 0.4)`: the dome unfolding. */
private val UnfoldEasing: Easing = CameraZoomDialSpringEasing(0.85, 0.4)

/** `COUISpringInterpolator(0.6, 0)`: the dome folding. */
private val FoldEasing: Easing = CameraZoomDialSpringEasing(0.6, 0.0)

/** `ha.a4.q()`: SysSans-En at weight 533. */
private val LabelFontWeight = FontWeight(533)

private const val MULTIPLY_SIGN = "×"
private const val MULTIPLY_SIGN_WIDTH = 8f
private const val MULTIPLY_SIGN_HEIGHT = 15f
private val FallbackWidth = 360.dp

/** `ZoomSeekBar.A2`: the zoom from which ticks pack into a band and major ticks dim. */
private const val DENSE_FROM = 15f
private const val DENSE_FROM_TENTHS = 150
private const val WIDE_RANGE_FROM = 6f
private const val WIDE_RANGE_ANGLE = 90f
private const val NARROW_RANGE_ANGLE = 60f
private const val MINOR_TICK_ALPHA = 0.15f
private const val DENSE_MAJOR_TICK_ALPHA = 0.7f
private const val EDGE_FADE_MARGIN = 5f
private const val EDGE_FADE_RANGE = 8f
private const val MAX_OVERSCROLL = 0.021f
private const val OVERSCROLL_RESISTANCE = 0.04f
private const val OVERSCROLL_STRETCH = 900f
private const val SNAP_VELOCITY = 200f
private const val SNAP_RELEASE_BASE = 0.31f
private const val SNAP_RELEASE_SLOPE = -0.155f
private const val SNAP_RELEASE_WIDTH_RATIO = 0.104f
private const val SWIPE_THRESHOLD_PX = 10f
private const val TAN_30 = 0.57735026f
private const val HAPTIC_STEP_FROM = 20f
private const val HAPTIC_STEP = 5f
private const val REBOUND_DURATION = 500
private const val GLIDE_DURATION = 300
private const val EXPAND_DELAY = 70
private const val COLLAPSED_UNFOLD = 0.5f
private const val VALUE_EPSILON = 1e-4f

/** A tick on an entry of the stops, labelled with its value. */
private const val FLAG_STOP = 1

/** A full-white tick (dimmed from 15x on). */
private const val FLAG_MAJOR = 2

/** A tick at 15x or beyond. */
private const val FLAG_DENSE = 4

/** A tick drawn as its own line. */
private const val FLAG_LINE = 8

/** A tick merged into the translucent band. */
private const val FLAG_BAND = 16

/** Default values of [CameraZoomDial], from the camera's `zoom_arc_seekbar_*` dimensions. */
object CameraZoomDialDefaults {
    /**
     * The marked stops of a 0.6x-120x camera with a 3x telephoto, as `ZoomSeekBar.getZoomSections`
     * builds them (ultra wide, 1x, 2x, the telephoto, 10x, 20x and the maximum). The real list comes
     * from the device configuration.
     */
    val Stops: List<Float> = listOf(0.6f, 1f, 2f, 3f, 10f, 20f, 120f)

    /** The dome radius (`zoom_arc_seekbar_bg_circle_radius`). */
    val Radius = 199.dp

    /** How much smaller the dome is when it starts to unfold (`..._unfold_anim_radius_between_distance`). */
    val UnfoldDistance = 33.dp

    /** The rim of the dome (`zoom_arc_seekbar_bg_stroke_width`). */
    val BorderWidth = 0.7.dp

    /** The tick length (`zoom_arc_seekbar_indicator_line_height`). */
    val TickLength = 10.dp

    /** The gap between the rim and the ticks (`zoom_arc_seekbar_indicator_line_padding_top`). */
    val TickPaddingTop = 8.dp

    /** The tick width (`zoom_arc_seekbar_indicator_line_width`). */
    val TickWidth = 0.66.dp

    /** The width of the accent indicator (`zoom_arc_seekbar_indicator_selected_line_width`). */
    val IndicatorWidth = 1.5.dp

    /** The gap between the ticks and the labels (`zoom_arc_seekbar_marker_center_margin_top`). */
    val LabelMarginTop = 18.dp

    /** The label baseline below the label point (`zoom_arc_seekbar_marker_text_y_offset`). */
    val LabelBaselineOffset = 4.dp

    /** The size of the stop labels (`zoom_arc_seekbar_mark_text_size`). */
    val LabelTextSize = 12.dp

    /** The size of the current value (`zoom_arc_seekbar_fixed_mark_text_size`). */
    val ValueTextSize = 12.dp

    /** The fade of a label sliding under the current value (`zoom_arc_seekbar_intersect_area_size`). */
    val LabelFadeMargin = 4.dp

    /** The gap before the "×" sign (`zoom_arc_seekbar_marker_multiple_space`). */
    val MultiplySignSpacing = 1.dp

    /** How near a touch must be to a stop label to glide there (`zoom_seek_arc_bar_mark_button_click_size`). */
    val LabelTouchSize = 15.dp

    /** The hot area beside the collapsed control (`zoom_arc_seekbar_focus_horizontal_hot_area_offset`). */
    val HotAreaHorizontal = 17.dp

    /** The hot area above and below the collapsed control (`..._focus_vertical_hot_area_offset`). */
    val HotAreaVertical = 12.dp

    /** The center of the collapsed control below the top (`zoom_arc_seekbar_focus_circle_center_y`). */
    val ContentCenterY = 86.7.dp

    /** The least height of the dial (`zoom_arc_seekbar_height`). */
    val MinHeight = 98.dp

    /** How long after the last touch the dial asks to collapse (`CameraSeekBar.j1`). */
    val CollapseDelayMillis = 1500L

    /**
     * The fixed camera palette: the dome `zoom_arc_seekbar_bg_color` (50% black) with a
     * `zoom_arc_seekbar_bg_stroke_color` (10% white) rim, white ticks and labels, and the current value
     * and indicator in the camera accent (`couiColorPrimary`, `color_primary_light_hasselblad` #FB822A).
     */
    @Composable
    fun cameraZoomDialColors(
        containerColor: Color = Color(0x7F000000),
        borderColor: Color = Color(0x1AFFFFFF),
        tickColor: Color = Color.White,
        labelColor: Color = Color.White,
        indicatorColor: Color = Color(0xFFFB822A),
        valueColor: Color = Color(0xFFFB822A),
    ): CameraZoomDialColors = remember(containerColor, borderColor, tickColor, labelColor, indicatorColor, valueColor) {
        CameraZoomDialColors(
            containerColor = containerColor,
            borderColor = borderColor,
            tickColor = tickColor,
            labelColor = labelColor,
            indicatorColor = indicatorColor,
            valueColor = valueColor,
        )
    }
}

/**
 * The colors of a [CameraZoomDial].
 *
 * @param containerColor The dome.
 * @param borderColor The rim of the dome.
 * @param tickColor The ticks; minor ticks and the band use 15% of it.
 * @param labelColor The stop labels.
 * @param indicatorColor The fixed tick over the current value.
 * @param valueColor The current value and its "×" sign.
 */
@Immutable
data class CameraZoomDialColors(
    val containerColor: Color,
    val borderColor: Color,
    val tickColor: Color,
    val labelColor: Color,
    val indicatorColor: Color,
    val valueColor: Color,
)
