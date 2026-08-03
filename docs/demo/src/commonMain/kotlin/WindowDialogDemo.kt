// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.ButtonDefaults
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.theme.LocalDismissState
import io.github.suqi8.coui.kmp.window.WindowDialog

@Composable
fun WindowDialogDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var showDialog by remember { mutableStateOf(false) }
            Card {
                TextButton(
                    text = "Show a WindowDialog",
                    onClick = { showDialog = true },
                )
                WindowDialog(
                    title = "WindowDialog Title",
                    summary = "This is a window-level dialog that does not require COUIPopupHost.",
                    show = showDialog,
                    onDismissRequest = { showDialog = false },
                ) {
                    val dismiss = LocalDismissState.current
                    TextButton(
                        text = "Confirm",
                        onClick = { dismiss?.invoke() },
                        modifier = Modifier.fillMaxWidth(),
                        // COUI dialog bar button: a full-cell square-cornered rect with no press
                        // scale (COUIAlertDialogBottomButtonNewNormal drawableRadius=0dp,
                        // scaleEnable=false, stateListAnimator=@null); the only press feedback is
                        // the couiColorPress tint over the whole cell. Metrics come from
                        // COUIButtonBarLayout's measured horizontal bar.
                        pressScaleEnabled = false,
                        cornerRadius = 0.dp,
                        minHeight = DialogDefaults.ButtonBarMinHeight,
                        insideMargin = DialogDefaults.ButtonBarInsideMargin,
                        colors = ButtonDefaults.textButtonColorsBorderless(),
                    )
                }
            }
        }
    }
}
