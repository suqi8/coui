// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.input

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.LocalDemoSnackbarHostState
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CodeTextField
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_code_text_field_completed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_code_text_field_security
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_code_text_field_security_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_code_text_field_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_code_text_field_variant_default
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_code_text_field_variant_security
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

internal val CodeTextFieldDemo = Demo(
    id = "code_text_field",
    apiName = "CodeTextField",
    title = Res.string.demo_code_text_field_title,
    category = DemoCategory.Input,
    page = DemoPage.Variants { codeTextFieldPage() },
    keywords = listOf("otp", "验证码", "COUICodeInputView"),
)

/** The cell counts offered by the playground. */
private val CellCounts = listOf(4, 5, 6)

private fun LazyListScope.codeTextFieldPage() {
    playground {
        val focusManager = LocalFocusManager.current
        val snackbarHostState = LocalDemoSnackbarHostState.current
        val scope = rememberCoroutineScope()
        var code by rememberSaveable { mutableStateOf("") }
        var cellCountIndex by rememberSaveable { mutableIntStateOf(CellCounts.lastIndex) }
        var security by rememberSaveable { mutableStateOf(false) }
        var enabled by rememberSaveable { mutableStateOf(true) }

        // The cells are card-coloured: preview them on the page background.
        PlaygroundPreview(inCard = false, contentPadding = PaddingValues(horizontal = 16.dp)) {
            CodeTextField(
                value = code,
                onValueChange = { code = it.filter(Char::isDigit) },
                modifier = Modifier.fillMaxWidth(),
                cellCount = CellCounts[cellCountIndex],
                enabled = enabled,
                security = security,
                onComplete = { entered ->
                    focusManager.clearFocus()
                    scope.launch {
                        snackbarHostState.showSnackbar(getString(Res.string.demo_code_text_field_completed, entered))
                    }
                },
            )
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = CellCounts.map { it.toString() },
                    selectedIndex = cellCountIndex,
                    title = stringResource(Res.string.common_count),
                    onSelectedIndexChange = {
                        cellCountIndex = it
                        code = code.take(CellCounts[it])
                    },
                )
            }
            item {
                SwitchPreference(
                    checked = security,
                    onCheckedChange = { security = it },
                    title = stringResource(Res.string.demo_code_text_field_security),
                    summary = stringResource(Res.string.demo_code_text_field_security_summary),
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
    variant(key = "default", title = Res.string.demo_code_text_field_variant_default, inCard = false) {
        val focusManager = LocalFocusManager.current
        var code by remember { mutableStateOf("") }
        CodeTextField(
            value = code,
            onValueChange = { code = it.filter(Char::isDigit) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onComplete = { focusManager.clearFocus() },
        )
    }
    variant(key = "security", title = Res.string.demo_code_text_field_variant_security, inCard = false) {
        val focusManager = LocalFocusManager.current
        var pin by remember { mutableStateOf("") }
        CodeTextField(
            value = pin,
            onValueChange = { pin = it.filter(Char::isDigit) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            cellCount = 4,
            security = true,
            onComplete = { focusManager.clearFocus() },
        )
    }
    variant(key = "disabled", title = Res.string.common_disabled, inCard = false) {
        CodeTextField(
            value = "12",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            enabled = false,
        )
    }
}
