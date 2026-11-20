// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Create: ColorOS `tool_menu_ic_edit` (com.coloros.alarmclock_16.19.4). */
val COUIIcons.Create: ImageVector
    get() {
        _create?.let { return it }
        return ImageVector.Builder(
            name = "Create",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M20.25,6.25L17.75,3.75C17.4167,3.4167 17.0833,3.25 16.6667,3.25C16.25,3.25 15.8333,3.4167 15.5833,3.75L4.25,15L3.25,20.75L9,19.75L20.25,8.5C20.9167,7.8333 20.9167,6.8333 20.25,6.25ZM8.25,18.3333L5.0833,18.9167L5.5833,15.75L13.9167,7.4167L16.5833,10L8.25,18.3333ZM19.25,7.4167L17.6667,9L15.0833,6.4167L16.5833,4.75H16.6667L19.1667,7.25C19.25,7.3333 19.25,7.3333 19.25,7.4167Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _create = it }
    }

private var _create: ImageVector? = null
