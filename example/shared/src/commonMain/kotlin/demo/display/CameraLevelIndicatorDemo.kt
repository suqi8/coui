// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.display

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import io.github.suqi8.coui.kmp.basic.CameraGridOverlay
import io.github.suqi8.coui.kmp.basic.CameraGridType
import io.github.suqi8.coui.kmp.basic.CameraLevelIndicator
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.rememberCameraLevelState
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_level_indicator_center_mark
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_level_indicator_grid
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_level_indicator_grid_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_level_indicator_haptic
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_level_indicator_level
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_level_indicator_pitch
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_level_indicator_pitch_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_level_indicator_reading
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_level_indicator_roll
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_level_indicator_roll_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_level_indicator_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_level_indicator_show_level
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_level_indicator_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_level_indicator_variant_center_mark
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_level_indicator_variant_sway
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_level_indicator_variant_top_down
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/** The demo page of `CameraLevelIndicator`, listed in its category and found by the search. */
internal val CameraLevelIndicatorDemo = Demo(
    id = "camera_level_indicator",
    apiName = "CameraLevelIndicator",
    title = Res.string.demo_camera_level_indicator_title,
    category = DemoCategory.Display,
    page = DemoPage.Variants { cameraLevelIndicatorPage() },
    searchTerms = Res.string.demo_camera_level_indicator_search_terms,
    isNew = true,
)

private fun LazyListScope.cameraLevelIndicatorPage() {
    playground {
        var roll by rememberSaveable { mutableFloatStateOf(4f) }
        var pitch by rememberSaveable { mutableFloatStateOf(0f) }
        var showLevel by rememberSaveable { mutableStateOf(true) }
        var showCenterMark by rememberSaveable { mutableStateOf(false) }
        var showGrid by rememberSaveable { mutableStateOf(true) }
        var haptic by rememberSaveable { mutableStateOf(true) }
        val levelState = rememberCameraLevelState()

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            LevelStage {
                CameraGridOverlay(
                    type = CameraGridType.Grid,
                    visible = showGrid,
                    levelState = levelState,
                    modifier = Modifier.matchParentSize(),
                )
                CameraLevelIndicator(
                    rollDegrees = roll,
                    pitchDegrees = pitch,
                    state = levelState,
                    showLevel = showLevel,
                    showCenterMark = showCenterMark,
                    hapticFeedbackEnabled = haptic,
                    modifier = Modifier.matchParentSize(),
                )
            }
            val reading = stringResource(
                Res.string.demo_camera_level_indicator_reading,
                roll.roundToInt().toString(),
                pitch.roundToInt().toString(),
            )
            val level = stringResource(Res.string.demo_camera_level_indicator_level)
            Text(
                text = if (levelState.isLevel) "$reading · $level" else reading,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        PlaygroundControls {
            item {
                SliderPreference(
                    value = roll,
                    onValueChange = { roll = it },
                    title = stringResource(Res.string.demo_camera_level_indicator_roll),
                    summary = stringResource(Res.string.demo_camera_level_indicator_roll_summary),
                    valueText = "${roll.roundToInt()}°",
                    valueRange = -100f..100f,
                )
            }
            item {
                SliderPreference(
                    value = pitch,
                    onValueChange = { pitch = it },
                    title = stringResource(Res.string.demo_camera_level_indicator_pitch),
                    summary = stringResource(Res.string.demo_camera_level_indicator_pitch_summary),
                    valueText = "${pitch.roundToInt()}°",
                    valueRange = -10f..110f,
                )
            }
            item {
                SwitchPreference(
                    checked = showLevel,
                    onCheckedChange = { showLevel = it },
                    title = stringResource(Res.string.demo_camera_level_indicator_show_level),
                )
            }
            item {
                SwitchPreference(
                    checked = showCenterMark,
                    onCheckedChange = { showCenterMark = it },
                    title = stringResource(Res.string.demo_camera_level_indicator_center_mark),
                )
            }
            item {
                SwitchPreference(
                    checked = showGrid,
                    onCheckedChange = { showGrid = it },
                    title = stringResource(Res.string.demo_camera_level_indicator_grid),
                    summary = stringResource(Res.string.demo_camera_level_indicator_grid_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = haptic,
                    onCheckedChange = { haptic = it },
                    title = stringResource(Res.string.demo_camera_level_indicator_haptic),
                )
            }
        }
    }
    variant(key = "sway", title = Res.string.demo_camera_level_indicator_variant_sway, inCard = false) {
        // A hand-held camera rocking through level: the line fades in, turns the accent color and
        // fades out after holding level, then returns as the device tilts again.
        val transition = rememberInfiniteTransition()
        val roll by transition.animateFloat(
            initialValue = -9f,
            targetValue = 9f,
            animationSpec = infiniteRepeatable(tween(durationMillis = 5000, easing = SwayEasing), RepeatMode.Reverse),
        )
        LevelStage {
            CameraLevelIndicator(
                rollDegrees = roll,
                pitchDegrees = 0f,
                hapticFeedbackEnabled = false,
                modifier = Modifier.matchParentSize(),
            )
        }
    }
    variant(key = "top_down", title = Res.string.demo_camera_level_indicator_variant_top_down, inCard = false) {
        // Lying almost flat: the accent cross circles the white target cross.
        val transition = rememberInfiniteTransition()
        val turn by transition.animateFloat(
            initialValue = 0f,
            targetValue = 2f * PI.toFloat(),
            animationSpec = infiniteRepeatable(tween(durationMillis = 6000, easing = LinearEasing)),
        )
        LevelStage {
            CameraLevelIndicator(
                rollDegrees = 4f * cos(turn),
                pitchDegrees = 90f + 4f * sin(turn),
                modifier = Modifier.matchParentSize(),
            )
        }
    }
    variant(key = "center_mark", title = Res.string.demo_camera_level_indicator_variant_center_mark, inCard = false) {
        LevelStage {
            CameraGridOverlay(type = CameraGridType.Grid, modifier = Modifier.matchParentSize())
            CameraLevelIndicator(
                rollDegrees = 0f,
                pitchDegrees = 0f,
                showLevel = false,
                showCenterMark = true,
                modifier = Modifier.matchParentSize(),
            )
        }
    }
}

/** A slow sine sway, so the roll lingers at both ends and crosses level briskly. */
private val SwayEasing = Easing { (1f - cos(it * PI.toFloat())) / 2f }

/** A portrait 3:4 viewfinder: a photo standing in for the camera preview. */
@Composable
private fun LevelStage(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 360.dp)
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .clip(RoundedCornerShape(16.dp)),
        ) {
            Image(
                painter = painterResource(Res.drawable.blur_test_bg),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
            )
            content()
        }
    }
}
