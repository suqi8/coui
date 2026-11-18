// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.utils.CouiHapticEffect
import io.github.suqi8.coui.kmp.utils.rememberCouiHaptic
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * The inertial zoom control of the camera, the port of `camera_inertial_zoom_layout.xml`: an
 * `InertiaZoomBar` joystick between a minus and a plus `InertialZoomButton`, with the
 * `InertiaZoomRotableTextView` labels (current, minimum and maximum zoom) above it.
 *
 * The bar does not map positions to zoom ratios: its thumb rests in the middle and works like a
 * joystick. Dragging it sideways (past the touch slop) zooms continuously, faster the farther the
 * thumb is from the middle; every 25ms the zoom moves by `(k / 5) * ((zoom - minZoom) /
 * (20 - minZoom) * 0.4795 + 0.0152)` where `k` runs from -5 at the start to +5 at the end of the
 * bar (`ZoomUIManager` handler, `getZoomByVelocity`). While dragging, the thumb grows from 34.5 to
 * 35dp and the lines from 2 to 4dp (300ms on (0.3, 0, 0.1, 1)), a white line joins the middle to
 * the thumb, the thumb hides its ratio and the labels above fade and scale in (250ms on
 * (0.5, 0, 0.3, 1)). On release the thumb glides back to the middle in 333 to 667ms depending on
 * how far it went, the ratio fades back in (183ms) and the labels fade out. Hitting [minZoom] or
 * [maxZoom] plays a haptic and dims the bar to 50% (180ms) until the drag turns back. A quick tap
 * jumps the thumb to the finger, shows the labels for one second and glides back without zooming.
 *
 * The buttons step the zoom by 0.1 in 100ms on (0, 0, 0.1, 1) when tapped, and zoom continuously
 * at the speed of `k` = ±2 while held after a long press; they dim to 50% while pressed.
 *
 * The control always uses its fixed light-on-dark palette, since it sits over the camera preview.
 *
 * @param zoom The current zoom ratio.
 * @param onZoomChange Called with the new zoom ratio, coerced into [minZoom]..[maxZoom], every frame
 *   the zoom changes while the bar is dragged or a button is held, and on every frame of a button
 *   step.
 * @param modifier The modifier to be applied to the control.
 * @param enabled Whether the bar and the buttons accept input.
 * @param minZoom The lowest zoom ratio.
 * @param maxZoom The highest zoom ratio.
 * @param showStepButtons Whether to show the minus and plus buttons.
 * @param showValueLabels Whether to show the current, minimum and maximum ratios above the bar while
 *   it is used.
 * @param colors The colors of the control, see [CameraZoomBarDefaults.cameraZoomBarColors].
 */
