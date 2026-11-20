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

/** VolumeOff: ColorOS `preview_icon_media_volume_mute` (com.coloros.filemanager_16.12.2). */
val COUIIcons.VolumeOff: ImageVector
    get() {
        _volumeOff?.let { return it }
        return ImageVector.Builder(
            name = "VolumeOff",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M6.965,8H3.804C3.086,8 2.504,8.582 2.504,9.3V14.7C2.504,15.418 3.086,16 3.804,16H6.965C7.31,16 7.641,16.137 7.885,16.381L12.504,21V3L7.885,7.619C7.641,7.863 7.31,8 6.965,8Z"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M21.496,9.996C21.769,9.722 21.769,9.279 21.496,9.006C21.222,8.732 20.779,8.732 20.506,9.006L18.501,11.011L16.496,9.006C16.222,8.732 15.779,8.732 15.506,9.006C15.232,9.279 15.232,9.722 15.506,9.996L17.511,12.001L15.506,14.006C15.232,14.279 15.232,14.722 15.506,14.996C15.779,15.269 16.222,15.269 16.496,14.996L18.501,12.991L20.506,14.996C20.779,15.269 21.222,15.269 21.496,14.996C21.769,14.722 21.769,14.279 21.496,14.006L19.491,12.001L21.496,9.996Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _volumeOff = it }
    }

private var _volumeOff: ImageVector? = null
