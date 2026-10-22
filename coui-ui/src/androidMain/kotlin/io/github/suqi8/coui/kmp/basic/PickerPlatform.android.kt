// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import android.content.Context
import android.graphics.Paint
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Handler
import android.os.HandlerThread
import android.provider.Settings
import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.suqi8.coui.kmp.ui.R
import java.util.Calendar
import java.util.Locale

@Composable
internal actual fun rememberNumberPickerClickSound(): () -> Unit {
    val context = LocalContext.current.applicationContext
    return remember(context) {
        NumberPickerClickSound.prepare(context)
        val play: () -> Unit = { NumberPickerClickSound.play() }
        play
    }
}

/**
 * COUISoundLoadUtil + COUINumberPicker's "touchEffect" handler thread: one SoundPool on a
 * background thread, the sound loaded once, and a pending play replaced by the newest one.
 */
private object NumberPickerClickSound {
    // AudioAttributes.FLAG_BYPASS_MUTE (hidden), which COUISoundLoadUtil sets as the literal 128.
    private const val FLAG_BYPASS_MUTE = 128

    // COUINumberPicker starts its NumberPickerHandlerThread at priority -16.
    private const val THREAD_PRIORITY = -16

    private var appContext: Context? = null
    private var handler: Handler? = null
    private var soundPool: SoundPool? = null
    private var soundId = 0

    private val playRunnable = Runnable {
        val context = appContext ?: return@Runnable
        val pool = soundPool ?: return@Runnable
        // COUISoundLoadUtil.querySoundEffectsEnabled: the system "touch sounds" switch.
        if (Settings.System.getInt(context.contentResolver, Settings.System.SOUND_EFFECTS_ENABLED, 0) != 0 && soundId != 0) {
            pool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    @Synchronized
    fun prepare(context: Context) {
        if (handler != null) return
        appContext = context
        val thread = HandlerThread("COUINumberPickerSound", THREAD_PRIORITY).apply { start() }
        val threadHandler = Handler(thread.looper)
        threadHandler.post {
            val pool = SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setFlags(FLAG_BYPASS_MUTE)
                        .setLegacyStreamType(AudioManager.STREAM_SYSTEM)
                        .build(),
                )
                .build()
            soundId = pool.load(context, R.raw.coui_numberpicker_click, 0)
            soundPool = pool
        }
        handler = threadHandler
    }

    fun play() {
        val threadHandler = handler ?: return
        threadHandler.removeCallbacks(playRunnable)
        threadHandler.post(playRunnable)
    }
}

// COUINumberPicker keeps its 750 / 5000 px/s constants in raw pixels.
internal actual fun numberPickerVelocityScale(density: Float): Float = 1f

// COUINumberPicker measures the metrics on a fresh Paint (default typeface) before it switches
// the paint to sans-serif-medium.
internal actual fun numberPickerFontTopBottom(textSizePx: Float): FloatArray {
    val metrics = Paint().apply { textSize = textSizePx }.fontMetrics
    return floatArrayOf(metrics.top, metrics.bottom)
}

internal actual fun platformDatePattern(): String = DateFormat.getBestDateTimePattern(Locale.getDefault(), "yyyyMMMMdd")

internal actual fun platformToday(): DateValue {
    val calendar = Calendar.getInstance()
    return DateValue(
        year = calendar.get(Calendar.YEAR),
        month = calendar.get(Calendar.MONTH) + 1,
        day = calendar.get(Calendar.DAY_OF_MONTH),
    )
}
