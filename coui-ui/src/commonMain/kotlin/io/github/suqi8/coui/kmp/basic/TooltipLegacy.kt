// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
@file:Suppress("DEPRECATION")

package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import io.github.suqi8.coui.kmp.squircle.squircleBackground
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalContentColor
import kotlin.math.max

private const val LEGACY_MESSAGE =
    "The Miuix tooltip is replaced by the COUI 17 COUIToolTips port: use TooltipBox(state, tooltip) with " +
        "Tooltip / IconTooltip / ImageTooltip."

/**
 * Spring driving the legacy tooltip's exit scale. A spring (not a tween) honors the inbound velocity
 * left by the enter spring when a dismiss interrupts an unsettled enter.
 */
private val LegacyTooltipExitScaleSpec: FiniteAnimationSpec<Float> =
    spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium, visibilityThreshold = 0.0001f)

/**
 * The Miuix tooltip box: anchors a [PlainTooltip] / [RichTooltip] to its [content] through a
 * [PopupPositionProvider], with a caret-less Material-style bubble.
 *
 * @param positionProvider the [PopupPositionProvider] positioning the tooltip.
 * @param tooltip the tooltip content.
 * @param state the [TooltipState] controlling visibility.
 * @param modifier the modifier applied to the anchor wrapper.
 * @param focusable whether the tooltip is focusable.
 * @param enableUserInput whether hover / long press on the anchor shows the tooltip.
 * @param content the anchor content.
 */
@Deprecated(LEGACY_MESSAGE)
@Composable
fun TooltipBox(
    positionProvider: PopupPositionProvider,
    tooltip: @Composable TooltipScope.() -> Unit,
    state: TooltipState,
    modifier: Modifier = Modifier,
    focusable: Boolean = false,
    enableUserInput: Boolean = true,
    content: @Composable () -> Unit,
) {
    var anchorBounds by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val positioning = (positionProvider as? LegacyTooltipPositionProvider)?.positioning ?: TooltipAnchorPosition.Below
    val scope = remember(positioning) { TooltipScopeImpl(positioning) { anchorBounds } }

    TooltipBoxImpl(
        positionProvider = positionProvider,
        tooltip = {
            AnimatedVisibility(
                visibleState = state.transition,
                enter = fadeIn(ListPopupDefaults.AlphaEnterAnimationSpec) +
                    scaleIn(initialScale = 0.9f, animationSpec = ListPopupDefaults.FractionAnimationSpec),
                exit = fadeOut(ListPopupDefaults.AlphaExitAnimationSpec) +
                    scaleOut(targetScale = 0.9f, animationSpec = LegacyTooltipExitScaleSpec),
            ) {
                scope.tooltip()
            }
        },
        state = state,
        modifier = modifier.onGloballyPositioned { anchorBounds = it },
        properties = PopupProperties(focusable = focusable),
        enableUserInput = enableUserInput,
        content = content,
    )
}

/**
 * The Miuix plain tooltip: a short label on the inverse surface.
 *
 * @param modifier the modifier applied to the tooltip.
 * @param caretShape when non-null, draws a caret pointing at the anchor.
 * @param maxWidth the maximum width of the tooltip.
 * @param cornerRadius the corner radius of the tooltip.
 * @param containerColor the container color of the tooltip.
 * @param contentColor the content color of the tooltip.
 * @param insideMargin the margin inside the tooltip.
 * @param content the tooltip content.
 */
@Deprecated(LEGACY_MESSAGE)
@Composable
fun TooltipScope.PlainTooltip(
    modifier: Modifier = Modifier,
    caretShape: Shape? = null,
    maxWidth: Dp = TooltipDefaults.PlainTooltipMaxWidth,
    cornerRadius: Dp = TooltipDefaults.PlainTooltipCornerRadius,
    containerColor: Color = TooltipDefaults.plainTooltipContainerColor,
    contentColor: Color = TooltipDefaults.plainTooltipContentColor,
    insideMargin: PaddingValues = TooltipDefaults.PlainTooltipInsideMargin,
    content: @Composable () -> Unit,
) {
    LegacyTooltipSurface(
        caretShape = caretShape,
        maxWidth = maxWidth,
        cornerRadius = cornerRadius,
        containerColor = containerColor,
        insideMargin = insideMargin,
        modifier = modifier.semantics(mergeDescendants = false) {
            isTraversalGroup = true
            liveRegion = LiveRegionMode.Polite
        },
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            content()
        }
    }
}

/**
 * The Miuix rich tooltip: a surface-container card with an optional title and action.
 *
 * @param modifier the modifier applied to the tooltip.
 * @param title the optional title shown above the body.
 * @param action the optional action shown below the body.
 * @param caretShape when non-null, draws a caret pointing at the anchor.
 * @param maxWidth the maximum width of the tooltip.
 * @param cornerRadius the corner radius of the tooltip.
 * @param colors the [RichTooltipColors] of the tooltip.
 * @param insideMargin the margin inside the tooltip.
 * @param text the body content of the tooltip.
 */
