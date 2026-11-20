// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Help: ColorOS `ic_help` (settings_17.0.0). */
val COUIIcons.Help: ImageVector
    get() {
        _help?.let { return it }
        return ImageVector.Builder(
            name = "Help",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
            autoMirror = true,
        ).apply {
            addPath(
                pathData = addPathNodes("M2,12C2,6.48 6.48,2 12,2C17.52,2 22,6.48 22,12C22,17.52 17.52,22 12,22C6.48,22 2,17.52 2,12ZM13,16V18H11V16H13ZM12,20C7.59,20 4,16.41 4,12C4,7.59 7.59,4 12,4C16.41,4 20,7.59 20,12C20,16.41 16.41,20 12,20ZM8,10C8,7.79 9.79,6 12,6C14.21,6 16,7.79 16,10C16,11.283 15.21,11.973 14.441,12.646C13.711,13.283 13,13.905 13,15H11C11,13.179 11.942,12.457 12.77,11.822C13.42,11.324 14,10.879 14,10C14,8.9 13.1,8 12,8C10.9,8 10,8.9 10,10H8Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _help = it }
    }

private var _help: ImageVector? = null
