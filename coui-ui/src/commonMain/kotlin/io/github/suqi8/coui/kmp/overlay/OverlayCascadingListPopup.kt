// Copyright 2026, compose-coui-ui contributors
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
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.DropdownColors
import io.github.suqi8.coui.kmp.basic.DropdownDefaults
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.basic.ListPopupDefaults
import io.github.suqi8.coui.kmp.basic.PopupPositionProvider
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.layout.CascadingListPopupLayout
import io.github.suqi8.coui.kmp.utils.COUIPopupUtils.Companion.PopupLayout
import io.github.suqi8.coui.kmp.utils.popupHostInsideBackdrop

/**
 * A cascading list popup rendered inside the host `Scaffold`. Cascading depth is limited to 2.
 *
 * @param show Whether the popup is shown.
 * @param entries Grouped dropdown entries; top-level [DropdownItem]s with non-empty
 *   [DropdownItem.children] become submenu triggers.
 * @param onDismissRequest Invoked when the popup wants to be dismissed.
 * @param onDismissFinished Invoked after the exit animation finishes.
 * @param popupModifier Modifier applied to the popup body.
 * @param popupPositionProvider Position strategy for the primary popup relative to its anchor.
 * @param alignment Alignment of the primary popup.
 * @param enableWindowDim Whether to dim the rest of the window while the popup is shown.
 * @param maxHeight Maximum height of either side. Null bounds it by the safe area.
 * @param minWidth Minimum width of the popup.
 * @param renderInRootScaffold Whether to render in the outermost Scaffold.
 * @param dropdownColors Colors used by every row.
 * @param collapseOnSelection When true, selecting any leaf dismisses the popup.
 * @param backgroundColor The surface of both menus fill when it is not blurred (couiColorBackgroundTop, COUI's
 *   non-blur popup background). A translucent color alongside a blurring [backgroundModifier] paints
 *   a custom blur instead.
 * @param backgroundModifier A [Modifier] applied inside the menu surfaces clip, under the rows, on top of
 *   the default material blur.
 * @param materialBlur Whether the surface of both menus blurs what lies behind it with the COUI 17 popup material
 *   ([ListPopupDefaults.materialBlurSpec]: 160px, glow overlay), the ColorOS 17 look and the default.
 *   The blur is drawn by the [io.github.suqi8.coui.kmp.effect.LocalPopupMaterialBlur] provider in
 *   scope where the popup is called; without one, or without runtime-shader support, the popup keeps
 *   [backgroundColor]. Pass false to opt out. (COUI itself leaves popup blur off unless an app calls
 *   `COUIPopupListWindow.setUseBackgroundBlur`.)
 * @param materialEffect Whether the surface of both menus draws the COUI 17 material effects: the
 *   shadow-edge stroke and the touch spotlight (`RoundFrameLayout.initMaterialEffects`). Drawn on the
 *   blurred and the opaque surface alike (COUI only draws them once blurred). Both [materialBlur]
 *   and [materialEffect] default to [LocalCOUIMaterialEffectEnabled].
 */
@Composable
fun OverlayCascadingListPopup(
    show: Boolean,
    entries: List<DropdownEntry>,
    onDismissRequest: () -> Unit,
    popupModifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    onDismissFinished: (() -> Unit)? = null,
    popupPositionProvider: PopupPositionProvider = ListPopupDefaults.DropdownPositionProvider,
    alignment: PopupPositionProvider.Align = PopupPositionProvider.Align.End,
    enableWindowDim: Boolean = false,
    maxHeight: Dp? = null,
    minWidth: Dp = ListPopupDefaults.MinWidth,
    renderInRootScaffold: Boolean = true,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    collapseOnSelection: Boolean = true,
    backgroundColor: Color = ListPopupDefaults.backgroundColor(),
    materialBlur: Boolean = LocalCOUIMaterialEffectEnabled.current,
    materialEffect: Boolean = LocalCOUIMaterialEffectEnabled.current,
) {
    CascadingListPopupLayout(
        show = show,
        popupHost = { visible, hostContent ->
            val visibleState = remember { mutableStateOf(false) }
            visibleState.value = visible
            PopupLayout(
                visible = visibleState,
                // COUIPopupListWindow.dismiss makes the window untouchable before the exit
                // animation, so the page takes touches again while the menus leave.
                touchable = show,
                enableWindowDim = false,
                enableBackHandler = false,
                enterTransition = EnterTransition.None,
                exitTransition = ExitTransition.None,
                renderInRootScaffold = renderInRootScaffold,
            ) {
                hostContent()
            }
        },
        entries = entries,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        popupModifier = popupModifier,
        popupPositionProvider = popupPositionProvider,
        alignment = alignment,
        enableWindowDim = enableWindowDim,
        maxHeight = maxHeight,
        minWidth = minWidth,
        dropdownColors = dropdownColors,
        collapseOnSelection = collapseOnSelection,
        backgroundColor = backgroundColor,
        backgroundModifier = backgroundModifier,
        // A popup drawn inside the page recording it would blur stays opaque (no self-sampling).
        materialBlur = materialBlur && !popupHostInsideBackdrop(renderInRootScaffold),
        materialEffect = materialEffect,
    )
}
