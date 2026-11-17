// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.NumericKeyboard
import io.github.suqi8.coui.kmp.basic.NumericKeyboardDefaults
import io.github.suqi8.coui.kmp.basic.NumericKeyboardPressEffect
import io.github.suqi8.coui.kmp.basic.SimpleLock
import io.github.suqi8.coui.kmp.basic.SimpleLockDefaults
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.rememberNumericKeyboardState
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch

fun LazyListScope.numericKeyboardSection() {
    item(key = "numericKeyboard") {
        SmallTitle(text = "NumericKeyboard")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            // The Settings keyboard: ripple press, delete on the right, "Done" on the left. Its
            // keys are card-colored discs, so it sits on the page surface instead of the card.
            var input by remember { mutableStateOf("") }
            val state = rememberNumericKeyboardState()
            val scope = rememberCoroutineScope()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(COUITheme.colorScheme.backgroundWithCard)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(text = input.ifEmpty { "Type a number" })
                NumericKeyboard(
                    state = state,
                    onNumberClick = { if (input.length < 12) input += it },
                    leftKey = if (input.isNotEmpty()) NumericKeyboardDefaults.finishKey() else null,
                    rightKey = NumericKeyboardDefaults.deleteKey(),
                    onLeftKeyClick = { input = "" },
                    onRightKeyClick = { input = input.dropLast(1) },
                )
                TextButton(
                    text = "Enter animation",
                    onClick = { scope.launch { state.animateEnter(hasLeftKey = input.isNotEmpty()) } },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            // The lock-screen glass keys over a wallpaper, driving a PIN dot row.
            var pin by remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFF243B55), Color(0xFF141E30))))
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SimpleLock(
                    filledCount = pin.length,
                    colors = SimpleLockDefaults.transparentColors(),
                    glowEffect = true,
                )
                NumericKeyboard(
                    onNumberClick = { pin = if (pin.length < 4) pin + it else it.toString() },
                    rightKey = NumericKeyboardDefaults.deleteKey(),
                    onRightKeyClick = { pin = pin.dropLast(1) },
                    pressEffect = NumericKeyboardPressEffect.Light,
                    colors = NumericKeyboardDefaults.lockScreenColors(),
                )
            }
        }
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            // Disabled: keys ignore touches.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(COUITheme.colorScheme.backgroundWithCard)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                NumericKeyboard(
                    onNumberClick = {},
                    enabled = false,
                )
            }
        }
    }
}
