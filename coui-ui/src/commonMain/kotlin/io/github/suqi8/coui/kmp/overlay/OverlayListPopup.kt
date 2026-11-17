// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.overlay

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import io.github.suqi8.coui.kmp.basic.ListPopupColumn
import io.github.suqi8.coui.kmp.basic.ListPopupDefaults
import io.github.suqi8.coui.kmp.basic.PopupPositionProvider
import io.github.suqi8.coui.kmp.basic.PreciseClickState
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.layout.ListPopupLayout
import io.github.suqi8.coui.kmp.utils.COUIPopupUtils.Companion.PopupLayout

/**
 * A popup with a list of items.
 *
 * @param show Whether the [OverlayListPopup] is shown.
 * @param popupModifier The modifier to be applied to the [OverlayListPopup].
 * @param popupPositionProvider The [PopupPositionProvider] of the [OverlayListPopup].
 * @param alignment The alignment of the [OverlayListPopup].
 * @param enableWindowDim Whether to dim the window while the [OverlayListPopup] is shown. Off by
 *   default, as COUI 17 `COUIPopupListWindow` never dims what lies behind a menu.
 *   Defaults to false: `COUIPopupListWindow` calls `setBackgroundDrawable(null)` and never dims,
 *   so a COUI dropdown leaves the content behind it untouched.
 * @param onDismissRequest The callback when the [OverlayListPopup] is dismissed.
 * @param onDismissFinished The callback when the [OverlayListPopup] is completely dismissed (after exit animation).
 * @param maxHeight The maximum height of the [OverlayListPopup]. If null, the height will be calculated automatically.
 * @param minWidth The minimum width of the [OverlayListPopup].
 * @param renderInRootScaffold Whether to render the popup in the root (outermost) Scaffold.
 *   When true (default), the popup covers the full screen. When false, it renders within the
 *   current Scaffold's bounds with position compensation.
 * @param preciseClickState Opens the popup at the last point recorded by
 *   [io.github.suqi8.coui.kmp.basic.preciseClickAnchor] on the anchor instead of centring it on the
 *   anchor, matching COUI `PreciseClickHelper`. Null (default) keeps anchor-centred placement.
 * @param backgroundColor The popup surface fill when it is not blurred (couiColorBackgroundTop, COUI's
 *   non-blur popup background). A translucent color alongside a blurring [backgroundModifier] paints
 *   a custom blur instead.
 * @param backgroundModifier A [Modifier] applied inside the popup surface clip, under the rows, on top of
 *   the default material blur.
 * @param materialBlur Whether the popup surface blurs what lies behind it with the COUI 17 popup material
 *   ([ListPopupDefaults.materialBlurSpec]: 160px, glow overlay), the ColorOS 17 look and the default.
 *   The blur is drawn by the [io.github.suqi8.coui.kmp.effect.LocalPopupMaterialBlur] provider in
 *   scope where the popup is called; without one, or without runtime-shader support, the popup keeps
 *   [backgroundColor]. Pass false to opt out. (COUI itself leaves popup blur off unless an app calls
 *   `COUIPopupListWindow.setUseBackgroundBlur`.)
 * @param materialEffect Whether the popup surface draws the COUI 17 material effects: the
 *   shadow-edge stroke and the touch spotlight (`RoundFrameLayout.initMaterialEffects`). Drawn on the
 *   blurred and the opaque surface alike (COUI only draws them once blurred). Both [materialBlur]
 *   and [materialEffect] default to [LocalCOUIMaterialEffectEnabled].
 * @param content The [Composable] content of the [OverlayListPopup]. You should use the [ListPopupColumn] in general.
 */
@Composable
fun OverlayListPopup(
    show: Boolean,
    popupModifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    popupPositionProvider: PopupPositionProvider = ListPopupDefaults.DropdownPositionProvider,
    alignment: PopupPositionProvider.Align = PopupPositionProvider.Align.Start,
    enableWindowDim: Boolean = false,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    maxHeight: Dp? = null,
    minWidth: Dp = ListPopupDefaults.MinWidth,
    renderInRootScaffold: Boolean = true,
    preciseClickState: PreciseClickState? = null,
    backgroundColor: Color = ListPopupDefaults.backgroundColor(),
    materialBlur: Boolean = LocalCOUIMaterialEffectEnabled.current,
    materialEffect: Boolean = LocalCOUIMaterialEffectEnabled.current,
    content: @Composable () -> Unit,
) {
    ListPopupLayout(
        show = show,
        popupHost = { visible, hostContent ->
            val visibleState = remember { mutableStateOf(false) }
            visibleState.value = visible
            PopupLayout(
                visible = visibleState,
                enableWindowDim = false,
                enableBackHandler = false,
                enterTransition = EnterTransition.None,
                exitTransition = ExitTransition.None,
                renderInRootScaffold = renderInRootScaffold,
            ) {
                hostContent()
            }
        },
        popupModifier = popupModifier,
        popupPositionProvider = popupPositionProvider,
        alignment = alignment,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        maxHeight = maxHeight,
        minWidth = minWidth,
        preciseClickState = preciseClickState,
        backgroundColor = backgroundColor,
        backgroundModifier = backgroundModifier,
        materialBlur = materialBlur,
        materialEffect = materialEffect,
        content = content,
    )
}
