// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.then
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalContentColor
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * Background mode of a [TextField], mirroring COUIEditText couiBackgroundMode.
 *
 * - [Rectangle]: stroke-only rounded rectangle (no fill), 10dp corners; a 0.33dp stroke in
 *   couiColorDivider that turns couiColorPrimary while focused (1dp only in error state)
 *   (COUIEditText MODE_BACKGROUND_RECT, Widget.COUI.EditText.HintAnim.Rectangle).
 * - [Line]: no fill; a 0.33dp bottom underline plus a focused 1dp primary line that
 *   expands from the start edge (COUIEditText MODE_BACKGROUND_LINE — the form used by
 *   ColorOS Settings dialogs and bottom-sheet editors via
 *   Widget.COUI.EditText.HintAnim.Line.HintDisable / Widget.COUI.Input).
 * - [None]: no background decoration at all — bare text (COUIEditText
 *   MODE_BACKGROUND_NONE / MODE_BACKGROUND_NO_LINE, the form used inside white
 *   input cards such as COUICardSingleInputView).
 */
enum class TextFieldMode { Rectangle, Line, None }

/**
 * A [TextField] component with COUI style.
 *
 * @param state The [TextFieldState] to be shown in the text field.
 * @param modifier The modifier to be applied to the [TextField].
 * @param backgroundMode The [TextFieldMode] of the background decoration. Defaults to
 *   [TextFieldMode.Line], the form ColorOS Settings uses for dialog / panel inputs.
 * @param insideMargin The margin inside the [TextField]. Defaults to the
 *   COUI padding of the selected [backgroundMode].
 * @param colors The [TextFieldColors] applied to the [TextField]. Use
 *   [TextFieldDefaults.textFieldColors] to customize.
 * @param cornerRadius The corner radius of the [TextField] (only used by
 *   [TextFieldMode.Rectangle]).
 * @param minHeight The minimum height of the [TextField]. Defaults to
 *   [TextFieldDefaults.minHeight] of the [backgroundMode]: 62dp for [TextFieldMode.Line] and
 *   [TextFieldMode.Rectangle] (Widget.COUI.EditText coui_edit_text_height_min), 0dp for
 *   [TextFieldMode.None]. The content is centered vertically inside it.
 * @param label The label (hint) to be displayed when the [TextField] is empty.
 * @param useLabelAsPlaceholder Whether the label behaves as a plain placeholder that
 *   disappears once text is entered (COUI default). Set to `false` to enable the COUI
 *   HintAnim floating label: in [TextFieldMode.Line] the field reserves 10dp + the collapsed
 *   label height + 5dp above the text and the label floats to 10dp from the top; in
 *   [TextFieldMode.Rectangle] the border starts half a collapsed label below the top and the
 *   floating label rides on it inside a 4dp cutout (COUIEditText / COUICutoutDrawable).
 * @param justShowFocusLine Only used by [TextFieldMode.Line]: hide the resting
 *   underline and show just the focused expanding line, mirroring
 *   COUIInputPreference couiJustShowFocusLine.
 * @param enabled Whether the [TextField] is enabled.
 * @param readOnly Whether the [TextField] is read-only.
 * @param isError Whether the [TextField] is in error state. Turning this on blends the
 *   border / underline to [TextFieldColors.errorColor], tints the text selection with it and,
 *   when the field is focused, plays a horizontal shake animation; when the 450ms error
 *   animation ends the whole text is selected, so the error-tinted selection covers it, mirroring
 *   COUIErrorEditTextHelper.setErrorStateEnd. The label keeps its color, as in COUI.
 * @param maxCount The maximum number of characters. When not null a "count/max"
 *   counter is shown at the end and input is truncated at [maxCount], mirroring
 *   COUIInputView couiInputMaxCount. The counter switches to
 *   [TextFieldColors.counterErrorColor] at the limit.
 * @param showClearButton Whether to show a clear (fast delete) button when the
 *   [TextField] is focused and not empty, mirroring COUIEditText quickDelete.
 * @param showPasswordToggle Whether to show an eye toggle that switches the
 *   password visibility, mirroring COUIInputView couiEnablePassword. While the
 *   password is hidden, the text is masked and [outputTransformation] is ignored. The toggle
 *   follows [enabled] (COUIInputView.setEnabled): a disabled toggle is not clickable and uses
 *   [TextFieldColors.disabledIconColor].
 * @param initialPasswordVisible Whether the password starts visible. Defaults to masked, like a
 *   COUIEditText with a password input type; [InputView] passes `true` because COUIInputView
 *   couiPasswordType defaults to OPEN.
 * @param inputTransformation The input transformation to be applied to the [TextField].
 * @param textStyle The text style to be applied to the [TextField]. Defaults to the
 *   COUI input text of the selected [backgroundMode]: 16sp regular
 *   (couiEditTextTextAppearance), bold in [TextFieldMode.Rectangle]
 *   (Widget.COUI.EditText.HintAnim.Rectangle android:textStyle=bold).
 * @param keyboardOptions The keyboard options to be applied to the [TextField].
 * @param onKeyboardAction The keyboard action handler for the [TextField].
 * @param lineLimits The line limits for the [TextField].
 * @param leadingIcon The leading icon to be displayed in the [TextField].
 * @param trailingIcon The trailing icon to be displayed in the [TextField].
 * @param onTextLayout The callback to be called when the text layout changes.
 * @param interactionSource The interaction source to be applied to the [TextField].
 * @param cursorBrush The brush to be used for the cursor.
 * @param outputTransformation The output transformation for the text field.
 * @param scrollState The scroll state for the text field.
 */
@Composable
fun TextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    backgroundMode: TextFieldMode = TextFieldMode.Line,
    insideMargin: DpSize = TextFieldDefaults.insideMargin(backgroundMode),
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    cornerRadius: Dp = TextFieldDefaults.CornerRadius,
    minHeight: Dp = TextFieldDefaults.minHeight(backgroundMode),
    label: String = "",
    useLabelAsPlaceholder: Boolean = true,
    justShowFocusLine: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    maxCount: Int? = null,
    showClearButton: Boolean = false,
    showPasswordToggle: Boolean = false,
    initialPasswordVisible: Boolean = false,
    inputTransformation: InputTransformation? = null,
    textStyle: TextStyle = TextFieldDefaults.textStyle(backgroundMode),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(colors.borderColor),
    outputTransformation: OutputTransformation? = null,
    scrollState: ScrollState = rememberScrollState(),
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val labelState by remember(label, useLabelAsPlaceholder, enabled) {
        derivedStateOf {
            resolveLabelState(label, useLabelAsPlaceholder, state.text.isNotEmpty(), isFocused && enabled)
        }
    }

    val currentOnTextLayout by rememberUpdatedState(onTextLayout)

    val contentColor = LocalContentColor.current
    val resolvedTextStyle = remember(textStyle, contentColor, enabled, colors.disabledTextColor) {
        val textColor = if (enabled) textStyle.color.takeOrElse { contentColor } else colors.disabledTextColor
        textStyle.copy(color = textColor)
    }

    var passwordVisible by rememberSaveable { mutableStateOf(initialPasswordVisible) }
    val resolvedOutputTransformation =
        if (showPasswordToggle && !passwordVisible) PasswordMaskOutputTransformation else outputTransformation
    val resolvedInputTransformation = if (maxCount != null) {
        remember(inputTransformation, maxCount) {
            val limiter = InputTransformation {
                // Truncate instead of rejecting, mirroring COUIInputView subSequence(0, maxCount).
                if (length > maxCount) replace(maxCount, length, "")
            }
            inputTransformation?.then(limiter) ?: limiter
        }
    } else {
        inputTransformation
    }

    ErrorSelectAllEffect(isError = isError) {
        state.edit { selection = TextRange(0, length) }
    }

    ErrorSelectionColors(isError = isError, errorColor = colors.errorColor) {
        BasicTextField(
            state = state,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = resolvedTextStyle,
            cursorBrush = cursorBrush,
            keyboardOptions = keyboardOptions,
            onKeyboardAction = onKeyboardAction,
            lineLimits = lineLimits,
            onTextLayout = currentOnTextLayout,
            interactionSource = interactionSource,
            inputTransformation = resolvedInputTransformation,
            outputTransformation = resolvedOutputTransformation,
            scrollState = scrollState,
            decorator = TextFieldDecorator { innerTextField ->
                val textLength = state.text.length
                TextFieldChrome(
                    label = label,
                    labelState = labelState,
                    floatingLabel = !useLabelAsPlaceholder && label.isNotEmpty(),
                    labelNormalFontSize = resolvedTextStyle.fontSize.takeIf { it.isSp }?.value ?: DefaultLabelFontSize,
                    colors = colors,
                    cornerRadius = cornerRadius,
                    insideMargin = insideMargin,
                    minHeight = minHeight,
                    backgroundMode = backgroundMode,
                    justShowFocusLine = justShowFocusLine,
                    enabled = enabled,
                    isError = isError,
                    counterText = maxCount?.let { "$textLength/$it" },
                    counterOverflow = maxCount != null && textLength >= maxCount,
                    clearButtonVisible = showClearButton && enabled && isFocused && textLength > 0,
                    onClearText = if (showClearButton) ({ state.clearText() }) else null,
                    passwordVisible = if (showPasswordToggle) passwordVisible else null,
                    onPasswordToggle = { passwordVisible = !passwordVisible },
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    isFocused = isFocused,
                    innerTextField = innerTextField,
                )
            },
        )
    }
}

