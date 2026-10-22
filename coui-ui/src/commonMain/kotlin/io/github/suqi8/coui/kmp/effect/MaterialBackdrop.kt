// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

/**
 * A COUI 17 view backdrop blur, the parameters `COUIMaterialBlurEffect.setViewBlurEffect(view,
 * radius, c1, m1, c2, m2)` hands the Oplus framework: the content behind the view is blurred by
 * [blurRadiusPx], then [layer1] and [layer2] are blended over it in order with Android
 * `BlendModeColorFilter` semantics (the constant colour is the source, the blurred backdrop the
 * destination).
 *
 * @param blurRadiusPx The blur radius in raw pixels, as COUI passes it (no dp conversion). The
 *   framework turns it into a Gaussian sigma of `0.57735 * r + 0.5` (HWUI
 *   `Blur::convertRadiusToSigma`).
 * @param layer1 The first colour blended over the blurred backdrop.
 * @param mode1 How [layer1] is blended.
 * @param layer2 The second colour, blended over the result of [layer1].
 * @param mode2 How [layer2] is blended.
 */
@Immutable
data class MaterialBackdropSpec(
    val blurRadiusPx: Float,
    val layer1: Color,
    val mode1: BlendMode,
    val layer2: Color,
    val mode2: BlendMode,
)

/**
 * Draws a [MaterialBackdropSpec] behind a component, the COUI 17 view backdrop blur
 * (`OplusViewBackgroundRenderEffect`). coui-ui has no backdrop capture of its own, so an app that
 * records its content (for example with coui-blur `layerBackdrop` + `couiViewBlur`) provides one
 * through [LocalMaterialBackdrop].
 *
 * The backdrop a provider samples must not contain the component that draws it: a component inside
 * the recorded content would sample itself. Provide it around content that sits on top of the
 * recorded layer (a sibling), not inside it.
 */
@Stable
fun interface MaterialBackdropProvider {
    /**
     * Returns a modifier that paints [spec] behind the modified element, clipped to [shape], or
     * [Modifier] when the backdrop cannot be drawn.
     *
     * @param spec The blur radius and colour layers.
     * @param shape The outline the backdrop is clipped to, the component's own shape.
     */
    fun materialBackdrop(spec: MaterialBackdropSpec, shape: Shape): Modifier
}

/**
 * The [MaterialBackdropProvider] components such as [io.github.suqi8.coui.kmp.basic.Button] use for
 * their COUI 17 backdrop blur. Null (the default) means no backdrop is available, and the
 * components keep their opaque fill, which is COUI's own non-blur fallback.
 *
 * The coui-ui overlays (dialogs, bottom sheets, popups) provide null for their content: a button
 * on a panel would otherwise sample the page behind the panel. COUI's own dialog buttons never
 * use the backdrop either (`drawableMaterialEffect`).
 */
val LocalMaterialBackdrop = staticCompositionLocalOf<MaterialBackdropProvider?> { null }
