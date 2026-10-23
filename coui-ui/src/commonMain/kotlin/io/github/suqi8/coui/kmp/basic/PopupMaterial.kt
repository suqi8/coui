// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import io.github.suqi8.coui.kmp.effect.MaterialStrokePresets
import io.github.suqi8.coui.kmp.effect.SpotLightShader
import io.github.suqi8.coui.kmp.effect.SpotLightState
import io.github.suqi8.coui.kmp.effect.SpotLightType
import io.github.suqi8.coui.kmp.effect.materialStroke
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asBrush
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch

/** Creates the touch spotlight state of a popup surface (`COUISpotLightEffectDrawable`). */
@Composable
internal fun rememberPopupSpotLight(): SpotLightState = remember { SpotLightState() }

/**
 * Feeds touches on this node to [spotLight] (`RoundFrameLayout.dispatchTouchEvent` →
 * `COUISpotLightEffectDrawable.onTouchEvent`): glow at the DOWN point, follow the finger, then
 * extinguish. Observes in the initial pass and consumes nothing.
 *
 * @param spotLight The spotlight to drive.
 * @param enabled Whether touches drive it.
 * @param suppressed Read on each touch; while `true` the spotlight stays dark.
 */
@Composable
internal fun Modifier.popupSpotLightInput(
    spotLight: SpotLightState,
    enabled: Boolean,
    suppressed: () -> Boolean = { false },
): Modifier {
    if (!enabled || !isRuntimeShaderSupported()) return this
    val scope = rememberCoroutineScope()
    val currentSuppressed by rememberUpdatedState(suppressed)
    return pointerInput(spotLight) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            scope.launch { if (!currentSuppressed()) spotLight.glow(down.position) }
            while (true) {
                val change = awaitPointerEvent(PointerEventPass.Initial).changes
                    .firstOrNull { it.id == down.id } ?: break
                if (change.changedToUp() || !change.pressed) break
                scope.launch { if (!currentSuppressed()) spotLight.follow(change.position) }
            }
            scope.launch { spotLight.extinguish() }
        }
    }
}

/**
 * The material effects a COUI 17 popup surface draws once its window background is blurred
 * (`RoundFrameLayout.initMaterialEffects`): the `COUIShadowEdgeDrawable` stroke
 * ([MaterialStrokePresets.PopupLight] / [MaterialStrokePresets.PopupDark], weight 2.5) and the
 * `TYPE_TRANSLUCENT_LARGE_1` touch spotlight, both under the rows. Needs runtime shaders; elsewhere
 * nothing is drawn, like COUI on a device without the effects.
 *
 * @param enabled Whether the effects are drawn.
 * @param spotLightSuppressed Read on each touch; while it returns `true` the spotlight stays dark
 *   (COUI turns it off on the main menu while a sub menu is open).
 * @param spotLight The spotlight state to draw.
 * @param trackTouches Whether this node feeds its own touches to [spotLight]; turn it off when an
 *   ancestor does it with [popupSpotLightInput].
 */
@Composable
internal fun Modifier.popupMaterialEffect(
    enabled: Boolean,
    spotLightSuppressed: () -> Boolean = { false },
    spotLight: SpotLightState = rememberPopupSpotLight(),
    trackTouches: Boolean = true,
): Modifier {
    if (!enabled) return this
    val shader = remember { if (isRuntimeShaderSupported()) RuntimeShader(SpotLightShader.Source) else null }
    val isDark = COUITheme.colorScheme.background.luminance() < 0.5f
    val type = SpotLightType.TranslucentLarge1
    return this
        .materialStroke(
            light = MaterialStrokePresets.PopupLight,
            dark = MaterialStrokePresets.PopupDark,
            topCornerRadius = PopupCornerRadius,
            bottomCornerRadius = PopupCornerRadius,
            weight = PopupCornerWeight,
        )
        .then(if (trackTouches) Modifier.popupSpotLightInput(spotLight, enabled = true, suppressed = spotLightSuppressed) else Modifier)
        .then(
            if (shader == null) {
                Modifier
            } else {
                Modifier.drawBehind {
                    val glow = spotLight.progress.value
                    if (glow <= 0f) return@drawBehind
                    val light = spotLight.position.value
                    SpotLightShader.setUniforms(
                        shader = shader,
                        lightX = light.x,
                        lightY = light.y,
                        radius = SpotLightShader.radius(glow, spotLight.extinguishing, type.endRadius),
                        intensity = type.endIntensity(isDark) * glow,
                    )
                    drawRect(brush = shader.asBrush(), blendMode = type.blendMode(isDark))
                }
            },
        )
}
