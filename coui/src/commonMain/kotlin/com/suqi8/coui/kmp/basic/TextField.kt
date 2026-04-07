// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0
// Adapted for COUI style based on provided specifications.

package com.suqi8.coui.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.roundToInt

// --- COUI 基础定义 ---

/**
 * 定义 COUI 输入框的两种主要模式。
 */
enum class CouiTextFieldMode {
    Line, Rect
}

/**
 * 包含了 COUI 自定义的动画插值器。
 */
private object CouiInterpolators {
    val InEase = CubicBezierEasing(0.0f, 0.0f, 0.1f, 1.0f)
    val MoveEase = CubicBezierEasing(0.3f, 0.0f, 0.1f, 1.0f)
    val Ease = CubicBezierEasing(0.33f, 0.0f, 0.67f, 1.0f)
}

/**
 * 包含了 CouiTextField 的默认值。
 */
object CouiTextFieldDefaults {
    // --- 颜色 (Light Mode) ---
    private val LightFocusedIndicator = Color(0xFF0066FF); private val LightUnfocusedIndicator = Color(0x1F000000)
    private val LightDisabledIndicator = Color(0x19000000); private val LightErrorIndicator = Color(0x4DE82A18)
    private val LightLabel = Color(0x8A000000); private val LightText = Color(0xE6000000)
    private val LightDisabledText = LightText.copy(alpha = 0.26f); private val LightError = Color(0xFFE82A18)

    // --- 颜色 (Dark Mode) ---
    private val DarkFocusedIndicator = Color(0xFF247CFF); private val DarkUnfocusedIndicator = Color(0x33FFFFFF)
    private val DarkDisabledIndicator = Color(0x19FFFFFF); private val DarkErrorIndicator = Color(0xFFEA3939)
    private val DarkLabel = Color(0x8AFFFFFF); private val DarkText = Color(0xE6FFFFFF)
    private val DarkDisabledText = DarkText.copy(alpha = 0.26f); private val DarkError = Color(0xFFFF6C61)

    @Composable
    fun textFieldColors(): CouiTextFieldColors {
        val isDark = isSystemInDarkTheme()
        return remember(isDark) {
            DefaultCouiTextFieldColors(
                textColor = if (isDark) DarkText else LightText,
                disabledTextColor = if (isDark) DarkDisabledText else LightDisabledText,
                cursorColor = if (isDark) DarkFocusedIndicator else LightFocusedIndicator,
                errorCursorColor = if (isDark) DarkError else LightError,
                focusedIndicatorColor = if (isDark) DarkFocusedIndicator else LightFocusedIndicator,
                unfocusedIndicatorColor = if (isDark) DarkUnfocusedIndicator else LightUnfocusedIndicator,
                disabledIndicatorColor = if (isDark) DarkDisabledIndicator else LightDisabledIndicator,
                errorIndicatorColor = if (isDark) DarkErrorIndicator else LightErrorIndicator,
                focusedLabelColor = if (isDark) DarkFocusedIndicator else LightFocusedIndicator,
                unfocusedLabelColor = if (isDark) DarkLabel else LightLabel,
                disabledLabelColor = if (isDark) DarkDisabledText else LightDisabledText,
                errorLabelColor = if (isDark) DarkError else LightError,
                placeholderColor = if (isDark) DarkLabel else LightLabel,
                disabledPlaceholderColor = if (isDark) DarkDisabledText else LightDisabledText
            )
        }
    }
}

@Immutable
interface CouiTextFieldColors {
    @Composable fun textColor(enabled: Boolean): State<Color>
    @Composable fun placeholderColor(enabled: Boolean): State<Color>
    @Composable fun labelColor(enabled: Boolean, isError: Boolean, interactionSource: MutableInteractionSource): State<Color>
    @Composable fun indicatorColor(enabled: Boolean, isError: Boolean, interactionSource: MutableInteractionSource): State<Color>
    @Composable fun cursorColor(isError: Boolean): State<Color>
}