/**
 * A [TextField] component with COUI style.
 *
 * @param value The input [TextFieldValue] to be shown in the text field.
 * @param onValueChange The callback that is triggered when the input service updates values in
 *   [TextFieldValue]. An updated [TextFieldValue] comes as a parameter of the callback.
 * @param modifier The modifier to be applied to the [TextField].
 * @param backgroundMode The [TextFieldMode] of the background decoration. Defaults to
 *   [TextFieldMode.Line], the form ColorOS Settings uses for dialog / panel inputs.
 * @param insideMargin The margin inside the [TextField]. Defaults to the
 *   COUI padding of the selected [backgroundMode].
 * @param colors The [TextFieldColors] applied to the [TextField]. Use
 *   [TextFieldDefaults.textFieldColors] to customize.
 * @param cornerRadius The corner radius of the [TextField] (only used by
 *   [TextFieldMode.Rectangle]).
 * @param minHeight The minimum height of the [TextField]. Defaults to
 *   [TextFieldDefaults.minHeight] of the [backgroundMode]: 62dp for [TextFieldMode.Line] and
 *   [TextFieldMode.Rectangle] (Widget.COUI.EditText coui_edit_text_height_min), 0dp for
 *   [TextFieldMode.None]. The content is centered vertically inside it.
 * @param label The label (hint) to be displayed when the [TextField] is empty.
 * @param useLabelAsPlaceholder Whether the label behaves as a plain placeholder that
 *   disappears once text is entered (COUI default). Set to `false` to enable the COUI
 *   HintAnim floating label: in [TextFieldMode.Line] the field reserves 10dp + the collapsed
 *   label height + 5dp above the text and the label floats to 10dp from the top; in
 *   [TextFieldMode.Rectangle] the border starts half a collapsed label below the top and the
 *   floating label rides on it inside a 4dp cutout (COUIEditText / COUICutoutDrawable).
 * @param justShowFocusLine Only used by [TextFieldMode.Line]: hide the resting
 *   underline and show just the focused expanding line, mirroring
 *   COUIInputPreference couiJustShowFocusLine.
 * @param enabled Whether the [TextField] is enabled.
 * @param readOnly Whether the [TextField] is read-only.
 * @param isError Whether the [TextField] is in error state. Turning this on blends the
 *   border / underline to [TextFieldColors.errorColor], tints the text selection with it and,
 *   when the field is focused, plays a horizontal shake animation; when the 450ms error
 *   animation ends the whole text is selected, so the error-tinted selection covers it, mirroring
 *   COUIErrorEditTextHelper.setErrorStateEnd. The label keeps its color, as in COUI.
 * @param maxCount The maximum number of characters. When not null a "count/max"
 *   counter is shown at the end and input is truncated at [maxCount], mirroring
 *   COUIInputView couiInputMaxCount. The counter switches to
 *   [TextFieldColors.counterErrorColor] at the limit.
 * @param showClearButton Whether to show a clear (fast delete) button when the
 *   [TextField] is focused and not empty, mirroring COUIEditText quickDelete.
 * @param showPasswordToggle Whether to show an eye toggle that switches the
 *   password visibility, mirroring COUIInputView couiEnablePassword. While the
 *   password is hidden, the text is masked and [visualTransformation] is ignored. The toggle
 *   follows [enabled] (COUIInputView.setEnabled): a disabled toggle is not clickable and uses
 *   [TextFieldColors.disabledIconColor].
 * @param initialPasswordVisible Whether the password starts visible. Defaults to masked, like a
 *   COUIEditText with a password input type; [InputView] passes `true` because COUIInputView
 *   couiPasswordType defaults to OPEN.
 * @param textStyle The text style to be applied to the [TextField]. Defaults to the
 *   COUI input text of the selected [backgroundMode].
 * @param keyboardOptions The keyboard options to be applied to the [TextField].
 * @param keyboardActions The keyboard actions to be applied to the [TextField].
 * @param leadingIcon The leading icon to be displayed in the [TextField].
 * @param trailingIcon The trailing icon to be displayed in the [TextField].
 * @param singleLine Whether the text field is single line.
 * @param maxLines The maximum number of lines allowed to be displayed in [TextField].
 * @param minLines The minimum number of lines allowed to be displayed in [TextField]. It is required
 *   that 1 <= [minLines] <= [maxLines].
 * @param visualTransformation The visual transformation to be applied to the [TextField].
 * @param onTextLayout The callback to be called when the text layout changes.
 * @param interactionSource The interaction source to be applied to the [TextField].
 * @param cursorBrush The brush to be used for the cursor.
 */
@Composable
fun TextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    backgroundMode: TextFieldMode = TextFieldMode.Line,
    insideMargin: DpSize = TextFieldDefaults.insideMargin(backgroundMode),
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    cornerRadius: Dp = TextFieldDefaults.CornerRadius,
    minHeight: Dp = TextFieldDefaults.minHeight(backgroundMode),
    label: String = "",
    useLabelAsPlaceholder: Boolean = true,
    justShowFocusLine: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    maxCount: Int? = null,
    showClearButton: Boolean = false,
    showPasswordToggle: Boolean = false,
    initialPasswordVisible: Boolean = false,
    textStyle: TextStyle = TextFieldDefaults.textStyle(backgroundMode),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(colors.borderColor),
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val labelState = resolveLabelState(label, useLabelAsPlaceholder, value.text.isNotEmpty(), isFocused && enabled)

    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnTextLayout by rememberUpdatedState(onTextLayout)

    val contentColor = LocalContentColor.current
    val resolvedTextStyle = remember(textStyle, contentColor, enabled, colors.disabledTextColor) {
        val textColor = if (enabled) textStyle.color.takeOrElse { contentColor } else colors.disabledTextColor
        textStyle.copy(color = textColor)
    }

    var passwordVisible by rememberSaveable { mutableStateOf(initialPasswordVisible) }
    val resolvedVisualTransformation = if (showPasswordToggle && !passwordVisible) {
        remember { PasswordVisualTransformation() }
    } else {
        visualTransformation
    }
    val currentValue by rememberUpdatedState(value)
    ErrorSelectAllEffect(isError = isError) {
        val latest = currentValue
        currentOnValueChange(latest.copy(selection = TextRange(0, latest.text.length)))
    }
    val handleValueChange: (TextFieldValue) -> Unit = if (maxCount == null) {
        { currentOnValueChange(it) }
    } else {
        remember(maxCount) {
            { new ->
                if (new.text.length > maxCount) {
                    // Truncate instead of rejecting, mirroring COUIInputView subSequence(0, maxCount).
                    currentOnValueChange(
                        TextFieldValue(
                            text = new.text.take(maxCount),
                            selection = TextRange(
                                minOf(new.selection.start, maxCount),
                                minOf(new.selection.end, maxCount),
                            ),
                        ),
                    )
                } else {
                    currentOnValueChange(new)
                }
            }
        }
    }

    ErrorSelectionColors(isError = isError, errorColor = colors.errorColor) {
        BasicTextField(
            value = value,
            onValueChange = handleValueChange,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = resolvedTextStyle,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            visualTransformation = resolvedVisualTransformation,
            onTextLayout = currentOnTextLayout,
            interactionSource = interactionSource,
            cursorBrush = cursorBrush,
            decorationBox = @Composable { innerTextField ->
                TextFieldChrome(
                    label = label,
                    labelState = labelState,
                    floatingLabel = !useLabelAsPlaceholder && label.isNotEmpty(),
                    labelNormalFontSize = resolvedTextStyle.fontSize.takeIf { it.isSp }?.value ?: DefaultLabelFontSize,
                    colors = colors,
                    cornerRadius = cornerRadius,
                    insideMargin = insideMargin,
                    minHeight = minHeight,
                    backgroundMode = backgroundMode,
                    justShowFocusLine = justShowFocusLine,
                    enabled = enabled,
                    isError = isError,
                    counterText = maxCount?.let { "${value.text.length}/$it" },
                    counterOverflow = maxCount != null && value.text.length >= maxCount,
                    clearButtonVisible = showClearButton && enabled && isFocused && value.text.isNotEmpty(),
                    onClearText = if (showClearButton) ({ currentOnValueChange(TextFieldValue("")) }) else null,
                    passwordVisible = if (showPasswordToggle) passwordVisible else null,
                    onPasswordToggle = { passwordVisible = !passwordVisible },
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    isFocused = isFocused,
                    innerTextField = innerTextField,
                )
            },
        )
    }
}

