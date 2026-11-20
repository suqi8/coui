// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Translate: ColorOS `drawable/deep_link_base_translate` (com.coloros.note_16.7.2). */
val COUIIcons.Translate: ImageVector
    get() {
        _translate?.let { return it }
        return ImageVector.Builder(
            name = "Translate",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M5.959,4.5C5.959,4.294 6.02,4.093 6.135,3.922C6.249,3.75 6.412,3.617 6.602,3.538C6.792,3.459 7.002,3.438 7.204,3.479C7.406,3.519 7.592,3.618 7.737,3.764C7.883,3.909 7.982,4.095 8.022,4.297C8.063,4.499 8.042,4.709 7.963,4.899C7.884,5.089 7.751,5.252 7.579,5.366C7.408,5.481 7.207,5.542 7.001,5.542C6.725,5.542 6.46,5.432 6.264,5.237C6.069,5.041 5.959,4.777 5.959,4.5ZM21.084,17.635H19.134L18.624,16.097H15.695L15.195,17.635H13.343L16.251,9.575H18.167L21.084,17.635ZM18.197,14.775L17.713,13.269C17.489,12.567 17.315,11.85 17.193,11.124H17.167C17.026,11.848 16.846,12.564 16.627,13.269L16.127,14.775H18.197ZM15.463,3.073L10.281,13.323C9.385,12.981 8.553,12.491 7.819,11.874C8.662,10.761 9.298,9.505 9.695,8.167H11.167V6.667H2.834V8.167H8.127C7.809,9.088 7.359,9.959 6.792,10.752C6.435,10.252 6.178,9.689 6.034,9.092L4.574,9.435C4.791,10.349 5.207,11.204 5.794,11.939C4.85,12.917 3.72,13.694 2.469,14.225L3.048,15.609C4.471,15.005 5.759,14.123 6.837,13.014C7.662,13.712 8.595,14.271 9.601,14.669L7.201,19.416L8.539,20.094L16.801,3.75L15.463,3.073Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _translate = it }
    }

private var _translate: ImageVector? = null
