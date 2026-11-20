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

/** Pin: ColorOS `drawable/ic_menu_label_pin` (com.coloros.filemanager_16.12.2). */
val COUIIcons.Pin: ImageVector
    get() {
        _pin?.let { return it }
        return ImageVector.Builder(
            name = "Pin",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M5.3,2.7C5.3,2.313 5.613,2 6,2H18C18.386,2 18.7,2.313 18.7,2.7C18.7,3.087 18.386,3.4 18,3.4H16.2V8.331C19.017,10.334 20.7,13.582 20.7,17.052V17.2C20.7,17.587 20.386,17.9 20,17.9H13.153L12.495,22.634C12.415,23.209 11.584,23.209 11.505,22.634L10.847,17.9H4C3.613,17.9 3.3,17.587 3.3,17.2V17.052C3.3,13.582 4.982,10.334 7.8,8.331V3.4H6C5.613,3.4 5.3,3.087 5.3,2.7ZM9.2,3.4H14.8V8.7C14.8,8.934 14.917,9.153 15.111,9.283L15.158,9.314C17.592,10.936 19.111,13.602 19.284,16.5H4.716C4.888,13.602 6.408,10.936 8.841,9.314L8.888,9.283C9.083,9.153 9.2,8.934 9.2,8.7V3.4Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _pin = it }
    }

private var _pin: ImageVector? = null
