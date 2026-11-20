// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Timer: ColorOS `drawable/tab_timer` (com.coloros.alarmclock_16.19.4). */
val COUIIcons.Timer: ImageVector
    get() {
        _timer?.let { return it }
        return ImageVector.Builder(
            name = "Timer",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M5.2,2.3H18.8V3.551C18.8,4.613 18.378,5.632 17.626,6.382L6.374,17.618C5.622,18.368 5.2,19.387 5.2,20.449L5.2,21.7H18.8V20.449C18.8,19.387 18.378,18.368 17.626,17.618L6.374,6.382C5.622,5.632 5.2,4.613 5.2,3.551L5.2,2.3Z"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }.build().also { _timer = it }
    }

private var _timer: ImageVector? = null
