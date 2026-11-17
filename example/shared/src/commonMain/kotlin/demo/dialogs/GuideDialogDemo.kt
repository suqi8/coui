// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.dialogs

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.layout.GuideDialogPage
import io.github.suqi8.coui.kmp.overlay.OverlayGuideDialog
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_next
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_window_dim
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_image_height
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_open_pages
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_open_pages_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_open_single
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_open_single_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_page1_description
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_page1_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_page2_description
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_page2_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_page3_description
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_page3_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_page4_description
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_page4_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_result_finished
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_result_none
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_result_skipped
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_show
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_single_description
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_single_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_skip
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_start
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_guide_dialog_variant_pages
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.window.WindowGuideDialog
import org.jetbrains.compose.resources.stringResource

internal val GuideDialogDemo = Demo(
    id = "guide_dialog",
    apiName = "OverlayGuideDialog",
    title = Res.string.demo_guide_dialog_title,
    category = DemoCategory.Dialogs,
    page = DemoPage.Variants {
        playground {
            GuideDialogPlayground()
        }
        variant(key = "pages", title = Res.string.demo_guide_dialog_variant_pages) {
            GuideDialogsVariant()
        }
    },
    keywords = listOf("onboarding", "tips", "引导", "新手", "弹窗"),
)

/** The page counts the playground offers; numbers stay literals. */
private val PageCounts = listOf("1", "2", "3", "4")

// The last action of the guide, kept as a code so its text follows the language.
private const val RESULT_NONE = 0
private const val RESULT_FINISHED = 1
private const val RESULT_SKIPPED = 2

/**
 * Builds up to four sample guide pages. The images are flat swatches of the theme primary colour,
 * standing in for the illustrations of a real guide.
 */
@Composable
private fun rememberGuidePages(count: Int): List<GuideDialogPage> {
    val primary = COUITheme.colorScheme.primary
    val titles = listOf(
        stringResource(Res.string.demo_guide_dialog_page1_title),
        stringResource(Res.string.demo_guide_dialog_page2_title),
        stringResource(Res.string.demo_guide_dialog_page3_title),
        stringResource(Res.string.demo_guide_dialog_page4_title),
    )
    val descriptions = listOf(
        stringResource(Res.string.demo_guide_dialog_page1_description),
        stringResource(Res.string.demo_guide_dialog_page2_description),
        stringResource(Res.string.demo_guide_dialog_page3_description),
        stringResource(Res.string.demo_guide_dialog_page4_description),
    )
    return remember(primary, titles, descriptions, count) {
        List(count) { index ->
            GuideDialogPage(
                title = titles[index],
                description = descriptions[index],
                image = ColorPainter(primary.copy(alpha = 0.25f + 0.15f * index)),
            )
        }
    }
}

@Composable
private fun ColumnScope.GuideDialogPlayground() {
    var hostIndex by rememberSaveable { mutableIntStateOf(0) }
    var material by rememberSaveable { mutableStateOf(true) }
    var countIndex by rememberSaveable { mutableIntStateOf(2) }
    var imageHeight by rememberSaveable { mutableFloatStateOf(240f) }
    var skippable by rememberSaveable { mutableStateOf(true) }
    var windowDim by rememberSaveable { mutableStateOf(true) }
    var show by remember { mutableStateOf(false) }
    var result by rememberSaveable { mutableIntStateOf(RESULT_NONE) }
    val window = hostIndex == HOST_WINDOW
    val pages = rememberGuidePages(count = countIndex + 1)

    PlaygroundPreview {
        DialogLaunchButton(text = stringResource(Res.string.demo_guide_dialog_show), onClick = { show = true })
        DialogDemoCaption(text = if (window) "WindowGuideDialog" else "OverlayGuideDialog")
        DialogDemoCaption(
            text = stringResource(
                when (result) {
                    RESULT_FINISHED -> Res.string.demo_guide_dialog_result_finished
                    RESULT_SKIPPED -> Res.string.demo_guide_dialog_result_skipped
                    else -> Res.string.demo_guide_dialog_result_none
                },
            ),
        )
    }
    PlaygroundControls {
        dialogHostRow(selectedIndex = hostIndex, onSelectedIndexChange = { hostIndex = it })
        dialogMaterialRow(checked = material, onCheckedChange = { material = it }, enabled = !window)
        dialogDropdownRow(
            title = Res.string.common_count,
            items = PageCounts,
            selectedIndex = countIndex,
            onSelectedIndexChange = { countIndex = it },
        )
        item {
            SliderPreference(
                value = imageHeight,
                onValueChange = { imageHeight = it },
                title = stringResource(Res.string.demo_guide_dialog_image_height),
                valueText = "${imageHeight.toInt()} dp",
                valueRange = 160f..328f,
            )
        }
        dialogSwitchRow(
            title = Res.string.demo_guide_dialog_skip,
            checked = skippable,
            onCheckedChange = { skippable = it },
        )
        dialogSwitchRow(
            title = Res.string.demo_dialog_window_dim,
            checked = windowDim,
            onCheckedChange = { windowDim = it },
        )
    }

    GuideDialog(
        show = show,
        window = window,
        material = material,
        pages = pages,
        imageHeight = imageHeight.toInt(),
        enableWindowDim = windowDim,
        onStart = {
            result = RESULT_FINISHED
            show = false
        },
        // Without its own callback the skip button falls back to onStart.
        onSkip = if (skippable) {
            {
                result = RESULT_SKIPPED
                show = false
            }
        } else {
            null
        },
        onDismissRequest = { show = false },
        onDismissFinished = null,
    )
}

