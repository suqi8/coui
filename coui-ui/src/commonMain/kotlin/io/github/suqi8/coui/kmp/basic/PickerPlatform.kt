// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.runtime.Composable

/**
 * Returns a callback that plays the COUI picker tick (`coui_numberpicker_click.ogg`) once.
 *
 * Android plays it through a SoundPool on a background thread, only while the system touch
 * sounds setting is on (COUISoundLoadUtil); every other platform is silent.
 */
@Composable
internal expect fun rememberNumberPickerClickSound(): () -> Unit

/**
 * Scale applied to COUINumberPicker's raw px/s velocity constants (750 / 5000 px/s).
 *
 * Android uses them unscaled, exactly like COUI. Other platforms treat them as px at the
 * reference ColorOS phone density ([NumberPickerReferenceDensity]) and scale them by the actual
 * density, so a flick feels the same as on that phone.
 */
internal expect fun numberPickerVelocityScale(density: Float): Float

/** Density of the reference ColorOS phone (3.5x) the non-Android velocity scale is based on. */
internal val NumberPickerReferenceDensity = 3.5f

/**
 * The default typeface's `FontMetrics.top` and `bottom` (px, top negative) at [textSizePx].
 *
 * COUINumberPicker reads these from a default-typeface Paint at the unselected text size and
 * centers every row on them, so the rows sit about 0.8dp higher than an ascent/descent center.
 */
internal expect fun numberPickerFontTopBottom(textSizePx: Float): FloatArray

/**
 * The locale's best "yyyyMMMMdd" date pattern (COUIDatePicker.reorderSpinners), used to order the
 * year, month and day wheels. Empty when the platform cannot tell.
 */
internal expect fun platformDatePattern(): String

/** Today's date in the system time zone. */
internal expect fun platformToday(): DateValue