@Deprecated(LEGACY_MESSAGE)
@Composable
fun TooltipScope.RichTooltip(
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
    caretShape: Shape? = null,
    maxWidth: Dp = TooltipDefaults.RichTooltipMaxWidth,
    cornerRadius: Dp = TooltipDefaults.RichTooltipCornerRadius,
    colors: RichTooltipColors = TooltipDefaults.richTooltipColors(),
    insideMargin: PaddingValues = TooltipDefaults.RichTooltipInsideMargin,
    text: @Composable () -> Unit,
) {
    LegacyTooltipSurface(
        caretShape = caretShape,
        maxWidth = maxWidth,
        cornerRadius = cornerRadius,
        containerColor = colors.containerColor,
        insideMargin = insideMargin,
        modifier = modifier.semantics(mergeDescendants = false) {
            isTraversalGroup = true
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (title != null) {
                CompositionLocalProvider(LocalContentColor provides colors.titleContentColor) {
                    title()
                }
            }
            CompositionLocalProvider(LocalContentColor provides colors.contentColor) {
                text()
            }
            if (action != null) {
                Box(modifier = Modifier.align(Alignment.End)) {
                    CompositionLocalProvider(LocalContentColor provides colors.actionContentColor) {
                        action()
                    }
                }
            }
        }
    }
}

@Composable
private fun TooltipScope.LegacyTooltipSurface(
    caretShape: Shape?,
    maxWidth: Dp,
    cornerRadius: Dp,
    containerColor: Color,
    insideMargin: PaddingValues,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val drawCaret = caretShape != null &&
        (positioning == TooltipAnchorPosition.Above || positioning == TooltipAnchorPosition.Below)
    val caretOnTop = positioning == TooltipAnchorPosition.Below
    val caretSize = TooltipDefaults.caretSize
    var selfBounds by remember { mutableStateOf<LayoutCoordinates?>(null) }

    Box(
        modifier = modifier
            .widthIn(max = maxWidth)
            .onGloballyPositioned { selfBounds = it }
            .then(
                if (drawCaret) {
                    Modifier.drawBehind {
                        val anchor = obtainAnchorBounds() ?: return@drawBehind
                        val self = selfBounds ?: return@drawBehind
                        if (!anchor.isAttached || !self.isAttached) return@drawBehind
                        val anchorCenterX = anchor.positionInWindow().x + anchor.size.width / 2f
                        val selfX = self.positionInWindow().x
                        val caretHalf = caretSize.width.toPx() / 2f
                        val cr = cornerRadius.toPx()
                        val centerX = (anchorCenterX - selfX)
                            .coerceIn(cr + caretHalf, size.width - cr - caretHalf)
                        val caretHeight = caretSize.height.toPx()
                        val path = Path()
                        if (caretOnTop) {
                            path.moveTo(centerX, 0f)
                            path.lineTo(centerX - caretHalf, caretHeight)
                            path.lineTo(centerX + caretHalf, caretHeight)
                        } else {
                            path.moveTo(centerX, size.height)
                            path.lineTo(centerX - caretHalf, size.height - caretHeight)
                            path.lineTo(centerX + caretHalf, size.height - caretHeight)
                        }
                        path.close()
                        drawPath(path, color = containerColor)
                    }
                } else {
                    Modifier
                },
            ),
    ) {
        Box(
            modifier = Modifier
                .padding(
                    top = if (drawCaret && caretOnTop) caretSize.height else 0.dp,
                    bottom = if (drawCaret && !caretOnTop) caretSize.height else 0.dp,
                )
                .dropShadow(
                    shape = RoundedCornerShape(cornerRadius),
                    shadow = Shadow(radius = 10.dp, color = Color.Black, alpha = 0.1f),
                )
                .squircleBackground(color = containerColor, cornerRadius = cornerRadius, weight = COUIRoundCorner.Large.Weight)
                .padding(insideMargin),
        ) {
            content()
        }
    }
}

/**
 * The Miuix plain tooltip convenience: [TooltipBox] + [PlainTooltip] for a short text label.
 *
 * @param text the text label of the tooltip.
 * @param modifier the modifier applied to the anchor wrapper.
 * @param state the [TooltipState] controlling visibility.
 * @param enabled whether hover / long press on the anchor shows the tooltip.
 * @param positioning the preferred [TooltipAnchorPosition] of the tooltip.
 * @param containerColor the container color of the tooltip.
 * @param contentColor the content color of the tooltip.
 * @param content the anchor content.
 */
