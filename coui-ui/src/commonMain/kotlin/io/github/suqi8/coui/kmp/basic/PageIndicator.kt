// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * The COUI page indicator (`COUIPageIndicator` / `COUIPageIndicator2`) following a [PagerState]:
 * a row of 6dp dots, the current one in [PageIndicatorColors.traceColor]. While the pager scrolls
 * the current dot stretches towards the next one and snaps over to it (the trace "worm"), and the
 * dots it leaves and reaches cross-fade between the two colors.
 *
 * With more than five pages at most six dots are shown: four full dots flanked by smaller 4.5dp and
 * 2.5dp ones that grow and shrink as the window of visible dots follows the current page.
 *
 * @param pagerState The pager the indicator follows.
 * @param modifier The modifier applied to the indicator.
 * @param pageCount The number of dots.
 * @param dotClickable Whether tapping the indicator selects the dot nearest to the tap
 *   (`dotClickable`, `OnIndicatorDotClickListener`).
 * @param colors The dot and trace colors.
 * @param onDotClick Called with the tapped dot. `null` scrolls the pager to it.
 */
@Composable
fun PageIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    pageCount: Int = pagerState.pageCount,
    dotClickable: Boolean = true,
    colors: PageIndicatorColors = PageIndicatorDefaults.colors(),
    onDotClick: ((Int) -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val currentOnDotClick by rememberUpdatedState(onDotClick)
    PageIndicatorLayout(
        pageCount = pageCount,
        position = { pagerState.currentPage + pagerState.currentPageOffsetFraction },
        onDotClick = if (dotClickable) {
            { index ->
                val callback = currentOnDotClick
                if (callback != null) callback(index) else scope.launch { pagerState.animateScrollToPage(index) }
            }
        } else {
            null
        },
        colors = colors,
        modifier = modifier,
    )
}

/**
 * The COUI page indicator (`COUIPageIndicator` / `COUIPageIndicator2`) at a given position: a row of
 * 6dp dots, the current one in [PageIndicatorColors.traceColor].
 *
 * Between two pages ([currentPageOffsetFraction] above 0) the current dot stretches towards the next
 * one and snaps over to it (the trace "worm"). With [animated], a change of [currentPage] while the
 * offset is 0 runs that trace on COUI's position spring (damping ratio 1, stiffness 1500,
 * `setCurrentPosition(_, _, true)`); otherwise the indicator jumps.
 *
 * @param pageCount The number of dots.
 * @param currentPage The current page.
 * @param modifier The modifier applied to the indicator.
 * @param currentPageOffsetFraction How far the view has moved from [currentPage] towards the next
 *   page, from 0 to 1 (`onPageScrolled`'s position offset).
 * @param animated Whether page changes animate.
 * @param colors The dot and trace colors.
 * @param onDotClick Called with the dot nearest to a tap; `null` makes the dots not clickable.
 */
@Composable
fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    currentPageOffsetFraction: Float = 0f,
    animated: Boolean = true,
    colors: PageIndicatorColors = PageIndicatorDefaults.colors(),
    onDotClick: ((Int) -> Unit)? = null,
) {
    val target = currentPage + currentPageOffsetFraction
    val position = remember { Animatable(target, visibilityThreshold = POSITION_VISIBILITY_THRESHOLD) }
    LaunchedEffect(target, animated) {
        if (animated && currentPageOffsetFraction == 0f) {
            position.animateTo(target, spring(dampingRatio = 1f, stiffness = POSITION_SPRING_STIFFNESS))
        } else {
            position.snapTo(target)
        }
    }
    PageIndicatorLayout(
        pageCount = pageCount,
        position = { position.value },
        onDotClick = onDotClick,
        colors = colors,
        modifier = modifier,
    )
}

