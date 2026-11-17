// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.squircle.addLegacySquircleRect
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sign

/**
 * One action revealed behind a [SwipeActionItem], the counterpart of a `COUISlideMenuItem`.
 *
 * @param label The text drawn in the middle of the action (COUISlideMenuItem.mText). The slot
 *   widens to fit it plus [SwipeActionItemDefaults.ActionTextPadding] on each side.
 * @param icon The icon drawn in the middle of the action (COUISlideMenuItem.mIcon), at its
 *   intrinsic size.
 * @param color The fill behind this action. [Color.Unspecified] keeps the shared
 *   [SwipeActionItemColors.containerColor] (COUISlideView draws one color for all items unless
 *   `addColor` gave every item its own).
 * @param contentDescription The accessibility label of the action. Defaults to [label].
 */
@Immutable
data class SwipeAction(
    val label: String? = null,
    val icon: ImageVector? = null,
    val color: Color = Color.Unspecified,
    val contentDescription: String? = label,
)

/** How the actions behind a [SwipeActionItem] are drawn (COUISlideView.setMenuItemStyle). */
enum class SwipeActionMenuStyle {
    /**
     * MENU_ITEM_NORMAL_RECT_STYLE: one fill runs from the content edge to the end of the row and
     * the actions sit side by side; past the full menu they spread apart.
     */
    Rect,

    /**
     * MENU_ITEM_ROUND_RECT_STYLE: every action is its own rounded rectangle, separated by
     * [SwipeActionItemDefaults.RoundRectActionGap] and inset by
     * [SwipeActionItemDefaults.RoundRectStartMargin] / [SwipeActionItemDefaults.RoundRectEndMargin];
     * the menu stays packed while over-sliding.
     */
    RoundRect,
}

/**
 * The state of a [SwipeActionItem]: how far its menu is revealed, and the delete animation.
 *
 * Create it with [rememberSwipeActionItemState]; hoist it to close the item from outside, e.g.
 * when another item opens or the list scrolls (COUISlideView.shrink).
 */
@Stable
class SwipeActionItemState {
    /** How far the content is slid away from its rest position, in pixels (COUISlideView scrollX). */
    var revealOffset by mutableFloatStateOf(0f)
        private set

    /**
     * Whether the menu is (partly) revealed. Derived, so reading it in composition does not
     * recompose on every frame of a drag.
     */
    val isOpen: Boolean by derivedStateOf { revealOffset > 0f }

    /** Whether the delete animation is running; dragging is ignored meanwhile. */
    var isDeleting by mutableStateOf(false)
        internal set

    /**
     * The width of the whole menu (COUISlideView mHolderWidth): all actions together, plus the
     * gaps and margins of [SwipeActionMenuStyle.RoundRect]. Written by the measure pass.
     */
    internal var menuWidth = 0f

    /** The item width, written by the measure pass. */
    internal var itemWidth = 0f

    /** The fraction of the content height that is still laid out (the delete collapse). */
    internal var heightFraction by mutableFloatStateOf(1f)

    /** The reveal offset at release when a fast delete started (COUISlideView mUpScrollX). */
    internal var fastDeleteUpOffset by mutableFloatStateOf(-1f)

    private val mutex = MutatorMutex()

    /**
     * Closes the menu like COUISlideView.shrink: a Scroller run with the (0.133, 0, 0.3, 1) curve
     * lasting three milliseconds per pixel, capped at 200ms.
     */
    suspend fun close() {
        if (isDeleting) return
        val duration = min((abs(revealOffset) * 3f).roundToInt(), SHRINK_MAX_DURATION_MILLIS)
        animateRevealTo(0f, tween(duration, easing = SlideEase))
    }

    /** Opens the menu on the release spring (damping 1, stiffness 200). */
    suspend fun open() {
        if (isDeleting || menuWidth <= 0f) return
        animateRevealTo(menuWidth, SettleSpring)
    }

    /** Jumps back to the closed, fully expanded rest state without animation. */
    suspend fun snapClose() {
        mutex.mutate(MutatePriority.PreventUserInput) {
            revealOffset = 0f
            heightFraction = 1f
            fastDeleteUpOffset = -1f
            isDeleting = false
        }
    }

    internal suspend fun stopAnimation() {
        mutex.mutate(MutatePriority.UserInput) {}
    }

