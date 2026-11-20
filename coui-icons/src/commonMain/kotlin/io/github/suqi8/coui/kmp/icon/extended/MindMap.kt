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
 * MindMap: ColorOS has no drawing for this one, so it keeps the earlier symbol-font glyph
 * (its light weight, the closest to the ColorOS 1.4 stroke).
 */
val COUIIcons.MindMap: ImageVector
    get() {
        if (_mindMap != null) return _mindMap!!
        _mindMap = ImageVector.Builder(
            name = "MindMap",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 1203.6f,
            viewportHeight = 1203.6f,
        ).apply {
            group(scaleY = -1.0f, translationY = 1203.6f) {
                addPath(
                    pathData = listOf(
                        PathNode.MoveTo(887.3f, 167.2f),
                        PathNode.VerticalTo(185.4f),
                        PathNode.QuadTo(887.3f, 196.8f, 881.3f, 201.3f),
                        PathNode.QuadTo(875.3f, 205.8f, 866.3f, 205.8f),
                        PathNode.HorizontalTo(631.3f),
                        PathNode.QuadTo(568.3f, 205.8f, 539.2f, 208.0f),
                        PathNode.QuadTo(510.2f, 210.1f, 486.3f, 218.8f),
                        PathNode.QuadTo(446.3f, 231.8f, 413.3f, 261.8f),
                        PathNode.QuadTo(396.3f, 277.8f, 382.3f, 299.8f),
                        PathNode.QuadTo(368.2f, 321.8f, 343.3f, 367.8f),
                        PathNode.LineTo(333.3f, 385.8f),
                        PathNode.LineTo(236.3f, 565.8f),
                        PathNode.QuadTo(235.3f, 568.8f, 236.0f, 570.3f),
                        PathNode.QuadTo(236.6f, 571.8f, 239.3f, 571.8f),
                        PathNode.HorizontalTo(762.3f),
                        PathNode.QuadTo(769.5f, 571.8f, 774.4f, 577.4f),
                        PathNode.QuadTo(779.3f, 582.9f, 779.3f, 589.9f),
                        PathNode.VerticalTo(612.8f),
                        PathNode.QuadTo(779.3f, 619.8f, 774.4f, 625.3f),
                        PathNode.QuadTo(769.5f, 630.8f, 762.3f, 630.8f),
                        PathNode.HorizontalTo(241.3f),
                        PathNode.QuadTo(233.3f, 630.8f, 236.3f, 637.8f),
                        PathNode.LineTo(335.3f, 820.8f),
                        PathNode.QuadTo(363.3f, 872.8f, 378.8f, 897.3f),
                        PathNode.QuadTo(394.3f, 921.8f, 411.0f, 938.5f),
                        PathNode.QuadTo(444.3f, 971.8f, 488.6f, 985.8f),
                        PathNode.QuadTo(511.3f, 993.8f, 539.3f, 995.8f),
                        PathNode.QuadTo(567.3f, 997.8f, 627.3f, 997.8f),
                        PathNode.HorizontalTo(867.3f),
                        PathNode.QuadTo(876.3f, 997.8f, 881.8f, 1002.3f),
                        PathNode.QuadTo(887.3f, 1006.8f, 887.3f, 1016.8f),
                        PathNode.VerticalTo(1036.8f),
                        PathNode.QuadTo(887.3f, 1047.8f, 881.8f, 1052.3f),
                        PathNode.QuadTo(876.3f, 1056.8f, 867.3f, 1056.8f),
                        PathNode.HorizontalTo(661.3f),
                        PathNode.QuadTo(576.3f, 1056.8f, 540.3f, 1054.8f),
                        PathNode.QuadTo(504.3f, 1052.8f, 474.3f, 1043.8f),
                        PathNode.QuadTo(418.3f, 1023.8f, 376.3f, 984.8f),
                        PathNode.QuadTo(354.4f, 964.8f, 334.9f, 934.3f),
                        PathNode.QuadTo(315.3f, 903.8f, 274.3f, 828.8f),
                        PathNode.LineTo(172.3f, 645.8f),
                        PathNode.QuadTo(167.7f, 636.9f, 163.5f, 633.9f),
                        PathNode.QuadTo(159.3f, 630.8f, 152.3f, 630.8f),
                        PathNode.HorizontalTo(117.3f),
                        PathNode.QuadTo(110.8f, 630.8f, 105.5f, 625.3f),
                        PathNode.QuadTo(100.3f, 619.8f, 100.3f, 612.3f),
                        PathNode.VerticalTo(589.8f),
                        PathNode.QuadTo(100.3f, 582.9f, 105.5f, 577.3f),
                        PathNode.QuadTo(110.8f, 571.8f, 117.3f, 571.8f),
                        PathNode.HorizontalTo(152.3f),
                        PathNode.QuadTo(164.9f, 571.8f, 172.3f, 559.8f),
                        PathNode.LineTo(271.3f, 378.8f),
                        PathNode.QuadTo(316.3f, 298.8f, 336.3f, 267.3f),
                        PathNode.QuadTo(356.3f, 235.8f, 379.3f, 215.8f),
                        PathNode.QuadTo(420.5f, 179.2f, 471.3f, 161.8f),
                        PathNode.QuadTo(501.2f, 151.5f, 538.7f, 149.1f),
                        PathNode.QuadTo(576.3f, 146.8f, 666.3f, 146.8f),
                        PathNode.HorizontalTo(866.3f),
                        PathNode.QuadTo(875.3f, 146.8f, 881.3f, 151.3f),
                        PathNode.QuadTo(887.3f, 155.8f, 887.3f, 167.2f),
                        PathNode.Close,
                        PathNode.MoveTo(1103.3f, 175.8f),
                        PathNode.QuadTo(1103.3f, 200.4f, 1086.1f, 217.6f),
                        PathNode.QuadTo(1069.0f, 234.8f, 1045.3f, 234.8f),
                        PathNode.QuadTo(1020.3f, 234.8f, 1003.8f, 217.6f),
                        PathNode.QuadTo(987.3f, 200.4f, 987.3f, 175.8f),
                        PathNode.QuadTo(987.3f, 152.1f, 1003.8f, 135.0f),
                        PathNode.QuadTo(1020.3f, 117.8f, 1045.2f, 117.8f),
                        PathNode.QuadTo(1069.3f, 117.8f, 1086.3f, 135.0f),
                        PathNode.QuadTo(1103.3f, 152.1f, 1103.3f, 175.8f),
                        PathNode.Close,
                        PathNode.MoveTo(1103.3f, 600.8f),
                        PathNode.QuadTo(1103.3f, 625.4f, 1086.1f, 642.6f),
                        PathNode.QuadTo(1069.0f, 659.8f, 1045.3f, 659.8f),
                        PathNode.QuadTo(1020.3f, 659.8f, 1003.8f, 642.6f),
                        PathNode.QuadTo(987.3f, 625.4f, 987.3f, 600.8f),
                        PathNode.QuadTo(987.3f, 577.1f, 1003.8f, 560.0f),
                        PathNode.QuadTo(1020.3f, 542.8f, 1045.2f, 542.8f),
                        PathNode.QuadTo(1069.3f, 542.8f, 1086.3f, 560.0f),
                        PathNode.QuadTo(1103.3f, 577.1f, 1103.3f, 600.8f),
                        PathNode.Close,
                        PathNode.MoveTo(1103.3f, 1027.8f),
                        PathNode.QuadTo(1103.3f, 1051.5f, 1086.1f, 1068.6f),
                        PathNode.QuadTo(1069.0f, 1085.8f, 1045.3f, 1085.8f),
                        PathNode.QuadTo(1020.3f, 1085.8f, 1003.8f, 1068.6f),
                        PathNode.QuadTo(987.3f, 1051.5f, 987.3f, 1027.8f),
                        PathNode.QuadTo(987.3f, 1003.2f, 1003.8f, 986.0f),
                        PathNode.QuadTo(1020.3f, 968.8f, 1045.2f, 968.8f),
                        PathNode.QuadTo(1069.3f, 968.8f, 1086.3f, 986.0f),
                        PathNode.QuadTo(1103.3f, 1003.2f, 1103.3f, 1027.8f),
                        PathNode.Close,
                    ),
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1f,
                    pathFillType = PathFillType.NonZero,
                )
            }
        }.build()
        return _mindMap!!
    }

private var _mindMap: ImageVector? = null
