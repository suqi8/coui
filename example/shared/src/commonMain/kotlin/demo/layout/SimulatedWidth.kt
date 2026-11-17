// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.layout

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_layout_width_fit
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_layout_width_readout
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/**
 * The layout widths the layout playgrounds can lay their preview out at, so the split and grid
 * breakpoints (600dp, 840dp) can be tried on a phone. [Dp.Unspecified] follows the card width.
 */
internal val SimulatedWidths: List<Dp> = listOf(Dp.Unspecified, 360.dp, 600.dp, 840.dp, 1280.dp)

/** The dropdown labels of [SimulatedWidths]. */
@Composable
internal fun simulatedWidthLabels(): List<String> {
    val fit = stringResource(Res.string.demo_layout_width_fit)
    return remember(fit) { SimulatedWidths.map { if (it.isSpecified) "${it.value.roundToInt()}dp" else fit } }
}

/**
 * Lays [content] out [simulatedWidth] wide (or as wide as the available space when unspecified)
 * and scales it down to fit when that is wider than the available space. Touches follow the
 * scaled content. A read-out of the layout width and the scale sits below.
 *
 * @param simulatedWidth The width [content] is measured at, or [Dp.Unspecified] for the available width.
 * @param modifier The modifier for the box.
 * @param height The visible height; the content is measured at the height that fills it after
 *   scaling. Unspecified wraps the content height.
 * @param content The previewed layout.
 */
@Composable
internal fun SimulatedWidthBox(
    simulatedWidth: Dp,
    modifier: Modifier = Modifier,
    height: Dp = Dp.Unspecified,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val target = if (simulatedWidth.isSpecified) simulatedWidth else maxWidth
        val scale = if (target > maxWidth) maxWidth / target else 1f
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ScaledLayout(width = target, height = height, scale = scale, content = content)
            Text(
                text = stringResource(
                    Res.string.demo_layout_width_readout,
                    "${target.value.roundToInt()}dp",
                    "${(scale * 100).roundToInt()}%",
                ),
                modifier = Modifier.padding(top = 8.dp),
                style = COUITheme.textStyles.bodyS,
                color = COUITheme.colorScheme.labelSecondary,
            )
        }
    }
}

/** Measures [content] [width] wide and draws it scaled by [scale] from its top start corner. */
@Composable
private fun ScaledLayout(
    width: Dp,
    height: Dp,
    scale: Float,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = Modifier.clipToBounds()) { measurables, _ ->
        val widthPx = width.roundToPx()
        val childConstraints = if (height.isSpecified) {
            Constraints.fixed(widthPx, (height / scale).roundToPx())
        } else {
            Constraints(minWidth = widthPx, maxWidth = widthPx)
        }
        val placeables = measurables.map { it.measure(childConstraints) }
        val childHeight = placeables.maxOfOrNull { it.height } ?: 0
        layout((widthPx * scale).roundToInt(), (childHeight * scale).roundToInt()) {
            placeables.forEach { placeable ->
                placeable.placeWithLayer(0, 0) {
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin(0f, 0f)
                }
            }
        }
    }
}
