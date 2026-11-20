// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** UploadCloud: ColorOS `drawable/color_tool_menu_ic_upload_cloud` (com.coloros.filemanager_16.12.2). */
val COUIIcons.UploadCloud: ImageVector
    get() {
        _uploadCloud?.let { return it }
        return ImageVector.Builder(
            name = "UploadCloud",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M12,22L12,10"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M6.211,9.632C6.4,6.599 8.92,4.198 12,4.198C15.08,4.198 17.6,6.599 17.788,9.632C17.809,9.953 18.045,10.219 18.362,10.277C20.602,10.683 22.3,12.644 22.3,15C22.3,17.651 20.151,19.8 17.5,19.8H14V21.2H17.5C20.924,21.2 23.7,18.424 23.7,15C23.7,12.141 21.765,9.735 19.134,9.017C18.655,5.505 15.644,2.798 12,2.798C8.356,2.798 5.345,5.505 4.866,9.017C2.235,9.735 0.3,12.141 0.3,15C0.3,18.424 3.076,21.2 6.5,21.2H10V19.8H6.5C3.849,19.8 1.7,17.651 1.7,15C1.7,12.644 3.398,10.683 5.637,10.277C5.954,10.219 6.191,9.953 6.211,9.632Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M15,12L12,9L9,12"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }.build().also { _uploadCloud = it }
    }

private var _uploadCloud: ImageVector? = null
