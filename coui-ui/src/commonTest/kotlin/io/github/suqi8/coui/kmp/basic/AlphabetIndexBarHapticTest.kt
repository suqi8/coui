// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import kotlin.test.Test
import kotlin.test.assertEquals

class AlphabetIndexBarHapticTest {

    @Test
    fun aStillFingerTicksAtHalfStrength() {
        assertEquals(0.5f, indexBarHapticIntensity(0f), 1e-6f)
    }

    @Test
    fun theTickGrowsWithTheDragSpeedUpToFullStrength() {
        // VibrateUtils.getVibratorValueWithLimit(4000, 8000, 50, 100) = 75.
        assertEquals(0.75f, indexBarHapticIntensity(4000f), 1e-6f)
        assertEquals(1f, indexBarHapticIntensity(8000f), 1e-6f)
        assertEquals(1f, indexBarHapticIntensity(20000f), 1e-6f)
    }

    @Test
    fun theStrengthIsTruncatedToWholePercentLikeTheOriginal() {
        // 1000 / 8000 * 50 + 50 = 56.25, cast to int.
        assertEquals(0.56f, indexBarHapticIntensity(1000f), 1e-6f)
    }
}
