// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.anim.SinOutEasing
import io.github.suqi8.coui.kmp.anim.folmeSpring
import io.github.suqi8.coui.kmp.effect.MaterialBlurBlendMode
import io.github.suqi8.coui.kmp.effect.MaterialBlurSpec
import io.github.suqi8.coui.kmp.effect.couiTouchMotion
import io.github.suqi8.coui.kmp.interfaces.ExperimentalScrollBarApi
import io.github.suqi8.coui.kmp.squircle.addSquircleRect
import io.github.suqi8.coui.kmp.squircle.isSquircleEnabled
import io.github.suqi8.coui.kmp.squircle.squircleSurface
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.COUILayoutGrid
import io.github.suqi8.coui.kmp.utils.CouiHapticEffect
import io.github.suqi8.coui.kmp.utils.rememberCouiHaptic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

private fun PopupPositionProvider.Align.resolve(layoutDirection: LayoutDirection): PopupPositionProvider.Align {
    if (layoutDirection == LayoutDirection.Ltr) return this
    return when (this) {
        PopupPositionProvider.Align.Start -> PopupPositionProvider.Align.End
        PopupPositionProvider.Align.End -> PopupPositionProvider.Align.Start
        PopupPositionProvider.Align.TopStart -> PopupPositionProvider.Align.TopEnd
        PopupPositionProvider.Align.TopEnd -> PopupPositionProvider.Align.TopStart
        PopupPositionProvider.Align.BottomStart -> PopupPositionProvider.Align.BottomEnd
        PopupPositionProvider.Align.BottomEnd -> PopupPositionProvider.Align.BottomStart
    }
}

/** Vertical inset of the list inside the popup (COUI coui_popup_list_padding_vertical). */
internal val PopupListVerticalPadding = 8.dp

/** Top and bottom inset of the popup scroll bar (COUI coui_popup_list_window_scrollbar_vertical_padding_new). */
private val PopupScrollBarVerticalPadding = 18.dp

/** Horizontal inset of the shared press mask inside a row (COUI coui_popup_list_window_mask_margin_horizontal). */
private val PopupMaskHorizontalMargin = 8.dp

/**
 * Corner radius of the shared press mask on a multi-line row
 * (COUI coui_popup_list_window_vertical_mask_radius_16_1, the COUI 17 value).
 */
private val PopupMaskCornerRadius = 28.dp

/** Smooth-corner weight of the shared mask on a multi-line row (coui_popup_list_window_shared_mask_smooth_round_corner_weight). */
private const val POPUP_MASK_CORNER_WEIGHT = 3f

/** Weight of the capsule mask of a single-line row (COUITouchRecyclerView SMOOTH_WEIGHT_FULL_ROUND). */
private const val POPUP_MASK_CAPSULE_WEIGHT = 1f

/**
 * Press fade of the shared mask (COUIMaskEffectDrawable press animator: COUISpringForce
 * response 0.3 / bounce 0).
 */
private val PopupMaskPressSpring = spring<Float>(dampingRatio = 1f, stiffness = 438.65f, visibilityThreshold = 0.001f)

/**
 * Travel of the shared mask between rows (COUITouchRecyclerView: COUISpringForce bounce 0,
 * response 0.15 — MASK_SPRING_RESPONSE_VERTICAL_ONLY).
 */
private val PopupMaskTravelSpring = spring<Float>(dampingRatio = 1f, stiffness = 1754.6f, visibilityThreshold = 0.001f)

/** Mask travel when the list has horizontal items (`MASK_SPRING_RESPONSE_MIXED`, response 0.2). */
private val PopupMaskTravelSpringMixed = spring<Float>(dampingRatio = 1f, stiffness = 986.96f, visibilityThreshold = 0.001f)

/**
 * Minimum press progress the mask reaches before a release may fade it out, so quick taps still
 * flash (COUI COUIMaskEffectDrawable DEFAULT_MIN_PROGRESS_FOR_TOUCH_ENTER_ANIMATION).
 */
private const val POPUP_MASK_MIN_VISIBLE_PROGRESS = 0.7f

/** Long-press timeout of a row that opens a sub menu (COUIPopupListWindow DEFAULT_REGION_BASED_LONG_PRESS_TIMEOUT_MS). */
internal const val POPUP_SUBMENU_LONG_PRESS_TIMEOUT_MILLIS = 650L

/**
 * A single popup row registered with a [PopupListGestureHost].
 *
 * Bounds are held as plain fields rather than snapshot state: they are written by
 * [ListPopupColumn]'s measure policy, which knows the exact placement order and offsets, and are
 * only read back during the pointer and draw phases, both of which run after layout on every frame.
 */
@Stable
internal class PopupListItemHandle {
    var top: Float = 0f
    var bottom: Float = 0f
    var enabled: Boolean = true
    var hasIcon: Boolean = false
    var hasSubmenu: Boolean = false

    /**
     * Whether the row shows a single title line and no description, which turns the shared mask
     * into a capsule (COUITouchRecyclerView.isSingleLineVerticalItem).
     */
    var singleLine: Boolean = true
    var onClick: () -> Unit = {}

    /** Long-press action of a row that opens a sub menu (`RegionBasedLongPressDetector`), or `null`. */
    var onLongClick: (() -> Unit)? = null

    /**
     * Whether this is a cell of the horizontal item row (`MixedPopupListAdapter` item view type 0):
     * it then only covers [left]..[right] of the list and carries its own mask radius.
     */
    var horizontal: Boolean = false
    var left: Float = 0f
    var right: Float = 0f

    /** Cell bounds inside the horizontal row, written by its layout before the list places the row. */
    var localTop: Float = 0f
    var localBottom: Float = 0f

    /** Mask corner radius of a horizontal cell (17dp icon-only / 23dp labelled on COUI 17). */
    var maskRadius: Float = 0f

    /** Mask height cap of an icon-only cell (`mHorizontalIconOnlyMaskHeight`, 40dp); 0 for none. */
    var maskHeightCap: Float = 0f

    fun contains(x: Float, y: Float): Boolean = y >= top && y < bottom && (!horizontal || (x >= left && x < right))
}

/**
 * Parent data of the horizontal item row: its cells, whose [PopupListItemHandle.localTop] /
 * [PopupListItemHandle.left] the row's layout fills in, so [ListPopupColumn] can register them as
 * selectable targets of the shared mask once it knows where the row sits.
 */
internal class PopupHorizontalRowParentData(val cells: () -> List<PopupListItemHandle>) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@PopupHorizontalRowParentData
}

/**
 * The one press mask a COUI 17 popup list draws (COUITouchRecyclerView's shared
 * `COUIMaskEffectDrawable`): a rounded rect inset [PopupMaskHorizontalMargin] into the row under
 * the finger that slides to the next row instead of flashing a new one.
 */
@Stable
internal class PopupSharedMask {
    /**
     * Press (touch-enter) progress, 0 → 1. Held as plain snapshot state rather than an `Animatable`
     * so a snap lands in the same frame as the bounds change that goes with it: an
     * `Animatable.snapTo` only runs once its coroutine is dispatched and the mutex of a cancelled
     * animation is released, which let one frame draw the new bounds with the old progress.
     */
    var press: Float by mutableFloatStateOf(0f)
        private set
    private var pressVelocity = 0f

    /** Travel progress from [from] to [to], 0 → 1; written together with [from] and [to]. */
    var travel: Float by mutableFloatStateOf(1f)
        private set

    /** The row the mask rests on, or the one it is travelling to. */
    var target: PopupListItemHandle? by mutableStateOf(null)
        private set

    /** Whether the mask is fading out at its last bounds (COUITouchRecyclerView mSharedMaskIsExiting). */
    var exiting: Boolean by mutableStateOf(false)
        private set

    private var from: MaskGeometry? = null
    private var to: MaskGeometry? = null
    private var pressJob: Job? = null
    private var travelJob: Job? = null

    /** The travel spring; COUI uses response 0.2 instead of 0.15 when the list has horizontal items. */
    var travelSpec: SpringSpec<Float> = PopupMaskTravelSpring

    /** Whether the travel spring is running (COUITouchRecyclerView mSharedMaskSpringAnim.isRunning). */
    val isTravelling: Boolean
        get() = travelJob?.isActive == true

    /** The mask geometry for the current frame, or `null` when nothing is drawn. */
    fun current(): MaskGeometry? {
        val end = to ?: return null
        val start = from ?: return end
        return start.lerp(end, travel.coerceIn(0f, 1f))
    }

    /**
     * Attaches the mask to [row] (COUITouchRecyclerView.attachSharedMaskToView). A mask that is
     * pressed on another row slides there on the travel spring, starting from the bounds drawn this
     * very frame (`startSharedMaskAnim(mSharedMaskBounds, end)`). A fresh press, or one that follows
     * a fade-out (`mSharedMaskIsExiting` empties the bounds), snaps to the row (`syncMaskRect`) and
     * presses in from whatever is left of the fade.
     */
    fun attach(row: PopupListItemHandle, geometry: MaskGeometry, scope: CoroutineScope) {
        val previous = current()
        val wasExiting = exiting
        exiting = false
        target = row
        if (previous == null || wasExiting || press <= 0f) {
            travelJob?.cancel()
            from = null
            to = geometry
            travel = 1f
        } else if (previous != geometry) {
            travelJob?.cancel()
            from = previous
            to = geometry
            travel = 0f
            val spec = travelSpec
            travelJob = scope.launch {
                animate(initialValue = 0f, targetValue = 1f, animationSpec = spec) { value, _ ->
                    travel = value
                }
            }
        }
        enterPress(scope)
    }

    /**
     * Shows the mask fully pressed on [row] and lets it fade out at once: the press a compact sub
     * menu header inherits from the row that opened it (`COUITouchRecyclerView`
     * `beginSharedMaskPressHandoffThenAnimate(progress, 0)`).
     */
    fun flash(row: PopupListItemHandle, geometry: MaskGeometry, scope: CoroutineScope) {
        travelJob?.cancel()
        pressJob?.cancel()
        from = null
        to = geometry
        travel = 1f
        target = row
        exiting = false
        press = 1f
        pressVelocity = 0f
        exit(scope)
    }

    /** Re-reads the geometry of the resting row after a layout pass, e.g. when the list scrolls. */
    fun refresh(geometry: MaskGeometry) {
        if (isTravelling) return
        from = null
        to = geometry
        travel = 1f
    }

