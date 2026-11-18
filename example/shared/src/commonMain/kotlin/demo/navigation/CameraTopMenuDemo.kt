// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import io.github.suqi8.coui.kmp.basic.CameraTopMenu
import io.github.suqi8.coui.kmp.basic.CameraTopMenuItem
import io.github.suqi8.coui.kmp.basic.CameraTopMenuOptionRow
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBlurProvider
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_top_menu_flash
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_top_menu_flash_auto
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_top_menu_flash_off
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_top_menu_flash_on
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_top_menu_flash_torch
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_top_menu_fps
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_top_menu_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_top_menu_material_blur
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_top_menu_material_blur_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_top_menu_ratio
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_top_menu_ratio_full
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_top_menu_ratio_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_top_menu_ratio_title_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_top_menu_resolution
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_top_menu_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_top_menu_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_top_menu_variant_group
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_camera_top_menu_video
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import utils.rememberPopupMaterialBlur

/** The demo page of `CameraTopMenu`, listed in its category and found by the search. */
internal val CameraTopMenuDemo = Demo(
    id = "camera_top_menu",
    apiName = "CameraTopMenu",
    title = Res.string.demo_camera_top_menu_title,
    category = DemoCategory.Navigation,
    page = DemoPage.Variants { cameraTopMenuPage() },
    searchTerms = Res.string.demo_camera_top_menu_search_terms,
    isNew = true,
)

private const val KEY_FLASH = "flash"
private const val KEY_RATIO = "ratio"
private const val KEY_VIDEO = "video"
private const val ROW_SIZE = "size"
private const val ROW_FPS = "fps"

private val DemoRatioLabels = listOf("1:1", "4:3", "16:9")
private val DemoResolutionLabels = listOf("720P", "1080P", "4K")
private val DemoFpsLabels = listOf("30", "60")

