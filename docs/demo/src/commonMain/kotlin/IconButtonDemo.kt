// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import com.suqi8.coui.kmp.basic.Card
import com.suqi8.coui.kmp.basic.Icon
import com.suqi8.coui.kmp.basic.IconButton
import com.suqi8.coui.kmp.icon.MiuixIcons
import com.suqi8.coui.kmp.icon.icons.useful.ImmersionMore
import com.suqi8.coui.kmp.icon.icons.useful.Like
import com.suqi8.coui.kmp.theme.COUITheme

@Composable
fun IconButtonDemo() {
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
            Card(
                insideMargin = PaddingValues(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                ) {
                    IconButton(
                        modifier = Modifier,
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Useful.ImmersionMore,
                            tint = COUITheme.colorScheme.onBackground,
                            contentDescription = "More"
                        )
                    }
                    IconButton(
                        modifier = Modifier,
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Useful.Like,
                            contentDescription = "Like"
                        )
                    }
                    IconButton(
                        modifier = Modifier,
                        onClick = {},
                        enabled = false
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Useful.ImmersionMore,
                            tint = COUITheme.colorScheme.disabledOnSecondaryVariant,
                            contentDescription = "More"
                        )
                    }
                    IconButton(
                        modifier = Modifier,
                        onClick = {},
                        enabled = false
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Useful.Like,
                            tint = COUITheme.colorScheme.disabledOnSecondaryVariant,
                            contentDescription = "Like"
                        )
                    }
                }
            }
        }
    }
}
