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

/** WorldClock: ColorOS `drawable/tab_world_time` (com.coloros.alarmclock_16.19.4). */
val COUIIcons.WorldClock: ImageVector
    get() {
        _worldClock?.let { return it }
        return ImageVector.Builder(
            name = "WorldClock",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M12,2.7C11.204,2.7 10.251,3.45 9.456,5.216C9.067,6.081 8.748,7.127 8.529,8.3H15.47C15.251,7.127 14.933,6.081 14.543,5.216C13.749,3.45 12.795,2.7 12,2.7ZM8.179,4.642C7.711,5.684 7.344,6.927 7.107,8.3H3.465C4.5,5.915 6.506,4.049 8.984,3.2C8.681,3.636 8.412,4.124 8.179,4.642ZM6.916,9.7H2.986C2.799,10.436 2.7,11.206 2.7,12C2.7,12.794 2.799,13.564 2.986,14.3H6.916C6.84,13.558 6.8,12.788 6.8,12C6.8,11.212 6.84,10.442 6.916,9.7ZM7.107,15.7H3.465C4.5,18.085 6.506,19.951 8.984,20.8C8.681,20.364 8.412,19.875 8.179,19.358C7.711,18.316 7.344,17.073 7.107,15.7ZM8.529,15.7H15.47C15.251,16.873 14.933,17.919 14.543,18.784C13.749,20.55 12.795,21.3 12,21.3C11.204,21.3 10.251,20.55 9.456,18.784C9.067,17.919 8.748,16.873 8.529,15.7ZM15.676,14.3H8.323C8.243,13.566 8.2,12.795 8.2,12C8.2,11.205 8.243,10.434 8.323,9.7H15.676C15.757,10.434 15.8,11.205 15.8,12C15.8,12.795 15.757,13.566 15.676,14.3ZM16.892,15.7C16.656,17.073 16.289,18.316 15.82,19.358C15.587,19.875 15.319,20.364 15.015,20.8C17.493,19.951 19.499,18.085 20.535,15.7H16.892ZM21.013,14.3H17.084C17.16,13.558 17.2,12.788 17.2,12C17.2,11.212 17.16,10.442 17.084,9.7H21.013C21.2,10.436 21.3,11.206 21.3,12C21.3,12.794 21.2,13.564 21.013,14.3ZM15.015,3.2C17.493,4.049 19.499,5.915 20.535,8.3H16.892C16.656,6.927 16.289,5.684 15.82,4.642C15.587,4.124 15.319,3.636 15.015,3.2ZM12,22.7C17.909,22.7 22.7,17.909 22.7,12C22.7,6.091 17.909,1.3 12,1.3C6.09,1.3 1.3,6.091 1.3,12C1.3,17.909 6.09,22.7 12,22.7Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _worldClock = it }
    }

private var _worldClock: ImageVector? = null
