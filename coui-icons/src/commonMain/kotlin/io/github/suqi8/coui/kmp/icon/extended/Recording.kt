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

/** Recording: ColorOS `ic_recording_group_all` (com.coloros.soundrecorder_16.17.8). */
val COUIIcons.Recording: ImageVector
    get() {
        _recording?.let { return it }
        return ImageVector.Builder(
            name = "Recording",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M10.182,2.209C10.568,2.209 10.882,2.522 10.882,2.909V21.091C10.882,21.477 10.568,21.791 10.182,21.791C9.795,21.791 9.482,21.477 9.482,21.091V2.909C9.482,2.522 9.795,2.209 10.182,2.209ZM7.245,6.432C7.245,6.045 6.932,5.732 6.545,5.732C6.159,5.732 5.845,6.045 5.845,6.432V17.568C5.845,17.955 6.159,18.268 6.545,18.268C6.932,18.268 7.245,17.955 7.245,17.568V6.432ZM3.609,10.352C3.609,9.966 3.296,9.652 2.909,9.652C2.522,9.652 2.209,9.966 2.209,10.352V13.648C2.209,14.034 2.522,14.348 2.909,14.348C3.296,14.348 3.609,14.034 3.609,13.648V10.352ZM14.518,8.364C14.518,7.977 14.205,7.664 13.818,7.664C13.432,7.664 13.118,7.977 13.118,8.364V15.636C13.118,16.023 13.432,16.336 13.818,16.336C14.205,16.336 14.518,16.023 14.518,15.636L14.518,8.364ZM17.454,4.482C17.841,4.482 18.154,4.795 18.154,5.182V18.818C18.154,19.205 17.841,19.518 17.454,19.518C17.068,19.518 16.754,19.205 16.754,18.818V5.182C16.754,4.795 17.068,4.482 17.454,4.482ZM21.791,10.352C21.791,9.966 21.477,9.652 21.091,9.652C20.704,9.652 20.391,9.966 20.391,10.352V13.648C20.391,14.034 20.704,14.348 21.091,14.348C21.477,14.348 21.791,14.034 21.791,13.648V10.352Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _recording = it }
    }

private var _recording: ImageVector? = null
