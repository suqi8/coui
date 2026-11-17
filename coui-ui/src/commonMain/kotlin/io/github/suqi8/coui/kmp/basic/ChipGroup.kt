// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp

/**
 * A [ChipGroup] component with COUI style, mirroring `COUIChipGroup`.
 *
 * Lays its chips out in rows that wrap at the available width, [horizontalSpacing] apart within a
 * row and [verticalSpacing] between rows (`coui_chip_group_default_spacing_horizontal` /
 * `_vertical`, 8dp). With [singleLine] the chips never wrap; put the group in a horizontally
 * scrolling container when they may overflow.
 *
 * Selection is held by [state]: call [ChipGroupState.toggle] from each chip's `onClick` and read
 * [ChipGroupState.isSelected] for its `selected` flag. The single-selection and
 * selection-required rules are those of `COUICheckableGroup`.
 *
 * With [collapsable] set and more rows than [collapsedMaxRows], the group shows the COUI
 * expand / collapse button (an [IconChip], `Widget.COUI.Chip.Suggestion.Collapsable` with
 * `ic_chip_expand` / `ic_chip_collapse`). While [ChipGroupState.collapsed] only the first
 * [collapsedMaxRows] rows are shown, and the last of them keeps room for the expand button after
 * its last chip (`COUIChipGroup.isChipExceedCurrentLine`); expanded, the collapse button follows the
 * last chip. Height changes run on the group's `COUISpringForce` (response 0.35, bounce 0).
 *
 * @param modifier The modifier to be applied to the [ChipGroup].
 * @param state The [ChipGroupState] holding the selection and the collapsed flag.
 * @param singleLine Whether the chips stay on one row (`COUIChipGroup.setSingleLine`).
 * @param collapsable Whether the group may collapse to [collapsedMaxRows] rows
 *   (`COUIChipGroup.setIsCollapsable`).
 * @param collapsedMaxRows The number of rows shown while collapsed
 *   (`COUIChipGroup.setCollapsedMaxRows`, 3 by default).
 * @param horizontalSpacing The spacing between two chips of a row.
 * @param verticalSpacing The spacing between two rows.
 * @param collapseButtonColors The [ChipColors] of the expand / collapse button
 *   (`setCollapsableButtonBackgroundColor` / `setCollapsableButtonIconTint`).
 * @param content The chips of the group.
 */
@Composable
fun ChipGroup(
    modifier: Modifier = Modifier,
    state: ChipGroupState = rememberChipGroupState(),
    singleLine: Boolean = false,
    collapsable: Boolean = false,
    collapsedMaxRows: Int = ChipGroupDefaults.CollapsedMaxRows,
    horizontalSpacing: Dp = ChipGroupDefaults.HorizontalSpacing,
    verticalSpacing: Dp = ChipGroupDefaults.VerticalSpacing,
    collapseButtonColors: ChipColors = ChipDefaults.chipColors(),
    content: @Composable () -> Unit,
) {
    val canCollapse = collapsable && !singleLine && collapsedMaxRows > 0
    val collapsed = state.collapsed
    Layout(
        contents = listOf(
            content,
            {
                if (canCollapse) {
                    IconChip(
                        onClick = { state.collapsed = !collapsed },
                        colors = collapseButtonColors,
                    ) {
                        Icon(
                            imageVector = if (collapsed) ChipExpandIcon else ChipCollapseIcon,
                            contentDescription = null,
                        )
                    }
                }
            },
        ),
        modifier = modifier.animateContentSize(animationSpec = ChipGroupSizeSpring),
    ) { (chipMeasurables, buttonMeasurables), constraints ->
        val hSpacing = horizontalSpacing.roundToPx()
        val vSpacing = verticalSpacing.roundToPx()
        val childConstraints = Constraints(maxWidth = constraints.maxWidth)
        val chips = chipMeasurables.map { it.measure(childConstraints) }
        val button = buttonMeasurables.firstOrNull()?.measure(childConstraints)
        val maxWidth = if (singleLine) Constraints.Infinity else constraints.maxWidth

        // Full flow, used to tell whether the group overflows collapsedMaxRows at all.
        val fullRows = flowRows(chips, maxWidth, hSpacing)
        val overflows = canCollapse && button != null && fullRows > collapsedMaxRows

        val positions = arrayOfNulls<IntOffset>(chips.size)
        var x = 0
        var y = 0
        var row = 0
        var rowHeight = 0
        var contentWidth = 0
        var buttonPosition: IntOffset? = null
        for (index in chips.indices) {
            val chip = chips[index]
            if (x > 0 && x + chip.width > maxWidth) {
                row++
                if (overflows && collapsed && row >= collapsedMaxRows) break
                x = 0
                y += rowHeight + vSpacing
                rowHeight = 0
            }
            if (overflows && collapsed && row == collapsedMaxRows - 1 && button != null) {
                // The last visible row keeps room for the expand button after its last chip.
                if (x + chip.width + hSpacing + button.width > maxWidth) {
                    break
                }
            }
            positions[index] = IntOffset(x, y)
            rowHeight = maxOf(rowHeight, chip.height)
            contentWidth = maxOf(contentWidth, x + chip.width)
            x += chip.width + hSpacing
        }
        if (overflows && button != null) {
            if (x > 0 && x + button.width > maxWidth) {
                x = 0
                y += rowHeight + vSpacing
                rowHeight = 0
            }
            buttonPosition = IntOffset(x, y)
            rowHeight = maxOf(rowHeight, button.height)
            contentWidth = maxOf(contentWidth, x + button.width)
        }
        val width = constraints.constrainWidth(contentWidth)
        val buttonAt = buttonPosition
        val height = constraints.constrainHeight(if (chips.isEmpty() && buttonAt == null) 0 else y + rowHeight)
        layout(width, height) {
            chips.forEachIndexed { index, chip ->
                positions[index]?.let { chip.placeRelative(it.x, it.y) }
            }
            if (button != null && buttonAt != null) {
                button.placeRelative(buttonAt.x, buttonAt.y)
            }
        }
    }
}

