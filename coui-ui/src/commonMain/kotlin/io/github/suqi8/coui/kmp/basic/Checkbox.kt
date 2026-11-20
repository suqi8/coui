// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import kotlin.math.min

/**
 * A [Checkbox] component with COUI style, supporting three states: On, Off, and Indeterminate.
 *
 * Like `COUICheckBox`, the box itself never scales on press: an interactive checkbox shows a 40dp
 * circular press highlight (`checkbox_ripple_bg_radius` 20dp, couiColorPress) behind the 24dp
 * drawable, and the same circle takes the hover and keyboard-focus layers of its
 * `COUIMaskRippleDrawable`. Pass `onClick = null` when a surrounding row handles the click, as the
 * COUI checkbox preference does, so only the row shows its press state. COUICheckBox plays no
 * haptic feedback.
 *
 * State changes replay the COUI AVDs while enabled: the fill fades, the checkmark draws or erases
 * itself, the indeterminate bar grows from or retracts to its left end, and the layers pop
 * 1 -> 1.08 -> 1 (the checkmark stays unscaled between indeterminate and checked). A disabled
 * checkbox jumps to its new state, except between indeterminate and checked
 * (`coui_checkbox_part_to_selected_for_disabled` and `coui_checkbox_selected_to_part_for_disabled`).
 * [CheckboxStyle.Shape] swaps in the shape checkbox's own unchecked drawables and AVDs.
 *
 * @param state The current [ToggleableState] of the [Checkbox].
 * @param onClick The callback to be called when the [Checkbox] is clicked. The caller is
 *   responsible for updating the state. If `null`, the [Checkbox] is not interactive.
 * @param modifier The modifier to be applied to the [Checkbox].
 * @param colors The [CheckboxColors] of the [Checkbox]. Use [CheckboxDefaults.shapeCheckboxColors]
 *   with [CheckboxStyle.Shape].
 * @param enabled Whether the [Checkbox] is enabled.
 * @param style The COUI drawable set: [CheckboxStyle.Default], or [CheckboxStyle.Shape] for the
 *   shape checkbox laid over images.
 */
