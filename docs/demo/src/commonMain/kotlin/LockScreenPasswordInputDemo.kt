// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.LockScreenPasswordInput
import io.github.suqi8.coui.kmp.basic.LockScreenPasswordInputMode

@Composable
fun LockScreenPasswordInputDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var password by remember { mutableStateOf("") }
            var error by remember { mutableStateOf("") }
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
            var desktop by remember { mutableStateOf("") }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(Color(0xFF5B247A), Color(0xFF1BCEDF))))
                    .padding(horizontal = 16.dp, vertical = 32.dp),
            ) {
                LockScreenPasswordInput(
                    value = desktop,
                    onValueChange = { desktop = it },
                    onNextClick = { desktop = "" },
                    mode = LockScreenPasswordInputMode.Desktop,
                    label = "Enter password",
                )
            }
        }
    }
}
