// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.LocalDemoSnackbarHostState
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.Button
import io.github.suqi8.coui.kmp.basic.ButtonDefaults
import io.github.suqi8.coui.kmp.basic.ButtonMaterial
import io.github.suqi8.coui.kmp.basic.DescTextButton
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.TextButtonColors
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Add
import io.github.suqi8.coui.kmp.icon.extended.Share
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_add
import io.github.suqi8.coui.kmp.shared.generated.resources.common_cancel
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_large
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.common_share
import io.github.suqi8.coui.kmp.shared.generated.resources.common_size
import io.github.suqi8.coui.kmp.shared.generated.resources.common_small
import io.github.suqi8.coui.kmp.shared.generated.resources.common_style
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_backdrop
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_backdrop_blur
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_background
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_borderless
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_click_count
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_clicked
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_desc
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_label
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_label_long
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_later
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_later_desc
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_material_label
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_press_scale
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_submit
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_transparent
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_type_borderless
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_type_primary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_type_secondary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_type_transparent
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_upgrade
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_upgrade_desc
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_variant_backdrop
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_variant_custom
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_variant_desc
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_variant_material
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_variant_small
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_variant_types
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_width
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_width_fill
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_width_large
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_width_medium
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_width_wrap
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalContentColor
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

internal val ButtonDemo = Demo(
    id = "button",
    apiName = "Button",
    title = Res.string.demo_button_title,
    category = DemoCategory.Buttons,
    page = DemoPage.Variants { buttonPage() },
    keywords = listOf("TextButton", "DescTextButton", "按钮"),
)

private const val TYPE_SECONDARY = 0
private const val TYPE_PRIMARY = 1
private const val TYPE_BORDERLESS = 2
private const val TYPE_TRANSPARENT = 3

private const val WIDTH_WRAP = 0
private const val WIDTH_MEDIUM = 1
private const val WIDTH_LARGE = 2

