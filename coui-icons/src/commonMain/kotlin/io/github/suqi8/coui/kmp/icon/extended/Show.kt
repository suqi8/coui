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

/** Show: ColorOS `coui_btn_eye_on` (settings_17.0.0). */
val COUIIcons.Show: ImageVector
    get() {
        _show?.let { return it }
        return ImageVector.Builder(
            name = "Show",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 27.625f,
            viewportHeight = 27.625f,
        ).apply {
            group(translationX = -3.1875f, translationY = -3.1875f) {
                addPath(
                    pathData = addPathNodes("M6,17.001C12,27.716 21.999,27.716 27.999,17.001C21.999,6.286 12,6.286 6,17.001ZM13.983,17.003C13.983,15.337 15.333,13.987 16.999,13.987C18.665,13.987 20.015,15.337 20.015,17.003C20.015,18.669 18.665,20.019 16.999,20.019C15.333,20.019 13.983,18.669 13.983,17.003ZM16.999,11.983C14.227,11.983 11.979,14.231 11.979,17.003C11.979,19.775 14.227,22.023 16.999,22.023C19.771,22.023 22.019,19.775 22.019,17.003C22.019,14.231 19.771,11.983 16.999,11.983Z"),
                    pathFillType = PathFillType.EvenOdd,
                    fill = SolidColor(Color.Black),
                )
            }
        }.build().also { _show = it }
    }

private var _show: ImageVector? = null
