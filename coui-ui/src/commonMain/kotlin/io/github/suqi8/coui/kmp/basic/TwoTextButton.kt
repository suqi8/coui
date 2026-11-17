// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A button with two labels drawn on top of each other at their own opacity, after COUI 17
 * `COUITwoTextButton` (a `COUIButton` subclass). The guide dialog uses it as its primary "Next" /
 * "Start" button and cross-fades the two labels while its pages scroll
 * (`COUIGuideDialogButtonLayout.textAlphaAnimate`); [TwoTextButtonDefaults.outgoingTextAlpha] and
 * [TwoTextButtonDefaults.incomingTextAlpha] reproduce that fade.
 *
 * Each label is a single line, ellipsized at the end to the width inside the padding and centred
 * in the button (`COUITwoTextButton.drawText`). The alphas are read in the draw phase, so animating
 * them redraws the labels without recomposing the button.
 *
 * @param startText The first label (`setStartText`).
 * @param nextText The second label (`setNextText`).
 * @param startTextAlpha The opacity of [startText], 0..1 (`setStartTextAlpha`).
 * @param nextTextAlpha The opacity of [nextText], 0..1 (`setNextTextAlpha`).
 * @param onClick The callback when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 * @param enabled Whether the button is enabled.
 * @param pressScaleEnabled Whether the button shrinks while pressed, see [Button].
 * @param cornerRadius The corner radius of the button, see [Button].
 * @param minWidth The minimum width of the button.
 * @param minHeight The minimum height of the button.
 * @param colors The [ButtonColors] of the button. Defaults to the primary button, as the guide
 *   dialog sets `ButtonType.PRIMARY` on `couiButtonColorfulLargeStyle`.
 * @param insideMargin The margin inside the button.
 * @param textStyle The [TextStyle] of both labels.
 * @param material The COUI 17 material layer, see [Button].
 * @param materialBackdrop Whether the material may replace the fill with the backdrop blur, see
 *   [Button].
 */
@Composable
fun TwoTextButton(
    startText: String,
    nextText: String,
    startTextAlpha: () -> Float,
    nextTextAlpha: () -> Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    pressScaleEnabled: Boolean = true,
    cornerRadius: Dp = ButtonDefaults.CornerRadius,
    minWidth: Dp = ButtonDefaults.MinWidth,
    minHeight: Dp = ButtonDefaults.MinHeight,
    colors: ButtonColors = ButtonDefaults.buttonColorsPrimary(),
    insideMargin: PaddingValues = ButtonDefaults.InsideMargin,
    textStyle: TextStyle = COUITheme.textStyles.buttonL,
    material: ButtonMaterial = ButtonMaterial.Auto,
    materialBackdrop: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        pressScaleEnabled = pressScaleEnabled,
        cornerRadius = cornerRadius,
        minWidth = minWidth,
        minHeight = minHeight,
        colors = colors,
        insideMargin = insideMargin,
        material = material,
        materialBackdrop = materialBackdrop,
    ) {
        Box(
            // Announce the label that is currently the more visible one.
            modifier = Modifier.clearAndSetSemantics {
                contentDescription = if (startTextAlpha() >= nextTextAlpha()) startText else nextText
            },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = startText,
                modifier = Modifier.graphicsLayer { alpha = startTextAlpha().coerceIn(0f, 1f) },
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = nextText,
                modifier = Modifier.graphicsLayer { alpha = nextTextAlpha().coerceIn(0f, 1f) },
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * Defaults of [TwoTextButton]: the label cross-fade of COUI `COUIGuideDialogButtonLayout`.
 *
 * The guide dialog drives the fade with the page offset `f` (0..1, `COUIEaseInterpolator`-eased)
 * between the second-to-last and the last page, measured from the page the drag started on: the
 * label of that page fades out over `f` = 0.5..0.85 and the other label fades in over `f` = 0.5..1.
 */
object TwoTextButtonDefaults {
    /** Where both fades start (`TEXT_FADE_START_PROGRESS`). */
    val TextFadeStartProgress = 0.5f

    /** Where the outgoing label is gone (`TEXT_FADE_END_PROGRESS`). */
    val TextFadeEndProgress = 0.85f

    /** The length of the outgoing fade (`TEXT_FADE_RANGE`). */
    val TextFadeRange = 0.35f

    /**
     * The opacity of the label that is being replaced at transition progress [progress]: 1 up to
     * 0.5, then `(0.85 - progress) / 0.35`, clamped to 0..1.
     */
    fun outgoingTextAlpha(progress: Float): Float = if (progress > TextFadeStartProgress) {
        ((TextFadeEndProgress - progress) / TextFadeRange).coerceIn(0f, 1f)
    } else {
        1f
    }

    /**
     * The opacity of the label that is taking over at transition progress [progress]:
     * `(progress - 0.5) / 0.5`, clamped to 0..1.
     */
    fun incomingTextAlpha(progress: Float): Float = ((progress - TextFadeStartProgress) / (1f - TextFadeStartProgress)).coerceIn(0f, 1f)
}
