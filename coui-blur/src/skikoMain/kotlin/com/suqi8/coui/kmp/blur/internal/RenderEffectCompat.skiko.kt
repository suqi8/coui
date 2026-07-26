// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.blur.internal

import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.skiaImageFilter
import org.jetbrains.skia.ImageFilter
import com.suqi8.coui.kmp.shader.RuntimeShader
import com.suqi8.coui.kmp.shader.asSkikoRuntimeShader

internal actual fun RenderEffect?.chain(other: RenderEffect): RenderEffect = if (this != null) {
    ImageFilter.makeCompose(
        other.skiaImageFilter,
        this.skiaImageFilter,
    ).asComposeRenderEffect()
} else {
    other
}

internal actual fun runtimeShaderEffect(
    runtimeShader: RuntimeShader,
    uniformShaderName: String,
): RenderEffect = ImageFilter.makeRuntimeShader(
    runtimeShader.asSkikoRuntimeShader(),
    uniformShaderName,
    null,
).asComposeRenderEffect()
