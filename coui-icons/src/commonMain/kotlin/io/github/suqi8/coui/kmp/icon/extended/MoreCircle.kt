// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/**
 * MoreCircle: ColorOS has no drawing for this one, so it keeps the earlier symbol-font glyph
 * (its light weight, the closest to the ColorOS 1.4 stroke).
 */
val COUIIcons.MoreCircle: ImageVector
    get() {
        if (_moreCircle != null) return _moreCircle!!
        _moreCircle = ImageVector.Builder(
            name = "MoreCircle",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 1194.0f,
            viewportHeight = 1194.0f,
        ).apply {
            group(scaleY = -1.0f, translationY = 1194.0f) {
                addPath(
                    pathData = listOf(
                        PathNode.MoveTo(1094.5f, 596.3f),
                        PathNode.QuadTo(1094.5f, 731.5f, 1027.5f, 846.0f),
                        PathNode.QuadTo(960.5f, 960.5f, 846.7f, 1027.5f),
                        PathNode.QuadTo(732.9f, 1094.5f, 597.7f, 1094.5f),
                        PathNode.QuadTo(462.5f, 1094.5f, 348.0f, 1027.5f),
                        PathNode.QuadTo(233.5f, 960.5f, 166.5f, 846.0f),
                        PathNode.QuadTo(99.5f, 731.5f, 99.5f, 596.3f),
                        PathNode.QuadTo(99.5f, 461.1f, 166.5f, 347.3f),
                        PathNode.QuadTo(233.5f, 233.5f, 348.0f, 166.5f),
                        PathNode.QuadTo(462.5f, 99.5f, 597.7f, 99.5f),
                        PathNode.QuadTo(732.9f, 99.5f, 846.7f, 166.5f),
                        PathNode.QuadTo(960.5f, 233.5f, 1027.5f, 347.3f),
                        PathNode.QuadTo(1094.5f, 461.1f, 1094.5f, 596.3f),
                        PathNode.Close,
                        PathNode.MoveTo(159.5f, 596.5f),
                        PathNode.QuadTo(159.5f, 715.9f, 218.5f, 816.2f),
                        PathNode.QuadTo(277.5f, 916.5f, 378.0f, 975.5f),
                        PathNode.QuadTo(478.5f, 1034.5f, 598.0f, 1034.5f),
                        PathNode.QuadTo(716.5f, 1034.5f, 816.8f, 975.7f),
                        PathNode.QuadTo(917.2f, 916.8f, 976.3f, 816.2f),
                        PathNode.QuadTo(1035.5f, 715.5f, 1035.5f, 596.5f),
                        PathNode.QuadTo(1035.5f, 477.5f, 976.3f, 376.8f),
                        PathNode.QuadTo(917.2f, 276.2f, 816.8f, 217.3f),
                        PathNode.QuadTo(716.5f, 158.5f, 598.0f, 158.5f),
                        PathNode.QuadTo(478.5f, 158.5f, 378.0f, 217.5f),
                        PathNode.QuadTo(277.5f, 276.5f, 218.5f, 376.8f),
                        PathNode.QuadTo(159.5f, 477.1f, 159.5f, 596.5f),
                        PathNode.Close,
                        PathNode.MoveTo(428.5f, 596.2f),
                        PathNode.QuadTo(428.5f, 613.5f, 416.3f, 626.0f),
                        PathNode.QuadTo(404.1f, 638.5f, 387.3f, 638.5f),
                        PathNode.QuadTo(370.5f, 638.5f, 358.0f, 626.2f),
                        PathNode.QuadTo(345.5f, 614.0f, 345.5f, 596.4f),
                        PathNode.QuadTo(345.5f, 579.5f, 358.0f, 567.5f),
                        PathNode.QuadTo(370.5f, 555.5f, 387.5f, 555.5f),
                        PathNode.QuadTo(404.5f, 555.5f, 416.5f, 567.6f),
                        PathNode.QuadTo(428.5f, 579.6f, 428.5f, 596.2f),
                        PathNode.Close,
                        PathNode.MoveTo(638.5f, 596.2f),
                        PathNode.QuadTo(638.5f, 613.5f, 626.3f, 626.0f),
                        PathNode.QuadTo(614.1f, 638.5f, 597.3f, 638.5f),
                        PathNode.QuadTo(580.5f, 638.5f, 568.0f, 626.2f),
                        PathNode.QuadTo(555.5f, 614.0f, 555.5f, 596.4f),
                        PathNode.QuadTo(555.5f, 579.5f, 568.0f, 567.5f),
                        PathNode.QuadTo(580.5f, 555.5f, 597.5f, 555.5f),
                        PathNode.QuadTo(614.5f, 555.5f, 626.5f, 567.6f),
                        PathNode.QuadTo(638.5f, 579.6f, 638.5f, 596.2f),
                        PathNode.Close,
                        PathNode.MoveTo(848.5f, 596.2f),
                        PathNode.QuadTo(848.5f, 613.5f, 836.4f, 626.0f),
                        PathNode.QuadTo(824.4f, 638.5f, 807.8f, 638.5f),
                        PathNode.QuadTo(790.5f, 638.5f, 778.0f, 626.0f),
                        PathNode.QuadTo(765.5f, 613.5f, 765.5f, 596.5f),
                        PathNode.QuadTo(765.5f, 579.5f, 777.6f, 567.5f),
                        PathNode.QuadTo(789.7f, 555.5f, 807.1f, 555.5f),
                        PathNode.QuadTo(824.5f, 555.5f, 836.5f, 567.6f),
                        PathNode.QuadTo(848.5f, 579.6f, 848.5f, 596.2f),
                        PathNode.Close,
                    ),
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1f,
                    pathFillType = PathFillType.NonZero,
                )
            }
        }.build()
        return _moreCircle!!
    }

private var _moreCircle: ImageVector? = null
