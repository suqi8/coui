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
 * Merge: ColorOS has no drawing for this one, so it keeps the earlier symbol-font glyph
 * (its light weight, the closest to the ColorOS 1.4 stroke).
 */
val COUIIcons.Merge: ImageVector
    get() {
        if (_merge != null) return _merge!!
        _merge = ImageVector.Builder(
            name = "Merge",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 1180.8f,
            viewportHeight = 1180.8f,
        ).apply {
            group(scaleY = -1.0f, translationY = 1180.8f) {
                addPath(
                    pathData = listOf(
                        PathNode.MoveTo(859.4f, 360.4f),
                        PathNode.LineTo(1069.4f, 571.4f),
                        PathNode.QuadTo(1082.4f, 584.4f, 1081.9f, 594.4f),
                        PathNode.QuadTo(1081.4f, 604.4f, 1068.4f, 617.4f),
                        PathNode.LineTo(855.4f, 830.4f),
                        PathNode.QuadTo(849.4f, 835.4f, 841.9f, 835.9f),
                        PathNode.QuadTo(834.4f, 836.4f, 828.4f, 830.4f),
                        PathNode.LineTo(813.4f, 815.4f),
                        PathNode.QuadTo(807.4f, 809.4f, 807.4f, 802.4f),
                        PathNode.QuadTo(807.4f, 795.4f, 813.4f, 789.4f),
                        PathNode.LineTo(978.4f, 623.4f),
                        PathNode.HorizontalTo(816.4f),
                        PathNode.QuadTo(751.4f, 623.4f, 725.9f, 624.4f),
                        PathNode.QuadTo(700.4f, 625.4f, 681.4f, 631.4f),
                        PathNode.QuadTo(639.4f, 645.4f, 615.4f, 665.4f),
                        PathNode.QuadTo(599.4f, 677.4f, 582.9f, 698.4f),
                        PathNode.QuadTo(566.4f, 719.4f, 529.4f, 769.4f),
                        PathNode.LineTo(514.4f, 790.4f),
                        PathNode.QuadTo(481.4f, 836.4f, 460.9f, 860.4f),
                        PathNode.QuadTo(440.4f, 884.4f, 419.4f, 900.4f),
                        PathNode.QuadTo(375.4f, 932.4f, 335.4f, 943.4f),
                        PathNode.QuadTo(309.4f, 951.4f, 279.9f, 953.4f),
                        PathNode.QuadTo(250.4f, 955.4f, 189.4f, 955.4f),
                        PathNode.HorizontalTo(120.4f),
                        PathNode.QuadTo(110.4f, 955.4f, 104.4f, 951.4f),
                        PathNode.QuadTo(98.4f, 947.4f, 98.4f, 937.4f),
                        PathNode.VerticalTo(914.4f),
                        PathNode.QuadTo(98.4f, 904.4f, 104.4f, 900.4f),
                        PathNode.QuadTo(110.4f, 896.4f, 120.4f, 896.4f),
                        PathNode.HorizontalTo(182.4f),
                        PathNode.QuadTo(243.4f, 896.4f, 270.4f, 894.9f),
                        PathNode.QuadTo(297.4f, 893.4f, 317.4f, 888.4f),
                        PathNode.QuadTo(352.4f, 877.4f, 383.4f, 854.4f),
                        PathNode.QuadTo(399.4f, 843.4f, 414.4f, 825.4f),
                        PathNode.QuadTo(429.4f, 807.4f, 458.4f, 766.4f),
                        PathNode.LineTo(470.4f, 750.4f),
                        PathNode.LineTo(485.4f, 729.4f),
                        PathNode.QuadTo(524.4f, 676.4f, 541.9f, 655.4f),
                        PathNode.QuadTo(559.4f, 634.4f, 579.4f, 619.4f),
                        PathNode.QuadTo(619.4f, 588.4f, 664.4f, 575.4f),
                        PathNode.QuadTo(690.4f, 567.4f, 719.9f, 565.4f),
                        PathNode.QuadTo(749.4f, 563.4f, 809.4f, 563.4f),
                        PathNode.HorizontalTo(979.4f),
                        PathNode.LineTo(815.4f, 399.4f),
                        PathNode.QuadTo(808.4f, 392.4f, 807.9f, 385.9f),
                        PathNode.QuadTo(807.4f, 379.4f, 813.4f, 372.4f),
                        PathNode.LineTo(827.4f, 357.4f),
                        PathNode.QuadTo(836.4f, 349.4f, 843.9f, 350.9f),
                        PathNode.QuadTo(851.4f, 352.4f, 859.4f, 360.4f),
                        PathNode.Close,
                        PathNode.MoveTo(334.4f, 237.4f),
                        PathNode.QuadTo(377.4f, 250.4f, 419.4f, 280.4f),
                        PathNode.QuadTo(436.4f, 292.4f, 450.4f, 308.9f),
                        PathNode.QuadTo(464.4f, 325.4f, 491.4f, 361.4f),
                        PathNode.QuadTo(503.4f, 376.4f, 514.4f, 391.4f),
                        PathNode.LineTo(586.4f, 488.4f),
                        PathNode.QuadTo(591.4f, 494.4f, 589.9f, 502.4f),
                        PathNode.QuadTo(588.4f, 510.4f, 580.4f, 514.4f),
                        PathNode.LineTo(562.4f, 526.4f),
                        PathNode.QuadTo(554.4f, 531.4f, 547.4f, 528.9f),
                        PathNode.QuadTo(540.4f, 526.4f, 535.4f, 519.4f),
                        PathNode.LineTo(470.4f, 430.4f),
                        PathNode.QuadTo(432.4f, 379.4f, 416.4f, 359.4f),
                        PathNode.QuadTo(400.4f, 339.4f, 383.4f, 326.4f),
                        PathNode.QuadTo(359.4f, 307.4f, 317.4f, 293.4f),
                        PathNode.QuadTo(298.4f, 287.4f, 272.9f, 286.4f),
                        PathNode.QuadTo(247.4f, 285.4f, 182.4f, 285.4f),
                        PathNode.HorizontalTo(118.4f),
                        PathNode.QuadTo(108.4f, 285.4f, 103.4f, 279.9f),
                        PathNode.QuadTo(98.4f, 274.4f, 98.4f, 265.4f),
                        PathNode.VerticalTo(245.4f),
                        PathNode.QuadTo(98.4f, 235.4f, 103.9f, 230.4f),
                        PathNode.QuadTo(109.4f, 225.4f, 119.4f, 225.4f),
                        PathNode.HorizontalTo(189.4f),
                        PathNode.QuadTo(250.4f, 225.4f, 279.9f, 227.4f),
                        PathNode.QuadTo(309.4f, 229.4f, 334.4f, 237.4f),
                        PathNode.Close,
                    ),
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1f,
                    pathFillType = PathFillType.NonZero,
                )
            }
        }.build()
        return _merge!!
    }

private var _merge: ImageVector? = null
