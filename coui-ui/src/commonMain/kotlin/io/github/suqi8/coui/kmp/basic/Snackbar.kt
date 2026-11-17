// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.AccessibilityManager
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Close
import io.github.suqi8.coui.kmp.icon.basic.SearchCleanup
import io.github.suqi8.coui.kmp.squircle.SquircleShape
import io.github.suqi8.coui.kmp.squircle.isSquircleEnabled
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.COUILayoutGrid
import io.github.suqi8.coui.kmp.utils.layerOverflow
import io.github.suqi8.coui.kmp.utils.layerOverflowInner
import io.github.suqi8.coui.kmp.utils.layerOverflowOuter
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Possible durations of the [Snackbar].
 */
sealed interface SnackbarDuration {
    /** Show the Snackbar for a short period of time. */
    data object Short : SnackbarDuration

    /** Show the Snackbar for a long period of time. */
    data object Long : SnackbarDuration

    /** Show the Snackbar indefinitely until dismissed. */
    data object Indefinite : SnackbarDuration

    /** Show the Snackbar for a custom period of time. */
    data class Custom(val durationMillis: kotlin.Long) : SnackbarDuration {
        init {
            require(durationMillis > 0) { "durationMillis must be greater than 0" }
        }
    }
}

/**
 * Possible results of the [Snackbar].
 */
enum class SnackbarResult {
    /** The Snackbar was dismissed. */
    Dismissed,

    /** The Snackbar's action was performed. */
    ActionPerformed,
}

/**
 * Visuals for a [Snackbar].
 *
 * @param message text to be shown in the Snackbar
 * @param actionLabel optional action label to be shown in the Snackbar
 * @param withDismissAction whether to show a dismiss action in the Snackbar
 * @param duration duration of the Snackbar
 */
@Immutable
data class SnackbarVisuals(
    val message: String,
    val actionLabel: String?,
    val withDismissAction: Boolean,
    val duration: SnackbarDuration,
)

/**
 * Interface representing the data of a [Snackbar].
 */
interface SnackbarData {
    /** Visuals of the Snackbar. */
    val visuals: SnackbarVisuals

    /** Dismiss the Snackbar. */
    suspend fun dismiss()

    /** Perform the action of the Snackbar. */
    suspend fun performAction()
}

private enum class SnackbarSwipeToDismissValue {
    StartToEnd,
    EndToStart,
    Settled,
}

/**
 * State of the [SnackbarHost].
 *
 * It allows to show a [Snackbar] with a message and an optional action.
 */
@Stable
class SnackbarHostState {
    private val entries = mutableStateListOf<SnackbarEntry>()
    internal val currentSnackbars: List<SnackbarEntry> get() = entries
    suspend fun newestSnackbarData(): SnackbarData? = mutex.withLock {
        entries.firstOrNull { it.visible }?.data
    }

    suspend fun oldestSnackbarData(): SnackbarData? = mutex.withLock {
        entries.lastOrNull { it.visible }?.data
    }

    private val mutex = Mutex()
    private var idCounter = 0L

    internal suspend fun removeEntry(entry: SnackbarEntry) {
        mutex.withLock {
            entries.remove(entry)
        }
    }

    /**
     * Shows a [Snackbar] with the provided [message].
     *
     * @param message text to be shown in the Snackbar
     * @param actionLabel optional action label to be shown in the Snackbar
     * @param withDismissAction whether to show a dismiss action in the Snackbar
     * @param duration duration of the Snackbar
     * @return result of the Snackbar
     */
    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration = SnackbarDuration.Short,
    ): SnackbarResult {
        val result = CompletableDeferred<SnackbarResult>()
        val visuals = SnackbarVisuals(message, actionLabel, withDismissAction, duration)

        mutex.withLock {
            val currentId = ++idCounter
            val data = object : SnackbarData {
                override val visuals = visuals
                private val snackbarMutex = Mutex()
                private var completed = false

                override suspend fun dismiss() {
                    snackbarMutex.withLock {
                        if (completed) return
                        completed = true
                    }
                    if (!result.isCompleted) result.complete(SnackbarResult.Dismissed)
                    clear()
                }

                override suspend fun performAction() {
                    snackbarMutex.withLock {
                        if (completed) return
                        completed = true
                    }
                    if (!result.isCompleted) result.complete(SnackbarResult.ActionPerformed)
                    clear()
                }

                private suspend fun clear() {
                    this@SnackbarHostState.mutex.withLock {
                        val index = entries.indexOfFirst { it.id == currentId }
                        if (index != -1) entries[index] = entries[index].copy(visible = false)
                    }
                }
            }
            val entry = SnackbarEntry(currentId, data)
            entries.add(0, entry)
        }

        return result.await()
    }

    @Immutable
    internal data class SnackbarEntry(
        val id: Long,
        val data: SnackbarData,
        val visible: Boolean = true,
    )
}

/**
 * Convert [SnackbarDuration] to milliseconds, taking into account accessibility settings.
 */
internal fun SnackbarDuration.toMillis(
    hasAction: Boolean,
    accessibilityManager: AccessibilityManager?,
): Long {
    val original = when (this) {
        SnackbarDuration.Indefinite -> Long.MAX_VALUE
        SnackbarDuration.Long -> 10000L
        SnackbarDuration.Short -> 4000L
        is SnackbarDuration.Custom -> durationMillis
    }
    if (accessibilityManager == null) {
        return original
    }
    return accessibilityManager.calculateRecommendedTimeoutMillis(
        originalTimeoutMillis = original,
        containsIcons = true,
        containsText = true,
        containsControls = hasAction,
    )
}

