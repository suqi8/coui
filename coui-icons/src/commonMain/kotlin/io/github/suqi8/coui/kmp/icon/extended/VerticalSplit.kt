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

/** VerticalSplit: ColorOS `ic_split_screen (rotated 90)` (com.coloros.note_16.7.2). */
val COUIIcons.VerticalSplit: ImageVector
    get() {
        _verticalSplit?.let { return it }
        return ImageVector.Builder(
            name = "VerticalSplit",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
            autoMirror = true,
        ).apply {
            group(rotate = 90f, pivotX = 12f, pivotY = 12f) {
                addPath(
                    pathData = addPathNodes("M2.3,19.699C2.3,20.803 3.195,21.699 4.3,21.699H11.999C11.999,21.699 12,21.699 12,21.699C12,21.699 12.001,21.699 12.001,21.699H19.7C20.805,21.699 21.7,20.803 21.7,19.699V4.299C21.7,3.194 20.805,2.299 19.7,2.299H4.3C3.195,2.299 2.3,3.194 2.3,4.299V19.699ZM12.7,20.299H19.7C20.031,20.299 20.3,20.03 20.3,19.699V4.299C20.3,3.967 20.031,3.699 19.7,3.699L12.7,3.699L12.7,20.299ZM11.3,3.699L4.3,3.699C3.969,3.699 3.7,3.967 3.7,4.299L3.7,19.699C3.7,20.03 3.969,20.299 4.3,20.299H11.3L11.3,3.699Z"),
                    pathFillType = PathFillType.EvenOdd,
                    fill = SolidColor(Color.Black),
                )
            }
        }.build().also { _verticalSplit = it }
    }

private var _verticalSplit: ImageVector? = null
