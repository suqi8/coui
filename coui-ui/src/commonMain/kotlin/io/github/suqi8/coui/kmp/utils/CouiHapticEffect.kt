// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import androidx.compose.runtime.Composable

/**
 * A COUI (ColorOS) haptic effect, mapped to OPLUS linear-motor feedback constants on Android and to
 * the closest standard haptic on every other platform.
 *
 * The numeric values mirror `COUIHapticFeedbackConstants`, the OEM `performHapticFeedback`
 * constants the real COUI widgets pass to `View.performHapticFeedback`:
 * - [Switch]: `GRANULAR_SHORT_VIBRATE` (302), a tap / confirm
 * - [Strength]: `GRANULAR_SHORT_VIBRATE_SYNC` (308), a discrete step / tick
 * - [ScrollEdge]: `EDGE_LIST_VIBRATE` (307), a scroll view hitting its edge
 * - [LongVibrate]: `LONG_VIBRATE` (300), a long error buzz
 * - [KeyboardTouch]: `KEYBOARD_TOUCH_FEEDBACK` (301), a keyboard key tap
 * - [ShortTripleVibrate]: `SHORT_TRIPLE_VIBRATE` (304), a triple error buzz
 * - [SteppingShort]: `STEPPING_SHORT_VIBRATE` (305), a slider drag step
 * - [EdgeShort]: `EDGE_SHORT_VIBRATE` (306), a slider reaching an end
 * - [EdgeSqueeze]: `EDGE_SQUEEZE` (14), a panel settling at its edge
 */
enum class CouiHapticEffect {
    /**
     * `GRANULAR_SHORT_VIBRATE` (302): switch, checkbox, tab, segment, FAB and popup-list taps
     * (`COUISwitch`, `COUICheckBoxPreference`, `COUITabLayout`, `COUISegmentButtonLayout`,
     * `COUIFloatingButton`, `COUITouchListView`), and the lock-pattern / numeric-keyboard hit on
     * linear-motor devices.
     */
    Switch,

    /**
     * `GRANULAR_SHORT_VIBRATE_SYNC` (308): stepper and picker ticks (`COUIStepperView`,
     * `COUINumberPicker`), the alphabet index bar (`COUITouchSearchView.performFeedback`) and a
     * sectioned slider's step (`VibrationFeedbackHandler.performNormalFeedback`). Where the device
     * does not recognise 308 the official widgets retry 302, and so does this effect on Android.
     */
    Strength,

    /**
     * `EDGE_LIST_VIBRATE` (307): a scroll view reaching its edge (`COUIScrollView`,
     * `COUINestedScrollView`, `COUIHorizontalScrollView`).
     */
    ScrollEdge,

    /**
     * `LONG_VIBRATE` (300): the wrong-password buzz on devices without a linear motor
     * (`COUISimpleLock.performFeedback`, `COUILockPatternView.performWrongModeFeedback`). On Android
     * prefer [ShortTripleVibrate], which already falls back to 300 on such devices.
     */
    LongVibrate,

    /**
     * `KEYBOARD_TOUCH_FEEDBACK` (301): a numeric-keyboard key press (`COUINumericKeyboard`
     * `setTouchFeedback`). The official widget plays 302 on linear-motor devices and 301 otherwise;
     * on Android this effect makes the same choice.
     */
    KeyboardTouch,

    /**
     * `SHORT_TRIPLE_VIBRATE` (304): the wrong-password buzz of `COUISimpleLock` and
     * `COUILockPatternView`. The official widgets play 304 on linear-motor devices and 300
     * ([LongVibrate]) otherwise; on Android this effect makes the same choice.
     */
    ShortTripleVibrate,

    /**
     * `STEPPING_SHORT_VIBRATE` (305): each progress step while a continuous slider is dragged
     * (`VibrationFeedbackHandler.performNormalFeedback`, `COUIVerticalSeekBar`).
     */
    SteppingShort,

    /**
     * `EDGE_SHORT_VIBRATE` (306): a slider reaching its minimum or maximum
     * (`VibrationFeedbackHandler.performNormalFeedback`, `COUIVerticalSeekBar`).
     */
    EdgeShort,

    /**
     * `EDGE_SQUEEZE` (14): a bottom sheet settling fully expanded at the end of its show animation
     * (`COUIBottomSheetDialog.animationEnd`, behavior state `STATE_EXPANDED`).
     */
    EdgeSqueeze,
}

/**
 * Performs a [CouiHapticEffect]. On OPLUS/ColorOS devices this invokes the matching linear-motor
 * waveform through `View.performHapticFeedback`; on other Android builds and on non-Android targets
 * it falls back to the closest standard Compose haptic.
 *
 * Returns a callback so the effect can be triggered from outside the composition (e.g. inside an
 * animation or gesture callback) without re-resolving platform handles each time.
 */
@Composable
expect fun rememberCouiHaptic(): (CouiHapticEffect) -> Unit

/**
 * Performs a [CouiHapticEffect] at a relative intensity in `0..1`, for widgets whose COUI original
 * scales a RichTap transient with the gesture velocity (COUINumberPicker through
 * `VibrateUtils.setLinearMotorVibratorStrength`).
 *
 * On Android 11+ apps holding the `VIBRATE` permission, on a vibrator that supports composition
 * primitives, this plays a scaled `VibrationEffect.Composition` primitive (the closest public API to
 * the RichTap transient) while the system haptic feedback setting is on. Otherwise, and on every
 * other platform, it falls back to [rememberCouiHaptic] and the intensity is ignored.
 */
@Composable
expect fun rememberCouiScaledHaptic(): (effect: CouiHapticEffect, intensity: Float) -> Unit
