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

/** Photos: ColorOS `app_ic_tab_photo` (com.coloros.gallery3d_16.50.46). */
val COUIIcons.Photos: ImageVector
    get() {
        _photos?.let { return it }
        return ImageVector.Builder(
            name = "Photos",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M3.705,4.3C3.705,3.969 3.973,3.7 4.305,3.7H19.705C20.036,3.7 20.305,3.969 20.305,4.3V11.675C18.358,11.943 16.091,12.48 12.512,13.683L12.512,13.683L12.503,13.686L12.361,13.736L12.361,13.736L12.36,13.736C11.918,13.891 11.455,14.054 10.968,14.227C8.525,13.068 6.036,11.975 3.705,11.544V4.3ZM3.705,12.97V19.7C3.705,20.031 3.973,20.3 4.305,20.3H19.705C20.036,20.3 20.305,20.031 20.305,19.7V19.458C17.787,18.995 15.102,17.763 12.497,16.512C12.226,16.382 11.956,16.253 11.687,16.124L11.684,16.122L11.683,16.122C8.87,14.772 6.169,13.475 3.705,12.97ZM20.305,18.032V13.089C18.512,13.35 16.365,13.866 12.962,15.009L12.825,15.057L12.752,15.082C12.869,15.138 12.985,15.194 13.101,15.249L13.103,15.25C15.646,16.471 18.08,17.576 20.305,18.032ZM4.305,2.3C3.2,2.3 2.305,3.195 2.305,4.3V19.7C2.305,20.805 3.2,21.7 4.305,21.7H19.705C20.809,21.7 21.705,20.805 21.705,19.7V4.3C21.705,3.195 20.809,2.3 19.705,2.3H4.305ZM15.505,9.25C16.195,9.25 16.755,8.69 16.755,8C16.755,7.31 16.195,6.75 15.505,6.75C14.814,6.75 14.255,7.31 14.255,8C14.255,8.69 14.814,9.25 15.505,9.25Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _photos = it }
    }

private var _photos: ImageVector? = null
