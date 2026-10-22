// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.TouchMotion
import io.github.suqi8.coui.kmp.squircle.squircleClip
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.COUITheme.isDynamicColor
import io.github.suqi8.coui.kmp.theme.LocalColors
import io.github.suqi8.coui.kmp.utils.CouiHapticEffect
import io.github.suqi8.coui.kmp.utils.rememberCouiHaptic
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * A [Switch] component with COUI style.
 *
 * Follows the COUI 17 `COUISwitch`: the thumb springs between the ends and stretches with its
 * position on the way, a toggle in flight is redirected instead of restarted, and the thumb can be
 * dragged across the track.
 *
 * @param checked The checked state of the [Switch].
 * @param onCheckedChange The callback to be called when the state of the [Switch] changes.
 * @param modifier The modifier to be applied to the [Switch].
 * @param colors The [SwitchColors] of the [Switch].
 * @param enabled Whether the [Switch] is enabled.
 * @param isLoading Whether the [Switch] is in the COUI loading state: the thumb shrinks away and
 *   is replaced by a spinning gradient arc; touch input is swallowed while loading.
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    colors: SwitchColors = SwitchDefaults.switchColors(),
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    val currentChecked by rememberUpdatedState(checked)
    val interactionSource = remember { MutableInteractionSource() }
    val isSourcePressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val scope = rememberCoroutineScope()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val couiHaptic = rememberCouiHaptic()
    val currentCouiHaptic by rememberUpdatedState(couiHaptic)

    // COUI loading interpolator (COUISwitch.initStartLoadingAnimator()).
    val couiLoadingEasing = remember { CubicBezierEasing(0.3f, 0f, 0.1f, 1f) }

    val motion = remember { SwitchMotionState(if (checked) 1f else 0f) }

    // COUISwitch.animateWhenStateChanged(): the toggle spring, retargeted (velocity kept) when the
    // state flips again mid-flight. Also springs the thumb back when a drag settled on a state the
    // owner did not accept.
    LaunchedEffect(checked, motion.settleCount) {
        val target = if (checked) 1f else 0f
        if (motion.fraction.value == target && !motion.fraction.isRunning) return@LaunchedEffect
        motion.isDragging = false
        val token = ++motion.toggleToken
        motion.isToggling = true
        try {
            motion.fraction.animateTo(target, SwitchMotion.ToggleSpring)
        } finally {
            if (motion.toggleToken == token) motion.isToggling = false
        }
    }

    // COUISwitch StateEffectAnimator press / hover layers (response 0.3, bounce 0).
    val pressed = enabled && (motion.isPressed || isSourcePressed)
    LaunchedEffect(pressed) {
        motion.pressProgress.animateTo(if (pressed) 1f else 0f, SwitchMotion.StateEffectSpring)
    }
    val hovered = enabled && isHovered
    LaunchedEffect(hovered) {
        motion.hoverProgress.animateTo(if (hovered) 1f else 0f, SwitchMotion.StateEffectSpring)
    }

    // COUISwitch.drawOuterCircle(): the thumb colour follows the checked state without a transition.
    val thumbColor = if (checked) colors.checkedThumbColor(enabled) else colors.uncheckedThumbColor(enabled)
    val uncheckedTrackColor = colors.uncheckedTrackColor(true)
    val checkedTrackColor = colors.checkedTrackColor(true)
    // COUISwitch.drawBar(): a disabled switch shows the flat disabled tint of its state.
    val disabledTrackColor = if (checked) colors.checkedTrackColor(false) else colors.uncheckedTrackColor(false)
    val pressColor = COUITheme.colorScheme.press
    val hoverColor = COUITheme.colorScheme.hover

    // COUI loading state (COUISwitch.startLoading()/stopLoading()).
    val thumbScale = remember { Animatable(1f) }
    val loadingAlpha = remember { Animatable(0f) }
    val loadingScale = remember { Animatable(0.5f) }
    val loadingRotation = remember { Animatable(0f) }
    LaunchedEffect(isLoading) {
        if (isLoading) {
            loadingAlpha.snapTo(0f)
            loadingScale.snapTo(0.5f)
            launch { thumbScale.animateTo(0f, tween(durationMillis = 433, easing = couiLoadingEasing)) }
            launch { loadingScale.animateTo(1f, tween(durationMillis = 550, easing = couiLoadingEasing)) }
            launch { loadingAlpha.animateTo(1f, tween(durationMillis = 550, easing = couiLoadingEasing)) }
            while (true) {
                loadingRotation.snapTo(0f)
                loadingRotation.animateTo(360f, tween(durationMillis = 800, easing = LinearEasing))
            }
        } else {
            thumbScale.snapTo(1f)
            loadingAlpha.animateTo(0f, tween(durationMillis = 100, easing = couiLoadingEasing))
        }
    }

    // Loading switches cannot be toggled (COUISwitch loading style swallows touch).
    val interactive = enabled && !isLoading
    val hasCallback = onCheckedChange != null
    val toggleableModifier = if (hasCallback) {
        remember(checked, interactive, interactionSource) {
            Modifier.toggleable(
                value = checked,
                onValueChange = { v ->
                    currentOnCheckedChange?.invoke(v)
                    currentCouiHaptic(CouiHapticEffect.Switch)
                },
                enabled = interactive,
                role = Role.Switch,
                interactionSource = interactionSource,
            )
        }
    } else {
        Modifier.semantics {
            role = Role.Switch
            toggleableState = ToggleableState(checked)
            if (!enabled) disabled()
        }
    }
    // COUI 17 thumb drag (COUISwitch + COUISpringDragHelper). It sits inside the toggleable so it
    // sees the pointer first and, once dragging, consumes it to cancel the tap.
    val dragModifier = if (hasCallback && interactive) {
        Modifier.pointerInput(motion, isRtl) {
            detectSwitchDrag(
                motion = motion,
                isRtl = isRtl,
                onDrag = { target -> scope.launch { motion.fraction.animateTo(target, SwitchMotion.DragSpring) } },
                onRelease = { velocity ->
                    scope.launch {
                        val travelPx = SwitchDefaults.Travel.toPx()
                        val limit = if (travelPx > 0f) SwitchMotion.TransformVelocity.toPx() / travelPx else 0f
                        val target = SwitchMotion.attachTarget(motion.fraction.value, velocity, limit)
                        motion.isAttaching = true
                        try {
                            motion.fraction.animateTo(
                                target,
                                SwitchMotion.AttachSpring,
                                initialVelocity = velocity.coerceIn(-limit, limit),
                            )
                            // COUISwitch onStateChange(IDLE) -> setChecked(calculateTargetCheckedState()).
                            val settledChecked = motion.fraction.value >= 0.5f
                            if (settledChecked != currentChecked) {
                                currentOnCheckedChange?.invoke(settledChecked)
                                currentCouiHaptic(CouiHapticEffect.Switch)
                            }
                            motion.settleCount++
                        } finally {
                            motion.isAttaching = false
                            motion.isDragging = false
                        }
                    }
                },
            )
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            .size(SwitchDefaults.TrackWidth, SwitchDefaults.TrackHeight)
            // COUISwitch clips the track to its outline, setSmoothRoundRect(rect, height / 2) without a
            // weight: the COUI 17 soft capsule.
            .squircleClip(COUIRoundCorner.Full, weight = COUIRoundCorner.UnspecifiedWeight)
            .drawBehind {
                if (!enabled) {
                    drawRect(disabledTrackColor)
                } else {
                    // The bar colour follows the thumb position (blendARGB(unchecked, checked, f)), then
                    // the hover and press layers are composited over it.
                    val fraction = motion.fraction.value.coerceIn(0f, 1f)
                    drawRect(SwitchMotion.blendArgb(uncheckedTrackColor, checkedTrackColor, fraction))
                    val hover = motion.hoverProgress.value
                    if (hover > 0f) drawRect(hoverColor.copy(alpha = hoverColor.alpha * hover))
                    val press = motion.pressProgress.value
                    if (press > 0f) drawRect(pressColor.copy(alpha = pressColor.alpha * press))
                }
            }
            .hoverable(
                interactionSource = interactionSource,
                enabled = interactive,
            )
            .then(toggleableModifier)
            .then(dragModifier),
    ) {
        Box(
            modifier = Modifier
                .align(AbsoluteAlignment.CenterLeft)
                .absoluteOffset {
                    // COUISwitch.setOuterCircleRectF(): the stretched thumb always grows to the right
                    // of its translated left edge.
                    val fraction = motion.fraction.value
                    val translation = (if (isRtl) 1f - fraction else fraction) * SwitchDefaults.Travel.toPx()
                    IntOffset((SwitchDefaults.ThumbStartOffset.toPx() + translation).roundToInt(), 0)
                }
                .layout { measurable, _ ->
                    // Stretch by resizing instead of scaling so the thumb stays a capsule.
                    val thumbPx = SwitchDefaults.ThumbSize.roundToPx()
                    val stretch = if (motion.isDragging) 1f else SwitchMotion.thumbStretch(motion.fraction.value)
                    val stretchedPx = (thumbPx * stretch).roundToInt().coerceAtLeast(0)
                    val placeable = measurable.measure(Constraints.fixed(stretchedPx, thumbPx))
                    layout(stretchedPx, thumbPx) { placeable.place(0, 0) }
                },
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        // COUI loading hides the thumb by scaling it about its centre.
                        scaleX = thumbScale.value
                        scaleY = thumbScale.value
                    }
                    .then(
                        if (enabled) {
                            // COUISwitch clears the shadow layer when disabled.
                            Modifier.dropShadow(shape = CircleShape, shadow = SwitchDefaults.ThumbShadow)
                        } else {
                            Modifier
                        },
                    )
                    .drawBehind {
                        // A circle at rest, a capsule while stretched (COUISwitch.drawOuterCircle).
                        drawRoundRect(
                            color = thumbColor,
                            cornerRadius = CornerRadius(size.height / 2f),
                        )
                    },
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .drawBehind {
                        // COUISwitch.drawLoading(): arc geometry follows switch_loading.png
                        // (stroke 8/40, centre-line diameter 32/40, ~312 deg alpha-tail sweep).
                        val alpha = loadingAlpha.value
                        if (alpha > 0f) {
                            val side = size.minDimension
                            val strokeWidth = side * 0.2f
                            val arcDiameter = side * 0.8f
                            val topLeft = Offset((size.width - arcDiameter) / 2f, (size.height - arcDiameter) / 2f)
                            val color = thumbColor
                            val brush = Brush.sweepGradient(
                                0f to color.copy(alpha = 0f),
                                312f / 360f to color.copy(alpha = color.alpha * alpha),
                                center = Offset(size.width / 2f, size.height / 2f),
                            )
                            scale(loadingScale.value) {
                                // +150 deg phase puts the head at ~102 deg like the source PNG.
                                rotate(loadingRotation.value + 150f) {
                                    drawArc(
                                        brush = brush,
                                        startAngle = 0f,
                                        sweepAngle = 312f,
                                        useCenter = false,
                                        topLeft = topLeft,
                                        size = Size(arcDiameter, arcDiameter),
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                                    )
                                }
                            }
                        }
                    },
            )
        }
    }
}

