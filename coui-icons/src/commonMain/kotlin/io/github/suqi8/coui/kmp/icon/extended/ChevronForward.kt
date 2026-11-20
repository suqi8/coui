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

/** ChevronForward: ColorOS `coui_ic_chevron_end` (com.coloros.calendar_17.6.20). */
val COUIIcons.ChevronForward: ImageVector
    get() {
        _chevronForward?.let { return it }
        return ImageVector.Builder(
            name = "ChevronForward",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
            autoMirror = true,
        ).apply {
            addPath(
                pathData = addPathNodes("M10.16,5.636L16.171,11.646C16.366,11.842 16.366,12.158 16.171,12.354L10.16,18.364"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Miter,
            )
        }.build().also { _chevronForward = it }
    }

private var _chevronForward: ImageVector? = null