/**
 * A text field component with COUI style.
 *
 * @param value The text to be displayed in the text field.
 * @param onValueChange The callback to be called when the value changes.
 * @param modifier The modifier to be applied to the [TextField].
 * @param backgroundMode The [TextFieldMode] of the background decoration. Defaults to
 *   [TextFieldMode.Line], the form ColorOS Settings uses for dialog / panel inputs.
 * @param insideMargin The margin inside the [TextField]. Defaults to the
 *   COUI padding of the selected [backgroundMode].
 * @param colors The [TextFieldColors] applied to the [TextField]. Use
 *   [TextFieldDefaults.textFieldColors] to customize.
 * @param cornerRadius The corner radius of the [TextField] (only used by
 *   [TextFieldMode.Rectangle]).
 * @param minHeight The minimum height of the [TextField]. Defaults to
 *   [TextFieldDefaults.minHeight] of the [backgroundMode]: 62dp for [TextFieldMode.Line] and
 *   [TextFieldMode.Rectangle] (Widget.COUI.EditText coui_edit_text_height_min), 0dp for
 *   [TextFieldMode.None]. The content is centered vertically inside it.
 * @param label The label (hint) to be displayed when the [TextField] is empty.
 * @param useLabelAsPlaceholder Whether the label behaves as a plain placeholder that
 *   disappears once text is entered (COUI default). Set to `false` to enable the COUI
 *   HintAnim floating label: in [TextFieldMode.Line] the field reserves 10dp + the collapsed
 *   label height + 5dp above the text and the label floats to 10dp from the top; in
 *   [TextFieldMode.Rectangle] the border starts half a collapsed label below the top and the
 *   floating label rides on it inside a 4dp cutout (COUIEditText / COUICutoutDrawable).
 * @param justShowFocusLine Only used by [TextFieldMode.Line]: hide the resting
 *   underline and show just the focused expanding line, mirroring
 *   COUIInputPreference couiJustShowFocusLine.
 * @param enabled Whether the [TextField] is enabled.
 * @param readOnly Whether the [TextField] is read-only.
 * @param isError Whether the [TextField] is in error state. Turning this on blends the
 *   border / underline to [TextFieldColors.errorColor], tints the text selection with it and,
 *   when the field is focused, plays a horizontal shake animation; when the 450ms error
 *   animation ends the whole text is selected, so the error-tinted selection covers it, mirroring
 *   COUIErrorEditTextHelper.setErrorStateEnd. The label keeps its color, as in COUI.
 * @param maxCount The maximum number of characters. When not null a "count/max"
 *   counter is shown at the end and input is truncated at [maxCount], mirroring
 *   COUIInputView couiInputMaxCount. The counter switches to
 *   [TextFieldColors.counterErrorColor] at the limit.
 * @param showClearButton Whether to show a clear (fast delete) button when the
 *   [TextField] is focused and not empty, mirroring COUIEditText quickDelete.
 * @param showPasswordToggle Whether to show an eye toggle that switches the
 *   password visibility, mirroring COUIInputView couiEnablePassword. While the
 *   password is hidden, the text is masked and [visualTransformation] is ignored. The toggle
 *   follows [enabled] (COUIInputView.setEnabled): a disabled toggle is not clickable and uses
 *   [TextFieldColors.disabledIconColor].
 * @param initialPasswordVisible Whether the password starts visible. Defaults to masked, like a
 *   COUIEditText with a password input type; [InputView] passes `true` because COUIInputView
 *   couiPasswordType defaults to OPEN.
 * @param textStyle The text style to be applied to the [TextField]. Defaults to the
 *   COUI input text of the selected [backgroundMode].
 * @param keyboardOptions The keyboard options to be applied to the [TextField].
 * @param keyboardActions The keyboard actions to be applied to the [TextField].
 * @param leadingIcon The leading icon to be displayed in the [TextField].
 * @param trailingIcon The trailing icon to be displayed in the [TextField].
 * @param singleLine Whether the text field is single line.
 * @param maxLines The maximum number of lines allowed to be displayed in [TextField].
 * @param minLines The minimum number of lines allowed to be displayed in [TextField]. It is required
 *   that 1 <= [minLines] <= [maxLines].
 * @param visualTransformation The visual transformation to be applied to the [TextField].
 * @param onTextLayout The callback to be called when the text layout changes.
 * @param interactionSource The interaction source to be applied to the [TextField].
 * @param cursorBrush The brush to be used for the cursor.
 */
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    backgroundMode: TextFieldMode = TextFieldMode.Line,
    insideMargin: DpSize = TextFieldDefaults.insideMargin(backgroundMode),
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    cornerRadius: Dp = TextFieldDefaults.CornerRadius,
    minHeight: Dp = TextFieldDefaults.minHeight(backgroundMode),
    label: String = "",
    useLabelAsPlaceholder: Boolean = true,
    justShowFocusLine: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    maxCount: Int? = null,
    showClearButton: Boolean = false,
    showPasswordToggle: Boolean = false,
    initialPasswordVisible: Boolean = false,
    textStyle: TextStyle = TextFieldDefaults.textStyle(backgroundMode),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(colors.borderColor),
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val labelState = resolveLabelState(label, useLabelAsPlaceholder, value.isNotEmpty(), isFocused && enabled)

    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnTextLayout by rememberUpdatedState(onTextLayout)

    val contentColor = LocalContentColor.current
    val resolvedTextStyle = remember(textStyle, contentColor, enabled, colors.disabledTextColor) {
        val textColor = if (enabled) textStyle.color.takeOrElse { contentColor } else colors.disabledTextColor
        textStyle.copy(color = textColor)
    }

    var passwordVisible by rememberSaveable { mutableStateOf(initialPasswordVisible) }
    val resolvedVisualTransformation = if (showPasswordToggle && !passwordVisible) {
        remember { PasswordVisualTransformation() }
    } else {
        visualTransformation
    }

    // Mirrors the String overload of BasicTextField, but keeps the selection here so the error
    // state can select the whole text (COUIErrorEditTextHelper.setErrorStateEnd).
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)
    SideEffect {
        if (textFieldValue.selection != textFieldValueState.selection ||
            textFieldValue.composition != textFieldValueState.composition
        ) {
            textFieldValueState = textFieldValue
        }
    }
    var lastTextValue by remember(value) { mutableStateOf(value) }
    val currentValue by rememberUpdatedState(value)
    ErrorSelectAllEffect(isError = isError) {
        val latest = currentValue
        textFieldValueState = TextFieldValue(text = latest, selection = TextRange(0, latest.length))
    }
    val handleValueChange: (TextFieldValue) -> Unit = { new ->
        // Truncate instead of rejecting, mirroring COUIInputView subSequence(0, maxCount).
        val limited = if (maxCount != null && new.text.length > maxCount) {
            TextFieldValue(
                text = new.text.take(maxCount),
                selection = TextRange(minOf(new.selection.start, maxCount), minOf(new.selection.end, maxCount)),
            )
        } else {
            new
        }
        textFieldValueState = limited
        val changed = lastTextValue != limited.text
        lastTextValue = limited.text
        if (changed) currentOnValueChange(limited.text)
    }

    ErrorSelectionColors(isError = isError, errorColor = colors.errorColor) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = handleValueChange,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = resolvedTextStyle,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            visualTransformation = resolvedVisualTransformation,
            onTextLayout = currentOnTextLayout,
            interactionSource = interactionSource,
            cursorBrush = cursorBrush,
            decorationBox = @Composable { innerTextField ->
                TextFieldChrome(
                    label = label,
                    labelState = labelState,
                    floatingLabel = !useLabelAsPlaceholder && label.isNotEmpty(),
                    labelNormalFontSize = resolvedTextStyle.fontSize.takeIf { it.isSp }?.value ?: DefaultLabelFontSize,
                    colors = colors,
                    cornerRadius = cornerRadius,
                    insideMargin = insideMargin,
                    minHeight = minHeight,
                    backgroundMode = backgroundMode,
                    justShowFocusLine = justShowFocusLine,
                    enabled = enabled,
                    isError = isError,
                    counterText = maxCount?.let { "${value.length}/$it" },
                    counterOverflow = maxCount != null && value.length >= maxCount,
                    clearButtonVisible = showClearButton && enabled && isFocused && value.isNotEmpty(),
                    onClearText = if (showClearButton) ({ currentOnValueChange("") }) else null,
                    passwordVisible = if (showPasswordToggle) passwordVisible else null,
                    onPasswordToggle = { passwordVisible = !passwordVisible },
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    isFocused = isFocused,
                    innerTextField = innerTextField,
                )
            },
        )
    }
}

