// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Recent: ColorOS `drawable/ic_main_recent` (com.coloros.filemanager_16.12.2). */
val COUIIcons.Recent: ImageVector
    get() {
        _recent?.let { return it }
        return ImageVector.Builder(
            name = "Recent",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            group() {
                group(clipPathData = addPathNodes("M0,0h24v24h-24z")) {
                    addPath(
                        pathData = addPathNodes("M2.7,12C2.7,6.864 6.864,2.7 12,2.7C17.136,2.7 21.3,6.864 21.3,12C21.3,17.136 17.136,21.3 12,21.3C6.864,21.3 2.7,17.136 2.7,12ZM12,1.3C6.09,1.3 1.3,6.09 1.3,12C1.3,17.909 6.09,22.7 12,22.7C17.909,22.7 22.7,17.909 22.7,12C22.7,6.09 17.909,1.3 12,1.3ZM12,5.611C12.335,5.611 12.611,5.875 12.627,6.211L12.867,11.542L15.973,14.977C16.199,15.227 16.189,15.609 15.952,15.847C15.714,16.084 15.332,16.094 15.082,15.869L11.274,12.426C11.164,12.325 11.104,12.181 11.11,12.032L11.373,6.211C11.388,5.875 11.664,5.611 12,5.611Z"),
                        pathFillType = PathFillType.EvenOdd,
                        fill = SolidColor(Color.Black),
                    )
                }
            }
        }.build().also { _recent = it }
    }

private var _recent: ImageVector? = null
