// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.HorizontalDivider
import com.suqi8.coui.kmp.basic.SmallTitle
import com.suqi8.coui.kmp.basic.Switch
import com.suqi8.coui.kmp.basic.Text
import com.suqi8.coui.kmp.preference.SwitchPreference
import com.suqi8.coui.kmp.theme.COUITheme

fun LazyListScope.switchSection() {
    item(key = "switch") {
        val switch = remember { mutableStateOf(false) }
        val switchTrue = remember { mutableStateOf(true) }
        val superSwitch = remember { mutableStateOf("false") }
        val superSwitchState = remember { mutableStateOf(false) }
        val superSwitchAnimState = remember { mutableStateOf(false) }

        SmallTitle(text = "Switch")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Switch(
                    checked = switch.value,
                    onCheckedChange = { switch.value = it },
                )
                Switch(
                    checked = switchTrue.value,
                    onCheckedChange = { switchTrue.value = it },
                    modifier = Modifier.padding(start = 6.dp),
                )
                Switch(
                    checked = false,
                    onCheckedChange = { },
                    modifier = Modifier.padding(start = 6.dp),
                    enabled = false,
                )
                Switch(
                    checked = true,
                    onCheckedChange = { },
                    modifier = Modifier.padding(start = 6.dp),
                    enabled = false,
                )
                Switch(
                    checked = switchTrue.value,
                    onCheckedChange = { switchTrue.value = it },
                    modifier = Modifier.padding(start = 6.dp),
                    isLoading = true,
                )
            }
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            SwitchPreference(
                title = "Switch",
                summary = "Click to expand a Switch",
                checked = superSwitchAnimState.value,
                onCheckedChange = {
                    superSwitchAnimState.value = it
                },
            )

            AnimatedVisibility(
                visible = superSwitchAnimState.value,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                // Divider lives inside the animated block so it only shows when expanded.
                Column {
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    SwitchPreference(
                        title = "Switch",
                        checked = superSwitchState.value,
                        endActions = {
                            Text(
                                text = superSwitch.value,
                                color = COUITheme.colorScheme.onSurfaceVariantActions,
                            )
                        },
                        onCheckedChange = {
                            superSwitchState.value = it
                            superSwitch.value = "$it"
                        },
                    )
                }
            }
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            SwitchPreference(
                title = "Disabled Switch",
                checked = true,
                enabled = false,
                onCheckedChange = {},
            )
        }
    }
}
