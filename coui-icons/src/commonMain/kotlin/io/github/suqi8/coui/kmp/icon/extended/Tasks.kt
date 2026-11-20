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

/** Tasks: ColorOS `drawable/color_menu_ic_todo_normal` (com.coloros.note_16.7.2). */
val COUIIcons.Tasks: ImageVector
    get() {
        _tasks?.let { return it }
        return ImageVector.Builder(
            name = "Tasks",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M19.7,4H4.3C3.582,4 3,4.582 3,5.3V19.7C3,20.418 3.582,21 4.3,21H19.7C20.418,21 21,20.418 21,19.7V5.3C21,4.582 20.418,4 19.7,4Z"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M8,14.398L10.667,17.198L16,11.598"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M3.7,7.843H20.3"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M6.7,2.8C6.7,2.413 6.387,2.1 6,2.1C5.613,2.1 5.3,2.413 5.3,2.8H6.7ZM5.3,2.8V3.3H6.7V2.8H5.3Z"),
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M18.7,2.8C18.7,2.413 18.387,2.1 18,2.1C17.613,2.1 17.3,2.413 17.3,2.8H18.7ZM17.3,2.8V3.3H18.7V2.8H17.3Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _tasks = it }
    }

private var _tasks: ImageVector? = null
