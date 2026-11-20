// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Report: ColorOS `main_intercept` (com.coloros.phonemanager_17.5.3). */
val COUIIcons.Report: ImageVector
    get() {
        _report?.let { return it }
        return ImageVector.Builder(
            name = "Report",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M11.447,1.012C11.815,0.959 12.188,0.959 12.555,1.012C12.986,1.075 13.398,1.233 14.029,1.469L18.611,3.188C19.508,3.524 20.134,3.747 20.604,4.161C20.998,4.509 21.302,4.948 21.489,5.439C21.712,6.025 21.701,6.689 21.701,7.646V10.427C21.701,16.425 18.14,20.589 13.145,22.751C12.881,22.865 12.658,22.966 12.368,23.013C12.146,23.05 11.856,23.05 11.634,23.013C11.344,22.966 11.122,22.865 10.858,22.751C5.862,20.589 2.301,16.425 2.301,10.427V7.646C2.301,6.689 2.29,6.025 2.513,5.439C2.7,4.948 3.004,4.509 3.399,4.161C3.868,3.747 4.494,3.524 5.391,3.188L9.973,1.469C10.604,1.233 11.016,1.075 11.447,1.012ZM12.002,14.557C11.139,14.557 10.439,15.257 10.439,16.121C10.439,16.984 11.139,17.684 12.002,17.684C12.865,17.684 13.565,16.984 13.566,16.121C13.566,15.257 12.865,14.557 12.002,14.557ZM11.987,5.918C11.121,5.918 10.442,6.661 10.519,7.523L10.518,7.524L10.519,7.529L11.037,12.492C11.084,12.991 11.504,13.374 12.006,13.374C12.51,13.374 12.93,12.987 12.974,12.485L12.975,12.486L13.457,7.519L13.159,7.491L13.458,7.519V7.515C13.53,6.655 12.851,5.918 11.987,5.918ZM12.006,13.074C11.962,13.074 11.92,13.069 11.878,13.061C11.919,13.069 11.962,13.073 12.006,13.073C12.05,13.073 12.092,13.069 12.134,13.061L12.006,13.074ZM10.816,7.497L11.336,12.461C11.337,12.469 11.34,12.478 11.341,12.486C11.34,12.478 11.337,12.47 11.336,12.462L10.816,7.498V7.497ZM10.812,7.384C10.812,7.37 10.812,7.357 10.813,7.344C10.812,7.357 10.812,7.37 10.812,7.384ZM10.893,6.965C10.853,7.067 10.826,7.175 10.816,7.288C10.818,7.273 10.819,7.259 10.82,7.245C10.833,7.147 10.858,7.054 10.893,6.965Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _report = it }
    }

private var _report: ImageVector? = null
