// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.CircularProgressIndicator
import io.github.suqi8.coui.kmp.basic.CircularProgressState
import io.github.suqi8.coui.kmp.basic.CircularProgressType
import io.github.suqi8.coui.kmp.basic.CompProgressIndicator
import io.github.suqi8.coui.kmp.basic.CompProgressIndicatorType
import io.github.suqi8.coui.kmp.basic.InfiniteProgressIndicator
import io.github.suqi8.coui.kmp.basic.LinearProgressIndicator
import io.github.suqi8.coui.kmp.basic.ProgressIndicatorDefaults
import io.github.suqi8.coui.kmp.basic.RotatingProgressIndicator
import io.github.suqi8.coui.kmp.basic.SmallTitle

fun LazyListScope.progressIndicatorSection() {
    item(key = "progressIndicator") {
        SmallTitle(text = "ProgressIndicator")
        val progressValues = listOf(0.0f, 0.25f, 0.5f, 0.75f, 1.0f, null)
        val animatedProgressValue by rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse,
            ),
        )

        LinearProgressIndicator(
            progress = animatedProgressValue,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
        )
        progressValues.forEach { progressValue ->
            LinearProgressIndicator(
                progress = progressValue,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
            )
        }
        LinearProgressIndicator(
            progress = 0.5f,
            enabled = false,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
        )
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            CircularProgressIndicator(
                progress = animatedProgressValue,
            )
            progressValues.forEach { progressValue ->
                CircularProgressIndicator(
                    progress = progressValue,
                )
            }
            InfiniteProgressIndicator(
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically),
            )
            RotatingProgressIndicator(
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically),
            )
            RotatingProgressIndicator(
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically),
                size = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorSize,
                ringDiameter = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorRingDiameter,
                strokeWidth = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorStrokeWidth,
            )
        }
        SmallTitle(text = "Circular progress states")
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            var state by remember { mutableStateOf(CircularProgressState.Normal) }
            CircularProgressIndicator(
                progress = 0.6f,
                state = state,
                type = CircularProgressType.OnImage,
                modifier = Modifier.clickable {
                    state = when (state) {
                        CircularProgressState.Normal -> CircularProgressState.Paused
                        CircularProgressState.Paused -> CircularProgressState.Error
                        CircularProgressState.Error -> CircularProgressState.Normal
                    }
                },
            )
            CircularProgressIndicator(progress = 0.6f, state = CircularProgressState.Error)
            CircularProgressIndicator(progress = 0.6f, type = CircularProgressType.FollowTheme)
            CircularProgressIndicator(
                progress = animatedProgressValue,
                size = ProgressIndicatorDefaults.MediumCircularProgressIndicatorSize,
                strokeWidth = ProgressIndicatorDefaults.MediumCircularProgressIndicatorStrokeWidth,
            )
        }
        SmallTitle(text = "CompProgressIndicator")
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CompProgressIndicator(type = CompProgressIndicatorType.Large)
            CompProgressIndicator(type = CompProgressIndicatorType.Small)
            CompProgressIndicator(type = CompProgressIndicatorType.SmallWithTextHorizontal, text = "Loading")
            CompProgressIndicator(type = CompProgressIndicatorType.LargeWithTextVertical, text = "Loading")
            CompProgressIndicator(type = CompProgressIndicatorType.SmallWithTextVertical, text = "Loading")
        }
    }
}
