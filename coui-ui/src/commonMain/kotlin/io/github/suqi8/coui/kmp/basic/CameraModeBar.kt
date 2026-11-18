// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.effect.MaterialBlurBlendMode
import io.github.suqi8.coui.kmp.effect.MaterialBlurProvider
import io.github.suqi8.coui.kmp.effect.MaterialBlurSpec
import io.github.suqi8.coui.kmp.utils.CouiHapticEffect
import io.github.suqi8.coui.kmp.utils.rememberCouiHaptic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * The camera mode strip, the port of the camera's headline (`ModePickerView`, laid out by
 * `DimenConstants`): a single line of mode names (Night, Portrait, Photo, Video, ...) scrolling
 * under a fixed capsule in the middle that marks the selected mode.
 *
 * The names are laid out [CameraModeBarDefaults.ItemGap] apart and centered on the bar; the
 * selected one sits in a [CameraModeBarDefaults.CapsuleHeight] capsule padded by
 * [CameraModeBarDefaults.CapsulePadding] on both sides (at most
 * [CameraModeBarDefaults.CapsuleMaxWidth] wide, longer names are ellipsized). The capsule follows
 * the strip: while it moves between two modes its width blends from one name to the other. A name
 * turns to the accent color while it is within 40dp of the middle, fully within 15dp. The strip
 * fades out over the outer [CameraModeBarDefaults.EdgeFadeWidth] at both ends.
 *
 * - **Drag**: the strip follows 67% of the finger and stretches past the first and last mode on a
 *   rubber band. The mode under the capsule flips once the middle passes the edge of the next name
 *   by 28px in the direction of travel, with a light haptic tick.
 * - **Release**: the strip springs to that mode (`COUISpringForce`, bounce 0.05, response 0.3s); a
 *   release faster than 5500px/s carries on three times as many modes as were dragged.
 * - **Tap**: tapping a name (its width plus 8dp on both sides) springs to it.
 *
 * [onSelectedIndexChange] is called once the gesture settles on a new mode, like the camera
 * switching mode on release. When [selectedIndex] changes from outside the strip springs to it.
 *
 * The capsule is the camera's material blur with its edge light when [materialBlur] is given, and
 * otherwise a 12% white fill. The bar uses its fixed light-on-dark palette, since it sits over the
 * camera preview or the black area around it.
 *
 * @param modes The mode names, from start to end. Must not be empty.
 * @param selectedIndex The index of the selected mode.
 * @param onSelectedIndexChange Called with the new mode when a drag, fling or tap settles on it.
 * @param modifier The modifier to be applied to the bar. The bar fills the width it is given.
 * @param enabled Whether the strip accepts drags and taps.
 * @param materialBlur Paints the camera material blur behind the capsule. Pass a provider recording
 *   the content under the bar (for example the camera preview); null keeps the plain fill. The
 *   recorded content must not contain the bar itself.
 * @param colors The colors of the bar, see [CameraModeBarDefaults.cameraModeBarColors].
 */
