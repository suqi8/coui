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

/** Close2: ColorOS `breathinglight_multi_led_power` (settings_17.0.0). */
val COUIIcons.Close2: ImageVector
    get() {
        _close2?.let { return it }
        return ImageVector.Builder(
            name = "Close2",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24.525f,
            viewportHeight = 24.525f,
        ).apply {
            group(translationX = -5.7375f, translationY = -5.6475f) {
                addPath(
                    pathData = addPathNodes("M17,8.2002V15.2002H19V8.2002H17ZM12.0164,15.3172C12.809,13.9164 14.068,12.8381 15.5739,12.2701L14.8681,10.3988C12.9241,11.132 11.2989,12.524 10.2758,14.3323C9.2526,16.1406 8.8963,18.2506 9.2689,20.2946C9.6416,22.3386 10.7195,24.1871 12.315,25.518C13.9105,26.8489 15.9223,27.5778 18,27.5778C20.0777,27.5778 22.0896,26.8489 23.685,25.518C25.2805,24.1871 26.3585,22.3386 26.7311,20.2946C27.1037,18.2506 26.7474,16.1406 25.7243,14.3323C24.7011,12.524 23.0759,11.132 21.1319,10.3988L20.4261,12.2701C21.9321,12.8381 23.191,13.9164 23.9836,15.3172C24.7762,16.718 25.0522,18.3525 24.7635,19.9359C24.4749,21.5193 23.6398,22.9512 22.4039,23.9822C21.168,25.0132 19.6095,25.5778 18,25.5778C16.3905,25.5778 14.8321,25.0132 13.5961,23.9822C12.3602,22.9512 11.5252,21.5193 11.2365,19.9359C10.9478,18.3525 11.2238,16.718 12.0164,15.3172Z"),
                    pathFillType = PathFillType.EvenOdd,
                    fill = SolidColor(Color.Black),
                )
            }
        }.build().also { _close2 = it }
    }

private var _close2: ImageVector? = null
