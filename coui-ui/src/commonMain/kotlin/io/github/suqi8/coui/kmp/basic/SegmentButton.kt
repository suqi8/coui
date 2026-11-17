// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangedIgnoreConsumed
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect
import io.github.suqi8.coui.kmp.effect.FrameworkEdgeBlendMode
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBackdropSpec
import io.github.suqi8.coui.kmp.effect.ShadowEdgePresets
import io.github.suqi8.coui.kmp.effect.ShadowEdgeShader
import io.github.suqi8.coui.kmp.effect.SpotLightShader
import io.github.suqi8.coui.kmp.effect.SpotLightState
import io.github.suqi8.coui.kmp.effect.SpotLightType
import io.github.suqi8.coui.kmp.effect.StrokeEffectParams
import io.github.suqi8.coui.kmp.effect.TouchMotion
import io.github.suqi8.coui.kmp.effect.couiFocusStroke
import io.github.suqi8.coui.kmp.effect.couiPressScale
import io.github.suqi8.coui.kmp.effect.couiTouchMotion
import io.github.suqi8.coui.kmp.effect.pressSpring
import io.github.suqi8.coui.kmp.effect.splitFrameworkStroke
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asBrush
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.squircle.SquircleShape
import io.github.suqi8.coui.kmp.squircle.addSquircleRect
import io.github.suqi8.coui.kmp.squircle.isSquircleEnabled
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.COUILayoutGrid
import io.github.suqi8.coui.kmp.utils.CouiHapticEffect
import io.github.suqi8.coui.kmp.utils.rememberCouiHaptic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * A [SegmentButton] (segmented control) with COUI style, ported from COUI 17
 * `COUISegmentButtonLayout`.
 *
 * A capsule track holds one text segment per entry of [items]; a capsule indicator marks the
 * selected one and moves between segments on the COUI springs:
 * - **Position**: response 0.4, bounce 0.1 (`POS_SPRING_*`), clamped between the first and last
 *   segment centres.
 * - **Width**: response 0.6 → 0.4 and bounce 0.5 → 0.1 by how different the two widths are
 *   (`calculateDynamicWidthParam`, exponent 2), started at 1000 px/s; a growing indicator may
 *   overshoot by at most 7dp (`coui_segment_regular_extra_width`, 5dp with [SegmentButtonDefaults.PaddingSmall]).
 * - **Height**: dips to 90% and springs back (response 0.6, bounce 0.5, -500 px/s).
 *
 * Touches follow `COUISegmentButtonLayout.dispatchTouchEvent`: the touched segment shrinks with
 * the COUI press scale; tapping a segment selects it on release. Pressing the selected segment
 * (or within 10px of the indicator) grabs the indicator, which presses in and can be dragged:
 * sliding onto another segment selects it with a haptic tick, and dragging within the selected
 * segment pulls the indicator on a rubber band (stiffness 0.12, response 0.6). The whole control
 * follows the `COUIViewTouchAnimator` press scale, rubber band and drag deformation.
 *
 * Segment widths follow `onMeasure`: each segment is its label plus 12dp a side, at least 52dp.
 * A fixed width (for example `Modifier.fillMaxWidth()`), or labels wider than the available
 * width, distributes the width with `calculateChildrenWidths`: segments narrower than an equal
 * share keep their width and the rest split what is left. Otherwise the control wraps its labels.
 *
 * @param items The segment labels.
 * @param selectedIndex The index of the selected segment.
 * @param onSelectedIndexChange Called when the user selects another segment, by tap or drag.
 * @param modifier The modifier to be applied to the [SegmentButton].
 * @param enabled Whether the [SegmentButton] reacts to touches. COUI draws a disabled control
 *   unchanged (`setEnabled` only drops touches), and so does this one.
 * @param materialEffect Whether the COUI 17 material is drawn
 *   (`COUISegmentButtonLayout.setMaterialEffectEnabled(true)`, off by default in COUI): the
 *   `TYPE_FRAMEWORK_CAPSULE_6` edge light + inner shadow on the track, the
 *   `TYPE_FRAMEWORK_CAPSULE_10` edge light on the indicator, the `TYPE_CAPSULE_2` touch spotlight,
 *   and the `TYPE_CONTENT_SEGMENT_BUTTON` backdrop blur ([SegmentButtonDefaults.backdropSpec]). coui-ui
 *   has no backdrop capture of its own: the blur is drawn only when a [LocalMaterialBackdrop]
 *   provider is present, and then it replaces the track fill. Without one the track keeps its
 *   fill, COUI's non-blur fallback. The edge light and spotlight need runtime shaders.
 * @param dragAnimationEnabled Whether the whole control follows the `COUIViewTouchAnimator` touch
 *   motion (`setDragAnimationEnabled`).
 * @param hapticFeedbackEnabled Whether dragging the indicator onto another segment ticks.
 * @param responsiveWidthEnabled Whether a fixed width is capped to the COUI responsive grid
 *   (`setResponsiveWidthEnabled`, on by default): 4 grid columns of the window on small screens
 *   (the full grid), 6 on medium and 8 on large ones (`getResponsiveWidth`). A capped control is
 *   centred in the fixed width it was given.
 * @param height The height of the [SegmentButton], at least [SegmentButtonDefaults.MinHeight].
 * @param padding The inset of the indicator from the track edge (`setSegmentPadding`).
 * @param colors The [SegmentButtonColors] of the [SegmentButton].
 * @param textStyle The label style.
 */
