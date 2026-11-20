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
 * Promotions: ColorOS has no drawing for this one, so it keeps the earlier symbol-font glyph
 * (its light weight, the closest to the ColorOS 1.4 stroke).
 */
val COUIIcons.Promotions: ImageVector
    get() {
        if (_promotions != null) return _promotions!!
        _promotions = ImageVector.Builder(
            name = "Promotions",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 1273.2f,
            viewportHeight = 1273.2f,
        ).apply {
            group(scaleY = -1.0f, translationY = 1273.2f) {
                addPath(
                    pathData = listOf(
                        PathNode.MoveTo(1167.1f, 736.6f),
                        PathNode.QuadTo(1167.1f, 845.6f, 1148.1f, 937.6f),
                        PathNode.QuadTo(1129.1f, 1029.6f, 1098.1f, 1083.1f),
                        PathNode.QuadTo(1067.1f, 1136.6f, 1033.1f, 1136.6f),
                        PathNode.QuadTo(1021.1f, 1136.6f, 1015.1f, 1133.6f),
                        PathNode.QuadTo(981.1f, 1120.6f, 931.1f, 1097.6f),
                        PathNode.QuadTo(881.1f, 1074.6f, 813.1f, 1041.6f),
                        PathNode.QuadTo(687.1f, 980.6f, 562.1f, 953.6f),
                        PathNode.QuadTo(553.1f, 940.6f, 544.1f, 908.6f),
                        PathNode.QuadTo(525.1f, 837.6f, 525.1f, 733.6f),
                        PathNode.QuadTo(525.1f, 629.6f, 545.1f, 559.6f),
                        PathNode.QuadTo(551.1f, 536.6f, 563.1f, 516.6f),
                        PathNode.QuadTo(676.1f, 489.6f, 785.1f, 440.6f),
                        PathNode.QuadTo(878.1f, 396.6f, 933.1f, 372.1f),
                        PathNode.QuadTo(988.1f, 347.6f, 1019.1f, 337.6f),
                        PathNode.QuadTo(1024.1f, 336.6f, 1033.1f, 336.6f),
                        PathNode.QuadTo(1067.1f, 336.6f, 1098.1f, 390.1f),
                        PathNode.QuadTo(1129.1f, 443.6f, 1148.1f, 535.6f),
                        PathNode.QuadTo(1167.1f, 627.6f, 1167.1f, 736.6f),
                        PathNode.Close,
                        PathNode.MoveTo(579.1f, 231.6f),
                        PathNode.LineTo(499.1f, 535.6f),
                        PathNode.QuadTo(486.1f, 584.6f, 480.1f, 656.1f),
                        PathNode.QuadTo(474.1f, 727.6f, 478.6f, 804.6f),
                        PathNode.QuadTo(483.1f, 881.6f, 501.1f, 945.6f),
                        PathNode.HorizontalTo(318.1f),
                        PathNode.QuadTo(261.1f, 945.6f, 212.1f, 917.1f),
                        PathNode.QuadTo(163.1f, 888.6f, 134.6f, 839.6f),
                        PathNode.QuadTo(106.1f, 790.6f, 106.1f, 733.6f),
                        PathNode.QuadTo(106.1f, 675.6f, 134.6f, 626.6f),
                        PathNode.QuadTo(163.1f, 577.6f, 211.6f, 549.1f),
                        PathNode.QuadTo(260.1f, 520.6f, 318.1f, 520.6f),
                        PathNode.HorizontalTo(360.1f),
                        PathNode.LineTo(440.1f, 196.6f),
                        PathNode.QuadTo(447.1f, 167.6f, 472.6f, 152.1f),
                        PathNode.QuadTo(498.1f, 136.6f, 527.1f, 144.6f),
                        PathNode.QuadTo(557.1f, 151.6f, 572.1f, 177.1f),
                        PathNode.QuadTo(587.1f, 202.6f, 579.1f, 231.6f),
                        PathNode.Close,
                        PathNode.MoveTo(956.1f, 737.6f),
                        PathNode.QuadTo(956.1f, 825.6f, 965.1f, 897.1f),
                        PathNode.QuadTo(974.1f, 968.6f, 991.6f, 1010.1f),
                        PathNode.QuadTo(1009.1f, 1051.6f, 1032.1f, 1051.6f),
                        PathNode.QuadTo(1053.1f, 1051.6f, 1070.6f, 1008.1f),
                        PathNode.QuadTo(1088.1f, 964.6f, 1098.1f, 892.6f),
                        PathNode.QuadTo(1108.1f, 820.6f, 1108.1f, 737.6f),
                        PathNode.QuadTo(1108.1f, 653.6f, 1098.1f, 581.6f),
                        PathNode.QuadTo(1088.1f, 509.6f, 1070.6f, 466.1f),
                        PathNode.QuadTo(1053.1f, 422.6f, 1032.1f, 422.6f),
                        PathNode.QuadTo(1009.1f, 422.6f, 991.6f, 464.1f),
                        PathNode.QuadTo(974.1f, 505.6f, 965.1f, 577.1f),
                        PathNode.QuadTo(956.1f, 648.6f, 956.1f, 737.6f),
                        PathNode.Close,
                    ),
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1f,
                    pathFillType = PathFillType.NonZero,
                )
            }
        }.build()
        return _promotions!!
    }

private var _promotions: ImageVector? = null