@Composable
fun CameraModeBar(
    modes: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    materialBlur: MaterialBlurProvider? = null,
    colors: CameraModeBarColors = CameraModeBarDefaults.cameraModeBarColors(),
) {
    require(modes.isNotEmpty()) { "CameraModeBar needs at least one mode" }
    val count = modes.size
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val haptic = rememberCouiHaptic()
    val scope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer()

    val textStyle = remember(density) {
        TextStyle(
            fontSize = cameraTextSize(CameraModeBarDefaults.TextSize, density.fontScale),
            fontWeight = FontWeight.Medium,
        )
    }
    val maxTextWidth = with(density) {
        (CameraModeBarDefaults.CapsuleMaxWidth - CameraModeBarDefaults.CapsulePadding * 2).roundToPx()
    }
    // ModePickerView.T: names longer than the capsule allows are ellipsized.
    val layouts = remember(modes, textStyle, maxTextWidth, textMeasurer) {
        modes.map {
            textMeasurer.measure(
                text = it,
                style = textStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                constraints = Constraints(maxWidth = maxTextWidth.coerceAtLeast(0)),
            )
        }
    }
    val gapPx = with(density) { CameraModeBarDefaults.ItemGap.toPx() }
    val geometry = remember(layouts, gapPx) {
        ModeBarGeometry.of(layouts.map { it.size.width.toFloat() }, gapPx)
    }

    val state = remember { CameraModeBarState() }
    val currentOnChange by rememberUpdatedState(onSelectedIndexChange)
    val currentHaptic by rememberUpdatedState(haptic)
    val currentSelected by rememberUpdatedState(selectedIndex.coerceIn(0, count - 1))
    state.onCommit = { index -> if (index != currentSelected) currentOnChange(index) }
    state.onTargetDragged = { currentHaptic(CouiHapticEffect.Strength) }

    if (state.geometry !== geometry) {
        // New names or text size: lay the strip out again on the selected mode.
        state.geometry = geometry
        state.jumpTo(selectedIndex.coerceIn(0, count - 1))
    }
    LaunchedEffect(selectedIndex) {
        val target = selectedIndex.coerceIn(0, count - 1)
        if (target != state.target && !state.dragging) state.slideTo(scope, target)
    }

    val capsuleShape = CircleShape
    val blurSpec = remember(density, colors) {
        MaterialBlurSpec(
            blurRadiusPx = with(density) { CameraModeBarDefaults.BlurRadius.toPx() },
            mixColor = colors.blurMixColor,
            blendColor = colors.blurBlendColor,
            blendMode = MaterialBlurBlendMode.GlowOverlay,
        )
    }
    val blurModifier = materialBlur?.materialBlur(blurSpec, capsuleShape)
    val capsuleBackground = if (blurModifier != null) {
        blurModifier.cameraInnerShadow(CameraInnerShadows.Panel, CameraModeBarDefaults.CapsuleHeight / 2)
    } else {
        Modifier.background(colors.capsuleColor, capsuleShape)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(CameraModeBarDefaults.Height)
            .selectableGroup()
            .semantics { stateDescription = modes[state.target.coerceIn(0, count - 1)] }
            .pointerInput(enabled, geometry, layoutDirection) {
                if (!enabled) return@pointerInput
                val sign = if (layoutDirection == LayoutDirection.Rtl) -1f else 1f
                val touchExtension = CameraModeBarDefaults.TouchExtension.toPx()
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val tracker = VelocityTracker()
                    tracker.addPosition(down.uptimeMillis, down.position)
                    var startCenter = state.center
                    var dragging = false
                    var moved = false
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (change.changedToUp()) {
                            if (dragging) {
                                change.consume()
                                tracker.addPosition(change.uptimeMillis, change.position)
                                state.endDrag(scope, tracker.calculateVelocity().x)
                            } else if (!moved && !change.isConsumed &&
                                change.uptimeMillis - down.uptimeMillis < viewConfiguration.longPressTimeoutMillis
                            ) {
                                // GestureDetector.onSingleTapUp, ignored while the strip slides.
                                change.consume()
                                val position = state.center + sign * (change.position.x - size.width / 2f)
                                val index = geometry.hitTest(position, touchExtension)
                                if (index >= 0 && !state.animating) state.tapTo(scope, index)
                            }
                            break
                        }
                        if (change.isConsumed) {
                            if (dragging) state.endDrag(scope, 0f)
                            break
                        }
                        tracker.addPosition(change.uptimeMillis, change.position)
                        val dx = change.position.x - down.position.x
                        val dy = change.position.y - down.position.y
                        if (abs(dx) > viewConfiguration.touchSlop || abs(dy) > viewConfiguration.touchSlop) moved = true
                        if (!dragging) {
                            // An upward swipe is left to the parent (the camera hands it to the
                            // bottom menu).
                            if (dy < 0f && abs(dy) > MODE_BAR_VERTICAL_SWIPE_PX &&
                                abs(dx) < MODE_BAR_VERTICAL_SWIPE_MAX_DX_PX && abs(dy) >= abs(dx)
                            ) {
                                break
                            }
                            if (abs(dx * MODE_BAR_DRAG_RATIO) < MODE_BAR_MIN_DRAG_PX) continue
                            dragging = true
                            state.beginDrag(currentSelected)
                            startCenter = state.center
                        }
                        change.consume()
                        state.drag(
                            rawCenter = startCenter - sign * dx * MODE_BAR_DRAG_RATIO,
                            velocity = sign * tracker.calculateVelocity().x,
                            rubberBandLimit = size.width / 4f,
                        )
                    }
                }
            },
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .layout { measurable, _ ->
                    val padding = CameraModeBarDefaults.CapsulePadding.toPx()
                    val width = (geometry.textWidthAt(state.center) + padding * 2f).roundToInt().coerceAtLeast(0)
                    val height = CameraModeBarDefaults.CapsuleHeight.roundToPx()
                    val placeable = measurable.measure(Constraints.fixed(width, height))
                    layout(width, height) { placeable.place(0, 0) }
                }
                .then(capsuleBackground),
        )
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
        ) {
            val w = size.width
            val half = w / 2f
            val rtl = layoutDirection == LayoutDirection.Rtl
            val center = state.center
            val strokeWidth = CameraModeBarDefaults.TextStrokeWidth.toPx()
            val start = CameraModeBarDefaults.HighlightStartDistance.toPx()
            val end = CameraModeBarDefaults.HighlightEndDistance.toPx()
            val target = state.target
            val previous = state.previous
            for (i in 0 until count) {
                val layout = layouts[i]
                val offset = geometry.centers[i] - center
                val x = if (rtl) half - offset else half + offset
                val textWidth = layout.size.width
                if (x + textWidth / 2f < 0f || x - textWidth / 2f > w) continue
                val topLeft = Offset(x - textWidth / 2f, (size.height - layout.size.height) / 2f)
                // ModePickerView.I/H: the selected and the previous mode take the accent as they
                // near the middle, over the plain name and a thin dark outline.
                val accent = if (i == target || i == previous) highlightFraction(abs(offset), start, end) else 0f
                drawText(layout, color = colors.textStrokeColor, topLeft = topLeft, drawStyle = Stroke(width = strokeWidth))
                if (accent < 1f) drawText(layout, color = colors.textColor, topLeft = topLeft)
                if (accent > 0f) drawText(layout, color = colors.selectedTextColor, topLeft = topLeft, alpha = accent)
            }
            // ModePickerView.E0: DST_OUT gradients erase the strip towards both ends.
            val fade = CameraModeBarDefaults.EdgeFadeWidth.toPx().coerceAtMost(half)
            if (fade > 0f) {
                drawRect(
                    brush = Brush.horizontalGradient(0f to Color.Black, 1f to Color.Transparent, startX = 0f, endX = fade),
                    size = Size(fade, size.height),
                    blendMode = BlendMode.DstOut,
                )
                drawRect(
                    brush = Brush.horizontalGradient(0f to Color.Transparent, 1f to Color.Black, startX = w - fade, endX = w),
                    topLeft = Offset(w - fade, 0f),
                    size = Size(fade, size.height),
                    blendMode = BlendMode.DstOut,
                )
            }
        }
    }
}

