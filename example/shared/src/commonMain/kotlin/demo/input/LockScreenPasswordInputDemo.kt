// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import io.github.suqi8.coui.kmp.basic.LockScreenPasswordInput
import io.github.suqi8.coui.kmp.basic.LockScreenPasswordInputDefaults
import io.github.suqi8.coui.kmp.basic.LockScreenPasswordInputMode
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_mode
import io.github.suqi8.coui.kmp.shared.generated.resources.common_next
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_error_state
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_input_password_toggle
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_screen_password_input_clear_error
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_screen_password_input_enter_password
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_screen_password_input_hint_sample
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_screen_password_input_incorrect
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_screen_password_input_length_limit
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_screen_password_input_length_limit_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_screen_password_input_min_length
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_screen_password_input_play_press
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_screen_password_input_show_error
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_screen_password_input_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_screen_password_input_variant_desktop
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_lock_screen_password_input_variant_setting
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

internal val LockScreenPasswordInputDemo = Demo(
    id = "lock_screen_password_input",
    apiName = "LockScreenPasswordInput",
    title = Res.string.demo_lock_screen_password_input_title,
    category = DemoCategory.Input,
    page = DemoPage.Variants { lockScreenPasswordInputPage() },
    keywords = listOf("pin", "密码", "COUILockScreenPwdInputView"),
)

/** The correct password of the samples. */
private const val SAMPLE_PASSWORD = "123456"

/** The sample wallpaper behind the desktop (lock screen) mode. */
private val DesktopWallpaper = Brush.linearGradient(listOf(Color(0xFF5B247A), Color(0xFF1BCEDF)))

private fun LazyListScope.lockScreenPasswordInputPage() {
    variant(key = "setting", title = Res.string.demo_lock_screen_password_input_variant_setting) {
        SettingPasswordInput()
    }
    playground {
        var password by rememberSaveable { mutableStateOf("") }
        var modeIndex by rememberSaveable { mutableIntStateOf(LockScreenPasswordInputMode.Setting.ordinal) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        var passwordToggle by rememberSaveable { mutableStateOf(true) }
        var limitLength by rememberSaveable { mutableStateOf(true) }
        var minCount by rememberSaveable { mutableFloatStateOf(LockScreenPasswordInputDefaults.MinCount.toFloat()) }
        var isError by rememberSaveable { mutableStateOf(false) }
        val mode = LockScreenPasswordInputMode.entries[modeIndex]
        val desktop = mode == LockScreenPasswordInputMode.Desktop
        val min = minCount.roundToInt()
        // The Setting field and button are card-coloured: show them on the page surface.
        val surface = if (desktop) DesktopWallpaper else SolidColor(COUITheme.colorScheme.backgroundWithCard)

        PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
            WallpaperSurface(
                brush = surface,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = if (desktop) 32.dp else 16.dp),
            ) {
                LockScreenPasswordInput(
                    value = password,
                    onValueChange = {
                        password = it
                        isError = false
                    },
                    onNextClick = { isError = it != SAMPLE_PASSWORD },
                    mode = mode,
                    enabled = enabled,
                    label = stringResource(Res.string.demo_lock_screen_password_input_hint_sample),
                    errorMessage = if (isError) stringResource(Res.string.demo_lock_screen_password_input_incorrect) else "",
                    showPasswordToggle = passwordToggle,
                    enableInputCount = limitLength,
                    minCount = min,
                    nextContentDescription = stringResource(Res.string.common_next),
                )
            }
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = LockScreenPasswordInputMode.entries.map { it.name },
                    selectedIndex = modeIndex,
                    title = stringResource(Res.string.common_mode),
                    onSelectedIndexChange = { modeIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = limitLength,
                    onCheckedChange = { limitLength = it },
                    title = stringResource(Res.string.demo_lock_screen_password_input_length_limit),
                    summary = stringResource(Res.string.demo_lock_screen_password_input_length_limit_summary),
                )
            }
            item {
                SliderPreference(
                    value = minCount,
                    onValueChange = { minCount = it },
                    title = stringResource(Res.string.demo_lock_screen_password_input_min_length),
                    valueText = min.toString(),
                    enabled = limitLength,
                    valueRange = 4f..8f,
                    steps = 3,
                )
            }
            item {
                SwitchPreference(
                    checked = passwordToggle,
                    onCheckedChange = { passwordToggle = it },
                    title = stringResource(Res.string.demo_input_password_toggle),
                )
            }
            item {
                SwitchPreference(
                    checked = isError,
                    onCheckedChange = { isError = it },
                    title = stringResource(Res.string.demo_input_error_state),
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
    variant(key = "desktop", title = Res.string.demo_lock_screen_password_input_variant_desktop) {
        DesktopPasswordInput()
    }
    variant(key = "disabled", title = Res.string.common_disabled) {
        PageSurface(contentPadding = PaddingValues(16.dp)) {
            LockScreenPasswordInput(
                value = "",
                onValueChange = {},
                onNextClick = {},
                enabled = false,
                label = stringResource(Res.string.common_disabled),
                nextContentDescription = stringResource(Res.string.common_next),
            )
        }
    }
}

/**
 * Settings mode with the 6-16 length rule: the arrow activates at 6 digits. The error fades in
 * over 217ms and out over 283ms (showError / closeError).
 */
@Composable
private fun SettingPasswordInput() {
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    PageSurface(
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        LockScreenPasswordInput(
            value = password,
            onValueChange = {
                password = it
                isError = false
            },
            onNextClick = { isError = it != SAMPLE_PASSWORD },
            label = stringResource(Res.string.demo_lock_screen_password_input_hint_sample),
            errorMessage = if (isError) stringResource(Res.string.demo_lock_screen_password_input_incorrect) else "",
            enableInputCount = true,
            nextContentDescription = stringResource(Res.string.common_next),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(
                text = stringResource(Res.string.demo_lock_screen_password_input_show_error),
                onClick = { isError = true },
                modifier = Modifier.weight(1f),
            )
            TextButton(
                text = stringResource(Res.string.demo_lock_screen_password_input_clear_error),
                onClick = { isError = false },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * Desktop (lock screen) mode over a wallpaper: press the arrow to see the light, or replay it with
 * the button (it only runs while the arrow is active, so the field is filled first).
 */
@Composable
private fun DesktopPasswordInput() {
    var password by remember { mutableStateOf("") }
    val nextInteraction = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()
    WallpaperSurface(
        brush = DesktopWallpaper,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 32.dp),
    ) {
        LockScreenPasswordInput(
            value = password,
            onValueChange = { password = it },
            onNextClick = { password = "" },
            mode = LockScreenPasswordInputMode.Desktop,
            label = stringResource(Res.string.demo_lock_screen_password_input_enter_password),
            nextContentDescription = stringResource(Res.string.common_next),
            nextButtonInteractionSource = nextInteraction,
        )
        TextButton(
            text = stringResource(Res.string.demo_lock_screen_password_input_play_press),
            onClick = {
                if (password.isEmpty()) password = "1234"
                scope.launch {
                    val press = PressInteraction.Press(Offset.Zero)
                    nextInteraction.emit(press)
                    delay(400)
                    nextInteraction.emit(PressInteraction.Release(press))
                }
            },
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
