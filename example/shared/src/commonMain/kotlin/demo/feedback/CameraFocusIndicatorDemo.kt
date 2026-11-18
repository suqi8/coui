// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.feedback

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CameraFocusIndicator
import io.github.suqi8.coui.kmp.basic.CameraFocusIndicatorState
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.rememberCameraFocusIndicatorState
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_hide
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_focus_indicator_auto_hide
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_focus_indicator_auto_hide_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_focus_indicator_focus_center
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_focus_indicator_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_focus_indicator_label_focus
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_focus_indicator_label_locked
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_focus_indicator_lock_button
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_focus_indicator_lock_button_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_focus_indicator_locked
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_focus_indicator_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_focus_indicator_show_label
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_focus_indicator_state_format
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_focus_indicator_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_focus_indicator_unlocked
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_focus_indicator_variant_locked
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/** The demo page of `CameraFocusIndicator`, listed in its category and found by the search. */
internal val CameraFocusIndicatorDemo = Demo(
    id = "camera_focus_indicator",
    apiName = "CameraFocusIndicator",
    title = Res.string.demo_camera_focus_indicator_title,
    category = DemoCategory.Feedback,
    page = DemoPage.Variants { cameraFocusIndicatorPage() },
    searchTerms = Res.string.demo_camera_focus_indicator_search_terms,
    isNew = true,
)

private fun LazyListScope.cameraFocusIndicatorPage() {
    playground {
        var enabled by rememberSaveable { mutableStateOf(true) }
        var autoHide by rememberSaveable { mutableStateOf(true) }
        var lockButton by rememberSaveable { mutableStateOf(true) }
        var showLabel by rememberSaveable { mutableStateOf(false) }
        val state = rememberCameraFocusIndicatorState()
        var stageSize by remember { mutableStateOf(IntSize.Zero) }

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            FocusStage(onSizeChange = { stageSize = it }) {
                CameraFocusIndicator(
                    state = state,
                    modifier = Modifier.fillMaxSize(),
                    enabled = enabled,
                    autoHide = autoHide,
                    showLockButton = lockButton,
                    label = if (showLabel) focusLabel(state) else null,
                )
            }
            Text(
                text = stringResource(Res.string.demo_camera_focus_indicator_hint),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
            )
            Text(
                text = focusStateText(state),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        PlaygroundControls {
            item {
                SwitchPreference(
                    checked = autoHide,
                    onCheckedChange = { autoHide = it },
                    title = stringResource(Res.string.demo_camera_focus_indicator_auto_hide),
                    summary = stringResource(Res.string.demo_camera_focus_indicator_auto_hide_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = lockButton,
                    onCheckedChange = { lockButton = it },
                    title = stringResource(Res.string.demo_camera_focus_indicator_lock_button),
                    summary = stringResource(Res.string.demo_camera_focus_indicator_lock_button_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = showLabel,
                    onCheckedChange = { showLabel = it },
                    title = stringResource(Res.string.demo_camera_focus_indicator_show_label),
                )
            }
            item {
                SwitchPreference(
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                    title = stringResource(Res.string.common_enabled),
                )
            }
            item {
                ArrowPreference(
                    title = stringResource(Res.string.demo_camera_focus_indicator_focus_center),
                    summary = "focusAt()",
                    onClick = { state.focusAt(Offset(stageSize.width / 2f, stageSize.height / 2f)) },
                )
            }
            item {
                ArrowPreference(
                    title = stringResource(Res.string.common_hide),
                    summary = "hide()",
                    onClick = { state.hide() },
                )
            }
        }
    }
    variant(key = "locked", title = Res.string.demo_camera_focus_indicator_variant_locked, inCard = false) {
        val state = rememberCameraFocusIndicatorState()
        FocusStage(
            onSizeChange = { size ->
                if (state.focusPosition == null) {
                    state.focusAt(Offset(size.width * 0.4f, size.height * 0.45f))
                    state.adjustExposure(0.2f)
                    state.lock(true)
                }
            },
        ) {
            CameraFocusIndicator(
                state = state,
                modifier = Modifier.fillMaxSize(),
                autoHide = false,
                label = focusLabel(state),
            )
        }
    }
}

/** The camera's focus label: "Focus", or the AE/AF lock hint while locked. */
@Composable
private fun focusLabel(state: CameraFocusIndicatorState): String = stringResource(
    if (state.isLocked) Res.string.demo_camera_focus_indicator_label_locked else Res.string.demo_camera_focus_indicator_label_focus,
)

/** The exposure (as -1..+1 around the center) and the lock state. */
@Composable
private fun focusStateText(state: CameraFocusIndicatorState): String {
    val signed = ((state.exposure - 0.5f) * 200f).roundToInt() / 100f
    val exposure = (if (signed > 0f) "+" else "") + signed.toString()
    val lock = stringResource(
        if (state.isLocked) Res.string.demo_camera_focus_indicator_locked else Res.string.demo_camera_focus_indicator_unlocked,
    )
    return stringResource(Res.string.demo_camera_focus_indicator_state_format, exposure, lock)
}

/** A photo standing in for the camera preview, with [content] laid over it. */
@Composable
private fun FocusStage(
    onSizeChange: (IntSize) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(440.dp)
            .clip(RoundedCornerShape(16.dp))
            .onSizeChanged(onSizeChange),
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
