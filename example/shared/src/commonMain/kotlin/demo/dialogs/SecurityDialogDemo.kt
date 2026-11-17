// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.dialogs

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.overlay.OverlaySecurityDialog
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_cancel
import io.github.suqi8.coui.kmp.shared.generated.resources.common_confirm
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_off
import io.github.suqi8.coui.kmp.shared.generated.resources.common_on
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_window_dim
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_checkbox
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_checkbox_initial
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_checkbox_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_long_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_notice_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_open_overlay
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_open_overlay_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_open_window
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_open_window_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_result_cancelled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_result_confirmed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_result_link
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_result_none
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_show
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_statement
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_statement_link
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_statement_row
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_security_dialog_variant_hosts
import io.github.suqi8.coui.kmp.window.WindowSecurityDialog
import org.jetbrains.compose.resources.stringResource

internal val SecurityDialogDemo = Demo(
    id = "security_dialog",
    apiName = "OverlaySecurityDialog",
    title = Res.string.demo_security_dialog_title,
    category = DemoCategory.Dialogs,
    page = DemoPage.Variants {
        playground {
            SecurityDialogPlayground()
        }
        variant(key = "hosts", title = Res.string.demo_security_dialog_variant_hosts) {
            SecurityDialogsVariant()
        }
    },
    keywords = listOf("permission", "privacy", "权限", "隐私", "弹窗"),
)

// The last action of a security dialog, kept as a code so its text follows the language.
private const val RESULT_NONE = 0
private const val RESULT_CONFIRMED = 1
private const val RESULT_CONFIRMED_DONT_REMIND = 2
private const val RESULT_CANCELLED = 3
private const val RESULT_LINK = 4

@Composable
private fun ColumnScope.SecurityDialogPlayground() {
    var hostIndex by rememberSaveable { mutableIntStateOf(0) }
    var material by rememberSaveable { mutableStateOf(true) }
    var showSummary by rememberSaveable { mutableStateOf(true) }
    var longSummary by rememberSaveable { mutableStateOf(false) }
    var showStatement by rememberSaveable { mutableStateOf(true) }
    var showCheckbox by rememberSaveable { mutableStateOf(true) }
    var initialChecked by rememberSaveable { mutableStateOf(false) }
    var windowDim by rememberSaveable { mutableStateOf(true) }
    var show by remember { mutableStateOf(false) }
    var result by rememberSaveable { mutableIntStateOf(RESULT_NONE) }
    val window = hostIndex == HOST_WINDOW

    PlaygroundPreview {
        DialogLaunchButton(text = stringResource(Res.string.demo_security_dialog_show), onClick = { show = true })
        DialogDemoCaption(text = if (window) "WindowSecurityDialog" else "OverlaySecurityDialog")
        DialogDemoCaption(text = securityResultText(result))
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
            title = Res.string.common_long_text,
            checked = longSummary,
            onCheckedChange = { longSummary = it },
            enabled = showSummary,
        )
        dialogSwitchRow(
            title = Res.string.demo_security_dialog_statement_row,
            checked = showStatement,
            onCheckedChange = { showStatement = it },
        )
        dialogSwitchRow(
            title = Res.string.demo_security_dialog_checkbox,
            checked = showCheckbox,
            onCheckedChange = { showCheckbox = it },
        )
        dialogSwitchRow(
            title = Res.string.demo_security_dialog_checkbox_initial,
            checked = initialChecked,
            onCheckedChange = { initialChecked = it },
            enabled = showCheckbox,
        )
        dialogSwitchRow(
            title = Res.string.demo_dialog_window_dim,
            checked = windowDim,
            onCheckedChange = { windowDim = it },
        )
    }

    SecurityDialog(
        show = show,
        window = window,
        material = material,
        summary = when {
            !showSummary -> null
            longSummary -> stringResource(Res.string.demo_security_dialog_long_summary)
            else -> stringResource(Res.string.demo_security_dialog_summary)
        },
        showStatement = showStatement,
        showCheckbox = showCheckbox,
        initialChecked = initialChecked,
        enableWindowDim = windowDim,
        onResult = { code ->
            result = code
            if (code != RESULT_LINK) show = false
        },
        onDismissFinished = null,
    )
}

@Composable
private fun securityResultText(result: Int): String = when (result) {
    RESULT_CONFIRMED -> stringResource(
        Res.string.demo_security_dialog_result_confirmed,
        stringResource(Res.string.common_off),
    )

    RESULT_CONFIRMED_DONT_REMIND -> stringResource(
        Res.string.demo_security_dialog_result_confirmed,
        stringResource(Res.string.common_on),
    )

    RESULT_CANCELLED -> stringResource(Res.string.demo_security_dialog_result_cancelled)

    RESULT_LINK -> stringResource(Res.string.demo_security_dialog_result_link)

    else -> stringResource(Res.string.demo_security_dialog_result_none)
}

