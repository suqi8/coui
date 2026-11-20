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

/** Stopwatch: ColorOS `drawable/tab_stopwatch` (com.coloros.alarmclock_16.19.4). */
val COUIIcons.Stopwatch: ImageVector
    get() {
        _stopwatch?.let { return it }
        return ImageVector.Builder(
            name = "Stopwatch",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M10.499,0.925C10.113,0.925 9.799,1.238 9.799,1.625C9.799,2.012 10.113,2.325 10.499,2.325H13.499C13.886,2.325 14.199,2.012 14.199,1.625C14.199,1.238 13.886,0.925 13.499,0.925H10.499ZM3.45,13.125C3.45,8.403 7.278,4.575 12,4.575C16.722,4.575 20.55,8.403 20.55,13.125C20.55,17.847 16.722,21.675 12,21.675C7.278,21.675 3.45,17.847 3.45,13.125ZM12,3.175C6.505,3.175 2.05,7.63 2.05,13.125C2.05,18.62 6.505,23.075 12,23.075C17.495,23.075 21.95,18.62 21.95,13.125C21.95,7.63 17.495,3.175 12,3.175ZM11.36,7.095C11.373,6.751 11.655,6.478 11.999,6.478C12.344,6.478 12.626,6.751 12.639,7.095L12.89,13.76C12.909,14.265 12.505,14.685 12,14.685C11.494,14.685 11.09,14.265 11.109,13.76L11.36,7.095ZM20.421,4.112C20.148,3.839 19.705,3.839 19.432,4.112C19.158,4.386 19.158,4.829 19.432,5.102L20.022,5.693C20.295,5.966 20.739,5.966 21.012,5.693C21.285,5.419 21.285,4.976 21.012,4.703L20.421,4.112Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _stopwatch = it }
    }

private var _stopwatch: ImageVector? = null
