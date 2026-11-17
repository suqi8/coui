// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.dialogs

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.overlay.OverlayInputDialog
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_cancel
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_cancelable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_cancelable_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_window_dim
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_counter
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_counter_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_open_password
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_open_password_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_open_rename
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_open_rename_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_password
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_password_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_password_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_password_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_rename
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_require_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_require_text_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_result
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_result_none
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_save
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_show
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_dialog_variant_input
import io.github.suqi8.coui.kmp.window.WindowInputDialog
import org.jetbrains.compose.resources.stringResource

internal val InputDialogDemo = Demo(
    id = "input_dialog",
    apiName = "OverlayInputDialog",
    title = Res.string.demo_input_dialog_title,
    category = DemoCategory.Dialogs,
    page = DemoPage.Variants {
        playground {
            InputDialogPlayground()
        }
        variant(key = "input", title = Res.string.demo_input_dialog_variant_input) {
            InputDialogsVariant()
        }
    },
    keywords = listOf("edit", "rename", "输入", "重命名", "弹窗"),
    isNew = true,
)

/** The character limit of the counter form (COUIAlertDialog.SingleInput). */
private const val MAX_COUNT = 20

@Composable
private fun ColumnScope.InputDialogPlayground() {
    var hostIndex by rememberSaveable { mutableIntStateOf(0) }
    var material by rememberSaveable { mutableStateOf(true) }
    var showSummary by rememberSaveable { mutableStateOf(false) }
    var counter by rememberSaveable { mutableStateOf(false) }
    var password by rememberSaveable { mutableStateOf(false) }
    var requireText by rememberSaveable { mutableStateOf(true) }
    var cancelable by rememberSaveable { mutableStateOf(true) }
    var windowDim by rememberSaveable { mutableStateOf(true) }
    var show by remember { mutableStateOf(false) }
    var value by rememberSaveable { mutableStateOf("") }
    var saved by rememberSaveable { mutableStateOf<String?>(null) }
    val window = hostIndex == HOST_WINDOW

    val dismiss = { show = false }
    val negative = DialogButtonBarAction(text = stringResource(Res.string.common_cancel), onClick = dismiss)
    val positive = DialogButtonBarAction(
        text = stringResource(Res.string.demo_input_dialog_save),
        enabled = !requireText || value.isNotEmpty(),
        onClick = {
            saved = value
            show = false
        },
    )
    val title = stringResource(Res.string.demo_input_dialog_rename)
    val summary = if (showSummary) stringResource(Res.string.demo_input_dialog_summary) else null
    val label = stringResource(Res.string.demo_input_dialog_hint)
    val maxCount = if (counter) MAX_COUNT else null
    val onDismissRequest = if (cancelable) dismiss else null
    val blur = demoPanelBlur(enabled = material && !window)

    PlaygroundPreview {
        DialogLaunchButton(text = stringResource(Res.string.demo_input_dialog_show), onClick = { show = true })
        DialogDemoCaption(text = if (window) "WindowInputDialog" else "OverlayInputDialog")
        DialogDemoCaption(
            text = saved?.let { stringResource(Res.string.demo_input_dialog_result, it) }
                ?: stringResource(Res.string.demo_input_dialog_result_none),
        )
    }
    PlaygroundControls {
        dialogHostRow(selectedIndex = hostIndex, onSelectedIndexChange = { hostIndex = it })
        dialogMaterialRow(checked = material, onCheckedChange = { material = it }, enabled = !window)
        dialogSwitchRow(
            title = Res.string.common_show_summary,
            checked = showSummary,
            onCheckedChange = { showSummary = it },
        )
        dialogSwitchRow(
            title = Res.string.demo_input_dialog_counter,
            checked = counter,
            onCheckedChange = { counter = it },
            summary = Res.string.demo_input_dialog_counter_summary,
        )
        dialogSwitchRow(
            title = Res.string.demo_input_dialog_password,
            checked = password,
            onCheckedChange = { password = it },
            summary = Res.string.demo_input_dialog_password_summary,
        )
        dialogSwitchRow(
            title = Res.string.demo_input_dialog_require_text,
            checked = requireText,
            onCheckedChange = { requireText = it },
            summary = Res.string.demo_input_dialog_require_text_summary,
        )
        dialogSwitchRow(
            title = Res.string.demo_dialog_cancelable,
            checked = cancelable,
            onCheckedChange = { cancelable = it },
            summary = Res.string.demo_dialog_cancelable_summary,
        )
        dialogSwitchRow(
            title = Res.string.demo_dialog_window_dim,
            checked = windowDim,
            onCheckedChange = { windowDim = it },
        )
    }

    if (window) {
        WindowInputDialog(
            show = show,
            value = value,
            onValueChange = { value = it },
            title = title,
            summary = summary,
            label = label,
            maxCount = maxCount,
            showPasswordToggle = password,
            negative = negative,
            positive = positive,
            enableWindowDim = windowDim,
            onDismissRequest = onDismissRequest,
        )
    } else {
        OverlayInputDialog(
            show = show,
            value = value,
            onValueChange = { value = it },
            backgroundModifier = blur?.backgroundModifier ?: Modifier,
            title = title,
            summary = summary,
            label = label,
            maxCount = maxCount,
            showPasswordToggle = password,
            negative = negative,
            positive = positive,
            backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
            enableWindowDim = windowDim,
            onDismissRequest = onDismissRequest,
            materialStroke = blur != null,
        )
    }
}

