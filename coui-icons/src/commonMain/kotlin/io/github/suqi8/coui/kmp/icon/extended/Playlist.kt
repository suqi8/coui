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
 * Playlist: ColorOS has no drawing for this one, so it keeps the earlier symbol-font glyph
 * (its light weight, the closest to the ColorOS 1.4 stroke).
 */
val COUIIcons.Playlist: ImageVector
    get() {
        if (_playlist != null) return _playlist!!
        _playlist = ImageVector.Builder(
            name = "Playlist",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 1072.8f,
            viewportHeight = 1072.8f,
        ).apply {
            group(scaleY = -1.0f, translationY = 1072.8f) {
                addPath(
                    pathData = listOf(
                        PathNode.MoveTo(345.9f, 819.0f),
                        PathNode.LineTo(162.0f, 924.6f),
                        PathNode.QuadTo(154.1f, 929.4f, 145.7f, 928.0f),
                        PathNode.QuadTo(137.4f, 926.5f, 131.9f, 919.9f),
                        PathNode.QuadTo(126.5f, 913.4f, 126.5f, 903.8f),
                        PathNode.VerticalTo(691.7f),
                        PathNode.QuadTo(126.5f, 683.0f, 131.9f, 676.5f),
                        PathNode.QuadTo(137.4f, 669.9f, 145.7f, 668.5f),
                        PathNode.QuadTo(154.1f, 667.0f, 162.0f, 671.8f),
                        PathNode.LineTo(345.9f, 777.4f),
                        PathNode.QuadTo(353.8f, 782.2f, 356.7f, 790.2f),
                        PathNode.QuadTo(359.6f, 798.2f, 356.7f, 806.2f),
                        PathNode.QuadTo(353.8f, 814.2f, 345.9f, 819.0f),
                        PathNode.Close,
                        PathNode.MoveTo(953.3f, 162.8f),
                        PathNode.VerticalTo(183.8f),
                        PathNode.QuadTo(953.3f, 194.3f, 947.5f, 198.8f),
                        PathNode.QuadTo(941.7f, 203.4f, 932.2f, 203.4f),
                        PathNode.HorizontalTo(140.6f),
                        PathNode.QuadTo(131.8f, 203.4f, 125.6f, 198.4f),
                        PathNode.QuadTo(119.5f, 193.4f, 119.5f, 185.2f),
                        PathNode.VerticalTo(163.4f),
                        PathNode.QuadTo(119.5f, 153.0f, 125.3f, 148.2f),
                        PathNode.QuadTo(131.1f, 143.3f, 140.6f, 143.3f),
                        PathNode.HorizontalTo(932.2f),
                        PathNode.QuadTo(941.7f, 143.3f, 947.5f, 147.9f),
                        PathNode.QuadTo(953.3f, 152.3f, 953.3f, 162.8f),
                        PathNode.Close,
                        PathNode.MoveTo(953.3f, 479.5f),
                        PathNode.VerticalTo(500.5f),
                        PathNode.QuadTo(953.3f, 511.0f, 947.5f, 515.5f),
                        PathNode.QuadTo(941.7f, 520.0f, 932.2f, 520.0f),
                        PathNode.HorizontalTo(140.6f),
                        PathNode.QuadTo(131.8f, 520.0f, 125.6f, 515.0f),
                        PathNode.QuadTo(119.5f, 510.0f, 119.5f, 501.8f),
                        PathNode.VerticalTo(480.0f),
                        PathNode.QuadTo(119.5f, 469.7f, 125.3f, 464.8f),
                        PathNode.QuadTo(131.1f, 460.0f, 140.6f, 460.0f),
                        PathNode.HorizontalTo(932.2f),
                        PathNode.QuadTo(941.7f, 460.0f, 947.5f, 464.5f),
                        PathNode.QuadTo(953.3f, 469.0f, 953.3f, 479.5f),
                        PathNode.Close,
                        PathNode.MoveTo(953.3f, 793.3f),
                        PathNode.VerticalTo(814.3f),
                        PathNode.QuadTo(953.3f, 824.8f, 947.5f, 829.3f),
                        PathNode.QuadTo(941.7f, 833.8f, 932.2f, 833.8f),
                        PathNode.HorizontalTo(502.0f),
                        PathNode.QuadTo(493.1f, 833.8f, 487.0f, 828.8f),
                        PathNode.QuadTo(480.8f, 823.9f, 480.8f, 815.6f),
                        PathNode.VerticalTo(793.9f),
                        PathNode.QuadTo(480.8f, 783.5f, 486.7f, 778.6f),
                        PathNode.QuadTo(492.4f, 773.8f, 502.0f, 773.8f),
                        PathNode.HorizontalTo(932.2f),
                        PathNode.QuadTo(941.7f, 773.8f, 947.5f, 778.3f),
                        PathNode.QuadTo(953.3f, 782.8f, 953.3f, 793.3f),
                        PathNode.Close,
                    ),
                    fill = SolidColor(Color.Black),
                    fillAlpha = 1f,
                    pathFillType = PathFillType.NonZero,
                )
            }
        }.build()
        return _playlist!!
    }

private var _playlist: ImageVector? = null
