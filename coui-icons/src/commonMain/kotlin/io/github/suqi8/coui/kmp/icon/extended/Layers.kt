// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Layers: ColorOS `de_bottomlayer` (com.coloros.gallery3d_16.50.46). */
val COUIIcons.Layers: ImageVector
    get() {
        _layers?.let { return it }
        return ImageVector.Builder(
            name = "Layers",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 20f,
            viewportHeight = 20f,
        ).apply {
            addPath(
                pathData = addPathNodes("M8.187,7.119C8.715,6.805 9.373,6.804 9.9,7.118L16.995,11.346C17.65,11.736 17.65,12.686 16.995,13.076L9.9,17.304C9.372,17.618 8.715,17.617 8.187,17.303L1.093,13.075C0.479,12.709 0.44,11.853 0.977,11.426L1.093,11.347L8.187,7.119ZM9.297,8.132C9.141,8.039 8.946,8.039 8.791,8.132L1.944,12.211L8.791,16.29C8.946,16.383 9.141,16.383 9.297,16.29L16.139,12.211L9.297,8.132Z"),
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M0.797,7.252L4.922,4.794L8.492,2.666C8.834,2.463 9.259,2.463 9.601,2.666L10.078,2.95"),
                fill = SolidColor(Color.Black),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.18f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M15.808,7.641C16.009,7.641 16.185,7.54 16.292,7.388L18.222,5.459L18.297,5.366C18.448,5.137 18.423,4.826 18.222,4.624C18.02,4.422 17.708,4.398 17.479,4.549L17.386,4.624L16.398,5.613V1.939L16.386,1.82C16.331,1.552 16.093,1.35 15.808,1.35C15.524,1.35 15.285,1.552 15.23,1.82L15.219,1.939V5.637L14.207,4.624C13.976,4.394 13.602,4.394 13.372,4.624C13.142,4.854 13.142,5.229 13.372,5.459L15.38,7.466C15.467,7.553 15.574,7.608 15.686,7.629C15.726,7.637 15.767,7.641 15.808,7.641Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _layers = it }
    }

private var _layers: ImageVector? = null