    private fun enterPress(scope: CoroutineScope) {
        pressJob?.cancel()
        pressJob = scope.launch { animatePress(1f) }
    }

    /**
     * Springs [press] to [target] from its current value and velocity (COUI StateEffectAnimator
     * `animateToProgress(progress, true)` restarts from the current value). Returns early, without
     * throwing, once [stopAt] is reached.
     */
    private suspend fun animatePress(target: Float, stopAt: Float = Float.NaN) {
        try {
            animate(
                initialValue = press,
                targetValue = target,
                initialVelocity = pressVelocity,
                animationSpec = PopupMaskPressSpring,
            ) { value, velocity ->
                press = value
                pressVelocity = velocity
                if (!stopAt.isNaN() && value >= stopAt) throw PressMaskThresholdReached()
            }
            pressVelocity = 0f
        } catch (_: PressMaskThresholdReached) {
            // Minimum visible progress reached; the caller continues from here.
        }
    }

    /**
     * Fades the mask out where it stands (COUITouchRecyclerView.clearSharedMask(true) plus
     * COUIMaskEffectDrawable.setTouchExited), first letting a fresh press reach
     * [POPUP_MASK_MIN_VISIBLE_PROGRESS] so a quick tap still flashes. A running travel spring is
     * left alone, as COUI keeps `mSharedMaskSpringAnim` running while the mask exits.
     */
    fun exit(scope: CoroutineScope) {
        if (target == null && !exiting) return
        exiting = true
        target = null
        pressJob?.cancel()
        pressJob = scope.launch {
            if (press > 0f && press < POPUP_MASK_MIN_VISIBLE_PROGRESS) {
                animatePress(1f, stopAt = POPUP_MASK_MIN_VISIBLE_PROGRESS)
            }
            animatePress(0f)
            if (exiting) {
                exiting = false
                travelJob?.cancel()
                from = null
                to = null
                travel = 1f
            }
        }
    }

    /** Drops the mask immediately (COUITouchRecyclerView.clearSharedMask(false)). */
    fun clear() {
        pressJob?.cancel()
        travelJob?.cancel()
        exiting = false
        target = null
        from = null
        to = null
        travel = 1f
        press = 0f
        pressVelocity = 0f
    }
}

/** Thrown to stop a deferred press-in mask once [POPUP_MASK_MIN_VISIBLE_PROGRESS] is reached. */
private class PressMaskThresholdReached : CancellationException("Press mask reached the minimum visible progress")

/**
 * The rect, corner radius and corner weight of the shared mask, all lerped together while the mask
 * travels (COUITouchRecyclerView `lerpRect` + start/end corner radius and weight).
 */
@Immutable
internal data class MaskGeometry(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val radius: Float,
    val weight: Float,
) {
    fun lerp(end: MaskGeometry, fraction: Float): MaskGeometry = MaskGeometry(
        left = left + (end.left - left) * fraction,
        top = top + (end.top - top) * fraction,
        right = right + (end.right - right) * fraction,
        bottom = bottom + (end.bottom - bottom) * fraction,
        radius = radius + (end.radius - radius) * fraction,
        weight = weight + (end.weight - weight) * fraction,
    )
}

/**
 * Resolves the shared mask geometry of [row] in a list [width] wide
 * (COUITouchRecyclerView.computeSharedMaskEndForItem + calculateVerticalMaskBounds): inset
 * [horizontalMargin] on both sides, full row height; a single-line row becomes a capsule
 * (radius = half the height, weight 1), any other row takes [cornerRadius] at weight 3.
 */
internal fun popupMaskGeometry(
    row: PopupListItemHandle,
    width: Float,
    horizontalMargin: Float,
    cornerRadius: Float,
): MaskGeometry {
    if (row.horizontal) {
        // computeSharedMaskEndForItem, type 0: the whole cell, an icon-only cell capped at 40dp.
        val bottom = if (row.maskHeightCap > 0f) minOf(row.bottom, row.top + row.maskHeightCap) else row.bottom
        return MaskGeometry(row.left, row.top, row.right, bottom, row.maskRadius, POPUP_MASK_CORNER_WEIGHT)
    }
    val height = row.bottom - row.top
    return if (row.singleLine) {
        MaskGeometry(horizontalMargin, row.top, width - horizontalMargin, row.bottom, height / 2f, POPUP_MASK_CAPSULE_WEIGHT)
    } else {
        MaskGeometry(horizontalMargin, row.top, width - horizontalMargin, row.bottom, cornerRadius, POPUP_MASK_CORNER_WEIGHT)
    }
}

/**
 * Owns the drag-to-select gesture and the shared press mask of one popup list, mirroring COUI 17
 * COUITouchRecyclerView. Rows register through [Modifier.popupListItem] and never track their own
 * press state: the host resolves the row under the pointer from the bounds the measure policy
 * publishes, so dragging slides the mask between rows and a release selects whichever row the
 * finger currently rests on.
 */
@Stable
internal class PopupListGestureHost {
    val items = mutableStateListOf<PopupListItemHandle>()
    val mask = PopupSharedMask()

    /** Rows in placement order, written by [ListPopupColumn]'s measure policy. */
    private var ordered: List<PopupListItemHandle> = emptyList()

    /** Vertical bands of the non-selectable children (headers, group dividers), in list space. */
    private var neutralBands: List<Pair<Float, Float>> = emptyList()

    /** The row the gesture currently tracks (COUITouchRecyclerView.mDownView), selectable or not. */
    private var activeItem: PopupListItemHandle? = null

    /** The row whose long press fired during this gesture; lifting on it selects nothing. */
    private var longPressedItem: PopupListItemHandle? = null

    /** List width in pixels, for the mask geometry. */
    var width: Float = 0f

    /** Mask insets in pixels, resolved by [ListPopupColumn]. */
    var maskHorizontalMargin: Float = 0f
    var maskCornerRadius: Float = 0f

    /** The scope the mask animations run in, set by [ListPopupColumn]. */
    var scope: CoroutineScope? = null

    /** The list content's coordinates (scroll applied), for gestures relayed from another list. */
    var coordinates: LayoutCoordinates? = null

    /** The row whose mask is locked on (`setHoverStateLocked`), e.g. the anchor of an open sub menu. */
    private var heldItem: PopupListItemHandle? = null

    /** A row waiting for its first placement to receive a press handoff. */
    private var pendingHandoff: PopupListItemHandle? = null

    /** True while the list has no scroll range (COUITouchRecyclerView: neither direction can scroll). */
    var isDynamicSelection: Boolean = true

    /** The viewport height the parent allows, captured before the scroll container is measured. */
    var viewportMax: Int = Int.MAX_VALUE

    /**
     * The viewport height COUI shows: the list insets plus every row that fits whole
     * (COUIPopupListWindow.measurePopupWindowForRecyclerView stops at the first row that would
     * overflow the maximum height). [Int.MAX_VALUE] until the list has been measured once.
     */
    var snappedHeight: Int by mutableStateOf(Int.MAX_VALUE)

    val hasIcon: Boolean
        get() = items.any { it.hasIcon }

    val hasSubmenu: Boolean
        get() = items.any { it.hasSubmenu }

    fun onPlaced(rows: List<PopupListItemHandle>, neutral: List<Pair<Float, Float>>) {
        ordered = rows
        neutralBands = neutral
        mask.travelSpec = if (rows.any { it.horizontal }) PopupMaskTravelSpringMixed else PopupMaskTravelSpring
        val target = mask.target
        if (target != null && !mask.exiting) mask.refresh(geometryOf(target))
        val handoff = pendingHandoff
        val scope = scope
        if (handoff != null && scope != null && handoff in rows && handoff.bottom > handoff.top) {
            pendingHandoff = null
            mask.flash(handoff, geometryOf(handoff), scope)
        }
    }

    /** Locks the mask on [handle] until [release] (`ListItemMaskEffectDrawable.setHoverStateLocked`). */
    fun hold(handle: PopupListItemHandle) {
        heldItem = handle
        val scope = scope ?: return
        mask.attach(handle, geometryOf(handle), scope)
    }

    /** Releases a mask locked by [hold]. */
    fun release(handle: PopupListItemHandle) {
        if (heldItem !== handle) return
        heldItem = null
        val scope = scope ?: return
        mask.exit(scope)
    }

    /** Hands a full press to [handle] once it is placed, then lets it fade. */
    fun handoff(handle: PopupListItemHandle) {
        pendingHandoff = handle
    }

    fun register(handle: PopupListItemHandle) {
        if (!items.contains(handle)) items.add(handle)
    }

    fun unregister(handle: PopupListItemHandle) {
        items.remove(handle)
        ordered = ordered - handle
    }

    /** Rows top to bottom, as placed by the last measure pass. */
    fun sortedItems(): List<PopupListItemHandle> = ordered

    private fun geometryOf(row: PopupListItemHandle): MaskGeometry = popupMaskGeometry(row, width, maskHorizontalMargin, maskCornerRadius)

    /** The selectable row or cell under ([x], [y]) in list space (`findChildViewUnder`), if any. */
    fun rowAt(x: Float, y: Float): PopupListItemHandle? = ordered.firstOrNull { it.contains(x, y) }

    private fun isNeutral(y: Float): Boolean = neutralBands.any { (top, bottom) -> y >= top && y < bottom }

    /** DOWN: tracks the row under ([x], [y]) and attaches the mask to it when enabled. */
    fun onDown(x: Float, y: Float, scope: CoroutineScope): PopupListItemHandle? {
        longPressedItem = null
        val target = rowAt(x, y) ?: return null
        activeItem = target
        if (target.enabled) mask.attach(target, geometryOf(target), scope)
        return target
    }

