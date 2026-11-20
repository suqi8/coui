// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Hide: ColorOS `coui_btn_eye_off` (settings_17.0.0). */
val COUIIcons.Hide: ImageVector
    get() {
        _hide?.let { return it }
        return ImageVector.Builder(
            name = "Hide",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 27.625f,
            viewportHeight = 27.625f,
        ).apply {
            group(translationX = -3.1875f, translationY = -3.1875f) {
                addPath(
                    pathData = addPathNodes("M6.706,6.734C6.315,7.124 6.315,7.757 6.706,8.148L10.245,11.686C8.702,12.992 7.263,14.773 6,17.028C10.299,24.705 16.652,26.882 22.115,23.557L25.824,27.266C26.215,27.657 26.848,27.657 27.239,27.266C27.629,26.876 27.629,26.243 27.239,25.852L8.12,6.734C7.73,6.343 7.096,6.343 6.706,6.734ZM19.772,21.214L18.306,19.748C17.911,19.939 17.468,20.046 16.999,20.046C15.333,20.046 13.983,18.695 13.983,17.029C13.983,16.561 14.09,16.118 14.28,15.722L12.814,14.256C12.286,15.051 11.979,16.004 11.979,17.029C11.979,19.802 14.227,22.049 16.999,22.049C18.024,22.049 18.978,21.742 19.772,21.214ZM16.819,14.019L20.01,17.209C20.013,17.15 20.015,17.09 20.015,17.029C20.015,15.364 18.665,14.013 16.999,14.013C16.939,14.013 16.879,14.015 16.819,14.019ZM22.019,17.029C22.019,17.679 21.896,18.3 21.671,18.87L24.496,21.696C25.759,20.473 26.941,18.917 27.999,17.028C23.937,9.774 18.044,7.431 12.798,9.997L15.158,12.358C15.728,12.133 16.349,12.009 16.999,12.009C19.771,12.009 22.019,14.257 22.019,17.029Z"),
                    pathFillType = PathFillType.EvenOdd,
                    fill = SolidColor(Color.Black),
                )
            }
        }.build().also { _hide = it }
    }

private var _hide: ImageVector? = null