@Composable
fun CameraZoomBar(
    zoom: Float,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minZoom: Float = CameraZoomBarDefaults.MinZoom,
    maxZoom: Float = CameraZoomBarDefaults.MaxZoom,
    showStepButtons: Boolean = true,
    showValueLabels: Boolean = true,
    colors: CameraZoomBarColors = CameraZoomBarDefaults.cameraZoomBarColors(),
) {
    require(maxZoom > minZoom) { "CameraZoomBar needs maxZoom > minZoom" }
    val haptic = rememberCouiHaptic()
    val scope = rememberCoroutineScope()
    val currentZoom by rememberUpdatedState(zoom)
    val currentOnZoomChange by rememberUpdatedState(onZoomChange)
    val currentHaptic by rememberUpdatedState(haptic)

    // ZoomUIManager.K: the zoom speed (-5..5), and whether the 25ms zoom loop (message 1) runs.
    var velocity by remember { mutableFloatStateOf(0f) }
    var zooming by remember { mutableStateOf(false) }
    // InertiaZoomBar.e()/d(): the bar dims to 50% at a limit until the drag turns back.
    val barAlpha = remember { Animatable(1f) }
    var atLimit by remember { mutableStateOf(false) }
    // h2.f / h2.a: the labels above the bar.
    val labelsVisibility = remember { Animatable(0f) }
    var labelsJob by remember { mutableStateOf<Job?>(null) }

    fun showLabels(visible: Boolean) {
        labelsJob?.cancel()
        labelsJob = scope.launch {
            labelsVisibility.animateTo(if (visible) 1f else 0f, tween(durationMillis = 250, easing = ZoomLabelEasing))
        }
    }

    fun dimBar(dim: Boolean) {
        atLimit = dim
        scope.launch { barAlpha.animateTo(if (dim) 0.5f else 1f, tween(durationMillis = 180, easing = ZoomStandardEasing)) }
    }

    LaunchedEffect(zooming, minZoom, maxZoom) {
        if (!zooming) return@LaunchedEffect
        var last = withFrameMillis { it }
        var pending = 0L
        while (true) {
            val now = withFrameMillis { it }
            pending += now - last
            last = now
            if (pending < ZOOM_TICK_MILLIS) continue
            var z = currentZoom.coerceIn(minZoom, maxZoom)
            val k = velocity
            while (pending >= ZOOM_TICK_MILLIS) {
                pending -= ZOOM_TICK_MILLIS
                val before = z
                z = (z + (k / 5f) * ((z - minZoom) / (ZOOM_RATE_REFERENCE - minZoom) * ZOOM_RATE_SLOPE + ZOOM_RATE_BASE))
                    .coerceIn(minZoom, maxZoom)
                if ((z == minZoom && before > minZoom) || (z == maxZoom && before < maxZoom)) {
                    // onMinZoomValueResponsed / onMaxZoomValueResponsed.
                    currentHaptic(CouiHapticEffect.Strength)
                    if (!atLimit) dimBar(true)
                }
            }
            if (atLimit && ((z == minZoom && k > 0f) || (z == maxZoom && k < 0f))) dimBar(false)
            if (z != currentZoom) currentOnZoomChange(z)
        }
    }

    fun startZooming(speed: Float) {
        velocity = speed
        zooming = true
        currentHaptic(CouiHapticEffect.Strength)
        val z = currentZoom
        if ((z <= minZoom && speed <= 0f) || (z >= maxZoom && speed >= 0f)) dimBar(true)
    }

    fun stopZooming() {
        zooming = false
        velocity = 0f
        if (atLimit) dimBar(false)
    }

    var stepJob by remember { mutableStateOf<Job?>(null) }
    fun step(delta: Float) {
        // InertialZoomButton.performClick: zoom ± 0.1 in 100ms on zoom_interpolator_in.
        stepJob?.cancel()
        val from = currentZoom
        val to = (from + delta).coerceIn(minZoom, maxZoom)
        if (to == from) return
        stepJob = scope.launch {
            Animatable(from).animateTo(to, tween(durationMillis = 100, easing = ZoomStepEasing)) {
                currentOnZoomChange(value)
            }
        }
    }

    val zoomText = CameraLensBarDefaults.formatZoomRatio(zoom)
    Column(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .semantics {
                stateDescription = zoomText + "×"
                progressBarRangeInfo = ProgressBarRangeInfo(zoom.coerceIn(minZoom, maxZoom), minZoom..maxZoom)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showValueLabels) {
            ZoomValueLabels(
                zoomText = zoomText,
                minText = CameraLensBarDefaults.formatZoomRatio(minZoom),
                maxText = CameraLensBarDefaults.formatZoomRatio(maxZoom),
                visibility = { labelsVisibility.value },
                colors = colors,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CameraZoomBarDefaults.LabelsHeight - CameraZoomBarDefaults.LabelsOverlap),
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showStepButtons) {
                ZoomStepButton(
                    plus = false,
                    enabled = enabled,
                    onStep = { step(-CameraZoomBarDefaults.StepRatio) },
                    onHoldStart = { startZooming(-CameraZoomBarDefaults.HoldVelocity) },
                    onHoldEnd = { stopZooming() },
                    colors = colors,
                )
            }
            ZoomJoystick(
                zoomText = zoomText,
                enabled = enabled,
                onDragStart = { speed ->
                    showLabels(true)
                    startZooming(speed)
                },
                onDrag = { speed -> velocity = speed },
                onDragEnd = {
                    stopZooming()
                    showLabels(false)
                },
                onTap = {
                    labelsJob?.cancel()
                    labelsJob = scope.launch {
                        labelsVisibility.animateTo(1f, tween(durationMillis = 250, easing = ZoomLabelEasing))
                        delay(LABELS_TAP_HOLD_MILLIS)
                        labelsVisibility.animateTo(0f, tween(durationMillis = 250, easing = ZoomLabelEasing))
                    }
                },
                barAlpha = { barAlpha.value },
                colors = colors,
                modifier = Modifier.padding(horizontal = CameraZoomBarDefaults.BarHorizontalMargin),
            )
            if (showStepButtons) {
                ZoomStepButton(
                    plus = true,
                    enabled = enabled,
                    onStep = { step(CameraZoomBarDefaults.StepRatio) },
                    onHoldStart = { startZooming(CameraZoomBarDefaults.HoldVelocity) },
                    onHoldEnd = { stopZooming() },
                    colors = colors,
                )
            }
        }
    }
}

