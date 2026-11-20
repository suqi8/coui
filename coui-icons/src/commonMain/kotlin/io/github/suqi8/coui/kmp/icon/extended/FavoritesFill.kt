// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** FavoritesFill: ColorOS `drawable/base_ic_favorite_filled_normal` (com.coloros.gallery3d_16.50.46). */
val COUIIcons.FavoritesFill: ImageVector
    get() {
        _favoritesFill?.let { return it }
        return ImageVector.Builder(
            name = "FavoritesFill",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M17.042,2.426C17.802,2.426 18.554,2.58 19.253,2.877C19.952,3.174 20.583,3.61 21.11,4.157C22.202,5.292 22.782,7.012 22.782,8.528C22.782,10.352 22.221,11.68 21.168,13.098C20.472,14.036 18.442,16.125 16.551,17.945C15.596,18.864 14.658,19.732 13.914,20.374C13.544,20.694 13.212,20.965 12.946,21.16C12.815,21.257 12.686,21.344 12.569,21.411C12.511,21.444 12.442,21.48 12.369,21.509C12.308,21.533 12.186,21.576 12.037,21.576C11.882,21.576 11.756,21.529 11.696,21.505C11.623,21.475 11.555,21.438 11.496,21.404C11.378,21.335 11.246,21.245 11.11,21.145C10.837,20.941 10.495,20.657 10.111,20.325C9.342,19.658 8.371,18.76 7.393,17.821C6.413,16.882 5.419,15.896 4.602,15.055C3.804,14.233 3.141,13.514 2.84,13.11C1.708,11.684 1.219,10.129 1.219,8.531C1.219,7.015 1.799,5.295 2.891,4.16C3.418,3.613 4.05,3.177 4.749,2.88C5.448,2.582 6.2,2.429 6.96,2.429C7.72,2.429 8.472,2.582 9.171,2.88C9.869,3.177 10.501,3.612 11.027,4.158L11.029,4.16L12.024,5.182L12.973,4.157C13.5,3.61 14.131,3.174 14.83,2.877C15.529,2.579 16.282,2.426 17.042,2.426Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _favoritesFill = it }
    }

private var _favoritesFill: ImageVector? = null
