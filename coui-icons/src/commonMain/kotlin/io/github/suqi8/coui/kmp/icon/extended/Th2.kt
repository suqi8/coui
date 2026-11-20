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

/** Th2: ColorOS `ic_main_tab_back_today frame + bold day number` (com.coloros.calendar_17.6.20). */
val COUIIcons.Th2: ImageVector
    get() {
        _th2?.let { return it }
        return ImageVector.Builder(
            name = "Th2",
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
                pathData = addPathNodes("M14.235 16.91V17.9H9.859V17.056L11.928 14.839Q12.238 14.489 12.42 14.224Q12.602 13.958 12.681 13.744Q12.761 13.53 12.761 13.339Q12.761 13.049 12.664 12.84Q12.567 12.63 12.381 12.515Q12.194 12.4 11.925 12.4Q11.632 12.4 11.425 12.543Q11.217 12.685 11.108 12.935Q10.999 13.185 10.999 13.502H9.73Q9.73 12.928 10.002 12.453Q10.273 11.978 10.772 11.694Q11.27 11.411 11.948 11.411Q12.623 11.411 13.087 11.628Q13.55 11.846 13.79 12.251Q14.03 12.657 14.03 13.218Q14.03 13.535 13.928 13.837Q13.826 14.138 13.639 14.434Q13.451 14.73 13.189 15.034Q12.927 15.337 12.604 15.665L11.491 16.91Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _th2 = it }
    }

private var _th2: ImageVector? = null
