// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.ButtonDefaults
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardDefaults
import io.github.suqi8.coui.kmp.basic.CustomSnackbar
import io.github.suqi8.coui.kmp.basic.Slider
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.TwoTextButton
import io.github.suqi8.coui.kmp.basic.TwoTextButtonDefaults
import io.github.suqi8.coui.kmp.effect.rememberSpringChainState
import io.github.suqi8.coui.kmp.effect.springChain
import io.github.suqi8.coui.kmp.effect.springChainItem
import io.github.suqi8.coui.kmp.theme.COUITheme

fun LazyListScope.springChainAndCardSection() {
    item(key = "springChain") {
        SmallTitle(text = "SpringChain")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Drag past the top or bottom edge, then let go",
                    color = COUITheme.colorScheme.labelSecondaryVariant,
                    style = COUITheme.textStyles.bodyM,
                )
                SpringChainListDemo()
                SpringChainGridDemo()
            }
        }
    }
    item(key = "shadowCard") {
        SmallTitle(text = "Card (shadow)")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                insideMargin = PaddingValues(16.dp),
                shadow = CardDefaults.shadow(),
            ) {
                Text(text = "Shadow card")
                Text(
                    text = "12dp shadow, offset 2dp downwards",
                    color = COUITheme.colorScheme.labelSecondaryVariant,
                    style = COUITheme.textStyles.bodyM,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    insideMargin = PaddingValues(16.dp),
                    shadow = CardDefaults.shadow(hideTop = true),
                    onClick = {},
                ) {
                    Text(text = "Top hidden")
                }
                Card(
                    modifier = Modifier.weight(1f),
                    insideMargin = PaddingValues(16.dp),
                    shadow = CardDefaults.shadow(size = 24.dp, angle = 45, offset = 6.dp),
                ) {
                    Text(text = "Angle 45")
                }
            }
        }
    }
    item(key = "customSnackbar") {
        SmallTitle(text = "CustomSnackbar")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            CustomSnackbarDemo()
        }
    }
    item(key = "twoTextButton") {
        SmallTitle(text = "TwoTextButton")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            TwoTextButtonDemo()
        }
    }
}

@Composable
private fun SpringChainListDemo() {
    val listState = rememberLazyListState()
    val chainState = rememberSpringChainState(listState)
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .springChain(chainState),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(20) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .springChainItem(chainState, row = index),
                cornerRadius = 16.dp,
                insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                colors = CardDefaults.defaultColors(color = COUITheme.colorScheme.cardPressed),
            ) {
                Text(text = "Row ${index + 1}")
            }
        }
    }
}

@Composable
private fun SpringChainGridDemo() {
    val gridState = rememberLazyGridState()
    val chainState = rememberSpringChainState(gridState)
    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMNS),
        state = gridState,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .springChain(chainState),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(24) { index ->
            Card(
                modifier = Modifier
                    .height(56.dp)
                    .springChainItem(chainState, row = index / GRID_COLUMNS),
                cornerRadius = 16.dp,
                colors = CardDefaults.defaultColors(color = COUITheme.colorScheme.cardPressed),
            ) {
                Box(modifier = Modifier.height(56.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = "${index + 1}")
                }
            }
        }
    }
}

@Composable
private fun CustomSnackbarDemo() {
    var visible by remember { mutableStateOf(false) }
    var dismissCount by remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextButton(
            text = if (visible) "Showing (dismissed $dismissCount)" else "Show custom snackbar (dismissed $dismissCount)",
            onClick = { visible = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = !visible,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            if (visible) {
                CustomSnackbar(
                    onDismiss = {
                        visible = false
                        dismissCount++
                    },
                    touchSlidable = true,
                    pressFeedback = true,
                    autoDismissMillis = 4000L,
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        insideMargin = PaddingValues(16.dp),
                        colors = CardDefaults.defaultColors(color = COUITheme.colorScheme.cardPressed),
                        shadow = CardDefaults.shadow(),
                    ) {
                        Text(text = "Custom content")
                        Text(
                            text = "Drag down to dismiss; idle 4s dismisses too",
                            color = COUITheme.colorScheme.labelSecondaryVariant,
                            style = COUITheme.textStyles.bodyM,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TwoTextButtonDemo() {
    var progress by remember { mutableFloatStateOf(0f) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Page offset ${(progress * 100).toInt()}%",
            color = COUITheme.colorScheme.labelSecondaryVariant,
            style = COUITheme.textStyles.bodyM,
        )
        Slider(
            value = progress,
            onValueChange = { progress = it },
        )
        TwoTextButton(
            startText = "Next",
            nextText = "Get started",
            startTextAlpha = { TwoTextButtonDefaults.outgoingTextAlpha(progress) },
            nextTextAlpha = { TwoTextButtonDefaults.incomingTextAlpha(progress) },
            onClick = { progress = if (progress < 0.5f) 1f else 0f },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColorsPrimary(),
        )
    }
}

private const val GRID_COLUMNS = 4
