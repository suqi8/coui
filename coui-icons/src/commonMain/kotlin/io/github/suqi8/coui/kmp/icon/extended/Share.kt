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

/** Share: ColorOS `menu_ic_about_share_normal` (settings_17.0.0). */
val COUIIcons.Share: ImageVector
    get() {
        _share?.let { return it }
        return ImageVector.Builder(
            name = "Share",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M16.93 4.93c-1.01 0-1.83 0.81-1.83 1.82 0 1 0.82 1.83 1.83 1.83 1 0 1.82-0.82 1.82-1.83 0-1-0.82-1.82-1.82-1.82ZM13.5 6.75c0-1.9 1.53-3.42 3.43-3.42 1.89 0 3.42 1.53 3.42 3.42 0 1.9-1.53 3.43-3.43 3.43-1.1 0-2.07-0.52-2.7-1.32l-4.43 2.5c0.04 0.2 0.06 0.42 0.06 0.64 0 0.22-0.02 0.44-0.06 0.65l4.44 2.5c0.62-0.81 1.6-1.32 2.7-1.32 1.89 0 3.42 1.53 3.42 3.42 0 1.9-1.53 3.43-3.43 3.43-1.89 0-3.42-1.54-3.42-3.43 0-0.22 0.02-0.44 0.06-0.65l-4.44-2.49c-0.62 0.8-1.6 1.32-2.7 1.32C4.54 15.43 3 13.89 3 12c0-1.9 1.53-3.42 3.42-3.42 1.1 0 2.08 0.51 2.7 1.31l4.44-2.5c-0.04-0.2-0.06-0.42-0.06-0.64Zm-7.08 3.43c-1 0-1.82 0.81-1.82 1.82 0 1 0.82 1.83 1.83 1.83 1 0 1.82-0.82 1.82-1.83 0-1-0.82-1.82-1.83-1.82Zm8.68 7.07c0-1 0.82-1.82 1.83-1.82 1 0 1.82 0.81 1.82 1.82 0 1-0.82 1.83-1.82 1.83-1.01 0-1.83-0.82-1.83-1.83Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _share = it }
    }

private var _share: ImageVector? = null
