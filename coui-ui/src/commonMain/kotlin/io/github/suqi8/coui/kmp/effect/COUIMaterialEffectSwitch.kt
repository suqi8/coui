// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * The app-wide switch for the COUI 17 material effects: the background blur and colour filters,
 * the edge lighting and inner shadow, and the touch spotlight that the material components draw.
 *
 * COUI turns these effects on and off for every widget at once through `COUIMaterialStateManager`,
 * which follows the system material switches (`system_material_stroke_enable`,
 * `system_material_blur_enable`), the power-saving modes (`low_power`,
 * `super_powersave_mode_state`) and the animation level (`persist.sys.oplus.anim_level`), and
 * notifies the widgets through `OnMaterialStateChangeListener`. Those are ColorOS system settings;
 * this library leaves the decision to the app, which can provide `false` here, for example while
 * its own reduced-effects or battery-saver setting is on.
 *
 * The material components read it as the default of their material flag, so a flag passed
 * explicitly to a component still wins. Defaults to `true`, the state of a stock ColorOS 17
 * device.
 */
val LocalCOUIMaterialEffectEnabled: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf { true }