    internal suspend fun animateRevealTo(target: Float, spec: AnimationSpec<Float>, initialVelocity: Float = 0f) {
        mutex.mutate {
            animate(revealOffset, target, initialVelocity, spec) { value, _ ->
                // A fling towards the closed state must not push the content past its rest edge.
                revealOffset = value.coerceAtLeast(0f)
            }
        }
    }

    /**
     * Applies a drag delta, in reveal direction. Within the menu the content follows the finger
     * 1:1; beyond it COUISlideView's onTouchEvent damps the delta with
     * `COUIPhysicalAnimationUtil.calcRealOffset`: `delta * (1 - min(over / (width - menu), 1)) * 2 / 5`.
     */
    internal fun dragBy(delta: Float) {
        if (isDeleting) return
        var effective = delta
        val menu = menuWidth
        if (revealOffset >= menu && delta > 0f) {
            val range = (itemWidth - menu).coerceAtLeast(1f)
            effective = delta * (1f - min((revealOffset - menu) / range, 1f)) * OVER_SLIDE_DAMPING
        }
        revealOffset = (revealOffset + effective).coerceAtLeast(0f)
    }

    /** Plays the COUISlideDeleteAnimation: the content slides out while the row collapses. */
    internal suspend fun runDeleteAnimation(fastDelete: Boolean) {
        isDeleting = true
        fastDeleteUpOffset = if (fastDelete) revealOffset else -1f
        mutex.mutate(MutatePriority.PreventUserInput) {
            coroutineScope {
                launch {
                    animate(revealOffset, itemWidth, 0f, tween(SLIDE_OUT_DURATION_MILLIS, easing = DeleteEase)) { value, _ ->
                        revealOffset = value
                    }
                }
                launch {
                    delay(COLLAPSE_DELAY_MILLIS)
                    animate(1f, 0f, 0f, tween(COLLAPSE_DURATION_MILLIS, easing = DeleteEase)) { value, _ ->
                        heightFraction = value
                    }
                }
            }
        }
    }
}

/** Creates and remembers a [SwipeActionItemState]. */
@Composable
fun rememberSwipeActionItemState(): SwipeActionItemState = remember { SwipeActionItemState() }

/**
 * A [SwipeActionItem] component with COUI style.
 *
 * A list row that slides sideways to reveal a menu of actions behind it, mirroring ColorOS's
 * COUISlideView. Dragging towards the start reveals the actions at the end edge; past the full
 * menu the drag is damped and, in [SwipeActionMenuStyle.Rect], the actions spread apart
 * proportionally. [SwipeActionMenuStyle.RoundRect] draws every action as its own rounded
 * rectangle instead and keeps the menu packed. On release the row settles open or closed on a
 * damping-1 / stiffness-200 spring, choosing by fling direction (beyond 1000 px/s) or by
 * whether more than half of the menu is showing. Tapping the content while the menu is open
 * closes it.
 *
 * With [onDelete], the end-most action is the built-in delete action (a trash icon on
 * couiColorError, COUISlideView.setDeleteEnable). Clicking it plays COUISlideDeleteAnimation:
 * the red fill (in [SwipeActionMenuStyle.RoundRect] without own action colors, the rounded
 * rectangle) grows across the row while the content slides out (330ms) and the row height
 * collapses to zero (400ms, 10ms later), both on the (0.3, 0, 0.1, 1) curve; [onDelete] is
 * called when the collapse ends, at which point the caller removes the row. [fastDeleteEnabled]
 * also deletes on an over-slide past the menu by 48dp, or by 30dp with a fast fling.
 *
 * @param actions The actions, in visual order from start to end. The delete action, when
 *   [onDelete] is set, follows them at the end.
 * @param onActionClick Called with the index in [actions] of the clicked action. The menu stays
 *   open, like COUISlideView; call [SwipeActionItemState.close] to close it.
 * @param modifier The modifier to be applied to the [SwipeActionItem].
 * @param state The [SwipeActionItemState] of the item.
 * @param enabled Whether the item can be swiped (COUISlideView.setSlideEnable).
 * @param onDelete Adds the built-in delete action, called once the delete animation finishes.
 * @param fastDeleteEnabled Whether over-sliding deletes the row (COUISlideView.enableFastDelete).
 *   Only takes effect with [onDelete].
 * @param deleteContentDescription The accessibility label of the delete action.
 * @param menuStyle How the actions are drawn (COUISlideView.setMenuItemStyle).
 * @param actionMinWidth The minimum width of each action (COUI coui_slideview_menuitem_width).
 * @param colors The [SwipeActionItemColors] of the menu.
 * @param textStyle The text style of the action labels.
 * @param content The row content that slides over the actions. It should have an opaque
 *   background, since the actions are drawn behind it.
 */
