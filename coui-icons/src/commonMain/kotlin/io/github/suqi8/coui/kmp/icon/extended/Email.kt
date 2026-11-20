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

/** Email: ColorOS `ic_email_calendar` (com.coloros.calendar_17.6.20). */
val COUIIcons.Email: ImageVector
    get() {
        _email?.let { return it }
        return ImageVector.Builder(
            name = "Email",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
            autoMirror = true,
        ).apply {
            addPath(
                pathData = addPathNodes("M4.049,5.874C4.049,5.833 4.083,5.799 4.124,5.799H19.874C19.916,5.799 19.949,5.833 19.949,5.874V6.781L11.999,12.749L4.049,6.781V5.874ZM4.049,8.782V18.125C4.049,18.166 4.083,18.199 4.124,18.199H19.874C19.916,18.199 19.949,18.166 19.949,18.125V8.782L12.479,14.389L11.999,14.75L11.519,14.389L4.049,8.782ZM4.124,4.199C3.199,4.199 2.449,4.949 2.449,5.874V18.125C2.449,19.049 3.199,19.799 4.124,19.799H19.874C20.799,19.799 21.549,19.049 21.549,18.125V5.874C21.549,4.949 20.799,4.199 19.874,4.199H4.124Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _email = it }
    }

private var _email: ImageVector? = null
