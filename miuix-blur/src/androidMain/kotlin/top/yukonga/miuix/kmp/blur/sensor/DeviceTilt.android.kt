// Copyright 2026, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package top.yukonga.miuix.kmp.blur.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberDeviceTilt(smoothing: Float): State<DeviceTilt> {
    val context = LocalContext.current
    val tilt = remember { mutableStateOf(DeviceTilt.Zero) }

    DisposableEffect(context, smoothing) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
            ?: sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (sensorManager == null || sensor == null) {
            return@DisposableEffect onDispose { }
        }

        val rotationMatrix = FloatArray(9)
        val orientation = FloatArray(3)
        var smoothPitch = 0f
        var smoothRoll = 0f
        var smoothGravityX = 0f
        var smoothGravityY = 0f
        var initialized = false

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getOrientation(rotationMatrix, orientation)
                // orientation: [azimuth, pitch, roll] in radians
                // Gravity in device space = R^T * (0, 0, -1)_world = -(R[2][0], R[2][1], R[2][2])
                val gravityX = -rotationMatrix[6]
                val gravityY = -rotationMatrix[7]
                if (!initialized) {
                    smoothPitch = orientation[1]
                    smoothRoll = orientation[2]
                    smoothGravityX = gravityX
                    smoothGravityY = gravityY
                    initialized = true
                } else {
                    smoothPitch += (orientation[1] - smoothPitch) * smoothing
                    smoothRoll += (orientation[2] - smoothRoll) * smoothing
                    smoothGravityX += (gravityX - smoothGravityX) * smoothing
                    smoothGravityY += (gravityY - smoothGravityY) * smoothing
                }
                tilt.value = DeviceTilt(
                    pitch = smoothPitch,
                    roll = smoothRoll,
                    gravityX = smoothGravityX,
                    gravityY = smoothGravityY,
                )
            }

            override fun onAccuracyChanged(s: Sensor?, a: Int) = Unit
        }
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    return tilt
}
