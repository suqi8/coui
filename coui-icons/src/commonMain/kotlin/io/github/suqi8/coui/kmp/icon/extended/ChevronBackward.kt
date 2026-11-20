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

/** ChevronBackward: ColorOS `coui_ic_chevron_start` (com.coloros.calendar_17.6.20). */
val COUIIcons.ChevronBackward: ImageVector
    get() {
        _chevronBackward?.let { return it }
        return ImageVector.Builder(
            name = "ChevronBackward",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
            autoMirror = true,
        ).apply {
            addPath(
                pathData = addPathNodes("M13.84,5.636L7.829,11.646C7.634,11.842 7.634,12.158 7.829,12.354L13.84,18.364"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Miter,
            )
        }.build().also { _chevronBackward = it }
    }

private var _chevronBackward: ImageVector? = null