private enum class LabelAnimState { Hidden, Placeholder, Normal, Floating }

/**
 * Resolves the visual state of the label.
 *
 * With [useLabelAsPlaceholder] (the COUI HintDisable default) the label is a plain
 * placeholder: visible while the field is empty, gone once text is entered.
 * Otherwise it mirrors COUIEditText couiHintEnabled: the label collapses (floats)
 * as soon as the field is focused or filled
 * (COUIEditText.updateLabelState: `!isEmpty || (isEnabled && hasFocus())`).
 */
private fun resolveLabelState(
    label: String,
    useLabelAsPlaceholder: Boolean,
    hasText: Boolean,
    isFocused: Boolean,
): LabelAnimState = when {
    label.isEmpty() -> LabelAnimState.Hidden
    useLabelAsPlaceholder -> if (hasText) LabelAnimState.Placeholder else LabelAnimState.Normal
    hasText || isFocused -> LabelAnimState.Floating
    else -> LabelAnimState.Normal
}

/** Contains default values used by [TextField]. */
object TextFieldDefaults {
    /** The default corner radius of the [TextField] (COUI coui_textinput_corner_radius). */
    val CornerRadius = 10.dp

    /**
     * The default inside margin of the [TextField] in [TextFieldMode.Rectangle].
     * From COUI Widget.COUI.EditText.HintAnim.Rectangle: paddingLeft 16dp / rectModePaddingTop 12dp /
     * paddingBottom 12dp (COUI paddingRight is 10dp to leave room for the trailing delete icon;
     * the symmetric [DpSize] API keeps 16dp on both sides).
     */
    val InsideMargin = DpSize(16.dp, 12.dp)

    /**
     * The default inside margin of the [TextField] in [TextFieldMode.Line].
     * From COUI Widget.COUI.EditText.HintAnim.Line.HintDisable (the style ColorOS Settings
     * uses for dialog inputs): horizontal padding 0dp,
     * coui_input_edit_text_no_title_padding_top / _bottom 15dp.
     */
    val LineInsideMargin = DpSize(0.dp, 15.dp)

    /**
     * The default inside margin of the [TextField] in [TextFieldMode.None].
     * From COUI Widget.COUI.EditText base style: vertical padding 9dp.
     */
    val NoneInsideMargin = DpSize(0.dp, 9.dp)

    /** Returns the default inside margin for the given [mode]. */
    fun insideMargin(mode: TextFieldMode): DpSize = when (mode) {
        TextFieldMode.Rectangle -> InsideMargin
        TextFieldMode.Line -> LineInsideMargin
        TextFieldMode.None -> NoneInsideMargin
    }

    /**
     * The minimum height of the [TextFieldMode.Line] and [TextFieldMode.Rectangle] fields
     * (COUI Widget.COUI.EditText android:minHeight = coui_edit_text_height_min, inherited by the
     * HintAnim.Line, Line.HintDisable and Rectangle styles).
     */
    val MinHeight = 62.dp

    /**
     * Returns the default minimum height for the given [mode]: [MinHeight] for the decorated
     * modes and 0dp for [TextFieldMode.None], the undecorated field of the Widget.COUI.Input
     * family (card inputs), which resets android:minHeight to 0.
     */
    fun minHeight(mode: TextFieldMode): Dp = if (mode == TextFieldMode.None) 0.dp else MinHeight

    /**
     * The gap above the floating label in [TextFieldMode.Line] (COUI
     * coui_textinput_line_padding_top).
     */
    internal val LineLabelPaddingTop = 10.dp

    /**
     * The gap between the floating label and the text in [TextFieldMode.Line] (COUI
     * coui_textinput_line_padding_middle).
     */
    internal val LineLabelPaddingMiddle = 5.dp

    /**
     * The horizontal room the [TextFieldMode.Rectangle] border cutout leaves around the floating
     * label (COUI coui_textinput_label_cutout_padding).
     */
    internal val LabelCutoutPadding = 4.dp

    /** The character counter font size (COUI coui_input_view input_count: 10sp). */
    val CounterFontSize = 10.sp

    /**
     * The width of the focused underline and of the error border / underline
     * (COUI coui_textinput_focus_stroke_width).
     */
    internal val BorderWidth = 1.dp

    /**
     * The width of the resting underline and of the rect border outside the error state
     * (COUI coui_textinput_stroke_width).
     */
    internal val UnfocusedBorderWidth = 0.33.dp

    /**
     * The label font size when the label is floating above the text
     * (COUI collapsedTextSize = coui_textinput_hint_text_size: 10sp).
     */
    internal val LabelFontSizeFloating = 10.sp

    /** Horizontal amplitude of the error shake animation (COUI coui_edit_text_shake_amplitude). */
    internal val ShakeAmplitude = 5.33.dp

    /**
     * Spacing before the trailing counter and the clear button (COUI
     * coui_input_button_layout_margin_start on input_count / delete_button).
     */
    internal val CounterPadding = 8.dp

    /**
     * Spacing before the password eye (COUI coui_card_input_password_margin_start on
     * checkbox_password).
     */
    internal val PasswordButtonPaddingStart = 2.dp

    /**
     * The end padding of the built-in buttons in [TextFieldMode.Rectangle] (COUI
     * Widget.COUI.EditText.HintAnim.Rectangle android:paddingRight, which the delete icon sits in).
     */
    internal val RectButtonEndPadding = 10.dp

    /**
     * The size of the clear button (ic_edit_text_delete: a 22dp vector with an 18dp circle at
     * 2..20, laid out at its intrinsic size like the COUIEditText compound drawable and the
     * COUIInputView wrap_content delete_button). Touches near it still reach it through the
     * minimum touch target expansion.
     */
    internal val ClearButtonVisualSize = 22.dp

    /** The size of the password eye toggle (COUI coui_inputview_password_size). */
    internal val PasswordIconSize = 28.dp

    /**
     * The default input text style for the given [mode]: 16sp regular
     * (COUI couiEditTextTextAppearance — couiTextAppearanceHeadline6 16sp with fontFamily
     * reset to sans-serif-regular), bold in [TextFieldMode.Rectangle]
     * (Widget.COUI.EditText.HintAnim.Rectangle sets android:textStyle=bold).
     */
    @Composable
    fun textStyle(mode: TextFieldMode): TextStyle {
        val base = COUITheme.textStyles.bodyXL
        return remember(base, mode) {
            if (mode == TextFieldMode.Rectangle) base.copy(fontWeight = FontWeight.Bold) else base
        }
    }

    /**
     * Default colors used by the [TextField].
     *
     * @param labelColor The color of the label / placeholder
     *   (COUI Widget.COUI.EditText android:textColorHint = couiColorLabelSecondary; the collapsed
     *   label couiColorSecondNeutral resolves to the same 54% neutral).
     * @param borderColor The color of the border / focused underline
     *   (COUI couiStrokeColor = couiColorPrimary).
     * @param unfocusedBorderColor The color of the border / underline when the [TextField]
     *   is not focused (COUI couiDefaultStrokeColor = couiColorDivider).
     * @param errorColor The border / underline color and the selection tint in error state
     *   (COUI couiEditTextErrorColor, which defaults to couiColorErrorTextBg: red at 30% alpha in
     *   light mode, opaque in dark mode).
     * @param counterColor The color of the character counter (COUI couiColorHintNeutral).
     * @param counterErrorColor The color of the counter once the text reaches the limit
     *   (COUIInputView sets couiColorError on the count view).
     * @param iconColor The tint of the built-in clear / password buttons (COUI couiColorLabelSecondary).
     * @param disabledIconColor The tint of the password toggle when the [TextField] is disabled
     *   (COUI coui_btn_eye_on_disabled / coui_btn_eye_off_disabled: couiColorLabelQuaternary).
     * @param disabledTextColor The input text color when the [TextField] is disabled
     *   (COUI coui_edit_text_color state_enabled=false -> couiColorDisabledNeutral).
     * @param disabledBorderColor The border / underline and the label color when the [TextField]
     *   is disabled (COUI couiDisabledStrokeColor = coui_textinput_stroke_color_disabled, which
     *   COUIEditText.updateLabelState also applies to the collapsed and expanded hint).
     */
    @Composable
    fun textFieldColors(
        labelColor: Color = COUITheme.colorScheme.labelSecondary,
        borderColor: Color = COUITheme.colorScheme.primary,
        unfocusedBorderColor: Color = COUITheme.colorScheme.divider,
        errorColor: Color = COUITheme.colorScheme.errorTextBg,
        counterColor: Color = COUITheme.colorScheme.labelTertiary,
        counterErrorColor: Color = COUITheme.colorScheme.error,
        iconColor: Color = COUITheme.colorScheme.labelSecondary,
        disabledIconColor: Color = COUITheme.colorScheme.quarternaryNeutral,
        disabledTextColor: Color = COUITheme.colorScheme.disabledNeutral,
        disabledBorderColor: Color = COUITheme.colorScheme.disabledTextFieldBorder,
    ): TextFieldColors = remember(
        labelColor,
        borderColor,
        unfocusedBorderColor,
        errorColor,
        counterColor,
        counterErrorColor,
        iconColor,
        disabledIconColor,
        disabledTextColor,
        disabledBorderColor,
    ) {
        TextFieldColors(
            labelColor = labelColor,
            borderColor = borderColor,
            unfocusedBorderColor = unfocusedBorderColor,
            errorColor = errorColor,
            counterColor = counterColor,
            counterErrorColor = counterErrorColor,
            iconColor = iconColor,
            disabledIconColor = disabledIconColor,
            disabledTextColor = disabledTextColor,
            disabledBorderColor = disabledBorderColor,
        )
    }
}

