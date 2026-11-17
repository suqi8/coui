// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.pickers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.ColorPalette
import io.github.suqi8.coui.kmp.basic.ColorPicker
import io.github.suqi8.coui.kmp.basic.ColorSpace
import io.github.suqi8.coui.kmp.basic.ColorSwatchPicker
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextField
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_color_picker_color_space
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_color_picker_component
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_color_picker_gray_column
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_color_picker_hue_columns
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_color_picker_rows
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_color_picker_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_color_picker_show_preview
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_color_picker_show_preview_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_color_picker_title
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource
import kotlin.math.round
import kotlin.math.roundToInt

/** The demo page of `ColorPicker`, listed in its category and found by the search. */
internal val ColorPickerDemo = Demo(
    id = "color_picker",
    apiName = "ColorPicker",
    title = Res.string.demo_color_picker_title,
    category = DemoCategory.Pickers,
    page = DemoPage.Variants { colorPickerPage() },
    keywords = listOf("ColorPalette", "ColorSwatchPicker"),
    searchTerms = Res.string.demo_color_picker_search_terms,
)

/** The pickers of the playground, by API name. */
private val ColorPickerTypes = listOf("ColorPicker", "ColorPalette", "ColorSwatchPicker")

/** The sample swatches of the swatch picker; sample data, not UI colours. */
private val SwatchColors = listOf(
    Color(0xFF0080FF),
    Color(0xFF00BD13),
    Color(0xFFF08222),
    Color(0xFFDB382C),
    Color(0xFFDB9A00),
    Color(0xFF8A8A8A),
)

/** Saves a colour state as its packed 64-bit value. */
private val ColorStateSaver = Saver<MutableState<Color>, Long>(
    save = { it.value.value.toLong() },
    restore = { mutableStateOf(Color(it.toULong())) },
)

private fun LazyListScope.colorPickerPage() {
    playground {
        val initialColor = COUITheme.colorScheme.primary
        var color by rememberSaveable(saver = ColorStateSaver) { mutableStateOf(initialColor) }
        var typeIndex by rememberSaveable { mutableIntStateOf(0) }
        var colorSpaceIndex by rememberSaveable { mutableIntStateOf(ColorSpace.HSV.ordinal) }
        var showPreview by rememberSaveable { mutableStateOf(true) }
        var rows by rememberSaveable { mutableIntStateOf(7) }
        var hueColumns by rememberSaveable { mutableIntStateOf(12) }
        var grayColumn by rememberSaveable { mutableStateOf(true) }
        var swatchIndex by rememberSaveable { mutableIntStateOf(0) }
        var enabled by rememberSaveable { mutableStateOf(true) }

        PlaygroundPreview {
            when (typeIndex) {
                0 -> ColorPicker(
                    color = color,
                    onColorChanged = { color = it },
                    showPreview = showPreview,
                    colorSpace = ColorSpace.entries[colorSpaceIndex],
                )

                1 -> ColorPalette(
                    color = color,
                    onColorChanged = { color = it },
                    rows = rows,
                    hueColumns = hueColumns,
                    includeGrayColumn = grayColumn,
                    showPreview = showPreview,
                )

                else -> ColorSwatchPicker(
                    colors = SwatchColors,
                    selectedIndex = swatchIndex,
                    onSwatchSelected = {
                        swatchIndex = it
                        color = SwatchColors[it]
                    },
                    enabled = enabled,
                )
            }
            RgbaText(color = color, modifier = Modifier.padding(top = 12.dp))
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = ColorPickerTypes,
                    selectedIndex = typeIndex,
                    title = stringResource(Res.string.demo_color_picker_component),
                    onSelectedIndexChange = { typeIndex = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    items = ColorSpace.entries.map { it.name },
                    selectedIndex = colorSpaceIndex,
                    title = stringResource(Res.string.demo_color_picker_color_space),
                    enabled = typeIndex == 0,
                    onSelectedIndexChange = { colorSpaceIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = showPreview,
                    onCheckedChange = { showPreview = it },
                    title = stringResource(Res.string.demo_color_picker_show_preview),
                    summary = stringResource(Res.string.demo_color_picker_show_preview_summary),
                    enabled = typeIndex != 2,
                )
            }
            item {
                SliderPreference(
                    value = rows.toFloat(),
                    onValueChange = { rows = it.roundToInt() },
                    title = stringResource(Res.string.demo_color_picker_rows),
                    valueText = rows.toString(),
                    enabled = typeIndex == 1,
                    valueRange = 3f..9f,
                    steps = 5,
                )
            }
            item {
                SliderPreference(
                    value = hueColumns.toFloat(),
                    onValueChange = { hueColumns = it.roundToInt() },
                    title = stringResource(Res.string.demo_color_picker_hue_columns),
                    valueText = hueColumns.toString(),
                    enabled = typeIndex == 1,
                    valueRange = 6f..18f,
                    steps = 11,
                )
            }
            item {
                SwitchPreference(
                    checked = grayColumn,
                    onCheckedChange = { grayColumn = it },
                    title = stringResource(Res.string.demo_color_picker_gray_column),
                    enabled = typeIndex == 1,
                )
            }
            item {
                SwitchPreference(
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                    title = stringResource(Res.string.common_enabled),
                    enabled = typeIndex == 2,
                )
            }
        }
    }
    variant(key = "colorSwatchPicker", title = "ColorSwatchPicker") {
        var selectedIndex by remember { mutableIntStateOf(0) }
        ColorSwatchPicker(
            colors = SwatchColors,
            selectedIndex = selectedIndex,
            onSwatchSelected = { selectedIndex = it },
            modifier = Modifier.padding(16.dp),
        )
    }
    ColorSpace.entries.forEach { colorSpace ->
        variant(key = "colorPicker-${colorSpace.name}", title = "ColorPicker (${colorSpace.name})") {
            ColorPickerVariant(colorSpace = colorSpace)
        }
    }
    variant(key = "colorPalette", title = "ColorPalette") {
        ColorPickerVariant(colorSpace = null)
    }
}