/**
 * The camera's text sizing (`b4.a.c(size, fontScale, 4)`): the size follows the font scale up to
 * [CameraModeBarDefaults.MaxFontScale] and stops there.
 */
internal fun cameraTextSize(size: Dp, fontScale: Float): TextUnit = (size.value * min(fontScale, CameraModeBarDefaults.MaxFontScale) / fontScale).sp

/** `COUIEaseInterpolator` (0, 0, 0.1, 1), the accent fade of the names (`ModePickerView.P1`). */
private val ModeBarHighlightEasing = CubicBezierEasing(0f, 0f, 0.1f, 1f)

/**
 * The slide spring of `HeadLineSlideAnimatorHelper`: `COUISpringForce` bounce 0.05 (damping ratio
 * 0.95) and response 0.3s (stiffness (2 pi / 0.3)^2).
 */
private val ModeBarSlideSpring = spring<Float>(dampingRatio = 0.95f, stiffness = 438.65f)

/** The share of the finger travel the strip follows (`ModePickerView.onTouchEvent`). */
private const val MODE_BAR_DRAG_RATIO = 0.67f

/** The followed travel (px) below which a move is ignored. */
private const val MODE_BAR_MIN_DRAG_PX = 10f

/** How far (px) the middle must pass the edge of the next name before it is selected. */
private const val MODE_BAR_SWITCH_HYSTERESIS_PX = 28f

/** The finger speed (px/s) above which the drag direction counts for the switch. */
private const val MODE_BAR_DIRECTION_VELOCITY = 3f

/** The release speed (px/s) above which a fling carries on past the dragged mode. */
private const val MODE_BAR_FAST_FLING_VELOCITY = 5500f

/** How many times the dragged distance (in modes) a fast fling adds. */
private const val MODE_BAR_FLING_MULTIPLIER = 3

/** An upward move (px) taller than this and narrower than [MODE_BAR_VERTICAL_SWIPE_MAX_DX_PX]. */
private const val MODE_BAR_VERTICAL_SWIPE_PX = 30f

/** The widest horizontal travel (px) an upward swipe may have. */
private const val MODE_BAR_VERTICAL_SWIPE_MAX_DX_PX = 150f

/**
 * `ModePickerView.I`: no accent from [start] away from the middle, the full accent within [end],
 * eased in between.
 */
private fun highlightFraction(distance: Float, start: Float, end: Float): Float {
    if (distance >= start) return 0f
    val t = (start - distance) / (start - end)
    return if (t >= 1f) 1f else ModeBarHighlightEasing.transform(t)
}

