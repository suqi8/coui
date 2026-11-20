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
 * ReplyAll: ColorOS has no drawing for this one, so it keeps the earlier symbol-font glyph
 * (its light weight, the closest to the ColorOS 1.4 stroke).
 */
val COUIIcons.ReplyAll: ImageVector
    get() {
        if (_replyAll != null) return _replyAll!!
        _replyAll = ImageVector.Builder(
            name = "ReplyAll",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 1293.6f,
            viewportHeight = 1293.6f,
        ).apply {
            group(scaleY = -1.0f, translationY = 1293.6f) {
                addPath(
                    pathData = listOf(
                        PathNode.MoveTo(994.8f, 620.3f),
                        PathNode.QuadTo(994.8f, 722.3f, 934.3f, 805.3f),
                        PathNode.QuadTo(873.8f, 888.3f, 772.3f, 935.8f),
                        PathNode.QuadTo(670.8f, 983.3f, 550.8f, 983.3f),
                        PathNode.QuadTo(438.8f, 983.3f, 336.8f, 937.8f),
                        PathNode.QuadTo(234.8f, 892.3f, 171.3f, 809.8f),
                        PathNode.QuadTo(107.8f, 727.3f, 107.8f, 620.3f),
                        PathNode.QuadTo(107.8f, 543.3f, 142.3f, 478.3f),
                        PathNode.QuadTo(176.8f, 413.3f, 233.8f, 366.3f),
                        PathNode.QuadTo(290.8f, 319.3f, 359.8f, 291.3f),
                        PathNode.QuadTo(373.8f, 286.3f, 378.8f, 275.8f),
                        PathNode.QuadTo(383.8f, 265.3f, 379.8f, 252.3f),
                        PathNode.LineTo(357.8f, 179.3f),
                        PathNode.QuadTo(355.8f, 174.3f, 359.8f, 171.3f),
                        PathNode.QuadTo(363.8f, 168.3f, 369.8f, 170.3f),
                        PathNode.LineTo(645.8f, 250.3f),
                        PathNode.QuadTo(804.8f, 296.3f, 899.8f, 383.3f),
                        PathNode.QuadTo(994.8f, 470.3f, 994.8f, 620.3f),
                        PathNode.Close,
                        PathNode.MoveTo(381.8f, 347.3f),
                        PathNode.QuadTo(289.8f, 383.3f, 228.3f, 454.8f),
                        PathNode.QuadTo(166.8f, 526.3f, 166.8f, 620.3f),
                        PathNode.QuadTo(166.8f, 710.3f, 222.8f, 779.3f),
                        PathNode.QuadTo(278.8f, 848.3f, 367.3f, 885.8f),
                        PathNode.QuadTo(455.8f, 923.3f, 550.8f, 923.3f),
                        PathNode.QuadTo(659.8f, 923.3f, 747.8f, 881.8f),
                        PathNode.QuadTo(835.8f, 840.3f, 885.3f, 770.3f),
                        PathNode.QuadTo(934.8f, 700.3f, 934.8f, 620.3f),
                        PathNode.QuadTo(934.8f, 491.3f, 851.3f, 418.3f),
                        PathNode.QuadTo(767.8f, 345.3f, 626.8f, 305.3f),
                        PathNode.LineTo(444.8f, 252.3f),
                        PathNode.QuadTo(440.8f, 251.3f, 438.3f, 253.3f),
                        PathNode.QuadTo(435.8f, 255.3f, 435.8f, 259.3f),
                        PathNode.QuadTo(439.8f, 291.3f, 423.8f, 313.8f),
                        PathNode.QuadTo(407.8f, 336.3f, 381.8f, 347.3f),
                        PathNode.Close,
                        PathNode.MoveTo(1106.8f, 579.3f),
                        PathNode.QuadTo(1144.8f, 622.3f, 1165.3f, 673.8f),
                        PathNode.QuadTo(1185.8f, 725.3f, 1185.8f, 790.3f),
                        PathNode.QuadTo(1185.8f, 884.3f, 1130.8f, 960.8f),
                        PathNode.QuadTo(1075.8f, 1037.3f, 982.3f, 1081.3f),
                        PathNode.QuadTo(888.8f, 1125.3f, 777.8f, 1125.3f),
                        PathNode.QuadTo(687.8f, 1125.3f, 591.8f, 1087.3f),
                        PathNode.QuadTo(587.8f, 1086.3f, 586.8f, 1082.3f),
                        PathNode.QuadTo(585.8f, 1078.3f, 587.8f, 1075.3f),
                        PathNode.QuadTo(589.8f, 1072.3f, 594.8f, 1072.3f),
                        PathNode.QuadTo(644.8f, 1071.3f, 690.8f, 1060.3f),
                        PathNode.QuadTo(697.8f, 1058.3f, 713.8f, 1061.3f),
                        PathNode.QuadTo(724.8f, 1062.3f, 741.3f, 1063.8f),
                        PathNode.QuadTo(757.8f, 1065.3f, 777.8f, 1065.3f),
                        PathNode.QuadTo(877.8f, 1065.3f, 957.8f, 1027.3f),
                        PathNode.QuadTo(1037.8f, 989.3f, 1082.3f, 925.8f),
                        PathNode.QuadTo(1126.8f, 862.3f, 1126.8f, 790.3f),
                        PathNode.QuadTo(1126.8f, 721.3f, 1093.8f, 665.3f),
                        PathNode.QuadTo(1090.8f, 656.3f, 1091.8f, 647.3f),
                        PathNode.QuadTo(1094.8f, 616.3f, 1091.8f, 587.3f),
                        PathNode.QuadTo(1090.8f, 576.3f, 1095.3f, 573.8f),
                        PathNode.QuadTo(1099.8f, 571.3f, 1106.8f, 579.3f),
                        PathNode.Close,
                    ),
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1f,
                    pathFillType = PathFillType.NonZero,
                )
            }
        }.build()
        return _replyAll!!
    }

private var _replyAll: ImageVector? = null
