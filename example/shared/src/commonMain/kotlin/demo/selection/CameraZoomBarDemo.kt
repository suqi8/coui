// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.selection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CameraLensBarDefaults
import io.github.suqi8.coui.kmp.basic.CameraZoomBar
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_value_format
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_zoom_bar_extended_range
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_zoom_bar_extended_range_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_zoom_bar_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_zoom_bar_step_buttons
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_zoom_bar_step_buttons_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_zoom_bar_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_zoom_bar_value_labels
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_zoom_bar_value_labels_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_zoom_bar_variant_bar_only
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** The demo page of `CameraZoomBar`, listed in its category and found by the search. */
internal val CameraZoomBarDemo = Demo(
    id = "camera_zoom_bar",
    apiName = "CameraZoomBar",
    title = Res.string.demo_camera_zoom_bar_title,
    category = DemoCategory.Selection,
    page = DemoPage.Variants { cameraZoomBarPage() },
    searchTerms = Res.string.demo_camera_zoom_bar_search_terms,
    isNew = true,
)

private fun LazyListScope.cameraZoomBarPage() {
    playground {
        var zoom by rememberSaveable { mutableFloatStateOf(1f) }
        var extended by rememberSaveable { mutableStateOf(false) }
        val maxZoom = if (extended) 120f else 20f
        var buttons by rememberSaveable { mutableStateOf(true) }
        var labels by rememberSaveable { mutableStateOf(true) }
        var enabled by rememberSaveable { mutableStateOf(true) }

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            ZoomStage {
                CameraZoomBar(
                    zoom = zoom.coerceAtMost(maxZoom),
                    onZoomChange = { zoom = it },
                    enabled = enabled,
                    maxZoom = maxZoom,
                    showStepButtons = buttons,
                    showValueLabels = labels,
                )
            }
            Text(
                text = stringResource(Res.string.common_value_format, CameraLensBarDefaults.formatZoomRatio(zoom) + "×"),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        PlaygroundControls {
            item {
                SwitchPreference(
                    checked = buttons,
                    onCheckedChange = { buttons = it },
                    title = stringResource(Res.string.demo_camera_zoom_bar_step_buttons),
                    summary = stringResource(Res.string.demo_camera_zoom_bar_step_buttons_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = labels,
                    onCheckedChange = { labels = it },
                    title = stringResource(Res.string.demo_camera_zoom_bar_value_labels),
                    summary = stringResource(Res.string.demo_camera_zoom_bar_value_labels_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = extended,
                    onCheckedChange = { extended = it },
                    title = stringResource(Res.string.demo_camera_zoom_bar_extended_range),
                    summary = stringResource(Res.string.demo_camera_zoom_bar_extended_range_summary),
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
    variant(key = "bar_only", title = Res.string.demo_camera_zoom_bar_variant_bar_only, inCard = false) {
        var zoom by remember { mutableFloatStateOf(2f) }
        ZoomStage {
            CameraZoomBar(
                zoom = zoom,
                onZoomChange = { zoom = it },
                showStepButtons = false,
                showValueLabels = false,
            )
        }
    }
}

/**
 * A camera-like stage: a darkened photo standing in for the viewfinder. The zoom control is wider
 * than some phones' content area, so it may overflow the stage at both ends there.
 */
@Composable
private fun ZoomStage(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
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
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.25f)),
        )
        Box(
            modifier = Modifier
                .wrapContentWidth(unbounded = true)
                .padding(vertical = 24.dp),
        ) {
            content()
        }
    }
}