@Composable
fun Checkbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    colors: CheckboxColors = CheckboxDefaults.checkboxColors(),
    enabled: Boolean = true,
    style: CheckboxStyle = CheckboxStyle.Default,
) {
    val currentOnClickState = rememberUpdatedState(onClick)

    val transition = updateTransition(state, label = "CheckboxTransition")

    // Fill alpha timeline from the COUI AVDs.
    val fillFractionState = transition.animateFloat(
        transitionSpec = {
            if (!checkboxPlaysDefaultAvd(style, enabled, initialState, targetState)) {
                snap()
            } else if (targetState != ToggleableState.Off) {
                tween(durationMillis = 150, easing = CouiCheckboxEasing)
            } else {
                keyframes {
                    durationMillis = 300
                    1f at 150 using CouiCheckboxEasing
                }
            }
        },
        label = "FillFraction",
    ) { if (it != ToggleableState.Off) 1f else 0f }

    // Checkmark trim: trimPathEnd runs 0 <-> 1 over the full 300ms.
    val checkFractionState = transition.animateFloat(
        transitionSpec = {
            if (checkboxPlaysDefaultAvd(style, enabled, initialState, targetState)) {
                tween(durationMillis = 300, easing = CouiCheckboxEasing)
            } else {
                snap()
            }
        },
        label = "CheckFraction",
    ) { if (it == ToggleableState.On) 1f else 0f }

    // Indeterminate bar reveal from the part AVD clip-path morphs. A growing bar is the overlap of a
    // clip on the checkbox easing and a fill path morphed linearly, both from the left.
    val barFractionState = transition.animateFloat(
        transitionSpec = {
            when {
                !checkboxPlaysDefaultAvd(style, enabled, initialState, targetState) -> snap()

                targetState == ToggleableState.Indeterminate && initialState == ToggleableState.On ->
                    keyframes {
                        durationMillis = 300
                        0f at 100 using CouiCheckboxBarGrowEasing
                    }

                targetState == ToggleableState.Indeterminate -> tween(durationMillis = 300, easing = CouiCheckboxBarGrowEasing)

                targetState == ToggleableState.On -> tween(durationMillis = 200, easing = CouiCheckboxEasing)

                else -> tween(durationMillis = 300, easing = CouiCheckboxEasing)
            }
        },
        label = "BarFraction",
    ) { if (it == ToggleableState.Indeterminate) 1f else 0f }

    // The 1 -> 1.08 -> 1 pop of the COUI checkbox AVDs, per layer group: the box (ring and fill),
    // the checkmark and the indeterminate bar, each with the easings of its own AVD targets.
    val boxPop = remember { Animatable(1f) }
    val markPop = remember { Animatable(1f) }
    val barPop = remember { Animatable(1f) }
    // The shape checkbox AVDs from and to unchecked run on their own timeline.
    val shapeMotion = remember { CheckboxShapeMotion() }
    var skipInitialPop by remember { mutableStateOf(true) }
    // The state the last pop effect saw; only written from the effect.
    val previousState = remember { arrayOf(state) }
    LaunchedEffect(state) {
        val from = previousState[0]
        previousState[0] = state
        if (skipInitialPop) {
            skipInitialPop = false
            return@LaunchedEffect
        }
        boxPop.snapTo(1f)
        markPop.snapTo(1f)
        barPop.snapTo(1f)
        if (!checkboxAnimates(style, enabled, from, state)) return@LaunchedEffect
        val shapeAvd = if (style == CheckboxStyle.Shape) checkboxShapeAvd(from, state) else null
        if (shapeAvd != null) {
            shapeMotion.play(shapeAvd)
            return@LaunchedEffect
        }
        launch { boxPop.checkboxPop(CouiCheckboxEasing, CouiCheckboxEasing) }
        when {
            // coui_checkbox_unselected_to_selected: the check settles on its own easing.
            from == ToggleableState.Off && state == ToggleableState.On ->
                launch { markPop.checkboxPop(CouiCheckboxEasing, CouiCheckboxMarkSettleEasing) }

            from == ToggleableState.On && state == ToggleableState.Off ->
                launch { markPop.checkboxPop(CouiCheckboxEasing, CouiCheckboxEasing) }

            // coui_checkbox_part_to_selected / selected_to_part leave the checkmark unscaled.
            else -> Unit
        }
        when {
            // coui_checkbox_part_to_unselected / part_to_selected: the bar pops on a quicker rise.
            from == ToggleableState.Indeterminate ->
                launch { barPop.checkboxPop(CouiCheckboxBarRiseEasing, CouiCheckboxEasing) }

            state == ToggleableState.Indeterminate ->
                launch { barPop.checkboxPop(CouiCheckboxEasing, CouiCheckboxEasing) }

            else -> Unit
        }
    }

    // COUI crossfades layers instead of morphing colors.
    val ringColor = colors.uncheckedBackgroundColor(enabled)
    val ringFillColor = colors.uncheckedFillColor(enabled)
    val fillColor = colors.checkedBackgroundColor(enabled)
    val markColor = colors.checkedForegroundColor(enabled)
    // Once no AVD runs, the indeterminate state shows its static drawable
    // (coui_btn_part_check_on_normal / _disabled), whose box sits 0.666dp lower than the AVD frames.
    val restingIndeterminate = state == ToggleableState.Indeterminate && !transition.isRunning

    val checkPath = remember { Path() }
    val pressColor = COUITheme.colorScheme.press
    // The COUIMaskRippleDrawable circle: the press ripple, with its hover and focus layers.
    val pressIndication = remember(pressColor) { CircularPressIndication(CheckboxPressRadius, pressColor) }

    val finalModifier = if (onClick != null) {
        Modifier.triStateToggleable(
            state = state,
            onClick = { currentOnClickState.value?.invoke() },
            enabled = enabled,
            role = Role.Checkbox,
            interactionSource = null,
            indication = pressIndication,
        )
    } else {
        Modifier.semantics {
            role = Role.Checkbox
            toggleableState = state
            if (!enabled) disabled()
        }
    }

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            .requiredSize(24.dp)
            // Before the drawing so the press highlight sits behind the box, like a view background.
            .then(finalModifier)
            .drawWithCache {
                val d = size.minDimension

                val fillTopLeft = Offset(FillInsetFraction * d, FillInsetFraction * d)
                val fillSize = Size(FillSizeFraction * d, FillSizeFraction * d)
                val fillCornerRadius = CornerRadius(FillCornerRadiusFraction * d)

                val ringTopLeft = Offset(RingInsetFraction * d, RingInsetFraction * d)
                val ringSize = Size(RingSizeFraction * d, RingSizeFraction * d)
                val ringCornerRadius = CornerRadius(RingCornerRadiusFraction * d)
                val ringStroke = Stroke(width = RingStrokeWidthFraction * d)
                // A colored unchecked fill drawn with the default style fills the ring's interior.
                val ringHalfStroke = RingStrokeWidthFraction * d / 2f
                val ringFillTopLeft = Offset(ringTopLeft.x + ringHalfStroke, ringTopLeft.y + ringHalfStroke)
                val ringFillSize = Size(ringSize.width - 2f * ringHalfStroke, ringSize.height - 2f * ringHalfStroke)
                val ringFillCornerRadius = CornerRadius(RingCornerRadiusFraction * d - ringHalfStroke)
                val shape = if (style == CheckboxStyle.Shape) CheckboxShapeGeometry(d) else null

                val checkCache = CheckmarkCache(
                    startPoint = Offset(CheckStartPoint.x * d, CheckStartPoint.y * d),
                    middlePoint = Offset(CheckMiddlePoint.x * d, CheckMiddlePoint.y * d),
                    endPoint = Offset(CheckEndPoint.x * d, CheckEndPoint.y * d),
                )
                val checkStroke = Stroke(
                    width = CheckStrokeWidthFraction * d,
                    cap = StrokeCap.Butt,
                    join = StrokeJoin.Round,
                )

                val barFullLength = BarLengthFraction * d
                val restOffset = if (restingIndeterminate) RestIndeterminateOffsetFraction * d else 0f
                val barTop = when {
                    !restingIndeterminate -> (d - AvdBarThicknessFraction * d) / 2f
                    enabled -> RestBarTopFraction * d
                    else -> RestDisabledBarTopFraction * d
                }
                val barThickness = when {
                    !restingIndeterminate -> AvdBarThicknessFraction * d
                    enabled -> RestBarThicknessFraction * d
                    else -> RestDisabledBarThicknessFraction * d
                }
                val barTopLeft = Offset(BarStartFraction * d, barTop)
                val restFillTopLeft = fillTopLeft.copy(y = fillTopLeft.y + restOffset)

                onDrawBehind {
                    val shapeAvd = shapeMotion.avd
                    if (shape != null && shapeAvd != null) {
                        drawCheckboxShapeAvd(
                            avd = shapeAvd,
                            time = shapeMotion.elapsed.value,
                            shape = shape,
                            ringColor = ringColor,
                            darkFillColor = ringFillColor,
                            fillColor = fillColor,
                            markColor = markColor,
                            checkPath = checkPath,
                        )
                        return@onDrawBehind
                    }
                    scale(scale = boxPop.value) {
                        val fillFraction = fillFractionState.value
                        if (fillFraction < 1f) {
                            if (shape != null) {
                                // coui_shape_btn_check_off_normal / _disabled: a dark fill inside a light ring.
                                val restFill = if (enabled) shape.restFill else shape.restDisabledFill
                                val restRing = if (enabled) shape.restRing else shape.restDisabledRing
                                drawCheckboxRoundBox(restFill, ringFillColor)
                                drawCheckboxRoundBox(restRing, ringColor, style = shape.ringStroke)
                            } else {
                                if (ringFillColor.alpha > 0f) {
                                    drawRoundRect(
                                        color = ringFillColor,
                                        topLeft = ringFillTopLeft,
                                        size = ringFillSize,
                                        cornerRadius = ringFillCornerRadius,
                                    )
                                }
                                drawRoundRect(
                                    color = ringColor,
                                    topLeft = ringTopLeft,
                                    size = ringSize,
                                    cornerRadius = ringCornerRadius,
                                    style = ringStroke,
                                )
                            }
                        }
                        if (fillFraction > 0f) {
                            drawRoundRect(
                                color = fillColor,
                                topLeft = restFillTopLeft,
                                size = fillSize,
                                cornerRadius = fillCornerRadius,
                                alpha = fillFraction,
                            )
                        }
                    }
                    scale(scale = markPop.value) {
                        drawTrimmedCheckmark(
                            color = markColor,
                            fraction = checkFractionState.value,
                            path = checkPath,
                            cache = checkCache,
                            stroke = checkStroke,
                        )
                    }
                    val barFraction = barFractionState.value
                    if (barFraction > 0f) {
                        scale(scale = barPop.value) {
                            drawRect(
                                color = markColor,
                                topLeft = barTopLeft,
                                size = Size(barFullLength * barFraction, barThickness),
                            )
                        }
                    }
                }
            },
    ) {}
}

