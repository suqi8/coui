// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.menus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.IconTooltip
import io.github.suqi8.coui.kmp.basic.ImageTooltip
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.Tooltip
import io.github.suqi8.coui.kmp.basic.TooltipAnchorPosition
import io.github.suqi8.coui.kmp.basic.TooltipBox
import io.github.suqi8.coui.kmp.basic.TooltipMode
import io.github.suqi8.coui.kmp.basic.TooltipScope
import io.github.suqi8.coui.kmp.basic.TooltipStyle
import io.github.suqi8.coui.kmp.basic.rememberTooltipState
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.blur_test_bg
import io.github.suqi8.coui.kmp.shared.generated.resources.common_end
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_mode
import io.github.suqi8.coui.kmp.shared.generated.resources.common_position
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_title
import io.github.suqi8.coui.kmp.shared.generated.resources.common_start
import io.github.suqi8.coui.kmp.shared.generated.resources.common_style
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_button_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_button_image
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_button_info
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_button_no_arrow
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_button_tooltips
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_dismiss_outside
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_got_it
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_icon_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_icon_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_image_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_image_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_no_arrow_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_show
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_show_arrow
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_show_dismiss
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_text_info
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_text_tooltips
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_variant_placement
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_tooltip_variant_styles
import io.github.suqi8.coui.kmp.shared.generated.resources.ic_launcher
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_text
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

internal val TooltipDemo = Demo(
    id = "tooltip",
    apiName = "Tooltip",
    title = Res.string.demo_tooltip_title,
    category = DemoCategory.Menus,
    page = DemoPage.Variants {
        playground { TooltipPlayground() }
        variant(key = "styles", title = Res.string.demo_tooltip_variant_styles) {
            TooltipStylesContent()
        }
        variant(key = "placement", title = Res.string.demo_tooltip_variant_placement) {
            TooltipPlacementContent()
        }
    },
    keywords = listOf("tips", "提示", "TooltipBox", "IconTooltip", "ImageTooltip", "气泡"),
)

private val TooltipPlacements = listOf(
    TooltipAnchorPosition.Above,
    TooltipAnchorPosition.Below,
    TooltipAnchorPosition.Start,
    TooltipAnchorPosition.End,
    TooltipAnchorPosition.Left,
    TooltipAnchorPosition.Right,
)

@Composable
private fun ColumnScope.TooltipPlayground() {
    var styleIndex by rememberSaveable { mutableIntStateOf(0) }
    var modeIndex by rememberSaveable { mutableIntStateOf(0) }
    var placementIndex by rememberSaveable { mutableIntStateOf(0) }
    var showArrow by rememberSaveable { mutableStateOf(true) }
    var dismissOutside by rememberSaveable { mutableStateOf(true) }
    var longText by rememberSaveable { mutableStateOf(false) }
    var showTitle by rememberSaveable { mutableStateOf(true) }
    var showDismiss by rememberSaveable { mutableStateOf(true) }

    val style = TooltipStyle.entries[styleIndex]
    val mode = TooltipMode.entries[modeIndex]
    val longBody = stringResource(Res.string.sample_long_text)
    val body = when {
        longText -> longBody
        style == TooltipStyle.Icon -> stringResource(Res.string.demo_tooltip_icon_text)
        style == TooltipStyle.Image -> stringResource(Res.string.demo_tooltip_image_text)
        mode == TooltipMode.Info -> stringResource(Res.string.demo_tooltip_text_info)
        else -> stringResource(Res.string.demo_tooltip_text_tooltips)
    }
    val title = when {
        !showTitle -> null
        style == TooltipStyle.Image -> stringResource(Res.string.demo_tooltip_image_title)
        else -> stringResource(Res.string.demo_tooltip_icon_title)
    }
    val dismissText = if (showDismiss) stringResource(Res.string.demo_tooltip_got_it) else null
    val icon = painterResource(Res.drawable.ic_launcher)
    val image = painterResource(Res.drawable.blur_test_bg)

    PlaygroundPreview {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
            TooltipDemoButton(
                label = stringResource(Res.string.demo_tooltip_show),
                placement = TooltipPlacements[placementIndex],
                showArrow = showArrow,
                dismissOnTouchOutside = dismissOutside,
            ) {
                when (style) {
                    TooltipStyle.Default -> Tooltip(text = body, mode = mode)
                    TooltipStyle.Icon -> IconTooltip(text = body, icon = icon, title = title, dismissText = dismissText)
                    TooltipStyle.Image -> ImageTooltip(text = body, image = image, title = title, dismissText = dismissText)
                }
            }
        }
    }
    val styleItems = TooltipStyle.entries.map { it.name }
    val modeItems = TooltipMode.entries.map { it.name }
    val placementItems = TooltipPlacements.map { it.toString() }
    val styleTitle = stringResource(Res.string.common_style)
    val modeTitle = stringResource(Res.string.common_mode)
    val placementTitle = stringResource(Res.string.common_position)
    val arrowTitle = stringResource(Res.string.demo_tooltip_show_arrow)
    val outsideTitle = stringResource(Res.string.demo_tooltip_dismiss_outside)
    val longTitle = stringResource(Res.string.common_long_text)
    val titleTitle = stringResource(Res.string.common_show_title)
    val dismissTitle = stringResource(Res.string.demo_tooltip_show_dismiss)
    PlaygroundControls {
        item {
            OverlayDropdownPreference(
                items = styleItems,
                selectedIndex = styleIndex,
                title = styleTitle,
                summary = "TooltipStyle",
                onSelectedIndexChange = { styleIndex = it },
            )
        }
        item {
            OverlayDropdownPreference(
                items = modeItems,
                selectedIndex = modeIndex,
                title = modeTitle,
                summary = "TooltipMode",
                enabled = style == TooltipStyle.Default,
                onSelectedIndexChange = { modeIndex = it },
            )
        }
        item {
            OverlayDropdownPreference(
                items = placementItems,
                selectedIndex = placementIndex,
                title = placementTitle,
                summary = "placement",
                onSelectedIndexChange = { placementIndex = it },
            )
        }
        item {
            SwitchPreference(
                checked = showArrow,
                onCheckedChange = { showArrow = it },
                title = arrowTitle,
                summary = "showArrow",
            )
        }
        item {
            SwitchPreference(
                checked = dismissOutside,
                onCheckedChange = { dismissOutside = it },
                title = outsideTitle,
                summary = "dismissOnTouchOutside",
            )
        }
        item {
            SwitchPreference(checked = longText, onCheckedChange = { longText = it }, title = longTitle)
        }
        item {
            SwitchPreference(
                checked = showTitle,
                onCheckedChange = { showTitle = it },
                title = titleTitle,
                enabled = style != TooltipStyle.Default,
            )
        }
        item {
            SwitchPreference(
                checked = showDismiss,
                onCheckedChange = { showDismiss = it },
                title = dismissTitle,
                summary = "dismissText",
                enabled = style != TooltipStyle.Default,
            )
        }
    }
}

