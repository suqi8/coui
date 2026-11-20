// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Scan: ColorOS `drawable/rcs_ic_scan_code_entrance_normal` (com.android.mms_16.60.10). */
val COUIIcons.Scan: ImageVector
    get() {
        _scan?.let { return it }
        return ImageVector.Builder(
            name = "Scan",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M2.8,4.9C2.8,3.74 3.74,2.8 4.9,2.8H8C8.387,2.8 8.7,3.113 8.7,3.5C8.7,3.886 8.387,4.2 8,4.2H4.9C4.513,4.2 4.2,4.513 4.2,4.9V8C4.2,8.386 3.887,8.7 3.5,8.7C3.113,8.7 2.8,8.386 2.8,8V4.9Z"),
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M1.3,12C1.3,11.613 1.613,11.3 2,11.3H22C22.387,11.3 22.7,11.613 22.7,12C22.7,12.386 22.387,12.7 22,12.7H2C1.613,12.7 1.3,12.386 1.3,12Z"),
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M19.1,21.2C20.26,21.2 21.2,20.26 21.2,19.1L21.2,16C21.2,15.613 20.887,15.3 20.5,15.3C20.113,15.3 19.8,15.613 19.8,16V19.1C19.8,19.486 19.487,19.8 19.1,19.8L16,19.8C15.613,19.8 15.3,20.113 15.3,20.5C15.3,20.886 15.613,21.2 16,21.2H19.1Z"),
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M4.9,21.2C3.74,21.2 2.8,20.26 2.8,19.1L2.8,16C2.8,15.613 3.113,15.3 3.5,15.3C3.887,15.3 4.2,15.613 4.2,16V19.1C4.2,19.486 4.513,19.8 4.9,19.8H8C8.387,19.8 8.7,20.113 8.7,20.5C8.7,20.886 8.387,21.2 8,21.2L4.9,21.2Z"),
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M21.2,4.9C21.2,3.74 20.26,2.8 19.1,2.8L16,2.8C15.613,2.8 15.3,3.113 15.3,3.5C15.3,3.886 15.613,4.2 16,4.2L19.1,4.2C19.487,4.2 19.8,4.513 19.8,4.9V8C19.8,8.386 20.114,8.7 20.5,8.7C20.887,8.7 21.2,8.386 21.2,8V4.9Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _scan = it }
    }

private var _scan: ImageVector? = null
