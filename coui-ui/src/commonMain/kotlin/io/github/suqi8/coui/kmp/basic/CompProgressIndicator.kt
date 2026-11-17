// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * The layout of a [CompProgressIndicator] (COUICompProgressIndicator `couiLoadingType`).
 */
enum class CompProgressIndicatorType {
    /** `LARGE_ANIMATION`: the 26dp spinner alone. */
    Large,

    /** `SMALL_ANIMATION`: the 14dp spinner alone. */
    Small,

    /** `SMALL_ANIMATION_WITH_TEXT_HORIZONTAL`: the 14dp spinner with a one-line tip 4dp after it. */
    SmallWithTextHorizontal,

    /** `LARGE_ANIMATION_WITH_TEXT_VERTICAL`: the 26dp spinner over a tip of up to two lines, 16dp below it. */
    LargeWithTextVertical,

    /** `SMALL_ANIMATION_WITH_TEXT_VERTICAL`: the 14dp spinner over a tip of up to two lines, 12dp below it. */
    SmallWithTextVertical,
}

/**
 * A [CompProgressIndicator] with COUI style.
 *
 * The composite loading indicator of COUI 17 `COUICompProgressIndicator`: the rotating spinner
 * (`couiRotatingSpinnerJsonName`, the [RotatingProgressIndicator] animation) at the large (26dp,
 * `coui_loading_large_width`) or small (14dp, `coui_loading_small_width`) size, optionally with a
 * loading tip in couiTextBodyS / couiColorLabelSecondary, centered. The vertical tips are at least
 * 40dp wide and keep 4dp below them (`Widget.COUI.COUICompProgressIndicator.TipsTextView.Vertical`,
 * `coui_loading_textview_bottom_margin`).
 *
 * @param modifier The modifier to be applied to the indicator.
 * @param type The [CompProgressIndicatorType] layout.
 * @param text The loading tip (`loadingTips`). Only the `...WithText...` types draw it.
 * @param color The color of the spinner arc.
 * @param textColor The color of the tip.
 * @param textStyle The [TextStyle] of the tip.
 */
@Composable
fun CompProgressIndicator(
    modifier: Modifier = Modifier,
    type: CompProgressIndicatorType = CompProgressIndicatorType.Large,
    text: String? = null,
    color: Color = ProgressIndicatorDefaults.rotatingProgressIndicatorColor(),
    textColor: Color = CompProgressIndicatorDefaults.textColor(),
    textStyle: TextStyle = COUITheme.textStyles.bodyS,
) {
    val large = type == CompProgressIndicatorType.Large || type == CompProgressIndicatorType.LargeWithTextVertical
    val size = if (large) CompProgressIndicatorDefaults.LargeSize else CompProgressIndicatorDefaults.SmallSize
    val spinner: @Composable () -> Unit = {
        RotatingProgressIndicator(
            color = color,
            size = size,
            ringDiameter = size * RotatingSpinnerRingRatio,
            strokeWidth = size * RotatingSpinnerStrokeRatio,
        )
    }
    val tip = text?.takeIf { it.isNotEmpty() }
    when (type) {
        CompProgressIndicatorType.Large, CompProgressIndicatorType.Small -> {
            Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) { spinner() }
        }

        CompProgressIndicatorType.SmallWithTextHorizontal -> {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                spinner()
                if (tip != null) {
                    Text(
                        text = tip,
                        modifier = Modifier.padding(start = CompProgressIndicatorDefaults.HorizontalTextSpacing),
                        color = textColor,
                        style = textStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        CompProgressIndicatorType.LargeWithTextVertical, CompProgressIndicatorType.SmallWithTextVertical -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                spinner()
                if (tip != null) {
                    Text(
                        text = tip,
                        modifier = Modifier
                            .padding(
                                top = if (large) {
                                    CompProgressIndicatorDefaults.LargeVerticalTextSpacing
                                } else {
                                    CompProgressIndicatorDefaults.SmallVerticalTextSpacing
                                },
                                bottom = CompProgressIndicatorDefaults.VerticalTextBottomMargin,
                            )
                            .widthIn(min = CompProgressIndicatorDefaults.VerticalTextMinWidth),
                        color = textColor,
                        style = textStyle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

/**
 * coui_rotating_loading.json: a 26-unit ellipse at 300% group scale is 78 units across on an
 * 84-unit comp, and its stroke 2 x 300% = 6 units, scaled with the view.
 */
private val RotatingSpinnerRingRatio = 78f / 84f
private val RotatingSpinnerStrokeRatio = 6f / 84f

/** Contains default values used by [CompProgressIndicator]. */
object CompProgressIndicatorDefaults {
    /** The large spinner (COUI coui_loading_large_width / _height). */
    val LargeSize: Dp = 26.dp

    /** The small spinner (COUI coui_loading_small_width / _height). */
    val SmallSize: Dp = 14.dp

    /** The gap between the small spinner and a horizontal tip (COUI coui_loading_textview_left_margin). */
    val HorizontalTextSpacing: Dp = 4.dp

    /** The gap above a tip under the large spinner (COUI coui_loading_textview_top_margin). */
    val LargeVerticalTextSpacing: Dp = 16.dp

    /** The gap above a tip under the small spinner (COUI coui_loading_textview_top_margin_small). */
    val SmallVerticalTextSpacing: Dp = 12.dp

    /** The space below a vertical tip (COUI coui_loading_textview_bottom_margin). */
    val VerticalTextBottomMargin: Dp = 4.dp

    /** The minimum width of a vertical tip (TipsTextView.Vertical android:minWidth). */
    val VerticalTextMinWidth: Dp = 40.dp

    /** The tip color (TipsTextView `?couiColorLabelSecondary`). */
    @Composable
    fun textColor(): Color = COUITheme.colorScheme.labelSecondary
}
