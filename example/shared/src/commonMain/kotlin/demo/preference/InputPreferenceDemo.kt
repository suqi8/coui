// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.preference

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.preference.EditTextPreference
import io.github.suqi8.coui.kmp.preference.InputPreference
import io.github.suqi8.coui.kmp.preference.StepperPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_cancel
import io.github.suqi8.coui.kmp.shared.generated.resources.common_confirm
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_title
import io.github.suqi8.coui.kmp.shared.generated.resources.common_steps
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_clear_button
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_copies
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_device_name
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_device_name_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_dialog_message
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_empty_allowed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_error
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_error_message
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_font_scale
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_font_scale_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_hotspot_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_locked
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_max_count
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_my_phone
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_nickname
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_nickname_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_no_spaces
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_password_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_password_toggle
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_range
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_read_only
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_show_value
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_signature
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_signature_message
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_preference_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_component
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_input_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_name
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_password
import org.jetbrains.compose.resources.stringResource

private val InputTypes = listOf("InputPreference", "EditTextPreference", "StepperPreference")
private val StepperRanges = listOf(0 to 10, 1 to 99, 50 to 200)
private val StepperSteps = listOf(1, 5, 10)

internal val InputPreferenceDemo = Demo(
    id = "input_preference",
    apiName = "InputPreference",
    title = Res.string.demo_input_preference_title,
    category = DemoCategory.Preference,
    page = DemoPage.Variants {
        playground {
            val state = rememberPreferenceRowState(showSummary = false)
            var type by rememberSaveable { mutableIntStateOf(0) }
            var showTitle by rememberSaveable { mutableStateOf(true) }
            var maxCount by rememberSaveable { mutableStateOf(false) }
            var clearButton by rememberSaveable { mutableStateOf(true) }
            var passwordToggle by rememberSaveable { mutableStateOf(false) }
            var error by rememberSaveable { mutableStateOf(false) }
            var readOnly by rememberSaveable { mutableStateOf(false) }
            var showValue by rememberSaveable { mutableStateOf(true) }
            var emptyAllowed by rememberSaveable { mutableStateOf(false) }
            var dialogMessage by rememberSaveable { mutableStateOf(false) }
            var rangeIndex by rememberSaveable { mutableIntStateOf(0) }
            var stepIndex by rememberSaveable { mutableIntStateOf(0) }
            var text by rememberSaveable { mutableStateOf("") }
            var number by rememberSaveable { mutableIntStateOf(5) }
            val (minValue, maxValue) = StepperRanges[rangeIndex]
            val title = state.title(Res.string.sample_name)
            PreferencePreview {
                item {
                    when (type) {
                        0 -> InputPreference(
                            value = text,
                            onValueChange = { text = it },
                            title = if (showTitle) title else null,
                            hint = stringResource(Res.string.sample_input_hint),
                            enabled = state.enabled,
                            readOnly = readOnly,
                            maxCount = if (maxCount) 20 else null,
                            showClearButton = clearButton,
                            showPasswordToggle = passwordToggle,
                            errorMessage = if (error) stringResource(Res.string.demo_input_preference_error_message) else "",
                        )

                        1 -> EditTextPreference(
                            value = text,
                            onValueChange = { text = it },
                            title = title,
                            positiveButtonText = stringResource(Res.string.common_confirm),
                            negativeButtonText = stringResource(Res.string.common_cancel),
                            summary = state.summary(),
                            dialogMessage = if (dialogMessage) stringResource(Res.string.demo_input_preference_signature_message) else null,
                            hint = stringResource(Res.string.sample_input_hint),
                            showValue = showValue,
                            supportEmptyInput = emptyAllowed,
                            startAction = if (state.showIcon) {
                                { PreferenceStartIcon() }
                            } else {
                                null
                            },
                            enabled = state.enabled,
                        )

                        else -> StepperPreference(
                            value = number.coerceIn(minValue, maxValue),
                            onValueChange = { number = it },
                            title = title,
                            summary = state.summary(),
                            minValue = minValue,
                            maxValue = maxValue,
                            step = StepperSteps[stepIndex],
                            startAction = if (state.showIcon) {
                                { PreferenceStartIcon() }
                            } else {
                                null
                            },
                            enabled = state.enabled,
                        )
                    }
                }
            }
            PlaygroundControls {
                literalChoiceControl(Res.string.demo_pref_component, InputTypes, type) { type = it }
                when (type) {
                    0 -> {
                        switchControl(Res.string.common_show_title, showTitle) { showTitle = it }
                        switchControl(Res.string.demo_input_preference_max_count, maxCount) { maxCount = it }
                        switchControl(Res.string.demo_input_preference_clear_button, clearButton) { clearButton = it }
                        switchControl(Res.string.demo_input_preference_password_toggle, passwordToggle) { passwordToggle = it }
                        switchControl(Res.string.demo_input_preference_error, error) { error = it }
                        switchControl(Res.string.demo_input_preference_read_only, readOnly) { readOnly = it }
                        switchControl(Res.string.common_enabled, state.enabled) { state.enabled = it }
                    }

                    1 -> {
                        switchControl(Res.string.demo_input_preference_show_value, showValue) { showValue = it }
                        switchControl(Res.string.demo_input_preference_empty_allowed, emptyAllowed) { emptyAllowed = it }
                        switchControl(Res.string.demo_input_preference_dialog_message, dialogMessage) { dialogMessage = it }
                        preferenceRowControls(state, endText = false)
                    }

                    else -> {
                        literalChoiceControl(
                            Res.string.demo_input_preference_range,
                            StepperRanges.map { (min, max) -> "$min – $max" },
                            rangeIndex,
                        ) { rangeIndex = it }
                        literalChoiceControl(
                            Res.string.common_steps,
                            StepperSteps.map { it.toString() },
                            stepIndex,
                        ) { stepIndex = it }
                        preferenceRowControls(state, endText = false)
                    }
                }
            }
        }
        variant(key = "input", title = "InputPreference") {
            InputRows()
        }
        variant(key = "editText", title = "EditTextPreference") {
            EditTextRows()
        }
        variant(key = "stepper", title = "StepperPreference") {
            StepperRows()
        }
    },
    keywords = listOf("设置", "输入", "input", "edit", "stepper", "步进"),
)

