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
 * Carrier: ColorOS has no drawing for this one, so it keeps the earlier symbol-font glyph
 * (its light weight, the closest to the ColorOS 1.4 stroke).
 */
val COUIIcons.Carrier: ImageVector
    get() {
        if (_carrier != null) return _carrier!!
        _carrier = ImageVector.Builder(
            name = "Carrier",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 1110.0f,
            viewportHeight = 1110.0f,
        ).apply {
            group(scaleY = -1.0f, translationY = 1110.0f) {
                addPath(
                    pathData = listOf(
                        PathNode.MoveTo(893.7f, 124.8f),
                        PathNode.QuadTo(939.4f, 148.9f, 959.0f, 196.6f),
                        PathNode.QuadTo(968.7f, 220.0f, 968.2f, 254.9f),
                        PathNode.QuadTo(967.8f, 289.9f, 959.1f, 375.6f),
                        PathNode.LineTo(918.4f, 782.7f),
                        PathNode.QuadTo(911.7f, 850.4f, 906.3f, 881.5f),
                        PathNode.QuadTo(901.0f, 912.5f, 888.4f, 932.8f),
                        PathNode.QuadTo(866.8f, 969.0f, 826.3f, 989.1f),
                        PathNode.QuadTo(805.6f, 999.1f, 776.0f, 1001.1f),
                        PathNode.QuadTo(746.4f, 1003.0f, 674.5f, 1003.0f),
                        PathNode.HorizontalTo(435.5f),
                        PathNode.QuadTo(363.6f, 1003.0f, 334.0f, 1001.1f),
                        PathNode.QuadTo(304.4f, 999.1f, 283.7f, 989.1f),
                        PathNode.QuadTo(243.2f, 969.0f, 221.6f, 932.8f),
                        PathNode.QuadTo(209.0f, 912.5f, 203.7f, 881.5f),
                        PathNode.QuadTo(198.3f, 850.4f, 191.6f, 782.7f),
                        PathNode.LineTo(150.9f, 375.6f),
                        PathNode.QuadTo(142.2f, 289.9f, 141.8f, 254.9f),
                        PathNode.QuadTo(141.3f, 220.0f, 152.0f, 196.6f),
                        PathNode.QuadTo(169.6f, 148.9f, 216.2f, 124.8f),
                        PathNode.QuadTo(238.7f, 112.8f, 274.1f, 109.9f),
                        PathNode.QuadTo(309.5f, 107.0f, 395.9f, 107.0f),
                        PathNode.HorizontalTo(714.1f),
                        PathNode.QuadTo(800.5f, 107.0f, 835.9f, 109.9f),
                        PathNode.QuadTo(871.3f, 112.8f, 893.7f, 124.8f),
                        PathNode.Close,
                        PathNode.MoveTo(363.1f, 813.6f),
                        PathNode.VerticalTo(866.9f),
                        PathNode.QuadTo(363.1f, 874.4f, 367.5f, 879.1f),
                        PathNode.QuadTo(371.9f, 883.9f, 380.1f, 883.9f),
                        PathNode.HorizontalTo(393.7f),
                        PathNode.QuadTo(402.7f, 883.9f, 407.2f, 879.5f),
                        PathNode.QuadTo(411.7f, 875.1f, 411.7f, 866.9f),
                        PathNode.VerticalTo(813.6f),
                        PathNode.QuadTo(411.7f, 775.4f, 430.7f, 742.4f),
                        PathNode.QuadTo(449.9f, 709.4f, 482.9f, 690.2f),
                        PathNode.QuadTo(515.8f, 671.2f, 555.0f, 671.2f),
                        PathNode.QuadTo(594.2f, 671.2f, 627.2f, 690.2f),
                        PathNode.QuadTo(660.1f, 709.4f, 679.3f, 742.4f),
                        PathNode.QuadTo(698.3f, 775.4f, 698.3f, 813.6f),
                        PathNode.VerticalTo(866.9f),
                        PathNode.QuadTo(698.3f, 875.1f, 703.3f, 879.5f),
                        PathNode.QuadTo(708.2f, 883.9f, 716.3f, 883.9f),
                        PathNode.HorizontalTo(729.0f),
                        PathNode.QuadTo(738.0f, 883.9f, 742.5f, 879.5f),
                        PathNode.QuadTo(747.0f, 875.1f, 747.0f, 866.9f),
                        PathNode.VerticalTo(813.6f),
                        PathNode.QuadTo(747.0f, 761.4f, 721.2f, 717.6f),
                        PathNode.QuadTo(695.5f, 673.8f, 651.5f, 648.1f),
                        PathNode.QuadTo(607.4f, 622.5f, 555.1f, 622.5f),
                        PathNode.QuadTo(502.6f, 622.5f, 458.6f, 648.1f),
                        PathNode.QuadTo(414.6f, 673.8f, 388.8f, 717.6f),
                        PathNode.QuadTo(363.1f, 761.4f, 363.1f, 813.6f),
                        PathNode.Close,
                    ),
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1f,
                    pathFillType = PathFillType.NonZero,
                )
            }
        }.build()
        return _carrier!!
    }

private var _carrier: ImageVector? = null