/**
 * Host for [Snackbar]s to be shown.
 *
 * @param state state of the [SnackbarHost]
 * @param modifier modifier to be applied to the [SnackbarHost]
 * @param canSwipeToDismiss flag of can be dismissed by swipe of the current [SnackbarHost]
 * @param content content of the [SnackbarHost]
 */
@Composable
fun SnackbarHost(
    state: SnackbarHostState,
    modifier: Modifier = Modifier,
    canSwipeToDismiss: Boolean = true,
    content: @Composable (SnackbarData) -> Unit = { Snackbar(it) },
) {
    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        LazyColumn(
            reverseLayout = true,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            // 28dp + the 4dp item padding = 32dp bottom offset (COUI coui_snack_bar_margin_bottom).
            // The lazy list clips its children to its own bounds along the scroll axis, so the top
            // padding keeps room for the shadow above the topmost snack bar. The list grows upwards
            // from its bottom anchor, so the snack bar itself does not move.
            contentPadding = PaddingValues(top = SnackbarHostShadowClearance, bottom = 28.dp),
        ) {
            itemsIndexed(state.currentSnackbars, key = { _, entry -> entry.id }) { index, entry ->
                val visibleState = remember { MutableTransitionState(false) }
                val accessibilityManager = LocalAccessibilityManager.current

                val anchoredDraggableState = remember {
                    AnchoredDraggableState(
                        initialValue = SnackbarSwipeToDismissValue.Settled,
                    )
                }
                // COUISnackBar.onTouchEvent: DOWN / MOVE cancel the auto-dismiss timer, UP / CANCEL
                // restart it with the full duration.
                var isTouched by remember { mutableStateOf(false) }

                visibleState.targetState = entry.visible

                if (!visibleState.targetState && visibleState.isIdle) {
                    LaunchedEffect(entry) { state.removeEntry(entry) }
                }

                LaunchedEffect(entry, isTouched) {
                    if (isTouched) return@LaunchedEffect
                    val duration = entry.data.visuals.duration.toMillis(
                        entry.data.visuals.actionLabel != null,
                        accessibilityManager,
                    )
                    delay(duration)

                    if (anchoredDraggableState.currentValue != SnackbarSwipeToDismissValue.Settled) return@LaunchedEffect
                    entry.data.dismiss()
                }

                LaunchedEffect(anchoredDraggableState.currentValue) {
                    if (anchoredDraggableState.currentValue != SnackbarSwipeToDismissValue.Settled) {
                        entry.data.dismiss()
                    }
                }

                AnimatedVisibility(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            // Observe only: the action, dismiss icon and swipe keep their events.
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Initial)
                                    isTouched = event.changes.any { it.pressed }
                                }
                            }
                        }
                        .onSizeChanged { size ->
                            val width = size.width.toFloat()

                            val anchors = DraggableAnchors {
                                SnackbarSwipeToDismissValue.Settled at 0f
                                SnackbarSwipeToDismissValue.StartToEnd at width
                                SnackbarSwipeToDismissValue.EndToStart at -width
                            }
                            anchoredDraggableState.updateAnchors(anchors)
                        }
                        .anchoredDraggable(
                            state = anchoredDraggableState,
                            orientation = Orientation.Horizontal,
                            enabled = entry.visible && canSwipeToDismiss,
                            flingBehavior = AnchoredDraggableDefaults.flingBehavior(
                                state = anchoredDraggableState,
                                positionalThreshold = { distance: Float -> distance * 0.5f },
                            ),
                        )
                        .offset {
                            val offset = try {
                                anchoredDraggableState.requireOffset()
                            } catch (_: IllegalStateException) {
                                0f
                            }
                            IntOffset(offset.roundToInt(), 0)
                        }
                        .zIndex((state.currentSnackbars.size - index).toFloat())
                        .then(if (entry.visible) Modifier.animateItem() else Modifier)
                        // The fade renders offscreen at the layer bounds: grow them so the shadow
                        // is not cut off while the snack bar shows or dismisses.
                        .layerOverflowOuter(SnackbarShadowOverflow),
                    visibleState = visibleState,
                    // COUISnackBar.setSnackBarProgress: show scales 0.8 -> 1 and fades 0 -> 1,
                    // dismiss scales 1 -> 0.8 and fades 1 -> 0; there is no translation animation.
                    enter = fadeIn(SnackbarShowAnimationSpec) + scaleIn(SnackbarShowAnimationSpec, initialScale = 0.8f),
                    exit = fadeOut(SnackbarDismissAnimationSpec) + scaleOut(SnackbarDismissAnimationSpec, targetScale = 0.8f),
                ) {
                    Box(modifier = Modifier.layerOverflowInner(SnackbarShadowOverflow)) {
                        content(entry.data)
                    }
                }
            }
        }
    }
}

