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

/** AddCircle: ColorOS `ic_category_add` (com.coloros.filemanager_16.12.2). */
val COUIIcons.AddCircle: ImageVector
    get() {
        _addCircle?.let { return it }
        return ImageVector.Builder(
            name = "AddCircle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M16.25,12H7.75"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Miter,
            )
            addPath(
                pathData = addPathNodes("M12,16.25V7.75"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Miter,
            )
            addPath(
                pathData = addPathNodes("M12,22C17.523,22 22,17.523 22,12C22,6.477 17.523,2 12,2C6.477,2 2,6.477 2,12C2,17.523 6.477,22 12,22Z"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Miter,
            )
        }.build().also { _addCircle = it }
    }

private var _addCircle: ImageVector? = null