/** The measured names: their widths and centers along the strip (`ModePickerView.f0`, `R1`). */
@Immutable
private class ModeBarGeometry(val widths: FloatArray, val centers: FloatArray) {
    val last get() = centers.size - 1

    /** The name width the capsule wraps when the middle of the bar is at [center]. */
    fun textWidthAt(center: Float): Float {
        if (center <= centers[0]) return widths[0]
        if (center >= centers[last]) return widths[last]
        for (i in 0 until last) {
            if (center < centers[i + 1]) {
                val fraction = (center - centers[i]) / (centers[i + 1] - centers[i])
                return widths[i] + (widths[i + 1] - widths[i]) * fraction
            }
        }
        return widths[last]
    }

    /** `ModePickerView.x`: the name hit at [position], each widened by [extension] on both sides. */
    fun hitTest(position: Float, extension: Float): Int {
        for (i in centers.indices) {
            val half = widths[i] / 2f
            if (position >= centers[i] - half - extension && position < centers[i] + half + extension) return i
        }
        return -1
    }

    /** `ModePickerView.onTouchEvent` ACTION_MOVE: the mode under the capsule while dragging. */
    fun dragTarget(position: Float, velocity: Float, current: Int): Int {
        if (position < centers[0]) return 0
        if (position > centers[last]) return last
        for (i in 0 until last) {
            if (position >= centers[i] && position < centers[i + 1]) {
                val forward = centers[i] + widths[i] / 2f + MODE_BAR_SWITCH_HYSTERESIS_PX
                val backward = centers[i + 1] - widths[i + 1] / 2f - MODE_BAR_SWITCH_HYSTERESIS_PX
                return when {
                    velocity < -MODE_BAR_DIRECTION_VELOCITY && position > forward -> i + 1
                    velocity > MODE_BAR_DIRECTION_VELOCITY && position < backward -> i
                    else -> current
                }
            }
        }
        return current
    }

    /** The rubber band past either end: the overshoot `o` shows as `(1 - 1 / (o / limit + 1)) * limit`. */
    fun rubberBand(position: Float, limit: Float): Float {
        if (limit <= 0f) return position.coerceIn(centers[0], centers[last])
        return when {
            position < centers[0] -> centers[0] - (1f - 1f / ((centers[0] - position) / limit + 1f)) * limit
            position > centers[last] -> centers[last] + (1f - 1f / ((position - centers[last]) / limit + 1f)) * limit
            else -> position
        }
    }

    companion object {
        fun of(widths: List<Float>, gap: Float): ModeBarGeometry {
            val w = widths.toFloatArray()
            val c = FloatArray(w.size)
            var x = 0f
            for (i in w.indices) {
                c[i] = x + w[i] / 2f
                x += w[i] + gap
            }
            return ModeBarGeometry(w, c)
        }
    }
}

/**
 * The scroll model of [CameraModeBar]. [center] is the position along the strip shown in the
 * middle of the bar (`ModePickerView.p2`), [target] the mode under the capsule (`H1`/`J1`) and
 * [previous] the one it left (`F2`), which fades out of the accent. Every animation runs in [job].
 */
@Stable
private class CameraModeBarState {
    var center by mutableFloatStateOf(0f)
    var target by mutableIntStateOf(0)
    var previous by mutableIntStateOf(0)
    var geometry: ModeBarGeometry? = null
    var dragging = false
    var onCommit: (Int) -> Unit = {}
    var onTargetDragged: () -> Unit = {}

    private var job: Job? = null
    private var dragStartIndex = 0

    val animating: Boolean get() = job?.isActive == true

    fun jumpTo(index: Int) {
        val g = geometry ?: return
        cancel()
        center = g.centers[index.coerceIn(0, g.last)]
        target = index
        previous = index
    }

    private fun cancel() {
        job?.cancel()
        job = null
    }

    private fun retarget(index: Int) {
        if (index != target) {
            previous = target
            target = index
        }
    }

    /** Springs to [index] after an outside change. */
    fun slideTo(scope: CoroutineScope, index: Int) {
        val g = geometry ?: return
        retarget(index)
        animateTo(scope, g.centers[index])
    }

    fun tapTo(scope: CoroutineScope, index: Int) {
        slideTo(scope, index)
        onCommit(index)
    }

    fun beginDrag(selected: Int) {
        cancel()
        dragging = true
        dragStartIndex = selected
    }

    fun drag(rawCenter: Float, velocity: Float, rubberBandLimit: Float) {
        val g = geometry ?: return
        center = g.rubberBand(rawCenter, rubberBandLimit)
        val index = g.dragTarget(center, velocity, target)
        if (index != target) {
            retarget(index)
            onTargetDragged()
        }
    }