private fun LazyListScope.buttonPage() {
    playground {
        var type by rememberSaveable { mutableIntStateOf(TYPE_SECONDARY) }
        var small by rememberSaveable { mutableStateOf(false) }
        var widthIndex by rememberSaveable { mutableIntStateOf(WIDTH_WRAP) }
        var materialIndex by rememberSaveable { mutableIntStateOf(ButtonMaterial.Auto.ordinal) }
        var backgroundIndex by rememberSaveable { mutableIntStateOf(StageBackground.Plain.ordinal) }
        var materialBackdrop by rememberSaveable { mutableStateOf(true) }
        var desc by rememberSaveable { mutableStateOf(false) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        var pressScale by rememberSaveable { mutableStateOf(true) }
        var longText by rememberSaveable { mutableStateOf(false) }

        val snackbarHostState = LocalDemoSnackbarHostState.current
        val scope = rememberCoroutineScope()
        val onClick: () -> Unit = {
            scope.launch { snackbarHostState.showSnackbar(getString(Res.string.demo_button_clicked)) }
        }
        val colors = textButtonColorsOf(type)
        val material = ButtonMaterial.entries[materialIndex]
        val largeWidth = ButtonDefaults.largeWidth()
        val widthModifier = when (widthIndex) {
            WIDTH_WRAP -> Modifier
            WIDTH_MEDIUM -> Modifier.width(ButtonDefaults.MediumWidth)
            WIDTH_LARGE -> if (largeWidth == Dp.Unspecified) Modifier.fillMaxWidth() else Modifier.width(largeWidth)
            else -> Modifier.fillMaxWidth()
        }

        PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
            ButtonStage(background = StageBackground.entries[backgroundIndex]) {
                if (desc) {
                    DescTextButton(
                        text = stringResource(if (longText) Res.string.demo_button_label_long else Res.string.demo_button_upgrade),
                        descText = stringResource(Res.string.demo_button_upgrade_desc),
                        onClick = onClick,
                        modifier = widthModifier,
                        enabled = enabled,
                        pressScaleEnabled = pressScale,
                        colors = colors,
                        material = material,
                        materialBackdrop = materialBackdrop,
                    )
                } else {
                    TextButton(
                        text = stringResource(if (longText) Res.string.demo_button_label_long else Res.string.demo_button_label),
                        onClick = onClick,
                        modifier = widthModifier,
                        enabled = enabled,
                        pressScaleEnabled = pressScale,
                        minWidth = if (small) ButtonDefaults.MinWidthSmall else ButtonDefaults.MinWidth,
                        minHeight = if (small) ButtonDefaults.MinHeightSmall else ButtonDefaults.MinHeight,
                        colors = colors,
                        insideMargin = if (small) ButtonDefaults.InsideMarginSmall else ButtonDefaults.InsideMargin,
                        textStyle = if (small) ButtonDefaults.textStyleSmall() else COUITheme.textStyles.buttonL,
                        maxLines = if (longText) 2 else 1,
                        material = material,
                        materialBackdrop = materialBackdrop,
                    )
                }
            }
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_style),
                    summary = "colors",
                    items = listOf(
                        stringResource(Res.string.demo_button_type_secondary),
                        stringResource(Res.string.demo_button_type_primary),
                        stringResource(Res.string.demo_button_type_borderless),
                        stringResource(Res.string.demo_button_type_transparent),
                    ),
                    selectedIndex = type,
                    onSelectedIndexChange = {
                        type = it
                        // The transparent type is made for coloured backdrops.
                        if (it == TYPE_TRANSPARENT && backgroundIndex == StageBackground.Plain.ordinal) {
                            backgroundIndex = StageBackground.Accent.ordinal
                        }
                    },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_size),
                    summary = "minWidth / minHeight / insideMargin / textStyle",
                    items = listOf(stringResource(Res.string.common_large), stringResource(Res.string.common_small)),
                    selectedIndex = if (small) 1 else 0,
                    onSelectedIndexChange = { small = it == 1 },
                    enabled = !desc,
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.demo_button_width),
                    items = listOf(
                        stringResource(Res.string.demo_button_width_wrap),
                        stringResource(Res.string.demo_button_width_medium),
                        stringResource(Res.string.demo_button_width_large),
                        stringResource(Res.string.demo_button_width_fill),
                    ),
                    selectedIndex = widthIndex,
                    onSelectedIndexChange = { widthIndex = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_material),
                    summary = "material",
                    items = ButtonMaterial.entries.map { it.name },
                    selectedIndex = materialIndex,
                    onSelectedIndexChange = { materialIndex = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.demo_button_background),
                    items = StageBackground.entries.map { stringResource(it.label) },
                    selectedIndex = backgroundIndex,
                    onSelectedIndexChange = { backgroundIndex = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_button_backdrop_blur),
                    summary = "materialBackdrop",
                    checked = materialBackdrop,
                    onCheckedChange = { materialBackdrop = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_button_desc),
                    summary = "DescTextButton",
                    checked = desc,
                    onCheckedChange = { desc = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_button_press_scale),
                    summary = "pressScaleEnabled",
                    checked = pressScale,
                    onCheckedChange = { pressScale = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.common_enabled),
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.common_long_text),
                    checked = longText,
                    onCheckedChange = { longText = it },
                )
            }
        }
    }
    variant(key = "types", title = Res.string.demo_button_variant_types) {
        var clickCount by remember { mutableIntStateOf(0) }
        var submitClickCount by remember { mutableIntStateOf(0) }
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ButtonPair(
                start = {
                    TextButton(
                        text = if (clickCount == 0) {
                            stringResource(Res.string.common_cancel)
                        } else {
                            stringResource(Res.string.demo_button_click_count, clickCount)
                        },
                        onClick = { clickCount++ },
                        modifier = it,
                    )
                },
                end = {
                    TextButton(
                        text = if (submitClickCount == 0) {
                            stringResource(Res.string.demo_button_submit)
                        } else {
                            stringResource(Res.string.demo_button_click_count, submitClickCount)
                        },
                        onClick = { submitClickCount++ },
                        modifier = it,
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                    )
                },
            )
            ButtonPair(
                start = {
                    TextButton(
                        text = stringResource(Res.string.common_disabled),
                        onClick = {},
                        modifier = it,
                        enabled = false,
                    )
                },
                end = {
                    TextButton(
                        text = stringResource(Res.string.common_disabled),
                        onClick = {},
                        modifier = it,
                        enabled = false,
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                    )
                },
            )
            ButtonPair(
                start = {
                    TextButton(
                        text = stringResource(Res.string.demo_button_borderless),
                        onClick = {},
                        modifier = it,
                        colors = ButtonDefaults.textButtonColorsBorderless(),
                    )
                },
                end = {
                    TextButton(
                        text = stringResource(Res.string.common_disabled),
                        onClick = {},
                        modifier = it,
                        enabled = false,
                        colors = ButtonDefaults.textButtonColorsBorderless(),
                    )
                },
            )
        }
    }
    // COUI 17 material layer (edge lighting + touch spotlight), forced on whatever the app setting.
    variant(key = "material", title = Res.string.demo_button_variant_material) {
        Column(modifier = Modifier.padding(16.dp)) {
            ButtonPair(
                start = {
                    TextButton(
                        text = stringResource(Res.string.demo_button_material_label),
                        onClick = {},
                        modifier = it,
                        material = ButtonMaterial.Secondary,
                    )
                },
                end = {
                    TextButton(
                        text = stringResource(Res.string.demo_button_material_label),
                        onClick = {},
                        modifier = it,
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                        material = ButtonMaterial.Primary,
                    )
                },
            )
        }
    }
    // The small size tier, and the transparent type laid over a coloured backdrop.
    variant(key = "small", title = Res.string.demo_button_variant_small) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(COUITheme.colorScheme.primary)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SmallTextButton(text = stringResource(Res.string.common_small))
            SmallTextButton(
                text = stringResource(Res.string.demo_button_transparent),
                colors = ButtonDefaults.textButtonColorsTransparent(),
                material = ButtonMaterial.Transparent,
            )
        }
    }
    // The desc-type button at the medium single-button width.
    variant(key = "desc", title = Res.string.demo_button_variant_desc) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DescTextButton(
                text = stringResource(Res.string.demo_button_upgrade),
                descText = stringResource(Res.string.demo_button_upgrade_desc),
                onClick = {},
                modifier = Modifier.width(ButtonDefaults.MediumWidth),
                colors = ButtonDefaults.textButtonColorsPrimary(),
            )
            DescTextButton(
                text = stringResource(Res.string.demo_button_later),
                descText = stringResource(Res.string.demo_button_later_desc),
                onClick = {},
                modifier = Modifier.weight(1f),
            )
        }
    }
    // The buttons sit on top of a recorded image, so the material replaces their fill with the
    // blurred, tinted image.
    variant(key = "backdrop", title = Res.string.demo_button_variant_backdrop) {
        ButtonStage(
            background = StageBackground.Photo,
            modifier = Modifier.padding(16.dp),
            cornerRadius = 16.dp,
            contentPadding = PaddingValues(12.dp),
        ) {
            Column {
                ButtonPair(
                    start = {
                        TextButton(
                            text = stringResource(Res.string.demo_button_backdrop),
                            onClick = {},
                            modifier = it,
                        )
                    },
                    end = {
                        TextButton(
                            text = stringResource(Res.string.demo_button_backdrop),
                            onClick = {},
                            modifier = it,
                            colors = ButtonDefaults.textButtonColorsPrimary(),
                        )
                    },
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SmallTextButton(
                        text = stringResource(Res.string.demo_button_transparent),
                        colors = ButtonDefaults.textButtonColorsTransparent(),
                    )
                    SmallTextButton(
                        text = stringResource(Res.string.common_disabled),
                        enabled = false,
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                    )
                }
            }
        }
    }
    // The content-slot Button for icon + label buttons.
    variant(key = "custom", title = Res.string.demo_button_variant_custom) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(onClick = {}, modifier = Modifier.weight(1f)) {
                IconLabel(icon = COUIIcons.Add, label = stringResource(Res.string.common_add))
            }
            Button(
                onClick = {},
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColorsPrimary(),
            ) {
                IconLabel(icon = COUIIcons.Share, label = stringResource(Res.string.common_share))
            }
        }
    }
}

