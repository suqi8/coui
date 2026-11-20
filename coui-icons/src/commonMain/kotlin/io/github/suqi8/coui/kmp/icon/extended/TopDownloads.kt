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
 * TopDownloads: ColorOS has no drawing for this one, so it keeps the earlier symbol-font glyph
 * (its light weight, the closest to the ColorOS 1.4 stroke).
 */
val COUIIcons.TopDownloads: ImageVector
    get() {
        if (_topDownloads != null) return _topDownloads!!
        _topDownloads = ImageVector.Builder(
            name = "TopDownloads",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 1257.6f,
            viewportHeight = 1257.6f,
        ).apply {
            group(scaleY = -1.0f, translationY = 1257.6f) {
                addPath(
                    pathData = listOf(
                        PathNode.MoveTo(307.8f, 181.3f),
                        PathNode.QuadTo(310.8f, 206.3f, 327.8f, 223.8f),
                        PathNode.QuadTo(344.8f, 241.3f, 370.8f, 245.3f),
                        PathNode.LineTo(492.8f, 264.3f),
                        PathNode.QuadTo(514.8f, 267.3f, 525.3f, 269.3f),
                        PathNode.QuadTo(535.8f, 271.3f, 542.8f, 277.3f),
                        PathNode.QuadTo(558.8f, 288.3f, 565.8f, 303.3f),
                        PathNode.QuadTo(568.8f, 311.3f, 569.8f, 323.8f),
                        PathNode.QuadTo(570.8f, 336.3f, 570.8f, 353.3f),
                        PathNode.VerticalTo(435.3f),
                        PathNode.QuadTo(484.8f, 448.3f, 414.3f, 499.8f),
                        PathNode.QuadTo(343.8f, 551.3f, 303.8f, 628.3f),
                        PathNode.QuadTo(263.8f, 705.3f, 263.8f, 794.3f),
                        PathNode.VerticalTo(1065.3f),
                        PathNode.QuadTo(263.8f, 1076.3f, 271.3f, 1083.3f),
                        PathNode.QuadTo(278.8f, 1090.3f, 288.8f, 1090.3f),
                        PathNode.HorizontalTo(969.8f),
                        PathNode.QuadTo(980.8f, 1090.3f, 987.8f, 1083.3f),
                        PathNode.QuadTo(994.8f, 1076.3f, 994.8f, 1065.3f),
                        PathNode.VerticalTo(794.3f),
                        PathNode.QuadTo(994.8f, 703.3f, 952.3f, 624.3f),
                        PathNode.QuadTo(909.8f, 545.3f, 836.3f, 494.3f),
                        PathNode.QuadTo(762.8f, 443.3f, 673.8f, 433.3f),
                        PathNode.VerticalTo(353.3f),
                        PathNode.QuadTo(673.8f, 318.3f, 678.8f, 305.3f),
                        PathNode.QuadTo(684.8f, 288.3f, 701.8f, 277.3f),
                        PathNode.QuadTo(709.8f, 271.3f, 719.8f, 269.3f),
                        PathNode.QuadTo(729.8f, 267.3f, 750.8f, 264.3f),
                        PathNode.LineTo(874.8f, 246.3f),
                        PathNode.QuadTo(901.8f, 242.3f, 918.8f, 223.8f),
                        PathNode.QuadTo(935.8f, 205.3f, 936.8f, 181.3f),
                        PathNode.QuadTo(937.8f, 175.3f, 933.8f, 171.3f),
                        PathNode.QuadTo(929.8f, 167.3f, 924.8f, 167.3f),
                        PathNode.HorizontalTo(320.8f),
                        PathNode.QuadTo(314.8f, 167.3f, 311.3f, 171.3f),
                        PathNode.QuadTo(307.8f, 175.3f, 307.8f, 181.3f),
                        PathNode.Close,
                        PathNode.MoveTo(104.8f, 869.3f),
                        PathNode.VerticalTo(1003.3f),
                        PathNode.QuadTo(104.8f, 1017.3f, 115.3f, 1027.8f),
                        PathNode.QuadTo(125.8f, 1038.3f, 139.8f, 1038.3f),
                        PathNode.HorizontalTo(183.8f),
                        PathNode.QuadTo(194.8f, 1038.3f, 204.3f, 1029.3f),
                        PathNode.QuadTo(213.8f, 1020.3f, 213.8f, 1008.3f),
                        PathNode.VerticalTo(783.3f),
                        PathNode.QuadTo(213.8f, 777.3f, 210.8f, 773.8f),
                        PathNode.QuadTo(207.8f, 770.3f, 202.8f, 770.3f),
                        PathNode.QuadTo(177.8f, 770.3f, 155.3f, 783.3f),
                        PathNode.QuadTo(132.8f, 796.3f, 118.8f, 818.8f),
                        PathNode.QuadTo(104.8f, 841.3f, 104.8f, 869.3f),
                        PathNode.Close,
                        PathNode.MoveTo(1044.8f, 783.3f),
                        PathNode.VerticalTo(1008.3f),
                        PathNode.QuadTo(1044.8f, 1020.3f, 1053.8f, 1029.3f),
                        PathNode.QuadTo(1062.8f, 1038.3f, 1074.8f, 1038.3f),
                        PathNode.HorizontalTo(1117.8f),
                        PathNode.QuadTo(1132.8f, 1038.3f, 1142.8f, 1027.8f),
                        PathNode.QuadTo(1152.8f, 1017.3f, 1152.8f, 1003.3f),
                        PathNode.VerticalTo(869.3f),
                        PathNode.QuadTo(1152.8f, 841.3f, 1139.3f, 818.8f),
                        PathNode.QuadTo(1125.8f, 796.3f, 1103.3f, 783.3f),
                        PathNode.QuadTo(1080.8f, 770.3f, 1054.8f, 770.3f),
                        PathNode.QuadTo(1049.8f, 770.3f, 1047.3f, 773.8f),
                        PathNode.QuadTo(1044.8f, 777.3f, 1044.8f, 783.3f),
                        PathNode.Close,
                        PathNode.MoveTo(625.8f, 611.3f),
                        PathNode.HorizontalTo(654.8f),
                        PathNode.QuadTo(659.8f, 611.3f, 663.3f, 615.3f),
                        PathNode.QuadTo(666.8f, 619.3f, 666.8f, 623.3f),
                        PathNode.VerticalTo(933.3f),
                        PathNode.QuadTo(666.8f, 939.3f, 663.3f, 942.3f),
                        PathNode.QuadTo(659.8f, 945.3f, 654.8f, 945.3f),
                        PathNode.HorizontalTo(632.8f),
                        PathNode.QuadTo(622.8f, 945.3f, 617.8f, 942.3f),
                        PathNode.LineTo(550.8f, 907.3f),
                        PathNode.QuadTo(546.8f, 905.3f, 544.8f, 901.3f),
                        PathNode.QuadTo(542.8f, 897.3f, 542.8f, 893.3f),
                        PathNode.VerticalTo(864.3f),
                        PathNode.QuadTo(542.8f, 857.3f, 547.3f, 854.8f),
                        PathNode.QuadTo(551.8f, 852.3f, 557.8f, 855.3f),
                        PathNode.LineTo(613.8f, 885.3f),
                        PathNode.VerticalTo(623.3f),
                        PathNode.QuadTo(613.8f, 619.3f, 617.8f, 615.3f),
                        PathNode.QuadTo(621.8f, 611.3f, 625.8f, 611.3f),
                        PathNode.Close,
                    ),
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1f,
                    pathFillType = PathFillType.NonZero,
                )
            }
        }.build()
        return _topDownloads!!
    }

private var _topDownloads: ImageVector? = null
