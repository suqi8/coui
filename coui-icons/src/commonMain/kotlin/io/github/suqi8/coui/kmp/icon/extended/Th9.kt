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

/** Th9: ColorOS `ic_main_tab_back_today frame + bold day number` (com.coloros.calendar_17.6.20). */
val COUIIcons.Th9: ImageVector
    get() {
        _th9?.let { return it }
        return ImageVector.Builder(
            name = "Th9",
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
                pathData = addPathNodes("M10.753 16.936H10.839Q11.364 16.936 11.743 16.807Q12.123 16.677 12.37 16.431Q12.616 16.185 12.735 15.828Q12.854 15.471 12.854 15.012V13.8Q12.854 13.451 12.787 13.189Q12.719 12.926 12.594 12.751Q12.47 12.576 12.304 12.489Q12.138 12.402 11.939 12.402Q11.729 12.402 11.567 12.505Q11.405 12.608 11.292 12.785Q11.179 12.962 11.121 13.188Q11.062 13.414 11.062 13.658Q11.062 13.908 11.119 14.129Q11.176 14.351 11.29 14.521Q11.404 14.69 11.577 14.787Q11.749 14.884 11.981 14.884Q12.206 14.884 12.383 14.8Q12.56 14.716 12.686 14.574Q12.812 14.432 12.879 14.26Q12.946 14.088 12.949 13.912L13.366 14.141Q13.366 14.45 13.233 14.748Q13.101 15.047 12.867 15.291Q12.633 15.535 12.324 15.68Q12.016 15.825 11.665 15.825Q11.217 15.825 10.868 15.662Q10.52 15.498 10.282 15.205Q10.043 14.912 9.921 14.52Q9.798 14.128 9.798 13.672Q9.798 13.206 9.949 12.797Q10.101 12.388 10.384 12.077Q10.667 11.765 11.064 11.588Q11.46 11.411 11.948 11.411Q12.438 11.411 12.837 11.599Q13.235 11.788 13.522 12.133Q13.809 12.478 13.965 12.958Q14.121 13.437 14.121 14.02V14.46Q14.121 15.082 13.988 15.619Q13.854 16.156 13.588 16.588Q13.322 17.02 12.929 17.327Q12.535 17.634 12.016 17.798Q11.498 17.962 10.857 17.962H10.753Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _th9 = it }
    }

private var _th9: ImageVector? = null
