// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** SelectAll: ColorOS `color_menu_ic_all_sel_normal` (com.coloros.note_16.7.2). */
val COUIIcons.SelectAll: ImageVector
    get() {
        _selectAll?.let { return it }
        return ImageVector.Builder(
            name = "SelectAll",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M7,19.7C7,20.418 7.582,21 8.3,21H19.7C20.418,21 21,20.418 21,19.7V8.3C21,7.582 20.418,7 19.7,7H8.3C7.582,7 7,7.582 7,8.3V19.7Z"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M11,13.854L13.095,16L17,12"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M5,17H4.3C3.582,17 3,16.418 3,15.7V4.3C3,3.582 3.582,3 4.3,3H15.7C16.418,3 17,3.582 17,4.3V5"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Round,
            )
        }.build().also { _selectAll = it }
    }

private var _selectAll: ImageVector? = null
