// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.AlphabetIndexBar
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun AlphabetIndexBarDemo() {
    var selected by remember { mutableStateOf("-") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
    ) {
        Text(
            text = "Selected: $selected",
            color = COUITheme.colorScheme.onBackground,
            style = COUITheme.textStyles.title3,
            modifier = Modifier.align(Alignment.Center),
        )
        AlphabetIndexBar(
            onKeySelected = { selected = it },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .padding(end = 4.dp),
        )
    }
}
