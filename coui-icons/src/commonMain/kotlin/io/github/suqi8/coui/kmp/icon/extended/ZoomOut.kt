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

/** ZoomOut: ColorOS `icon_detail_collapse` (com.coloros.note_16.7.2). */
val COUIIcons.ZoomOut: ImageVector
    get() {
        _zoomOut?.let { return it }
        return ImageVector.Builder(
            name = "ZoomOut",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
            autoMirror = true,
        ).apply {
            addPath(
                pathData = addPathNodes("M4,20.5L10.5,14M10.5,14H4.5M10.5,14V20"),
                fill = SolidColor(Color.Black),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M21,3.5L14.5,10M14.5,10L20.5,10M14.5,10V4"),
                fill = SolidColor(Color.Black),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }.build().also { _zoomOut = it }
    }

private var _zoomOut: ImageVector? = null