private fun LazyListScope.cameraTopMenuPage() {
    playground {
        var materialBlur by rememberSaveable { mutableStateOf(true) }
        var ratioTitle by rememberSaveable { mutableStateOf(false) }
        var expanded by rememberSaveable { mutableStateOf<String?>(null) }
        var flash by rememberSaveable { mutableIntStateOf(1) }
        var ratio by rememberSaveable { mutableIntStateOf(1) }
        var size by rememberSaveable { mutableIntStateOf(1) }
        var fps by rememberSaveable { mutableIntStateOf(0) }

        val flashItem = flashItem(flash)
        val ratioItem = ratioItem(ratio, ratioTitle)
        val videoItem = videoItem(size, fps)
        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            CameraTopMenuStage(materialBlur = materialBlur, onDismiss = { expanded = null }) { blur ->
                CameraTopMenu(
                    onItemClick = { key -> expanded = if (expanded == key) null else key },
                    onOptionSelected = { itemKey, rowKey, index ->
                        when (itemKey) {
                            KEY_FLASH -> flash = index
                            KEY_RATIO -> ratio = index
                            KEY_VIDEO -> if (rowKey == ROW_SIZE) size = index else fps = index
                        }
                        // A single-row strip closes once an option is picked; the grouped panel stays.
                        if (itemKey != KEY_VIDEO) expanded = null
                    },
                    startItems = listOf(flashItem),
                    middleItems = listOf(ratioItem),
                    endItems = listOf(videoItem),
                    expandedItemKey = expanded,
                    materialBlur = blur,
                )
            }
            Text(
                text = stringResource(Res.string.demo_camera_top_menu_hint),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        PlaygroundControls {
            item {
                SwitchPreference(
                    checked = materialBlur,
                    onCheckedChange = { materialBlur = it },
                    title = stringResource(Res.string.demo_camera_top_menu_material_blur),
                    summary = stringResource(Res.string.demo_camera_top_menu_material_blur_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = ratioTitle,
                    onCheckedChange = { ratioTitle = it },
                    title = stringResource(Res.string.demo_camera_top_menu_ratio_title),
                    summary = stringResource(Res.string.demo_camera_top_menu_ratio_title_summary),
                )
            }
        }
    }
    variant(key = "group", title = Res.string.demo_camera_top_menu_variant_group, inCard = false) {
        var expanded by remember { mutableStateOf<String?>(KEY_VIDEO) }
        var size by remember { mutableIntStateOf(2) }
        var fps by remember { mutableIntStateOf(1) }
        CameraTopMenuStage(materialBlur = true, onDismiss = { expanded = null }) { blur ->
            CameraTopMenu(
                onItemClick = { key -> expanded = if (expanded == key) null else key },
                onOptionSelected = { _, rowKey, index -> if (rowKey == ROW_SIZE) size = index else fps = index },
                endItems = listOf(videoItem(size, fps)),
                expandedItemKey = expanded,
                materialBlur = blur,
            )
        }
    }
}

@Composable
private fun flashItem(selected: Int): CameraTopMenuItem {
    val labels = listOf(
        stringResource(Res.string.demo_camera_top_menu_flash_off),
        stringResource(Res.string.demo_camera_top_menu_flash_auto),
        stringResource(Res.string.demo_camera_top_menu_flash_on),
        stringResource(Res.string.demo_camera_top_menu_flash_torch),
    )
    val title = stringResource(Res.string.demo_camera_top_menu_flash)
    return remember(selected, labels, title) {
        CameraTopMenuItem(
            key = KEY_FLASH,
            icon = when (selected) {
                0 -> DemoFlashOffIcon
                1 -> DemoFlashAutoIcon
                else -> DemoFlashOnIcon
            },
            contentDescription = title,
            options = listOf(CameraTopMenuOptionRow(key = KEY_FLASH, options = labels, selectedIndex = selected)),
        )
    }
}

@Composable
private fun ratioItem(selected: Int, withTitle: Boolean): CameraTopMenuItem {
    val title = stringResource(Res.string.demo_camera_top_menu_ratio)
    val full = stringResource(Res.string.demo_camera_top_menu_ratio_full)
    return remember(selected, withTitle, title, full) {
        CameraTopMenuItem(
            key = KEY_RATIO,
            icon = when (selected) {
                0 -> DemoRatioSquareIcon
                1 -> DemoRatioStandardIcon
                else -> DemoRatioWideIcon
            },
            contentDescription = title,
            options = listOf(
                CameraTopMenuOptionRow(
                    key = KEY_RATIO,
                    options = DemoRatioLabels + full,
                    selectedIndex = selected,
                    title = if (withTitle) title else null,
                ),
            ),
        )
    }
}

@Composable
private fun videoItem(size: Int, fps: Int): CameraTopMenuItem {
    val title = stringResource(Res.string.demo_camera_top_menu_video)
    val sizeTitle = stringResource(Res.string.demo_camera_top_menu_resolution)
    val fpsTitle = stringResource(Res.string.demo_camera_top_menu_fps)
    return remember(size, fps, title, sizeTitle, fpsTitle) {
        CameraTopMenuItem(
            key = KEY_VIDEO,
            icon = DemoRatioWideIcon,
            contentDescription = title,
            options = listOf(
                CameraTopMenuOptionRow(key = ROW_SIZE, options = DemoResolutionLabels, selectedIndex = size, title = sizeTitle),
                CameraTopMenuOptionRow(key = ROW_FPS, options = DemoFpsLabels, selectedIndex = fps, title = fpsTitle),
            ),
        )
    }
}

/**
 * A camera-like stage: a photo standing in for the viewfinder, recorded into a backdrop so the bar
 * over it can take the camera material blur. Tapping the photo closes the open panel.
 */
@Composable
private fun CameraTopMenuStage(
    materialBlur: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (MaterialBlurProvider?) -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    val provider = rememberPopupMaterialBlur(backdrop)
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            ),
        contentAlignment = Alignment.TopCenter,
    ) {
        Image(
            painter = painterResource(Res.drawable.blur_test_bg),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .layerBackdrop(backdrop),
            contentScale = ContentScale.Crop,
        )
        Box(modifier = Modifier.padding(top = 16.dp)) {
            content(if (materialBlur) provider else null)
        }
    }
}

private fun demoIcon(name: String, block: ImageVector.Builder.() -> Unit): ImageVector = ImageVector.Builder(name, 20.dp, 20.dp, 20f, 20f).apply(block).build()

/** `camera_menu_flash_off`. */
private val DemoFlashOffIcon: ImageVector by lazy {
    demoIcon("DemoFlashOff") {
        addPath(
            pathData = addPathNodes(
                "M2.666,4.786C1.618,6.257 1,8.056 1,10C1,14.971 5.029,19 10,19C11.944,19 13.742,18.381 15.213,17.333" +
                    "L15.931,18.051C14.271,19.275 12.22,20 10,20C4.477,20 0,15.523 0,10C0,7.779 0.724,5.728 1.948,4.068" +
                    "L2.666,4.786ZM17.415,16.707C17.191,16.954 16.956,17.191 16.709,17.415L16,16.706" +
                    "C16.249,16.483 16.485,16.248 16.707,15.999L17.415,16.707ZM10,0C15.523,0 20,4.477 20,10" +
                    "C20,12.22 19.275,14.269 18.051,15.929L17.334,15.212C18.381,13.741 19,11.943 19,10" +
                    "C19,5.029 14.971,1 10,1C8.057,1 6.258,1.618 4.787,2.665L4.07,1.948C5.73,0.724 7.78,0 10,0Z" +
                    "M4,3.292C3.751,3.514 3.515,3.751 3.293,3.999L2.584,3.29C2.808,3.043 3.045,2.808 3.292,2.584L4,3.292Z",
            ),
            fill = SolidColor(Color.White),
            fillAlpha = 0.55f,
        )
        addPath(
            pathData = addPathNodes("M2.575,1.868L1.868,2.575L17.424,18.132L18.132,17.425L2.575,1.868Z"),
            fill = SolidColor(Color.White),
        )
        addPath(
            pathData = addPathNodes(
                "M10.955,13.075L9.002,15.448C8.799,15.694 8.402,15.504 8.467,15.191L9.252,11.372L10.955,13.075Z" +
                    "M8.894,11.015H6.318C6.06,11.015 5.918,10.714 6.082,10.515L7.126,9.246L8.894,11.015Z" +
                    "M13.684,8.976C13.943,8.977 14.084,9.277 13.92,9.476L12.871,10.749L11.098,8.976H13.684Z" +
                    "M11,4.542C11.203,4.296 11.599,4.486 11.535,4.799L10.747,8.625L9.041,6.919L11,4.542Z",
            ),
            fill = SolidColor(Color.White),
        )
    }
}