@Deprecated(
    LEGACY_MESSAGE,
    ReplaceWith(
        "TooltipBox(state = state, tooltip = { Tooltip(text = text, mode = TooltipMode.Info) }, modifier = modifier, " +
            "placement = positioning, enableUserInput = enabled, content = content)",
    ),
)
@Composable
fun TooltipBox(
    text: String,
    modifier: Modifier = Modifier,
    state: TooltipState = rememberTooltipState(isPersistent = false),
    enabled: Boolean = true,
    positioning: TooltipAnchorPosition = TooltipAnchorPosition.Below,
    containerColor: Color = TooltipDefaults.plainTooltipContainerColor,
    contentColor: Color = TooltipDefaults.plainTooltipContentColor,
    content: @Composable () -> Unit,
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(positioning),
        tooltip = {
            PlainTooltip(containerColor = containerColor, contentColor = contentColor) {
                Text(
                    text = text,
                    color = contentColor,
                    style = COUITheme.textStyles.body2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        state = state,
        modifier = modifier,
        focusable = false,
        enableUserInput = enabled,
        content = content,
    )
}

/**
 * The Miuix rich tooltip convenience: [TooltipBox] + [RichTooltip] with an optional title and a
 * single text action.
 *
 * @param text the body text of the tooltip.
 * @param modifier the modifier applied to the anchor wrapper.
 * @param state the [TooltipState] controlling visibility.
 * @param title the optional title shown above the body.
 * @param actionText the optional action label.
 * @param onActionClick called when the action is clicked, before the tooltip is dismissed.
 * @param enabled whether hover / long press on the anchor shows the tooltip.
 * @param positioning the preferred [TooltipAnchorPosition] of the tooltip.
 * @param colors the [RichTooltipColors] of the tooltip.
 * @param content the anchor content.
 */
@Deprecated(LEGACY_MESSAGE)
@Composable
fun RichTooltipBox(
    text: String,
    modifier: Modifier = Modifier,
    state: TooltipState = rememberTooltipState(isPersistent = true),
    title: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    positioning: TooltipAnchorPosition = TooltipAnchorPosition.Below,
    colors: RichTooltipColors = TooltipDefaults.richTooltipColors(),
    content: @Composable () -> Unit,
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(positioning),
        tooltip = {
            RichTooltip(
                title = title?.let {
                    {
                        Text(
                            text = it,
                            color = colors.titleContentColor,
                            style = COUITheme.textStyles.subtitle,
                        )
                    }
                },
                action = actionText?.let {
                    {
                        TextButton(
                            text = it,
                            onClick = {
                                onActionClick?.invoke()
                                state.dismiss()
                            },
                            minWidth = 0.dp,
                            minHeight = 0.dp,
                            cornerRadius = TooltipDefaults.RichTooltipActionCornerRadius,
                            colors = ButtonDefaults.textButtonColors(
                                color = Color.Transparent,
                                textColor = colors.actionContentColor,
                            ),
                            insideMargin = TooltipDefaults.RichTooltipActionInsideMargin,
                        )
                    }
                },
                colors = colors,
            ) {
                Text(
                    text = text,
                    color = colors.contentColor,
                    style = COUITheme.textStyles.body2,
                )
            }
        },
        state = state,
        modifier = modifier,
        focusable = true,
        enableUserInput = enabled,
        content = content,
    )
}

/**
 * Colors for the Miuix [RichTooltip].
 *
 * @param containerColor container color of the rich tooltip.
 * @param contentColor color of the rich tooltip body text.
 * @param titleContentColor color of the rich tooltip title.
 * @param actionContentColor color of the rich tooltip action label.
 */
@Deprecated("Use TooltipColors with TooltipDefaults.tooltipColors.")
@Immutable
data class RichTooltipColors(
    val containerColor: Color,
    val contentColor: Color,
    val titleContentColor: Color,
    val actionContentColor: Color,
)

/**
 * Positions a legacy tooltip on the preferred side of its anchor, centered on the cross axis,
 * flipping to the opposite side when there is no room and clamped to the window bounds.
 */
@Immutable
internal class LegacyTooltipPositionProvider(
    val positioning: TooltipAnchorPosition,
    private val spacing: Int,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val resolved = positioning.toTooltipSide(layoutDirection)
        val centerX = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
        val centerY = anchorBounds.top + (anchorBounds.height - popupContentSize.height) / 2

        var x = centerX
        var y = centerY
        when (resolved) {
            TooltipSide.Below -> {
                val below = anchorBounds.bottom + spacing
                val above = anchorBounds.top - popupContentSize.height - spacing
                y = if (below + popupContentSize.height <= windowSize.height || above < 0) below else above
            }

            TooltipSide.Above -> {
                val above = anchorBounds.top - popupContentSize.height - spacing
                val below = anchorBounds.bottom + spacing
                y = if (above >= 0 || below + popupContentSize.height > windowSize.height) above else below
            }

            TooltipSide.Left -> {
                val left = anchorBounds.left - popupContentSize.width - spacing
                val right = anchorBounds.right + spacing
                x = if (left >= 0 || right + popupContentSize.width > windowSize.width) left else right
            }

            TooltipSide.Right -> {
                val right = anchorBounds.right + spacing
                val left = anchorBounds.left - popupContentSize.width - spacing
                x = if (right + popupContentSize.width <= windowSize.width || left < 0) right else left
            }
        }

        x = x.coerceIn(0, max(0, windowSize.width - popupContentSize.width))
        y = y.coerceIn(0, max(0, windowSize.height - popupContentSize.height))
        return IntOffset(x, y)
    }
}

internal object LegacyTooltipCaretShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val path = Path().apply {
            moveTo(size.width / 2f, 0f)
            lineTo(0f, size.height)
            lineTo(size.width, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}