/**
 * The thumb motion of a [Switch]: `COUISwitch.mCircleTranslation` expressed as the checked fraction
 * (0 = off end, 1 = on end, independent of the layout direction), plus the gesture flags.
 */
@Stable
private class SwitchMotionState(initialFraction: Float) {
    val fraction = Animatable(initialFraction, visibilityThreshold = SwitchMotion.FractionVisibilityThreshold)
    val pressProgress = Animatable(0f, visibilityThreshold = 0.001f)
    val hoverProgress = Animatable(0f, visibilityThreshold = 0.001f)

    /** The thumb follows a drag or springs to an end after one: no stretch (`onSizeChange`). */
    var isDragging by mutableStateOf(false)

    /** A pointer is down on the switch (the press layer stays up for the whole drag). */
    var isPressed by mutableStateOf(false)

    /** `mIsAttachAnimRunning`: the post-drag spring runs and new touches are ignored. */
    var isAttaching = false

    /** The toggle spring runs; `COUISwitch.canDrag()` refuses a drag meanwhile. */
    var isToggling = false
    var toggleToken = 0

    /** Bumped when a drag settles, to re-sync the thumb with the owner's state. */
    var settleCount by mutableIntStateOf(0)
}

/**
 * Tracks a drag on the switch: once the pointer leaves the touch slop the thumb follows it,
 * mapped proportionally (`COUISwitch.convertToTranslationCoordinates`: x / width * travel), and the
 * gesture is consumed so the tap is cancelled. [onRelease] receives the release velocity in checked
 * fractions per second.
 */