/**
 * Colors used by a [TextField] in different parts of the component.
 *
 * @param labelColor The color of the label / placeholder.
 * @param borderColor The color of the border / focused underline.
 * @param unfocusedBorderColor The color of the border / underline when the [TextField] is not focused.
 * @param errorColor The border / underline color and the selection tint in error state.
 * @param counterColor The color of the character counter.
 * @param counterErrorColor The color of the character counter once the text reaches the limit.
 * @param iconColor The tint of the built-in clear / password buttons.
 * @param disabledIconColor The tint of the password toggle when the [TextField] is disabled.
 * @param disabledTextColor The input text color when the [TextField] is disabled.
 * @param disabledBorderColor The border / underline and the label color when the [TextField] is
 *   disabled.
 */
@Immutable
data class TextFieldColors(
    val labelColor: Color,
    val borderColor: Color,
    val unfocusedBorderColor: Color,
    val errorColor: Color,
    val counterColor: Color,
    val counterErrorColor: Color,
    val iconColor: Color,
    val disabledIconColor: Color,
    val disabledTextColor: Color,
    val disabledBorderColor: Color,
)

/** Fallback label font size (COUI input text: 16sp) when [TextStyle.fontSize] is not in sp. */
private val DefaultLabelFontSize = 16f

/** COUIEaseInterpolator: cubic-bezier(0.33, 0, 0.67, 1). */
private val CouiEaseEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** COUIInEaseInterpolator: cubic-bezier(0, 0, 0.1, 1). */
private val CouiInEaseEasing = CubicBezierEasing(0f, 0f, 0.1f, 1f)

/** COUIMoveEaseInterpolator: cubic-bezier(0.3, 0, 0.1, 1), used by the label collapse. */
private val CouiMoveEaseEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** Duration of the error color blend (COUIErrorEditTextHelper DURATION_HINT_ANIMATOR). */
private val ErrorColorAnimDuration = 217

/** Duration of the line mode focus underline animation (COUIEditText BACKGROUND_ANIMATION_DURATION). */
private val LineAnimDuration = 250

/** Duration of the label collapse animation (COUIEditText LABEL_SCALE_ANIMATION_DURATION). */
private val LabelAnimDuration = 200

/** Total duration of the error shake animation (COUIErrorEditTextHelper ShakeInterpolator TOTAL_DURATION). */
private val ShakeDuration = 450

/** Duration of the slash drawn over the eye when the password is hidden (coui_btn_eye_off_anim). */
private val PasswordEyeOffAnimDuration = 300

/** Duration of the slash wiped off the eye when the password is shown (coui_btn_eye_on_anim). */
private val PasswordEyeOnAnimDuration = 200

/**
 * `-ascent / textSize` of the collapsed label paint, COUICollapseTextHelper.getCollapsedTextHeight
 * (sans-serif hhea ascent 1900 / 2048).
 */
private val CollapsedLabelAscentRatio = 0.9277f

/**
 * `(descent - ascent) / textSize` of the collapsed label paint, COUICollapseTextHelper.getHintHeight
 * (the 1.1719 sans-serif line height).
 */
private val CollapsedLabelHeightRatio = 1.1719f

/**
 * Selects the whole text once the error animation ends, as
 * COUIErrorEditTextHelper.setErrorStateEnd(true, true, true) does after the 450ms error animator
 * set, so the error-tinted selection ([ErrorSelectionColors]) covers the text.
 */
@Composable
private fun ErrorSelectAllEffect(
    isError: Boolean,
    selectAll: () -> Unit,
) {
    val currentSelectAll by rememberUpdatedState(selectAll)
    LaunchedEffect(isError) {
        if (isError) {
            delay(ShakeDuration.toLong())
            currentSelectAll()
        }
    }
}

/**
 * Tints the text selection with [errorColor] at 30% alpha while [isError] is on, as
 * COUIErrorEditTextHelper.setErrorStateEnd sets the highlight color to the error RGB at alpha 0.3.
 */
@Composable
private fun ErrorSelectionColors(
    isError: Boolean,
    errorColor: Color,
    content: @Composable () -> Unit,
) {
    // Always provide the local, so toggling the error keeps the text field in the same slot.
    val current = LocalTextSelectionColors.current
    val selectionColors = remember(current, isError, errorColor) {
        if (isError) {
            TextSelectionColors(handleColor = current.handleColor, backgroundColor = errorColor.copy(alpha = ErrorSelectionAlpha))
        } else {
            current
        }
    }
    CompositionLocalProvider(LocalTextSelectionColors provides selectionColors, content = content)
}

/** COUIErrorEditTextHelper selection mask alpha. */
private val ErrorSelectionAlpha = 0.3f

/** Masks every character with a bullet, used while the password toggle hides the text. */
private object PasswordMaskOutputTransformation : OutputTransformation {
    override fun TextFieldBuffer.transformOutput() {
        val masked = "•".repeat(length)
        replace(0, length, masked)
    }
}

/**
 * Builds the shared chrome (background / underline, animated border, label, error shake,
 * counter, clear / password buttons, leading/trailing icons, padding) around the inner text field.
 * Centralizes derived state + animations so the three [TextField] overloads only differ in the
 * [BasicTextField] call.
 */
