// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.suqi8.coui.kmp.basic.DropdownColors
import io.github.suqi8.coui.kmp.basic.DropdownDefaults
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.basic.ListPopupDefaults
import io.github.suqi8.coui.kmp.basic.PopupPositionProvider
import io.github.suqi8.coui.kmp.layout.CascadingListPopupLayout
import io.github.suqi8.coui.kmp.utils.RemovePlatformDialogDefaultEffects
import io.github.suqi8.coui.kmp.utils.WindowNavigationEventScope
import io.github.suqi8.coui.kmp.utils.platformDialogProperties

/**
 * A cascading list popup rendered at window level (as a [Dialog]) instead of inside a
 * `Scaffold`. Otherwise behaves identically to [io.github.suqi8.coui.kmp.overlay.OverlayCascadingListPopup].
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
 * @param dropdownColors Colors used by every row.
 * @param collapseOnSelection When true, selecting any leaf dismisses the popup.
 * @param backgroundColor The surfaces of both menus fill (couiColorBackgroundTop). Pass a transparent color
 *   alongside a blurring [backgroundModifier].
 * @param backgroundModifier A [Modifier] applied inside the surfaces of both menus clip, under the rows: the
 *   slot for the COUI 17 blurred background, e.g. coui-blur's
 *   `Modifier.couiMaterialBlur(backdrop, MaterialBlurDefaults.popup(isDark))`. COUI leaves popup blur
 *   off unless an app asks for it (`COUIPopupListWindow.setUseBackgroundBlur`).
 * @param materialEffect Whether to draw the material effects of a blurred COUI 17 popup: the
 *   shadow-edge stroke and the touch spotlight (`RoundFrameLayout.initMaterialEffects`).
 */
@Composable
fun WindowCascadingListPopup(
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
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    collapseOnSelection: Boolean = true,
    backgroundColor: Color = ListPopupDefaults.backgroundColor(),
    materialEffect: Boolean = false,
) {
    val currentOnDismissRequest = rememberUpdatedState(onDismissRequest)

    CascadingListPopupLayout(
        show = show,
        popupHost = { visible, hostContent ->
            if (visible) {
                Dialog(
                    onDismissRequest = { currentOnDismissRequest.value() },
                    properties = platformDialogProperties(),
                ) {
                    RemovePlatformDialogDefaultEffects()
                    WindowNavigationEventScope {
                        hostContent()
                    }
                }
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
        materialEffect = materialEffect,
    )
}
