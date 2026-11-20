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

/** Th15: ColorOS `ic_main_tab_back_today frame + bold day number` (com.coloros.calendar_17.6.20). */
val COUIIcons.Th15: ImageVector
    get() {
        _th15?.let { return it }
        return ImageVector.Builder(
            name = "Th15",
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
                pathData = addPathNodes("M10.359 11.49V17.9H9.093V12.949L7.576 13.431V12.432L10.225 11.49Z M13.629 14.986 12.621 14.744 12.986 11.502H16.579V12.524H14.028L13.867 13.946Q13.995 13.871 14.253 13.785Q14.512 13.699 14.834 13.699Q15.298 13.699 15.66 13.844Q16.022 13.99 16.275 14.267Q16.528 14.543 16.658 14.942Q16.788 15.34 16.788 15.846Q16.788 16.275 16.657 16.66Q16.526 17.045 16.259 17.345Q15.992 17.645 15.587 17.816Q15.181 17.988 14.629 17.988Q14.215 17.988 13.83 17.864Q13.445 17.74 13.139 17.5Q12.833 17.26 12.652 16.914Q12.471 16.567 12.459 16.125H13.718Q13.744 16.399 13.861 16.594Q13.977 16.79 14.172 16.894Q14.366 16.998 14.623 16.998Q14.865 16.998 15.036 16.906Q15.206 16.814 15.312 16.649Q15.418 16.484 15.468 16.262Q15.519 16.04 15.519 15.78Q15.519 15.522 15.457 15.31Q15.396 15.099 15.269 14.945Q15.141 14.792 14.947 14.71Q14.753 14.628 14.49 14.628Q14.141 14.628 13.948 14.737Q13.756 14.847 13.629 14.986Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _th15 = it }
    }

private var _th15: ImageVector? = null
