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

/** ContactsCircle: ColorOS `settings_search_usercenter_default_profile_ic` (settings_17.0.0). */
val COUIIcons.ContactsCircle: ImageVector
    get() {
        _contactsCircle?.let { return it }
        return ImageVector.Builder(
            name = "ContactsCircle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 50f,
            viewportHeight = 50f,
        ).apply {
            addPath(
                pathData = addPathNodes("M25 47c12.15 0 22-9.85 22-22S37.15 3 25 3 3 12.85 3 25s9.85 22 22 22Zm9.31-13.79c0.24 0 0.46-0.11 0.6-0.3 0.13-0.2 0.17-0.44 0.1-0.66l-1.12-3.34c-0.67-2-2.85-3.4-5.3-3.4H21.4c-2.46 0-4.64 1.4-5.3 3.4l-1.12 3.34c-0.07 0.22-0.03 0.47 0.1 0.66 0.14 0.19 0.36 0.3 0.6 0.3H34.3Zm-4.78-14.27c0 2.5-2.03 4.53-4.53 4.53-2.5 0-4.53-2.03-4.53-4.53 0-2.5 2.03-4.53 4.53-4.53 2.5 0 4.53 2.03 4.53 4.53Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _contactsCircle = it }
    }

private var _contactsCircle: ImageVector? = null