    /**
     * MOVE in dynamic selection mode. Returns `true` when the mask moved onto a new enabled row,
     * which is when COUI plays its haptic.
     */
    fun onMoveTo(x: Float, y: Float, scope: CoroutineScope): Boolean {
        val target = rowAt(x, y)
        if (target == null) {
            // Headers and group dividers keep the current state; empty space (the list insets)
            // releases the selection.
            if (!isNeutral(y)) cancel(scope)
            return false
        }
        if (target === activeItem) return false
        activeItem = target
        return if (target.enabled) {
            mask.attach(target, geometryOf(target), scope)
            true
        } else {
            // Deliberate deviation from COUI 17, which fades the mask out over a disabled row
            // (clearSharedMask(true)) and snaps it onto the next enabled row: a pressed mask keeps
            // its full press and slides onto the disabled row on the travel spring, so a drag never
            // breaks the mask's motion. The row stays unselectable: no haptic, and releasing over
            // it selects nothing (onUp checks enabled).
            if (mask.target != null && !mask.exiting) mask.attach(target, geometryOf(target), scope)
            false
        }
    }

    /** Releases the selection without picking anything. */
    fun cancel(scope: CoroutineScope) {
        activeItem = null
        mask.exit(scope)
    }

    /**
     * Hands the gesture to another list (the finger entered the sub menu): nothing here is selected
     * on release any more, and the mask fades unless it is locked on the anchor of the open sub menu.
     */
    fun yield(scope: CoroutineScope) {
        activeItem = null
        if (mask.target !== heldItem) mask.exit(scope)
    }

    /** UP: selects the row the finger rests on (COUI resolves the target at UP, not at DOWN). */
    fun onUp(scope: CoroutineScope) {
        val target = activeItem
        val longPressed = longPressedItem
        activeItem = null
        longPressedItem = null
        mask.exit(scope)
        if (target != null && target.enabled && target !== longPressed) target.onClick()
    }

    /**
     * A long press on [row] that opens a sub menu (`RegionBasedLongPressDetector`, 650ms): the
     * selection is released and the row's long-press action runs instead of a click. Returns
     * whether it fired.
     */
    fun onLongPress(row: PopupListItemHandle, scope: CoroutineScope): Boolean {
        val action = row.onLongClick
        if (activeItem !== row || !row.enabled || action == null) return false
        // The row stays the tracked one, so resting on it is not a new selection, but lifting on it
        // no longer clicks it; dragging away keeps selecting.
        longPressedItem = row
        mask.exit(scope)
        action()
        return true
    }

    /** The row the gesture tracks, used by the sub-menu long press. */
    fun active(): PopupListItemHandle? = activeItem

    /** Whether ([x], [y]) lands on a row, a header or a divider of this list. */
    fun isInsideList(x: Float, y: Float): Boolean = x >= 0f && x < width && (rowAt(x, y) != null || isNeutral(y))
}

internal val LocalPopupListGestureHost = staticCompositionLocalOf<PopupListGestureHost?> { null }

/** Which list of a cascading popup a [ListPopupColumn] is, for [PopupGestureRelay]. */
internal enum class PopupListRole { Standalone, Main, Sub }

internal val LocalPopupListRole = staticCompositionLocalOf { PopupListRole.Standalone }

/**
 * Lets one gesture run across the main and the sub menu of a cascading popup
 * (`COUIPopupMenuRootView.forwardTouchToSubMenuIfNeeded`): once a long press on the main menu has
 * opened the sub menu and it has entered, the moves of that same finger are re-targeted at the sub
 * list — a synthetic DOWN when it enters the sub menu, a CANCEL when it leaves — and lifting the
 * finger over a sub-menu row selects it.
 */
@Stable
internal class PopupGestureRelay {
    /** The sub list's host, set while a sub menu is composed. */
    var subHost: PopupListGestureHost? = null

    /** Whether the sub menu has finished entering (`mSubMenuEntered`). */
    var subEntered: Boolean = false
}

internal val LocalPopupGestureRelay = staticCompositionLocalOf<PopupGestureRelay?> { null }

/**
 * Records where an anchor was last touched, so a popup can open at the finger rather than centred on
 * the anchor — COUI `PreciseClickHelper` / `PreciseLongPressHelper`, which stash the DOWN `(x, y)`
 * and pass it to the click callback, after which `PopupMenuLocateHelper.setAnchor` collapses the
 * anchor bounds to that single point.
 *
 * The point is kept in **window** coordinates, because the composable that positions the popup is
 * usually not the node that receives the touch (a preference hosts its popup inside its trailing
 * action slot, while the whole row is what gets tapped), so an anchor-local offset would be measured
 * against the wrong origin.
 *
 * [Offset.Unspecified] means "no point recorded": the popup then falls back to centring on the
 * anchor, which is also COUI's TalkBack path (`PreciseClickHelper` substitutes the view centre when
 * TalkBack is enabled or the recorded point is still the initial `(0, 0)`).
 */
@Stable
class PreciseClickState {
    /** The last recorded touch point in window coordinates, or [Offset.Unspecified] if none. */
    var touchPoint: Offset by mutableStateOf(Offset.Unspecified)
        internal set

    /**
     * Coordinates of the anchor the point was recorded against, used to convert the touch offset
     * into window space. Held here rather than in a `remember` so [preciseClickAnchor] stays a plain
     * modifier factory instead of a `composed` one.
     */
    internal var anchorCoordinates: LayoutCoordinates? = null

    /** Discards the recorded point, restoring anchor-centred positioning. */
    fun clear() {
        touchPoint = Offset.Unspecified
    }
}

/** Creates and remembers a [PreciseClickState] for a point-anchored popup. */
@Composable
fun rememberPreciseClickState(): PreciseClickState = remember { PreciseClickState() }

/**
 * Records the DOWN position of each touch into [state], mirroring COUI `PreciseClickHelper`'s
 * `OnTouchListener`. Attach to the anchor of a popup that was given the same [PreciseClickState] so
 * the popup opens at the finger.
 *
 * The handler observes in [PointerEventPass.Initial] and consumes nothing, so the anchor's own
 * `clickable` still sees the whole gesture; accessibility clicks produce no pointer event at all and
 * therefore leave the point unrecorded, which is exactly COUI's TalkBack fallback.
 */
fun Modifier.preciseClickAnchor(state: PreciseClickState): Modifier = this
    .onGloballyPositioned { state.anchorCoordinates = it }
    .pointerInput(state) {
        awaitPointerEventScope {
            while (true) {
                val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                val anchor = state.anchorCoordinates
                state.touchPoint = if (anchor != null && anchor.isAttached) {
                    anchor.localToWindow(down.position)
                } else {
                    Offset.Unspecified
                }
            }
        }
    }

/**
 * Parent data a registered row exposes to [ListPopupColumn]'s measure policy, so the policy can tell
 * selectable rows apart from group divider bands and headers without inspecting the composition.
 */
private class PopupListItemParentData(val handle: PopupListItemHandle) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@PopupListItemParentData
}

private val IntrinsicMeasurable.popupListItemHandle: PopupListItemHandle?
    get() = (parentData as? PopupListItemParentData)?.handle

/**
 * Registers a popup row with the enclosing [PopupListGestureHost] so the list can own its press
 * feedback (the shared mask) and its click.
 *
 * The row keeps its own click semantics: the accessibility action below is registered independently
 * of the pointer tracking, matching COUI's TalkBack path, which bypasses the drag gesture entirely
 * and calls `performItemClick`.
 *
 * @param selected Whether the row is the selected option.
 * @param enabled Whether the row reacts to input.
 * @param role The accessibility [Role] exposed for the row.
 * @param hasIcon Whether the row shows a leading icon.
 * @param singleLine Whether the row shows one title line and no description (capsule mask).
 * @param hasSubmenu Whether the row opens a sub menu (widens the menu to 196dp).
 * @param held Whether the shared mask stays locked on this row, as COUI does on the row whose sub
 *   menu is open on a larger window.
 * @param pressHandoff Whether this row inherits a full press when it first appears, which then
 *   fades (the header of a compact sub menu).
 * @param onLongClick The action of a 650ms long press, or `null` for none.
 * @param onClick Invoked when the row is selected, by pointer or by accessibility action.
 */
@Composable
internal fun Modifier.popupListItem(
    selected: Boolean,
    enabled: Boolean,
    role: Role,
    hasIcon: Boolean,
    singleLine: Boolean = true,
    hasSubmenu: Boolean = false,
    held: Boolean = false,
    pressHandoff: Boolean = false,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit,
): Modifier {
    val host = LocalPopupListGestureHost.current ?: return this.selectable(
        selected = selected,
        enabled = enabled,
        role = role,
        onClick = onClick,
    )

    val handle = remember { PopupListItemHandle() }
    val currentOnClick by rememberUpdatedState(onClick)
    handle.enabled = enabled
    handle.hasIcon = hasIcon
    handle.singleLine = singleLine
    handle.hasSubmenu = hasSubmenu
    handle.onClick = remember { { currentOnClick() } }
    val currentOnLongClick by rememberUpdatedState(onLongClick)
    handle.onLongClick = if (onLongClick != null) remember { { currentOnLongClick?.invoke() } } else null

    DisposableEffect(host, handle) {
        host.register(handle)
        onDispose { host.unregister(handle) }
    }
    DisposableEffect(host, handle, held) {
        if (held) host.hold(handle)
        onDispose { if (held) host.release(handle) }
    }
    LaunchedEffect(host, handle, pressHandoff) {
        if (pressHandoff) host.handoff(handle)
    }

    val parentData = remember(handle) { PopupListItemParentData(handle) }

    return this
        .then(parentData)
        .semantics(mergeDescendants = true) {
            this.role = role
            this.selected = selected
            if (enabled) {
                onClick {
                    currentOnClick()
                    true
                }
            } else {
                disabled()
            }
        }
}

/**
 * Resolves the width of a popup list from the widest child (COUI
 * COUIPopupListWindow.measurePopupWindowForRecyclerView): the widest row, clamped to
 * [ListPopupDefaults.MinWidth] — or [ListPopupDefaults.MinWidthWithSubmenu] when a row opens a sub
 * menu (`getMainMenuMinWidth`) — and [ListPopupDefaults.MaxWidth], then into the parent constraints.
 */
internal fun resolvePopupListWidth(
    widest: Int,
    minWidth: Int,
    maxWidth: Int,
    parentMin: Int,
    parentMax: Int,
): Int {
    val upper = maxOf(maxWidth, parentMin).coerceAtMost(parentMax)
    val lower = maxOf(minWidth, parentMin).coerceAtMost(upper)
    return widest.coerceIn(lower, upper)
}

/**
 * The viewport height COUI gives a popup list (COUIPopupListWindow.measurePopupWindowForRecyclerView):
 * both list insets, then each child in order while the next one still fits inside [maxHeight]. The
 * first child that would overflow stops the sum, so the viewport never ends on a partial row.
 */
