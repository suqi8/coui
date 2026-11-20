// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.animate
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import io.github.suqi8.coui.kmp.anim.folmeSpring
import io.github.suqi8.coui.kmp.utils.COUITouchInteraction
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * The [CardListPosition] the rows of a [CardListGroup] read as the default of their
 * `cardListPosition` parameter. [CardListPosition.None] outside a group.
 */
val LocalCardListPosition = staticCompositionLocalOf { CardListPosition.None }

/**
 * The divider slot of the [CardListGroup] row a [BasicComponent] sits in, `null` outside a group.
 */
internal val LocalCardListItem = staticCompositionLocalOf<CardListItemState?> { null }

/**
 * The receiver of a [CardListGroup] builder: each [item] is one row of the group.
 */
interface CardListGroupScope {
    /**
     * Adds a row to the group.
     *
     * @param key An optional stable key for the row, so a row keeps its state when rows before it
     *   appear or disappear.
     * @param showDivider Whether the divider below this row is drawn (COUI `couiShowDivider`).
     * @param content The row, typically a preference or a [BasicComponent].
     */
    fun item(key: Any? = null, showDivider: Boolean = true, content: @Composable () -> Unit)
}

/**
 * A group of card list rows that resolves each row's [CardListPosition] from its index, as COUI
 * does for the preferences of a group (`COUICardListHelper.getPositionInGroup(Preference)`): the
 * first row is [CardListPosition.Head], the last [CardListPosition.Tail], a single row
 * [CardListPosition.Full]. Each row reads it through [LocalCardListPosition] as the default of its
 * `cardListPosition`, so the rounded outer edges get the COUI head / tail padding without the
 * caller numbering the rows; a row that passes `cardListPosition` explicitly keeps its own.
 *
 * The group also draws the COUI row dividers (`COUIPreferenceItemDecoration`): below every
 * [BasicComponent] row whose position is [CardListPosition.Head] or [CardListPosition.Middle], a
 * [DividerDefaults.Thickness] line in [DividerDefaults.DividerColor] that starts at the row's title
 * and ends [DividerDefaults.CardInset] before the card edge. The divider is hidden when that row or
 * the row below it is `selected`, or has the menu of a [SwipeActionItem] out. Pressing a row fades
 * the dividers on both of its sides out on a response 0.15 / bounce 0 spring and back in once the
 * press ends (`COUIRecyclerDividerManager`, on by default in `COUIRecyclerView`). Rows that are not
 * a [BasicComponent] (or a preference built on it) draw no divider, so do not add
 * [HorizontalDivider]s between the rows yourself.
 *
 * Each row is wrapped in a `Column`, emitted straight into the parent (for example the `Column` of
 * a [Card]). Rows can be added conditionally; positions follow the rows present.
 *
 * @param showDividers Whether the group draws row dividers at all
 *   (COUIPreferenceFragment `mEnableInternalDivider`).
 * @param content Declares the rows with [CardListGroupScope.item].
 */
@Composable
fun CardListGroup(
    showDividers: Boolean = true,
    content: CardListGroupScope.() -> Unit,
) {
    val items = CardListGroupScopeImpl().apply(content).items
    val keys = items.mapIndexed { index, item -> item.key ?: CardListGroupIndexKey(index) }
    // Row slots outlive recompositions by key, so a row's neighbor can read its selected state.
    val slotsByKey = remember { mutableMapOf<Any, CardListItemState>() }
    val slots = keys.map { slotsByKey.getOrPut(it) { CardListItemState() } }
    slotsByKey.keys.retainAll(keys.toSet())
    val dividerColor = DividerDefaults.DividerColor
    items.forEachIndexed { index, item ->
        key(keys[index]) {
            val slot = slots[index]
            val next = slots.getOrNull(index + 1)
            val dividerModifier = if (showDividers && item.showDivider) {
                Modifier.cardListRowDivider(slot, next, dividerColor)
            } else {
                Modifier
            }
            CompositionLocalProvider(
                LocalCardListPosition provides cardListPositionOf(index, items.size),
                LocalCardListItem provides slot,
            ) {
                Column(modifier = dividerModifier) {
                    item.content()
                }
            }
        }
    }
}

/**
 * What a [BasicComponent] reports to its [CardListGroup] row: whether it is there at all, its
 * position and selected state, where its title starts and how far a press has faded the dividers
 * around it.
 */
@Stable
internal class CardListItemState {
    private var attached by mutableIntStateOf(0)
    var selected by mutableStateOf(false)
    var position by mutableStateOf(CardListPosition.None)

    /** The distance in px from the row's start edge to its title, or -1 before the first layout. */
    var dividerStart by mutableIntStateOf(-1)

    /**
     * Whether the row hides the dividers on both of its sides at once, as `COUISlideView` does
     * through `ICOUIDividerDecoration.drawDivider` while its menu is out or the row is deleted.
     */
    var dividerSuppressed by mutableStateOf(false)

