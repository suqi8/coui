// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.blur.sensor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
actual fun rememberDeviceTilt(smoothing: Float): State<DeviceTilt> = remember { mutableStateOf(DeviceTilt.Zero) }
