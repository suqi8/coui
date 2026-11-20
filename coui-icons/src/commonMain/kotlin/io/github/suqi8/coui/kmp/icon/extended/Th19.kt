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

/** Th19: ColorOS `ic_main_tab_back_today frame + bold day number` (com.coloros.calendar_17.6.20). */
val COUIIcons.Th19: ImageVector
    get() {
        _th19?.let { return it }
        return ImageVector.Builder(
            name = "Th19",
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
                pathData = addPathNodes("M10.359 11.49V17.9H9.093V12.949L7.576 13.431V12.432L10.225 11.49Z M13.334 16.936H13.421Q13.945 16.936 14.325 16.807Q14.705 16.677 14.951 16.431Q15.198 16.185 15.317 15.828Q15.436 15.471 15.436 15.012V13.8Q15.436 13.451 15.368 13.189Q15.301 12.926 15.176 12.751Q15.052 12.576 14.886 12.489Q14.72 12.402 14.521 12.402Q14.311 12.402 14.149 12.505Q13.987 12.608 13.874 12.785Q13.761 12.962 13.702 13.188Q13.644 13.414 13.644 13.658Q13.644 13.908 13.701 14.129Q13.758 14.351 13.872 14.521Q13.986 14.69 14.158 14.787Q14.331 14.884 14.563 14.884Q14.788 14.884 14.965 14.8Q15.142 14.716 15.268 14.574Q15.393 14.432 15.461 14.26Q15.528 14.088 15.531 13.912L15.947 14.141Q15.947 14.45 15.815 14.748Q15.683 15.047 15.449 15.291Q15.215 15.535 14.906 15.68Q14.597 15.825 14.247 15.825Q13.799 15.825 13.45 15.662Q13.102 15.498 12.863 15.205Q12.625 14.912 12.502 14.52Q12.38 14.128 12.38 13.672Q12.38 13.206 12.531 12.797Q12.682 12.388 12.966 12.077Q13.249 11.765 13.646 11.588Q14.042 11.411 14.53 11.411Q15.02 11.411 15.418 11.599Q15.817 11.788 16.104 12.133Q16.391 12.478 16.547 12.958Q16.703 13.437 16.703 14.02V14.46Q16.703 15.082 16.569 15.619Q16.436 16.156 16.17 16.588Q15.904 17.02 15.511 17.327Q15.117 17.634 14.598 17.798Q14.079 17.962 13.439 17.962H13.334Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _th19 = it }
    }

private var _th19: ImageVector? = null
