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

/** Mic: ColorOS `coui_search_view_voice_icon_normal` (settings_17.0.0). */
val COUIIcons.Mic: ImageVector
    get() {
        _mic?.let { return it }
        return ImageVector.Builder(
            name = "Mic",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 20f,
            viewportHeight = 20f,
        ).apply {
            addPath(
                pathData = addPathNodes("M5.967,4.583C5.967,2.356 7.773,0.55 10.001,0.55C12.228,0.55 14.034,2.356 14.034,4.583V9.583C14.034,11.811 12.228,13.617 10.001,13.617C7.773,13.617 5.967,11.811 5.967,9.583V4.583ZM10.001,1.95C8.546,1.95 7.367,3.129 7.367,4.583V9.583C7.367,11.038 8.546,12.217 10.001,12.217C11.455,12.217 12.634,11.038 12.634,9.583V4.583C12.634,3.129 11.455,1.95 10.001,1.95ZM9.3,16.08C6.021,15.73 3.467,12.955 3.467,9.583C3.467,9.197 3.78,8.883 4.167,8.883C4.553,8.883 4.867,9.197 4.867,9.583C4.867,12.418 7.165,14.717 10,14.717C12.835,14.717 15.134,12.418 15.134,9.583C15.134,9.197 15.447,8.883 15.833,8.883C16.22,8.883 16.534,9.197 16.534,9.583C16.534,12.955 13.979,15.73 10.7,16.08V18.05H12.5C12.887,18.05 13.2,18.363 13.2,18.75C13.2,19.137 12.887,19.45 12.5,19.45L10,19.45L7.5,19.45C7.114,19.45 6.8,19.137 6.8,18.75C6.8,18.363 7.114,18.05 7.5,18.05H9.3V16.08Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _mic = it }
    }

private var _mic: ImageVector? = null
