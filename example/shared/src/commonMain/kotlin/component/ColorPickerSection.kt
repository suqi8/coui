// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.ColorPalette
import io.github.suqi8.coui.kmp.basic.ColorPicker
import io.github.suqi8.coui.kmp.basic.ColorSpace
import io.github.suqi8.coui.kmp.basic.ColorSwatchPicker
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextField
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.round

fun LazyListScope.colorPickerSection() {
    item(key = "colorSwatchPicker") {
        SmallTitle(text = "ColorSwatchPicker")
        ColorSwatchPickerCard()
    }

    item(key = "colorPicker-HSV") {
        SmallTitle(text = "ColorPicker (HSV)")
        ColorPickerCard(colorSpace = ColorSpace.HSV)
    }

    item(key = "colorPicker-OKHSV") {
        SmallTitle(text = "ColorPicker (OKHSV)")
        ColorPickerCard(colorSpace = ColorSpace.OKHSV)
    }

    item(key = "colorPicker-OKLAB") {
        SmallTitle(text = "ColorPicker (OKLAB)")
        ColorPickerCard(colorSpace = ColorSpace.OKLAB)
    }

    item(key = "colorPicker-OKLCH") {
        SmallTitle(text = "ColorPicker (OKLCH)")
        ColorPickerCard(colorSpace = ColorSpace.OKLCH)
    }

    item(key = "colorPalette") {
        SmallTitle(text = "ColorPalette")
        val couiColor = COUITheme.colorScheme.primary
        var selectedColor by remember { mutableStateOf(couiColor) }
        var colorHex by remember(selectedColor) {
            mutableStateOf(
                selectedColor.toArgb().toHexString(HexFormat.UpperCase),
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            insideMargin = PaddingValues(16.dp),
        ) {
            Row(
                modifier = Modifier.padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "RGBA: ${(selectedColor.red * 255).toInt()}, " +
                        "${(selectedColor.green * 255).toInt()}, " +
                        "${(selectedColor.blue * 255).toInt()}, " +
                        "${(round(selectedColor.alpha * 100) / 100.0)}",
                    modifier = Modifier.weight(1f),
                )
            }
            ColorPalette(
                color = selectedColor,
                onColorChanged = { selectedColor = it },
                showPreview = false,
            )
            ColorHexTextField(colorHex) { newHex, newColor ->
                colorHex = newHex
                if (newColor != null) selectedColor = newColor
            }
        }
    }
}

@Composable
private fun ColorSwatchPickerCard() {
    val swatchColors = remember {
        listOf(
            Color(0xFF0066FF),
            Color(0xFF24B232),
            Color(0xFFF08222),
            Color(0xFFDB382C),
            Color(0xFFDB9A00),
            Color(0xFF8A8A8A),
        )
    }
    var selectedIndex by remember { mutableIntStateOf(0) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        insideMargin = PaddingValues(16.dp),
    ) {
        ColorSwatchPicker(
            colors = swatchColors,
            selectedIndex = selectedIndex,
            onSwatchSelected = { selectedIndex = it },
        )
    }
}

@Composable
private fun ColorPickerCard(colorSpace: ColorSpace) {
    val couiColor = COUITheme.colorScheme.primary
    var selectedColor by remember { mutableStateOf(couiColor) }
    var colorHex by remember(selectedColor) {
        mutableStateOf(
            selectedColor.toArgb().toHexString(HexFormat.UpperCase),
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        insideMargin = PaddingValues(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "RGBA: ${(selectedColor.red * 255).toInt()}, " +
                    "${(selectedColor.green * 255).toInt()}, " +
                    "${(selectedColor.blue * 255).toInt()}, " +
                    "${(round(selectedColor.alpha * 100) / 100.0)}",
                modifier = Modifier.weight(1f),
            )
        }
        ColorPicker(
            color = selectedColor,
            onColorChanged = { selectedColor = it },
            colorSpace = colorSpace,
            showPreview = false,
        )
        ColorHexTextField(colorHex) { newHex, newColor ->
            colorHex = newHex
            if (newColor != null) selectedColor = newColor
        }
    }
}

@Composable
private fun ColorHexTextField(
    colorHex: String,
    onUpdate: (String, Color?) -> Unit,
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
        leadingIcon = {
            Text(
                "HEX: #",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 16.dp),
            )
        },
        modifier = Modifier.padding(top = 12.dp),
    )
}
