// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.LockPatternDisplayMode
import io.github.suqi8.coui.kmp.basic.LockPatternView
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.patternToString
import io.github.suqi8.coui.kmp.basic.rememberLockPatternState
import io.github.suqi8.coui.kmp.basic.stringToPattern
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_value_format
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_haptic
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_pattern_view_clear
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_pattern_view_correct
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_pattern_view_display_mode
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_pattern_view_display_mode_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_pattern_view_draw_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_pattern_view_enter_animation
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_pattern_view_fingerprint_fail
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_pattern_view_fingerprint_match
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_pattern_view_fingerprint_no_match
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_pattern_view_fingerprint_ok
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_pattern_view_release
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_pattern_view_replay
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_pattern_view_show_path
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_pattern_view_stealth
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_pattern_view_stealth_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_pattern_view_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_pattern_view_variant_unlock
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_pattern_view_wrong
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal val LockPatternViewDemo = Demo(
    id = "lock_pattern_view",
    apiName = "LockPatternView",
    title = Res.string.demo_lock_pattern_view_title,
    category = DemoCategory.Input,
    page = DemoPage.Variants { lockPatternViewPage() },
    keywords = listOf("pattern", "手势", "图案密码", "COUILockPatternView"),
)

/** The correct pattern of the samples: an "L", 1-4-7-8-9. */
private const val SAMPLE_PATTERN = "14789"

/** The status line above the unlock sample. */
private enum class PatternMessage(val text: StringResource) {
    Hint(Res.string.demo_lock_pattern_view_draw_hint),
    Release(Res.string.demo_lock_pattern_view_release),
    Correct(Res.string.demo_lock_pattern_view_correct),
    Wrong(Res.string.demo_lock_pattern_view_wrong),
    FingerprintMatch(Res.string.demo_lock_pattern_view_fingerprint_match),
    FingerprintNoMatch(Res.string.demo_lock_pattern_view_fingerprint_no_match),
}

private fun LazyListScope.lockPatternViewPage() {
    variant(key = "unlock", title = Res.string.demo_lock_pattern_view_variant_unlock) {
        UnlockSample()
    }
    playground {
        val state = rememberLockPatternState()
        val scope = rememberCoroutineScope()
        var stealth by rememberSaveable { mutableStateOf(false) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        var haptic by rememberSaveable { mutableStateOf(true) }
        var modeIndex by rememberSaveable { mutableIntStateOf(LockPatternDisplayMode.Correct.ordinal) }
        var drawn by remember { mutableStateOf("") }

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            Text(
                text = if (drawn.isEmpty()) {
                    stringResource(Res.string.demo_lock_pattern_view_draw_hint)
                } else {
                    stringResource(Res.string.common_value_format, drawn)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            LockPatternView(
                onPatternDetected = { drawn = patternToString(it) },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                state = state,
                enabled = enabled,
                inStealthMode = stealth,
                hapticFeedbackEnabled = haptic,
                onPatternCellAdded = { drawn = patternToString(it) },
            )
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = LockPatternDisplayMode.entries.map { it.name },
                    selectedIndex = modeIndex,
                    title = stringResource(Res.string.demo_lock_pattern_view_display_mode),
                    summary = stringResource(Res.string.demo_lock_pattern_view_display_mode_summary),
                    onSelectedIndexChange = {
                        modeIndex = it
                        drawn = SAMPLE_PATTERN
                        state.setPattern(LockPatternDisplayMode.entries[it], stringToPattern(SAMPLE_PATTERN))
                    },
                )
            }
            item {
                SwitchPreference(
                    checked = stealth,
                    onCheckedChange = { stealth = it },
                    title = stringResource(Res.string.demo_lock_pattern_view_stealth),
                    summary = stringResource(Res.string.demo_lock_pattern_view_stealth_summary),
                )
            }
            item {
                SwitchPreference(
                    checked = haptic,
                    onCheckedChange = { haptic = it },
                    title = stringResource(Res.string.demo_input_haptic),
                )
            }
            item {
                SwitchPreference(
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                    title = stringResource(Res.string.common_enabled),
                )
            }
            item {
                ArrowPreference(
                    title = stringResource(Res.string.demo_lock_pattern_view_enter_animation),
                    onClick = {
                        state.clearPattern()
                        drawn = ""
                        scope.launch { state.animateEnter() }
                    },
                )
            }
            item {
                ArrowPreference(
                    title = stringResource(Res.string.demo_lock_pattern_view_clear),
                    onClick = {
                        state.clearPattern()
                        drawn = ""
                    },
                )
            }
        }
    }
    variant(key = "disabled", title = Res.string.common_disabled) {
        // Disabled: touches are ignored.
        LockPatternView(
            onPatternDetected = {},
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp),
            enabled = false,
        )
    }
}

/**
 * The correct pattern is an "L" (1-4-7-8-9). Anything else flashes red and clears.
 */
@Composable
private fun UnlockSample() {
    val state = rememberLockPatternState()
    val scope = rememberCoroutineScope()
    var message by remember { mutableStateOf(PatternMessage.Hint) }
    var wrongCode by remember { mutableStateOf("") }
    var stealth by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = if (message == PatternMessage.Wrong) {
                stringResource(message.text, wrongCode)
            } else {
                stringResource(message.text)
            },
        )
        LockPatternView(
            onPatternDetected = { pattern ->
                val code = patternToString(pattern)
                if (code == SAMPLE_PATTERN) {
                    message = PatternMessage.Correct
                    scope.launch {
                        delay(600)
                        state.clearPattern()
                    }
                } else {
                    wrongCode = code
                    message = PatternMessage.Wrong
                    state.setDisplayMode(LockPatternDisplayMode.Wrong)
                    state.clearPattern()
                }
            },
            state = state,
            inStealthMode = stealth,
            onPatternStart = { message = PatternMessage.Release },
        )
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextButton(
                text = stringResource(Res.string.demo_lock_pattern_view_replay),
                onClick = { state.setPattern(LockPatternDisplayMode.Animate, stringToPattern(SAMPLE_PATTERN)) },
                modifier = Modifier.weight(1f),
            )
            TextButton(
                text = stringResource(Res.string.demo_lock_pattern_view_enter_animation),
                onClick = {
                    state.clearPattern()
                    scope.launch { state.animateEnter() }
                },
                modifier = Modifier.weight(1f),
            )
            TextButton(
                text = stringResource(if (stealth) Res.string.demo_lock_pattern_view_show_path else Res.string.demo_lock_pattern_view_stealth),
                onClick = { stealth = !stealth },
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextButton(
                text = stringResource(Res.string.demo_lock_pattern_view_fingerprint_ok),
                onClick = {
                    message = PatternMessage.FingerprintMatch
                    state.setDisplayMode(LockPatternDisplayMode.FingerprintMatch)
                },
                modifier = Modifier.weight(1f),
            )
            TextButton(
                text = stringResource(Res.string.demo_lock_pattern_view_fingerprint_fail),
                onClick = {
                    message = PatternMessage.FingerprintNoMatch
                    state.setDisplayMode(LockPatternDisplayMode.FingerprintNoMatch)
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}
