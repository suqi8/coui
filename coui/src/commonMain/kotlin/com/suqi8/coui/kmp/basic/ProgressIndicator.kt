// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.ProgressIndicatorDefaults.ProgressIndicatorColors
import com.suqi8.coui.kmp.theme.COUITheme

/**
 * COUI 风格的线性进度条 (LinearProgressIndicator)。
 *
 * @param progress 当前进度 (0.0f - 1.0f)，传 null 则为无限加载状态。
 * @param modifier 修饰符。
 * @param colors 颜色配置。
 * @param height 高度，默认为 4dp。
 */
@Composable
fun LinearProgressIndicator(
    progress: Float? = null,
    modifier: Modifier = Modifier,
    colors: ProgressIndicatorColors = ProgressIndicatorDefaults.progressIndicatorColors(),
    height: Dp = ProgressIndicatorDefaults.DefaultLinearHeight
) {
    val trackColor = colors.trackColor()
    val indicatorColor = colors.indicatorColor(true)

    if (progress == null) {
        // 无限加载动画
        val infiniteTransition = rememberInfiniteTransition()
        // COUI 的线性无限加载通常是一个较短的滑块来回移动，或者渐隐渐显
        // 这里实现一个经典的来回移动效果
        val animation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 2000
                    0f at 0 using CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
                    1f at 1000 using CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
                    0f at 2000 using CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
                },
                repeatMode = RepeatMode.Restart
            )
        )

        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
        ) {
            val trackHeight = size.height
            val cornerRadius = CornerRadius(trackHeight / 2)

            // 绘制轨道
            drawRoundRect(
                color = trackColor,
                size = size,
                cornerRadius = cornerRadius
            )

            // 绘制滑块 (占总宽度的 30%)
            val indicatorWidth = size.width * 0.3f
            // 计算滑块左边缘位置
            val indicatorLeft = (size.width - indicatorWidth) * animation

            drawRoundRect(
                color = indicatorColor,
                topLeft = Offset(indicatorLeft, 0f),
                size = Size(indicatorWidth, trackHeight),
                cornerRadius = cornerRadius
            )
        }
    } else {
        // 确定性进度条
        val progressValue = progress.coerceIn(0f, 1f)
        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
        ) {
            val trackHeight = size.height
            val cornerRadius = CornerRadius(trackHeight / 2)

            // 绘制轨道
            drawRoundRect(
                color = trackColor,
                size = size,
                cornerRadius = cornerRadius
            )

            // 绘制进度
            drawRoundRect(
                color = indicatorColor,
                size = Size(size.width * progressValue, trackHeight),
                cornerRadius = cornerRadius
            )
        }
    }
}

/**
 * COUI 风格的圆形进度条 (CircularProgressIndicator)。
 * 通常用于显示具体的进度百分比。
 *
 * @param progress 当前进度 (0.0f - 1.0f)，传 null 则为无限加载状态。
 * @param modifier 修饰符。
 * @param colors 颜色配置。
 * @param strokeWidth 描边宽度，默认为大号 5dp。
 * @param size 尺寸，默认为大号 40dp。
 */
@Composable
fun CircularProgressIndicator(
    progress: Float? = null,
    modifier: Modifier = Modifier,
    colors: ProgressIndicatorColors = ProgressIndicatorDefaults.progressIndicatorColors(),
    strokeWidth: Dp = ProgressIndicatorDefaults.LargeStrokeWidth,
    size: Dp = ProgressIndicatorDefaults.LargeSize
) {
    val trackColor = colors.trackColor()
    val indicatorColor = colors.indicatorColor(true)

    if (progress == null) {
        // 无限加载状态：使用标准的旋转+伸缩动画
        val transition = rememberInfiniteTransition()
        val currentRotation by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1332, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        val currentSweep by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1332
                    // 模拟 Material/COUI 的快慢交替效果
                    10f at 0 using CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
                    280f at 666 using CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
                    10f at 1332 using CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
                },
                repeatMode = RepeatMode.Restart
            )
        )

        Canvas(modifier = modifier.size(size)) {
            val strokeWidthPx = strokeWidth.toPx()
            val diameter = this.size.minDimension - strokeWidthPx
            val topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2)
            val arcSize = Size(diameter, diameter)

            // 绘制轨道 (可选，有些无限加载不显示轨道)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx)
            )

            // 绘制动态进度
            rotate(degrees = currentRotation) {
                drawArc(
                    color = indicatorColor,
                    startAngle = -90f, // 从顶部开始
                    sweepAngle = currentSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )
            }
        }
    } else {
        // 确定性进度条
        val progressValue by rememberUpdatedState(progress.coerceIn(0f, 1f))
        Canvas(modifier = modifier.size(size)) {
            val strokeWidthPx = strokeWidth.toPx()
            val diameter = this.size.minDimension - strokeWidthPx
            val topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2)
            val arcSize = Size(diameter, diameter)

            // 绘制轨道
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx)
            )

            // 绘制进度
            drawArc(
                color = indicatorColor,
                startAngle = -90f,
                sweepAngle = 360f * progressValue,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }
    }
}

