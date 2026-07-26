// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.CircularProgressIndicator
import com.suqi8.coui.kmp.basic.InfiniteProgressIndicator
import com.suqi8.coui.kmp.basic.LinearProgressIndicator
import com.suqi8.coui.kmp.basic.SmallTitle

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
        }
    }
}
