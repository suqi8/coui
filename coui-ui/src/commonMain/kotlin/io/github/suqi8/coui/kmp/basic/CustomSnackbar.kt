// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import io.github.suqi8.coui.kmp.effect.couiPressScale
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.abs

/**
 * A snack bar whose whole look is supplied by [content], after COUI `COUICustomSnackBar`: a bare
 * frame around a custom view that adds the snack bar behaviour and no surface of its own.
 *
 * - **Auto dismiss** (`setAutoDismiss` / `setAutoDismissTime`): with [autoDismissMillis] set, the
 *   snack bar dismisses itself that long after the last touch; every touch event restarts the
 *   countdown (`dispatchTouchEvent` -> `g()` reposts the dismiss runnable).
 * - **Slide to dismiss** (`setTouchSlidable`): the snack bar follows a downward drag and cannot move
 *   above its place; releasing it while it is dragged down dismisses it. COUI starts following on the
 *   first moved pixel, so a jittery tap on a button inside would dismiss the snack bar; here the drag
 *   starts past the touch slop and a shorter drag springs back.
 * - **Press feedback** (`setPressFeedBack`): the snack bar shrinks while pressed
 *   (`COUIPressFeedbackHelper`, drawn with the COUI 17 touch-motion press scale).
 * - **Dismiss animation** (`setDismissWithAnim`): a 180ms fade on `PathInterpolator(0.1, 0, 0.1, 1)`
 *   (`COUICustomSnackAnimUtil`), after which [onDismiss] runs; without it [onDismiss] runs at once.
 *
 * Place it yourself (for example at the bottom of a `Box`) and remove it from the composition in
 * [onDismiss]; wrap it in `AnimatedVisibility` for a show animation, which COUI leaves to the caller
 * (`setShowAnimSet`). To show it through a [SnackbarHost], use the [SnackbarData] overload.
 *
 * @param onDismiss Called once the snack bar has been dismissed (after the fade, if any).
 * @param modifier The modifier to be applied to the [CustomSnackbar].
 * @param touchSlidable Whether the snack bar can be dragged down to dismiss it.
 * @param pressFeedback Whether the snack bar shrinks while pressed.
 * @param dismissWithAnimation Whether dismissing fades the snack bar out first.
 * @param autoDismissMillis The idle time after which the snack bar dismisses itself, or `null` to
 *   keep it until it is slid away or removed.
 * @param content The custom content of the snack bar, including its surface.
 */
