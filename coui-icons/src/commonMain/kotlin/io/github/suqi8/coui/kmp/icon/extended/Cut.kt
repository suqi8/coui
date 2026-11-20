// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Cut: ColorOS `note_ic_cut_normal` (com.coloros.note_16.7.2). */
val COUIIcons.Cut: ImageVector
    get() {
        _cut?.let { return it }
        return ImageVector.Builder(
            name = "Cut",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
            autoMirror = true,
        ).apply {
            addPath(
                pathData = addPathNodes("M4.801,10.058C6.486,10.058 7.853,8.692 7.853,7.007C7.853,5.322 6.486,3.956 4.801,3.956C3.116,3.956 1.75,5.322 1.75,7.007C1.75,8.692 3.116,10.058 4.801,10.058Z"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M4.801,20.044C6.486,20.044 7.853,18.678 7.853,16.993C7.853,15.308 6.486,13.941 4.801,13.941C3.116,13.941 1.75,15.308 1.75,16.993C1.75,18.678 3.116,20.044 4.801,20.044Z"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M20.624,17.992C20.191,17.992 19.774,17.865 19.415,17.623L7.33,9.391L8.383,7.827L21.831,16.975C22.057,17.129 22.056,17.457 21.837,17.62C21.61,17.788 21.345,17.894 21.06,17.95C20.917,17.978 20.771,17.992 20.624,17.992Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M20.624,5.83C20.191,5.83 19.774,5.957 19.415,6.198L7.33,14.431L8.383,15.995L21.831,6.846C22.057,6.693 22.056,6.365 21.837,6.202C21.61,6.034 21.345,5.927 21.06,5.872C20.917,5.844 20.771,5.83 20.624,5.83Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M19,12.9C19.497,12.9 19.9,12.497 19.9,12C19.9,11.503 19.497,11.1 19,11.1C18.503,11.1 18.1,11.503 18.1,12C18.1,12.497 18.503,12.9 19,12.9Z"),
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M22,12.9C22.497,12.9 22.9,12.497 22.9,12C22.9,11.503 22.497,11.1 22,11.1C21.503,11.1 21.1,11.503 21.1,12C21.1,12.497 21.503,12.9 22,12.9Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _cut = it }
    }

private var _cut: ImageVector? = null
