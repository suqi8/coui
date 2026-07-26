// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.CodeTextField
import io.github.suqi8.coui.kmp.basic.InputView
import io.github.suqi8.coui.kmp.basic.SmallTitle

fun LazyListScope.inputViewSection() {
    item(key = "inputView") {
        val focusManager = LocalFocusManager.current

        var nickname by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var signature by remember { mutableStateOf("") }

        // ColorOS card inputs: COUICardSingleInputView (single line, title inside the card,
        // counter / clear button at the end, error caption below the card) and
        // COUICardMultiInputView (multi line, counter at the bottom-end corner).
        SmallTitle(text = "InputView (card single input)")
        InputView(
            value = nickname,
            onValueChange = { nickname = it },
            title = "Nickname",
            label = "Enter your nickname",
            showCount = true,
            maxCount = 10,
            showClearButton = true,
            errorMessage = if (nickname.contains(' ')) "Nickname cannot contain spaces" else "",
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        )
        InputView(
            value = password,
            onValueChange = { password = it },
            title = "Password",
            label = "At least 8 characters",
            showPasswordToggle = true,
            errorMessage = if (password.isNotEmpty() && password.length < 8) "Password is too short" else "",
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        )
        SmallTitle(text = "InputView (card multi input)")
        InputView(
            value = signature,
            onValueChange = { signature = it },
            label = "Signature (multi line)",
            showCount = true,
            maxCount = 100,
            singleLine = false,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        )
    }
    item(key = "codeTextField") {
        val focusManager = LocalFocusManager.current

        var code by remember { mutableStateOf("") }
        var pin by remember { mutableStateOf("") }

        SmallTitle(text = "CodeTextField")
        CodeTextField(
            value = code,
            onValueChange = { code = it.filter(Char::isDigit) },
            onComplete = { focusManager.clearFocus() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
        )
        SmallTitle(text = "CodeTextField (security)")
        CodeTextField(
            value = pin,
            onValueChange = { pin = it.filter(Char::isDigit) },
            cellCount = 4,
            security = true,
            onComplete = { focusManager.clearFocus() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        )
    }
}