/**
 * Inline input rows: titled, counted with an error, and a password.
 */
@Composable
private fun InputRows() {
    val myPhone = stringResource(Res.string.demo_input_preference_my_phone)
    var deviceName by rememberSaveable { mutableStateOf<String?>(null) }
    var hotspotName by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val hotspotError = if (hotspotName.contains(' ')) stringResource(Res.string.demo_input_preference_no_spaces) else ""
    // InputPreference is not a BasicComponent, so the group draws no dividers between these rows.
    CardListGroup {
        item {
            InputPreference(
                value = deviceName ?: myPhone,
                onValueChange = { deviceName = it },
                title = stringResource(Res.string.demo_input_preference_device_name),
                hint = stringResource(Res.string.demo_input_preference_device_name_hint),
            )
        }
        item {
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            InputPreference(
                value = hotspotName,
                onValueChange = { hotspotName = it },
                hint = stringResource(Res.string.demo_input_preference_hotspot_hint),
                maxCount = 20,
                errorMessage = hotspotError,
            )
        }
        item {
            // COUIInputPreference.drawDivider: no divider below a row in error state.
            if (hotspotError.isEmpty()) HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            InputPreference(
                value = password,
                onValueChange = { password = it },
                title = stringResource(Res.string.sample_password),
                hint = stringResource(Res.string.demo_input_preference_password_hint),
                showPasswordToggle = true,
            )
        }
    }
}

/**
 * Rows that edit their value in a dialog.
 */
@Composable
private fun EditTextRows() {
    var nickname by rememberSaveable { mutableStateOf("Suqi") }
    var signature by rememberSaveable { mutableStateOf("") }
    val confirm = stringResource(Res.string.common_confirm)
    val cancel = stringResource(Res.string.common_cancel)
    CardListGroup {
        item {
            EditTextPreference(
                value = nickname,
                onValueChange = { nickname = it },
                title = stringResource(Res.string.demo_input_preference_nickname),
                positiveButtonText = confirm,
                negativeButtonText = cancel,
                hint = stringResource(Res.string.demo_input_preference_nickname_hint),
            )
        }
        item {
            EditTextPreference(
                value = signature,
                onValueChange = { signature = it },
                title = stringResource(Res.string.demo_input_preference_signature),
                positiveButtonText = confirm,
                negativeButtonText = cancel,
                summary = signature.ifEmpty { stringResource(Res.string.demo_input_preference_empty_allowed) },
                dialogMessage = stringResource(Res.string.demo_input_preference_signature_message),
                showValue = false,
                supportEmptyInput = true,
            )
        }
        item {
            EditTextPreference(
                value = stringResource(Res.string.demo_input_preference_locked),
                onValueChange = {},
                title = stringResource(Res.string.common_disabled),
                positiveButtonText = confirm,
                negativeButtonText = cancel,
                enabled = false,
            )
        }
    }
}

/**
 * Rows with a minus / plus stepper.
 */
@Composable
private fun StepperRows() {
    var copies by rememberSaveable { mutableIntStateOf(1) }
    var fontScale by rememberSaveable { mutableIntStateOf(100) }
    CardListGroup {
        item {
            StepperPreference(
                value = copies,
                onValueChange = { copies = it },
                title = stringResource(Res.string.demo_input_preference_copies),
                minValue = 1,
                maxValue = 10,
            )
        }
        item {
            StepperPreference(
                value = fontScale,
                onValueChange = { fontScale = it },
                title = stringResource(Res.string.demo_input_preference_font_scale),
                summary = stringResource(Res.string.demo_input_preference_font_scale_summary),
                minValue = 50,
                maxValue = 200,
                step = 10,
            )
        }
        item {
            StepperPreference(
                value = 3,
                onValueChange = {},
                title = stringResource(Res.string.common_disabled),
                enabled = false,
            )
        }
    }
}