@Immutable
private class DefaultCouiTextFieldColors(
    private val textColor: Color, private val disabledTextColor: Color, private val cursorColor: Color, private val errorCursorColor: Color,
    private val focusedIndicatorColor: Color, private val unfocusedIndicatorColor: Color, private val disabledIndicatorColor: Color, private val errorIndicatorColor: Color,
    private val focusedLabelColor: Color, private val unfocusedLabelColor: Color, private val disabledLabelColor: Color, private val errorLabelColor: Color,
    private val placeholderColor: Color, private val disabledPlaceholderColor: Color
) : CouiTextFieldColors {
    @Composable override fun textColor(enabled: Boolean): State<Color> = rememberUpdatedState(if (enabled) textColor else disabledTextColor)
    @Composable override fun placeholderColor(enabled: Boolean): State<Color> = rememberUpdatedState(if (enabled) placeholderColor else disabledPlaceholderColor)
    @Composable
    override fun labelColor(enabled: Boolean, isError: Boolean, interactionSource: MutableInteractionSource): State<Color> {
        val isFocused by interactionSource.collectIsFocusedAsState()
        val targetValue = when {
            !enabled -> disabledLabelColor
            isError -> errorLabelColor
            isFocused -> focusedLabelColor
            else -> unfocusedLabelColor
        }
        return animateColorAsState(targetValue, tween(durationMillis = 200, easing = CouiInterpolators.Ease))
    }
    @Composable
    override fun indicatorColor(enabled: Boolean, isError: Boolean, interactionSource: MutableInteractionSource): State<Color> {
        val isFocused by interactionSource.collectIsFocusedAsState()
        val targetValue = when {
            !enabled -> disabledIndicatorColor
            isError -> errorIndicatorColor
            isFocused -> focusedIndicatorColor
            else -> unfocusedIndicatorColor
        }
        return rememberUpdatedState(targetValue)
    }
    @Composable override fun cursorColor(isError: Boolean): State<Color> = rememberUpdatedState(if (isError) errorCursorColor else cursorColor)
}

/**
 * 一个实现了 COUI 清除按钮的 Composable。
 */
@Composable
fun CouiClearButton(onClick: () -> Unit, modifier: Modifier = Modifier, tint: Color = COUITheme.colorScheme.onSecondaryContainer) {
    val clearIcon = remember {
        ImageVector.Builder("CouiClear", 22.dp, 22.dp, 22f, 22f).path(fill = SolidColor(Color.Unspecified)) {
            moveTo(11.0f, 20.0f); curveTo(15.971f, 20.0f, 20.0f, 15.971f, 20.0f, 11.0f); curveTo(20.0f, 6.029f, 15.971f, 2.0f, 11.0f, 2.0f)
            curveTo(6.029f, 2.0f, 2.0f, 6.029f, 2.0f, 11.0f); curveTo(2.0f, 15.971f, 6.029f, 20.0f, 11.0f, 20.0f); close()
            moveTo(8.596f, 7.464f); lineTo(11.0f, 9.868f); lineTo(13.404f, 7.465f); curveToRelative(0.312f, -0.313f, 0.819f, -0.313f, 1.131f, 0.0f)
            curveToRelative(0.313f, 0.312f, 0.313f, 0.819f, 0.0f, 1.131f); lineTo(12.132f, 11.0f); lineTo(14.536f, 13.404f)
            curveToRelative(0.313f, 0.312f, 0.313f, 0.819f, 0.0f, 1.131f); curveToRelative(-0.312f, 0.313f, -0.819f, 0.313f, -1.131f, 0.0f)
            lineTo(11.0f, 12.131f); lineTo(8.595f, 14.536f); curveToRelative(-0.312f, 0.313f, -0.819f, 0.313f, -1.131f, 0.0f)
            curveToRelative(-0.313f, -0.312f, -0.313f, -0.819f, 0.0f, -1.131f); lineTo(9.869f, 11.0f); lineTo(7.465f, 8.595f)
            curveToRelative(-0.313f, -0.312f, -0.313f, -0.819f, 0.0f, -1.131f); curveToRelative(0.312f, -0.313f, 0.819f, -0.313f, 1.131f, 0.0f); close()
        }.build()
    }
    Icon(clearIcon, "Clear", modifier.clickable(onClick = onClick), tint)
}

// --- DecorationBox and Layout ---

