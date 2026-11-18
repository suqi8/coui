// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CameraEffectButton
import io.github.suqi8.coui.kmp.basic.CameraMenuButton
import io.github.suqi8.coui.kmp.basic.CameraMenuButtonDefaults
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBlurProvider
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_menu_button_bokeh
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_menu_button_colorful_border
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_menu_button_edge_light
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_menu_button_effect_visible
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_menu_button_filter
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_menu_button_gradient
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_menu_button_gradient_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_menu_button_label
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_menu_button_material_blur
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_menu_button_material_blur_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_menu_button_ratio
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_menu_button_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_menu_button_selected
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_menu_button_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_menu_button_variant_effect
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_menu_button_variant_menu
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import utils.rememberPopupMaterialBlur

/** The demo page of `CameraMenuButton` and `CameraEffectButton`, listed in its category and found by the search. */
internal val CameraMenuButtonDemo = Demo(
    id = "camera_menu_button",
    apiName = "CameraMenuButton",
    title = Res.string.demo_camera_menu_button_title,
    category = DemoCategory.Buttons,
    page = DemoPage.Variants { cameraMenuButtonPage() },
    searchTerms = Res.string.demo_camera_menu_button_search_terms,
    isNew = true,
)

private fun LazyListScope.cameraMenuButtonPage() {
    playground {
        var selected by rememberSaveable { mutableStateOf(true) }
        var showLabel by rememberSaveable { mutableStateOf(true) }
        var gradient by rememberSaveable { mutableStateOf(true) }
        var colorfulBorder by rememberSaveable { mutableStateOf(false) }
        var edgeLight by rememberSaveable { mutableStateOf(true) }
        var enabled by rememberSaveable { mutableStateOf(true) }

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            CameraMenuStage(materialBlur = false) {
                val colors = if (gradient) {
                    CameraMenuButtonDefaults.cameraMenuButtonColors()
                } else {
                    CameraMenuButtonDefaults.cameraMenuButtonColors(
                        selectedGradientTopColor = Color.Unspecified,
                        selectedGradientBottomColor = Color.Unspecified,
                    )
                }
                CameraMenuButton(
                    onClick = { selected = !selected },
                    icon = DemoBokehIcon,
                    contentDescription = stringResource(Res.string.demo_camera_menu_button_bokeh),
                    selected = selected,
                    enabled = enabled,
                    label = if (showLabel) stringResource(Res.string.demo_camera_menu_button_bokeh) else null,
                    colorfulBorder = colorfulBorder,
                    edgeLight = edgeLight,
                    colors = colors,
                )
            }
        }
        PlaygroundControls {
            item {
                SwitchPreference(
                    checked = selected,
                    onCheckedChange = { selected = it },
                    title = stringResource(Res.string.demo_camera_menu_button_selected),
                )
            }
            item {
                SwitchPreference(
                    checked = showLabel,
                    onCheckedChange = { showLabel = it },
                    title = stringResource(Res.string.demo_camera_menu_button_label),
                )
            }
            item {
                SwitchPreference(
                    checked = gradient,
                    onCheckedChange = { gradient = it },
                    title = stringResource(Res.string.demo_camera_menu_button_gradient),
                    summary = stringResource(Res.string.demo_camera_menu_button_gradient_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = colorfulBorder,
                    onCheckedChange = { colorfulBorder = it },
                    title = stringResource(Res.string.demo_camera_menu_button_colorful_border),
                )
            }
            item {
                SwitchPreference(
                    checked = edgeLight,
                    onCheckedChange = { edgeLight = it },
                    title = stringResource(Res.string.demo_camera_menu_button_edge_light),
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
    variant(key = "menu", title = Res.string.demo_camera_menu_button_variant_menu, inCard = false) {
        var selectedIndex by remember { mutableIntStateOf(0) }
        val labels = listOf(
            stringResource(Res.string.demo_camera_menu_button_bokeh),
            stringResource(Res.string.demo_camera_menu_button_filter),
            stringResource(Res.string.demo_camera_menu_button_ratio),
        )
        val icons = listOf(DemoBokehIcon, DemoFilterIcon, DemoRatioIcon)
        CameraMenuStage(materialBlur = false) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                labels.forEachIndexed { index, label ->
                    CameraMenuButton(
                        onClick = { selectedIndex = index },
                        icon = icons[index],
                        contentDescription = label,
                        selected = index == selectedIndex,
                        label = label,
                        colorfulBorder = index == 2,
                    )
                }
            }
        }
    }
    variant(key = "effect", title = Res.string.demo_camera_menu_button_variant_effect, inCard = false) {
        var visible by remember { mutableStateOf(true) }
        var materialBlur by remember { mutableStateOf(true) }
        var bokehOn by remember { mutableStateOf(false) }
        var filterOn by remember { mutableStateOf(true) }
        CameraMenuStage(materialBlur = materialBlur) { blur ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                CameraEffectButton(
                    onClick = { bokehOn = !bokehOn },
                    icon = DemoBokehIcon,
                    contentDescription = stringResource(Res.string.demo_camera_menu_button_bokeh),
                    visible = visible,
                    selected = bokehOn,
                    materialBlur = blur,
                )
                CameraEffectButton(
                    onClick = { filterOn = !filterOn },
                    icon = DemoFilterIcon,
                    contentDescription = stringResource(Res.string.demo_camera_menu_button_filter),
                    visible = visible,
                    selected = filterOn,
                    materialBlur = blur,
                )
            }
        }
        SwitchPreference(
            checked = visible,
            onCheckedChange = { visible = it },
            title = stringResource(Res.string.demo_camera_menu_button_effect_visible),
        )
        SwitchPreference(
            checked = materialBlur,
            onCheckedChange = { materialBlur = it },
            title = stringResource(Res.string.demo_camera_menu_button_material_blur),
            summary = stringResource(Res.string.demo_camera_menu_button_material_blur_summary),
        )
    }
}

/**
 * A camera-like stage: a photo standing in for the viewfinder, recorded into a backdrop so the
 * buttons over it can take the camera material blur.
 */
@Composable
private fun CameraMenuStage(
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
            modifier = Modifier.padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content(if (materialBlur) provider else null)
        }
    }
}

