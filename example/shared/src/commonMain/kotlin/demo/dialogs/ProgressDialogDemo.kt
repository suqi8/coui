// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.dialogs

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import io.github.suqi8.coui.kmp.overlay.OverlayLoadingDialog
import io.github.suqi8.coui.kmp.overlay.OverlayProgressDialog
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_cancel
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_title
import io.github.suqi8.coui.kmp.shared.generated.resources.common_style
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_cancelable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_window_dim
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_cancel_button
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_cancelable_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_downloading
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_loading
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_open_cancelable_loading
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_open_cancelable_loading_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_open_loading
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_open_loading_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_open_progress
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_open_progress_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_please_wait
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_show
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_size_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_speed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_type_progress
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_type_spinner
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_variant_loading
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_progress_dialog_variant_progress
import io.github.suqi8.coui.kmp.window.WindowLoadingDialog
import io.github.suqi8.coui.kmp.window.WindowProgressDialog
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

internal val ProgressDialogDemo = Demo(
    id = "progress_dialog",
    apiName = "OverlayProgressDialog",
    title = Res.string.demo_progress_dialog_title,
    category = DemoCategory.Dialogs,
    page = DemoPage.Variants {
        playground {
            ProgressDialogPlayground()
        }
        variant(key = "progress", title = Res.string.demo_progress_dialog_variant_progress) {
            ProgressDialogVariant()
        }
        variant(key = "loading", title = Res.string.demo_progress_dialog_variant_loading) {
            LoadingDialogsVariant()
        }
    },
    keywords = listOf("loading", "OverlayLoadingDialog", "spinner", "加载", "弹窗"),
    isNew = true,
)

private const val TYPE_PROGRESS = 0

/** How long the fake task of the spinner takes to finish, in milliseconds. */
private const val LOADING_DURATION_MS = 3000L

/** The fake task speeds of the playground, as the progress step per 60 ms tick. */
private val ProgressSteps = listOf(0.005f, 0.02f, 0.05f)

@Composable
private fun ColumnScope.ProgressDialogPlayground() {
    var hostIndex by rememberSaveable { mutableIntStateOf(0) }
    var material by rememberSaveable { mutableStateOf(true) }
    var typeIndex by rememberSaveable { mutableIntStateOf(TYPE_PROGRESS) }
    var showTitle by rememberSaveable { mutableStateOf(true) }
    var showSummary by rememberSaveable { mutableStateOf(false) }
    var cancelButton by rememberSaveable { mutableStateOf(true) }
    var cancelable by rememberSaveable { mutableStateOf(false) }
    var speedIndex by rememberSaveable { mutableIntStateOf(1) }
    var windowDim by rememberSaveable { mutableStateOf(true) }
    var show by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    val window = hostIndex == HOST_WINDOW
    val isProgress = typeIndex == TYPE_PROGRESS

    // The fake task: the bar fills up, the spinner runs for a while, then the dialog closes. With a
    // cancel button the spinner keeps going until it is cancelled.
    LaunchedEffect(show, isProgress, cancelButton) {
        if (!show) return@LaunchedEffect
        if (isProgress) {
            progress = 0f
            while (progress < 1f) {
                delay(60)
                progress = (progress + ProgressSteps[speedIndex]).coerceAtMost(1f)
            }
            show = false
        } else if (!cancelButton) {
            delay(LOADING_DURATION_MS)
            show = false
        }
    }

    val dismiss = { show = false }
    val blur = demoPanelBlur(enabled = material && !window)
    val backgroundModifier = blur?.backgroundModifier ?: Modifier
    val backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor()
    val title = if (showTitle) stringResource(Res.string.demo_progress_dialog_downloading) else null
    val cancelAction = if (cancelButton) {
        DialogButtonBarAction(text = stringResource(Res.string.common_cancel), onClick = dismiss)
    } else {
        null
    }
    val onDismissRequest = if (cancelable) dismiss else null

    val typeItems = listOf(
        stringResource(Res.string.demo_progress_dialog_type_progress),
        stringResource(Res.string.demo_progress_dialog_type_spinner),
    )

    PlaygroundPreview {
        DialogLaunchButton(text = stringResource(Res.string.demo_progress_dialog_show), onClick = { show = true })
        DialogDemoCaption(
            text = (if (window) "Window" else "Overlay") + if (isProgress) "ProgressDialog" else "LoadingDialog",
        )
    }
    PlaygroundControls {
        dialogHostRow(selectedIndex = hostIndex, onSelectedIndexChange = { hostIndex = it })
        dialogMaterialRow(checked = material, onCheckedChange = { material = it }, enabled = !window)
        dialogDropdownRow(
            title = Res.string.common_style,
            items = typeItems,
            selectedIndex = typeIndex,
            onSelectedIndexChange = { typeIndex = it },
        )
        dialogSwitchRow(
            title = Res.string.common_show_title,
            checked = showTitle,
            onCheckedChange = { showTitle = it },
        )
        dialogSwitchRow(
            title = Res.string.common_show_summary,
            checked = showSummary,
            onCheckedChange = { showSummary = it },
        )
        dialogSwitchRow(
            title = Res.string.demo_progress_dialog_cancel_button,
            checked = cancelButton,
            onCheckedChange = { cancelButton = it },
        )
        dialogSwitchRow(
            title = Res.string.demo_dialog_cancelable,
            checked = cancelable,
            onCheckedChange = { cancelable = it },
            summary = Res.string.demo_progress_dialog_cancelable_summary,
        )
        dialogDropdownRow(
            title = Res.string.demo_progress_dialog_speed,
            items = ProgressSteps.map { step -> "${(step * 100 / 0.06f).toInt()}%/s" },
            selectedIndex = speedIndex,
            onSelectedIndexChange = { speedIndex = it },
            enabled = isProgress,
        )
        dialogSwitchRow(
            title = Res.string.demo_dialog_window_dim,
            checked = windowDim,
            onCheckedChange = { windowDim = it },
        )
    }

    if (isProgress) {
        val summary = if (showSummary) {
            stringResource(Res.string.demo_progress_dialog_size_summary, (progress * 100).toInt())
        } else {
            null
        }
        if (window) {
            WindowProgressDialog(
                show = show,
                progress = progress,
                title = title,
                summary = summary,
                cancelAction = cancelAction,
                enableWindowDim = windowDim,
                onDismissRequest = onDismissRequest,
            )
        } else {
            OverlayProgressDialog(
                show = show,
                progress = progress,
                backgroundModifier = backgroundModifier,
                title = title,
                summary = summary,
                cancelAction = cancelAction,
                backgroundColor = backgroundColor,
                enableWindowDim = windowDim,
                onDismissRequest = onDismissRequest,
                materialStroke = blur != null,
            )
        }
    } else {
        val text = if (showSummary) {
            stringResource(Res.string.demo_progress_dialog_please_wait)
        } else {
            null
        }
        if (window) {
            WindowLoadingDialog(
                show = show,
                text = text,
                enableWindowDim = windowDim,
                onDismissRequest = onDismissRequest,
                title = title,
                cancelAction = cancelAction,
            )
        } else {
            OverlayLoadingDialog(
                show = show,
                backgroundModifier = backgroundModifier,
                text = text,
                backgroundColor = backgroundColor,
                enableWindowDim = windowDim,
                onDismissRequest = onDismissRequest,
                materialStroke = blur != null,
                title = title,
                cancelAction = cancelAction,
            )
        }
    }
}

