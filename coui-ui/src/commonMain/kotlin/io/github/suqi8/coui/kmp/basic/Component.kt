// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.interfaces.HoldDownObserver
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.roundToInt

/**
 * A basic component with COUI style. Widely used in other extension components.
 *
 * @param modifier The modifier to be applied to the [BasicComponent].
 * @param title The title of the [BasicComponent].
 * @param titleColor The color of the title.
 * @param summary The summary of the [BasicComponent].
 * @param summaryColor The color of the summary.
 * @param startAction The [Composable] content on the start side of the [BasicComponent].
 * @param endActions The [Composable] content on the end side of the [BasicComponent].
 * @param bottomAction The [Composable] content at the bottom of the [BasicComponent].
 * @param insideMargin The margin inside the [BasicComponent].
 * @param cardListPosition The row's position inside its card group. Rounded outer edges receive an
 *   extra [CardListPosition.HeadOrTailPadding]. Defaults to the position a [CardListGroup] provides ([CardListPosition.None] outside one).
 * @param onClick The callback when the [BasicComponent] is clicked.
 * @param onClickLabel Optional label describing the click action for accessibility services.
 * @param role The semantic [Role] of the [BasicComponent] for accessibility services.
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param enabled Whether the [BasicComponent] is enabled.
 * @param selected Whether the row is selected (`COUIPreference.setSelected`): the press layer stays
 *   on, and inside a [CardListGroup] the divider below this row and the one above it are hidden.
 * @param startBadge Whether a red dot sits on the top end corner of [startAction] (COUI `img_red_dot`).
 * @param endBadgeCount The red dot between the title and [endActions] (COUI `jump_icon_red_dot`):
 *   `null` hides it, `0` shows a plain dot and a positive value shows the number.
 * @param endIcon An optional 16dp icon shown right before [endActions] (COUI `assignment_icon`).
 * @param interactionSource The [MutableInteractionSource] for the [BasicComponent].
 *   The value should remain null or non-null for the lifetime of this component;
 *   switching across recompositions will allocate a new internal source and lose pending interactions.
 */
