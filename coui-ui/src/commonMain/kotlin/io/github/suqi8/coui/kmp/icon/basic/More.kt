// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/**
 * COUI coui_toolbar_menu_icon_more_normal (the `coui_floating_toolbar_more` overflow icon): three
 * vertically stacked dots of radius 1.75 at y 4.875, 12 and 19.125.
 */
val COUIIcons.Basic.More: ImageVector
    get() {
        if (_more != null) return _more!!
        _more = ImageVector.Builder("More", 24.dp, 24.dp, 24f, 24f).apply {
            path(fill = SolidColor(Color.Black)) {
                listOf(4.875f, 12f, 19.125f).forEach { centerY ->
                    moveTo(12f, centerY - 1.75f)
                    arcToRelative(1.75f, 1.75f, 0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = 0f, dy1 = 3.5f)
                    arcToRelative(1.75f, 1.75f, 0f, isMoreThanHalf = true, isPositiveArc = true, dx1 = 0f, dy1 = -3.5f)
                    close()
                }
            }
        }.build()
        return _more!!
    }

private var _more: ImageVector? = null
