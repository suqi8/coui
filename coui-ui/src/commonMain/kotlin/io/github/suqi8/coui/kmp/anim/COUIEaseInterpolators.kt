// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.anim

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

/** `COUIEaseInterpolator`: `PathInterpolator(0.33, 0, 0.67, 1)`, a symmetric ease-in-out. */
val COUIEaseEasing: Easing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** `COUIInEaseInterpolator`: `PathInterpolator(0, 0, 0.1, 1)`, a steep deceleration. */
val COUIInEaseEasing: Easing = CubicBezierEasing(0f, 0f, 0.1f, 1f)

/** `COUIMoveEaseInterpolator`: `PathInterpolator(0.3, 0, 0.1, 1)`, an ease-in with a steep deceleration. */
val COUIMoveEaseEasing: Easing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** `COUIOutEaseInterpolator`: `PathInterpolator(0.3, 0, 1, 1)`, an acceleration. */
val COUIOutEaseEasing: Easing = CubicBezierEasing(0.3f, 0f, 1f, 1f)
