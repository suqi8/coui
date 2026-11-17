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
import io.github.suqi8.coui.kmp.layout.DialogButtonBar
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.overlay.OverlayDialog
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_cancel
import io.github.suqi8.coui.kmp.shared.generated.resources.common_confirm
import io.github.suqi8.coui.kmp.shared.generated.resources.common_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_backup_delete_all
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_backup_delete_both
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_backup_not_now
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_backup_turn_on
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_basic_message
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_basic_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_buttons
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_buttons_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_cancelable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_cancelable_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_long_labels
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_long_labels_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_long_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_message
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_message_long
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_neutral_label
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_open_long
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_open_long_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_open_overlay
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_open_overlay_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_open_recommended
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_open_recommended_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_open_stacked
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_open_stacked_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_open_window
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_open_window_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_recommended
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_recommended_message
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_recommended_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_recommended_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_show
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_stacked_message
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_stacked_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_terms_agree
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_terms_disagree
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_terms_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_variant_basic
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_variant_buttons
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_variant_long
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_window_dim
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialogs_length_long
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialogs_length_none
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialogs_length_scrolling
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialogs_length_short
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_text
import io.github.suqi8.coui.kmp.window.WindowDialog
import org.jetbrains.compose.resources.stringResource

internal val DialogDemo = Demo(
    id = "dialog",
    apiName = "OverlayDialog",
    title = Res.string.demo_dialog_title,
    category = DemoCategory.Dialogs,
    page = DemoPage.Variants {
        playground {
            DialogPlayground()
        }
        variant(key = "basic", title = Res.string.demo_dialog_variant_basic) {
            BasicDialogsVariant()
        }
        variant(key = "buttons", title = Res.string.demo_dialog_variant_buttons) {
            ButtonBarDialogsVariant()
        }
        variant(key = "long", title = Res.string.demo_dialog_variant_long) {
            LongMessageDialogVariant()
        }
    },
    keywords = listOf("alert", "WindowDialog", "DialogButtonBar", "弹窗"),
)

/** The button counts the playground offers; numbers stay literals. */
private val ButtonCounts = listOf("1", "2", "3")

