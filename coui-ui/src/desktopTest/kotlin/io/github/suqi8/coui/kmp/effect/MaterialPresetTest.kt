// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import androidx.compose.animation.core.CubicBezierEasing
import io.github.suqi8.coui.kmp.anim.COUIEaseEasing
import io.github.suqi8.coui.kmp.anim.COUIInEaseEasing
import io.github.suqi8.coui.kmp.anim.COUIMoveEaseEasing
import io.github.suqi8.coui.kmp.anim.COUIOutEaseEasing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class MaterialPresetTest {

    @Test
    fun easingsUseTheOfficialControlPoints() {
        // COUI{,In,Move,Out}EaseInterpolator: PathInterpolator control points.
        assertEquals(CubicBezierEasing(0.33f, 0f, 0.67f, 1f), COUIEaseEasing)
        assertEquals(CubicBezierEasing(0f, 0f, 0.1f, 1f), COUIInEaseEasing)
        assertEquals(CubicBezierEasing(0.3f, 0f, 0.1f, 1f), COUIMoveEaseEasing)
        assertEquals(CubicBezierEasing(0.3f, 0f, 1f, 1f), COUIOutEaseEasing)
    }

    @Test
    fun topBarLevelsResolveTheOfficialStrokes() {
        // ToolbarMaterialEffectResolver: preset 1 -> CAPSULE_7 / CIRCLE_3, otherwise CAPSULE_1 / CIRCLE_1.
        assertSame(ShadowEdgePresets.Capsule7, TopBarMaterialLevel.Immersive.stroke(isCapsule = true, isDark = true))
        assertSame(ShadowEdgePresets.Circle3, TopBarMaterialLevel.Immersive.stroke(isCapsule = false, isDark = false))
        assertSame(ShadowEdgePresets.CapsuleLight, TopBarMaterialLevel.Default.stroke(isCapsule = true, isDark = false))
        assertSame(ShadowEdgePresets.CircleDark, TopBarMaterialLevel.DefaultBlur.stroke(isCapsule = false, isDark = true))
        // In a blurred window: preset 1 -> CAPSULE_9 / CIRCLE_5, otherwise CAPSULE_8 / CIRCLE_4.
        assertSame(ShadowEdgePresets.Capsule9, TopBarMaterialLevel.Immersive.strokeInWindow(isCapsule = true, isDark = false))
        assertSame(ShadowEdgePresets.Circle5, TopBarMaterialLevel.Immersive.strokeInWindow(isCapsule = false, isDark = true))
        assertSame(ShadowEdgePresets.Capsule8Dark, TopBarMaterialLevel.Default.strokeInWindow(isCapsule = true, isDark = true))
        assertSame(ShadowEdgePresets.Circle4Light, TopBarMaterialLevel.DefaultBlur.strokeInWindow(isCapsule = false, isDark = false))
    }

    @Test
    fun newStrokePresetsMatchCOUIMaterialStrokeEffect() {
        // FRAMEWORK_CAPSULE_PARAMS_7: (alpha 0.5, width 2.5dp, angle 0, fade in 0.05, out 1, scale 1).
        with(ShadowEdgePresets.Capsule7) {
            assertEquals(0.5f, edgeAlpha)
            assertEquals(2.5f, edgeWidth)
            assertEquals(0.05f, shadowFadeIn)
            assertEquals(1f, shadowFadeOut)
            assertEquals(1f, shadowFadeScale)
        }
        // FRAMEWORK_CIRCLE_PARAMS_3: (0.5, 2.0dp, 0, 0.01, 1, 1.15).
        with(ShadowEdgePresets.Circle3) {
            assertEquals(0.5f, edgeAlpha)
            assertEquals(2f, edgeWidth)
            assertEquals(0.01f, shadowFadeIn)
            assertEquals(1.15f, shadowFadeScale)
        }
        // FRAMEWORK_CAPSULE_PARAMS_11_LIGHT / _DARK: 0.45 / 0.1 alpha at 2.5dp.
        assertEquals(0.45f, DrawableMaterialStrokes.segmentButton(isDark = false).edgeAlpha)
        assertEquals(0.1f, DrawableMaterialStrokes.segmentButton(isDark = true).edgeAlpha)
        // FRAMEWORK_CAPSULE_PARAMS_12 / 13: 2.0dp / 2.3dp lines.
        assertEquals(2f, DrawableMaterialStrokes.navigationBar(isDark = false).edgeWidth)
        assertEquals(2.3f, DrawableMaterialStrokes.toolbar(isDark = true).edgeWidth)
    }
}