@Composable
fun SegmentButton(
    items: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    materialEffect: Boolean = false,
    dragAnimationEnabled: Boolean = true,
    hapticFeedbackEnabled: Boolean = true,
    responsiveWidthEnabled: Boolean = true,
    height: Dp = SegmentButtonDefaults.Height,
    padding: Dp = SegmentButtonDefaults.Padding,
    colors: SegmentButtonColors = SegmentButtonDefaults.segmentButtonColors(),
    textStyle: TextStyle = SegmentButtonDefaults.textStyle(),
) {
    val state = remember { SegmentButtonState() }
    val scope = rememberCoroutineScope()
    val haptic = rememberCouiHaptic()
    val currentOnSelectedIndexChange by rememberUpdatedState(onSelectedIndexChange)
    val currentHapticEnabled by rememberUpdatedState(hapticFeedbackEnabled)
    val isDark = COUITheme.colorScheme.isDark
    val squircleEnabled = isSquircleEnabled()

    // selectSegmentAt from outside: the indicator follows the hoisted selection. A touch moves
    // the indicator at once, as COUI does, so an owner that follows later (a pager's
    // currentPage) does not pull it back in between.
    SideEffect {
        if (selectedIndex != state.paramIndex) {
            state.paramIndex = selectedIndex
            state.selected = selectedIndex
            if (state.indicatorTarget != selectedIndex) state.animateTo(selectedIndex, scope)
        }
    }
    // setEnabled(false) runs handleActionCancel.
    LaunchedEffect(enabled) {
        if (!enabled) state.cancelTouch(scope)
    }

    // The material layers: edge light + inner shadow, indicator edge, spotlight.
    val shaders = remember(materialEffect) {
        if (materialEffect && isRuntimeShaderSupported()) {
            SegmentShaders(
                trackShadow = RuntimeShader(ShadowEdgeShader.Source),
                trackEdge = RuntimeShader(ShadowEdgeShader.Source),
                indicatorEdge = RuntimeShader(ShadowEdgeShader.Source),
                spotLight = RuntimeShader(SpotLightShader.Source),
            )
        } else {
            null
        }
    }
    val spotlight = remember { SpotLightState() }

    // applyViewBlurEffect: the backdrop blur is the fill and the track turns transparent; without
    // a backdrop the fill stays (onDraw).
    val backdropProvider = if (materialEffect) LocalMaterialBackdrop.current else null
    val backdropModifier = if (backdropProvider != null) {
        val spec = SegmentButtonDefaults.backdropSpec(isDark)
        val shape = remember { SquircleShape(COUIRoundCorner.Full, COUIRoundCorner.CapsuleWeight) }
        remember(backdropProvider, spec, shape) { backdropProvider.materialBackdrop(spec, shape) }
    } else {
        null
    }
    val trackColor = if (backdropModifier != null) Color.Transparent else colors.backgroundColor
    val trackStroke = if (isDark) TrackStrokeDark else TrackStrokeLight
    val indicatorStroke = if (isDark) IndicatorStrokeDark else IndicatorStrokeLight
    val spotType = SpotLightType.Capsule2
    val spotIntensity = spotType.endIntensity(isDark)
    val spotBlend = spotType.blendMode(isDark)
    val spotNoise = spotType.noise(isDark)
    val spotDither = spotType.dither(isDark)
    // getResponsiveWidth: 8 grid columns on large screens, 6 on medium, 4 on small (small margin).
    val windowSize = LocalWindowInfo.current.containerDpSize
    val responsiveWidth = if (responsiveWidthEnabled && windowSize.width > 0.dp) {
        val columns = when (COUILayoutGrid.screenTier(windowSize.width, windowSize.height)) {
            COUILayoutGrid.ScreenTier.Large -> 8
            COUILayoutGrid.ScreenTier.Medium -> 6
            COUILayoutGrid.ScreenTier.Small -> 4
        }
        COUILayoutGrid.gridWidth(columns, windowSize.width, windowSize.height, COUILayoutGrid.MarginType.Small)
    } else {
        null
    }

    Layout(
        content = {
            items.forEachIndexed { index, label ->
                val textColor by animateColorAsState(
                    targetValue = if (index == selectedIndex) colors.selectedTextColor else colors.unselectedTextColor,
                    animationSpec = tween(TextColorDurationMillis, easing = MoveEase),
                )
                val itemInteractionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .semantics {
                            role = Role.Tab
                            selected = index == selectedIndex
                            if (!enabled) disabled()
                            onClick {
                                if (enabled && index != selectedIndex) currentOnSelectedIndexChange(index)
                                true
                            }
                        }
                        // dispatchKeyEvent: DPAD_CENTER / ENTER released on a focused segment selects it.
                        .onKeyEvent { event ->
                            val confirm = event.key == Key.Enter || event.key == Key.NumPadEnter || event.key == Key.DirectionCenter
                            if (confirm && event.type == KeyEventType.KeyUp && enabled && index != selectedIndex) {
                                currentOnSelectedIndexChange(index)
                            }
                            false
                        }
                        .focusable(enabled = enabled, interactionSource = itemInteractionSource)
                        .couiPressScale(pressed = state.pressed[index] == true, enabled = enabled)
                        // initStateEffectBackground: a TYPE_INNER COUIStrokeDrawable on each segment,
                        // a capsule of the segment bounds.
                        .couiFocusStroke(interactionSource = itemInteractionSource, shape = CircleShape, enabled = enabled, inner = true)
                        .padding(horizontal = SegmentButtonDefaults.SegmentPaddingHorizontal),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        color = textColor,
                        style = textStyle,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        modifier = modifier
            .selectableGroup()
            .couiTouchMotion(interactionSource = null, enabled = enabled && dragAnimationEnabled)
            .then(backdropModifier ?: Modifier)
            .pointerInput(enabled, shaders != null) {
                if (!enabled) return@pointerInput
                val spot = shaders != null
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    state.beginGesture(scope)
                    if (spot) scope.launch { spotlight.glow(down.position) }
                    // ACTION_DOWN
                    state.lastTouch = state.touch
                    state.touch = state.indexAt(down.position.x)
                    state.press(state.touch, true)
                    if (!state.dragging) {
                        val grab = state.selected == state.touch || state.isInIndicator(down.position)
                        state.dragging = grab
                        if (grab) {
                            state.lastX = down.position.x
                            state.centerLastX = state.centerX
                            state.pressIndicator(true, scope)
                        }
                    }
                    // The slop the foundation drag detectors use for this pointer type, so a
                    // dragged indicator claims the gesture no later than a pager would take it.
                    val touchSlop = viewConfiguration.touchSlop *
                        (if (down.type == PointerType.Mouse) MOUSE_TO_TOUCH_SLOP_RATIO else 1f)
                    var claimed = false
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id }
                        if (change == null) {
                            state.cancelTouch(scope)
                            break
                        }
                        if (!change.pressed) {
                            // ACTION_UP
                            state.lastTouch = state.touch
                            state.touch = state.indexAt(change.position.x)
                            state.resetTouchEffect(scope)
                            state.dragging = false
                            val target = state.touch
                            if (target != state.selected && target >= 0) {
                                state.select(target, scope)
                                currentOnSelectedIndexChange(target)
                            }
                            break
                        }
                        if (!claimed && state.dragging && abs(change.position.x - down.position.x) >= touchSlop) {
                            // attemptClaimDrag: past the touch slop a dragged indicator keeps the
                            // gesture, so a horizontal pager or scroller can no longer take it.
                            claimed = true
                        }
                        if (claimed) {
                            change.consume()
                        } else {
                            // Parents see the event after this node: check on the final pass
                            // whether one of them consumed it.
                            val final = awaitPointerEvent(PointerEventPass.Final)
                            val finalChange = final.changes.firstOrNull { it.id == down.id }
                            if (finalChange != null && finalChange.isConsumed && finalChange.positionChangedIgnoreConsumed()) {
                                // A scrolling parent took the gesture: ACTION_CANCEL.
                                state.cancelTouch(scope)
                                break
                            }
                        }
                        if (!change.positionChangedIgnoreConsumed()) continue
                        // ACTION_MOVE
                        if (spot) scope.launch { spotlight.follow(change.position) }
                        state.lastTouch = state.touch
                        state.touch = state.indexAt(change.position.x)
                        if (state.dragging) {
                            val target = state.touch
                            if (target != state.selected && target >= 0) {
                                state.select(target, scope)
                                currentOnSelectedIndexChange(target)
                                if (currentHapticEnabled) haptic(CouiHapticEffect.Switch)
                                state.press(state.lastSelected, false)
                                state.press(state.selected, true)
                            } else if (!state.posRunning) {
                                state.rubber(change.position.x, scope)
                            }
                        } else if (state.touch != state.lastTouch) {
                            state.press(if (state.lastTouch < 0) state.lastSelected else state.lastTouch, false)
                        }
                    }
                    state.pressed.clear()
                    if (spot) scope.launch { spotlight.extinguish() }
                }
            }
            .drawWithCache {
                val trackPath = Path().apply {
                    addSquircleRect(size.width, size.height, size.height / 2f, COUIRoundCorner.CapsuleWeight, squircleEnabled)
                }
                val indicatorPath = Path()
                val trackShadowBrush: Brush?
                val trackEdgeBrush: Brush?
                if (shaders != null && size.width > 0f && size.height > 0f) {
                    // applyInnerShadowStrokeParams: TYPE_FRAMEWORK_CAPSULE_6 on a weight-1.0
                    // capsule (applyMaterialEffect corner params), split into its two passes.
                    configureStroke(shaders.trackShadow, size, size.height / 2f, TrackStrokeWeight, trackStroke, withShadow = true)
                    configureStroke(shaders.trackEdge, size, size.height / 2f, TrackStrokeWeight, trackStroke, withShadow = true)
                    splitFrameworkStroke(shadowPass = shaders.trackShadow, edgePass = shaders.trackEdge)
                    trackShadowBrush = shaders.trackShadow.asBrush()
                    trackEdgeBrush = shaders.trackEdge.asBrush()
                } else {
                    trackShadowBrush = null
                    trackEdgeBrush = null
                }
                val shadowOffset = IndicatorShadowOffset.toPx()

                onDrawWithContent {
                    // onDraw: the track fill (skipped while the backdrop blur fills the track).
                    if (trackColor.alpha > 0f) drawPath(trackPath, trackColor)
                    if (trackShadowBrush != null && trackEdgeBrush != null) {
                        drawRect(trackShadowBrush)
                        drawRect(trackEdgeBrush, blendMode = FrameworkEdgeBlendMode)
                    }
                    val rect = state.indicatorRect(size.width)
                    if (rect.width > 0f && rect.height > 0f) {
                        val radius = rect.height / 2f
                        indicatorPath.rewind()
                        indicatorPath.addSquircleRect(rect, radius, COUIRoundCorner.CapsuleWeight, squircleEnabled)
                        // drawShadowWithGradient: below the indicator, outside its outline.
                        clipPath(indicatorPath, ClipOp.Difference) {
                            val shadowTopLeft = Offset(rect.left, rect.top + shadowOffset)
                            val shadowSize = Size(rect.width, size.height - shadowTopLeft.y)
                            if (shadowSize.height > 0f) {
                                if (isDark) {
                                    drawRoundRect(
                                        color = colors.indicatorShadowColor,
                                        topLeft = shadowTopLeft,
                                        size = shadowSize,
                                        cornerRadius = CornerRadius(radius),
                                    )
                                } else {
                                    drawRoundRect(
                                        brush = Brush.radialGradient(
                                            colors = listOf(colors.indicatorShadowColor, colors.indicatorShadowEndColor),
                                            center = rect.center,
                                            radius = rect.width / 2f,
                                            tileMode = TileMode.Clamp,
                                        ),
                                        topLeft = shadowTopLeft,
                                        size = shadowSize,
                                        cornerRadius = CornerRadius(radius),
                                    )
                                }
                            }
                        }
                        drawPath(indicatorPath, colors.indicatorColor)
                        if (shaders != null) {
                            // drawIndicatorShadowEdge: the AGSL COUIShadowEdgeDrawable, edge only,
                            // weight 2, drawn source-over inside the indicator.
                            val indicatorSize = Size(rect.width, rect.height)
                            configureStroke(shaders.indicatorEdge, indicatorSize, radius, IndicatorStrokeWeight, indicatorStroke, withShadow = false)
                            clipPath(indicatorPath) {
                                translate(rect.left, rect.top) {
                                    drawRect(shaders.indicatorEdge.asBrush(), size = indicatorSize)
                                }
                            }
                        }
                    }
                    drawContent()
                    // drawSpotLight: over the labels, clipped to the track.
                    if (shaders != null) {
                        val glow = spotlight.progress.value
                        if (glow > 0f) {
                            val light = spotlight.position.value
                            SpotLightShader.setUniforms(
                                shader = shaders.spotLight,
                                lightX = light.x,
                                lightY = light.y,
                                radius = SpotLightShader.radius(glow, spotlight.extinguishing),
                                intensity = spotIntensity * glow,
                                noise = spotNoise,
                                dither = spotDither,
                            )
                            clipPath(trackPath) {
                                drawRect(brush = shaders.spotLight.asBrush(), blendMode = spotBlend)
                            }
                        }
                    }
                }
            },
    ) { measurables, constraints ->
        val paddingPx = padding.roundToPx()
        val heightPx = constraints.constrainHeight(height.coerceAtLeast(SegmentButtonDefaults.MinHeight).roundToPx())
        val innerHeight = (heightPx - 2 * paddingPx).coerceAtLeast(0)
        val minSegment = SegmentButtonDefaults.SegmentMinWidth.roundToPx()
        // getSegmentTextWidth: the label plus its horizontal padding, at least the min width.
        val natural = IntArray(measurables.size) { max(measurables[it].maxIntrinsicWidth(innerHeight), minSegment) }
        val naturalTotal = natural.sum() + 2 * paddingPx
        val fixedWidth = constraints.hasFixedWidth
        val width: Int
        val widths: IntArray
        if (fixedWidth || naturalTotal > constraints.maxWidth || naturalTotal < constraints.minWidth) {
            width = when {
                // onMeasure (EXACTLY): size = min(getResponsiveWidth(), size).
                fixedWidth && responsiveWidth != null -> min(constraints.maxWidth, responsiveWidth.roundToPx())

                fixedWidth -> constraints.maxWidth

                else -> naturalTotal.coerceIn(constraints.minWidth, constraints.maxWidth)
            }
            widths = distributeSegmentWidths(natural, (width - 2 * paddingPx).coerceAtLeast(0))
        } else {
            width = naturalTotal
            widths = natural
        }
        val placeables = measurables.mapIndexed { index, measurable ->
            measurable.measure(Constraints.fixed(widths[index].coerceAtLeast(0), innerHeight))
        }
        val measureDensity: Density = this
        val rtl = layoutDirection == LayoutDirection.Rtl
        layout(width, heightPx) {
            // A horizontal LinearLayout: the first segment sits at the start edge. The indicator
            // and the hit tests work in absolute coordinates.
            var x = paddingPx
            val lefts = FloatArray(placeables.size)
            val rights = FloatArray(placeables.size)
            placeables.forEachIndexed { index, placeable ->
                val left = if (rtl) width - x - widths[index] else x
                placeable.place(left, paddingPx)
                lefts[index] = left.toFloat()
                rights[index] = (left + widths[index]).toFloat()
                x += widths[index]
            }
            state.onLayout(
                density = measureDensity,
                lefts = lefts,
                rights = rights,
                width = width.toFloat(),
                height = heightPx.toFloat(),
                padding = paddingPx.toFloat(),
                paddingDp = padding,
            )
        }
    }
}

