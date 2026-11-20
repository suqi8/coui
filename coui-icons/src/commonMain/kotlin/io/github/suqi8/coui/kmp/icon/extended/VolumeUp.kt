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

/** VolumeUp: ColorOS `preview_icon_media_volume_voice` (com.coloros.filemanager_16.12.2). */
val COUIIcons.VolumeUp: ImageVector
    get() {
        _volumeUp?.let { return it }
        return ImageVector.Builder(
            name = "VolumeUp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M13.632,2.354C13.894,2.462 14.064,2.718 14.064,3.001V21.001C14.064,21.284 13.894,21.539 13.632,21.647C13.37,21.756 13.069,21.696 12.869,21.496L8.25,16.876C8.137,16.764 7.985,16.701 7.826,16.701H4.664C3.559,16.701 2.664,15.805 2.664,14.701V9.301C2.664,8.196 3.559,7.301 4.664,7.301H7.826C7.985,7.301 8.137,7.238 8.25,7.125L12.869,2.506C13.069,2.306 13.37,2.246 13.632,2.354ZM12.664,4.691L9.24,8.115L8.745,7.62L9.24,8.115C8.865,8.49 8.356,8.701 7.826,8.701H4.664C4.333,8.701 4.064,8.969 4.064,9.301V14.701C4.064,15.032 4.333,15.301 4.664,15.301H7.826C8.356,15.301 8.865,15.512 9.24,15.887L8.745,16.382L9.24,15.887L12.664,19.311V4.691ZM16.029,8.53C16.352,8.317 16.786,8.406 16.999,8.729C17.618,9.668 17.979,10.793 17.979,12.001C17.979,13.208 17.618,14.333 16.999,15.273C16.786,15.595 16.352,15.684 16.029,15.472C15.707,15.259 15.618,14.825 15.83,14.502C16.303,13.784 16.579,12.926 16.579,12.001C16.579,11.075 16.303,10.217 15.83,9.499C15.618,9.177 15.707,8.743 16.029,8.53ZM19.508,6.458C19.278,6.147 18.839,6.082 18.529,6.313C18.219,6.543 18.154,6.982 18.384,7.292C19.36,8.608 19.938,10.236 19.938,12.001C19.938,13.766 19.36,15.394 18.384,16.71C18.154,17.02 18.219,17.459 18.529,17.689C18.839,17.919 19.278,17.854 19.508,17.544C20.658,15.995 21.338,14.077 21.338,12.001C21.338,9.925 20.658,8.006 19.508,6.458Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _volumeUp = it }
    }

private var _volumeUp: ImageVector? = null