/**
 * The COUI drawable set a [Checkbox] draws and animates.
 */
enum class CheckboxStyle {
    /** The regular COUI checkbox (`couiCheckBoxStyle`, drawable `coui_checkbox_state`). */
    Default,

    /**
     * The COUI shape checkbox laid over images and colored backdrops
     * (`Widget.COUI.CompoundButton.COUICheckBox.Shape`, theme attribute `couiShapeCheckBoxStyle`,
     * drawable `coui_shape_checkbox_state`); draw it with [CheckboxDefaults.shapeCheckboxColors].
     *
     * Its unchecked box is a dark fill inside a light ring (`coui_shape_btn_check_off_*`), and its
     * changes from and to unchecked play the shape AVDs (`coui_shape_checkbox_*`): the layers pop to
     * 1.2 within 20-33ms and settle over the next 160-167ms, the fill fades in or out within a few
     * frames, the 1.5dp checkmark draws or erases itself after 33ms, and the indeterminate state shows
     * a round-capped dash that grows from or retracts to its left end. Indeterminate <-> checked reuse
     * the default AVDs, and a disabled shape checkbox never animates.
     */
    Shape,
}

/** One AVD pop: 1 -> [PopPeakScale] on [rise], then back to 1 on [settle], 150ms each. */
private suspend fun Animatable<Float, AnimationVector1D>.checkboxPop(rise: Easing, settle: Easing) {
    animateTo(PopPeakScale, tween(PopHalfDurationMillis, easing = rise))
    animateTo(1f, tween(PopHalfDurationMillis, easing = settle))
}

private class CheckmarkCache(
    val startPoint: Offset,
    val middlePoint: Offset,
    val endPoint: Offset,
) {
    val firstSegmentLength = (middlePoint - startPoint).getDistance()
    val secondSegmentLength = (endPoint - middlePoint).getDistance()
    val totalLength = firstSegmentLength + secondSegmentLength
}

