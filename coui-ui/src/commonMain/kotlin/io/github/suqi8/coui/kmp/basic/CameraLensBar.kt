// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.utils.CouiHapticEffect
import io.github.suqi8.coui.kmp.utils.rememberCouiHaptic
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.sqrt

/**
 * The lens switcher of the camera, the port of the camera's `SwitchCameraBar` in its divided style
 * (`dividedSwitchBarStyle="true"`, the movie mode lens bar of `switch_camera_layout.xml`): one dark
 * disc per lens, the zoom ratio of each lens written in it.
 *
 * Unselected lenses show the bare ratio ("0.6", "3") in white; the selected lens grows from
 * [radius] to [selectedRadius] and shows the ratio in bold accent color followed by the camera's
 * small "×" glyph ("1×"). Tapping another lens starts the switch at once, without waiting for
 * [selectedIndex] to change: the new disc grows and the old one shrinks in 400ms on
 * (0.3, 0, 0.1, 1) while the ratio slides aside to make room for the "×", and the colors cross
 * over in 400ms on (0.33, 0, 0.67, 1) after a 100ms delay. Taps are ignored while a switch is
 * running. Pressing the selected disc shrinks it to 85% and darkens it (500ms, a critically damped
 * spring); releasing springs it back in 280ms.
 *
 * The bar is as wide as its discs ([CameraLensBarDefaults.width]) and
 * [CameraLensBarDefaults.Height] high, and always uses a fixed light-on-dark palette, since it sits
 * over the camera preview.
 *
 * @param labels The zoom ratios of the lenses, from the widest to the longest, already formatted
 *   (see [CameraLensBarDefaults.formatZoomRatio]). Must not be empty.
 * @param selectedIndex The index of the selected lens.
 * @param onSelectedIndexChange Called with the index of the tapped lens.
 * @param modifier The modifier to be applied to the bar.
 * @param enabled Whether the lenses accept taps.
 * @param radius The radius of an unselected disc.
 * @param selectedRadius The radius of the selected disc.
 * @param itemSpacing The gap between two unselected discs.
 * @param textSize The size of the ratios, in dp like the camera's dimension (not scaled with the
 *   font scale).
 * @param colors The colors of the bar, see [CameraLensBarDefaults.cameraLensBarColors].
 */
