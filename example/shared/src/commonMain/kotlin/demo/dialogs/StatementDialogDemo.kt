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
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.blur.MaterialBlurDefaults
import io.github.suqi8.coui.kmp.layout.BottomSheetDefaults
import io.github.suqi8.coui.kmp.layout.StatementPrivacyItem
import io.github.suqi8.coui.kmp.layout.UserStatementListItem
import io.github.suqi8.coui.kmp.overlay.OverlayIndividualStatementDialog
import io.github.suqi8.coui.kmp.overlay.OverlayUserStatementDialog
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.common_style
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_window_dim
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_app_message
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_app_name
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_back_closes
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_back_closes_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_confirm
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_content
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_content_list
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_content_message
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_exit
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_individual_statement
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_individual_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_list_network
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_list_network_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_list_storage
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_list_storage_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_location_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_long_statement
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_open_individual_overlay
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_open_individual_overlay_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_open_individual_window
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_open_individual_window_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_open_user_overlay
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_open_user_overlay_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_open_user_window
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_open_user_window_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_privacy_crash
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_privacy_crash_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_privacy_items
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_privacy_location
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_privacy_location_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_privacy_suggestions
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_privacy_suggestions_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_show
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_type_individual
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_type_user
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_user_statement
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_variant_dialogs
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_statement_dialog_welcome
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_text
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.window.WindowIndividualStatementDialog
import io.github.suqi8.coui.kmp.window.WindowUserStatementDialog
import org.jetbrains.compose.resources.stringResource

internal val StatementDialogDemo = Demo(
    id = "statement_dialog",
    apiName = "UserStatementDialog",
    title = Res.string.demo_statement_dialog_title,
    category = DemoCategory.Dialogs,
    page = DemoPage.Variants {
        playground {
            StatementDialogPlayground()
        }
        variant(key = "dialogs", title = Res.string.demo_statement_dialog_variant_dialogs) {
            StatementDialogsVariant()
        }
    },
    keywords = listOf("IndividualStatementDialog", "agreement", "隐私", "privacy", "协议", "弹窗"),
    isNew = true,
)

private const val TYPE_USER = 0
private const val CONTENT_MESSAGE = 0

/** The optional feature counts of the individual statement playground; numbers stay literals. */
private val PrivacyItemCounts = listOf("0", "1", "2", "3")

/** The sample optional features of the individual statement dialog. */
@Composable
private fun rememberPrivacyItems(): List<StatementPrivacyItem> {
    val titles = listOf(
        stringResource(Res.string.demo_statement_dialog_privacy_suggestions),
        stringResource(Res.string.demo_statement_dialog_privacy_crash),
        stringResource(Res.string.demo_statement_dialog_privacy_location),
    )
    val summaries = listOf(
        stringResource(Res.string.demo_statement_dialog_privacy_suggestions_summary),
        stringResource(Res.string.demo_statement_dialog_privacy_crash_summary),
        stringResource(Res.string.demo_statement_dialog_privacy_location_summary),
    )
    return remember(titles, summaries) {
        titles.zip(summaries) { title, summary -> StatementPrivacyItem(title = title, summary = summary) }
    }
}

/** The sample permission rows of the user statement dialog. */
@Composable
private fun rememberUserListItems(): List<UserStatementListItem> {
    val network = stringResource(Res.string.demo_statement_dialog_list_network)
    val networkSummary = stringResource(Res.string.demo_statement_dialog_list_network_summary)
    val storage = stringResource(Res.string.demo_statement_dialog_list_storage)
    val storageSummary = stringResource(Res.string.demo_statement_dialog_list_storage_summary)
    return remember(network, networkSummary, storage, storageSummary) {
        listOf(
            UserStatementListItem(title = network, summary = networkSummary),
            UserStatementListItem(title = storage, summary = storageSummary),
        )
    }
}

/** Prepends [lead] (and, for [long], a few sample paragraphs) to the agreement links. */
@Composable
private fun rememberStatementText(lead: String, links: AnnotatedString, long: Boolean = false): AnnotatedString {
    val paragraph = stringResource(Res.string.sample_long_text)
    return remember(lead, links, long, paragraph) {
        buildAnnotatedString {
            append(lead)
            if (long) {
                repeat(3) {
                    append(" ")
                    append(paragraph)
                }
            }
            append(" ")
            append(links)
        }
    }
}

