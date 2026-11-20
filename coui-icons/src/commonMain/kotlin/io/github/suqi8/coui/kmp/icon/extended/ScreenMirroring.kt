// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** ScreenMirroring: ColorOS `icon_chip_screencast` (com.coloros.gallery3d_16.50.46). */
val COUIIcons.ScreenMirroring: ImageVector
    get() {
        _screenMirroring?.let { return it }
        return ImageVector.Builder(
            name = "ScreenMirroring",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            group() {
                group(clipPathData = addPathNodes("M0,0h24v24h-24z")) {
                    addPath(
                        pathData = addPathNodes("M15.549,12.077C13.587,10.115 10.411,10.115 8.449,12.077"),
                        stroke = SolidColor(Color.Black),
                        strokeLineWidth = 1.4f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round,
                    )
                    addPath(
                        pathData = addPathNodes("M17.622,10.003C14.514,6.895 9.485,6.895 6.377,10.003"),
                        stroke = SolidColor(Color.Black),
                        strokeLineWidth = 1.4f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round,
                    )
                    group() {
                        group(clipPathData = addPathNodes("M21.16,20.774L14.313,13.927C13.078,12.693 10.922,12.693 9.688,13.927L2.84,20.774H1V3H23V21L21.16,20.774Z")) {
                            addPath(
                                pathData = addPathNodes("M20.7,3.97H3.3C2.582,3.97 2,4.552 2,5.27V14.67C2,15.388 2.582,15.97 3.3,15.97H20.7C21.418,15.97 22,15.388 22,14.67V5.27C22,4.552 21.418,3.97 20.7,3.97Z"),
                                stroke = SolidColor(Color.Black),
                                strokeLineWidth = 1.4f,
                                strokeLineCap = StrokeCap.Butt,
                                strokeLineJoin = StrokeJoin.Miter,
                            )
                        }
                    }
                    addPath(
                        pathData = addPathNodes("M6.475,19.969L11.103,15.341C11.599,14.845 12.403,14.845 12.899,15.341L17.527,19.969H6.475Z"),
                        stroke = SolidColor(Color.Black),
                        strokeLineWidth = 1.4f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round,
                    )
                }
            }
        }.build().also { _screenMirroring = it }
    }

private var _screenMirroring: ImageVector? = null
