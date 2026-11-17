// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import android.os.Build
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

// COUIHapticFeedbackConstants: the OPLUS/ColorOS feedback constants the real COUI widgets pass to
// View.performHapticFeedback. The ColorOS framework maps them to dedicated linear-motor waveforms,
// while AOSP builds ignore unknown constants (performHapticFeedback returns false).
private const val OPLUS_LONG_VIBRATE = 300
private const val OPLUS_KEYBOARD_TOUCH_FEEDBACK = 301
private const val OPLUS_GRANULAR_SHORT_VIBRATE = 302
private const val OPLUS_SHORT_TRIPLE_VIBRATE = 304
private const val OPLUS_STEPPING_SHORT_VIBRATE = 305
private const val OPLUS_EDGE_SHORT_VIBRATE = 306
private const val OPLUS_EDGE_LIST_VIBRATE = 307
private const val OPLUS_GRANULAR_SHORT_VIBRATE_SYNC = 308

// COUIHapticFeedbackConstants.EDGE_SQUEEZE, the hidden AOSP HapticFeedbackConstants.EDGE_SQUEEZE.
private const val EDGE_SQUEEZE = 14

// AOSP fallbacks (HapticFeedbackConstants) for non-ColorOS devices.
private const val AOSP_LONG_PRESS = 0 // HapticFeedbackConstants.LONG_PRESS
private const val AOSP_KEYBOARD_TAP = 3 // HapticFeedbackConstants.KEYBOARD_TAP
private const val AOSP_CLOCK_TICK = 4 // HapticFeedbackConstants.CLOCK_TICK
private const val AOSP_CONTEXT_CLICK = 6 // HapticFeedbackConstants.CONTEXT_CLICK
private const val AOSP_REJECT = 17 // HapticFeedbackConstants.REJECT (API 30)

/**
 * `VibrateUtils.isLinearMotorVersion`: whether the OPLUS feature `oplus.software.vibrator_lmvibrator`
 * is present. The lock views and the numeric keyboard pick their constant by it. Read reflectively,
 * as the OPLUS framework classes only exist on ColorOS; anywhere else it is `false`.
 */
private val isLinearMotorVersion: Boolean by lazy {
    try {
        val managerClass = Class.forName("com.oplus.content.OplusFeatureConfigManager")
        val manager = managerClass.getMethod("getInstance").invoke(null)
        managerClass.getMethod("hasFeature", String::class.java)
            .invoke(manager, "oplus.software.vibrator_lmvibrator") as? Boolean ?: false
    } catch (_: Throwable) {
        false
    }
}

/**
 * The constants tried in order for [effect]: the OPLUS constant (or the official widget's choice
 * between two of them), any OPLUS retry the official widget makes, then the AOSP fallback.
 */
private fun hapticChain(effect: CouiHapticEffect): IntArray = when (effect) {
    CouiHapticEffect.Switch -> intArrayOf(OPLUS_GRANULAR_SHORT_VIBRATE, AOSP_CONTEXT_CLICK)

    // VibrationFeedbackHandler / COUITouchSearchView / COUINumberPicker retry 302 when 308 fails.
    CouiHapticEffect.Strength -> intArrayOf(OPLUS_GRANULAR_SHORT_VIBRATE_SYNC, OPLUS_GRANULAR_SHORT_VIBRATE, AOSP_CLOCK_TICK)

    CouiHapticEffect.ScrollEdge -> intArrayOf(OPLUS_EDGE_LIST_VIBRATE, AOSP_CLOCK_TICK)

    CouiHapticEffect.LongVibrate -> intArrayOf(OPLUS_LONG_VIBRATE, rejectFallback())

    // COUINumericKeyboard.setTouchFeedback: 302 with a linear motor, 301 without.
    CouiHapticEffect.KeyboardTouch -> intArrayOf(
        if (isLinearMotorVersion) OPLUS_GRANULAR_SHORT_VIBRATE else OPLUS_KEYBOARD_TOUCH_FEEDBACK,
        AOSP_KEYBOARD_TAP,
    )

    // COUISimpleLock.performFeedback / COUILockPatternView.performWrongModeFeedback: 304 with a
    // linear motor, 300 without.
    CouiHapticEffect.ShortTripleVibrate -> intArrayOf(
        if (isLinearMotorVersion) OPLUS_SHORT_TRIPLE_VIBRATE else OPLUS_LONG_VIBRATE,
        rejectFallback(),
    )

    CouiHapticEffect.SteppingShort -> intArrayOf(OPLUS_STEPPING_SHORT_VIBRATE, AOSP_CLOCK_TICK)

    CouiHapticEffect.EdgeShort -> intArrayOf(OPLUS_EDGE_SHORT_VIBRATE, AOSP_CONTEXT_CLICK)

    CouiHapticEffect.EdgeSqueeze -> intArrayOf(EDGE_SQUEEZE, AOSP_CONTEXT_CLICK)
}

private fun rejectFallback(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) AOSP_REJECT else AOSP_LONG_PRESS

/** Plays the first constant of [effect]'s chain that the device recognises. */
private fun View.performCouiHaptic(effect: CouiHapticEffect) {
    for (constant in hapticChain(effect)) {
        if (performHapticFeedback(constant)) return
    }
}

@Composable
actual fun rememberCouiHaptic(): (CouiHapticEffect) -> Unit {
    val view = LocalView.current
    return remember(view) {
        { effect: CouiHapticEffect -> view.performCouiHaptic(effect) }
    }
}
