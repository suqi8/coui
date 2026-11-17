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
import io.github.suqi8.coui.kmp.basic.CodeTextField
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun CodeTextFieldDemo() {
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
            var code by remember { mutableStateOf("") }
            var pin by remember { mutableStateOf("") }

            Text(
                text = if (code.length == 6) "Code entered: $code" else "Enter the 6-digit code",
                color = COUITheme.colorScheme.labelSecondary,
                style = COUITheme.textStyles.bodyM,
            )
            CodeTextField(
                value = code,
                onValueChange = { code = it.filter(Char::isDigit) },
                modifier = Modifier.fillMaxWidth(),
            )
            CodeTextField(
                value = pin,
                onValueChange = { pin = it.filter(Char::isDigit) },
                modifier = Modifier.fillMaxWidth(),
                cellCount = 4,
                security = true,
            )
            CodeTextField(
                value = "2580",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                cellCount = 4,
                enabled = false,
            )
        }
    }
}
