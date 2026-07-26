// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.ButtonDefaults
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.SmallTitle
import com.suqi8.coui.kmp.basic.SnackbarDuration
import com.suqi8.coui.kmp.basic.SnackbarHostState
import com.suqi8.coui.kmp.basic.SnackbarResult
import com.suqi8.coui.kmp.basic.TextButton
import kotlinx.coroutines.launch

fun LazyListScope.snackbarSection(snackbarHostState: SnackbarHostState) {
    item(key = "snackbar") {
        SmallTitle(text = "Snackbar")
        val scope = rememberCoroutineScope()
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TextButton(
                        text = "Dismiss oldest",
                        onClick = {
                            scope.launch {
                                snackbarHostState.oldestSnackbarData()?.dismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(
                        text = "Dismiss newest",
                        onClick = {
                            scope.launch {
                                snackbarHostState.newestSnackbarData()?.dismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TextButton(
                        text = "Short (4s)",
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("This is a short message")
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(
                        text = "Long (10s)",
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "This is a long message to display more text content",
                                    duration = SnackbarDuration.Long,
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TextButton(
                        text = "Custom (2s)",
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "This message will last for 2 seconds",
                                    duration = SnackbarDuration.Custom(2000L),
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                    var text by remember { mutableStateOf("Action") }
                    TextButton(
                        text = text,
                        onClick = {
                            scope.launch {
                                text = "Action: Alive"
                                val result = snackbarHostState.showSnackbar(
                                    message = "This message has an action",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Short,
                                )
                                text = when (result) {
                                    SnackbarResult.ActionPerformed -> "Action: Undo"
                                    else -> "Action: Expired"
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TextButton(
                        text = "Dismissible",
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "This message can be removed via the close button",
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Long,
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(
                        text = "Indefinite",
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Indefinite message, dismiss manually",
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Indefinite,
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
