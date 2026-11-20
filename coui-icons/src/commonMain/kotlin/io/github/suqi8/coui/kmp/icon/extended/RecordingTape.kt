// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** RecordingTape: ColorOS `ic_recording_group_normal` (com.coloros.soundrecorder_16.17.8). */
val COUIIcons.RecordingTape: ImageVector
    get() {
        _recordingTape?.let { return it }
        return ImageVector.Builder(
            name = "RecordingTape",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            group() {
                group(clipPathData = addPathNodes("M0,0h24v24h-24z")) {
                    addPath(
                        pathData = addPathNodes("M8,10.45C7.144,10.45 6.45,11.144 6.45,12C6.45,12.856 7.144,13.55 8,13.55C8.856,13.55 9.55,12.856 9.55,12C9.55,11.144 8.856,10.45 8,10.45ZM10.51,10.45C10.789,10.9 10.95,11.431 10.95,12C10.95,13.629 9.629,14.95 8,14.95C6.371,14.95 5.05,13.629 5.05,12C5.05,10.371 6.371,9.05 8,9.05H16C17.629,9.05 18.95,10.371 18.95,12C18.95,13.629 17.629,14.95 16,14.95C14.371,14.95 13.05,13.629 13.05,12C13.05,11.431 13.211,10.9 13.489,10.45H10.51ZM16,10.45C15.144,10.45 14.45,11.144 14.45,12C14.45,12.856 15.144,13.55 16,13.55C16.856,13.55 17.55,12.856 17.55,12C17.55,11.144 16.856,10.45 16,10.45Z"),
                        pathFillType = PathFillType.EvenOdd,
                        fill = SolidColor(Color.Black),
                    )
                    addPath(
                        pathData = addPathNodes("M3.3,4L20.7,4A1.3,1.3 0,0 1,22 5.3L22,18.7A1.3,1.3 0,0 1,20.7 20L3.3,20A1.3,1.3 0,0 1,2 18.7L2,5.3A1.3,1.3 0,0 1,3.3 4z"),
                        stroke = SolidColor(Color.Black),
                        strokeLineWidth = 1.4f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Miter,
                    )
                }
            }
        }.build().also { _recordingTape = it }
    }

private var _recordingTape: ImageVector? = null