/**
 * The security dialog on either host. The COUI 17 material blur only applies to the Overlay host.
 *
 * @param onResult Invoked with a `RESULT_*` code.
 */
@Composable
private fun SecurityDialog(
    show: Boolean,
    window: Boolean,
    material: Boolean,
    summary: String?,
    showStatement: Boolean,
    showCheckbox: Boolean,
    initialChecked: Boolean,
    enableWindowDim: Boolean,
    onResult: (Int) -> Unit,
    onDismissFinished: (() -> Unit)?,
) {
    val title = stringResource(Res.string.demo_security_dialog_notice_title)
    val statement = if (showStatement) stringResource(Res.string.demo_security_dialog_statement) else null
    val statementLink = if (showStatement) stringResource(Res.string.demo_security_dialog_statement_link) else null
    val checkboxText = if (showCheckbox) stringResource(Res.string.demo_security_dialog_checkbox_text) else null
    val confirmText = stringResource(Res.string.common_confirm)
    val cancelText = stringResource(Res.string.common_cancel)
    val onConfirm: (Boolean) -> Unit = { dontRemind ->
        onResult(if (dontRemind) RESULT_CONFIRMED_DONT_REMIND else RESULT_CONFIRMED)
    }
    val onCancel = { onResult(RESULT_CANCELLED) }
    val onLinkClick = { onResult(RESULT_LINK) }
    if (window) {
        WindowSecurityDialog(
            show = show,
            onConfirm = onConfirm,
            onCancel = onCancel,
            title = title,
            summary = summary,
            statement = statement,
            statementLinkText = statementLink,
            onLinkClick = onLinkClick,
            checkboxText = checkboxText,
            initialChecked = initialChecked,
            confirmText = confirmText,
            cancelText = cancelText,
            enableWindowDim = enableWindowDim,
            onDismissFinished = onDismissFinished,
        )
    } else {
        val blur = demoPanelBlur(enabled = material)
        OverlaySecurityDialog(
            show = show,
            onConfirm = onConfirm,
            onCancel = onCancel,
            backgroundModifier = blur?.backgroundModifier ?: Modifier,
            title = title,
            summary = summary,
            statement = statement,
            statementLinkText = statementLink,
            onLinkClick = onLinkClick,
            checkboxText = checkboxText,
            initialChecked = initialChecked,
            confirmText = confirmText,
            cancelText = cancelText,
            backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
            enableWindowDim = enableWindowDim,
            onDismissFinished = onDismissFinished,
            materialStroke = blur != null,
        )
    }
}

@Composable
private fun ColumnScope.SecurityDialogsVariant() {
    var showOverlay by remember { mutableStateOf(false) }
    var overlayHoldDown by remember { mutableStateOf(false) }
    var showWindow by remember { mutableStateOf(false) }
    var windowHoldDown by remember { mutableStateOf(false) }
    var result by remember { mutableIntStateOf(RESULT_NONE) }
    CardListGroup {
        dialogLaunchRow(
            title = Res.string.demo_security_dialog_open_overlay,
            summary = Res.string.demo_security_dialog_open_overlay_summary,
            holdDown = overlayHoldDown,
            onClick = {
                showOverlay = true
                overlayHoldDown = true
            },
        )
        dialogLaunchRow(
            title = Res.string.demo_security_dialog_open_window,
            summary = Res.string.demo_security_dialog_open_window_summary,
            holdDown = windowHoldDown,
            onClick = {
                showWindow = true
                windowHoldDown = true
            },
        )
    }
    DialogDemoCaption(
        text = securityResultText(result),
        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp),
    )
    val summary = stringResource(Res.string.demo_security_dialog_summary)
    SecurityDialog(
        show = showOverlay,
        window = false,
        material = true,
        summary = summary,
        showStatement = true,
        showCheckbox = true,
        initialChecked = false,
        enableWindowDim = true,
        onResult = { code ->
            result = code
            if (code != RESULT_LINK) showOverlay = false
        },
        onDismissFinished = { overlayHoldDown = false },
    )
    SecurityDialog(
        show = showWindow,
        window = true,
        material = false,
        summary = summary,
        showStatement = true,
        showCheckbox = true,
        initialChecked = false,
        enableWindowDim = true,
        onResult = { code ->
            result = code
            if (code != RESULT_LINK) showWindow = false
        },
        onDismissFinished = { windowHoldDown = false },
    )
}