/**
 * COUI 风格的小型无限加载指示器 (Loading View)。
 * 对应原生的 18dp LoadingView，通常用于行内加载。
 *
 * @param modifier 修饰符。
 * @param color 指示器颜色，默认为主色。
 * @param size 尺寸，默认为 18dp。
 * @param strokeWidth 描边宽度，默认为 2.67dp。
 */
@Composable
fun LoadingView(
    modifier: Modifier = Modifier,
    color: Color = COUITheme.colorScheme.primary,
    size: Dp = ProgressIndicatorDefaults.LoadingViewSize,
    strokeWidth: Dp = ProgressIndicatorDefaults.LoadingViewStrokeWidth
) {
    val transition = rememberInfiniteTransition()
    // COUI 的小加载动画通常是一个快速旋转的圆弧，可能带有轻微的长度变化
    val currentRotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier.size(size)) {
        val strokeWidthPx = strokeWidth.toPx()
        val diameter = this.size.minDimension - strokeWidthPx
        val topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2)
        val arcSize = Size(diameter, diameter)

        rotate(degrees = currentRotation) {
            // 绘制一个约 270 度的圆弧 (3/4 圆)
            drawArc(
                color = color,
                startAngle = 0f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }
    }
}

object ProgressIndicatorDefaults {
    // --- Linear ---
    val DefaultLinearHeight = 4.dp // 现代化 COUI 风格通常较细

    // --- Circular (ProgressBar) ---
    // [精确数值] Large: 40dp size, 5dp stroke
    val LargeSize = 40.dp
    val LargeStrokeWidth = 5.dp
    // [精确数值] Medium: 30dp size, 3dp stroke
    val MediumSize = 30.dp
    val MediumStrokeWidth = 3.dp

    // --- Loading View (Small Infinite) ---
    // [精确数值] 18dp size, 2.67dp stroke
    val LoadingViewSize = 18.dp
    val LoadingViewStrokeWidth = 2.67.dp

    @Composable
    fun progressIndicatorColors(
        indicatorColor: Color = COUITheme.colorScheme.primary,
        trackColor: Color = COUITheme.colorScheme.tertiaryContainerVariant, // 通常是一个较淡的颜色
        disabledIndicatorColor: Color = COUITheme.colorScheme.disabledPrimarySlider,
        disabledTrackColor: Color = COUITheme.colorScheme.tertiaryContainerVariant.copy(alpha = 0.5f)
    ): ProgressIndicatorColors {
        return ProgressIndicatorColors(
            indicatorColor = indicatorColor,
            trackColor = trackColor,
            disabledIndicatorColor = disabledIndicatorColor,
            disabledTrackColor = disabledTrackColor
        )
    }

    @Immutable
    class ProgressIndicatorColors(
        private val indicatorColor: Color,
        private val trackColor: Color,
        private val disabledIndicatorColor: Color,
        private val disabledTrackColor: Color
    ) {
        @Stable
        internal fun indicatorColor(enabled: Boolean): Color =
            if (enabled) indicatorColor else disabledIndicatorColor

        @Stable
        internal fun trackColor(): Color = trackColor // 暂不区分启用/禁用轨道的颜色，可按需添加
    }
}
