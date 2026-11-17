// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.InputView
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_clear_button
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_error_state
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_max_count
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_password_toggle
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_read_only
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_show_count
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_single_line
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_view_error_message
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_view_nickname
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_view_nickname_error
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_view_nickname_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_view_password_error
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_view_password_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_view_signature_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_view_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_view_variant_multi
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_view_variant_single
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_input_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_name
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_password
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

internal val InputViewDemo = Demo(
    id = "input_view",
    apiName = "InputView",
    title = Res.string.demo_input_view_title,
    category = DemoCategory.Input,
    page = DemoPage.Variants { inputViewPage() },
    keywords = listOf("输入框", "COUICardSingleInputView", "COUICardMultiInputView"),
)

/** The minimum password length of the password sample. */
private const val MIN_PASSWORD_LENGTH = 8

private fun LazyListScope.inputViewPage() {
    playground {
        val focusManager = LocalFocusManager.current
        var text by rememberSaveable { mutableStateOf("") }
        var showTitle by rememberSaveable { mutableStateOf(true) }
        var showCount by rememberSaveable { mutableStateOf(true) }
        var maxCount by rememberSaveable { mutableFloatStateOf(20f) }
        var clearButton by rememberSaveable { mutableStateOf(true) }
        var passwordToggle by rememberSaveable { mutableStateOf(false) }
        var singleLine by rememberSaveable { mutableStateOf(true) }
        var isError by rememberSaveable { mutableStateOf(false) }
        var readOnly by rememberSaveable { mutableStateOf(false) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        val count = maxCount.roundToInt()

        // The input view draws its own card: preview it on the page background.
        PlaygroundPreview(inCard = false, contentPadding = PaddingValues(horizontal = 16.dp)) {
            InputView(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                title = if (showTitle) stringResource(Res.string.sample_name) else "",
                label = stringResource(Res.string.sample_input_hint),
                enabled = enabled,
                readOnly = readOnly,
                showCount = showCount,
                maxCount = count,
                showClearButton = clearButton,
                showPasswordToggle = passwordToggle,
                errorMessage = if (isError) stringResource(Res.string.demo_input_view_error_message) else "",
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (passwordToggle) KeyboardType.Password else KeyboardType.Text,
                    imeAction = if (singleLine) ImeAction.Done else ImeAction.Default,
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                singleLine = singleLine,
            )
        }
        PlaygroundControls {
            item {
                SwitchPreference(
                    checked = showTitle,
                    onCheckedChange = { showTitle = it },
                    title = stringResource(Res.string.common_show_title),
                )
            }
            item {
                SwitchPreference(
                    checked = showCount,
                    onCheckedChange = { showCount = it },
                    title = stringResource(Res.string.demo_input_show_count),
                )
            }
            item {
                SliderPreference(
                    value = maxCount,
                    onValueChange = { maxCount = it },
                    title = stringResource(Res.string.demo_input_max_count),
                    valueText = count.toString(),
                    valueRange = 5f..100f,
                    steps = 18,
                )
            }
            item {
                SwitchPreference(
                    checked = clearButton,
                    onCheckedChange = { clearButton = it },
                    title = stringResource(Res.string.demo_input_clear_button),
                )
            }
            item {
                SwitchPreference(
                    checked = passwordToggle,
                    onCheckedChange = { passwordToggle = it },
                    title = stringResource(Res.string.demo_input_password_toggle),
                )
            }
            item {
                SwitchPreference(
                    checked = singleLine,
                    onCheckedChange = { singleLine = it },
                    title = stringResource(Res.string.demo_input_single_line),
                )
            }
            item {
                SwitchPreference(
                    checked = isError,
                    onCheckedChange = { isError = it },
                    title = stringResource(Res.string.demo_input_error_state),
                )
            }
            item {
                SwitchPreference(
                    checked = readOnly,
                    onCheckedChange = { readOnly = it },
                    title = stringResource(Res.string.demo_input_read_only),
                )
            }
            item {
                SwitchPreference(
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                    title = stringResource(Res.string.common_enabled),
                )
            }
        }
    }
    variant(key = "single", title = Res.string.demo_input_view_variant_single, inCard = false) {
        SingleInputs()
    }
    variant(key = "multi", title = Res.string.demo_input_view_variant_multi, inCard = false) {
        MultiInput()
    }
    variant(key = "disabled", title = Res.string.common_disabled, inCard = false) {
        InputView(
            value = "",
            onValueChange = {},
            modifier = Modifier.padding(horizontal = 16.dp),
            title = stringResource(Res.string.sample_name),
            label = stringResource(Res.string.common_disabled),
            enabled = false,
        )
    }
}

/**
 * COUICardSingleInputView: single line, title inside the card, counter / clear button at the end
 * and the error caption below the card.
 */
@Composable
private fun SingleInputs() {
    val focusManager = LocalFocusManager.current
    var nickname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        InputView(
            value = nickname,
            onValueChange = { nickname = it },
            modifier = Modifier.padding(bottom = 12.dp),
            title = stringResource(Res.string.demo_input_view_nickname),
            label = stringResource(Res.string.demo_input_view_nickname_hint),
            showCount = true,
            maxCount = 10,
            showClearButton = true,
            errorMessage = if (nickname.contains(' ')) stringResource(Res.string.demo_input_view_nickname_error) else "",
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        )
        InputView(
            value = password,
            onValueChange = { password = it },
            title = stringResource(Res.string.sample_password),
            label = stringResource(Res.string.demo_input_view_password_hint),
            showPasswordToggle = true,
            errorMessage = if (password.isNotEmpty() && password.length < MIN_PASSWORD_LENGTH) {
                stringResource(Res.string.demo_input_view_password_error)
            } else {
                ""
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        )
    }
}

/**
 * COUICardMultiInputView: multi line, counter at the bottom-end corner.
 */
@Composable
private fun MultiInput() {
    val focusManager = LocalFocusManager.current
    var signature by remember { mutableStateOf("") }
    InputView(
        value = signature,
        onValueChange = { signature = it },
        modifier = Modifier.padding(horizontal = 16.dp),
        label = stringResource(Res.string.demo_input_view_signature_hint),
        showCount = true,
        maxCount = 100,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        singleLine = false,
    )
}