/**
 * A Snackbar is a temporary message that appears at the bottom of the screen.
 *
 * Follows COUI 17 `COUISnackBar`: the message, optional icon and action sit on one capsule line;
 * once the message wraps or the line does not fit, the snack bar switches to the vertical layout
 * with the action below the message, aligned to the end, and the corners drop to [cornerRadius].
 * The dismiss icon is a library extension with no COUI counterpart.
 *
 * @param data data of the [Snackbar]
 * @param modifier modifier to be applied to the [Snackbar]
 * @param icon optional leading icon, mirroring COUISnackBar's iv_snack_bar_icon slot. Pass it
 *   through [SnackbarHost]'s `content` lambda: `SnackbarHost(state) { Snackbar(it, icon = { ... }) }`.
 * @param cornerRadius corner radius of the [Snackbar] in the vertical (multi-line) layout, drawn at
 *   the ColorOS default smoothing weight ([COUIRoundCorner.UnspecifiedWeight])
 * @param singleLineCornerRadius corner radius of the [Snackbar] in the single-line layout;
 *   [Dp.Unspecified] (the default) rounds it at half its height, the COUI 17 soft capsule
 * @param maxWidth the maximum width of the [Snackbar], excluding its 16dp outer margins;
 *   [Dp.Unspecified] removes the cap. The default follows the COUI responsive grid, see
 *   [SnackbarDefaults.maxWidth]
 * @param colors colors of the [Snackbar]
 * @param insideMargin margin inside the [Snackbar]; in the single-line layout the end margin is
 *   replaced by a 4dp action margin when the action button is the trailing element
 */
