// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** NotesFill: ColorOS `drawable/color_menu_ic_note_pressed` (com.coloros.note_16.7.2). */
val COUIIcons.NotesFill: ImageVector
    get() {
        _notesFill?.let { return it }
        return ImageVector.Builder(
            name = "NotesFill",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M20.627,2.3H4.627C4.24,2.3 3.926,2.613 3.926,3V17C3.926,19.561 2.936,20.453 2.936,20.453C2.704,20.638 2.614,20.95 2.712,21.231C2.81,21.512 3.075,21.7 3.373,21.7H17.627C19.163,21.7 21.327,20.242 21.327,17V3C21.327,2.613 21.013,2.3 20.627,2.3ZM7.877,7.167C7.877,6.937 8.063,6.75 8.293,6.75H9.96C10.19,6.75 10.377,6.937 10.377,7.167V8.833C10.377,9.063 10.19,9.25 9.96,9.25H8.293C8.063,9.25 7.877,9.063 7.877,8.833V7.167ZM12.627,16.7H8.627C8.24,16.7 7.926,16.387 7.926,16C7.926,15.613 8.24,15.3 8.627,15.3H12.627C13.013,15.3 13.327,15.613 13.327,16C13.327,16.387 13.013,16.7 12.627,16.7ZM16.627,12.7H8.627C8.24,12.7 7.926,12.387 7.926,12C7.926,11.613 8.24,11.3 8.627,11.3H16.627C17.013,11.3 17.327,11.613 17.327,12C17.327,12.387 17.013,12.7 16.627,12.7ZM16.627,8.7H12.627C12.24,8.7 11.927,8.387 11.927,8C11.927,7.613 12.24,7.3 12.627,7.3H16.627C17.013,7.3 17.327,7.613 17.327,8C17.327,8.387 17.013,8.7 16.627,8.7Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _notesFill = it }
    }

private var _notesFill: ImageVector? = null
