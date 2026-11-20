// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The enter pivot a COUI 17 menu stretches about under a finger (`PopupMenuDomain`
 * `getMainMenuEnterPivotX` / `getMainMenuEnterPivotY`) and the colour curve of the shared press
 * mask (`StateEffectAnimator` with `ArgbEvaluator`).
 */
class PopupMotionGeometryTest {

    private val menu = IntSize(width = 160, height = 200)

    @Test
    fun aMenuBelowItsAnchorStretchesFromTheAnchorOnItsTopEdge() {
        val anchor = IntRect(left = 100, top = 0, right = 140, bottom = 40)
        val pivot = popupEnterPivot(anchor, IntOffset(x = 60, y = 48), menu)
        assertEquals(60f / 160f, pivot.pivotFractionX, 1e-4f)
        assertEquals(0f, pivot.pivotFractionY)
    }

    @Test
    fun aMenuAboveItsAnchorStretchesFromItsBottomEdge() {
        val anchor = IntRect(left = 100, top = 600, right = 140, bottom = 640)
        val pivot = popupEnterPivot(anchor, IntOffset(x = 60, y = 392), menu)
        assertEquals(60f / 160f, pivot.pivotFractionX, 1e-4f)
        assertEquals(1f, pivot.pivotFractionY)
    }

    @Test
    fun anAnchorBesideTheMenuClampsThePivotToTheNearEdge() {
        val right = popupEnterPivot(IntRect(left = 400, top = 0, right = 440, bottom = 40), IntOffset(x = 60, y = 48), menu)
        val left = popupEnterPivot(IntRect(left = 0, top = 0, right = 20, bottom = 40), IntOffset(x = 60, y = 48), menu)
        assertEquals(1f, right.pivotFractionX)
        assertEquals(0f, left.pivotFractionX)
    }

    @Test
    fun aTouchPointAnchorPivotsOnThePoint() {
        val point = IntRect(left = 140, top = 30, right = 140, bottom = 30)
        val pivot = popupEnterPivot(point, IntOffset(x = 60, y = 30), menu)
        assertEquals(80f / 160f, pivot.pivotFractionX, 1e-4f)
        assertEquals(0f, pivot.pivotFractionY)
    }

    @Test
    fun theDarkMaskRisesFromGreyWhileItFadesIn() {
        val white = Color(0x26FFFFFF)
        val half = popupMaskColor(white, 0.5f)
        assertEquals(0.5f.pow(1f / 2.2f), half.red, 0.01f)
        assertEquals(half.red, half.blue, 0.01f)
        assertEquals(0.5f * white.alpha, half.alpha, 0.01f)
        val full = popupMaskColor(white, 1f)
        assertEquals(1f, full.red, 0.01f)
        assertEquals(white.alpha, full.alpha, 0.01f)
        assertEquals(0f, popupMaskColor(white, 0f).alpha)
    }

    @Test
    fun theLightMaskStaysBlackAndFadesLinearly() {
        val black = Color(0x14000000)
        val half = popupMaskColor(black, 0.5f)
        assertEquals(0f, half.red)
        assertEquals(0.5f * black.alpha, half.alpha, 0.01f)
    }
}