@Composable
fun Snackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    cornerRadius: Dp = SnackbarDefaults.CornerRadius,
    singleLineCornerRadius: Dp = SnackbarDefaults.SingleLineCornerRadius,
    maxWidth: Dp = SnackbarDefaults.maxWidth(),
    colors: SnackbarColors = SnackbarDefaults.snackbarColors(),
    insideMargin: PaddingValues = SnackbarDefaults.InsideMargin,
) {
    val visuals = data.visuals
    val scope = rememberCoroutineScope()

    // COUISnackBar.adjustLayout: the horizontal layout keeps the capsule ("default") radius, the
    // vertical one falls back to couiRoundCornerL. The layout below reports which one it chose.
    var isVertical by remember(visuals) { mutableStateOf(false) }
    // COUISnackBar outline on ColorOS 17: setSmoothRoundRect(rect, radius) without a weight, the
    // soft capsule at half the height and default_g2_weight for couiRoundCornerL.
    val squircleEnabled = isSquircleEnabled()
    val radius = when {
        isVertical -> cornerRadius
        singleLineCornerRadius == Dp.Unspecified -> COUIRoundCorner.Full
        else -> singleLineCornerRadius
    }
    val shape = remember(radius, squircleEnabled) {
        when {
            squircleEnabled -> SquircleShape(radius, COUIRoundCorner.UnspecifiedWeight)
            radius == COUIRoundCorner.Full -> CircleShape
            else -> RoundedCornerShape(radius)
        }
    }
    val isDark = COUITheme.colorScheme.isDark

    Surface(
        modifier = modifier
            .semantics { liveRegion = LiveRegionMode.Polite }
            // COUI card margins: grid_guide_column_card_margin_start (16dp) on each side.
            .padding(horizontal = 16.dp, vertical = SnackbarItemVerticalMargin)
            .widthIn(max = maxWidth)
            .dropShadow(shape = shape, shadow = if (isDark) SnackbarShadowDark else SnackbarShadowLight)
            .pointerInput(Unit) {
                detectTapGestures { /* Consume click */ }
            },
        shape = shape,
        color = colors.containerColor,
        contentColor = colors.contentColor,
    ) {
        Layout(
            content = {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .layoutId(SnackbarSlot.Icon)
                            .size(SnackbarDefaults.IconSize),
                        contentAlignment = Alignment.Center,
                    ) {
                        icon()
                    }
                }

                Text(
                    text = visuals.message,
                    color = colors.contentColor,
                    style = SnackbarDefaults.textStyle(),
                    modifier = Modifier.layoutId(SnackbarSlot.Message),
                )

                if (!visuals.actionLabel.isNullOrEmpty()) {
                    val onAction by rememberUpdatedState(data::performAction)
                    Text(
                        text = visuals.actionLabel,
                        color = colors.actionContentColor,
                        style = SnackbarDefaults.textStyle(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .layoutId(SnackbarSlot.Action)
                            // COUIMaskRippleDrawable custom ripple mask: a capsule around the label.
                            .clip(CircleShape)
                            .clickable { scope.launch { onAction() } }
                            .padding(SnackbarActionPadding),
                    )
                }

                if (visuals.withDismissAction) {
                    val onDismiss by rememberUpdatedState(data::dismiss)
                    Icon(
                        imageVector = COUIIcons.Basic.SearchCleanup,
                        contentDescription = "Dismiss",
                        tint = colors.dismissActionContentColor,
                        modifier = Modifier
                            .layoutId(SnackbarSlot.Dismiss)
                            .padding(start = 8.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable { scope.launch { onDismiss() } },
                    )
                }
            },
        ) { measurables, constraints ->
            val iconMeasurable = measurables.firstOrNull { it.layoutId == SnackbarSlot.Icon }
            val messageMeasurable = measurables.first { it.layoutId == SnackbarSlot.Message }
            val actionMeasurable = measurables.firstOrNull { it.layoutId == SnackbarSlot.Action }
            val dismissMeasurable = measurables.firstOrNull { it.layoutId == SnackbarSlot.Dismiss }

            val start = insideMargin.calculateStartPadding(layoutDirection).roundToPx()
            val end = insideMargin.calculateEndPadding(layoutDirection).roundToPx()
            val top = insideMargin.calculateTopPadding().roundToPx()
            val bottom = insideMargin.calculateBottomPadding().roundToPx()
            val actionEnd = SnackbarActionEndMargin.roundToPx()
            val actionSpacing = SnackbarActionSpacing.roundToPx()
            val iconSpacing = SnackbarIconSpacing.roundToPx()
            val maxWidth = if (constraints.hasBoundedWidth) constraints.maxWidth else Int.MAX_VALUE / 2

            val loose = Constraints()
            val iconPlaceable = iconMeasurable?.measure(loose)
            val dismissPlaceable = dismissMeasurable?.measure(loose)
            val actionPlaceable = actionMeasurable?.measure(
                Constraints(maxWidth = (maxWidth - start - actionEnd - actionSpacing).coerceAtLeast(0)),
            )
            val iconWidth = iconPlaceable?.let { it.width + iconSpacing } ?: 0
            val dismissWidth = dismissPlaceable?.width ?: 0
            val actionWidth = actionPlaceable?.let { it.width + actionSpacing } ?: 0
            // The action keeps only its 4dp margin when it is the trailing element.
            val horizontalEnd = if (actionPlaceable != null && dismissPlaceable == null) actionEnd else end

            // COUISnackBar.isVertical: more than one message line, or a line wider than the bar.
            val horizontalMessageWidth = maxWidth - start - iconWidth - actionWidth - dismissWidth - horizontalEnd
            val vertical = visuals.message.contains('\n') ||
                messageMeasurable.maxIntrinsicWidth(Constraints.Infinity) > horizontalMessageWidth
            if (vertical != isVertical) isVertical = vertical

            if (!vertical) {
                val message = messageMeasurable.measure(Constraints(maxWidth = horizontalMessageWidth.coerceAtLeast(0)))
                val iconClearance = SnackbarIconVerticalMargin.roundToPx()
                val height = maxOf(
                    top + message.height + bottom,
                    iconPlaceable?.let { it.height + 2 * iconClearance } ?: 0,
                    actionPlaceable?.let { top + it.height + bottom } ?: 0,
                    dismissPlaceable?.let { top + it.height + bottom } ?: 0,
                ).coerceIn(constraints.minHeight, constraints.maxHeight)
                val width = (start + iconWidth + message.width + actionWidth + dismissWidth + horizontalEnd)
                    .coerceIn(constraints.minWidth, maxWidth)
                layout(width, height) {
                    // COUISnackBar.layoutHorizontally: every child is centred on the bar height.
                    var x = start
                    iconPlaceable?.let {
                        it.placeRelative(x, (height - it.height) / 2)
                        x += iconWidth
                    }
                    message.placeRelative(x, (height - message.height) / 2)
                    x += message.width
                    actionPlaceable?.let {
                        it.placeRelative(x + actionSpacing, (height - it.height) / 2)
                        x += actionWidth
                    }
                    dismissPlaceable?.let { it.placeRelative(x, (height - it.height) / 2) }
                }
            } else {
                // COUISnackBar.layoutVertically: the message (and icon) start 12dp from the top,
                // the action sits 10dp under the message, aligned to the end, 9dp off the bottom.
                val verticalTop = SnackbarVerticalTopMargin.roundToPx()
                val messageWidth = (maxWidth - start - iconWidth - dismissWidth - end).coerceAtLeast(0)
                val message = messageMeasurable.measure(Constraints(maxWidth = messageWidth))
                val rowHeight = maxOf(message.height, iconPlaceable?.height ?: 0, dismissPlaceable?.height ?: 0)
                val actionTop = verticalTop + message.height + SnackbarVerticalActionSpacing.roundToPx()
                val height = (
                    if (actionPlaceable != null) {
                        maxOf(actionTop + actionPlaceable.height + SnackbarVerticalActionBottomMargin.roundToPx(), verticalTop + rowHeight + bottom)
                    } else {
                        verticalTop + rowHeight + bottom
                    }
                    ).coerceIn(constraints.minHeight, constraints.maxHeight)
                val width = maxOf(
                    start + iconWidth + message.width + dismissWidth + end,
                    actionPlaceable?.let { start + it.width + actionEnd } ?: 0,
                ).coerceIn(constraints.minWidth, maxWidth)
                layout(width, height) {
                    var x = start
                    iconPlaceable?.let {
                        it.placeRelative(x, verticalTop + (rowHeight - it.height) / 2)
                        x += iconWidth
                    }
                    message.placeRelative(x, verticalTop + (rowHeight - message.height) / 2)
                    dismissPlaceable?.let {
                        it.placeRelative(width - end - it.width, verticalTop + (rowHeight - it.height) / 2)
                    }
                    actionPlaceable?.placeRelative(width - actionEnd - actionPlaceable.width, actionTop)
                }
            }
        }
    }
}

private enum class SnackbarSlot { Icon, Message, Action, Dismiss }

/**
 * Colors for [Snackbar].
 *
 * @param containerColor container color of the Snackbar
 * @param contentColor content color of the Snackbar
 * @param actionContentColor action content color of the Snackbar
 * @param dismissActionContentColor dismiss action content color of the Snackbar
 */
@Immutable
data class SnackbarColors(
    val containerColor: Color,
    val contentColor: Color,
    val actionContentColor: Color,
    val dismissActionContentColor: Color,
)

/**
 * Defaults for [Snackbar].
 */
object SnackbarDefaults {
    /** The default corner radius of the vertical (multi-line) layout (COUI couiRoundCornerL). */
    val CornerRadius = 16.dp

