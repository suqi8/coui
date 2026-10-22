// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Checks the COUI 17 toolbar icon expansion math decompiled from `COUISearchBar`
 * (`showInToolBar`, the `mIconExpandAnimation` listener, `funBtnAnimateIconExpand`,
 * `setToolBarScale`).
 */
class SearchBarIconExpandTest {

    @Test
    fun capsuleWidth_growsFromCircleToFullWidth() {
        assertEquals(40f, iconExpandCapsuleWidth(height = 40f, fullWidth = 300f, progress = 0f), 1e-4f)
        assertEquals(170f, iconExpandCapsuleWidth(height = 40f, fullWidth = 300f, progress = 0.5f), 1e-4f)
        assertEquals(300f, iconExpandCapsuleWidth(height = 40f, fullWidth = 300f, progress = 1f), 1e-4f)
        // Never narrower than the circle, even on a spring undershoot.
        assertEquals(40f, iconExpandCapsuleWidth(height = 40f, fullWidth = 300f, progress = -0.1f), 1e-4f)
    }

    @Test
    fun capsuleBlur_peaksHalfWay() {
        assertEquals(0f, iconExpandCapsuleBlurPx(0f), 1e-4f)
        assertEquals(30f, iconExpandCapsuleBlurPx(0.5f), 1e-4f)
        assertEquals(15f, iconExpandCapsuleBlurPx(0.25f), 1e-4f)
        assertEquals(0f, iconExpandCapsuleBlurPx(1f), 1e-4f)
    }

    @Test
    fun action_staysHiddenUpToFivePercent() {
        assertEquals(0f, iconExpandActionScale(0.05f), 1e-6f)
        assertEquals(0f, iconExpandActionAlpha(0.05f), 1e-6f)
        assertEquals(0.6f + 0.4f * 0.06f, iconExpandActionScale(0.06f), 1e-6f)
        assertEquals(0.06f, iconExpandActionAlpha(0.06f), 1e-6f)
        assertEquals(1f, iconExpandActionScale(1f), 1e-6f)
        assertEquals(30f * 0.5f, iconExpandActionBlurPx(0.5f), 1e-4f)
    }

    @Test
    fun toolbarChildren_fadeAndShrinkToTheExpandEndScale() {
        val collapsed = ToolbarSearchState()
        assertEquals(1f, collapsed.toolbarAlpha(), 1e-6f)
        assertEquals(1f, collapsed.toolbarScale(), 1e-6f)
        val expanded = ToolbarSearchState(initiallyExpanded = true)
        assertEquals(0f, expanded.toolbarAlpha(), 1e-6f)
        assertEquals(0.8f, expanded.toolbarScale(), 1e-6f)
    }

    @Test
    fun bar_isShownOnlyWhileExpandedOrFolding() {
        val state = ToolbarSearchState()
        assertFalse(state.barVisible)
        state.expand()
        assertTrue(state.barVisible)
        val expanded = ToolbarSearchState(initiallyExpanded = true)
        expanded.collapse()
        // Still at progress 1: the fold-back has not run yet.
        assertTrue(expanded.barVisible)
    }
}
