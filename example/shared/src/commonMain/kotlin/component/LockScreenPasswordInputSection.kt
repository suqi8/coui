// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.LockScreenPasswordInput
import io.github.suqi8.coui.kmp.basic.LockScreenPasswordInputMode
import io.github.suqi8.coui.kmp.basic.SmallTitle

fun LazyListScope.lockScreenPasswordInputSection() {
    item(key = "lockScreenPasswordInput") {
        SmallTitle(text = "LockScreenPasswordInput")
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            // Settings mode with the 6-16 length rule: the arrow activates at 6 digits.
            var password by remember { mutableStateOf("") }
            var error by remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                LockScreenPasswordInput(
                    value = password,
                    onValueChange = {
                        password = it
                        error = ""
                    },
                    onNextClick = { error = if (it == "123456") "" else "Incorrect password" },
                    label = "Password (123456)",
                    errorMessage = error,
                    enableInputCount = true,
                )
            }
        }
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            // Desktop (lock screen) mode over a wallpaper: press the arrow to see the light.
            var password by remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(Color(0xFF5B247A), Color(0xFF1BCEDF))))
                    .padding(horizontal = 16.dp, vertical = 32.dp),
            ) {
                LockScreenPasswordInput(
                    value = password,
                    onValueChange = { password = it },
                    onNextClick = { password = "" },
                    mode = LockScreenPasswordInputMode.Desktop,
                    label = "Enter password",
                )
            }
        }
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            // Disabled field.
            LockScreenPasswordInput(
                value = "",
                onValueChange = {},
                onNextClick = {},
                enabled = false,
                label = "Disabled",
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
