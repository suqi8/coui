// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import io.github.suqi8.coui.kmp.effect.TouchMotionState

/**
 * The touch motion of a COUI 17 popup panel (`BasePopupMenuAnimationController.makeAnimator` for
 * the main menu, `makeSubMenuAnimator` for a sub menu): a `COUIPopupTouchMotionAnimator` with the
 * deformation and the rubber band on and the press scale off, on the default touch-motion tier
 * (28dp rubber band, 150dp deformation, area-resolved hand-up spring, see [TouchMotionState]).
 *
 * The animator is fed from `RoundFrameLayout.dispatchTouchEvent` (`OnDispatchTouchEventListener`),
 * which sees every event of a gesture before the list does, whoever handles it afterwards. So the
 * panel keeps stretching and following the finger for the whole gesture: while the list
 * drag-selects, while it scrolls, and while the finger is outside the panel, and it springs back
 * from wherever it is when the finger lifts. Events are observed in the initial pass, and nothing
 * here treats a consumed move as a cancellation (unlike the button motion, which a scrolling parent
 * may take over): a popup list consumes every drag past the touch slop for its scroll, which used to
 * end the panel's stretch after a few pixels.
 *
 * The pointer input sits outside the layer it drives, so positions are in the panel's unstretched
 * space, as the main menu's animator maps each event back through the panel matrix
 * (`MotionEvent.transform(getMatrix())`) and the drag is the finger's own.
 *
 * @param state The animator state; hoist it when another panel follows this one
 *   (`ParentInfluenceUnit`).
 * @param enabled Whether touches drive the panel. Turning it off springs the panel home
 *   (`resetTouchOnly(true)`) and ignores the gesture in progress, as the main menu does while its
 *   sub menu is visible.
 * @param transformOrigin The point, as fractions of the panel size, the deformation stretches about:
 *   the view pivot the animator's scale is applied around. COUI leaves the main menu's pivot at its
 *   enter pivot ([popupEnterPivot]) and corrects only the opening scale back to the centre
 *   (`COUIPopupTouchMotionAnimator.computePivotCorrectionX`), so the stretch is anchored there.
 */
@Composable
internal fun Modifier.popupTouchMotion(
    state: TouchMotionState,
    enabled: Boolean = true,
    transformOrigin: () -> TransformOrigin = { TransformOrigin.Center },
): Modifier {
    val scope = rememberCoroutineScope()
    val currentOrigin by rememberUpdatedState(transformOrigin)
    LaunchedEffect(state, enabled) {
        if (!enabled) {
            state.touching = false
            with(state) { densityOrNull?.release(0f, 0f, scope) }
        }
    }
    return this
        .pointerInput(state, enabled) {
            if (!enabled) return@pointerInput
            state.densityOrNull = this
            awaitEachGesture {
                val first = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                val tracker = VelocityTracker()
                tracker.addPosition(first.uptimeMillis, first.position)
                var activeId: PointerId = first.id
                var lifted = false
                state.touching = true
                // COUIITouchMotionUnit.handleTouchDown: a finger that catches the panel still
                // springing back stops it there and resumes the drag from that displacement.
                with(state) { down(first.position, scope) }
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    // ACTION_POINTER_DOWN: the newest pointer drives the panel.
                    val newcomer = event.changes.firstOrNull { it.pressed && !it.previousPressed && it.id != activeId }
                    if (newcomer != null) {
                        activeId = newcomer.id
                        state.switchPointer(newcomer.position)
                        tracker.resetTracking()
                        tracker.addPosition(newcomer.uptimeMillis, newcomer.position)
                        continue
                    }
                    val active = event.changes.firstOrNull { it.id == activeId }
                    if (active == null || !active.pressed) {
                        // ACTION_POINTER_UP of the active pointer hands over to another one.
                        val next = event.changes.firstOrNull { it.pressed && it.id != activeId }
                        if (next != null) {
                            activeId = next.id
                            state.switchPointer(next.position)
                            tracker.resetTracking()
                            tracker.addPosition(next.uptimeMillis, next.position)
                            continue
                        }
                        if (active != null) {
                            tracker.addPosition(active.uptimeMillis, active.position)
                            lifted = true
                        }
                        break
                    }
                    tracker.addPosition(active.uptimeMillis, active.position)
                    with(state) { move(active.position, scope) }
                }
                state.touching = false
                // ACTION_UP hands the finger's velocity to the hand-up spring; a lost pointer
                // (ACTION_CANCEL) springs home without one.
                val velocity = if (lifted) tracker.calculateVelocity() else null
                with(state) { release(velocity?.x ?: 0f, velocity?.y ?: 0f, scope) }
            }
        }
        .graphicsLayer {
            state.width = size.width
            state.height = size.height
            state.widthDp = size.width / density
            state.heightDp = size.height / density
            // The press scale unit is off (setPressScaleEnable(false)): only the deformation and
            // the rubber band reach the panel.
            scaleX = state.scaleX.value
            scaleY = state.scaleY.value
            translationX = state.translationX.value
            translationY = state.translationY.value
            // `this.`: the parameter of the same name would shadow the layer property.
            this.transformOrigin = currentOrigin()
        }
}

/**
 * The pivot a COUI 17 main menu at [menuOffset] × [menuSize] keeps while it opens from [anchor] and
 * while a finger deforms it (`PopupMenuDomain.getMainMenuEnterPivotX` / `getMainMenuEnterPivotY`
 * with the default centre path anchor): the anchor's horizontal centre, clamped into the menu, on
 * the menu edge that faces the anchor (the top edge when the menu centre lies below the anchor
 * centre, `isMainMenuBelowAnchor`). A menu opened below its anchor therefore stretches downwards
 * from its top edge, as if hanging from the anchor. Integer centres as in `Rect.centerX()`.
 */
internal fun popupEnterPivot(anchor: IntRect, menuOffset: IntOffset, menuSize: IntSize): TransformOrigin {
    if (menuSize.width <= 0 || menuSize.height <= 0) return TransformOrigin.Center
    val anchorCenterX = (anchor.left + anchor.right) shr 1
    val anchorCenterY = (anchor.top + anchor.bottom) shr 1
    val menuCenterY = menuOffset.y + (menuSize.height shr 1)
    val pivotX = (anchorCenterX - menuOffset.x).coerceIn(0, menuSize.width)
    val below = menuCenterY > anchorCenterY
    return TransformOrigin(pivotX.toFloat() / menuSize.width, if (below) 0f else 1f)
}