    /**
     * The default corner radius of the single-line layout: [Dp.Unspecified], half the snack bar
     * height (COUI 16+ smooth corners round the single-line bar into a capsule).
     */
    val SingleLineCornerRadius = Dp.Unspecified

    /**
     * The default maximum width: six columns of the COUI responsive layout grid with the small
     * margin type, measured on the current window (`COUISnackBar.getMaxWidth`). On compact
     * windows (narrower than 600dp) the grid has only four columns, so the snack bar may span the
     * whole window minus its 16dp margins; on larger windows it stays centred at six columns.
     */
    @Composable
    fun maxWidth(): Dp {
        val containerSize = LocalWindowInfo.current.containerSize
        val density = LocalDensity.current
        return remember(containerSize, density) {
            if (containerSize.width <= 0 || containerSize.height <= 0) {
                Dp.Unspecified
            } else {
                with(density) {
                    COUILayoutGrid.gridWidth(
                        columns = 6,
                        windowWidth = containerSize.width.toDp(),
                        windowHeight = containerSize.height.toDp(),
                    )
                }
            }
        }
    }

    /**
     * The default inside margin
     * (COUI coui_snack_bar_child_margin_horizontal_no_icon_start/end, coui_snack_bar_child_margin_vertical).
     */
    val InsideMargin = PaddingValues(horizontal = 16.dp, vertical = 10.dp)

    /** The size of the leading icon (COUI coui_snack_bar_icon_width/height). */
    val IconSize = 30.dp

    /** The default text style of the message and action label (COUI couiTextAppearanceButton: 14sp, sans-serif-medium). */
    @Composable
    fun textStyle(): TextStyle = COUITheme.textStyles.bodyM.copy(fontWeight = FontWeight.Medium)

    /**
     * The default [SnackbarColors]. Container and content follow COUI couiColorBackgroundTop /
     * couiColorPrimaryNeutral; the action label uses couiColorPrimaryTextOnPopup and the dismiss
     * icon uses couiColorLabelTertiary.
     */
    @Composable
    fun snackbarColors(
        containerColor: Color = COUITheme.colorScheme.backgroundTop,
        contentColor: Color = COUITheme.colorScheme.labelPrimary,
        actionContentColor: Color = COUITheme.colorScheme.primaryTextOnPopup,
        dismissActionContentColor: Color = COUITheme.colorScheme.labelTertiary,
    ): SnackbarColors = remember(containerColor, contentColor, actionContentColor, dismissActionContentColor) {
        SnackbarColors(
            containerColor = containerColor,
            contentColor = contentColor,
            actionContentColor = actionContentColor,
            dismissActionContentColor = dismissActionContentColor,
        )
    }
}

/** The end margin when the action button is the trailing element (COUI coui_snack_bar_action_margin_horizontal_end). */
private val SnackbarActionEndMargin = 4.dp

/** The top margin of the message in the vertical layout (COUI coui_snack_bar_child_margin_vertical_multi_lines). */
private val SnackbarVerticalTopMargin = 12.dp

/** The gap between the message and the action in the vertical layout (COUI coui_snack_bar_action_margin_top_horizontal). */
private val SnackbarVerticalActionSpacing = 10.dp

/** The bottom margin of the action in the vertical layout (COUI coui_snack_bar_action_margin_bottom_multi_lines). */
private val SnackbarVerticalActionBottomMargin = 9.dp

/** The vertical margin around each snack bar inside [SnackbarHost]. */
private val SnackbarItemVerticalMargin = 4.dp

/** The blur radius of the snack bar shadow (COUI coui_snack_bar_shadow_size). */
private val SnackbarShadowRadius = 26.dp

/** The downward offset of the snack bar shadow. */
private val SnackbarShadowOffsetY = 4.dp

/**
 * How far the blurred shadow visibly reaches past the snack bar outline: a blur radius r maps to a
 * Gaussian sigma of about 0.58r, and the shadow fades out at about two sigmas.
 */
private val SnackbarShadowExtent = SnackbarShadowRadius * 1.16f

/**
 * The extra space above the topmost snack bar that keeps its shadow inside [SnackbarHost]'s
 * clipped lazy list: the shadow extent above the outline minus the item margin already there.
 */
internal val SnackbarHostShadowClearance = SnackbarShadowExtent - SnackbarShadowOffsetY - SnackbarItemVerticalMargin

/**
 * `ShadowUtils.setElevationToView(layout, 2, ...)` in light schemes: the Oplus light-source shadow
 * at alpha 40/255, approximated by a soft drop shadow.
 */
private val SnackbarShadowLight = Shadow(
    radius = SnackbarShadowRadius,
    color = Color.Black,
    alpha = 0.14f,
    offset = DpOffset(x = 0.dp, y = SnackbarShadowOffsetY),
)

/**
 * The dark-scheme twin of [SnackbarShadowLight]: the shadow alpha rises to 120/255
 * (`integers.xml` night), so the approximation is roughly three times as dense.
 */
private val SnackbarShadowDark = Shadow(
    radius = SnackbarShadowRadius,
    color = Color.Black,
    alpha = 0.4f,
    offset = DpOffset(x = 0.dp, y = SnackbarShadowOffsetY),
)

/** How far the snack bar shadow reaches past the outline, kept inside the show / dismiss layer. */
private val SnackbarShadowOverflow = SnackbarShadowLight.layerOverflow()

