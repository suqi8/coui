// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import androidx.compose.ui.graphics.BlendMode
import kotlin.test.Test
import kotlin.test.assertEquals

class SpotLightTypeTest {

    @Test
    fun opaqueMedium1MatchesTheSeekBarPreset() {
        // COUISpotLightEffect TYPE_OPAQUE_MEDIUM_1_{LIGHT,DARK}: OVERLAY, 0.6 light / 0.8 dark.
        assertEquals(0.6f, SpotLightType.OpaqueMedium1.endIntensity(isDark = false))
        assertEquals(0.8f, SpotLightType.OpaqueMedium1.endIntensity(isDark = true))
        assertEquals(BlendMode.Overlay, SpotLightType.OpaqueMedium1.blendMode(isDark = false))
        assertEquals(BlendMode.Overlay, SpotLightType.OpaqueMedium1.blendMode(isDark = true))
    }

    @Test
    fun existingPresetsKeepTheirValues() {
        assertEquals(1f, SpotLightType.OpaqueSmall2.endIntensity(isDark = false))
        assertEquals(0.15f, SpotLightType.OpaqueSmall2.endIntensity(isDark = true))
        assertEquals(BlendMode.Plus, SpotLightType.OpaqueSmall2.blendMode(isDark = true))
        assertEquals(BlendMode.Plus, SpotLightType.OpaqueMedium2.blendMode(isDark = true))
        assertEquals(0.5f, SpotLightType.TransparentMedium1.endIntensity(isDark = false))
        assertEquals(0.3f, SpotLightType.TransparentMedium1.endIntensity(isDark = true))
        assertEquals(BlendMode.Overlay, SpotLightType.TransparentMedium1.blendMode(isDark = true))
    }

    @Test
    fun theButtonTypesUseTheirPresets() {
        assertEquals(SpotLightType.OpaqueMedium1, ContentButtonMaterialType.Primary.spotLight)
        assertEquals(SpotLightType.OpaqueMedium2, ContentButtonMaterialType.Secondary.spotLight)
        assertEquals(SpotLightType.TransparentMedium1, ContentButtonMaterialType.Transparent.spotLight)
    }
}
