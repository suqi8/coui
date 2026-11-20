// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Theme: ColorOS `btn_theme` (com.coloros.weather2_17.8.4). */
val COUIIcons.Theme: ImageVector
    get() {
        _theme?.let { return it }
        return ImageVector.Builder(
            name = "Theme",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24.375f,
            viewportHeight = 24.375f,
        ).apply {
            group(translationX = 0.0625f, translationY = 0.1875f) {
                addPath(
                    pathData = addPathNodes("M8.954,3.865C8.585,3.74 8.183,3.746 7.818,3.882L7.818,3.882L7.813,3.884L6.072,4.549C5.594,4.727 5.163,5.013 4.812,5.383C4.461,5.755 4.199,6.202 4.048,6.691L4.048,6.691L4.046,6.698L2.646,11.374L2.396,12.21L3.252,12.373L6.988,13.088V18.562C6.988,19.003 7.163,19.425 7.475,19.737C7.786,20.049 8.209,20.224 8.65,20.224H15.65C16.091,20.224 16.514,20.049 16.826,19.737C17.137,19.425 17.313,19.003 17.313,18.562V13.088L21.048,12.373L21.905,12.21L21.654,11.374L20.254,6.698L20.254,6.698L20.253,6.692C20.102,6.203 19.841,5.754 19.489,5.382C19.138,5.01 18.707,4.724 18.228,4.545L16.49,3.884L16.49,3.884L16.486,3.882C16.12,3.746 15.719,3.74 15.35,3.865C14.985,3.989 14.674,4.233 14.467,4.557C14.212,4.935 13.869,5.245 13.467,5.46C13.063,5.676 12.611,5.79 12.152,5.79C11.693,5.79 11.241,5.676 10.836,5.46C10.434,5.245 10.091,4.935 9.837,4.557C9.63,4.233 9.318,3.989 8.954,3.865ZM17.313,11.484L19.896,10.99L18.747,7.156L18.747,7.153C18.667,6.895 18.529,6.659 18.344,6.463C18.158,6.266 17.929,6.115 17.676,6.02L17.67,6.018L17.67,6.018L15.933,5.357C15.908,5.348 15.881,5.348 15.856,5.357C15.83,5.365 15.809,5.383 15.794,5.405L15.788,5.415L15.781,5.425C15.382,6.022 14.843,6.511 14.21,6.849C13.576,7.188 12.87,7.365 12.152,7.365C11.434,7.365 10.727,7.188 10.094,6.849C9.461,6.511 8.921,6.022 8.522,5.425L8.516,5.415L8.509,5.405C8.495,5.383 8.473,5.365 8.448,5.357C8.423,5.348 8.395,5.348 8.37,5.357L6.631,6.022L6.624,6.024L6.624,6.024C6.371,6.118 6.142,6.269 5.956,6.466C5.771,6.661 5.634,6.897 5.553,7.154L4.405,10.99L6.988,11.484V8.937H8.563V18.562C8.563,18.585 8.572,18.607 8.588,18.624C8.605,18.64 8.627,18.649 8.65,18.649H15.65C15.673,18.649 15.696,18.64 15.712,18.624C15.728,18.607 15.738,18.585 15.738,18.562V8.937H17.313V11.484Z"),
                    pathFillType = PathFillType.EvenOdd,
                    fill = SolidColor(Color.Black),
                )
            }
        }.build().also { _theme = it }
    }

private var _theme: ImageVector? = null
