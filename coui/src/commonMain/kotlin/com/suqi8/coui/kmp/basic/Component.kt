// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.suqi8.coui.kmp.interfaces.HoldDownInteraction
import com.suqi8.coui.kmp.theme.COUITheme

/**
 * 列表项在卡片组中的位置。
 */
enum class CouiListItemPosition {
    Top, Middle, Bottom, Single
}

private enum class SlotsEnum { Start, Center, End }

/**
 * A base list item component following **COUI** design language.
 *
 * This composable is used to build structured list items inside cards or sections.
 * It supports title, summary, optional left and right content areas, click behavior,
 * and layout position awareness (top/middle/bottom/single).
 *
 * @param title The main title text.
 * @param titleModifier Modifier applied to the title text.
 * @param titleColor The color configuration for the title text.
 *        Use [BasicComponentDefaults.titleColor] to get default colors.
 * @param summary The summary (secondary text) below the title.
 * @param summaryColor The color configuration for the summary text.
 *        Use [BasicComponentDefaults.summaryColor] to get default colors.
 * @param leftAction Optional composable content displayed at the start of the row
 *        (e.g., an icon or switch).
 * @param rightActions Optional composable content displayed at the end of the row
 *        (e.g., arrow, toggle, or additional actions).
 * @param modifier The modifier applied to the entire component.
 * @param insideMargin The internal padding of the component.
 *        Defaults to [BasicComponentDefaults.InsideMargin].
 * @param onClick The callback invoked when the component is clicked.
 *        If null, the component will not be clickable.
 * @param position The position of this item in a list or group.
 *        Used to adjust rounded corners and padding.
 * @param holdDownState Whether the component is currently in a pressed (hold-down) state.
 * @param enabled Whether the component is enabled for interaction and color updates.
 * @param interactionSource The [MutableInteractionSource] controlling interaction states.
 */
