// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.ButtonDefaults
import io.github.suqi8.coui.kmp.basic.ButtonMaterial
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Stepper
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.theme.COUITheme

fun LazyListScope.buttonSection() {
    item(key = "button") {
        var buttonText by remember { mutableStateOf("Cancel") }
        var submitButtonText by remember { mutableStateOf("Submit") }
        var clickCount by remember { mutableIntStateOf(0) }
        var submitClickCount by remember { mutableIntStateOf(0) }

        SmallTitle(text = "Button")
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(
                text = buttonText,
                onClick = {
                    clickCount++
                    buttonText = "Click: $clickCount"
                },
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                text = submitButtonText,
                onClick = {
                    submitClickCount++
                    submitButtonText = "Click: $submitClickCount"
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary(),
            )
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(
                text = "Disabled",
                onClick = {},
                modifier = Modifier.weight(1f),
                enabled = false,
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                text = "Disabled",
                onClick = {},
                enabled = false,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary(),
            )
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(
                text = "Borderless",
                onClick = {},
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsBorderless(),
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                text = "Disabled",
                onClick = {},
                enabled = false,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsBorderless(),
            )
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // COUI 17 material layer (edge lighting + touch spotlight); off by default on ColorOS 17.
            TextButton(
                text = "Material",
                onClick = {},
                modifier = Modifier.weight(1f),
                material = ButtonMaterial.Secondary,
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                text = "Material",
                onClick = {},
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                material = ButtonMaterial.Primary,
            )
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(COUITheme.colorScheme.primary)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Small size tier, and the transparent type laid over a colored backdrop.
            TextButton(
                text = "Small",
                onClick = {},
                minWidth = ButtonDefaults.MinWidthSmall,
                minHeight = ButtonDefaults.MinHeightSmall,
                insideMargin = ButtonDefaults.InsideMarginSmall,
                textStyle = ButtonDefaults.textStyleSmall(),
            )
            Spacer(Modifier.width(12.dp))
            TextButton(
                text = "Transparent",
                onClick = {},
                minWidth = ButtonDefaults.MinWidthSmall,
                minHeight = ButtonDefaults.MinHeightSmall,
                insideMargin = ButtonDefaults.InsideMarginSmall,
                textStyle = ButtonDefaults.textStyleSmall(),
                colors = ButtonDefaults.textButtonColorsTransparent(),
                material = ButtonMaterial.Transparent,
            )
        }
        var stepperValue by remember { mutableIntStateOf(2) }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Stepper")
            Stepper(
                value = stepperValue,
                onValueChange = { stepperValue = it },
                minValue = 0,
                maxValue = 10,
            )
        }
    }
}