@Composable
internal fun PageIndicatorLayout(
    pageCount: Int,
    position: () -> Float,
    onDotClick: ((Int) -> Unit)?,
    colors: PageIndicatorColors,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val model = remember(density) {
        with(density) {
            PageIndicatorModel(
                normalDotSize = PageIndicatorDefaults.DotSize.toPx(),
                mediumDotSize = PageIndicatorDefaults.MediumDotSize.toPx(),
                smallDotSize = PageIndicatorDefaults.SmallDotSize.toPx(),
                dotSpacing = PageIndicatorDefaults.DotSpacing.toPx(),
            )
        }
    }
    model.setDotsCount(pageCount)
    val currentOnDotClick by rememberUpdatedState(onDotClick)
    val width = with(density) { model.visibleWidth().toDp() }
    Canvas(
        modifier = modifier
            .size(width = width, height = PageIndicatorDefaults.DotSize)
            .then(
                if (onDotClick != null) {
                    Modifier.pointerInput(model, layoutDirection) {
                        // onTouchEvent: a tap (released within the tap timeout) picks a dot.
                        detectTapGestures { offset ->
                            val index = model.clickedDotIndex(offset.x, layoutDirection == LayoutDirection.Rtl)
                            if (index >= 0) currentOnDotClick?.invoke(index)
                        }
                    }
                } else {
                    Modifier
                },
            ),
    ) {
        val value = position()
        val index = floor(value).toInt()
        model.setCurrentPosition(index, value - index)
        model.draw(this, colors, layoutDirection == LayoutDirection.Rtl)
    }
}

/**
 * The colors of a [PageIndicator].
 *
 * @param dotColor The color of the dots (`dotColor`).
 * @param traceColor The color of the current dot and the trace (`traceDotColor`).
 */
@Immutable
data class PageIndicatorColors(
    val dotColor: Color,
    val traceColor: Color,
)

/** Contains default values used by [PageIndicator]. */
object PageIndicatorDefaults {
    /** The full dot size and the indicator height (coui_page_indicator_dot_size). */
    val DotSize = 6.dp

    /** The dots next to the four full ones when there are more than five pages (coui_page_indicator_dot_size_medium). */
    val MediumDotSize = 4.5.dp

    /** The outermost dots when there are more than five pages (coui_page_indicator_dot_size_small). */
    val SmallDotSize = 2.5.dp

    /**
     * The dot spacing attribute (coui_page_indicator_dot_spacing). `PageIndicatorModel` doubles it:
     * dots are 6dp apart, each dot taking a 12dp step.
     */
    val DotSpacing = 3.dp

    /**
     * The `Widget.COUI.COUIPageIndicator` colors: dots `?couiColorHintNeutral` (`#42000000` /
     * `#4DFFFFFF`), trace `?couiColorLabelPrimary` (`#E6000000` / `#E6FFFFFF`).
     *
     * @param dotColor The color of the dots.
     * @param traceColor The color of the current dot and the trace.
     */
    @Composable
    fun colors(
        dotColor: Color = COUITheme.colorScheme.labelTertiary,
        traceColor: Color = COUITheme.colorScheme.labelPrimary,
    ): PageIndicatorColors = remember(dotColor, traceColor) {
        PageIndicatorColors(dotColor = dotColor, traceColor = traceColor)
    }
}

/** `PageIndicatorModel.mSpringAnimation`: stiffness 1500, damping ratio 1. */
private const val POSITION_SPRING_STIFFNESS = 1500f

/** `setMinimumVisibleChange(0.005f)`. */
private const val POSITION_VISIBILITY_THRESHOLD = 0.005f

/** `MAX_VISIBLE_DOT_NUMBER`. */
private const val MAX_VISIBLE_DOTS = 6

/** `DEFAULT_COLOR_FACTOR_THRESHOLD`. */
private const val COLOR_FACTOR_THRESHOLD = 0.05f

/** `DEFAULT_MOVE_FACTOR_THRESHOLD`. */
private const val MOVE_FACTOR_THRESHOLD = 0.005f

/** `COUIEaseInterpolator`: `PathInterpolator(0.33, 0, 0.67, 1)`. */
private val PageIndicatorEase = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

