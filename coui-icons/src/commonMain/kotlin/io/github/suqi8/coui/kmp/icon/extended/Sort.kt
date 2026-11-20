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

/** Sort: ColorOS `clear_filter_icon` (com.coloros.phonemanager_17.5.3). */
val COUIIcons.Sort: ImageVector
    get() {
        _sort?.let { return it }
        return ImageVector.Builder(
            name = "Sort",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M19.4245,5V15.254L20.6988,14.0447L21.8002,15.2053L19.1752,17.6964L17.8245,18.9782V17.1161V5H19.4245ZM15.9245,5.0216V6.8837V18.9999H14.3245V8.7458L13.0502,9.9551L11.9488,8.7946L14.5738,6.3034L15.9245,5.0216ZM8.9995,8.425H1.9995V6.825H8.9995V8.425ZM9.8745,12.8H1.9995V11.2H9.8745V12.8ZM1.9995,17.175H10.7495V15.575H1.9995V17.175Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _sort = it }
    }

private var _sort: ImageVector? = null