internal fun snapPopupListHeight(childHeights: List<Int>, verticalInsets: Int, maxHeight: Int): Int {
    var height = verticalInsets
    for (child in childHeights) {
        if (height + child > maxHeight) break
        height += child
    }
    return height
}

/**
 * A column that automatically aligns the width to the widest item.
 *
 * The column owns the COUI 17 popup gesture: a single pointer handler resolves the row under the
 * finger, so dragging slides the shared press mask between rows and a release selects the row the
 * finger currently rests on. It also adds the 8dp list insets (COUI coui_popup_list_padding_vertical)
 * above the first and below the last child, and limits its viewport to whole rows.
 *
 * @param content The items
 */
@OptIn(ExperimentalScrollBarApi::class)
@Composable
fun ListPopupColumn(
    content: @Composable () -> Unit,
) {
    val host = remember { PopupListGestureHost() }
    ListPopupColumn(host = host, content = content)
}

/** [ListPopupColumn] driven by a given [host], which tests use to observe the shared mask. */
@OptIn(ExperimentalScrollBarApi::class)
@Composable
internal fun ListPopupColumn(
    host: PopupListGestureHost,
    content: @Composable () -> Unit,
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val performHaptic = rememberCouiHaptic()
    val hapticFeedback = LocalHapticFeedback.current
    val performLongPressHaptic = remember(hapticFeedback) {
        { hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress) }
    }
    val touchSlop = LocalViewConfiguration.current.touchSlop
    val maskColor = COUITheme.colorScheme.hover
    val squircleEnabled = isSquircleEnabled()

    host.isDynamicSelection = !scrollState.canScrollForward && !scrollState.canScrollBackward
    host.scope = coroutineScope
    val relay = LocalPopupGestureRelay.current
    val role = LocalPopupListRole.current
    if (relay != null && role == PopupListRole.Sub) {
        DisposableEffect(relay, host) {
            relay.subHost = host
            onDispose { if (relay.subHost === host) relay.subHost = null }
        }
    }

    val measurePolicy = remember(host) {
        object : MeasurePolicy {
            private fun Density.minWidthPx(): Int = if (host.hasSubmenu) {
                ListPopupDefaults.MinWidthWithSubmenu.roundToPx()
            } else {
                ListPopupDefaults.MinWidth.roundToPx()
            }

            private fun widest(measurables: List<IntrinsicMeasurable>, height: Int): Int {
                var widest = 0
                for (measurable in measurables) {
                    val w = measurable.maxIntrinsicWidth(height)
                    if (w > widest) widest = w
                }
                return widest
            }

            override fun MeasureScope.measure(
                measurables: List<Measurable>,
                constraints: Constraints,
            ): MeasureResult {
                val listWidth = resolvePopupListWidth(
                    widest = widest(measurables, Constraints.Infinity),
                    minWidth = minWidthPx(),
                    maxWidth = ListPopupDefaults.MaxWidth.roundToPx(),
                    parentMin = constraints.minWidth,
                    parentMax = constraints.maxWidth,
                )
                val inset = PopupListVerticalPadding.roundToPx()
                val childConstraints = Constraints(minWidth = listWidth, maxWidth = listWidth)

                val placeables = ArrayList<Placeable>(measurables.size)
                val offsets = ArrayList<Int>(measurables.size)
                val rows = ArrayList<PopupListItemHandle>(measurables.size)
                val neutral = ArrayList<Pair<Float, Float>>()
                var y = inset
                for (measurable in measurables) {
                    val placeable = measurable.measure(childConstraints)
                    val handle = measurable.popupListItemHandle
                    val cells = (measurable.parentData as? PopupHorizontalRowParentData)?.cells?.invoke()
                    if (cells != null) {
                        for (cell in cells) {
                            cell.top = y + cell.localTop
                            cell.bottom = y + cell.localBottom
                            rows.add(cell)
                        }
                        // Gaps between the cells keep the current target, as COUI does over the row.
                        neutral.add(y.toFloat() to (y + placeable.height).toFloat())
                    } else if (handle != null) {
                        handle.top = y.toFloat()
                        handle.bottom = (y + placeable.height).toFloat()
                        rows.add(handle)
                    } else {
                        neutral.add(y.toFloat() to (y + placeable.height).toFloat())
                    }
                    offsets.add(y)
                    placeables.add(placeable)
                    y += placeable.height
                }
                val listHeight = y + inset

                host.width = listWidth.toFloat()
                host.maskHorizontalMargin = PopupMaskHorizontalMargin.toPx()
                host.maskCornerRadius = PopupMaskCornerRadius.toPx()
                host.onPlaced(rows, neutral)
                val snapped = snapPopupListHeight(placeables.map { it.height }, 2 * inset, host.viewportMax)
                if (host.snappedHeight != snapped) host.snappedHeight = snapped

                return layout(listWidth, listHeight) {
                    for (i in placeables.indices) {
                        placeables[i].placeRelative(0, offsets[i])
                    }
                }
            }

            override fun IntrinsicMeasureScope.minIntrinsicHeight(
                measurables: List<IntrinsicMeasurable>,
                width: Int,
            ): Int {
                val listWidth = resolvePopupListWidth(
                    widest = widest(measurables, Constraints.Infinity),
                    minWidth = minWidthPx(),
                    maxWidth = ListPopupDefaults.MaxWidth.roundToPx(),
                    parentMin = 0,
                    parentMax = Constraints.Infinity,
                )
                var height = 2 * PopupListVerticalPadding.roundToPx()
                for (measurable in measurables) height += measurable.minIntrinsicHeight(listWidth)
                return height
            }

            override fun IntrinsicMeasureScope.maxIntrinsicWidth(
                measurables: List<IntrinsicMeasurable>,
                height: Int,
            ): Int = resolvePopupListWidth(
                widest = widest(measurables, height),
                minWidth = minWidthPx(),
                maxWidth = ListPopupDefaults.MaxWidth.roundToPx(),
                parentMin = 0,
                parentMax = Constraints.Infinity,
            )
        }
    }

    Box(propagateMinConstraints = true) {
        Layout(
            content = {
                CompositionLocalProvider(LocalPopupListGestureHost provides host) {
                    content()
                }
            },
            modifier = Modifier
                .focusGroup()
                .layout { measurable, constraints ->
                    // Snap the viewport to whole rows, as COUI sizes the RecyclerView before showing it.
                    host.viewportMax = constraints.maxHeight
                    val snapped = host.snappedHeight
                    val viewport = if (snapped < constraints.maxHeight) {
                        constraints.copy(maxHeight = snapped.coerceAtLeast(constraints.minHeight))
                    } else {
                        constraints
                    }
                    val placeable = measurable.measure(viewport)
                    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
                }
                .verticalScroll(state = scrollState)
                .onGloballyPositioned { host.coordinates = it }
                .popupListGestures(
                    host = host,
                    relay = if (role == PopupListRole.Main) relay else null,
                    scope = coroutineScope,
                    touchSlop = touchSlop,
                    performHaptic = performHaptic,
                    performLongPressHaptic = performLongPressHaptic,
                )
                .drawWithContent {
                    drawContent()
                    drawPopupSharedMask(host.mask, maskColor, squircleEnabled)
                },
            measurePolicy = measurePolicy,
        )
        if (scrollState.maxValue > 0) {
            // COUITouchRecyclerView.applyPopupVerticalScrollBarDelegate: a COUIScrollBar inset 18dp
            // from the top and the bottom (coui_popup_list_window_scrollbar_vertical_padding_new).
            VerticalScrollBar(
                adapter = rememberScrollBarAdapter(scrollState),
                modifier = Modifier.matchParentSize(),
                trackPadding = PaddingValues(vertical = PopupScrollBarVerticalPadding),
            )
        }
    }
}

/**
 * The COUI 17 popup drag-to-select gesture (smali COUITouchRecyclerView.dispatchTouchEvent). A
 * single handler on the list owns DOWN/MOVE/UP: in dynamic selection mode (a list with no scroll
 * range) dragging moves the shared mask across rows with a haptic per new enabled row; in the
 * scrollable case the selection is released once the drag passes the touch slop so scrolling wins.
 */
private fun Modifier.popupListGestures(
    host: PopupListGestureHost,
    relay: PopupGestureRelay?,
    scope: CoroutineScope,
    touchSlop: Float,
    performHaptic: (CouiHapticEffect) -> Unit,
    performLongPressHaptic: () -> Unit,
): Modifier = pointerInput(host, relay) {
    awaitPointerEventScope {
        while (true) {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            val downY = down.position.y
            val downRow = host.onDown(down.position.x, downY, scope)
            var tracking = downRow != null
            // RegionBasedLongPressDetector (650ms) on a row that opens a sub menu. It watches the
            // whole gesture: holding still is not required, only staying on the row, and a row the
            // finger drags onto starts its own countdown.
            val longPress = PopupLongPressTracker(host, scope, performLongPressHaptic)
            longPress.update(down.position)
            // The sub list this gesture currently drives, once the long press has handed it over.
            var relayed: PopupListGestureHost? = null

            while (tracking) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                val change: PointerInputChange = event.changes.firstOrNull { it.id == down.id } ?: break

                if (event.changes.size > 1) {
                    // A second pointer cancels the selection, as COUI does on ACTION_POINTER_DOWN.
                    host.cancel(scope)
                    relayed?.cancel(relayed.scope ?: scope)
                    tracking = false
                    break
                }

                if (change.changedToUpIgnoreConsumed()) {
                    if (relayed != null) relayed.onUp(relayed.scope ?: scope) else host.onUp(scope)
                    change.consume()
                    tracking = false
                    break
                }

                if (longPress.triggered) {
                    // The finger steers the sub menu the long press opened once it has entered and
                    // the finger is over it (forwardTouchToSubMenuIfNeeded). Elsewhere the drag keeps
                    // selecting in this list: a deliberate deviation from COUI, which stops the main
                    // list's touch while a sub menu is open (onSubMenuStartToEnter), so a held finger
                    // never ends up with a menu that ignores it.
                    val wasRelayed = relayed != null
                    relayed = relaySubMenuMove(relay, host, relayed, change.position, scope, performHaptic)
                    if (relayed != null) {
                        if (!wasRelayed) host.yield(scope)
                        continue
                    }
                }

                val y = change.position.y
                if (host.isDynamicSelection) {
                    if (host.onMoveTo(change.position.x, y, scope)) {
                        performHaptic(CouiHapticEffect.Switch)
                    }
                } else if (abs(y - downY) > touchSlop) {
                    host.cancel(scope)
                    tracking = false
                }
                if (tracking) longPress.update(change.position)
            }
            longPress.stop()
        }
    }
}

