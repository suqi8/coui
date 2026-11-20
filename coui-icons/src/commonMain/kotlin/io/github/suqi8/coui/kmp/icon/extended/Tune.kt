// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Tune: ColorOS `ic_play_setting` (com.coloros.soundrecorder_16.17.8). */
val COUIIcons.Tune: ImageVector
    get() {
        _tune?.let { return it }
        return ImageVector.Builder(
            name = "Tune",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M9.001,13.3C10.805,13.3 12.307,14.591 12.634,16.3H22.001C22.388,16.3 22.701,16.614 22.701,17C22.701,17.386 22.387,17.7 22.001,17.7H12.634C12.306,19.409 10.805,20.7 9.001,20.7C7.197,20.7 5.696,19.409 5.368,17.7H2.001C1.614,17.7 1.301,17.386 1.301,17C1.301,16.613 1.614,16.3 2.001,16.3H5.367C5.694,14.591 7.197,13.3 9.001,13.3ZM9.001,14.7C7.731,14.7 6.701,15.73 6.701,17C6.701,18.27 7.731,19.3 9.001,19.3C10.271,19.3 11.301,18.27 11.301,17C11.301,15.73 10.271,14.7 9.001,14.7Z"),
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M15.001,3.3C16.805,3.3 18.306,4.591 18.634,6.3H22.001C22.388,6.3 22.701,6.613 22.701,7C22.701,7.386 22.387,7.7 22.001,7.7H18.634C18.306,9.409 16.805,10.7 15.001,10.7C13.197,10.7 11.696,9.409 11.368,7.7H2.001C1.614,7.7 1.301,7.387 1.301,7C1.301,6.613 1.614,6.3 2.001,6.3H11.367C11.694,4.591 13.197,3.3 15.001,3.3ZM15.001,4.7C13.731,4.7 12.701,5.73 12.701,7C12.701,8.27 13.731,9.3 15.001,9.3C16.271,9.3 17.301,8.27 17.301,7C17.301,5.73 16.271,4.7 15.001,4.7Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _tune = it }
    }

private var _tune: ImageVector? = null