@Composable
fun SwipeActionItem(
    actions: List<SwipeAction>,
    onActionClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    state: SwipeActionItemState = rememberSwipeActionItemState(),
    enabled: Boolean = true,
    onDelete: (() -> Unit)? = null,
    fastDeleteEnabled: Boolean = false,
    deleteContentDescription: String? = null,
    menuStyle: SwipeActionMenuStyle = SwipeActionMenuStyle.Rect,
    actionMinWidth: Dp = SwipeActionItemDefaults.ActionMinWidth,
    colors: SwipeActionItemColors = SwipeActionItemDefaults.swipeActionItemColors(),
    textStyle: TextStyle = SwipeActionItemDefaults.textStyle(),
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val layoutDirection = LocalLayoutDirection.current
    val isRtl = layoutDirection == LayoutDirection.Rtl
    val currentOnActionClick by rememberUpdatedState(onActionClick)
    val currentOnDelete by rememberUpdatedState(onDelete)
    val hasDelete = onDelete != null
    val fastDelete = fastDeleteEnabled && hasDelete

    // Visual order start -> end; COUISlideView keeps the delete item at index 0, i.e. at the end.
    val menuActions = if (hasDelete) {
        actions + SwipeAction(icon = SlideDeleteIcon, contentDescription = deleteContentDescription)
    } else {
        actions
    }
    val actionColors = menuActions.map { if (it.color.isSpecified) it.color else colors.containerColor }
    val hasOwnColors = menuActions.any { it.color.isSpecified }
    val actionCount = menuActions.size
    // The slot widths, written by the measure pass and read by the draw pass of the same frame.
    val slotWidths = remember(actionCount) { FloatArray(actionCount) }
    val roundRect = menuStyle == SwipeActionMenuStyle.RoundRect
    val actionPath = remember { Path() }

    val startDelete: (Boolean) -> Unit = { isFast ->
        if (!state.isDeleting) {
            scope.launch {
                state.runDeleteAnimation(isFast)
                currentOnDelete?.invoke()
                state.snapClose()
            }
        }
    }

    val dragModifier = if (enabled && actionCount > 0) {
        Modifier.pointerInput(state, isRtl, fastDelete) {
            // Positive values point in the reveal direction (towards the start edge).
            val toReveal = if (isRtl) 1f else -1f
            detectSwipeGestures(
                canStart = { dx -> !state.isDeleting && (state.revealOffset > 0f || dx * toReveal > 0f) },
                onDragStart = {
                    // Undispatched, so a running settle animation stops before the first delta.
                    scope.launch(start = CoroutineStart.UNDISPATCHED) { state.stopAnimation() }
                },
                onDrag = { dx -> state.dragBy(dx * toReveal) },
                onDragEnd = { velocityX -> settle(state, velocityX * toReveal, fastDelete, scope, startDelete, density) },
                onDragCancel = { settle(state, 0f, fastDelete, scope, startDelete, density) },
            )
        }
    } else {
        Modifier
    }

    Layout(
        modifier = modifier
            .clipToBounds()
            .then(dragModifier)
            .drawBehind {
                val reveal = state.revealOffset
                if (reveal <= 0f || actionCount == 0) return@drawBehind
                val width = size.width
                val menu = state.menuWidth
                if (roundRect) {
                    val startMargin = SwipeActionItemDefaults.RoundRectStartMargin.roundToPx().toFloat()
                    val actionGap = SwipeActionItemDefaults.RoundRectActionGap.roundToPx().toFloat()
                    // COUISlideView caps couiRoundCornerM at half of the row height.
                    val radius = min(size.height / 2f, SwipeActionItemDefaults.RoundRectCornerRadius.roundToPx().toFloat())
                    if (state.isDeleting && !hasOwnColors) {
                        // drawItemBackground with mSlideDeleteInRoundMode / mSlideDelete: a single
                        // rounded fill from the start margin to the end margin follows the content.
                        val left = width - reveal + startMargin
                        val right = width - SwipeActionItemDefaults.RoundRectEndMargin.roundToPx()
                        drawRoundRectMirrored(actionPath, colors.containerColor, left, right, radius, isRtl)
                    } else {
                        for (v in 0 until actionCount) {
                            val start = startMargin + v * actionGap +
                                slotStart(v, actionCount, slotWidths, reveal, width, menu, state, spreads = false)
                            drawRoundRectMirrored(actionPath, actionColors[v], start, start + slotWidths[v], radius, isRtl)
                        }
                    }
                    return@drawBehind
                }
                // COUISlideView.drawItemBackground: one fill from the content edge to the end.
                drawRectMirrored(colors.containerColor, width - reveal, width, isRtl)
                if (hasOwnColors) {
                    val spread = if (state.isDeleting) 0f else (reveal - menu).coerceAtLeast(0f)
                    val gap = (spread / (actionCount + 1f)).roundToInt().toFloat()
                    for (v in 0 until actionCount) {
                        val start = slotStart(v, actionCount, slotWidths, reveal, width, menu, state, spreads = true)
                        val end = start + slotWidths[v]
                        // COUISlideView widens each colored slot by half of the spread gap, the
                        // start-most one by a whole gap, and the end-most one to the edge.
                        val left = if (v == 0 && actionCount > 1) start - gap else start - gap / 2f
                        val right = if (v == actionCount - 1) width else end + gap / 2f
                        drawRectMirrored(actionColors[v], left, right, isRtl)
                    }
                }
            },
        content = {
            menuActions.forEachIndexed { index, action ->
                val isDeleteAction = hasDelete && index == actionCount - 1
                SwipeActionSlot(
                    action = action,
                    minWidth = actionMinWidth,
                    contentColor = colors.contentColor,
                    textStyle = textStyle,
                    onClick = {
                        if (state.isDeleting) return@SwipeActionSlot
                        if (isDeleteAction) startDelete(false) else currentOnActionClick(index)
                    },
                )
            }
            Box(propagateMinConstraints = true) { content() }
            // While open, a tap on the content closes the menu instead of reaching the content.
            if (state.isOpen && !state.isDeleting) {
                Box(
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { scope.launch { state.close() } },
                )
            } else {
                Box(Modifier)
            }
        },
    ) { measurables, constraints ->
        val contentPlaceable = measurables[actionCount].measure(constraints.copy(minHeight = 0))
        val width = contentPlaceable.width
        val contentHeight = contentPlaceable.height
        val actionConstraints = Constraints(minHeight = contentHeight, maxHeight = contentHeight)
        val actionPlaceables = List(actionCount) { measurables[it].measure(actionConstraints) }
        var menu = 0f
        actionPlaceables.forEachIndexed { v, placeable ->
            slotWidths[v] = placeable.width.toFloat()
            menu += placeable.width
        }
        // COUISlideView.itemWidthChange: the round rect menu adds its gaps and both margins.
        val startMargin = if (roundRect) SwipeActionItemDefaults.RoundRectStartMargin.roundToPx() else 0
        val actionGap = if (roundRect) SwipeActionItemDefaults.RoundRectActionGap.roundToPx() else 0
        if (roundRect && actionCount > 0) {
            menu += (actionCount - 1) * actionGap + startMargin + SwipeActionItemDefaults.RoundRectEndMargin.roundToPx()
        }
        state.menuWidth = menu
        state.itemWidth = width.toFloat()
        val overlayPlaceable = measurables[actionCount + 1].measure(Constraints.fixed(width, contentHeight))
        val height = (contentHeight * state.heightFraction).roundToInt()
        layout(width, constraints.constrainHeight(height)) {
            val reveal = state.revealOffset
            actionPlaceables.forEachIndexed { v, placeable ->
                val x = startMargin + v * actionGap +
                    slotStart(v, actionCount, slotWidths, reveal, width.toFloat(), menu, state, spreads = !roundRect)
                placeable.placeRelative(x.roundToInt(), 0)
            }
            contentPlaceable.placeRelative(-reveal.roundToInt(), 0)
            overlayPlaceable.placeRelative(-reveal.roundToInt(), 0)
        }
    }
}

