// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Filter: ColorOS `ic_filter_icon_new` (com.coloros.filemanager_16.12.2). */
val COUIIcons.Filter: ImageVector
    get() {
        _filter?.let { return it }
        return ImageVector.Builder(
            name = "Filter",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 18f,
            viewportHeight = 18f,
            autoMirror = true,
        ).apply {
            addPath(
                pathData = addPathNodes("M15.232,3.616C15.437,3.372 15.264,3 14.945,3H3.055C2.736,3 2.563,3.372 2.768,3.616L7.5,9.248V14.25L10.5,15.75V9.248L15.232,3.616Z"),
                pathFillType = PathFillType.EvenOdd,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.05f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }.build().also { _filter = it }
    }

private var _filter: ImageVector? = null