/** The runtime shaders of the material layers. */
private class SegmentShaders(
    val trackShadow: RuntimeShader,
    val trackEdge: RuntimeShader,
    val indicatorEdge: RuntimeShader,
    val spotLight: RuntimeShader,
)

/**
 * Feeds [ShadowEdgeShader] a capsule stroke of [params] on a [size] pixel shape (edge style 0,
 * shadow style 0). [withShadow] = `false` turns the inner shadow off, as the indicator's
 * `setShadowEnable(false)` does.
 */
private fun Density.configureStroke(
    shader: RuntimeShader,
    size: Size,
    cornerRadius: Float,
    weight: Float,
    params: StrokeEffectParams,
    withShadow: Boolean,
) {
    shader.setFloatUniform("u_commonArray", ShadowEdgeShader.commonArray(size.width, size.height, cornerRadius, weight))
    shader.setFloatUniform("u_shadow", if (withShadow) 1f else 0f)
    shader.setFloatUniform(
        "u_shadowArray",
        ShadowEdgeShader.shadowArray(
            spec = ShadowEdgePresets.CapsuleShadow,
            width = size.width,
            height = size.height,
            fadeWidthScale = params.shadowFadeScale,
            fadeAlphaInScale = params.shadowFadeIn,
            fadeAlphaOutScale = params.shadowFadeOut,
            density = density,
        ),
    )
    shader.setFloatUniform("u_edge", 1f)
    shader.setFloatUniform(
        "u_edgeArray",
        ShadowEdgeShader.edgeArray(
            spec = ShadowEdgePresets.CapsuleEdge,
            width = size.width,
            height = size.height,
            lineWidth = params.edgeWidth.dp.toPx(),
            lineAlpha = params.edgeAlpha,
            lineAngle = params.edgeAngle,
            density = density,
        ),
    )
}

