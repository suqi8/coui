// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.feedback

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.LocalDemoSnackbarHostState
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.ButtonDefaults
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.NotificationSnackbar
import io.github.suqi8.coui.kmp.basic.SnackbarDuration
import io.github.suqi8.coui.kmp.basic.SnackbarResult
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Image
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_share
import io.github.suqi8.coui.kmp.shared.generated.resources.common_undo
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_action_alive
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_action_expired
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_action_undo
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_button_action
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_button_custom
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_button_dismiss_newest
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_button_dismiss_oldest
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_button_dismissible
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_button_indefinite
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_button_long
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_button_multiline
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_button_short
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_duration
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_message
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_message_action
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_message_custom
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_message_dismissible
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_message_indefinite
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_message_kind_long
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_message_kind_multiline
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_message_kind_short
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_message_long
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_message_multiline
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_message_short
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_notification_shared
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_notification_subtitle
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_notification_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_result
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_show
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_show_again
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_variant_actions
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_variant_durations
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_variant_notification
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_variant_queue
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_with_action
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_snackbar_with_dismiss
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/** The demo page of `Snackbar`, listed in its category and found by the search. */
internal val SnackbarDemo = Demo(
    id = "snackbar",
    apiName = "Snackbar",
    title = Res.string.demo_snackbar_title,
    category = DemoCategory.Feedback,
    page = DemoPage.Variants {
        playground { SnackbarPlayground() }
        variant(key = "durations", title = Res.string.demo_snackbar_variant_durations) {
            SnackbarDurationsContent()
        }
        variant(key = "actions", title = Res.string.demo_snackbar_variant_actions) {
            SnackbarActionsContent()
        }
        variant(key = "queue", title = Res.string.demo_snackbar_variant_queue) {
            SnackbarQueueContent()
        }
        variant(key = "notification", title = Res.string.demo_snackbar_variant_notification, inCard = false) {
            NotificationSnackbarContent()
        }
    },
    keywords = listOf("SnackbarHost", "NotificationSnackbar"),
    searchTerms = Res.string.demo_snackbar_search_terms,
)

private val SnackbarMessages: List<StringResource> = listOf(
    Res.string.demo_snackbar_message_short,
    Res.string.demo_snackbar_message_long,
    Res.string.demo_snackbar_message_multiline,
)

private val SnackbarDurations: List<SnackbarDuration> = listOf(
    SnackbarDuration.Short,
    SnackbarDuration.Long,
    SnackbarDuration.Custom(2000L),
    SnackbarDuration.Indefinite,
)

/** The duration names as written in code. */
private val SnackbarDurationNames = listOf("Short", "Long", "Custom(2000L)", "Indefinite")

@Composable
private fun ColumnScope.SnackbarPlayground() {
    val hostState = LocalDemoSnackbarHostState.current
    val scope = rememberCoroutineScope()
    var messageIndex by rememberSaveable { mutableIntStateOf(0) }
    var withAction by rememberSaveable { mutableStateOf(true) }
    var withDismiss by rememberSaveable { mutableStateOf(false) }
    var durationIndex by rememberSaveable { mutableIntStateOf(0) }
    var lastResult by rememberSaveable { mutableStateOf("-") }

    PlaygroundPreview {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TextButton(
                text = stringResource(Res.string.demo_snackbar_show),
                onClick = {
                    scope.launch {
                        val result = hostState.showSnackbar(
                            message = getString(SnackbarMessages[messageIndex]),
                            actionLabel = if (withAction) getString(Res.string.common_undo) else null,
                            withDismissAction = withDismiss,
                            duration = SnackbarDurations[durationIndex],
                        )
                        lastResult = result.name
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary(),
            )
            TextButton(
                text = stringResource(Res.string.demo_snackbar_button_dismiss_newest),
                onClick = { scope.launch { hostState.newestSnackbarData()?.dismiss() } },
                modifier = Modifier.weight(1f),
            )
        }
        Text(
            text = stringResource(Res.string.demo_snackbar_result, lastResult),
            modifier = Modifier.padding(top = 12.dp),
            color = COUITheme.colorScheme.labelSecondaryVariant,
            style = COUITheme.textStyles.bodyM,
        )
    }
    val messageItems = listOf(
        stringResource(Res.string.demo_snackbar_message_kind_short),
        stringResource(Res.string.demo_snackbar_message_kind_long),
        stringResource(Res.string.demo_snackbar_message_kind_multiline),
    )
    val messageTitle = stringResource(Res.string.demo_snackbar_message)
    val actionTitle = stringResource(Res.string.demo_snackbar_with_action)
    val dismissTitle = stringResource(Res.string.demo_snackbar_with_dismiss)
    val durationTitle = stringResource(Res.string.demo_snackbar_duration)
    PlaygroundControls {
        item {
            OverlayDropdownPreference(
                items = messageItems,
                selectedIndex = messageIndex,
                title = messageTitle,
                onSelectedIndexChange = { messageIndex = it },
            )
        }
        item {
            SwitchPreference(
                checked = withAction,
                onCheckedChange = { withAction = it },
                title = actionTitle,
                summary = "actionLabel",
            )
        }
        item {
            SwitchPreference(
                checked = withDismiss,
                onCheckedChange = { withDismiss = it },
                title = dismissTitle,
                summary = "withDismissAction",
            )
        }
        item {
            OverlayDropdownPreference(
                items = SnackbarDurationNames,
                selectedIndex = durationIndex,
                title = durationTitle,
                summary = "SnackbarDuration",
                onSelectedIndexChange = { durationIndex = it },
            )
        }
    }
}

/** Short (4s), long (10s) and custom (2s) durations. */
@Composable
private fun SnackbarDurationsContent() {
    val hostState = LocalDemoSnackbarHostState.current
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(
            text = stringResource(Res.string.demo_snackbar_button_short),
            onClick = { scope.launch { hostState.showSnackbar(getString(Res.string.demo_snackbar_message_short)) } },
            modifier = Modifier.weight(1f),
        )
        TextButton(
            text = stringResource(Res.string.demo_snackbar_button_long),
            onClick = {
                scope.launch {
                    hostState.showSnackbar(
                        message = getString(Res.string.demo_snackbar_message_long),
                        duration = SnackbarDuration.Long,
                    )
                }
            },
            modifier = Modifier.weight(1f),
        )
        TextButton(
            text = stringResource(Res.string.demo_snackbar_button_custom),
            onClick = {
                scope.launch {
                    hostState.showSnackbar(
                        message = getString(Res.string.demo_snackbar_message_custom),
                        duration = SnackbarDuration.Custom(2000L),
                    )
                }
            },
            modifier = Modifier.weight(1f),
        )
    }
}