@Composable
internal fun CouiTextFieldDecoration(
    value: String, enabled: Boolean, isError: Boolean, interactionSource: MutableInteractionSource,
    mode: CouiTextFieldMode, colors: CouiTextFieldColors, innerTextField: @Composable () -> Unit,
    label: @Composable (() -> Unit)?, placeholder: @Composable (() -> Unit)?,
    leadingIcon: @Composable (() -> Unit)?, trailingIcon: @Composable (() -> Unit)?
) {
    val lineModePaddingTop = 27.dp; val lineModePaddingBottom = 15.dp
    val rectModePaddingTop = 12.dp; val rectModePaddingBottom = 12.dp
    val cornerRadius = 10.dp; val strokeWidthDefault = 1.dp; val strokeWidthFocused = 2.dp; val labelCutoutPadding = 4.dp
    val horizontalPadding = 16.dp
    val iconPadding = 16.dp

    val isFocused by interactionSource.collectIsFocusedAsState()
    val hasText = value.isNotEmpty()

    val labelAnimationProgress by animateFloatAsState(if (isFocused || hasText) 1f else 0f, tween(200, easing = CouiInterpolators.MoveEase))
    val focusAnimationProgress by animateFloatAsState(if (isFocused) 1f else 0f, tween(250, easing = CouiInterpolators.InEase))

    var labelWidth by remember { mutableStateOf(0f) }
    var leadingWidth by remember { mutableStateOf(0) }

    val unfocusedIndicatorColor = colors.indicatorColor(enabled, isError = false, interactionSource = remember { MutableInteractionSource() }).value
    val indicatorColor = colors.indicatorColor(enabled, isError, interactionSource).value
    val finalPlaceholder: @Composable (() -> Unit)? = if (label != null || hasText) null else placeholder

    val topPadding = if (mode == CouiTextFieldMode.Line) lineModePaddingTop else rectModePaddingTop
    val bottomPadding = if (mode == CouiTextFieldMode.Line) lineModePaddingBottom else rectModePaddingBottom

    Layout(
        modifier = Modifier.drawWithContent {
            drawContent()
            if (mode == CouiTextFieldMode.Line) {
                val strokeWidthPx = strokeWidthDefault.toPx()
                drawLine(unfocusedIndicatorColor, Offset(0f, size.height - strokeWidthPx / 2), Offset(size.width, size.height - strokeWidthPx / 2), strokeWidthPx)
                if (focusAnimationProgress > 0.01f) {
                    drawLine(indicatorColor, Offset(0f, size.height - strokeWidthFocused.toPx() / 2), Offset(size.width * focusAnimationProgress, size.height - strokeWidthFocused.toPx() / 2), strokeWidthFocused.toPx())
                }
            } else {
                val strokeWidthPx = lerp(strokeWidthDefault.toPx(), strokeWidthFocused.toPx(), focusAnimationProgress)
                val cornerRadiusPx = cornerRadius.toPx()

                if (labelAnimationProgress < 0.1f || labelWidth == 0f) {
                    drawRoundRect(indicatorColor, cornerRadius = CornerRadius(cornerRadiusPx), style = Stroke(strokeWidthPx))
                } else {
                    val labelScale = lerp(1f, 10.sp.toPx() / 17.sp.toPx(), 1f)
                    val cutoutWidth = (labelWidth * labelScale) + labelCutoutPadding.toPx() * 2

                    val iconPaddingPx = if (leadingWidth > 0) iconPadding.toPx() else 0f
                    val cutoutStart = horizontalPadding.toPx() + leadingWidth + iconPaddingPx - labelCutoutPadding.toPx()

                    val path = Path().apply {
                        val rect = size.toRect().deflate(strokeWidthPx / 2)
                        moveTo(rect.right - cornerRadiusPx, rect.top)
                        arcTo(Rect(rect.right - 2 * cornerRadiusPx, rect.top, rect.right, rect.top + 2 * cornerRadiusPx), -90f, 90f, false)
                        lineTo(rect.right, rect.bottom - cornerRadiusPx)
                        arcTo(Rect(rect.right - 2 * cornerRadiusPx, rect.bottom - 2 * cornerRadiusPx, rect.right, rect.bottom), 0f, 90f, false)
                        lineTo(rect.left + cornerRadiusPx, rect.bottom)
                        arcTo(Rect(rect.left, rect.bottom - 2 * cornerRadiusPx, rect.left + 2 * cornerRadiusPx, rect.bottom), 90f, 90f, false)
                        lineTo(rect.left, rect.top + cornerRadiusPx)
                        arcTo(Rect(rect.left, rect.top, rect.left + 2 * cornerRadiusPx, rect.top + 2 * cornerRadiusPx), 180f, 90f, false)
                        lineTo(cutoutStart, rect.top)
                        moveTo(cutoutStart + cutoutWidth, rect.top)
                        lineTo(rect.right - cornerRadiusPx, rect.top)
                    }
                    drawPath(path, color = indicatorColor, style = Stroke(width = strokeWidthPx))
                }
            }
        },
        content = {
            if (leadingIcon != null) Box(Modifier.layoutId("leading")) { leadingIcon() }
            if (trailingIcon != null) Box(Modifier.layoutId("trailing")) { trailingIcon() }
            if (finalPlaceholder != null) Box(Modifier.layoutId("placeholder")) { finalPlaceholder() }
            if (label != null) Box(Modifier.layoutId("label")) { label() }
            Box(Modifier.layoutId("textField")) { innerTextField() }
        }
    ) { measurables, constraints ->
        val topPaddingPx = topPadding.roundToPx()
        val bottomPaddingPx = bottomPadding.roundToPx()
        val horizontalPaddingPx = horizontalPadding.roundToPx()

        val leadingPlaceable = measurables.find { it.layoutId == "leading" }?.measure(constraints.copy(minWidth = 0))
        leadingWidth = leadingPlaceable?.width ?: 0 // Update leadingWidth

        val trailingPlaceable = measurables.find { it.layoutId == "trailing" }?.measure(constraints.copy(minWidth = 0))

        val trailingWidth = trailingPlaceable?.width ?: 0
        val iconPaddingPx = if(leadingWidth > 0) iconPadding.roundToPx() else 0

        val textStartX = leadingWidth + iconPaddingPx

        val textAvailableWidth = constraints.maxWidth - (2 * horizontalPaddingPx + textStartX + trailingWidth +
                if (trailingWidth > 0) iconPadding.roundToPx() else 0)

        val textConstraints = constraints.copy(minWidth = 0, maxWidth = textAvailableWidth, minHeight = 0)

        val textFieldPlaceable = measurables.first { it.layoutId == "textField" }.measure(textConstraints)
        val placeholderPlaceable = measurables.find { it.layoutId == "placeholder" }?.measure(textConstraints)

        val height = topPaddingPx + textFieldPlaceable.height + bottomPaddingPx
        val width = constraints.maxWidth

        val labelPlaceable = measurables.find { it.layoutId == "label" }?.let {
            val placeable = it.measure(textConstraints)
            labelWidth = placeable.width.toFloat()
            placeable
        }

        layout(width, height) {
            leadingPlaceable?.placeRelative(horizontalPaddingPx, (height - leadingPlaceable.height) / 2)
            trailingPlaceable?.placeRelative(width - trailingWidth - horizontalPaddingPx, (height - trailingPlaceable.height) / 2)

            val textVerticalPosition = topPaddingPx
            val finaltextStartX = horizontalPaddingPx + textStartX

            if (labelPlaceable != null) {
                val labelScale = lerp(1f, 10.sp.value / 17.sp.value, labelAnimationProgress)
                val labelBaseline = labelPlaceable[FirstBaseline]; val textFieldBaseline = textFieldPlaceable[FirstBaseline]
                val baseLineShift = if (labelBaseline != -1 && textFieldBaseline != -1) textFieldBaseline - labelBaseline else 0

                val labelYExpanded = textVerticalPosition + baseLineShift
                val labelYShrunk = 0
                val labelY = lerp(labelYExpanded.toFloat(), labelYShrunk.toFloat(), labelAnimationProgress)

                labelPlaceable.placeRelativeWithLayer(finaltextStartX, labelY.roundToInt()) {
                    scaleX = labelScale; scaleY = labelScale; transformOrigin = TransformOrigin(0f, 0f)
                }
            }

            if (labelAnimationProgress < 1f) {
                placeholderPlaceable?.placeRelative(finaltextStartX, textVerticalPosition)
            }
            textFieldPlaceable.placeRelative(finaltextStartX, textVerticalPosition)
        }
    }
}

