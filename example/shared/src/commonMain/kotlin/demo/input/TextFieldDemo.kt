// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import io.github.suqi8.coui.kmp.basic.TextField
import io.github.suqi8.coui.kmp.basic.TextFieldMode
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_mode
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_clear_button
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_error_state
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_password_toggle
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_read_only
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_single_line
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_text_field_device_name
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_text_field_digits_only
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_text_field_floating_label
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_text_field_floating_line
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_text_field_floating_rectangle
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_text_field_focus_line
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_text_field_focus_line_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_text_field_max_10
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_text_field_no_decoration
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_text_field_password
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_text_field_rectangle
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_text_field_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_text_field_state_based
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_text_field_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_text_field_variant_card
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_text_field_variant_line
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_text_field_variant_rectangle
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_input_hint
import org.jetbrains.compose.resources.stringResource

/** The demo page of `TextField`, listed in its category and found by the search. */
internal val TextFieldDemo = Demo(
    id = "text_field",
    apiName = "TextField",
    title = Res.string.demo_text_field_title,
    category = DemoCategory.Input,
    page = DemoPage.Variants { textFieldPage() },
    keywords = listOf("COUIEditText"),
    searchTerms = Res.string.demo_text_field_search_terms,
)

/** The character limit of the "max count" fields. */
private const val MAX_COUNT = 10

