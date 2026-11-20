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

/** Rename: ColorOS `menu_ic_rename_normal` (com.coloros.soundrecorder_16.17.8). */
val COUIIcons.Rename: ImageVector
    get() {
        _rename?.let { return it }
        return ImageVector.Builder(
            name = "Rename",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M4.683,16.278C4.726,16.155 4.796,16.043 4.889,15.951L17.427,3.413C17.95,2.889 18.965,2.849 20.053,3.937C21.143,5.027 21.104,6.043 20.58,6.566L8.042,19.105C7.95,19.197 7.838,19.267 7.715,19.31L3.055,20.938L4.683,16.278Z"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M15.006,5.834C15.529,5.31 16.544,5.27 17.632,6.358C18.722,7.448 18.683,8.464 18.159,8.987"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
            )
        }.build().also { _rename = it }
    }

private var _rename: ImageVector? = null
