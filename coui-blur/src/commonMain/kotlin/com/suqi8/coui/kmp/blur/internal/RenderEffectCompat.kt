// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.blur.internal

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RenderEffect
import com.suqi8.coui.kmp.blur.RuntimeShader

/**
 * Chains [other] after this [RenderEffect]. If this is null, returns [other] directly.
 */
internal expect fun RenderEffect?.chain(other: RenderEffect): RenderEffect

/**
 * Creates a [RenderEffect] from a [RuntimeShader] that reads its input image
 * from the uniform named [uniformShaderName].
 */
internal expect fun runtimeShaderEffect(
    runtimeShader: RuntimeShader,
    uniformShaderName: String,
): RenderEffect
