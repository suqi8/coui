// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Redo: ColorOS `color_menu_ic_redo_normal` (com.coloros.note_16.7.2). */
val COUIIcons.Redo: ImageVector
    get() {
        _redo?.let { return it }
        return ImageVector.Builder(
            name = "Redo",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M20.3,7.5C20.3,7.113 20.614,6.8 21,6.8C21.387,6.8 21.7,7.113 21.7,7.5V13.5C21.7,13.886 21.387,14.2 21,14.2H15C14.614,14.2 14.3,13.886 14.3,13.5C14.3,13.113 14.614,12.8 15,12.8H19.136C18.361,11.45 17.21,10.345 15.816,9.625C14.156,8.767 12.25,8.507 10.421,8.889C8.591,9.271 6.949,10.272 5.771,11.723C4.592,13.174 3.95,14.986 3.951,16.855C3.952,17.242 3.638,17.556 3.252,17.556C2.865,17.556 2.552,17.243 2.551,16.857C2.55,14.666 3.303,12.541 4.684,10.84C6.065,9.14 7.99,7.967 10.135,7.519C12.279,7.071 14.513,7.375 16.459,8.381C18.064,9.21 19.392,10.474 20.3,12.017V7.5Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _redo = it }
    }

private var _redo: ImageVector? = null