/**
 * `COUISegmentButtonLayout.calculateChildrenWidths`: segments narrower than an equal share of
 * [total] keep their [lengths]; the others split what is left equally. When every segment is
 * narrower than the first equal share, all of them take that share; leftover width after the
 * split goes evenly to every segment but the widest.
 */
private fun distributeSegmentWidths(lengths: IntArray, total: Int): IntArray {
    val size = lengths.size
    if (size == 0) return IntArray(0)
    val order = (0 until size).sortedBy { lengths[it] }
    var remaining = total.toFloat()
    val equalShare = remaining / size
    val out = FloatArray(size)
    var i = 0
    var share = equalShare
    var pivot = -1
    while (i < size) {
        val length = lengths[order[i]].toFloat()
        if (length < share) {
            out[order[i]] = length
            remaining -= length
            i++
        } else if (pivot == i) {
            while (i < size) {
                out[order[i]] = share
                i++
            }
            remaining = 0f
        } else {
            share = remaining / (size - i)
            pivot = i
        }
    }
    if (pivot == -1) return IntArray(size) { equalShare.roundToInt() }
    if (remaining > 0f) {
        for (k in 0 until size - 1) out[order[k]] += remaining / (size - 1)
    }
    return IntArray(size) { out[it].roundToInt() }
}

