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

/** File: ColorOS `drawable/picture3d_editor_ic_enhance_text_convert_doc` (com.coloros.gallery3d_16.50.46). */
val COUIIcons.File: ImageVector
    get() {
        _file?.let { return it }
        return ImageVector.Builder(
            name = "File",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M4.2 2.45H5h13.12c0.45 0 0.87 0.18 1.19 0.5c0.31 0.3 0.49 0.73 0.49 1.18v12.25v0.87c0 1.14-0.46 2.23-1.26 3.04c-0.8 0.8-1.9 1.26-3.04 1.26h-0.87h-8.76c-0.44 0-0.87-0.18-1.18-0.49c-0.31-0.31-0.5-0.74-0.5-1.18V3.25v-0.8zm11.22 17.5h0.08c0.71 0 1.4-0.29 1.9-0.8c0.51-0.5 0.8-1.19 0.8-1.9v-0.07h-2.78v2.77zm2.78-4.37h-3.58h-0.8v0.8v3.57H5.87c-0.02 0-0.03 0-0.05-0.02L5.8 19.88V4.05h12.32c0.02 0 0.04 0 0.06 0.02l0.02 0.06v11.45zm-2.7-3.65h-7v-1.6h7v1.6zm-7-3.5h6.12v-1.6H8.5v1.6zm3.5 7H8.5v-1.6H12v1.6z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _file = it }
    }

private var _file: ImageVector? = null
