// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.LinkText
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.appendLink
import io.github.suqi8.coui.kmp.preference.BottomPreference
import io.github.suqi8.coui.kmp.preference.CheckboxWithDividerPreference
import io.github.suqi8.coui.kmp.preference.PagerFooterPreference
import io.github.suqi8.coui.kmp.preference.PagerHeaderPreference
import io.github.suqi8.coui.kmp.preference.SpannablePreference
import io.github.suqi8.coui.kmp.preference.SwitchWithDividerPreference
import io.github.suqi8.coui.kmp.theme.COUITheme

fun LazyListScope.linkAndDividerPreferenceSection() {
    item(key = "linkAndDividerPreference") {
        // The last tapped target, shown in the header so every click has visible feedback.
        val lastAction = remember { mutableStateOf("Nothing tapped yet") }
        val wlanChecked = remember { mutableStateOf(true) }
        val bluetoothChecked = remember { mutableStateOf(false) }
        val syncChecked = remember { mutableStateOf(true) }
        val backupChecked = remember { mutableStateOf(false) }
        val linkClicks = remember { mutableIntStateOf(0) }

        SmallTitle(text = "PagerHeaderPreference")
        PagerHeaderPreference(
            summary = "Tap a row to open its page, or use the control to toggle it. Last action: ${lastAction.value}",
        )

        SmallTitle(text = "SwitchWithDividerPreference")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            CardListGroup {
                item {
                    SwitchWithDividerPreference(
                        checked = wlanChecked.value,
                        onCheckedChange = {
                            wlanChecked.value = it
                            lastAction.value = "WLAN switched ${if (it) "on" else "off"}"
                        },
                        title = "WLAN",
                        summary = if (wlanChecked.value) "Connected to Home-5G" else "Off",
                        onClick = { lastAction.value = "Opened the WLAN page" },
                    )
                }
                item {
                    SwitchWithDividerPreference(
                        checked = bluetoothChecked.value,
                        onCheckedChange = {
                            bluetoothChecked.value = it
                            lastAction.value = "Bluetooth switched ${if (it) "on" else "off"}"
                        },
                        title = "Bluetooth",
                        onClick = { lastAction.value = "Opened the Bluetooth page" },
                    )
                }
                item {
                    SwitchWithDividerPreference(
                        checked = true,
                        onCheckedChange = {},
                        title = "Disabled",
                        summary = "Both parts are disabled",
                        onClick = {},
                        enabled = false,
                    )
                }
            }
        }

        SmallTitle(text = "CheckboxWithDividerPreference")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            CardListGroup {
                item {
                    CheckboxWithDividerPreference(
                        checked = syncChecked.value,
                        onCheckedChange = {
                            syncChecked.value = it
                            lastAction.value = "Photos sync ${if (it) "checked" else "unchecked"}"
                        },
                        title = "Photos",
                        summary = "Tap the row to choose albums",
                        onClick = { lastAction.value = "Opened the Photos sync page" },
                    )
                }
                item {
                    CheckboxWithDividerPreference(
                        checked = backupChecked.value,
                        onCheckedChange = {
                            backupChecked.value = it
                            lastAction.value = "Contacts sync ${if (it) "checked" else "unchecked"}"
                        },
                        title = "Contacts",
                        onClick = { lastAction.value = "Opened the Contacts sync page" },
                    )
                }
            }
        }

        SmallTitle(text = "SpannablePreference")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            CardListGroup {
                item {
                    SpannablePreference(
                        title = "Location services",
                        summary = buildAnnotatedString {
                            append("Apps use your location to show nearby results. ")
                            appendLink("Learn more", onClick = { lastAction.value = "Opened Learn more" })
                        },
                        onClick = { lastAction.value = "Tapped the Location services row" },
                    )
                }
                item {
                    SpannablePreference(
                        title = "Disabled",
                        summary = buildAnnotatedString {
                            append("The link of a disabled row is greyed out: ")
                            appendLink("Privacy policy", onClick = {})
                        },
                        enabled = false,
                    )
                }
            }
        }

        SmallTitle(text = "LinkText")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            LinkText(
                text = buildAnnotatedString {
                    append("Read the ")
                    appendLink("User Agreement", onClick = { linkClicks.intValue++ })
                    append(" and the ")
                    appendLink("Privacy Policy", onClick = { linkClicks.intValue++ })
                    append(". Links clicked: ${linkClicks.intValue}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = COUITheme.colorScheme.labelSecondary,
                style = COUITheme.textStyles.bodyS,
            )
        }

        SmallTitle(text = "PagerFooterPreference + BottomPreference")
        PagerFooterPreference(
            summary = buildAnnotatedString {
                append("Turning off WLAN keeps mobile data on. ")
                appendLink("Network settings", onClick = { lastAction.value = "Opened Network settings" })
            },
        )
        BottomPreference()
    }
}