@Composable
private fun TextFieldChrome(
    label: String,
    labelState: LabelAnimState,
    floatingLabel: Boolean,
    labelNormalFontSize: Float,
    colors: TextFieldColors,
    cornerRadius: Dp,
    insideMargin: DpSize,
    minHeight: Dp,
    backgroundMode: TextFieldMode,
    justShowFocusLine: Boolean,
    enabled: Boolean,
    isError: Boolean,
    counterText: String?,
    counterOverflow: Boolean,
    clearButtonVisible: Boolean,
    onClearText: (() -> Unit)?,
    passwordVisible: Boolean?,
    onPasswordToggle: () -> Unit,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    isFocused: Boolean,
    innerTextField: @Composable () -> Unit,
) {
    // Error blend (COUIErrorEditTextHelper.mHintColorChangeProgress): when the error turns on, the
    // border / underline blends from its current color to the error color over 217ms with
    // COUIEaseInterpolator (getGradientColor, a per-channel ARGB blend); when it turns off,
    // setErrorStateEnd(false) restores the normal drawing at once.
    val errorProgress = remember { Animatable(if (isError) 1f else 0f) }
    LaunchedEffect(isError) {
        if (!isError) {
            errorProgress.snapTo(0f)
        } else if (errorProgress.value < 1f) {
            errorProgress.snapTo(0f)
            errorProgress.animateTo(1f, tween(ErrorColorAnimDuration, easing = CouiEaseEasing))
        }
    }
    val errorActive = isError && enabled
    // The error stroke is the 1dp couiFocusStrokeWidth from the start of the blend
    // (COUIErrorEditTextHelper.init gets mStrokeWidthFocused); outside the error state the rect
    // border keeps the 0.33dp couiStrokeWidth even when focused, only its color changes.
    val borderWidth = if (errorActive) TextFieldDefaults.BorderWidth else TextFieldDefaults.UnfocusedBorderWidth
    // Rectangle mode: the stroke color switches at once with the focus and enabled state
    // (COUIEditText.updateTextInputBoxState -> applyBoxAttributes); disabled fields use
    // couiDisabledStrokeColor.
    val rectStrokeColor = when {
        !enabled -> colors.disabledBorderColor
        isFocused -> colors.borderColor
        else -> colors.unfocusedBorderColor
    }
    // Line mode: base underline (0.33dp, the 1dp focused width in error state) plus a focused
    // overlay expanding from the start edge. With justShowFocusLine (COUIInputPreference
    // couiJustShowFocusLine) the resting underline is not drawn. A disabled field draws the resting
    // underline with couiDisabledStrokeColor (COUIEditText.draw).
    val underlineBaseColor = if (enabled) colors.unfocusedBorderColor else colors.disabledBorderColor
    val lineFraction = remember { Animatable(if (isFocused) 1f else 0f) }
    val lineAlpha = remember { Animatable(if (isFocused) 1f else 0f) }
    if (backgroundMode == TextFieldMode.Line) {
        LaunchedEffect(isFocused, enabled) {
            if (isFocused && enabled) {
                // COUIEditText animateToShowBackground: alpha snaps to 255, the focused line
                // expands from 0 to full width in 250ms with COUIInEaseInterpolator.
                lineAlpha.snapTo(1f)
                lineFraction.snapTo(0f)
                lineFraction.animateTo(1f, tween(LineAnimDuration, easing = CouiInEaseEasing))
            } else if (!enabled) {
                // COUIEditText updateLineModeBackground: disabled resets mDrawXProgress to 0.
                lineAlpha.snapTo(0f)
                lineFraction.snapTo(0f)
            } else {
                // COUIEditText.draw only paints the focused line while hasFocus(), so it is gone as
                // soon as the field loses focus (the animateToHideBackground fade is never visible).
                lineAlpha.snapTo(0f)
            }
        }
    }
    // Error shake: COUIErrorEditTextHelper.ShakeInterpolator keyframes
    // offsets {0, -1, 0.5, -0.5, 0} x amplitude at {0, 83, 216, 333, 450}ms,
    // eased between keyframes with COUIEaseInterpolator.
    // The shake only plays when the field is focused as the error turns on
    // (COUIErrorEditTextHelper mIsFocusedAtAnimateBeginning).
    val shakeOffset = remember { Animatable(0f) }
    val currentIsFocused by rememberUpdatedState(isFocused)
    LaunchedEffect(isError) {
        if (isError && currentIsFocused) {
            shakeOffset.snapTo(0f)
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = ShakeDuration
                    0f at 0 using CouiEaseEasing
                    -1f at 83 using CouiEaseEasing
                    0.5f at 216 using CouiEaseEasing
                    -0.5f at 333 using CouiEaseEasing
                    0f at ShakeDuration
                },
            )
        } else {
            shakeOffset.snapTo(0f)
        }
    }

    // COUI never tints the label in error state: COUIErrorEditTextHelper.drawCollapseText has
    // no caller, so the hint keeps its color. A disabled field draws the collapsed and expanded
    // hint in couiDisabledStrokeColor, set at once by COUIEditText.updateLabelState.
    val labelColor = if (enabled) colors.labelColor else colors.disabledBorderColor
    // Label collapse: 200ms with COUIMoveEaseInterpolator
    // (COUIEditText LABEL_SCALE_ANIMATION_DURATION / mPathInterpolator1).
    val labelFraction = animateFloatAsState(
        if (labelState == LabelAnimState.Floating) 1f else 0f,
        animationSpec = tween(LabelAnimDuration, easing = CouiMoveEaseEasing),
    )
    // COUICollapseTextHelper draws a collapsing label at its expanded size and scales the canvas
    // (mScale = size / expandedTextSize, the size interpolated linearly); only once it rests at the
    // collapsed size is it laid out at that size. The label is therefore measured at the expanded
    // size while it moves and scaled in its layer, so the animation never recomposes per frame.
    val labelCollapsedAtRest by remember { derivedStateOf { labelFraction.value >= 1f } }
    val labelMeasuredFontSize = if (labelCollapsedAtRest) TextFieldDefaults.LabelFontSizeFloating.value else labelNormalFontSize
    val labelScale: () -> Float = {
        val size = labelNormalFontSize + (TextFieldDefaults.LabelFontSizeFloating.value - labelNormalFontSize) * labelFraction.value
        size / labelMeasuredFontSize
    }

    // Vertical geometry. With the HintAnim floating label COUIEditText.getModePaddingTop reserves
    // room above the text: in line mode coui_textinput_line_padding_top + the collapsed label
    // height (getHintHeight) + coui_textinput_line_padding_middle, with the collapsed label at
    // padding_top; in rect mode rectModePaddingTop + half the collapsed label ascent
    // (getCollapsedTextHeight), with the border starting half that ascent below the top
    // (getBoundsTop) and the collapsed label at 0 (calculateCollapsedTextTopBounds).
    val density = LocalDensity.current
    val collapsedAscent = with(density) {
        (TextFieldDefaults.LabelFontSizeFloating.toPx() * CollapsedLabelAscentRatio).toDp()
    }
    val collapsedLabelHeight = with(density) {
        (TextFieldDefaults.LabelFontSizeFloating.toPx() * CollapsedLabelHeightRatio).toDp()
    }
    val labelGeometry = when {
        floatingLabel && backgroundMode == TextFieldMode.Rectangle -> LabelGeometry(
            contentTop = insideMargin.height + collapsedAscent / 2,
            collapsedTop = 0.dp,
            borderTop = collapsedAscent / 2,
        )

        floatingLabel && backgroundMode == TextFieldMode.Line -> LabelGeometry(
            contentTop = TextFieldDefaults.LineLabelPaddingTop + collapsedLabelHeight + TextFieldDefaults.LineLabelPaddingMiddle,
            collapsedTop = TextFieldDefaults.LineLabelPaddingTop,
            borderTop = 0.dp,
        )

        // The undecorated mode keeps its padding: the floating label moves up and the text down
        // by half the vertical inside margin.
        else -> LabelGeometry(
            contentTop = insideMargin.height,
            collapsedTop = null,
            borderTop = 0.dp,
        )
    }
    val textOffset = if (labelGeometry.collapsedTop == null && labelState == LabelAnimState.Floating) {
        insideMargin.height / 2
    } else {
        0.dp
    }
    // COUIEditText.openCutout / closeCutout: the rect border is cut around the collapsed label as
    // soon as the label starts collapsing, and closed as soon as it starts expanding.
    val cutout = remember { LabelCutout() }
    val cutoutOpen = floatingLabel && backgroundMode == TextFieldMode.Rectangle && labelState == LabelAnimState.Floating
    // In rect mode COUI sets sans-serif-medium on the collapse helper's paint itself
    // (COUIEditText.initHintMode: setTypefaces for couiBackgroundMode == 2 ->
    // COUIChangeTextUtil.adaptBoldAndMediumFont), so the label is medium both expanded and
    // collapsed; the other modes keep the regular hint.
    val labelFontWeight = if (backgroundMode == TextFieldMode.Rectangle) FontWeight.Medium else FontWeight.Normal

    val backgroundModifier = when (backgroundMode) {
        // A stroke-only round rect (GradientDrawable / COUICutoutDrawable with no fill and plain
        // circular corners), inset by half the stroke like GradientDrawable, starting at the
        // border top and cut open around the collapsed label.
        TextFieldMode.Rectangle -> Modifier.drawBehind {
            val strokePx = borderWidth.toPx()
            val half = strokePx / 2f
            val top = labelGeometry.borderTop.toPx()
            val boxWidth = size.width - strokePx
            val boxHeight = size.height - top - strokePx
            if (boxWidth <= 0f || boxHeight <= 0f) return@drawBehind
            val radius = (cornerRadius.toPx() - half).coerceAtLeast(0f)
            val strokeColor = if (errorActive) {
                lerpArgb(rectStrokeColor, colors.errorColor, errorProgress.value)
            } else {
                rectStrokeColor
            }
            val drawBorder: DrawScope.() -> Unit = {
                drawRoundRect(
                    color = strokeColor,
                    topLeft = Offset(half, top + half),
                    size = Size(boxWidth, boxHeight),
                    cornerRadius = CornerRadius(radius),
                    style = Stroke(width = strokePx),
                )
            }
            if (cutoutOpen && cutout.right > cutout.left) {
                val left = if (layoutDirection == LayoutDirection.Rtl) size.width - cutout.right else cutout.left
                val right = if (layoutDirection == LayoutDirection.Rtl) size.width - cutout.left else cutout.right
                clipRect(left = left, top = 0f, right = right, bottom = top + strokePx * 2, clipOp = ClipOp.Difference) {
                    drawBorder()
                }
            } else {
                drawBorder()
            }
        }

        TextFieldMode.Line -> Modifier.drawBehind {
            val blend = if (errorActive) errorProgress.value else 0f
            if (!justShowFocusLine || errorActive) {
                // COUIErrorEditTextHelper.drawModeBackgroundLine: the resting line, 1dp in error
                // state, blended towards the error color.
                val baseWidth = borderWidth.toPx()
                drawRect(
                    color = lerpArgb(underlineBaseColor, colors.errorColor, blend),
                    topLeft = Offset(0f, size.height - baseWidth),
                    size = Size(size.width, baseWidth),
                )
            }
            val alpha = lineAlpha.value
            if (alpha > 0f) {
                val focusWidth = TextFieldDefaults.BorderWidth.toPx()
                drawRect(
                    color = lerpArgb(colors.borderColor, colors.errorColor, blend),
                    topLeft = Offset(0f, size.height - focusWidth),
                    size = Size(size.width * lineFraction.value, focusWidth),
                    alpha = alpha,
                )
            }
        }

        TextFieldMode.None -> Modifier
    }

    val hasBuiltinTrailing = counterText != null || onClearText != null || passwordVisible != null
    val hasBuiltinButtons = onClearText != null || passwordVisible != null
    val builtinTrailing: (@Composable () -> Unit)? = if (!hasBuiltinTrailing) {
        null
    } else {
        {
            // The buttons sit at the field end: flush in the line / bare modes (paddingRight 0)
            // and within the 10dp paddingRight of the rect mode.
            val clusterEndPadding = when {
                !hasBuiltinButtons -> insideMargin.width
                backgroundMode == TextFieldMode.Rectangle -> minOf(TextFieldDefaults.RectButtonEndPadding, insideMargin.width)
                else -> 0.dp
            }
            Row(
                modifier = Modifier.padding(end = clusterEndPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (counterText != null) {
                    // COUIInputView switches the count view to couiColorError at the limit
                    // without an animation.
                    Text(
                        text = counterText,
                        fontSize = TextFieldDefaults.CounterFontSize,
                        color = if (counterOverflow) colors.counterErrorColor else colors.counterColor,
                        modifier = Modifier.padding(start = TextFieldDefaults.CounterPadding),
                    )
                }
                // The clear button pops in and out with no transition: COUIEditText posts
                // setCompoundDrawables and COUIInputView.updateDeleteButton flips the visibility.
                if (onClearText != null && clearButtonVisible) {
                    TextFieldClearButton(
                        color = colors.iconColor,
                        onClear = onClearText,
                        modifier = Modifier.padding(start = TextFieldDefaults.CounterPadding),
                    )
                }
                if (passwordVisible != null) {
                    TextFieldPasswordToggle(
                        visible = passwordVisible,
                        enabled = enabled,
                        tint = if (enabled) colors.iconColor else colors.disabledIconColor,
                        onToggle = onPasswordToggle,
                        modifier = Modifier.padding(start = TextFieldDefaults.PasswordButtonPaddingStart),
                    )
                }
            }
        }
    }

    val hasTrailingContent = trailingIcon != null || hasBuiltinTrailing
    val contentPadding = PaddingValues(
        start = if (leadingIcon == null) insideMargin.width else 0.dp,
        top = labelGeometry.contentTop,
        end = if (hasTrailingContent) 0.dp else insideMargin.width,
        bottom = insideMargin.height,
    )

    TextFieldDecorationBox(
        label = label,
        labelVisible = labelState != LabelAnimState.Hidden && labelState != LabelAnimState.Placeholder,
        labelFontSize = labelMeasuredFontSize,
        labelFontWeight = labelFontWeight,
        labelColor = labelColor,
        labelFraction = { labelFraction.value },
        labelScale = labelScale,
        collapsedLabelTop = labelGeometry.collapsedTop,
        legacyFloatOffset = insideMargin.height / 2,
        textOffset = textOffset,
        shakeOffset = { shakeOffset.value },
        minHeight = minHeight,
        contentPadding = contentPadding,
        cutout = cutout,
        backgroundModifier = backgroundModifier,
        leadingIcon = leadingIcon,
        builtinTrailing = builtinTrailing,
        trailingIcon = trailingIcon,
        innerTextField = innerTextField,
    )
}

