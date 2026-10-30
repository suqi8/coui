// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBackdropSpec
import io.github.suqi8.coui.kmp.effect.TouchMotion
import io.github.suqi8.coui.kmp.squircle.SquircleShape
import io.github.suqi8.coui.kmp.squircle.squircleBackground
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.graphicsLayerWithOverflow
import io.github.suqi8.coui.kmp.utils.layerOverflow

/**
 * A [FloatingToolbar] that renders its content in a Card, arranged either horizontally or vertically.
 * The actual placement on screen is handled by the parent, typically Scaffold.
 *
 * @param modifier The modifier to be applied to the [FloatingToolbar].
 * @param visible Whether the toolbar is shown. Changes animate with the COUI 17
 *   `COUIBottomFloatingToolbar` menu view springs: it scales in about its centre (response 0.5,
 *   bounce 0.4) and scales out to nothing (response 0.25, bounce 0), shadow included. A hidden
 *   toolbar keeps its place in the layout but takes no touches and has no semantics.
 * @param materialEffect Whether the COUI 17 bottom-bar material is used. ColorOS 17 draws the
 *   toolbar with `TYPE_FRAMEWORK_BOTTOM_BAR` (a 25px backdrop blur, see
 *   [FloatingToolbarDefaults.backdropSpec]) over a transparent background
 *   (`COUIBottomFloatingToolbarMenuView.updateBlurEffect`). coui-ui has no backdrop capture of its
 *   own: the blur is drawn only when a [LocalMaterialBackdrop] provider is present, and then it
 *   replaces [color]. Without one the toolbar keeps [color], COUI's non-blur fallback.
 * @param color Background color of the [FloatingToolbar].
 * @param cornerRadius Corner radius of the [FloatingToolbar].
 * @param outSidePadding Padding outside the [FloatingToolbar].
 * @param shadowElevation The shadow elevation of the [FloatingToolbar].
 * @param showDivider Whether to show the divider line around the [FloatingToolbar].
 * @param content The [Composable] content of the [FloatingToolbar].
 */
@Composable
fun FloatingToolbar(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    materialEffect: Boolean = true,
    color: Color = FloatingToolbarDefaults.defaultColor(),
    cornerRadius: Dp = FloatingToolbarDefaults.CornerRadius,
    outSidePadding: PaddingValues = FloatingToolbarDefaults.OutSidePadding,
    shadowElevation: Dp = 4.dp,
    showDivider: Boolean = false,
    content: @Composable () -> Unit,
) {
    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    val dividerColor = COUITheme.colorScheme.dividerLine

    // MenuViewAnimatorImpl.updateAttachState: the attach / detach scale spring, restarted from the
    // current scale, about the view centre. There is no fade.
    val scale = remember { Animatable(if (visible) 1f else 0f) }
    LaunchedEffect(visible) {
        scale.animateTo(
            targetValue = if (visible) 1f else 0f,
            animationSpec = if (visible) {
                TouchMotion.spring(response = 0.5f, bounce = 0.4f, visibilityThreshold = 0.001f)
            } else {
                TouchMotion.spring(response = 0.25f, bounce = 0f, visibilityThreshold = 0.001f)
            },
        )
    }

    val dividerMod = if (showDivider) {
        Modifier
            .squircleBackground(color = dividerColor, cornerRadius = cornerRadius, weight = COUIRoundCorner.UnspecifiedWeight)
            .padding(0.75.dp)
    } else {
        Modifier
    }
    val hasShadow = shadowElevation > 0.dp
    val shadowMod = if (hasShadow) Modifier.dropShadow(shape = shape, shadow = FloatingToolbarShadow) else Modifier

    // updateBlurEffect: with the material the backdrop blur is the fill and the background turns
    // transparent; without a backdrop the opaque fill stays.
    val backdropProvider = if (materialEffect) LocalMaterialBackdrop.current else null
    val isDark = COUITheme.colorScheme.background.luminance() < 0.5f
    val backdropModifier = if (backdropProvider != null) {
        val spec = FloatingToolbarDefaults.backdropSpec(isDark)
        val backdropShape = remember(cornerRadius) { SquircleShape(cornerRadius, COUIRoundCorner.UnspecifiedWeight) }
        remember(backdropProvider, spec, backdropShape) { backdropProvider.materialBackdrop(spec, backdropShape) }
    } else {
        null
    }

    Box(
        modifier = modifier
            .padding(outSidePadding)
            // The layer reaches past the toolbar, so a fade or render effect added to it never cuts
            // the shadow off.
            .graphicsLayerWithOverflow(if (hasShadow) FloatingToolbarShadow.layerOverflow() else 0.dp) {
                val s = scale.value.coerceAtLeast(0f)
                scaleX = s
                scaleY = s
            }
            .then(
                if (visible) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures { /* Consume click */ }
                    }
                } else {
                    // A hidden toolbar is INVISIBLE: no touches, nothing for accessibility.
                    Modifier
                        .clearAndSetSemantics { }
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                                }
                            }
                        }
                },
            )
            .then(dividerMod)
            .then(shadowMod)
            .then(backdropModifier ?: Modifier)
            .squircleBackground(
                color = if (backdropModifier != null) Color.Transparent else color,
                cornerRadius = cornerRadius,
                weight = COUIRoundCorner.UnspecifiedWeight,
            ),
    ) {
        content()
    }
}

/** The toolbar's soft drop shadow. */
private val FloatingToolbarShadow = Shadow(
    radius = 10.dp,
    color = Color.Black,
    alpha = 0.1f,
)

object FloatingToolbarDefaults {

    /**
     * Default corner radius of the [FloatingToolbar]: a capsule. COUI 17
     * `COUIBottomFloatingToolbarMenuView` derives it as half the content height
     * (`coui_floating_toolbar_menu_view_height` 44dp -> 22dp) and calls `addSmoothRoundRect`
     * without a weight: a fully rounded toolbar is the weight-1.0 Oplus soft capsule, any smaller
     * radius takes `default_g2_weight` 2.45 ([COUIRoundCorner.UnspecifiedWeight]).
     */
    val CornerRadius = 22.dp

    /**
     * Default color of the [FloatingToolbar].
     */
    @Composable
    fun defaultColor() = COUITheme.colorScheme.surfaceContainer

    /**
     * The COUI 17 material of the toolbar, `COUIMaterialBlurEffect.TYPE_FRAMEWORK_BOTTOM_BAR`, the
     * same bottom-bar material as [FloatingNavigationBarDefaults.backdropSpec].
     *
     * @param isDark Whether the dark-scheme layers are used.
     */
    fun backdropSpec(isDark: Boolean): MaterialBackdropSpec = FloatingNavigationBarDefaults.backdropSpec(isDark)

    /**
     * Default padding outside the [FloatingToolbar].
     */
    val OutSidePadding = PaddingValues(12.dp, 8.dp)
}
