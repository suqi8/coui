// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Months: ColorOS `drawable/ic_for_overview_24` (com.coloros.calendar_17.6.20). */
val COUIIcons.Months: ImageVector
    get() {
        _months?.let { return it }
        return ImageVector.Builder(
            name = "Months",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M17.04,1.2C19.056,1.2 20.065,1.2 20.835,1.593C21.512,1.938 22.063,2.489 22.408,3.166C22.8,3.936 22.8,4.944 22.8,6.96V17.04C22.8,19.056 22.801,20.065 22.408,20.835L22.269,21.082C21.923,21.646 21.428,22.106 20.835,22.408L20.688,22.477C19.935,22.8 18.931,22.8 17.04,22.8H6.96C5.07,22.8 4.066,22.8 3.313,22.477L3.166,22.408C2.573,22.106 2.077,21.646 1.731,21.082L1.593,20.835C1.2,20.065 1.2,19.056 1.2,17.04V6.96C1.2,5.07 1.2,4.066 1.523,3.313L1.593,3.166C1.895,2.573 2.354,2.077 2.918,1.731L3.166,1.593C3.936,1.2 4.944,1.2 6.96,1.2H17.04ZM2.9,17.04C2.9,18.076 2.901,18.752 2.943,19.267C2.984,19.761 3.054,19.957 3.107,20.063C3.29,20.42 3.58,20.711 3.938,20.894C4.043,20.947 4.239,21.016 4.734,21.057C5.249,21.099 5.924,21.101 6.96,21.101H7.791V12.85H2.9V17.04ZM16.09,21.101H17.04C18.076,21.101 18.752,21.099 19.267,21.057C19.761,21.016 19.957,20.947 20.063,20.894C20.42,20.711 20.711,20.42 20.894,20.063C20.947,19.957 21.016,19.761 21.057,19.267C21.099,18.752 21.101,18.076 21.101,17.04V12.85H16.09V21.101ZM9.49,21.101H14.391V12.85H9.49V21.101ZM6.96,2.9C5.924,2.9 5.249,2.901 4.734,2.943C4.239,2.984 4.043,3.054 3.938,3.107C3.58,3.29 3.29,3.58 3.107,3.938C3.054,4.043 2.984,4.239 2.943,4.734C2.901,5.249 2.9,5.924 2.9,6.96V11.15H7.791V2.9H6.96ZM16.09,11.15H21.101V6.96C21.101,5.924 21.099,5.249 21.057,4.734C21.016,4.239 20.947,4.043 20.894,3.938C20.711,3.58 20.42,3.29 20.063,3.107C19.957,3.054 19.761,2.984 19.267,2.943C18.752,2.901 18.076,2.9 17.04,2.9H16.09V11.15ZM9.49,11.15H14.391V2.9H9.49V11.15Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _months = it }
    }

private var _months: ImageVector? = null