/** The text button colours of a playground type. */
@Composable
private fun textButtonColorsOf(type: Int): TextButtonColors = when (type) {
    TYPE_PRIMARY -> ButtonDefaults.textButtonColorsPrimary()
    TYPE_BORDERLESS -> ButtonDefaults.textButtonColorsBorderless()
    TYPE_TRANSPARENT -> ButtonDefaults.textButtonColorsTransparent()
    else -> ButtonDefaults.textButtonColors()
}

/** Two buttons sharing a row equally, 12dp apart; each slot gets its weight modifier. */
@Composable
private fun ButtonPair(
    start: @Composable (Modifier) -> Unit,
    end: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        start(Modifier.weight(1f))
        end(Modifier.weight(1f))
    }
}

/** A text button in the COUI small size tier. */
@Composable
private fun SmallTextButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: TextButtonColors = ButtonDefaults.textButtonColors(),
    material: ButtonMaterial = ButtonMaterial.Auto,
) {
    TextButton(
        text = text,
        onClick = {},
        modifier = modifier,
        enabled = enabled,
        minWidth = ButtonDefaults.MinWidthSmall,
        minHeight = ButtonDefaults.MinHeightSmall,
        colors = colors,
        insideMargin = ButtonDefaults.InsideMarginSmall,
        textStyle = ButtonDefaults.textStyleSmall(),
        material = material,
    )
}

/** An icon followed by a label in the button content colour. */
@Composable
private fun IconLabel(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(6.dp))
        Text(text = label, color = LocalContentColor.current, style = COUITheme.textStyles.buttonL)
    }
}