/**
 * The guide dialog on either host. The COUI 17 material blur only applies to the Overlay host.
 *
 * @param imageHeight The image height in dp.
 */
@Composable
private fun GuideDialog(
    show: Boolean,
    window: Boolean,
    material: Boolean,
    pages: List<GuideDialogPage>,
    imageHeight: Int,
    enableWindowDim: Boolean,
    onStart: () -> Unit,
    onSkip: (() -> Unit)?,
    onDismissRequest: () -> Unit,
    onDismissFinished: (() -> Unit)?,
) {
    val skipText = stringResource(Res.string.demo_guide_dialog_skip)
    val nextText = stringResource(Res.string.common_next)
    val startText = stringResource(Res.string.demo_guide_dialog_start)
    if (window) {
        WindowGuideDialog(
            show = show,
            pages = pages,
            onStart = onStart,
            onSkip = onSkip,
            onDismissRequest = onDismissRequest,
            skipText = skipText,
            nextText = nextText,
            startText = startText,
            imageHeight = imageHeight.dp,
            enableWindowDim = enableWindowDim,
            onDismissFinished = onDismissFinished,
        )
    } else {
        val blur = demoPanelBlur(enabled = material)
        OverlayGuideDialog(
            show = show,
            pages = pages,
            onStart = onStart,
            backgroundModifier = blur?.backgroundModifier ?: Modifier,
            onSkip = onSkip,
            onDismissRequest = onDismissRequest,
            skipText = skipText,
            nextText = nextText,
            startText = startText,
            imageHeight = imageHeight.dp,
            backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
            enableWindowDim = enableWindowDim,
            materialStroke = blur != null,
            onDismissFinished = onDismissFinished,
        )
    }
}

/** A three-page guide with a pager and a single-page tip. */
@Composable
private fun ColumnScope.GuideDialogsVariant() {
    var showPages by remember { mutableStateOf(false) }
    var pagesHoldDown by remember { mutableStateOf(false) }
    var showSingle by remember { mutableStateOf(false) }
    var singleHoldDown by remember { mutableStateOf(false) }
    CardListGroup {
        dialogLaunchRow(
            title = Res.string.demo_guide_dialog_open_pages,
            summary = Res.string.demo_guide_dialog_open_pages_summary,
            holdDown = pagesHoldDown,
            onClick = {
                showPages = true
                pagesHoldDown = true
            },
        )
        dialogLaunchRow(
            title = Res.string.demo_guide_dialog_open_single,
            summary = Res.string.demo_guide_dialog_open_single_summary,
            holdDown = singleHoldDown,
            onClick = {
                showSingle = true
                singleHoldDown = true
            },
        )
    }
    val pages = rememberGuidePages(count = 3)
    val primary = COUITheme.colorScheme.primary
    val singleTitle = stringResource(Res.string.demo_guide_dialog_single_title)
    val singleDescription = stringResource(Res.string.demo_guide_dialog_single_description)
    val singlePage = remember(primary, singleTitle, singleDescription) {
        listOf(
            GuideDialogPage(
                title = singleTitle,
                description = singleDescription,
                image = ColorPainter(primary.copy(alpha = 0.3f)),
            ),
        )
    }
    GuideDialog(
        show = showPages,
        window = false,
        material = true,
        pages = pages,
        imageHeight = 240,
        enableWindowDim = true,
        onStart = { showPages = false },
        onSkip = null,
        onDismissRequest = { showPages = false },
        onDismissFinished = { pagesHoldDown = false },
    )
    GuideDialog(
        show = showSingle,
        window = false,
        material = true,
        pages = singlePage,
        imageHeight = 240,
        enableWindowDim = true,
        onStart = { showSingle = false },
        onSkip = null,
        onDismissRequest = { showSingle = false },
        onDismissFinished = { singleHoldDown = false },
    )
}