/**
 * A [ColorPicker] in [colorSpace], or a [ColorPalette] when it is null, with the RGBA read-out
 * and a HEX field that edits the colour.
 */
@Composable
private fun ColorPickerVariant(
    colorSpace: ColorSpace?,
    modifier: Modifier = Modifier,
) {
    val initialColor = COUITheme.colorScheme.primary
    var color by remember { mutableStateOf(initialColor) }
    var colorHex by remember(color) { mutableStateOf(color.toArgb().toHexString(HexFormat.UpperCase)) }

    Column(modifier = modifier.padding(16.dp)) {
        RgbaText(color = color, modifier = Modifier.padding(bottom = 12.dp))
        if (colorSpace != null) {
            ColorPicker(
                color = color,
                onColorChanged = { color = it },
                showPreview = false,
                colorSpace = colorSpace,
            )
        } else {
            ColorPalette(
                color = color,
                onColorChanged = { color = it },
                showPreview = false,
            )
        }
        ColorHexTextField(
            colorHex = colorHex,
            onUpdate = { newHex, newColor ->
                colorHex = newHex
                if (newColor != null) color = newColor
            },
        )
    }
}

@Composable
private fun RgbaText(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "RGBA: ${(color.red * 255).roundToInt()}, " +
            "${(color.green * 255).roundToInt()}, " +
            "${(color.blue * 255).roundToInt()}, " +
            "${round(color.alpha * 100) / 100.0}",
        modifier = modifier.fillMaxWidth(),
        style = COUITheme.textStyles.bodyM,
        color = COUITheme.colorScheme.labelSecondary,
        textAlign = TextAlign.Start,
    )
}

@Composable
private fun ColorHexTextField(
    colorHex: String,
    onUpdate: (String, Color?) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = colorHex,
        onValueChange = { newHex ->
            if (newHex.length <= 8 && newHex.all { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }) {
                val upperHex = newHex.uppercase()
                val newColor = if (newHex.length == 8) {
                    Color(upperHex.toUInt(16).toInt())
                } else {
                    null
                }
                onUpdate(upperHex, newColor)
            }
        },
        modifier = modifier.padding(top = 12.dp),
        leadingIcon = {
            Text(
                "HEX: #",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 16.dp),
            )
        },
    )
}