/**
 * COUI RegionBasedLongPressDetector for one gesture of a popup list. Only a row that opens a sub
 * menu is a target. The countdown runs while the finger stays anywhere on that row (moving inside it
 * does not reset it, there is no touch slop); leaving the row stops it, and the row the finger
 * moves onto starts a fresh one. Firing plays the long-press haptic
 * (`performHapticFeedback(LONG_PRESS)`) and hands the row its long-press action.
 */
private class PopupLongPressTracker(
    private val host: PopupListGestureHost,
    private val scope: CoroutineScope,
    private val performHaptic: () -> Unit,
) {
    /** Whether the long press fired during this gesture (`STATE_TRIGGERED`). */
    var triggered: Boolean = false
        private set

    private var target: PopupListItemHandle? = null
    private var job: Job? = null

    /** Follows the finger at [position], in list space (`updatePosition` / `startDetection`). */
    fun update(position: Offset) {
        if (triggered) return
        val current = target
        if (current != null && current.contains(position.x, position.y)) return
        stop()
        val row = host.rowAt(position.x, position.y) ?: return
        if (!row.enabled || row.onLongClick == null) return
        target = row
        job = scope.launch {
            delay(POPUP_SUBMENU_LONG_PRESS_TIMEOUT_MILLIS)
            job = null
            target = null
            if (host.onLongPress(row, scope)) {
                triggered = true
                performHaptic()
            }
        }
    }

    /** Ends the detection (`stopDetection`), e.g. on UP. */
    fun stop() {
        job?.cancel()
        job = null
        target = null
    }
}

/**
 * Re-targets one move of a relayed gesture at the sub list: the point is carried from the main
 * list's content space through the window into the sub list's. Entering the sub list starts a
 * press there (synthetic DOWN), moving drags its mask, leaving cancels it. Returns the sub host the
 * gesture is now pressing, or `null`.
 */
private fun relaySubMenuMove(
    relay: PopupGestureRelay?,
    host: PopupListGestureHost,
    relayed: PopupListGestureHost?,
    position: Offset,
    scope: CoroutineScope,
    performHaptic: (CouiHapticEffect) -> Unit,
): PopupListGestureHost? {
    val sub = relay?.subHost?.takeIf { relay.subEntered }
    val from = host.coordinates
    val to = sub?.coordinates
    if (sub == null || from == null || to == null || !from.isAttached || !to.isAttached) {
        relayed?.cancel(relayed.scope ?: scope)
        return null
    }
    // The sub list's mask animates in the sub list's own scope, which outlives this gesture.
    val subScope = sub.scope ?: scope
    val local = to.windowToLocal(from.localToWindow(position))
    if (!sub.isInsideList(local.x, local.y)) {
        // cancelSubMenuGestureOutsideBounds.
        relayed?.cancel(subScope)
        return null
    }
    if (relayed !== sub) {
        // The synthetic ACTION_DOWN attaches the mask without a haptic, like a real DOWN.
        return if (sub.onDown(local.x, local.y, subScope) != null) sub else null
    }
    if (sub.onMoveTo(local.x, local.y, subScope)) performHaptic(CouiHapticEffect.Switch)
    return sub
}

/**
 * Claims every touch that lands on a popup surface once its lists have seen the DOWN, so a tap on
 * the menu never also counts as a tap outside it (the PopupWindow content consumes its own
 * touches). Only the DOWN is consumed: the menu's touch motion keeps following the moves.
 */
internal fun Modifier.consumePopupTouches(): Modifier = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Main)
            event.changes.forEach { if (it.changedToDownIgnoreConsumed()) it.consume() }
        }
    }
}

/** Draws the shared press mask over the rows (COUITouchRecyclerView.dispatchDraw → drawSharedMask). */
private fun DrawScope.drawPopupSharedMask(
    mask: PopupSharedMask,
    color: Color,
    squircleEnabled: Boolean,
) {
    val progress = mask.press.coerceIn(0f, 1f)
    if (progress <= 0f) return
    val geometry = mask.current() ?: return
    val rect = Rect(geometry.left, geometry.top, geometry.right, geometry.bottom)
    if (rect.isEmpty) return
    val path = Path()
    path.addSquircleRect(rect, geometry.radius, geometry.weight, squircleEnabled)
    drawPath(path = path, color = color, alpha = progress)
}

@Stable
interface PopupPositionProvider {
    /**
     * Calculate the position (offset) of Popup
     *
     * @param anchorBounds Bounds of the anchored (parent) component
     * @param windowBounds Bounds of the safe area of window (excluding the [WindowInsets.Companion.statusBars],
     *   [WindowInsets.Companion.navigationBars] and [WindowInsets.Companion.captionBar])
     * @param layoutDirection [LayoutDirection]
     * @param popupContentSize Actual size of the popup content
     * @param popupMargin (Extra) Margins for the popup content. See [PopupPositionProvider.getMargins]
     * @param alignment Alignment of the popup (relative to the window). See [PopupPositionProvider.Align]
     */
    fun calculatePosition(
        anchorBounds: IntRect,
        windowBounds: IntRect,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
        popupMargin: IntRect,
        alignment: Align,
    ): IntOffset

    /**
     * (Extra) Margins for the popup content.
     */
    fun getMargins(): PaddingValues

    /**
     * Position relative to the window, not relative to the anchor!
     */
    enum class Align {
        Start,
        End,
        TopStart,
        TopEnd,
        BottomStart,
        BottomEnd,
    }
}

object ListPopupDefaults {
    /**
     * Scale enter spring of the COUI 16 main menu (COUISpringForce response 0.35 / bounce 0.2).
     * The COUI 17 list popups use [ScaleEnterAnimationSpec]; this one remains for other floating
     * surfaces that borrowed it.
     */
    val FractionAnimationSpec = spring(dampingRatio = 0.8f, stiffness = 322.27f, visibilityThreshold = 0.0001f)

    /** Scale exit spring of the COUI 16 main menu (COUISpringForce response 0.3 / bounce 0). */
    val FractionExitAnimationSpec = spring<Float>(dampingRatio = 1f, stiffness = 438.65f, visibilityThreshold = 0.0001f)

    /** Alpha enter spring of the COUI 16 main menu (response 0.35 / bounce 0.2). */
    val AlphaEnterAnimationSpec = folmeSpring<Float>(damping = 0.8f, response = 0.35f)

    /** Alpha exit spring of the COUI 16 main menu (response 0.25 / bounce 0). */
    val AlphaExitAnimationSpec = folmeSpring<Float>(damping = 1f, response = 0.25f)

    /**
     * Travel of the menu along its gravity path while opening (COUI 17
     * `PopupGravityMotionParams.openPathSpring`, `SpringData(bounce 0.13, response 0.29)`; the
     * animate-kit spring damps at `1 - bounce`).
     */
    val PathEnterAnimationSpec: SpringSpec<Float> = popupSpring(response = 0.29f, bounce = 0.13f)

    /** Scale 0 → 1 while opening (`openScaleSpring`, bounce 0.2, response 0.4). */
    val ScaleEnterAnimationSpec: SpringSpec<Float> = popupSpring(response = 0.4f, bounce = 0.2f)

    /** Alpha 0 → 1 while opening (`openOpacitySpring`, bounce 0, response 0.27). */
    val OpacityEnterAnimationSpec: SpringSpec<Float> = popupSpring(response = 0.27f, bounce = 0f)

    /** Travel back to the anchor while closing (`closePathSpring`, bounce 0, response 0.26). */
    val PathExitAnimationSpec: SpringSpec<Float> = popupSpring(response = 0.26f, bounce = 0f)

    /** Scale 1 → 0 while closing (`closeScaleSpring`, bounce 0, response 0.21). */
    val ScaleExitAnimationSpec: SpringSpec<Float> = popupSpring(response = 0.21f, bounce = 0f)

    /** Alpha 1 → 0 while closing (`closeOpacitySpring`, bounce 0, response 0.23). */
    val OpacityExitAnimationSpec: SpringSpec<Float> = popupSpring(response = 0.23f, bounce = 0f)

    /**
     * Extra clearance reserved below the status bar before the popup may be placed
     * (COUI coui_popup_list_window_top_status_bar_margin).
     */
    val TopBarrier = 16.dp

    /**
     * Extra clearance reserved above the navigation bar (or the IME) before the popup may be placed
     * (COUI coui_popup_list_window_bottom_navigation_bar_margin).
     */
    val BottomBarrier = 32.dp

    /** The popup surface fill (`coui_popup_window_background`: `?couiColorBackgroundTop`). */
    @Composable
    fun backgroundColor(): Color = COUITheme.colorScheme.backgroundTop

    /**
     * The COUI 17 popup material the popups blur with by default (`RoundFrameLayout`
     * `DEFAULT_BLUR_RADIUS_PX` 160 and `applyBlurColorParams`, SDR branch): blend mode 4
     * (glow overlay) in both schemes, mix `coui_popup_list_mix_blur_light` `#99CDCDCD` / `_dark`
     * `#99262626`, blend `coui_popup_list_blend_blur_light` `#BFEEEEEE` / `_dark` `#99262626`.
     *
     * @param isDark Whether the dark scheme is active.
     */
    fun materialBlurSpec(isDark: Boolean): MaterialBlurSpec = if (isDark) PopupMaterialBlurDark else PopupMaterialBlurLight

    private val PopupMaterialBlurLight = MaterialBlurSpec(
        blurRadiusPx = 160f,
        mixColor = Color(0x99CDCDCD),
        blendColor = Color(0xBFEEEEEE),
        blendMode = MaterialBlurBlendMode.GlowOverlay,
    )

    private val PopupMaterialBlurDark = MaterialBlurSpec(
        blurRadiusPx = 160f,
        mixColor = Color(0x99262626),
        blendColor = Color(0x99262626),
        blendMode = MaterialBlurBlendMode.GlowOverlay,
    )

