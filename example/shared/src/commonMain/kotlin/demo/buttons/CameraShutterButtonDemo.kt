// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import io.github.suqi8.coui.kmp.basic.CameraShutterButton
import io.github.suqi8.coui.kmp.basic.CameraShutterMode
import io.github.suqi8.coui.kmp.basic.CameraShutterStyle
import io.github.suqi8.coui.kmp.basic.CameraSwitchButton
import io.github.suqi8.coui.kmp.basic.CameraSwitchButtonDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBlurProvider
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_loading
import io.github.suqi8.coui.kmp.shared.generated.resources.common_mode
import io.github.suqi8.coui.kmp.shared.generated.resources.common_orientation
import io.github.suqi8.coui.kmp.shared.generated.resources.common_style
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_capture
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_long_exposure
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_long_exposure_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_material_blur
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_material_blur_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_pause
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_paused
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_photo
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_recording
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_resume
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_shots
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_switch_camera
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_timer
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_timer_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_variant_controls
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_variant_states
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_variant_styles
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_shutter_button_video
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import utils.rememberPopupMaterialBlur

/** The demo page of `CameraShutterButton` and `CameraSwitchButton`, found by the search. */
internal val CameraShutterButtonDemo = Demo(
    id = "camera_shutter_button",
    apiName = "CameraShutterButton",
    title = Res.string.demo_camera_shutter_button_title,
    category = DemoCategory.Buttons,
    page = DemoPage.Variants { cameraShutterButtonPage() },
    searchTerms = Res.string.demo_camera_shutter_button_search_terms,
    isNew = true,
)

/** How long the demo long exposure runs. */
private const val LONG_EXPOSURE_MILLIS = 3000L

/** How long the demo shows the spinner after a long exposure, while "saving". */
private const val SAVING_MILLIS = 800L

