// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** SearchDevice: ColorOS `ai_quantum_detection` (com.coloros.phonemanager_17.5.3). */
val COUIIcons.SearchDevice: ImageVector
    get() {
        _searchDevice?.let { return it }
        return ImageVector.Builder(
            name = "SearchDevice",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M 12 1 C 18.075 1 23 5.925 23 12 C 23 18.075 18.075 23 12 23 C 5.925 23 1 18.075 1 12 C 1 5.925 5.925 1 12 1 Z M 12 2.35 C 6.67 2.35 2.35 6.67 2.35 12 C 2.35 17.329 6.67 21.65 12 21.65 C 17.329 21.65 21.65 17.329 21.65 12 C 21.65 6.67 17.329 2.35 12 2.35 Z M 12 6 C 13.304 6 14.51 6.417 15.494 7.124 L 14.328 7.975 C 13.643 7.578 12.848 7.35 12 7.35 C 9.432 7.35 7.35 9.432 7.35 12 C 7.35 14.568 9.432 16.65 12 16.65 C 14.568 16.65 16.65 14.568 16.65 12 C 16.65 11.258 16.475 10.557 16.165 9.935 L 12.399 12.573 L 11.998 12 L 11.596 11.427 L 16.825 7.765 C 17.142 7.543 17.578 7.619 17.8 7.936 C 18.021 8.252 17.944 8.689 17.627 8.911 L 17.28 9.153 C 17.738 10 18 10.969 18 12 C 18 15.314 15.314 18 12 18 C 8.686 18 6 15.314 6 12 C 6 8.686 8.686 6 12 6 Z M 12.399 12.573 C 12.083 12.795 11.647 12.717 11.425 12.401 C 11.204 12.085 11.28 11.649 11.596 11.427 L 12.399 12.573 Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _searchDevice = it }
    }

private var _searchDevice: ImageVector? = null
