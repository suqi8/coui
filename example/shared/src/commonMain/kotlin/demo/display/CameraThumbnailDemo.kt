// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.display

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CameraThumbnail
import io.github.suqi8.coui.kmp.basic.CameraThumbnailTransition
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.common_delete
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_orientation
import io.github.suqi8.coui.kmp.shared.generated.resources.common_value_format
import io.github.suqi8.coui.kmp.shared.generated.resources.common_visible
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_thumbnail_burst
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_thumbnail_empty
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_thumbnail_empty_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_thumbnail_opened
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_thumbnail_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_thumbnail_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_thumbnail_transition
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_thumbnail_variant_burst
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_thumbnail_variant_delete
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** The demo page of `CameraThumbnail`, listed in its category and found by the search. */
internal val CameraThumbnailDemo = Demo(
    id = "camera_thumbnail",
    apiName = "CameraThumbnail",
    title = Res.string.demo_camera_thumbnail_title,
    category = DemoCategory.Display,
    page = DemoPage.Variants { cameraThumbnailPage() },
    searchTerms = Res.string.demo_camera_thumbnail_search_terms,
    isNew = true,
)

private fun LazyListScope.cameraThumbnailPage() {
    playground {
        val photos = rememberDemoPhotos()
        var shot by rememberSaveable { mutableIntStateOf(0) }
        var empty by rememberSaveable { mutableStateOf(false) }
        var transition by rememberSaveable { mutableIntStateOf(CameraThumbnailTransition.ScaleIn.ordinal) }
        var orientation by rememberSaveable { mutableIntStateOf(0) }
        var visible by rememberSaveable { mutableStateOf(true) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        var opened by rememberSaveable { mutableIntStateOf(0) }

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            CameraBottomBar(
                onShutter = {
                    empty = false
                    shot++
                },
            ) {
                CameraThumbnail(
                    image = if (empty) null else photos[shot % photos.size],
                    onClick = { opened++ },
                    enabled = enabled,
                    visible = visible,
                    transition = CameraThumbnailTransition.entries[transition],
                    rotation = -Orientations[orientation],
                    contentDescription = stringResource(Res.string.demo_camera_thumbnail_title),
                )
            }
            Text(
                text = stringResource(Res.string.demo_camera_thumbnail_opened, opened),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
            )
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.demo_camera_thumbnail_transition),
                    summary = "transition",
                    items = CameraThumbnailTransition.entries.map { it.name },
                    selectedIndex = transition,
                    onSelectedIndexChange = { transition = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_orientation),
                    summary = "rotation",
                    items = Orientations.map { "$it°" },
                    selectedIndex = orientation,
                    onSelectedIndexChange = { orientation = it },
                )
            }
            item {
                SwitchPreference(
                    checked = empty,
                    onCheckedChange = { empty = it },
                    title = stringResource(Res.string.demo_camera_thumbnail_empty),
                    summary = stringResource(Res.string.demo_camera_thumbnail_empty_summary),
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
                SwitchPreference(
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                    title = stringResource(Res.string.common_enabled),
                )
            }
        }
    }
    variant(key = "burst", title = Res.string.demo_camera_thumbnail_variant_burst, inCard = false) {
        val photos = rememberDemoPhotos()
        val scope = rememberCoroutineScope()
        var shot by remember { mutableIntStateOf(0) }
        CameraBottomBar(
            onShutter = {
                // A burst delivers a photo every 120ms: images arriving early replace the growing one.
                scope.launch {
                    repeat(BURST_COUNT) {
                        shot++
                        delay(BURST_INTERVAL_MILLIS)
                    }
                }
            },
            shutterLabel = stringResource(Res.string.demo_camera_thumbnail_burst),
        ) {
            CameraThumbnail(image = photos[shot % photos.size], onClick = {})
        }
        Text(
            text = stringResource(Res.string.common_value_format, shot.toString()),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
    variant(key = "delete", title = Res.string.demo_camera_thumbnail_variant_delete, inCard = false) {
        val photos = rememberDemoPhotos()
        var shot by remember { mutableIntStateOf(photos.size - 1) }
        CameraBottomBar(
            onShutter = { shot = (shot - 1 + photos.size) % photos.size },
            shutterLabel = stringResource(Res.string.common_delete),
        ) {
            CameraThumbnail(
                image = photos[shot],
                onClick = {},
                transition = CameraThumbnailTransition.ScaleOut,
            )
        }
    }
}

/** The device orientations the playground can turn the thumbnail to. */
private val Orientations = listOf(0, 90, 180, 270)

private const val BURST_COUNT = 8

private const val BURST_INTERVAL_MILLIS = 120L

/** A few "photos": the sample picture and gradients standing in for later shots. */
@Composable
private fun rememberDemoPhotos(): List<Painter> {
    val photo = painterResource(Res.drawable.blur_test_bg)
    return remember(photo) {
        listOf(
            photo,
            BrushPainter(Brush.linearGradient(listOf(Color(0xFFFFB36B), Color(0xFF7B3FA0)))),
            BrushPainter(Brush.verticalGradient(listOf(Color(0xFF8FD3F4), Color(0xFF1E5D3A)))),
            BrushPainter(Brush.radialGradient(listOf(Color(0xFFFFF1C1), Color(0xFFD9534F)))),
            BrushPainter(Brush.linearGradient(listOf(Color(0xFF2B2E4A), Color(0xFF53C6D9)))),
        )
    }
}

/**
 * A stand-in for the camera's bottom bar: the thumbnail at the start and a shutter in the
 * middle, on the black viewfinder background.
 */
@Composable
private fun CameraBottomBar(
    onShutter: () -> Unit,
    modifier: Modifier = Modifier,
    shutterLabel: String? = null,
    thumbnail: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
            .padding(horizontal = 32.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        thumbnail()
        Box(
            modifier = Modifier
                .size(72.dp)
                .border(4.dp, Color.White, CircleShape)
                .padding(8.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = onShutter),
        )
        if (shutterLabel != null) {
            TextButton(text = shutterLabel, onClick = onShutter, modifier = Modifier.width(64.dp))
        } else {
            Spacer(Modifier.width(40.dp))
        }
    }
}
