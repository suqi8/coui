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

/** Th13: ColorOS `ic_main_tab_back_today frame + bold day number` (com.coloros.calendar_17.6.20). */
val COUIIcons.Th13: ImageVector
    get() {
        _th13?.let { return it }
        return ImageVector.Builder(
            name = "Th13",
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
                pathData = addPathNodes("M10.359 11.49V17.9H9.093V12.949L7.576 13.431V12.432L10.225 11.49Z M13.736 14.152H14.411Q14.737 14.152 14.947 14.039Q15.158 13.926 15.262 13.722Q15.366 13.519 15.366 13.248Q15.366 12.998 15.271 12.807Q15.177 12.616 14.984 12.508Q14.792 12.4 14.494 12.4Q14.264 12.4 14.067 12.493Q13.869 12.585 13.749 12.754Q13.629 12.922 13.629 13.158H12.36Q12.36 12.632 12.643 12.239Q12.927 11.847 13.405 11.629Q13.884 11.411 14.461 11.411Q15.11 11.411 15.598 11.618Q16.087 11.825 16.36 12.23Q16.633 12.635 16.633 13.233Q16.633 13.534 16.493 13.819Q16.352 14.105 16.089 14.33Q15.826 14.555 15.453 14.689Q15.081 14.823 14.615 14.823H13.736ZM13.736 15.115V14.457H14.615Q15.137 14.457 15.534 14.577Q15.93 14.697 16.198 14.918Q16.466 15.138 16.601 15.441Q16.736 15.744 16.736 16.107Q16.736 16.555 16.565 16.905Q16.394 17.255 16.085 17.497Q15.776 17.739 15.362 17.863Q14.948 17.988 14.463 17.988Q14.057 17.988 13.668 17.877Q13.279 17.765 12.965 17.539Q12.651 17.313 12.464 16.966Q12.278 16.619 12.278 16.149H13.546Q13.546 16.393 13.669 16.586Q13.793 16.778 14.009 16.888Q14.226 16.998 14.495 16.998Q14.8 16.998 15.017 16.887Q15.234 16.775 15.35 16.571Q15.466 16.367 15.466 16.092Q15.466 15.742 15.339 15.529Q15.212 15.315 14.975 15.215Q14.738 15.115 14.411 15.115Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _th13 = it }
    }

private var _th13: ImageVector? = null
