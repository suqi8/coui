// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.feedback

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CameraRecordTimer
import io.github.suqi8.coui.kmp.basic.CameraRecordTimerDefaults
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.common_visible
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_record_timer_count_down
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_record_timer_count_down_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_record_timer_light
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_record_timer_plate
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_record_timer_recording
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_record_timer_recording_dot
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_record_timer_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_record_timer_start
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_record_timer_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_record_timer_variant_dot
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_record_timer_variant_hours
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_record_timer_variant_plate
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** The demo page of `CameraRecordTimer`, listed in its category and found by the search. */
internal val CameraRecordTimerDemo = Demo(
    id = "camera_record_timer",
    apiName = "CameraRecordTimer",
    title = Res.string.demo_camera_record_timer_title,
    category = DemoCategory.Feedback,
    page = DemoPage.Variants { cameraRecordTimerPage() },
    searchTerms = Res.string.demo_camera_record_timer_search_terms,
    isNew = true,
)

/** The recorded time the playground starts from: none, just under an hour, just under ten hours. */
private val RecordTimerStarts = listOf(0L, 59L * 60_000L + 50_000L, 9L * 3_600_000L + 59L * 60_000L + 50_000L)
private val RecordTimerStartLabels = listOf("00:00", "59:50", "09:59:50")

/** The count-down the playground runs: one minute. */
private const val RECORD_TIMER_COUNT_DOWN_MILLIS = 60_000L

private fun LazyListScope.cameraRecordTimerPage() {
    playground {
        var recording by rememberSaveable { mutableStateOf(true) }
        var visible by rememberSaveable { mutableStateOf(true) }
        var countDown by rememberSaveable { mutableStateOf(false) }
        var plate by rememberSaveable { mutableStateOf(true) }
        var dot by rememberSaveable { mutableStateOf(false) }
        var light by rememberSaveable { mutableStateOf(false) }
        var startIndex by rememberSaveable { mutableIntStateOf(0) }
        val elapsed = rememberRecordingElapsed(recording, startIndex, countDown)
        val time = if (countDown) {
            (RECORD_TIMER_COUNT_DOWN_MILLIS - elapsed).coerceAtLeast(0L)
        } else {
            RecordTimerStarts[startIndex] + elapsed
        }

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            RecordTimerStage {
                CameraRecordTimer(
                    timeMillis = time,
                    visible = visible,
                    countDown = countDown,
                    showBackground = plate,
                    showRecordingDot = dot,
                    fontWeight = if (light) CameraRecordTimerDefaults.LightFontWeight else CameraRecordTimerDefaults.RegularFontWeight,
                )
            }
        }
        PlaygroundControls {
            item {
                SwitchPreference(
                    checked = recording,
                    onCheckedChange = { recording = it },
                    title = stringResource(Res.string.demo_camera_record_timer_recording),
                )
            }
            item {
                SwitchPreference(
                    checked = visible,
                    onCheckedChange = { visible = it },
                    title = stringResource(Res.string.common_visible),
                )
            }
            item {
                OverlayDropdownPreference(
                    items = RecordTimerStartLabels,
                    selectedIndex = startIndex,
                    title = stringResource(Res.string.demo_camera_record_timer_start),
                    enabled = !countDown,
                    onSelectedIndexChange = { startIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = countDown,
                    onCheckedChange = { countDown = it },
                    title = stringResource(Res.string.demo_camera_record_timer_count_down),
                    summary = stringResource(Res.string.demo_camera_record_timer_count_down_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = plate,
                    onCheckedChange = { plate = it },
                    title = stringResource(Res.string.demo_camera_record_timer_plate),
                )
            }
            item {
                SwitchPreference(
                    checked = dot,
                    onCheckedChange = { dot = it },
                    title = stringResource(Res.string.demo_camera_record_timer_recording_dot),
                )
            }
            item {
                SwitchPreference(
                    checked = light,
                    onCheckedChange = { light = it },
                    title = stringResource(Res.string.demo_camera_record_timer_light),
                )
            }
        }
    }
    variant(key = "plate", title = Res.string.demo_camera_record_timer_variant_plate, inCard = false) {
        RecordTimerStage {
            CameraRecordTimer(timeMillis = 12_000L)
        }
    }
    variant(key = "dot", title = Res.string.demo_camera_record_timer_variant_dot, inCard = false) {
        val elapsed = rememberRecordingElapsed(running = true)
        RecordTimerStage {
            CameraRecordTimer(timeMillis = elapsed, showBackground = false, showRecordingDot = true)
        }
    }
    variant(key = "hours", title = Res.string.demo_camera_record_timer_variant_hours, inCard = false) {
        RecordTimerStage {
            CameraRecordTimer(timeMillis = 3_723_000L)
        }
    }
}

/**
 * The milliseconds counted while [running], like a recording clock; it starts over from zero when any
 * of [keys] changes.
 */
@Composable
private fun rememberRecordingElapsed(running: Boolean, vararg keys: Any): Long {
    var elapsed by remember(*keys) { mutableLongStateOf(0L) }
    LaunchedEffect(running, *keys) {
        if (!running) return@LaunchedEffect
        var last = withFrameMillis { it }
        while (true) {
            val now = withFrameMillis { it }
            elapsed += now - last
            last = now
        }
    }
    return elapsed
}

/** A camera-like stage: a photo standing in for the viewfinder behind the timer. */
@Composable
private fun RecordTimerStage(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
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
                .heightIn(min = 120.dp)
                .padding(vertical = 40.dp, horizontal = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content,
        )
    }
}