/** The number of rows [chips] take when wrapped at [maxWidth]. */
private fun flowRows(chips: List<Placeable>, maxWidth: Int, spacing: Int): Int {
    if (chips.isEmpty()) return 0
    var rows = 1
    var x = 0
    for (chip in chips) {
        if (x > 0 && x + chip.width > maxWidth) {
            rows++
            x = 0
        }
        x += chip.width + spacing
    }
    return rows
}

/**
 * The state of a [ChipGroup]: the selected chip indices and whether a collapsable group is
 * collapsed.
 *
 * @param singleSelection Whether at most one chip may be selected: selecting a chip unselects the
 *   previous one (`COUIChipGroup.setSingleSelection`).
 * @param selectionRequired Whether the last selected chip refuses to be unselected
 *   (`COUIChipGroup.setSelectionRequired`), in single and multiple selection alike.
 * @param initialSelection The indices selected at first. In single selection only the first is
 *   kept.
 * @param initialCollapsed Whether a collapsable group starts collapsed (`isCollapsed`, `false` by
 *   default).
 */
@Stable
class ChipGroupState(
    val singleSelection: Boolean = false,
    val selectionRequired: Boolean = false,
    initialSelection: Set<Int> = emptySet(),
    initialCollapsed: Boolean = false,
) {
    /** The indices of the selected chips. */
    var selectedIndices: Set<Int> by mutableStateOf(
        if (singleSelection) initialSelection.take(1).toSet() else initialSelection.toSet(),
    )
        private set

    /** Whether a collapsable [ChipGroup] shows only its first rows. */
    var collapsed: Boolean by mutableStateOf(initialCollapsed)

    /** Whether the chip at [index] is selected. */
    fun isSelected(index: Int): Boolean = index in selectedIndices

    /**
     * Toggles the chip at [index] following `COUICheckableGroup`: selecting it unselects the
     * previous chip in single selection, and unselecting the last selected chip is refused while
     * [selectionRequired] is set.
     */
    fun toggle(index: Int) {
        val current = selectedIndices
        selectedIndices = if (index in current) {
            if (selectionRequired && current.size == 1) current else current - index
        } else if (singleSelection) {
            setOf(index)
        } else {
            current + index
        }
    }

    /** Clears the selection (`COUIChipGroup.clearCheck`). */
    fun clearSelection() {
        selectedIndices = emptySet()
    }
}

/**
 * Creates and remembers a [ChipGroupState].
 *
 * @param singleSelection Whether at most one chip may be selected.
 * @param selectionRequired Whether the last selected chip refuses to be unselected.
 * @param initialSelection The indices selected at first.
 * @param initialCollapsed Whether a collapsable group starts collapsed.
 */
@Composable
fun rememberChipGroupState(
    singleSelection: Boolean = false,
    selectionRequired: Boolean = false,
    initialSelection: Set<Int> = emptySet(),
    initialCollapsed: Boolean = false,
): ChipGroupState = remember(singleSelection, selectionRequired) {
    ChipGroupState(
        singleSelection = singleSelection,
        selectionRequired = selectionRequired,
        initialSelection = initialSelection,
        initialCollapsed = initialCollapsed,
    )
}

/** Contains default values used by [ChipGroup]. */
object ChipGroupDefaults {
    /** The spacing between two chips of a row (COUI coui_chip_group_default_spacing_horizontal). */
    val HorizontalSpacing = 8.dp

    /** The spacing between two rows (COUI coui_chip_group_default_spacing_vertical). */
    val VerticalSpacing = 8.dp

    /** The rows shown while collapsed (COUIChipGroup collapsedMaxRows default). */
    val CollapsedMaxRows = 3
}

/** COUIChipGroup mGroupHeightChangeAnimation: COUISpringForce(response 0.35, bounce 0). */
private val ChipGroupSizeSpring = spring<IntSize>(dampingRatio = 1f, stiffness = 322.27f)

/** COUI `ic_chip_expand` (16 x 16 viewport, 0.93dp round stroke, tinted couiColorLabelPrimary). */
private val ChipExpandIcon: ImageVector by lazy { chevronIcon("ChipExpand", "M4,6L8,10L12,6") }

/** COUI `ic_chip_collapse` (16 x 16 viewport, 0.93dp round stroke, tinted couiColorLabelPrimary). */
private val ChipCollapseIcon: ImageVector by lazy { chevronIcon("ChipCollapse", "M12,10L8,6L4,10") }

private fun chevronIcon(name: String, pathData: String): ImageVector = ImageVector.Builder(name, 16.dp, 16.dp, 16f, 16f).apply {
    addPath(
        pathData = addPathNodes(pathData),
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 0.933333f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
}.build()