    /** Default animation spec driving the background dim while entering. */
    val DimEnterAnimationSpec = tween<Float>(durationMillis = 300, easing = SinOutEasing)

    /** Default animation spec driving the background dim while exiting. */
    val DimExitAnimationSpec = tween<Float>(durationMillis = 150, easing = SinOutEasing)

    /** Re-enter spring used when a cancelled back gesture restores the popup; matches [ScaleEnterAnimationSpec]. */
    val ResetAnimationSpec: SpringSpec<Float> = popupSpring(response = 0.4f, bounce = 0.2f)

    /**
     * Default minimum width of the popup (COUI coui_popup_list_window_min_width).
     * Also the lower clamp inside [ListPopupColumn]'s width measurement.
     */
    val MinWidth = 168.dp

    /**
     * Minimum width of a popup with a row that opens a sub menu
     * (COUI coui_popup_list_window_width_with_icon, `COUIPopupListWindow.getMainMenuMinWidth`).
     */
    val MinWidthWithSubmenu = 196.dp

    /**
     * Default maximum width of the popup window (COUI coui_popup_list_window_max_width).
     * Also the upper clamp inside [ListPopupColumn]'s width measurement.
     */
    val MaxWidth = 256.dp

    /**
     * Default minimum height the popup will occupy when measured. Used as the floor for the
     * effective `maxHeight` and `minHeight` constraints during placement.
     */
    val MinPopupHeight = 50.dp

    /**
     * The vertical gap between the anchor and the popup (COUI
     * coui_popup_list_window_default_vertical_gap_to_anchor, the `DEFAULT_ANCHOR_OUTSETS`).
     */
    val AnchorGap = 8.dp

    /**
     * The horizontal barrier of a compact window: COUI keeps the menu inside the responsive grid
     * margin (`ResponsiveUIModel.chooseMargin(MARGIN_SMALL)`, layout_grid_margin_compat_small).
     * Medium windows use 24dp and expanded ones 40dp; see [horizontalBarrier].
     */
    val HorizontalBarrier = 16.dp

    /**
     * The horizontal barrier COUI keeps a menu away from the window edges for a window of
     * [widthDp] × [heightDp] (`PopupMenuLocateHelper` left/right barrier rules with the
     * `MARGIN_SMALL` grid margin of `LayoutGridSystem`): 16dp in a compact window (narrower than
     * 600dp), 40dp in an `Expanded` one, 24dp in every medium-tier window.
     */
    fun horizontalBarrier(widthDp: Float, heightDp: Float): Dp = COUILayoutGrid.margin(widthDp.dp, heightDp.dp)

    /**
     * Creates a [PopupPositionProvider] that anchors the popup directly below (or above when there
     * is no room) the anchor, used by dropdown-style list popups.
     *
     * When the anchor bounds arrive collapsed to a single point — which is what
     * [Modifier.preciseClickAnchor] produces from the recorded touch point — the vertical gap to the
     * anchor is dropped, matching COUI `PopupMenuLocateHelper.setAnchor`: it collapses `mAnchorBounds`
     * to `(left + x, top + y)` and pairs it with the context rule's EMPTY_OUTSETS instead of
     * DEFAULT_ANCHOR_OUTSETS.
     *
     * @param verticalMargin The gap between the popup and the anchor.
     * @param horizontalMargin An extra horizontal margin applied on top of the window barriers.
     */
    fun dropdownPositionProvider(
        verticalMargin: Dp = AnchorGap,
        horizontalMargin: Dp = 0.dp,
    ): PopupPositionProvider = object : PopupPositionProvider {
        private val margins = PaddingValues(horizontal = horizontalMargin, vertical = verticalMargin)

        override fun calculatePosition(
            anchorBounds: IntRect,
            windowBounds: IntRect,
            layoutDirection: LayoutDirection,
            popupContentSize: IntSize,
            popupMargin: IntRect,
            alignment: PopupPositionProvider.Align,
        ): IntOffset {
            // A zero-area anchor is a recorded touch point; COUI then uses EMPTY_OUTSETS.
            val isPointAnchored = anchorBounds.width == 0 && anchorBounds.height == 0
            return locateDropdownPopup(
                anchor = anchorBounds,
                available = IntRect(
                    left = windowBounds.left + popupMargin.left,
                    top = windowBounds.top,
                    right = windowBounds.right - popupMargin.right,
                    bottom = windowBounds.bottom,
                ),
                size = popupContentSize,
                gapAbove = if (isPointAnchored) 0 else popupMargin.top,
                gapBelow = if (isPointAnchored) 0 else popupMargin.bottom,
            )
        }

        override fun getMargins(): PaddingValues = margins
    }

    /** Default dropdown [PopupPositionProvider] created by [dropdownPositionProvider]. */
    val DropdownPositionProvider: PopupPositionProvider = dropdownPositionProvider()

    /** A [PopupPositionProvider] that anchors the popup to a corner of the anchor for context menus. */
    val ContextMenuPositionProvider = object : PopupPositionProvider {
        override fun calculatePosition(
            anchorBounds: IntRect,
            windowBounds: IntRect,
            layoutDirection: LayoutDirection,
            popupContentSize: IntSize,
            popupMargin: IntRect,
            alignment: PopupPositionProvider.Align,
        ): IntOffset {
            val offsetX: Int
            val offsetY: Int
            when (alignment.resolve(layoutDirection)) {
                PopupPositionProvider.Align.TopStart -> {
                    offsetX = anchorBounds.left + popupMargin.left
                    offsetY = anchorBounds.bottom + popupMargin.top
                }

                PopupPositionProvider.Align.TopEnd -> {
                    offsetX = anchorBounds.right - popupContentSize.width - popupMargin.right
                    offsetY = anchorBounds.bottom + popupMargin.top
                }

                PopupPositionProvider.Align.BottomStart -> {
                    offsetX = anchorBounds.left + popupMargin.left
                    offsetY = anchorBounds.top - popupContentSize.height - popupMargin.bottom
                }

                PopupPositionProvider.Align.BottomEnd -> {
                    offsetX = anchorBounds.right - popupContentSize.width - popupMargin.right
                    offsetY = anchorBounds.top - popupContentSize.height - popupMargin.bottom
                }

                else -> {
                    // Fallback
                    offsetX = if (alignment.resolve(layoutDirection) == PopupPositionProvider.Align.End) {
                        anchorBounds.right - popupContentSize.width - popupMargin.right
                    } else {
                        anchorBounds.left + popupMargin.left
                    }
                    offsetY = if (windowBounds.bottom - anchorBounds.bottom > popupContentSize.height) {
                        // Show below
                        anchorBounds.bottom + popupMargin.bottom
                    } else if (anchorBounds.top - windowBounds.top > popupContentSize.height) {
                        // Show above
                        anchorBounds.top - popupContentSize.height - popupMargin.top
                    } else {
                        // Not enough room either way: COUI falls back to the available top.
                        windowBounds.top
                    }
                }
            }
            return IntOffset(
                x = offsetX.coerceIn(
                    windowBounds.left,
                    (windowBounds.right - popupContentSize.width).coerceAtLeast(windowBounds.left),
                ),
                y = offsetY.coerceIn(
                    windowBounds.top.coerceAtMost(windowBounds.bottom - popupContentSize.height),
                    windowBounds.bottom - popupContentSize.height,
                ),
            )
        }

        override fun getMargins(): PaddingValues = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
    }
}

/** An animate-kit spring (`IAnimCurve.Spring`): damping `1 - bounce`, stiffness `(2π / response)²`. */
private fun popupSpring(response: Float, bounce: Float): SpringSpec<Float> = spring(
    dampingRatio = (1f - bounce).coerceAtLeast(0.01f),
    stiffness = (2f * PI.toFloat() / response).let { it * it },
    visibilityThreshold = 0.001f,
)

/**
 * Places a dropdown menu the way COUI 17 `PopupMenuLocateHelper` does.
 *
 * - X (`mMainMenuLocateXRule`): centred on the anchor, pushed inside [available]; a menu that is
 *   still too wide is centred in [available].
 * - Y (`mMainMenuLocateYRule`): below the anchor grown by [gapBelow] when the menu fits there, else
 *   above it (grown by [gapAbove]), else at the top of [available] — COUI does not centre a menu
 *   that fits on neither side on its anchor.
 * - Finally the global-offset rule clamps the menu into [available].
 *
 * @param anchor The anchor bounds in window coordinates; a zero-area rect is a touch point.
 * @param available The window minus every barrier.
 * @param size The menu size.
 * @param gapAbove The anchor outset above the anchor.
 * @param gapBelow The anchor outset below the anchor.
 */
internal fun locateDropdownPopup(
    anchor: IntRect,
    available: IntRect,
    size: IntSize,
    gapAbove: Int,
    gapBelow: Int,
): IntOffset {
    var x = anchor.left + anchor.width / 2 - size.width / 2
    if (x < available.left) x = available.left
    if (x + size.width > available.right) x = available.right - size.width
    if (x < available.left) x = available.left + (available.width - size.width) / 2

    val realTop = anchor.top - gapAbove
    val realBottom = anchor.bottom + gapBelow
    val below = maxOf(realBottom, available.top)
    val above = minOf(realTop, available.bottom)
    val y = when {
        available.bottom - below >= size.height -> below
        above - available.top >= size.height -> above - size.height
        else -> available.top
    }

    val clampedX = x.coerceIn(available.left, maxOf(available.left, available.right - size.width))
    val clampedY = y.coerceIn(available.top, maxOf(available.top, available.bottom - size.height))
    return IntOffset(clampedX, clampedY)
}

/**
 * The cubic Bézier a COUI 17 menu centre travels along while it opens
 * (`BezierGravityPathMath.controlPoints` with the default `PopupGravityMotionParams`): from the
 * anchor's edge point, as an offset from the menu centre, to the menu centre itself. The control
 * points sit at [GRAVITY_P1_T] / [GRAVITY_P2_T] of the chord, pulled down along its normal by the
 * adaptive gravity, so a menu that opens off to the side of its anchor swings in on a slight arc.
 * All distances are raw pixels, as COUI defines them.
 */