/**
 * The `InertiaZoomBar` joystick. Reports the zoom speed `k` (-5..5) of the thumb position while it
 * is dragged.
 */
@Composable
private fun ZoomJoystick(
    zoomText: String,
    enabled: Boolean,
    onDragStart: (Float) -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
    onTap: () -> Unit,
    barAlpha: () -> Float,
    colors: CameraZoomBarColors,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer()
    val currentOnDragStart by rememberUpdatedState(onDragStart)
    val currentOnDrag by rememberUpdatedState(onDrag)
    val currentOnDragEnd by rememberUpdatedState(onDragEnd)
    val currentOnTap by rememberUpdatedState(onTap)

    // InertiaZoomBar.v (0..100 here 0..1, resting at the middle), the dragging state (x / z), the
    // stroke animation (G0/H0), the thumb size animation (I0/J0) and the ratio alpha (z0, E0).
    val progress = remember { Animatable(0.5f) }
    var dragging by remember { mutableStateOf(false) }
    val emphasis = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(1f) }

    val textStyle = remember(density) {
        TextStyle(fontSize = (CameraZoomBarDefaults.ThumbTextSize.value / density.fontScale).sp, fontWeight = FontWeight.Bold)
    }
    val textLayout = remember(zoomText, textStyle) { textMeasurer.measure(zoomText, textStyle) }
    val suffix = rememberZoomSuffix(large = false)

    fun emphasize(on: Boolean) {
        scope.launch { emphasis.animateTo(if (on) 1f else 0f, tween(durationMillis = 300, easing = ZoomMoveEaseEasing)) }
    }

    fun returnToMiddle(from: Float) {
        // InertiaZoomBar.g: 333ms plus up to 334ms for a thumb at either end.
        val duration = 333 + (abs(from - 0.5f) / 0.5f * 334f).toInt()
        scope.launch {
            progress.snapTo(from)
            progress.animateTo(0.5f, tween(durationMillis = duration, easing = ZoomMoveEaseEasing))
        }
        scope.launch {
            textAlpha.snapTo(0f)
            textAlpha.animateTo(1f, tween(durationMillis = 183, easing = ZoomStandardEasing))
        }
    }

    Canvas(
        modifier = modifier
            .size(CameraZoomBarDefaults.BarWidth, CameraZoomBarDefaults.BarHeight)
            .graphicsLayer { alpha = barAlpha() }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                val pad = CameraZoomBarDefaults.BarPadding.toPx()
                fun progressAt(x: Float): Float = ((x - pad) / (size.width - pad * 2f)).coerceIn(0f, 1f)
                fun speedOf(p: Float): Float = p * 10f - 5f
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var isDragging = false
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) {
                            val p = progressAt(change.position.x)
                            if (isDragging) {
                                dragging = false
                                emphasize(false)
                                currentOnDragEnd()
                            } else if (change.uptimeMillis - down.uptimeMillis < viewConfiguration.longPressTimeoutMillis) {
                                // A tap: the thumb jumps to the finger and glides back.
                                currentOnTap()
                            }
                            returnToMiddle(p)
                            change.consume()
                            break
                        }
                        if (event.changes.size >= 2) continue
                        if (!isDragging && abs(change.position.x - down.position.x) > viewConfiguration.touchSlop) {
                            isDragging = true
                            dragging = true
                            emphasize(true)
                            val p = progressAt(change.position.x)
                            scope.launch { progress.snapTo(p) }
                            currentOnDragStart(speedOf(p))
                        } else if (isDragging) {
                            val p = progressAt(change.position.x)
                            scope.launch { progress.snapTo(p) }
                            currentOnDrag(speedOf(p))
                        }
                        if (isDragging) change.consume()
                    }
                    if (isDragging && dragging) {
                        // Cancelled (ACTION_CANCEL).
                        dragging = false
                        emphasize(false)
                        currentOnDragEnd()
                        returnToMiddle(progress.value)
                    }
                }
            },
    ) {
        val w = size.width
        val cy = size.height / 2f
        val pad = CameraZoomBarDefaults.BarPadding.toPx()
        val e = emphasis.value
        val thumb = lerpPx(CameraZoomBarDefaults.ThumbSize.toPx(), CameraZoomBarDefaults.DraggingThumbSize.toPx(), e)
        val line = lerpPx(CameraZoomBarDefaults.LineWidth.toPx(), CameraZoomBarDefaults.DraggingLineWidth.toPx(), e)
        // The thumb ring follows the line at half its width: 1dp at rest, 2dp while dragging.
        val thumbStroke = line / 2f
        // InertiaZoomBar.h and i: the thumb center travels between the padded ends.
        val q = progress.value * (w - pad * 2f - thumb) + pad + thumb / 2f
        val half = thumb / 2f

        // The highlight line from the middle to the thumb edge, under the track.
        if (q > (w + thumb) / 2f) {
            drawLine(colors.highlightColor, Offset(w / 2f, cy), Offset(q - half, cy), strokeWidth = line, cap = StrokeCap.Butt)
        } else if (q < (w - thumb) / 2f) {
            drawLine(colors.highlightColor, Offset(w / 2f, cy), Offset(q + half, cy), strokeWidth = line, cap = StrokeCap.Butt)
        }
        val corner = CornerRadius(line / 2f)
        if (q - half > pad) {
            val topLeft = Offset(pad, cy - line / 2f)
            val trackSize = Size(q - half + line / 2f - pad, line)
            drawRoundRect(colors.trackShadowColor, topLeft, trackSize, corner)
            drawRoundRect(colors.trackColor, topLeft, trackSize, corner)
        }
        if (q + half < w - pad) {
            val topLeft = Offset(q + half - line / 2f, cy - line / 2f)
            val trackSize = Size(w - pad - (q + half - line / 2f), line)
            drawRoundRect(colors.trackShadowColor, topLeft, trackSize, corner)
            drawRoundRect(colors.trackColor, topLeft, trackSize, corner)
        }
        drawCircle(colors.thumbColor, radius = (thumb - thumbStroke) / 2f, center = Offset(q, cy), style = Stroke(thumbStroke))

        // The ratio inside the thumb (getTextBitmap): hidden while dragging, the "×" too.
        if (!dragging && textAlpha.value > 0f) {
            val gap = CameraZoomBarDefaults.ThumbSuffixSpacing.toPx()
            val glyphWidth = CameraZoomBarDefaults.SmallSuffixWidth.toPx()
            val groupWidth = textLayout.size.width + gap + glyphWidth
            val textLeft = q - groupWidth / 2f
            val textTop = cy - textLayout.size.height / 2f
            drawOutlinedText(textLayout, Offset(textLeft, textTop), colors, textAlpha.value)
            drawZoomSuffix(
                suffix,
                large = false,
                left = textLeft + textLayout.size.width + gap,
                centerY = cy,
                color = colors.contentColor,
                alpha = textAlpha.value,
            )
        }
    }
}