/**
 * The left edge (in LTR coordinates) of the action at visual index [v], following
 * COUISlideView.drawItemBackground. COUI index `c = count - 1 - v` and `k = count - c = v + 1`:
 * - while dragging past the menu the actions spread by `k * over / (count + 1)` when [spreads]
 *   (the round rect style passes false: COUISlideView zeroes that term with mIsMenuRoundStyle);
 * - during the delete started from the menu the actions stay where the full menu put them;
 * - during a fast delete the spread frozen at release keeps growing as the content leaves, in
 *   both styles.
 *
 * The round rect style adds its start margin and gaps on top of the returned value, like
 * COUISlideView offsetting each icon by `(count - 1 - c) * gap + startMargin`.
 */
private fun slotStart(
    v: Int,
    count: Int,
    widths: FloatArray,
    reveal: Float,
    width: Float,
    menu: Float,
    state: SwipeActionItemState,
    spreads: Boolean,
): Float {
    var before = 0f
    for (i in 0 until v) before += widths[i]
    val k = v + 1
    val upOffset = state.fastDeleteUpOffset
    val base = when {
        state.isDeleting && upOffset >= 0f -> {
            val spread = k * (upOffset - menu) / (count + 1f)
            val remaining = (width - upOffset).coerceAtLeast(1f)
            (width - upOffset) + spread + spread * (reveal - upOffset) / remaining
        }

        state.isDeleting -> width - min(reveal, menu)

        spreads -> width - reveal + k * (reveal - menu).coerceAtLeast(0f) / (count + 1f)

        else -> width - reveal
    }
    return base + before
}

