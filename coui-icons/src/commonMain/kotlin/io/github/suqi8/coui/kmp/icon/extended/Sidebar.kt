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

/** Sidebar: ColorOS `app_ic_side_pane_control` (com.coloros.gallery3d_16.50.46). */
val COUIIcons.Sidebar: ImageVector
    get() {
        _sidebar?.let { return it }
        return ImageVector.Builder(
            name = "Sidebar",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M3.301,20.701C2.196,20.701 1.301,19.805 1.301,18.701V5.301C1.301,4.196 2.196,3.301 3.301,3.301H20.701C21.805,3.301 22.701,4.196 22.701,5.301V18.701C22.701,19.805 21.805,20.701 20.701,20.701L10.002,20.701H10.001H10H3.301ZM20.701,19.301H10.701L10.701,4.701L20.701,4.701C21.032,4.701 21.301,4.969 21.301,5.301V18.701C21.301,19.032 21.032,19.301 20.701,19.301ZM3.301,4.701L9.301,4.701L9.301,19.301H3.301C2.969,19.301 2.701,19.032 2.701,18.701L2.701,5.301C2.701,4.969 2.969,4.701 3.301,4.701ZM5.001,6.851C4.642,6.851 4.351,7.142 4.351,7.501C4.351,7.86 4.642,8.151 5.001,8.151H7.001C7.36,8.151 7.651,7.86 7.651,7.501C7.651,7.142 7.36,6.851 7.001,6.851H5.001ZM4.351,10.001C4.351,9.642 4.642,9.351 5.001,9.351H7.001C7.36,9.351 7.651,9.642 7.651,10.001C7.651,10.36 7.36,10.651 7.001,10.651H5.001C4.642,10.651 4.351,10.36 4.351,10.001ZM5.001,11.851C4.642,11.851 4.351,12.142 4.351,12.501C4.351,12.86 4.642,13.151 5.001,13.151H7.001C7.36,13.151 7.651,12.86 7.651,12.501C7.651,12.142 7.36,11.851 7.001,11.851H5.001Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _sidebar = it }
    }

private var _sidebar: ImageVector? = null
