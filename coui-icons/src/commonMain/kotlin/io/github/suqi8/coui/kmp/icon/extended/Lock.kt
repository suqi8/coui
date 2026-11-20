// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Lock: ColorOS `note_ic_encrypted_normal` (com.coloros.note_16.7.2). */
val COUIIcons.Lock: ImageVector
    get() {
        _lock?.let { return it }
        return ImageVector.Builder(
            name = "Lock",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M12.001,2.4C10.154,2.4 8.701,3.836 8.701,5.553V9.6H15.301V5.553C15.301,3.836 13.848,2.4 12.001,2.4ZM7.301,5.553V9.6H4.882C3.722,9.6 2.801,10.553 2.801,11.704V19.696C2.801,20.847 3.722,21.8 4.882,21.8H19.119C20.28,21.8 21.201,20.847 21.201,19.696V11.704C21.201,10.553 20.28,9.6 19.119,9.6H16.701V5.553C16.701,3.014 14.572,1 12.001,1C9.43,1 7.301,3.014 7.301,5.553ZM4.882,11C4.517,11 4.201,11.304 4.201,11.704V19.696C4.201,20.096 4.517,20.4 4.882,20.4H19.119C19.485,20.4 19.801,20.096 19.801,19.696V11.704C19.801,11.304 19.485,11 19.119,11H4.882ZM13.501,14.315C13.501,14.891 13.177,15.391 12.7,15.642V17.698C12.7,18.085 12.387,18.399 12,18.399C11.614,18.399 11.3,18.085 11.3,17.698V15.642C10.825,15.39 10.501,14.89 10.501,14.315C10.501,13.486 11.173,12.815 12.001,12.815C12.829,12.815 13.501,13.486 13.501,14.315Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _lock = it }
    }

private var _lock: ImageVector? = null
