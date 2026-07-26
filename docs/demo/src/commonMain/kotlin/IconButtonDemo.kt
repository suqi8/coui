// Copyright 2025, compose-coui-ui contributors
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
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Favorites
import io.github.suqi8.coui.kmp.icon.extended.More
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun IconButtonDemo() {
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
            Card(
                insideMargin = PaddingValues(16.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                ) {
                    IconButton(
                        modifier = Modifier,
                        onClick = {},
                    ) {
                        Icon(
                            imageVector = COUIIcons.More,
                            tint = COUITheme.colorScheme.onBackground,
                            contentDescription = "More",
                        )
                    }
                    IconButton(
                        modifier = Modifier,
                        onClick = {},
                    ) {
                        Icon(
                            imageVector = COUIIcons.Favorites,
                            contentDescription = "Favorites",
                        )
                    }
                    IconButton(
                        modifier = Modifier,
                        onClick = {},
                        enabled = false,
                    ) {
                        Icon(
                            imageVector = COUIIcons.More,
                            tint = COUITheme.colorScheme.disabledOnSecondaryVariant,
                            contentDescription = "More",
                        )
                    }
                    IconButton(
                        modifier = Modifier,
                        onClick = {},
                        enabled = false,
                    ) {
                        Icon(
                            imageVector = COUIIcons.Favorites,
                            tint = COUITheme.colorScheme.disabledOnSecondaryVariant,
                            contentDescription = "Favorites",
                        )
                    }
                }
            }
        }
    }
}
