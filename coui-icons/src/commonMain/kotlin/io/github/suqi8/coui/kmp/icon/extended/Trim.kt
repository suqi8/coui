// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Trim: ColorOS `base_ic_video_trim_normal` (com.coloros.gallery3d_16.50.46). */
val COUIIcons.Trim: ImageVector
    get() {
        _trim?.let { return it }
        return ImageVector.Builder(
            name = "Trim",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            group() {
                group(clipPathData = addPathNodes("M0.801,0.747h24v24h-24z")) {
                    addPath(
                        pathData = addPathNodes("M22.297,6.078C22.372,6.153 22.364,6.278 22.28,6.343L12.516,13.878L9.278,16.137C9.3,16.157 9.322,16.178 9.343,16.199C10.621,17.477 10.622,19.551 9.344,20.829C8.065,22.107 5.992,22.107 4.713,20.828C3.435,19.55 3.435,17.477 4.713,16.199C5.286,15.627 6.018,15.313 6.766,15.253C8.715,14.879 9.995,13.548 10.966,11.969C11.035,11.856 11.124,11.757 11.229,11.679L19.069,5.843C20.054,5.11 21.428,5.209 22.297,6.078ZM8.252,17.29C7.576,16.614 6.481,16.614 5.805,17.289C5.13,17.965 5.13,19.061 5.805,19.737C6.481,20.412 7.576,20.411 8.252,19.736C8.927,19.06 8.927,17.966 8.252,17.29ZM9.345,4.659C10.623,5.938 10.623,8.01 9.345,9.289C9.324,9.31 9.301,9.331 9.279,9.352L10.661,10.315L10.392,10.516C10.121,10.718 9.902,10.966 9.737,11.233C9.651,11.373 9.562,11.508 9.473,11.638C8.738,10.953 7.855,10.444 6.763,10.236C6.016,10.175 5.287,9.86 4.716,9.289C3.437,8.01 3.437,5.938 4.716,4.659C5.994,3.381 8.067,3.381 9.345,4.659ZM8.252,5.75C7.577,5.076 6.482,5.076 5.807,5.75C5.131,6.426 5.131,7.521 5.806,8.197C6.482,8.873 7.578,8.873 8.253,8.197C8.928,7.521 8.928,6.426 8.252,5.75ZM22.282,19.146C22.366,19.211 22.374,19.336 22.299,19.411C21.43,20.28 20.056,20.379 19.071,19.646L13.111,15.209L13.366,15.032L15.158,13.648L22.282,19.146ZM12.607,12.165C12.392,11.951 12.043,11.95 11.828,12.165C11.613,12.381 11.613,12.729 11.828,12.944C12.043,13.159 12.392,13.16 12.608,12.945C12.823,12.73 12.822,12.381 12.607,12.165Z"),
                        fill = SolidColor(Color.Black),
                    )
                }
            }
        }.build().also { _trim = it }
    }

private var _trim: ImageVector? = null
