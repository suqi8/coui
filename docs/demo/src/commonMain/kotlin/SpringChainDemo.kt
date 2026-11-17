// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.effect.rememberSpringChainState
import io.github.suqi8.coui.kmp.effect.springChain
import io.github.suqi8.coui.kmp.effect.springChainItem
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun SpringChainDemo() {
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
            Text(
                text = "Touch-drag past the top or bottom edge, then let go",
                color = COUITheme.colorScheme.labelSecondaryVariant,
            )
            val listState = rememberLazyListState()
            val chainState = rememberSpringChainState(listState)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .springChain(chainState),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(20) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .springChainItem(chainState, row = index),
                        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        Text(text = "Row ${index + 1}")
                    }
                }
            }
        }
    }
}
