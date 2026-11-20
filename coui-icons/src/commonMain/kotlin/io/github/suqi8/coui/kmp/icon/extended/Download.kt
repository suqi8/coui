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

/** Download: ColorOS `drawable/ic_menu_download_remote_mac` (com.coloros.filemanager_16.12.2). */
val COUIIcons.Download: ImageVector
    get() {
        _download?.let { return it }
        return ImageVector.Builder(
            name = "Download",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
            autoMirror = true,
        ).apply {
            group() {
                group(clipPathData = addPathNodes("M0,0h24v24h-24z")) {
                    addPath(
                        pathData = addPathNodes("M11.993,1.949C12.38,1.949 12.693,2.263 12.693,2.649V13.873L15.998,10.568C16.271,10.294 16.715,10.294 16.988,10.568C17.261,10.841 17.261,11.284 16.988,11.557L12.488,16.058C12.215,16.331 11.771,16.331 11.498,16.058L6.998,11.557C6.725,11.284 6.725,10.841 6.998,10.568C7.271,10.294 7.715,10.294 7.988,10.568L11.293,13.873V2.649C11.293,2.263 11.606,1.949 11.993,1.949ZM3.693,17.563C3.693,17.176 3.38,16.862 2.993,16.862C2.606,16.862 2.293,17.176 2.293,17.563V19.263C2.293,20.367 3.188,21.263 4.293,21.263H19.693C20.798,21.263 21.693,20.367 21.693,19.263V17.563C21.693,17.176 21.38,16.862 20.993,16.862C20.606,16.862 20.293,17.176 20.293,17.563V19.263C20.293,19.594 20.024,19.862 19.693,19.862H4.293C3.962,19.862 3.693,19.594 3.693,19.263V17.563Z"),
                        pathFillType = PathFillType.EvenOdd,
                        fill = SolidColor(Color.Black),
                    )
                }
            }
        }.build().also { _download = it }
    }

private var _download: ImageVector? = null
