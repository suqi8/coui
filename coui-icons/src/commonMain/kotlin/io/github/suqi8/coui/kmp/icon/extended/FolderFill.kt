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

/** FolderFill: ColorOS `drawable/editor_extra_insert_file_fg` (com.coloros.note_16.7.2). */
val COUIIcons.FolderFill: ImageVector
    get() {
        _folderFill?.let { return it }
        return ImageVector.Builder(
            name = "FolderFill",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 28f,
            viewportHeight = 28f,
            autoMirror = true,
        ).apply {
            addPath(
                pathData = addPathNodes("M5.501,3.23C4.213,3.23 3.168,4.275 3.168,5.564V7.062C3.593,6.9 4.054,6.812 4.537,6.812H23.463C23.94,6.812 24.396,6.898 24.816,7.055C24.634,5.947 23.672,5.102 22.513,5.102H13.263L11.674,3.773C11.254,3.423 10.724,3.23 10.177,3.23H5.501ZM5.486,8.328L5.518,8.328H22.483L22.515,8.328C22.942,8.328 23.312,8.328 23.615,8.354C23.936,8.381 24.26,8.441 24.569,8.607C25.024,8.852 25.385,9.241 25.595,9.712C25.738,10.033 25.773,10.361 25.776,10.682C25.779,10.987 25.751,11.356 25.719,11.782L25.719,11.782L25.717,11.814L24.971,21.769L24.969,21.798C24.941,22.169 24.917,22.493 24.875,22.761C24.831,23.045 24.759,23.33 24.602,23.603C24.367,24.009 24.016,24.335 23.593,24.539C23.31,24.676 23.02,24.726 22.733,24.749C22.463,24.77 22.138,24.77 21.766,24.77H21.737H6.264H6.235C5.863,24.77 5.538,24.77 5.268,24.749C4.981,24.726 4.691,24.676 4.408,24.539C3.985,24.335 3.634,24.009 3.399,23.603C3.242,23.33 3.17,23.045 3.126,22.761C3.084,22.493 3.06,22.169 3.032,21.798L3.03,21.769L2.284,11.814L2.282,11.782C2.25,11.356 2.222,10.987 2.225,10.682C2.228,10.361 2.263,10.033 2.406,9.712C2.616,9.241 2.977,8.852 3.432,8.607C3.741,8.441 4.065,8.381 4.386,8.354C4.689,8.328 5.059,8.328 5.486,8.328Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _folderFill = it }
    }

private var _folderFill: ImageVector? = null
