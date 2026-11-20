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

/** Th22: ColorOS `ic_main_tab_back_today frame + bold day number` (com.coloros.calendar_17.6.20). */
val COUIIcons.Th22: ImageVector
    get() {
        _th22?.let { return it }
        return ImageVector.Builder(
            name = "Th22",
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
                pathData = addPathNodes("M11.653 16.91V17.9H7.277V17.056L9.347 14.839Q9.656 14.489 9.838 14.224Q10.02 13.958 10.099 13.744Q10.179 13.53 10.179 13.339Q10.179 13.049 10.082 12.84Q9.985 12.63 9.799 12.515Q9.612 12.4 9.343 12.4Q9.051 12.4 8.843 12.543Q8.636 12.685 8.526 12.935Q8.417 13.185 8.417 13.502H7.148Q7.148 12.928 7.42 12.453Q7.691 11.978 8.19 11.694Q8.689 11.411 9.367 11.411Q10.042 11.411 10.505 11.628Q10.968 11.846 11.208 12.251Q11.448 12.657 11.448 13.218Q11.448 13.535 11.346 13.837Q11.244 14.138 11.057 14.434Q10.87 14.73 10.607 15.034Q10.345 15.337 10.022 15.665L8.909 16.91Z M16.816 16.91V17.9H12.441V17.056L14.51 14.839Q14.82 14.489 15.001 14.224Q15.183 13.958 15.263 13.744Q15.343 13.53 15.343 13.339Q15.343 13.049 15.246 12.84Q15.149 12.63 14.963 12.515Q14.776 12.4 14.506 12.4Q14.214 12.4 14.007 12.543Q13.799 12.685 13.69 12.935Q13.58 13.185 13.58 13.502H12.312Q12.312 12.928 12.583 12.453Q12.855 11.978 13.354 11.694Q13.852 11.411 14.53 11.411Q15.205 11.411 15.669 11.628Q16.132 11.846 16.372 12.251Q16.611 12.657 16.611 13.218Q16.611 13.535 16.509 13.837Q16.407 14.138 16.22 14.434Q16.033 14.73 15.771 15.034Q15.509 15.337 15.185 15.665L14.073 16.91Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _th22 = it }
    }

private var _th22: ImageVector? = null