/**
 * The `InertialZoomButton`: a 44dp target with the minus or plus cross, dimming to 50% while
 * pressed (`COUIPressFeedbackHelper` type 0: 200ms in, 340ms out).
 */
@Composable
private fun ZoomStepButton(
    plus: Boolean,
    enabled: Boolean,
    onStep: () -> Unit,
    onHoldStart: () -> Unit,
    onHoldEnd: () -> Unit,
    colors: CameraZoomBarColors,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val alpha = remember { Animatable(1f) }
    val currentOnStep by rememberUpdatedState(onStep)
    val currentOnHoldStart by rememberUpdatedState(onHoldStart)
    val currentOnHoldEnd by rememberUpdatedState(onHoldEnd)
    Canvas(
        modifier = modifier
            .size(CameraZoomBarDefaults.ButtonSize)
            .graphicsLayer { this.alpha = alpha.value }
            .semantics {
                role = Role.Button
                if (enabled) {
                    onClick {
                        currentOnStep()
                        true
                    }
                }
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                var holding = false
                detectTapGestures(
                    onPress = {
                        scope.launch { alpha.animateTo(0.5f, tween(durationMillis = 200, easing = ZoomStandardEasing)) }
                        tryAwaitRelease()
                        if (holding) {
                            holding = false
                            currentOnHoldEnd()
                        }
                        scope.launch { alpha.animateTo(1f, tween(durationMillis = 340, easing = ZoomStandardEasing)) }
                    },
                    onLongPress = {
                        holding = true
                        currentOnHoldStart()
                    },
                    onTap = { currentOnStep() },
                )
            },
    ) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val arm = CameraZoomBarDefaults.ButtonCrossLength.toPx() / 2f
        val thick = CameraZoomBarDefaults.ButtonCrossWidth.toPx() / 2f
        val ring = CameraZoomBarDefaults.ButtonShadowStep.toPx()
        // The soft dark halo of inertial_zoom_plus/minus_button.png, ring by ring.
        ButtonShadowAlphas.forEachIndexed { index, layerAlpha ->
            val grow = ring * (ButtonShadowAlphas.size - index)
            val shadow = colors.buttonShadowColor.copy(alpha = colors.buttonShadowColor.alpha * layerAlpha)
            drawCrossBar(c, arm + grow, thick + grow, horizontal = true, color = shadow)
            if (plus) drawCrossBar(c, arm + grow, thick + grow, horizontal = false, color = shadow)
        }
        drawCrossBar(c, arm, thick, horizontal = true, color = colors.buttonIconColor)
        if (plus) drawCrossBar(c, arm, thick, horizontal = false, color = colors.buttonIconColor)
    }
}

