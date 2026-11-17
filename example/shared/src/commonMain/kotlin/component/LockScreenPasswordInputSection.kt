// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.LockScreenPasswordInput
import io.github.suqi8.coui.kmp.basic.LockScreenPasswordInputMode
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun LazyListScope.lockScreenPasswordInputSection() {
    item(key = "lockScreenPasswordInput") {
        SmallTitle(text = "LockScreenPasswordInput")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            // Settings mode with the 6-16 length rule: the arrow activates at 6 digits. The field
            // and the button are card-colored, so they sit on the page surface.
            var password by remember { mutableStateOf("") }
            var error by remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(COUITheme.colorScheme.backgroundWithCard)
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
                // showError / closeError: the error fades in over 217ms and out over 283ms.
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(text = "Show error", onClick = { error = "Incorrect password" }, modifier = Modifier.weight(1f))
                    TextButton(text = "Clear error", onClick = { error = "" }, modifier = Modifier.weight(1f))
                }
            }
        }
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            // Desktop (lock screen) mode over a wallpaper: press the arrow to see the light, or
            // replay it with the button (it only runs while the arrow is active, so the field is
            // filled first).
            var password by remember { mutableStateOf("") }
            val nextInteraction = remember { MutableInteractionSource() }
            val scope = rememberCoroutineScope()
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
                    nextButtonInteractionSource = nextInteraction,
                )
                TextButton(
                    text = "Play press animation",
                    onClick = {
                        if (password.isEmpty()) password = "1234"
                        scope.launch {
                            val press = PressInteraction.Press(Offset.Zero)
                            nextInteraction.emit(press)
                            delay(400)
                            nextInteraction.emit(PressInteraction.Release(press))
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
        }
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            // Disabled field.
            LockScreenPasswordInput(
                value = "",
                onValueChange = {},
                onNextClick = {},
                enabled = false,
                label = "Disabled",
                modifier = Modifier
                    .background(COUITheme.colorScheme.backgroundWithCard)
                    .padding(16.dp),
            )
        }
    }
}
