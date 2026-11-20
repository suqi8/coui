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

/** Clear: ColorOS `de_toolkit_stroke_eraser_icon_pop` (com.coloros.note_16.7.2). */
val COUIIcons.Clear: ImageVector
    get() {
        _clear?.let { return it }
        return ImageVector.Builder(
            name = "Clear",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
            autoMirror = true,
        ).apply {
            addPath(
                pathData = addPathNodes("M6.869,10.566L15.346,2.328L22.27,9.73L14.032,18.207L6.869,10.566Z"),
                fill = SolidColor(Color.Black),
                fillAlpha = 0.0889f,
            )
            addPath(
                pathData = addPathNodes("M21.388,22.349H6.781C6.39,22.349 6.016,22.191 5.744,21.912L1.839,17.906C1.293,17.346 1.29,16.454 1.832,15.891L14.49,2.731C15.058,2.139 16.004,2.138 16.574,2.727L22.175,8.514C22.716,9.074 22.719,9.962 22.18,10.524L10.844,22.349M14.358,18.596L6.782,10.765"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }.build().also { _clear = it }
    }

private var _clear: ImageVector? = null
