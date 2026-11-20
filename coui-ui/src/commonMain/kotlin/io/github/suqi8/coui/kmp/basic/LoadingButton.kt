// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalContentColor

/**
 * A [LoadingButton] component with COUI style.
 *
 * A text button that can switch into a loading state, mirroring COUILoadingButton: while
 * [isLoading] is `true` the label is hidden and three tiny dots pulse in a staggered wave
 * (each dot fades 20% -> 50% -> 100% -> 50% -> 20% alpha, dots offset by 333ms over a 1332ms
 * cycle), and clicks are swallowed so the action cannot be triggered again.
 *
 * When [loadingText] is provided, the loading state shows that label followed by three animated
 * dot glyphs instead of the plain dot indicator (COUIButton isShowLoadingText / loadingText), and
 * falls back to the plain dots when the text and its dots do not fit the button's label area.
 *
 * @param text The text of the [LoadingButton] when it is not loading.
 * @param onClick The callback when the [LoadingButton] is clicked. Not invoked while [isLoading].
 * @param modifier The modifier to be applied to the [LoadingButton].
 * @param isLoading Whether the [LoadingButton] is in the loading state.
 * @param loadingText The text shown next to the animated dots while loading, or `null` to show
 *   only the dot indicator.
 * @param enabled Whether the [LoadingButton] is enabled.
 * @param cornerRadius The corner radius of the [LoadingButton].
 * @param minWidth The minimum width of the [LoadingButton].
 * @param minHeight The minimum height of the [LoadingButton].
 * @param colors The [TextButtonColors] of the [LoadingButton]. The loading dots use the content
 *   color, like COUILoadingButton draws them with the label paint.
 * @param insideMargin The margin inside the [LoadingButton].
 * @param textStyle The [TextStyle] of the label, the loading text and its dots. Pass
 *   [ButtonDefaults.textStyleSmall] with the `Small` metrics for the COUI small size tier.
 * @param material The COUI 17 material layer drawn over the fill, see [Button].
 * @param materialBackdrop Whether the material may replace the fill with the backdrop blur, see
 *   [Button].
 * @param interactionSource The [MutableInteractionSource] to be used for the [LoadingButton].
 * @param indication The [Indication] to be used for the [LoadingButton]. Defaults to `null`
 *   because the COUI press feedback (scale + press tint) is built into the button itself.
 */
