// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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
 * With [animationsEnabled] the chips move like `COUIChip.ChipGroupAnimatorImpl`: a chip whose place
 * changes springs there (response 0.3, bounce 0), a chip the group stops showing fades out where it
 * was and one it shows again fades back in (alpha springs, response 0.2), a chip added to the group
 * also grows from 0.8, and the expand and collapse buttons cross-fade the same way. A chip is
 * followed by its `Modifier.layoutId` when it has one, by its index otherwise. Nothing animates on
 * the first layout or when the available width changes (`COUIChipGroup` skips the animations after
 * an orientation or screen size change).
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
 * @param animationsEnabled Whether chips animate when the layout changes
 *   (`COUIChipGroup.setChipGroupAnimationsEnabled`, on by default).
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
    animationsEnabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val canCollapse = collapsable && !singleLine && collapsedMaxRows > 0
    val collapsed = state.collapsed
    val scope = rememberCoroutineScope()
    val motion = remember { ChipGroupMotion() }
    Layout(
        contents = listOf(
            content,
            {
                if (canCollapse) {
                    // COUIChipGroup keeps two chips, mExpandChip and mCollapseChip, and fades one out
                    // while the other one fades in. A button on its way out ignores taps.
                    IconChip(
                        onClick = { if (motion.isShown(ChipGroupExpandButtonKey)) state.collapsed = false },
                        colors = collapseButtonColors,
                    ) {
                        Icon(imageVector = ChipExpandIcon, contentDescription = null)
                    }
                    IconChip(
                        onClick = { if (motion.isShown(ChipGroupCollapseButtonKey)) state.collapsed = true },
                        colors = collapseButtonColors,
                    ) {
                        Icon(imageVector = ChipCollapseIcon, contentDescription = null)
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
        val expandButton = buttonMeasurables.getOrNull(0)?.measure(childConstraints)
        val collapseButton = buttonMeasurables.getOrNull(1)?.measure(childConstraints)
        val maxWidth = if (singleLine) Constraints.Infinity else constraints.maxWidth

        // Full flow, used to tell whether the group overflows collapsedMaxRows at all.
        val fullRows = flowRows(chips, maxWidth, hSpacing)
        val overflows = canCollapse && expandButton != null && collapseButton != null && fullRows > collapsedMaxRows
        val button = when {
            !overflows -> null
            collapsed -> expandButton
            else -> collapseButton
        }

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
        if (button != null) {
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
        val keys = List(chipMeasurables.size) { index -> chipMeasurables[index].layoutId ?: ChipGroupIndexKey(index) }
        val rtl = layoutDirection == LayoutDirection.Rtl
        val availableWidth = constraints.maxWidth
        layout(width, height) {
            // The motion runs from the placement, which intrinsic measurements never reach. Reading
            // the relayout counter places the group again once a hidden chip has faded out.
            motion.observeRelayout()
            val animate = animationsEnabled && motion.hasLaidOut && motion.lastAvailableWidth == availableWidth
            motion.hasLaidOut = true
            motion.lastAvailableWidth = availableWidth
            chips.forEachIndexed { index, chip ->
                placeChipGroupItem(chip, motion.sync(keys[index], positions[index], animate, scope), rtl)
            }
            if (expandButton != null) {
                val target = if (button === expandButton) buttonAt else null
                placeChipGroupItem(expandButton, motion.sync(ChipGroupExpandButtonKey, target, animate, scope), rtl)
            }
            if (collapseButton != null) {
                val target = if (button === collapseButton) buttonAt else null
                placeChipGroupItem(collapseButton, motion.sync(ChipGroupCollapseButtonKey, target, animate, scope), rtl)
            }
            motion.retain(keys, hasButtons = expandButton != null)
        }
    }
}

/**
 * Places [placeable] where [item] is laid out (or, fading out, where it was last laid out), with
 * its animated offset, alpha and scale applied in a layer so the animations do not lay out again.
 */
private fun Placeable.PlacementScope.placeChipGroupItem(placeable: Placeable, item: ChipGroupItemMotion, rtl: Boolean) {
    if (!item.isPlaced) return
    val position = item.position
    placeable.placeRelativeWithLayer(position.x, position.y) {
        val offset = item.offset
        // The offset is in left-to-right coordinates, mirrored like the position.
        translationX = if (rtl) -offset.x else offset.x
        translationY = offset.y
        alpha = item.alpha
        scaleX = item.scale
        scaleY = item.scale
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

/** The identity of a chip without a `layoutId`: its index in the group. */
private data class ChipGroupIndexKey(val index: Int)

/** The identity of the expand button (`COUIChipGroup.mExpandChip`). */
private object ChipGroupExpandButtonKey

/** The identity of the collapse button (`COUIChipGroup.mCollapseChip`). */
private object ChipGroupCollapseButtonKey

/** The [ChipGroupItemMotion] of every chip and button of a [ChipGroup], by identity. */
private class ChipGroupMotion {
    private val items = mutableMapOf<Any, ChipGroupItemMotion>()

    /** Bumped when a hidden item has faded out, so the group stops placing it. */
    private var relayoutRequests by mutableIntStateOf(0)

    /** Whether the group has been laid out once: the first layout never animates. */
    var hasLaidOut = false

    /** The available width of the last layout: a new one lays out without animation. */
    var lastAvailableWidth = -1

    fun observeRelayout(): Int = relayoutRequests

    fun isShown(key: Any): Boolean = items[key]?.shown == true

    /** Moves the item of [key] to [target] (`null` when the group does not show it). */
    fun sync(key: Any, target: IntOffset?, animate: Boolean, scope: CoroutineScope): ChipGroupItemMotion {
        // An item first seen by an animated layout is a chip added to the group (COUIChipGroup.addChip).
        val item = items.getOrPut(key) { ChipGroupItemMotion(addedToGroup = animate) }
        item.sync(target, animate, scope) { relayoutRequests++ }
        return item
    }

    /** Drops the items of chips that left the group, and of the buttons when there are none. */
    fun retain(chipKeys: List<Any>, hasButtons: Boolean) {
        if (items.size == chipKeys.size + (if (hasButtons) 2 else 0)) return
        val kept = chipKeys.toHashSet()
        if (hasButtons) {
            kept += ChipGroupExpandButtonKey
            kept += ChipGroupCollapseButtonKey
        }
        val iterator = items.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.key !in kept) {
                entry.value.cancel()
                iterator.remove()
            }
        }
    }
}

/**
 * One chip of a [ChipGroup] as `COUIChip.ChipGroupAnimatorImpl` moves it: its place, the offset of
 * its drawing from that place, its alpha and its scale. The animated values are states read only by
 * the chip's placement layer.
 *
 * @param addedToGroup Whether the chip joined an already laid-out group: only such a chip grows
 *   from 0.8 when it first shows (`updateAttachState` runs before its first layout); a chip the
 *   group only stops hiding fades in at full size.
 */
private class ChipGroupItemMotion(private val addedToGroup: Boolean) {
    /** The place of the last layout that showed the item, in left-to-right coordinates. */
    var position = IntOffset.Zero
        private set

    /** Whether the item has been laid out before (`mRealBounds` is not empty). */
    private var hasPosition = false

    /** Whether the group shows the item (`mIsAttachedToChipGroup`). */
    var shown = false
        private set

    /** A hidden item stays placed where it was while it fades out. */
    private var fadingOut = false

    val isPlaced: Boolean get() = shown || fadingOut

    var offset by mutableStateOf(Offset.Zero)
        private set
    var alpha by mutableFloatStateOf(1f)
        private set
    var scale by mutableFloatStateOf(1f)
        private set

    private val moveAnimation = Animatable(Offset.Zero, Offset.VectorConverter, ChipGroupOffsetVisibilityThreshold)
    private val alphaAnimation = Animatable(1f, ChipGroupFadeVisibilityThreshold)
    private val scaleAnimation = Animatable(1f, ChipGroupFadeVisibilityThreshold)
    private var moveJob: Job? = null
    private var fadeJob: Job? = null
    private var scaleJob: Job? = null

    fun sync(target: IntOffset?, animate: Boolean, scope: CoroutineScope, onFadedOut: () -> Unit) {
        if (target != null) {
            if (!animate) {
                settle(visible = true)
            } else if (shown) {
                // updateChipRealBounds: a laid-out chip springs from where it is drawn to its new place.
                if (target != position) moveTo(target, scope)
            } else {
                // updateAttachState(true): fade in, and grow from 0.8 when it was just added.
                fadeIn(scaleIn = addedToGroup && !hasPosition, scope = scope)
            }
            position = target
            hasPosition = true
            shown = true
            fadingOut = false
        } else if (shown) {
            shown = false
            // updateAttachState(false): fade out where it was, then leave the layout.
            if (animate) fadeOut(scope, onFadedOut) else settle(visible = false)
        }
    }

    fun cancel() {
        moveJob?.cancel()
        fadeJob?.cancel()
        scaleJob?.cancel()
    }

    private fun settle(visible: Boolean) {
        cancel()
        fadingOut = false
        offset = Offset.Zero
        alpha = if (visible) 1f else 0f
        scale = 1f
    }

    private fun moveTo(target: IntOffset, scope: CoroutineScope) {
        val drawnOffset = Snapshot.withoutReadObservation { offset }
        val start = drawnOffset + Offset((position.x - target.x).toFloat(), (position.y - target.y).toFloat())
        // Set at once, so this very placement already draws the chip where it was.
        offset = start
        // animateToFinalPosition keeps the running spring's velocity; a cancelled Animatable drops
        // it, so it is read before the running move is cancelled.
        val velocity = moveAnimation.velocity
        moveJob?.cancel()
        moveJob = scope.launch {
            moveAnimation.snapTo(start)
            moveAnimation.animateTo(Offset.Zero, ChipGroupMoveSpring, initialVelocity = velocity) { offset = value }
        }
    }

    private fun fadeIn(scaleIn: Boolean, scope: CoroutineScope) {
        // A re-shown chip is drawn at its new place right away (ChipGroupAnimatorImpl resets
        // mDrawingBounds while the chip is detached).
        moveJob?.cancel()
        offset = Offset.Zero
        val from = if (fadingOut) Snapshot.withoutReadObservation { alpha } else 0f
        alpha = from
        fadeJob?.cancel()
        fadeJob = scope.launch {
            alphaAnimation.snapTo(from)
            alphaAnimation.animateTo(1f, ChipGroupFadeSpring) { alpha = value }
        }
        if (scaleIn) {
            scale = ChipGroupAppearScale
            scaleJob?.cancel()
            scaleJob = scope.launch {
                scaleAnimation.snapTo(ChipGroupAppearScale)
                scaleAnimation.animateTo(1f, ChipGroupFadeSpring) { scale = value }
            }
        }
    }

    private fun fadeOut(scope: CoroutineScope, onFadedOut: () -> Unit) {
        fadingOut = true
        val from = Snapshot.withoutReadObservation { alpha }
        fadeJob?.cancel()
        fadeJob = scope.launch {
            alphaAnimation.snapTo(from)
            alphaAnimation.animateTo(0f, ChipGroupFadeSpring) { alpha = value }
            fadingOut = false
            onFadedOut()
        }
    }
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

/** ChipGroupAnimatorImpl mHorizontalAnimation / mVerticalAnimation: COUISpringForce(response 0.3, bounce 0). */
private val ChipGroupMoveSpring = spring(dampingRatio = 1f, stiffness = 438.65f, visibilityThreshold = Offset(0.5f, 0.5f))

/** ChipGroupAnimatorImpl mAlphaAnimation / mScaleAnimation: COUISpringForce(response 0.2, bounce 0). */
private val ChipGroupFadeSpring = spring(dampingRatio = 1f, stiffness = 986.96f, visibilityThreshold = 0.001f)

/** The scale a chip added to the group grows from (ChipGroupAnimatorImpl mScale 0.8). */
private val ChipGroupAppearScale = 0.8f

private val ChipGroupOffsetVisibilityThreshold = Offset(0.5f, 0.5f)

private val ChipGroupFadeVisibilityThreshold = 0.001f

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
