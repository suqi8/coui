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
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.InputView

@Composable
fun InputViewDemo() {
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
            var nickname by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var signature by remember { mutableStateOf("") }

            InputView(
                value = nickname,
                onValueChange = { nickname = it },
                modifier = Modifier.fillMaxWidth(),
                title = "Nickname",
                label = "Enter your nickname",
                showCount = true,
                maxCount = 10,
                showClearButton = true,
                errorMessage = if (nickname.contains(' ')) "Nickname cannot contain spaces" else "",
            )
            InputView(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = "Password (at least 8 characters)",
                showPasswordToggle = true,
                errorMessage = if (password.isNotEmpty() && password.length < 8) "Password is too short" else "",
            )
            InputView(
                value = signature,
                onValueChange = { signature = it },
                modifier = Modifier.fillMaxWidth(),
                label = "Signature",
                showCount = true,
                maxCount = 100,
                singleLine = false,
            )
            InputView(
                value = "Read only value",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                title = "Account",
                enabled = false,
            )
        }
    }
}
