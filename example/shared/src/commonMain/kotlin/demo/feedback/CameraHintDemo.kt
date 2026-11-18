// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.feedback

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CameraHint
import io.github.suqi8.coui.kmp.basic.CameraHintBackground
import io.github.suqi8.coui.kmp.basic.CameraHintDefaults
import io.github.suqi8.coui.kmp.basic.CameraOutlinedText
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBlurProvider
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show
import io.github.suqi8.coui.kmp.shared.generated.resources.common_visible
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_hint_auto_hide
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_hint_auto_hide_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_hint_background
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_hint_gradient_sample
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_hint_material_blur
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_hint_material_blur_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_hint_message
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_hint_outlined_sample
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_hint_sample_burst
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_hint_sample_long
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_hint_sample_steady
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_hint_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_hint_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_hint_variant_backgrounds
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_hint_variant_outlined
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import utils.rememberPopupMaterialBlur

/** The demo page of `CameraHint`, listed in its category and found by the search. */
internal val CameraHintDemo = Demo(
    id = "camera_hint",
    apiName = "CameraHint",
    title = Res.string.demo_camera_hint_title,
    category = DemoCategory.Feedback,
    page = DemoPage.Variants { cameraHintPage() },
    searchTerms = Res.string.demo_camera_hint_search_terms,
    isNew = true,
)

private val SampleHints = listOf(
    Res.string.demo_camera_hint_sample_burst,
    Res.string.demo_camera_hint_sample_steady,
    Res.string.demo_camera_hint_sample_long,
)

private fun LazyListScope.cameraHintPage() {
    playground {
        var sample by rememberSaveable { mutableIntStateOf(0) }
        var background by rememberSaveable { mutableIntStateOf(CameraHintBackground.Dark.ordinal) }
        var materialBlur by rememberSaveable { mutableStateOf(false) }
        var autoHide by rememberSaveable { mutableStateOf(true) }
        var visible by rememberSaveable { mutableStateOf(true) }

        // The camera hides a hint 3 seconds after showing it.
        LaunchedEffect(visible, autoHide, sample) {
            if (visible && autoHide) {
                delay(CameraHintDefaults.AutoHideMillis)
                visible = false
            }
        }

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            CameraStage(materialBlur = materialBlur) { blur ->
                CameraHint(
                    text = stringResource(SampleHints[sample]),
                    visible = visible,
                    background = CameraHintBackground.entries[background],
                    materialBlur = blur,
                )
            }
            TextButton(
                text = stringResource(Res.string.common_show),
                onClick = { visible = true },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
            )
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.demo_camera_hint_message),
                    items = SampleHints.map { stringResource(it) },
                    selectedIndex = sample,
                    onSelectedIndexChange = {
                        sample = it
                        visible = true
                    },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.demo_camera_hint_background),
                    summary = "background",
                    items = CameraHintBackground.entries.map { it.name },
                    selectedIndex = background,
                    onSelectedIndexChange = { background = it },
                )
            }
            item {
                SwitchPreference(
                    checked = materialBlur,
                    onCheckedChange = { materialBlur = it },
                    title = stringResource(Res.string.demo_camera_hint_material_blur),
                    summary = stringResource(Res.string.demo_camera_hint_material_blur_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = autoHide,
                    onCheckedChange = { autoHide = it },
                    title = stringResource(Res.string.demo_camera_hint_auto_hide),
                    summary = stringResource(Res.string.demo_camera_hint_auto_hide_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = visible,
                    onCheckedChange = { visible = it },
                    title = stringResource(Res.string.common_visible),
                )
            }
        }
    }
    variant(key = "backgrounds", title = Res.string.demo_camera_hint_variant_backgrounds, inCard = false) {
        CameraStage(materialBlur = false) {
            val text = stringResource(Res.string.demo_camera_hint_sample_burst)
            CameraHintBackground.entries.forEach { background ->
                CameraHint(text = text, background = background)
            }
        }
    }
    variant(key = "outlined", title = Res.string.demo_camera_hint_variant_outlined, inCard = false) {
        CameraStage(materialBlur = false) {
            CameraOutlinedText(text = stringResource(Res.string.demo_camera_hint_outlined_sample))
            CameraOutlinedText(
                text = stringResource(Res.string.demo_camera_hint_gradient_sample),
                brush = Brush.horizontalGradient(CameraHintDefaults.GuideGradientColors),
                textSize = 20.dp,
                fontWeight = FontWeight.Bold,
                strokeWidth = 0.dp,
            )
        }
    }
}

/**
 * A camera-like stage: a photo standing in for the viewfinder, recorded into a backdrop so the
 * hints over it can take the camera material blur.
 */
@Composable
private fun CameraStage(
    materialBlur: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(MaterialBlurProvider?) -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    val provider = rememberPopupMaterialBlur(backdrop)
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .heightIn(min = 160.dp)
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
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content(if (materialBlur) provider else null)
        }
    }
}
