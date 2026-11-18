// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.pickers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import io.github.suqi8.coui.kmp.basic.ExposureBar
import io.github.suqi8.coui.kmp.basic.ExposureBarDefaults
import io.github.suqi8.coui.kmp.basic.ScaleBar
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBlurProvider
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_value_format
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_exposure_bar_material_blur
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_exposure_bar_material_blur_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_exposure_bar_reset_button
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_exposure_bar_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_exposure_bar_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_exposure_bar_variant_ruler
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_exposure_bar_variant_wide
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_exposure_bar_wide_range
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_exposure_bar_wide_range_summary
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import utils.rememberPopupMaterialBlur

/** The demo page of `ExposureBar`, listed in its category and found by the search. */
internal val ExposureBarDemo = Demo(
    id = "exposure_bar",
    apiName = "ExposureBar",
    title = Res.string.demo_exposure_bar_title,
    category = DemoCategory.Pickers,
    page = DemoPage.Variants { exposureBarPage() },
    searchTerms = Res.string.demo_exposure_bar_search_terms,
    isNew = true,
)

private fun LazyListScope.exposureBarPage() {
    playground {
        var wide by rememberSaveable { mutableStateOf(false) }
        val values = if (wide) ExposureBarDefaults.WideValues else ExposureBarDefaults.Values
        var index by rememberSaveable(wide) { mutableIntStateOf(values.size / 2) }
        var materialBlur by rememberSaveable { mutableStateOf(true) }
        var showReset by rememberSaveable { mutableStateOf(true) }
        var enabled by rememberSaveable { mutableStateOf(true) }

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            CameraStage(materialBlur = materialBlur) { blur ->
                ExposureBar(
                    selectedIndex = index,
                    onSelectedIndexChange = { index = it },
                    enabled = enabled,
                    values = values,
                    showResetButton = showReset,
                    materialBlur = blur,
                )
            }
            Text(
                text = stringResource(Res.string.common_value_format, values[index] + " EV"),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        PlaygroundControls {
            item {
                SwitchPreference(
                    checked = wide,
                    onCheckedChange = { wide = it },
                    title = stringResource(Res.string.demo_exposure_bar_wide_range),
                    summary = stringResource(Res.string.demo_exposure_bar_wide_range_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = materialBlur,
                    onCheckedChange = { materialBlur = it },
                    title = stringResource(Res.string.demo_exposure_bar_material_blur),
                    summary = stringResource(Res.string.demo_exposure_bar_material_blur_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = showReset,
                    onCheckedChange = { showReset = it },
                    title = stringResource(Res.string.demo_exposure_bar_reset_button),
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
    variant(key = "wide", title = Res.string.demo_exposure_bar_variant_wide, inCard = false) {
        var index by remember { mutableIntStateOf(ExposureBarDefaults.WideValues.size / 2) }
        CameraStage(materialBlur = true) { blur ->
            ExposureBar(
                selectedIndex = index,
                onSelectedIndexChange = { index = it },
                values = ExposureBarDefaults.WideValues,
                materialBlur = blur,
            )
        }
    }
    variant(key = "ruler", title = Res.string.demo_exposure_bar_variant_ruler, inCard = false) {
        val isos = remember { listOf("50", "64", "80", "100", "125", "160", "200", "250", "320", "400", "500", "640", "800", "1000", "1250", "1600", "2000", "2500", "3200") }
        var index by remember { mutableIntStateOf(3) }
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                // The camera rulers sit on the dark viewfinder.
                .background(Color.Black),
        ) {
            ScaleBar(
                labels = isos,
                selectedIndex = index,
                onSelectedIndexChange = { index = it },
                valueSuffix = " ISO",
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
    }
}

/**
 * A camera-like stage: a photo standing in for the viewfinder, recorded into a backdrop so the
 * panel over it can take the camera material blur.
 */
@Composable
private fun CameraStage(
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
            modifier = Modifier.padding(vertical = 40.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content(if (materialBlur) provider else null)
        }
    }
}
