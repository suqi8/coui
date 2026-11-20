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

/** Paste: ColorOS `note_ic_paste` (com.coloros.note_16.7.2). */
val COUIIcons.Paste: ImageVector
    get() {
        _paste?.let { return it }
        return ImageVector.Builder(
            name = "Paste",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 28.125f,
            viewportHeight = 28.125f,
        ).apply {
            group(translationX = 2.7825f, translationY = 2.0625f) {
                group() {
                    group(clipPathData = addPathNodes("M0,0.5h24v24h-24z")) {
                        addPath(
                            pathData = addPathNodes("M7.459,4.2C7.356,4.582 7.301,4.985 7.301,5.4V5.5C7.301,5.886 7.614,6.2 8.001,6.2H16.001C16.387,6.2 16.701,5.886 16.701,5.5V5.4C16.701,4.985 16.646,4.582 16.543,4.2H19.301V18.565C19.301,21.128 17.645,21.8 17.045,21.8H3.966C4.362,21.074 4.702,20.023 4.702,18.565V4.2H7.459ZM8.106,2.8H4.002C3.615,2.8 3.302,3.113 3.302,3.5V18.565C3.302,19.915 2.96,20.772 2.654,21.274C2.499,21.528 2.348,21.699 2.245,21.801C2.193,21.852 2.152,21.886 2.129,21.904C2.118,21.913 2.111,21.918 2.109,21.92C1.856,22.091 1.743,22.407 1.83,22.7C1.919,22.997 2.191,23.2 2.501,23.2H17.045C18.416,23.2 20.701,21.904 20.701,18.565V3.5C20.701,3.113 20.388,2.8 20.001,2.8H15.896C15.067,1.592 13.677,0.8 12.101,0.8H11.901C10.325,0.8 8.935,1.592 8.106,2.8ZM15.245,4.8H8.757C9.038,3.319 10.339,2.2 11.901,2.2H12.101C13.663,2.2 14.964,3.319 15.245,4.8ZM12.701,9.5C12.701,9.113 12.387,8.8 12.001,8.8C11.614,8.8 11.301,9.113 11.301,9.5V12.8H8.001C7.614,12.8 7.301,13.113 7.301,13.5C7.301,13.886 7.614,14.2 8.001,14.2H11.301V17.5C11.301,17.886 11.614,18.2 12.001,18.2C12.387,18.2 12.701,17.886 12.701,17.5V14.2H16.001C16.387,14.2 16.701,13.886 16.701,13.5C16.701,13.113 16.387,12.8 16.001,12.8H12.701V9.5Z"),
                            pathFillType = PathFillType.EvenOdd,
                            fill = SolidColor(Color.Black),
                        )
                    }
                }
            }
        }.build().also { _paste = it }
    }

private var _paste: ImageVector? = null
