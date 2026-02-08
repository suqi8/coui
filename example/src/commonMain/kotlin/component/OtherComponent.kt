// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suqi8.coui.kmp.basic.Button
import com.suqi8.coui.kmp.basic.ButtonDefaults
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.CardDefaults
import com.suqi8.coui.kmp.basic.Checkbox
import com.suqi8.coui.kmp.basic.CircularProgressIndicator
import com.suqi8.coui.kmp.basic.ColorPalette
import com.suqi8.coui.kmp.basic.ColorPicker
import com.suqi8.coui.kmp.basic.ColorSpace
import com.suqi8.coui.kmp.basic.CouiClearButton
import com.suqi8.coui.kmp.basic.CouiTextFieldMode
import com.suqi8.coui.kmp.basic.DescriptionButtonContent
import com.suqi8.coui.kmp.basic.Icon
import com.suqi8.coui.kmp.basic.LinearProgressIndicator
import com.suqi8.coui.kmp.basic.LoadingView
import com.suqi8.coui.kmp.basic.OutlinedButton
import com.suqi8.coui.kmp.basic.RangeSlider
import com.suqi8.coui.kmp.basic.Slider
import com.suqi8.coui.kmp.basic.SliderDefaults
import com.suqi8.coui.kmp.basic.SmallTitle
import com.suqi8.coui.kmp.basic.TabRow
import com.suqi8.coui.kmp.basic.TabRowWithContour
import com.suqi8.coui.kmp.basic.Text
import com.suqi8.coui.kmp.basic.TextButton
import com.suqi8.coui.kmp.basic.TextField
import com.suqi8.coui.kmp.basic.VerticalSlider
import com.suqi8.coui.kmp.icon.MiuixIcons
import com.suqi8.coui.kmp.icon.icons.useful.Like
import com.suqi8.coui.kmp.icon.icons.useful.Scan
import com.suqi8.coui.kmp.theme.COUITheme
import com.suqi8.coui.kmp.utils.PressFeedbackType
import kotlin.math.round