/** The horizontal progress dialog (COUIAlertDialog.Progress.Cancelable), filled by a fake task. */
@Composable
private fun ColumnScope.ProgressDialogVariant() {
    var show by remember { mutableStateOf(false) }
    var holdDown by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    CardListGroup {
        dialogLaunchRow(
            title = Res.string.demo_progress_dialog_open_progress,
            summary = Res.string.demo_progress_dialog_open_progress_summary,
            holdDown = holdDown,
            onClick = {
                show = true
                holdDown = true
            },
        )
    }
    LaunchedEffect(show) {
        if (show) {
            progress = 0f
            while (progress < 1f) {
                delay(60)
                progress = (progress + 0.02f).coerceAtMost(1f)
            }
            show = false
        }
    }
    val blur = demoPanelBlur(enabled = true)
    OverlayProgressDialog(
        show = show,
        progress = progress,
        title = stringResource(Res.string.demo_progress_dialog_downloading),
        cancelAction = DialogButtonBarAction(text = stringResource(Res.string.common_cancel), onClick = { show = false }),
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = { show = false },
        onDismissFinished = { holdDown = false },
        materialStroke = blur != null,
    )
}

/**
 * The rotating progress dialogs (COUIRotatingDialogBuilder): a spinner that closes itself like a
 * finished task would, and the cancelable form with a title and a cancel button.
 */
@Composable
private fun ColumnScope.LoadingDialogsVariant() {
    var showLoading by remember { mutableStateOf(false) }
    var loadingHoldDown by remember { mutableStateOf(false) }
    var showCancelable by remember { mutableStateOf(false) }
    var cancelableHoldDown by remember { mutableStateOf(false) }
    CardListGroup {
        dialogLaunchRow(
            title = Res.string.demo_progress_dialog_open_loading,
            summary = Res.string.demo_progress_dialog_open_loading_summary,
            holdDown = loadingHoldDown,
            onClick = {
                showLoading = true
                loadingHoldDown = true
            },
        )
        dialogLaunchRow(
            title = Res.string.demo_progress_dialog_open_cancelable_loading,
            summary = Res.string.demo_progress_dialog_open_cancelable_loading_summary,
            holdDown = cancelableHoldDown,
            onClick = {
                showCancelable = true
                cancelableHoldDown = true
            },
        )
    }
    // Auto-dismiss the plain spinner after a short delay, like a finished task would.
    LaunchedEffect(showLoading) {
        if (showLoading) {
            delay(LOADING_DURATION_MS)
            showLoading = false
        }
    }
    // Both builders sit on COUIAlertDialogBuilder, so they take the same blurred window and
    // material stroke as the alert dialogs.
    val blur = demoPanelBlur(enabled = true)
    OverlayLoadingDialog(
        show = showLoading,
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        text = stringResource(Res.string.demo_progress_dialog_loading),
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = { showLoading = false },
        onDismissFinished = { loadingHoldDown = false },
        materialStroke = blur != null,
    )
    OverlayLoadingDialog(
        show = showCancelable,
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        title = stringResource(Res.string.demo_progress_dialog_downloading),
        text = stringResource(Res.string.demo_progress_dialog_please_wait),
        cancelAction = DialogButtonBarAction(
            text = stringResource(Res.string.common_cancel),
            onClick = { showCancelable = false },
        ),
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissFinished = { cancelableHoldDown = false },
        materialStroke = blur != null,
    )
}
