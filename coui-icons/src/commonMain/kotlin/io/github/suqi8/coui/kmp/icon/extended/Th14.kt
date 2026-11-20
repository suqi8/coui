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

/** Th14: ColorOS `ic_main_tab_back_today frame + bold day number` (com.coloros.calendar_17.6.20). */
val COUIIcons.Th14: ImageVector
    get() {
        _th14?.let { return it }
        return ImageVector.Builder(
            name = "Th14",
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
                pathData = addPathNodes("M10.359 11.49V17.9H9.093V12.949L7.576 13.431V12.432L10.225 11.49Z M16.921 15.511V16.501H12.31L12.251 15.733L14.917 11.502H15.923L14.832 13.324L13.507 15.511ZM16.196 11.502V17.9H14.931V11.502Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _th14 = it }
    }

private var _th14: ImageVector? = null
