// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.theme.COUITheme

// 假设您已经定义了 COUITheme，如果未定义，请替换为 MaterialTheme 或硬编码颜色
// import com.yourpackage.ui.theme.COUITheme

/**
 * COUI-style Checkbox component.
 *
 * This component implements a circular COUI-style checkbox
 * with smooth animations, color transitions, and haptic feedback.
 * It supports enabled/disabled states and animated checkmark drawing.
 *
 * ### Animation behavior
 * - When checked: background fills smoothly, and the checkmark is drawn progressively.
 * - When unchecked: the checkmark fades out and the background returns to outline.
 *
 * @param checked Whether the checkbox is currently checked.
 * @param onCheckedChange Callback triggered when the checked state changes.
 *   If null, the checkbox is displayed as non-interactive.
 * @param modifier Modifier to be applied to the checkbox.
 * @param enabled Whether the checkbox is interactive. Default is `true`.
 * @param colors Defines colors for different checkbox states (see [CheckboxDefaults.checkboxColors]).
 * @param interactionSource Optional interaction source for ripple and gesture tracking.
 */
@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.checkboxColors(),
    interactionSource: MutableInteractionSource? = null
) {
    val hapticFeedback = LocalHapticFeedback.current

    // 1. 定义过渡动画
    val animationSpec = tween<Float>(durationMillis = 200, easing = FastOutSlowInEasing)

    // 选中状态的动画进度 (0f = 未选中, 1f = 选中)
    val checkProgress = remember { Animatable(if (checked) 1f else 0f) }
    LaunchedEffect(checked) {
        checkProgress.animateTo(
            targetValue = if (checked) 1f else 0f,
            animationSpec = animationSpec
        )
    }

    // 颜色过渡
    val borderColor by animateColorAsState(
        targetValue = colors.borderColor(enabled, checked),
        animationSpec = tween(durationMillis = 200)
    )
    val backgroundColor by animateColorAsState(
        targetValue = colors.backgroundColor(enabled, checked),
        animationSpec = tween(durationMillis = 200)
    )
    val checkmarkColor by animateColorAsState(
        targetValue = colors.checkmarkColor(enabled, checked),
        animationSpec = tween(durationMillis = 200)
    )

    // 对勾动画状态
    val checkmarkAnim = rememberCheckmarkAnimationState(checked)

    // 2. 构建点击修饰符
    val toggleableModifier = if (onCheckedChange != null) {
        Modifier.toggleable(
            value = checked,
            onValueChange = {
                onCheckedChange(it)
                hapticFeedback.performHapticFeedback(
                    if (it) HapticFeedbackType.LongPress else HapticFeedbackType.TextHandleMove // COUI 通常使用较轻的触感
                )
            },
            enabled = enabled,
            role = Role.Checkbox,
            interactionSource = interactionSource,
            indication = null // 如果需要点击波纹，这里可以传入 LocalIndication.current
        )
    } else {
        Modifier
    }

    // 3. 绘制组件
    // 使用固定 24dp 尺寸
    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            .requiredSize(24.dp)
            .clip(CircleShape)
            .then(toggleableModifier),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.requiredSize(24.dp)) {
            val strokeWidthPx = 1.5.dp.toPx() // 未选中时的描边宽度
            val radius = size.minDimension / 2f

            // 绘制背景和描边
            // 通过 checkProgress 控制从空心到实心的过渡
            val currentStrokeWidth = strokeWidthPx * (1f - checkProgress.value)
            // 当 progress 接近 1 时，完全填充；接近 0 时，只有描边
            val isFilled = checkProgress.value > 0.95f

            if (!isFilled) {
                // 绘制未选中时的描边圆环
                drawCircle(
                    color = borderColor,
                    radius = radius - strokeWidthPx / 2,
                    style = Stroke(width = strokeWidthPx)
                )
            }

            // 绘制选中时的实心背景 (带有缩放过渡效果)
            if (checkProgress.value > 0f) {
                drawCircle(
                    color = backgroundColor,
                    radius = radius * checkProgress.value, // 简单的缩放进入效果
                    style = Fill
                )
            }

            // 绘制对勾
            if (checkProgress.value > 0f) {
                drawTrimmedCheckmark(
                    color = checkmarkColor,
                    // 仅在选中动画时显示对勾，淡入淡出
                    alpha = checkmarkAnim.alpha.value * checkProgress.value,
                    trimStart = checkmarkAnim.startTrim.value,
                    trimEnd = checkmarkAnim.endTrim.value
                )
            }
        }
    }
}

