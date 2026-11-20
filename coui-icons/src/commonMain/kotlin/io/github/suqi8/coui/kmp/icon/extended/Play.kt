// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Play: ColorOS `photoeditorpage_menu_play` (com.coloros.gallery3d_16.50.46). */
val COUIIcons.Play: ImageVector
    get() {
        _play?.let { return it }
        return ImageVector.Builder(
            name = "Play",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M8,5v14l11,-7z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _play = it }
    }

private var _play: ImageVector? = null