/**
 * The single-edit input dialog (COUIAlertDialog.SingleEdit), and the counter form
 * (COUIAlertDialog.SingleInput) with a masked password field.
 */
@Composable
private fun ColumnScope.InputDialogsVariant() {
    var showRename by remember { mutableStateOf(false) }
    var renameHoldDown by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var passwordHoldDown by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var secret by remember { mutableStateOf("") }
    CardListGroup {
        dialogLaunchRow(
            title = Res.string.demo_input_dialog_open_rename,
            summary = Res.string.demo_input_dialog_open_rename_summary,
            holdDown = renameHoldDown,
            onClick = {
                showRename = true
                renameHoldDown = true
            },
        )
        dialogLaunchRow(
            title = Res.string.demo_input_dialog_open_password,
            summary = Res.string.demo_input_dialog_open_password_summary,
            holdDown = passwordHoldDown,
            onClick = {
                showPassword = true
                passwordHoldDown = true
            },
        )
    }
    val blur = demoPanelBlur(enabled = true)
    val cancel = stringResource(Res.string.common_cancel)
    val save = stringResource(Res.string.demo_input_dialog_save)
    OverlayInputDialog(
        show = showRename,
        value = name,
        onValueChange = { name = it },
        title = stringResource(Res.string.demo_input_dialog_rename),
        label = stringResource(Res.string.demo_input_dialog_hint),
        negative = DialogButtonBarAction(text = cancel, onClick = { showRename = false }),
        positive = DialogButtonBarAction(text = save, enabled = name.isNotEmpty(), onClick = { showRename = false }),
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = { showRename = false },
        onDismissFinished = { renameHoldDown = false },
        materialStroke = blur != null,
    )
    OverlayInputDialog(
        show = showPassword,
        value = secret,
        onValueChange = { secret = it },
        title = stringResource(Res.string.demo_input_dialog_password_title),
        label = stringResource(Res.string.demo_input_dialog_password_hint),
        maxCount = MAX_COUNT,
        showPasswordToggle = true,
        negative = DialogButtonBarAction(text = cancel, onClick = { showPassword = false }),
        positive = DialogButtonBarAction(text = save, enabled = secret.isNotEmpty(), onClick = { showPassword = false }),
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = { showPassword = false },
        onDismissFinished = { passwordHoldDown = false },
        materialStroke = blur != null,
    )
}