@Immutable
internal data class PopupGravityPath(
    val ax: Float,
    val ay: Float,
    val p1x: Float,
    val p1y: Float,
    val p2x: Float,
    val p2y: Float,
) {
    /** The offset from the final menu centre at path progress [t] (0 = anchor, 1 = settled). */
    fun pointAt(t: Float): Offset {
        val u = 1f - t
        val b0 = u * u * u
        val b1 = 3f * u * u * t
        val b2 = 3f * u * t * t
        // The end point is (0, 0), so its term vanishes.
        return Offset(
            x = ax * b0 + p1x * b1 + p2x * b2,
            y = ay * b0 + p1y * b1 + p2y * b2,
        )
    }

    companion object {
        /** `gravityDistanceMinPx`. */
        const val GRAVITY_DISTANCE_MIN = 30f

        /** `gravityDistanceMaxPx`. */
        const val GRAVITY_DISTANCE_MAX = 400f

        /** `gravityArcMaxPx`. */
        const val GRAVITY_ARC_MAX = 130f

        /** `p1T`. */
        const val GRAVITY_P1_T = 0.35f

        /** `p2T`. */
        const val GRAVITY_P2_T = 0.65f

        val None = PopupGravityPath(0f, 0f, 0f, 0f, 0f, 0f)

        /**
         * Builds the path from the anchor point offset ([ax], [ay]) relative to the menu centre to
         * the centre (`BezierGravityPathMath.controlPoints` + `adaptiveGravityPx`).
         */
        fun from(ax: Float, ay: Float, pxScale: Float = 1f): PopupGravityPath {
            val dx = -ax
            val dy = -ay
            val length = sqrt(dx * dx + dy * dy)
            if (length < 1f) return PopupGravityPath(ax, ay, ax, ay, 0f, 0f)
            val distanceMin = GRAVITY_DISTANCE_MIN * pxScale
            val span = (GRAVITY_DISTANCE_MAX - GRAVITY_DISTANCE_MIN) * pxScale
            val distanceFactor = ((length - distanceMin) / span).coerceIn(0f, 1f)
            val absX = abs(dx)
            val absY = abs(dy)
            val gravity = GRAVITY_ARC_MAX * pxScale * distanceFactor * (min(absX, absY) / (max(absX, absY) + 1.0E-4f))
            // The chord normal, flipped to point down the screen.
            var nx = -dy / length
            var ny = dx / length
            if (ny < 0f) {
                nx = -nx
                ny = -ny
            }
            if (abs(ny) < 0.001f) {
                nx = 0f
                ny = 1f
            }
            return PopupGravityPath(
                ax = ax,
                ay = ay,
                p1x = ax + GRAVITY_P1_T * dx + nx * gravity,
                p1y = ay + GRAVITY_P1_T * dy + ny * gravity,
                p2x = ax + GRAVITY_P2_T * dx + nx * gravity,
                p2y = ay + GRAVITY_P2_T * dy + ny * gravity,
            )
        }
    }
}

/**
 * Scale of the gravity path's raw-pixel constants: COUI tuned 30 / 400 / 130 px on its 3.5x
 * phones. Android keeps raw pixels exactly like COUI; other platforms read them as pixels at that
 * reference density and scale by the actual one, as the number picker does with its velocities.
 */
internal fun popupGravityPxScale(density: Float): Float = numberPickerVelocityScale(density)

/**
 * Corner radius of the popup container: COUI 17 `RoundFrameLayout.getSmoothRadiusAndWeight` takes
 * coui_popup_round_frame_layout_corner_radius (28dp) for main and sub menus alike.
 */
internal val PopupCornerRadius = 28.dp

/** Smoothing weight of [PopupCornerRadius]: weight_16_1_normal (2.5), from the same method. */
internal val PopupCornerWeight = COUIRoundCorner.Normal.Weight

/**
 * Resolves the COUI popup container color for the current theme: `coui_popup_window_background`
 * fills with `?couiColorBackgroundTop` (#FFFFFF / #333333).
 */
@Composable
internal fun popupSurfaceColor(): Color = COUITheme.colorScheme.backgroundTop

/**
 * The OPlus light-source shadow a COUI 17 popup casts (`RoundFrameLayout.setClipMode` →
 * `ShadowUtils.setElevationToView(this, 3)`, `SHADOW_LV4`): the elevation, the light position and
 * radius from `coui_shadow_*_level4` (light: 180dp, y -200dp, z 10000dp, r 2000dp; `values-night`:
 * 36dp, y -1500dp, r 12000dp) and the shadow color alpha `coui_shadow_color_lv4` (48 / 150).
 *
 * The renderer (Skia `SkShadowUtils`, which HWUI draws shadows with) turns that into two layers:
 * - **Spot**: the outline offset away from the light by `e * (y - lightY) / (lightZ - e)` and blurred
 *   over the penumbra `lightR * e / (lightZ - e)`, at `colorAlpha * spotShadowAlpha`.
 * - **Ambient**: the outline blurred by `e / 2` (`kAmbientHeightFactor * kAmbientGeomFactor`) without
 *   offset, at `colorAlpha * ambientShadowAlpha / (1 + e / 128)`.
 *
 * COUI's themes leave `spotShadowAlpha` / `ambientShadowAlpha` alone, so the framework defaults
 * (0.19 / 0.039) apply. A Compose blur radius `r` fades over about `1.5 r`, so the penumbra is
 * divided by 1.5.
 */
@Immutable
internal data class PopupShadowSpec(
    val elevation: Dp,
    val lightY: Dp,
    val lightZ: Dp,
    val lightRadius: Dp,
    val colorAlpha: Float,
) {
    /** The spot blur radius: the penumbra as a Compose blur radius. */
    val spotRadius: Dp
        get() = lightRadius * (elevation / (lightZ - elevation)) / PENUMBRA_PER_BLUR_RADIUS

    /** The spot alpha (`colorAlpha * spotShadowAlpha`). */
    val spotAlpha: Float
        get() = colorAlpha * SPOT_SHADOW_ALPHA

    /** The ambient blur radius (`SkShadowUtils` ambient blur, `e / 2`). */
    val ambientRadius: Dp
        get() = elevation / 2f

    /** The ambient alpha (`colorAlpha * ambientShadowAlpha / (1 + e / 128)`). */
    val ambientAlpha: Float
        get() = colorAlpha * AMBIENT_SHADOW_ALPHA / (1f + elevation.value / 128f)

    /** The spot offset of an outline centred [centerY] dp down the window. */
    fun spotOffsetY(centerY: Dp): Dp {
        if (elevation <= 0.dp || lightZ <= elevation) return 0.dp
        return elevation * ((centerY - lightY) / (lightZ - elevation))
    }

    companion object {
        private const val SPOT_SHADOW_ALPHA = 0.19f
        private const val AMBIENT_SHADOW_ALPHA = 0.039f
        private const val PENUMBRA_PER_BLUR_RADIUS = 1.5f

        val Light = PopupShadowSpec(180.dp, (-200).dp, 10000.dp, 2000.dp, 48f / 255f)
        val Dark = PopupShadowSpec(36.dp, (-1500).dp, 10000.dp, 12000.dp, 150f / 255f)
    }
}

/**
 * Casts the popup's [PopupShadowSpec] shadow under [shape]: the ambient layer, then the spot layer
 * whose offset follows where the surface sits below the light. [alpha] scales both, as the
 * `RoundFrameLayout` override outline alpha does while a compact sub menu grows.
 */
@Composable
internal fun Modifier.popupDropShadow(shape: Shape, alpha: () -> Float = { 1f }): Modifier {
    val spec = if (COUITheme.colorScheme.background.luminance() < 0.5f) PopupShadowSpec.Dark else PopupShadowSpec.Light
    val centerY = remember { mutableFloatStateOf(Float.NaN) }
    return this
        .onGloballyPositioned { coordinates ->
            centerY.floatValue = coordinates.positionInWindow().y + coordinates.size.height / 2f
        }
        .dropShadow(shape) {
            radius = spec.ambientRadius.toPx()
            color = Color.Black.copy(alpha = spec.ambientAlpha * alpha().coerceIn(0f, 1f))
        }
        .dropShadow(shape) {
            val y = centerY.floatValue.takeUnless { it.isNaN() } ?: 0f
            radius = spec.spotRadius.toPx()
            offset = Offset(0f, spec.spotOffsetY((y / density).dp).toPx())
            color = Color.Black.copy(alpha = spec.spotAlpha * alpha().coerceIn(0f, 1f))
        }
}

internal fun safeTransformOrigin(x: Float, y: Float): TransformOrigin {
    val safeX = if (x.isNaN() || x < 0f) 0f else x
    val safeY = if (y.isNaN() || y < 0f) 0f else y
    return TransformOrigin(safeX, safeY)
}

/**
 * Describes how the popup is placed relative to its anchor, used to drive the directional reveal
 * and transform origin.
 *
 * @property showBelow Whether the popup is shown below the anchor.
 * @property showAbove Whether the popup is shown above the anchor.
 * @property isRightAligned Whether the popup is aligned to the right edge of the anchor.
 */
@Immutable
data class PopupLayoutPosition(
    val showBelow: Boolean,
    val showAbove: Boolean,
    val isRightAligned: Boolean,
)

/**
 * The resolved layout information for a list popup, produced by [rememberListPopupLayoutInfo].
 *
 * @property windowBounds Bounds of the area the popup is placed within: the window minus its
 *   insets and the COUI barriers (16dp below the status bar, 32dp above the navigation bar, the
 *   grid margin on both sides).
 * @property popupMargin The (extra) margins applied around the popup content.
 * @property effectiveTransformOrigin The transform origin in window coordinates used to scale the popup from its anchor corner.
 * @property localTransformOrigin The transform origin local to the popup content used by its [graphicsLayer] scaling.
 * @property popupLayoutPosition The resolved [PopupLayoutPosition] describing how the popup is placed relative to its anchor.
 */
@Immutable
data class ListPopupLayoutInfo(
    val windowBounds: IntRect,
    val popupMargin: IntRect,
    val effectiveTransformOrigin: TransformOrigin,
    val localTransformOrigin: TransformOrigin,
    val popupLayoutPosition: PopupLayoutPosition,
)