private fun DrawScope.drawTrimmedCheckmark(
    color: Color,
    fraction: Float,
    path: Path,
    cache: CheckmarkCache,
    stroke: Stroke,
) {
    val endDistance = cache.totalLength * fraction
    if (endDistance <= 0f) return

    path.rewind()
    path.moveTo(cache.startPoint.x, cache.startPoint.y)
    if (endDistance <= cache.firstSegmentLength) {
        val t = endDistance / cache.firstSegmentLength
        path.lineTo(
            cache.startPoint.x + (cache.middlePoint.x - cache.startPoint.x) * t,
            cache.startPoint.y + (cache.middlePoint.y - cache.startPoint.y) * t,
        )
    } else {
        path.lineTo(cache.middlePoint.x, cache.middlePoint.y)
        val t = ((endDistance - cache.firstSegmentLength) / cache.secondSegmentLength).coerceAtMost(1f)
        path.lineTo(
            cache.middlePoint.x + (cache.endPoint.x - cache.middlePoint.x) * t,
            cache.middlePoint.y + (cache.endPoint.y - cache.middlePoint.y) * t,
        )
    }

    drawPath(
        path = path,
        color = color,
        style = stroke,
    )
}

/** The four `coui_shape_checkbox_*` AVDs; indeterminate <-> checked reuse the default ones. */
private enum class CheckboxShapeAvd(val durationMillis: Int) {
    /** `coui_shape_checkbox_unselected_to_selected`. */
    UnselectedToSelected(200),

    /** `coui_shape_checkbox_selected_to_unselected` (its time group runs on to 317ms). */
    SelectedToUnselected(317),

    /** `coui_shape_checkbox_unselected_to_part`. */
    UnselectedToPart(200),

    /** `coui_shape_checkbox_part_to_unselected`. */
    PartToUnselected(200),
}

/** The shape checkbox AVD in flight and its clock, driven from the state effect. */
private class CheckboxShapeMotion {
    /** The AVD playing, `null` when the checkbox shows a static drawable or a default AVD. */
    var avd by mutableStateOf<CheckboxShapeAvd?>(null)
        private set

    /** The time into [avd], in milliseconds. */
    val elapsed = Animatable(0f)

    private var plays = 0

    suspend fun play(avd: CheckboxShapeAvd) {
        val play = ++plays
        try {
            elapsed.snapTo(0f)
            this.avd = avd
            elapsed.animateTo(
                targetValue = avd.durationMillis.toFloat(),
                animationSpec = tween(durationMillis = avd.durationMillis, easing = LinearEasing),
            )
        } finally {
            // A newer play owns the field once it started.
            if (plays == play) this.avd = null
        }
    }
}

/** A rounded square of the 24-unit checkbox viewport, in pixels. */
private class CheckboxRoundBox(unit: Float, inset: Float, side: Float, radius: Float) {
    val topLeft = Offset(inset * unit, inset * unit)
    val size = Size(side * unit, side * unit)
    val cornerRadius = CornerRadius(radius * unit)
}

/**
 * The shape checkbox drawables for a box of [d] pixels (the 24-unit viewport; the part AVDs use a
 * 72-unit one, divided by 3 here).
 */
private class CheckboxShapeGeometry(d: Float) {
    private val unit = d / ViewportSize

    // coui_shape_btn_check_off_normal, centred: its 25 x 24 viewport would put the box 0.5 off
    // the centre the other states share.
    val restFill = CheckboxRoundBox(unit, 4.3f, 15.4f, 2.7f)
    val restRing = CheckboxRoundBox(unit, 3.65f, 16.7f, 3.35f)

    // coui_shape_btn_check_off_disabled.
    val restDisabledFill = CheckboxRoundBox(unit, 4.5f, 15f, 3f)
    val restDisabledRing = CheckboxRoundBox(unit, 3.7f, 16.6f, 3.8f)

    val ringStroke = Stroke(width = 1.3f * unit)

    // $coui_shape_checkbox_unselected_to_selected__0 / selected_to_unselected__0.
    val avdRing = CheckboxRoundBox(unit, 3.7f, 16.6f, 3.8f)
    val avdDarkFill = CheckboxRoundBox(unit, 4.5f, 15f, 3f)
    val avdFill = CheckboxRoundBox(unit, 3f, 18f, 4f)

    // The 72-unit check polyline scaled by 1/3 about (12, 12), 4.5 wide: 1.5dp.
    val check = CheckmarkCache(
        startPoint = Offset(7.2938f * unit, 11.6667f * unit),
        middlePoint = Offset(10.8534f * unit, 15.2297f * unit),
        endPoint = Offset(17.6561f * unit, 8.3637f * unit),
    )
    val checkStroke = Stroke(width = 1.5f * unit, cap = StrokeCap.Butt, join = StrokeJoin.Round)

    // $coui_shape_checkbox_unselected_to_part__0 / part_to_unselected__0: a +-22 dark fill, a light
    // band between +-22 and +-24 (drawn as its 0.667dp centre line), a +-24 fill and a 4-unit dash
    // from -11 to 11.25 with round caps.
    val partDarkFill = CheckboxRoundBox(unit, 4.6667f, 14.6667f, 2.6667f)
    val partBand = CheckboxRoundBox(unit, 4.3333f, 15.3333f, 3.878f)
    val partBandStroke = Stroke(width = 0.6667f * unit)
    val partFill = CheckboxRoundBox(unit, 4f, 16f, 3.3333f)
    val dashStart = Offset(8.3333f * unit, 12f * unit)
    val dashLength = 7.4167f * unit
    val dashWidth = 1.3333f * unit
}