/**
 * Remembers and controls the checkmark animation state.
 *
 * Uses `Animatable` to smoothly animate the checkmark’s drawing path.
 *
 * - When `checked = true`, the checkmark is drawn progressively from start to end.
 * - When `checked = false`, the checkmark fades out and resets.
 *
 * @param checked Current checked state.
 * @return [CheckmarkAnimationState] instance containing animation values.
 */
@Composable
private fun rememberCheckmarkAnimationState(checked: Boolean): CheckmarkAnimationState {
    val checkAlpha = remember { Animatable(if (checked) 1f else 0f) }
    val checkStartTrim = remember { Animatable(if (checked) 0f else 0f) }
    val checkEndTrim = remember { Animatable(if (checked) 1f else 0f) }

    LaunchedEffect(checked) {
        if (checked) {
            // 选中动画：对勾快速画出
            checkAlpha.snapTo(1f)
            checkStartTrim.snapTo(0f)
            checkEndTrim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
        } else {
            // 取消选中动画：对勾快速淡出或收回
            checkAlpha.animateTo(0f, tween(durationMillis = 100))
            checkStartTrim.snapTo(0f)
            checkEndTrim.snapTo(0f)
        }
    }
    return remember(checkAlpha, checkStartTrim, checkEndTrim) {
        CheckmarkAnimationState(checkAlpha, checkStartTrim, checkEndTrim)
    }
}

/**
 * Holds animation states for the checkmark.
 *
 * Contains three animated properties:
 * - [alpha]: Opacity of the checkmark.
 * - [startTrim]: Start fraction of the drawn path.
 * - [endTrim]: End fraction of the drawn path.
 */
@Stable
private class CheckmarkAnimationState(
    val alpha: Animatable<Float, AnimationVector1D>,
    val startTrim: Animatable<Float, AnimationVector1D>,
    val endTrim: Animatable<Float, AnimationVector1D>
)

/**
 * Draws a trimmed checkmark path.
 *
 * This function draws a two-segment checkmark and supports partial rendering
 * through the `trimStart` and `trimEnd` parameters, enabling smooth
 * drawing animations (like progress-based reveal).
 *
 * @param color The color of the checkmark stroke.
 * @param alpha The transparency of the checkmark (0f–1f).
 * @param trimStart Start fraction of the visible path (0f–1f).
 * @param trimEnd End fraction of the visible path (0f–1f).
 */
