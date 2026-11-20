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

/** Th5: ColorOS `ic_main_tab_back_today frame + bold day number` (com.coloros.calendar_17.6.20). */
val COUIIcons.Th5: ImageVector
    get() {
        _th5?.let { return it }
        return ImageVector.Builder(
            name = "Th5",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M7.434,3.8C7.434,3.358 7.792,3 8.234,3C8.676,3 9.034,3.358 9.034,3.8V4.656H15.024V3.8C15.024,3.358 15.382,3 15.824,3C16.265,3 16.624,3.358 16.624,3.8V4.656H18.993C19.987,4.656 20.793,5.462 20.793,6.456V19.07C20.793,20.064 19.987,20.87 18.993,20.87H4.993C3.999,20.87 3.193,20.064 3.193,19.07V6.456C3.193,5.462 3.999,4.656 4.993,4.656H7.434V3.8ZM8.139,6.256H4.993C4.882,6.256 4.793,6.345 4.793,6.456V7.853H19.193V6.456C19.193,6.345 19.103,6.256 18.993,6.256H15.918C15.887,6.259 15.856,6.261 15.824,6.261C15.792,6.261 15.76,6.259 15.729,6.256H8.329C8.298,6.259 8.266,6.261 8.234,6.261C8.202,6.261 8.17,6.259 8.139,6.256ZM4.793,19.07V9.453H19.193V19.07C19.193,19.181 19.103,19.27 18.993,19.27H4.993C4.882,19.27 4.793,19.181 4.793,19.07Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M11.047 14.986 10.039 14.744 10.405 11.502H13.997V12.524H11.446L11.285 13.946Q11.413 13.871 11.672 13.785Q11.93 13.699 12.252 13.699Q12.717 13.699 13.079 13.844Q13.441 13.99 13.693 14.267Q13.946 14.543 14.076 14.942Q14.207 15.34 14.207 15.846Q14.207 16.275 14.076 16.66Q13.944 17.045 13.677 17.345Q13.411 17.645 13.005 17.816Q12.6 17.988 12.047 17.988Q11.633 17.988 11.248 17.864Q10.863 17.74 10.557 17.5Q10.251 17.26 10.07 16.914Q9.889 16.567 9.878 16.125H11.136Q11.162 16.399 11.279 16.594Q11.395 16.79 11.59 16.894Q11.784 16.998 12.041 16.998Q12.283 16.998 12.454 16.906Q12.624 16.814 12.731 16.649Q12.837 16.484 12.887 16.262Q12.937 16.04 12.937 15.78Q12.937 15.522 12.876 15.31Q12.814 15.099 12.687 14.945Q12.559 14.792 12.365 14.71Q12.171 14.628 11.908 14.628Q11.559 14.628 11.367 14.737Q11.174 14.847 11.047 14.986Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _th5 = it }
    }

private var _th5: ImageVector? = null