@Composable
fun CameraLensBar(
    labels: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    radius: Dp = CameraLensBarDefaults.Radius,
    selectedRadius: Dp = CameraLensBarDefaults.SelectedRadius,
    itemSpacing: Dp = CameraLensBarDefaults.ItemSpacing,
    textSize: Dp = CameraLensBarDefaults.TextSize,
    colors: CameraLensBarColors = CameraLensBarDefaults.cameraLensBarColors(),
) {
    require(labels.isNotEmpty()) { "CameraLensBar needs at least one lens" }
    val count = labels.size
    val density = LocalDensity.current
    val haptic = rememberCouiHaptic()
    val scope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer()
    val currentOnChange by rememberUpdatedState(onSelectedIndexChange)

    // SwitchCameraBar.M0 (the selected disc) and N0 (the one it leaves), and the two animations of
    // a switch: `D0`'s scale and text offset (R, P, Q) and `i1`'s color crossfade (C1).
    var shownIndex by remember { mutableIntStateOf(selectedIndex.coerceIn(0, count - 1)) }
    var previousIndex by remember { mutableIntStateOf(shownIndex) }
    val switchProgress = remember { Animatable(1f) }
    val colorProgress = remember { Animatable(1f) }
    // The press feedback on the selected disc: `I1` (scale) and `J1` (fill alpha, 0..1 here).
    val pressScale = remember { Animatable(1f) }
    val pressFill = remember { Animatable(0f) }

    fun switchTo(index: Int) {
        previousIndex = shownIndex
        shownIndex = index
        scope.launch {
            switchProgress.snapTo(0f)
            switchProgress.animateTo(1f, tween(durationMillis = 400, easing = LensMoveEaseEasing))
        }
        scope.launch {
            colorProgress.snapTo(0f)
            colorProgress.animateTo(1f, tween(durationMillis = 400, delayMillis = 100, easing = LensStandardEasing))
        }
    }

    LaunchedEffect(selectedIndex, count) {
        val target = selectedIndex.coerceIn(0, count - 1)
        if (target != shownIndex) switchTo(target)
    }

    val regularStyle = remember(textSize, density) {
        TextStyle(fontSize = (textSize.value / density.fontScale).sp, fontWeight = FontWeight(LENS_TEXT_WEIGHT))
    }
    val selectedStyle = remember(regularStyle) { regularStyle.copy(fontWeight = FontWeight.Bold) }
    val regularLayouts = remember(labels, regularStyle) { labels.map { textMeasurer.measure(it, regularStyle) } }
    val selectedLayouts = remember(labels, selectedStyle) { labels.map { textMeasurer.measure(it, selectedStyle) } }
    val suffixPath = remember { PathParser().parsePathString(LENS_SUFFIX_PATH).toPath() }

    val scaleUp = selectedRadius / radius
    val barWidth = CameraLensBarDefaults.width(count, radius, selectedRadius, itemSpacing)

    Canvas(
        modifier = modifier
            .width(barWidth)
            .height(CameraLensBarDefaults.Height)
            .semantics {
                role = Role.Button
                stateDescription = labels[shownIndex.coerceIn(0, count - 1)] + "×"
            }
            .pointerInput(enabled, count, radius, itemSpacing) {
                if (!enabled) return@pointerInput
                detectTapGestures(
                    onPress = { offset ->
                        // SwitchCameraBar.onTouchEvent: only a press on the selected disc shrinks it.
                        val r = radius.toPx()
                        val center = lensCenter(shownIndex, count, size.width.toFloat(), r, itemSpacing.toPx())
                        val cy = size.height / 2f
                        val hit = offset.x in (center - r)..(center + r) && offset.y in (cy - r)..(cy + r)
                        if (hit) {
                            scope.launch { pressScale.animateTo(PRESSED_SCALE, tween(500, easing = PressInEasing)) }
                            scope.launch { pressFill.animateTo(1f, tween(500, easing = PressFillInEasing)) }
                        }
                        tryAwaitRelease()
                        if (pressScale.value != 1f || pressFill.value != 0f) {
                            scope.launch { pressScale.animateTo(1f, tween(280, easing = PressOutEasing)) }
                            scope.launch { pressFill.animateTo(0f, tween(280, easing = PressFillOutEasing)) }
                        }
                    },
                    onTap = { offset ->
                        // x2.onSingleTapUp: the lens under the finger, in equal slices of the width.
                        val index = floor(offset.x / (size.width / count.toFloat())).toInt().coerceIn(0, count - 1)
                        if (!switchProgress.isRunning && index != shownIndex) {
                            haptic(CouiHapticEffect.Switch)
                            switchTo(index)
                            currentOnChange(index)
                        }
                    },
                )
            },
    ) {
        val r = radius.toPx()
        val spacing = itemSpacing.toPx()
        val cy = size.height / 2f
        val inkHeight = regularStyle.fontSize.toPx() * LENS_DIGIT_INK_RATIO
        val baseline = cy + inkHeight / 2f
        val glyphWidth = CameraLensBarDefaults.SuffixWidth.toPx()
        val glyphHeight = CameraLensBarDefaults.SuffixHeight.toPx()
        val glyphGap = CameraLensBarDefaults.SuffixSpacing.toPx()
        val glyphOffsetY = CameraLensBarDefaults.SuffixOffsetY.toPx()
        val switching = switchProgress.value
        val crossfade = colorProgress.value
        val selectedFill = lerp(colors.selectedContainerColor, colors.pressedSelectedContainerColor, pressFill.value)

        for (i in 0 until count) {
            val cx = lensCenter(i, count, size.width, r, spacing)
            // How "selected" this lens is: 1 for the shown lens, 0 for the others, in between for
            // the two lenses of a running switch.
            val (scaleFraction, colorFraction) = when (i) {
                shownIndex -> switching to crossfade
                previousIndex -> (1f - switching) to (1f - crossfade)
                else -> 0f to 0f
            }
            val isTarget = i == shownIndex
            val discScale = 1f + (scaleUp - 1f) * scaleFraction
            val fill = if (isTarget) selectedFill else colors.containerColor
            val press = if (isTarget) pressScale.value else 1f
            drawCircle(color = fill, radius = r * discScale * press, center = Offset(cx, cy))

            val layout = if (isTarget) selectedLayouts[i] else regularLayouts[i]
            val textColor = lerp(colors.contentColor, colors.selectedContentColor, colorFraction)
            // The ratio slides left by half the "×" (P and Q of getZoomUnitTextOffsetAnimator) so
            // the ratio and the glyph end up centered together.
            val groupShift = (glyphWidth + glyphGap) / 2f * scaleFraction
            val textLeft = cx - layout.size.width / 2f - groupShift
            scale(press, pivot = Offset(cx, cy)) {
                drawText(
                    textLayoutResult = layout,
                    color = textColor,
                    topLeft = Offset(textLeft, baseline - layout.firstBaseline),
                )
                if (colorFraction > 0f) {
                    // The glyph is centered on the text box center, 0.5dp lower (SwitchCameraBar.H0).
                    val boxCenter = baseline - layout.firstBaseline + layout.size.height / 2f
                    val glyphTop = boxCenter + glyphOffsetY - glyphHeight / 2f
                    translate(left = textLeft + layout.size.width + glyphGap, top = glyphTop) {
                        scale(
                            scaleX = glyphWidth / LENS_SUFFIX_VIEWPORT_WIDTH,
                            scaleY = glyphHeight / LENS_SUFFIX_VIEWPORT_HEIGHT,
                            pivot = Offset.Zero,
                        ) {
                            drawPath(path = suffixPath, color = colors.suffixColor.copy(alpha = colors.suffixColor.alpha * colorFraction))
                        }
                    }
                }
            }
        }
    }
}

