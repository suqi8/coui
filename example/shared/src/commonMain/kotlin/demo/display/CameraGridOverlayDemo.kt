// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.display

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.common_visible
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_grid_overlay_level
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_grid_overlay_level_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_grid_overlay_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_grid_overlay_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_grid_overlay_type
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_grid_overlay_type_diagonal
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_grid_overlay_type_grid
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_grid_overlay_type_spiral
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_grid_overlay_type_symmetry
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_grid_overlay_variant_types
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_level_indicator_roll
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/** The demo page of `CameraGridOverlay`, listed in its category and found by the search. */
internal val CameraGridOverlayDemo = Demo(
    id = "camera_grid_overlay",
    apiName = "CameraGridOverlay",
    title = Res.string.demo_camera_grid_overlay_title,
    category = DemoCategory.Display,
    page = DemoPage.Variants { cameraGridOverlayPage() },
    searchTerms = Res.string.demo_camera_grid_overlay_search_terms,
    isNew = true,
)

/** The guide types in menu order, with their names. */
private val GridTypes: List<Pair<CameraGridType, StringResource>> = listOf(
    CameraGridType.Grid to Res.string.demo_camera_grid_overlay_type_grid,
    CameraGridType.Diagonal to Res.string.demo_camera_grid_overlay_type_diagonal,
    CameraGridType.GoldenSpiral to Res.string.demo_camera_grid_overlay_type_spiral,
    CameraGridType.Symmetry to Res.string.demo_camera_grid_overlay_type_symmetry,
)

private fun LazyListScope.cameraGridOverlayPage() {
    playground {
        var typeIndex by rememberSaveable { mutableIntStateOf(0) }
        var visible by rememberSaveable { mutableStateOf(true) }
        var withLevel by rememberSaveable { mutableStateOf(true) }
        var roll by rememberSaveable { mutableFloatStateOf(3f) }
        val levelState = rememberCameraLevelState()
        val type = GridTypes[typeIndex].first

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            GridStage(modifier = Modifier.padding(horizontal = 16.dp)) {
                CameraGridOverlay(
                    type = type,
                    visible = visible,
                    levelState = if (withLevel) levelState else null,
                    modifier = Modifier.matchParentSize(),
                )
                if (withLevel) {
                    CameraLevelIndicator(
                        rollDegrees = roll,
                        pitchDegrees = 0f,
                        state = levelState,
                        modifier = Modifier.matchParentSize(),
                    )
                }
            }
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = GridTypes.map { stringResource(it.second) },
                    selectedIndex = typeIndex,
                    title = stringResource(Res.string.demo_camera_grid_overlay_type),
                    onSelectedIndexChange = { typeIndex = it },
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
                    checked = withLevel,
                    onCheckedChange = { withLevel = it },
                    title = stringResource(Res.string.demo_camera_grid_overlay_level),
                    summary = stringResource(Res.string.demo_camera_grid_overlay_level_summary),
                )
            }
            item {
                SliderPreference(
                    value = roll,
                    onValueChange = { roll = it },
                    title = stringResource(Res.string.demo_camera_level_indicator_roll),
                    valueText = "${roll.roundToInt()}°",
                    valueRange = -15f..15f,
                    enabled = withLevel,
                )
            }
        }
    }
    variant(key = "types", title = Res.string.demo_camera_grid_overlay_variant_types, inCard = false) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GridTypes.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { (type, name) ->
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            GridStage {
                                CameraGridOverlay(type = type, modifier = Modifier.matchParentSize())
                            }
                            Text(
                                text = stringResource(name),
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

/** A portrait 3:4 viewfinder: a photo standing in for the camera preview. */
@Composable
private fun GridStage(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
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
