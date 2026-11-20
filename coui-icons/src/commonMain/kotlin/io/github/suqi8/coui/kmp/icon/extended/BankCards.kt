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

/** BankCards: ColorOS `event_info_card_icon` (com.coloros.calendar_17.6.20). */
val COUIIcons.BankCards: ImageVector
    get() {
        _bankCards?.let { return it }
        return ImageVector.Builder(
            name = "BankCards",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 18f,
            viewportHeight = 18f,
            autoMirror = true,
        ).apply {
            addPath(
                pathData = addPathNodes("M3.04,2.85H14.96C15.215,2.85 15.439,2.85 15.623,2.865C15.818,2.881 16.015,2.916 16.206,3.014C16.488,3.157 16.717,3.387 16.861,3.669C16.958,3.86 16.994,4.057 17.01,4.252C17.025,4.436 17.025,4.659 17.025,4.915V6.601H0.975V4.915C0.975,4.659 0.975,4.436 0.99,4.252C1.006,4.057 1.041,3.86 1.138,3.669C1.282,3.387 1.511,3.157 1.794,3.014C1.984,2.916 2.181,2.881 2.376,2.865C2.561,2.85 2.784,2.85 3.04,2.85Z"),
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M0.975,7.651V13.085C0.975,13.341 0.975,13.564 0.99,13.748C1.006,13.943 1.041,14.14 1.138,14.331C1.282,14.613 1.511,14.843 1.794,14.986C1.984,15.084 2.181,15.119 2.376,15.135C2.561,15.15 2.784,15.15 3.04,15.15H14.96C15.215,15.15 15.439,15.15 15.623,15.135C15.818,15.119 16.015,15.084 16.206,14.986C16.488,14.843 16.717,14.613 16.861,14.331C16.958,14.14 16.994,13.943 17.01,13.748C17.025,13.564 17.025,13.341 17.025,13.085V7.651H0.975ZM3.646,11.77C3.662,11.686 3.736,11.625 3.822,11.625H6.228C6.304,11.625 6.361,11.694 6.346,11.769L6.104,12.98C6.087,13.064 6.013,13.125 5.927,13.125H3.521C3.445,13.125 3.388,13.056 3.403,12.981L3.646,11.77Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _bankCards = it }
    }

private var _bankCards: ImageVector? = null
