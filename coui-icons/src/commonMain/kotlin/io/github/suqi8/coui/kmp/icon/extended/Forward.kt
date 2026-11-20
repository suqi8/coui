// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Forward: ColorOS `mms_dr_menu_forward` (com.android.mms_16.60.10). */
val COUIIcons.Forward: ImageVector
    get() {
        _forward?.let { return it }
        return ImageVector.Builder(
            name = "Forward",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M16,1.3C15.613,1.3 15.3,1.613 15.3,2C15.3,2.387 15.613,2.7 16,2.7H20.31L10.505,12.505C10.232,12.778 10.232,13.222 10.505,13.495C10.778,13.768 11.222,13.768 11.495,13.495L21.3,3.69V8C21.3,8.387 21.613,8.7 22,8.7C22.387,8.7 22.7,8.387 22.7,8V2C22.7,1.613 22.387,1.3 22,1.3H16Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M18.949,11V19.672C18.949,20.459 18.305,21.103 17.518,21.103H3.869C3.082,21.103 2.438,20.459 2.438,19.672V6.023C2.438,5.236 3.082,4.592 3.869,4.592H13"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Round,
            )
        }.build().also { _forward = it }
    }

private var _forward: ImageVector? = null
