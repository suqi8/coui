// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Sidebar: ColorOS `coui_pane_fold_normal` (com.coloros.calendar_17.6.20). */
val COUIIcons.Basic.Sidebar: ImageVector
    get() {
        _sidebar?.let { return it }
        return ImageVector.Builder(
            name = "Sidebar",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            group(translationX = -12f, translationY = -14f) {
                addPath(
                    pathData = addPathNodes("M14,32.7L14,19.3A1.3,1.3 0,0 1,15.3 18L32.7,18A1.3,1.3 0,0 1,34 19.3L34,32.7A1.3,1.3 135,0 1,32.7 34L15.3,34A1.3,1.3 135,0 1,14 32.7z"),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.4f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                )
                addPath(
                    pathData = addPathNodes("M22,34L22,18"),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.4f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                )
            }
        }.build().also { _sidebar = it }
    }

private var _sidebar: ImageVector? = null