/** Bit mask of the [checked] items' indices in [all]. */
private fun checkedMask(all: List<StatementPrivacyItem>, checked: List<StatementPrivacyItem>): Int = checked.fold(0) { mask, item ->
    val index = all.indexOf(item)
    if (index >= 0) mask or (1 shl index) else mask
}

@Composable
private fun ColumnScope.StatementDialogPlayground() {
    var hostIndex by rememberSaveable { mutableIntStateOf(0) }
    var material by rememberSaveable { mutableStateOf(true) }
    var typeIndex by rememberSaveable { mutableIntStateOf(TYPE_USER) }
    var contentIndex by rememberSaveable { mutableIntStateOf(CONTENT_MESSAGE) }
    var showLogo by rememberSaveable { mutableStateOf(true) }
    var privacyCountIndex by rememberSaveable { mutableIntStateOf(2) }
    var longStatement by rememberSaveable { mutableStateOf(false) }
    var backCloses by rememberSaveable { mutableStateOf(true) }
    var windowDim by rememberSaveable { mutableStateOf(true) }
    var show by remember { mutableStateOf(false) }
    var resultCode by rememberSaveable { mutableIntStateOf(STATEMENT_RESULT_NONE) }
    var resultArg by rememberSaveable { mutableIntStateOf(0) }
    val window = hostIndex == HOST_WINDOW
    val isUser = typeIndex == TYPE_USER

    val links = rememberAgreementLinks { link ->
        resultCode = STATEMENT_RESULT_LINK
        resultArg = link
    }
    val listItems = rememberUserListItems()
    val allPrivacyItems = rememberPrivacyItems()
    val shownPrivacyItems = allPrivacyItems.take(privacyCountIndex)
    val userStatement = rememberStatementText(stringResource(Res.string.demo_statement_dialog_user_statement), links, longStatement)
    val individualStatement = rememberStatementText(
        stringResource(Res.string.demo_statement_dialog_individual_statement),
        links,
        longStatement,
    )
    val primary = COUITheme.colorScheme.primary
    val logo = remember(primary) { ColorPainter(primary) }
    val blur = demoPanelBlur(enabled = material && !window, style = MaterialBlurDefaults::bottomSheet)
    val confirmText = stringResource(Res.string.demo_statement_dialog_confirm)
    val exitText = stringResource(Res.string.demo_statement_dialog_exit)
    val onExit = {
        resultCode = STATEMENT_RESULT_EXITED
        show = false
    }
    val onDismissRequest = if (backCloses) {
        { show = false }
    } else {
        null
    }

    val typeItems = listOf(
        stringResource(Res.string.demo_statement_dialog_type_user),
        stringResource(Res.string.demo_statement_dialog_type_individual),
    )
    val contentItems = listOf(
        stringResource(Res.string.demo_statement_dialog_content_message),
        stringResource(Res.string.demo_statement_dialog_content_list),
    )

    PlaygroundPreview {
        DialogLaunchButton(text = stringResource(Res.string.demo_statement_dialog_show), onClick = { show = true })
        DialogDemoCaption(
            text = (if (window) "Window" else "Overlay") + if (isUser) "UserStatementDialog" else "IndividualStatementDialog",
        )
        DialogDemoCaption(
            text = statementResultText(
                code = resultCode,
                arg = resultArg,
                names = if (resultCode == STATEMENT_RESULT_LIST_ITEM) listItems.map { it.title } else allPrivacyItems.map { it.title },
            ),
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
        dialogDropdownRow(
            title = Res.string.demo_statement_dialog_content,
            items = contentItems,
            selectedIndex = contentIndex,
            onSelectedIndexChange = { contentIndex = it },
            enabled = isUser,
        )
        dialogSwitchRow(
            title = Res.string.common_show_icon,
            checked = showLogo,
            onCheckedChange = { showLogo = it },
            enabled = isUser,
        )
        dialogDropdownRow(
            title = Res.string.demo_statement_dialog_privacy_items,
            items = PrivacyItemCounts,
            selectedIndex = privacyCountIndex,
            onSelectedIndexChange = { privacyCountIndex = it },
            enabled = !isUser,
        )
        dialogSwitchRow(
            title = Res.string.common_long_text,
            checked = longStatement,
            onCheckedChange = { longStatement = it },
            summary = Res.string.demo_statement_dialog_long_statement,
        )
        dialogSwitchRow(
            title = Res.string.demo_statement_dialog_back_closes,
            checked = backCloses,
            onCheckedChange = { backCloses = it },
            summary = Res.string.demo_statement_dialog_back_closes_summary,
        )
        dialogSwitchRow(
            title = Res.string.demo_dialog_window_dim,
            checked = windowDim,
            onCheckedChange = { windowDim = it },
        )
    }

    val welcome = stringResource(Res.string.demo_statement_dialog_welcome)
    val appName = stringResource(Res.string.demo_statement_dialog_app_name)
    val appMessage = stringResource(Res.string.demo_statement_dialog_app_message)
    val individualTitle = stringResource(Res.string.demo_statement_dialog_individual_title)
    val userOnConfirm = {
        resultCode = STATEMENT_RESULT_AGREED
        show = false
    }
    val onListItemClick: (Int) -> Unit = { index ->
        resultCode = STATEMENT_RESULT_LIST_ITEM
        resultArg = index
    }
    val individualOnConfirm: (List<StatementPrivacyItem>) -> Unit = { checked ->
        resultCode = STATEMENT_RESULT_ENABLED
        resultArg = checkedMask(allPrivacyItems, checked)
        show = false
    }
    val withList = contentIndex != CONTENT_MESSAGE
    when {
        isUser && window -> WindowUserStatementDialog(
            show = show,
            onConfirm = userOnConfirm,
            onExit = onExit,
            logo = if (showLogo) logo else null,
            title = welcome,
            appName = appName,
            appMessage = if (withList) null else appMessage,
            listItems = if (withList) listItems else emptyList(),
            onListItemClick = onListItemClick,
            statement = userStatement,
            confirmText = confirmText,
            exitText = exitText,
            enableWindowDim = windowDim,
            onDismissRequest = onDismissRequest,
        )

        isUser -> OverlayUserStatementDialog(
            show = show,
            onConfirm = userOnConfirm,
            onExit = onExit,
            backgroundModifier = blur?.backgroundModifier ?: Modifier,
            logo = if (showLogo) logo else null,
            title = welcome,
            appName = appName,
            appMessage = if (withList) null else appMessage,
            listItems = if (withList) listItems else emptyList(),
            onListItemClick = onListItemClick,
            statement = userStatement,
            confirmText = confirmText,
            exitText = exitText,
            backgroundColor = blur?.backgroundColor ?: BottomSheetDefaults.backgroundColor(),
            enableWindowDim = windowDim,
            onDismissRequest = onDismissRequest,
            materialStroke = blur != null,
        )

        window -> WindowIndividualStatementDialog(
            show = show,
            onConfirm = individualOnConfirm,
            onExit = onExit,
            title = individualTitle,
            statement = individualStatement,
            privacyItems = shownPrivacyItems,
            confirmText = confirmText,
            exitText = exitText,
            enableWindowDim = windowDim,
            onDismissRequest = onDismissRequest,
        )

        else -> OverlayIndividualStatementDialog(
            show = show,
            onConfirm = individualOnConfirm,
            onExit = onExit,
            backgroundModifier = blur?.backgroundModifier ?: Modifier,
            title = individualTitle,
            statement = individualStatement,
            privacyItems = shownPrivacyItems,
            confirmText = confirmText,
            exitText = exitText,
            backgroundColor = blur?.backgroundColor ?: BottomSheetDefaults.backgroundColor(),
            enableWindowDim = windowDim,
            onDismissRequest = onDismissRequest,
            materialStroke = blur != null,
        )
    }
}

/**
 * The four statement panels of the original demo: the user statement with a message (Overlay) and
 * with a permission list (Window), the individual statement with optional features (Overlay) and a
 * plain one (Window).
 */
@Composable
private fun ColumnScope.StatementDialogsVariant() {
    var showUserOverlay by remember { mutableStateOf(false) }
    var showUserWindow by remember { mutableStateOf(false) }
    var showIndividualOverlay by remember { mutableStateOf(false) }
    var showIndividualWindow by remember { mutableStateOf(false) }
    var resultCode by remember { mutableIntStateOf(STATEMENT_RESULT_NONE) }
    var resultArg by remember { mutableIntStateOf(0) }
    val links = rememberAgreementLinks { link ->
        resultCode = STATEMENT_RESULT_LINK
        resultArg = link
    }
    val listItems = rememberUserListItems()
    val privacyItems = rememberPrivacyItems().take(2)
    val userStatement = rememberStatementText(stringResource(Res.string.demo_statement_dialog_user_statement), links)
    val individualStatement = rememberStatementText(stringResource(Res.string.demo_statement_dialog_individual_statement), links)
    val welcome = stringResource(Res.string.demo_statement_dialog_welcome)
    val appName = stringResource(Res.string.demo_statement_dialog_app_name)
    val confirmText = stringResource(Res.string.demo_statement_dialog_confirm)
    val exitText = stringResource(Res.string.demo_statement_dialog_exit)
    val blur = demoPanelBlur(enabled = true, style = MaterialBlurDefaults::bottomSheet)

    CardListGroup {
        dialogLaunchRow(
            title = Res.string.demo_statement_dialog_open_user_overlay,
            summary = Res.string.demo_statement_dialog_open_user_overlay_summary,
            holdDown = showUserOverlay,
            onClick = { showUserOverlay = true },
        )
        dialogLaunchRow(
            title = Res.string.demo_statement_dialog_open_user_window,
            summary = Res.string.demo_statement_dialog_open_user_window_summary,
            holdDown = showUserWindow,
            onClick = { showUserWindow = true },
        )
        dialogLaunchRow(
            title = Res.string.demo_statement_dialog_open_individual_overlay,
            summary = Res.string.demo_statement_dialog_open_individual_overlay_summary,
            holdDown = showIndividualOverlay,
            onClick = { showIndividualOverlay = true },
        )
        dialogLaunchRow(
            title = Res.string.demo_statement_dialog_open_individual_window,
            summary = Res.string.demo_statement_dialog_open_individual_window_summary,
            holdDown = showIndividualWindow,
            onClick = { showIndividualWindow = true },
        )
    }
    DialogDemoCaption(
        text = statementResultText(
            code = resultCode,
            arg = resultArg,
            names = if (resultCode == STATEMENT_RESULT_LIST_ITEM) listItems.map { it.title } else privacyItems.map { it.title },
        ),
        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp),
    )

    OverlayUserStatementDialog(
        show = showUserOverlay,
        onConfirm = {
            resultCode = STATEMENT_RESULT_AGREED
            showUserOverlay = false
        },
        onExit = {
            resultCode = STATEMENT_RESULT_EXITED
            showUserOverlay = false
        },
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        title = welcome,
        appName = appName,
        appMessage = stringResource(Res.string.demo_statement_dialog_app_message),
        statement = userStatement,
        confirmText = confirmText,
        exitText = exitText,
        backgroundColor = blur?.backgroundColor ?: BottomSheetDefaults.backgroundColor(),
        onDismissRequest = { showUserOverlay = false },
        materialStroke = blur != null,
    )
    WindowUserStatementDialog(
        show = showUserWindow,
        onConfirm = {
            resultCode = STATEMENT_RESULT_AGREED
            showUserWindow = false
        },
        onExit = {
            resultCode = STATEMENT_RESULT_EXITED
            showUserWindow = false
        },
        title = welcome,
        appName = appName,
        listItems = listItems,
        onListItemClick = { index ->
            resultCode = STATEMENT_RESULT_LIST_ITEM
            resultArg = index
        },
        statement = userStatement,
        confirmText = confirmText,
        exitText = exitText,
        onDismissRequest = { showUserWindow = false },
    )
    OverlayIndividualStatementDialog(
        show = showIndividualOverlay,
        onConfirm = { checked ->
            resultCode = STATEMENT_RESULT_ENABLED
            resultArg = checkedMask(privacyItems, checked)
            showIndividualOverlay = false
        },
        onExit = {
            resultCode = STATEMENT_RESULT_EXITED
            showIndividualOverlay = false
        },
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        title = stringResource(Res.string.demo_statement_dialog_individual_title),
        statement = individualStatement,
        privacyItems = privacyItems,
        confirmText = confirmText,
        exitText = exitText,
        backgroundColor = blur?.backgroundColor ?: BottomSheetDefaults.backgroundColor(),
        onDismissRequest = { showIndividualOverlay = false },
        materialStroke = blur != null,
    )
    WindowIndividualStatementDialog(
        show = showIndividualWindow,
        onConfirm = {
            resultCode = STATEMENT_RESULT_AGREED
            showIndividualWindow = false
        },
        onExit = {
            resultCode = STATEMENT_RESULT_EXITED
            showIndividualWindow = false
        },
        title = stringResource(Res.string.demo_statement_dialog_location_title),
        statement = individualStatement,
        confirmText = confirmText,
        exitText = exitText,
        onDismissRequest = { showIndividualWindow = false },
    )
}
