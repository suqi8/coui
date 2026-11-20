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

/** FileDownloads: ColorOS `drawable/main_category_download_round` (com.coloros.filemanager_16.12.2). */
val COUIIcons.FileDownloads: ImageVector
    get() {
        _fileDownloads?.let { return it }
        return ImageVector.Builder(
            name = "FileDownloads",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M6,0L18,0A6,6 0,0 1,24 6L24,18A6,6 0,0 1,18 24L6,24A6,6 0,0 1,0 18L0,6A6,6 0,0 1,6 0z M12.219,14.147L15.334,11.032C15.53,10.835 15.391,10.499 15.113,10.499H12.623V5.365C12.623,5.193 12.483,5.053 12.311,5.053H11.686C11.513,5.053 11.373,5.193 11.373,5.365V10.499H8.883C8.605,10.499 8.466,10.835 8.662,11.032L11.777,14.147C11.899,14.269 12.097,14.269 12.219,14.147Z M17,14.686V16.062C17,16.165 16.916,16.249 16.813,16.249H7.187C7.084,16.249 7,16.165 7,16.062V14.686C7,14.514 6.86,14.374 6.688,14.374H6.063C5.89,14.374 5.75,14.514 5.75,14.686V16.062C5.75,16.855 6.394,17.499 7.187,17.499H16.813C17.607,17.499 18.25,16.855 18.25,16.062V14.686C18.25,14.514 18.11,14.374 17.938,14.374H17.313C17.14,14.374 17,14.514 17,14.686Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _fileDownloads = it }
    }

private var _fileDownloads: ImageVector? = null
