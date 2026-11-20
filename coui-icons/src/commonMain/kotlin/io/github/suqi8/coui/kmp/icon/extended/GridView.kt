// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** GridView: ColorOS `color_tool_menu_ic_mode_grid` (com.coloros.filemanager_16.12.2). */
val COUIIcons.GridView: ImageVector
    get() {
        _gridView?.let { return it }
        return ImageVector.Builder(
            name = "GridView",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M14,7.333C14,8.267 14,8.733 14.182,9.09C14.341,9.404 14.596,9.659 14.91,9.818C15.267,10 15.733,10 16.667,10H18.333C19.267,10 19.733,10 20.09,9.818C20.404,9.659 20.659,9.404 20.818,9.09C21,8.733 21,8.267 21,7.333V5.667C21,4.733 21,4.267 20.818,3.91C20.659,3.596 20.404,3.341 20.09,3.182C19.733,3 19.267,3 18.333,3L16.667,3C15.733,3 15.267,3 14.91,3.182C14.596,3.341 14.341,3.596 14.182,3.91C14,4.267 14,4.733 14,5.667V7.333Z"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M3,18.333C3,19.267 3,19.733 3.182,20.09C3.341,20.404 3.596,20.659 3.91,20.818C4.267,21 4.733,21 5.667,21H7.333C8.267,21 8.733,21 9.09,20.818C9.404,20.659 9.659,20.404 9.818,20.09C10,19.733 10,19.267 10,18.333V16.667C10,15.733 10,15.267 9.818,14.91C9.659,14.596 9.404,14.341 9.09,14.182C8.733,14 8.267,14 7.333,14H5.667C4.733,14 4.267,14 3.91,14.182C3.596,14.341 3.341,14.596 3.182,14.91C3,15.267 3,15.733 3,16.667V18.333Z"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M3,7.333C3,8.267 3,8.733 3.182,9.09C3.341,9.404 3.596,9.659 3.91,9.818C4.267,10 4.733,10 5.667,10H7.333C8.267,10 8.733,10 9.09,9.818C9.404,9.659 9.659,9.404 9.818,9.09C10,8.733 10,8.267 10,7.333V5.667C10,4.733 10,4.267 9.818,3.91C9.659,3.596 9.404,3.341 9.09,3.182C8.733,3 8.267,3 7.333,3L5.667,3C4.733,3 4.267,3 3.91,3.182C3.596,3.341 3.341,3.596 3.182,3.91C3,4.267 3,4.733 3,5.667L3,7.333Z"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M14,18.333C14,19.267 14,19.733 14.182,20.09C14.341,20.404 14.596,20.659 14.91,20.818C15.267,21 15.733,21 16.667,21H18.333C19.267,21 19.733,21 20.09,20.818C20.404,20.659 20.659,20.404 20.818,20.09C21,19.733 21,19.267 21,18.333V16.667C21,15.733 21,15.267 20.818,14.91C20.659,14.596 20.404,14.341 20.09,14.182C19.733,14 19.267,14 18.333,14H16.667C15.733,14 15.267,14 14.91,14.182C14.596,14.341 14.341,14.596 14.182,14.91C14,15.267 14,15.733 14,16.667V18.333Z"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Round,
            )
        }.build().also { _gridView = it }
    }

private var _gridView: ImageVector? = null