@Composable
fun CustomSnackbar(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    touchSlidable: Boolean = false,
    pressFeedback: Boolean = false,
    dismissWithAnimation: Boolean = true,
    autoDismissMillis: Long? = null,
    content: @Composable () -> Unit,
) {
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val currentDismissWithAnimation by rememberUpdatedState(dismissWithAnimation)
    val scope = rememberCoroutineScope()
    val alpha = remember { Animatable(1f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var pressed by remember { mutableStateOf(false) }
    // Touch events restart the auto dismiss countdown.
    val touchSignal = remember { Channel<Unit>(Channel.CONFLATED) }
    val dismissed = remember { BooleanArray(1) }

    val dismiss: () -> Unit = remember {
        {
            if (!dismissed[0]) {
                dismissed[0] = true
                if (currentDismissWithAnimation) {
                    scope.launch {
                        alpha.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(
                                durationMillis = CustomSnackbarDefaults.DismissDurationMillis,
                                easing = CustomSnackbarDefaults.DismissEasing,
                            ),
                        )
                        currentOnDismiss()
                    }
                } else {
                    currentOnDismiss()
                }
            }
        }
    }

    if (autoDismissMillis != null) {
        LaunchedEffect(autoDismissMillis) {
            while (true) {
                withTimeoutOrNull(autoDismissMillis) { touchSignal.receive() } ?: break
            }
            dismiss()
        }
    }

    Box(
        modifier = modifier
            .semantics { liveRegion = LiveRegionMode.Polite }
            .graphicsLayer {
                translationY = offsetY
                this.alpha = alpha.value
            }
            .couiPressScale(pressed = pressed, enabled = pressFeedback)
            .pointerInput(touchSlidable, pressFeedback) {
                awaitPointerEventScope {
                    var down = false
                    var sliding = false
                    var rejected = false
                    var slid = false
                    var pendingY = 0f
                    var pendingX = 0f
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        touchSignal.trySend(Unit)
                        val change = event.changes.firstOrNull() ?: continue
                        if (!down) {
                            if (!change.pressed) continue
                            // ACTION_DOWN.
                            down = true
                            sliding = false
                            rejected = false
                            slid = false
                            pendingX = 0f
                            pendingY = 0f
                            if (pressFeedback) pressed = true
                            continue
                        }
                        if (change.pressed) {
                            // ACTION_MOVE.
                            if (!touchSlidable || rejected) continue
                            val delta = change.positionChange()
                            if (!sliding) {
                                pendingX += delta.x
                                pendingY += delta.y
                                val slop = viewConfiguration.touchSlop
                                if (abs(pendingX) < slop && abs(pendingY) < slop) continue
                                // Only a downward drag slides; anything else stays with the content.
                                if (pendingY <= abs(pendingX)) {
                                    rejected = true
                                    continue
                                }
                                sliding = true
                            }
                            // The snack bar never moves above its place (layout clamped to p / q).
                            val candidate = offsetY + delta.y
                            if (candidate < 0f) {
                                offsetY = 0f
                                slid = false
                            } else {
                                offsetY = candidate
                                slid = true
                            }
                            change.consume()
                        } else {
                            // ACTION_UP / ACTION_CANCEL.
                            down = false
                            if (pressFeedback) pressed = false
                            if (slid) {
                                dismiss()
                            } else {
                                offsetY = 0f
                            }
                        }
                    }
                }
            },
        propagateMinConstraints = true,
    ) {
        content()
    }
}

/**
 * A [CustomSnackbar] bound to a [SnackbarData] shown by a [SnackbarHost]: sliding it away dismisses
 * the data. The host's own timeout, show / dismiss animation and horizontal swipe still apply; pass
 * `canSwipeToDismiss = false` to the host to leave dragging to this snack bar.
 *
 * ```kotlin
 * SnackbarHost(state, canSwipeToDismiss = false) { data ->
 *     CustomSnackbar(data, touchSlidable = true) {
 *         Card(shadow = CardDefaults.shadow()) { Text(data.visuals.message) }
 *     }
 * }
 * ```
 *
 * @param data The [SnackbarData] of the snack bar.
 * @param modifier The modifier to be applied to the [CustomSnackbar].
 * @param touchSlidable Whether the snack bar can be dragged down to dismiss it.
 * @param pressFeedback Whether the snack bar shrinks while pressed.
 * @param dismissWithAnimation Whether a slide dismissal fades the snack bar out first.
 * @param content The custom content of the snack bar.
 */
@Composable
fun CustomSnackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
    touchSlidable: Boolean = false,
    pressFeedback: Boolean = false,
    dismissWithAnimation: Boolean = true,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val currentData by rememberUpdatedState(data)
    CustomSnackbar(
        onDismiss = { scope.launch { currentData.dismiss() } },
        modifier = modifier,
        touchSlidable = touchSlidable,
        pressFeedback = pressFeedback,
        dismissWithAnimation = dismissWithAnimation,
        content = content,
    )
}

/**
 * Defaults of [CustomSnackbar], from COUI `COUICustomSnackAnimUtil`.
 */
object CustomSnackbarDefaults {
    /** The dismiss fade duration (`COUICustomSnackAnimUtil` dismiss `AnimatorSet.setDuration(180)`). */
    val DismissDurationMillis = 180

    /** The dismiss fade easing (`COUICustomSnackAnimUtil` `PathInterpolator(0.1, 0, 0.1, 1)`). */
    val DismissEasing: Easing = CubicBezierEasing(0.1f, 0f, 0.1f, 1f)
}