private fun ease(fraction: Float): Float = PageIndicatorEase.transform(fraction.coerceIn(0f, 1f))

/**
 * `android.animation.ArgbEvaluator`: the alpha mixes linearly, the colour channels in linear light
 * (decoded with gamma 2.2 and encoded again), as `drawTrace` blends the dot and trace colours.
 */
private fun argbEvaluate(fraction: Float, start: Color, end: Color): Color {
    fun toLinear(channel: Float) = channel.toDouble().pow(2.2).toFloat()
    fun toGamma(channel: Float) = channel.coerceIn(0f, 1f).toDouble().pow(1.0 / 2.2).toFloat()
    return Color(
        red = toGamma(toLinear(start.red) + (toLinear(end.red) - toLinear(start.red)) * fraction),
        green = toGamma(toLinear(start.green) + (toLinear(end.green) - toLinear(start.green)) * fraction),
        blue = toGamma(toLinear(start.blue) + (toLinear(end.blue) - toLinear(start.blue)) * fraction),
        alpha = (start.alpha + (end.alpha - start.alpha) * fraction).coerceIn(0f, 1f),
    )
}

/**
 * `COUIPageIndicator.PageIndicatorModel`: the dot sizes, the visible window and the trace path for
 * a position, in pixels. The window and its direction depend on the previous positions, so one model
 * follows one indicator.
 */