/**
 * `SwitchCameraBar.g` in the divided style: the lenses are centered in the bar, [radius] * 2 +
 * [spacing] apart.
 */
private fun lensCenter(index: Int, count: Int, width: Float, radius: Float, spacing: Float): Float = width / 2f + (index - (count - 1) / 2f) * (spacing + radius * 2f)

/** The weight of the camera's lens font (`a4.q()`, SysSans-En at `wght` 533). */
private const val LENS_TEXT_WEIGHT = 533

/** The ink height of tabular digits relative to the font size, used to center the ratios. */
private const val LENS_DIGIT_INK_RATIO = 0.7f

/** The press scale of the selected disc (`SwitchCameraBar.r1`, 1 to 0.85). */
private const val PRESSED_SCALE = 0.85f

/** The "×" glyph of `multiple_zoom_hasselblad` / `multiple_zoom_yellow` (6 x 14 viewport). */
private const val LENS_SUFFIX_PATH =
    "M1.0764,4.6733L0.1735,5.5761L2.0967,7.4992L0.1729,9.4231L1.0776,10.3278L3.0014,8.4039L4.9238,10.3263" +
        "L5.8266,9.4235L3.9042,7.5011L5.8278,5.5776L4.9231,4.6729L2.9995,6.5964L1.0764,4.6733Z"
private const val LENS_SUFFIX_VIEWPORT_WIDTH = 6f
private const val LENS_SUFFIX_VIEWPORT_HEIGHT = 14f

