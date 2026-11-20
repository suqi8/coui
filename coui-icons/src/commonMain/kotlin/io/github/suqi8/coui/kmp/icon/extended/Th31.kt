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

/** Th31: ColorOS `ic_main_tab_back_today frame + bold day number` (com.coloros.calendar_17.6.20). */
val COUIIcons.Th31: ImageVector
    get() {
        _th31?.let { return it }
        return ImageVector.Builder(
            name = "Th31",
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
                pathData = addPathNodes("M8.573 14.152H9.248Q9.573 14.152 9.784 14.039Q9.994 13.926 10.098 13.722Q10.202 13.519 10.202 13.248Q10.202 12.998 10.108 12.807Q10.013 12.616 9.82 12.508Q9.628 12.4 9.33 12.4Q9.101 12.4 8.903 12.493Q8.706 12.585 8.585 12.754Q8.465 12.922 8.465 13.158H7.196Q7.196 12.632 7.48 12.239Q7.763 11.847 8.242 11.629Q8.721 11.411 9.298 11.411Q9.946 11.411 10.435 11.618Q10.924 11.825 11.196 12.23Q11.469 12.635 11.469 13.233Q11.469 13.534 11.329 13.819Q11.189 14.105 10.925 14.33Q10.662 14.555 10.29 14.689Q9.918 14.823 9.451 14.823H8.573ZM8.573 15.115V14.457H9.451Q9.973 14.457 10.37 14.577Q10.767 14.697 11.035 14.918Q11.303 15.138 11.437 15.441Q11.572 15.744 11.572 16.107Q11.572 16.555 11.401 16.905Q11.23 17.255 10.921 17.497Q10.612 17.739 10.199 17.863Q9.785 17.988 9.299 17.988Q8.893 17.988 8.504 17.877Q8.115 17.765 7.801 17.539Q7.487 17.313 7.3 16.966Q7.114 16.619 7.114 16.149H8.383Q8.383 16.393 8.506 16.586Q8.629 16.778 8.846 16.888Q9.062 16.998 9.332 16.998Q9.637 16.998 9.854 16.887Q10.07 16.775 10.186 16.571Q10.302 16.367 10.302 16.092Q10.302 15.742 10.175 15.529Q10.049 15.315 9.812 15.215Q9.575 15.115 9.248 15.115Z M15.522 11.49V17.9H14.257V12.949L12.74 13.431V12.432L15.389 11.49Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _th31 = it }
    }

private var _th31: ImageVector? = null