    /**
     * How far a press has faded out the dividers on both sides of the row: 0 shows them, 1 hides
     * them. Only read while drawing.
     */
    var pressHide by mutableFloatStateOf(0f)
        private set

    private var pressHideVelocity = 0f

    val isAttached: Boolean get() = attached > 0

    fun attach() {
        attached++
    }

    fun detach() {
        attached--
        if (attached <= 0) {
            attached = 0
            selected = false
        }
    }

    /**
     * Fades the dividers on both sides of the row out while [interactionSource] reports a touch
     * ([COUITouchInteraction], from the touch down) or a press, and back in once both end, after
     * `COUIRecyclerDividerManager`: on touch down the shared divider alpha springs from its current
     * value to 0, on release (or once the list takes the gesture over) back to the
     * `couiColorDivider` alpha, on a `COUISpringForce` of response 0.15 / bounce 0. Returns only when
     * cancelled, leaving the dividers shown.
     */
    suspend fun hideDividersWhilePressed(interactionSource: InteractionSource) {
        try {
            coroutineScope {
                val presses = mutableListOf<PressInteraction.Press>()
                val touches = mutableListOf<COUITouchInteraction.Enter>()
                var animation: Job? = null
                interactionSource.interactions.collect { interaction ->
                    when (interaction) {
                        is PressInteraction.Press -> presses += interaction
                        is PressInteraction.Release -> presses -= interaction.press
                        is PressInteraction.Cancel -> presses -= interaction.press
                        is COUITouchInteraction.Enter -> touches += interaction
                        is COUITouchInteraction.Exit -> touches -= interaction.enter
                        else -> return@collect
                    }
                    val target = if (presses.isEmpty() && touches.isEmpty()) 0f else 1f
                    animation?.cancel()
                    animation = launch {
                        animate(pressHide, target, pressHideVelocity, DividerPressHideSpring) { value, velocity ->
                            pressHide = value.coerceIn(0f, 1f)
                            pressHideVelocity = velocity
                        }
                        pressHideVelocity = 0f
                    }
                }
            }
        } finally {
            pressHide = 0f
            pressHideVelocity = 0f
        }
    }
}

/**
 * Draws the divider below a row, over its content, after `COUIDividerItemDecoration.onDrawOver`
 * with `COUIPreference.drawDivider`, `COUIPreferenceItemDecoration.shouldHideDividerWhenSelected`
 * and the pressed-row alpha of `COUIRecyclerDividerManager`.
 */
private fun Modifier.cardListRowDivider(
    slot: CardListItemState,
    next: CardListItemState?,
    color: Color,
): Modifier = drawWithContent {
    drawContent()
    if (!slot.isAttached) return@drawWithContent
    val position = slot.position
    if (position != CardListPosition.Head && position != CardListPosition.Middle) return@drawWithContent
    if (slot.selected || next?.selected == true) return@drawWithContent
    // COUISlideView.drawDivider: a row with its menu out hides the dividers on both of its sides.
    if (slot.dividerSuppressed || next?.dividerSuppressed == true) return@drawWithContent
    // COUIDividerItemDecoration: the dividers below the pressed row and above it take the
    // press-hide alpha (mPressDividerPos, mPressDividerPos - 1).
    val alpha = 1f - max(slot.pressHide, next?.pressHide ?: 0f)
    if (alpha <= 0f) return@drawWithContent
    // COUIRecyclerView: max(1px, round(coui_list_divider_height)).
    val stroke = DividerDefaults.Thickness.toPx().roundToInt().coerceAtLeast(1).toFloat()
    val endInset = DividerDefaults.CardInset.toPx()
    val start = slot.dividerStart.takeIf { it >= 0 }?.toFloat() ?: endInset
    val end = size.width - endInset
    if (end <= start) return@drawWithContent
    val left = if (layoutDirection == LayoutDirection.Rtl) size.width - end else start
    drawRect(
        color = color,
        topLeft = Offset(left, size.height - stroke),
        size = Size(end - start, stroke),
        alpha = alpha.coerceAtMost(1f),
    )
}

/** COUIRecyclerDividerManager SPRING_RESPONSE 0.15 / SPRING_BOUNCE 0. */
private val DividerPressHideSpring = folmeSpring<Float>(damping = 1f, response = 0.15f)

private class CardListGroupItem(val key: Any?, val showDivider: Boolean, val content: @Composable () -> Unit)

/** The key of a row given no key, so it never collides with a caller's key. */
private data class CardListGroupIndexKey(val index: Int)

private class CardListGroupScopeImpl : CardListGroupScope {
    val items = mutableListOf<CardListGroupItem>()

    override fun item(key: Any?, showDivider: Boolean, content: @Composable () -> Unit) {
        items += CardListGroupItem(key, showDivider, content)
    }
}
