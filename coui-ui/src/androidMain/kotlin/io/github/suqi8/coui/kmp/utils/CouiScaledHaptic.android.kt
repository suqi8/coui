// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

@Composable
actual fun rememberCouiScaledHaptic(): (effect: CouiHapticEffect, intensity: Float) -> Unit {
    val view = LocalView.current
    val plain = rememberCouiHaptic()
    return remember(view, plain) {
        val composer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ScaledHapticComposer.create(view.context) else null
        { effect: CouiHapticEffect, intensity: Float ->
            if (composer == null || !view.isHapticFeedbackEnabled) {
                plain(effect)
            } else {
                composer.play(effect, intensity)
            }
        }
    }
}

/**
 * Plays a scaled composition primitive in place of COUI's RichTap transient. Only created when the
 * app may vibrate and the vibrator supports the primitives.
 */
@RequiresApi(Build.VERSION_CODES.R)
private class ScaledHapticComposer(
    private val context: Context,
    private val vibrator: Vibrator,
) {
    fun play(effect: CouiHapticEffect, intensity: Float) {
        // VibrateUtils only vibrates while Settings.System "haptic_feedback_enabled" is on.
        if (Settings.System.getInt(context.contentResolver, Settings.System.HAPTIC_FEEDBACK_ENABLED, 0) == 0) return
        val primitive = when (effect) {
            CouiHapticEffect.Switch -> VibrationEffect.Composition.PRIMITIVE_CLICK
            CouiHapticEffect.Strength, CouiHapticEffect.ScrollEdge -> VibrationEffect.Composition.PRIMITIVE_TICK
        }
        vibrator.vibrate(
            VibrationEffect.startComposition()
                .addPrimitive(primitive, intensity.coerceIn(0f, 1f))
                .compose(),
        )
    }

    companion object {
        fun create(context: Context): ScaledHapticComposer? {
            if (context.checkSelfPermission(Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) return null
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(VibratorManager::class.java)?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Vibrator::class.java)
            } ?: return null
            if (!vibrator.hasVibrator()) return null
            val supported = vibrator.areAllPrimitivesSupported(
                VibrationEffect.Composition.PRIMITIVE_CLICK,
                VibrationEffect.Composition.PRIMITIVE_TICK,
            )
            return if (supported) ScaledHapticComposer(context.applicationContext, vibrator) else null
        }
    }
}