private fun DrawScope.drawCrossBar(center: Offset, halfLength: Float, halfThickness: Float, horizontal: Boolean, color: Color) {
    val hx = if (horizontal) halfLength else halfThickness
    val hy = if (horizontal) halfThickness else halfLength
    drawRoundRect(
        color = color,
        topLeft = Offset(center.x - hx, center.y - hy),
        size = Size(hx * 2f, hy * 2f),
        cornerRadius = CornerRadius(halfThickness),
    )
}

/**
 * The labels row above the bar (`inertial_zoom_value_text`, `_min_value_text`, `_max_value_text`):
 * the current ratio in the middle (18dp, the large "×"), the minimum and maximum at the ends (12dp,
 * the small "×"). The current ratio scales from 50% and the ends from 0% as they fade in.
 */
@Composable
private fun ZoomValueLabels(
    zoomText: String,
    minText: String,
    maxText: String,
    visibility: () -> Float,
    colors: CameraZoomBarColors,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val valueStyle = remember(density) {
        TextStyle(fontSize = (CameraZoomBarDefaults.ValueTextSize.value / density.fontScale).sp, fontWeight = FontWeight.Normal)
    }
    val endStyle = remember(density) {
        TextStyle(fontSize = (CameraZoomBarDefaults.MinMaxTextSize.value / density.fontScale).sp, fontWeight = FontWeight.Normal)
    }
    val valueLayout = remember(zoomText, valueStyle) { textMeasurer.measure(zoomText, valueStyle) }
    val standardWidth = remember(valueStyle) { textMeasurer.measure(STANDARD_VALUE_TEXT, valueStyle).size.width }
    val minLayout = remember(minText, endStyle) { textMeasurer.measure(minText, endStyle) }
    val maxLayout = remember(maxText, endStyle) { textMeasurer.measure(maxText, endStyle) }
    val largeSuffix = rememberZoomSuffix(large = true)
    val smallSuffix = rememberZoomSuffix(large = false)

    Canvas(modifier = modifier) {
        val v = visibility()
        if (v <= 0f) return@Canvas
        // The labels are centered in a 90dp row whose bottom tucks 7.67dp under the bar.
        val cy = CameraZoomBarDefaults.LabelsHeight.toPx() / 2f
        val cx = size.width / 2f

        // The current ratio keeps its "×" where "2.5×" would put it (getStandardTextWidth), so the
        // glyph does not jump while the digits change.
        val boxWidth = CameraZoomBarDefaults.ValueWidth.toPx()
        val largeGap = CameraZoomBarDefaults.LargeSuffixSpacing.toPx()
        val largeGlyph = CameraZoomBarDefaults.LargeSuffixWidth.toPx()
        val glyphLeft = cx - boxWidth / 2f + (boxWidth - largeGlyph + standardWidth + largeGap) / 2f
        val valueScale = 0.5f + 0.5f * v
        scale(valueScale, pivot = Offset(cx, cy)) {
            val valueTopLeft = Offset(glyphLeft - largeGap - valueLayout.size.width, cy - valueLayout.size.height / 2f)
            drawOutlinedText(valueLayout, valueTopLeft, colors, v)
            drawZoomSuffix(
                largeSuffix,
                large = true,
                left = glyphLeft,
                centerY = cy,
                color = colors.contentColor,
                alpha = v,
            )
        }

        val margin = CameraZoomBarDefaults.MinMaxMargin.toPx()
        val smallGap = CameraZoomBarDefaults.ThumbSuffixSpacing.toPx()
        val smallGlyph = CameraZoomBarDefaults.SmallSuffixWidth.toPx()
        val minWidth = minLayout.size.width + smallGap + smallGlyph
        scale(v, pivot = Offset(margin + minWidth / 2f, cy)) {
            drawOutlinedText(minLayout, Offset(margin, cy - minLayout.size.height / 2f), colors, v)
            drawZoomSuffix(
                smallSuffix,
                large = false,
                left = margin + minLayout.size.width + smallGap,
                centerY = cy,
                color = colors.contentColor,
                alpha = v,
            )
        }
        val maxWidth = maxLayout.size.width + smallGap + smallGlyph
        val maxLeft = size.width - margin - maxWidth
        scale(v, pivot = Offset(maxLeft + maxWidth / 2f, cy)) {
            drawOutlinedText(maxLayout, Offset(maxLeft, cy - maxLayout.size.height / 2f), colors, v)
            drawZoomSuffix(
                smallSuffix,
                large = false,
                left = maxLeft + maxLayout.size.width + smallGap,
                centerY = cy,
                color = colors.contentColor,
                alpha = v,
            )
        }
    }
}

