// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextField
import io.github.suqi8.coui.kmp.blur.MaterialBlurDefaults
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Contacts
import io.github.suqi8.coui.kmp.layout.DialogButtonBar
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.overlay.OverlayDialog
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.theme.COUITheme
import utils.panelBlur

fun LazyListScope.arrowSection() {
    item(key = "arrow") {
        var volume by remember { mutableFloatStateOf(0.5f) }
        val showVolumeDialog = remember { mutableStateOf(false) }
        val volumeDialogHoldDown = remember { mutableStateOf(false) }

        SmallTitle(text = "Arrow")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            CardListGroup {
                item {
                    ArrowPreference(
                        title = "Arrow",
                        startAction = {
                            Box {
                                Icon(
                                    imageVector = COUIIcons.Contacts,
                                    contentDescription = "Personal",
                                    tint = COUITheme.colorScheme.labelPrimary,
                                )
                            }
                        },
                        endActions = {
                            Text(
                                text = "End",
                                fontSize = COUITheme.textStyles.bodyM.fontSize,
                                color = COUITheme.colorScheme.labelTertiary,
                            )
                        },
                        onClick = {},
                    )
                }
                item {
                    SliderPreference(
                        title = "Volume",
                        valueText = "${(volume * 100).toInt()}%",
                        value = volume,
                        onValueChange = { volume = it },
                        onClick = {
                            showVolumeDialog.value = true
                            volumeDialogHoldDown.value = true
                        },
                        holdDownState = volumeDialogHoldDown.value,
                    )
                }
                item {
                    ArrowPreference(
                        title = "Disabled Arrow",
                        endActions = {
                            Text(
                                text = "End",
                                fontSize = COUITheme.textStyles.bodyM.fontSize,
                                color = COUITheme.colorScheme.disabledNeutral,
                            )
                        },
                        enabled = false,
                    )
                }
            }
        }

        SliderDialog(
            showVolumeDialog,
            volumeState = { volume },
            onVolumeChange = { volume = it },
            onDismissFinished = { volumeDialogHoldDown.value = false },
        )
    }
}

@Composable
private fun SliderDialog(
    showDialog: MutableState<Boolean>,
    volumeState: () -> Float,
    onVolumeChange: (Float) -> Unit,
    onDismissFinished: () -> Unit,
) {
    val blur = panelBlur(MaterialBlurDefaults::dialog)
    OverlayDialog(
        show = showDialog.value,
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        title = "Adjust Volume",
        summary = "Enter 0-100",
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = {
            showDialog.value = false
        },
        onDismissFinished = onDismissFinished,
        materialStroke = blur != null,
        content = {
            var text by remember { mutableStateOf(((volumeState() * 100).toInt()).toString()) }
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
                    if (digits.isEmpty()) {
                        text = ""
                    } else {
                        val limited = digits.take(3)
                        val num = limited.toIntOrNull() ?: 0
                        val clamped = num.coerceIn(0, 100)
                        text = clamped.toString()
                    }
                },
            )
            DialogButtonBar(
                negative = DialogButtonBarAction(text = "Cancel", onClick = { showDialog.value = false }),
                positive = DialogButtonBarAction(
                    text = "Confirm",
                    onClick = {
                        val parsed = text.toIntOrNull()
                        val clamped = parsed?.coerceIn(0, 100) ?: ((volumeState() * 100).toInt())
                        onVolumeChange(clamped / 100f)
                        showDialog.value = false
                    },
                ),
            )
        },
    )
}
