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

/** Reset: ColorOS `restore_default` (settings_17.0.0). */
val COUIIcons.Reset: ImageVector
    get() {
        _reset?.let { return it }
        return ImageVector.Builder(
            name = "Reset",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M19.7758,8.5508V3.3008H18.1758V5.9857C17.3152,5.1064 16.2704,4.413 15.1044,3.9621C13.1246,3.1967 10.933,3.1835 8.9442,3.9253C6.9555,4.6671 5.3077,6.1122 4.3128,7.9872C3.3179,9.8622 3.0449,12.0368 3.5456,14.0995C4.0462,16.1622 5.2857,17.9698 7.0295,19.18C8.7732,20.3903 10.9002,20.9192 13.0077,20.6668C15.1152,20.4143 17.057,19.3979 18.4655,17.81C19.874,16.2221 20.6515,14.1729 20.6508,12.0504L19.0508,12.0509C19.0514,13.782 18.4173,15.4532 17.2686,16.7483C16.1198,18.0433 14.5362,18.8722 12.8173,19.0781C11.0985,19.284 9.3639,18.8526 7.9418,17.8656C6.5196,16.8786 5.5087,15.4044 5.1004,13.7221C4.6921,12.0399 4.9147,10.2663 5.7261,8.7371C6.5375,7.208 7.8814,6.0294 9.5033,5.4244C11.1253,4.8195 12.9128,4.8302 14.5273,5.4545C15.7481,5.9265 16.806,6.7245 17.5922,7.7508H13.7258V9.3508H18.9758C19.4176,9.3508 19.7758,8.9926 19.7758,8.5508Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _reset = it }
    }

private var _reset: ImageVector? = null