/**
 * `calculateDynamicWidthParam`: [base] eased towards [min] by how far apart the two widths are,
 * `base - (base - min) * (|a - b| / max(a, b))^exponent`, clamped to `[0, 1]`.
 */
private fun dynamicWidthParam(from: Float, to: Float, base: Float, min: Float, exponent: Float): Float {
    if (from <= 0f || to <= 0f) return base
    val ratio = abs(from - to) / max(from, to)
    return (base - (base - min) * ratio.pow(exponent)).coerceIn(0f, 1f)
}

/**
 * The indicator and touch state of one [SegmentButton]: the `FloatValueHolder`s the COUI springs
 * drive (centre, width, height, press scale) and the touch bookkeeping of `dispatchTouchEvent`.
 */
@Stable
private class SegmentButtonState {
    // Layout, written during placement.
    private var lefts = FloatArray(0)
    private var rights = FloatArray(0)
    private var containerWidth = 0f
    private var containerHeight = 0f
    private var paddingPx = 0f
    private var density = 1f
    private var smallPadding = false
    private var rubberMaxDistance = 0f
    private var switchDistance = 0f
    private var extraWidth = 0f
    private var laidOut = false

    // The indicator.
    var centerX by mutableFloatStateOf(0f)
    private var centerY by mutableFloatStateOf(0f)
    private var indicatorWidth by mutableFloatStateOf(0f)
    private var indicatorHeight by mutableFloatStateOf(0f)
    private var indicatorScale by mutableFloatStateOf(1f)
    private var posJob: Job? = null
    private var widthJob: Job? = null
    private var heightJob: Job? = null
    private var rubberJob: Job? = null
    private var scaleJob: Job? = null
    private var posToken = 0
    private var rubberVelocity = 0f
    var posRunning = false
        private set
    private var enableRubber = false
    var indicatorTarget = -1
        private set

    // Selection.
    var paramIndex = -1
    var selected = 0
    var lastSelected = -1
        private set

    // Touch.
    val pressed = mutableStateMapOf<Int, Boolean>()
    var dragging = false
    var touch = -1
    var lastTouch = -1
    var lastX = 0f
    var centerLastX = 0f

    private val lastIndex: Int get() = lefts.size - 1

    fun onLayout(
        density: Density,
        lefts: FloatArray,
        rights: FloatArray,
        width: Float,
        height: Float,
        padding: Float,
        paddingDp: Dp,
    ) {
        this.density = density.density
        // isSmallPadding: the inset is nearer coui_segment_padding_tiny than coui_segment_padding.
        smallPadding = abs(paddingDp.value - SegmentButtonDefaults.PaddingSmall.value) <
            abs(paddingDp.value - SegmentButtonDefaults.Padding.value)
        with(density) {
            rubberMaxDistance = (if (smallPadding) SegmentButtonDefaults.PaddingSmall else SegmentButtonDefaults.Padding).toPx()
            switchDistance = SwitchDistance.toPx()
            extraWidth = (if (smallPadding) ExtraWidthSmall else ExtraWidthRegular).toPx()
        }
        val changed = !laidOut ||
            !lefts.contentEquals(this.lefts) ||
            !rights.contentEquals(this.rights) ||
            width != containerWidth ||
            height != containerHeight ||
            padding != paddingPx
        this.lefts = lefts
        this.rights = rights
        containerWidth = width
        containerHeight = height
        paddingPx = padding
        laidOut = true
        if (changed) resetIndicator()
    }

    /** `resetIndicatorRect`: cancels every spring and snaps the indicator onto the selection. */
    private fun resetIndicator() {
        if (lefts.isEmpty()) return
        posJob?.cancel()
        rubberJob?.cancel()
        widthJob?.cancel()
        heightJob?.cancel()
        posRunning = false
        val index = selected.coerceIn(0, lastIndex)
        indicatorTarget = index
        centerX = (lefts[index] + rights[index]) / 2f
        centerY = containerHeight / 2f
        indicatorWidth = rights[index] - lefts[index]
        indicatorHeight = containerHeight - 2f * paddingPx
    }

