// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** RotateLeft: ColorOS `document_edit_rotation_icon` (com.coloros.ocrscanner_16.3.13). */
val COUIIcons.RotateLeft: ImageVector
    get() {
        _rotateLeft?.let { return it }
        return ImageVector.Builder(
            name = "RotateLeft",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
            autoMirror = true,
        ).apply {
            addPath(
                pathData = addPathNodes("M5.586,6.768C6.367,5.987 7.633,5.987 8.414,6.768L12.232,10.586C13.014,11.367 13.014,12.633 12.232,13.414L8.414,17.232C7.633,18.014 6.367,18.014 5.586,17.232L1.768,13.414C0.987,12.633 0.987,11.367 1.768,10.586L5.586,6.768Z"),
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M12.582,0.788C12.831,0.492 13.272,0.454 13.568,0.702C13.864,0.951 13.903,1.392 13.654,1.688L12.573,2.976C16.728,2.774 20.605,5.478 21.726,9.659C23.016,14.477 20.157,19.429 15.34,20.72C13.752,21.145 12.147,21.119 10.664,20.721C10.291,20.62 10.071,20.236 10.171,19.863C10.271,19.49 10.655,19.269 11.028,19.369C12.28,19.706 13.634,19.727 14.977,19.367C19.048,18.276 21.464,14.092 20.373,10.021C19.41,6.427 16.036,4.124 12.457,4.385L14.003,5.682C14.299,5.93 14.338,6.371 14.09,6.667C13.841,6.963 13.4,7.002 13.104,6.754L10.413,4.496C10.299,4.406 10.21,4.282 10.17,4.131C10.165,4.112 10.164,4.091 10.16,4.072C10.156,4.047 10.15,4.022 10.148,3.996C10.147,3.981 10.149,3.966 10.148,3.951C10.148,3.92 10.148,3.889 10.151,3.858C10.153,3.845 10.158,3.832 10.16,3.819C10.166,3.787 10.172,3.756 10.183,3.725C10.185,3.718 10.189,3.711 10.191,3.704C10.219,3.63 10.256,3.559 10.31,3.495L12.582,0.788Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _rotateLeft = it }
    }

private var _rotateLeft: ImageVector? = null
