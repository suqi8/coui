// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** SearchCleanup: ColorOS `ic_edit_text_delete_search_view` (settings_17.0.0). */
val COUIIcons.Basic.SearchCleanup: ImageVector
    get() {
        _searchCleanup?.let { return it }
        return ImageVector.Builder(
            name = "SearchCleanup",
            defaultWidth = 18.dp,
            defaultHeight = 18.dp,
            viewportWidth = 18f,
            viewportHeight = 18f,
        ).apply {
            group(translationX = -9f, translationY = -9f) {
                addPath(
                    pathData = addPathNodes("M18,18m-9,0a9,9 0,1 1,18 0a9,9 0,1 1,-18 0 M18,16.868L15.031,13.898L13.899,15.03L16.869,18L13.898,20.97L15.03,22.101L18,19.131L20.97,22.101L22.102,20.969L19.132,18L22.101,15.03L20.969,13.899L18,16.868Z"),
                    pathFillType = PathFillType.EvenOdd,
                    fill = SolidColor(Color.Black),
                )
            }
        }.build().also { _searchCleanup = it }
    }

private var _searchCleanup: ImageVector? = null