fun LazyListScope.otherComponent(
    miuixIcons: List<ImageVector>,
    focusManager: FocusManager,
    padding: PaddingValues
) {
    item(key = "button") {
        var clickCount by remember { mutableIntStateOf(0) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { clickCount++ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Click: $clickCount")
            }
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.weight(1f)
            ) {
                Text("Disabled")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { clickCount++ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Normal")
            }
            OutlinedButton(
                onClick = {},
                enabled = false,
                modifier = Modifier.weight(1f)
            ) {
                Text("Disabled")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(
                text = "Submit",
                onClick = { clickCount++ },
                modifier = Modifier.weight(1f)
            )
            TextButton(
                text = "Cancel",
                onClick = {},
                enabled = false,
                modifier = Modifier.weight(1f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {},
                minWidth = ButtonDefaults.LargeWidth // [精确数值] 使用 174dp 宽度
            ) {
                Text("Primary Large Action")
            }
            OutlinedButton(
                onClick = {},
                minWidth = ButtonDefaults.LargeWidth
            ) {
                Text("Secondary Large Action")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 实心带描述
            Button(
                onClick = {},
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 54.dp) // 稍微增高以容纳两行文字
            ) {
                DescriptionButtonContent(
                    text = "Auto Boost",
                    description = "Recommended"
                )
            }
            // 边框带描述
            OutlinedButton(
                onClick = {},
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 54.dp)
            ) {
                DescriptionButtonContent(
                    text = "Manual Mode",
                    description = "Advanced"
                )
            }
        }
    }

    item(key = "progressIndicator") {
        SmallTitle(text = "ProgressIndicator")
        val progressValues = listOf(0.0f, 0.25f, 0.5f, 0.75f, 1.0f, null)
        val animatedProgressValue by rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            )
        )

        LinearProgressIndicator(
            progress = animatedProgressValue,
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .padding(bottom = 12.dp)
        )
        progressValues.forEach { progressValue ->
            LinearProgressIndicator(
                progress = progressValue,
                modifier = Modifier
                    .padding(horizontal = 15.dp) // Increased from 12.dp.
                    .padding(bottom = 12.dp)
            )
        }
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            CircularProgressIndicator(
                progress = animatedProgressValue
            )
            progressValues.forEach { progressValue ->
                CircularProgressIndicator(
                    progress = progressValue
                )
            }
            LoadingView()
        }
    }

    item(key = "textField") {
        var text1 by remember { mutableStateOf("") }
        var text2 by remember { mutableStateOf("") }
        var text3 by remember { mutableStateOf("") }
        var text4 by remember { mutableStateOf("") }
        var text5 by remember { mutableStateOf("") }
        var isErrorChecked by remember { mutableStateOf(false) }

        SmallTitle(text = "TextField")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp)
        ) {
            // --- 示例 1: 默认 Line 模式，带 Placeholder ---
            // 这个示例现在正确地只使用 placeholder，没有 label
            TextField(
                value = text1,
                onValueChange = { text1 = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
                placeholder = { Text("Line Mode with Placeholder") },
                trailingIcon = {
                    if (text1.isNotEmpty()) {
                        CouiClearButton(onClick = { text1 = "" })
                    }
                },
                mode = CouiTextFieldMode.Line,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { /* focusManager.clearFocus() */ })
            )

            // --- 示例 2: Rect 模式，带 Label 和 Leading Icon ---
            TextField(
                value = text2,
                onValueChange = { text2 = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
                label = { Text("Rect Mode with Label") },
                leadingIcon = {
                    Icon(
                        imageVector = MiuixIcons.Useful.Scan,
                        contentDescription = "User"
                    )
                },
                mode = CouiTextFieldMode.Rect,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { /* focusManager.clearFocus() */ })
            )

            // --- [修正] 示例 3: Line 模式，只带 Label ---
            // 演示 Label 的浮动效果
            TextField(
                value = text3,
                onValueChange = { text3 = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
                label = { Text("Floating Label only") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { /* focusManager.clearFocus() */ })
            )

            // --- [新增] 示例 4: Rect 模式，只带 Placeholder ---
            // 演示无 Label 时的 Rect 模式
            TextField(
                value = text4,
                onValueChange = { text4 = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
                placeholder = { Text("Rect Mode with Placeholder only") },
                mode = CouiTextFieldMode.Rect,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { /* focusManager.clearFocus() */ })
            )


            // --- 示例 5: 错误状态演示 (带交互) ---
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                TextField(
                    value = text5,
                    onValueChange = { text5 = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    label = { Text("Error State Demo") },
                    isError = isErrorChecked, // 由 Checkbox 控制
                    trailingIcon = {
                        if (text5.isNotEmpty()) {
                            CouiClearButton(onClick = { text5 = "" })
                        }
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { isErrorChecked = !isErrorChecked }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = isErrorChecked,
                        onCheckedChange = { isErrorChecked = it }
                    )
                    Text("Toggle Error State", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }

    item(key = "slider") {
        SmallTitle(text = "Slider")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp)
        ) {
            var sliderValue by remember { mutableStateOf(0.3f) }
            Text(
                text = "Normal: ${(sliderValue * 100).toInt()}%",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp, bottom = 4.dp)
            )
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
            )
            var stepsValue by remember { mutableStateOf(5f) }
            Text(
                text = "Steps: ${stepsValue.toInt()}/8",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 4.dp)
            )
            Slider(
                value = stepsValue,
                onValueChange = { stepsValue = it },
                valueRange = 0f..8f,
                steps = 7,
                hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
            )
            var stepsWithKeyPointsValue by remember { mutableStateOf(5f) }
            Text(
                text = "Steps with Key Points: ${stepsWithKeyPointsValue.toInt()}/8",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 4.dp)
            )
            Slider(
                value = stepsWithKeyPointsValue,
                onValueChange = { stepsWithKeyPointsValue = it },
                valueRange = 0f..8f,
                steps = 7,
                hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                showKeyPoints = true,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
            )
            var customKeyPointsValue by remember { mutableStateOf(25f) }
            Text(
                text = "Custom Key Points: ${customKeyPointsValue.toInt()}%",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 4.dp)
            )
            Slider(
                value = customKeyPointsValue,
                onValueChange = { customKeyPointsValue = it },
                valueRange = 0f..100f,
                showKeyPoints = true,
                hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                keyPoints = listOf(0f, 25f, 50f, 75f, 100f),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
            )
            val disabledValue by remember { mutableStateOf(0.7f) }
            Text(
                text = "Disabled: ${(disabledValue * 100).toInt()}%",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 4.dp)
            )
            Slider(
                value = disabledValue,
                onValueChange = {},
                enabled = false,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
            )
        }

        // RangeSlider
        SmallTitle(text = "RangeSlider")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp)
        ) {
            var rangeValue by remember { mutableStateOf(0.2f..0.8f) }
            Text(
                text = "Range: ${(rangeValue.start * 100).toInt()}% - ${(rangeValue.endInclusive * 100).toInt()}%",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp, bottom = 4.dp)
            )
            RangeSlider(
                value = rangeValue,
                onValueChange = { rangeValue = it },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
            )
            var rangeStepsValue by remember { mutableStateOf(2f..8f) }
            Text(
                text = "Range with Key Points: ${rangeStepsValue.start.toInt()} - ${rangeStepsValue.endInclusive.toInt()}",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 4.dp)
            )
            RangeSlider(
                value = rangeStepsValue,
                onValueChange = { rangeStepsValue = it },
                valueRange = 0f..8f,
                steps = 7,
                hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                showKeyPoints = true,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
            )
            var customRangeValue by remember { mutableStateOf(20f..80f) }
            Text(
                text = "Custom Range Points: ${customRangeValue.start.toInt()}% - ${customRangeValue.endInclusive.toInt()}%",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 4.dp)
            )
            RangeSlider(
                value = customRangeValue,
                onValueChange = { customRangeValue = it },
                valueRange = 0f..100f,
                showKeyPoints = true,
                hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                keyPoints = listOf(0f, 20f, 40f, 60f, 80f, 100f),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
            )
            var disabledRangeValue by remember { mutableStateOf(0.3f..0.7f) }
            Text(
                text = "Disabled: ${(disabledRangeValue.start * 100).toInt()}% - ${(disabledRangeValue.endInclusive * 100).toInt()}%",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 4.dp)
            )
            RangeSlider(
                value = disabledRangeValue,
                onValueChange = {},
                enabled = false,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
            )
        }

        // VerticalSlider
        SmallTitle(text = "VerticalSlider")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                var verticalValue1 by remember { mutableStateOf(0.3f) }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    VerticalSlider(
                        value = verticalValue1,
                        onValueChange = { verticalValue1 = it },
                        modifier = Modifier.size(25.dp, 160.dp)
                    )
                    Text(
                        text = "Normal\n${(verticalValue1 * 100).toInt()}%",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                var verticalValue2 by remember { mutableStateOf(5f) }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    VerticalSlider(
                        value = verticalValue2,
                        onValueChange = { verticalValue2 = it },
                        valueRange = 0f..6f,
                        steps = 5,
                        hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                        modifier = Modifier.size(25.dp, 160.dp)
                    )
                    Text(
                        text = "Steps\n${verticalValue2.toInt()}/6",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                var verticalValue3 by remember { mutableStateOf(5f) }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    VerticalSlider(
                        value = verticalValue3,
                        onValueChange = { verticalValue3 = it },
                        valueRange = 0f..6f,
                        steps = 5,
                        hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                        showKeyPoints = true,
                        modifier = Modifier.size(25.dp, 160.dp)
                    )
                    Text(
                        text = "Points\n${verticalValue3.toInt()}/6",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                var verticalValue4 by remember { mutableStateOf(50f) }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    VerticalSlider(
                        value = verticalValue4,
                        onValueChange = { verticalValue4 = it },
                        valueRange = 0f..100f,
                        showKeyPoints = true,
                        hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                        keyPoints = listOf(0f, 25f, 50f, 75f, 100f),
                        modifier = Modifier.size(25.dp, 160.dp)
                    )
                    Text(
                        text = "Custom\n${verticalValue4.toInt()}%",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                val disabledVerticalValue by remember { mutableStateOf(0.7f) }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    VerticalSlider(
                        value = disabledVerticalValue,
                        onValueChange = {},
                        enabled = false,
                        modifier = Modifier.size(25.dp, 160.dp)
                    )
                    Text(
                        text = "Disabled\n${(disabledVerticalValue * 100).toInt()}%",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    item(key = "tabRow") {
        SmallTitle(text = "TabRow")
        val tabTexts = remember { listOf("Tab 1", "Tab 2", "Tab 3") }
        val tabTexts1 = remember { listOf("Tab 1", "Tab 2", "Tab 3", "Tab 4", "Tab 5", "Tab 6") }
        var selectedTabIndex by remember { mutableStateOf(0) }
        var selectedTabIndex1 by remember { mutableStateOf(0) }
        TabRow(
            tabs = tabTexts,
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp)
        ) {
            selectedTabIndex = it
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            insideMargin = PaddingValues(16.dp)
        ) {
            TabRowWithContour(
                tabs = tabTexts1,
                selectedTabIndex = selectedTabIndex1,
            ) {
                selectedTabIndex1 = it
            }
            val selectedTabText by remember(selectedTabIndex1) {
                derivedStateOf { tabTexts1[selectedTabIndex1] }
            }
            Text(
                text = "Selected Tab: $selectedTabText",
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }

    item(key = "icon") {
        SmallTitle(text = "Icon")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            insideMargin = PaddingValues(16.dp)
        ) {
            FlowRow {
                miuixIcons.forEach { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (icon != MiuixIcons.Useful.Like) COUITheme.colorScheme.onBackground else Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    item(key = "colorPicker-HSV") {
        SmallTitle(text = "ColorPicker (HSV)")
        val miuixColor = COUITheme.colorScheme.primary
        var selectedColor by remember { mutableStateOf(miuixColor) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            insideMargin = PaddingValues(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HEX: #${selectedColor.toArgb().toHexString(HexFormat.UpperCase)}" +
                            "\nRGBA: ${(selectedColor.red * 255).toInt()}, " +
                            "${(selectedColor.green * 255).toInt()}, " +
                            "${(selectedColor.blue * 255).toInt()}, " +
                            "${(round(selectedColor.alpha * 100) / 100.0)}",
                    modifier = Modifier.weight(1f)
                )
            }
            ColorPicker(
                initialColor = selectedColor,
                onColorChanged = { selectedColor = it },
                showPreview = false
            )
        }
    }

    item(key = "colorPicker-OKHSV") {
        SmallTitle(text = "ColorPicker (OKHSV)")
        val miuixColor = COUITheme.colorScheme.primary
        var selectedColor by remember { mutableStateOf(miuixColor) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),

            insideMargin = PaddingValues(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HEX: #${selectedColor.toArgb().toHexString(HexFormat.UpperCase)}" +
                            "\nRGBA: ${(selectedColor.red * 255).toInt()}, " +
                            "${(selectedColor.green * 255).toInt()}, " +
                            "${(selectedColor.blue * 255).toInt()}, " +
                            "${(round(selectedColor.alpha * 100) / 100.0)}",
                    modifier = Modifier.weight(1f)
                )
            }
            ColorPicker(
                initialColor = selectedColor,
                onColorChanged = { selectedColor = it },
                colorSpace = ColorSpace.OKHSV,
                showPreview = false
            )
        }
    }

    item(key = "colorPicker-OKLAB") {
        SmallTitle(text = "ColorPicker (OKLAB)")
        val miuixColor = COUITheme.colorScheme.primary
        var selectedColor by remember { mutableStateOf(miuixColor) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            insideMargin = PaddingValues(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HEX: #${selectedColor.toArgb().toHexString(HexFormat.UpperCase)}" +
                            "\nRGBA: ${(selectedColor.red * 255).toInt()}, " +
                            "${(selectedColor.green * 255).toInt()}, " +
                            "${(selectedColor.blue * 255).toInt()}, " +
                            "${(round(selectedColor.alpha * 100) / 100.0)}",
                    modifier = Modifier.weight(1f)
                )
            }
            ColorPicker(
                initialColor = selectedColor,
                onColorChanged = { selectedColor = it },
                colorSpace = ColorSpace.OKLAB,
                showPreview = false
            )
        }
    }

    item(key = "colorPicker-OKLCH") {
        SmallTitle(text = "ColorPicker (OKLCH)")
        val miuixColor = COUITheme.colorScheme.primary
        var selectedColor by remember { mutableStateOf(miuixColor) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),

            insideMargin = PaddingValues(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HEX: #${selectedColor.toArgb().toHexString(HexFormat.UpperCase)}" +
                            "\nRGBA: ${(selectedColor.red * 255).toInt()}, " +
                            "${(selectedColor.green * 255).toInt()}, " +
                            "${(selectedColor.blue * 255).toInt()}, " +
                            "${(round(selectedColor.alpha * 100) / 100.0)}",
                    modifier = Modifier.weight(1f)
                )
            }
            ColorPicker(
                initialColor = selectedColor,
                onColorChanged = { selectedColor = it },
                colorSpace = ColorSpace.OKLCH,
                showPreview = false
            )
        }
    }

    item(key = "colorPalette") {
        SmallTitle(text = "ColorPalette")
        val miuixColor = COUITheme.colorScheme.primary
        var selectedColor by remember { mutableStateOf(miuixColor) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            insideMargin = PaddingValues(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HEX: #${selectedColor.toArgb().toHexString(HexFormat.UpperCase)}" +
                            "\nRGBA: ${(selectedColor.red * 255).toInt()}, " +
                            "${(selectedColor.green * 255).toInt()}, " +
                            "${(selectedColor.blue * 255).toInt()}, " +
                            "${(round(selectedColor.alpha * 100) / 100.0)}",
                    modifier = Modifier.weight(1f)
                )
            }
            ColorPalette(
                initialColor = selectedColor,
                onColorChanged = { selectedColor = it },
                showPreview = false
            )
        }
    }

    item(key = "card") {
        SmallTitle(text = "Card")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            colors = CardDefaults.defaultColors(
                color = COUITheme.colorScheme.primaryVariant
            ),
            insideMargin = PaddingValues(16.dp),
            pressFeedbackType = PressFeedbackType.None,
            showIndication = true
        ) {
            Text(
                color = COUITheme.colorScheme.onPrimary,
                text = "Card",
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                color = COUITheme.colorScheme.onPrimaryVariant,
                text = "ShowIndication: true",
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp + padding.calculateBottomPadding()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                insideMargin = PaddingValues(16.dp),
                pressFeedbackType = PressFeedbackType.Sink,
                showIndication = true,
                onClick = { println("Card click") },
                content = {
                    Text(
                        color = COUITheme.colorScheme.onSurface,
                        text = "Card",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        color = COUITheme.colorScheme.onSurfaceVariantSummary,
                        text = "PressFeedback\nType: Sink",
                        style = COUITheme.textStyles.body
                    )
                }
            )
            Card(
                modifier = Modifier.weight(1f),
                insideMargin = PaddingValues(16.dp),
                pressFeedbackType = PressFeedbackType.Tilt,
                onLongPress = { println("Card long press") },
                content = {
                    Text(
                        color = COUITheme.colorScheme.onSurface,
                        text = "Card",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        color = COUITheme.colorScheme.onSurfaceVariantSummary,
                        text = "PressFeedback\nType: Tilt",
                        style = COUITheme.textStyles.body
                    )
                }
            )
        }
    }
}
