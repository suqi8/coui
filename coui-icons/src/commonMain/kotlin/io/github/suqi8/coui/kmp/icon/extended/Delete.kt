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

/** Delete: ColorOS `tool_menu_ic_delete_normal` (settings_17.0.0). */
val COUIIcons.Delete: ImageVector
    get() {
        _delete?.let { return it }
        return ImageVector.Builder(
            name = "Delete",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
            autoMirror = true,
        ).apply {
            addPath(
                pathData = addPathNodes("M10.25,2.8869H9.7151L9.5107,3.3812L8.2678,6.3869H3.25V7.9869H5.075V19.4369C5.075,19.8812 5.2515,20.3072 5.5656,20.6213C5.8797,20.9354 6.3058,21.1119 6.75,21.1119H17.25C17.6942,21.1119 18.1203,20.9354 18.4344,20.6213C18.7485,20.3072 18.925,19.8811 18.925,19.4369V7.9869H20.75V6.3869H15.7321L14.4892,3.3812L14.2848,2.8869H13.75H10.25ZM14.0007,6.3869L13.2151,4.4869H10.7848L9.9992,6.3869H14.0007ZM7.6062,7.9869H6.675V19.4369C6.675,19.4568 6.6829,19.4759 6.697,19.4899C6.711,19.504 6.7301,19.5119 6.75,19.5119H17.25C17.2699,19.5119 17.289,19.504 17.303,19.4899C17.3171,19.4759 17.325,19.4568 17.325,19.4369V7.9869H16.3937H15.1972H8.8027H7.6062ZM9.45,16.8119V10.6869H11.05V16.8119H9.45ZM12.95,10.6869V16.8119H14.55V10.6869H12.95Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _delete = it }
    }

private var _delete: ImageVector? = null