    /** ACTION_UP: springs to the dragged mode, or further on a fast fling, and commits it. */
    fun endDrag(scope: CoroutineScope, velocity: Float) {
        val g = geometry ?: return
        dragging = false
        var index = target
        if (abs(velocity) > MODE_BAR_FAST_FLING_VELOCITY) {
            index = (index + (index - dragStartIndex) * MODE_BAR_FLING_MULTIPLIER).coerceIn(0, g.last)
        }
        retarget(index)
        animateTo(scope, g.centers[index])
        onCommit(index)
    }

    private fun animateTo(scope: CoroutineScope, to: Float) {
        cancel()
        val from = center
        job = scope.launch {
            animate(initialValue = from, targetValue = to, animationSpec = ModeBarSlideSpring) { value, _ ->
                center = value
            }
        }
    }
}

/**
 * Default values of [CameraModeBar], from the camera's `head_line_*` and `headline_*` dimensions.
 */
object CameraModeBarDefaults {
    /** The bar height (`head_line_height`). */
    val Height = 40.dp

    /** The capsule height (`head_line_selected_bg_height`, `headline_recycle_view_item_height`). */
    val CapsuleHeight = 34.dp

    /** The capsule padding at both sides of the name (`head_line_selected_bg_padding_left_right`). */
    val CapsulePadding = 16.dp

    /** The widest capsule; longer names are ellipsized (`head_line_selected_bg_max_width`). */
    val CapsuleMaxWidth = 198.dp

    /** The gap between two names (`headline_item_gap`, `ModePickerView.getHeadlineTextGap`). */
    val ItemGap = 32.dp

    /** The name size (`head_line_scale_size` 13sp), following the font scale up to [MaxFontScale]. */
    val TextSize = 13.dp

    /** The largest font scale the names follow (`b4.a.c` level 4). */
    val MaxFontScale = 1.35f

    /** The width of the dark outline around the names (`mode_title_text_stroke_size`). */
    val TextStrokeWidth = 0.33.dp

    /** The width over which the strip fades out at both ends (`mode_headline_edge_effect_with`). */
    val EdgeFadeWidth = 72.dp

    /** The distance from the middle at which a name starts turning to the accent. */
    val HighlightStartDistance = 40.dp

    /** The distance from the middle within which a name is fully in the accent. */
    val HighlightEndDistance = 15.dp

    /** The extra touch width at both sides of a name (`headline_item_extra_touch_offset`). */
    val TouchExtension = 8.dp

    /** The material blur radius of the capsule (`setting_blur_radius`). */
    val BlurRadius = 80.dp

    /**
     * The fixed camera palette: white names (`ModePickerView.n1`) taking the camera accent
     * (`couiColorPrimary`, `color_primary_light_hasselblad` #FB822A) under the capsule, outlined by
     * `text_view_stroke_color` (40% black); the capsule is the `BlendHelper` fill (white at 12%) or
     * the material blur colors of `BlurBackgroundImpl` (dark, progress 0).
     */
    @Composable
    fun cameraModeBarColors(
        textColor: Color = Color.White,
        selectedTextColor: Color = Color(0xFFFB822A),
        textStrokeColor: Color = Color(0x66000000),
        capsuleColor: Color = Color(0x1EFFFFFF),
        blurMixColor: Color = Color(red = 0.302f, green = 0.302f, blue = 0.302f, alpha = 0.6f),
        blurBlendColor: Color = Color(red = 0.15f, green = 0.15f, blue = 0.15f, alpha = 0.15f),
    ): CameraModeBarColors = remember(textColor, selectedTextColor, textStrokeColor, capsuleColor, blurMixColor, blurBlendColor) {
        CameraModeBarColors(
            textColor = textColor,
            selectedTextColor = selectedTextColor,
            textStrokeColor = textStrokeColor,
            capsuleColor = capsuleColor,
            blurMixColor = blurMixColor,
            blurBlendColor = blurBlendColor,
        )
    }
}

/**
 * The colors of a [CameraModeBar].
 *
 * @param textColor The mode names.
 * @param selectedTextColor The name under the capsule.
 * @param textStrokeColor The thin outline around every name.
 * @param capsuleColor The capsule fill without material blur.
 * @param blurMixColor The material blur mix color.
 * @param blurBlendColor The material blur glow-overlay color.
 */
@Immutable
data class CameraModeBarColors(
    val textColor: Color,
    val selectedTextColor: Color,
    val textStrokeColor: Color,
    val capsuleColor: Color,
    val blurMixColor: Color,
    val blurBlendColor: Color,
)
