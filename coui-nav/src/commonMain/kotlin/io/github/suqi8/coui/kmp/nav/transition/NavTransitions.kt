// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.nav.transition

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.util.fastRoundToInt

/**
 * Preset library of [NavTransition]s, all implemented as pure functions of the single float
 * driver [NavTransitionScope.relativeDepth] (d = animatedTop - entryIndex).
 *
 * Every preset maps `d` to spatial graphics-layer properties only (translationX / scaleX /
 * scaleY / alpha / clip / shape) and reads `d` deferred inside the [navGraphicsTransition]
 * block, so animations cost zero recomposition. The same `d -> visual` mapping is reused by
 * both the spring-convergence path and the interactive gesture path (predictive back / edge
 * swipe), so no preset needs a separate predictive-back animation.
 *
 * Depth segments handled by each preset:
 * - `d <= 0`  : this entry is entering/leaving the top (use its own transition).
 * - `0 < d <= opaqueDepth` : this entry is covered by an upper entry (covered treatment).
 *
 * Slide-style presets keep `translation` linear in `d` (a finger-driven `snapTo` follows the finger
 * 1:1, §7.1) but snap the rendered entering offset to whole device pixels so a corner-clipped page's
 * anti-aliased edge does not shimmer under fractional-offset compositing. Parallax / dim easing may be
 * non-linear since it is identical in both drive modes.
 */
object NavTransitions {

    /**
     * Duration of the COUI 17 page switch. Every part of `Animation.COUI.Activity` (the window
     * animation style of `Theme.COUI`: `coui_open_slide_enter` / `_exit`, `coui_close_slide_enter` /
     * `_exit`) and of its ColorOS shell twin (`oplus_open_slide_*` / `oplus_close_slide_*`) runs
     * 350ms.
     */
    const val COUI_PAGE_DURATION_MILLIS: Int = 350

    /**
     * Share of the width the covered page shifts toward the leading edge during a COUI page
     * switch: `coui_open_slide_exit` / `coui_close_slide_enter` translate it by -30%
     * (`toXDelta -29.999996%`, mirrored under RTL).
     */
    const val COUI_COVERED_SHIFT_FRACTION: Float = 0.3f

    /**
     * Share of the width the revealed page shifts while a back gesture drives: the ColorOS
     * predictive back places it at `-(width - x) / 4` for a closing page at `x`
     * (`CrossBackAnimationExt.applyTransformForOpeningTarget`).
     */
    const val COUI_GESTURE_COVERED_SHIFT_FRACTION: Float = 0.25f

    /**
     * Curve of a COUI page switch: `coui_open_slide_enter_translate_interpolator`,
     * cubic-bezier(0.3, 0.1, 0.3, 1), the curve of the page sliding in. It drives the whole switch:
     * the other official translate curves (covered page 0.3/0.15/0.3/1, page coming back
     * 0.3/0.26/0.4/1, page leaving 0.25/0.1/0.3/1) stay within about 0.04 of it at every instant, so one
     * shared driver curve reproduces all four.
     */
    val COUIPageEasing: Easing = CubicBezierEasing(0.3f, 0.1f, 0.3f, 1f)

    /**
     * Settle physics of [COUIDefault]:
     * - **programmatic** push/pop: [COUI_PAGE_DURATION_MILLIS] on [COUIPageEasing], the COUI page
     *   switch;
     * - **commit** of a back gesture: a critically damped spring at stiffness 400, and **cancel** at
     *   450, as the ColorOS predictive back settles a released page
     *   (`CrossBackAnimationExt.DROP_STIFFNESS_VALUE` / `DRAG_CANCEL_STIFFNESS_VALUE`, damping ratio 1).
     */
    val COUIPageMotion: NavMotion = NavMotion(
        commit = NavSettleSpec.Spring(dampingRatio = 1f, stiffness = 400f),
        cancel = NavSettleSpec.Spring(dampingRatio = 1f, stiffness = 450f),
        programmatic = NavSettleSpec.Tween(durationMillis = COUI_PAGE_DURATION_MILLIS, easing = COUIPageEasing),
    )

