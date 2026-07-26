// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.ButtonDefaults
import com.suqi8.coui.kmp.basic.SmallTitle
import com.suqi8.coui.kmp.basic.Stepper
import com.suqi8.coui.kmp.basic.Text
import com.suqi8.coui.kmp.basic.TextButton

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
        var stepperValue by remember { mutableIntStateOf(2) }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
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
