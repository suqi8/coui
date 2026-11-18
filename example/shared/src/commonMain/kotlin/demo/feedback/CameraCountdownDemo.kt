// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.feedback

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CameraCountdown
import io.github.suqi8.coui.kmp.basic.CameraCountdownDefaults
import io.github.suqi8.coui.kmp.basic.CameraCountdownRing
import io.github.suqi8.coui.kmp.basic.CameraExposureCountdown
import io.github.suqi8.coui.kmp.basic.CameraRecordTimer
import io.github.suqi8.coui.kmp.basic.Slider
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_countdown_compact
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_countdown_compact_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_countdown_duration
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_countdown_restart
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_countdown_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_countdown_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_countdown_variant_draining
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_countdown_variant_exposure
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_countdown_variant_ring
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** The demo page of `CameraCountdown`, listed in its category and found by the search. */
internal val CameraCountdownDemo = Demo(
    id = "camera_countdown",
    apiName = "CameraCountdown",
    title = Res.string.demo_camera_countdown_title,
    category = DemoCategory.Feedback,
    page = DemoPage.Variants { cameraCountdownPage() },
    searchTerms = Res.string.demo_camera_countdown_search_terms,
    isNew = true,
)

/** The camera's self-timer lengths (`timer_shutter_values`): 3s and 10s. */
private val CountdownDurations = listOf(3_000L, 10_000L)

/** The long exposure the exposure variant counts down, repeated. */
private const val COUNTDOWN_EXPOSURE_MILLIS = 12_000L

private fun LazyListScope.cameraCountdownPage() {
    playground {
        var durationIndex by rememberSaveable { mutableIntStateOf(0) }
        var compact by rememberSaveable { mutableStateOf(false) }
        var run by remember { mutableIntStateOf(0) }
        val remaining = rememberCountdown(CountdownDurations[durationIndex], key = run, repeat = false)

        PlaygroundPreview {
            CountdownStage(minHeight = CameraCountdownDefaults.LargeSize + 32.dp) {
                CameraCountdown(remainingMillis = remaining, compact = compact)
            }
            TextButton(
                text = stringResource(Res.string.demo_camera_countdown_restart),
                onClick = { run++ },
                modifier = Modifier
                    .padding(top = 12.dp)
                    .align(Alignment.CenterHorizontally),
            )
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = CountdownDurations.map { "${it / 1000}s" },
                    selectedIndex = durationIndex,
                    title = stringResource(Res.string.demo_camera_countdown_duration),
                    onSelectedIndexChange = { durationIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = compact,
                    onCheckedChange = { compact = it },
                    title = stringResource(Res.string.demo_camera_countdown_compact),
                    summary = stringResource(Res.string.demo_camera_countdown_compact_summary),
                )
            }
        }
    }
    variant(key = "exposure", title = Res.string.demo_camera_countdown_variant_exposure, inCard = false) {
        val remaining = rememberCountdown(COUNTDOWN_EXPOSURE_MILLIS, key = Unit, repeat = true)
        CountdownStage {
            CameraExposureCountdown(remainingMillis = remaining)
        }
    }
    variant(key = "ring", title = Res.string.demo_camera_countdown_variant_ring) {
        var progress by remember { mutableFloatStateOf(0.4f) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            CountdownStage(fillWidth = false) {
                CameraCountdownRing(
                    progress = progress,
                    modifier = Modifier.size(CameraCountdownDefaults.RingSize),
                )
            }
        }
        Slider(
            value = progress,
            onValueChange = { progress = it },
            modifier = Modifier.padding(16.dp),
        )
    }
    variant(key = "draining", title = Res.string.demo_camera_countdown_variant_draining, inCard = false) {
        val total = CameraCountdownDefaults.RecordingStopCountdownMillis.toLong()
        val remaining = rememberCountdown(total, key = Unit, repeat = true)
        val elapsed = (total - remaining).toFloat() / total
        CountdownStage {
            CameraCountdownRing(
                progress = CameraCountdownDefaults.DrainingRingEasing.transform(elapsed),
                modifier = Modifier.size(CameraCountdownDefaults.TinyDrainingRingSize),
                draining = true,
                strokeWidth = CameraCountdownDefaults.TinyDrainingRingStrokeWidth,
            ) {
                CameraRecordTimer(timeMillis = remaining, countDown = true, showBackground = false)
            }
        }
    }
}

/**
 * The time left of a countdown of [totalMillis], restarted whenever [key] changes and, with
 * [repeat], started over each time it runs out.
 */
@Composable
private fun rememberCountdown(totalMillis: Long, key: Any, repeat: Boolean): Long {
    var remaining by remember(totalMillis, key) { mutableLongStateOf(totalMillis) }
    LaunchedEffect(totalMillis, key, repeat) {
        do {
            val start = withFrameMillis { it }
            remaining = totalMillis
            while (remaining > 0L) {
                remaining = (totalMillis - (withFrameMillis { it } - start)).coerceAtLeast(0L)
            }
        } while (repeat)
    }
    return remaining
}

/** A camera-like stage: a photo standing in for the viewfinder behind the countdown. */
@Composable
private fun CountdownStage(
    minHeight: Dp = 120.dp,
    fillWidth: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.blur_test_bg),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier
                .padding(24.dp)
                .heightIn(min = minHeight),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )
    }
}
