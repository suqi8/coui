// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.TouchMotion
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch

/**
 * The hover and focus layers COUI draws as the background of a navigation item, below its icon and
 * label: `COUIMaskEffectDrawable(context, MASK_EFFECT_TYPE_CONTAINER_WIDGET)` on the classic bar and
 * rail items (`COUINavigationItemView.configStateEffectBackground`, `COUINavigationRailItemView`) and
 * `COUIMaskRippleDrawable.setCustomRippleMask` on the floating bar items
 * (`COUIBottomFloatingToolbarItemView.updateBackgroundMaskRect`).
 *
 * Each layer fades to `?couiColorHover` / `?couiColorFocus` on its own response 0.3 / bounce 0 spring
 * (`StateEffectAnimator`), the focus layer above the hover one. A disabled item drops both at once.
 * The selected state draws nothing (`enableSelectedState(false)`), and there is no press layer: the
 * items strip `state_pressed` or never report a touch to the drawable.
 *
 * @param interactionSource The item's interactions: hover from a hoverable or clickable node, focus
 *   from a focusable one.
 * @param enabled Whether the item is enabled.
 * @param cornerRadius The corner radius of the mask.
 * @param horizontalInset How far the mask keeps from the item's start and end; a negative value lets
 *   it reach past them.
 */
@Composable
internal fun Modifier.navigationItemStateMask(
    interactionSource: InteractionSource,
    enabled: Boolean,
    cornerRadius: Dp,
    horizontalInset: Dp = 0.dp,
): Modifier {
    val hover = remember { Animatable(0f, visibilityThreshold = NAVIGATION_ITEM_MASK_THRESHOLD) }
    val focus = remember { Animatable(0f, visibilityThreshold = NAVIGATION_ITEM_MASK_THRESHOLD) }
    val hoverColor = COUITheme.colorScheme.hover
    val focusColor = COUITheme.colorScheme.focus
    LaunchedEffect(interactionSource, enabled) {
        if (!enabled) {
            // onViewStateChanged(state_enabled): animateToProgress(0, false) on every layer.
            hover.snapTo(0f)
            focus.snapTo(0f)
            return@LaunchedEffect
        }
        val hovers = mutableListOf<HoverInteraction.Enter>()
        val focuses = mutableListOf<FocusInteraction.Focus>()
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is HoverInteraction.Enter -> {
                    hovers += interaction
                    launch { hover.animateTo(1f, NavigationItemMaskSpring) }
                }

                is HoverInteraction.Exit -> {
                    hovers -= interaction.enter
                    if (hovers.isEmpty()) launch { hover.animateTo(0f, NavigationItemMaskSpring) }
                }

                is FocusInteraction.Focus -> {
                    focuses += interaction
                    launch { focus.animateTo(1f, NavigationItemMaskSpring) }
                }

                is FocusInteraction.Unfocus -> {
                    focuses -= interaction.focus
                    if (focuses.isEmpty()) launch { focus.animateTo(0f, NavigationItemMaskSpring) }
                }
            }
        }
    }
    return drawBehind {
        val hoverAlpha = hover.value.coerceIn(0f, 1f)
        val focusAlpha = focus.value.coerceIn(0f, 1f)
        if (hoverAlpha <= 0f && focusAlpha <= 0f) return@drawBehind
        val inset = horizontalInset.toPx()
        val maskSize = Size((size.width - inset * 2f).coerceAtLeast(0f), size.height)
        val topLeft = Offset(inset, 0f)
        val radius = cornerRadius.toPx().coerceAtMost(maskSize.minDimension / 2f)
        val corner = CornerRadius(radius, radius)
        if (hoverAlpha > 0f) {
            drawRoundRect(color = hoverColor, topLeft = topLeft, size = maskSize, cornerRadius = corner, alpha = hoverAlpha)
        }
        if (focusAlpha > 0f) {
            drawRoundRect(color = focusColor, topLeft = topLeft, size = maskSize, cornerRadius = corner, alpha = focusAlpha)
        }
    }
}

/** `StateEffectAnimator`'s spring on the hover and focus layers: response 0.3, bounce 0. */
private val NavigationItemMaskSpring = TouchMotion.spring(
    response = 0.3f,
    bounce = 0f,
    visibilityThreshold = NAVIGATION_ITEM_MASK_THRESHOLD,
)

private const val NAVIGATION_ITEM_MASK_THRESHOLD = 0.001f