/** Draws a camera zoom text with its thin dark outline (`RotableTextView` stroke paint). */
private fun DrawScope.drawOutlinedText(layout: TextLayoutResult, topLeft: Offset, colors: CameraZoomBarColors, alpha: Float) {
    drawText(
        textLayoutResult = layout,
        color = colors.contentOutlineColor,
        topLeft = topLeft,
        alpha = alpha,
        drawStyle = Stroke(width = CameraZoomBarDefaults.TextOutlineWidth.toPx()),
    )
    drawText(textLayoutResult = layout, color = colors.contentColor, topLeft = topLeft, alpha = alpha)
}

/** The two paths of `inertia_zoombar_multiple_small` / `_large`: the white "×" and its 30% black rim. */
private class ZoomSuffixPaths(val fill: Path, val rim: Path)

@Composable
private fun rememberZoomSuffix(large: Boolean): ZoomSuffixPaths = remember(large) {
    val fill = PathParser().parsePathString(if (large) ZOOM_SUFFIX_LARGE_FILL else ZOOM_SUFFIX_SMALL_FILL).toPath()
    val rim = PathParser().parsePathString(if (large) ZOOM_SUFFIX_LARGE_RIM else ZOOM_SUFFIX_SMALL_RIM).toPath()
    rim.fillType = PathFillType.EvenOdd
    fill.fillType = PathFillType.EvenOdd
    ZoomSuffixPaths(fill, rim)
}

private fun DrawScope.drawZoomSuffix(
    paths: ZoomSuffixPaths,
    large: Boolean,
    left: Float,
    centerY: Float,
    color: Color,
    alpha: Float,
) {
    val width = (if (large) CameraZoomBarDefaults.LargeSuffixWidth else CameraZoomBarDefaults.SmallSuffixWidth).toPx()
    val height = (if (large) CameraZoomBarDefaults.LargeSuffixHeight else CameraZoomBarDefaults.SmallSuffixHeight).toPx()
    val viewportWidth = if (large) 12f else 8f
    val viewportHeight = if (large) 20f else 16f
    translate(left = left, top = centerY - height / 2f) {
        scale(scaleX = width / viewportWidth, scaleY = height / viewportHeight, pivot = Offset.Zero) {
            drawPath(paths.fill, color, alpha = alpha)
            drawPath(paths.rim, Color.Black, alpha = 0.3f * alpha)
        }
    }
}

private fun lerpPx(start: Float, stop: Float, fraction: Float): Float = start + (stop - start) * fraction

/** The zoom loop period (`ZoomUIManager` handler, `sendEmptyMessageDelayed(1, 25)`). */
private const val ZOOM_TICK_MILLIS = 25L

/** The zoom ratio the speed formula is scaled against (`getZoomByVelocity`). */
private const val ZOOM_RATE_REFERENCE = 20f

/** The zoom-proportional part of the speed formula. */
private const val ZOOM_RATE_SLOPE = 0.4795f

/** The constant part of the speed formula. */
private const val ZOOM_RATE_BASE = 0.0152f

/** How long a tap keeps the labels up (`ThreadManager.n(..., 1000)`). */
private const val LABELS_TAP_HOLD_MILLIS = 1000L

/** The text whose width places the "×" of the current ratio (`getStandardTextWidth`). */
private const val STANDARD_VALUE_TEXT = "2.5"

/** The halo of the button crosses, outermost ring first (alpha steps of the PNG shadow). */
private val ButtonShadowAlphas = floatArrayOf(0.016f, 0.015f, 0.033f, 0.067f)