// --- Public TextField APIs ---

@Composable
fun TextField(
    value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default, label: @Composable (() -> Unit)? = null, placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null, trailingIcon: @Composable (() -> Unit)? = null, isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None, keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default, singleLine: Boolean = true, maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1, onTextLayout: (TextLayoutResult) -> Unit = {}, interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(Color.Unspecified), mode: CouiTextFieldMode = CouiTextFieldMode.Line, colors: CouiTextFieldColors = CouiTextFieldDefaults.textFieldColors()
) {
    val mergedTextStyle = textStyle.merge(TextStyle(color = colors.textColor(enabled).value, fontSize = 17.sp))
    val shakeController = remember { Animatable(0f) }
    val density = LocalDensity.current
    LaunchedEffect(isError) {
        if (isError) {
            val amplitude = with(density) { 5.33.dp.toPx() }
            shakeController.animateTo(0f, tween(0))
            shakeController.animateTo(0f, keyframes {
                durationMillis = 450
                amplitude at 83 using CouiInterpolators.Ease; (amplitude / 2) at 216 using CouiInterpolators.Ease
                (amplitude / 2) at 333 using CouiInterpolators.Ease; 0f at 450 using CouiInterpolators.Ease
            })
        } else { shakeController.snapTo(0f) }
    }

    BasicTextField(
        value = value, onValueChange = onValueChange, modifier = modifier.graphicsLayer { translationX = shakeController.value },
        enabled = enabled, readOnly = readOnly, textStyle = mergedTextStyle,
        cursorBrush = if (cursorBrush.isUnspecified) SolidColor(colors.cursorColor(isError).value) else cursorBrush,
        visualTransformation = visualTransformation, keyboardOptions = keyboardOptions, keyboardActions = keyboardActions,
        interactionSource = interactionSource, singleLine = singleLine, maxLines = maxLines, minLines = minLines,
        onTextLayout = onTextLayout,
        decorationBox = { innerTextField ->
            CouiTextFieldDecoration(value, enabled, isError, interactionSource, mode, colors, innerTextField, label, placeholder, leadingIcon, trailingIcon)
        }
    )
}