/** The vertical clearance of the leading icon (COUI coui_snack_bar_icon_margin_top_horizontal). */
private val SnackbarIconVerticalMargin = 16.dp

/** The gap between the leading icon and the message (COUI coui_snack_bar_child_margin_horizontal_start). */
private val SnackbarIconSpacing = 16.dp

/** The minimum gap between the message and the action press area (COUI coui_snack_bar_child_margin_horizontal_end). */
private val SnackbarActionSpacing = 16.dp

/**
 * The padding of the action press area: `COUITextViewCompatUtil.setPressRippleDrawable`, called
 * by `COUISnackBar.setOnAction` whenever the action has a listener, pads the label with
 * text_ripple_bg_padding_horizontal / text_ripple_bg_padding_vertical (12dp / 4dp).
 */
private val SnackbarActionPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)

/** COUISnackBar.animateSpring show spring: COUISpringForce(response = 0.3f, bounce = 0f). */
private val SnackbarShowAnimationSpec = spring<Float>(dampingRatio = 1f, stiffness = 438.65f)

/** COUISnackBar.animateSpring dismiss spring: COUISpringForce(response = 0.25f, bounce = 0f). */
private val SnackbarDismissAnimationSpec = spring<Float>(dampingRatio = 1f, stiffness = 631.65f)

/**
 * The shape of the leading image of a [NotificationSnackbar] (COUINotificationSnackBar `ImageType`).
 */
enum class NotificationSnackbarIconType {
    /** A 44dp square (`coui_notification_snack_bar_icon_width`). */
    Square,

    /** A 36dp x 48dp portrait image (`coui_notification_snack_bar_vertical_icon_width/height`). */
    Vertical,

    /** A 58dp x 44dp landscape image (`coui_notification_snack_bar_horizontal_icon_width/height`). */
    Horizontal,
}

/**
 * A notification-style snack bar with COUI style, after COUI 17 `COUINotificationSnackBar`: a
 * floating card with a leading image, a title and an optional subtitle, a small action button and a
 * close button in the top end corner.
 *
 * - **Layout** (`coui_notification_snack_bar_item`): the image sits 16dp from the start, top and
 *   bottom edges and 8dp before the text; the title (`couiTextAppearanceButton`, 14sp medium,
 *   `couiColorPrimaryNeutral`) and the subtitle (`couiTextAppearanceDescription`, 12sp,
 *   `couiColorSecondNeutral`, 2dp below) take two lines each; the small button
 *   (`Widget.COUI.Button.Small`, at most 120dp wide and two lines) is vertically centred; the 20dp
 *   close button sits 6dp from the top and end edges.
 * - **Surface**: `couiColorBackgroundTop`, rounded at `couiRoundCornerL` and lifted with the same
 *   shadow as [Snackbar].
 * - **Swipe** (`COUINotificationSnackBar.onTouchEvent`): a drag past the touch slop picks an axis
 *   (COUI picks it on the first pixel, which would cancel every slightly moving tap on the buttons
 *   and steal a scrolling parent's gestures). Sideways the card follows the finger; past an eighth
 *   of its width it slides off screen at 1.75px per ms (`SLIDING_SPEED`, COUIMoveEaseInterpolator)
 *   and [onDismiss] runs, otherwise it springs back (response 0.35, bounce 0). Downwards it follows
 *   the finger and dismisses the same way past an eighth of its width; once dragged it is held to an
 *   eighth of its width above its place. A drag that starts upwards is left to the parent.
 *
 * Show it through the `content` lambda of [SnackbarHost] to get the host's show / dismiss animation
 * and timeout (COUI clamps the duration to 4s..10s, [SnackbarDuration.Short] /
 * [SnackbarDuration.Long]); see the [SnackbarData] overload.
 *
 * @param title The title (COUI content text).
 * @param actionLabel The label of the action button. COUI requires one (`COUINotificationSnackBar.make`
 *   rejects an empty button text).
 * @param onActionClick Called when the action button is clicked.
 * @param onDismiss Called when the close button is clicked or the card is swiped away.
 * @param modifier The modifier to be applied to the [NotificationSnackbar].
 * @param subtitle The optional subtitle (COUI `setSubContent`).
 * @param icon The optional leading image, laid out in the box [iconType] gives.
 * @param iconType The size of the leading image box.
 * @param swipeToDismiss Whether the card can be swiped away.
 * @param maxWidth The maximum width, excluding the 16dp outer margins; see [SnackbarDefaults.maxWidth].
 * @param cornerRadius The corner radius of the card (COUI `couiRoundCornerL`).
 * @param colors The [NotificationSnackbarColors] of the card.
 */