/** The action states of a snackbar */
private enum class ActionState { Idle, Alive, Undone, Expired }

/**
 * An action whose result is shown on its button, a close button, an indefinite snackbar and a
 * multi-line message, which COUI 17 lays out vertically with the action below the message.
 * Touching a snackbar pauses its auto-dismiss timer.
 */
@Composable
private fun SnackbarActionsContent() {
    val hostState = LocalDemoSnackbarHostState.current
    val scope = rememberCoroutineScope()
    var actionState by rememberSaveable { mutableStateOf(ActionState.Idle) }
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            TextButton(
                text = stringResource(
                    when (actionState) {
                        ActionState.Idle -> Res.string.demo_snackbar_button_action
                        ActionState.Alive -> Res.string.demo_snackbar_action_alive
                        ActionState.Undone -> Res.string.demo_snackbar_action_undo
                        ActionState.Expired -> Res.string.demo_snackbar_action_expired
                    },
                ),
                onClick = {
                    scope.launch {
                        actionState = ActionState.Alive
                        val result = hostState.showSnackbar(
                            message = getString(Res.string.demo_snackbar_message_action),
                            actionLabel = getString(Res.string.common_undo),
                            duration = SnackbarDuration.Short,
                        )
                        actionState = if (result == SnackbarResult.ActionPerformed) ActionState.Undone else ActionState.Expired
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary(),
            )
            TextButton(
                text = stringResource(Res.string.demo_snackbar_button_dismissible),
                onClick = {
                    scope.launch {
                        hostState.showSnackbar(
                            message = getString(Res.string.demo_snackbar_message_dismissible),
                            withDismissAction = true,
                            duration = SnackbarDuration.Long,
                        )
                    }
                },
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            TextButton(
                text = stringResource(Res.string.demo_snackbar_button_indefinite),
                onClick = {
                    scope.launch {
                        hostState.showSnackbar(
                            message = getString(Res.string.demo_snackbar_message_indefinite),
                            withDismissAction = true,
                            duration = SnackbarDuration.Indefinite,
                        )
                    }
                },
                modifier = Modifier.weight(1f),
            )
            TextButton(
                text = stringResource(Res.string.demo_snackbar_button_multiline),
                onClick = {
                    scope.launch {
                        hostState.showSnackbar(
                            message = getString(Res.string.demo_snackbar_message_multiline),
                            actionLabel = getString(Res.string.common_undo),
                            duration = SnackbarDuration.Long,
                        )
                    }
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** Dismisses the oldest or the newest snackbar of the queue. */
@Composable
private fun SnackbarQueueContent() {
    val hostState = LocalDemoSnackbarHostState.current
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(
            text = stringResource(Res.string.demo_snackbar_button_dismiss_oldest),
            onClick = { scope.launch { hostState.oldestSnackbarData()?.dismiss() } },
            modifier = Modifier.weight(1f),
        )
        TextButton(
            text = stringResource(Res.string.demo_snackbar_button_dismiss_newest),
            onClick = { scope.launch { hostState.newestSnackbarData()?.dismiss() } },
            modifier = Modifier.weight(1f),
        )
    }
}

/**
 * The COUI 17 notification snack bar, shown in place: swipe it sideways or down, or tap its close
 * button, to dismiss it.
 */
@Composable
private fun ColumnScope.NotificationSnackbarContent() {
    var visible by rememberSaveable { mutableStateOf(true) }
    var actionCount by rememberSaveable { mutableIntStateOf(0) }
    AnimatedVisibility(visible = visible) {
        NotificationSnackbar(
            title = stringResource(Res.string.demo_snackbar_notification_title),
            subtitle = if (actionCount == 0) {
                stringResource(Res.string.demo_snackbar_notification_subtitle)
            } else {
                stringResource(Res.string.demo_snackbar_notification_shared, actionCount)
            },
            actionLabel = stringResource(Res.string.common_share),
            onActionClick = { actionCount++ },
            onDismiss = { visible = false },
            icon = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(COUITheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(imageVector = COUIIcons.Image, contentDescription = null, tint = COUITheme.colorScheme.onPrimary)
                }
            },
        )
    }
    if (!visible) {
        TextButton(
            text = stringResource(Res.string.demo_snackbar_show_again),
            onClick = { visible = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
    }
}
