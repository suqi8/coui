// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Remove: ColorOS `ic_guest_exit` (settings_17.0.0). */
val COUIIcons.Remove: ImageVector
    get() {
        _remove?.let { return it }
        return ImageVector.Builder(
            name = "Remove",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M3,13v-2h8.65L9.1,8.45 10.5,7l5,5 -5,5 -1.4,-1.45L11.65,13zM5,15v4h14L19,5L5,5v4L3,9L3,5q0,-0.825 0.587,-1.413Q4.175,3 5,3h14q0.825,0 1.413,0.587Q21,4.175 21,5v14q0,0.825 -0.587,1.413Q19.825,21 19,21L5,21q-0.825,0 -1.413,-0.587Q3,19.825 3,19v-4z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _remove = it }
    }

private var _remove: ImageVector? = null
