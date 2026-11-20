// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** ConvertFile: ColorOS `drawable/document_pic2pdf_to_word` (com.coloros.ocrscanner_16.3.13). */
val COUIIcons.ConvertFile: ImageVector
    get() {
        _convertFile?.let { return it }
        return ImageVector.Builder(
            name = "ConvertFile",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M14.5,2H5.3C4.582,2 4,2.582 4,3.3L4,20.7C4,21.418 4.582,22 5.3,22H18.7C19.418,22 20,21.418 20,20.7V9.5"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M19.94,1.325L20.061,1.719C20.389,2.781 21.219,3.611 22.281,3.939L22.676,4.06C23.108,4.194 23.108,4.806 22.676,4.94L22.281,5.061C21.219,5.389 20.389,6.219 20.061,7.281L19.94,7.675C19.806,8.108 19.194,8.108 19.06,7.675L18.939,7.281C18.611,6.219 17.781,5.389 16.719,5.061L16.324,4.94C15.892,4.806 15.892,4.194 16.324,4.06L16.719,3.939C17.781,3.611 18.611,2.781 18.939,1.719L19.06,1.325C19.194,0.892 19.806,0.892 19.94,1.325Z"),
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M13.278,16L12,11.068L10.731,16H9.327L7.662,9.538H8.976L10.101,14.506L11.343,9.538H12.675L13.917,14.506L15.042,9.538H16.338L14.682,16H13.278Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _convertFile = it }
    }

private var _convertFile: ImageVector? = null