@Composable
fun NotificationSnackbar(
    title: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: (@Composable () -> Unit)? = null,
    iconType: NotificationSnackbarIconType = NotificationSnackbarIconType.Square,
    swipeToDismiss: Boolean = true,
    maxWidth: Dp = SnackbarDefaults.maxWidth(),
    cornerRadius: Dp = SnackbarDefaults.CornerRadius,
    colors: NotificationSnackbarColors = SnackbarDefaults.notificationSnackbarColors(),
) {
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val cardTopInWindow = remember { floatArrayOf(0f) }
    val windowSize = LocalWindowInfo.current.containerSize
    val currentWindowSize by rememberUpdatedState(windowSize)

    val squircleEnabled = isSquircleEnabled()
    val shape = remember(cornerRadius, squircleEnabled) {
        if (squircleEnabled) SquircleShape(cornerRadius, COUIRoundCorner.UnspecifiedWeight) else RoundedCornerShape(cornerRadius)
    }
    val isDark = COUITheme.colorScheme.isDark

    val swipeModifier = if (swipeToDismiss) {
        Modifier.pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                val touchSlop = viewConfiguration.touchSlop
                var axis = SwipeAxis.None
                var pendingDrag = Offset.Zero
                // The drag position is tracked here: the launched snaps run later, so reading the
                // Animatable value back per event would drop the moves of the same frame.
                var dragX = offsetX.value
                var dragY = offsetY.value
                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    if (!change.pressed) break
                    if (axis == SwipeAxis.None) {
                        // COUI intercepts on the first pixel; past the touch slop instead, so a tap
                        // on the action or close button still clicks and a scrolling parent keeps
                        // the gestures that are not a swipe.
                        if (change.isConsumed) break
                        pendingDrag += change.positionChange()
                        if (pendingDrag.getDistance() < touchSlop) continue
                        axis = when {
                            abs(pendingDrag.x) > abs(pendingDrag.y) -> SwipeAxis.Horizontal

                            // A swipe up from rest is left to a scrolling parent: only a drag down
                            // can dismiss the card.
                            pendingDrag.y > 0f || dragY != 0f -> SwipeAxis.Vertical

                            else -> break
                        }
                        change.consume()
                        continue
                    }
                    val delta = change.positionChange()
                    change.consume()
                    if (axis == SwipeAxis.Horizontal) {
                        dragX += delta.x
                        val x = dragX
                        scope.launch { offsetX.snapTo(x) }
                    } else {
                        // Upwards the card is held to an eighth of its width.
                        dragY = (dragY + delta.y).let { if (it < 0f) maxOf(-size.width / 8f, it) else it }
                        val y = dragY
                        scope.launch { offsetY.snapTo(y) }
                    }
                }
                val threshold = size.width / 8f
                val cardWidth = size.width.toFloat()
                val cardHeight = size.height.toFloat()
                when (axis) {
                    SwipeAxis.Horizontal -> scope.launch {
                        val x = offsetX.value
                        if (abs(x) > threshold) {
                            // startDismissAnimationX: off screen, (distance - |x|) / 1.75 ms.
                            val distance = (currentWindowSize.width - cardWidth) / 2f + cardWidth
                            val duration = ((distance - abs(x)) / NOTIFICATION_SLIDING_SPEED).toInt().coerceAtLeast(0)
                            offsetX.animateTo(
                                targetValue = if (x < 0f) -distance else distance,
                                animationSpec = tween(durationMillis = duration, easing = NotificationMoveEase),
                            )
                            currentOnDismiss()
                        } else {
                            offsetX.animateTo(0f, NotificationSpringBack)
                        }
                    }

                    SwipeAxis.Vertical -> scope.launch {
                        val y = offsetY.value
                        if (y > threshold) {
                            // startDismissAnimationY: past the window bottom, distance / 1.75 ms.
                            val distance = (currentWindowSize.height - cardTopInWindow[0]).coerceAtLeast(cardHeight) + cardHeight
                            offsetY.animateTo(
                                targetValue = distance,
                                animationSpec = tween(
                                    durationMillis = (distance / NOTIFICATION_SLIDING_SPEED).toInt(),
                                    easing = NotificationMoveEase,
                                ),
                            )
                            currentOnDismiss()
                        } else {
                            offsetY.animateTo(0f, NotificationSpringBack)
                        }
                    }

                    SwipeAxis.None -> Unit
                }
            }
        }
    } else {
        Modifier
    }

    Surface(
        modifier = modifier
            .semantics { liveRegion = LiveRegionMode.Polite }
            .padding(horizontal = 16.dp, vertical = SnackbarItemVerticalMargin)
            .widthIn(max = maxWidth)
            .fillMaxWidth()
            .onGloballyPositioned { cardTopInWindow[0] = it.positionInWindow().y }
            .graphicsLayer {
                translationX = offsetX.value
                translationY = offsetY.value
            }
            .then(swipeModifier)
            .dropShadow(shape = shape, shadow = if (isDark) SnackbarShadowDark else SnackbarShadowLight),
        shape = shape,
        color = colors.containerColor,
        contentColor = colors.titleColor,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                val iconSize = when (iconType) {
                    NotificationSnackbarIconType.Square -> DpSize(44.dp, 44.dp)
                    NotificationSnackbarIconType.Vertical -> DpSize(36.dp, 48.dp)
                    NotificationSnackbarIconType.Horizontal -> DpSize(58.dp, 44.dp)
                }
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = NotificationContentEndMargin)
                        .size(iconSize),
                    contentAlignment = Alignment.Center,
                ) {
                    icon()
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    // Without an image the COUI text layout has no start margin and would touch the
                    // edge; the 16dp image margin is kept instead.
                    .padding(
                        start = if (icon == null) 16.dp else 0.dp,
                        top = 16.dp,
                        bottom = 16.dp,
                        end = NotificationContentEndMargin,
                    ),
            ) {
                Text(
                    text = title,
                    color = colors.titleColor,
                    style = COUITheme.textStyles.buttonM,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!subtitle.isNullOrEmpty()) {
                    Text(
                        text = subtitle,
                        color = colors.subtitleColor,
                        style = COUITheme.textStyles.bodyXS,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
            if (actionLabel.isNotEmpty()) {
                TextButton(
                    text = actionLabel,
                    onClick = onActionClick,
                    modifier = Modifier.widthIn(max = NotificationButtonMaxWidth),
                    minWidth = 0.dp,
                    minHeight = 0.dp,
                    colors = colors.actionColors,
                    insideMargin = ButtonDefaults.InsideMarginSmall,
                    textStyle = ButtonDefaults.textStyleSmall(),
                    maxLines = 2,
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(top = 6.dp, start = 2.dp, end = 6.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = onDismiss)
                    .padding(2.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = COUIIcons.Basic.Close,
                    contentDescription = "Close",
                    tint = colors.closeIconColor,
                )
            }
        }
    }
}

/**
 * A [NotificationSnackbar] bound to a [SnackbarData] shown by a [SnackbarHost]: the message is the
 * title, the action label the button, the button performs the action, and the close button and the
 * swipe dismiss it.
 *
 * ```kotlin
 * SnackbarHost(state, canSwipeToDismiss = false) { data ->
 *     NotificationSnackbar(data, subtitle = "...", icon = { ... })
 * }
 * ```
 *
 * @param data The [SnackbarData] of the snack bar. Set its [SnackbarVisuals.actionLabel]: COUI
 *   requires the button text (`COUINotificationSnackBar.make` throws without one); without it the
 *   card is drawn with no button.
 * @param modifier The modifier to be applied to the [NotificationSnackbar].
 * @param subtitle The optional subtitle.
 * @param icon The optional leading image.
 * @param iconType The size of the leading image box.
 * @param swipeToDismiss Whether the card can be swiped away. Pass `canSwipeToDismiss = false` to
 *   the [SnackbarHost] so its own horizontal swipe does not compete.
 * @param maxWidth The maximum width, excluding the 16dp outer margins.
 * @param cornerRadius The corner radius of the card.
 * @param colors The [NotificationSnackbarColors] of the card.
 */
@Composable
fun NotificationSnackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: (@Composable () -> Unit)? = null,
    iconType: NotificationSnackbarIconType = NotificationSnackbarIconType.Square,
    swipeToDismiss: Boolean = true,
    maxWidth: Dp = SnackbarDefaults.maxWidth(),
    cornerRadius: Dp = SnackbarDefaults.CornerRadius,
    colors: NotificationSnackbarColors = SnackbarDefaults.notificationSnackbarColors(),
) {
    val scope = rememberCoroutineScope()
    val currentData by rememberUpdatedState(data)
    NotificationSnackbar(
        title = data.visuals.message,
        actionLabel = data.visuals.actionLabel.orEmpty(),
        onActionClick = { scope.launch { currentData.performAction() } },
        onDismiss = { scope.launch { currentData.dismiss() } },
        modifier = modifier,
        subtitle = subtitle,
        icon = icon,
        iconType = iconType,
        swipeToDismiss = swipeToDismiss,
        maxWidth = maxWidth,
        cornerRadius = cornerRadius,
        colors = colors,
    )
}