private const val ZOOM_SUFFIX_SMALL_FILL =
    "M1.592,5.748L0.746,6.594L3.15,8.998L0.746,11.402L1.594,12.25L3.998,9.846L6.401,12.249L7.247,11.403" +
        "L4.844,9L7.249,6.595L6.401,5.747L3.996,8.152L1.592,5.748Z"
private const val ZOOM_SUFFIX_SMALL_RIM =
    "M0.213,6.594L1.592,5.215L3.996,7.618L6.401,5.213L7.782,6.595L5.377,9L7.78,11.403L6.401,12.782L3.998,10.379" +
        "L1.594,12.783L0.212,11.402L2.617,8.998L0.213,6.594ZM3.15,8.998L0.746,11.402L1.594,12.25L3.998,9.846" +
        "L6.401,12.249L7.247,11.403L4.844,9L7.249,6.595L6.401,5.747L3.996,8.152L1.592,5.748L0.746,6.594L3.15,8.998Z"
private const val ZOOM_SUFFIX_LARGE_FILL =
    "M2.325,6.19L1.191,7.325L4.867,11L1.191,14.675L2.325,15.81L6.001,12.134L9.676,15.81L10.811,14.676L7.135,11" +
        "L10.811,7.324L9.676,6.19L6.001,9.866L2.325,6.19Z"
private const val ZOOM_SUFFIX_LARGE_RIM =
    "M0.724,7.325L2.325,5.724L6.001,9.399L9.676,5.724L11.277,7.324L7.602,11L11.277,14.676L9.676,16.277L6.001,12.601" +
        "L2.325,16.276L0.724,14.675L4.4,11L0.724,7.325ZM4.866,11L1.191,14.675L2.325,15.81L6.001,12.134L9.676,15.81" +
        "L10.81,14.676L7.135,11L10.811,7.324L9.676,6.19L6.001,9.866L2.325,6.19L1.191,7.325L4.866,11Z"

/** `c9.a.e` (0.3, 0, 0.1, 1): strokes, thumb size and the glide back to the middle. */
private val ZoomMoveEaseEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** `c9.a.a` (0.33, 0, 0.67, 1): bar dimming, ratio fade and button press. */
private val ZoomStandardEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** `ZoomUIManager.j` (0.5, 0, 0.3, 1): the labels. */
private val ZoomLabelEasing = CubicBezierEasing(0.5f, 0f, 0.3f, 1f)

/** `zoom_interpolator_in` (0, 0, 0.1, 1): a button step. */
private val ZoomStepEasing = CubicBezierEasing(0f, 0f, 0.1f, 1f)

/**
 * Default values of [CameraZoomBar], from the camera's `inertial_zoombar_*` and `inertial_zoom_*`
 * dimensions.
 */
object CameraZoomBarDefaults {
    /** The default lowest zoom ratio, the ultra wide lens (`com.oplus.ultar.wide.zoom.point.value`). */
    val MinZoom = 0.6f

    /** The default highest zoom ratio, the reference of the speed formula. */
    val MaxZoom = 20f

    /** The zoom step of a button tap (`InertialZoomButton` listeners, ±0.1). */
    val StepRatio = 0.1f

    /** The zoom speed `k` while a button is held (`ZoomUIManager` handler messages 2 and 3). */
    val HoldVelocity = 2f

    /** The bar width (`inertial_zoombar_layout_width`). */
    val BarWidth = 244.dp

    /** The bar height (`inertial_zoombar_layout_height`). */
    val BarHeight = 46.dp

    /** The bar inset at both ends (`inertial_zoombar_padding_horizontal`). */
    val BarPadding = 2.dp

    /** The gap between the bar and the buttons (`inertial_zoom_minus_button_margin_left_and_right`). */
    val BarHorizontalMargin = 3.33.dp

    /** The track and highlight line width (`inertial_zoombar_stroke_width`). */
    val LineWidth = 2.dp

    /** The track and highlight line width while dragging (`inertial_zoombar_stroke_width_dragging`). */
    val DraggingLineWidth = 4.dp

    /** The thumb ring width at rest (`inertial_zoombar_thumb_stroke_width`); half the line while it animates. */
    val ThumbStrokeWidth = 1.dp

    /** The thumb diameter at rest (`inertial_zoombar_thumb_release_size`). */
    val ThumbSize = 34.5.dp

    /** The thumb diameter while dragging (`inertial_zoombar_thumb_dragging_size`). */
    val DraggingThumbSize = 35.dp

    /** The size of the ratio in the thumb (`inertial_zoombar_text_size`). */
    val ThumbTextSize = 12.dp

