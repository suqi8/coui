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

/** Copy: ColorOS `note_ic_copy_normal` (com.coloros.note_16.7.2). */
val COUIIcons.Copy: ImageVector
    get() {
        _copy?.let { return it }
        return ImageVector.Builder(
            name = "Copy",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            group() {
                group(clipPathData = addPathNodes("M0,0h24v24h-24z")) {
                    addPath(
                        pathData = addPathNodes("M15.702,21.7C16.807,21.7 17.702,20.805 17.702,19.7V8.3C17.702,7.196 16.807,6.3 15.702,6.3H4.302C3.198,6.3 2.302,7.196 2.302,8.3V19.7C2.302,20.805 3.198,21.7 4.302,21.7H15.702ZM16.302,19.7C16.302,20.032 16.034,20.3 15.702,20.3H4.302C3.971,20.3 3.702,20.032 3.702,19.7V8.3C3.702,7.969 3.971,7.7 4.302,7.7H15.702C16.034,7.7 16.302,7.969 16.302,8.3V19.7ZM20.302,15.7C20.302,16.032 20.034,16.3 19.702,16.3H19.002V17.7H19.702C20.807,17.7 21.702,16.805 21.702,15.7V4.3C21.702,3.196 20.807,2.3 19.702,2.3H8.302C7.198,2.3 6.302,3.196 6.302,4.3V5H7.702V4.3C7.702,3.969 7.971,3.7 8.302,3.7H19.702C20.034,3.7 20.302,3.969 20.302,4.3V15.7Z"),
                        pathFillType = PathFillType.EvenOdd,
                        fill = SolidColor(Color.Black),
                    )
                }
            }
        }.build().also { _copy = it }
    }

private var _copy: ImageVector? = null