private fun DrawScope.drawRectMirrored(color: Color, left: Float, right: Float, isRtl: Boolean) {
    val l = if (isRtl) size.width - right else left
    val r = if (isRtl) size.width - left else right
    if (r <= l) return
    drawRect(color = color, topLeft = Offset(l, 0f), size = Size(r - l, size.height))
}

/**
 * Fills one round rect action of the full item height, drawn like COUISlideView with
 * `COUIShapePath.getRoundRectPath` ([addLegacySquircleRect]); [path] is reused across frames.
 */
private fun DrawScope.drawRoundRectMirrored(
    path: Path,
    color: Color,
    left: Float,
    right: Float,
    radius: Float,
    isRtl: Boolean,
) {
    val l = if (isRtl) size.width - right else left
    val r = if (isRtl) size.width - left else right
    if (r <= l) return
    path.reset()
    path.addLegacySquircleRect(width = r - l, height = size.height, cornerRadius = radius, left = l)
    drawPath(path = path, color = color)
}

/**
 * COUISlideView's drag pickup (onInterceptTouchEvent): the row takes the gesture once the
 * horizontal travel passes the touch slop while being more than twice the vertical travel, and
 * gives it up for good once the vertical travel passes the slop first, so a mostly vertical drag
 * keeps scrolling the parent list. Unlike the View, a closed row also leaves drags towards the
 * end edge to its parent ([canStart] returns false), so it does not block a horizontal pager.
 * The first delta excludes the slop, like COUISlideView offsetting mLastMotionX by mTouchSlop.
 */
private suspend fun PointerInputScope.detectSwipeGestures(
    canStart: (dx: Float) -> Boolean,
    onDragStart: () -> Unit,
    onDrag: (dx: Float) -> Unit,
    onDragEnd: (velocityX: Float) -> Unit,
    onDragCancel: () -> Unit,
) = awaitEachGesture {
    val down = awaitFirstDown(requireUnconsumed = false)
    val slop = viewConfiguration.touchSlop
    var total = Offset.Zero
    var slopChange: PointerInputChange? = null
    while (slopChange == null) {
        val event = awaitPointerEvent()
        val change = event.changes.firstOrNull { it.id == down.id } ?: return@awaitEachGesture
        if (!change.pressed || change.isConsumed) return@awaitEachGesture
        total += change.positionChange()
        val dx = abs(total.x)
        val dy = abs(total.y)
        if (dx > slop && dx * PICKUP_RATIO > dy) {
            if (!canStart(total.x)) return@awaitEachGesture
            slopChange = change
        } else if (dy > slop) {
            return@awaitEachGesture
        } else {
            // A parent that consumed the move (e.g. a list that started scrolling) owns it.
            awaitPointerEvent(PointerEventPass.Final)
            if (change.isConsumed) return@awaitEachGesture
        }
    }
    val velocityTracker = VelocityTracker()
    velocityTracker.addPointerInputChange(slopChange)
    slopChange.consume()
    onDragStart()
    onDrag(total.x - sign(total.x) * slop)
    val lifted = horizontalDrag(slopChange.id) { change ->
        velocityTracker.addPointerInputChange(change)
        onDrag(change.positionChange().x)
        change.consume()
    }
    if (lifted) onDragEnd(velocityTracker.calculateVelocity().x) else onDragCancel()
}

