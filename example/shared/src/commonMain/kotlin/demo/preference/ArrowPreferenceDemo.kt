// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.preference

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.LocalDemoSnackbarHostState
import demo.framework.PlaygroundControls
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.TextField
import io.github.suqi8.coui.kmp.blur.MaterialBlurDefaults
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Contacts
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.layout.DialogButtonBar
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.overlay.OverlayDialog
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_cancel
import io.github.suqi8.coui.kmp.shared.generated.resources.common_clicked
import io.github.suqi8.coui.kmp.shared.generated.resources.common_confirm
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_arrow_preference_clickable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_arrow_preference_hold_down
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_arrow_preference_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_arrow_preference_variant_basic
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_arrow_preference_variant_dialog
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_arrow_preference_volume
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_arrow_preference_volume_dialog_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_arrow_preference_volume_dialog_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_arrow_preference_volume_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_profile
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_settings
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_summary
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import utils.panelBlur

internal val ArrowPreferenceDemo = Demo(
    id = "arrow_preference",
    apiName = "ArrowPreference",
    title = Res.string.demo_arrow_preference_title,
    category = DemoCategory.Preference,
    page = DemoPage.Variants {
        playground {
            val state = rememberPreferenceRowState(showIcon = true, showEndText = true)
            var clickable by rememberSaveable { mutableStateOf(true) }
            var holdDown by rememberSaveable { mutableStateOf(false) }
            val snackbar = LocalDemoSnackbarHostState.current
            val scope = rememberCoroutineScope()
            val title = state.title()
            val clickedMessage = stringResource(Res.string.common_clicked, title)
            val onRowClick: () -> Unit = { scope.launch { snackbar.showSnackbar(clickedMessage) } }
            PreferencePreview {
                item {
                    ArrowPreference(
                        title = title,
                        summary = state.summary(),
                        startAction = if (state.showIcon) {
                            { PreferenceStartIcon() }
                        } else {
                            null
                        },
                        endActions = {
                            if (state.showEndText) PreferenceEndText(enabled = state.enabled)
                        },
                        onClick = if (clickable) onRowClick else null,
                        holdDownState = holdDown,
                        enabled = state.enabled,
                    )
                }
            }
            PlaygroundControls {
                preferenceRowControls(state)
                switchControl(Res.string.demo_arrow_preference_clickable, clickable) { clickable = it }
                switchControl(Res.string.demo_arrow_preference_hold_down, holdDown) { holdDown = it }
            }
        }
        variant(key = "basic", title = Res.string.demo_arrow_preference_variant_basic) {
            val snackbar = LocalDemoSnackbarHostState.current
            val scope = rememberCoroutineScope()
            val profile = stringResource(Res.string.sample_profile)
            val clickedProfile = stringResource(Res.string.common_clicked, profile)
            CardListGroup {
                item {
                    ArrowPreference(
                        title = profile,
                        startAction = { PreferenceStartIcon(imageVector = COUIIcons.Contacts) },
                        endActions = { PreferenceEndText() },
                        onClick = { scope.launch { snackbar.showSnackbar(clickedProfile) } },
                    )
                }
                item {
                    ArrowPreference(
                        title = stringResource(Res.string.sample_settings),
                        summary = stringResource(Res.string.sample_summary),
                        startAction = { PreferenceStartIcon(imageVector = COUIIcons.Settings) },
                        onClick = {},
                    )
                }
                item {
                    ArrowPreference(
                        title = stringResource(Res.string.common_disabled),
                        endActions = { PreferenceEndText(enabled = false) },
                        enabled = false,
                    )
                }
            }
        }
        variant(key = "dialog", title = Res.string.demo_arrow_preference_variant_dialog) {
            VolumeDialogRow()
        }
    },
    keywords = listOf("设置", "arrow", "箭头", "跳转"),
)

/**
 * A slider row whose click opens a dialog to type the exact value; the row stays held down until
 * the dialog has finished dismissing.
 */
@Composable
private fun ColumnScope.VolumeDialogRow() {
    var volume by remember { mutableFloatStateOf(0.5f) }
    var showDialog by remember { mutableStateOf(false) }
    var holdDown by remember { mutableStateOf(false) }
    CardListGroup {
        item {
            SliderPreference(
                title = stringResource(Res.string.demo_arrow_preference_volume),
                summary = stringResource(Res.string.demo_arrow_preference_volume_summary),
                valueText = "${(volume * 100).toInt()}%",
                value = volume,
                onValueChange = { volume = it },
                onClick = {
                    showDialog = true
                    holdDown = true
                },
                holdDownState = holdDown,
            )
        }
    }
    VolumeDialog(
        show = showDialog,
        volume = { volume },
        onVolumeChange = { volume = it },
        onDismissRequest = { showDialog = false },
        onDismissFinished = { holdDown = false },
    )
}

@Composable
private fun VolumeDialog(
    show: Boolean,
    volume: () -> Float,
    onVolumeChange: (Float) -> Unit,
    onDismissRequest: () -> Unit,
    onDismissFinished: () -> Unit,
) {
    val blur = panelBlur(MaterialBlurDefaults::dialog)
    OverlayDialog(
        show = show,
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        title = stringResource(Res.string.demo_arrow_preference_volume_dialog_title),
        summary = stringResource(Res.string.demo_arrow_preference_volume_dialog_summary),
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        materialStroke = blur != null,
        content = {
            var text by remember { mutableStateOf((volume() * 100).toInt().toString()) }
            TextField(
                // The dialog content slot is unpadded (COUI button bars span the panel), so
                // custom content brings its own 24dp side margins.
                // COUI coui_single_inputview_bottom_alert_dialog_layout: 24dp sides, 18dp below.
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 18.dp),
                value = text,
                // COUIInputView sets no minimum height of its own.
                minHeight = 0.dp,
                maxLines = 1,
                onValueChange = { newValue ->
                    val digits = newValue.filter { it.isDigit() }
                    text = if (digits.isEmpty()) "" else (digits.take(3).toIntOrNull() ?: 0).coerceIn(0, 100).toString()
                },
            )
            DialogButtonBar(
                negative = DialogButtonBarAction(
                    text = stringResource(Res.string.common_cancel),
                    onClick = onDismissRequest,
                ),
                positive = DialogButtonBarAction(
                    text = stringResource(Res.string.common_confirm),
                    onClick = {
                        val clamped = text.toIntOrNull()?.coerceIn(0, 100) ?: (volume() * 100).toInt()
                        onVolumeChange(clamped / 100f)
                        onDismissRequest()
                    },
                ),
            )
        },
    )
}
