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

/** Th10: ColorOS `ic_main_tab_back_today frame + bold day number` (com.coloros.calendar_17.6.20). */
val COUIIcons.Th10: ImageVector
    get() {
        _th10?.let { return it }
        return ImageVector.Builder(
            name = "Th10",
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
                pathData = addPathNodes("M10.359 11.49V17.9H9.093V12.949L7.576 13.431V12.432L10.225 11.49Z M16.745 14.14V15.247Q16.745 15.968 16.591 16.488Q16.437 17.007 16.151 17.34Q15.865 17.673 15.468 17.83Q15.072 17.988 14.588 17.988Q14.201 17.988 13.868 17.888Q13.535 17.788 13.269 17.578Q13.003 17.368 12.812 17.044Q12.621 16.721 12.52 16.273Q12.419 15.825 12.419 15.247V14.14Q12.419 13.416 12.574 12.901Q12.728 12.386 13.016 12.055Q13.303 11.725 13.7 11.569Q14.097 11.414 14.579 11.414Q14.967 11.414 15.3 11.513Q15.632 11.612 15.899 11.819Q16.165 12.027 16.354 12.348Q16.542 12.67 16.644 13.116Q16.745 13.563 16.745 14.14ZM15.478 15.412V13.968Q15.478 13.621 15.439 13.361Q15.401 13.1 15.326 12.918Q15.252 12.736 15.142 12.622Q15.032 12.508 14.891 12.455Q14.751 12.402 14.579 12.402Q14.366 12.402 14.2 12.486Q14.034 12.569 13.92 12.752Q13.806 12.934 13.746 13.235Q13.686 13.535 13.686 13.968V15.412Q13.686 15.762 13.725 16.024Q13.763 16.287 13.839 16.473Q13.915 16.659 14.024 16.775Q14.133 16.892 14.275 16.945Q14.417 16.998 14.588 16.998Q14.8 16.998 14.965 16.914Q15.129 16.829 15.245 16.642Q15.361 16.455 15.42 16.151Q15.478 15.846 15.478 15.412Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _th10 = it }
    }

private var _th10: ImageVector? = null