    /** `updateIndicatorRect` + `clampIndicatorRectToContainer`. */
    fun indicatorRect(width: Float): Rect {
        if (lefts.isEmpty()) return Rect.Zero
        val halfWidth = indicatorScale * indicatorWidth / 2f
        val halfHeight = indicatorScale * indicatorHeight / 2f
        val left = max(centerX - halfWidth, rubberMaxDistance)
        val right = min(centerX + halfWidth, width - rubberMaxDistance)
        return Rect(left, centerY - halfHeight, right, centerY + halfHeight)
    }

    /** `getSelectedIndexFromX` / `isInsideView`: a dragged indicator only switches 5dp inside a segment. */
    fun indexAt(x: Float): Int {
        val inset = if (dragging) switchDistance else 0f
        for (i in lefts.indices) {
            if (x >= lefts[i] + inset && x <= rights[i] - inset) return i
        }
        return -1
    }

    /** `isTouchInIndicator`: the indicator outline grown by 10px. */
    fun isInIndicator(position: Offset): Boolean {
        val rect = indicatorRect(containerWidth)
        if (rect.width <= 0f || rect.height <= 0f) return false
        return position.x >= rect.left - IndicatorTouchSlopPx &&
            position.x <= rect.right + IndicatorTouchSlopPx &&
            position.y >= rect.top - IndicatorTouchSlopPx &&
            position.y <= rect.bottom + IndicatorTouchSlopPx
    }

    /** Re-reads the hoisted selection when the owner kept the old one after the last gesture. */
    fun beginGesture(scope: CoroutineScope) {
        if (paramIndex in lefts.indices && selected != paramIndex) {
            selected = paramIndex
            animateTo(paramIndex, scope)
        }
    }

    /** `selectSegmentAt` from a touch: the indicator moves at once, as COUI does. */
    fun select(index: Int, scope: CoroutineScope) {
        if (index !in lefts.indices || index == selected) return
        lastSelected = selected
        selected = index
        animateTo(index, scope)
    }

    /** `executeScaleAnimatorAt`: the per-segment `COUIPressFeedbackHelper`. */
    fun press(index: Int, down: Boolean) {
        if (index !in lefts.indices) return
        pressed[index] = down
    }

    /** The indicator's `COUIPressFeedbackHelper`, sized by the indicator itself. */
    fun pressIndicator(down: Boolean, scope: CoroutineScope) {
        val widthDp = indicatorWidth / density
        val heightDp = indicatorHeight / density
        val target = if (down) COUIMaterialEffect.pressTargetScale(widthDp, heightDp) else 1f
        scaleJob?.cancel()
        scaleJob = scope.launch {
            animate(indicatorScale, target, animationSpec = pressSpring(widthDp, heightDp, down)) { value, _ ->
                indicatorScale = value
            }
        }
    }

    /** `animateIndicatorToPosition`. */
    fun animateTo(index: Int, scope: CoroutineScope) {
        if (!laidOut || index !in lefts.indices) {
            indicatorTarget = index
            return
        }
        indicatorTarget = index
        rubberJob?.cancel()
        val targetCenter = (lefts[index] + rights[index]) / 2f
        val targetWidth = rights[index] - lefts[index]
        centerLastX = targetCenter
        val fromWidth = indicatorWidth
        val growing = targetWidth > fromWidth

        // Position: clamped between the first and the last segment centres.
        val firstCenter = (lefts[0] + rights[0]) / 2f
        val lastCenter = (lefts[lastIndex] + rights[lastIndex]) / 2f
        val minCenter = max(0f, min(firstCenter, lastCenter))
        val maxCenter = min(containerWidth, max(firstCenter, lastCenter))
        val posBounce = if (growing) translationBounceShortToLong(fromWidth, targetWidth) else PosSpringBounce
        val token = ++posToken
        posRunning = true
        posJob?.cancel()
        posJob = scope.launch {
            try {
                animate(
                    initialValue = centerX.coerceIn(minCenter, maxCenter),
                    targetValue = targetCenter.coerceIn(minCenter, maxCenter),
                    initialVelocity = 0f,
                    animationSpec = TouchMotion.spring(PosSpringResponse, posBounce, SpringThresholdPx),
                ) { value, _ -> centerX = value.coerceIn(minCenter, maxCenter) }
            } finally {
                // The end listener re-arms the rubber band.
                enableRubber = true
                if (token == posToken) posRunning = false
            }
        }

        // Width: a growing indicator may overshoot by the extra width only.
        val widthMax: Float
        val widthBounce: Float
        if (growing) {
            widthMax = targetWidth + extraWidth
            widthBounce = WidthSpringBounce
        } else {
            widthMax = Float.POSITIVE_INFINITY
            widthBounce = dynamicWidthParam(fromWidth, targetWidth, WidthSpringBounce, WidthSpringBounceMin, WidthDiffExponent)
        }
        val widthResponse = dynamicWidthParam(fromWidth, targetWidth, WidthSpringResponse, WidthSpringResponseMin, WidthDiffExponent)
        widthJob?.cancel()
        widthJob = scope.launch {
            animate(
                initialValue = fromWidth,
                targetValue = targetWidth,
                initialVelocity = WidthSpringVelocity,
                animationSpec = TouchMotion.spring(widthResponse, widthBounce, SpringThresholdPx),
            ) { value, _ -> indicatorWidth = min(value, widthMax) }
        }

        // Height: dips towards 90% and springs back.
        val fullHeight = containerHeight - 2f * paddingPx
        val minHeight = MinHeightFraction * fullHeight
        heightJob?.cancel()
        heightJob = scope.launch {
            animate(
                initialValue = max(minHeight, indicatorHeight),
                targetValue = max(minHeight, fullHeight),
                initialVelocity = HeightSpringVelocity,
                animationSpec = TouchMotion.spring(HeightSpringResponse, HeightSpringBounce, SpringThresholdPx),
            ) { value, _ -> indicatorHeight = max(abs(value), minHeight) }
        }
    }

