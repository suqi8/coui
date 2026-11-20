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

/** CallRecording: ColorOS `ic_recording_group_call` (com.coloros.soundrecorder_16.17.8). */
val COUIIcons.CallRecording: ImageVector
    get() {
        _callRecording?.let { return it }
        return ImageVector.Builder(
            name = "CallRecording",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M5.518,3.107C5.309,3.17 5.043,3.273 4.637,3.433L3.929,3.712C3.299,3.959 3.175,4.022 3.081,4.117C2.993,4.204 2.898,4.374 2.869,4.495C2.837,4.628 2.844,4.737 2.945,5.309C3.556,8.774 5.421,12.546 8.528,15.653C11.689,18.814 15.512,20.663 19.016,21.233C19.593,21.326 19.702,21.332 19.836,21.298C19.956,21.267 20.125,21.168 20.212,21.079C20.305,20.982 20.366,20.856 20.602,20.218L20.843,19.566C20.992,19.164 21.088,18.901 21.146,18.695C21.202,18.497 21.203,18.409 21.197,18.359C21.178,18.192 21.107,18.035 20.993,17.911C20.959,17.874 20.892,17.817 20.706,17.729C20.512,17.637 20.251,17.538 19.85,17.386L16.483,16.114C16.396,16.081 16.376,16.073 16.36,16.069C16.09,15.982 15.794,16.045 15.583,16.235C15.571,16.246 15.556,16.26 15.49,16.326L15.48,16.336C15.412,16.404 15.358,16.457 15.292,16.515C14.427,17.271 13.047,17.379 12.075,16.766C12.001,16.719 11.964,16.693 11.914,16.656C11.127,16.085 10.392,15.464 9.573,14.645C8.754,13.826 8.133,13.092 7.562,12.304C7.525,12.254 7.499,12.217 7.452,12.144C6.839,11.172 6.947,9.791 7.703,8.926C7.761,8.86 7.814,8.806 7.883,8.738L7.892,8.728C7.958,8.662 7.972,8.647 7.983,8.635C8.173,8.424 8.236,8.128 8.15,7.858C8.145,7.842 8.138,7.823 8.105,7.736L6.849,4.414C6.695,4.006 6.593,3.739 6.5,3.542C6.41,3.352 6.352,3.284 6.314,3.25C6.188,3.136 6.028,3.065 5.859,3.048C5.808,3.043 5.719,3.046 5.518,3.107ZM5.114,1.766C5.399,1.68 5.689,1.624 5.999,1.655C6.466,1.702 6.905,1.897 7.252,2.211C7.484,2.42 7.637,2.673 7.764,2.941C7.886,3.197 8.007,3.518 8.149,3.893L8.159,3.919L9.414,7.241L9.419,7.254C9.444,7.32 9.466,7.377 9.483,7.432C9.721,8.175 9.547,8.988 9.026,9.569C8.987,9.613 8.945,9.655 8.895,9.705L8.885,9.716C8.803,9.797 8.782,9.819 8.757,9.847C8.409,10.246 8.354,10.948 8.637,11.397C8.659,11.432 8.665,11.441 8.695,11.481L8.695,11.483C9.221,12.207 9.794,12.886 10.563,13.655C11.332,14.424 12.011,14.997 12.736,15.523L12.737,15.524C12.777,15.553 12.786,15.559 12.821,15.582C13.27,15.864 13.972,15.81 14.371,15.461C14.399,15.436 14.421,15.415 14.502,15.333L14.513,15.323C14.563,15.273 14.606,15.231 14.649,15.192C15.23,14.672 16.043,14.498 16.786,14.735C16.841,14.752 16.898,14.774 16.964,14.799L16.978,14.804L20.345,16.077L20.37,16.086C20.739,16.226 21.054,16.345 21.306,16.464C21.57,16.59 21.819,16.74 22.027,16.967C22.339,17.308 22.535,17.739 22.588,18.198C22.623,18.504 22.573,18.79 22.494,19.072C22.419,19.34 22.302,19.656 22.166,20.026L22.156,20.052L21.915,20.703C21.903,20.735 21.892,20.767 21.88,20.798C21.699,21.29 21.542,21.716 21.219,22.051C20.953,22.327 20.556,22.558 20.185,22.653C19.736,22.769 19.33,22.703 18.871,22.627C18.844,22.623 18.818,22.619 18.792,22.615C14.986,21.996 10.899,20.004 7.538,16.643C4.235,13.34 2.229,9.31 1.566,5.553C1.562,5.526 1.557,5.5 1.552,5.474C1.471,5.018 1.4,4.615 1.508,4.166C1.597,3.795 1.821,3.397 2.09,3.127C2.417,2.8 2.838,2.635 3.323,2.445C3.354,2.433 3.385,2.421 3.417,2.409L4.125,2.13L4.151,2.12C4.524,1.973 4.844,1.848 5.114,1.766Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
            addPath(
                pathData = addPathNodes("M13,5.7V7.3"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M15.3,4.144V8.856"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M17.6,3V10"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = addPathNodes("M19.9,5.188V7.811"),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }.build().also { _callRecording = it }
    }

private var _callRecording: ImageVector? = null
