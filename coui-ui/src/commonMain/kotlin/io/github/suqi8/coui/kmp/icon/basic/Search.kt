// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Search: ColorOS `coui_search_view_icon_normal` (settings_17.0.0). */
val COUIIcons.Basic.Search: ImageVector
    get() {
        _search?.let { return it }
        return ImageVector.Builder(
            name = "Search",
            defaultWidth = 20.dp,
            defaultHeight = 20.dp,
            viewportWidth = 20f,
            viewportHeight = 20f,
        ).apply {
            addPath(
                pathData = addPathNodes("M2.822,8.191C2.822,5.226 5.226,2.822 8.191,2.822C11.156,2.822 13.56,5.226 13.56,8.191C13.56,11.156 11.156,13.56 8.191,13.56C5.226,13.56 2.822,11.156 2.822,8.191ZM8.191,1.422C4.452,1.422 1.422,4.452 1.422,8.191C1.422,11.929 4.452,14.96 8.191,14.96C9.735,14.96 11.159,14.442 12.298,13.571L17.075,18.348C17.426,18.699 17.996,18.699 18.347,18.348C18.699,17.997 18.699,17.427 18.347,17.075L13.571,12.299C14.442,11.16 14.96,9.736 14.96,8.191C14.96,4.452 11.929,1.422 8.191,1.422Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _search = it }
    }

private var _search: ImageVector? = null