private fun LazyListScope.cameraShutterButtonPage() {
    playground {
        var video by rememberSaveable { mutableStateOf(false) }
        var style by rememberSaveable { mutableIntStateOf(CameraShutterStyle.Glass.ordinal) }
        var materialBlur by rememberSaveable { mutableStateOf(true) }
        var longExposure by rememberSaveable { mutableStateOf(false) }
        var timer by rememberSaveable { mutableStateOf(false) }
        var loading by rememberSaveable { mutableStateOf(false) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        var orientation by rememberSaveable { mutableIntStateOf(0) }
        var recording by remember { mutableStateOf(false) }
        var paused by remember { mutableStateOf(false) }
        var shots by remember { mutableIntStateOf(0) }
        var exposing by remember { mutableStateOf(false) }
        var saving by remember { mutableStateOf(false) }
        var progress by remember { mutableStateOf<Float?>(null) }

        LaunchedEffect(exposing) {
            if (!exposing) return@LaunchedEffect
            val start = withFrameMillis { it }
            var elapsed = 0L
            while (elapsed < LONG_EXPOSURE_MILLIS) {
                elapsed = withFrameMillis { it } - start
                progress = (elapsed.toFloat() / LONG_EXPOSURE_MILLIS).coerceAtMost(1f)
            }
            progress = null
            saving = true
            delay(SAVING_MILLIS)
            saving = false
            shots++
            exposing = false
        }

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            ShutterStage(materialBlur = materialBlur) { blur ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (video && recording) {
                        CameraSwitchButton(
                            onClick = { paused = !paused },
                            enabled = enabled,
                            icon = if (paused) CameraSwitchButtonDefaults.ResumeIcon else CameraSwitchButtonDefaults.PauseIcon,
                            contentDescription = stringResource(
                                if (paused) Res.string.demo_camera_shutter_button_resume else Res.string.demo_camera_shutter_button_pause,
                            ),
                            iconRotation = orientation * 90f,
                            size = CameraSwitchButtonDefaults.ControlSize,
                            materialBlur = blur,
                        )
                    } else {
                        Spacer(Modifier.size(CameraSwitchButtonDefaults.ControlSize))
                    }
                    CameraShutterButton(
                        onClick = {
                            when {
                                // A running long exposure ignores further shots.
                                exposing -> Unit

                                video -> {
                                    recording = !recording
                                    paused = false
                                }

                                longExposure -> exposing = true

                                else -> shots++
                            }
                        },
                        enabled = enabled,
                        mode = if (video) CameraShutterMode.Video else CameraShutterMode.Photo,
                        recording = recording,
                        captureProgress = progress,
                        loading = loading || saving,
                        timerLabel = if (timer) "3" else null,
                        style = CameraShutterStyle.entries[style],
                        materialBlur = blur,
                    )
                    CameraSwitchButton(
                        onClick = {},
                        enabled = enabled && !recording,
                        contentDescription = stringResource(Res.string.demo_camera_shutter_button_switch_camera),
                        iconRotation = orientation * 90f,
                        size = CameraSwitchButtonDefaults.ControlSize,
                        materialBlur = blur,
                    )
                }
            }
            Text(
                text = when {
                    video && recording && paused -> stringResource(Res.string.demo_camera_shutter_button_paused)
                    video && recording -> stringResource(Res.string.demo_camera_shutter_button_recording)
                    else -> stringResource(Res.string.demo_camera_shutter_button_shots, shots)
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_mode),
                    summary = "mode",
                    items = listOf(
                        stringResource(Res.string.demo_camera_shutter_button_photo),
                        stringResource(Res.string.demo_camera_shutter_button_video),
                    ),
                    selectedIndex = if (video) 1 else 0,
                    onSelectedIndexChange = {
                        video = it == 1
                        recording = false
                        paused = false
                    },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_style),
                    summary = "style",
                    items = CameraShutterStyle.entries.map { it.name },
                    selectedIndex = style,
                    onSelectedIndexChange = { style = it },
                )
            }
            item {
                SwitchPreference(
                    checked = materialBlur,
                    onCheckedChange = { materialBlur = it },
                    title = stringResource(Res.string.demo_camera_shutter_button_material_blur),
                    summary = stringResource(Res.string.demo_camera_shutter_button_material_blur_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = longExposure,
                    onCheckedChange = { longExposure = it },
                    title = stringResource(Res.string.demo_camera_shutter_button_long_exposure),
                    summary = stringResource(Res.string.demo_camera_shutter_button_long_exposure_summary),
                    enabled = !video,
                )
            }
            item {
                SwitchPreference(
                    checked = timer,
                    onCheckedChange = { timer = it },
                    title = stringResource(Res.string.demo_camera_shutter_button_timer),
                    summary = stringResource(Res.string.demo_camera_shutter_button_timer_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = loading,
                    onCheckedChange = { loading = it },
                    title = stringResource(Res.string.common_loading),
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_orientation),
                    summary = "iconRotation",
                    items = listOf("0°", "90°", "180°", "270°"),
                    selectedIndex = orientation,
                    onSelectedIndexChange = { orientation = it },
                )
            }
            item {
                SwitchPreference(
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                    title = stringResource(Res.string.common_enabled),
                )
            }
        }
    }
    variant(key = "states", title = Res.string.demo_camera_shutter_button_variant_states, inCard = false) {
        ShutterStage(materialBlur = true) { blur ->
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CameraShutterButton(onClick = {}, materialBlur = blur)
                    CameraShutterButton(onClick = {}, mode = CameraShutterMode.Video, materialBlur = blur)
                    CameraShutterButton(onClick = {}, mode = CameraShutterMode.Video, recording = true, materialBlur = blur)
                    CameraShutterButton(onClick = {}, captureProgress = 0.6f, materialBlur = blur)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CameraShutterButton(onClick = {}, loading = true, materialBlur = blur)
                    CameraShutterButton(onClick = {}, timerLabel = "10", materialBlur = blur)
                    CameraShutterButton(onClick = {}, enabled = false, materialBlur = blur)
                }
            }
        }
    }
    variant(key = "styles", title = Res.string.demo_camera_shutter_button_variant_styles, inCard = false) {
        var video by remember { mutableStateOf(false) }
        ShutterStage(materialBlur = true) { blur ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CameraShutterStyle.entries.forEach { style ->
                    CameraShutterButton(
                        onClick = { video = !video },
                        mode = if (video) CameraShutterMode.Video else CameraShutterMode.Photo,
                        style = style,
                        materialBlur = blur,
                    )
                }
            }
        }
    }
    variant(key = "controls", title = Res.string.demo_camera_shutter_button_variant_controls, inCard = false) {
        var paused by remember { mutableStateOf(false) }
        var turns by remember { mutableIntStateOf(0) }
        ShutterStage(materialBlur = true) { blur ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CameraSwitchButton(
                    onClick = { turns++ },
                    contentDescription = stringResource(Res.string.demo_camera_shutter_button_switch_camera),
                    iconRotation = turns * 90f,
                    materialBlur = blur,
                )
                CameraSwitchButton(
                    onClick = { paused = !paused },
                    icon = if (paused) CameraSwitchButtonDefaults.ResumeIcon else CameraSwitchButtonDefaults.PauseIcon,
                    contentDescription = stringResource(
                        if (paused) Res.string.demo_camera_shutter_button_resume else Res.string.demo_camera_shutter_button_pause,
                    ),
                    size = CameraSwitchButtonDefaults.ControlSize,
                    materialBlur = blur,
                )
                // The capture-while-recording button keeps the plain 50% black circle.
                CameraSwitchButton(
                    onClick = {},
                    icon = CameraSwitchButtonDefaults.CaptureIcon,
                    contentDescription = stringResource(Res.string.demo_camera_shutter_button_capture),
                    size = CameraSwitchButtonDefaults.ControlSize,
                    iconSize = CameraSwitchButtonDefaults.CaptureIconSize,
                )
            }
        }
    }
}

/**
 * A camera-like stage: a photo standing in for the viewfinder, recorded into a backdrop so the
 * buttons over it can take the camera material blur.
 */
@Composable
private fun ShutterStage(
    materialBlur: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (MaterialBlurProvider?) -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    val provider = rememberPopupMaterialBlur(backdrop)
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.blur_test_bg),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .layerBackdrop(backdrop),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier.padding(vertical = 32.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content(if (materialBlur) provider else null)
        }
    }
}
