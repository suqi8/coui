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

/** Notes: ColorOS `drawable/color_menu_ic_note_normal` (com.coloros.note_16.7.2). */
val COUIIcons.Notes: ImageVector
    get() {
        _notes?.let { return it }
        return ImageVector.Builder(
            name = "Notes",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M3.927,3C3.927,2.613 4.241,2.3 4.627,2.3H20.627C21.014,2.3 21.327,2.613 21.327,3V17C21.327,20.386 19.014,21.7 17.627,21.7H3.373C3.076,21.7 2.811,21.512 2.713,21.231C2.616,20.954 2.702,20.647 2.927,20.461C2.929,20.459 2.933,20.455 2.94,20.448C2.958,20.431 2.99,20.398 3.032,20.348C3.117,20.247 3.243,20.073 3.375,19.811C3.636,19.29 3.927,18.399 3.927,17V3ZM4.693,20.3H17.627C18.241,20.3 19.927,19.613 19.927,17V3.7H5.327V17C5.327,18.502 5.032,19.571 4.693,20.3ZM11.927,8C11.927,7.613 12.241,7.3 12.627,7.3H16.627C17.014,7.3 17.327,7.613 17.327,8C17.327,8.386 17.014,8.7 16.627,8.7H12.627C12.241,8.7 11.927,8.386 11.927,8ZM8.627,11.3C8.241,11.3 7.927,11.613 7.927,12C7.927,12.386 8.241,12.7 8.627,12.7H16.627C17.014,12.7 17.327,12.386 17.327,12C17.327,11.613 17.014,11.3 16.627,11.3H8.627ZM7.927,16C7.927,15.613 8.241,15.3 8.627,15.3H12.627C13.014,15.3 13.327,15.613 13.327,16C13.327,16.386 13.014,16.7 12.627,16.7H8.627C8.241,16.7 7.927,16.386 7.927,16ZM8.294,6.75H9.961C10.191,6.75 10.377,6.936 10.377,7.166V8.833C10.377,9.063 10.191,9.25 9.961,9.25H8.294C8.064,9.25 7.877,9.063 7.877,8.833V7.166C7.877,6.936 8.064,6.75 8.294,6.75Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _notes = it }
    }

private var _notes: ImageVector? = null