/**
 * Computes and remembers the [ListPopupLayoutInfo] for a list popup from its anchor and content size.
 *
 * @param alignment The [PopupPositionProvider.Align] of the popup relative to the window.
 * @param popupPositionProvider The [PopupPositionProvider] that computes the popup offset and margins.
 * @param parentBounds The bounds of the anchor (parent) component in window coordinates.
 * @param popupContentSize The measured size of the popup content; [IntSize.Zero] before it is measured.
 */
@Composable
fun rememberListPopupLayoutInfo(
    alignment: PopupPositionProvider.Align,
    popupPositionProvider: PopupPositionProvider,
    parentBounds: IntRect,
    popupContentSize: IntSize,
): ListPopupLayoutInfo {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val layoutDirection = LocalLayoutDirection.current
    val displayCutout = WindowInsets.displayCutout
    val statusBars = WindowInsets.statusBars
    val navigationBars = WindowInsets.navigationBars
    val captionBar = WindowInsets.captionBar
    val ime = WindowInsets.ime

    val margins = popupPositionProvider.getMargins()
    val popupMargin = remember(layoutDirection, density, margins) {
        with(density) {
            IntRect(
                left = margins.calculateLeftPadding(layoutDirection).roundToPx(),
                top = margins.calculateTopPadding().roundToPx(),
                right = margins.calculateRightPadding(layoutDirection).roundToPx(),
                bottom = margins.calculateBottomPadding().roundToPx(),
            )
        }
    }

    val containerSize = windowInfo.containerSize
    val imeBottom = ime.getBottom(density)

    val windowBounds = remember(
        layoutDirection,
        density,
        displayCutout,
        statusBars,
        navigationBars,
        captionBar,
        imeBottom,
        containerSize,
    ) {
        with(density) {
            // COUI available rect: the visible frame (status bar, navigation bar or IME removed)
            // shrunk by the top / bottom barriers, and the grid margin at both sides unless the
            // cut-out reaches further in.
            val barrier = ListPopupDefaults.horizontalBarrier(
                widthDp = containerSize.width / density.density,
                heightDp = containerSize.height / density.density,
            ).roundToPx()
            val bottomInset = maxOf(navigationBars.getBottom(this) + captionBar.getBottom(this), imeBottom)
            IntRect(
                left = maxOf(displayCutout.getLeft(this, layoutDirection), barrier),
                top = statusBars.getTop(this) + ListPopupDefaults.TopBarrier.roundToPx(),
                right = containerSize.width - maxOf(displayCutout.getRight(this, layoutDirection), barrier),
                bottom = containerSize.height - bottomInset - ListPopupDefaults.BottomBarrier.roundToPx(),
            )
        }
    }

    val predictedTransformOrigin = remember(alignment, popupMargin, parentBounds, layoutDirection, containerSize) {
        val xInWindow = when (alignment.resolve(layoutDirection)) {
            PopupPositionProvider.Align.End,
            PopupPositionProvider.Align.TopEnd,
            PopupPositionProvider.Align.BottomEnd,
            -> parentBounds.right - popupMargin.right

            else -> parentBounds.left + popupMargin.left
        }
        val yInWindow = when (alignment.resolve(layoutDirection)) {
            PopupPositionProvider.Align.BottomEnd, PopupPositionProvider.Align.BottomStart ->
                parentBounds.top - popupMargin.bottom

            else ->
                parentBounds.bottom + popupMargin.bottom
        }
        safeTransformOrigin(
            xInWindow / containerSize.width.toFloat(),
            yInWindow / containerSize.height.toFloat(),
        )
    }

    val calculatedOffset = remember(
        popupContentSize,
        windowBounds,
        parentBounds,
        alignment,
        layoutDirection,
        popupMargin,
        popupPositionProvider,
    ) {
        if (popupContentSize == IntSize.Zero) {
            IntOffset.Zero
        } else {
            popupPositionProvider.calculatePosition(
                parentBounds,
                windowBounds,
                layoutDirection,
                popupContentSize,
                popupMargin,
                alignment,
            )
        }
    }

    val popupLayoutPosition = remember(
        popupContentSize,
        windowBounds,
        parentBounds,
        alignment,
        calculatedOffset,
        layoutDirection,
    ) {
        if (popupContentSize == IntSize.Zero) {
            val isRightAligned = when (alignment.resolve(layoutDirection)) {
                PopupPositionProvider.Align.End,
                PopupPositionProvider.Align.TopEnd,
                PopupPositionProvider.Align.BottomEnd,
                -> true

                else -> false
            }
            PopupLayoutPosition(showBelow = true, showAbove = false, isRightAligned = isRightAligned)
        } else {
            // COUI PopupMenuDomain.isMainMenuBelowAnchor: the menu centre below the anchor centre.
            val popupCenterY = calculatedOffset.y + popupContentSize.height / 2
            val anchorCenterY = parentBounds.top + parentBounds.height / 2
            val showBelow = popupCenterY > anchorCenterY
            val showAbove = !showBelow

            val distLeft = abs(calculatedOffset.x - parentBounds.left)
            val distRight = abs((calculatedOffset.x + popupContentSize.width) - parentBounds.right)
            val isRightAligned = distRight < distLeft

            PopupLayoutPosition(showBelow = showBelow, showAbove = showAbove, isRightAligned = isRightAligned)
        }
    }

    val effectiveTransformOrigin = remember(
        popupContentSize,
        calculatedOffset,
        containerSize,
        predictedTransformOrigin,
    ) {
        if (popupContentSize == IntSize.Zero) {
            predictedTransformOrigin
        } else {
            safeTransformOrigin(
                (calculatedOffset.x + popupContentSize.width / 2f) / containerSize.width.toFloat(),
                (calculatedOffset.y + popupContentSize.height / 2f) / containerSize.height.toFloat(),
            )
        }
    }

    return ListPopupLayoutInfo(
        windowBounds = windowBounds,
        popupMargin = popupMargin,
        effectiveTransformOrigin = effectiveTransformOrigin,
        // COUI 17 scales the menu about its centre: the view pivot sits at the enter pivot, but
        // COUIPopupTouchMotionAnimator.computePivotCorrection translates it back to the centre.
        localTransformOrigin = TransformOrigin.Center,
        popupLayoutPosition = popupLayoutPosition,
    )
}

/**
 * The gravity path of a main menu at [menuOffset] × [menuSize] opening from [anchor]
 * (`PopupMenuDomain.computeMainMenuPopupPathAnchorOffset`, centre slot): the path starts on the
 * anchor's horizontal centre, at its bottom edge when the menu opens below and its top edge when
 * it opens above, expressed relative to the menu centre.
 */
internal fun popupGravityPathFor(
    anchor: IntRect,
    menuOffset: IntOffset,
    menuSize: IntSize,
    density: Float,
): PopupGravityPath {
    if (menuSize == IntSize.Zero) return PopupGravityPath.None
    val centerX = menuOffset.x + menuSize.width / 2f
    val centerY = menuOffset.y + menuSize.height / 2f
    val below = centerY > anchor.top + anchor.height / 2f
    val anchorX = anchor.left + anchor.width / 2f
    val anchorY = if (below) anchor.bottom.toFloat() else anchor.top.toFloat()
    return PopupGravityPath.from(anchorX - centerX, anchorY - centerY, popupGravityPxScale(density))
}

/**
 * The scaling and fading container that hosts a list popup's content.
 *
 * @param popupContentSize The last reported size of the content, compared against the latest
 *   measurement to avoid redundant [onPopupContentSizeChange] callbacks.
 * @param onPopupContentSizeChange Called when the measured content size changes.
 * @param fractionProgress Provides the current scale (0 → 1) of the popup.
 * @param alphaProgress Provides the current alpha (0 → 1) of the popup content.
 * @param popupLayoutPosition The [PopupLayoutPosition] describing the popup's spawn direction.
 * @param localTransformOrigin The transform origin local to the content used while scaling;
 *   COUI 17 scales about the centre.
 * @param modifier The modifier to be applied to the popup container.
 * @param translation Provides the current offset of the popup from its resting place, i.e. the
 *   point of its gravity path.
 * @param backgroundColor The surface fill, couiColorBackgroundTop by default. Pass a transparent
 *   color alongside a blurring [backgroundModifier].
 * @param backgroundModifier A [Modifier] applied inside the surface clip, under the content and over
 *   [backgroundColor]. Use it to paint the COUI 17 blurred background, e.g. with coui-blur's
 *   `Modifier.couiMaterialBlur(backdrop, MaterialBlurDefaults.popup(isDark))`.
 * @param materialEffect Whether to draw the material effects COUI adds to a blurred popup
 *   (`RoundFrameLayout.initMaterialEffects`): the shadow-edge stroke and the touch spotlight.
 * @param content The content of the popup.
 */
@Composable
fun ListPopupContent(
    popupContentSize: IntSize,
    onPopupContentSizeChange: (IntSize) -> Unit,
    fractionProgress: () -> Float,
    alphaProgress: () -> Float,
    popupLayoutPosition: PopupLayoutPosition,
    localTransformOrigin: TransformOrigin,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    translation: () -> Offset = { Offset.Zero },
    backgroundColor: Color = popupSurfaceColor(),
    materialEffect: Boolean = false,
    content: @Composable () -> Unit,
) {
    val shape = remember { RoundedCornerShape(PopupCornerRadius) }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                val size = coordinates.size
                if (popupContentSize != size) onPopupContentSizeChange(size)
            }
            .graphicsLayer {
                val scale = fractionProgress().coerceAtLeast(0f)
                scaleX = scale
                scaleY = scale
                val offset = translation()
                translationX = offset.x
                translationY = offset.y
                transformOrigin = localTransformOrigin
            }
            // COUI BasePopupMenuAnimationController.makeAnimator: deformation and rubber band
            // follow a finger on the menu, without the press scale.
            .couiTouchMotion(interactionSource = null, pressScaleEnabled = false)
            // The shadow fades by its own colour outside the fading layer: a layer below full
            // alpha renders offscreen at its own bounds and would cut the shadow off.
            .popupDropShadow(shape, alpha = alphaProgress)
            .graphicsLayer { alpha = alphaProgress().coerceIn(0f, 1f) }
            .squircleSurface(color = backgroundColor, cornerRadius = PopupCornerRadius, weight = PopupCornerWeight)
            .then(backgroundModifier)
            .popupMaterialEffect(enabled = materialEffect)
            .consumePopupTouches(),
    ) {
        content()
    }
}