    /**
     * Default preset: the COUI 17 page switch.
     *
     * - Push / pop (`Animation.COUI.Activity`): the entering page slides in full-width from the
     *   trailing edge (d: -1 -> 0, translationX: +width -> 0) while the covered page shifts
     *   [COUI_COVERED_SHIFT_FRACTION] (30%) of the width toward the leading edge (d: 0 -> 1); a pop
     *   moves both pages back along the same geometry. [COUIPageMotion] times both: 350ms on the
     *   COUI curve, which the official pop curves (`coui_close_slide_*`) follow within about 0.04.
     * - Back gesture (ColorOS predictive back, `CrossBackAnimationExt`): the closing page follows the
     *   finger 1:1 and the revealed page trails it by [COUI_GESTURE_COVERED_SHIFT_FRACTION] (25%);
     *   the release settles on the critically damped 400 (commit) / 450 (cancel) springs.
     *
     * The official covered page fades to 0.5 alpha over a black backdrop (`coui_open_slide_exit`,
     * `oplus_close_slide_exit` `background #ff000000`); here that darkening is the default
     * [io.github.suqi8.coui.kmp.nav.core.NavDisplayEffects.dimAmount] scrim (0.5 black), linear in
     * depth exactly as the ColorOS gesture scrim (`0.5 - x / width * 0.5`). The rounded corners of
     * the moving page (`setHasRoundedCorners` on the open-enter / close-exit animations) are the
     * effects layer's corner clip, which follows the screen corner by default. Geometry is linear in
     * `d` for 1:1 finger tracking (entering offset pixel-snapped); RTL mirrored.
     *
     * Built with [navDirectionalTransition] (push / pop share one branch, the gesture uses the
     * other), so its static contracts follow that helper: `opaqueDepth` 1, no swipe-to-dismiss.
     * The branches differ only in the covered page's shift, so a back gesture that grabs a page
     * switch mid-flight moves the covered page from the 30% to the 25% track at the grab (the
     * helper's documented trade-off); a gesture from rest switches while that page is fully
     * covered, and the gesture branch stays in charge until its settle ends.
     */
    val COUIDefault: NavTransition = navDirectionalTransition(
        push = couiPageSlide(coveredShift = COUI_COVERED_SHIFT_FRACTION, motion = COUIPageMotion),
        predictivePop = couiPageSlide(coveredShift = COUI_GESTURE_COVERED_SHIFT_FRACTION, motion = COUIPageMotion),
    )

    /**
     * Bottom-up modal: the entering page slides up from the bottom edge (d: -1 -> 0,
     * translationY: +height -> 0). The layer below is kept fully visible and untouched, so
     * [opaqueDepth] is raised to 2f to stop the host from culling it. Like every preset, swipe-to-
     * dismiss is opt-in: pass `entry(swipeDismiss = NavSwipeDirection.TopToBottom)` to let a downward
     * swipe dismiss it (the translation is linear, so the gesture follows the finger 1:1).
     */
    val Modal: NavTransition = navGraphicsTransition(opaqueDepth = 2f) { scope ->
        val d = scope.relativeDepth
        if (d <= 0f) {
            val height = scope.layoutSize.height.toFloat()
            translationY = (-d).coerceIn(0f, 1f) * height
        }
        // d > 0: covered layer stays fully visible and untransformed (kept by opaqueDepth = 2f).
    }

    /**
     * Instant transition: no visual change, the top entry simply swaps in/out. Lower layers are
     * fully occluded (opaqueDepth = 1f) so only the top + one covered layer are kept.
     */
    val None: NavTransition = navGraphicsTransition(opaqueDepth = 1f, dismissDirection = NavSwipeDirection.None) {
        // Intentionally empty: identity transform, instant swap. No motion means no swipe to follow,
        // so the interactive dismiss gesture is disabled; pop via the back button / system back.
    }
}

/**
 * One branch of the COUI page switch: the entering/leaving page slides the full width from the
 * trailing edge, the covered page shifts [coveredShift] of the width toward the leading edge.
 * Both are linear in depth, so a finger-driven depth moves the closing page 1:1.
 */
private fun couiPageSlide(coveredShift: Float, motion: NavMotion): NavTransition = navGraphicsTransition(opaqueDepth = 1f, motion = motion) { scope ->
    translationX = couiPageTranslationX(
        relativeDepth = scope.relativeDepth,
        width = scope.layoutSize.width.toFloat(),
        rtl = scope.layoutDirection == LayoutDirection.Rtl,
        coveredShift = coveredShift,
    )
    // No alpha on the covered page: the official 0.5 fade over a black backdrop is the effects
    // layer's black dim scrim (dimAmount 0.5), drawn under the top page over the whole revealed area.
}

/**
 * Horizontal offset of a layer at [relativeDepth] in the COUI page switch:
 * - `d <= 0` (entering / leaving the top): a full-width slide from the trailing edge, snapped to
 *   whole device pixels — the corner-clipped page's anti-aliased edge otherwise shimmers as a thin
 *   line at a fractional X over the scrim while moving (<= 0.5px, still 1:1 with the finger);
 * - `d > 0` (covered): [coveredShift] of the width toward the leading edge, linear in depth.
 *
 * Mirrored for RTL ([rtl]).
 */
internal fun couiPageTranslationX(relativeDepth: Float, width: Float, rtl: Boolean, coveredShift: Float): Float = if (relativeDepth <= 0f) {
    ((if (rtl) -1f else 1f) * (-relativeDepth).coerceIn(0f, 1f) * width).fastRoundToInt().toFloat()
} else {
    (if (rtl) 1f else -1f) * coverProgress(relativeDepth) * width * coveredShift
}

/**
 * Linear progress of the "covered" segment: maps d in [0, 1] -> [0, 1] where 0 means fully at
 * the top (d = 0) and 1 means fully covered by the layer above (d = 1).
 */
private fun coverProgress(d: Float): Float = d.coerceIn(0f, 1f)
