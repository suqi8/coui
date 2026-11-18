// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import io.github.suqi8.coui.kmp.basic.CameraModeBar
import io.github.suqi8.coui.kmp.basic.CameraModeTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBlurProvider
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.common_value_format
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_mode_bar_material_blur_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_mode_bar_mode_high_res
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_mode_bar_mode_more
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_mode_bar_mode_movie
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_mode_bar_mode_night
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_mode_bar_mode_pano
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_mode_bar_mode_photo
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_mode_bar_mode_portrait
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_mode_bar_mode_pro
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_mode_bar_mode_slow_motion
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_mode_bar_mode_time_lapse
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_mode_bar_mode_video
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_mode_bar_more_modes
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_mode_bar_more_modes_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_mode_bar_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_mode_bar_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_mode_bar_title_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_mode_bar_variant_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import utils.rememberPopupMaterialBlur

/** The demo page of `CameraModeBar` and `CameraModeTitle`, listed in its category and found by the search. */
internal val CameraModeBarDemo = Demo(
    id = "camera_mode_bar",
    apiName = "CameraModeBar",
    title = Res.string.demo_camera_mode_bar_title,
    category = DemoCategory.Navigation,
    page = DemoPage.Variants { cameraModeBarPage() },
    searchTerms = Res.string.demo_camera_mode_bar_search_terms,
    isNew = true,
)

/** The usual modes of the camera strip, ending with "More". */
@Composable
private fun baseModes(): List<String> = listOf(
    stringResource(Res.string.demo_camera_mode_bar_mode_night),
    stringResource(Res.string.demo_camera_mode_bar_mode_portrait),
    stringResource(Res.string.demo_camera_mode_bar_mode_photo),
    stringResource(Res.string.demo_camera_mode_bar_mode_video),
    stringResource(Res.string.demo_camera_mode_bar_mode_pro),
    stringResource(Res.string.demo_camera_mode_bar_mode_more),
)

/** A strip wider than the screen, with the modes usually kept under "More". */
@Composable
private fun extendedModes(): List<String> = listOf(
    stringResource(Res.string.demo_camera_mode_bar_mode_night),
    stringResource(Res.string.demo_camera_mode_bar_mode_portrait),
    stringResource(Res.string.demo_camera_mode_bar_mode_photo),
    stringResource(Res.string.demo_camera_mode_bar_mode_video),
    stringResource(Res.string.demo_camera_mode_bar_mode_pro),
    stringResource(Res.string.demo_camera_mode_bar_mode_pano),
    stringResource(Res.string.demo_camera_mode_bar_mode_slow_motion),
    stringResource(Res.string.demo_camera_mode_bar_mode_time_lapse),
    stringResource(Res.string.demo_camera_mode_bar_mode_high_res),
    stringResource(Res.string.demo_camera_mode_bar_mode_movie),
)

/** The index of "Photo" in both mode lists. */
private const val PHOTO_INDEX = 2

private fun LazyListScope.cameraModeBarPage() {
    playground {
        var moreModes by rememberSaveable { mutableStateOf(false) }
        var materialBlur by rememberSaveable { mutableStateOf(true) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        var index by rememberSaveable(moreModes) { mutableIntStateOf(PHOTO_INDEX) }
        val modes = if (moreModes) extendedModes() else baseModes()

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            ViewfinderStage(materialBlur = materialBlur) { blur ->
                CameraModeBar(
                    modes = modes,
                    selectedIndex = index,
                    onSelectedIndexChange = { index = it },
                    enabled = enabled,
                    materialBlur = blur,
                )
            }
            Text(
                text = stringResource(Res.string.common_value_format, modes[index.coerceIn(0, modes.size - 1)]),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        PlaygroundControls {
            item {
                SwitchPreference(
                    checked = moreModes,
                    onCheckedChange = { moreModes = it },
                    title = stringResource(Res.string.demo_camera_mode_bar_more_modes),
                    summary = stringResource(Res.string.demo_camera_mode_bar_more_modes_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = materialBlur,
                    onCheckedChange = { materialBlur = it },
                    title = stringResource(Res.string.common_material),
                    summary = stringResource(Res.string.demo_camera_mode_bar_material_blur_summary),
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
    variant(key = "title", title = Res.string.demo_camera_mode_bar_variant_title, inCard = false) {
        val modes = baseModes()
        val moreIndex = modes.size - 1
        var index by remember { mutableIntStateOf(PHOTO_INDEX) }
        var titleOpen by remember { mutableStateOf(false) }
        ViewfinderStage(materialBlur = true) { blur ->
            if (titleOpen) {
                CameraModeTitle(
                    onClick = {
                        titleOpen = false
                        index = PHOTO_INDEX
                    },
                    title = stringResource(Res.string.demo_camera_mode_bar_mode_slow_motion),
                    materialBlur = blur,
                )
            } else {
                CameraModeBar(
                    modes = modes,
                    selectedIndex = index,
                    onSelectedIndexChange = {
                        index = it
                        // "More" opens a mode of its own, shown as a title instead of the strip.
                        if (it == moreIndex) titleOpen = true
                    },
                    materialBlur = blur,
                )
            }
        }
        Text(
            text = stringResource(Res.string.demo_camera_mode_bar_title_hint),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

/**
 * A camera-like stage: a photo standing in for the viewfinder, recorded into a backdrop so the
 * capsule over it can take the camera material blur. The bar sits near its bottom edge.
 */
@Composable
private fun ViewfinderStage(
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
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp)),
    ) {
        Image(
            painter = painterResource(Res.drawable.blur_test_bg),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .layerBackdrop(backdrop),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            content(if (materialBlur) provider else null)
        }
    }
}
