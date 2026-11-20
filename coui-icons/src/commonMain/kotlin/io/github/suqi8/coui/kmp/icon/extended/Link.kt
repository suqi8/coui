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

/** Link: ColorOS `drawable/ic_urllink_calendar` (com.coloros.calendar_17.6.20). */
val COUIIcons.Link: ImageVector
    get() {
        _link?.let { return it }
        return ImageVector.Builder(
            name = "Link",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
            autoMirror = true,
        ).apply {
            addPath(
                pathData = addPathNodes("M5.795,13.22C5.134,13.881 4.763,14.777 4.763,15.712C4.763,16.647 5.134,17.544 5.795,18.205C6.456,18.866 7.353,19.237 8.288,19.237C8.751,19.237 9.209,19.146 9.637,18.969C10.064,18.792 10.453,18.532 10.78,18.205L12.636,16.349L13.839,17.551L11.982,19.407C11.497,19.892 10.921,20.277 10.287,20.539C9.653,20.802 8.974,20.937 8.288,20.937C6.902,20.937 5.573,20.387 4.593,19.407C3.613,18.427 3.063,17.098 3.063,15.712C3.063,14.326 3.613,12.997 4.593,12.017L6.449,10.161L7.651,11.363L5.795,13.22ZM13.22,5.795L11.364,7.651L10.161,6.449L12.018,4.593C12.503,4.108 13.079,3.723 13.713,3.46C14.347,3.198 15.026,3.063 15.712,3.063C16.399,3.063 17.078,3.198 17.712,3.46C18.346,3.723 18.922,4.108 19.407,4.593C19.892,5.078 20.277,5.654 20.54,6.288C20.802,6.922 20.937,7.601 20.937,8.288C20.937,8.974 20.802,9.653 20.54,10.287C20.277,10.921 19.892,11.497 19.407,11.982L17.551,13.838L16.349,12.636L18.205,10.78C18.532,10.453 18.792,10.064 18.969,9.637C19.146,9.209 19.237,8.75 19.237,8.288C19.237,7.825 19.146,7.366 18.969,6.939C18.792,6.511 18.532,6.122 18.205,5.795C17.878,5.468 17.489,5.208 17.061,5.031C16.634,4.854 16.175,4.763 15.712,4.763C15.249,4.763 14.791,4.854 14.363,5.031C13.936,5.208 13.547,5.468 13.22,5.795ZM8.889,16.313L16.314,8.889L15.112,7.687L7.687,15.111L8.889,16.313Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _link = it }
    }

private var _link: ImageVector? = null