@Composable
fun BasicComponent(
    title: String? = null,
    titleModifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    leftAction: @Composable (() -> Unit?)? = null,
    rightActions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    onClick: (() -> Unit)? = null,
    position: CouiListItemPosition = CouiListItemPosition.Middle,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val indication = LocalIndication.current
    val extraTopDp = if (position == CouiListItemPosition.Top || position == CouiListItemPosition.Single) 2.dp else 0.dp
    val extraBottomDp = if (position == CouiListItemPosition.Bottom || position == CouiListItemPosition.Single) 2.dp else 0.dp
    val minHeight = 48.dp
    val density = LocalDensity.current
    val horizontalGapPx = with(density) { 16.dp.roundToPx() }

    val holdDown = remember { mutableStateOf<HoldDownInteraction.HoldDown?>(null) }
    LaunchedEffect(holdDownState) {
        if (holdDownState) {
            val interaction = HoldDownInteraction.HoldDown()
            holdDown.value = interaction
            interactionSource.emit(interaction)
        } else {
            holdDown.value?.let { oldValue ->
                interactionSource.emit(HoldDownInteraction.Release(oldValue))
                holdDown.value = null
            }
        }
    }

    val clickableModifier = remember(onClick, enabled, interactionSource) {
        if (onClick != null && enabled) {
            Modifier.clickable(
                indication = indication,
                interactionSource = interactionSource,
                onClick = onClick
            )
        } else Modifier
    }

    val layoutDirection = LocalLayoutDirection.current

    SubcomposeLayout(
        modifier = modifier
            .heightIn(min = minHeight + extraTopDp + extraBottomDp)
            .fillMaxWidth()
            .then(clickableModifier)
            .padding(
                start = insideMargin.calculateStartPadding(layoutDirection),
                end = insideMargin.calculateEndPadding(layoutDirection),
                top = insideMargin.calculateTopPadding() + extraTopDp,
                bottom = insideMargin.calculateBottomPadding() + extraBottomDp
            )
    ) { constraints ->
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        // Left action
        val leftPlaceables = leftAction?.let {
            subcompose("leftAction") { it() }.map { it.measure(looseConstraints) }
        } ?: emptyList()
        val leftWidth = leftPlaceables.maxOfOrNull { it.width } ?: 0
        val leftHeight = leftPlaceables.maxOfOrNull { it.height } ?: 0

        // Right actions
        val rightPlaceables = subcompose("rightActions") {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                content = rightActions
            )
        }.map { it.measure(looseConstraints) }
        val rightWidth = rightPlaceables.maxOfOrNull { it.width } ?: 0
        val rightHeight = rightPlaceables.maxOfOrNull { it.height } ?: 0

        // Content area
        val leftGap = if (leftWidth > 0) horizontalGapPx else 0
        val rightGap = if (rightWidth > 0) horizontalGapPx else 0
        val contentMaxWidth = maxOf(0, constraints.maxWidth - leftWidth - leftGap - rightWidth - rightGap)

        val titlePlaceable = title?.let {
            subcompose("title") {
                Box(
                    modifier = Modifier.heightIn(min = 21.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        lineHeight = 1.2.em,
                        fontWeight = FontWeight.Medium,
                        color = titleColor.color(enabled),
                        modifier = titleModifier
                    )
                }
            }.first().measure(looseConstraints.copy(maxWidth = contentMaxWidth))
        }

        val summaryPlaceable = summary?.let {
            subcompose("summary") {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    lineHeight = 1.3.em,
                    fontWeight = FontWeight.Normal,
                    color = summaryColor.color(enabled)
                )
            }.first().measure(looseConstraints.copy(maxWidth = contentMaxWidth))
        }

        val gap = 2.dp.roundToPx()
        val contentHeight = (titlePlaceable?.height ?: 0) +
                (if (titlePlaceable != null && summaryPlaceable != null) gap else 0) +
                (summaryPlaceable?.height ?: 0)
        val layoutHeight = maxOf(leftHeight, rightHeight, contentHeight).coerceAtLeast(constraints.minHeight)

        layout(constraints.maxWidth, layoutHeight) {
            var x = 0
            if (leftWidth > 0) {
                leftPlaceables.forEach {
                    it.placeRelative(x, (layoutHeight - it.height) / 2)
                }
                x += leftWidth + leftGap
            }

            var contentY = (layoutHeight - contentHeight) / 2
            titlePlaceable?.let {
                it.placeRelative(x, contentY)
                contentY += it.height
                if (summaryPlaceable != null) contentY += gap
            }
            summaryPlaceable?.placeRelative(x, contentY)

            val rightX = constraints.maxWidth - rightWidth
            rightPlaceables.forEach {
                it.placeRelative(rightX, (layoutHeight - it.height) / 2)
            }
        }
    }
}

/**
 * Default configuration values for [BasicComponent].
 */
object BasicComponentDefaults {

    /** Default inner padding for the component. */
    val InsideMargin = PaddingValues(vertical = 8.dp, horizontal = 16.dp)

    /** Default title color scheme. */
    @Composable
    fun titleColor(
        enabledColor: Color = COUITheme.colorScheme.onSurface,
        disabledColor: Color = COUITheme.colorScheme.disabledOnSecondaryVariant
    ) = BasicComponentColors(enabledColor, disabledColor)

    /** Default summary color scheme. */
    @Composable
    fun summaryColor(
        enabledColor: Color = COUITheme.colorScheme.onSurfaceVariantSummary,
        disabledColor: Color = COUITheme.colorScheme.disabledOnSecondaryVariant
    ) = BasicComponentColors(enabledColor, disabledColor)
}

/**
 * Holds color states for [BasicComponent].
 *
 * @param enabledColor Color when the component is enabled.
 * @param disabledColor Color when the component is disabled.
 */
@Immutable
class BasicComponentColors(
    private val enabledColor: Color,
    private val disabledColor: Color
) {
    @Stable
    internal fun color(enabled: Boolean): Color =
        if (enabled) enabledColor else disabledColor
}