/** One button per tooltip style and mode. */
@Composable
private fun TooltipStylesContent() {
    val gotIt = stringResource(Res.string.demo_tooltip_got_it)
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val tooltipsText = stringResource(Res.string.demo_tooltip_text_tooltips)
        TooltipDemoButton(label = stringResource(Res.string.demo_tooltip_button_tooltips)) {
            Tooltip(text = tooltipsText)
        }
        val infoText = stringResource(Res.string.demo_tooltip_text_info)
        TooltipDemoButton(label = stringResource(Res.string.demo_tooltip_button_info), dismissOnTouchOutside = true) {
            Tooltip(text = infoText, mode = TooltipMode.Info)
        }
        val iconTitle = stringResource(Res.string.demo_tooltip_icon_title)
        val iconText = stringResource(Res.string.demo_tooltip_icon_text)
        val icon = painterResource(Res.drawable.ic_launcher)
        TooltipDemoButton(label = stringResource(Res.string.demo_tooltip_button_icon), placement = TooltipAnchorPosition.Below) {
            IconTooltip(title = iconTitle, text = iconText, icon = icon, dismissText = gotIt)
        }
        val imageTitle = stringResource(Res.string.demo_tooltip_image_title)
        val imageText = stringResource(Res.string.demo_tooltip_image_text)
        val image = painterResource(Res.drawable.blur_test_bg)
        TooltipDemoButton(label = stringResource(Res.string.demo_tooltip_button_image), placement = TooltipAnchorPosition.Below) {
            ImageTooltip(title = imageTitle, text = imageText, image = image, dismissText = gotIt)
        }
    }
}

/** Start and end placements and a tooltip without its arrow. */
@Composable
private fun TooltipPlacementContent() {
    val start = stringResource(Res.string.common_start)
    val end = stringResource(Res.string.common_end)
    val noArrowText = stringResource(Res.string.demo_tooltip_no_arrow_text)
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TooltipDemoButton(label = start, placement = TooltipAnchorPosition.Start, dismissOnTouchOutside = true) {
            Tooltip(text = start, mode = TooltipMode.Info)
        }
        TooltipDemoButton(
            label = stringResource(Res.string.demo_tooltip_button_no_arrow),
            showArrow = false,
            dismissOnTouchOutside = true,
        ) {
            Tooltip(text = noArrowText, mode = TooltipMode.Info)
        }
        TooltipDemoButton(label = end, placement = TooltipAnchorPosition.End, dismissOnTouchOutside = true) {
            Tooltip(text = end, mode = TooltipMode.Info)
        }
    }
}

/** A text button that toggles its own tooltip. */
@Composable
private fun TooltipDemoButton(
    label: String,
    modifier: Modifier = Modifier,
    placement: TooltipAnchorPosition = TooltipAnchorPosition.Above,
    showArrow: Boolean = true,
    dismissOnTouchOutside: Boolean = false,
    tooltip: @Composable TooltipScope.() -> Unit,
) {
    val state = rememberTooltipState()
    val scope = rememberCoroutineScope()
    TooltipBox(
        state = state,
        tooltip = tooltip,
        modifier = modifier,
        placement = placement,
        showArrow = showArrow,
        dismissOnTouchOutside = dismissOnTouchOutside,
    ) {
        TextButton(
            text = label,
            onClick = {
                if (state.isVisible) state.dismiss() else scope.launch { state.show() }
            },
        )
    }
}
