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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
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
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
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
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

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

    /** Whether the menu is (partly) revealed. */
    val isOpen: Boolean get() = revealOffset > 0f

    /** Whether the delete animation is running; dragging is ignored meanwhile. */
    var isDeleting by mutableStateOf(false)
        internal set

    /** The width of all actions together (COUISlideView mHolderWidth), written by the measure pass. */
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
            animate(revealOffset, target, initialVelocity, spec) { value, _ -> revealOffset = value }
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
 * COUISlideView in its default rectangular menu style (MENU_ITEM_NORMAL_RECT_STYLE). Dragging
 * towards the start reveals the actions at the end edge; past the full menu the drag is damped
 * and the actions spread apart proportionally. On release the row settles open or closed on a
 * damping-1 / stiffness-200 spring, choosing by fling direction (beyond 1000 px/s) or by
 * whether more than half of the menu is showing. Tapping the content while the menu is open
 * closes it.
 *
 * With [onDelete], the end-most action is the built-in delete action (a trash icon on
 * couiColorError, COUISlideView.setDeleteEnable). Clicking it plays COUISlideDeleteAnimation:
 * the red fill grows across the row while the content slides out (330ms) and the row height
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
    val slotWidths = remember { FloatArray(MAX_ACTIONS) }

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
            val velocityTracker = VelocityTracker()
            detectHorizontalDragGestures(
                onDragStart = {
                    velocityTracker.resetTracking()
                    scope.launch { state.stopAnimation() }
                },
                onDragEnd = {
                    val velocity = velocityTracker.calculateVelocity().x.let { if (isRtl) it else -it }
                    settle(state, velocity, fastDelete, scope, startDelete, density)
                },
                onDragCancel = {
                    settle(state, 0f, fastDelete, scope, startDelete, density)
                },
                onHorizontalDrag = { change, dragAmount ->
                    velocityTracker.addPosition(change.uptimeMillis, change.position)
                    change.consume()
                    state.dragBy(if (isRtl) dragAmount else -dragAmount)
                },
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
                // COUISlideView.drawItemBackground: one fill from the content edge to the end.
                drawRectMirrored(colors.containerColor, width - reveal, width, isRtl)
                if (hasOwnColors) {
                    val menu = state.menuWidth
                    val spread = if (state.isDeleting) 0f else (reveal - menu).coerceAtLeast(0f)
                    val gap = (spread / (actionCount + 1f)).roundToInt().toFloat()
                    for (v in 0 until actionCount) {
                        val start = slotStart(v, actionCount, slotWidths, reveal, width, menu, state)
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
            if (v < MAX_ACTIONS) slotWidths[v] = placeable.width.toFloat()
            menu += placeable.width
        }
        state.menuWidth = menu
        state.itemWidth = width.toFloat()
        val overlayPlaceable = measurables[actionCount + 1].measure(Constraints.fixed(width, contentHeight))
        val height = (contentHeight * state.heightFraction).roundToInt()
        layout(width, constraints.constrainHeight(height)) {
            val reveal = state.revealOffset
            actionPlaceables.forEachIndexed { v, placeable ->
                val x = slotStart(v, actionCount, slotWidths, reveal, width.toFloat(), menu, state)
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
 * - while dragging past the menu the actions spread by `k * over / (count + 1)`;
 * - during the delete started from the menu the actions stay where the full menu put them;
 * - during a fast delete the spread frozen at release keeps growing as the content leaves.
 */
private fun slotStart(
    v: Int,
    count: Int,
    widths: FloatArray,
    reveal: Float,
    width: Float,
    menu: Float,
    state: SwipeActionItemState,
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

        else -> width - reveal + k * (reveal - menu).coerceAtLeast(0f) / (count + 1f)
    }
    return base + before
}

private fun DrawScope.drawRectMirrored(color: Color, left: Float, right: Float, isRtl: Boolean) {
    val l = if (isRtl) size.width - right else left
    val r = if (isRtl) size.width - left else right
    if (r <= l) return
    drawRect(color = color, topLeft = Offset(l, 0f), size = Size(r - l, size.height))
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

    /** The text style of the labels (COUI coui_slide_view_text_size 15sp). */
    @Composable
    fun textStyle(): TextStyle = COUITheme.textStyles.body1.copy(fontSize = 15.sp)

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

/** The most actions whose slot widths are tracked for drawing. */
private const val MAX_ACTIONS = 16
