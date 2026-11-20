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

/** Edit: ColorOS `color_menu_ic_edit_normal` (com.coloros.weather2_17.8.4). */
val COUIIcons.Edit: ImageVector
    get() {
        _edit?.let { return it }
        return ImageVector.Builder(
            name = "Edit",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 72f,
            viewportHeight = 72f,
        ).apply {
            addPath(
                pathData = addPathNodes("M 42 15 L 18 15 C 16.895 15 16 15.895 16 17 L 16 55 C 16 56.105 16.895 57 18 57 L 54 57 C 55.105 57 56 56.105 56 55 L 56 30 M 29 42 L 56 15"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 4f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
            )
            addPath(
                pathData = addPathNodes("M 42 14.5 L 42 15.5 L 41 15.5 L 41 14.5 Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _edit = it }
    }

private var _edit: ImageVector? = null
