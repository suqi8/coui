// Copyright 2025, compose-miuix-ui contributors
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.BasicComponent
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.Icon
import com.suqi8.coui.kmp.icon.MiuixIcons
import com.suqi8.coui.kmp.icon.icons.useful.Personal
import com.suqi8.coui.kmp.theme.COUITheme

@Composable
fun BasicComponentDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xfff77062), Color(0xfffe5196)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card {
                BasicComponent(
                    title = "BasicComponent",
                    summary = "Without onClick"
                )
                BasicComponent(
                    title = "Wi-Fi",
                    summary = "Connected to MIUI-WiFi",
                    onClick = { /* Handle click event */ }
                )
                BasicComponent(
                    title = "Nickname",
                    summary = "A brief introduction",
                    leftAction = {
                        Icon(
                            modifier = Modifier.padding(end = 16.dp),
                            imageVector = MiuixIcons.Useful.Personal,
                            contentDescription = "Avatar Icon",
                            tint = COUITheme.colorScheme.onBackground
                        )
                    },
                    onClick = { /* Handle click event */ }
                )
                BasicComponent(
                    title = "Mobile Network",
                    summary = "SIM card not inserted",
                    enabled = false
                )
            }
        }
    }
}