    /** `getEffectiveTranslationBounceForShortToLong`. */
    private fun translationBounceShortToLong(from: Float, to: Float): Float {
        if (!smallPadding || from <= 0f || to <= 0f) return PosSpringBounce
        return dynamicWidthParam(from, to, PosSpringBounce, PosSpringBounceMin, PosDiffExponent)
    }

    /** `mOverDistance` as `updateIndicatorRect` derives it: the press slack plus the edge allowance. */
    private fun overDistance(): Float {
        val slack = ((1f - indicatorScale) * indicatorWidth / 2f).toInt()
        val atEdge = selected == 0 || selected == lastIndex
        var over = slack + if (atEdge) paddingPx.toInt() else switchDistance.toInt()
        if (atEdge) over = min(over, slack + max(0, paddingPx.toInt() - rubberMaxDistance.toInt()))
        return over.toFloat()
    }

    /** `handleRubberBandEffect`: the indicator follows the finger on a rubber band. */
    fun rubber(x: Float, scope: CoroutineScope) {
        if (enableRubber) {
            lastX = x
            enableRubber = false
        }
        val dx = x - lastX
        if (dx == 0f) return
        val offset = TouchMotion.rubberBand(dx, RubberStiffness, overDistance())
        springCenterTo(centerLastX + offset, scope)
    }

    /** `resetRubberBandEffect`: the indicator springs back onto its segment. */
    private fun resetRubber(index: Int, scope: CoroutineScope) {
        if (index !in lefts.indices) return
        springCenterTo((lefts[index] + rights[index]) / 2f, scope)
    }

    private fun springCenterTo(target: Float, scope: CoroutineScope) {
        val velocity = if (rubberJob?.isActive == true) rubberVelocity else 0f
        rubberJob?.cancel()
        rubberJob = scope.launch {
            animate(
                initialValue = centerX,
                targetValue = target,
                initialVelocity = velocity,
                animationSpec = TouchMotion.spring(RubberSpringResponse, RubberSpringBounce, SpringThresholdPx),
            ) { value, v ->
                centerX = value
                rubberVelocity = v
            }
        }
    }

    /** `resetTouchEffect`. */
    fun resetTouchEffect(scope: CoroutineScope) {
        press(if (lastTouch < 0) selected else lastTouch, false)
        if (dragging) {
            pressIndicator(false, scope)
            if (!posRunning) resetRubber(selected, scope)
        }
    }

    /** `handleActionCancel`. */
    fun cancelTouch(scope: CoroutineScope) {
        resetTouchEffect(scope)
        dragging = false
        pressed.clear()
    }
}

object SegmentButtonDefaults {

    /** The default height: the `SegmentButton` style `layout_height`, 40dp. */
    val Height = 40.dp

    /** The height of the small variant: the `SegmentButton.Tiny` style `layout_height`, 32dp. */
    val HeightSmall = 32.dp

    /** The minimum height, `coui_segment_min_height` 28dp. */
    val MinHeight = 28.dp

    /** The indicator inset, `coui_segment_padding` 4dp (`PADDING_LARGE`). */
    val Padding = 4.dp

    /** The small indicator inset, `coui_segment_padding_tiny` 2dp (`PADDING_SMALL`, `SegmentButton.Tiny`). */
    val PaddingSmall = 2.dp

    /** The horizontal padding of each label, `coui_segment_btn_padding_horizontal` 12dp. */
    val SegmentPaddingHorizontal = 12.dp

    /** The minimum width of a segment, `coui_segment_min_width` 52dp. */
    val SegmentMinWidth = 52.dp

    /** The label style: `couiTextButtonM`, 14sp sans-serif-medium. */
    @Composable
    fun textStyle(): TextStyle = COUITheme.textStyles.buttonM

    /**
     * The default [SegmentButtonColors]:
     * - track `coui_color_segment_button_background` #0F000000 / #1FFFFFFF (night);
     * - indicator `coui_color_segment_button_indicator` #B2FFFFFF / #33FFFFFF (night);
     * - both labels `?couiColorLabelPrimary` (`loadAttr`);
     * - indicator shadow `coui_segment_button_shadow_color` #0D000000 / #14000000 (night), fading to
     *   `coui_segment_button_shadow_end_color` #03000000 in the light scheme.
     */
    @Composable
    fun segmentButtonColors(
        backgroundColor: Color = if (isDarkScheme()) BackgroundDark else BackgroundLight,
        indicatorColor: Color = if (isDarkScheme()) IndicatorDark else IndicatorLight,
        selectedTextColor: Color = COUITheme.colorScheme.labelPrimary,
        unselectedTextColor: Color = COUITheme.colorScheme.labelPrimary,
        indicatorShadowColor: Color = if (isDarkScheme()) ShadowDark else ShadowLight,
        indicatorShadowEndColor: Color = ShadowEnd,
    ): SegmentButtonColors = remember(
        backgroundColor,
        indicatorColor,
        selectedTextColor,
        unselectedTextColor,
        indicatorShadowColor,
        indicatorShadowEndColor,
    ) {
        SegmentButtonColors(
            backgroundColor = backgroundColor,
            indicatorColor = indicatorColor,
            selectedTextColor = selectedTextColor,
            unselectedTextColor = unselectedTextColor,
            indicatorShadowColor = indicatorShadowColor,
            indicatorShadowEndColor = indicatorShadowEndColor,
        )
    }

    /**
     * The COUI 17 material of the track, `COUIMaterialBlurEffect.TYPE_CONTENT_SEGMENT_BUTTON`:
     * 25px, then `#7F333333` COLOR_DODGE and `#99EFEFEF` LUMINOSITY (light), or `#7F7F7F7F`
     * OVERLAY and `#99262626` LUMINOSITY (dark).
     *
     * @param isDark Whether the dark-scheme layers are used.
     */
    fun backdropSpec(isDark: Boolean): MaterialBackdropSpec = if (isDark) BackdropDark else BackdropLight

