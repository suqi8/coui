// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.InputViewDefaults
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextField
import io.github.suqi8.coui.kmp.basic.TextFieldColors
import io.github.suqi8.coui.kmp.basic.TextFieldDefaults
import io.github.suqi8.coui.kmp.basic.TextFieldMode
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A card list row that holds an inline single-line input field, mirroring COUIInputPreference.
 *
 * COUI inflates `coui_input_preference.xml`: a `COUICardListSelectedItemLayout` (which never shows
 * a press state, `consumeDispatchingEventForState(true)`) padded 32dp from the screen edges, i.e.
 * 16dp inside the card, around a `COUICardListItemInputView`. That input view puts the optional
 * [title] above the field (couiTextAppearanceHeadline6, 10dp top / 4dp bottom padding, 22dp min
 * height) and draws the field as a `COUIScrolledEditText` in `Widget.COUI.InputPreference`
 * (couiTextBodyL, single line, no delete icon) whose underline only appears while focused
 * (`couiJustShowFocusLine`, default true). `onBindViewHolder` switches the field to
 * MODE_BACKGROUND_NO_LINE on the last row of a card group, so the tail and single rows never draw
 * a line.
 *
 * Field paddings follow `COUICardListItemInputView`: 13dp / 13dp without a title
 * (coui_input_edit_text_no_title_padding_*_inPreference), 0dp / 11dp below a title
 * (coui_input_edit_text_has_title_padding_top / coui_input_edit_error_text_has_title_padding_bottom).
 *
 * @param value The text of the input field.
 * @param onValueChange The callback when the text changes.
 * @param modifier The modifier to be applied to the [InputPreference].
 * @param title The title above the field. Hidden when null or empty.
 * @param hint The hint shown while the field is empty.
 * @param enabled Whether the [InputPreference] is enabled.
 * @param readOnly Whether the field is read-only.
 * @param justShowFocusLine Whether to hide the resting underline and show only the focused line
 *   (COUI couiJustShowFocusLine). Has no effect on the last row of a card group, which never draws
 *   a line.
 * @param maxCount The maximum number of characters. When not null a "count/max" counter is shown
 *   at the end of the field and longer input is truncated.
 * @param showClearButton Whether to show the clear button while the field is focused and not
 *   empty. COUI turns it off for the preference field (`setShowDeleteIcon(false)`).
 * @param showPasswordToggle Whether to show the password eye toggle.
 * @param errorMessage The error text below the field. Empty hides the error line. COUI draws no list
 *   divider below a row in error state (`COUIInputPreference.drawDivider`), so skip that divider too.
 * @param colors The [InputPreferenceColors] of the title and the error line.
 * @param textFieldColors The [TextFieldColors] of the field.
 * @param textStyle The text style of the input text.
 * @param keyboardOptions The keyboard options of the field.
 * @param keyboardActions The keyboard actions of the field.
 * @param visualTransformation The visual transformation of the field.
 * @param insideMargin The horizontal margin between the card edge and the content.
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra
 *   padding, and the last row ([CardListPosition.Tail] / [CardListPosition.Full]) draws no line.
 * @param interactionSource The interaction source of the field.
 */
@Composable
fun InputPreference(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    hint: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    justShowFocusLine: Boolean = true,
    maxCount: Int? = null,
    showClearButton: Boolean = false,
    showPasswordToggle: Boolean = false,
    errorMessage: String = "",
    colors: InputPreferenceColors = InputPreferenceDefaults.inputPreferenceColors(),
    textFieldColors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    textStyle: TextStyle = InputViewDefaults.textStyle(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    insideMargin: PaddingValues = InputPreferenceDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    interactionSource: MutableInteractionSource? = null,
) {
    val hasTitle = !title.isNullOrEmpty()
    val isError = errorMessage.isNotEmpty()
    // COUIInputPreference.onBindViewHolder: positions 3 (tail) and 4 (full) switch the field to
    // MODE_BACKGROUND_NO_LINE.
    val isLastRow = cardListPosition == CardListPosition.Tail || cardListPosition == CardListPosition.Full
    val backgroundMode = if (isLastRow) TextFieldMode.None else TextFieldMode.Line
    val extraPaddingTop = if (cardListPosition == CardListPosition.Head || cardListPosition == CardListPosition.Full) {
        CardListPosition.HeadOrTailPadding
    } else {
        0.dp
    }
    val extraPaddingBottom = if (isLastRow) CardListPosition.HeadOrTailPadding else 0.dp

    // Keep the last message so the fade-out still shows it (COUIInputView hides the error view
    // only after its disappear animation). Recorded after composition, so no state is written
    // during it.
    var lastError by remember { mutableStateOf(errorMessage) }
    if (isError) SideEffect { lastError = errorMessage }
    val displayedError = if (isError) errorMessage else lastError

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(insideMargin)
            .padding(top = extraPaddingTop, bottom = extraPaddingBottom),
    ) {
        if (!title.isNullOrEmpty()) {
            Text(
                text = title,
                style = InputViewDefaults.titleTextStyle(),
                color = colors.titleColor(enabled),
                modifier = Modifier
                    .padding(
                        top = InputPreferenceDefaults.TitlePaddingTop,
                        bottom = InputPreferenceDefaults.TitlePaddingBottom,
                    )
                    .defaultMinSize(minHeight = InputPreferenceDefaults.TitleMinHeight),
            )
        }
        // TextField pads its field symmetrically; with a title COUI pads 0dp above / 11dp below, so
        // pad 11dp on both sides and trim the top 11dp off the measured height.
        val fieldPadding = if (hasTitle) {
            InputPreferenceDefaults.FieldPaddingBottomWithTitle
        } else {
            InputPreferenceDefaults.FieldPaddingVertical
        }
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .then(if (hasTitle) Modifier.trimTop(fieldPadding) else Modifier),
            backgroundMode = backgroundMode,
            insideMargin = DpSize(0.dp, fieldPadding),
            colors = textFieldColors,
            label = hint,
            justShowFocusLine = justShowFocusLine,
            enabled = enabled,
            readOnly = readOnly,
            isError = isError,
            maxCount = maxCount,
            showClearButton = showClearButton,
            showPasswordToggle = showPasswordToggle,
            textStyle = textStyle,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
        )
        AnimatedVisibility(
            visible = isError,
            enter = fadeIn(tween(durationMillis = ERROR_APPEAR_DURATION, easing = CouiEaseEasing)),
            exit = fadeOut(tween(durationMillis = ERROR_DISAPPEAR_DURATION, easing = CouiEaseEasing)),
        ) {
            Text(
                text = displayedError,
                fontSize = InputPreferenceDefaults.ErrorFontSize,
                color = colors.errorColor,
                modifier = Modifier.padding(vertical = InputPreferenceDefaults.ErrorPaddingVertical),
            )
        }
    }
}

