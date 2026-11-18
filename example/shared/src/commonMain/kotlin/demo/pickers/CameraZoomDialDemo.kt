// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.pickers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CameraZoomDial
import io.github.suqi8.coui.kmp.basic.CameraZoomDialDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_value_format
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_zoom_dial_expanded
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_zoom_dial_expanded_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_zoom_dial_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_zoom_dial_short_range
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_zoom_dial_short_range_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_zoom_dial_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_zoom_dial_variant_pinned
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** The demo page of `CameraZoomDial`, listed in its category and found by the search. */
internal val CameraZoomDialDemo = Demo(
    id = "camera_zoom_dial",
    apiName = "CameraZoomDial",
    title = Res.string.demo_camera_zoom_dial_title,
    category = DemoCategory.Pickers,
    page = DemoPage.Variants { cameraZoomDialPage() },
    searchTerms = Res.string.demo_camera_zoom_dial_search_terms,
    isNew = true,
)

/** A camera that ends at 5x: the dial spans 60° instead of 90°. */
private val ShortRangeStops = listOf(0.6f, 1f, 2f, 5f)

private fun LazyListScope.cameraZoomDialPage() {
    playground {
        var shortRange by rememberSaveable { mutableStateOf(false) }
        val stops = if (shortRange) ShortRangeStops else CameraZoomDialDefaults.Stops
        var zoom by rememberSaveable(shortRange) { mutableFloatStateOf(1f) }
        var expanded by rememberSaveable { mutableStateOf(false) }
        var enabled by rememberSaveable { mutableStateOf(true) }

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            ViewfinderStage {
                CameraZoomDial(
                    value = zoom,
                    onValueChange = { zoom = it },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    enabled = enabled,
                    stops = stops,
                ) {
                    LensButtons(
                        value = zoom,
                        lenses = stops.filter { it <= 3f },
                        onSelect = { zoom = it },
                    )
                }
            }
            Text(
                text = stringResource(Res.string.common_value_format, formatRatio(zoom) + "×"),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        PlaygroundControls {
            item {
                SwitchPreference(
                    checked = expanded,
                    onCheckedChange = { expanded = it },
                    title = stringResource(Res.string.demo_camera_zoom_dial_expanded),
                    summary = stringResource(Res.string.demo_camera_zoom_dial_expanded_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = shortRange,
                    onCheckedChange = { shortRange = it },
                    title = stringResource(Res.string.demo_camera_zoom_dial_short_range),
                    summary = stringResource(Res.string.demo_camera_zoom_dial_short_range_summary),
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
    variant(key = "pinned", title = Res.string.demo_camera_zoom_dial_variant_pinned, inCard = false) {
        var zoom by remember { mutableFloatStateOf(2f) }
        ViewfinderStage {
            // Ignoring the collapse requests keeps the dial unfolded.
            CameraZoomDial(
                value = zoom,
                onValueChange = { zoom = it },
                expanded = true,
                onExpandedChange = {},
            )
        }
    }
}

/** A photo standing in for the viewfinder, with the dial at its bottom edge like in the camera. */
@Composable
private fun ViewfinderStage(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Image(
            painter = painterResource(Res.drawable.blur_test_bg),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
        )
        Box(modifier = Modifier.widthIn(max = 420.dp).fillMaxWidth()) {
            content()
        }
    }
}

/**
 * A stand-in for the camera's lens buttons: a translucent capsule of round buttons, the active lens
 * showing the current ratio in the camera accent.
 */
@Composable
private fun LensButtons(
    value: Float,
    lenses: List<Float>,
    onSelect: (Float) -> Unit,
) {
    val active = lenses.lastOrNull { value >= it - 0.001f } ?: lenses.first()
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color(0x26000000))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        lenses.forEach { lens ->
            val selected = lens == active
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (selected) Color(0xA6000000) else Color(0x26000000))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onSelect(lens) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (selected) formatRatio(value) + "×" else formatRatio(lens),
                    color = if (selected) Color(0xFFFB822A) else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

/** The ratio rounded down to 0.1, without a trailing ".0". */
private fun formatRatio(value: Float): String {
    val tenths = ((value + 1e-5f) * 10f).toInt()
    return if (tenths % 10 == 0) (tenths / 10).toString() else "${tenths / 10}.${tenths % 10}"
}