private fun LazyListScope.textFieldPage() {
    playground {
        val focusManager = LocalFocusManager.current
        var text by rememberSaveable { mutableStateOf("") }
        var modeIndex by rememberSaveable { mutableIntStateOf(TextFieldMode.Line.ordinal) }
        var floatingLabel by rememberSaveable { mutableStateOf(false) }
        var focusLineOnly by rememberSaveable { mutableStateOf(false) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        var readOnly by rememberSaveable { mutableStateOf(false) }
        var isError by rememberSaveable { mutableStateOf(false) }
        var limit by rememberSaveable { mutableStateOf(false) }
        var clearButton by rememberSaveable { mutableStateOf(true) }
        var passwordToggle by rememberSaveable { mutableStateOf(false) }
        var singleLine by rememberSaveable { mutableStateOf(true) }
        val mode = TextFieldMode.entries[modeIndex]

        PlaygroundPreview {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                backgroundMode = mode,
                label = stringResource(Res.string.sample_input_hint),
                useLabelAsPlaceholder = !floatingLabel,
                justShowFocusLine = focusLineOnly,
                enabled = enabled,
                readOnly = readOnly,
                isError = isError,
                maxCount = if (limit) MAX_COUNT else null,
                showClearButton = clearButton,
                showPasswordToggle = passwordToggle,
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
                OverlayDropdownPreference(
                    items = TextFieldMode.entries.map { it.name },
                    selectedIndex = modeIndex,
                    title = stringResource(Res.string.common_mode),
                    onSelectedIndexChange = { modeIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = floatingLabel,
                    onCheckedChange = { floatingLabel = it },
                    title = stringResource(Res.string.demo_text_field_floating_label),
                )
            }
            item {
                SwitchPreference(
                    checked = focusLineOnly,
                    onCheckedChange = { focusLineOnly = it },
                    title = stringResource(Res.string.demo_text_field_focus_line),
                    summary = stringResource(Res.string.demo_text_field_focus_line_summary),
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
                    checked = limit,
                    onCheckedChange = { limit = it },
                    title = stringResource(Res.string.demo_text_field_max_10),
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
    variant(key = "line", title = Res.string.demo_text_field_variant_line) {
        LineFields()
    }
    variant(key = "card", title = Res.string.demo_text_field_variant_card) {
        CardFields()
    }
    variant(key = "rectangle", title = Res.string.demo_text_field_variant_rectangle) {
        RectangleFields()
    }
}

/**
 * The ColorOS Settings dialog form: Line mode with a plain placeholder and a clear button
 * (`?couiEditTextLineHintDisableStyle` + quickDelete), plus the password, counter, error and
 * disabled states.
 */
@Composable
private fun LineFields() {
    val focusManager = LocalFocusManager.current
    val done = KeyboardOptions(imeAction = ImeAction.Done)
    val clearFocus = KeyboardActions(onDone = { focusManager.clearFocus() })

    var dialogText by remember { mutableStateOf("") }
    val stateText = rememberTextFieldState(initialText = "")
    var passwordText by remember { mutableStateOf("") }
    var countText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        TextField(
            value = dialogText,
            onValueChange = { dialogText = it },
            modifier = Modifier.padding(bottom = 12.dp),
            label = stringResource(Res.string.demo_text_field_device_name),
            showClearButton = true,
            keyboardOptions = done,
            keyboardActions = clearFocus,
            singleLine = true,
        )
        TextField(
            state = stateText,
            modifier = Modifier.padding(bottom = 12.dp),
            label = stringResource(Res.string.demo_text_field_state_based),
            keyboardOptions = done,
            onKeyboardAction = { focusManager.clearFocus() },
        )
        TextField(
            value = passwordText,
            onValueChange = { passwordText = it },
            modifier = Modifier.padding(bottom = 12.dp),
            label = stringResource(Res.string.demo_text_field_password),
            showPasswordToggle = true,
            initialPasswordVisible = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = clearFocus,
            singleLine = true,
        )
        TextField(
            value = countText,
            onValueChange = { countText = it },
            modifier = Modifier.padding(bottom = 12.dp),
            label = stringResource(Res.string.demo_text_field_max_10),
            maxCount = MAX_COUNT,
            keyboardOptions = done,
            keyboardActions = clearFocus,
            singleLine = true,
        )
        TextField(
            value = errorText,
            onValueChange = { errorText = it },
            modifier = Modifier.padding(bottom = 12.dp),
            label = stringResource(Res.string.demo_text_field_digits_only),
            isError = errorText.isNotEmpty() && !errorText.all { it.isDigit() },
            keyboardOptions = done,
            keyboardActions = clearFocus,
            singleLine = true,
        )
        // Disabled: the underline switches to couiDisabledStrokeColor (#19000000 / #19FFFFFF).
        TextField(
            value = "",
            onValueChange = {},
            label = stringResource(Res.string.common_disabled),
            enabled = false,
            singleLine = true,
        )
    }
}

/**
 * The ColorOS Settings card-preference form: bare text on the card (COUIInputPreference,
 * `couiJustShowFocusLine=true`: the focused line expands under the text; None mode never shows a
 * line).
 */
@Composable
private fun CardFields() {
    val focusManager = LocalFocusManager.current
    var focusLineText by remember { mutableStateOf("") }
    var cardText by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        TextField(
            value = focusLineText,
            onValueChange = { focusLineText = it },
            modifier = Modifier.padding(vertical = 6.dp),
            label = stringResource(Res.string.demo_text_field_focus_line),
            justShowFocusLine = true,
            // Widget.COUI.Input has no 62dp EditText minimum height.
            minHeight = 0.dp,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            singleLine = true,
        )
        TextField(
            value = cardText,
            onValueChange = { cardText = it },
            modifier = Modifier.padding(vertical = 6.dp),
            backgroundMode = TextFieldMode.None,
            label = stringResource(Res.string.demo_text_field_no_decoration),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            singleLine = true,
        )
    }
}

/**
 * The floating label (`couiHintEnabled`) on a line and the stroke-only Rectangle mode.
 */
@Composable
private fun RectangleFields() {
    val focusManager = LocalFocusManager.current
    val done = KeyboardOptions(imeAction = ImeAction.Done)
    val clearFocus = KeyboardActions(onDone = { focusManager.clearFocus() })
    var floatingLineText by remember { mutableStateOf("") }
    var rectText by remember { mutableStateOf("") }
    var floatingText by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = floatingLineText,
            onValueChange = { floatingLineText = it },
            modifier = Modifier.padding(bottom = 12.dp),
            label = stringResource(Res.string.demo_text_field_floating_line),
            useLabelAsPlaceholder = false,
            keyboardOptions = done,
            keyboardActions = clearFocus,
            singleLine = true,
        )
        TextField(
            value = rectText,
            onValueChange = { rectText = it },
            modifier = Modifier.padding(bottom = 12.dp),
            backgroundMode = TextFieldMode.Rectangle,
            label = stringResource(Res.string.demo_text_field_rectangle),
            keyboardOptions = done,
            keyboardActions = clearFocus,
            singleLine = true,
        )
        TextField(
            value = floatingText,
            onValueChange = { floatingText = it },
            backgroundMode = TextFieldMode.Rectangle,
            label = stringResource(Res.string.demo_text_field_floating_rectangle),
            useLabelAsPlaceholder = false,
            keyboardOptions = done,
            keyboardActions = clearFocus,
            singleLine = true,
        )
    }
}