/** Measures the content normally but reports [trim] less height, cutting it off the top. */
private fun Modifier.trimTop(trim: Dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val trimPx = trim.roundToPx().coerceAtMost(placeable.height)
    layout(placeable.width, placeable.height - trimPx) {
        placeable.place(0, -trimPx)
    }
}

object InputPreferenceDefaults {

    /**
     * The horizontal margin between the card edge and the content: COUI coui_input_preference.xml
     * pads the row 32dp (support_preference_category_layout_title_margin_start / _end) from the
     * screen, minus the 16dp card inset.
     */
    val InsideMargin = PaddingValues(horizontal = 16.dp)

    /** The top padding of the title (COUI coui_input_preference_title_padding_top). */
    val TitlePaddingTop = 10.dp

    /** The bottom padding of the title (COUI coui_input_preference_title_padding_bottom). */
    val TitlePaddingBottom = 4.dp

    /** The minimum height of the title (COUI coui_inputView_title_minheight). */
    val TitleMinHeight = 22.dp

    /**
     * The vertical padding of the field without a title
     * (COUI coui_input_edit_text_no_title_padding_top_inPreference / _bottom_inPreference).
     */
    val FieldPaddingVertical = 13.dp

    /**
     * The bottom padding of the field below a title
     * (COUI coui_input_edit_error_text_has_title_padding_bottom; the top padding is
     * coui_input_edit_text_has_title_padding_top = 0dp).
     */
    val FieldPaddingBottomWithTitle = 11.dp

    /** The font size of the error line (COUI coui_input_view.xml text_input_error, couiTextAppearanceCaption). */
    val ErrorFontSize: TextUnit = 10.sp

    /** The vertical padding of the error line (COUI coui_input_view.xml text_input_error 4dp / 4dp). */
    val ErrorPaddingVertical = 4.dp

    /**
     * The default [InputPreferenceColors].
     *
     * @param titleColor The title color (COUI coui_input_title_color -> couiColorPrimaryNeutral).
     * @param disabledTitleColor The title color when disabled (coui_input_title_color disabled state).
     * @param errorColor The error line color (COUI couiColorError).
     */
    @Composable
    fun inputPreferenceColors(
        titleColor: Color = COUITheme.colorScheme.onSurface,
        disabledTitleColor: Color = COUITheme.colorScheme.disabledOnSurface,
        errorColor: Color = COUITheme.colorScheme.error,
    ): InputPreferenceColors = remember(titleColor, disabledTitleColor, errorColor) {
        InputPreferenceColors(
            titleColor = titleColor,
            disabledTitleColor = disabledTitleColor,
            errorColor = errorColor,
        )
    }
}

/**
 * Colors used by an [InputPreference].
 *
 * @param titleColor The color of the title.
 * @param disabledTitleColor The color of the title when disabled.
 * @param errorColor The color of the error line.
 */
@Immutable
data class InputPreferenceColors(
    val titleColor: Color,
    val disabledTitleColor: Color,
    val errorColor: Color,
) {
    @Stable
    internal fun titleColor(enabled: Boolean): Color = if (enabled) titleColor else disabledTitleColor
}

/** COUIEaseInterpolator (0.33, 0, 0.67, 1) used by the error line fade. */
private val CouiEaseEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** COUIInputView APPEAR_DURATION. */
private const val ERROR_APPEAR_DURATION = 217

/** COUIInputView DISAPPEAR_DURATION. */
private const val ERROR_DISAPPEAR_DURATION = 283
