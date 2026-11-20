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

/** Undo: ColorOS `color_menu_ic_undo_normal` (com.coloros.note_16.7.2). */
val COUIIcons.Undo: ImageVector
    get() {
        _undo?.let { return it }
        return ImageVector.Builder(
            name = "Undo",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M3.7,7.5C3.7,7.113 3.387,6.8 3,6.8C2.613,6.8 2.3,7.113 2.3,7.5V13.5C2.3,13.886 2.613,14.2 3,14.2H9C9.387,14.2 9.7,13.886 9.7,13.5C9.7,13.113 9.387,12.8 9,12.8H4.865C5.64,11.45 6.79,10.345 8.184,9.625C9.844,8.767 11.75,8.507 13.579,8.889C15.409,9.271 17.051,10.272 18.229,11.723C19.408,13.174 20.05,14.986 20.049,16.855C20.049,17.242 20.362,17.556 20.748,17.556C21.135,17.556 21.449,17.243 21.449,16.857C21.451,14.666 20.697,12.541 19.316,10.84C17.935,9.14 16.01,7.967 13.866,7.519C11.721,7.071 9.488,7.375 7.541,8.381C5.937,9.21 4.608,10.474 3.7,12.017V7.5Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _undo = it }
    }

private var _undo: ImageVector? = null
