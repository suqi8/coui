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

/** ExpandLess: ColorOS `coui_line_arrow_expanded` (com.coloros.calendar_17.6.20). */
val COUIIcons.ExpandLess: ImageVector
    get() {
        _expandLess?.let { return it }
        return ImageVector.Builder(
            name = "ExpandLess",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            group(translationX = 4f, translationY = 0f) {
                addPath(
                    pathData = addPathNodes("M8.595,10.257C8.751,10.099 8.829,10.019 8.921,9.997C8.973,9.984 9.027,9.984 9.079,9.997C9.171,10.019 9.249,10.099 9.405,10.257L15.035,16L16,15.025L9.795,8.798C9.489,8.491 9.336,8.337 9.154,8.293C9.053,8.269 8.947,8.269 8.846,8.293C8.664,8.337 8.511,8.491 8.205,8.798L2,15.025L2.966,16L8.595,10.257Z"),
                    pathFillType = PathFillType.EvenOdd,
                    fill = SolidColor(Color.Black),
                )
            }
        }.build().also { _expandLess = it }
    }

private var _expandLess: ImageVector? = null