/** `c9.a.e` (0.3, 0, 0.1, 1), the scale and text offset of a switch. */
private val LensMoveEaseEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** `com.coui.appcompat.animation.b` (0.33, 0, 0.67, 1), the color crossfade of a switch. */
private val LensStandardEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** `COUISpringInterpolator(0.5, 0)`, the press-in scale. */
private val PressInEasing: Easing = LensSpringEasing(response = 0.5, bounce = 0.0)

/** `COUISpringInterpolator(0.85, 0.6)`, the release scale. */
private val PressOutEasing: Easing = LensSpringEasing(response = 0.85, bounce = 0.6)

/** `com.coui.appcompat.animation.c` (0, 0, 0.1, 1), the press-in fill. */
private val PressFillInEasing = CubicBezierEasing(0f, 0f, 0.1f, 1f)

/** `com.coui.appcompat.animation.f` (0.3, 0, 1, 1), the release fill. */
private val PressFillOutEasing = CubicBezierEasing(0.3f, 0f, 1f, 1f)

/**
 * `com.coui.appcompat.animation.p(response, bounce)` (`COUISpringInterpolator`): a unit spring of
 * stiffness (2π / response)² and damping ratio 1 - bounce, evaluated over one second of spring time
 * and normalized so that it ends at 1.
 */
private class LensSpringEasing(response: Double, bounce: Double) : Easing {
    private val omega = 2.0 * PI / (if (response == 0.0) 1.0 else response)
    private val zeta = (1.0 - bounce).let { if (it <= 0.0) 1.15 else it }
    private val damped = if (zeta < 1.0) sqrt(1.0 - zeta * zeta) * omega else 0.0
    private val coefficient = when {
        zeta < 1.0 -> zeta * omega / damped
        zeta == 1.0 -> omega
        else -> zeta * omega
    }
    private val end = raw(1.0).let { if (it == 0.0 || it.isNaN()) 1.0 else it }

    private fun raw(t: Double): Double {
        val decay = exp(-zeta * omega * t)
        val rest = when {
            zeta < 1.0 -> (sin(damped * t) * coefficient + cos(damped * t)) * decay

            zeta == 1.0 -> exp(-t * omega) * (coefficient * t + 1.0)

            else -> {
                val s = sqrt(zeta * zeta - 1.0) * omega
                (cosh(t * s) * s + sinh(t * s) * zeta * omega) * (decay / s)
            }
        }
        return 1.0 - rest
    }

    override fun transform(fraction: Float): Float = (raw(fraction.coerceAtLeast(0f).toDouble()) / end).toFloat()
}

/**
 * Default values of [CameraLensBar], from the camera's `movie_seekbar_*` and `zoom_*` dimensions.
 */
object CameraLensBarDefaults {
    /** The camera's default lenses: ultra wide 0.6x, main 1x and tele 2x (`SwitchCameraBar.i`). */
    val Labels: List<String> = listOf("0.6", "1", "2")

    /** The bar height (`switch_camera_bar_height`). */
    val Height = 36.dp

    /** The radius of an unselected disc (`movie_seekbar_circle_radius`). */
    val Radius = 14.dp

    /** The radius of the selected disc (`movie_seekbar_selected_circle_radius`). */
    val SelectedRadius = 16.dp

    /** The gap between two discs (`movie_seekbar_circle_margin`). */
    val ItemSpacing = 17.dp

    /** The radius of an unselected disc on large screens (`movie_seekbar_circle_enlarge_radius`). */
    val EnlargedRadius = 15.dp

    /** The radius of the selected disc on large screens (`movie_seekbar_selected_circle_enlarge_radius`). */
    val EnlargedSelectedRadius = 18.dp

    /** The size of the ratios (`movie_seekbar_text_size`). */
    val TextSize = 10.dp

    /** The width of the "×" glyph (`multiple_zoom_hasselblad`). */
    val SuffixWidth = 6.dp

