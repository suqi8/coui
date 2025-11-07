// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.anim

import androidx.compose.animation.core.Easing
import kotlin.math.PI
import kotlin.math.sin

val SinOutEasing: Easing = Easing { fraction ->
    sin((fraction * PI / 2).toFloat())
}
