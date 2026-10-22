// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Checks the COUI 17 functional button frame math decompiled from `COUISearchBar`'s fun-button
 * spring listener (`scale = 0.5 + 0.5p`, `alpha = remapAlphaProgress(p, 0.2)`, text alpha `p`).
 */
class SearchBarMotionTest {

    @Test
    fun scale_growsFromHalfToFull() {
        assertEquals(0.5f, searchBarActionScale(0f), 1e-6f)
        assertEquals(0.75f, searchBarActionScale(0.5f), 1e-6f)
        assertEquals(1f, searchBarActionScale(1f), 1e-6f)
    }

    @Test
    fun alpha_reachesFullAtOneFifth() {
        assertEquals(0f, searchBarActionAlpha(0f), 1e-6f)
        assertEquals(0.5f, searchBarActionAlpha(0.1f), 1e-6f)
        assertEquals(1f, searchBarActionAlpha(0.2f), 1e-6f)
        assertEquals(1f, searchBarActionAlpha(0.6f), 1e-6f)
    }

    @Test
    fun alpha_isClampedOnSpringOvershoot() {
        assertEquals(0f, searchBarActionAlpha(-0.03f), 1e-6f)
        assertEquals(1f, searchBarActionAlpha(1.04f), 1e-6f)
    }

    @Test
    fun labelAlpha_composesToProgress() {
        for (p in listOf(0.05f, 0.1f, 0.2f, 0.5f, 0.9f, 1f)) {
            assertEquals(p, searchBarActionAlpha(p) * searchBarActionTextAlpha(p), 1e-5f)
        }
        assertEquals(0f, searchBarActionTextAlpha(0f), 1e-6f)
    }
}
