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

/** Album: ColorOS `app_ic_tab_album` (com.coloros.gallery3d_16.50.46). */
val COUIIcons.Album: ImageVector
    get() {
        _album?.let { return it }
        return ImageVector.Builder(
            name = "Album",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M21.287,4.167C21.637,4.167 21.967,4.217 22.277,4.317C22.207,3.287 21.337,2.467 20.287,2.467H4.887C3.837,2.467 2.967,3.287 2.897,4.317C3.207,4.217 3.537,4.167 3.887,4.167H21.287ZM3.891,5.468C2.786,5.468 1.891,6.363 1.891,7.468V19.868C1.891,20.972 2.786,21.868 3.891,21.868H21.291C22.395,21.868 23.291,20.972 23.291,19.868V7.468C23.291,6.363 22.395,5.468 21.291,5.468H3.891ZM3.291,7.468C3.291,7.136 3.559,6.868 3.891,6.868H21.291C21.622,6.868 21.891,7.136 21.891,7.468V19.868C21.891,20.199 21.622,20.468 21.291,20.468H3.891C3.559,20.468 3.291,20.199 3.291,19.868V7.468Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _album = it }
    }

private var _album: ImageVector? = null
