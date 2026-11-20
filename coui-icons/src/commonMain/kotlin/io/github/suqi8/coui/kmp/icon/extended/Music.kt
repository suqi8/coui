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

/** Music: ColorOS `videoeditor_ic_menu_music` (com.coloros.gallery3d_16.50.46). */
val COUIIcons.Music: ImageVector
    get() {
        _music?.let { return it }
        return ImageVector.Builder(
            name = "Music",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 27.3f,
            viewportHeight = 27.3f,
        ).apply {
            group(translationX = 0.775f, translationY = 1.77f) {
                addPath(
                    pathData = addPathNodes("M14.385,7.485L14.294,18.499V18.501C14.294,20.82 12.413,22.701 10.094,22.701C7.774,22.701 5.894,20.82 5.894,18.501C5.894,16.181 7.774,14.301 10.094,14.301C11.169,14.301 12.151,14.705 12.894,15.37V3.992C12.894,3.09 13.497,2.3 14.367,2.063L18.278,0.996C19.041,0.788 19.794,1.363 19.794,2.154V4.011C19.794,4.909 19.194,5.698 18.329,5.938L14.825,6.911C14.567,6.983 14.388,7.217 14.385,7.485Z"),
                    pathFillType = PathFillType.EvenOdd,
                    fill = SolidColor(Color.Black),
                )
            }
        }.build().also { _music = it }
    }

private var _music: ImageVector? = null