/** COUISlideView's ACTION_UP decision. */
private fun settle(
    state: SwipeActionItemState,
    velocity: Float,
    fastDelete: Boolean,
    scope: CoroutineScope,
    startDelete: (Boolean) -> Unit,
    density: Float,
) {
    if (state.isDeleting) return
    val reveal = state.revealOffset
    val menu = state.menuWidth
    if (fastDelete) {
        val overSlide = SwipeActionItemDefaults.FastDeleteOverSlide.value * density
        val quick = SwipeActionItemDefaults.FastDeleteQuickSlop.value * density
        if ((velocity > FLING_VELOCITY && reveal > menu + quick) || reveal > menu + overSlide) {
            startDelete(true)
            return
        }
    }
    // The fast delete branch only compares against half of the menu.
    val target = when {
        !fastDelete && velocity > FLING_VELOCITY -> menu
        !fastDelete && velocity < -FLING_VELOCITY -> 0f
        reveal - menu * 0.5f > 0f -> menu
        else -> 0f
    }
    scope.launch { state.animateRevealTo(target, SettleSpring, velocity) }
}

@Composable
private fun SwipeActionSlot(
    action: SwipeAction,
    minWidth: Dp,
    contentColor: Color,
    textStyle: TextStyle,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = minWidth)
            .clickable(
                interactionSource = null,
                indication = null,
                role = Role.Button,
                onClickLabel = action.contentDescription,
                onClick = onClick,
            )
            .padding(horizontal = SwipeActionItemDefaults.ActionTextPadding),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (action.icon != null) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.contentDescription,
                    tint = contentColor,
                )
            }
            if (action.label != null) {
                Text(
                    text = action.label,
                    color = contentColor,
                    style = textStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                )
            }
        }
    }
}

/** Contains default values used by [SwipeActionItem]. */
object SwipeActionItemDefaults {
    /** The minimum width of an action (COUI coui_slideview_menuitem_width). */
    val ActionMinWidth = 80.dp

    /**
     * The padding on each side of an action label: a label wider than [ActionMinWidth] widens
     * its action to `text + 2 x 12dp` (COUI coui_slide_view_text_padding, COUISlideView.addItem).
     */
    val ActionTextPadding = 12.dp

    /** The over-slide past the menu that deletes on release (COUI coui_slideview_over_slide_delete). */
    val FastDeleteOverSlide = 48.dp

    /** The over-slide that deletes with a fast fling (COUI coui_slideview_quick_delete). */
    val FastDeleteQuickSlop = 30.dp

    /**
     * The corner radius of each [SwipeActionMenuStyle.RoundRect] action: COUISlideView reads the
     * couiRoundCornerM theme attribute (coui_round_corner_m) and caps it at half of the row height.
     */
    val RoundRectCornerRadius = COUIRoundCorner.M

    /** The gap between two [SwipeActionMenuStyle.RoundRect] actions (COUI coui_slide_view_menuitem_gap_size). */
    val RoundRectActionGap = 4.dp

    /**
     * The space between the content edge and the first [SwipeActionMenuStyle.RoundRect] action
     * (COUI coui_slide_view_menuitem_start_margin).
     */
    val RoundRectStartMargin = 8.dp

    /**
     * The space between the last [SwipeActionMenuStyle.RoundRect] action and the end of the row
     * (COUI coui_slide_view_menuitem_end_margin).
     */
    val RoundRectEndMargin = 12.dp

    /** The text style of the labels (COUI coui_slide_view_text_size 15sp). */
    @Composable
    fun textStyle(): TextStyle = COUITheme.textStyles.bodyXL.copy(fontSize = 15.sp)

