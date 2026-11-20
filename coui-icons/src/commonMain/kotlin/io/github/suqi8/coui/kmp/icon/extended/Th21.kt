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

/** Th21: ColorOS `ic_main_tab_back_today frame + bold day number` (com.coloros.calendar_17.6.20). */
val COUIIcons.Th21: ImageVector
    get() {
        _th21?.let { return it }
        return ImageVector.Builder(
            name = "Th21",
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
                pathData = addPathNodes("M11.653 16.91V17.9H7.277V17.056L9.347 14.839Q9.656 14.489 9.838 14.224Q10.02 13.958 10.099 13.744Q10.179 13.53 10.179 13.339Q10.179 13.049 10.082 12.84Q9.985 12.63 9.799 12.515Q9.612 12.4 9.343 12.4Q9.051 12.4 8.843 12.543Q8.636 12.685 8.526 12.935Q8.417 13.185 8.417 13.502H7.148Q7.148 12.928 7.42 12.453Q7.691 11.978 8.19 11.694Q8.689 11.411 9.367 11.411Q10.042 11.411 10.505 11.628Q10.968 11.846 11.208 12.251Q11.448 12.657 11.448 13.218Q11.448 13.535 11.346 13.837Q11.244 14.138 11.057 14.434Q10.87 14.73 10.607 15.034Q10.345 15.337 10.022 15.665L8.909 16.91Z M15.522 11.49V17.9H14.257V12.949L12.74 13.431V12.432L15.389 11.49Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _th21 = it }
    }

private var _th21: ImageVector? = null
