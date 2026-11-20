// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** More: ColorOS `coui_toolbar_menu_icon_more_normal` (settings_17.0.0). */
val COUIIcons.More: ImageVector
    get() {
        _more?.let { return it }
        return ImageVector.Builder(
            name = "More",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M12,13.75C12.967,13.75 13.75,12.966 13.75,12C13.75,11.033 12.967,10.25 12,10.25C11.033,10.25 10.25,11.033 10.25,12C10.25,12.966 11.033,13.75 12,13.75Z"),
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M12,6.625C12.967,6.625 13.75,5.842 13.75,4.875C13.75,3.908 12.967,3.125 12,3.125C11.033,3.125 10.25,3.908 10.25,4.875C10.25,5.842 11.033,6.625 12,6.625Z"),
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M12,20.875C12.967,20.875 13.75,20.091 13.75,19.125C13.75,18.158 12.967,17.375 12,17.375C11.033,17.375 10.25,18.158 10.25,19.125C10.25,20.091 11.033,20.875 12,20.875Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _more = it }
    }

private var _more: ImageVector? = null
