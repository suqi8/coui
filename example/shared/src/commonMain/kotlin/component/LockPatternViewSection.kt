// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.LockPatternDisplayMode
import io.github.suqi8.coui.kmp.basic.LockPatternView
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.patternToString
import io.github.suqi8.coui.kmp.basic.rememberLockPatternState
import io.github.suqi8.coui.kmp.basic.stringToPattern
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun LazyListScope.lockPatternViewSection() {
    item(key = "lockPatternView") {
        SmallTitle(text = "LockPatternView")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            // The correct pattern is an "L": 1-4-7-8-9. Anything else flashes red and clears.
            val state = rememberLockPatternState()
            val scope = rememberCoroutineScope()
            var message by remember { mutableStateOf("Draw an L (1-4-7-8-9)") }
            var stealth by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = message)
                LockPatternView(
                    state = state,
                    inStealthMode = stealth,
                    onPatternStart = { message = "Release when done" },
                    onPatternDetected = { pattern ->
                        val code = patternToString(pattern)
                        if (code == "14789") {
                            message = "Correct"
                            scope.launch {
                                delay(600)
                                state.clearPattern()
                            }
                        } else {
                            message = "Wrong pattern ($code)"
                            state.setDisplayMode(LockPatternDisplayMode.Wrong)
                            state.clearPattern()
                        }
                    },
                )
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TextButton(
                        text = "Replay",
                        onClick = { state.setPattern(LockPatternDisplayMode.Animate, stringToPattern("14789")) },
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(
                        text = "Enter anim",
                        onClick = {
                            state.clearPattern()
                            scope.launch { state.animateEnter() }
                        },
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(
                        text = if (stealth) "Show path" else "Stealth",
                        onClick = { stealth = !stealth },
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TextButton(
                        text = "Fingerprint OK",
                        onClick = {
                            message = "Fingerprint match"
                            state.setDisplayMode(LockPatternDisplayMode.FingerprintMatch)
                        },
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(
                        text = "Fingerprint fail",
                        onClick = {
                            message = "Fingerprint no match"
                            state.setDisplayMode(LockPatternDisplayMode.FingerprintNoMatch)
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            // Disabled: touches are ignored.
            LockPatternView(
                onPatternDetected = {},
                enabled = false,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp),
            )
        }
    }
}
