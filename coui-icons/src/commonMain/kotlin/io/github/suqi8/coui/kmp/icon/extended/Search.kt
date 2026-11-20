// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Search: ColorOS `ic_menu_search_normal` (settings_17.0.0). */
val COUIIcons.Search: ImageVector
    get() {
        _search?.let { return it }
        return ImageVector.Builder(
            name = "Search",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M4.401,10.115C4.401,6.959 6.959,4.4 10.115,4.4C13.271,4.4 15.829,6.959 15.829,10.115C15.829,13.271 13.271,15.829 10.115,15.829C6.959,15.829 4.401,13.271 4.401,10.115ZM10.115,2.8C6.076,2.8 2.801,6.075 2.801,10.115C2.801,14.154 6.076,17.429 10.115,17.429C11.774,17.429 13.304,16.876 14.532,15.945L19.351,20.764C19.741,21.155 20.374,21.155 20.765,20.764C21.155,20.374 21.155,19.741 20.765,19.35L15.946,14.531C16.877,13.304 17.429,11.774 17.429,10.115C17.429,6.075 14.155,2.8 10.115,2.8Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _search = it }
    }

private var _search: ImageVector? = null