    /** The gap before the small "×" (`inertial_zoom_multiple_small_space`). */
    val ThumbSuffixSpacing = 1.dp

    /** The small "×" width (`inertia_zoombar_multiple_small`). */
    val SmallSuffixWidth = 8.dp

    /** The small "×" height (`inertia_zoombar_multiple_small`). */
    val SmallSuffixHeight = 16.dp

    /** The large "×" width (`inertia_zoombar_multiple_large`). */
    val LargeSuffixWidth = 12.dp

    /** The large "×" height (`inertia_zoombar_multiple_large`). */
    val LargeSuffixHeight = 20.dp

    /** The gap before the large "×" (`inertial_zoom_multiple_large_space`). */
    val LargeSuffixSpacing = 1.5.dp

    /** The button size (`inertial_zoombar_button_size`). */
    val ButtonSize = 44.dp

    /** The length of the button cross arms (`inertial_zoom_plus_button.png`, 48px at xxhdpi). */
    val ButtonCrossLength = 16.dp

    /** The width of the button cross arms (`inertial_zoom_plus_button.png`, 4px at xxhdpi). */
    val ButtonCrossWidth = 1.33.dp

    /** The width of one ring of the button halo (1px at xxhdpi). */
    val ButtonShadowStep = 0.33.dp

    /** The height of the labels row (`inertial_zoom_value_text_height`). */
    val LabelsHeight = 90.dp

    /** How far the labels row tucks under the bar (`inertial_zoom_value_text_margin_bottom`, -7.67dp). */
    val LabelsOverlap = 7.67.dp

    /** The size of the current ratio (`inertial_zoom_value_text_size`). */
    val ValueTextSize = 18.dp

    /** The width of the current ratio box (`inertial_zoom_value_width`). */
    val ValueWidth = 60.dp

    /** The size of the minimum and maximum ratios (`inertial_min_max_zoom_value_text_size`). */
    val MinMaxTextSize = 12.dp

    /** The inset of the minimum and maximum ratios (`inertial_zoom_min_max_zoom_value_margin_start`). */
    val MinMaxMargin = 22.dp

    /** The width of the text outline (`text_view_stroke_width` x 2). */
    val TextOutlineWidth = 0.67.dp

    /**
     * The fixed camera palette: `inertia_zoom_bar_stroke_color` (30% white) over
     * `color_black_with_20_percent_transparency` for the track, a white highlight line
     * (`inertia_zoom_bar_heighlight_stroke_color`), thumb (`inertia_zoom_thumb_color`) and text
     * (`inertia_zoom_text_color`), a `text_view_stroke_color` (40% black) text outline and white
     * button crosses with a dark halo.
     */
    @Composable
    fun cameraZoomBarColors(
        trackColor: Color = Color(0x4DFFFFFF),
        trackShadowColor: Color = Color(0x33000000),
        highlightColor: Color = Color.White,
        thumbColor: Color = Color.White,
        contentColor: Color = Color.White,
        contentOutlineColor: Color = Color(0x66000000),
        buttonIconColor: Color = Color.White,
        buttonShadowColor: Color = Color.Black,
    ): CameraZoomBarColors = remember(
        trackColor,
        trackShadowColor,
        highlightColor,
        thumbColor,
        contentColor,
        contentOutlineColor,
        buttonIconColor,
        buttonShadowColor,
    ) {
        CameraZoomBarColors(
            trackColor = trackColor,
            trackShadowColor = trackShadowColor,
            highlightColor = highlightColor,
            thumbColor = thumbColor,
            contentColor = contentColor,
            contentOutlineColor = contentOutlineColor,
            buttonIconColor = buttonIconColor,
            buttonShadowColor = buttonShadowColor,
        )
    }
}

/**
 * The colors of a [CameraZoomBar].
 *
 * @param trackColor The track on both sides of the thumb.
 * @param trackShadowColor The dark layer under the track.
 * @param highlightColor The line from the middle to the dragged thumb.
 * @param thumbColor The thumb ring.
 * @param contentColor The zoom ratios.
 * @param contentOutlineColor The outline of the zoom ratios.
 * @param buttonIconColor The minus and plus crosses.
 * @param buttonShadowColor The halo of the crosses.
 */
@Immutable
data class CameraZoomBarColors(
    val trackColor: Color,
    val trackShadowColor: Color,
    val highlightColor: Color,
    val thumbColor: Color,
    val contentColor: Color,
    val contentOutlineColor: Color,
    val buttonIconColor: Color,
    val buttonShadowColor: Color,
)