/** `camera_menu_flash_auto`. */
private val DemoFlashAutoIcon: ImageVector by lazy {
    demoIcon("DemoFlashAuto") {
        addPath(
            pathData = addPathNodes(
                "M10,0C15.523,0 20,4.477 20,10C20,11.078 19.827,12.115 19.512,13.088C19.229,12.906 18.926,12.753 18.606,12.636" +
                    "C18.861,11.802 19,10.917 19,10C19,5.029 14.971,1 10,1C5.029,1 1,5.029 1,10C1,14.971 5.029,19 10,19" +
                    "C10.917,19 11.801,18.862 12.635,18.607C12.752,18.927 12.906,19.229 13.088,19.512" +
                    "C12.115,19.827 11.078,20 10,20C4.477,20 0,15.523 0,10C0,4.477 4.477,0 10,0Z",
            ),
            fill = SolidColor(Color.White),
            fillAlpha = 0.55f,
        )
        addPath(
            pathData = addPathNodes(
                "M10.999,4.547C11.202,4.301 11.598,4.491 11.534,4.803L10.675,8.98H13.684C13.942,8.981 14.083,9.281 13.919,9.48" +
                    "L9.001,15.452C8.798,15.698 8.401,15.509 8.466,15.196L9.325,11.019H6.317C6.059,11.019 5.917,10.718 6.081,10.519" +
                    "L10.999,4.547Z",
            ),
            fill = SolidColor(Color.White),
        )
        addPath(
            pathData = addPathNodes(
                "M17.865,14.394L19.544,19.161L19.609,19.344H18.307L18.277,19.248L17.987,18.331H16.47L16.187,19.247L16.156,19.344" +
                    "H14.881L16.605,14.394L16.637,14.302H17.832L17.865,14.394Z" +
                    "M17.153,16.121L16.781,17.316H17.669L17.297,16.121C17.275,16.051 17.252,15.966 17.225,15.867" +
                    "C17.198,15.966 17.174,16.051 17.153,16.121Z",
            ),
            fill = SolidColor(Color.White),
            pathFillType = PathFillType.EvenOdd,
        )
    }
}

/** `camera_menu_flash_on`: a white disc with a black bolt. */
private val DemoFlashOnIcon: ImageVector by lazy {
    demoIcon("DemoFlashOn") {
        addPath(
            pathData = addPathNodes("M10,10m-10,0a10,10 0,1 1,20 0a10,10 0,1 1,-20 0"),
            fill = SolidColor(Color.White),
        )
        addPath(
            pathData = addPathNodes(
                "M11,4.542C11.203,4.296 11.599,4.486 11.535,4.799L10.676,8.976H13.684C13.943,8.977 14.084,9.277 13.92,9.476" +
                    "L9.002,15.448C8.799,15.694 8.402,15.504 8.467,15.191L9.326,11.015H6.318C6.06,11.015 5.918,10.714 6.082,10.515" +
                    "L11,4.542Z",
            ),
            fill = SolidColor(Color.Black),
        )
    }
}

private fun ImageVector.Builder.ratioFrame(path: String) {
    addPath(
        pathData = addPathNodes(path),
        stroke = SolidColor(Color.White),
        strokeLineWidth = 1f,
        strokeLineJoin = StrokeJoin.Round,
    )
}

/** `photo_ratio_square`. */
private val DemoRatioSquareIcon: ImageVector by lazy { demoIcon("DemoRatioSquare") { ratioFrame("M3.333,3.333h13.333v13.333h-13.333z") } }

/** `photo_ratio_standard`. */
private val DemoRatioStandardIcon: ImageVector by lazy { demoIcon("DemoRatioStandard") { ratioFrame("M4.167,2.5h11.667v15h-11.667z") } }

/** `photo_ratio_16_9`. */
private val DemoRatioWideIcon: ImageVector by lazy { demoIcon("DemoRatioWide") { ratioFrame("M5.417,1.958h9.167v16.083h-9.167z") } }
