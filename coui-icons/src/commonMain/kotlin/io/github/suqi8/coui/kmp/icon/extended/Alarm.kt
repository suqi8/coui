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

/** Alarm: ColorOS `drawable/tab_alarm` (com.coloros.alarmclock_16.19.4). */
val COUIIcons.Alarm: ImageVector
    get() {
        _alarm?.let { return it }
        return ImageVector.Builder(
            name = "Alarm",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M16.615,2.884C16.337,2.665 16.328,2.25 16.607,2.08C17.593,1.478 18.994,1.401 20.461,2.55C21.927,3.699 22.205,5.091 21.875,6.206C21.782,6.522 21.381,6.617 21.102,6.399L16.615,2.884ZM7.394,2.08C7.673,2.25 7.665,2.665 7.386,2.884L2.899,6.399C2.62,6.617 2.22,6.522 2.126,6.206C1.797,5.091 2.075,3.699 3.541,2.55C5.007,1.401 6.409,1.478 7.394,2.08ZM3.702,12.65C3.702,8.066 7.418,4.35 12.002,4.35C16.586,4.35 20.302,8.066 20.302,12.65C20.302,17.234 16.586,20.95 12.002,20.95C7.418,20.95 3.702,17.234 3.702,12.65ZM12.002,2.95C6.645,2.95 2.302,7.293 2.302,12.65C2.302,15.028 3.157,17.206 4.578,18.893L3.306,21.118C3.126,21.432 3.179,21.827 3.435,22.083C3.748,22.396 4.256,22.396 4.569,22.083L6.216,20.436C7.831,21.639 9.833,22.35 12.002,22.35C14.17,22.35 16.173,21.639 17.788,20.436L19.435,22.083C19.748,22.396 20.256,22.396 20.569,22.083C20.825,21.827 20.878,21.432 20.698,21.118L19.426,18.893C20.846,17.206 21.702,15.028 21.702,12.65C21.702,7.293 17.359,2.95 12.002,2.95ZM12.149,7.65C11.83,7.65 11.567,7.899 11.549,8.217L11.327,12.213L9.38,14.425C9.168,14.666 9.18,15.03 9.408,15.256C9.633,15.479 9.991,15.491 10.23,15.284L12.816,13.055C12.933,12.954 12.997,12.804 12.989,12.649L12.748,8.218C12.731,7.9 12.468,7.65 12.149,7.65Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _alarm = it }
    }

private var _alarm: ImageVector? = null
