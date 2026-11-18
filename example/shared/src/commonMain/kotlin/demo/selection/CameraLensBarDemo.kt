// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.selection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
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
import io.github.suqi8.coui.kmp.basic.CameraLensBar
import io.github.suqi8.coui.kmp.basic.CameraLensBarDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_value_format
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_lens_bar_enlarged
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_lens_bar_enlarged_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_lens_bar_light_background
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_lens_bar_light_background_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_lens_bar_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_lens_bar_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_lens_bar_ultra_tele
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_lens_bar_ultra_tele_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_lens_bar_variant_four
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** The demo page of `CameraLensBar`, listed in its category and found by the search. */
internal val CameraLensBarDemo = Demo(
    id = "camera_lens_bar",
    apiName = "CameraLensBar",
    title = Res.string.demo_camera_lens_bar_title,
    category = DemoCategory.Selection,
    page = DemoPage.Variants { cameraLensBarPage() },
    searchTerms = Res.string.demo_camera_lens_bar_search_terms,
    isNew = true,
)

/** A phone with an ultra wide, a main and a 3x tele lens. */
private val ThreeLenses = listOf(0.6f, 1f, 3f).map(CameraLensBarDefaults::formatZoomRatio)

/** The same phone with a 6x ultra tele lens as well. */
private val FourLenses = listOf(0.6f, 1f, 3f, 6f).map(CameraLensBarDefaults::formatZoomRatio)

private fun LazyListScope.cameraLensBarPage() {
    playground {
        var ultraTele by rememberSaveable { mutableStateOf(false) }
        val labels = if (ultraTele) FourLenses else ThreeLenses
        var index by rememberSaveable(ultraTele) { mutableIntStateOf(1) }
        var light by rememberSaveable { mutableStateOf(false) }
        var enlarged by rememberSaveable { mutableStateOf(false) }
        var enabled by rememberSaveable { mutableStateOf(true) }

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            LensStage(light = light) {
                CameraLensBar(
                    labels = labels,
                    selectedIndex = index,
                    onSelectedIndexChange = { index = it },
                    enabled = enabled,
                    radius = if (enlarged) CameraLensBarDefaults.EnlargedRadius else CameraLensBarDefaults.Radius,
                    selectedRadius = if (enlarged) CameraLensBarDefaults.EnlargedSelectedRadius else CameraLensBarDefaults.SelectedRadius,
                    colors = if (light) CameraLensBarDefaults.lightBackgroundColors() else CameraLensBarDefaults.cameraLensBarColors(),
                )
            }
            Text(
                text = stringResource(Res.string.common_value_format, labels[index] + "×"),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        PlaygroundControls {
            item {
                SwitchPreference(
                    checked = ultraTele,
                    onCheckedChange = { ultraTele = it },
                    title = stringResource(Res.string.demo_camera_lens_bar_ultra_tele),
                    summary = stringResource(Res.string.demo_camera_lens_bar_ultra_tele_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = light,
                    onCheckedChange = { light = it },
                    title = stringResource(Res.string.demo_camera_lens_bar_light_background),
                    summary = stringResource(Res.string.demo_camera_lens_bar_light_background_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = enlarged,
                    onCheckedChange = { enlarged = it },
                    title = stringResource(Res.string.demo_camera_lens_bar_enlarged),
                    summary = stringResource(Res.string.demo_camera_lens_bar_enlarged_summary),
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
    variant(key = "four", title = Res.string.demo_camera_lens_bar_variant_four, inCard = false) {
        var index by remember { mutableIntStateOf(1) }
        LensStage(light = false) {
            CameraLensBar(
                labels = FourLenses,
                selectedIndex = index,
                onSelectedIndexChange = { index = it },
            )
        }
    }
    variant(key = "light", title = Res.string.demo_camera_lens_bar_light_background, inCard = false) {
        var index by remember { mutableIntStateOf(1) }
        LensStage(light = true) {
            CameraLensBar(
                labels = ThreeLenses,
                selectedIndex = index,
                onSelectedIndexChange = { index = it },
                colors = CameraLensBarDefaults.lightBackgroundColors(),
            )
        }
    }
}

/**
 * A camera-like stage: a photo standing in for the viewfinder, darkened unless [light] so the bar
 * sits on the kind of scene its palette is made for.
 */
@Composable
private fun LensStage(
    light: Boolean,
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
                .background(if (light) Color.White.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.25f)),
        )
        Box(modifier = Modifier.padding(vertical = 48.dp)) {
            content()
        }
    }
}
