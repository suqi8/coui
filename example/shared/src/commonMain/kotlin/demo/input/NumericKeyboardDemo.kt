// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.input

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.NumericKeyboard
import io.github.suqi8.coui.kmp.basic.NumericKeyboardDefaults
import io.github.suqi8.coui.kmp.basic.NumericKeyboardPressEffect
import io.github.suqi8.coui.kmp.basic.SimpleLock
import io.github.suqi8.coui.kmp.basic.SimpleLockDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.rememberNumericKeyboardState
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_delete
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_done
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_size
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_haptic
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_none
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_numeric_keyboard_enter_animation
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_numeric_keyboard_left_key
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_numeric_keyboard_placeholder
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_numeric_keyboard_press_effect
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_numeric_keyboard_press_effect_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_numeric_keyboard_right_key
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_numeric_keyboard_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_numeric_keyboard_variant_lock_screen
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_numeric_keyboard_variant_settings
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

internal val NumericKeyboardDemo = Demo(
    id = "numeric_keyboard",
    apiName = "NumericKeyboard",
    title = Res.string.demo_numeric_keyboard_title,
    category = DemoCategory.Input,
    page = DemoPage.Variants { numericKeyboardPage() },
    keywords = listOf("keypad", "数字键盘", "COUINumericKeyboard"),
)

/** The longest number the settings keyboard samples accept. */
private const val MAX_DIGITS = 12

/** The sample lock screen wallpaper. */
internal val LockScreenWallpaper = Brush.verticalGradient(listOf(Color(0xFF243B55), Color(0xFF141E30)))

private fun LazyListScope.numericKeyboardPage() {
    variant(key = "settings", title = Res.string.demo_numeric_keyboard_variant_settings) {
        SettingsKeyboard()
    }
    playground {
        var effectIndex by rememberSaveable { mutableIntStateOf(NumericKeyboardPressEffect.Ripple.ordinal) }
        var leftKeyIndex by rememberSaveable { mutableIntStateOf(1) }
        var rightKeyIndex by rememberSaveable { mutableIntStateOf(1) }
        var keySize by rememberSaveable { mutableFloatStateOf(NumericKeyboardDefaults.KeySize.value) }
        var haptic by rememberSaveable { mutableStateOf(true) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        var input by rememberSaveable { mutableStateOf("") }
        val effect = NumericKeyboardPressEffect.entries[effectIndex]
        val lockScreen = effect == NumericKeyboardPressEffect.Light

        val doneText = stringResource(Res.string.common_done)
        val deleteText = stringResource(Res.string.common_delete)
        val leftKey = if (leftKeyIndex == 1) NumericKeyboardDefaults.finishKey(doneText) else null
        val rightKey = if (rightKeyIndex == 1) NumericKeyboardDefaults.deleteKey(deleteText) else null
        val text = input.ifEmpty { stringResource(Res.string.demo_numeric_keyboard_placeholder) }
        // The Light keys are the lock-screen glass: show them over a wallpaper. The Ripple keys
        // are card-coloured discs: show them on the page surface.
        val surface = if (lockScreen) LockScreenWallpaper else SolidColor(COUITheme.colorScheme.backgroundWithCard)

        PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
            WallpaperSurface(
                brush = surface,
                contentPadding = PaddingValues(vertical = if (lockScreen) 24.dp else 16.dp),
            ) {
                Text(text = text, color = if (lockScreen) Color.White else Color.Unspecified)
                NumericKeyboard(
                    onNumberClick = { if (input.length < MAX_DIGITS) input += it },
                    enabled = enabled,
                    hapticFeedbackEnabled = haptic,
                    leftKey = leftKey,
                    rightKey = rightKey,
                    pressEffect = effect,
                    keySize = keySize.roundToInt().dp,
                    colors = if (lockScreen) NumericKeyboardDefaults.lockScreenColors() else NumericKeyboardDefaults.colors(),
                    onLeftKeyClick = { input = "" },
                    onRightKeyClick = { input = input.dropLast(1) },
                )
            }
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = NumericKeyboardPressEffect.entries.map { it.name },
                    selectedIndex = effectIndex,
                    title = stringResource(Res.string.demo_numeric_keyboard_press_effect),
                    summary = stringResource(Res.string.demo_numeric_keyboard_press_effect_summary),
                    onSelectedIndexChange = { effectIndex = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    items = listOf(stringResource(Res.string.demo_input_none), doneText),
                    selectedIndex = leftKeyIndex,
                    title = stringResource(Res.string.demo_numeric_keyboard_left_key),
                    onSelectedIndexChange = { leftKeyIndex = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    items = listOf(stringResource(Res.string.demo_input_none), deleteText),
                    selectedIndex = rightKeyIndex,
                    title = stringResource(Res.string.demo_numeric_keyboard_right_key),
                    onSelectedIndexChange = { rightKeyIndex = it },
                )
            }
            item {
                SliderPreference(
                    value = keySize,
                    onValueChange = { keySize = it },
                    title = stringResource(Res.string.common_size),
                    valueText = "${keySize.roundToInt()}dp",
                    valueRange = 56f..76f,
                    steps = 9,
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
        }
    }
    variant(key = "lock_screen", title = Res.string.demo_numeric_keyboard_variant_lock_screen) {
        LockScreenKeyboard()
    }
    variant(key = "disabled", title = Res.string.common_disabled) {
        // Disabled: keys ignore touches.
        PageSurface {
            NumericKeyboard(onNumberClick = {}, enabled = false)
        }
    }
}

/**
 * The Settings keyboard: ripple press, delete on the right, "Done" on the left once a digit is in.
 * Its keys are card-coloured discs, so it sits on the page surface instead of the card.
 */
@Composable
private fun SettingsKeyboard() {
    var input by remember { mutableStateOf("") }
    val state = rememberNumericKeyboardState()
    val scope = rememberCoroutineScope()
    PageSurface {
        Text(text = input.ifEmpty { stringResource(Res.string.demo_numeric_keyboard_placeholder) })
        NumericKeyboard(
            onNumberClick = { if (input.length < MAX_DIGITS) input += it },
            state = state,
            leftKey = if (input.isNotEmpty()) NumericKeyboardDefaults.finishKey(stringResource(Res.string.common_done)) else null,
            rightKey = NumericKeyboardDefaults.deleteKey(stringResource(Res.string.common_delete)),
            onLeftKeyClick = { input = "" },
            onRightKeyClick = { input = input.dropLast(1) },
        )
        TextButton(
            text = stringResource(Res.string.demo_numeric_keyboard_enter_animation),
            onClick = { scope.launch { state.animateEnter(hasLeftKey = input.isNotEmpty()) } },
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

/**
 * The lock-screen glass keys over a wallpaper, driving a PIN dot row.
 */
@Composable
private fun LockScreenKeyboard() {
    var pin by remember { mutableStateOf("") }
    WallpaperSurface(brush = LockScreenWallpaper) {
        SimpleLock(
            filledCount = pin.length,
            colors = SimpleLockDefaults.transparentColors(),
            glowEffect = true,
        )
        NumericKeyboard(
            onNumberClick = { pin = if (pin.length < 4) pin + it else it.toString() },
            rightKey = NumericKeyboardDefaults.deleteKey(stringResource(Res.string.common_delete)),
            pressEffect = NumericKeyboardPressEffect.Light,
            colors = NumericKeyboardDefaults.lockScreenColors(),
            onRightKeyClick = { pin = pin.dropLast(1) },
        )
    }
}