@Stable
internal class PageIndicatorModel(
    private val normalDotSize: Float,
    mediumDotSize: Float,
    smallDotSize: Float,
    dotSpacing: Float,
) {
    /** `mInterval`: twice the dot spacing attribute. */
    private val interval = dotSpacing * 2f

    /** `mScaleMaskIndex`: where each size level starts, relative to the window. */
    private val maskIndex = floatArrayOf(0f, -1f, -2f, -3f)

    /** `mScaleMaskSize`: how many dot steps each size level spans. */
    private val maskSize = floatArrayOf(3f, 5f, 7f, 9f)

    /** `mScaleSize`: the dot radius of each level. */
    private val scaleSize = floatArrayOf(normalDotSize / 2f, mediumDotSize / 2f, smallDotSize / 2f, 0f)

    private var count = 0
    private var currentIndex = 0
    private var currentOffset = 0f
    private var maskOffset = 0f
    private var movingToEnd = false
    private var visibleStart = 0f
    private var visibleEnd = 0f
    private var horizontalOffset = 0f
    private var radii = FloatArray(0)
    private var centers = FloatArray(0)

    private val tracePath = Path()
    private val pathUp = Path()
    private val pathDown = Path()
    private val traceRect = Path()
    private val pathLeft = Path()
    private val pathRight = Path()
    private val clipRect = Path()
    private val withoutUp = Path()

    /** `getVisibleRect`: the indicator width. */
    fun visibleWidth(): Float = min(MAX_VISIBLE_DOTS, count) * (interval + normalDotSize)

    /** `setDotsCount`: adds or removes dots at the end, one at a time. */
    fun setDotsCount(newCount: Int) {
        while (count < newCount) {
            count++
            verifyMask(removed = false)
            setCurrentPosition(currentIndex, currentOffset)
        }
        while (count > max(0, newCount)) {
            count--
            if (currentIndex + currentOffset > count - 1) {
                currentIndex = max(0, count - 1)
                currentOffset = 0f
            }
            verifyMask(removed = true)
            setCurrentPosition(currentIndex, currentOffset)
        }
    }

    private fun verifyMask(removed: Boolean) {
        if (removed) {
            maskOffset = if (count >= MAX_VISIBLE_DOTS) max(0f, maskOffset - 1f) else 0f
        }
        maskSize[0] = if (count < MAX_VISIBLE_DOTS) 5f else 3f
    }

    /**
     * `setCurrentPositionInternal`: moves the window of full dots so it contains the position, then
     * lays the dots out with the sizes of their levels. Out-of-range positions keep the last one.
     */
    fun setCurrentPosition(index: Int, offset: Float) {
        if (index + offset > count - 1 || index < 0) {
            layoutDots()
            return
        }
        val previous = currentIndex + currentOffset
        currentIndex = index
        currentOffset = offset
        val position = index + offset
        val windowStart = maskIndex[0]
        val windowSize = maskSize[0]
        if (position > windowStart + windowSize + maskOffset) {
            movingToEnd = true
        } else if (position < windowStart + maskOffset) {
            movingToEnd = false
        }
        if (position > windowStart + windowSize + maskOffset ||
            (position > windowStart + windowSize + floor(maskOffset) && position < windowStart + windowSize + maskOffset)
        ) {
            maskOffset = position - windowStart - windowSize
        } else if (position < windowStart + maskOffset ||
            (position > windowStart + maskOffset && position < windowStart + ceil(maskOffset))
        ) {
            maskOffset = position - windowStart
        }
        if (previous > position && previous >= windowStart + windowSize + floor(maskOffset) &&
            position <= windowStart + windowSize + floor(maskOffset)
        ) {
            maskOffset = floor(maskOffset)
        } else if (previous < position && previous <= windowStart + ceil(maskOffset) &&
            position >= windowStart + ceil(maskOffset)
        ) {
            maskOffset = ceil(maskOffset)
        }
        layoutDots()
    }

    private fun layoutDots() {
        val visibleOffset = if (count < MAX_VISIBLE_DOTS) 0f else min((count - MAX_VISIBLE_DOTS).toFloat(), max(0f, maskOffset - 1f))
        val start = visibleOffset * interval
        visibleStart = start
        val r0 = scaleSize[0]
        val r1 = scaleSize[1]
        val r2 = scaleSize[2]
        if (count < MAX_VISIBLE_DOTS) {
            visibleEnd = start + (interval + normalDotSize) * count
            horizontalOffset = 0f
        } else {
            val full = interval * 6f + r0 * 2f * 4f + r1 * 2f * 2f
            val edge = interval * 6f + r0 * 4f * 2f + r1 * 2f + r2 * 2f
            visibleEnd = start + when {
                visibleOffset < 1f -> edge + (full - edge) * visibleOffset
                visibleOffset > count - MAX_VISIBLE_DOTS -> full + (edge - full) * (visibleOffset - count + MAX_VISIBLE_DOTS)
                else -> full
            }
            horizontalOffset = ((normalDotSize + interval) * 6f - full) / 2f
        }
        if (radii.size != count) {
            radii = FloatArray(count)
            centers = FloatArray(count)
        }
        var x = interval / 2f
        for (i in 0 until count) {
            val radius = dotRadius(i)
            radii[i] = radius
            centers[i] = x + radius
            x += interval + radius * 2f
        }
        if (currentOffset != 0f) updatePath()
    }

    /** `getRadius`: the size level the dot falls into and its size within it. */
    private fun dotRadius(index: Int): Float {
        val position = index - maskOffset
        for (level in maskIndex.indices) {
            val start = maskIndex[level]
            if (position >= start && position <= start + maskSize[level]) return maskedSize(level, position)
        }
        return 0f
    }

    /** `getMaskedSize`: the radius of a dot easing between two levels. */
    private fun maskedSize(level: Int, position: Float): Float {
        if (level == 0) return scaleSize[0]
        val larger = scaleSize[level - 1]
        val smaller = scaleSize[level]
        return if (position < maskIndex[0]) {
            val span = maskIndex[level - 1] - maskIndex[level]
            val eased = ease(position - maskIndex[level])
            if (movingToEnd) {
                max(smaller, larger - (larger - smaller) * 2f * (1f - eased / span))
            } else {
                min(larger, smaller + (larger - smaller) * 2f * eased / span)
            }
        } else if (position <= maskIndex[0] + maskSize[0]) {
            0f
        } else {
            val span = maskIndex[level] + maskSize[level] - maskIndex[level - 1] - maskSize[level - 1]
            val eased = ease(maskIndex[level] + maskSize[level] - position)
            if (movingToEnd) {
                min(larger, smaller + (larger - smaller) * 2f * eased / span)
            } else {
                max(smaller, larger - (larger - smaller) * 2f * (1f - eased / span))
            }
        }
    }

    /** `getColorFactor`: the cross-fade of the dots the trace leaves and reaches. */
    private fun colorFactor(): Float = when {
        currentOffset <= COLOR_FACTOR_THRESHOLD -> currentOffset / COLOR_FACTOR_THRESHOLD
        currentOffset >= 1f - COLOR_FACTOR_THRESHOLD -> (1f - currentOffset) / COLOR_FACTOR_THRESHOLD
        else -> 1f
    }

    /** `getMoveFactor`: how far the trace has stretched, 0.005 to 0.495. */
    private fun moveFactor(): Float {
        val factor = when {
            currentOffset > COLOR_FACTOR_THRESHOLD && currentOffset <= 0.5f ->
                ease((currentOffset - COLOR_FACTOR_THRESHOLD) / 0.45f) / 2f

            currentOffset > 0.5f && currentOffset < 1f - COLOR_FACTOR_THRESHOLD ->
                ease((1f - currentOffset - COLOR_FACTOR_THRESHOLD) / 0.45f) / 2f

            else -> 0f
        }
        return factor.coerceIn(MOVE_FACTOR_THRESHOLD, 0.5f - MOVE_FACTOR_THRESHOLD)
    }

    /** `updatePath`: the two dots joined by a neck that thins as the trace moves over. */
    private fun updatePath() {
        if (currentIndex >= count - 1) return
        val cx = centers[currentIndex]
        val cy = normalDotSize / 2f
        val r = radii[currentIndex]
        val cx2 = centers[currentIndex + 1]
        val cy2 = normalDotSize / 2f
        val r2 = radii[currentIndex + 1]
        val move = moveFactor()
        val depart = if (currentOffset <= 0.5f) move * 2f * (interval + r2 + r) else 0f
        val port = if (currentOffset > 0.5f) move * 2f * (interval + r2 + r) else 0f
        val departCenter = cx + depart
        val neck = 0.5f - move
        val control1X = departCenter + r * neck * 2f
        val control1Y = cy - sqrt(max(0f, r * r - r * 2f * neck * 2f * r * neck))
        val portCenter = cx2 - port
        val control3X = portCenter - r2 * neck * 2f
        val control3Y = cy2 - sqrt(max(0f, r2 * r2 - r2 * 2f * neck * 2f * r2 * neck))
        val control2X = (control1X + control3X) / 2f
        val control2Y = if (control1Y - cy != 0f) {
            (r * r - (control2X - cx - depart) * (control1X - cx - depart)) / (control1Y - cy) + cy
        } else {
            cy - r
        }
        val snapAngle = (asin((neck * 2f).coerceIn(-1f, 1f)) * 180.0 / kotlin.math.PI).toFloat()

        val left = cx - r
        val right = cx2 + r2
        traceRect.reset()
        traceRect.addRect(Rect(left, 0f, right, normalDotSize))

        val top = cy - r
        val startRight = cx + r
        val bottom = cy + r
        pathUp.reset()
        pathUp.moveTo(left, cy)
        pathUp.arcTo(Rect(left, top, startRight, bottom), 180f, 90f, false)
        pathUp.lineTo(departCenter, top)
        pathUp.arcTo(Rect(left + depart, top, startRight + depart, bottom), 270f, snapAngle, false)
        pathUp.quadraticTo(control2X, control2Y, control3X, control3Y)
        val endLeft = cx2 - r2
        val top2 = cy2 - r2
        val bottom2 = cy2 + r2
        pathUp.arcTo(Rect(endLeft - port, top2, right - port, bottom2), 270f - snapAngle, snapAngle, false)
        pathUp.lineTo(cx2, top2)
        pathUp.arcTo(Rect(endLeft, top2, right, bottom2), 270f, 90f, false)
        pathUp.lineTo(right, 0f)
        pathUp.lineTo(left, 0f)
        pathUp.close()

        pathDown.reset()
        pathDown.moveTo(right, cy2)
        pathDown.arcTo(Rect(endLeft, top2, right, bottom2), 0f, 90f, false)
        pathDown.lineTo(portCenter, bottom2)
        pathDown.arcTo(Rect(endLeft - port, top2, right - port, bottom2), 90f, snapAngle, false)
        pathDown.quadraticTo(control2X, cy2 * 2f - control2Y, control1X, cy * 2f - control1Y)
        pathDown.arcTo(Rect(left + depart, top, startRight + depart, bottom), 90f - snapAngle, snapAngle, false)
        pathDown.lineTo(cx, bottom)
        pathDown.arcTo(Rect(left, top, startRight, bottom), 90f, 90f, false)
        pathDown.lineTo(left, normalDotSize)
        pathDown.lineTo(right, normalDotSize)
        pathDown.close()

        withoutUp.reset()
        withoutUp.op(traceRect, pathUp, PathOperation.Difference)
        tracePath.reset()
        tracePath.op(withoutUp, pathDown, PathOperation.Difference)

        clipRect.reset()
        clipRect.addRect(Rect(left, 0f, control2X + 0.5f, normalDotSize))
        pathLeft.reset()
        pathLeft.op(clipRect, tracePath, PathOperation.Intersect)
        clipRect.reset()
        clipRect.addRect(Rect(control2X, 0f, right, normalDotSize))
        pathRight.reset()
        pathRight.op(clipRect, tracePath, PathOperation.Intersect)
    }

    /**
     * `onDraw`: the dots inside the visible window (the two the trace joins are part of it) and the
     * trace. Right to left, COUI turns the canvas 180 degrees about the window's centre.
     */
    fun draw(scope: DrawScope, colors: PageIndicatorColors, rtl: Boolean) = with(scope) {
        withTransform({
            if (rtl) {
                translate(left = visibleEnd + horizontalOffset)
                scale(scaleX = -1f, scaleY = 1f, pivot = Offset.Zero)
            } else {
                translate(left = horizontalOffset - visibleStart)
            }
        }) {
            for (i in 0 until count) {
                if (currentOffset != 0f && (i == currentIndex || i - 1 == currentIndex)) continue
                val radius = radii[i]
                val center = centers[i]
                if (radius <= 0f || center + radius < visibleStart || center - radius > visibleEnd) continue
                drawCircle(
                    color = if (i == currentIndex) colors.traceColor else colors.dotColor,
                    radius = radius,
                    center = Offset(center, normalDotSize / 2f),
                )
            }
            if (currentOffset != 0f && currentIndex < count - 1) {
                val factor = colorFactor()
                if (factor == 1f) {
                    drawPath(tracePath, colors.traceColor)
                } else {
                    val blended = argbEvaluate(factor, colors.dotColor, colors.traceColor)
                    if (currentOffset <= 0.5f) {
                        drawPath(pathLeft, colors.traceColor)
                        drawPath(pathRight, blended)
                    } else {
                        drawPath(pathLeft, blended)
                        drawPath(pathRight, colors.traceColor)
                    }
                }
            }
        }
    }

    /** `getClickedDotIndex`: the dot under [x], else the nearest one; -1 without dots. */
    fun clickedDotIndex(x: Float, rtl: Boolean): Int {
        val local = if (rtl) visibleEnd + horizontalOffset - x else x - horizontalOffset + visibleStart
        var nearest = -1
        var nearestDistance = 0f
        for (i in 0 until count) {
            if (abs(local - centers[i]) <= radii[i] && radii[i] > 0f) return i
            val distance = abs(local - centers[i])
            if (nearest == -1 || distance < nearestDistance) {
                nearest = i
                nearestDistance = distance
            }
        }
        return nearest
    }
}