    /**
     * The default [SwipeActionItemColors]: the menu fill is COUISlideView's default
     * itemBackgroundColor couiColorError, the labels and icons use coui_slideview_textcolor
     * (#F9F9F9, i.e. white on the error fill).
     */
    @Composable
    fun swipeActionItemColors(
        containerColor: Color = COUITheme.colorScheme.error,
        contentColor: Color = COUITheme.colorScheme.onError,
    ): SwipeActionItemColors = remember(containerColor, contentColor) {
        SwipeActionItemColors(containerColor = containerColor, contentColor = contentColor)
    }
}

/**
 * Colors used by a [SwipeActionItem].
 *
 * @param containerColor The fill behind the actions without their own [SwipeAction.color].
 * @param contentColor The color of the action icons and labels.
 */
@Immutable
data class SwipeActionItemColors(
    val containerColor: Color,
    val contentColor: Color,
)

/** The trash icon of the delete action, ported from COUI coui_slide_view_delete.xml (24dp). */
private val SlideDeleteIcon: ImageVector by lazy {
    ImageVector.Builder("SlideDelete", 24.dp, 24.dp, 24f, 24f, autoMirror = true).apply {
        listOf(
            "M9,18.45a0.69,0.69 0,0 0,0.67 -0.72L9.45,10a0.7,0.7 0,1 0,-1.4 0l0.25,7.75A0.71,0.71 0,0 0,9 18.45Z",
            "M15,18.45a0.7,0.7 0,0 0,0.72 -0.68L16,10a0.69,0.69 0,0 0,-0.67 -0.72,0.71 0.71,0 0,0 -0.73,0.68l-0.25,7.75A0.7,0.7 0,0 0,15 18.45Z",
            "M12,18.45a0.7,0.7 0,0 0,0.7 -0.7V10a0.7,0.7 0,0 0,-1.4 0v7.75A0.7,0.7 0,0 0,12 18.45Z",
            "M20.25,4.3L15.51,4.3L15,2.67A2,2 0,0 0,13.07 1.3L10.94,1.3A2,2 0,0 0,9 2.67L8.5,4.3L3.75,4.3a0.7,0.7 0,0 0,0 1.4h0.59" +
                "l0.89,15.12a2,2 0,0 0,2 1.88h9.55a2,2 0,0 0,2 -1.88L19.66,5.7h0.59a0.7,0.7 0,0 0,0 -1.4ZM10.37,3.11a0.6,0.6 0,0 1," +
                "0.57 -0.41h2.13a0.6,0.6 0,0 1,0.56 0.41L14,4.3L10,4.3ZM17.37,20.74a0.61,0.61 0,0 1,-0.6 0.56L7.23,21.3a0.6,0.6 0,0 1," +
                "-0.6 -0.56l-0.88,-15L18.26,5.74Z",
        ).forEach { data ->
            addPath(
                pathData = addPathNodes(data),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.White),
            )
        }
    }.build()
}

/** COUISlideDeleteAnimation curve PathInterpolator(0.3, 0, 0.1, 1). */
private val DeleteEase = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** COUISlideView's Scroller curve PathInterpolator(0.133, 0, 0.3, 1). */
private val SlideEase = CubicBezierEasing(0.133f, 0f, 0.3f, 1f)

/** COUISlideView.animationScrollTo: SpringForce dampingRatio 1, stiffness 200. */
private val SettleSpring = spring<Float>(dampingRatio = 1f, stiffness = 200f)

/** COUISlideView.onInterceptTouchEvent starts a drag only when `|dx| * 0.5 > |dy|`. */
private const val PICKUP_RATIO = 0.5f

/** COUISlideView's fling threshold on VelocityTracker units of 1000 (px/s). */
private const val FLING_VELOCITY = 1000f

/** `COUIPhysicalAnimationUtil.calcRealOffset` divides by 5 and doubles. */
private const val OVER_SLIDE_DAMPING = 0.4f

/** COUISlideView.smoothScrollTo caps the shrink at 200ms. */
private const val SHRINK_MAX_DURATION_MILLIS = 200

/** COUISlideDeleteAnimation SLIDE_VIEW_DURATION. */
private const val SLIDE_OUT_DURATION_MILLIS = 330

/** COUISlideDeleteAnimation ITEM_VIEW_DURATION. */
private const val COLLAPSE_DURATION_MILLIS = 400

/** COUISlideDeleteAnimation ANIMATION_DELAY. */
private const val COLLAPSE_DELAY_MILLIS = 10L