@Composable
fun LoadingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    loadingText: String? = null,
    enabled: Boolean = true,
    cornerRadius: Dp = ButtonDefaults.CornerRadius,
    minWidth: Dp = ButtonDefaults.MinWidth,
    minHeight: Dp = ButtonDefaults.MinHeight,
    colors: TextButtonColors = ButtonDefaults.textButtonColors(),
    insideMargin: PaddingValues = ButtonDefaults.InsideMargin,
    textStyle: TextStyle = COUITheme.textStyles.buttonL,
    material: ButtonMaterial = ButtonMaterial.Auto,
    materialBackdrop: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
) {
    val currentOnClick by rememberUpdatedState(onClick)
    val currentIsLoading by rememberUpdatedState(isLoading)
    // COUILoadingButton keeps reacting to touches while loading (press feedback still plays),
    // but the click itself must not re-trigger the action.
    val clickHandler = remember { { if (!currentIsLoading) currentOnClick() } }
    val mappedColors = remember(colors) {
        ButtonColors(
            color = colors.color,
            disabledColor = colors.disabledColor,
            contentColor = colors.textColor,
            disabledContentColor = colors.disabledTextColor,
        )
    }
    Button(
        onClick = clickHandler,
        modifier = modifier,
        enabled = enabled,
        cornerRadius = cornerRadius,
        minWidth = minWidth,
        minHeight = minHeight,
        colors = mappedColors,
        insideMargin = insideMargin,
        material = material,
        materialBackdrop = materialBackdrop,
        interactionSource = interactionSource,
        indication = indication,
    ) {
        val layoutDirection = LocalLayoutDirection.current
        // The label area of the button when it is not wider than its label: the min width minus
        // the horizontal margins.
        val minInnerWidth = minWidth - insideMargin.calculateStartPadding(layoutDirection) -
            insideMargin.calculateEndPadding(layoutDirection)
        // COUIButton caps its text size at the G4 font scale; the loading text is drawn with the
        // same paint.
        val labelStyle = buttonLabelStyle(textStyle)
        Layout(
            content = {
                // COUILoadingButton clears the label while loading; keep it measured at alpha 0
                // so the button does not resize when the state toggles.
                Text(
                    text = text,
                    modifier = Modifier.graphicsLayer { alpha = if (isLoading) 0f else 1f },
                    style = labelStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (isLoading) {
                    LoadingDots()
                    if (loadingText != null) LoadingTextWithDots(loadingText, labelStyle)
                }
            },
        ) { measurables, constraints ->
            val label = measurables[0].measure(constraints.copy(minWidth = 0))
            val dots = measurables.getOrNull(1)?.measure(Constraints())
            val loadingLabel = measurables.getOrNull(2)?.measure(Constraints())
            // COUILoadingButton.onDraw: when the loading text and its dots are wider than the
            // button minus its padding, the plain dot indicator is drawn instead.
            val innerWidth = maxOf(label.width, minInnerWidth.roundToPx()).coerceAtMost(constraints.maxWidth)
            val loading = when {
                loadingLabel != null && loadingLabel.width <= innerWidth -> loadingLabel
                else -> dots
            }
            val width = constraints.constrainWidth(maxOf(label.width, loading?.width ?: 0))
            val height = constraints.constrainHeight(maxOf(label.height, loading?.height ?: 0))
            layout(width, height) {
                label.placeRelative((width - label.width) / 2, (height - label.height) / 2)
                if (loading != null) {
                    loading.placeRelative((width - loading.width) / 2, (height - loading.height) / 2)
                }
            }
        }
    }
}

/** Contains default values used by [LoadingButton]. */
object LoadingButtonDefaults {

    /** The radius of each loading dot (COUI coui_loading_btn_circle_radius). */
    val DotRadius = 1.dp

    /** The gap between two neighbouring loading dots (COUI coui_loading_btn_circle_spacing). */
    val DotSpacing = 2.dp
}

/**
 * The three pulsing loading dots (COUILoadingButton.drawLoadingCircles): circles of
 * [LoadingButtonDefaults.DotRadius] spaced by [LoadingButtonDefaults.DotSpacing], drawn with the
 * content color and per-dot animated alpha. The original sets the alpha on the label paint
 * (`paint.setAlpha`), so the wave replaces the color's own alpha instead of multiplying it. The wave
 * runs in reading direction, so the first and third alphas swap in RTL like the original.
 */
@Composable
private fun LoadingDots() {
    val color = LocalContentColor.current
    val transition = rememberInfiniteTransition(label = "loadingButtonDots")
    val firstAlpha = transition.loadingDotAlpha(0)
    val secondAlpha = transition.loadingDotAlpha(1)
    val thirdAlpha = transition.loadingDotAlpha(2)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val radius = LoadingButtonDefaults.DotRadius
    val spacing = LoadingButtonDefaults.DotSpacing
    // The alphas are read in the draw phase, so the wave redraws the dots without recomposing.
    Canvas(modifier = Modifier.size(width = radius * 6 + spacing * 2, height = radius * 2)) {
        val r = radius.toPx()
        val step = r * 2 + spacing.toPx()
        val cy = size.height / 2f
        val startAlpha = if (isRtl) thirdAlpha.value else firstAlpha.value
        val endAlpha = if (isRtl) firstAlpha.value else thirdAlpha.value
        drawCircle(color = color.copy(alpha = startAlpha), radius = r, center = Offset(r, cy))
        drawCircle(color = color.copy(alpha = secondAlpha.value), radius = r, center = Offset(r + step, cy))
        drawCircle(color = color.copy(alpha = endAlpha), radius = r, center = Offset(r + 2 * step, cy))
    }
}

/**
 * The loading label followed by three animated dot glyphs (COUILoadingButton with
 * isShowLoadingText): each "." carries the same staggered alpha wave as the plain dots, again in
 * place of the label color's alpha (`drawClipDot` sets it on the paint).
 */
@Composable
private fun LoadingTextWithDots(loadingText: String, textStyle: TextStyle) {
    val transition = rememberInfiniteTransition(label = "loadingButtonDots")
    val firstAlpha = transition.loadingDotAlpha(0)
    val secondAlpha = transition.loadingDotAlpha(1)
    val thirdAlpha = transition.loadingDotAlpha(2)
    val glyphColor = textStyle.color.takeOrElse { LocalContentColor.current }.copy(alpha = 1f)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = loadingText,
            modifier = Modifier.alignByBaseline(),
            style = textStyle,
        )
        LoadingDotGlyph(textStyle, glyphColor) { firstAlpha.value }
        LoadingDotGlyph(textStyle, glyphColor) { secondAlpha.value }
        LoadingDotGlyph(textStyle, glyphColor) { thirdAlpha.value }
    }
}

@Composable
private fun RowScope.LoadingDotGlyph(textStyle: TextStyle, color: Color, alpha: () -> Float) {
    Text(
        text = ".",
        modifier = Modifier
            .alignByBaseline()
            .graphicsLayer { this.alpha = alpha() },
        color = color,
        style = textStyle,
    )
}

/**
 * One dot of the COUILoadingButton loading wave. The original chains twelve ValueAnimators with a
 * linear interpolator; per dot (delayed by [index] * 333ms) the alpha runs 51 -> 127.5 over 133ms,
 * -> 255 over 67ms, holds, -> 127.5 over 67ms at 467ms, -> 51 over 133ms, and the whole 1332ms set
 * restarts when it ends.
 */
@Composable
private fun InfiniteTransition.loadingDotAlpha(index: Int): State<Float> = animateFloat(
    initialValue = DotStartAlpha,
    targetValue = DotStartAlpha,
    animationSpec = infiniteRepeatable(
        animation = keyframes {
            durationMillis = DotCycleMillis
            DotStartAlpha at 0
            DotMidAlpha at 133
            DotEndAlpha at 200
            DotEndAlpha at 467
            DotMidAlpha at 534
            DotStartAlpha at 666
        },
        repeatMode = RepeatMode.Restart,
        initialStartOffset = StartOffset(index * DotStaggerMillis, StartOffsetType.Delay),
    ),
    label = "loadingButtonDotAlpha$index",
)

/** The length of one loading wave cycle: the last COUILoadingButton animator ends at 1199 + 133ms. */
private val DotCycleMillis = 1332

/** The delay between two neighbouring dots joining the wave. */
private val DotStaggerMillis = 333

/** COUILoadingButton DOT_START_ALPHA (51 / 255). */
private val DotStartAlpha = 0.2f

/** COUILoadingButton DOT_MID_ALPHA (127.5 / 255). */
private val DotMidAlpha = 0.5f

/** COUILoadingButton DOT_END_ALPHA (255 / 255). */
private val DotEndAlpha = 1f