@Composable
private fun ColumnScope.DialogPlayground() {
    var hostIndex by rememberSaveable { mutableIntStateOf(0) }
    var material by rememberSaveable { mutableStateOf(true) }
    var titleIndex by rememberSaveable { mutableIntStateOf(1) }
    var messageIndex by rememberSaveable { mutableIntStateOf(1) }
    var buttonCountIndex by rememberSaveable { mutableIntStateOf(1) }
    var longLabels by rememberSaveable { mutableStateOf(false) }
    var recommended by rememberSaveable { mutableStateOf(false) }
    var cancelable by rememberSaveable { mutableStateOf(true) }
    var windowDim by rememberSaveable { mutableStateOf(true) }
    var show by remember { mutableStateOf(false) }
    val window = hostIndex == HOST_WINDOW

    val none = stringResource(Res.string.demo_dialogs_length_none)
    val short = stringResource(Res.string.demo_dialogs_length_short)
    val long = stringResource(Res.string.demo_dialogs_length_long)
    val scrolling = stringResource(Res.string.demo_dialogs_length_scrolling)

    val shortTitle = stringResource(Res.string.demo_dialog_basic_title)
    val longTitle = stringResource(Res.string.demo_dialog_long_title)
    val shortMessage = stringResource(Res.string.demo_dialog_basic_message)
    val longMessage = stringResource(Res.string.demo_dialog_message_long)
    val paragraph = stringResource(Res.string.sample_long_text)
    val scrollingMessage = remember(paragraph) { List(10) { paragraph }.joinToString(separator = "\n\n") }

    val cancelText = stringResource(Res.string.common_cancel)
    val confirmText = stringResource(Res.string.common_confirm)
    val neutralText = stringResource(Res.string.demo_dialog_neutral_label)
    val longNegativeText = stringResource(Res.string.demo_dialog_backup_not_now)
    val longPositiveText = stringResource(Res.string.demo_dialog_backup_delete_both)
    val longNeutralText = stringResource(Res.string.demo_dialog_backup_delete_all)

    val dismiss = { show = false }
    val buttonCount = buttonCountIndex + 1
    val positive = DialogButtonBarAction(
        text = if (longLabels) longPositiveText else confirmText,
        recommended = recommended,
        onClick = dismiss,
    )
    val negative = if (buttonCount >= 2) {
        DialogButtonBarAction(text = if (longLabels) longNegativeText else cancelText, onClick = dismiss)
    } else {
        null
    }
    val neutral = if (buttonCount >= 3) {
        DialogButtonBarAction(text = if (longLabels) longNeutralText else neutralText, onClick = dismiss)
    } else {
        null
    }

    PlaygroundPreview {
        DialogLaunchButton(text = stringResource(Res.string.demo_dialog_show), onClick = { show = true })
        DialogDemoCaption(text = if (window) "WindowDialog" else "OverlayDialog")
    }
    PlaygroundControls {
        dialogHostRow(selectedIndex = hostIndex, onSelectedIndexChange = { hostIndex = it })
        dialogMaterialRow(checked = material, onCheckedChange = { material = it }, enabled = !window)
        dialogDropdownRow(
            title = Res.string.common_title,
            items = listOf(none, short, long),
            selectedIndex = titleIndex,
            onSelectedIndexChange = { titleIndex = it },
        )
        dialogDropdownRow(
            title = Res.string.demo_dialog_message,
            items = listOf(none, short, long, scrolling),
            selectedIndex = messageIndex,
            onSelectedIndexChange = { messageIndex = it },
        )
        dialogDropdownRow(
            title = Res.string.demo_dialog_buttons,
            items = ButtonCounts,
            selectedIndex = buttonCountIndex,
            onSelectedIndexChange = { buttonCountIndex = it },
            summary = Res.string.demo_dialog_buttons_summary,
        )
        dialogSwitchRow(
            title = Res.string.demo_dialog_long_labels,
            checked = longLabels,
            onCheckedChange = { longLabels = it },
            summary = Res.string.demo_dialog_long_labels_summary,
        )
        dialogSwitchRow(
            title = Res.string.demo_dialog_recommended,
            checked = recommended,
            onCheckedChange = { recommended = it },
            summary = Res.string.demo_dialog_recommended_summary,
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

    DemoAlertDialog(
        show = show,
        window = window,
        title = when (titleIndex) {
            0 -> null
            1 -> shortTitle
            else -> longTitle
        },
        summary = when (messageIndex) {
            0 -> null
            1 -> shortMessage
            2 -> longMessage
            else -> scrollingMessage
        },
        negative = negative,
        positive = positive,
        neutral = neutral,
        onDismissRequest = if (cancelable) dismiss else null,
        onDismissFinished = null,
        material = material,
        enableWindowDim = windowDim,
    )
}

/**
 * An alert dialog on either host with a [DialogButtonBar]. The COUI 17 material blur only applies
 * to the Overlay host, see [demoPanelBlur].
 */
@Composable
private fun DemoAlertDialog(
    show: Boolean,
    window: Boolean,
    title: String?,
    summary: String?,
    negative: DialogButtonBarAction?,
    positive: DialogButtonBarAction?,
    onDismissRequest: (() -> Unit)?,
    onDismissFinished: (() -> Unit)?,
    neutral: DialogButtonBarAction? = null,
    material: Boolean = true,
    enableWindowDim: Boolean = true,
) {
    // COUI 17 blurs the window behind the dialog when the material blur is on; the page backdrop
    // stands in for that window here.
    val blur = demoPanelBlur(enabled = material && !window)
    if (window) {
        WindowDialog(
            show = show,
            title = title,
            summary = summary,
            enableWindowDim = enableWindowDim,
            onDismissRequest = onDismissRequest,
            onDismissFinished = onDismissFinished,
        ) {
            DialogButtonBar(negative = negative, positive = positive, neutral = neutral)
        }
    } else {
        OverlayDialog(
            show = show,
            backgroundModifier = blur?.backgroundModifier ?: Modifier,
            title = title,
            summary = summary,
            backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
            enableWindowDim = enableWindowDim,
            onDismissRequest = onDismissRequest,
            onDismissFinished = onDismissFinished,
            materialStroke = blur != null,
        ) {
            DialogButtonBar(negative = negative, positive = positive, neutral = neutral)
        }
    }
}

@Composable
private fun ColumnScope.BasicDialogsVariant() {
    var showOverlay by remember { mutableStateOf(false) }
    var overlayHoldDown by remember { mutableStateOf(false) }
    var showWindow by remember { mutableStateOf(false) }
    var windowHoldDown by remember { mutableStateOf(false) }
    CardListGroup {
        dialogLaunchRow(
            title = Res.string.demo_dialog_open_overlay,
            summary = Res.string.demo_dialog_open_overlay_summary,
            holdDown = overlayHoldDown,
            onClick = {
                showOverlay = true
                overlayHoldDown = true
            },
        )
        dialogLaunchRow(
            title = Res.string.demo_dialog_open_window,
            summary = Res.string.demo_dialog_open_window_summary,
            holdDown = windowHoldDown,
            onClick = {
                showWindow = true
                windowHoldDown = true
            },
        )
    }
    val title = stringResource(Res.string.demo_dialog_basic_title)
    val message = stringResource(Res.string.demo_dialog_basic_message)
    val cancel = stringResource(Res.string.common_cancel)
    val confirm = stringResource(Res.string.common_confirm)
    DemoAlertDialog(
        show = showOverlay,
        window = false,
        title = title,
        summary = message,
        negative = DialogButtonBarAction(text = cancel, onClick = { showOverlay = false }),
        positive = DialogButtonBarAction(text = confirm, onClick = { showOverlay = false }),
        onDismissRequest = { showOverlay = false },
        onDismissFinished = { overlayHoldDown = false },
    )
    DemoAlertDialog(
        show = showWindow,
        window = true,
        title = title,
        summary = message,
        negative = DialogButtonBarAction(text = cancel, onClick = { showWindow = false }),
        positive = DialogButtonBarAction(text = confirm, onClick = { showWindow = false }),
        onDismissRequest = { showWindow = false },
        onDismissFinished = { windowHoldDown = false },
    )
}

/**
 * The two button bar tiers: labels too wide for a row flip the bar to a vertical stack
 * (COUIButtonBarLayout's needSetButVertical check), and the recommended action becomes a primary
 * capsule (COUIAlertDialogBuilder `isRecommend = true`).
 */
@Composable
private fun ColumnScope.ButtonBarDialogsVariant() {
    var showStacked by remember { mutableStateOf(false) }
    var stackedHoldDown by remember { mutableStateOf(false) }
    var showRecommended by remember { mutableStateOf(false) }
    var recommendedHoldDown by remember { mutableStateOf(false) }
    CardListGroup {
        dialogLaunchRow(
            title = Res.string.demo_dialog_open_stacked,
            summary = Res.string.demo_dialog_open_stacked_summary,
            holdDown = stackedHoldDown,
            onClick = {
                showStacked = true
                stackedHoldDown = true
            },
        )
        dialogLaunchRow(
            title = Res.string.demo_dialog_open_recommended,
            summary = Res.string.demo_dialog_open_recommended_summary,
            holdDown = recommendedHoldDown,
            onClick = {
                showRecommended = true
                recommendedHoldDown = true
            },
        )
    }
    val notNow = stringResource(Res.string.demo_dialog_backup_not_now)
    DemoAlertDialog(
        show = showStacked,
        window = false,
        title = stringResource(Res.string.demo_dialog_stacked_title),
        summary = stringResource(Res.string.demo_dialog_stacked_message),
        negative = DialogButtonBarAction(text = notNow, onClick = { showStacked = false }),
        positive = DialogButtonBarAction(
            text = stringResource(Res.string.demo_dialog_backup_delete_both),
            onClick = { showStacked = false },
        ),
        neutral = DialogButtonBarAction(
            text = stringResource(Res.string.demo_dialog_backup_delete_all),
            onClick = { showStacked = false },
        ),
        onDismissRequest = { showStacked = false },
        onDismissFinished = { stackedHoldDown = false },
    )
    DemoAlertDialog(
        show = showRecommended,
        window = false,
        title = stringResource(Res.string.demo_dialog_recommended_title),
        summary = stringResource(Res.string.demo_dialog_recommended_message),
        negative = DialogButtonBarAction(text = notNow, onClick = { showRecommended = false }),
        positive = DialogButtonBarAction(
            text = stringResource(Res.string.demo_dialog_backup_turn_on),
            recommended = true,
            onClick = { showRecommended = false },
        ),
        onDismissRequest = { showRecommended = false },
        onDismissFinished = { recommendedHoldDown = false },
    )
}

/** A message taller than the panel: it scrolls while the button bar stays in view. */
@Composable
private fun ColumnScope.LongMessageDialogVariant() {
    var show by remember { mutableStateOf(false) }
    var holdDown by remember { mutableStateOf(false) }
    CardListGroup {
        dialogLaunchRow(
            title = Res.string.demo_dialog_open_long,
            summary = Res.string.demo_dialog_open_long_summary,
            holdDown = holdDown,
            onClick = {
                show = true
                holdDown = true
            },
        )
    }
    val paragraph = stringResource(Res.string.sample_long_text)
    val message = remember(paragraph) { List(10) { paragraph }.joinToString(separator = "\n\n") }
    DemoAlertDialog(
        show = show,
        window = false,
        title = stringResource(Res.string.demo_dialog_terms_title),
        summary = message,
        negative = DialogButtonBarAction(
            text = stringResource(Res.string.demo_dialog_terms_disagree),
            onClick = { show = false },
        ),
        positive = DialogButtonBarAction(
            text = stringResource(Res.string.demo_dialog_terms_agree),
            onClick = { show = false },
        ),
        onDismissRequest = { show = false },
        onDismissFinished = { holdDown = false },
    )
}