@Composable
fun TextField(
    value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default, label: @Composable (() -> Unit)? = null, placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null, trailingIcon: @Composable (() -> Unit)? = null, isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None, keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default, singleLine: Boolean = true, maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1, onTextLayout: (TextLayoutResult) -> Unit = {}, interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(Color.Unspecified), mode: CouiTextFieldMode = CouiTextFieldMode.Line, colors: CouiTextFieldColors = CouiTextFieldDefaults.textFieldColors()
) {
    TextField(
        value = value.text, onValueChange = { onValueChange(value.copy(text = it, selection = value.selection, composition = value.composition)) },
        modifier, enabled, readOnly, textStyle, label, placeholder, leadingIcon, trailingIcon, isError,
        visualTransformation, keyboardOptions, keyboardActions, singleLine, maxLines, minLines,
        onTextLayout, interactionSource, cursorBrush, mode, colors
    )
}

@Composable
fun TextField(
    state: TextFieldState, modifier: Modifier = Modifier, enabled: Boolean = true, readOnly: Boolean = false, textStyle: TextStyle = TextStyle.Default,
    label: @Composable (() -> Unit)? = null, placeholder: @Composable (() -> Unit)? = null, leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null, isError: Boolean = false, keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null, lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }, cursorBrush: Brush = SolidColor(Color.Unspecified),
    outputTransformation: OutputTransformation? = null, inputTransformation: InputTransformation? = null,
    mode: CouiTextFieldMode = CouiTextFieldMode.Line, colors: CouiTextFieldColors = CouiTextFieldDefaults.textFieldColors()
) {
    val mergedTextStyle = textStyle.merge(TextStyle(color = colors.textColor(enabled).value, fontSize = 17.sp))
    val shakeController = remember { Animatable(0f) }
    val density = LocalDensity.current
    LaunchedEffect(isError) {
        if (isError) {
            val amplitude = with(density) { 5.33.dp.toPx() }
            shakeController.animateTo(0f, tween(0))
            shakeController.animateTo(0f, keyframes {
                durationMillis = 450
                amplitude at 83 using CouiInterpolators.Ease; (amplitude / 2) at 216 using CouiInterpolators.Ease
                (amplitude / 2) at 333 using CouiInterpolators.Ease; 0f at 450 using CouiInterpolators.Ease
            })
        } else { shakeController.snapTo(0f) }
    }

    BasicTextField(
        state = state, modifier = modifier.graphicsLayer { translationX = shakeController.value }, enabled = enabled, readOnly = readOnly,
        textStyle = mergedTextStyle,
        cursorBrush = if (cursorBrush.isUnspecified) SolidColor(colors.cursorColor(isError).value) else cursorBrush,
        keyboardOptions = keyboardOptions, onKeyboardAction = onKeyboardAction, lineLimits = lineLimits, onTextLayout = onTextLayout,
        interactionSource = interactionSource, outputTransformation = outputTransformation, inputTransformation = inputTransformation,
        decorator = { innerTextField ->
            CouiTextFieldDecoration(
                value = state.text.toString(), enabled, isError, interactionSource, mode, colors,
                innerTextField, label, placeholder, leadingIcon, trailingIcon
            )
        }
    )
}

// Helper Extensions and Functions
private val Brush.isUnspecified: Boolean get() = this is SolidColor && this.value == Color.Unspecified
private fun Size.toRect() = Rect(Offset.Zero, this)
