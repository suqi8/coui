// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * The COUI font scale caps (`COUIChangeTextUtil`): a text that may only grow up to a level keeps the
 * system font scale below the cap and is clamped above it.
 *
 * The levels are [G1] .. [G5]; their caps are [ScaleLevels] (0.9 / 1.0 / 1.15 / 1.35 / 1.6). [G1] means
 * "no cap", [G2] and [G3] snap to the steps below them, and [G4] / [G5] clamp.
 */
object COUIFontScale {
    /** COUIChangeTextUtil.G1: the font scale is not capped. */
    const val G1 = 1

    /** COUIChangeTextUtil.G2: 1.0 below a font scale of 1.15, else 1.15. */
    const val G2 = 2

    /** COUIChangeTextUtil.G3: 1.0 below 1.15, 1.15 below 1.6, else 1.35. */
    const val G3 = 3

    /** COUIChangeTextUtil.G4: the font scale is capped at 1.35. */
    const val G4 = 4

    /** COUIChangeTextUtil.G5: the font scale is capped at 1.6. */
    const val G5 = 5

    /** COUIChangeTextUtil.SCALE_LEVEL (H1 .. H5): the caps of [G1] .. [G5]. */
    val ScaleLevels: List<Float> = listOf(0.9f, 1.0f, 1.15f, 1.35f, 1.6f)

    /**
     * The font scale a text of [level] uses when the system font scale is [fontScale]
     * (`COUIChangeTextUtil.getSuitableFontSize` divided by the unscaled size).
     */
    fun suitableFontScale(fontScale: Float, level: Int): Float {
        if (level < G2) return fontScale
        val capped = level.coerceAtMost(G5)
        return when (capped) {
            G2 -> if (fontScale < 1.15f) 1.0f else 1.15f

            G3 -> when {
                fontScale < 1.15f -> 1.0f
                fontScale < 1.6f -> 1.15f
                else -> 1.35f
            }

            else -> fontScale.coerceAtMost(ScaleLevels[capped - 1])
        }
    }

    /**
     * Converts the `sp` size [fontSize] so that, once Compose applies the current font scale, it renders
     * at the COUI capped scale of [level] (`COUIChangeTextUtil.adaptFontSize`). Non-`sp` sizes are
     * returned unchanged.
     */
    @Composable
    @ReadOnlyComposable
    fun cappedFontSize(fontSize: TextUnit, level: Int): TextUnit {
        if (!fontSize.isSp) return fontSize
        val fontScale = LocalDensity.current.fontScale
        if (fontScale <= 0f) return fontSize
        return (fontSize.value * suitableFontScale(fontScale, level) / fontScale).sp
    }
}
