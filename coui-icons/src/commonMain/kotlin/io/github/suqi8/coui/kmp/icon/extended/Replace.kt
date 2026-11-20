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

/** Replace: ColorOS `videoeditor_trim_submenu_replace` (com.coloros.gallery3d_16.50.46). */
val COUIIcons.Replace: ImageVector
    get() {
        _replace?.let { return it }
        return ImageVector.Builder(
            name = "Replace",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 20f,
            viewportHeight = 20f,
        ).apply {
            addPath(
                pathData = addPathNodes("M16.427,4.528C16.427,4.523 16.427,4.518 16.427,4.513V8.712H15.2V14.887H5.011V16.115H4.178L4.196,16.116H16.427V15.472V4.528ZM14.977,5.113H4.775V11.287H3.552V15.475L3.552,15.472C3.552,13.134 3.5,3.884 3.5,3.884H14.977V5.113Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M13.435,6.114L14.346,7.024L15.941,5.429L15.943,5.431L16.853,4.521L14.35,2.017L13.439,2.927L15.031,4.519L13.435,6.114Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M6.561,17.09L5.65,18L4.055,16.405L4.053,16.407L3.143,15.496L5.646,12.993L6.557,13.903L4.966,15.494L6.561,17.09Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _replace = it }
    }

private var _replace: ImageVector? = null
