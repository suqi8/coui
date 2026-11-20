// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.suqi8.coui.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons

/** Send: ColorOS `agent_input_ic_send_enable_normal` (com.android.mms_16.60.10). */
val COUIIcons.Send: ImageVector
    get() {
        _send?.let { return it }
        return ImageVector.Builder(
            name = "Send",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 22f,
            viewportHeight = 22f,
        ).apply {
            addPath(
                pathData = addPathNodes("M21.719,10.996C21.719,11.249 21.584,11.467 21.358,11.58L6.731,18.895C6.437,19.041 6.188,18.924 6.095,18.865C6.002,18.808 5.785,18.638 5.785,18.31V12.83C5.785,12.172 6.32,11.638 6.977,11.638H12.81C13.164,11.638 13.452,11.351 13.452,10.996C13.452,10.642 13.164,10.354 12.81,10.354H6.977C6.32,10.354 5.785,9.82 5.785,9.163V3.683C5.785,3.354 6.002,3.185 6.095,3.128C6.188,3.069 6.437,2.951 6.73,3.098L21.358,10.413C21.584,10.525 21.719,10.744 21.719,10.996ZM3.667,7.604H2.751C2.396,7.604 2.109,7.892 2.109,8.246C2.109,8.601 2.396,8.888 2.751,8.888H3.667C4.022,8.888 4.309,8.601 4.309,8.246C4.309,7.892 4.022,7.604 3.667,7.604ZM3.667,13.104H2.751C2.396,13.104 2.109,13.392 2.109,13.746C2.109,14.101 2.396,14.388 2.751,14.388H3.667C4.022,14.388 4.309,14.101 4.309,13.746C4.309,13.392 4.022,13.104 3.667,13.104ZM3.667,10.354H0.917C0.563,10.354 0.275,10.642 0.275,10.996C0.275,11.351 0.563,11.638 0.917,11.638H3.667C4.022,11.638 4.309,11.351 4.309,10.996C4.309,10.642 4.022,10.354 3.667,10.354Z"),
                fill = SolidColor(Color.Black),
            )
        }.build().also { _send = it }
    }

private var _send: ImageVector? = null
