// Copyright 2026, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.Badge
import com.suqi8.coui.kmp.basic.BadgedBox
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.Icon
import com.suqi8.coui.kmp.basic.Text
import com.suqi8.coui.kmp.icon.COUIIcons
import com.suqi8.coui.kmp.icon.extended.Email
import com.suqi8.coui.kmp.icon.extended.Favorites
import com.suqi8.coui.kmp.icon.extended.Messages
import com.suqi8.coui.kmp.icon.extended.Settings

@Composable
fun BadgeDemo() {
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
            Card {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        BadgedBox(badge = { Badge() }) {
                            Icon(
                                imageVector = COUIIcons.Messages,
                                contentDescription = "Messages",
                                modifier = Modifier.size(28.dp),
                            )
                        }
                        BadgedBox(badge = { Badge { Text("8") } }) {
                            Icon(
                                imageVector = COUIIcons.Email,
                                contentDescription = "Email",
                                modifier = Modifier.size(28.dp),
                            )
                        }
                        BadgedBox(badge = { Badge { Text("99+") } }) {
                            Icon(
                                imageVector = COUIIcons.Settings,
                                contentDescription = "Settings",
                                modifier = Modifier.size(28.dp),
                            )
                        }
                        BadgedBox(badge = { Badge { Text("5") } }) {
                            Icon(
                                imageVector = COUIIcons.Favorites,
                                contentDescription = "Favorites",
                                modifier = Modifier.size(28.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
