// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** CloudFill: ColorOS `drawable/icon_tips_calendar_cloud_sync_fill` (com.coloros.calendar_17.6.20). */
val COUIIcons.CloudFill: ImageVector
    get() {
        _cloudFill?.let { return it }
        return ImageVector.Builder(
            name = "CloudFill",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M11.841,4C8.859,4 6.358,6.056 5.677,8.828C3.587,9.191 1.998,11.013 1.998,13.206C1.998,15.661 3.989,17.649 6.443,17.649V17.65H18.19V17.649C20.293,17.649 21.998,15.944 21.998,13.841C21.998,11.737 20.292,10.031 18.188,10.031C18.186,10.031 18.184,10.031 18.182,10.031C18.016,6.673 15.241,4 11.841,4Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _cloudFill = it }
    }

private var _cloudFill: ImageVector? = null
