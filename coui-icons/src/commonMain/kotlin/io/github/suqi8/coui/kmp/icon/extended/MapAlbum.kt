// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** MapAlbum: ColorOS `ic_event_location` (com.coloros.calendar_17.6.20). */
val COUIIcons.MapAlbum: ImageVector
    get() {
        _mapAlbum?.let { return it }
        return ImageVector.Builder(
            name = "MapAlbum",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 26.7f,
            viewportHeight = 26.7f,
        ).apply {
            group(translationX = 0.725f, translationY = 1.35f) {
                addPath(
                    pathData = addPathNodes("M18.783,15.472C20.025,14.052 20.776,12.199 20.776,10.172C20.776,5.7 17.118,2.074 12.604,2.074C8.091,2.074 4.433,5.7 4.433,10.172C4.433,12.195 5.181,14.044 6.418,15.463C8.29,17.725 10.318,19.852 12.488,21.829L12.587,21.92C12.597,21.928 12.612,21.928 12.621,21.92L12.721,21.829C14.888,19.854 16.913,17.73 18.783,15.472Z"),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.4f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                )
                addPath(
                    pathData = addPathNodes("M12.611,10.387m-3.617,0a3.617,3.617 0,1 1,7.233 0a3.617,3.617 0,1 1,-7.233 0"),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.4f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                )
            }
        }.build().also { _mapAlbum = it }
    }

private var _mapAlbum: ImageVector? = null
