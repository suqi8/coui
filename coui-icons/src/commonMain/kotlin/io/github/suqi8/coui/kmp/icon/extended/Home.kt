// Copyright 2026, compose-coui-ui contributors
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

/** Home: ColorOS `ic_location_resident` (com.coloros.alarmclock_16.19.4). */
val COUIIcons.Home: ImageVector
    get() {
        _home?.let { return it }
        return ImageVector.Builder(
            name = "Home",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 20.3f,
            viewportHeight = 20.3f,
        ).apply {
            group(translationX = -3.85f, translationY = -3.71f) {
                group() {
                    group(clipPathData = addPathNodes("M5,5h18v18h-18z")) {
                        addPath(
                            pathData = addPathNodes("M14.328,6.315C14.136,6.162 13.863,6.162 13.672,6.315L6.172,12.315C5.998,12.455 5.93,12.689 6.004,12.899C6.078,13.109 6.277,13.25 6.5,13.25H8V20C8,20.829 8.672,21.5 9.5,21.5H12.48C12.487,21.5 12.493,21.5 12.5,21.5H15.5C15.507,21.5 15.513,21.5 15.52,21.5H18.5C19.703,21.5 20,20.829 20,20V13.25H21.5C21.723,13.25 21.921,13.109 21.995,12.899C22.069,12.689 22.002,12.455 21.828,12.315L14.328,6.315ZM15.5,20.45L15.5,16.1C15.5,15.893 15.333,15.725 15.126,15.725H12.874C12.667,15.725 12.5,15.893 12.5,16.1L12.5,20.45H15.5C15.5,20.45 15.5,20.45 15.5,20.45Z"),
                            pathFillType = PathFillType.EvenOdd,
                            fill = SolidColor(Color.Black),
                        )
                    }
                }
            }
        }.build().also { _home = it }
    }

private var _home: ImageVector? = null