/**
 * The vertical geometry of the field content and its label.
 *
 * @param contentTop The padding above the text.
 * @param collapsedTop The top of the floating label in the field, or `null` to float it half the
 *   vertical inside margin above the text start.
 * @param borderTop The top of the [TextFieldMode.Rectangle] border.
 */
@Immutable
private class LabelGeometry(
    val contentTop: Dp,
    val collapsedTop: Dp?,
    val borderTop: Dp,
)

/**
 * The horizontal span (in px, left-to-right) of the [TextFieldMode.Rectangle] border cutout around
 * the collapsed label. Written while the decoration is placed and read when its border is drawn,
 * which always follows in the same frame.
 */
private class LabelCutout {
    var left = 0f
    var right = 0f
}

/**
 * Lays out leading icon, padded field, built-in trailing cluster and trailing icon in a row, each
 * centered vertically in a box at least [minHeight] tall (EditText gravity center_vertical within
 * its min height), and places the label: expanded on the text start, collapsed at
 * [collapsedLabelTop] (or [legacyFloatOffset] above the text start when null), interpolated by
 * [labelFraction]. The label is measured at [labelFontSize] and scaled by [labelScale] from its
 * top start corner. Records the collapsed label span in [cutout] for the rect border.
 */
@Composable
private fun TextFieldDecorationBox(
    label: String,
    labelVisible: Boolean,
    labelFontSize: Float,
    labelFontWeight: FontWeight,
    labelColor: Color,
    labelFraction: () -> Float,
    labelScale: () -> Float,
    collapsedLabelTop: Dp?,
    legacyFloatOffset: Dp,
    textOffset: Dp,
    shakeOffset: () -> Float,
    minHeight: Dp,
    contentPadding: PaddingValues,
    cutout: LabelCutout,
    leadingIcon: @Composable (() -> Unit)?,
    builtinTrailing: (@Composable () -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    backgroundModifier: Modifier = Modifier,
    innerTextField: @Composable () -> Unit,
) {
    Layout(
        modifier = backgroundModifier,
        content = {
            if (leadingIcon != null) {
                Box(modifier = Modifier.layoutId(DecorationSlot.Leading), contentAlignment = Alignment.Center) { leadingIcon() }
            }
            Box(
                modifier = Modifier
                    .layoutId(DecorationSlot.Field)
                    .padding(contentPadding),
                contentAlignment = Alignment.TopStart,
            ) {
                Box(
                    modifier = Modifier
                        .offset(y = textOffset)
                        .offset {
                            // COUIErrorEditTextHelper only shakes the text canvas, not the box.
                            IntOffset((shakeOffset() * TextFieldDefaults.ShakeAmplitude.toPx()).roundToInt(), 0)
                        },
                    contentAlignment = Alignment.CenterStart,
                ) {
                    innerTextField()
                }
            }
            if (builtinTrailing != null) {
                Box(modifier = Modifier.layoutId(DecorationSlot.BuiltinTrailing), contentAlignment = Alignment.Center) { builtinTrailing() }
            }
            if (trailingIcon != null) {
                Box(modifier = Modifier.layoutId(DecorationSlot.Trailing), contentAlignment = Alignment.Center) { trailingIcon() }
            }
            if (labelVisible) {
                // Scaled from its start edge, which stays on the text start while it moves.
                val labelOrigin = if (LocalLayoutDirection.current == LayoutDirection.Rtl) LabelOriginRtl else LabelOriginLtr
                Text(
                    text = label,
                    fontSize = labelFontSize.sp,
                    color = labelColor,
                    fontWeight = labelFontWeight,
                    modifier = Modifier
                        .layoutId(DecorationSlot.Label)
                        .graphicsLayer {
                            val scale = labelScale()
                            scaleX = scale
                            scaleY = scale
                            transformOrigin = labelOrigin
                        },
                    textAlign = TextAlign.Start,
                )
            }
        },
    ) { measurables, constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val leading = measurables.firstOrNull { it.layoutId == DecorationSlot.Leading }?.measure(loose)
        val builtin = measurables.firstOrNull { it.layoutId == DecorationSlot.BuiltinTrailing }?.measure(loose)
        val trailing = measurables.firstOrNull { it.layoutId == DecorationSlot.Trailing }?.measure(loose)
        val sideWidth = (leading?.width ?: 0) + (builtin?.width ?: 0) + (trailing?.width ?: 0)
        val fieldMeasurable = measurables.first { it.layoutId == DecorationSlot.Field }
        val field = if (constraints.hasBoundedWidth) {
            val fieldWidth = (constraints.maxWidth - sideWidth).coerceAtLeast(0)
            fieldMeasurable.measure(loose.copy(minWidth = fieldWidth, maxWidth = fieldWidth))
        } else {
            fieldMeasurable.measure(loose)
        }
        val padStart = contentPadding.calculateStartPadding(layoutDirection).roundToPx()
        val padEnd = contentPadding.calculateEndPadding(layoutDirection).roundToPx()
        val padTop = contentPadding.calculateTopPadding().roundToPx()
        val labelPlaceable = measurables.firstOrNull { it.layoutId == DecorationSlot.Label }
            ?.measure(Constraints(maxWidth = (field.width - padStart - padEnd).coerceAtLeast(0)))

        val width = constraints.constrainWidth(
            if (constraints.hasBoundedWidth) constraints.maxWidth else sideWidth + field.width,
        )
        val contentHeight = maxOf(leading?.height ?: 0, field.height, builtin?.height ?: 0, trailing?.height ?: 0)
        val height = constraints.constrainHeight(maxOf(contentHeight, minHeight.roundToPx()))

        layout(width, height) {
            var x = 0
            leading?.let {
                it.placeRelative(x, (height - it.height) / 2)
                x += it.width
            }
            val fieldX = x
            val fieldY = (height - field.height) / 2
            field.placeRelative(fieldX, fieldY)
            x += field.width
            builtin?.let {
                it.placeRelative(x, (height - it.height) / 2)
                x += it.width
            }
            trailing?.let { it.placeRelative(x, (height - it.height) / 2) }

            if (labelPlaceable != null) {
                val labelX = fieldX + padStart
                val expandedY = fieldY + padTop
                val collapsedY = collapsedLabelTop?.roundToPx() ?: (expandedY - legacyFloatOffset.roundToPx())
                val fraction = labelFraction()
                labelPlaceable.placeRelative(labelX, (expandedY + (collapsedY - expandedY) * fraction).roundToInt())
                // The cutout spans the collapsed label bounds (the label width scaled to the
                // collapsed size) plus coui_textinput_label_cutout_padding on both sides.
                val collapsedWidth = labelPlaceable.width * TextFieldDefaults.LabelFontSizeFloating.value / labelFontSize
                val cutoutPadding = TextFieldDefaults.LabelCutoutPadding.toPx()
                cutout.left = labelX - cutoutPadding
                cutout.right = labelX + collapsedWidth + cutoutPadding
            } else {
                cutout.left = 0f
                cutout.right = 0f
            }
        }
    }
}

/** The slots of [TextFieldDecorationBox]. */
private enum class DecorationSlot { Leading, Field, BuiltinTrailing, Trailing, Label }

/**
 * Clear (fast delete) button: the 22dp ic_edit_text_delete icon laid out at its intrinsic size, a
 * 9dp-radius circle tinted couiColorLabelSecondary with the cross knocked out of it, so the surface
 * behind shows through the cross. The cross arms are 1.6dp round-capped strokes whose cap centers
 * sit 2.97dp from the center on each axis. Like the COUIEditText compound drawable and the
 * COUIInputView delete_button (background @null), pressing it shows no feedback.
 */
@Composable
private fun TextFieldClearButton(
    color: Color,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentOnClear by rememberUpdatedState(onClear)
    Canvas(
        modifier = modifier
            .size(TextFieldDefaults.ClearButtonVisualSize)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
            ) { currentOnClear() }
            .semantics { contentDescription = "Clear text" }
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
    ) {
        // ic_edit_text_delete: 22dp viewport, circle centered at (11, 11) with radius 9.
        val s = size.width / 22f
        drawCircle(color = color, radius = 9f * s)
        val arm = 2.97f * s
        val stroke = 1.6f * s
        // BlendMode.Clear punches the cross out of the circle; the color only feeds the mask.
        drawLine(
            color = color,
            start = Offset(center.x - arm, center.y - arm),
            end = Offset(center.x + arm, center.y + arm),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
            blendMode = BlendMode.Clear,
        )
        drawLine(
            color = color,
            start = Offset(center.x - arm, center.y + arm),
            end = Offset(center.x + arm, center.y - arm),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
            blendMode = BlendMode.Clear,
        )
    }
}