/**
 * Colors of a [NotificationSnackbar].
 *
 * @param containerColor The card fill (COUI `couiColorBackgroundTop`).
 * @param titleColor The title color (COUI `couiColorPrimaryNeutral`).
 * @param subtitleColor The subtitle color (COUI `couiColorSecondNeutral`).
 * @param closeIconColor The close glyph tint (COUI `couiColorLabelTertiary`).
 * @param actionColors The colors of the action button.
 */
@Immutable
data class NotificationSnackbarColors(
    val containerColor: Color,
    val titleColor: Color,
    val subtitleColor: Color,
    val closeIconColor: Color,
    val actionColors: TextButtonColors,
)

/**
 * The default [NotificationSnackbarColors]: COUI couiColorBackgroundTop card,
 * couiColorPrimaryNeutral title, couiColorSecondNeutral subtitle, couiColorLabelTertiary close
 * glyph and the default (secondary) small button.
 */
@Composable
fun SnackbarDefaults.notificationSnackbarColors(
    containerColor: Color = COUITheme.colorScheme.backgroundTop,
    titleColor: Color = COUITheme.colorScheme.labelPrimary,
    subtitleColor: Color = COUITheme.colorScheme.labelSecondaryVariant,
    closeIconColor: Color = COUITheme.colorScheme.labelTertiary,
    actionColors: TextButtonColors = ButtonDefaults.textButtonColors(),
): NotificationSnackbarColors = remember(containerColor, titleColor, subtitleColor, closeIconColor, actionColors) {
    NotificationSnackbarColors(
        containerColor = containerColor,
        titleColor = titleColor,
        subtitleColor = subtitleColor,
        closeIconColor = closeIconColor,
        actionColors = actionColors,
    )
}

private enum class SwipeAxis { None, Horizontal, Vertical }

/** The gap after the image and after the text (COUI coui_notification_snack_bar_icon_end). */
private val NotificationContentEndMargin = 8.dp

/** The maximum width of the action button (COUI coui_snack_bar_button_width, compact windows). */
private val NotificationButtonMaxWidth = 120.dp

/** COUINotificationSnackBar `SLIDING_SPEED`, pixels per millisecond. */
private const val NOTIFICATION_SLIDING_SPEED = 1.75f

/** COUIMoveEaseInterpolator. */
private val NotificationMoveEase = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** `getViewSpringTranslationAnimator`: COUISpringForce(response = 0.35f, bounce = 0f). */
private val NotificationSpringBack = spring<Float>(dampingRatio = 1f, stiffness = 322.27f)
