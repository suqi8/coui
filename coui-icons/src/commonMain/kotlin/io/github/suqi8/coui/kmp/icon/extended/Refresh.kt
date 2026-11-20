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

/** Refresh: ColorOS `icon_aigc_refresh` (com.coloros.note_16.7.2). */
val COUIIcons.Refresh: ImageVector
    get() {
        _refresh?.let { return it }
        return ImageVector.Builder(
            name = "Refresh",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 22f,
            viewportHeight = 22f,
            autoMirror = true,
        ).apply {
            addPath(
                pathData = addPathNodes("M13.986,7.168C13.631,7.168 13.344,6.88 13.344,6.526C13.344,6.171 13.631,5.884 13.986,5.884H16.528C15.58,4.882 14.372,4.15 13.027,3.779C11.269,3.293 9.395,3.454 7.746,4.235C6.097,5.015 4.783,6.362 4.044,8.03C3.305,9.698 3.19,11.576 3.72,13.321C4.249,15.067 5.389,16.564 6.93,17.54C8.472,18.516 10.312,18.906 12.117,18.638C13.921,18.37 15.57,17.463 16.761,16.081C17.953,14.7 18.608,12.936 18.608,11.112C18.608,10.757 18.896,10.47 19.25,10.47C19.604,10.47 19.892,10.757 19.892,11.112C19.892,13.244 19.125,15.305 17.733,16.92C16.34,18.534 14.414,19.594 12.305,19.907C10.196,20.22 8.045,19.765 6.244,18.625C4.442,17.484 3.111,15.734 2.491,13.694C1.872,11.654 2.007,9.459 2.87,7.51C3.734,5.561 5.269,3.987 7.197,3.075C9.124,2.163 11.314,1.974 13.369,2.542C14.835,2.947 16.163,3.718 17.233,4.771V2.637C17.233,2.282 17.521,1.995 17.875,1.995C18.229,1.995 18.517,2.282 18.517,2.637L18.517,6.526C18.517,6.873 18.24,7.156 17.895,7.167C17.873,7.168 17.851,7.169 17.829,7.168L13.986,7.168Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _refresh = it }
    }

private var _refresh: ImageVector? = null