@Composable
fun BasicComponent(
    modifier: Modifier = Modifier,
    title: String? = null,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    startAction: @Composable (() -> Unit)? = null,
    endActions: @Composable (RowScope.() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    onClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    role: Role? = null,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
    selected: Boolean = false,
    startBadge: Boolean = false,
    endBadgeCount: Int? = null,
    endIcon: (@Composable () -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val textLines = remember { BasicComponentTextLines() }
    BasicComponentLayout(
        modifier = modifier,
        startAction = startAction,
        endActions = endActions,
        bottomAction = bottomAction,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        onClick = onClick,
        onClickLabel = onClickLabel,
        role = role,
        holdDownState = holdDownState,
        enabled = enabled,
        selected = selected,
        startBadge = startBadge,
        endBadgeCount = endBadgeCount,
        endIcon = endIcon,
        interactionSource = interactionSource,
        textLines = textLines,
    ) {
        if (title != null) {
            Text(
                text = title,
                style = COUITheme.textStyles.headlineXS,
                color = titleColor.color(enabled),
                onTextLayout = textLines::onTitleLayout,
            )
        } else {
            textLines.title = null
        }
        if (summary != null) {
            // COUI 17 coui_preference*.xml dropped the summary's layout_marginTop: the summary sits
            // directly below the title, in TextAppearance.COUI.Preference.Summary (13sp ArticleBody).
            Text(
                text = summary,
                style = COUITheme.textStyles.bodyS,
                color = summaryColor.color(enabled),
                onTextLayout = textLines::onSummaryLayout,
            )
        } else {
            textLines.summary = null
        }
    }
}

/**
 * A basic component with COUI style. Widely used in other extension components.
 *
 * @param modifier The modifier to be applied to the [BasicComponent].
 * @param startAction The [Composable] content on the start side of the [BasicComponent].
 * @param endActions The [Composable] content on the end side of the [BasicComponent].
 * @param bottomAction The [Composable] content at the bottom of the [BasicComponent].
 * @param insideMargin The margin inside the [BasicComponent].
 * @param cardListPosition The row's position inside its card group. Rounded outer edges receive an
 *   extra [CardListPosition.HeadOrTailPadding], so a standalone row is 4dp taller than a middle row
 *   and a head/tail row is 2dp taller. Defaults to the position a [CardListGroup] provides ([CardListPosition.None], no extra padding, outside one).
 * @param onClick The callback when the [BasicComponent] is clicked.
 * @param onClickLabel Optional label describing the click action for accessibility services.
 * @param role The semantic [Role] of the [BasicComponent] for accessibility services.
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param enabled Whether the [BasicComponent] is enabled.
 * @param selected Whether the row is selected (`COUIPreference.setSelected`): the press layer stays
 *   on, and inside a [CardListGroup] the divider below this row and the one above it are hidden.
 * @param startBadge Whether a red dot sits on the top end corner of [startAction] (COUI `img_red_dot`).
 * @param endBadgeCount The red dot between the content and [endActions] (COUI `jump_icon_red_dot`):
 *   `null` hides it, `0` shows a plain dot and a positive value shows the number.
 * @param endIcon An optional 16dp icon shown right before [endActions] (COUI `assignment_icon`).
 * @param interactionSource The [MutableInteractionSource] for the [BasicComponent].
 *   The value should remain null or non-null for the lifetime of this component;
 *   switching across recompositions will allocate a new internal source and lose pending interactions.
 * @param content The content of the [BasicComponent].
 */
@Composable
fun BasicComponent(
    modifier: Modifier = Modifier,
    startAction: @Composable (() -> Unit)? = null,
    endActions: @Composable (RowScope.() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    onClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    role: Role? = null,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
    selected: Boolean = false,
    startBadge: Boolean = false,
    endBadgeCount: Int? = null,
    endIcon: (@Composable () -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    BasicComponentLayout(
        modifier = modifier,
        startAction = startAction,
        endActions = endActions,
        bottomAction = bottomAction,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        onClick = onClick,
        onClickLabel = onClickLabel,
        role = role,
        holdDownState = holdDownState,
        enabled = enabled,
        selected = selected,
        startBadge = startBadge,
        endBadgeCount = endBadgeCount,
        endIcon = endIcon,
        interactionSource = interactionSource,
        textLines = null,
        content = content,
    )
}

/**
 * The shared body of both [BasicComponent] overloads. [textLines] is filled by the title / summary
 * texts of the title overload, so the start content can follow the COUI icon rules
 * (`COUICustomListSelectedLinearLayout.operateMultilineIconPosition`); free-form content passes `null`
 * and is treated as a single line.
 */
@Composable
private fun BasicComponentLayout(
    startAction: @Composable (() -> Unit)?,
    endActions: @Composable (RowScope.() -> Unit)?,
    bottomAction: (@Composable () -> Unit)?,
    insideMargin: PaddingValues,
    cardListPosition: CardListPosition,
    onClick: (() -> Unit)?,
    onClickLabel: String?,
    role: Role?,
    holdDownState: Boolean,
    enabled: Boolean,
    selected: Boolean,
    startBadge: Boolean,
    endBadgeCount: Int?,
    endIcon: (@Composable () -> Unit)?,
    interactionSource: MutableInteractionSource?,
    textLines: BasicComponentTextLines?,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val currentOnClick by rememberUpdatedState(onClick)

    // COUICardListSelectedItemLayout.setIsSelected locks the touch-enter (press) state of the row's
    // COUIMaskEffectDrawable, so a selected row keeps the couiColorPress layer on.
    HoldDownObserver(holdDownState || selected, interactionSource)

    val indication = LocalIndication.current
    val clickableModifier = remember(enabled, onClick != null, interactionSource, role, onClickLabel, indication) {
        if (enabled && onClick != null) {
            Modifier.clickable(
                interactionSource = interactionSource,
                indication = indication,
                onClickLabel = onClickLabel,
                role = role,
                onClick = { currentOnClick?.invoke() },
            )
        } else {
            // Still draw the state layer, so holdDownState / selected show on non-clickable rows.
            Modifier.indication(interactionSource, indication)
        }
    }

    // Register with the enclosing CardListGroup row slot so the group can draw the COUI divider
    // (COUIPreferenceItemDecoration) aligned with the title and hide it around selected rows.
    val listItem = LocalCardListItem.current?.takeIf { cardListPosition != CardListPosition.None }
    if (listItem != null) {
        DisposableEffect(listItem) {
            listItem.attach()
            onDispose { listItem.detach() }
        }
        SideEffect {
            listItem.selected = selected
            listItem.position = cardListPosition
        }
    }
    val hasEnd = endActions != null || endBadgeCount != null || endIcon != null
    if (listItem != null && startAction == null && !hasEnd) {
        val titleStart = with(LocalDensity.current) {
            insideMargin.calculateStartPadding(LocalLayoutDirection.current).roundToPx()
        }
        SideEffect { listItem.dividerStart = titleStart }
    }

    // COUI COUICardListSelectedItemLayout.setPadding: the rounded outer edges of a card group each get
    // an extra coui_list_card_head_or_tail_padding, and the row's minimum height grows by the same total.
    val extraPaddingTop = cardListPosition.extraPaddingTop
    val extraPaddingBottom = cardListPosition.extraPaddingBottom

    Column(
        modifier = modifier
            .heightIn(min = BasicComponentDefaults.MinHeight + extraPaddingTop + extraPaddingBottom)
            .fillMaxWidth()
            .then(clickableModifier)
            .padding(insideMargin)
            .padding(top = extraPaddingTop, bottom = extraPaddingBottom),
        verticalArrangement = Arrangement.Center,
    ) {
        if (startAction == null && !hasEnd) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                content = content,
            )
        } else {
            Layout(
                content = {
                    startAction?.let {
                        Box(modifier = Modifier.layoutId("start")) {
                            it()
                            if (startBadge) {
                                // COUI img_red_dot: end|top of img_layout, inset by support_preference_icon_red_dot_margin_top.
                                Badge(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(
                                            top = BasicComponentDefaults.StartBadgeInset,
                                            end = BasicComponentDefaults.StartBadgeInset,
                                        ),
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.layoutId("center"),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        content = content,
                    )
                    if (hasEnd) {
                        Column(
                            modifier = Modifier.layoutId("end"),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.End,
                        ) {
                            // Center end actions vertically so trailing value text lines up with taller siblings.
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                BasicComponentEndDecorations(
                                    endBadgeCount = endBadgeCount,
                                    endIcon = endIcon,
                                    hasEndActions = endActions != null,
                                )
                                endActions?.invoke(this)
                            }
                        }
                    }
                },
            ) { measurables, constraints ->
                val startGapPx = BasicComponentDefaults.StartActionSpacing.roundToPx()
                val endGapPx = BasicComponentDefaults.EndActionsSpacing.roundToPx()

                val startMeasurable = measurables.firstOrNull { it.layoutId == "start" }
                val centerMeasurable = measurables.first { it.layoutId == "center" }
                val endMeasurable = measurables.firstOrNull { it.layoutId == "end" }

                val maxWidth = constraints.maxWidth
                val maxHeight = constraints.maxHeight

                val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

                val startPlaceable = startMeasurable?.measure(looseConstraints)
                val startWidth = startPlaceable?.width ?: 0
                val startSpacerWidth = if (startWidth > 0) startGapPx else 0
                val widthAfterStart = (maxWidth - startWidth - startSpacerWidth).coerceAtLeast(0)

                // COUICustomLinearLayoutForPreference (couiAHavePriority): the title keeps its width and
                // the trailing content is guaranteed min(own width, assignment_in_right_low_priority_min_width).
                // Only when the trailing content is wider than that floor does the title width matter.
                val endTargetWidth = if (endMeasurable == null) {
                    0
                } else {
                    val available = (widthAfterStart - endGapPx).coerceAtLeast(0)
                    val endIntrinsicWidth = endMeasurable.maxIntrinsicWidth(maxHeight)
                    val lowPriorityMin = BasicComponentDefaults.EndLowPriorityMinWidth.roundToPx()
                    if (endIntrinsicWidth <= lowPriorityMin) {
                        endIntrinsicWidth
                    } else {
                        val centerIntrinsicWidth = centerMeasurable.maxIntrinsicWidth(maxHeight)
                        maxOf(lowPriorityMin, available - centerIntrinsicWidth).coerceAtMost(endIntrinsicWidth)
                    }.coerceAtMost(available)
                }
                val endPlaceable = endMeasurable?.measure(
                    looseConstraints.copy(maxWidth = endTargetWidth),
                )
                val endActualWidth = endPlaceable?.width ?: 0
                val endSpacerWidth = if (endActualWidth > 0) endGapPx else 0

                val widthForCenter = (widthAfterStart - endActualWidth - endSpacerWidth).coerceAtLeast(0)
                val centerPlaceable = centerMeasurable.measure(
                    looseConstraints.copy(maxWidth = widthForCenter),
                )

                val startHeight = startPlaceable?.height ?: 0
                val endHeight = endPlaceable?.height ?: 0
                val centerHeight = centerPlaceable.height

                // COUICustomListSelectedLinearLayout: the start content (img_layout) takes vertical margins
                // picked by its dp height and the text line count, measured from the row edge; this layout
                // sits inside the vertical insideMargin, so the margins are converted relative to it.
                val topPad = insideMargin.calculateTopPadding().roundToPx()
                val bottomPad = insideMargin.calculateBottomPadding().roundToPx()
                val lineCount = textLines?.lineCount ?: 1
                val topAligned = startPlaceable != null && textLines != null && lineCount > REMAINING_TOTAL_LINE
                var startTop = 0
                var rowHeight = maxOf(centerHeight, endHeight)
                if (startPlaceable != null) {
                    if (topAligned) {
                        val firstTwoLinesCenter = textLines?.firstTwoLinesCenter() ?: 0f
                        val minTop = BasicComponentDefaults.StartActionMinTopMargin.roundToPx() - topPad
                        startTop = maxOf((firstTwoLinesCenter - startHeight / 2f).roundToInt(), minTop)
                        rowHeight = maxOf(rowHeight, startTop + startHeight)
                    } else {
                        val margin = BasicComponentDefaults.startActionVerticalMargin(
                            heightDp = (startHeight / density).roundToInt(),
                            lineCount = lineCount,
                        ).roundToPx()
                        rowHeight = maxOf(rowHeight, startHeight + 2 * margin - topPad - bottomPad, 0)
                    }
                }
                val layoutHeight = rowHeight
                    .coerceIn(constraints.minHeight, maxHeight.takeIf { it != Constraints.Infinity } ?: rowHeight)

                val centerX = startWidth + startSpacerWidth
                listItem?.dividerStart = insideMargin.calculateStartPadding(layoutDirection).roundToPx() + centerX

                layout(width = maxWidth, height = layoutHeight) {
                    if (!topAligned) startTop = (rowHeight - startHeight) / 2
                    val centerTop = (rowHeight - centerHeight) / 2
                    val endTop = (rowHeight - endHeight).coerceAtLeast(0) / 2

                    startPlaceable?.placeRelative(0, startTop)

                    centerPlaceable.placeRelative(centerX, centerTop)

                    endPlaceable?.let {
                        val endX = maxWidth - it.width
                        it.placeRelative(endX, endTop)
                    }
                }
            }
        }

        if (bottomAction != null) {
            Spacer(modifier = Modifier.height(8.dp))
            bottomAction()
        }
    }
}

/**
 * The red dot and the 16dp icon COUI places between the title and the assignment
 * (`coui_preference_assignment_in_right.xml`): each keeps
 * `support_preference_reddot_margin_end_in_right_hasassignment` (4dp) to what follows it.
 */
@Composable
private fun BasicComponentEndDecorations(
    endBadgeCount: Int?,
    endIcon: (@Composable () -> Unit)?,
    hasEndActions: Boolean,
) {
    if (endBadgeCount == null && endIcon == null) return
    val gap = BasicComponentDefaults.EndDecorationSpacing
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (endBadgeCount != null) {
            Badge(
                modifier = Modifier.padding(end = if (endIcon != null || hasEndActions) gap else 0.dp),
                count = endBadgeCount,
            )
        }
        if (endIcon != null) {
            Box(
                modifier = Modifier
                    .padding(end = if (hasEndActions) gap else 0.dp)
                    .size(BasicComponentDefaults.EndIconSize),
                contentAlignment = Alignment.Center,
            ) {
                endIcon()
            }
        }
    }
}

/** COUICustomListSelectedLinearLayout.REMAINING_TOTAL_LINE: above this line count the start content is top aligned. */
private const val REMAINING_TOTAL_LINE = 2

/**
 * The latest text layouts of the title overload's title and summary, written while they are measured
 * (before the parent layout reads them), so the start content can follow the COUI line-count rules.
 */
private class BasicComponentTextLines {
    var title: TextLayoutResult? = null
    var summary: TextLayoutResult? = null

    fun onTitleLayout(result: TextLayoutResult) {
        title = result
    }

    fun onSummaryLayout(result: TextLayoutResult) {
        summary = result
    }

    val lineCount: Int
        get() = (title?.lineCount ?: 0) + (summary?.lineCount ?: 0)

    /**
     * The vertical center of the first two text lines (`checkFirstTwoLineCenter`): the top of the
     * first line and the bottom of the second, in the coordinates of the text column.
     */
    fun firstTwoLinesCenter(): Float {
        val title = title
        val summary = summary
        val top: Float
        val bottom: Float
        if (title != null) {
            top = title.getLineTop(0)
            bottom = when {
                title.lineCount >= 2 -> title.getLineBottom(1)
                summary != null -> title.size.height + summary.getLineBottom(0)
                else -> title.getLineBottom(0)
            }
        } else if (summary != null) {
            top = summary.getLineTop(0)
            bottom = summary.getLineBottom((summary.lineCount - 1).coerceAtMost(1))
        } else {
            return 0f
        }
        return (top + bottom) / 2f
    }
}

object BasicComponentDefaults {

    /**
     * The default minimum height of the [BasicComponent], before any card-group
     * position padding is added.
     */
    val MinHeight = 48.dp

    /**
     * The default margin inside the [BasicComponent].
     */
    val InsideMargin = PaddingValues(horizontal = 16.dp, vertical = 10.dp)

    /** The gap between the start content and the title (COUI coui_preference_icon_margin_right). */
    val StartActionSpacing = 16.dp

    /**
     * The minimum gap between the title and the end content (COUI support_preference_title_margin_end_in_right,
     * the message layout's end margin in coui_preference_withlittlewiget_assignment_in_right.xml).
     */
    val EndActionsSpacing = 16.dp

    /**
     * The width the end content always keeps when the title is too long to share the row
     * (COUI assignment_in_right_low_priority_min_width). The end content here also holds the trailing
     * widget, which COUI measures separately.
     */
    val EndLowPriorityMinWidth = 80.dp

    /** The gap after the end red dot and the end icon (COUI support_preference_reddot_margin_end_in_right_hasassignment). */
    val EndDecorationSpacing = 4.dp

    /** The size of the end icon (COUI assignment_icon in coui_preference_assignment_in_right.xml). */
    val EndIconSize = 16.dp

    /** The inset of the start red dot from the start content's top end corner (COUI support_preference_icon_red_dot_margin_top). */
    val StartBadgeInset = 2.dp

    /**
     * The minimum distance from the row top to top-aligned start content once the text runs past two
     * lines (COUI coui_preference_50icon_margin_vertical_default in `setIconTopInParent`).
     */
    val StartActionMinTopMargin = 8.dp

    /**
     * The vertical margin COUI gives the start content from the row edge
     * (`COUICustomListSelectedLinearLayout.setIconCenterInParent`), by its height in dp and the number
     * of text lines: 24dp -> 12dp / 20dp multi-line, 32dp -> 10dp / 16dp, 36dp -> 12dp / 14dp, 50dp -> 8dp,
     * any other size 14dp (coui_preference_{24,32,36,50}icon_margin_*, coui_preference_icon_margin_top).
     */
    fun startActionVerticalMargin(heightDp: Int, lineCount: Int): Dp {
        val singleLine = lineCount <= 1
        return when (heightDp) {
            24 -> if (singleLine) 12.dp else 20.dp
            32 -> if (singleLine) 10.dp else 16.dp
            36 -> if (singleLine) 12.dp else 14.dp
            50 -> 8.dp
            else -> 14.dp
        }
    }

    /**
     * The default color of the title.
     */
    @Composable
    fun titleColor(
        color: Color = COUITheme.colorScheme.labelPrimary,
        disabledColor: Color = COUITheme.colorScheme.disabledNeutral,
    ): BasicComponentColors = remember(color, disabledColor) {
        BasicComponentColors(
            color = color,
            disabledColor = disabledColor,
        )
    }

    /**
     * The default color of the summary (COUI coui_preference_secondary_text_color:
     * couiColorSecondNeutral, disabled couiColorLabelTertiary).
     */
    @Composable
    fun summaryColor(
        color: Color = COUITheme.colorScheme.labelSecondary,
        disabledColor: Color = COUITheme.colorScheme.disabledNeutral,
    ): BasicComponentColors = remember(color, disabledColor) {
        BasicComponentColors(
            color = color,
            disabledColor = disabledColor,
        )
    }
}

@Immutable
data class BasicComponentColors(
    val color: Color,
    val disabledColor: Color,
) {
    @Stable
    internal fun color(enabled: Boolean): Color = if (enabled) color else disabledColor
}
