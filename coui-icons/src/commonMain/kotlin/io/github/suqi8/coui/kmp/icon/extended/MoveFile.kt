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

/** MoveFile: ColorOS `drawable/color_tool_menu_ic_cut` (com.coloros.filemanager_16.12.2). */
val COUIIcons.MoveFile: ImageVector
    get() {
        _moveFile?.let { return it }
        return ImageVector.Builder(
            name = "MoveFile",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M4.3,2.3C3.195,2.3 2.3,3.195 2.3,4.3V19.7C2.3,20.804 3.195,21.7 4.3,21.7H15.7C16.804,21.7 17.7,20.804 17.7,19.7V15H16.3V19.7C16.3,20.031 16.031,20.3 15.7,20.3H12.702L12.702,3.7H15.7C16.031,3.7 16.3,3.968 16.3,4.3V9H17.7V4.3C17.7,3.195 16.804,2.3 15.7,2.3H4.3ZM11.298,3.7H4.3C3.968,3.7 3.7,3.968 3.7,4.3V19.7C3.7,20.031 3.968,20.3 4.3,20.3H11.298L11.298,3.7ZM15,11.3C14.613,11.3 14.3,11.613 14.3,12C14.3,12.386 14.613,12.7 15,12.7L21.31,12.7L19.505,14.505C19.232,14.778 19.232,15.221 19.505,15.495C19.778,15.768 20.221,15.768 20.495,15.495L23.495,12.495C23.768,12.221 23.768,11.778 23.495,11.505L20.495,8.505C20.221,8.231 19.778,8.231 19.505,8.505C19.232,8.778 19.232,9.221 19.505,9.495L21.31,11.3H15Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _moveFile = it }
    }

private var _moveFile: ImageVector? = null
