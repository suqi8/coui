// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import kotlin.test.Test
import kotlin.test.assertEquals

class BadgeCountFadeTest {

    @Test
    fun countFadeFollowsTheAccelerateDecelerateCurve() {
        // AccelerateDecelerateInterpolator: cos((t + 1) * PI) / 2 + 0.5.
        assertEquals(0f, CountFadeEasing.transform(0f), 1e-6f)
        assertEquals(0.14645f, CountFadeEasing.transform(0.25f), 1e-4f)
        assertEquals(0.5f, CountFadeEasing.transform(0.5f), 1e-6f)
        assertEquals(0.85355f, CountFadeEasing.transform(0.75f), 1e-4f)
        assertEquals(1f, CountFadeEasing.transform(1f), 1e-6f)
    }
}
