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

/** ExpandMore: ColorOS `coui_line_arrow_collapsed` (com.coloros.calendar_17.6.20). */
val COUIIcons.ExpandMore: ImageVector
    get() {
        _expandMore?.let { return it }
        return ImageVector.Builder(
            name = "ExpandMore",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            group(translationX = 4f, translationY = 0f) {
                addPath(
                    pathData = addPathNodes("M8.595,13.743C8.751,13.901 8.829,13.981 8.921,14.003C8.973,14.016 9.027,14.016 9.079,14.003C9.171,13.981 9.249,13.901 9.405,13.743L15.035,8L16,8.975L9.795,15.202C9.489,15.509 9.336,15.663 9.154,15.707C9.053,15.731 8.947,15.731 8.846,15.707C8.664,15.663 8.511,15.509 8.205,15.202L2,8.975L2.966,8L8.595,13.743Z"),
                    pathFillType = PathFillType.EvenOdd,
                    fill = SolidColor(Color.Black),
                )
            }
        }.build().also { _expandMore = it }
    }

private var _expandMore: ImageVector? = null
