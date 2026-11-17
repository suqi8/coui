// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.NumericKeyboard
import io.github.suqi8.coui.kmp.basic.NumericKeyboardDefaults
import io.github.suqi8.coui.kmp.basic.NumericKeyboardPressEffect
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun NumericKeyboardDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        ) {
            var input by remember { mutableStateOf("") }
            // The Settings keys are card-colored discs: they sit on the page surface, not on a card.
            Column(
                modifier = Modifier
                    .background(COUITheme.colorScheme.backgroundWithCard)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(text = input.ifEmpty { "Ripple" })
                NumericKeyboard(
                    onNumberClick = { if (input.length < 12) input += it },
                    rightKey = NumericKeyboardDefaults.deleteKey(),
                    onRightKeyClick = { input = input.dropLast(1) },
                )
            }
            var pin by remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .background(Brush.verticalGradient(listOf(Color(0xFF243B55), Color(0xFF141E30))))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(text = pin.ifEmpty { "Light" }, color = Color.White)
                NumericKeyboard(
                    onNumberClick = { if (pin.length < 12) pin += it },
                    rightKey = NumericKeyboardDefaults.deleteKey(),
                    onRightKeyClick = { pin = pin.dropLast(1) },
                    pressEffect = NumericKeyboardPressEffect.Light,
                    colors = NumericKeyboardDefaults.lockScreenColors(),
                )
            }
        }
    }
}
