// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/**
 * The circular reset arrow of the camera parameter panels, ported 1:1 from the camera
 * icon_reset_common.xml (24x24dp viewport: an open ring plus a 1.24444dp round-join arrow head).
 * Tint it with the panel's content color.
 */
val COUIIcons.Basic.Reset: ImageVector
    get() {
        if (_reset != null) return _reset!!
        _reset = ImageVector.Builder("Reset", 24.dp, 24.dp, 24f, 24f).apply {
            addPath(
                pathData = addPathNodes(
                    "M18.756,12C18.756,15.731 15.731,18.756 12,18.756C8.269,18.756 5.244,15.731 5.244,12" +
                        "C5.244,8.269 8.269,5.244 12,5.244C14.332,5.244 16.388,6.426 17.602,8.223H19.054" +
                        "C17.705,5.709 15.052,4 12,4C7.582,4 4,7.582 4,12C4,16.418 7.582,20 12,20" +
                        "C16.418,20 20,16.418 20,12C20,11.964 20,11.928 19.999,11.892H18.755" +
                        "C18.755,11.928 18.756,11.964 18.756,12Z",
                ),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M14.164,8.667H18.831V4"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.24444f,
                strokeLineJoin = StrokeJoin.Round,
            )
        }.build()
        return _reset!!
    }

private var _reset: ImageVector? = null
