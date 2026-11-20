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

/** Unpin: ColorOS `drawable/ic_menu_label_unpin` (com.coloros.filemanager_16.12.2). */
val COUIIcons.Unpin: ImageVector
    get() {
        _unpin?.let { return it }
        return ImageVector.Builder(
            name = "Unpin",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M1.505,1.813C1.778,1.54 2.221,1.54 2.495,1.813L7.8,7.119V3.508H6C5.613,3.508 5.3,3.195 5.3,2.808C5.3,2.422 5.613,2.108 6,2.108H8.5H15.5H18C18.387,2.108 18.7,2.422 18.7,2.808C18.7,3.195 18.387,3.508 18,3.508H16.2V8.439C19.018,10.442 20.7,13.69 20.7,17.16V17.308C20.7,17.695 20.387,18.008 20,18.008H18.69L22.495,21.813C22.768,22.087 22.768,22.53 22.495,22.803C22.221,23.077 21.778,23.077 21.505,22.803L21.393,22.692C21.383,22.68 21.372,22.668 21.36,22.656L1.472,2.768C1.232,2.493 1.243,2.075 1.505,1.813ZM6.376,9.652C4.425,11.633 3.3,14.322 3.3,17.16V17.308C3.3,17.695 3.613,18.008 4,18.008H10.847L11.505,22.743C11.584,23.318 12.415,23.318 12.495,22.743L13.153,18.008H14.732L13.332,16.608H4.716C4.85,14.351 5.802,12.234 7.366,10.642L6.376,9.652ZM9.2,8.519L17.29,16.608H19.284C19.112,13.71 17.592,11.045 15.159,9.422L15.112,9.391C14.917,9.261 14.8,9.042 14.8,8.808V3.508H9.2V8.519Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _unpin = it }
    }

private var _unpin: ImageVector? = null
