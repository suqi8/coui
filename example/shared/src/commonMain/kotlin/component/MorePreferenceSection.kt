// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Backup
import io.github.suqi8.coui.kmp.icon.extended.Location
import io.github.suqi8.coui.kmp.icon.extended.Lock
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.preference.AppInfoPreference
import io.github.suqi8.coui.kmp.preference.CardEntranceItem
import io.github.suqi8.coui.kmp.preference.CardEntrancePreference
import io.github.suqi8.coui.kmp.preference.CardEntranceType
import io.github.suqi8.coui.kmp.preference.CardInstructionIllustration
import io.github.suqi8.coui.kmp.preference.CardInstructionPage
import io.github.suqi8.coui.kmp.preference.CardInstructionPreference
import io.github.suqi8.coui.kmp.preference.EditTextPreference
import io.github.suqi8.coui.kmp.preference.InputPreference
import io.github.suqi8.coui.kmp.preference.StepperPreference
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * Demos of the settings-item extras: InputPreference, EditTextPreference, StepperPreference,
 * CardInstructionPreference, CardEntrancePreference and AppInfoPreference.
 */
fun LazyListScope.morePreferenceSection() {
    item(key = "appInfoPreference") {
        SmallTitle(text = "AppInfoPreference")
        AppInfoPreference(
            appName = "COUI Gallery",
            appVersion = "Version 17.0.0",
            icon = { DemoAppIcon() },
            copyText = "Copy",
            onCopyClick = {},
            modifier = Modifier.padding(bottom = 16.dp),
        )
    }
    item(key = "inputPreference") {
        var deviceName by remember { mutableStateOf("My Phone") }
        var hotspotName by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        SmallTitle(text = "InputPreference")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            CardListGroup {
                item {
                    InputPreference(
                        value = deviceName,
                        onValueChange = { deviceName = it },
                        title = "Device name",
                        hint = "Enter a name",
                    )
                }
                item {
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    InputPreference(
                        value = hotspotName,
                        onValueChange = { hotspotName = it },
                        hint = "Hotspot name (no title)",
                        maxCount = 20,
                        errorMessage = if (hotspotName.contains(' ')) "Spaces are not allowed" else "",
                    )
                }
                item {
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    InputPreference(
                        value = password,
                        onValueChange = { password = it },
                        title = "Password",
                        hint = "At least 8 characters",
                        showPasswordToggle = true,
                    )
                }
            }
        }
    }
    item(key = "editTextPreference") {
        var nickname by remember { mutableStateOf("Suqi") }
        var signature by remember { mutableStateOf("") }

        SmallTitle(text = "EditTextPreference")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            CardListGroup {
                item {
                    EditTextPreference(
                        value = nickname,
                        onValueChange = { nickname = it },
                        title = "Nickname",
                        positiveButtonText = "OK",
                        negativeButtonText = "Cancel",
                        hint = "Enter a nickname",
                    )
                }
                item {
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    EditTextPreference(
                        value = signature,
                        onValueChange = { signature = it },
                        title = "Signature",
                        summary = signature.ifEmpty { "Empty input allowed" },
                        positiveButtonText = "OK",
                        negativeButtonText = "Cancel",
                        dialogMessage = "Shown under your name.",
                        showValue = false,
                        supportEmptyInput = true,
                    )
                }
                item {
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    EditTextPreference(
                        value = "Locked",
                        onValueChange = {},
                        title = "Disabled EditTextPreference",
                        positiveButtonText = "OK",
                        negativeButtonText = "Cancel",
                        enabled = false,
                    )
                }
            }
        }
    }
    item(key = "stepperPreference") {
        var copies by remember { mutableIntStateOf(1) }
        var fontScale by remember { mutableIntStateOf(100) }

        SmallTitle(text = "StepperPreference")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            CardListGroup {
                item {
                    StepperPreference(
                        value = copies,
                        onValueChange = { copies = it },
                        title = "Copies",
                        minValue = 1,
                        maxValue = 10,
                    )
                }
                item {
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    StepperPreference(
                        value = fontScale,
                        onValueChange = { fontScale = it },
                        title = "Font scale",
                        summary = "Steps of 10, from 50 to 200",
                        minValue = 50,
                        maxValue = 200,
                        step = 10,
                    )
                }
                item {
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    StepperPreference(
                        value = 3,
                        onValueChange = {},
                        title = "Disabled StepperPreference",
                        enabled = false,
                    )
                }
            }
        }
    }
    item(key = "cardInstructionPreference") {
        SmallTitle(text = "CardInstructionPreference")
        CardInstructionPreference(
            pages = demoInstructionPages(),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        )
    }
    item(key = "cardEntrancePreference") {
        SmallTitle(text = "CardEntrancePreference")
        CardEntrancePreference(
            items = demoEntranceItems(),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        )
    }
}