    /** The height of the "×" glyph (`multiple_zoom_hasselblad`). */
    val SuffixHeight = 14.dp

    /** The gap between the ratio and the "×" glyph (`zoom_seekbar_multiple_space`). */
    val SuffixSpacing = 1.5.dp

    /** How far the "×" glyph sits below the center of the text (`zoom_text_offset_y`). */
    val SuffixOffsetY = 0.5.dp

    /**
     * The width of a bar of [count] lenses (`SwitchCameraBar.C`): the discs, the gaps between them
     * and room for the selected disc to grow.
     */
    fun width(
        count: Int,
        radius: Dp = Radius,
        selectedRadius: Dp = SelectedRadius,
        itemSpacing: Dp = ItemSpacing,
    ): Dp = itemSpacing * (count - 1).coerceAtLeast(0) + radius * 2 * count + (selectedRadius - radius) * 2

    /**
     * Formats a zoom ratio the way the camera labels its lenses (`ZoomUnit.b`, `DecimalFormat("#.#")`
     * rounding down): one decimal at most and no trailing ".0", so 1.0 gives "1" and 0.6 gives "0.6".
     */
    fun formatZoomRatio(ratio: Float): String {
        val tenths = floor((ratio + 1.0E-5f) * 10f).toInt()
        val whole = tenths / 10
        val decimal = tenths % 10
        return if (decimal == 0) whole.toString() else "$whole.$decimal"
    }

    /**
     * The fixed camera palette over a dark preview: discs of `color_black_with_50_percent_transparency`
     * (#7F000000), the selected disc at 50% black darkening to 65% while pressed (`J1` 0.5 to 0.65),
     * white ratios (`camera_white`) and the accent (`couiColorPrimary`,
     * `color_primary_light_hasselblad` #FB822A) for the selected ratio and its "×".
     */
    @Composable
    fun cameraLensBarColors(
        containerColor: Color = Color(0x7F000000),
        selectedContainerColor: Color = Color(0x80000000),
        pressedSelectedContainerColor: Color = Color(0xA6000000),
        contentColor: Color = Color.White,
        selectedContentColor: Color = Color(0xFFFB822A),
        suffixColor: Color = Color(0xFFFB822A),
    ): CameraLensBarColors = remember(
        containerColor,
        selectedContainerColor,
        pressedSelectedContainerColor,
        contentColor,
        selectedContentColor,
        suffixColor,
    ) {
        CameraLensBarColors(
            containerColor = containerColor,
            selectedContainerColor = selectedContainerColor,
            pressedSelectedContainerColor = pressedSelectedContainerColor,
            contentColor = contentColor,
            selectedContentColor = selectedContentColor,
            suffixColor = suffixColor,
        )
    }

    /**
     * The palette the camera switches to over a light background (`setLightBackground(true)`):
     * discs of `color_white_with_12_percent_transparency` (#1FFFFFFF), the selected one darkening
     * to 18% white while pressed.
     */
    @Composable
    fun lightBackgroundColors(): CameraLensBarColors = cameraLensBarColors(
        containerColor = Color(0x1FFFFFFF),
        selectedContainerColor = Color(0x1FFFFFFF),
        pressedSelectedContainerColor = Color(0x2EFFFFFF),
    )
}

/**
 * The colors of a [CameraLensBar].
 *
 * @param containerColor The unselected discs.
 * @param selectedContainerColor The selected disc.
 * @param pressedSelectedContainerColor The selected disc while pressed.
 * @param contentColor The unselected ratios.
 * @param selectedContentColor The selected ratio.
 * @param suffixColor The "×" after the selected ratio.
 */
@Immutable
data class CameraLensBarColors(
    val containerColor: Color,
    val selectedContainerColor: Color,
    val pressedSelectedContainerColor: Color,
    val contentColor: Color,
    val selectedContentColor: Color,
    val suffixColor: Color,
)
