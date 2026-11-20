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
 * AppRecording: ColorOS has no drawing for this one, so it keeps the earlier symbol-font glyph
 * (its light weight, the closest to the ColorOS 1.4 stroke).
 */
val COUIIcons.AppRecording: ImageVector
    get() {
        if (_appRecording != null) return _appRecording!!
        _appRecording = ImageVector.Builder(
            name = "AppRecording",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 1176.0f,
            viewportHeight = 1176.0f,
        ).apply {
            group(scaleY = -1.0f, translationY = 1176.0f) {
                addPath(
                    pathData = listOf(
                        PathNode.MoveTo(885.3f, 144.8f),
                        PathNode.QuadTo(949.9f, 179.0f, 977.9f, 238.5f),
                        PathNode.QuadTo(994.2f, 270.1f, 998.0f, 316.0f),
                        PathNode.QuadTo(1001.8f, 361.8f, 1001.8f, 471.3f),
                        PathNode.VerticalTo(589.6f),
                        PathNode.QuadTo(1001.8f, 603.7f, 995.9f, 609.6f),
                        PathNode.QuadTo(990.1f, 615.4f, 976.0f, 615.4f),
                        PathNode.HorizontalTo(937.2f),
                        PathNode.QuadTo(922.9f, 615.4f, 917.7f, 609.6f),
                        PathNode.QuadTo(912.3f, 603.7f, 912.3f, 589.6f),
                        PathNode.VerticalTo(471.3f),
                        PathNode.QuadTo(912.3f, 364.7f, 909.4f, 331.6f),
                        PathNode.QuadTo(904.9f, 292.1f, 898.2f, 279.8f),
                        PathNode.QuadTo(877.6f, 241.3f, 843.7f, 225.3f),
                        PathNode.QuadTo(827.1f, 217.8f, 792.8f, 214.9f),
                        PathNode.QuadTo(759.6f, 212.0f, 652.2f, 212.0f),
                        PathNode.HorizontalTo(487.6f),
                        PathNode.QuadTo(379.1f, 212.0f, 346.5f, 214.9f),
                        PathNode.QuadTo(311.9f, 217.7f, 295.0f, 225.3f),
                        PathNode.QuadTo(263.0f, 240.5f, 240.6f, 279.8f),
                        PathNode.QuadTo(232.3f, 296.3f, 230.2f, 331.6f),
                        PathNode.QuadTo(227.4f, 364.7f, 227.4f, 471.3f),
                        PathNode.VerticalTo(635.9f),
                        PathNode.QuadTo(227.4f, 743.4f, 230.2f, 776.2f),
                        PathNode.QuadTo(232.1f, 808.9f, 240.6f, 828.4f),
                        PathNode.QuadTo(263.0f, 868.8f, 295.0f, 882.9f),
                        PathNode.QuadTo(314.4f, 890.4f, 346.9f, 893.3f),
                        PathNode.QuadTo(379.1f, 896.1f, 476.2f, 896.1f),
                        PathNode.QuadTo(487.1f, 896.1f, 492.5f, 901.5f),
                        PathNode.QuadTo(498.0f, 906.8f, 498.0f, 916.9f),
                        PathNode.VerticalTo(965.0f),
                        PathNode.QuadTo(498.0f, 976.2f, 492.6f, 981.8f),
                        PathNode.QuadTo(487.2f, 987.4f, 476.4f, 987.4f),
                        PathNode.QuadTo(373.4f, 987.4f, 329.3f, 983.7f),
                        PathNode.QuadTo(285.3f, 980.1f, 254.3f, 963.4f),
                        PathNode.QuadTo(191.6f, 931.1f, 159.9f, 869.3f),
                        PathNode.QuadTo(143.7f, 838.0f, 140.3f, 792.2f),
                        PathNode.QuadTo(136.9f, 746.4f, 136.9f, 635.6f),
                        PathNode.VerticalTo(471.6f),
                        PathNode.QuadTo(136.9f, 361.8f, 140.3f, 316.0f),
                        PathNode.QuadTo(143.7f, 270.1f, 159.9f, 238.8f),
                        PathNode.QuadTo(191.6f, 177.1f, 254.4f, 144.9f),
                        PathNode.QuadTo(285.4f, 128.3f, 331.2f, 124.5f),
                        PathNode.QuadTo(377.1f, 120.7f, 487.9f, 120.7f),
                        PathNode.HorizontalTo(651.8f),
                        PathNode.QuadTo(761.7f, 120.7f, 807.4f, 124.4f),
                        PathNode.QuadTo(853.0f, 128.1f, 885.3f, 144.8f),
                        PathNode.Close,
                        PathNode.MoveTo(652.8f, 770.0f),
                        PathNode.VerticalTo(968.0f),
                        PathNode.QuadTo(652.8f, 975.3f, 647.4f, 979.6f),
                        PathNode.QuadTo(641.9f, 983.9f, 634.0f, 983.9f),
                        PathNode.HorizontalTo(618.9f),
                        PathNode.QuadTo(611.0f, 983.9f, 606.0f, 979.6f),
                        PathNode.QuadTo(601.0f, 975.3f, 601.0f, 968.0f),
                        PathNode.VerticalTo(770.0f),
                        PathNode.QuadTo(601.0f, 763.0f, 605.8f, 758.0f),
                        PathNode.QuadTo(610.5f, 753.1f, 617.8f, 753.1f),
                        PathNode.HorizontalTo(636.0f),
                        PathNode.QuadTo(642.6f, 753.1f, 647.7f, 758.1f),
                        PathNode.QuadTo(652.8f, 763.0f, 652.8f, 770.0f),
                        PathNode.Close,
                        PathNode.MoveTo(749.9f, 805.2f),
                        PathNode.VerticalTo(932.8f),
                        PathNode.QuadTo(749.9f, 940.1f, 744.4f, 944.4f),
                        PathNode.QuadTo(739.0f, 948.7f, 731.0f, 948.7f),
                        PathNode.HorizontalTo(715.9f),
                        PathNode.QuadTo(708.1f, 948.7f, 703.0f, 944.4f),
                        PathNode.QuadTo(698.0f, 940.1f, 698.0f, 932.8f),
                        PathNode.VerticalTo(805.2f),
                        PathNode.QuadTo(698.0f, 798.2f, 702.8f, 793.2f),
                        PathNode.QuadTo(707.5f, 788.3f, 714.9f, 788.3f),
                        PathNode.HorizontalTo(733.0f),
                        PathNode.QuadTo(739.7f, 788.3f, 744.7f, 793.3f),
                        PathNode.QuadTo(749.9f, 798.2f, 749.9f, 805.2f),
                        PathNode.Close,
                        PathNode.MoveTo(846.0f, 708.2f),
                        PathNode.VerticalTo(1039.4f),
                        PathNode.QuadTo(846.0f, 1046.6f, 840.5f, 1050.9f),
                        PathNode.QuadTo(835.1f, 1055.3f, 827.1f, 1055.3f),
                        PathNode.HorizontalTo(812.0f),
                        PathNode.QuadTo(804.2f, 1055.3f, 799.1f, 1050.9f),
                        PathNode.QuadTo(794.1f, 1046.6f, 794.1f, 1039.4f),
                        PathNode.VerticalTo(708.2f),
                        PathNode.QuadTo(794.1f, 701.1f, 798.9f, 696.2f),
                        PathNode.QuadTo(803.6f, 691.3f, 811.0f, 691.3f),
                        PathNode.HorizontalTo(829.1f),
                        PathNode.QuadTo(835.8f, 691.3f, 840.8f, 696.2f),
                        PathNode.QuadTo(846.0f, 701.1f, 846.0f, 708.2f),
                        PathNode.Close,
                        PathNode.MoveTo(942.1f, 770.0f),
                        PathNode.VerticalTo(968.0f),
                        PathNode.QuadTo(942.1f, 975.3f, 936.6f, 979.6f),
                        PathNode.QuadTo(931.2f, 983.9f, 923.2f, 983.9f),
                        PathNode.HorizontalTo(908.1f),
                        PathNode.QuadTo(900.3f, 983.9f, 895.2f, 979.6f),
                        PathNode.QuadTo(890.2f, 975.3f, 890.2f, 968.0f),
                        PathNode.VerticalTo(770.0f),
                        PathNode.QuadTo(890.2f, 763.0f, 895.0f, 758.0f),
                        PathNode.QuadTo(899.7f, 753.1f, 907.0f, 753.1f),
                        PathNode.HorizontalTo(925.2f),
                        PathNode.QuadTo(931.9f, 753.1f, 936.9f, 758.1f),
                        PathNode.QuadTo(942.1f, 763.0f, 942.1f, 770.0f),
                        PathNode.Close,
                        PathNode.MoveTo(1039.1f, 822.4f),
                        PathNode.VerticalTo(914.8f),
                        PathNode.QuadTo(1039.1f, 922.0f, 1033.7f, 926.3f),
                        PathNode.QuadTo(1028.2f, 930.6f, 1020.3f, 930.6f),
                        PathNode.HorizontalTo(1005.2f),
                        PathNode.QuadTo(997.3f, 930.6f, 992.2f, 926.3f),
                        PathNode.QuadTo(987.3f, 922.0f, 987.3f, 914.8f),
                        PathNode.VerticalTo(822.4f),
                        PathNode.QuadTo(987.3f, 815.3f, 992.0f, 810.4f),
                        PathNode.QuadTo(996.8f, 805.4f, 1004.1f, 805.4f),
                        PathNode.HorizontalTo(1022.3f),
                        PathNode.QuadTo(1028.9f, 805.4f, 1034.0f, 810.4f),
                        PathNode.QuadTo(1039.1f, 815.3f, 1039.1f, 822.4f),
                        PathNode.Close,
                    ),
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1f,
                    pathFillType = PathFillType.NonZero,
                )
            }
        }.build()
        return _appRecording!!
    }

private var _appRecording: ImageVector? = null