@Composable
private fun demoInstructionPages(): List<CardInstructionPage> {
    val primary = COUITheme.colorScheme.primary
    return remember(primary) {
        listOf(
            CardInstructionPage(
                illustrations = listOf(
                    CardInstructionIllustration(content = { DemoIllustration(primary, CircleShape) }),
                ),
                title = "Swipe up from the bottom",
                summary = "Swipe up from the bottom edge of the screen to go back to the home screen.",
            ),
            CardInstructionPage(
                illustrations = listOf(
                    CardInstructionIllustration(
                        content = { DemoIllustration(primary, RoundedCornerShape(16.dp)) },
                        title = "Left edge",
                    ),
                    CardInstructionIllustration(
                        content = { DemoIllustration(primary.copy(alpha = 0.6f), RoundedCornerShape(16.dp)) },
                        title = "Right edge",
                    ),
                ),
                title = "Swipe in from either side",
                summary = "Swipe in from the left or right edge to go back to the previous screen.",
            ),
            CardInstructionPage(
                illustrations = listOf(
                    CardInstructionIllustration(content = { DemoIllustration(primary.copy(alpha = 0.4f), CircleShape) }),
                ),
                summary = "A page without a title caps its text block at 64dp and fades it while it scrolls. " +
                    "This description is long enough to scroll: keep reading to see the fading edge " +
                    "appear at the top once you scroll down, and at the bottom while more text follows.",
            ),
        )
    }
}

@Composable
private fun DemoIllustration(color: Color, shape: Shape) {
    Box(
        modifier = Modifier
            .size(96.dp)
            .background(color, shape),
    )
}

@Composable
private fun demoEntranceItems(): List<CardEntranceItem> = remember {
    listOf(
        CardEntranceItem(
            title = "Privacy",
            summary = "Permissions",
            icon = { DemoEntranceIcon(COUIIcons.Lock) },
            onClick = {},
        ),
        CardEntranceItem(
            title = "Location",
            summary = "On",
            icon = { DemoEntranceIcon(COUIIcons.Location) },
            statusOn = true,
            onClick = {},
        ),
        CardEntranceItem(
            title = "Backup & restore",
            summary = "Last backup: yesterday",
            type = CardEntranceType.Large,
            icon = { DemoEntranceIcon(COUIIcons.Backup) },
            onClick = {},
        ),
        CardEntranceItem(
            title = "More settings",
            icon = { DemoEntranceIcon(COUIIcons.Settings) },
            onClick = {},
        ),
        CardEntranceItem(
            title = "Disabled",
            summary = "Unavailable",
            icon = { DemoEntranceIcon(COUIIcons.Settings) },
            enabled = false,
        ),
    )
}

@Composable
private fun DemoEntranceIcon(imageVector: ImageVector) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(COUITheme.colorScheme.primary, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = COUITheme.colorScheme.onPrimary,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun DemoAppIcon() {
    val primary = COUITheme.colorScheme.primary
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(primary, primary.copy(alpha = 0.55f)))),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = COUIIcons.Settings,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp),
        )
    }
}
