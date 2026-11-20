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

/** Blocklist: ColorOS `mms_dr_menu_action_add_to_blacklist` (com.android.mms_16.60.10). */
val COUIIcons.Blocklist: ImageVector
    get() {
        _blocklist?.let { return it }
        return ImageVector.Builder(
            name = "Blocklist",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M7.63,5.057C7.63,4.055 8.442,3.243 9.443,3.243C10.445,3.243 11.257,4.055 11.257,5.057C11.257,6.058 10.445,6.87 9.443,6.87C8.442,6.87 7.63,6.058 7.63,5.057ZM9.443,1.843C7.669,1.843 6.23,3.282 6.23,5.057C6.23,6.832 7.669,8.27 9.443,8.27C11.218,8.27 12.657,6.832 12.657,5.057C12.657,3.282 11.218,1.843 9.443,1.843ZM7.077,9.843H10.892C10.396,10.255 9.951,10.725 9.568,11.243H7.077C5.592,11.243 4.397,12.066 4.063,13.09L4.061,13.097L4.061,13.097L3.472,14.843H8.083C8.029,15.221 8,15.607 8,16C8,16.082 8.001,16.163 8.004,16.243H2.497C2.272,16.243 2.06,16.135 1.929,15.952C1.797,15.769 1.762,15.533 1.834,15.32L2.733,12.653C3.3,10.919 5.144,9.843 7.077,9.843ZM16,10.65C13.045,10.65 10.65,13.046 10.65,16C10.65,17.205 11.049,18.317 11.721,19.211L19.211,11.721C18.317,11.049 17.205,10.65 16,10.65ZM20.212,12.7L12.7,20.212C13.609,20.925 14.755,21.35 16,21.35C18.955,21.35 21.35,18.955 21.35,16C21.35,14.755 20.925,13.609 20.212,12.7ZM9.35,16C9.35,12.328 12.328,9.35 16,9.35C19.673,9.35 22.65,12.328 22.65,16C22.65,19.673 19.673,22.65 16,22.65C12.328,22.65 9.35,19.673 9.35,16Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _blocklist = it }
    }

private var _blocklist: ImageVector? = null
