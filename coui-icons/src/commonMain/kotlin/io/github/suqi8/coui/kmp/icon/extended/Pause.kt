// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Pause: ColorOS `photoeditorpage_menu_pause` (com.coloros.gallery3d_16.50.46). */
val COUIIcons.Pause: ImageVector
    get() {
        _pause?.let { return it }
        return ImageVector.Builder(
            name = "Pause",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M6,19h4V5H6v14zm8,-14v14h4V5h-4z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _pause = it }
    }

private var _pause: ImageVector? = null