private suspend fun PointerInputScope.detectSwitchDrag(
    motion: SwitchMotionState,
    isRtl: Boolean,
    onDrag: (target: Float) -> Unit,
    onRelease: (velocity: Float) -> Unit,
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        if (motion.isAttaching) {
            // COUISwitch.handleActionDown(): the whole gesture is ignored while the attach spring runs.
            down.consume()
            while (true) {
                val event = awaitPointerEvent()
                event.changes.forEach { it.consume() }
                if (event.changes.none { it.pressed }) break
            }
            return@awaitEachGesture
        }
        motion.isPressed = true
        val canDrag = !motion.isToggling
        val startFraction = motion.fraction.value
        val direction = if (isRtl) -1f else 1f
        val width = size.width.toFloat().coerceAtLeast(1f)
        val velocityTracker = VelocityTracker()
        velocityTracker.addPointerInputChange(down)
        var dragging = false
        try {
            while (true) {
                val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
                velocityTracker.addPointerInputChange(change)
                if (change.changedToUpIgnoreConsumed()) {
                    if (dragging) change.consume()
                    break
                }
                if (!dragging && canDrag && (change.position - down.position).getDistance() > viewConfiguration.touchSlop) {
                    dragging = true
                    motion.isDragging = true
                }
                if (dragging) {
                    change.consume()
                    val target = startFraction + direction * (change.position.x - down.position.x) / width
                    onDrag(target.coerceIn(0f, 1f))
                }
            }
        } finally {
            motion.isPressed = false
            // COUISwitch attaches on both ACTION_UP and ACTION_CANCEL after a drag.
            if (dragging) onRelease(direction * velocityTracker.calculateVelocity().x / width)
        }
    }
}

