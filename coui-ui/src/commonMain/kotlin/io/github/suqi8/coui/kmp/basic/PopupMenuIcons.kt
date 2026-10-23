// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * The trailing state icons of a COUI 17 popup row. Both are drawn black and tinted by the row,
 * as `MixedPopupListAdapter.setStateIcon` tints them through
 * `coui_popup_list_window_item_status_icon_tint_selector`.
 */
internal object PopupMenuIcons {

    /**
     * The check of a selected row (`coui_menu_ic_checkbox_selected_new`): a 1.6dp round-capped
     * stroke at 90% alpha, so the tint lands at 0.9 of its own alpha.
     */
    val Check: ImageVector by lazy {
        ImageVector.Builder(
            name = "PopupMenuCheck",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).addPath(
            pathData = addPathNodes("M9,12.435L13.388,17L23,7"),
            stroke = SolidColor(Color.Black),
            strokeAlpha = 0.9f,
            strokeLineWidth = 1.6f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ).build()
    }

    /**
     * The indicator of a row that opens a sub menu (`coui_list_expandable_indicator`): an up and a
     * down chevron, 1.4dp round-capped strokes, auto-mirrored under RTL. COUI 17 never rotates it;
     * only its tint follows the sub menu state.
     */
    val ExpandableIndicator: ImageVector by lazy {
        ImageVector.Builder(
            name = "PopupMenuExpandableIndicator",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
            autoMirror = true,
        ).addPath(
            pathData = addPathNodes("M14.2,10L18.329,5.413C18.527,5.192 18.873,5.192 19.072,5.413L23.2,10"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.4f,
            strokeLineCap = StrokeCap.Round,
        ).addPath(
            pathData = addPathNodes("M23.2,14L19.072,18.587C18.873,18.808 18.527,18.808 18.329,18.587L14.2,14"),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.4f,
            strokeLineCap = StrokeCap.Round,
        ).build()
    }
}
