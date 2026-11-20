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

/** Community: ColorOS `rcs_ic_chatbot_sms` (com.android.mms_16.60.10). */
val COUIIcons.Community: ImageVector
    get() {
        _community?.let { return it }
        return ImageVector.Builder(
            name = "Community",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M7.3,8.782C7.3,8.395 7.613,8.082 8,8.082H16C16.387,8.082 16.7,8.395 16.7,8.782C16.7,9.168 16.387,9.482 16,9.482H8C7.613,9.482 7.3,9.168 7.3,8.782Z"),
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M7.3,12.782C7.3,12.395 7.613,12.082 8,12.082H13C13.387,12.082 13.7,12.395 13.7,12.782C13.7,13.168 13.387,13.482 13,13.482H8C7.613,13.482 7.3,13.168 7.3,12.782Z"),
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M4.35,3C4.009,3 3.711,3 3.466,3.02C3.206,3.041 2.943,3.088 2.689,3.218C2.312,3.41 2.006,3.716 1.815,4.092C1.685,4.346 1.638,4.609 1.617,4.869C1.597,5.115 1.597,5.412 1.597,5.753V21.863C1.597,22.117 1.734,22.351 1.956,22.475C2.178,22.598 2.45,22.592 2.665,22.458L7.984,19.162C8.19,19.034 8.228,19.013 8.262,18.999C8.303,18.983 8.346,18.97 8.389,18.963C8.425,18.957 8.468,18.955 8.711,18.955H19.649C19.991,18.955 20.288,18.955 20.534,18.935C20.794,18.913 21.057,18.866 21.311,18.737C21.687,18.545 21.993,18.239 22.185,17.863C22.314,17.608 22.362,17.346 22.383,17.086C22.403,16.84 22.403,16.543 22.403,16.201V5.753C22.403,5.412 22.403,5.115 22.383,4.869C22.362,4.609 22.314,4.346 22.185,4.092C21.993,3.716 21.687,3.41 21.311,3.218C21.057,3.088 20.794,3.041 20.534,3.02C20.288,3 19.991,3 19.649,3H4.35ZM3.324,4.465C3.348,4.453 3.406,4.43 3.58,4.415C3.762,4.401 4.001,4.4 4.377,4.4H19.623C19.999,4.4 20.238,4.401 20.42,4.415C20.593,4.43 20.651,4.453 20.675,4.465C20.788,4.523 20.88,4.615 20.938,4.728C20.95,4.752 20.973,4.81 20.987,4.983C21.002,5.165 21.003,5.404 21.003,5.78V16.175C21.003,16.55 21.002,16.79 20.987,16.972C20.973,17.145 20.95,17.203 20.938,17.227C20.88,17.34 20.788,17.432 20.675,17.489C20.651,17.501 20.593,17.525 20.42,17.539C20.238,17.554 19.999,17.555 19.623,17.555L8.672,17.555C8.49,17.555 8.324,17.554 8.157,17.582C8.011,17.607 7.868,17.648 7.731,17.704C7.575,17.767 7.434,17.855 7.279,17.951L2.997,20.606V5.78C2.997,5.404 2.997,5.165 3.012,4.983C3.026,4.81 3.05,4.752 3.062,4.728C3.12,4.615 3.211,4.523 3.324,4.465Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _community = it }
    }

private var _community: ImageVector? = null