/** COUI 17 switch motion constants (`COUISwitchParams` defaults, `COUISwitch`). */
internal object SwitchMotion {
    /** `minimumVisibleChange` 0.1 px over the 70 px reference travel. */
    val FractionVisibilityThreshold: Float = 0.0015f

    /** `toggleSpring`: response 0.4, bounce 0.3. */
    val ToggleSpring: SpringSpec<Float> = TouchMotion.spring(0.4f, 0.3f, FractionVisibilityThreshold)

    /** `responseDrag` 0.3 / `bounceDrag` 0: the thumb chases the finger. */
    val DragSpring: SpringSpec<Float> = TouchMotion.spring(0.3f, 0f, FractionVisibilityThreshold)

    /** `responseAttach` 0.4 / `bounceAttach` 0.2: the thumb settles on an end after a drag. */
    val AttachSpring: SpringSpec<Float> = TouchMotion.spring(0.4f, 0.2f, FractionVisibilityThreshold)

    /** `StateEffectAnimator` press / hover: response 0.3, bounce 0. */
    val StateEffectSpring: SpringSpec<Float> = TouchMotion.spring(0.3f, 0f, 0.001f)

    /**
     * `transformVelocity` 1000 px/s of thumb travel at the 3.5x reference density: a faster release
     * flings the thumb to the end it moves towards, and the attach spring starts at most this fast.
     */
    val TransformVelocity: Dp = (1000f / 3.5f).dp

    /** Where the stretch peaks: 133 / 383, the ColorOS 16 timing turned into a distance. */
    private val StretchPeak: Float = 0.34725848f

    /** The peak thumb stretch. */
    private val StretchAmount: Float = 0.29999995f

    /**
     * `COUISwitch` circleTranslation setter: the thumb width factor at checked [fraction]. It
     * grows to 1.3 at 34.7 % of the travel and returns to 1 at the end; spring overshoot past an
     * end squashes it slightly below 1.
     */
    fun thumbStretch(fraction: Float): Float = if (fraction < StretchPeak) {
        1f + fraction / StretchPeak * StretchAmount
    } else {
        1f + StretchAmount - (fraction - StretchPeak) / (1f - StretchPeak) * StretchAmount
    }

    /**
     * `ColorUtils.blendARGB`: a per-channel blend of the non-premultiplied sRGB components (Compose's
     * `lerp` would blend in Oklab).
     */
    fun blendArgb(from: Color, to: Color, fraction: Float): Color {
        val inverse = 1f - fraction
        return Color(
            red = from.red * inverse + to.red * fraction,
            green = from.green * inverse + to.green * fraction,
            blue = from.blue * inverse + to.blue * fraction,
            alpha = from.alpha * inverse + to.alpha * fraction,
        )
    }

    /**
     * `COUISpringDragHelper.getAttachPosition()` over the stable points {0, 1}: a release faster
     * than [velocityLimit] goes to the end it moves towards, otherwise to the nearer end.
     */
    fun attachTarget(fraction: Float, velocity: Float, velocityLimit: Float): Float = when {
        velocityLimit > 0f && abs(velocity) >= velocityLimit -> if (velocity > 0f) 1f else 0f
        fraction > 0.5f -> 1f
        else -> 0f
    }
}

