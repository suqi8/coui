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

/** Add: ColorOS `menu_ic_add_normal` (settings_17.0.0). */
val COUIIcons.Add: ImageVector
    get() {
        _add?.let { return it }
        return ImageVector.Builder(
            name = "Add",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 72f,
            viewportHeight = 72f,
        ).apply {
            addPath(
                pathData = addPathNodes("M 12 36 L 60 36 M 36 12 L 36 60"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 4f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
            )
        }.build().also { _add = it }
    }

private var _add: ImageVector? = null
