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

/** Backup: ColorOS `drawable/tool_backup` (com.coloros.phonemanager_17.5.3). */
val COUIIcons.Backup: ImageVector
    get() {
        _backup?.let { return it }
        return ImageVector.Builder(
            name = "Backup",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M19.126 9.02C18.656 5.493 15.622 2.801 11.999 2.801C8.379 2.801 5.346 5.494 4.873 9.024C4.518 9.118 4.178 9.244 3.877 9.395C3.122 9.744 2.447 10.239 1.909 10.847L1.586 11.224C0.757 12.302 0.301 13.643 0.301 15C0.301 18.418 3.082 21.199 6.5 21.199H17.498C18.983 21.199 20.394 20.672 21.598 19.658C21.841 19.438 22.051 19.217 22.242 18.993C22.416 18.796 22.57 18.58 22.668 18.401C23.341 17.402 23.698 16.226 23.698 15C23.698 12.186 21.805 9.75 19.126 9.02ZM7.788 9.123C7.788 8.737 8.102 8.423 8.488 8.423C8.875 8.423 9.188 8.737 9.188 9.123L9.188 9.354C9.991 8.814 10.958 8.498 11.999 8.498C14.782 8.498 17.038 10.754 17.038 13.537C17.038 13.924 16.725 14.237 16.338 14.237C15.952 14.237 15.638 13.924 15.638 13.537C15.638 11.527 14.009 9.898 11.999 9.898C11.36 9.898 10.759 10.062 10.237 10.352H10.417C10.803 10.352 11.117 10.665 11.117 11.052C11.117 11.438 10.803 11.752 10.417 11.752H8.488C8.102 11.752 7.788 11.438 7.788 11.052V9.123ZM16.212 17.939C16.212 18.325 15.899 18.639 15.512 18.639C15.126 18.639 14.812 18.325 14.812 17.939V17.708C14.01 18.248 13.042 18.564 12.001 18.564C9.218 18.564 6.962 16.308 6.962 13.525C6.962 13.139 7.276 12.825 7.662 12.825C8.049 12.825 8.362 13.139 8.362 13.525C8.362 15.535 9.991 17.165 12.001 17.165C12.641 17.165 13.242 17 13.764 16.71H13.584C13.197 16.71 12.884 16.397 12.884 16.01C12.884 15.624 13.197 15.31 13.584 15.31L15.512 15.31C15.899 15.31 16.212 15.624 16.212 16.01V17.939Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _backup = it }
    }

private var _backup: ImageVector? = null