object SwitchDefaults {

    /** The track width of the [Switch] (COUISwitch bar_width, 44dp in COUI 17). */
    val TrackWidth: Dp = 44.dp

    /** The track height of the [Switch] (COUISwitch bar_height). */
    val TrackHeight: Dp = 24.dp

    /** The diameter of the [Switch] thumb (COUISwitch outer_circle_width). */
    val ThumbSize: Dp = 18.dp

    /** The inset of the thumb from the track edge when fully on one side (COUISwitch circle_padding). */
    private val ThumbMargin: Dp = 3.dp

    /** The thumb offset in the unchecked (off) position. */
    val ThumbStartOffset: Dp = ThumbMargin

    /** The thumb offset in the checked (on) position. */
    val ThumbEndOffset: Dp = TrackWidth - ThumbSize - ThumbMargin

    /** The distance the thumb travels between off and on. */
    val Travel: Dp = ThumbEndOffset - ThumbStartOffset

    /**
     * The drop shadow under the thumb while the [Switch] is enabled
     * (COUISwitch.setPaintShadowLayer at the 3.5x ColorOS reference density).
     */
    val ThumbShadow: Shadow = Shadow(
        radius = 2.29.dp,
        color = Color(0x19000000),
        offset = DpOffset(x = 0.dp, y = 1.14.dp),
    )

    /**
     * The default colors for the [Switch]. COUISwitch keeps the thumb white in both states; the
     * unchecked track is couiColorControls, the disabled unchecked track couiColorPressBackground
     * and both disabled thumbs coui_switch_thumb_disable_color.
     */
    @Composable
    fun switchColors(
        checkedThumbColor: Color = if (isDynamicColor) LocalColors.current.onPrimary else COUITheme.colorScheme.onPrimary,
        uncheckedThumbColor: Color = if (isDynamicColor) LocalColors.current.onPrimary else COUITheme.colorScheme.onSecondary,
        disabledCheckedThumbColor: Color = COUITheme.colorScheme.disabledSliderThumb,
        disabledUncheckedThumbColor: Color = COUITheme.colorScheme.disabledSliderThumb,
        checkedTrackColor: Color = COUITheme.colorScheme.primary,
        uncheckedTrackColor: Color = COUITheme.colorScheme.dragHandle,
        disabledCheckedTrackColor: Color = COUITheme.colorScheme.disabledPrimary,
        disabledUncheckedTrackColor: Color = COUITheme.colorScheme.secondaryContainer,
    ): SwitchColors = remember(
        checkedThumbColor,
        uncheckedThumbColor,
        disabledCheckedThumbColor,
        disabledUncheckedThumbColor,
        checkedTrackColor,
        uncheckedTrackColor,
        disabledCheckedTrackColor,
        disabledUncheckedTrackColor,
    ) {
        SwitchColors(
            checkedThumbColor = checkedThumbColor,
            uncheckedThumbColor = uncheckedThumbColor,
            disabledCheckedThumbColor = disabledCheckedThumbColor,
            disabledUncheckedThumbColor = disabledUncheckedThumbColor,
            checkedTrackColor = checkedTrackColor,
            uncheckedTrackColor = uncheckedTrackColor,
            disabledCheckedTrackColor = disabledCheckedTrackColor,
            disabledUncheckedTrackColor = disabledUncheckedTrackColor,
        )
    }
}

@Immutable
data class SwitchColors(
    private val checkedThumbColor: Color,
    private val uncheckedThumbColor: Color,
    private val disabledCheckedThumbColor: Color,
    private val disabledUncheckedThumbColor: Color,
    private val checkedTrackColor: Color,
    private val uncheckedTrackColor: Color,
    private val disabledCheckedTrackColor: Color,
    private val disabledUncheckedTrackColor: Color,
) {
    @Stable
    internal fun checkedThumbColor(enabled: Boolean): Color = if (enabled) checkedThumbColor else disabledCheckedThumbColor

    @Stable
    internal fun uncheckedThumbColor(enabled: Boolean): Color = if (enabled) uncheckedThumbColor else disabledUncheckedThumbColor

    @Stable
    internal fun checkedTrackColor(enabled: Boolean): Color = if (enabled) checkedTrackColor else disabledCheckedTrackColor

    @Stable
    internal fun uncheckedTrackColor(enabled: Boolean): Color = if (enabled) uncheckedTrackColor else disabledUncheckedTrackColor
}
