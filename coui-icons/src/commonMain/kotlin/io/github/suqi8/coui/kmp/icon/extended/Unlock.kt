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

/** Unlock: ColorOS `note_ic_decrypted_normal` (com.coloros.note_16.7.2). */
val COUIIcons.Unlock: ImageVector
    get() {
        _unlock?.let { return it }
        return ImageVector.Builder(
            name = "Unlock",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            addPath(
                pathData = addPathNodes("M12,1.657C10.153,1.657 8.7,3.093 8.7,4.81V9.6H19.119C20.279,9.6 21.2,10.553 21.2,11.704V19.696C21.2,20.847 20.279,21.8 19.119,21.8H4.881C3.721,21.8 2.8,20.847 2.8,19.696V11.704C2.8,10.553 3.721,9.6 4.881,9.6H7.3V4.81C7.3,2.271 9.429,0.257 12,0.257C14.571,0.257 16.7,2.271 16.7,4.81V6.3C16.7,6.686 16.387,7 16,7C15.613,7 15.3,6.686 15.3,6.3V4.81C15.3,3.093 13.847,1.657 12,1.657ZM4.881,11C4.516,11 4.2,11.304 4.2,11.704V19.696C4.2,20.096 4.516,20.4 4.881,20.4H19.119C19.484,20.4 19.8,20.096 19.8,19.696V11.704C19.8,11.304 19.484,11 19.119,11H4.881ZM13.5,14.315C13.5,14.891 13.175,15.391 12.699,15.642V17.699C12.699,18.085 12.386,18.399 11.999,18.399C11.612,18.399 11.299,18.085 11.299,17.699V15.642C10.824,15.39 10.5,14.89 10.5,14.315C10.5,13.487 11.172,12.815 12,12.815C12.828,12.815 13.5,13.487 13.5,14.315Z"),
                pathFillType = PathFillType.EvenOdd,
                fill = SolidColor(Color.Black),
            )
        }.build().also { _unlock = it }
    }

private var _unlock: ImageVector? = null