/** The camera's bokeh entry icon (`blur_menu_control_button`, 24dp). */
private val DemoBokehIcon: ImageVector by lazy {
    ImageVector.Builder("DemoBokeh", 24.dp, 24.dp, 24f, 24f).apply {
        addPath(
            pathData = addPathNodes(
                "M16.878,5.082C16.399,4.89 15.717,4.75 15.008,4.793C14.293,4.837 13.484,5.073 12.849,5.708" +
                    "C11.658,6.899 11.493,8.282 11.309,9.832L11.309,9.832C11.291,9.982 11.274,10.133 11.254,10.285" +
                    "C11.222,10.542 11.192,10.787 11.163,11.019H7.855V12.519H10.973C10.724,14.483 10.616,15.245 10.525,15.751" +
                    "C10.239,17.323 9.42,18.01 8.663,18.01C8.201,18.01 7.712,17.893 7.318,17.761C7.126,17.697 6.966,17.633 6.856,17.586" +
                    "C6.8,17.562 6.758,17.543 6.731,17.531L6.702,17.517L6.698,17.514C6.698,17.514 6.698,17.515 6.362,18.185" +
                    "C6.027,18.856 6.027,18.856 6.027,18.856L6.028,18.857L6.028,18.857L6.03,18.858L6.035,18.86L6.049,18.867" +
                    "C6.061,18.873 6.077,18.88 6.097,18.89C6.137,18.909 6.194,18.934 6.265,18.965C6.406,19.025 6.605,19.105 6.844,19.184" +
                    "C7.313,19.341 7.974,19.51 8.663,19.51C10.493,19.51 11.659,17.897 12,16.02C12.102,15.458 12.219,14.622 12.485,12.519" +
                    "H16.136V11.019H12.674C12.696,10.843 12.719,10.661 12.743,10.472C12.758,10.35 12.773,10.232 12.786,10.118" +
                    "L12.787,10.118C12.993,8.439 13.099,7.579 13.909,6.769C14.212,6.466 14.63,6.319 15.099,6.29" +
                    "C15.574,6.261 16.034,6.36 16.321,6.475L16.878,5.082Z",
            ),
            pathFillType = PathFillType.EvenOdd,
            fill = SolidColor(Color.White),
        )
    }.build()
}

/** The camera's filter entry icon (`face_beauty_menu_effect_button`, three overlapping rings). */
private val DemoFilterIcon: ImageVector by lazy {
    ImageVector.Builder("DemoFilter", 24.dp, 24.dp, 24f, 24f).apply {
        addPath(
            pathData = addPathNodes(
                "M15.216,19.905C18.289,19.905 20.779,17.415 20.779,14.342C20.779,11.269 18.289,8.779 15.216,8.779" +
                    "C12.144,8.779 9.653,11.269 9.653,14.342C9.653,17.415 12.144,19.905 15.216,19.905Z",
            ),
            stroke = SolidColor(Color.White),
            strokeAlpha = 0.54f,
            strokeLineWidth = 1.15f,
        )
        addPath(
            pathData = addPathNodes(
                "M8.792,19.905C11.865,19.905 14.356,17.415 14.356,14.342C14.356,11.269 11.865,8.779 8.792,8.779" +
                    "C5.72,8.779 3.229,11.269 3.229,14.342C3.229,17.415 5.72,19.905 8.792,19.905Z",
            ),
            stroke = SolidColor(Color.White),
            strokeAlpha = 0.54f,
            strokeLineWidth = 1.15f,
        )
        addPath(
            pathData = addPathNodes(
                "M12.004,14.342C15.077,14.342 17.568,11.851 17.568,8.779C17.568,5.706 15.077,3.215 12.004,3.215" +
                    "C8.932,3.215 6.441,5.706 6.441,8.779C6.441,11.851 8.932,14.342 12.004,14.342Z",
            ),
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.15f,
        )
    }.build()
}

/** The camera's 4:3 ratio icon (`photo_ratio_standard`, 20dp viewport). */
private val DemoRatioIcon: ImageVector by lazy {
    ImageVector.Builder("DemoRatio", 20.dp, 20.dp, 20f, 20f).apply {
        addPath(
            pathData = addPathNodes("M4.167,2.5h11.667v15h-11.667z"),
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1f,
            strokeLineJoin = StrokeJoin.Round,
        )
    }.build()
}