/**
 * The size of the password eye toggle of a [TextField]: [TextFieldDefaults.PasswordIconSize]
 * (coui_inputview_password_size). [LockScreenPasswordInput] provides the 34dp intrinsic size of
 * the coui_btn_eye drawables, which its wrap_content checkbox_password uses.
 */
internal val LocalTextFieldPasswordToggleSize = staticCompositionLocalOf { TextFieldDefaults.PasswordIconSize }

/**
 * Password visibility toggle: the eye icon redrawn from the coui_btn_eye_on / coui_btn_eye_off
 * 34dp vectors at [LocalTextFieldPasswordToggleSize], tinted couiColorLabelSecondary
 * (couiColorLabelQuaternary and not clickable when disabled, coui_btn_eye_*_disabled), with no
 * press feedback (checkbox_password background @null). Toggling replays the transitions of the
 * coui_edittext_password_icon animated-selector with the (0.3, 0, 0.1, 1) path interpolator:
 * hiding the password draws the slash from its top start to its end in 300ms, cutting its gap in
 * the eye as it goes (coui_btn_eye_off_anim); showing it wipes the slash and the gap away in the
 * same direction in 200ms (coui_btn_eye_on_anim).
 */
@Composable
private fun TextFieldPasswordToggle(
    visible: Boolean,
    enabled: Boolean,
    tint: Color,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentOnToggle by rememberUpdatedState(onToggle)
    val description = if (visible) "Hide password" else "Show password"
    // The slash covers [slashTail, slashHead] of its line, as fractions from its top start: all of
    // it while the password is hidden, none of it while shown.
    val slashTail = remember { Animatable(if (visible) 1f else 0f) }
    val slashHead = remember { Animatable(1f) }
    LaunchedEffect(visible) {
        // The transitions are not reversible: a toggle mid-way first jumps to the end of the
        // running one, as AnimatedStateListDrawable does.
        if (visible) {
            if (slashTail.value >= 1f) return@LaunchedEffect
            slashHead.snapTo(1f)
            slashTail.animateTo(1f, tween(PasswordEyeOnAnimDuration, easing = CouiMoveEaseEasing))
        } else {
            if (slashTail.value <= 0f && slashHead.value >= 1f) return@LaunchedEffect
            slashTail.snapTo(0f)
            slashHead.snapTo(0f)
            slashHead.animateTo(1f, tween(PasswordEyeOffAnimDuration, easing = CouiMoveEaseEasing))
        }
    }
    Canvas(
        modifier = modifier
            .size(LocalTextFieldPasswordToggleSize.current)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled,
                role = Role.Button,
            ) { currentOnToggle() }
            .semantics { contentDescription = description },
    ) {
        // Geometry from the 34dp coui_btn_eye_on vector: almond eye from (6,17) to (28,17)
        // with cubic bulges to y 27.716 / 6.286, pupil ring r 5.02 and dot r 3.016 at (17,17).
        val s = size.width / 34f
        val eyeCenter = Offset(17f * s, 17f * s)
        val eye = Path().apply {
            fillType = PathFillType.EvenOdd
            moveTo(6f * s, 17f * s)
            cubicTo(12f * s, 27.716f * s, 22f * s, 27.716f * s, 28f * s, 17f * s)
            cubicTo(22f * s, 6.286f * s, 12f * s, 6.286f * s, 6f * s, 17f * s)
            close()
            addOval(Rect(center = eyeCenter, radius = 5.02f * s))
            addOval(Rect(center = eyeCenter, radius = 3.016f * s))
        }
        val tail = slashTail.value
        val head = slashHead.value
        if (head <= tail) {
            drawPath(eye, color = tint)
        } else {
            // coui_btn_eye_off: the same eye cut along a 45-degree slash from (6.7,6.7) to
            // (27.2,27.3) with a small gap, plus the 2dp rounded slash stroke. While animating,
            // only the [tail, head] part of the slash and of the gap it cuts is drawn.
            val lineStart = Offset(6.7f * s, 6.73f * s)
            val lineVector = Offset(27.24f * s, 27.27f * s) - lineStart
            val slashStart = lineStart + lineVector * tail
            val slashEnd = lineStart + lineVector * head
            val normal = Offset(-0.7071f, 0.7071f)
            val halfBand = 2.8f * s
            val band = Path().apply {
                moveTo(slashStart.x + normal.x * halfBand, slashStart.y + normal.y * halfBand)
                lineTo(slashEnd.x + normal.x * halfBand, slashEnd.y + normal.y * halfBand)
                lineTo(slashEnd.x - normal.x * halfBand, slashEnd.y - normal.y * halfBand)
                lineTo(slashStart.x - normal.x * halfBand, slashStart.y - normal.y * halfBand)
                close()
            }
            clipPath(band, clipOp = ClipOp.Difference) {
                drawPath(eye, color = tint)
            }
            drawLine(
                color = tint,
                start = slashStart,
                end = slashEnd,
                strokeWidth = 2f * s,
                cap = StrokeCap.Round,
            )
        }
    }
}

/**
 * Blends [start] into [stop] channel by channel, like COUIErrorEditTextHelper.getGradientColor
 * (a straight ARGB interpolation, not the perceptual blend of [androidx.compose.ui.graphics.lerp]).
 */
private fun lerpArgb(start: Color, stop: Color, fraction: Float): Color = when {
    fraction <= 0f -> start

    fraction >= 1f -> stop

    else -> Color(
        red = start.red + (stop.red - start.red) * fraction,
        green = start.green + (stop.green - start.green) * fraction,
        blue = start.blue + (stop.blue - start.blue) * fraction,
        alpha = start.alpha + (stop.alpha - start.alpha) * fraction,
    )
}

/** The scale origin of the label: its top start corner. */
private val LabelOriginLtr = TransformOrigin(0f, 0f)
private val LabelOriginRtl = TransformOrigin(1f, 0f)