private fun DrawScope.drawTrimmedCheckmark(
    color: Color,
    alpha: Float,
    trimStart: Float,
    trimEnd: Float
) {
    if (alpha <= 0f) return

    val strokeWidth = 2.dp.toPx()
    // 标准化视口大小，方便计算坐标
    val viewportSize = 24f

    val startX = 6.5f
    val startY = 12f
    val midX = 10.5f
    val midY = 16f
    val endX = 17.5f
    val endY = 8.5f

    // 将相对坐标转换为实际画布坐标
    val p1 = Offset(startX / viewportSize * size.width, startY / viewportSize * size.height)
    val p2 = Offset(midX / viewportSize * size.width, midY / viewportSize * size.height)
    val p3 = Offset(endX / viewportSize * size.width, endY / viewportSize * size.height)

    // 计算总路径长度用于 trim
    val len1 = (p2 - p1).getDistance()
    val len2 = (p3 - p2).getDistance()
    val totalLen = len1 + len2

    val startLen = totalLen * trimStart
    val endLen = totalLen * trimEnd

    val path = Path()

    // 第一段 (短边)
    if (startLen < len1 && endLen > 0) {
        val s = (startLen / len1).coerceIn(0f, 1f)
        val e = (endLen / len1).coerceIn(0f, 1f)
        path.moveTo(lerp(p1.x, p2.x, s), lerp(p1.y, p2.y, s))
        path.lineTo(lerp(p1.x, p2.x, e), lerp(p1.y, p2.y, e))
    }

    // 第二段 (长边)
    if (endLen > len1) {
        val s = ((startLen - len1) / len2).coerceIn(0f, 1f)
        val e = ((endLen - len1) / len2).coerceIn(0f, 1f)
        if (startLen < len1) {
            // 如果第一段已经画了，直接连线
            path.lineTo(lerp(p2.x, p3.x, e), lerp(p2.y, p3.y, e))
        } else {
            // 否则移动到第二段起点
            path.moveTo(lerp(p2.x, p3.x, s), lerp(p2.y, p3.y, s))
            path.lineTo(lerp(p2.x, p3.x, e), lerp(p2.y, p3.y, e))
        }
    }

    drawPath(
        path = path,
        color = color,
        alpha = alpha,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

/**
 * Simple linear interpolation helper function.
 */
private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return (1 - fraction) * start + fraction * stop
}

object CheckboxDefaults {
    /**
     * Creates color configuration for the COUI-style checkbox.
     */
    @Composable
    fun checkboxColors(
        // 选中时的填充色 (通常是主色)
        checkedContainerColor: Color = COUITheme.colorScheme.primary,
        // 选中时的对勾颜色 (通常是白色)
        checkedCheckmarkColor: Color = COUITheme.colorScheme.onPrimary,
        // 未选中时的边框颜色 (通常是灰色)
        uncheckedBorderColor: Color = COUITheme.colorScheme.outline,
        // 禁用状态颜色
        disabledCheckedContainerColor: Color = COUITheme.colorScheme.primary.copy(alpha = 0.5f),
        disabledCheckedCheckmarkColor: Color = COUITheme.colorScheme.onPrimary.copy(alpha = 0.5f),
        disabledUncheckedBorderColor: Color = COUITheme.colorScheme.outline.copy(alpha = 0.3f)
    ): CheckboxColors = CheckboxColors(
        checkedContainerColor = checkedContainerColor,
        checkedCheckmarkColor = checkedCheckmarkColor,
        uncheckedBorderColor = uncheckedBorderColor,
        disabledCheckedContainerColor = disabledCheckedContainerColor,
        disabledCheckedCheckmarkColor = disabledCheckedCheckmarkColor,
        disabledUncheckedBorderColor = disabledUncheckedBorderColor
    )
}

@Immutable
class CheckboxColors internal constructor(
    private val checkedContainerColor: Color,
    private val checkedCheckmarkColor: Color,
    private val uncheckedBorderColor: Color,
    private val disabledCheckedContainerColor: Color,
    private val disabledCheckedCheckmarkColor: Color,
    private val disabledUncheckedBorderColor: Color
) {
    @Composable
    internal fun backgroundColor(enabled: Boolean, checked: Boolean): Color {
        return if (enabled) {
            if (checked) checkedContainerColor else Color.Transparent
        } else {
            if (checked) disabledCheckedContainerColor else Color.Transparent
        }
    }

    @Composable
    internal fun borderColor(enabled: Boolean, checked: Boolean): Color {
        return if (enabled) {
            if (checked) Color.Transparent else uncheckedBorderColor
        } else {
            if (checked) Color.Transparent else disabledUncheckedBorderColor
        }
    }

    @Composable
    internal fun checkmarkColor(enabled: Boolean, checked: Boolean): Color {
        return if (enabled) {
            if (checked) checkedCheckmarkColor else Color.Transparent
        } else {
            if (checked) disabledCheckedCheckmarkColor else Color.Transparent
        }
    }
}
