// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** MicSlash: ColorOS `ic_lla_mic_off` (com.oplus.screenrecorder_16.7.2). */
val COUIIcons.MicSlash: ImageVector
    get() {
        _micSlash?.let { return it }
        return ImageVector.Builder(
            name = "MicSlash",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            group(translationX = 1f, translationY = 1f) {
                group(clipPathData = addPathNodes("M0,0h22v22h-22z")) {
                    addPath(
                        pathData = addPathNodes("M2.329,1.339L6.634,5.644V5.042C6.634,2.631 8.589,0.675 11.001,0.675C13.413,0.675 15.368,2.631 15.368,5.042V10.542C15.368,11.613 14.983,12.594 14.343,13.353L15.301,14.311C16.184,13.305 16.719,11.986 16.719,10.543C16.719,10.156 17.031,9.843 17.418,9.843C17.804,9.843 18.118,10.156 18.118,10.543C18.118,12.373 17.427,14.042 16.292,15.302L20.663,19.674C20.937,19.947 20.937,20.39 20.663,20.664C20.499,20.829 20.272,20.894 20.058,20.86L2.328,3.13C2.219,3.021 2.084,2.956 1.943,2.934L1.338,2.329C1.065,2.055 1.065,1.612 1.338,1.339C1.612,1.065 2.055,1.065 2.329,1.339ZM6.634,9.416V10.542C6.634,12.954 8.589,14.91 11.001,14.91C11.348,14.91 11.686,14.869 12.01,14.792L6.634,9.416ZM13.085,15.868C12.439,16.121 11.736,16.26 11.001,16.26C7.843,16.26 5.284,13.7 5.284,10.543C5.284,10.156 4.97,9.843 4.584,9.843C4.197,9.843 3.884,10.156 3.884,10.543C3.884,14.237 6.699,17.274 10.301,17.626V19.927H8.251C7.864,19.927 7.551,20.24 7.551,20.627C7.551,21.014 7.864,21.327 8.251,21.327H10.995L11.001,21.327L11.007,21.327H13.751C14.137,21.327 14.451,21.014 14.451,20.627C14.451,20.24 14.137,19.927 13.751,19.927H11.701V17.626C12.572,17.541 13.396,17.299 14.146,16.929L13.085,15.868Z"),
                        pathFillType = PathFillType.EvenOdd,
                        fill = SolidColor(Color.Black),
                    )
                }
            }
        }.build().also { _micSlash = it }
    }

private var _micSlash: ImageVector? = null
