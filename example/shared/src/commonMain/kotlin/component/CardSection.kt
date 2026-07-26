// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suqi8.coui.kmp.basic.ButtonDefaults
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.SmallTitle
import com.suqi8.coui.kmp.basic.Text
import com.suqi8.coui.kmp.basic.TextButton
import com.suqi8.coui.kmp.overlay.OverlayDialog
import com.suqi8.coui.kmp.theme.COUITheme
import com.suqi8.coui.kmp.utils.PressFeedbackType

fun LazyListScope.cardSection() {
    item(key = "card") {
        SmallTitle(text = "Card")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            insideMargin = PaddingValues(16.dp),
            onClick = { println("Card click") },
        ) {
            Text(
                color = COUITheme.colorScheme.onSurface,
                text = "Card",
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                color = COUITheme.colorScheme.onSurfaceVariantSummary,
                text = "PressFeedbackType: Tint (default)",
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Card(
                modifier = Modifier.weight(1f),
                insideMargin = PaddingValues(16.dp),
                pressFeedbackType = PressFeedbackType.Sink,
                onClick = { println("Card click") },
                content = {
                    Text(
                        color = COUITheme.colorScheme.onSurface,
                        text = "Card",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        color = COUITheme.colorScheme.onSurfaceVariantSummary,
                        text = "PressFeedback\nType: Sink",
                        style = COUITheme.textStyles.paragraph,
                    )
                },
            )
            Card(
                modifier = Modifier.weight(1f),
                insideMargin = PaddingValues(16.dp),
                pressFeedbackType = PressFeedbackType.Tilt,
                onLongPress = { println("Card long press") },
                content = {
                    Text(
                        color = COUITheme.colorScheme.onSurface,
                        text = "Card",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        color = COUITheme.colorScheme.onSurfaceVariantSummary,
                        text = "PressFeedback\nType: Tilt",
                        style = COUITheme.textStyles.paragraph,
                    )
                },
            )
        }
        LongPressHoldDownCardDemo()
    }
}

@Composable
private fun LongPressHoldDownCardDemo() {
    var showDialog by remember { mutableStateOf(false) }
    var holdDown by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        insideMargin = PaddingValues(16.dp),
        holdDownState = holdDown,
        onLongPress = {
            showDialog = true
            holdDown = true
        },
        content = {
            Text(
                color = COUITheme.colorScheme.onSurface,
                text = "Card",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                color = COUITheme.colorScheme.onSurfaceVariantSummary,
                text = "Long press to show dialog",
                style = COUITheme.textStyles.paragraph,
            )
        },
    )

    OverlayDialog(
        show = showDialog,
        title = "Long Press Action",
        summary = "Triggered by long pressing the card.",
        onDismissRequest = { showDialog = false },
        onDismissFinished = { holdDown = false },
        content = {
            Row(
                // The dialog content slot is unpadded (COUI button bars span the panel), so
                // COUI assignment-style pill buttons keep 24dp side and bottom margins.
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(
                    text = "Cancel",
                    onClick = { showDialog = false },
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(20.dp))
                TextButton(
                    text = "Confirm",
                    onClick = { showDialog = false },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                )
            }
        },
    )
}