    private val BackgroundLight = Color(0x0F000000)
    private val BackgroundDark = Color(0x1FFFFFFF)
    private val IndicatorLight = Color(0xB2FFFFFF)
    private val IndicatorDark = Color(0x33FFFFFF)
    private val ShadowLight = Color(0x0D000000)
    private val ShadowDark = Color(0x14000000)
    private val ShadowEnd = Color(0x03000000)

    private val BackdropLight = MaterialBackdropSpec(
        blurRadiusPx = 25f,
        layer1 = Color(0x7F333333),
        mode1 = BlendMode.ColorDodge,
        layer2 = Color(0x99EFEFEF),
        mode2 = BlendMode.Luminosity,
    )

    private val BackdropDark = MaterialBackdropSpec(
        blurRadiusPx = 25f,
        layer1 = Color(0x7F7F7F7F),
        mode1 = BlendMode.Overlay,
        layer2 = Color(0x99262626),
        mode2 = BlendMode.Luminosity,
    )

    @Composable
    private fun isDarkScheme(): Boolean = COUITheme.colorScheme.isDark
}

/**
 * The colors of a [SegmentButton].
 *
 * @param backgroundColor The track fill (replaced by the backdrop blur when it is drawn).
 * @param indicatorColor The indicator fill.
 * @param selectedTextColor The label color of the selected segment.
 * @param unselectedTextColor The label color of the other segments.
 * @param indicatorShadowColor The shadow under the indicator: the gradient centre in the light
 *   scheme, the solid fill in the dark scheme (`drawShadowWithGradient`).
 * @param indicatorShadowEndColor The light-scheme gradient edge of the indicator shadow.
 */
@Immutable
data class SegmentButtonColors(
    val backgroundColor: Color,
    val indicatorColor: Color,
    val selectedTextColor: Color,
    val unselectedTextColor: Color,
    val indicatorShadowColor: Color,
    val indicatorShadowEndColor: Color,
)

/** `COUIMoveEaseInterpolator`, the label color fade. */
private val MoveEase = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** `COLOR_ANIMATOR_TIME`. */
private val TextColorDurationMillis = 300

/** `POS_SPRING_RESPONSE` / `POS_SPRING_BOUNCE` / `POS_SPRING_BOUNCE_MIN` / `POS_DIFF_CURVE_EXPONENT`. */
private val PosSpringResponse = 0.4f
private val PosSpringBounce = 0.1f
private val PosSpringBounceMin = 0.03f
private val PosDiffExponent = 1f

/** `WIDTH_SPRING_*` and `WIDTH_DIFF_CURVE_EXPONENT`; the start velocity is in px/s, as in COUI. */
private val WidthSpringResponse = 0.6f
private val WidthSpringResponseMin = 0.4f
private val WidthSpringBounce = 0.5f
private val WidthSpringBounceMin = 0.1f
private val WidthSpringVelocity = 1000f
private val WidthDiffExponent = 2f

/** `HEIGHT_SPRING_*` and `MIN_HEIGHT_PERCENT`. */
private val HeightSpringResponse = 0.6f
private val HeightSpringBounce = 0.5f
private val HeightSpringVelocity = -500f
private val MinHeightFraction = 0.9f

/** The rubber spring (`DEFAULT_SPRING_RESPONSE` / `DEFAULT_SPRING_BOUNCE`) and `DEFAULT_OVER_STIFFNESS`. */
private val RubberSpringResponse = 0.6f
private val RubberSpringBounce = 0f
private val RubberStiffness = 0.12f

/** 0.75 x the 1px `DynamicAnimation` minimum visible change. */
private val SpringThresholdPx = 0.75f

/** The foundation mouse slop over its touch slop (0.125dp / 18dp). */
private const val MOUSE_TO_TOUCH_SLOP_RATIO = 0.125f / 18f

/** `isTouchInIndicator` slop, raw pixels. */
private val IndicatorTouchSlopPx = 10f

/** `coui_segment_switch_distance`. */
private val SwitchDistance = 5.dp

/** `coui_segment_regular_extra_width` / `coui_segment_small_extra_width`. */
private val ExtraWidthRegular = 7.dp
private val ExtraWidthSmall = 5.dp

/** `coui_segment_shadow_offset`. */
private val IndicatorShadowOffset = 2.dp

/** `MATERIAL_CORNER_WEIGHT`: the corner weight of the track's framework stroke. */
private val TrackStrokeWeight = 1f

/** `INDICATOR_STROKE_CORNER_WEIGHT`: the indicator `COUIShadowEdgeDrawable` weight. */
private val IndicatorStrokeWeight = 2f

/** `FRAMEWORK_CAPSULE_PARAMS_6_LIGHT` (`TYPE_FRAMEWORK_CAPSULE_6`, the track stroke). */
private val TrackStrokeLight = StrokeEffectParams(0.6f, 2.5f, 0f, 0.15f, 1f, 1.15f)

/** `FRAMEWORK_CAPSULE_PARAMS_6_DARK`. */
private val TrackStrokeDark = StrokeEffectParams(0.8f, 2.5f, 0f, 0.05f, 1f, 1f)

/** `FRAMEWORK_CAPSULE_PARAMS_10_LIGHT` (`TYPE_FRAMEWORK_CAPSULE_10`, the indicator edge). */
private val IndicatorStrokeLight = StrokeEffectParams(0.8f, 1.8f, 0f, 0.05f, 0f, 1f)

/** `FRAMEWORK_CAPSULE_PARAMS_10_DARK`. */
private val IndicatorStrokeDark = StrokeEffectParams(0.15f, 1.8f, 0f, 0.02f, 0f, 1f)
