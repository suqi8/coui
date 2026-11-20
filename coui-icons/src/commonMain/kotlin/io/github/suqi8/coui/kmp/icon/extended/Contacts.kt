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

/** Contacts: ColorOS `ic_edit_go_contact` (com.coloros.calendar_17.6.20). */
val COUIIcons.Contacts: ImageVector
    get() {
        _contacts?.let { return it }
        return ImageVector.Builder(
            name = "Contacts",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M20.938,19.998L19.868,16.786C19.32,15.139 17.508,13.998 15.441,13.998H8.551C6.484,13.998 4.672,15.139 4.124,16.786L3.055,19.998H20.938ZM11.996,10.65C14.012,10.65 15.646,9.015 15.646,7C15.646,4.984 14.012,3.35 11.996,3.35C9.98,3.35 8.346,4.984 8.346,7C8.346,9.015 9.98,10.65 11.996,10.65Z"),
                pathFillType = PathFillType.EvenOdd,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }.build().also { _contacts = it }
    }

private var _contacts: ImageVector? = null