private fun DrawScope.drawCheckboxRoundBox(
    box: CheckboxRoundBox,
    color: Color,
    alpha: Float = 1f,
    style: DrawStyle = Fill,
) {
    if (alpha <= 0f || color.alpha <= 0f) return
    drawRoundRect(
        color = color,
        topLeft = box.topLeft,
        size = box.size,
        cornerRadius = box.cornerRadius,
        alpha = alpha.coerceAtMost(1f),
        style = style,
    )
}

/**
 * One frame of a shape checkbox AVD, [time] milliseconds in. The AVDs hard-code their translucent
 * whites and blacks; they are applied as alphas relative to the colors' resting ones (0.4).
 */
private fun DrawScope.drawCheckboxShapeAvd(
    avd: CheckboxShapeAvd,
    time: Float,
    shape: CheckboxShapeGeometry,
    ringColor: Color,
    darkFillColor: Color,
    fillColor: Color,
    markColor: Color,
    checkPath: Path,
) {
    when (avd) {
        CheckboxShapeAvd.UnselectedToSelected, CheckboxShapeAvd.SelectedToUnselected -> {
            val selecting = avd == CheckboxShapeAvd.UnselectedToSelected
            // The ring: strokeAlpha 0.3 that leaves at 67-84ms, or 0.05 that returns to 0.4 at 150-167ms.
            val ringAlpha = if (selecting) {
                checkboxAvdValue(time, 67f, 84f, ShapeSelectRingAlpha, 0f, CouiShapeCheckboxEasing)
            } else {
                checkboxAvdValue(time, 150f, 167f, ShapeDeselectRingAlpha, 1f, CouiShapeCheckboxEasing)
            }
            val fillAlpha = if (selecting) {
                checkboxAvdValue(time, 33f, 66f, 0f, 1f, CouiShapeCheckboxEasing)
            } else {
                checkboxAvdValue(time, 167f, 200f, 1f, 0f, CouiShapeCheckboxEasing)
            }
            val mark = if (selecting) {
                checkboxAvdValue(time, 33f, 200f, 0f, 1f, CouiShapeCheckboxEasing)
            } else {
                checkboxAvdValue(time, 33f, 200f, 1f, 0f, CouiShapeCheckboxEasing)
            }
            val pop = checkboxShapePop(time, 33f, 200f, CouiShapeCheckboxEasing, CouiShapeCheckboxEasing)
            scale(scale = pop) {
                drawCheckboxRoundBox(shape.avdRing, ringColor, ringAlpha, shape.ringStroke)
                // #4D000000 while selecting, black at 0.4 while deselecting.
                drawCheckboxRoundBox(shape.avdDarkFill, darkFillColor, if (selecting) ShapeAvdDarkFillAlpha else 1f)
                drawCheckboxRoundBox(shape.avdFill, fillColor, fillAlpha)
                drawTrimmedCheckmark(
                    color = markColor,
                    fraction = mark,
                    path = checkPath,
                    cache = shape.check,
                    stroke = shape.checkStroke,
                )
            }
        }

        CheckboxShapeAvd.UnselectedToPart, CheckboxShapeAvd.PartToUnselected -> {
            val toPart = avd == CheckboxShapeAvd.UnselectedToPart
            val pop = if (toPart) {
                checkboxShapePop(time, 20f, 180f, CouiShapeCheckboxEasing, CouiShapeCheckboxPartSettleEasing)
            } else {
                checkboxShapePop(time, 20f, 180f, CouiShapeCheckboxLeavePartRiseEasing, CouiShapeCheckboxLeavePartSettleEasing)
            }
            val fillAlpha = if (toPart) {
                checkboxAvdValue(time, 20f, 80f, 0f, 1f, CouiShapeCheckboxEasing)
            } else {
                checkboxAvdValue(time, 60f, 180f, 1f, 0f, CouiShapeCheckboxEasing)
            }
            // trimPathStart 1 -> 0.5 (or back) on the dash's out-and-back path: it grows from, or
            // retracts to, its left end.
            val dash = if (toPart) {
                checkboxAvdValue(time, 20f, 180f, 0f, 1f, CouiShapeCheckboxDashGrowEasing)
            } else {
                checkboxAvdValue(time, 20f, 180f, 1f, 0f, CouiCheckboxEasing)
            }
            scale(scale = pop) {
                // #4D000000 and #4DFFFFFF.
                drawCheckboxRoundBox(shape.partDarkFill, darkFillColor, ShapeAvdDarkFillAlpha)
                drawCheckboxRoundBox(shape.partBand, ringColor, ShapePartBandAlpha, shape.partBandStroke)
                drawCheckboxRoundBox(shape.partFill, fillColor, fillAlpha)
            }
            // The dash group has no pop.
            if (dash > 0f) {
                drawLine(
                    color = markColor,
                    start = shape.dashStart,
                    end = shape.dashStart + Offset(shape.dashLength * dash, 0f),
                    strokeWidth = shape.dashWidth,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

/** A keyframed AVD value: [from] until [start] ms, eased to [to] by [end] ms, then [to]. */
private fun checkboxAvdValue(time: Float, start: Float, end: Float, from: Float, to: Float, easing: Easing): Float = when {
    time <= start -> from
    time >= end -> to
    else -> from + (to - from) * easing.transform((time - start) / (end - start))
}

/** The shape AVD pop: 1 -> [ShapePopPeakScale] by [riseEnd] ms on [rise], back to 1 by [settleEnd] ms on [settle]. */
private fun checkboxShapePop(time: Float, riseEnd: Float, settleEnd: Float, rise: Easing, settle: Easing): Float = if (time < riseEnd) {
    checkboxAvdValue(time, 0f, riseEnd, 1f, ShapePopPeakScale, rise)
} else {
    checkboxAvdValue(time, riseEnd, settleEnd, ShapePopPeakScale, 1f, settle)
}

object CheckboxDefaults {
    /**
     * The default checkbox colors (COUI `coui_btn_check_*.xml`).
     *
     * - The checkmark / indeterminate bar is white, and `coui_btn_check_inner_color_on_disabled`
     *   #8AFFFFFF / #29FFFFFF when disabled.
     * - The unchecked ring is couiColorLabelTertiary #42000000 / #4DFFFFFF, and
     *   couiColorQuarternaryNeutral #14000000 / #26FFFFFF when disabled.
     * - The checked fill is couiColorPrimary, at 30% (`fillAlpha` 0.3) when disabled.
     * - The unchecked box has no fill.
     */
    @Composable
    fun checkboxColors(
        checkedForegroundColor: Color = COUITheme.colorScheme.onPrimary,
        disabledCheckedForegroundColor: Color = COUITheme.colorScheme.disabledOnPrimaryButton,
        checkedBackgroundColor: Color = COUITheme.colorScheme.primary,
        uncheckedBackgroundColor: Color = COUITheme.colorScheme.labelTertiary,
        disabledCheckedBackgroundColor: Color = COUITheme.colorScheme.disable,
        disabledUncheckedBackgroundColor: Color = COUITheme.colorScheme.quarternaryNeutral,
        uncheckedFillColor: Color = Color.Transparent,
        disabledUncheckedFillColor: Color = Color.Transparent,
    ): CheckboxColors = remember(
        checkedForegroundColor,
        disabledCheckedForegroundColor,
        checkedBackgroundColor,
        uncheckedBackgroundColor,
        disabledCheckedBackgroundColor,
        disabledUncheckedBackgroundColor,
        uncheckedFillColor,
        disabledUncheckedFillColor,
    ) {
        CheckboxColors(
            checkedForegroundColor = checkedForegroundColor,
            disabledCheckedForegroundColor = disabledCheckedForegroundColor,
            checkedBackgroundColor = checkedBackgroundColor,
            uncheckedBackgroundColor = uncheckedBackgroundColor,
            disabledCheckedBackgroundColor = disabledCheckedBackgroundColor,
            disabledUncheckedBackgroundColor = disabledUncheckedBackgroundColor,
            uncheckedFillColor = uncheckedFillColor,
            disabledUncheckedFillColor = disabledUncheckedFillColor,
        )
    }

    /**
     * The colors of the COUI shape checkbox, laid over images and colored backdrops
     * (`Widget.COUI.CompoundButton.COUICheckBox.Shape`, theme attribute `couiShapeCheckBoxStyle`,
     * drawable `coui_shape_checkbox_state`). Use them with `style = CheckboxStyle.Shape`, which
     * draws the shape geometry and plays the shape AVDs. Only the unchecked state differs from
     * [checkboxColors]; the same in light and dark themes:
     *
     * - `coui_shape_btn_check_off_normal`: a black 40% fill inside a white 40% ring.
     * - `coui_shape_btn_check_off_disabled`: a black 8% fill inside a
     *   `coui_color_disabled_neutral_dark` (#4DFFFFFF, white 30%) ring.
     * - The checked and indeterminate states reuse `coui_btn_check_on_*` / `coui_btn_part_check_on_*`.
     */
    @Composable
    fun shapeCheckboxColors(
        checkedForegroundColor: Color = COUITheme.colorScheme.onPrimary,
        disabledCheckedForegroundColor: Color = COUITheme.colorScheme.disabledOnPrimaryButton,
        checkedBackgroundColor: Color = COUITheme.colorScheme.primary,
        uncheckedBackgroundColor: Color = Color.White.copy(alpha = ShapeRingAlpha),
        disabledCheckedBackgroundColor: Color = COUITheme.colorScheme.disable,
        disabledUncheckedBackgroundColor: Color = Color.White.copy(alpha = ShapeDisabledRingAlpha),
        uncheckedFillColor: Color = Color.Black.copy(alpha = ShapeFillAlpha),
        disabledUncheckedFillColor: Color = Color.Black.copy(alpha = ShapeDisabledFillAlpha),
    ): CheckboxColors = checkboxColors(
        checkedForegroundColor = checkedForegroundColor,
        disabledCheckedForegroundColor = disabledCheckedForegroundColor,
        checkedBackgroundColor = checkedBackgroundColor,
        uncheckedBackgroundColor = uncheckedBackgroundColor,
        disabledCheckedBackgroundColor = disabledCheckedBackgroundColor,
        disabledUncheckedBackgroundColor = disabledUncheckedBackgroundColor,
        uncheckedFillColor = uncheckedFillColor,
        disabledUncheckedFillColor = disabledUncheckedFillColor,
    )

    /** The ring alpha of the shape checkbox (`coui_shape_btn_check_off_normal` `strokeAlpha` 0.4). */
    private val ShapeRingAlpha = 0.4f

    /** The disabled ring alpha of the shape checkbox (`coui_color_disabled_neutral_dark` #4DFFFFFF). */
    private val ShapeDisabledRingAlpha = 0x4D / 255f

    /** The fill alpha of the shape checkbox (`coui_shape_btn_check_off_normal` `fillAlpha` 0.4). */
    private val ShapeFillAlpha = 0.4f

    /** The disabled fill alpha of the shape checkbox (`coui_shape_btn_check_off_disabled` `fillAlpha` 0.08). */
    private val ShapeDisabledFillAlpha = 0.08f
}

/**
 * The colors of a [Checkbox].
 *
 * The unchecked state is a ring ([uncheckedBackgroundColor]) around an optional fill
 * ([uncheckedFillColor], transparent except for [CheckboxDefaults.shapeCheckboxColors]); the
 * checked and indeterminate states are a filled box ([checkedBackgroundColor]) with the mark
 * ([checkedForegroundColor]).
 */
@Immutable
data class CheckboxColors(
    private val checkedForegroundColor: Color,
    private val disabledCheckedForegroundColor: Color,
    private val checkedBackgroundColor: Color,
    private val uncheckedBackgroundColor: Color,
    private val disabledCheckedBackgroundColor: Color,
    private val disabledUncheckedBackgroundColor: Color,
    private val uncheckedFillColor: Color = Color.Transparent,
    private val disabledUncheckedFillColor: Color = Color.Transparent,
) {
    internal fun checkedForegroundColor(enabled: Boolean): Color = if (enabled) checkedForegroundColor else disabledCheckedForegroundColor

    internal fun checkedBackgroundColor(enabled: Boolean): Color = if (enabled) checkedBackgroundColor else disabledCheckedBackgroundColor

    internal fun uncheckedBackgroundColor(enabled: Boolean): Color = if (enabled) uncheckedBackgroundColor else disabledUncheckedBackgroundColor

    internal fun uncheckedFillColor(enabled: Boolean): Color = if (enabled) uncheckedFillColor else disabledUncheckedFillColor
}

// COUI checkbox geometry, expressed as fractions of the 24dp drawable viewport (coui_btn_check_*.xml).
private val ViewportSize = 24f

// Checked state (coui_btn_check_on_normal.xml): 18x18 rounded square at inset 3, corner radius 4.
private val FillInsetFraction = 3f / ViewportSize
private val FillSizeFraction = 18f / ViewportSize
private val FillCornerRadiusFraction = 4f / ViewportSize

// Unchecked state (coui_btn_check_off_normal.xml): 1.3 stroke on the 16.4x16.4 rounded rect at inset 3.8.
private val RingInsetFraction = 3.8f / ViewportSize
private val RingSizeFraction = 16.4f / ViewportSize
private val RingCornerRadiusFraction = 3.2f / ViewportSize
private val RingStrokeWidthFraction = 1.3f / ViewportSize

// Checkmark stroke of the COUI check AVD ($coui_checkbox_unselected_to_selected__0.xml): the 72-unit
// polyline (-14.12,-1) -> (-3.44,9.69) -> (16.97,-10.91) around (36, 36.764), 3.9 wide, divided by 3.
private val CheckStrokeWidthFraction = 1.3f / ViewportSize
private val CheckStartPoint = Offset(7.2933f / ViewportSize, 11.9213f / ViewportSize)
private val CheckMiddlePoint = Offset(10.8533f / ViewportSize, 15.4847f / ViewportSize)
private val CheckEndPoint = Offset(17.6567f / ViewportSize, 8.618f / ViewportSize)

// Indeterminate bar, 10dp long and revealed from the left. The AVD frames draw it 2dp thick around
// the centre ($coui_checkbox_unselected_to_part__0.xml, 30x6 in a 72 viewport); the static
// drawables move the whole box 0.666dp down and draw a 1.6dp bar at 11.866
// (coui_btn_part_check_on_normal.xml) or a 1.5dp bar at 11.5 (coui_btn_part_check_on_disabled.xml).
private val BarStartFraction = 7f / ViewportSize
private val BarLengthFraction = 10f / ViewportSize
private val AvdBarThicknessFraction = 2f / ViewportSize
private val RestIndeterminateOffsetFraction = 0.666f / ViewportSize
private val RestBarTopFraction = 11.866f / ViewportSize
private val RestBarThicknessFraction = 1.6f / ViewportSize
private val RestDisabledBarTopFraction = 11.5f / ViewportSize
private val RestDisabledBarThicknessFraction = 1.5f / ViewportSize

/**
 * Whether a [from] -> [to] change replays an AVD. Default: always while enabled, and while disabled
 * only between indeterminate and checked (coui_checkbox_state.xml has no other disabled
 * transitions). Shape: only while enabled (coui_shape_checkbox_state.xml gives its disabled states
 * no ids, so no transition reaches them).
 */
private fun checkboxAnimates(style: CheckboxStyle, enabled: Boolean, from: ToggleableState, to: ToggleableState): Boolean = enabled ||
    (style == CheckboxStyle.Default && from == ToggleableState.Indeterminate && to == ToggleableState.On) ||
    (style == CheckboxStyle.Default && from == ToggleableState.On && to == ToggleableState.Indeterminate)

/** The shape AVD of a [from] -> [to] change, or `null` where the shape checkbox reuses a default AVD. */
private fun checkboxShapeAvd(from: ToggleableState, to: ToggleableState): CheckboxShapeAvd? = when {
    from == ToggleableState.Off && to == ToggleableState.On -> CheckboxShapeAvd.UnselectedToSelected
    from == ToggleableState.On && to == ToggleableState.Off -> CheckboxShapeAvd.SelectedToUnselected
    from == ToggleableState.Off && to == ToggleableState.Indeterminate -> CheckboxShapeAvd.UnselectedToPart
    from == ToggleableState.Indeterminate && to == ToggleableState.Off -> CheckboxShapeAvd.PartToUnselected
    else -> null
}

/** Whether the default AVD timelines run a [from] -> [to] change (the shape AVDs replace some). */
private fun checkboxPlaysDefaultAvd(style: CheckboxStyle, enabled: Boolean, from: ToggleableState, to: ToggleableState): Boolean = checkboxAnimates(style, enabled, from, to) && (style == CheckboxStyle.Default || checkboxShapeAvd(from, to) == null)

// Shared easing of the COUI checkbox AVDs: cubic-bezier(0.3, 0, 0.1, 1).
private val CouiCheckboxEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

// The checkmark's settle back to 1 in coui_checkbox_unselected_to_selected: c0.3,0 0.633,1.
private val CouiCheckboxMarkSettleEasing = CubicBezierEasing(0.3f, 0f, 0.633f, 1f)

// The bar's rise to 1.08 when it leaves the indeterminate state (part_to_unselected /
// part_to_selected): c0.167,0 0.1,1.
private val CouiCheckboxBarRiseEasing = CubicBezierEasing(0.167f, 0f, 0.1f, 1f)

/**
 * The reveal of a growing indeterminate bar (unselected_to_part, selected_to_part): the AVD morphs
 * a clip path on [CouiCheckboxEasing] and the bar's own path on a linear interpolator
 * (`c0.167,0.167 0.833,0.833`), both growing from the left, so the visible bar is the narrower of
 * the two: it starts on the easing and continues linearly after about 17% of the time.
 */
private val CouiCheckboxBarGrowEasing = Easing { fraction -> min(CouiCheckboxEasing.transform(fraction), fraction) }

// Every COUI checkbox AVD pops its layers 1 -> 1.08 -> 1 (150ms up + 150ms down).
private val PopPeakScale = 1.08f
private val PopHalfDurationMillis = 150

// The shape AVDs: c0.33,0 0.67,1 (also written c0.333,0 0.667,1) for the pop, the fills and the check.
private val CouiShapeCheckboxEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

// unselected_to_part: the pop settles on c0.333,0 0.25,1 and the dash grows on c0.654,0 0.58,1.
private val CouiShapeCheckboxPartSettleEasing = CubicBezierEasing(0.333f, 0f, 0.25f, 1f)
private val CouiShapeCheckboxDashGrowEasing = CubicBezierEasing(0.654f, 0f, 0.58f, 1f)

// part_to_unselected: the pop rises on c0.167,0 0.833,1 and settles on c0.167,0 0.25,1.
private val CouiShapeCheckboxLeavePartRiseEasing = CubicBezierEasing(0.167f, 0f, 0.833f, 1f)
private val CouiShapeCheckboxLeavePartSettleEasing = CubicBezierEasing(0.167f, 0f, 0.25f, 1f)

// Every shape AVD pops its box 1 -> 1.2 -> 1.
private val ShapePopPeakScale = 1.2f

// The AVD alphas relative to the resting 0.4 of the shape colors: the ring's strokeAlpha 0.3 while
// selecting and 0.05 while deselecting; #4D000000 / #4DFFFFFF (0.3) for the dark fill and the band.
private val ShapeSelectRingAlpha = 0.3f / 0.4f
private val ShapeDeselectRingAlpha = 0.05f / 0.4f
private val ShapeAvdDarkFillAlpha = 0x4D / 255f / 0.4f
private val ShapePartBandAlpha = 0x4D / 255f / 0.4f

// COUICheckBox press ripple: circle mask of checkbox_ripple_bg_radius.
private val CheckboxPressRadius = 20.dp
