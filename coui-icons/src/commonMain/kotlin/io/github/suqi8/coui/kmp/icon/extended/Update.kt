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
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Update: ColorOS `drawable/settings_app_updater_ic_inner` (settings_17.0.0). */
val COUIIcons.Update: ImageVector
    get() {
        _update?.let { return it }
        return ImageVector.Builder(
            name = "Update",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 26.8f,
            viewportHeight = 26.8f,
        ).apply {
            group(translationX = -2.6f, translationY = -2.6f) {
                addPath(
                    pathData = addPathNodes("M16,16m10,-0a10,10 0,1 1,-20 -0a10,10 0,1 1,20 -0"),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.4f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Round,
                )
                addPath(
                    pathData = addPathNodes("M16,6C10.477,6 6,10.477 6,16C6,21.523 10.477,26 16,26C21.523,26 26,21.523 26,16C26,10.477 21.523,6 16,6ZM20.707,14.293L16.707,10.293C16.317,9.902 15.683,9.902 15.293,10.293L11.293,14.293C10.902,14.683 10.902,15.317 11.293,15.707C11.683,16.098 12.317,16.098 12.707,15.707L15,13.414L15,21C15,21.552 15.448,22 16,22C16.552,22 17,21.552 17,21L17,13.414L19.293,15.707C19.683,16.098 20.317,16.098 20.707,15.707C21.098,15.317 21.098,14.683 20.707,14.293Z"),
                    pathFillType = PathFillType.EvenOdd,
                    fill = SolidColor(Color.Black),
                )
            }
        }.build().also { _update = it }
    }

private var _update: ImageVector? = null
