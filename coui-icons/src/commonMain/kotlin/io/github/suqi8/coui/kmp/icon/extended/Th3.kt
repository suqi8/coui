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

/** Th3: ColorOS `ic_main_tab_back_today frame + bold day number` (com.coloros.calendar_17.6.20). */
val COUIIcons.Th3: ImageVector
    get() {
        _th3?.let { return it }
        return ImageVector.Builder(
            name = "Th3",
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
                pathData = addPathNodes("M11.154 14.152H11.83Q12.155 14.152 12.365 14.039Q12.576 13.926 12.68 13.722Q12.784 13.519 12.784 13.248Q12.784 12.998 12.689 12.807Q12.595 12.616 12.402 12.508Q12.21 12.4 11.912 12.4Q11.683 12.4 11.485 12.493Q11.288 12.585 11.167 12.754Q11.047 12.922 11.047 13.158H9.778Q9.778 12.632 10.062 12.239Q10.345 11.847 10.824 11.629Q11.302 11.411 11.879 11.411Q12.528 11.411 13.017 11.618Q13.505 11.825 13.778 12.23Q14.051 12.635 14.051 13.233Q14.051 13.534 13.911 13.819Q13.771 14.105 13.507 14.33Q13.244 14.555 12.872 14.689Q12.5 14.823 12.033 14.823H11.154ZM11.154 15.115V14.457H12.033Q12.555 14.457 12.952 14.577Q13.349 14.697 13.617 14.918Q13.885 15.138 14.019 15.441Q14.154 15.744 14.154 16.107Q14.154 16.555 13.983 16.905Q13.812 17.255 13.503 17.497Q13.194 17.739 12.78 17.863Q12.367 17.988 11.881 17.988Q11.475 17.988 11.086 17.877Q10.697 17.765 10.383 17.539Q10.069 17.313 9.882 16.966Q9.696 16.619 9.696 16.149H10.964Q10.964 16.393 11.088 16.586Q11.211 16.778 11.428 16.888Q11.644 16.998 11.914 16.998Q12.219 16.998 12.435 16.887Q12.652 16.775 12.768 16.571Q12.884 16.367 12.884 16.092Q12.884 15.742 12.757 15.529Q12.63 15.315 12.393 15.215Q12.156 15.115 11.83 15.115Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _th3 = it }
    }

private var _th3: ImageVector? = null
